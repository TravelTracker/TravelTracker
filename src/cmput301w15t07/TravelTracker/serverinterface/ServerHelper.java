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

package cmput301w15t07.TravelTracker.serverinterface;

import java.util.Collection;
import java.util.UUID;

import cmput301w15t07.TravelTracker.model.Claim;
import cmput301w15t07.TravelTracker.model.Document;
import cmput301w15t07.TravelTracker.model.Item;
import cmput301w15t07.TravelTracker.model.Tag;
import cmput301w15t07.TravelTracker.model.User;

/**
 * Contains helper functions for server operations.
 * 
 * @author ryant26
 */
public interface ServerHelper {
	
	/**
	 * This method deletes all passed documents from the ES cluster.
	 * When finished or on error will use the callback. 
	 * @param documents
	 */
	public <T extends Document> void deleteDocuments(Collection<T> documents) throws Exception;
	
	/**
	 * This method returns all claims from the server
	 * @return all claims in the server
	 * @throws Exception
	 */
	public Collection<Claim> getAllClaims() throws Exception;
	
	/**
	 * This method returns all items from the server
	 * @return all items in server
	 * @throws Exception
	 */
	public Collection<Item> getAllItems() throws Exception;
	
	/**
	 * This method gets all tags from the server
	 * @return all tags in server
	 * @throws Exception
	 */
	public Collection<Tag> getAllTags() throws Exception;
	
	/**
	 * This method gets all users from the server
	 * @return	all users in server
	 * @throws Exception
	 */
	public Collection<User> getAllUsers() throws Exception;
	
	/**
	 * Gets all claims for the passed user from the server asynchronously, returns it via 
	 * ResultCallback
	 * @param user
	 * @return collection of claims
	 */
	public Collection<Claim> getClaims(UUID user) throws Exception;
	
	/**
	 * Gets all expense items for the passed claim from the server asynchronously, returns it
	 * via ResultCallback
	 * @param claim
	 * @return collection of expense Items
	 */
	public Collection<Item> getExpenses(UUID claim) throws Exception;
	/**
	 * Gets all tags for the passed user from the server asynchronously, returns it
	 * via ResultCallback
	 * @param user
	 * @return collection of Tags
	 */
	public Collection<Tag> getTags(UUID user) throws Exception;
	
	/**
	 * Gets gets user for passed name from the server asynchronously, returns it
	 * via ResultCallback
	 * @param name
	 * @return user
	 */
	public User getUser(String name) throws Exception;
	
	/**
	 * Saves passed documents to the server asynchronously, returns the documents that
	 * were successfully saved via callback 
	 * @param documents
	 */
	public <T extends Document> void saveDocuments(Collection<T> documents) throws Exception;
}
