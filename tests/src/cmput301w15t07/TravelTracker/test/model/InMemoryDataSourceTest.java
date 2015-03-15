package cmput301w15t07.TravelTracker.test.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import cmput301w15t07.TravelTracker.model.Claim;
import cmput301w15t07.TravelTracker.model.InMemoryDataSource;
import cmput301w15t07.TravelTracker.model.Item;
import cmput301w15t07.TravelTracker.model.Tag;
import cmput301w15t07.TravelTracker.model.User;
import cmput301w15t07.TravelTracker.testutils.SynchronizedResultCallback;
import junit.framework.TestCase;

public class InMemoryDataSourceTest extends TestCase {
	/** The data source under test. */
	InMemoryDataSource source;
	
	/** Maximum time to wait for a callback */
	int timeout = 3000;
	
	public InMemoryDataSourceTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		
		source = new InMemoryDataSource();
	}
	

	////////////////////////
	// Test add functions //
	////////////////////////
	
	public void testAddUser() throws InterruptedException {
		assertNotNull("New user should be returned", addUser());
	}

	public void testAddClaim() throws InterruptedException {
		User user = addUser();
		Claim claim = addClaim(user);
	    
		assertNotNull("New claim should be returned", claim);
	}

	public void testAddItem() throws InterruptedException {
		User user = addUser();
		Claim claim = addClaim(user);
		Item item = addItem(claim);
	    
		assertNotNull("New user should be returned", item);
	}

	public void testAddTag() throws InterruptedException {
		User user = addUser();
		Tag tag = addTag(user);
	    
		assertNotNull("New tag should be returned", tag);
	}

	public void testAddClaimToNonexistentUser() throws InterruptedException {
		User user = addUser();

		SynchronizedResultCallback<Void> userCallback = new SynchronizedResultCallback<Void>();
		source.deleteUser(user.getUUID(), userCallback);
		userCallback.waitForResult();
		
		SynchronizedResultCallback<Claim> claimCallback = new SynchronizedResultCallback<Claim>();
		source.addClaim(user, claimCallback);
	    
		assertFalse("No result should be returned", claimCallback.waitForResult());
		assertNotNull("Error should be returned", claimCallback.getError());
	}

	public void testAddItemToNonexistentClaim() throws InterruptedException {
		User user = addUser();
		Claim claim = addClaim(user);

		SynchronizedResultCallback<Void> claimCallback = new SynchronizedResultCallback<Void>();
		source.deleteClaim(claim.getUUID(), claimCallback);
		claimCallback.waitForResult();
		
		SynchronizedResultCallback<Item> itemCallback = new SynchronizedResultCallback<Item>();
		source.addItem(claim, itemCallback);
	    
		assertFalse("No result should be returned", itemCallback.waitForResult());
		assertNotNull("Error should be returned", itemCallback.getError());
	}

	public void testAddTagToNonexistentUser() throws InterruptedException {
		User user = addUser();

		SynchronizedResultCallback<Void> userCallback = new SynchronizedResultCallback<Void>();
		source.deleteUser(user.getUUID(), userCallback);
		userCallback.waitForResult();
		
		SynchronizedResultCallback<Tag> tagCallback = new SynchronizedResultCallback<Tag>();
		source.addTag(user, tagCallback);
	    
		assertFalse("No result should be returned", tagCallback.waitForResult());
		assertNotNull("Error should be returned", tagCallback.getError());
	}
	

	////////////////////////
	// Test get functions //
	////////////////////////
	
	public void testGetUser() throws InterruptedException {
		User user = addUser();

		SynchronizedResultCallback<User> callback = new SynchronizedResultCallback<User>();
		source.getUser(user.getUUID(), callback);
		callback.waitForResult();
		
		assertEquals("Same user should be returned", user, callback.getResult());
	}
	
	public void testGetClaim() throws InterruptedException {
		User user = addUser();
		Claim claim = addClaim(user);

		SynchronizedResultCallback<Claim> callback = new SynchronizedResultCallback<Claim>();
		source.getClaim(claim.getUUID(), callback);
		callback.waitForResult();
		
		assertEquals("Same claim should be returned", claim, callback.getResult());
	}
	
	public void testGetItem() throws InterruptedException {
		User user = addUser();
		Claim claim = addClaim(user);
		Item item = addItem(claim);

		SynchronizedResultCallback<Item> callback = new SynchronizedResultCallback<Item>();
		source.getItem(item.getUUID(), callback);
		callback.waitForResult();
		
		assertEquals("Same item should be returned", item, callback.getResult());
	}
	
	public void testGetTag() throws InterruptedException {
		User user = addUser();
		Tag tag = addTag(user);

		SynchronizedResultCallback<Tag> callback = new SynchronizedResultCallback<Tag>();
		source.getTag(tag.getUUID(), callback);
		callback.waitForResult();
		
		assertEquals("Same tag should be returned", tag, callback.getResult());
	}
	
	public void testGetNonexistentUser() throws InterruptedException {
		SynchronizedResultCallback<User> callback = new SynchronizedResultCallback<User>();
		source.getUser(UUID.randomUUID(), callback);
		callback.waitForResult();
		
		assertFalse("No result should be returned", callback.getHasResult());
		assertNotNull("Error should be returned", callback.getError());
	}
	
	public void testGetNonexistentClaim() throws InterruptedException {
		SynchronizedResultCallback<Claim> callback = new SynchronizedResultCallback<Claim>();
		source.getClaim(UUID.randomUUID(), callback);
		callback.waitForResult();
		
		assertFalse("No result should be returned", callback.getHasResult());
		assertNotNull("Error should be returned", callback.getError());
	}
	
	public void testGetNonexistentItem() throws InterruptedException {
		SynchronizedResultCallback<Item> callback = new SynchronizedResultCallback<Item>();
		source.getItem(UUID.randomUUID(), callback);
		callback.waitForResult();
		
		assertFalse("No result should be returned", callback.getHasResult());
		assertNotNull("Error should be returned", callback.getError());
	}
	
	public void testGetNonexistentTag() throws InterruptedException {
		SynchronizedResultCallback<Tag> callback = new SynchronizedResultCallback<Tag>();
		source.getTag(UUID.randomUUID(), callback);
		callback.waitForResult();
		
		assertFalse("No result should be returned", callback.getHasResult());
		assertNotNull("Error should be returned", callback.getError());
	}
	

	///////////////////////////
	// Test delete functions //
	///////////////////////////

	public void testDeleteUser() throws InterruptedException {
		User user = addUser();

		SynchronizedResultCallback<Void> deleteCallback = new SynchronizedResultCallback<Void>();
		source.deleteUser(user.getUUID(), deleteCallback);
		
		assertTrue("Delete callback should return", deleteCallback.waitForResult());

		SynchronizedResultCallback<User> userCallback = new SynchronizedResultCallback<User>();
		source.getUser(user.getUUID(), userCallback);
		userCallback.waitForResult();
		
		assertNotNull("User should no longer exist", userCallback.getError());
	}

	public void testDeleteClaim() throws InterruptedException {
		User user = addUser();
		Claim claim = addClaim(user);

		SynchronizedResultCallback<Void> deleteCallback = new SynchronizedResultCallback<Void>();
		source.deleteClaim(claim.getUUID(), deleteCallback);
		
		assertTrue("Delete callback should return", deleteCallback.waitForResult());

		SynchronizedResultCallback<Claim> claimCallback = new SynchronizedResultCallback<Claim>();
		source.getClaim(claim.getUUID(), claimCallback);
		claimCallback.waitForResult();
		
		assertNotNull("Claim should no longer exist", claimCallback.getError());
	}

	public void testDeleteItem() throws InterruptedException {
		User user = addUser();
		Claim claim = addClaim(user);
		Item item = addItem(claim);

		SynchronizedResultCallback<Void> deleteCallback = new SynchronizedResultCallback<Void>();
		source.deleteItem(item.getUUID(), deleteCallback);
		
		assertTrue("Delete callback should return", deleteCallback.waitForResult());

		SynchronizedResultCallback<Item> itemCallback = new SynchronizedResultCallback<Item>();
		source.getItem(item.getUUID(), itemCallback);
		
		assertNotNull("Item should no longer exist", itemCallback.getError());
	}

	public void testDeleteTag() throws InterruptedException {
		User user = addUser();
		Tag tag = addTag(user);

		SynchronizedResultCallback<Void> deleteCallback = new SynchronizedResultCallback<Void>();
		source.deleteTag(tag.getUUID(), deleteCallback);
		
		assertTrue("Delete callback should return", deleteCallback.waitForResult());

		SynchronizedResultCallback<Tag> tagCallback = new SynchronizedResultCallback<Tag>();
		source.getTag(tag.getUUID(), tagCallback);
		
		assertNotNull("Tag should no longer exist", tagCallback.getError());
	}
	
	public void testDeleteNonexistentUser() throws InterruptedException {
		SynchronizedResultCallback<Void> callback = new SynchronizedResultCallback<Void>();
		source.deleteUser(UUID.randomUUID(), callback);
		
		assertFalse("No result should be returned", callback.waitForResult());
		assertNotNull("Error should be returned", callback.getError());
	}
	
	public void testDeleteNonexistentClaim() throws InterruptedException {
		SynchronizedResultCallback<Void> callback = new SynchronizedResultCallback<Void>();
		source.deleteClaim(UUID.randomUUID(), callback);
		
		assertFalse("No result should be returned", callback.waitForResult());
		assertNotNull("Error should be returned", callback.getError());
	}
	
	public void testDeleteNonexistentItem() throws InterruptedException {
		SynchronizedResultCallback<Void> callback = new SynchronizedResultCallback<Void>();
		source.deleteItem(UUID.randomUUID(), callback);
		
		assertFalse("No result should be returned", callback.waitForResult());
		assertNotNull("Error should be returned", callback.getError());
	}
	
	public void testDeleteNonexistentTag() throws InterruptedException {
		SynchronizedResultCallback<Void> callback = new SynchronizedResultCallback<Void>();
		source.deleteTag(UUID.randomUUID(), callback);
		
		assertFalse("No result should be returned", callback.waitForResult());
		assertNotNull("Error should be returned", callback.getError());
	}

	/* 
	 * Note: the following deletion tests are only applicable because InMemoryDataSource immediately
	 * returns a result. If this were asynchronous, orphan Documents would be cleaned up in the
	 * background at an unknown time. So if you grab this code to adapt for use with actual delayed
	 * calls... be warned.
	 */
	public void testDeleteUserWithClaims() throws InterruptedException {
		User user = addUser();
		Claim claim1 = addClaim(user);
		Claim claim2 = addClaim(user);

		SynchronizedResultCallback<Void> callback = new SynchronizedResultCallback<Void>();
		source.deleteUser(user.getUUID(), callback);

		SynchronizedResultCallback<Claim> claimCallback = new SynchronizedResultCallback<Claim>();
		source.getClaim(claim1.getUUID(), claimCallback);
		claimCallback.waitForResult();
		
		assertNotNull("First claim should not exist", claimCallback.getError());

		claimCallback = new SynchronizedResultCallback<Claim>();
		source.getClaim(claim2.getUUID(), claimCallback);
		claimCallback.waitForResult();
		
		assertNotNull("Second claim should not exist", claimCallback.getError());
	}

	public void testDeleteUserWithTags() throws InterruptedException {
		User user = addUser();
		Tag tag1 = addTag(user);
		Tag tag2 = addTag(user);

		SynchronizedResultCallback<Void> callback = new SynchronizedResultCallback<Void>();
		source.deleteUser(user.getUUID(), callback);

		SynchronizedResultCallback<Tag> tagCallback = new SynchronizedResultCallback<Tag>();
		source.getTag(tag1.getUUID(), tagCallback);
		tagCallback.waitForResult();
		
		assertNotNull("First tag should not exist", tagCallback.getError());

		tagCallback = new SynchronizedResultCallback<Tag>();
		source.getTag(tag2.getUUID(), tagCallback);
		tagCallback.waitForResult();
		
		assertNotNull("Second tag should not exist", tagCallback.getError());
	}

	public void testDeleteClaimWithItems() throws InterruptedException {
		User user = addUser();
		Claim claim = addClaim(user);
		Item item1 = addItem(claim);
		Item item2 = addItem(claim);

		SynchronizedResultCallback<Void> callback = new SynchronizedResultCallback<Void>();
		source.deleteClaim(claim.getUUID(), callback);

		SynchronizedResultCallback<Item> itemCallback = new SynchronizedResultCallback<Item>();
		source.getItem(item1.getUUID(), itemCallback);
		itemCallback.waitForResult();
		
		assertNotNull("First item should not exist", itemCallback.getError());

		itemCallback = new SynchronizedResultCallback<Item>();
		source.getItem(item2.getUUID(), itemCallback);
		itemCallback.waitForResult();
		
		assertNotNull("Second item should not exist", itemCallback.getError());
	}
	

	///////////////////////////
	// Test getAll functions //
	///////////////////////////
	
	public void testGetAllUsers() throws InterruptedException {
		ArrayList<User> users = new ArrayList<User>();
		users.add(addUser());
		users.add(addUser());
		users.add(addUser());

		SynchronizedResultCallback<Collection<User>> callback = new SynchronizedResultCallback<Collection<User>>();
		source.getAllUsers(callback);
		callback.waitForResult();
		Collection<User> newUsers = callback.getResult(); 
		
		assertEquals("Lists should be the same size", users.size(), newUsers.size());
		assertTrue("Both lists should have the same contents", newUsers.containsAll(users));
	}

	public void testGetAllClaims() throws InterruptedException {
		User user = addUser();
		ArrayList<Claim> claims = new ArrayList<Claim>();
		claims.add(addClaim(user));
		claims.add(addClaim(user));
		claims.add(addClaim(user));

		SynchronizedResultCallback<Collection<Claim>> callback = new SynchronizedResultCallback<Collection<Claim>>();
		source.getAllClaims(callback);
		callback.waitForResult();
		Collection<Claim> newClaims = callback.getResult();
		
		assertEquals("Lists should be the same size", claims.size(), newClaims.size());
		assertTrue("Both lists should have the same contents", newClaims.containsAll(claims));
	}

	public void testGetAllItems() throws InterruptedException {
		User user = addUser();
		Claim claim = addClaim(user);
		ArrayList<Item> items = new ArrayList<Item>();
		items.add(addItem(claim));
		items.add(addItem(claim));
		items.add(addItem(claim));
		
		SynchronizedResultCallback<Collection<Item>> callback = new SynchronizedResultCallback<Collection<Item>>();
		source.getAllItems(callback);
		callback.waitForResult();
		Collection<Item> newItems = callback.getResult();
		
		assertEquals("Lists should be the same size", items.size(), newItems.size());
		assertTrue("Both lists should have the same contents", newItems.containsAll(items));
	}

	public void testGetAllTags() throws InterruptedException {
		User user = addUser();
		ArrayList<Tag> tags = new ArrayList<Tag>();
		tags.add(addTag(user));

		SynchronizedResultCallback<Collection<Tag>> callback = new SynchronizedResultCallback<Collection<Tag>>();
		source.getAllTags(callback);
		callback.waitForResult();
		Collection<Tag> newTags = callback.getResult();
		
		assertEquals("Lists should be the same size", tags.size(), newTags.size());
		assertTrue("Both lists should have the same contents", newTags.containsAll(tags));
	}
	
	public void testGetAllUsersEmpty() throws InterruptedException {
		SynchronizedResultCallback<Collection<User>> callback = new SynchronizedResultCallback<Collection<User>>();
		source.getAllUsers(callback);
		callback.waitForResult();
		Collection<User> users = callback.getResult();
		
		assertTrue("Empty collection should be returned", users.size() == 0);
	}
	
	public void testGetAllClaimsEmpty() throws InterruptedException {
		SynchronizedResultCallback<Collection<Claim>> callback = new SynchronizedResultCallback<Collection<Claim>>();
		source.getAllClaims(callback);
		callback.waitForResult();
		Collection<Claim> users = callback.getResult();
		
		assertTrue("Empty collection should be returned", users.size() == 0);
	}
	
	public void testGetAllItemsEmpty() throws InterruptedException {
		SynchronizedResultCallback<Collection<Item>> callback = new SynchronizedResultCallback<Collection<Item>>();
		source.getAllItems(callback);
		callback.waitForResult();
		Collection<Item> users = callback.getResult();
		
		assertTrue("Empty collection should be returned", users.size() == 0);
	}
	
	public void testGetAllTagsEmpty() throws InterruptedException {
		SynchronizedResultCallback<Collection<Tag>> callback = new SynchronizedResultCallback<Collection<Tag>>();
		source.getAllTags(callback);
		callback.waitForResult();
		Collection<Tag> users = callback.getResult();
		
		assertTrue("Empty collection should be returned", users.size() == 0);
	}
	

	//////////////////////
	// Helper functions //
	//////////////////////

	/**
	 * Add a User to the DataSource.
	 * @return The User, or null if there was an error.
	 * @throws InterruptedException 
	 */
	private User addUser() throws InterruptedException {
		SynchronizedResultCallback<User> callback = new SynchronizedResultCallback<User>();
		source.addUser(callback);
		callback.waitForResult();
		
		return callback.getResult();
	}

	/**
	 * Add a Claim to the DataSource.
	 * @param user The User to which it belongs.
	 * @return The Claim, or null if there was an error.
	 * @throws InterruptedException 
	 */
	private Claim addClaim(User user) throws InterruptedException {
		SynchronizedResultCallback<Claim> callback = new SynchronizedResultCallback<Claim>();
		source.addClaim(user, callback);
		callback.waitForResult();
		
		return callback.getResult();
	}

	/**
	 * Add an Item to the DataSource.
	 * @param claim The Claim to which it belongs.
	 * @return The Item, or null if there was an error.
	 * @throws InterruptedException 
	 */
	private Item addItem(Claim claim) throws InterruptedException {
		SynchronizedResultCallback<Item> callback = new SynchronizedResultCallback<Item>();
		source.addItem(claim, callback);
		callback.waitForResult();
		
		return callback.getResult();
	}

	/**
	 * Add a Tag to the DataSource.
	 * @param user The User to which it belongs.
	 * @return The Tag, or null if there was an error.
	 * @throws InterruptedException 
	 */
	private Tag addTag(User user) throws InterruptedException {
		SynchronizedResultCallback<Tag> callback = new SynchronizedResultCallback<Tag>();
		source.addTag(user, callback);
		callback.waitForResult();
		
		return callback.getResult();
	}
}
