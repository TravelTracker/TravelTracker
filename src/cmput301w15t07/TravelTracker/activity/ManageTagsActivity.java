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

import cmput301w15t07.TravelTracker.R;
import cmput301w15t07.TravelTracker.model.Tag;
import cmput301w15t07.TravelTracker.model.UserData;
import cmput301w15t07.TravelTracker.serverinterface.ResultCallback;
import cmput301w15t07.TravelTracker.util.ManageTagsListAdapter;
import cmput301w15t07.TravelTracker.model.DataSource;
import cmput301w15t07.TravelTracker.util.Observer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Activity for a Claimant to manage his/her Tags.
 * 
 * @author kdbanman, colp, therabidsquirel, braedy
 *
 */
public class ManageTagsActivity extends TravelTrackerActivity implements Observer<DataSource> {
    /** Data about the logged-in user. */
    private UserData userData;
    
    /** Adapter for the list. */
    private ManageTagsListAdapter adapter;

    /** Are we currently in the loading screen. */
    private boolean loading;
    
    /** The EditText field where new Tag titles are entered. */
    private EditText titleEditText;

    /** The button pressed to add a Tag. */
    private Button addTagButton;

    /** The list view of Tags */
    private ListView tagListView;
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.manage_tags_menu, menu);
        
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.manage_tags_sign_out:
            signOut();
            break;
            
        case android.R.id.home:
        	onBackPressed();
        	break;
            
        default:
            break;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
        
        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        // Retrieve user info from bundle
        Bundle bundle = getIntent().getExtras();
        userData = (UserData) bundle.getSerializable(USER_DATA);
        
        appendNameToTitle(userData.getName());
        
        // Make adapter
        adapter = new ManageTagsListAdapter(this);
	}
        
    @Override
    public void update(DataSource observable) {
        // Gets tags and updates adapter
        datasource.getAllTags(new GetUserTagsCallback(this, adapter));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Set loading screen and notify loading
        setContentView(R.layout.loading_indeterminate);
        loading = true;
        
        // Actual request
        datasource.getAllTags(new GetUserTagsCallback(this, adapter));
    }

    /**
     * Sets the content view to the activity view and then gets all of the
     * widgets of the UI.
     */
    public void updateUI() {
        if (loading) {
            setContentView(R.layout.manage_tags_activity);
            
            titleEditText = 
                    (EditText) findViewById(R.id.manageTagsNewTagEditText);
            
            addTagButton = (Button) findViewById(R.id.manageTagsAddButton);
            
            tagListView = (ListView) findViewById(R.id.manageTagsTagListView);
            tagListView.setAdapter(adapter);
        }
        onLoaded();
    }
	
    /**
     * Callback to get all tags for a user.
     */
    public class GetUserTagsCallback implements ResultCallback<Collection<Tag>> {

        private ManageTagsActivity activity;
        private ManageTagsListAdapter adapter;

        public GetUserTagsCallback(ManageTagsActivity activity,
                ManageTagsListAdapter adapter) {
            this.activity = activity;
            this.adapter = adapter;
        }

        /** 
         * Ask the adapter to rebuild the list, and then update the UI.
         */
        @Override
        public void onResult(Collection<Tag> result) {
            adapter.rebuildList(result, userData.getUUID());
            activity.updateUI();
        }

        @Override
        public void onError(String message) {
            Toast.makeText(ManageTagsActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    }
}
