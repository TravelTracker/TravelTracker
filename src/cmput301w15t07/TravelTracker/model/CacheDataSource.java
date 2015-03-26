package cmput301w15t07.TravelTracker.model;

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


import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import android.content.Context;
import cmput301w15t07.TravelTracker.serverinterface.ResultCallback;
import cmput301w15t07.TravelTracker.serverinterface.ServerHelper;
import cmput301w15t07.TravelTracker.util.Observable;
import cmput301w15t07.TravelTracker.util.Observer;

/**
 * DataSource that caches Document model objects with local persistence iff ServerHelper reports an error
 * saving said objects.
 * This is the final DataSource for the deployed app.
 * 
 * @author kdbanman
 */
public class CacheDataSource extends Observable<DataSource> implements
		DataSource, Observer<Document> {
	
	private Context appContext;
	
	private ServerHelper server;
	
	// inUse* attributes are maps containing the Documents requested by the app's Views.
	private HashMap<UUID, Claim> inUseClaims;
	private HashMap<UUID, User> inUseUsers;
	private HashMap<UUID, Item> inUseItems;
	private HashMap<UUID, Tag> inUseTags;
	
	/**
	 * @param appContext May be null. Application context for displaying errors.
	 * @param server The interface for remote server or test stubs.
	 */
	public CacheDataSource(Context appContext, ServerHelper server) {
		this.appContext = appContext;
		this.server = server;
		
		inUseClaims = new HashMap<UUID, Claim>();
		inUseUsers = new HashMap<UUID, User>();
		inUseItems = new HashMap<UUID, Item>();
		inUseTags = new HashMap<UUID, Tag>();
	}

	@Override
	public void update(Document observable) {		
		// every time a document changes 

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
