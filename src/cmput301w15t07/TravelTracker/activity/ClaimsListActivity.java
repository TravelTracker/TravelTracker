package cmput301w15t07.TravelTracker.activity;

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
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Set;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ListView;
import cmput301w15t07.TravelTracker.R;
import cmput301w15t07.TravelTracker.TravelTrackerApp;
import cmput301w15t07.TravelTracker.model.Claim;
import cmput301w15t07.TravelTracker.model.DataSource;
import cmput301w15t07.TravelTracker.model.InMemoryDataSource;
import cmput301w15t07.TravelTracker.model.Item;
import cmput301w15t07.TravelTracker.model.UserData;
import cmput301w15t07.TravelTracker.serverinterface.ResultCallback;
import cmput301w15t07.TravelTracker.util.ClaimAdapter;
import cmput301w15t07.TravelTracker.util.Observer;


/**
 * List Claims.  Can be done as a Claimant or an Approver.
 * 
 * @author kdbanman, colp, thornhil
 *
 */
public class ClaimsListActivity extends Activity implements Observer<InMemoryDataSource> {
	/** String used to retrieve user data from intent */
	public static final String USER_DATA = "cmput301w15t07.TravelTracker.userData";
	
	//Class Fields
	private ClaimAdapter adapter;
	private DataSource ds;
	private HashMap<Claim, Collection<Item>> dataTable;
	
	/** Data about the logged-in user. */
	private UserData userData;
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.claims_list_menu, menu);
        
        return true;
    }
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
        setContentView(R.layout.claims_list_activity);
        
        // Retrieve user info from bundle
        Bundle bundle = getIntent().getExtras();
        userData = (UserData) bundle.getSerializable(USER_DATA);
        ds = ((TravelTrackerApp) getApplication()).getDataSource();
        
        adapter = new ClaimAdapter(this);
        ListView listView = (ListView) findViewById(R.id.claimsListClaimListView);
        listView.setAdapter(adapter);
        updateUI();
        
        //TODO get the data based on user
        
        
        //TODO add menu items
	}
	
	@Override
	public void update(InMemoryDataSource observable) {
		updateUI();
	}
	
	public void updateUI(){
		//TODO start a spinner here
		ds.getAllClaims(new claimsRetrievedListener(adapter));
	}
	
	private class claimsRetrievedListener implements ResultCallback<Collection<Claim>> {
		
		private ClaimAdapter adapter;
		
		public claimsRetrievedListener(ClaimAdapter adapter) {
			this.adapter = adapter;
		}
		
		@Override
		public void onResult(Collection<Claim> result) {
			for (Claim c : result){
				dataTable.put(c, null);
			}
			ds.getAllItems(new itemsRetrievedListener(adapter));
		}

		@Override
		public void onError(String message) {
			// TODO raise a toast
			
		}
		
	}
	
	private class itemsRetrievedListener implements ResultCallback<Collection<Item>>{
		
		private ClaimAdapter adapter;
		
		public itemsRetrievedListener(ClaimAdapter adapter) {
			this.adapter = adapter;
		}
		
		@Override
		public void onResult(Collection<Item> result) {
			for (Claim c : dataTable.keySet()){
				if (result.iterator().next().getClaim().equals(c)){
					dataTable.put(c, result);
					break;
				}
			}
			
			adapter.rebuildList(dataTable);
			
		}

		@Override
		public void onError(String message) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
}
