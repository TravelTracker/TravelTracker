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
import java.util.UUID;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import cmput301w15t07.TravelTracker.serverinterface.ElasticSearchHelper;
import cmput301w15t07.TravelTracker.serverinterface.FileSystemHelper;
import cmput301w15t07.TravelTracker.serverinterface.MergeResult;
import cmput301w15t07.TravelTracker.serverinterface.ResultCallback;
import cmput301w15t07.TravelTracker.serverinterface.ServerHelper;
import cmput301w15t07.TravelTracker.util.DeletionFlag;
import cmput301w15t07.TravelTracker.util.Observable;
import cmput301w15t07.TravelTracker.util.Observer;
import cmput301w15t07.TravelTracker.util.PersistentList;

/**
 * DataSource that caches Document model objects with local persistence iff ServerHelper reports an error
 * saving said objects.
 * This is the final DataSource for the deployed app.
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
	
	private static final String TODELETE_FILENAME = "cached_deletions.json";
	
	private Context appContext;
	
	private ServerHelper mainHelper;
	private ServerHelper backupHelper;
	
	private PersistentList<DeletionFlag> deletions;
	
	/**
	 * @param appContext May be null. Application context for displaying errors.
	 */
	public CacheDataSource(Context appContext) {
		this(appContext, new ElasticSearchHelper(), new FileSystemHelper(appContext));
	}
	
	/**
	 * @param appContext May be null. Application context for displaying errors.
	 * @param main The interface for remote server or test stubs.
	 * @param backup The interface for data persistence when main fails.
	 */
	public CacheDataSource(Context appContext, ServerHelper main, ServerHelper backup) {
		super();
		
		this.appContext = appContext;
		this.mainHelper = main;
		
		// TODO load existing documents from backup if they exist
		
		
		this.deletions = new PersistentList<DeletionFlag>(TODELETE_FILENAME, appContext, DeletionFlag.class);
	}

	/*
	 * existing superclass add* methods are fine, added document will
	 * be dirty, so it will be picked up on next sync cycle.
	 * 
	 * getters and deleters need some additional behaviour (like 
	 * preliminary synchronization) before affecting in-memory Documents
	 * 
	 */

	@Override
	public void deleteUser(UUID id, ResultCallback<Void> callback) {
		if (users.get(id) != null) {
			// add to toDelete list - will be picked up on sync cycle
			deletions.add(new DeletionFlag(users.get(id)));
			// remove from inmemory - may come back after sync cycle
			super.deleteUser(id, callback);
		}
	}

	@Override
	public void deleteClaim(UUID id, ResultCallback<Void> callback) {
		if (claims.get(id) != null) {
			// add to toDelete list - will be picked up on sync cycle
			deletions.add(new DeletionFlag(claims.get(id)));
			// remove from inmemory - may come back after sync cycle
			super.deleteClaim(id, callback);
		}
	}

	@Override
	public void deleteItem(UUID id, ResultCallback<Void> callback) {
		if (items.get(id) != null) {
			// add to toDelete list - will be picked up on sync cycle
			deletions.add(new DeletionFlag(items.get(id)));
			// remove from inmemory - may come back after sync cycle
			super.deleteItem(id, callback);
		}
	}

	@Override
	public void deleteTag(UUID id, ResultCallback<Void> callback) {
		if (tags.get(id) != null) {
			// add to toDelete list - will be picked up on sync cycle
			deletions.add(new DeletionFlag(tags.get(id)));
			// remove from inmemory - may come back after sync cycle
			super.deleteTag(id, callback);
		}
	}

	@Override
	public void getUser(UUID id, ResultCallback<User> callback) {
		// TODO: write below for User, then abstract into wrapped asynctask.
		
		// check main, if fail check backup.
		// if found in either
			// merge with inmemory.
			// call inmemory.getUser(id, callback) because it will be there now
			// (or if it isn't there it wasn't on the server or backup either)
		// else user not found
	}

	@Override
	public void getClaim(UUID id, ResultCallback<Claim> callback) {
	}

	@Override
	public void getItem(UUID id, ResultCallback<Item> callback) {

	}

	@Override
	public void getTag(UUID id, ResultCallback<Tag> callback) {
	}

	@Override
	public void getAllUsers(ResultCallback<Collection<User>> callback) {
	}

	@Override
	public void getAllClaims(ResultCallback<Collection<Claim>> callback) {
	}

	@Override
	public void getAllItems(ResultCallback<Collection<Item>> callback) {

	}

	@Override
	public void getAllTags(ResultCallback<Collection<Tag>> callback) {
		
	}
	
	/**
	 * 
	 * @author kdbanman
	 *
	 */
	private class syncDocumentsTask extends AsyncTask<Void, Void, String> {

		private ResultCallback callback;

		private Collection<User> retrievedUsers;
		private Collection<Claim> retrievedClaims;
		private Collection<Item> retrievedItems;
		private Collection<Tag> retrievedTags;
		
		public syncDocumentsTask(ResultCallback<?> callback) {
			this.callback = callback;
		}

		@Override
		protected String doInBackground(Void... params) {
			// attempt to pull all data from main
			// (push all to backup and return if fail)
			if (!retrieveFromMain()) {
				if (!dumpToBackup()) {
					return "Error saving to backup cache!";
				}
				return null; // normal execution - saved to backup, no error.
			}

			ArrayList<Document> pendingDeletions = new ArrayList<Document>();
			pendingDeletions.addAll(filterNonStaleDeletions(retrievedUsers));
			pendingDeletions.addAll(filterNonStaleDeletions(retrievedClaims));
			pendingDeletions.addAll(filterNonStaleDeletions(retrievedItems));
			pendingDeletions.addAll(filterNonStaleDeletions(retrievedTags));
			
			performPendingDeletions(pendingDeletions);
			
			// merge every remaining received document into inmemory
			// update observers (on gui thread!) (merge not cause update())
			
			// push in memory to server
				// set all documents to clean and purge backup if successful
				// push all to backup if fail
			return null;
		}

		private void performPendingDeletions(ArrayList<Document> pendingDeletions) {
			// remove above pending from remote and local in batch using .deletDocuments()
			
			try {
				mainHelper.deleteDocuments(pendingDeletions);
			} catch (IOException e) {
				Log.i("CacheDataSource", "Deletions from main unsuccessful, not clearing deletions list.");
				return;
			} catch (Exception e) {
				Log.e("CacheDataSource", "UNKNOWN ERROR WHILE PERFOMING DELETIONS ON MAIN");
				return;
			}
			try {
				backupHelper.deleteDocuments(pendingDeletions);
			} catch (IOException e) {
				Log.w("CacheDataSource", "Failed to delete from backup helper!");
				return;
			} catch (Exception e) {
				Log.e("CacheDataSource", "UNKNOWN ERROR WHILE PERFOMING DELETIONS ON BACKUP");
				return;
			}
			
			// remove pending deletions from deletion list since they were successful
			ArrayList<DeletionFlag> flagsToClear = new ArrayList<DeletionFlag>();
			for (Document deleted : pendingDeletions) {
				for (DeletionFlag flag: deletions) {
					if (flag.getToDelete().equals(deleted))
						flagsToClear.add(flag);
				}
			}
			deletions.removeAll(flagsToClear);
		}

		/**
		 * back on UI thread, do callback stuff, update observers.
		 * 
		 * @param errMsg  The error message if an error was encountered.  Null otherwise (hacky!).
		 */
		@Override
		protected void onPostExecute(String errMsg) {
			// if not null errmsg assume success
			
		}
		
		/**
		 * do deletions that are not out-of-date on received.
		 * 
		 * @param retrieved the retrieved documents to filter on
		 * @return 
		 * @return the deletions that were performed on retrieved (passed) documents.
		 */
		private ArrayList<Document> filterNonStaleDeletions(Collection<? extends Document> retrieved) {
			ArrayList<Document> pendingDeletions = new ArrayList<Document>();
			ArrayList<DeletionFlag> overriddenDeletions = new ArrayList<DeletionFlag>();
			for (DeletionFlag deletion : deletions) {
				switch (mergeDeletion(deletion, retrieved)) {
				case CHANGED:
					pendingDeletions.add(deletion.getToDelete());
					break;
				case OVERRIDDEN:
					overriddenDeletions.add(deletion);
					break;
				case NOT_FOUND:
					break;
				default:
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
						return MergeResult.CHANGED;
					} else {
						return MergeResult.OVERRIDDEN;
					}
				}
			}
			return MergeResult.NOT_FOUND;
		}

		/**
		 * @return false if retrieval fails, true if success
		 */
		private boolean dumpToBackup() {
			Log.i("CacheDataSource", "Dumping to local cache");
			// save all in memory documents
			try {
				backupHelper.saveDocuments(getUsers());
				backupHelper.saveDocuments(getClaims());
				backupHelper.saveDocuments(getItems());
				backupHelper.saveDocuments(getTags());
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
