/*
 *   Copyright 2015 Kirby Banman,
 *                  Stuart Bildfell,
 *                  Elliot Colp,
 *                  Christian Ellinger,
 *                  Braedy Kuzma,
 *                  Ryan Thornhill
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package cmput301w15t07.TravelTracker.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.gson.reflect.TypeToken;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import cmput301w15t07.TravelTracker.serverinterface.ElasticSearchHelper;
import cmput301w15t07.TravelTracker.serverinterface.FileSystemHelper;
import cmput301w15t07.TravelTracker.serverinterface.MergeResult;
import cmput301w15t07.TravelTracker.serverinterface.ResultCallback;
import cmput301w15t07.TravelTracker.serverinterface.ServerHelper;
import cmput301w15t07.TravelTracker.util.DeletionFlag;
import cmput301w15t07.TravelTracker.util.PersistentList;

/**
 * DataSource that caches Document model objects with local persistence iff ServerHelper reports an error
 * saving said objects.
 * 
 * This is the final DataSource for the deployed app.
 * 
 * In order to keep latency low, observers of this datasource are updated optimistically with changes, and are
 * only updated after network activity is complete *if* there is a change to local data.
 * 
 * The caching datasource will have a bunch of HashMap<UUID, Document subclass>, just like the in memory data 
 * source. Those hash maps are for the Document objects that the Views and Controllers refer to.
 * 
 * The cache will try to run save or delete with the ES server every time those in memory documents change 
 * (possibly capped at once per second or something).
 * If a save fails due to no connection, it will add the document id to a queue of to-save documents, and save
 * the changed (dirty) document to a local file
 * If a delete fails due to no connection, it will add the document id to a queue of to-delete documents, and 
 * save the deleted document to a local file (it's saved to use the document last changed timestamp for 
 * conflict resolution upon reconnection)
 * If a save or delete fails for another reason, I don't think this is very recoverable, so the user will be 
 * notified but I'm not quite sure what to do here yet.
 * The to-save and to-delete lists will be stored in local files, and the CacheDataSource will look for them 
 * and use them when being constructed, in case the app closed before reconnecting with the server.
 * 
 * When the caching datasource reconnects to the ES server, it'll try to save the documents in the to-save list, 
 * and try to delete the documents in the to-delete list. I say "try" because the cache will need to pull from 
 * the server all documents from the two lists, and only run the save/delete if the local last change timestamp 
 * is more recent than the remote last change timestamp.
 * 
 * Whew.
 * 
 * @author kdbanman
 */
public class CacheDataSource extends InMemoryDataSource {
	
	private static final String DELETE_USERS = "user_deletions.json";
	private static final String DELETE_CLAIMS = "claim_deletions.json";
	private static final String DELETE_ITEMS = "item_deletions.json";
	private static final String DELETE_TAGS = "tag_deletions.json";
	
	private Context appContext;
	
	private ServerHelper mainHelper;
	private ServerHelper backupHelper;

	private PersistentList<DeletionFlag<User>> userDeletions;
	private PersistentList<DeletionFlag<Claim>> claimDeletions;
	private PersistentList<DeletionFlag<Item>> itemDeletions;
	private PersistentList<DeletionFlag<Tag>> tagDeletions;
	
	private boolean updateRunning = false;
	private long syncNumber = 0;
	
	/**
	 * @param appContext May be null. Application context for displaying errors.
	 */
	public CacheDataSource(Context appContext, long updatePeriod) {
		this(appContext, updatePeriod, new ElasticSearchHelper(), new FileSystemHelper(appContext));
	}
	
	/**
	 * @param appContext May be null. Application context for displaying errors.
	 * @param main The interface for remote server or test stubs.
	 * @param backup The interface for data persistence when main fails.
	 */
	public CacheDataSource(Context appContext, long updatePeriod, ServerHelper main, ServerHelper backup) {
		super();
		
		this.appContext = appContext;
		this.mainHelper = main;
		this.backupHelper = backup;
		
		this.userDeletions = new PersistentList<DeletionFlag<User>>(DELETE_USERS, appContext, (new TypeToken<DeletionFlag<User>>(){}).getType());
		this.claimDeletions = new PersistentList<DeletionFlag<Claim>>(DELETE_CLAIMS, appContext, (new TypeToken<DeletionFlag<Claim>>(){}).getType());
		this.itemDeletions = new PersistentList<DeletionFlag<Item>>(DELETE_ITEMS, appContext, (new TypeToken<DeletionFlag<Item>>(){}).getType());
		this.tagDeletions = new PersistentList<DeletionFlag<Tag>>(DELETE_TAGS, appContext, (new TypeToken<DeletionFlag<Tag>>(){}).getType());
		
		// load any cached data into memory
		try {
			this.<User> loadIntoMemory(backup.getAllUsers(), users);
			this.<Claim> loadIntoMemory(backup.getAllClaims(), claims);
			this.<Item> loadIntoMemory(backup.getAllItems(), items);
			this.<Tag> loadIntoMemory(backup.getAllTags(), tags);
		} catch (Exception e) {
			warn("Failed to load local backup into memory");
		}
		
		// pull data from server
		new SyncDocumentsTask(new ResultCallback<Boolean>() {

			@Override
			public void onResult(Boolean result) {
				warn("Data imported from server");
			}

			@Override
			public void onError(String message) {
				warn(message);
			}
			
		}).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
		

		Handler uiHandler = new Handler(Looper.getMainLooper());
		uiHandler.postDelayed(new PollServerLoopTask(updatePeriod), updatePeriod);
	}
	
	@Override
	public void addUser(final ResultCallback<User> callback) {
		super.addUser(callback);
		
		new SyncUpdateTask("addUser").executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
	}
	
	@Override
	public void addClaim(final User user, final ResultCallback<Claim> callback) {
		super.addClaim(user, callback);
		
		new SyncUpdateTask("addClaim").executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
	}

	@Override
	public void addItem(final Claim claim, final ResultCallback<Item> callback) {
		super.addItem(claim, callback);
		
		new SyncUpdateTask("addItem").executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
	}

	@Override
	public void addTag(final User user, final ResultCallback<Tag> callback) {
		super.addTag(user, callback);
		
		new SyncUpdateTask("addTag").executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
	}

	@Override
	public void deleteUser(final UUID id, final ResultCallback<Void> callback) {
		if (users.get(id) != null) {
			// add to toDelete list - will be picked up on sync cycle
			userDeletions.add(new DeletionFlag<User>(users.get(id)));
			// remove from inmemory - may come back after sync cycle
			super.deleteUser(id, callback);
			
			new SyncUpdateTask("deleteUser").executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
		}
	}

	@Override
	public void deleteClaim(final UUID id, final ResultCallback<Void> callback) {
		if (claims.get(id) != null) {
			// add to toDelete list - will be picked up on sync cycle
			claimDeletions.add(new DeletionFlag<Claim>(claims.get(id)));
			// remove from inmemory - may come back after sync cycle
			super.deleteClaim(id, callback);
			
			new SyncUpdateTask("deleteClaim").executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
		}
	}

	@Override
	public void deleteItem(final UUID id, final ResultCallback<Void> callback) {
		if (items.get(id) != null) {
			// add to toDelete list - will be picked up on sync cycle
			itemDeletions.add(new DeletionFlag<Item>(items.get(id)));
			// remove from inmemory - may come back after sync cycle
			super.deleteItem(id, callback);
			
			new SyncUpdateTask("deleteItem").executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
		}
	}

	@Override
	public void deleteTag(final UUID id, final ResultCallback<Void> callback) {
		if (tags.get(id) != null) {
			// add to toDelete list - will be picked up on sync cycle
			tagDeletions.add(new DeletionFlag<Tag>(tags.get(id)));
			// remove from inmemory - may come back after sync cycle
			super.deleteTag(id, callback);
			
			new SyncUpdateTask("deleteTag").executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
		}
	}

	@Override
	public void getUser(final UUID id, final ResultCallback<User> callback) {
		if (users.get(id) != null) {
			super.getUser(id, callback);
		} else {
			// after sync, try again.  callback.error if still not there
			new SyncDocumentsTask(new SyncWrappedResultCallback(callback) {
				@Override
				public void onResult(Boolean changesMade) {
					if (changesMade) CacheDataSource.super.getUser(id, callback);
				}
			}).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
		}
	}

	@Override
	public void getClaim(final UUID id, final ResultCallback<Claim> callback) {
		if (claims.get(id) != null) {
			super.getClaim(id, callback);
		} else {
			// after sync, try again.  callback.error if still not there
			new SyncDocumentsTask(new SyncWrappedResultCallback(callback) {
				@Override
				public void onResult(Boolean changesMade) {
					if (changesMade) CacheDataSource.super.getClaim(id, callback);
				}
			}).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
		}
	}

	@Override
	public void getItem(final UUID id, final ResultCallback<Item> callback) {
		if (items.get(id) != null) {
			super.getItem(id, callback);
		} else {
			// after sync, try again.  callback.error if still not there
			new SyncDocumentsTask(new SyncWrappedResultCallback(callback) {
				@Override
				public void onResult(Boolean changesMade) {
					if (changesMade) CacheDataSource.super.getItem(id, callback);
				}
			}).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
		}
	}

	@Override
	public void getTag(final UUID id, final ResultCallback<Tag> callback) {
		if (tags.get(id) != null) {
			super.getTag(id, callback);
		} else {
			// after sync, try again.  callback.error if still not there
			new SyncDocumentsTask(new SyncWrappedResultCallback(callback) {
				@Override
				public void onResult(Boolean changesMade) {
					if (changesMade) CacheDataSource.super.getTag(id, callback);
				}
			}).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
		}
	}

	@Override
	public void getAllUsers(final ResultCallback<Collection<User>> callback) {
		super.getAllUsers(callback);
		// after sync, try again.  callback.error if still not there
		new SyncDocumentsTask(new SyncWrappedResultCallback(callback) {
			@Override
			public void onResult(Boolean changesMade) {
				if (changesMade) CacheDataSource.super.getAllUsers(callback);
			}
		}).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
	}

	@Override
	public void getAllClaims(final ResultCallback<Collection<Claim>> callback) {
		super.getAllClaims(callback);
		// after sync, try again.  callback.error if still not there
		new SyncDocumentsTask(new SyncWrappedResultCallback(callback) {
			@Override
			public void onResult(Boolean changesMade) {
				if (changesMade) CacheDataSource.super.getAllClaims(callback);
			}
		}).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
	}

	@Override
	public void getAllItems(final ResultCallback<Collection<Item>> callback) {
		super.getAllItems(callback);
		// after sync, try again.  callback.error if still not there
		new SyncDocumentsTask(new SyncWrappedResultCallback(callback) {
			@Override
			public void onResult(Boolean changesMade) {
				if (changesMade) CacheDataSource.super.getAllItems(callback);
			}
		}).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);

	}

	@Override
	public void getAllTags(final ResultCallback<Collection<Tag>> callback) {
		super.getAllTags(callback);
		// after sync, try again.  callback.error if still not there
		new SyncDocumentsTask(new SyncWrappedResultCallback(callback) {
			@Override
			public void onResult(Boolean changesMade) {
				if (changesMade) CacheDataSource.super.getAllTags(callback);
			}
		}).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
		
	}
	
	private <T extends Document> void loadIntoMemory(Collection<T> docs, Map<UUID, T> docMap) {
		for (T doc : docs) {
			docMap.put(doc.getUUID(), doc);
		}
	}

	private void warn(String msg) {
		Log.w("CacheDataSource", msg);
		Toast.makeText(appContext, msg, Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * ResultCallback wrapper for mating the DataSource parameter callbacks to the SyncDocumentTask.
	 * Leaves onResult unimplemented so results handling is flexible.
	 * @author kdbanman
	 *
	 */
	private abstract class SyncWrappedResultCallback implements ResultCallback<Boolean> {

		ResultCallback<?> errCallback;
		
		public SyncWrappedResultCallback(ResultCallback<?> callback) {
			this.errCallback = callback;
		}

		@Override
		public void onError(String message) {
			errCallback.onError(message);
		}
	}

	/**
	 * Callback that logs whether or not changes were made on a successful sync and updates
	 * observers accordingly.
	 * Logs and Toasts an error message if received.
	 * 
	 * @author kdbanman
	 *
	 */
	public class InfoReportingCallback implements ResultCallback<Boolean> {

		@Override
		public void onResult(Boolean changesMade) {
			if (changesMade) {
		        updateHandler.post(updateRunnable);
				Log.i("CacheDataSource", "New data retrieved from server.");
			} else {
				Log.i("CacheDataSource", "All data retrieved from server was known");
			}
			return;
		}

		@Override
		public void onError(String message) {
			warn(message);
		}

	}
	
	/**
	 * Sensible wrapper for update tasks for use in the poll cycle. Reports results and updates observers.
	 * @author kdbanman
	 *
	 */
	private class SyncUpdateTask extends AsyncTask<Void, Void, Void> {

		String caller;
		
		public SyncUpdateTask(String caller) {
			this.caller = caller;
		}
		
		@Override
		protected Void doInBackground(Void... params) {

			new SyncDocumentsTask(new InfoReportingCallback(), caller).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
			return null;
		}
		
	}
	
	/**
	 * Runnable that runs an update task and then creates a delayed instance of itself on the main loop.
	 * @author kdbanman
	 *
	 */
	private class PollServerLoopTask implements Runnable {
		long period;
		
		public PollServerLoopTask(long intervalMillis) {

			Log.i("CacheDataSource", "New delayed server poll created. " + Long.toString(intervalMillis) + " millis delay.");
			this.period = intervalMillis;
		}

		@Override
		public void run() {
			Log.i("CacheDataSource", "Executing server poll.");
			new SyncUpdateTask("poll loop").executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);

			Log.i("CacheDataSource", "Registering new server poll.");
			Handler uiHandler = new Handler(Looper.getMainLooper());
			uiHandler.postDelayed(new PollServerLoopTask(period), period);
		}
		
	}
	
	/**
	 * Main sync operation - pulls from main helper and merges with local content.
	 * Background task sets a booloan attribute to indicate changes were made during the merge. 
	 * @author kdbanman
	 *
	 */
	private class SyncDocumentsTask extends AsyncTask<Void, Void, String> {

		private ResultCallback<Boolean> callback;

		private Collection<User> retrievedUsers;
		private Collection<Claim> retrievedClaims;
		private Collection<Item> retrievedItems;
		private Collection<Tag> retrievedTags;
		
		private boolean changesMade = false;
		private long id;

		/**
		 * 
		 * @param callback sync result callback, or null for no action.
		 */
		public SyncDocumentsTask(ResultCallback<Boolean> callback) {
			this.callback = callback;
			this.id = syncNumber++;
			Log.i("CacheDataSource", "SyncDocs id " + Long.toString(id) + " created");
		}
		
		/**
		 * 
		 * @param callback sync result callback, or null for no action.
		 */
		public SyncDocumentsTask(ResultCallback<Boolean> callback, String caller) {
			this(callback);
			Log.i("CacheDataSource", "Sync requested from " + caller);
		}

		/**
		 * back on UI thread, do callback stuff, update observers.
		 * 
		 * @param errMsg  The error message if an error was encountered.  Null otherwise (hacky?).
		 */
		@Override
		protected void onPostExecute(String errMsg) {
			updateRunning = false;
			
			// if null errmsg assume success.
			
			// sync documents callback may be null, or it may wrap a get*() callback.
			// if not null, call the callback.
			if (callback != null) {
				if (errMsg == null) {
					callback.onResult(changesMade);
				} else {
					callback.onError(errMsg);
				}
			}

			Log.i("CacheDataSource", "SyncDocs id " + Long.toString(id) + " completed.");
		}

		@Override
		protected String doInBackground(Void... params) {

			updateRunning = true;
			Log.i("CacheDataSource", "SyncDocs id " + Long.toString(id) + " running");
			
			// attempt to pull all data from main
			// (push all in memory to backup and return if fail)
			if (!retrieveFromMain()) {
				if (!dumpToBackup()) {
					return "Error saving to backup cache!";
				}
				return null; // normal execution - saved to backup, no error.
			}

			Log.i("CacheDataSource", "Documents retrieved from remote.");

			this.<User>performPendingDeletions(this.<User>filterNonStaleDeletions(retrievedUsers, userDeletions), userDeletions);
			this.<Claim>performPendingDeletions(this.<Claim>filterNonStaleDeletions(retrievedClaims, claimDeletions), claimDeletions);
			this.<Item>performPendingDeletions(this.<Item>filterNonStaleDeletions(retrievedItems, itemDeletions), itemDeletions);
			this.<Tag>performPendingDeletions(this.<Tag>filterNonStaleDeletions(retrievedTags, tagDeletions), tagDeletions);
			
			Log.i("CacheDataSource", "Locally queued deletions completed.");
			
			// merge every remaining received document into inmemory
			changesMade = this.<User>mergeRetrieved(retrievedUsers, users);
			changesMade |= this.<Claim>mergeRetrieved(retrievedClaims, claims);
			changesMade |= this.<Item>mergeRetrieved(retrievedItems, items);
			changesMade |= this.<Tag>mergeRetrieved(retrievedTags, tags);

			Log.i("CacheDataSource", "Retrieved and existing documents merged.");
			
			// dump post-merge in memory stuff to cache
			if (!dumpToBackup()) {
				return "Error saving to backup cache after merge!";
			}
			
			// push in memory to server
			if (!pushToMain()) {
				Log.w("CacheDataSource", "push to main failed - maintaining dirty status");
				return null; // normal behaviour - no erron massage.
			}
			// set all documents to clean if successful
			setDirtyToClean(getDirtyUsers());
			setDirtyToClean(getDirtyClaims());
			setDirtyToClean(getDirtyItems());
			setDirtyToClean(getDirtyTags());
			
			Log.i("CacheDataSource", "Sync cycle completed.");
			
			return null;
		}

		private <T extends Document> boolean mergeRetrieved(Collection<T> retrieved, Map<UUID, T> local) {
			boolean changes = false;
			for (T toMerge : retrieved) {
				if (local.containsKey(toMerge.getUUID())) {
					boolean mergeResults = local.get(toMerge.getUUID()).mergeAttributesFrom(toMerge);
					changes |= mergeResults;
					if (mergeResults) {
						Log.i("CacheDataSource", "Existing document updated from remote.");
					}
				} else {
					changes |= true;
					local.put(toMerge.getUUID(), toMerge);
					Log.i("CacheDataSource", "New document retrieved from remote.");
				}
			}
			return changes;
		}

		private void setDirtyToClean(Collection<? extends Document> dirty) {
			for (Document doc : dirty) {
				doc.setClean();
			}
		}

		/**
		 * @return false if push fails, true if success
		 */
		private boolean pushToMain() {
			Log.i("CacheDataSource", "Dumping to main storage (remote)");
			// save all in memory documents
			try {
				mainHelper.saveDocuments(getUsers());
				mainHelper.saveDocuments(getClaims());
				mainHelper.saveDocuments(getItems());
				mainHelper.saveDocuments(getTags());
				Log.i("CacheDataSource", "Remote dump successful.");
				return true;
			} catch (IOException e) {
				Log.i("CacheDataSource", "Could not save to main (connection err).");
			} catch (Exception e) {
				Log.e("CacheDataSource", "UNKNOWN ERROR FROM BACKUP HELPER");
			}
			return false;
			
		}

		private <T extends Document> void performPendingDeletions(ArrayList<Document> pendingDeletions, List<DeletionFlag<T>> deletions) {
			// remove above pending from remote and local in batch using .deletDocuments()
			if (pendingDeletions.size() == 0)
				return;
			
			try {
				mainHelper.deleteDocuments(pendingDeletions);
				Log.i("CacheDataSource", "Remote deletion batch removed from remote storage.");
			} catch (IOException e) {
				Log.i("CacheDataSource", "Deletions from main unsuccessful, not clearing deletions list.");
				return;
			} catch (Exception e) {
				Log.e("CacheDataSource", "UNKNOWN ERROR WHILE PERFOMING DELETIONS ON MAIN");
				return;
			}
			try {
				backupHelper.deleteDocuments(pendingDeletions);
				Log.i("CacheDataSource", "Remote deletion batch removed from local storage.");
			} catch (IOException e) {
				Log.w("CacheDataSource", "Failed to delete from backup helper!");
				return;
			} catch (Exception e) {
				Log.e("CacheDataSource", "UNKNOWN ERROR WHILE PERFOMING DELETIONS ON BACKUP");
				return;
			}
			
			// remove pending deletions from deletion list since they were successful
			ArrayList<DeletionFlag<T>> flagsToClear = new ArrayList<DeletionFlag<T>>();
			for (Document deleted : pendingDeletions) {
				for (DeletionFlag<T> flag: deletions) {
					if (flag.getToDelete().equals(deleted)) {
						Log.i("CacheDataSource", "Completed deletion removed from local deletion flag queue.");
						flagsToClear.add(flag);
					}
				}
			}
			deletions.removeAll(flagsToClear);
		}
		
		/**
		 * do deletions that are not out-of-date on received.
		 * 
		 * @param retrieved the retrieved documents to filter on
		 * @return 
		 * @return the deletions that were performed on retrieved (passed) documents.
		 */
		private <T extends Document> ArrayList<Document> filterNonStaleDeletions(Collection<T> retrieved, List<DeletionFlag<T>> deletions) {
			ArrayList<Document> pendingDeletions = new ArrayList<Document>();
			ArrayList<DeletionFlag<T>> overriddenDeletions = new ArrayList<DeletionFlag<T>>();
			for (DeletionFlag<T> deletion : deletions) {
				switch (mergeDeletion(deletion, retrieved)) {
				case CHANGED:
					pendingDeletions.add(deletion.getToDelete());
					Log.i("CacheDataSource", "Local deletion added to remote removal batch.");
					break;
				case OVERRIDDEN:
					overriddenDeletions.add(deletion);
					Log.i("CacheDataSource", "Local deletion added to local ignore batch.");
					break;
				case NOT_FOUND:
					Log.i("CacheDataSource", "Local deletion not found in retrieved documents.");
					break;
				default:
					Log.w("CacheDataSource", "Unexpected merge result!.");
					break;
				}
			}
			// remove overridden deletions from deletion list
			deletions.removeAll(overriddenDeletions);
			return pendingDeletions;
		}
		
		private MergeResult mergeDeletion(DeletionFlag deletion,
				Collection<? extends Document> retrievedDocuments) {
			
			for (Document doc : retrievedDocuments) {
				if (deletion.getToDelete().equals(doc)) {
					// perform delete if timestamp is newer than retrieved lastChanged,
					// override deletion otherwise
					if (deletion.getDate().after(doc.getLastChanged())) {
						retrievedDocuments.remove(deletion.getToDelete());
						Log.i("CacheDataSource", "Locally deleted document removed from retrieved documents.");
						return MergeResult.CHANGED;
					} else {
						Log.i("CacheDataSource", "Local deletion out of date - *not* removed from retrieved documents.");
						return MergeResult.OVERRIDDEN;
					}
				}
			}
			return MergeResult.NOT_FOUND;
		}

		/**
		 * @return false if save fails, true if success
		 */
		private boolean dumpToBackup() {
			Log.i("CacheDataSource", "Dumping to local cache");
			// save all in memory documents
			try {
				backupHelper.saveDocuments(getUsers());
				backupHelper.saveDocuments(getClaims());
				backupHelper.saveDocuments(getItems());
				backupHelper.saveDocuments(getTags());
				Log.i("CacheDataSource", "local backup successful");
				return true;
			} catch (IOException e) {
				Log.e("CacheDataSource", "Could not save to backup!");
			} catch (Exception e) {
				Log.e("CacheDataSource", "UNKNOWN ERROR FROM BACKUP HELPER");
			}
			return false;
			
		}

		/**
		 * @return false if retrieval fails, true if success
		 */
		private boolean retrieveFromMain() {
			try {
				 retrievedUsers = mainHelper.getAllUsers();
				 retrievedClaims = mainHelper.getAllClaims();
				 retrievedItems = mainHelper.getAllItems();
				 retrievedTags = mainHelper.getAllTags();
				 return true;
			} catch (IOException e) {
				Log.i("CacheDataSource", "Connection error");
			} catch (Exception e) {
				Log.e("CacheDataSource", "UNKNOWN ERROR FROM SERVER HELPER");
			}
			return false;
		}
	}

}
