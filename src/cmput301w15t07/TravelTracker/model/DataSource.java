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

	/**
	 * Add a user.
	 * @param callback The result callback. On success, the result is the User.
	 */
	public void addUser(ResultCallback<User> callback);

	/**
	 * Add a claim.
	 * @param user The user to which the claim belongs. 
	 * @param callback The result callback. On success, the result is the Claim.
	 */
	public void addClaim(User user, ResultCallback<Claim> callback);

	/**
	 * Add an expense item.
	 * @param claim The claim to which the item belongs. 
	 * @param callback The result callback. On success, the result is the Item.
	 */
	public void addItem(Claim claim, ResultCallback<Item> callback);

	/**
	 * Add a tag.
	 * @param user The user to which the tag belongs. 
	 * @param callback The result callback. On success, the result is the Tag.
	 */
	public void addTag(User user, ResultCallback<Tag> callback);

	/**
	 * Delete a user.
	 * @param id The user's ID.
	 * @param callback The result callback. On success, the result is null.
	 */
	public void deleteUser(UUID id, ResultCallback<Void> callback);

	/**
	 * Delete a claim.
	 * @param id The claim's ID.
	 * @param callback The result callback. On success, the result is null.
	 */
	public void deleteClaim(UUID id, ResultCallback<Void> callback);

	/**
	 * Delete an item.
	 * @param id The item's ID.
	 * @param callback The result callback. On success, the result is null.
	 */
	public void deleteItem(UUID id, ResultCallback<Void> callback);

	/**
	 * Delete a tag.
	 * @param id The tag's ID.
	 * @param callback The result callback. On success, the result is null.
	 */
	public void deleteTag(UUID id, ResultCallback<Void> callback);

	/**
	 * Get a user.
	 * @param id The user's ID.
	 * @param callback The result callback. On success, the result is the User.
	 */
	public void getUser(UUID id, ResultCallback<User> callback);

	/**
	 * Get a claim.
	 * @param id The claim's ID.
	 * @param callback The result callback. On success, the result is the Claim.
	 */
	public void getClaim(UUID id, ResultCallback<Claim> callback);

	/**
	 * Get an item.
	 * @param id The item's ID.
	 * @param callback The result callback. On success, the result is the Item.
	 */
	public void getItem(UUID id, ResultCallback<Item> callback);

	/**
	 * Get a tag.
	 * @param id The tag's ID.
	 * @param callback The result callback. On success, the result is the Tag.
	 */
	public void getTag(UUID id, ResultCallback<Tag> callback);

	/**
	 * Get the collection of all users.
	 * @param callback The result callback. On success, the result is the collection of Users.
	 */
	public void getAllUsers(ResultCallback<Collection<User>> callback);

	/**
	 * Get the collection of all claims.
	 * @param callback The result callback. On success, the result is the collection of Claims.
	 */
	public void getAllClaims(ResultCallback<Collection<Claim>> callback);

	/**
	 * Get the collection of all items.
	 * @param callback The result callback. On success, the result is the collection of Items.
	 */
	public void getAllItems(ResultCallback<Collection<Item>> callback);

	/**
	 * Get the collection of all tags.
	 * @param callback The result callback. On success, the result is the collection of Tags.
	 */
	public void getAllTags(ResultCallback<Collection<Tag>> callback);
	
	/**
	 * @return A collection of all documents served by the DataSource which have
	 * marked themselves dirty.
	 */
	public Collection<Document> getDirtyDocuments();
}
