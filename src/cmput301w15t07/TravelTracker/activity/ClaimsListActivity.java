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


import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import cmput301w15t07.TravelTracker.R;
import cmput301w15t07.TravelTracker.model.Claim;
import cmput301w15t07.TravelTracker.model.DataSource;
import cmput301w15t07.TravelTracker.model.InMemoryDataSource;
import cmput301w15t07.TravelTracker.model.User;
import cmput301w15t07.TravelTracker.model.UserData;
import cmput301w15t07.TravelTracker.model.UserRole;
import cmput301w15t07.TravelTracker.serverinterface.ResultCallback;
import cmput301w15t07.TravelTracker.util.ClaimAdapter;
import cmput301w15t07.TravelTracker.util.ClaimsListDataHelper;
import cmput301w15t07.TravelTracker.util.ClaimsListDataHelper.InitialData;
import cmput301w15t07.TravelTracker.util.MultiSelectListener;
import cmput301w15t07.TravelTracker.util.MultiSelectListener.multiSelectMenuListener;
import cmput301w15t07.TravelTracker.util.Observable;
import cmput301w15t07.TravelTracker.util.Observer;

/**
 * List Claims.  Can be done as a Claimant or an Approver.
 * 
 * @author kdbanman, colp, thornhil, therabidsquirel
 *
 */
public class ClaimsListActivity extends TravelTrackerActivity implements Observer<DataSource> {
	//Class Fields
	private ClaimAdapter adapter;
	private InitialData data; 
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
    	    //TODO milestone 5
    	    return true;
    	    
    	case R.id.claims_list_manage_tags:
    	    //TODO milestone 5
    	    return true;
    	    
		case R.id.claims_list_add_claim:
			launchClaimInfoNewClaim(data.getUser());
			return true;
			
        case R.id.claims_list_sign_out:
            signOut();
           return true;
            
		default:
			break;
		}
    	
    	return super.onOptionsItemSelected(item);
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.claims_list_activity);
	    
        context = this;
        
        // Retrieve user info from bundle
        Bundle bundle = getIntent().getExtras();
        userData = (UserData) bundle.getSerializable(USER_DATA);

        appendNameToTitle(userData.getName());
        
        //TODO this will break when we change data sources
        ((Observable<DataSource>) datasource).addObserver(this);
        
        adapter = new ClaimAdapter(context, userData.getRole());
        ListView listView = (ListView) findViewById(R.id.claimsListClaimListView);
        listView.setAdapter(adapter);
        
        if (userData.getRole().equals(UserRole.CLAIMANT)){
	        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
	        listView.setMultiChoiceModeListener(new MultiSelectListener(new contextMenuListener(), R.menu.claims_list_context_menu));
        }
        
        listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				launchClaimInfo(adapter.getItem(position));
				
			}
		});
	}
	
	@Override
	protected void onResume() {
	    super.onResume();
        updateUI();
        
        //TODO get the data based on user
	}
	
	@Override
	public void update(DataSource observable) {
		updateUI();
	}
	
	private void deleteClaims(ArrayList<Integer> adapterPositions){
		
		//DONT delete directly with the positions
		//They will change as you delete items
		ArrayList<Claim> claimsToDelete = new ArrayList<Claim>();
		deleteClaimCallback dcb = new deleteClaimCallback();
		for (Integer i : adapterPositions){
			claimsToDelete.add(adapter.getItem(i));
		}
		
		for (Claim c : claimsToDelete){
			adapter.remove(c);
			datasource.deleteClaim(c.getUUID(), dcb);
		}
	}
	
	private void updateUI(){
		//TODO start a spinner here
		new ClaimsListDataHelper().getInitialData(new initalDataCallback(), userData, datasource);
	}
	
	private void launchClaimInfo(Claim claim){
		Intent intent = new Intent(context, ClaimInfoActivity.class);
    	intent.putExtra(ClaimInfoActivity.CLAIM_UUID, claim.getUUID());
    	
    	intent.putExtra(ClaimInfoActivity.USER_DATA, userData);
    	startActivity(intent);
	}
	
	private void launchClaimInfoNewClaim(User user){
		try{
			datasource.addClaim(user, new createNewClaimCallback());
		} catch (NullPointerException e) {
			// This probably means we are working offline
			//TODO figure out what to do here
			Log.d("ERROR", "The user in Initial Data is null");
		}
	}
	
	class initalDataCallback implements ResultCallback<InitialData>{

		@Override
		public void onResult(InitialData result) {
			adapter.rebuildList(result.getClaims(), result.getItems());
			data = result;
			//TODO stop spinner
		}

		@Override
		public void onError(String message) {
			// TODO Auto-generated method stub
			// TODO stop spinner
		}
		
	}

	class createNewClaimCallback implements ResultCallback<Claim>{

		@Override
		public void onResult(Claim result) {
			launchClaimInfo(result);
		}

		@Override
		public void onError(String message) {
			// TODO Auto-generated method stub
		}
		
	}
	
	class deleteClaimCallback implements ResultCallback<Void>{

		@Override
		public void onResult(Void result) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onError(String message) {
			// TODO Auto-generated method stub
			
		}

	}
	
	class contextMenuListener implements multiSelectMenuListener{

		@Override
		public void menuButtonClicked(ArrayList<Integer> selectedItems,
				MenuItem item) {
			switch (item.getItemId()) {
			case R.id.claims_list_context_delete:
				deleteClaims(selectedItems);
				break;
			default:
				break;
			}
			
		}
		
	}
	
}
