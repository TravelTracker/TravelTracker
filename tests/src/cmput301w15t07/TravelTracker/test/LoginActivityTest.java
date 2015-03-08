package cmput301w15t07.TravelTracker.test;

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


import java.util.UUID;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import cmput301w15t07.TravelTracker.R;
import cmput301w15t07.TravelTracker.TravelTrackerApp;
import cmput301w15t07.TravelTracker.activity.ClaimsListActivity;
import cmput301w15t07.TravelTracker.activity.LoginActivity;
import cmput301w15t07.TravelTracker.model.DataSource;
import cmput301w15t07.TravelTracker.model.User;
import cmput301w15t07.TravelTracker.model.UserData;
import cmput301w15t07.TravelTracker.model.UserRole;
import cmput301w15t07.TravelTracker.testutils.SynchronizedResultCallback;

/**
 * Test for entry activity - Logging in.
 * 
 * Each relevant Use Case UC.XxxYyy is tested with method testXxxYyy()
 * 
 * @author kdbanman, colp
 *
 */
public class LoginActivityTest extends ActivityUnitTestCase<LoginActivity> {
	DataSource dataSource;
	Instrumentation instrumentation;
	Activity activity;
	EditText nameEditText;
	Button loginButton;
	RadioButton claimantButton;
	RadioButton approverButton;
	
	public LoginActivityTest() {
		super(LoginActivity.class);
		
		TravelTrackerApp app = new TravelTrackerApp();
		setApplication(app);
		app.onCreate();
		
		dataSource = app.getDataSource();
	}
	
	@Override
	protected void setUp() throws Exception {
	    super.setUp();
	    
	    instrumentation = getInstrumentation();
	    
	    Intent intent = new Intent(instrumentation.getTargetContext(), LoginActivity.class);
	    startActivity(intent, null, null);
	    
	    activity = getActivity();
	    nameEditText = (EditText) activity.findViewById(R.id.loginNameEditText);
	    loginButton = (Button) activity.findViewById(R.id.loginLoginButton);
	    claimantButton = (RadioButton) activity.findViewById(R.id.loginClaimantRadioButton);
	    approverButton = (RadioButton) activity.findViewById(R.id.loginApproverRadioButton);
	}
	
	public void testLoginEmptyName() {
	    Intent newIntent = loginWithDetails("", UserRole.CLAIMANT);
		
		assertNotNull("Error should be displayed", nameEditText.getError());
		assertEquals("No activity should start", null, newIntent);
	}
	
	public void testLoginStartsActivity() {
		Intent newIntent = loginWithDetails("Foobar", UserRole.CLAIMANT);

		assertNotNull("Activity should start", newIntent);
		
		String activityClass = newIntent.resolveActivity(activity.getPackageManager()).getClassName();
		assertEquals("New activity should list claims",
					 "cmput301w15t07.TravelTracker.activity.ClaimsListActivity",
					 activityClass);
	}
	
	public void testLoginName() {
		Intent newIntent = loginWithDetails("Foobar", UserRole.CLAIMANT);
		UserData userData = (UserData) newIntent.getSerializableExtra(ClaimsListActivity.USER_DATA);
		
		assertEquals("Name should match", "Foobar", userData.getName());
	}
	
	public void testLoginClaimant() {
		Intent newIntent = loginWithDetails("Foobar", UserRole.CLAIMANT);
		UserData userData = (UserData) newIntent.getSerializableExtra(ClaimsListActivity.USER_DATA);
		
		assertEquals("Role should be claimant", UserRole.CLAIMANT, userData.getRole());
	}
	
	public void testLoginApprover() {
		Intent newIntent = loginWithDetails("Foobar", UserRole.APPROVER);
		UserData userData = (UserData) newIntent.getSerializableExtra(ClaimsListActivity.USER_DATA);
		
		assertEquals("Role should be claimant", UserRole.APPROVER, userData.getRole());
	}
	
	public void testLoginNewUser() throws InterruptedException {
		Intent newIntent = loginWithDetails("New user", UserRole.CLAIMANT);
		UserData userData = (UserData) newIntent.getSerializableExtra(ClaimsListActivity.USER_DATA);
		
		SynchronizedResultCallback<User> callback = new SynchronizedResultCallback<User>();
		dataSource.getUser(userData.getUUID(), callback);
		
		assertTrue("User should exist", callback.waitForResult());
	}
	
	public void testLoginExistingUser() {
		Intent newIntent = loginWithDetails("Existing user", UserRole.CLAIMANT);
		UserData userData = (UserData) newIntent.getSerializableExtra(ClaimsListActivity.USER_DATA);
		UUID originalID = userData.getUUID();
		
		// Login again
		newIntent = loginWithDetails("Existing user", UserRole.CLAIMANT);
		userData = (UserData) newIntent.getSerializableExtra(ClaimsListActivity.USER_DATA);
		UUID newID = userData.getUUID();
		
		assertEquals("Same user should be returned", originalID, newID);
	}

	/**
	 * Log in with the given details.
	 * @param name The name to use.
	 * @param role The role to use.
	 * @return The intent which was created (or null if no intent).
	 */
	public Intent loginWithDetails(final String name, final UserRole role) {
		nameEditText.setText(name);
		
		switch (role) {
		case APPROVER:
			approverButton.performClick();
			break;
			
		case CLAIMANT:
			claimantButton.performClick();
			break;
		}
		loginButton.performClick();
		
		return getStartedActivityIntent();
	}
}