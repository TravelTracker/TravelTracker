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

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import android.content.Context;
import cmput301w15t07.TravelTracker.serverinterface.ElasticSearchHelper;
import cmput301w15t07.TravelTracker.serverinterface.FileSystemHelper;
import cmput301w15t07.TravelTracker.serverinterface.ResultCallback;
import cmput301w15t07.TravelTracker.serverinterface.ServerHelper;
import cmput301w15t07.TravelTracker.util.Observable;
import cmput301w15t07.TravelTracker.util.Observer;

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
public class CacheDataSource extends Observable<DataSource> implements
		DataSource, Observer<Document> {
	
	private Context appContext;
	
	private ServerHelper mainHelper;
	private ServerHelper backupHelper;
	
	// inUse* attributes are maps containing the Documents requested by the app's Views.
	private HashMap<UUID, Claim> inUseClaims;
	private HashMap<UUID, User> inUseUsers;
	private HashMap<UUID, Item> inUseItems;
	private HashMap<UUID, Tag> inUseTags;
	
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
		this.appContext = appContext;
		this.mainHelper = main;
		
		inUseClaims = new HashMap<UUID, Claim>();
		inUseUsers = new HashMap<UUID, User>();
		inUseItems = new HashMap<UUID, Item>();
		inUseTags = new HashMap<UUID, Tag>();
	}

	@Override
	public void update(Document observable) {		
		// every time a document changes 
		// TODO try 
		
	}

	@Override
	public void addUser(ResultCallback<User> callback) {
	}

	@Override
	public void addClaim(User user, ResultCallback<Claim> callback) {
	}

	@Override
	public void addItem(Claim claim, ResultCallback<Item> callback) {
	}

	@Override
	public void addTag(User user, ResultCallback<Tag> callback) {
	}

	@Override
	public void deleteUser(UUID id, ResultCallback<Void> callback) {
	}

	@Override
	public void deleteClaim(UUID id, ResultCallback<Void> callback) {
	}

	@Override
	public void deleteItem(UUID id, ResultCallback<Void> callback) {
	}

	@Override
	public void deleteTag(UUID id, ResultCallback<Void> callback) {
	}

	@Override
	public void getUser(UUID id, ResultCallback<User> callback) {
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

	@Override
	public Collection<Document> getDirtyDocuments() {
		return null;
	}

}
