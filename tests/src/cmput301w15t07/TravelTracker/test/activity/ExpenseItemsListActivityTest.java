package cmput301w15t07.TravelTracker.test.activity;

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

import cmput301w15t07.TravelTracker.DataSourceSingleton;
import cmput301w15t07.TravelTracker.activity.ExpenseItemsListActivity;
import cmput301w15t07.TravelTracker.model.Claim;
import cmput301w15t07.TravelTracker.model.DataSource;
import cmput301w15t07.TravelTracker.model.GeneratedDataSource;
import cmput301w15t07.TravelTracker.model.User;
import cmput301w15t07.TravelTracker.model.UserData;
import cmput301w15t07.TravelTracker.model.UserRole;
import cmput301w15t07.TravelTracker.testutils.SynchronizedResultCallback;
import cmput301w15t07.TravelTracker.util.ExpenseItemsListAdapter;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.test.mock.MockApplication;
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
public class ExpenseItemsListActivityTest extends ActivityUnitTestCase<ExpenseItemsListActivity> {
    DataSource dataSource;
    Instrumentation instrumentation;
    Activity activity;
    
    User user;
    UserData userData;
    Collection<Claim> allClaims;
    Claim claim = null; // The specific claim we'll use items from
    
    ListView itemsList;
    ExpenseItemsListAdapter adapter;
    
    private static final String LOG_TAG = "ExpenseItemsListTest";
    

	public ExpenseItemsListActivityTest() {
		super(ExpenseItemsListActivity.class);
	}
	
	@Override
	protected void setUp() throws Exception {
	    // Use generated data.
        dataSource = new GeneratedDataSource();
        DataSourceSingleton.setDataSource(dataSource);
        
	    super.setUp();

		if (true) return;
        
        MockApplication app = new MockApplication();
        setApplication(app);
        
        
        instrumentation = getInstrumentation();

        // Get User
        SynchronizedResultCallback<User> userCallback = new SynchronizedResultCallback<User>();
        dataSource.addUser(userCallback);
        boolean success = userCallback.waitForResult();
        if (!success) {
            throw new RuntimeException("Couldn't add User, how can I test without a User?");
        }
        user = userCallback.getResult();
        userData = new UserData(user.getUUID(), user.getUserName(), UserRole.CLAIMANT);
        
        // Get all Claims
        SynchronizedResultCallback<Collection<Claim>> claimCallback = new SynchronizedResultCallback<Collection<Claim>>();
        dataSource.getAllClaims(claimCallback);
        success = claimCallback.waitForResult();
        if (!success) {
            throw new RuntimeException("Couldn't get Claims, why no Claims?");
        }
        allClaims = claimCallback.getResult();
        Log.d(LOG_TAG, "Length: " + allClaims.size());
        
        if (allClaims.isEmpty()) {
            throw new RuntimeException("Got no claims back.");
        }
        
        for (Claim c : allClaims) {
            if (c.getUser() == user.getUUID()) {
                claim = c;
                Log.d("ExpenseItemsListTest", c.toString());
                break;
            }
        }
        if (claim == null) {
            throw new RuntimeException("No claim for added user...");
        }
        
        Intent intent = new Intent(instrumentation.getTargetContext(), ExpenseItemsListActivity.class);
        intent.putExtra(ExpenseItemsListActivity.USER_DATA, userData);
        intent.putExtra(ExpenseItemsListActivity.CLAIM_UUID, claim.getUUID());
        startActivity(intent, null, null);

        activity = getActivity();
        itemsList = (ListView) activity.findViewById(cmput301w15t07.TravelTracker.R.id.itemsListListView);
        adapter = (ExpenseItemsListAdapter) itemsList.getAdapter();
	}
	
	public void testPreconditions() {
		if (true) return;
		
	    assertNotNull("Activity was null.", activity);
	    assertNotNull("ListView was null.", itemsList);
	    assertNotNull("Adapter was null.", adapter);
	}
	
	public void testListExpenseItems() {
		
	}
	
	public void testCreateExpenseItem() {
		
	}
	
	public void testViewExpenseItem() {
		
	}
	
	public void testEditExpenseItem() {
		
	}
	
	public void testDeleteExpenseItem() {
		
	}
}
