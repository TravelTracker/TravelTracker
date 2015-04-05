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

import cmput301w15t07.TravelTracker.activity.ManageTagsActivity;
import cmput301w15t07.TravelTracker.activity.TravelTrackerActivity;
import cmput301w15t07.TravelTracker.model.DataSource;
import cmput301w15t07.TravelTracker.model.InMemoryDataSource;
import cmput301w15t07.TravelTracker.model.Tag;
import cmput301w15t07.TravelTracker.model.User;
import cmput301w15t07.TravelTracker.model.UserData;
import cmput301w15t07.TravelTracker.model.UserRole;
import cmput301w15t07.TravelTracker.testutils.DataSourceUtils;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;

/**
 * Test for tag management activities.
 * 
 * Each relevant Use Case UC.XxxYyy is tested with method testXxxYyy()
 * 
 * @author kdbanman
 *
 */
public class ManageTagsActivityTest extends ActivityInstrumentationTestCase2<ManageTagsActivity> {

	DataSource ds;
	User user;
	ManageTagsActivity activity;
	
	public ManageTagsActivityTest() {
		super(ManageTagsActivity.class);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ds = new InMemoryDataSource();
		DataSourceSingleton.setDataSource(ds);
		user = DataSourceUtils.addUser("Bob", ds);
	}
	
	public void testManageTagsNumberOfTags() throws InterruptedException {
		int numberOfTags = 10;
		startWithTags(numberOfTags);
		
		ListView listView = (ListView) activity.findViewById(R.id.manageTagsTagListView);
		
		assertEquals(numberOfTags, listView.getCount());
	}
	
	public void testCreateTag() {
		
	}
	
	public void testEditTag() {
		
	}
	
	public void testDeleteTag() {
		
	}
	
	private void startWithTags(int number) throws InterruptedException{
		ArrayList<Tag> tags = addEmptyTags(number);
		startActivity();
	}
	
	private void startActivity() throws InterruptedException{
		// Create the intent
	Intent intent = new Intent();
	intent.putExtra(TravelTrackerActivity.USER_DATA,
					new UserData(user.getUUID(), user.getUserName(), UserRole.CLAIMANT));
	
	// Start the activity
	setActivityIntent(intent);
	activity = getActivity();
	activity.waitUntilLoaded();
	}
	
	private ArrayList<Tag> addEmptyTags(int number){
		ArrayList<Tag> out = new ArrayList<Tag>();
		for (int i=0; i < number; i++){
			Tag tag = DataSourceUtils.addEmptyTag(user, ds);
			tag.setTitle("Tag" + i);
		}
		return out;
	}

}
