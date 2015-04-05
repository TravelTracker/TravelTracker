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

import java.io.FileNotFoundException;
import java.util.Collection;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import cmput301w15t07.TravelTracker.R;
import cmput301w15t07.TravelTracker.model.User;
import cmput301w15t07.TravelTracker.model.UserData;
import cmput301w15t07.TravelTracker.model.UserRole;
import cmput301w15t07.TravelTracker.serverinterface.ResultCallback;
import cmput301w15t07.TravelTracker.util.GsonIOManager;

/**
 * Launch activity.  Log in as a User with a Name and Role.
 * 
 * @author kdbanman,
 *         colp,
 *         therabidsquirel
 *
 */
public class LoginActivity extends TravelTrackerActivity {
    
    private static final String LOGIN_FILENAME = "cached_login.json";
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);
		
		// Attempt to find last login.
		UserData userData = loadUserData();
		
		// If not null, load was successful and a last login was found.
		if (userData != null) {
		    // Set user's name from last login.
	        EditText nameEditText = (EditText) findViewById(R.id.loginNameEditText);
	        nameEditText.setText(userData.getName());
	        
	        // Set user's role from last login.
            UserRole role = userData.getRole();
	        if (role.equals(UserRole.CLAIMANT)) {
                RadioButton button = (RadioButton) findViewById(R.id.loginClaimantRadioButton);
                button.setChecked(true);
	        } else if (role.equals(UserRole.APPROVER)) {
                RadioButton button = (RadioButton) findViewById(R.id.loginApproverRadioButton);
                button.setChecked(true);
	        }
		}

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
     * There is no dataset in LoginActivity.
     * Called in onResume() and update(DataSource observable).
     */
    @Override
    public void updateActivity() {
        // Do nothing
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
		datasource.getAllUsers(new LoginResultCallback(userName, role));
	}
	
	/**
	 * Proceed to the next activity when user data has been retrieved.
	 */
	public void loginWithUserData(UserData userData) {
		// Start next activity
		Intent intent = new Intent(this, ClaimsListActivity.class);
		saveUserData(userData);
		intent.putExtra(ClaimsListActivity.USER_DATA, userData);
		startActivity(intent);
	}

	/**
	 * Attempt to load the UserData object from a predetermined file.
	 * @return The UserData object from the file, or null if an error occurs or no file is found.
	 */
	private UserData loadUserData() {
        GsonIOManager gson = new GsonIOManager(this);
        try {
            return gson.<UserData>load(LOGIN_FILENAME, (new TypeToken<UserData>() {}).getType());
        } catch (FileNotFoundException e) {
            // Do nothing. File is expected not to be found if running app for first time.
        } catch (JsonSyntaxException e) {
            warn("Cached " + LOGIN_FILENAME + " is incorrect type.");
        }
        return null;
    }
	
	/**
	 * Save a UserData object to a predetermined file.
	 * @param userData UserData to save.
	 */
	private void saveUserData(UserData userData) {
        GsonIOManager gson = new GsonIOManager(this);
        gson.save(userData, LOGIN_FILENAME, (new TypeToken<UserData>() {}).getType());
	}
    
    private void warn(String message) {
        Log.e("LoginActivity", message);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
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
			datasource.addUser(new ResultCallback<User>() {
				@Override
				public void onResult(User result) {
					result.setUserName(userName);
					
					// Log in with the new user data.
					UserData userData = new UserData(result.getUUID(), userName, userRole);
					LoginActivity.this.loginWithUserData(userData);
				}
				
				@Override
				public void onError(String message) {
					Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
				}
			});
        }

		@Override
        public void onError(String message) {
	        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
        }
	}
}
