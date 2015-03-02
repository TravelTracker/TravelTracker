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

import java.util.UUID;

import cmput301w15t07.TravelTracker.R;
import cmput301w15t07.TravelTracker.model.UserRole;
/**
 * Launch activity.  Log in as a User with a Name and Role.
 * 
 * @author kdbanman, colp
 *
 */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

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
				login();
			}
		});
	}

	/**
	 * Log in to the application using information from the name and role views.
	 * Report an error if necessary.
	 */
	public void login() {
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
		
		// Start next activity
		Intent intent = new Intent(this, ClaimsListActivity.class);
		intent.putExtra(ClaimsListActivity.USER_ROLE, role);
		intent.putExtra(ClaimsListActivity.USER_NAME, userName);
		startActivity(intent);
	}
}
