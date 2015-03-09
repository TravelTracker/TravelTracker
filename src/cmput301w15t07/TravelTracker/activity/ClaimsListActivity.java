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

import io.searchbox.indices.aliases.AddAliasMapping;

import java.util.Collection;
import java.util.Currency;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import cmput301w15t07.TravelTracker.R;
import cmput301w15t07.TravelTracker.model.Claim;
import cmput301w15t07.TravelTracker.model.Destination;
import cmput301w15t07.TravelTracker.model.InMemoryDataSource;
import cmput301w15t07.TravelTracker.model.UserData;
import cmput301w15t07.TravelTracker.util.Observer;


/**
 * List Claims.  Can be done as a Claimant or an Approver.
 * 
 * @author kdbanman, colp
 *
 */
public class ClaimsListActivity extends Activity implements Observer<InMemoryDataSource> {
	/** String used to retrieve user data from intent */
	public static final String USER_DATA = "cmput301w15t07.TravelTracker.userData";
	
	
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
	}
	
	@Override
	public void update(InMemoryDataSource observable) {
		updateUI();
	}
	
	public void updateUI(){
		//TODO do all UI tasks here
	}
	
	private class ClaimAdapter extends ArrayAdapter<Claim>{

		public ClaimAdapter(Context context) {
			super(context, R.layout.claims_list_row_item);
		}
		
		/**
		 * This function rebuilds the list with the passed claims
		 * @param claims
		 */
		public void rebuildList(Claim [] claims){
			//possible performance bottleneck
			clear();
			addAll(claims);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View workingView;
			if (convertView != null){
				workingView = convertView;
			} else {
				LayoutInflater inflator = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				workingView = inflator.inflate(R.layout.claims_list_row_item, parent, false);
			}
			
			// Get all fields 
			TextView name = (TextView) workingView.findViewById(R.id.claimsListRowItemName);
			TextView date = (TextView) workingView.findViewById(R.id.claimsListRowItemDate);
			LinearLayout destinationContainer = (LinearLayout) workingView.findViewById(R.id.claimsListDestinationContainer);
			LinearLayout totalsContainer = (LinearLayout) workingView.findViewById(R.id.claimsListTotalContainer);
			
			
			
			return workingView;
		}
		
		public void addTotal(Float amt,Currency currency){
			//TODO implement the dynamic addition of total textviews
		}
		
		public void addDestination(Destination dest){
			//TODO implement the dynamic addition of destination textviews
		}
	}

}
