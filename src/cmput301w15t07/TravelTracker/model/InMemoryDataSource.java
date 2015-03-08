package cmput301w15t07.TravelTracker.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import cmput301w15t07.TravelTracker.serverinterface.ResultCallback;
import cmput301w15t07.TravelTracker.util.Observable;
import cmput301w15t07.TravelTracker.util.Observer;

public class InMemoryDataSource extends Observable<InMemoryDataSource> implements DataSource, Observer<Document> {

	private HashMap<UUID, Claim> claims;
	private HashMap<UUID, User> users;
	private HashMap<UUID, Item> items;
	private HashMap<UUID, Tag> tags;

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
		
		users.put(user.getUUID(), user);
		
		callback.onResult(user);
	}
	
	@Override
	public void addClaim(User user, ResultCallback<Claim> callback) {
		Claim claim = new Claim(UUID.randomUUID());
		
		if (!users.containsValue(user)) {
			callback.onError("User not found.");
			return;
		}
		
		claim.setUser(user.getUUID());
		claims.put(claim.getUUID(), claim);
		
		callback.onResult(claim);
	}

	@Override
	public void addItem(Claim claim, ResultCallback<Item> callback) {
		Item item = new Item(UUID.randomUUID());
		
		item.setClaim(claim.getUUID());
		items.put(item.getUUID(), item);
		
		if (!claims.containsValue(claim)) {
			callback.onError("Claim not found.");
			return;
		}
		
		callback.onResult(item);
	}

	@Override
	public void addTag(User user, ResultCallback<Tag> callback) {
		Tag tag = new Tag(UUID.randomUUID());
		
		tag.setUser(user.getUUID());
		tags.put(tag.getUUID(), tag);
		
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
			callback.onResult(user);
		}
	}

	@Override
	public void getClaim(UUID id, ResultCallback<Claim> callback) {
		Claim claim = claims.get(id);
		
		if (claim == null) {
			callback.onError("Claim not found.");
		} else {
			callback.onResult(claim);
		}
	}

	@Override
	public void getItem(UUID id, ResultCallback<Item> callback) {
		Item item = items.get(id);
		
		if (item == null) {
			callback.onError("Item not found.");
		} else {
			callback.onResult(item);
		}
	}

	@Override
	public void getTag(UUID id, ResultCallback<Tag> callback) {
		Tag tag = tags.get(id);
		
		if (tag == null) {
			callback.onError("Tag not found.");
		} else {
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
	 * Delete a User internally, cleaning up any orphan Documents.
	 * @param id The User's UUID.
	 */
	private void internalDeleteUser(UUID id) {
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
	 * Delete a Claim internally, cleaning up any orphan Documents.
	 * @param id The Claim's UUID.
	 */
	private void internalDeleteClaim(UUID id) {
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
	 * Delete an Item internally.
	 * @param id The Item's ID.
	 */
	private void internalDeleteItem(UUID id) {
		items.remove(id);
	}
	
	/**
	 * Delete a Tag internally.
	 * @param id The Tag's ID.
	 */
	private void internalDeleteTag(UUID id) {
		tags.remove(id);
	}
}
