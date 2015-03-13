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
import java.util.UUID;

import cmput301w15t07.TravelTracker.R;
import cmput301w15t07.TravelTracker.model.Claim;
import cmput301w15t07.TravelTracker.model.Item;
import cmput301w15t07.TravelTracker.model.User;
import cmput301w15t07.TravelTracker.model.UserData;
import cmput301w15t07.TravelTracker.model.UserRole;
import cmput301w15t07.TravelTracker.serverinterface.ResultCallback;
import cmput301w15t07.TravelTracker.util.ItemsListAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Activity for listing all expense Items belonging to a particular Claim.
 * Possible as a Claimant or an Approver.
 * 
 * @author kdbanman,
 *         therabidsquirel,
 *         braedy
 *         cellinge
 *
 */
public class ExpenseItemsListActivity extends TravelTrackerActivity {
    /** Data about the logged-in user. */
    private UserData userData;
    
    /** UUID of the claim. */
    private UUID claimID;
    
    /** ListView */
    private ListView itemsList;
    
    /** ListView adapter */
    private ItemsListAdapter adapter;
    
    /** The current claim */ 
    Claim claim;
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.expense_items_list_menu, menu);
        
        // Menu items
        MenuItem addItemMenuItem = menu.findItem(R.id.expense_items_list_add_item);
        
        if (userData.getRole().equals(UserRole.CLAIMANT)) {
            
        } else if (userData.getRole().equals(UserRole.APPROVER)) {
            // Menu items an approver doesn't need to see or have access to
            addItemMenuItem.setEnabled(false).setVisible(false);
        }
        
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.expense_items_list_add_item:
        	//TODO get claim instance for this. 
        	launchExpenseInfoNewExpense(claim);
            return true;
            
        case R.id.expense_items_list_sign_out:
            signOut();
            break;
            
        default:
            break;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    private void launchExpenseInfoNewExpense(Claim claim){
    	try{
    		datasource.addItem(claim, new createNewItemCallback());
    	} catch (NullPointerException e){
    		//figure out something to do here
    	}
    }
    
    private void launchExpenseItemInfo(Item item){
    	Intent intent = new Intent(this, ExpenseItemInfoActivity.class);
    	intent.putExtra(ExpenseItemInfoActivity.ITEM_UUID, item.getUUID());
    	intent.putExtra(ExpenseItemInfoActivity.USER_DATA, userData);
    	startActivity(intent);
    	}
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Retrieve user info from bundle
        Bundle bundle = getIntent().getExtras();
        userData = (UserData) bundle.getSerializable(USER_DATA);
        
        // Get claim info
        claimID = (UUID) bundle.getSerializable(CLAIM_UUID);

        appendNameToTitle(userData.getName());
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        setContentView(R.layout.expense_items_list_activity);
        
        itemsList = (ListView) findViewById(R.id.itemsListListView);
        
        adapter = new ItemsListAdapter(this, new ArrayList<Item>());
        itemsList.setAdapter(adapter);
        //Get the current claim to be passed down to items
        datasource.getClaim(claimID, new ResultCallback<Claim>() {

			@Override
			public void onResult(Claim result) {
				claim = result;  
			}

			@Override
			public void onError(String message) {
				Toast.makeText(ExpenseItemsListActivity.this, message, Toast.LENGTH_LONG).show();
				
			}
        	
		});
    }
    
    
    
    class createNewItemCallback implements ResultCallback<Item>{
    	@Override
    	public void onResult(Item result){
    		launchExpenseItemInfo(result);
    	}
    	@Override
    	public void onError(String message){
    		
    	}
    }
}
