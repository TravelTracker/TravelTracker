package cmput301w15t07.TravelTracker.model;

import java.util.Collection;
import java.util.UUID;

import cmput301w15t07.TravelTracker.serverinterface.ResultCallback;

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

/**
 * Interface for serving model Documents from any persistent storage class.
 * Implementations should inherit from Observable so views may observe.
 * 
 * @author kdbanman
 *
 */
public interface DataSource {

	public void addUser(ResultCallback<User> callback);
	public void addClaim(User user, ResultCallback<Claim> callback);
	public void addItem(Claim claim, ResultCallback<Item> callback);
	public void addTag(User user, ResultCallback<Tag> callback);

	public void deleteUser(UUID id, ResultCallback<User> callback);
	public void deleteClaim(UUID id, ResultCallback<Claim> callback);
	public void deleteItem(UUID id, ResultCallback<Item> callback);
	public void deleteTag(UUID id, ResultCallback<Tag> callback);

	public void getUser(UUID id, ResultCallback<User> callback);
	public void getClaim(UUID id, ResultCallback<Claim> callback);
	public void getItem(UUID id, ResultCallback<Item> callback);
	public void getTag(UUID id, ResultCallback<Tag> callback);

	public void getAllUsers(ResultCallback<Collection<User>> callback);
	public void getAllClaims(ResultCallback<Collection<Claim>> callback);
	public void getAllItems(ResultCallback<Collection<Item>> callback);
	public void getAllTags(ResultCallback<Collection<Tag>> callback);
	
	/**
	 * @return A collection of all documents served by the DataSource which have
	 * marked themselves dirty.
	 */
	public Collection<Document> getDirtyDocuments();
}
