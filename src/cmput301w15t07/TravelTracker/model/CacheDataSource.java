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
    public void getUser(final UUID id, final ResultCallback<User> callback) {
        if (users.get(id) != null) {
            super.getUser(id, callback);
        } else {
            // after sync, try again.  callback.error if still not there
            new syncDocumentsTask(new syncWrappedResultCallback(callback) {
                @Override
                public void onResult(Boolean changesMade) {
                    if (changesMade) CacheDataSource.super.getUser(id, callback);
                }
            });
        }
    }

    @Override
    public void getClaim(final UUID id, final ResultCallback<Claim> callback) {
        if (claims.get(id) != null) {
            super.getClaim(id, callback);
        } else {
            // after sync, try again.  callback.error if still not there
            new syncDocumentsTask(new syncWrappedResultCallback(callback) {
                @Override
                public void onResult(Boolean changesMade) {
                    if (changesMade) CacheDataSource.super.getClaim(id, callback);
                }
            });
        }
    }

    @Override
    public void getItem(final UUID id, final ResultCallback<Item> callback) {
        if (items.get(id) != null) {
            super.getItem(id, callback);
        } else {
            // after sync, try again.  callback.error if still not there
            new syncDocumentsTask(new syncWrappedResultCallback(callback) {
                @Override
                public void onResult(Boolean changesMade) {
                    if (changesMade) CacheDataSource.super.getItem(id, callback);
                }
            });
        }
    }

    @Override
    public void getTag(final UUID id, final ResultCallback<Tag> callback) {
        if (tags.get(id) != null) {
            super.getTag(id, callback);
        } else {
            // after sync, try again.  callback.error if still not there
            new syncDocumentsTask(new syncWrappedResultCallback(callback) {
                @Override
                public void onResult(Boolean changesMade) {
                    if (changesMade) CacheDataSource.super.getTag(id, callback);
                }
            });
        }
    }

    @Override
    public void getAllUsers(final ResultCallback<Collection<User>> callback) {
        super.getAllUsers(callback);
        // after sync, try again.  callback.error if still not there
        new syncDocumentsTask(new syncWrappedResultCallback(callback) {
            @Override
            public void onResult(Boolean changesMade) {
                if (changesMade) CacheDataSource.super.getAllUsers(callback);
            }
        });
    }

    @Override
    public void getAllClaims(final ResultCallback<Collection<Claim>> callback) {
        super.getAllClaims(callback);
        // after sync, try again.  callback.error if still not there
        new syncDocumentsTask(new syncWrappedResultCallback(callback) {
            @Override
            public void onResult(Boolean changesMade) {
                if (changesMade) CacheDataSource.super.getAllClaims(callback);
            }
        });
    }

    @Override
    public void getAllItems(final ResultCallback<Collection<Item>> callback) {
        super.getAllItems(callback);
        // after sync, try again.  callback.error if still not there
        new syncDocumentsTask(new syncWrappedResultCallback(callback) {
            @Override
            public void onResult(Boolean changesMade) {
                if (changesMade) CacheDataSource.super.getAllItems(callback);
            }
        });

    }

    @Override
    public void getAllTags(final ResultCallback<Collection<Tag>> callback) {
        super.getAllTags(callback);
        // after sync, try again.  callback.error if still not there
        new syncDocumentsTask(new syncWrappedResultCallback(callback) {
            @Override
            public void onResult(Boolean changesMade) {
                if (changesMade) CacheDataSource.super.getAllTags(callback);
            }
        });
        
    }

    private void warn(String msg) {
        Log.w("CacheDataSource", msg);
        Toast.makeText(appContext, msg, Toast.LENGTH_LONG).show();
    }
    
    private abstract class syncWrappedResultCallback implements ResultCallback<Boolean> {

        ResultCallback<?> errCallback;
        
        public syncWrappedResultCallback(ResultCallback<?> callback) {
            this.errCallback = callback;
        }

        @Override
        public void onError(String message) {
            errCallback.onError(message);
        }
    }
    
    /**
     * 
     * @author kdbanman
     *
     */
    private class syncDocumentsTask extends AsyncTask<Void, Void, String> {

        private ResultCallback<Boolean> callback;

        private Collection<User> retrievedUsers;
        private Collection<Claim> retrievedClaims;
        private Collection<Item> retrievedItems;
        private Collection<Tag> retrievedTags;
        
        private boolean changesMade = false;

        /**
         * 
         * @param callback sync result callback, or null for no action.
         */
        public syncDocumentsTask(ResultCallback<Boolean> callback) {
            this.callback = callback;
        }
        
        /**
         * 
         * @param callback sync result callback, or null for no action.
         */
        public syncDocumentsTask() {
            this(null);
        }

        /**
         * back on UI thread, do callback stuff, update observers.
         * 
         * @param errMsg  The error message if an error was encountered.  Null otherwise (hacky!).
         */
        @Override
        protected void onPostExecute(String errMsg) {
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
            
            // TODO merge every remaining received document into inmemory
            // TODO if merge caused changes, update observers (on gui thread!) (merge not cause update())
            CacheDataSource.this.updateObservers(CacheDataSource.this); 
            // TODO ^this^ should be conditional, be sure to catch new additions from add*() methods,
            //      and remember that getAll callbacks detect changes made with a boolean callback.
            
            // dump post-merge in memory stuff to cache
            if (!dumpToBackup()) {
                return "Error saving to backup cache!";
            }
            
            // push in memory to server
            if (!pushToMain()) {
                // set all documents to clean if successful
                setDirtyToClean(getDirtyUsers());
                setDirtyToClean(getDirtyClaims());
                setDirtyToClean(getDirtyItems());
                setDirtyToClean(getDirtyTags());
            }
            return null;
        }

        private void setDirtyToClean(Collection<? extends Document> dirty) {
            for (Document doc : dirty) {
                doc.setClean();
            }
        }

        private boolean pushToMain() {
            Log.i("CacheDataSource", "Dumping to main storage (remote)");
            // save all in memory documents
            try {
                mainHelper.saveDocuments(getUsers());
                mainHelper.saveDocuments(getClaims());
                mainHelper.saveDocuments(getItems());
                mainHelper.saveDocuments(getTags());
                return true;
            } catch (IOException e) {
                Log.i("CacheDataSource", "Could not save to main (connection err).");
            } catch (Exception e) {
                Log.e("CacheDataSource", "UNKNOWN ERROR FROM BACKUP HELPER");
            }
            return false;
            
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
