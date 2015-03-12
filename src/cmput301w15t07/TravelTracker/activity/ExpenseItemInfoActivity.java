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

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import cmput301w15t07.TravelTracker.R;
import cmput301w15t07.TravelTracker.model.UserData;
import cmput301w15t07.TravelTracker.model.UserRole;

/**
 * Activity for viewing and managing data related to an individual Expense Item.
 * 
 * @author kdbanman,
 *         skwidz,
 *         therabidsquirel
 *
 */
public class ExpenseItemInfoActivity extends TravelTrackerActivity {
    /** Data about the logged-in user. */
	private UserData userData;

    /** UUID of the claim. */
    private UUID claimID;
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.expense_item_info_menu, menu);
		
		// Menu items
		MenuItem deleteItemMenuItem = menu.findItem(R.id.expense_item_info_delete_item);
		
        if (userData.getRole().equals(UserRole.CLAIMANT)) {
            
        } else if (userData.getRole().equals(UserRole.APPROVER)) {
            // Menu items an approver doesn't need to see or have access to
            deleteItemMenuItem.setEnabled(false).setVisible(false);
        }
        
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.expense_item_info_delete_item:
	        deleteExpenseItem();
	        break;
	        
	    case R.id.expense_item_info_sign_out:
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
		setContentView(R.layout.expense_info_activity);
		
		//user info from bundles
		Bundle bundle = getIntent().getExtras();
		userData = (UserData) bundle.getSerializable(USER_DATA);
		
        // Get claim info
        claimID = (UUID) bundle.getSerializable(CLAIM_UUID);

		appendNameToTitle(userData.getName());
		populateExpenseInfo();
			
		//attach view Listener for ItemStatus CheckedTextView
		final CheckedTextView itemStatus = (CheckedTextView) findViewById(R.id.expenseItemInfoStatusCheckedTextView);
		itemStatus.setOnClickListener(new View.OnClickListener() {
			
			
			@Override
			public void onClick(View v) {
				if(itemStatus.isChecked()){
					itemStatus.setChecked(false);
					setItemStatus(false);
				}
				else{
					itemStatus.setChecked(true);
					setItemStatus(true);
				}
			}
		});
		
		//Attach view Listener for receipt Image View
		ImageView receiptImage = (ImageView) findViewById(R.id.expenseItemInfoReceiptImageView);
		receiptImage.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO: add code for image picker
				
			}
		});
		
		//Attach listener for expense date button
		Button dateButton = (Button) findViewById(R.id.expenseItemInfoDateButton);
		dateButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				datePressed(v);
				
			}
		});
		
		
	}
	
	private void populateExpenseInfo() {
		// TODO Auto-generated method stub
		
	}

	public void setItemStatus(Boolean status){
		//TODO: implement status changes
	}
	
	public void deleteExpenseItem() {
		// TODO Auto-generated method stub
		
	}
	
	public void datePressed(View date){
		//TODO: spawn fragment for selecting date
	}
}
