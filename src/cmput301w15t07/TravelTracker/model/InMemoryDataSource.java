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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import cmput301w15t07.TravelTracker.serverinterface.ResultCallback;
import cmput301w15t07.TravelTracker.util.Observable;
import cmput301w15t07.TravelTracker.util.Observer;

/**
 * Mock data source for early stages of development and unit testing.  Intended for use
 * by dependency injection.
 * 
 * @author colp
 *
 */
public class InMemoryDataSource extends Observable<DataSource> implements DataSource, Observer<Document> {

	protected HashMap<UUID, Claim> claims;
	protected HashMap<UUID, User> users;
	protected HashMap<UUID, Item> items;
	protected HashMap<UUID, Tag> tags;

	public InMemoryDataSource() {
		claims = new HashMap<UUID, Claim>();
		users = new HashMap<UUID, User>();
		items = new HashMap<UUID, Item>();
		tags = new HashMap<UUID, Tag>();
	}
	
	@Override
	public void addUser(ResultCallback<User> callback) {
		User user = new User(UUID.randomUUID());
		user.addObserver(this);
		
		internalAddUser(user);
		
		callback.onResult(user);
	}
	
	@Override
	public void addClaim(User user, ResultCallback<Claim> callback) {
		Claim claim = new Claim(UUID.randomUUID());
		claim.addObserver(this);
		
		if (!users.containsValue(user)) {
			callback.onError("User not found.");
			return;
		}
		
		claim.setUser(user.getUUID());
		internalAddClaim(claim);
		
		callback.onResult(claim);
	}

	@Override
	public void addItem(Claim claim, ResultCallback<Item> callback) {
		Item item = new Item(UUID.randomUUID());
		item.addObserver(this);
		
		item.setClaim(claim.getUUID());
		internalAddItem(item);
		
		if (!claims.containsValue(claim)) {
			callback.onError("Claim not found.");
			return;
		}
		
		callback.onResult(item);
	}

	@Override
	public void addTag(User user, ResultCallback<Tag> callback) {
		Tag tag = new Tag(UUID.randomUUID());
		tag.addObserver(this);
		
		tag.setUser(user.getUUID());
		internalAddTag(tag);
		
		if (!users.containsValue(user)) {
			callback.onError("User not found.");
			return;
		}
		
		callback.onResult(tag);
	}

	@Override
	public void deleteUser(UUID id, final ResultCallback<Void> callback) {
		if (users.get(id) == null) {
			callback.onError("User not found.");
			
		} else {
			internalDeleteUser(id);
			callback.onResult(null);
		}
	}

	@Override
	public void deleteClaim(UUID id, ResultCallback<Void> callback) {
		if (claims.get(id) == null) {
			callback.onError("Claim not found.");
			
		} else {
			internalDeleteClaim(id);
			callback.onResult(null);
		}
	}

	@Override
	public void deleteItem(UUID id, ResultCallback<Void> callback) {
		if (items.get(id) == null) {
			callback.onError("Expense item not found.");
			
		} else {
			internalDeleteItem(id);
			callback.onResult(null);
		}
	}

	@Override
	public void deleteTag(UUID id, ResultCallback<Void> callback) {
		if (tags.get(id) == null) {
			callback.onError("Tag not found.");
			
		} else {
			internalDeleteTag(id);
			callback.onResult(null);
		}
	}

	@Override
	public void getUser(UUID id, ResultCallback<User> callback) {
		User user = users.get(id);
		
		if (user == null) {
			callback.onError("User not found.");
		} else {
			user.addObserver(this);
			callback.onResult(user);
		}
	}

	@Override
	public void getClaim(UUID id, ResultCallback<Claim> callback) {
		Claim claim = claims.get(id);
		
		if (claim == null) {
			callback.onError("Claim not found.");
		} else {
			claim.addObserver(this);
			callback.onResult(claim);
		}
	}

	@Override
	public void getItem(UUID id, ResultCallback<Item> callback) {
		Item item = items.get(id);
		
		if (item == null) {
			callback.onError("Item not found.");
		} else {
			item.addObserver(this);
			callback.onResult(item);
		}
	}

	@Override
	public void getTag(UUID id, ResultCallback<Tag> callback) {
		Tag tag = tags.get(id);
		
		if (tag == null) {
			callback.onError("Tag not found.");
		} else {
			tag.addObserver(this);
			callback.onResult(tag);
		}
	}

	@Override
	public void getAllUsers(ResultCallback<Collection<User>> callback) {
		callback.onResult(users.values());
	}

	@Override
	public void getAllClaims(ResultCallback<Collection<Claim>> callback) {
		callback.onResult(claims.values());

	}

	@Override
	public void getAllItems(ResultCallback<Collection<Item>> callback) {
		callback.onResult(items.values());

	}

	@Override
	public void getAllTags(ResultCallback<Collection<Tag>> callback) {
		callback.onResult(tags.values());

	}

	@Override
	public Collection<Document> getDirtyDocuments() {
		// this is for caching, probably not meaningful for in-memory storage.
		return null;
	}
	
	@Override
	public void update(Document observable) {
		updateObservers(this);
	}
    
    /**
     * Add a User internally.
     * @param u The User to add.
     */
    protected void internalAddUser(User u) {
        users.put(u.getUUID(), u);
        u.addObserver(this);
    }
	
	/**
	 * Delete a User internally, cleaning up any orphan Documents.
	 * @param id The User's UUID.
	 */
	protected void internalDeleteUser(UUID id) {
		// Get all child Claims
		ArrayList<UUID> claimsToRemove = new ArrayList<UUID>();
		for (Entry<UUID, Claim> entry : claims.entrySet()) {
			Claim claim = entry.getValue();
			
			if (claim.getUser() == id) {
				claimsToRemove.add(entry.getKey());
			}
		}
		
		// Handle in a separate loop to avoid modifying while iterating
		for (UUID claimID : claimsToRemove) {
			internalDeleteClaim(claimID);
		}
		
		// Get all child Tags
		ArrayList<UUID> tagsToRemove = new ArrayList<UUID>();
		for (Entry<UUID, Tag> entry : tags.entrySet()) {
			Tag claim = entry.getValue();
			
			if (claim.getUser() == id) {
				tagsToRemove.add(entry.getKey());
			}
		}
		
		for (UUID tagID : tagsToRemove) {
			internalDeleteTag(tagID);
		}
		
		// Finally, delete the User
		users.remove(id);
	}

    /**
     * Add a Claim internally.
     * @param c The Claim to add.
     */
    protected void internalAddClaim(Claim c) {
        claims.put(c.getUUID(), c);
        c.addObserver(this);
    }
	
	/**
	 * Delete a Claim internally, cleaning up any orphan Documents.
	 * @param id The Claim's UUID.
	 */
	protected void internalDeleteClaim(UUID id) {
		// Get all child Claims
		ArrayList<UUID> itemsToRemove = new ArrayList<UUID>();
		for (Entry<UUID, Item> entry : items.entrySet()) {
			Item item = entry.getValue();
			
			if (item.getClaim() == id) {
				itemsToRemove.add(entry.getKey());
			}
		}
		
		// Handle in a separate loop to avoid modifying while iterating
		for (UUID claimID : itemsToRemove) {
			internalDeleteItem(claimID);
		}
		
		// Finally, delete the Claim
		claims.remove(id);
	}
    
    /**
     * Add an Item internally. 
     * @param i The Item to add.
     */
    protected void internalAddItem(Item i) {
        items.put(i.getUUID(), i);
        i.addObserver(this);
    }
	
	/**
	 * Delete an Item internally.
	 * @param id The Item's ID.
	 */
	protected void internalDeleteItem(UUID id) {
		items.remove(id);
	}
    
    /**
     * Add a Tag internally.
     * @param t The Tag to add.
     */
    protected void internalAddTag(Tag t) {
        tags.put(t.getUUID(), t);
        t.addObserver(this);
    }
	
	/**
	 * Delete a Tag internally.
	 * @param id The Tag's ID.
	 */
	protected void internalDeleteTag(UUID id) {
		tags.remove(id);
	}
}
