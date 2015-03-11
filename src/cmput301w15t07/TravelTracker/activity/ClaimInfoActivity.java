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
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

import cmput301w15t07.TravelTracker.DataSourceSingleton;
import cmput301w15t07.TravelTracker.R;
import cmput301w15t07.TravelTracker.model.Claim;
import cmput301w15t07.TravelTracker.model.Item;
import cmput301w15t07.TravelTracker.model.DataSource;
import cmput301w15t07.TravelTracker.model.User;
import cmput301w15t07.TravelTracker.model.UserData;
import cmput301w15t07.TravelTracker.model.UserRole;
import cmput301w15t07.TravelTracker.serverinterface.ResultCallback;
import cmput301w15t07.TravelTracker.util.ClaimUtilities;
import cmput301w15t07.TravelTracker.util.DatePickerFragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity for managing an individual Claim.  Possible as a Claimant or
 * an Approver.
 * 
 * @author kdbanman,
 *         therabidsquirel,
 *         colp
 *
 */
public class ClaimInfoActivity extends TravelTrackerActivity {
    /** String used to retrieve user data from intent */
    public static final String USER_DATA = "cmput301w15t07.TravelTracker.userData";
    
    /** String used to retrieve claim UUID from intent */
    public static final String CLAIM_UUID = "cmput301w15t07.TravelTracker.claimUUID";
    
    /** Data about the logged-in user. */
    private UserData userData;
    
    /** UUID of the claim. */
    private UUID claimID;
    
    /** The current claim. */
    Claim claim;
    
    /** The currently open date picker fragment. */
    private DatePickerFragment datePicker = null;
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.claim_info_menu, menu);
        
        // Menu items
        MenuItem addDestinationMenuItem = menu.findItem(R.id.claim_info_add_destination);
        MenuItem addItemMenuItem = menu.findItem(R.id.claim_info_add_item);
        MenuItem deleteClaimMenuItem = menu.findItem(R.id.claim_info_delete_claim);
        
        // Attach sign out listener to sign out menu item
        MenuItem signOutMenuItem = menu.findItem(R.id.claim_info_sign_out);
        signOutMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                signOut();
                return false;
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
            
        } else if (userData.getRole().equals(UserRole.APPROVER)) {
            // Menu items an approver doesn't need to see or have access to
            addDestinationMenuItem.setEnabled(false).setVisible(false);
            addItemMenuItem.setEnabled(false).setVisible(false);
            deleteClaimMenuItem.setEnabled(false).setVisible(false);
        }
        
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	
    	// Show loading circle
        setContentView(R.layout.loading_indeterminate);
        
        // Retrieve user info from bundle
        Bundle bundle = getIntent().getExtras();
        userData = (UserData) bundle.getSerializable(USER_DATA);
        
        // Get claim info
        claimID = (UUID) bundle.getSerializable(CLAIM_UUID);
        DataSourceSingleton app = (DataSourceSingleton) getApplication();
        final DataSource source = app.getDataSource();
        
        // Retrieve claim
        // TODO: is there a better way to do this?
        source.getClaim(claimID, new ResultCallback<Claim>() {
			@Override
			public void onResult(final Claim claim) {
				
				// Claim retrieved; need items too
				source.getAllItems(new ResultCallback<Collection<Item>>() {
					@Override
					public void onResult(final Collection<Item> items) {
						// Don't need claimant if this user is the claimant
						if (userData.getRole() == UserRole.CLAIMANT) {
							onGetAllData(claim, items, null);
							
						// Need the claimant name
						} else if (userData.getRole() == UserRole.APPROVER) {
							
							// Retrieve claimant
							source.getUser(claim.getUser(), new ResultCallback<User>() {
								@Override
								public void onResult(User claimant) {
									onGetAllData(claim, items, claimant);
								}
								
								@Override
								public void onError(String message) {
									Toast.makeText(ClaimInfoActivity.this, message, Toast.LENGTH_LONG).show();
								}
							});
						}
					}
					
					@Override
					public void onError(String message) {
						Toast.makeText(ClaimInfoActivity.this, message, Toast.LENGTH_LONG).show();
					}
				});
			}
			
			@Override
			public void onError(String message) {
				Toast.makeText(ClaimInfoActivity.this, message, Toast.LENGTH_LONG).show();
			}
		});
    }
    
    public void onGetAllData(final Claim claim, final Collection<Item> items, User claimant) {
    	this.claim = claim;
    	setContentView(R.layout.claim_info_activity);
    	
        appendNameToTitle();
        populateClaimInfo(claim, items, claimant);
        
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
            // Attach edit date listener to start date button
            final Button startDateButton = (Button) findViewById(R.id.claimInfoStartDateButton);
            startDateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    datePressed(startDateButton, claim.getStartDate());
                }
            });
            
            // Attach edit date listener to end date button
            final Button endDateButton = (Button) findViewById(R.id.claimInfoEndDateButton);
            endDateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    datePressed(endDateButton, claim.getEndDate());
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
            claimantNameTextView.setVisibility(View.GONE);
            approverButtonsLinearLayout.setVisibility(View.GONE);
            returnClaimButton.setVisibility(View.GONE);
            approveClaimButton.setVisibility(View.GONE);
            commentEditText.setVisibility(View.GONE);
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
            
            // Views an approver doesn't need to see or have access to
            statusTextView.setVisibility(View.GONE);
            tagsLinearLayout.setVisibility(View.GONE);
            tagsSpace.setVisibility(View.GONE);
            submitClaimButton.setVisibility(View.GONE);
        }
        
        onLoaded();
    }

    public void signOut() {
    	// adapted from 
    	//    http://stackoverflow.com/questions/6298275/how-to-finish-every-activity-on-the-stack-except-the-first-in-android
    	// on 10 March 2015
    	Intent intent = new Intent(this, LoginActivity.class);
    	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Removes other Activities from stack
    	startActivity(intent);
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

    /**
     * Populate the fields with data.
     * @param claim The claim being viewed.
     * @param items All items (which will be filtered by claim UUID).
     * @param user The claimant, or null if the current user is the claimant.
     */
    public void populateClaimInfo(Claim claim, Collection<Item> items, User claimant) {
        Button startDateButton = (Button) findViewById(R.id.claimInfoStartDateButton);
        setButtonDate(startDateButton, claim.getStartDate());
        
        Button endDateButton = (Button) findViewById(R.id.claimInfoEndDateButton);
        setButtonDate(endDateButton, claim.getEndDate());
        
        String statusString = getString(R.string.claim_info_claim_status) + " " + claim.getStatus().getString(this);
        TextView statusTextView = (TextView) findViewById(R.id.claimInfoStatusTextView);
        statusTextView.setText(statusString);
        
        // Get list of claim items
        ArrayList<Item> claimItems = new ArrayList<Item>();
        for (Item item : items) {
        	if (item.getClaim() == claim.getUUID()) {
        		claimItems.add(item);
        	}
        }
        
        // Format string for view items button
        String formatString = getString(R.string.claim_info_view_items);
        String viewItemsButtonText = String.format(formatString, claimItems.size()); 
        
        Button viewItemsButton = (Button) findViewById(R.id.claimInfoViewItemsButton);
        viewItemsButton.setText(viewItemsButtonText);
        
        // List totals
        ArrayList<String> totals = ClaimUtilities.getTotals(claimItems);
        String totalsString = TextUtils.join("\n", totals);
        TextView totalsTextView = (TextView) findViewById(R.id.claimInfoCurrencyTotalsListTextView);
        totalsTextView.setText(totalsString);
        
        if (userData.getRole() == UserRole.APPROVER) {
        	TextView claimantNameTextView = (TextView) findViewById(R.id.claimInfoClaimantNameTextView);
        	String claimantString = getString(R.string.claim_info_claimant_name) + " " + claimant.getUserName();
        	claimantNameTextView.setText(claimantString);
        }
    }

    public void viewItems() {
        // Start next activity
        Intent intent = new Intent(this, ExpenseItemsListActivity.class);
        intent.putExtra(ExpenseItemsListActivity.USER_DATA, userData);
        intent.putExtra(ExpenseItemsListActivity.CLAIM_UUID, claimID);
        startActivity(intent);
    }

    public void datePressed(final Button dateButton, final Date date) {
        datePicker = new DatePickerFragment(date,
    		new DatePickerFragment.ResultCallback() {
				@Override
				public void onDatePickerFragmentResult(Date result) {
		        	// Update date button and calendar
					date.setTime(result.getTime());
					setButtonDate(dateButton, result);
					
					datePicker = null;
				}
				
				@Override
				public void onDatePickerFragmentCancelled() {
					datePicker = null;
				}
			});
        
        datePicker.show(getFragmentManager(), "datePicker");
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
    
    /**
     * Get the currently displayed date picker fragment.
     * @return The DatePickerFragment, or null if there isn't one.
     */
    public DatePickerFragment getDatePickerFragment() {
    	return datePicker;
    }
    
    private void setButtonDate(Button dateButton, Date date) {
    	java.text.DateFormat dateFormat = DateFormat.getMediumDateFormat(this);
    	String dateString = dateFormat.format(date);
		dateButton.setText(dateString);
    }
}
