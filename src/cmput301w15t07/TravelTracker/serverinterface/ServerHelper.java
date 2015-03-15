package cmput301w15t07.TravelTracker.serverinterface;

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
import java.util.UUID;

import cmput301w15t07.TravelTracker.model.Claim;
import cmput301w15t07.TravelTracker.model.Document;
import cmput301w15t07.TravelTracker.model.Item;
import cmput301w15t07.TravelTracker.model.Tag;
import cmput301w15t07.TravelTracker.model.User;
import io.searchbox.client.*;

/**
 * Contains helper functions for server operations.
 * 
 * @author ryant26
 */
public class ServerHelper {
	private JestClient conn;
	private Cache cache;
	
	/**
	 * Performs all the connection setup for this class to 
	 * connect to the ES cluster.
	 */
	private void getConnection(){
		
	}
	
	/**
	 * This method deletes all passed documents from the ES cluster.
	 * When finished or on error will use the callback. 
	 * @param documents
	 * @param callback
	 */
	public void deleteDocuments(Collection<Document> documents, ResultCallback<Collection<Document>> callback){
		
	}
	
	/**
	 * Gets all claims for the passed user from the server asynchronously, returns it via 
	 * ResultCallback
	 * @param user
	 * @param callback
	 */
	public void getClaims(UUID user, ResultCallback<Collection<Claim>> callback){
		
	}
	
	/**
	 * Gets all expense items for the passed claim from the server asynchronously, returns it
	 * via ResultCallback
	 * @param claim
	 * @param callback
	 */
	public void getExpenses(UUID claim, ResultCallback<Collection<Item>> callback){
		
	}
	
	/**
	 * Gets all tags for the passed user from the server asynchronously, returns it
	 * via ResultCallback
	 * @param user
	 * @param callback
	 */
	public void getTags(UUID user, ResultCallback<Collection<Tag>> callback){
		
	}
	
	/**
	 * Gets gets user for passed name from the server asynchronously, returns it
	 * via ResultCallback
	 * @param name
	 * @param callback
	 */
	public void getUser(String name, ResultCallback<User> callback){
		
	}
	
	/**
	 * Saves passed documents to the server asynchronously, returns the documents that
	 * were successfully saved via callback 
	 * @param documents
	 * @param callback
	 */
	public void saveDocuments(Collection<Document> documents, ResultCallback<Collection<Document>> callback){
		
	}
}
