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

import cmput301w15t07.TravelTracker.DataSourceSingleton;
import cmput301w15t07.TravelTracker.R;
import cmput301w15t07.TravelTracker.activity.ClaimInfoActivity;
import cmput301w15t07.TravelTracker.activity.ClaimsListActivity;
import cmput301w15t07.TravelTracker.activity.TravelTrackerActivity;
import cmput301w15t07.TravelTracker.model.Claim;
import cmput301w15t07.TravelTracker.model.DataSource;
import cmput301w15t07.TravelTracker.model.InMemoryDataSource;
import cmput301w15t07.TravelTracker.model.Status;
import cmput301w15t07.TravelTracker.model.User;
import cmput301w15t07.TravelTracker.model.UserData;
import cmput301w15t07.TravelTracker.model.UserRole;
import cmput301w15t07.TravelTracker.testutils.DataSourceUtils;
import android.app.Activity;
import android.app.Instrumentation.ActivityMonitor;
import android.content.Intent;
import android.os.Bundle;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Test for activity listing claims.
 * 
 * Each relevant Use Case UC.XxxYyy is tested with method testXxxYyy()
 * 
 * @author kdbanman,
 *         therabidsquirel
 *
 */
public class ClaimsListActivityTest extends ActivityInstrumentationTestCase2<ClaimsListActivity> {

    String name1 = "Bob";
    String name2 = "Alice";
    String name3 = "Josh";
    
    DataSource ds;
    
    User user1;
    User user2;
    
    Claim claim1;
    Claim claim2;
    Claim claim3;
    Claim claim4;
    Claim claim5;
    
    public ClaimsListActivityTest() {
        super(ClaimsListActivity.class);
    }
    
    @Override
    protected void setUp() throws Exception
    {
        // TODO Auto-generated method stub
        super.setUp();
        
        ds = new InMemoryDataSource();
        DataSourceSingleton.setDataSource(ds);
        
        user1 = DataSourceUtils.addUser(name1, ds);
        user2 = DataSourceUtils.addUser(name2, ds);
        
        claim1 = DataSourceUtils.addEmptyClaim(user1, ds);
        claim2 = DataSourceUtils.addEmptyClaim(user1, ds);
        claim3 = DataSourceUtils.addEmptyClaim(user1, ds);
        claim4 = DataSourceUtils.addEmptyClaim(user1, ds);
        claim5 = DataSourceUtils.addEmptyClaim(user2, ds);
    }
    
    public void testListExpenseClaimsClaimantCanSeeOnlyTheirClaims() {
        
        ClaimsListActivity activity = startActivity(new UserData(user1.getUUID(), user1.getUserName(), UserRole.CLAIMANT));
        ListView listView = (ListView) activity.findViewById(R.id.claimsListClaimListView);
        //ArrayAdapter<Claim> adapter =  (ArrayAdapter<Claim>) listView.getAdapter();
        
        assertEquals(4, listView.getCount());
    }
    
    public void testListExpenseClaimsApproverCanOnlySeeSubmitted() {
        
        claim1.setStatus(Status.SUBMITTED);
        claim2.setStatus(Status.SUBMITTED);

        ClaimsListActivity activity = startActivity(new UserData(user2.getUUID(), user2.getUserName(), UserRole.APPROVER));
        ListView listView = (ListView) activity.findViewById(R.id.claimsListClaimListView);
        
        assertEquals(2, listView.getCount());
    }
    
    public void testListExpenseClaimsApproverCannotSeeOwnClaims() {
        
        claim5.setStatus(Status.SUBMITTED);
        
        ClaimsListActivity activity = startActivity(new UserData(user2.getUUID(), user2.getUserName(), UserRole.APPROVER));
        ListView listView = (ListView) activity.findViewById(R.id.claimsListClaimListView);
        
        
        assertEquals(0, listView.getCount());
    }
    
    public void testListViewUpdates() throws Throwable{
        
        final ClaimsListActivity activity = startActivity(new UserData(user2.getUUID(), user2.getUserName(), UserRole.APPROVER));
        ListView listView = (ListView) activity.findViewById(R.id.claimsListClaimListView);

        assertEquals(0, listView.getCount());
        
        claim4.setStatus(Status.SUBMITTED);

        getInstrumentation().waitForIdleSync();
        Thread.sleep(300);
        
        assertEquals(1, listView.getCount());
    }
    
    public void testCreateExpenseClaim() throws Throwable {
        
        ClaimsListActivity activity = startActivity(new UserData(user2.getUUID(), user2.getUserName(), UserRole.CLAIMANT));
        ActivityMonitor monitor = getInstrumentation().addMonitor(ClaimInfoActivity.class.getName(), null, false);
        ListView listView = (ListView) activity.findViewById(R.id.claimsListClaimListView);
        
        assertEquals(1, listView.getCount());
        
        boolean success = getInstrumentation().invokeMenuActionSync(activity, R.id.claims_list_add_claim, 0);
        assertTrue(success);
        Activity newActivity = monitor.waitForActivityWithTimeout(3000);
        assertNotNull(newActivity);
        newActivity.finish();
        getInstrumentation().waitForIdleSync();
        Thread.sleep(300);
        assertEquals(2, listView.getCount());

    }
    
    
    public void testEditExpenseClaim() throws Throwable {
        final ClaimsListActivity activity = startActivity(new UserData(user2.getUUID(), user2.getUserName(), UserRole.CLAIMANT));
        ActivityMonitor monitor = getInstrumentation().addMonitor(ClaimInfoActivity.class.getName(), null, false);
        final ListView listView = (ListView) activity.findViewById(R.id.claimsListClaimListView);
        final ArrayAdapter<Claim> adapter =  (ArrayAdapter<Claim>) listView.getAdapter();
        final int position = 0;
        
        runTestOnUiThread(new Runnable()
        {
            
            @Override
            public void run()
            {
                
                boolean success = listView.performItemClick(adapter.getView(position, null, null), position, adapter.getItemId(position));
                assertTrue(success);
                
            }
        });
        
        Activity newActivity = monitor.waitForActivityWithTimeout(3000);
        assertNotNull(newActivity);
        validateIntent(user2, adapter.getItem(position), newActivity);
        newActivity.finish();
        getInstrumentation().waitForIdleSync();
        
        
    }
    
    public void testDeleteExpenseClaim() throws Throwable {
        final ClaimsListActivity activity = startActivity(new UserData(user2.getUUID(), user2.getUserName(), UserRole.CLAIMANT));
        ListView listView = (ListView) activity.findViewById(R.id.claimsListClaimListView);
        assertEquals(1, listView.getCount());
        
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                DataSourceUtils.deleteClaim(claim5, ds);
                getInstrumentation().callActivityOnResume(activity);
            }
        });
        
        getInstrumentation().waitForIdleSync();
        listView = (ListView) activity.findViewById(R.id.claimsListClaimListView);
        assertEquals(0, listView.getCount());
    }
    
    public void testViewExpenseClaimApprover() throws Throwable {
        claim1.setStatus(Status.SUBMITTED);
        final ClaimsListActivity activity = startActivity(new UserData(user2.getUUID(), user2.getUserName(), UserRole.APPROVER));
        ActivityMonitor monitor = getInstrumentation().addMonitor(ClaimInfoActivity.class.getName(), null, false);
        final ListView listView = (ListView) activity.findViewById(R.id.claimsListClaimListView);
        final ArrayAdapter<Claim> adapter =  (ArrayAdapter<Claim>) listView.getAdapter();
        
        assertEquals("Should only be 1 submitted claim", 1, listView.getCount());
        
        
        runTestOnUiThread(new Runnable()
        {
            
            @Override
            public void run()
            {
                int position = 0;
                boolean success = listView.performItemClick(adapter.getView(position, null, null), position, adapter.getItemId(position));
                assertTrue(success);
                
            }
        });
        
        Activity newActivity = monitor.waitForActivityWithTimeout(3000);
        assertNotNull(newActivity);
        validateIntent(user2, claim1, newActivity);
        newActivity.finish();
        getInstrumentation().waitForIdleSync();
        assertEquals("Count should not have changed", 1, listView.getCount());
    }
    
    public void testFilterClaimsByTag() {
        
    }
    
    private void validateIntent(User user, Claim claim, Activity openedActivity){
        Bundle bundle = openedActivity.getIntent().getExtras();
        UserData userData = (UserData) bundle.getSerializable(TravelTrackerActivity.USER_DATA);
        assertEquals(user.getUUID(), userData.getUUID());
        assertEquals(claim.getUUID(), bundle.getSerializable(TravelTrackerActivity.CLAIM_UUID));
    }
    
    private ClaimsListActivity startActivity(UserData data) {
        Intent intent = new Intent();
        intent.putExtra(ClaimsListActivity.USER_DATA, data);
        setActivityIntent(intent);
        ClaimsListActivity activity = getActivity();
        
        try {
            activity.waitUntilLoaded();
        } catch (InterruptedException e){
            fail("Could not load activity!");
        }
        
        getInstrumentation().waitForIdleSync();
        
        return activity;
    }

}
