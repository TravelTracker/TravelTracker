package cmput301w15t07.TravelTracker.test.model;

import java.util.Collection;
import java.util.UUID;

import cmput301w15t07.TravelTracker.model.Claim;
import cmput301w15t07.TravelTracker.model.GeneratedDataSource;
import cmput301w15t07.TravelTracker.model.Item;
import cmput301w15t07.TravelTracker.model.Tag;
import cmput301w15t07.TravelTracker.model.User;
import cmput301w15t07.TravelTracker.testutils.SynchronizedResultCallback;
import junit.framework.TestCase;

public class GeneratedDataSourceTest extends TestCase {
    /** The data source under test. */
    GeneratedDataSource source;
    private final int NUM_CLAIMS = 10;
    private final int NUM_ITEMS = 10;
    private final int NUM_TAGS = 10;
    
    public GeneratedDataSourceTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        
        source = new GeneratedDataSource();
    }
    
    public void testAddUser() throws InterruptedException {
        User user = addUser();
        assertNotNull("New user should be returned.", user);
        
        User user2 = getUser(user.getUUID());
        assertNotNull("New user wasn't added to datasource.", user2);
        assertEquals("Users from addUser and getUser didn't match.", user, user2);
        
        Collection<Claim> claims = getClaims();
        
        // Should have 10 claims
        assertEquals("Got incorrect number of claims: " + claims.size(), NUM_CLAIMS, claims.size());
        
        // All Claims should have the one User as parent
        for (Claim c : claims) {
            assertEquals("Claim didn't have User as parent.", user.getUUID(), c.getUser());
        }
        
        Collection<Item> items = getItems();
        
        // All claims should have 10 items
        for(Claim c : claims) {
            int count = countItemsPerClaim(c.getUUID(), items);
            assertEquals("Got incorrect amount of items: " + count, NUM_ITEMS, count);
        }
        
        Collection<Tag> tags = getTags();
        
        // Should have 10 tags
        assertEquals("Got incorrect number of tags: " + tags.size(), NUM_TAGS, tags.size());
        
        // All tags should have the one User as parent
        for (Tag t : tags) {
            assertEquals("Tag didn't have User as parent.", user.getUUID(), t.getUser());
        }
    }

    //////////////////////
    // Helper functions //
    //////////////////////
    /**
     * Add a User to the DataSource.
     * Ripped straight out of the InMemoryDataSourceTest.
     * 
     * @return The User, or null if there was an error.
     * @throws InterruptedException 
     */
    private User addUser() throws InterruptedException {
        SynchronizedResultCallback<User> callback = new SynchronizedResultCallback<User>();
        source.addUser(callback);
        
        boolean success = callback.waitForResult();
        assertTrue("Failed adding User.", success);
        
        return callback.getResult();
    }
    
    /**
     * Add a User to the DataSource.
     * Ripped straight out of the InMemoryDataSourceTest.
     * 
     * @return The User, or null if there was an error.
     * @throws InterruptedException 
     */
    private User getUser(UUID id) throws InterruptedException {
        SynchronizedResultCallback<User> callback = new SynchronizedResultCallback<User>();
        source.getUser(id, callback);
        
        boolean success = callback.waitForResult();
        assertTrue("Failed getting User.", success);
        
        return callback.getResult();
    }
    
    /**
     * Gets all claims from the DataSource.
     * 
     * @return The Collection of Claims.
     * @throws InterruptedException 
     */
    private Collection<Claim> getClaims() throws InterruptedException {
        SynchronizedResultCallback<Collection<Claim>> callback = new SynchronizedResultCallback<Collection<Claim>>();
        source.getAllClaims(callback);
        
        boolean success = callback.waitForResult();
        assertTrue("Failed getting Claims.", success);
        
        return callback.getResult();
    }
    
    private Collection<Item> getItems() throws InterruptedException {
        SynchronizedResultCallback<Collection<Item>> callback = new SynchronizedResultCallback<Collection<Item>>();
        source.getAllItems(callback);
        
        boolean success = callback.waitForResult();
        assertTrue("Failed getting Items", success);
        
        return callback.getResult();
        
    }
    
    private Collection<Tag> getTags() throws InterruptedException {
        SynchronizedResultCallback<Collection<Tag>> callback = new SynchronizedResultCallback<Collection<Tag>>();
        source.getAllTags(callback);
        
        boolean success = callback.waitForResult();
        assertTrue("Failed getting Tags", success);
        
        return callback.getResult();
        
    }
    
    /**
     * Counts the number of Items per Claim for a user.
     * 
     * @param uuid
     * @param items 
     * @return The number of Items per Claim if they're all similar or -1 if
     * claims have a different number of items.
     * @throws InterruptedException 
     */
    private int countItemsPerClaim(UUID claimID, Collection<Item> items) {
        int count = 0;
        
        for (Item i : items) {
            if (i.getClaim() == claimID) {
                ++count;
            }
        }
        
        return count;
    }

}
