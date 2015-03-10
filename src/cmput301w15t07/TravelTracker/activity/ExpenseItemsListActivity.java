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
import cmput301w15t07.TravelTracker.model.Item;
import cmput301w15t07.TravelTracker.model.UserData;
import cmput301w15t07.TravelTracker.util.ItemsListAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

/**
 * Activity for listing all expense Items belonging to a particular Claim.
 * Possible as a Claimant or an Approver.
 * 
 * @author kdbanman,
 *         therabidsquirel,
 *         braedy
 *
 */
public class ExpenseItemsListActivity extends Activity {
    /** String used to retrieve user data from intent */
    public static final String USER_DATA = "cmput301w15t07.TravelTracker.userData";
    
    /** String used to retrieve claim UUID from intent */
    public static final String CLAIM_UUID = "cmput301w15t07.TravelTracker.claimUUID";
    
    /** Data about the logged-in user. */
    private UserData userData;
    
    /** UUID of the claim. */
    private UUID claimID;
    
    /** ListView */
    private ListView itemsList;
    
    /** ListView adapter */
    private ItemsListAdapter adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expense_items_list_activity);
        
        itemsList = (ListView) findViewById(R.id.itemsListListView);
        
        adapter = new ItemsListAdapter(this, new ArrayList<Item>());
        itemsList.setAdapter(adapter);
    }
    
}
