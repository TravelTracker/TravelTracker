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

package cmput301w15t07.TravelTracker.test.activity;

import java.util.Collection;
import cmput301w15t07.TravelTracker.DataSourceSingleton;
import cmput301w15t07.TravelTracker.R;
import cmput301w15t07.TravelTracker.activity.ExpenseItemsListActivity;
import cmput301w15t07.TravelTracker.model.Claim;
import cmput301w15t07.TravelTracker.model.DataSource;
import cmput301w15t07.TravelTracker.model.InMemoryDataSource;
import cmput301w15t07.TravelTracker.model.Item;
import cmput301w15t07.TravelTracker.model.User;
import cmput301w15t07.TravelTracker.testutils.DataSourceUtils;
import cmput301w15t07.TravelTracker.testutils.SynchronizedResultCallback;
import cmput301w15t07.TravelTracker.util.ExpenseItemsListAdapter;
import android.app.Instrumentation;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Suppress;
import android.util.Log;
import android.widget.ListView;

/**
 * Test for expense item list activities.
 * 
 * Each relevant Use Case UC.XxxYyy is tested with method testXxxYyy()
 * 
 * @author kdbanman,
 *         braedy
 *
 */
public class ExpenseItemsListActivityTest extends ActivityInstrumentationTestCase2<ExpenseItemsListActivity> {
	// String constants in testing
	private static final String CLAIMANT_USER_NAME = "claimant";
	private static final String APPROVER_USER_NAME = "approver";
    private static final String LOG_TAG = "ExpenseItemsListTest";
	
    //  App relevant
    private DataSource dataSource;
    private Instrumentation instrumentation;
    
    // Test relevant
    private User claimant;
    private User approver;
    private Claim claim;
    
	public ExpenseItemsListActivityTest() {
		super(ExpenseItemsListActivity.class);
	}
	
	@Override
	protected void setUp() throws Exception {
	    // Use generated data.
        dataSource = new InMemoryDataSource();
        DataSourceSingleton.setDataSource(dataSource);
        
	    super.setUp();
        
        instrumentation = getInstrumentation();

        // Add claimant and approver
        claimant = DataSourceUtils.addUser(CLAIMANT_USER_NAME, dataSource);
        approver = DataSourceUtils.addUser(APPROVER_USER_NAME, dataSource);
        
        // Empty, no specific data needed. Just items later.
        claim = DataSourceUtils.addEmptyClaim(claimant, dataSource);
	}
	
	public void testPreconditions() throws InterruptedException {
		// Test that the users exist
		assertTrue("Claimant didn't exist.", userExists(CLAIMANT_USER_NAME));
		assertTrue("Approver didn't exist.", userExists(APPROVER_USER_NAME));
		
		// Test that the claimant has a claim
		assertTrue("Claimant didn't have claim.", hasClaim(claimant));
	}

	@Suppress
	public void testListExpenseItems() {
		ExpenseItemsListActivity activity =
				startActivityAsClaimant();
		
		ListView lv = (ListView) activity.findViewById(
				R.id.expenseItemsListListView);
		ExpenseItemsListAdapter adapter =
				(ExpenseItemsListAdapter) lv.getAdapter();
		
		// Assert empty
		assertEquals("List view was not empty.", 0, adapter.getCount());
		
		// Causes main thread activity
		Item i = DataSourceUtils.addEmptyItem(claim, dataSource);
		
		// Assert 1 element
		assertEquals("List view did not have 1 element: " + adapter.getCount(),
				1, adapter.getCount());
		assertEquals("First element was not the item added.", i,
				adapter.getItem(0));
	}
	
	public void testCreateExpenseItem() {
		
	}
	
	public void testViewExpenseItem() {
		
	}
	
	public void testEditExpenseItem() {
		
	}
	
	public void testDeleteExpenseItem() {
		
	}
	
    //////////////////////
    // Helper functions //
    //////////////////////
	/**
	 * @return The started activity
	 */
	public ExpenseItemsListActivity startActivityAsClaimant() {
        // Set up start intent
        Intent intent = new Intent(instrumentation.getTargetContext(),
        		ExpenseItemsListActivity.class);
        
        intent.putExtra(ExpenseItemsListActivity.USER_DATA, 
        		DataSourceUtils.getUserDataClaimant(CLAIMANT_USER_NAME));
        
        intent.putExtra(ExpenseItemsListActivity.CLAIM_UUID, claim.getUUID());
        setActivityIntent(intent);
        
        ExpenseItemsListActivity activity = getActivity();
        Log.d(LOG_TAG, activity.toString());
		return activity;
	}
	
    /**
     * Tests if a user is in the datasource.
     * 
     * @param name Name of the User.
     * @return true if a user with username name exists else false
     * @throws InterruptedException
     */
    private boolean userExists(String name) throws InterruptedException {
        SynchronizedResultCallback<Collection<User>> callback =
        		new SynchronizedResultCallback<Collection<User>>();
        dataSource.getAllUsers(callback);
        
        boolean success = callback.waitForResult();
        assertTrue("Failed getting Users.", success);
        
        Collection<User> users = callback.getResult();
        
        // Find the user and report true
        for (User u : users) {
        	if (u.getUserName().equals(name)) {
        		return true;
        	}
        }
        
        // Report it doesn't exist
        return false;
    }
	
	/**
	 * Checks if a user has at least one claim.
	 * 
	 * @param claimant Name of the User.
	 * @return true if the user has at least one claim attached.
	 * @throws InterruptedException 
	 */
	private boolean hasClaim(User claimant) throws InterruptedException {
		SynchronizedResultCallback<Collection<Claim>> callback =
				new SynchronizedResultCallback<Collection<Claim>>();
		dataSource.getAllClaims(callback);
        
        boolean success = callback.waitForResult();
        assertTrue("Failed getting claims.", success);
        
        Collection<Claim> claims = callback.getResult();
        
        // Find a claim and report true
        for (Claim c : claims) {
        	if(c.getUser().equals(claimant.getUUID())) {
        		return true;
        	}
        }
        
        // report no claim exists
		return false;
	}
    
}
