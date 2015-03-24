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

import cmput301w15t07.TravelTracker.R;
import cmput301w15t07.TravelTracker.model.DataSource;
import cmput301w15t07.TravelTracker.model.UserData;
import cmput301w15t07.TravelTracker.util.Observer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Activity for a Claimant to manage his/her Tags.
 * 
 * @author kdbanman, colp, therabidsquirel
 *
 */
public class ManageTagsActivity extends TravelTrackerActivity implements Observer<DataSource> {
    /** Data about the logged-in user. */
    private UserData userData;
    
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
        setContentView(R.layout.manage_tags_activity);
        
        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        // Retrieve user info from bundle
        Bundle bundle = getIntent().getExtras();
        userData = (UserData) bundle.getSerializable(USER_DATA);
        
        appendNameToTitle(userData.getName());
	}

	@Override
    public void update(DataSource observable) {
	    // TODO Auto-generated method stub
	    
    }
}
