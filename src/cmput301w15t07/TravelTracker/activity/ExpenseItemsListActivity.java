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
import java.util.Collection;
import java.util.UUID;

import cmput301w15t07.TravelTracker.R;
import cmput301w15t07.TravelTracker.model.Claim;
import cmput301w15t07.TravelTracker.model.InMemoryDataSource;
import cmput301w15t07.TravelTracker.model.Item;
import cmput301w15t07.TravelTracker.model.UserData;
import cmput301w15t07.TravelTracker.model.UserRole;
import cmput301w15t07.TravelTracker.serverinterface.ResultCallback;
import cmput301w15t07.TravelTracker.util.ExpenseItemsListAdapter;
import cmput301w15t07.TravelTracker.util.MultiSelectListener;
import cmput301w15t07.TravelTracker.util.Observer;
import cmput301w15t07.TravelTracker.util.MultiSelectListener.multiSelectMenuListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
public class ExpenseItemsListActivity extends TravelTrackerActivity implements Observer<InMemoryDataSource> {
    /** Data about the logged-in user. */
    private UserData userData;
    
    /** UUID of the claim. */
    private UUID claimID;
    
    /** The current claim */ 
    Claim claim;
    
    /** ListView */
    private ListView itemsList;
    
    /** ListView adapter */
    private ExpenseItemsListAdapter adapter;
    
    
    private boolean loading;
    
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
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Retrieve user info from bundle
        Bundle bundle = getIntent().getExtras();
        userData = (UserData) bundle.getSerializable(USER_DATA);

        appendNameToTitle(userData.getName());
        
        // Get claim info
        claimID = (UUID) bundle.getSerializable(CLAIM_UUID);
        
        // Get claim
        datasource.getClaim(claimID, new ResultCallback<Claim>() {
            @Override
            public void onResult(Claim result) {
                ExpenseItemsListActivity.this.claim = result;
            }

            @Override
            public void onError(String message) {
                Toast.makeText(ExpenseItemsListActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
        
        
        // Create adapter
        adapter = new ExpenseItemsListAdapter(this);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.loading_indeterminate);
        loading = true;
        
        datasource.getAllItems(new GetAllItemsCallback(this, adapter));
        
        //Get the current claim to be passed down to items
        datasource.getClaim(claimID, new GetClaimCallback());
    }
    
    /**
     * If the current UI is the indeterminate loading screen then the UI is
     * changed to the activity layout and the views set up accordingly.
     */
    private void changeUI() {
        if (loading) {
            // No longer loading
            loading = false;
            
            // Switch to actual layout
            setContentView(R.layout.expense_items_list_activity);
            
            // Get itemsList and set its adapter
            itemsList = (ListView) findViewById(R.id.expenseItemsListListView);
            itemsList.setAdapter(adapter);

            itemsList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            itemsList.setMultiChoiceModeListener(new MultiSelectListener(new ContextMenuListener(), R.menu.items_list_context_menu));
            
            itemsList.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                        int position, long id) {
                    // Launch edit expense item info with this item
                    launchExpenseItemInfo(adapter.getItem(position));
                }
            });
        }
                
    }
    
    private void launchExpenseInfoNewExpense(Claim claim){
        datasource.addItem(claim, new CreateNewItemCallback());
    }
    
    private void launchExpenseItemInfo(Item item){
        Intent intent = new Intent(this, ExpenseItemInfoActivity.class);
        intent.putExtra(ExpenseItemInfoActivity.ITEM_UUID, item.getUUID());
        intent.putExtra(ExpenseItemInfoActivity.CLAIM_UUID, claim.getUUID());
        intent.putExtra(ExpenseItemInfoActivity.USER_DATA, userData);
        startActivity(intent);
    }
    
    @Override
    public void update(InMemoryDataSource observable) {
        observable.getAllItems(new GetAllItemsCallback(this, adapter));
    }

    public void deleteItems(ArrayList<Integer> selectedItems) {
        // Deleting in place is bad
        ArrayList<Item> delete = new ArrayList<Item>();
        for (int i: selectedItems) {
            delete.add(adapter.getItem(i));
        }
        
        DeleteItemCallback cb = new DeleteItemCallback();
        for (Item i: delete) {
            adapter.remove(i);
            datasource.deleteItem(i.getUUID(), cb);
        }
    }
    
    class CreateNewItemCallback implements ResultCallback<Item> {
    	@Override
    	public void onResult(Item result){
    		launchExpenseItemInfo(result);
    	}
    	@Override
    	public void onError(String message){
    		Toast.makeText(ExpenseItemsListActivity.this, message, Toast.LENGTH_SHORT).show();
    	}
    }
    
    
    /**
     * Gets the current Items Collection from the data source then requests
     * that the adapter update its internal list with the new list. Then
     * requests that the activity update its UI.
     */
    class GetAllItemsCallback implements ResultCallback<Collection<Item>> {
        ExpenseItemsListActivity activity;
        ExpenseItemsListAdapter adapter;
        
        public GetAllItemsCallback(ExpenseItemsListActivity activity, ExpenseItemsListAdapter adapter) {
            this.activity = activity;
            this.adapter = adapter;
        }
        
        /**
         * Requests and adapter update and a UI change.
         * 
         * @param result The request result.
         */
        @Override
        public void onResult(Collection<Item> result) {
            adapter.rebuildList(result, claimID);
            
            // Request to change to list view
            if (activity != null) {
                activity.changeUI();
            }
        }
        
        @Override
        public void onError(String message){
            Toast.makeText(ExpenseItemsListActivity.this,
                    message,
                    Toast.LENGTH_SHORT)
                    .show();
        }
    }
    
    class GetClaimCallback implements ResultCallback<Claim> {
        @Override
        public void onResult(Claim result) {
            claim = result;  
        }

        @Override
        public void onError(String message) {
            Toast.makeText(ExpenseItemsListActivity.this, message, Toast.LENGTH_LONG).show();
        }
    }
    
    class DeleteItemCallback implements ResultCallback<Void> {
        // Do nothing
        @Override
        public void onResult(Void result) {}
        
        // Make toast
        @Override
        public void onError(String message) {
            Toast.makeText(ExpenseItemsListActivity.this,
                    message,
                    Toast.LENGTH_SHORT)
                    .show();
        }
    }
    
    class ContextMenuListener implements multiSelectMenuListener {

        @Override
        public void menuButtonClicked(ArrayList<Integer> selectedItems,
                MenuItem item) {
            switch (item.getItemId()) {
            case R.id.items_list_context_delete:
                deleteItems(selectedItems);
                break;
            default:
                break;
            }
        }
    }
    
}
