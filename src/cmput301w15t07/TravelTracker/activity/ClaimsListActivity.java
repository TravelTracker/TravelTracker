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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import cmput301w15t07.TravelTracker.R;
import cmput301w15t07.TravelTracker.model.Claim;
import cmput301w15t07.TravelTracker.model.InMemoryDataSource;
import cmput301w15t07.TravelTracker.model.Item;
import cmput301w15t07.TravelTracker.model.UserData;
import cmput301w15t07.TravelTracker.model.UserRole;
import cmput301w15t07.TravelTracker.serverinterface.ResultCallback;
import cmput301w15t07.TravelTracker.util.ClaimAdapter;
import cmput301w15t07.TravelTracker.util.Observer;

/**
 * List Claims.  Can be done as a Claimant or an Approver.
 * 
 * @author kdbanman, colp, thornhil, therabidsquirel
 *
 */
public class ClaimsListActivity extends TravelTrackerActivity implements Observer<InMemoryDataSource> {
	//Class Fields
	private ClaimAdapter adapter;
	private Collection<Claim> claims;
	private Collection<Item> items;
	private Context context;
	
	/** Data about the logged-in user. */
	private UserData userData;
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.claims_list_menu, menu);
        
        // Menu items
        MenuItem tagFilterMenuItem = menu.findItem(R.id.claims_list_filter_by_tag);
        MenuItem tagManageMenuItem = menu.findItem(R.id.claims_list_manage_tags);
        MenuItem addClaimMenuItem = menu.findItem(R.id.claims_list_add_claim);
        
        if (userData.getRole().equals(UserRole.CLAIMANT)) {
            
        } else if (userData.getRole().equals(UserRole.APPROVER)) {
            // Menu items an approver doesn't need to see or have access to
            tagFilterMenuItem.setEnabled(false).setVisible(false);
            tagManageMenuItem.setEnabled(false).setVisible(false);
            addClaimMenuItem.setEnabled(false).setVisible(false);
        }
        
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case R.id.claims_list_filter_by_tag:
    	    
    	    return true;
    	    
    	case R.id.claims_list_manage_tags:
    	    
    	    return true;
    	    
		case R.id.claims_list_add_claim:
			
			return true;
			
        case R.id.claims_list_sign_out:
            signOut();
            break;
            
		default:
			break;
		}
    	
    	return super.onOptionsItemSelected(item);
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
        context = this;
	}
	
	@Override
	protected void onResume() {
	    super.onResume();
	    
        setContentView(R.layout.claims_list_activity);
        
        // Retrieve user info from bundle
        Bundle bundle = getIntent().getExtras();
        userData = (UserData) bundle.getSerializable(USER_DATA);

        appendNameToTitle(userData.getName());
        
        adapter = new ClaimAdapter(context);
        ListView listView = (ListView) findViewById(R.id.claimsListClaimListView);
        listView.setAdapter(adapter);
        updateUI();
        
        listView.setOnItemClickListener(new itemSelectListener());
        //TODO get the data based on user
        
	}
	
	@Override
	public void update(InMemoryDataSource observable) {
		updateUI();
	}
	
	public void updateUI(){
		//TODO start a spinner here
		datasource.getAllClaims(new claimsRetrievedListener(adapter));
	}
	
	private class claimsRetrievedListener implements ResultCallback<Collection<Claim>> {
		
		private ClaimAdapter adapter;
		
		public claimsRetrievedListener(ClaimAdapter adapter) {
			this.adapter = adapter;
		}
		
		@Override
		public void onResult(Collection<Claim> result) {
			claims = result;
			datasource.getAllItems(new itemsRetrievedListener(adapter));
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
			items = result;
			
			adapter.rebuildList(claims, items);
			
		}

		@Override
		public void onError(String message) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	private class itemSelectListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Claim claim = adapter.getItem(position);
			
		 	Intent intent = new Intent(context, ClaimInfoActivity.class);
	    	intent.putExtra(ClaimInfoActivity.CLAIM_UUID, claim.getUUID());
	    	
	    	intent.putExtra(ClaimInfoActivity.USER_DATA, userData);
	    	startActivity(intent);
		}
		
	}
	
}
