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
import cmput301w15t07.TravelTracker.model.UserRole;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;


/**
 * List Claims.  Can be done as a Claimant or an Approver.
 * 
 * @author kdbanman, colp
 *
 */
public class ClaimsListActivity extends Activity {
	/** String used to retrieve user name from intent */
	public static final String USER_NAME = "cmput301w15t07.TravelTracker.userName";
	
	/** String used to retrieve user role from intent */
	public static final String USER_ROLE = "cmput301w15t07.TravelTracker.userRole";
	
	/** The name of the user */
	private String userName;
	
	/** The role of the user */
	private UserRole userRole;
	
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
        userName = bundle.getString(USER_NAME);
        userRole = (UserRole) bundle.getSerializable(USER_ROLE);
	}
}
