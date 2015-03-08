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
import cmput301w15t07.TravelTracker.model.UserData;
import cmput301w15t07.TravelTracker.model.UserRole;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

/**
 * Activity for managing an individual Claim.  Possible as a Claimant or
 * an Approver.
 * 
 * @author kdbanman,
 *         therabidsquirel
 *
 */
public class ClaimInfoActivity extends Activity {
    /** String used to retrieve user data from intent */
    public static final String USER_DATA = "cmput301w15t07.TravelTracker.userData";
    
    /** Data about the logged-in user. */
    private UserData userData;
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.claim_info_menu, menu);
        
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.claim_info_activity);
        
        // Retrieve user info from bundle
        Bundle bundle = getIntent().getExtras();
        userData = (UserData) bundle.getSerializable(USER_DATA);
        
        // TODO Get claim from bundle so its info can be used to populate the activity
        
        appendNameToTitle();
        populateClaimInfo();
        
        // Menu items
        MenuItem addDestinationMenuItem = (MenuItem) findViewById(R.id.claim_info_add_destination);
        MenuItem addItemMenuItem = (MenuItem) findViewById(R.id.claim_info_add_item);
        MenuItem deleteClaimMenuItem = (MenuItem) findViewById(R.id.claim_info_delete_claim);
        
        // Attach sign out listener to sign out menu item
        MenuItem signOutMenuItem = (MenuItem) findViewById(R.id.claim_info_sign_out);
        signOutMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                signOut();
                return false;
            }
        });
        
        // Claim attributes
        TextView claimantNameTextView = (TextView) findViewById(R.id.claimInfoClaimantNameTextView);
        TextView statusTextView = (TextView) findViewById(R.id.claimInfoStatusTextView);

        // Tags list
        LinearLayout tagsLinearLayout = (LinearLayout) findViewById(R.id.claimInfoTagsLinearLayout);
        Space tagsSpace = (Space) findViewById(R.id.claimInfoTagsSpace);

        // Claimant claim modifiers
        Button submitClaimButton = (Button) findViewById(R.id.claimInfoClaimSubmitButton);
        
        // Approver claim modifiers
        LinearLayout approverButtonsLinearLayout = (LinearLayout) findViewById(R.id.claimInfoApproverButtonsLinearLayout);
        Button returnClaimButton = (Button) findViewById(R.id.claimInfoClaimReturnButton);
        Button approveClaimButton = (Button) findViewById(R.id.claimInfoClaimApproveButton);
        EditText commentEditText = (EditText) findViewById(R.id.claimInfoCommentEditText);
        
        // Attach view items listener to view items button
        Button viewItemsButton = (Button) findViewById(R.id.claimInfoViewItemsButton);
        viewItemsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewItems();
            }
        });

        if (userData.getRole().equals(UserRole.CLAIMANT)) {
            // Attach add destination listener to add destination menu item
            addDestinationMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    addDestination();
                    return false;
                }
            });

            // Attach add item listener to add item menu item
            addItemMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    addItem();
                    return false;
                }
            });

            // Attach delete claim listener to delete claim menu item
            deleteClaimMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    deleteClaim();
                    return false;
                }
            });

            // Attach edit date listener to start date button
            Button startDateButton = (Button) findViewById(R.id.claimInfoStartDateButton);
            startDateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    datePressed(v);
                }
            });
            
            // Attach edit date listener to end date button
            Button endDateButton = (Button) findViewById(R.id.claimInfoEndDateButton);
            endDateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    datePressed(v);
                }
            });
            
            // Attach submit claim listener to submit claim button
            submitClaimButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    submitClaim();
                }
            });
            
            // Views a claimant doesn't need to see or have access to
            claimantNameTextView.setVisibility(View.INVISIBLE);
            approverButtonsLinearLayout.setVisibility(View.INVISIBLE);
            returnClaimButton.setVisibility(View.INVISIBLE);
            approveClaimButton.setVisibility(View.INVISIBLE);
            commentEditText.setVisibility(View.INVISIBLE);
        }
        
        else if (userData.getRole().equals(UserRole.APPROVER)) {
            // Attach return claim listener to return claim button
            returnClaimButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    returnClaim();
                }
            });
            
            // Attach approve claim listener to approve claim button
            approveClaimButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    approveClaim();
                }
            });

            // Menu items an approver doesn't need to see or have access to
            addDestinationMenuItem.setEnabled(false).setVisible(false);
            addItemMenuItem.setEnabled(false).setVisible(false);
            deleteClaimMenuItem.setEnabled(false).setVisible(false);
            
            // Views an approver doesn't need to see or have access to
            statusTextView.setVisibility(View.INVISIBLE);
            tagsLinearLayout.setVisibility(View.INVISIBLE);
            tagsSpace.setVisibility(View.INVISIBLE);
            submitClaimButton.setVisibility(View.INVISIBLE);
        }
        
    }

    public void signOut() {
        // TODO Auto-generated method stub
        
    }

    public void addDestination() {
        // TODO Auto-generated method stub
        
    }

    public void addItem() {
        // TODO Auto-generated method stub
        
    }

    public void deleteClaim() {
        // TODO Auto-generated method stub
        
    }

    public void appendNameToTitle() {
        setTitle(getTitle() + " - " + userData.getName());
    }

    public void populateClaimInfo() {
        // TODO Auto-generated method stub
        
    }

    public void viewItems() {
        // Start next activity
        Intent intent = new Intent(this, ExpenseItemsListActivity.class);
        intent.putExtra(ExpenseItemsListActivity.USER_DATA, userData);
        startActivity(intent);
    }

    public void datePressed(View dateButton) {
        // TODO Auto-generated method stub
        
    }

    public void submitClaim() {
        // TODO Auto-generated method stub
        
    }

    public void returnClaim() {
        // TODO Auto-generated method stub
        
    }

    public void approveClaim() {
        // TODO Auto-generated method stub
        
    }
}
