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
import java.util.UUID;

import cmput301w15t07.TravelTracker.R;
import cmput301w15t07.TravelTracker.model.Claim;
import cmput301w15t07.TravelTracker.model.DataSource;
import cmput301w15t07.TravelTracker.model.Item;
import cmput301w15t07.TravelTracker.model.UserData;
import cmput301w15t07.TravelTracker.serverinterface.MultiCallback;
import cmput301w15t07.TravelTracker.serverinterface.ResultCallback;
import cmput301w15t07.TravelTracker.util.ExpenseItemsListAdapter;
import cmput301w15t07.TravelTracker.util.MultiSelectListener;
import cmput301w15t07.TravelTracker.util.Observer;
import cmput301w15t07.TravelTracker.util.MultiSelectListener.multiSelectMenuListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
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
 *         braedy,
 *         cellinge
 *
 */
public class ExpenseItemsListActivity extends TravelTrackerActivity implements Observer<DataSource> {
    /** Key for multicallback for claim. */
    public static final int MULTI_CLAIM_KEY = 0;

    /** Key for multicallback for items. */
    public static final int MULTI_ITEMS_KEY = 1;

    /** Data about the logged-in user. */
    private UserData userData;
    
    /** UUID of the claim. */
    private UUID claimID;
    
    /** The current claim */ 
    Claim claim = null;
    
    /** The menu for the activity. */
    private Menu menu = null;

    /** ListView */
    private ListView itemsList;
    
    /** ListView adapter */
    private ExpenseItemsListAdapter adapter;
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.expense_items_list_menu, menu);
        this.menu = menu;
        
        if (claim != null) {
            hideMenuItems(menu, claim);
        }
        
        return true;
    }
    
    private void hideMenuItems(Menu menu, Claim claim) {
        // Menu items
        MenuItem addItemMenuItem =
                menu.findItem(R.id.expense_items_list_add_item);
        
        if (!isEditable(claim.getStatus(), userData.getRole())) {
            // Menu items that disappear when not editable
            addItemMenuItem.setEnabled(false).setVisible(false);
        }
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.expense_items_list_add_item:
        	launchExpenseInfoNewExpense(claim);
            return true;
            
        case R.id.expense_items_list_sign_out:
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
        
        // Get claim info
        claimID = (UUID) bundle.getSerializable(CLAIM_UUID);
        
        // Create adapter
        adapter = new ExpenseItemsListAdapter(this);
        
        datasource.addObserver(this);
    }
    
    /**
     * Update the activity when the dataset changes.
     * Called in onResume() and update(DataSource observable).
     */
    @Override
    public void updateActivity() {
        // Show loading circle
        setContentView(R.layout.loading_indeterminate);
        
        // Multicallback for claim and items
        MultiCallback multi = new MultiCallback(new UpdateDataCallback());
        
        // Create callbacks
        datasource.getClaim(claimID, multi.<Claim>createCallback(MULTI_CLAIM_KEY));
        datasource.getAllItems(multi.<Collection<Item>>createCallback(MULTI_ITEMS_KEY));
        
        // Notify ready so callback can execute
        multi.ready();
    }
    
    /**
     * If the current UI is the indeterminate loading screen then the UI is
     * changed to the activity layout and the views set up accordingly.
     */
    private void changeUI() {
        setContentView(R.layout.expense_items_list_activity);
        
        if (menu != null) {
            hideMenuItems(menu, claim);
        }
        
        // Get itemsList and set its adapter
        itemsList = (ListView) findViewById(R.id.expenseItemsListListView);
        itemsList.setAdapter(adapter);

        // Add delete listener only if we're a claimant and the claim
        // is in an editable state
        if (isEditable(claim.getStatus(), userData.getRole())) {
            itemsList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            itemsList.setMultiChoiceModeListener(new MultiSelectListener(new ContextMenuListener(),
                                                 R.menu.items_list_context_menu));
        }

        // Set onClick listener, send the Item to the Item Info Activity
        itemsList.setOnItemClickListener(new OnItemClickListener() {
        	@Override
        	public void onItemClick(AdapterView<?> parent, View view,
        			int position, long id) {
        		// Launch edit expense item info with this item
        		launchExpenseItemInfo(adapter.getItem(position));
        	}
        });
        
        onLoaded();
    }
    
    /**
     * Launch the ExpenseItemInfo activity for a new Item
     * @param claim The current claim 
     */
    private void launchExpenseInfoNewExpense(Claim claim){
        datasource.addItem(claim, new CreateNewItemCallback());
    }
    
    /** 
     * Launches the ExpenseItemInfo activity for the selected item
     * @param item Selected item to open
     */
    private void launchExpenseItemInfo(Item item){
        Intent intent = new Intent(this, ExpenseItemInfoActivity.class);
        intent.putExtra(FROM_CLAIM_INFO, false);
        intent.putExtra(ITEM_UUID, item.getUUID());
        intent.putExtra(CLAIM_UUID, claimID);
        intent.putExtra(USER_DATA, userData);
        startActivity(intent);
    }
    
    /**
     * delete selected items from the list
     * @param selectedItems
     */
    public void deleteItems(ArrayList<Integer> selectedItems) {
        // Deleting in place is bad
        ArrayList<Item> delete = new ArrayList<Item>();
        for (int i: selectedItems) {
            delete.add(adapter.getItem(i));
        }
        
        // TODO: This could probably be converted to a multi callback if it
        // stops us from having to request updates each time it's removed.
        DeleteItemCallback cb = new DeleteItemCallback();
        for (Item i: delete) {
            datasource.deleteItem(i.getUUID(), cb);
        }
    }
    
    /**
     * Callback for when a new item is added.
     */
    class CreateNewItemCallback implements ResultCallback<Item> {
    	@Override
    	public void onResult(Item result){
    		launchExpenseItemInfo(result);
    	}
    	@Override
    	public void onError(String message){
    		Toast.makeText(ExpenseItemsListActivity.this,
    		        message, Toast.LENGTH_SHORT).show();
    	}
    }
    
    /**
     * Multicallback meant to get all data required from the datasource that
     * this activity needs on update or resume.
     * 
     * Requests list rebuilt and UI update.
     */
    class UpdateDataCallback implements ResultCallback<SparseArray<Object>> {
        /**
         * Saves the claim, requests an adapter update,
         * and then a UI change.
         * 
         * @param result The request result.
         */
        @SuppressWarnings("unchecked")
        @Override
        public void onResult(SparseArray<Object> result) {
            claim = (Claim) result.get(MULTI_CLAIM_KEY);
            adapter.rebuildList(
                    (Collection<Item>) result.get(MULTI_ITEMS_KEY), claimID);
            ExpenseItemsListActivity.this.changeUI();
        }

        @Override
        public void onError(String message) {
            Toast.makeText(ExpenseItemsListActivity.this,
                    message, Toast.LENGTH_LONG).show();
        }
    }
    
    /**
     * Callback for deleting items
     */
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
    /**
     *  Listener for Context menu 
     */
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
