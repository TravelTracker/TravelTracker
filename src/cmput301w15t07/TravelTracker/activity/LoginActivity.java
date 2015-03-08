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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import cmput301w15t07.TravelTracker.R;
import cmput301w15t07.TravelTracker.TravelTrackerApp;
import cmput301w15t07.TravelTracker.model.DataSource;
import cmput301w15t07.TravelTracker.model.User;
import cmput301w15t07.TravelTracker.model.UserData;
import cmput301w15t07.TravelTracker.model.UserRole;
import cmput301w15t07.TravelTracker.serverinterface.ResultCallback;

/**
 * Launch activity.  Log in as a User with a Name and Role.
 * 
 * @author kdbanman, colp
 *
 */
public class LoginActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);

		// Attach login listener to login button
		Button loginButton = (Button) findViewById(R.id.loginLoginButton);
		loginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				loginPressed();
			}
		});
	}

	/**
	 * Attempt to log in to the application using information from the name and role views.
	 */
	public void loginPressed() {
		// Determine user
		EditText nameEditText = (EditText) findViewById(R.id.loginNameEditText);
		String userName = nameEditText.getText().toString();
		
		// Invalid user name
		if (userName.isEmpty()) {
			nameEditText.setError(getString(R.string.login_no_name_error));
			return;
		}
		
		// Determine role
		RadioGroup group = (RadioGroup) findViewById(R.id.loginRoleRadioGroup);
		UserRole role;
		
		switch (group.getCheckedRadioButtonId()) {
		case R.id.loginApproverRadioButton:
			role = UserRole.APPROVER;
			break;
			
		case R.id.loginClaimantRadioButton:
			role = UserRole.CLAIMANT;
			break;
			
		default:
			throw new RuntimeException("Somehow, no role was selected");
		}
		
		// Try to get the user's ID to login
		TravelTrackerApp app = (TravelTrackerApp) getApplication();
		DataSource source = app.getDataSource();
		source.getAllUsers(new LoginResultCallback(userName, role));
	}
	
	/**
	 * Proceed to the next activity when user data has been retrieved.
	 */
	public void loginWithUserData(UserData userData) {
		// Start next activity
		Intent intent = new Intent(this, ClaimsListActivity.class);
		intent.putExtra(ClaimsListActivity.USER_DATA, userData);
		startActivity(intent);
	}
	
	/**
	 * Callback for getAllUsers which attempts to get the user ID and log in.
	 */
	private class LoginResultCallback implements ResultCallback<Collection<User>> {
		String userName;
		UserRole userRole;
		
		public LoginResultCallback(String userName, UserRole userRole) {
	        this.userName = userName;
	        this.userRole = userRole;
        }
		
		@Override
        public void onResult(Collection<User> result) {
			// Try to find the user
			for (User user : result) {
				
				// There is an existing user with this name
				if (user.getUserName().equals(userName)) {
					UserData userData = new UserData(user.getUUID(), userName, userRole);
					LoginActivity.this.loginWithUserData(userData);
					
					return;
				}
			}
			
			// No user found; create a new one
			TravelTrackerApp app = (TravelTrackerApp) getApplication();
			DataSource source = app.getDataSource();
			source.addUser(new ResultCallback<User>() {
				@Override
				public void onResult(User result) {
					result.setUserName(userName);
					
					// Log in with the new user data.
					UserData userData = new UserData(result.getUUID(), userName, userRole);
					LoginActivity.this.loginWithUserData(userData);
				}
				
				@Override
				public void onError() {
					Toast.makeText(LoginActivity.this, "Failed to create a new user. Please try again.", Toast.LENGTH_LONG).show();
				}
			});
        }

		@Override
        public void onError() {
	        Toast.makeText(LoginActivity.this, "Failed to retrieve user list. Please try again.", Toast.LENGTH_LONG).show();
        }
	}
}
