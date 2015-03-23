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

import java.util.ArrayList;

import cmput301w15t07.TravelTracker.DataSourceSingleton;
import cmput301w15t07.TravelTracker.R;
import cmput301w15t07.TravelTracker.activity.ClaimsListActivity;
import cmput301w15t07.TravelTracker.model.Claim;
import cmput301w15t07.TravelTracker.model.DataSource;
import cmput301w15t07.TravelTracker.model.InMemoryDataSource;
import cmput301w15t07.TravelTracker.model.User;
import cmput301w15t07.TravelTracker.model.UserData;
import cmput301w15t07.TravelTracker.model.UserRole;
import cmput301w15t07.TravelTracker.testutils.DataSourceUtils;
import android.app.Activity;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Test for activity listing claims.
 * 
 * Each relevant Use Case UC.XxxYyy is tested with method testXxxYyy()
 * 
 * @author kdbanman
 *
 */
public class ClaimsListActivityTest extends ActivityInstrumentationTestCase2<ClaimsListActivity> {

	String name1 = "Bob";
	String name2 = "Alice";
	String name3 = "Josh";
	
	DataSource ds;
	
	User user1;
	User user2;
	
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
	}
	
	public void testListExpenseClaimsClaimantCanSeeOnlyTheirClaims() {
		DataSourceUtils.addEmptyClaim(user1, ds);
		DataSourceUtils.addEmptyClaim(user1, ds);
		DataSourceUtils.addEmptyClaim(user1, ds);
		DataSourceUtils.addEmptyClaim(user1, ds);
		DataSourceUtils.addEmptyClaim(user2, ds);
		
		ClaimsListActivity activity = startActivity(new UserData(user1.getUUID(), user1.getUserName(), UserRole.CLAIMANT));
		ListView listView = (ListView) activity.findViewById(R.id.claimsListClaimListView);
		//ArrayAdapter<Claim> adapter =  (ArrayAdapter<Claim>) listView.getAdapter();
		
		assertEquals(4, listView.getCount());
	}
	
	public void testListExpenseClaimsApprover() {
		
	}
	
	public void testCreateExpenseClaim() {
		
	}
	
	public void testEditExpenseClaim() {
		
	}
	
	public void testDeleteExpenseClaim() {
		
	}
	
	public void testViewExpenseClaimApprover() {
		
	}
	
	public void testViewExpenseClaimClaimant() {
		
	}
	
	public void testFilterClaimsByTag() {
		
	}
	
	private ClaimsListActivity startActivity(UserData data){
		Intent intent = new Intent();
		intent.putExtra(ClaimsListActivity.USER_DATA, data);
		setActivityIntent(intent);
		ClaimsListActivity activity = getActivity();
		//TODO uncomment when activity supports loading screen
//		try{
//			activity.waitUntilLoaded();
//		} catch (InterruptedException e){
//			fail("Could not load activity!");
//		}
		return activity;
	}

}
