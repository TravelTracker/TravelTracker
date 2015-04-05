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

package cmput301w15t07.TravelTracker.activity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import cmput301w15t07.TravelTracker.R;
import cmput301w15t07.TravelTracker.model.Claim;
import cmput301w15t07.TravelTracker.model.DataSource;
import cmput301w15t07.TravelTracker.model.Geolocation;
import cmput301w15t07.TravelTracker.model.Tag;
import cmput301w15t07.TravelTracker.model.User;
import cmput301w15t07.TravelTracker.model.UserData;
import cmput301w15t07.TravelTracker.model.UserRole;
import cmput301w15t07.TravelTracker.serverinterface.ResultCallback;
import cmput301w15t07.TravelTracker.util.ClaimAdapter;
import cmput301w15t07.TravelTracker.util.ClaimsListDataHelper;
import cmput301w15t07.TravelTracker.util.SelectTagFilterFragment;
import cmput301w15t07.TravelTracker.util.ClaimsListDataHelper.InitialData;
import cmput301w15t07.TravelTracker.util.MultiSelectListener;
import cmput301w15t07.TravelTracker.util.MultiSelectListener.multiSelectMenuListener;
import cmput301w15t07.TravelTracker.util.Observer;
import cmput301w15t07.TravelTracker.util.SelectLocationFragment;

import com.google.android.gms.maps.model.LatLng;

/**
 * List Claims. Can be done as a Claimant or an Approver.
 * 
 * @author kdbanman,
 *         colp,
 *         thornhil,
 *         therabidsquirel
 *
 */
public class ClaimsListActivity extends TravelTrackerActivity implements Observer<DataSource> {
	//Class Fields
    private ListView claimsList;
	private ClaimAdapter adapter;
	private InitialData data;
	User user;
	
	/** Data about the logged-in user. */
	private UserData userData;
	
	/** Tags UUIDs selected for filtering. */
	private HashSet<UUID> filterTags;
	
	/** Whether the filter is enabled. */
	private boolean filterEnabled = false;
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.claims_list_menu, menu);
        
        // Menu items
        MenuItem tagFilterMenuItem = menu.findItem(R.id.claims_list_filter_by_tag);
        MenuItem tagManageMenuItem = menu.findItem(R.id.claims_list_manage_tags);
        MenuItem addClaimMenuItem = menu.findItem(R.id.claims_list_add_claim);
        
        if (userData.getRole().equals(UserRole.APPROVER)) {
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
    	    launchFilterByTag();
    	    return true;
    	    
    	case R.id.claims_list_manage_tags:
    	    launchManageTags();
    	    return true;
    	    
		case R.id.claims_list_add_claim:
			launchClaimInfoNewClaim(data.getUser());
			return true;
            
        case R.id.claims_list_set_home_location:
        	launchSetHomeLocation();
        	return true;
			
        case R.id.claims_list_sign_out:
            signOut();
            return true;
           
        case android.R.id.home:
        	onBackPressed();
        	return true;
            
		default:
			return false;
		}
    }

    @Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        // Retrieve user info from bundle
        Bundle bundle = getIntent().getExtras();
        userData = (UserData) bundle.getSerializable(USER_DATA);
        appendNameToTitle(userData.getName());
	    
        // Create adapter
        adapter = new ClaimAdapter(this, userData.getRole());
        
        datasource.addObserver(this);
        
        // TODO Remove this without things breaking
        setContentView(R.layout.claims_list_activity);
	}
	
    /**
     * Update the activity when the dataset changes.
     * Called in onResume() and update(DataSource observable).
     */
    @Override
    public void updateActivity() {
        // Show loading circle
        // TODO Uncomment this without things breaking
//        setContentView(R.layout.loading_indeterminate);
        
        new ClaimsListDataHelper().getInitialData(new initalDataCallback(), userData, datasource);
    }
    
    /**
     * Change to the activity's layout and set it up its views accordingly.
     */
    private void onGetInitialData() {
        // TODO Uncomment this without things breaking
//        setContentView(R.layout.claims_list_activity);
        
        claimsList = (ListView) findViewById(R.id.claimsListClaimListView);
        claimsList.setAdapter(adapter);
        
        if (userData.getRole().equals(UserRole.CLAIMANT)){
            claimsList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            claimsList.setMultiChoiceModeListener(new MultiSelectListener(new contextMenuListener(), R.menu.claims_list_context_menu));
        }
        
        claimsList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                launchClaimInfo(adapter.getItem(position));
            }
        });
        
        onLoaded();
    }
    
	/**
	 * delete the claims at set adapter positions 
	 * @param adapterPositions
	 */
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
	
	/**
	 * Launch the claimInfo activity for the selected claim 
	 * @param claim
	 */
	private void launchClaimInfo(Claim claim){
		Intent intent = new Intent(this, ClaimInfoActivity.class);
    	intent.putExtra(ClaimInfoActivity.CLAIM_UUID, claim.getUUID());
    	intent.putExtra(ClaimInfoActivity.USER_DATA, userData);
    	startActivity(intent);
	}
	
    /**
     * Launch the Tag managing activity.
     */
    private void launchManageTags() {
        Intent intent = new Intent(this, ManageTagsActivity.class);
        intent.putExtra(ManageTagsActivity.USER_DATA, userData);
        startActivity(intent);
    }
    
    /**
     * Launch the select location fragment for home location.
     */
    private void launchSetHomeLocation() {
    	String title = getString(R.string.claims_list_set_home_location);
    	
    	SelectLocationFragment.ResultCallback callback = new SelectLocationFragment.ResultCallback() {
			@Override
			public void onSelectLocationResult(LatLng location) {
				setUserLocation(location);
			}
			
			@Override
			public void onSelectLocationCancelled() {}
		};
    	
		LatLng location = null;
		User user = data.getUser();
		Geolocation geolocation = user.getHomeLocation();
		
		if (geolocation != null) {
			location = geolocation.getLatLng();
		}
		
    	SelectLocationFragment fragment = new SelectLocationFragment(callback, location, title);
    	fragment.show(getFragmentManager(), "selectLocation");
    }
    /**
     * Launch the filter by tag fragment.
     */
    private void launchFilterByTag() {
		SelectTagFilterFragment filterFragment = new SelectTagFilterFragment(data.getTags(), filterEnabled,
		        filterTags, new SelectTagFilterFragment.ResultCallback() {
            @Override
            public void onSelectTagFilterFragmentResult(HashSet<UUID> selected,
                    boolean filterEnabled) {
                filterByTags(selected, filterEnabled);
            }

            @Override
            public void onSelectTagFilterFragmentCancelled() { }
		});
    	filterFragment.show(getFragmentManager(), "filterByTag");
    }
	/**
	 * launch the claimInfo activity for a new claim
	 * @param user The current user that will be assigned to the claim 
	 */
	private void launchClaimInfoNewClaim(User user){
		try{
			datasource.addClaim(user, new createNewClaimCallback());
		} catch (NullPointerException e) {
			// This probably means we are working offline
			//TODO figure out what to do here
			Log.d("ERROR", "The user in Initial Data is null");
		}
	}
	
	/**
	 * Set the user's location.
	 * @param location The location to set.
	 */
	private void setUserLocation(final LatLng location) {
		Geolocation geoloc = new Geolocation(location.latitude, location.longitude);
		data.getUser().setHomeLocation(geoloc);
	}
	/**
	 * Filter the claims by tag UUIDs.
	 * @param tagIDs The list of tag IDs. Only claims with at least one of these tags will be displayed.
	 * @param filterEnabled Whether the filter is enabled.
	 */
	private void filterByTags(HashSet<UUID> tagIDs, boolean filterEnabled) {
		filterTags = tagIDs;
		this.filterEnabled = filterEnabled;
		
		rebuildList();
	}
	/**
	 * Rebuild the ListView.
	 */
	private void rebuildList() {
	    // Only pass tags if filter is enabled
	    HashSet<UUID> tags = null;
	    if (filterEnabled) {
	        tags = filterTags;
	    }
	    
		adapter.rebuildList(data.getClaims(), data.getItems(), data.getUsers(), data.getUser(), tags);
	}
	/** Callback for the list data on load */
	class initalDataCallback implements ResultCallback<InitialData>{
		@Override
		public void onResult(InitialData result) {
			// Populate the list of tags
			if (filterTags == null) {
				filterTags = new HashSet<UUID>();
				
				for (Tag tag : result.getTags()) {
					filterTags.add(tag.getUUID());
				}
				
			// Turn on new tags by default
			} else {
				Collection<Tag> oldTags = data.getTags();
				
				for (Tag tag : result.getTags()) {
					if (!oldTags.contains(tag)) {
						filterTags.add(tag.getUUID());
					}
				}
			}
			
			data = result;
			filterByTags(filterTags, filterEnabled); // Will automatically rebuild list
            onGetInitialData();
		}

		@Override
		public void onError(String message) {
	        Toast.makeText(ClaimsListActivity.this, message, Toast.LENGTH_SHORT).show();
		}
	}
	
	/** Callback for creating a new claim */ 
	class createNewClaimCallback implements ResultCallback<Claim>{
		@Override
		public void onResult(Claim result) {
			launchClaimInfo(result);
		}

		@Override
		public void onError(String message) {
            Toast.makeText(ClaimsListActivity.this, message, Toast.LENGTH_SHORT).show();
		}
	}
	
	/** Callback for deleting a claim */
	class deleteClaimCallback implements ResultCallback<Void>{
		@Override
		public void onResult(Void result) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onError(String message) {
            Toast.makeText(ClaimsListActivity.this, message, Toast.LENGTH_SHORT).show();
		}
	}
	
	class contextMenuListener implements multiSelectMenuListener{
		@Override
		public void menuButtonClicked(ArrayList<Integer> selectedItems, MenuItem item) {
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
