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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import cmput301w15t07.TravelTracker.R;
import cmput301w15t07.TravelTracker.model.Claim;
import cmput301w15t07.TravelTracker.model.DataSource;
import cmput301w15t07.TravelTracker.model.Item;
import cmput301w15t07.TravelTracker.model.Status;
import cmput301w15t07.TravelTracker.model.User;
import cmput301w15t07.TravelTracker.model.UserData;
import cmput301w15t07.TravelTracker.model.UserRole;
import cmput301w15t07.TravelTracker.serverinterface.MultiCallback;
import cmput301w15t07.TravelTracker.serverinterface.ResultCallback;
import cmput301w15t07.TravelTracker.util.ApproverCommentAdapter;
import cmput301w15t07.TravelTracker.util.ClaimUtilities;
import cmput301w15t07.TravelTracker.util.DatePickerFragment;
import cmput301w15t07.TravelTracker.util.DestinationAdapter;
import cmput301w15t07.TravelTracker.util.Observer;
import cmput301w15t07.TravelTracker.util.TagAdapter;

/**
 * Activity for managing an individual Claim.  Possible as a Claimant or
 * an Approver.
 * 
 * @author kdbanman,
 *         therabidsquirel,
 *         colp,
 *         skwidz
 *
 */
public class ClaimInfoActivity extends TravelTrackerActivity implements Observer<DataSource> {
    /** ID used to retrieve items from MutliCallback. */
    public static final int MULTI_ITEMS_ID = 0;
    
    /** ID used to retrieve claimant from MutliCallback. */
    public static final int MULTI_CLAIMANT_ID = 1;
    
    /** ID used to retrieve last approver from MutliCallback. */
    public static final int MULTI_APPROVER_ID = 2;
    
    /** Data about the logged-in user. */
    private UserData userData;
    
    /** UUID of the claim. */
    private UUID claimID;
    
    /** The current claim. */
    Claim claim = null;
    
    /** The menu for the activity. */
    private Menu menu = null;

    /** The custom adapter for claim destinations. */
    DestinationAdapter destinationAdapter;

    /** The custom adapter for claim comments. */    
    ApproverCommentAdapter commentsListAdapter;
    
    /** The custom adapter for claim tags. */
    TagAdapter tagAdapter;

    /** The last alert dialog. */
    AlertDialog lastAlertDialog = null;
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.claim_info_menu, menu);
        this.menu = menu;
        
        if (claim != null) {
            hideMenuItems(menu, claim);
        }
        
        return true;
    }
    
    private void hideMenuItems(Menu menu, Claim claim) {
        // Menu items
        MenuItem addDestinationMenuItem = menu.findItem(R.id.claim_info_add_destination);
        MenuItem addItemMenuItem = menu.findItem(R.id.claim_info_add_item);
        MenuItem deleteClaimMenuItem = menu.findItem(R.id.claim_info_delete_claim);
        
        if (!isEditable(claim.getStatus(), userData.getRole())) {
            // Menu items that disappear when not editable
            addDestinationMenuItem.setEnabled(false).setVisible(false);
            addItemMenuItem.setEnabled(false).setVisible(false);
        }
        
        if (userData.getRole().equals(UserRole.APPROVER)) {
            deleteClaimMenuItem.setEnabled(false).setVisible(false);
        }
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.claim_info_add_destination:
            addDestination();
            return true;
            
        case R.id.claim_info_add_item:
            addItem(claim);
            return true;
            
        case R.id.claim_info_delete_claim:
            promptDeleteClaim();
            return true;
            
        case R.id.claim_info_sign_out:
            signOut();
            return true;
            
        case android.R.id.home:
        	onBackPressed();
            return true;
            
        default:
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        // Retrieve user info from bundle
        Bundle bundle = getIntent().getExtras();
        userData = (UserData) bundle.getSerializable(USER_DATA);
        appendNameToTitle(userData.getName());
        
        // Get claim info
        claimID = (UUID) bundle.getSerializable(CLAIM_UUID);
        
        datasource.addObserver(this);
    }
    
    /**
     * Update the activity when the dataset changes.
     * Called in onResume() and update(DataSource observable).
     */
    @Override
    public void updateActivity(){
        // Show loading circle
        setContentView(R.layout.loading_indeterminate);
        
        datasource.getClaim(claimID, new ClaimCallback());
    }
    
    /**
     * Get the last created AlertDialog.
     * @return The last dialog, or null if there isn't one.
     */
    public AlertDialog getLastAlertDialog() {
    	return lastAlertDialog;
    }
    
    /**
     * attach listeners to buttons/textviews/etc.
     * hide buttons/views according to user role
     * @param items Collection of a claims expense items
     * @param claimant User that created the claim
     * @param approver	user that approved the claim (if exsists)
     */
    public void onGetAllData(final Collection<Item> items, User claimant, User approver) {
    	setContentView(R.layout.claim_info_activity);
    	
        populateClaimInfo(claim, items, claimant, approver);
        
        if (menu != null) {
            hideMenuItems(menu, claim);
        }
        
        // Claim attributes
        LinearLayout statusLinearLayout = (LinearLayout) findViewById(R.id.claimInfoStatusLinearLayout);
        
        // Dates
        Button startDateButton = (Button) findViewById(R.id.claimInfoStartDateButton);
        Button endDateButton = (Button) findViewById(R.id.claimInfoEndDateButton);
        
        // Destinations list
        LinearLayout destinationsList = (LinearLayout) findViewById(R.id.claimInfoDestinationsLinearLayout);
        
        // Tags list
        TextView tagsTextView = (TextView) findViewById(R.id.claimInfoTagsTextView);
        LinearLayout tagsLinearLayout = (LinearLayout) findViewById(R.id.claimInfoTagsLinearLayout);
        View tagsThickHorizontalDivider = (View) findViewById(R.id.claimInfoTagsThickHorizontalDivider);

        // Claimant claim modifiers
        Button submitClaimButton = (Button) findViewById(R.id.claimInfoClaimSubmitButton);
        
        // Approver claim modifiers
        LinearLayout approverButtonsLinearLayout = (LinearLayout) findViewById(R.id.claimInfoApproverButtonsLinearLayout);
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
        	if (isEditable(claim.getStatus(), userData.getRole())) {
        	    // Color the LinearLayout to visually cue the user that it can be edited.
        	    colorViewEnabled(destinationsList);
        	    
	            // Attach edit date listener to start date button
	            startDateButton.setOnClickListener(new View.OnClickListener() {
	                @Override
	                public void onClick(View v) {
	                    startDatePressed();
	                }
	            });
	            
	            // Attach edit date listener to end date button
	            endDateButton.setOnClickListener(new View.OnClickListener() {
	                @Override
	                public void onClick(View v) {
	                   endDatePressed();
	                }
	            });
	            
	            // Attach submit claim listener to submit claim button
	            submitClaimButton.setOnClickListener(new View.OnClickListener() {
	                @Override
	                public void onClick(View v) {
	                    submitClaim();
	                }
	            });
        	} else {
        	    // These views should do nothing if the claim isn't editable
        	    colorViewDisabled(destinationsList);
        	    disableView(startDateButton);
        	    disableView(endDateButton);
        	    disableView(submitClaimButton);
        	}
            
            // Views a claimant doesn't need to see or have access to
            approverButtonsLinearLayout.setVisibility(View.GONE);
            commentEditText.setVisibility(View.GONE);
        }
        
        else if (userData.getRole().equals(UserRole.APPROVER)) {
            // Attach return claim listener to return claim button
            Button returnClaimButton = (Button) findViewById(R.id.claimInfoClaimReturnButton);
            returnClaimButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    returnClaim();
                }
            });
            
            // Attach approve claim listener to approve claim button
            Button approveClaimButton = (Button) findViewById(R.id.claimInfoClaimApproveButton);
            approveClaimButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    approveClaim();
                }
            });
            
            // The approver should see these views, but cannot use them.
            colorViewDisabled(destinationsList);
            disableView(startDateButton);
            disableView(endDateButton);
            
            // Views an approver doesn't need to see or have access to
            statusLinearLayout.setVisibility(View.GONE);
            tagsTextView.setVisibility(View.GONE);
            tagsLinearLayout.setVisibility(View.GONE);
            tagsThickHorizontalDivider.setVisibility(View.GONE);
            submitClaimButton.setVisibility(View.GONE);
            
            // No last approver
            if (approver == null) {
            	TextView lastApproverTextView = (TextView) findViewById(R.id.claimInfoApproverTextView);
            	lastApproverTextView.setVisibility(View.GONE);
            }
        }
        
        onLoaded();
    }
    
    public void addDestination() {
        LinearLayout destinationsList = (LinearLayout) findViewById(R.id.claimInfoDestinationsLinearLayout);
        destinationAdapter.addDestination(this, userData, destinationsList, getFragmentManager());
    }

    /**
     * Launches the ExpenseItemInfo activity for a new item
     * @param claim the current expense claim
     */
    public void addItem(Claim claim) {
        datasource.addItem(claim, new CreateNewItemCallback());
    }
    
    /**
     * Launches the ExpenseItemInfo activity for a selected item
     * @param item The selected expense item 
     */
    private void launchExpenseItemInfo(Item item){
        Intent intent = new Intent(this, ExpenseItemInfoActivity.class);
        intent.putExtra(ExpenseItemInfoActivity.FROM_CLAIM_INFO, true);
        intent.putExtra(ExpenseItemInfoActivity.ITEM_UUID, item.getUUID());
        intent.putExtra(ExpenseItemInfoActivity.CLAIM_UUID, claim.getUUID());
        intent.putExtra(ExpenseItemInfoActivity.USER_DATA, userData);
        startActivity(intent);
    }
    
    /**
     * Prompt for deleting the claim.
     */
    public void promptDeleteClaim() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.claim_info_delete_message)
			   .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						deleteClaim();
					}
			   })
			   .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Do nothing
					}
			   });
		
		lastAlertDialog = builder.create();
		lastAlertDialog.show();
    }

    /**
     * Delete the claim and finish the activity.
     */
    public void deleteClaim() {
        datasource.deleteClaim(claimID, new DeleteCallback());
    }

    /**
     * Populate the fields with data.
     * @param claim The claim being viewed.
     * @param items All items (which will be filtered by claim UUID).
     * @param claimant The claimant, or null if the current user is the claimant.
     * @param approver The last approver, or null if there isn't one.
     */
    public void populateClaimInfo(Claim claim, Collection<Item> items, User claimant, User approver) {
        Button startDateButton = (Button) findViewById(R.id.claimInfoStartDateButton);
        setButtonDate(startDateButton, claim.getStartDate());
        
        Button endDateButton = (Button) findViewById(R.id.claimInfoEndDateButton);
        setButtonDate(endDateButton, claim.getEndDate());
        
        TextView statusTextView = (TextView) findViewById(R.id.claimInfoStatusTextView);
        statusTextView.setText(claim.getStatus().getString(this));
        
        // Get list of claim items
        ArrayList<Item> claimItems = new ArrayList<Item>();
        for (Item item : items) {
        	if (item.getClaim().equals(claim.getUUID())) {
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
        
        // Show destinations
        LinearLayout destinationsList = (LinearLayout) findViewById(R.id.claimInfoDestinationsLinearLayout);
        destinationAdapter.createList(this, userData, destinationsList, getFragmentManager());
        
        if (userData.getRole().equals(UserRole.APPROVER)) {
        	// Claimant name
        	TextView claimantNameTextView = (TextView) findViewById(R.id.claimInfoClaimantNameTextView);
        	claimantNameTextView.setText(claimant.getUserName());
        } else if (userData.getRole().equals(UserRole.CLAIMANT)) {
            LinearLayout claimantNameLinearLayout = (LinearLayout) findViewById(R.id.claimInfoClaimantNameLinearLayout);
            claimantNameLinearLayout.setVisibility(View.GONE);
        }
    	
    	// Approver name (if there is one)
    	if (approver != null) {
            TextView approverTextView = (TextView) findViewById(R.id.claimInfoApproverTextView);
        	approverTextView.setText(approver.getUserName());
    	} else {
    	    LinearLayout approverLinearLayout = (LinearLayout) findViewById(R.id.claimInfoApproverLinearLayout);
    	    approverLinearLayout.setVisibility(View.GONE);
    	}
        
        // Show approver comments
        LinearLayout commentsList = (LinearLayout) findViewById(R.id.claimInfoCommentsLinearLayout);
        commentsListAdapter = new ApproverCommentAdapter(this, datasource, claim.getComments());
        
        for (int i = 0; i < commentsListAdapter.getCount(); i++) {
        	View commentView = commentsListAdapter.getView(i, null, null);
        	commentsList.addView(commentView);
        }
    	
    	// Scroll to top now in case comment list has extended the layout
    	// Referenced http://stackoverflow.com/a/4488149 on 12/03/15
    	final ScrollView scrollView = (ScrollView) findViewById(R.id.claimInfoScrollView);
    	
    	scrollView.post(new Runnable() {
			@Override public void run() {
			    scrollView.fullScroll(ScrollView.FOCUS_UP);
			}
		});
    }
    
    /**
     * starts the expenseItemList activity
     */
    public void viewItems() {
        // Start next activity
        Intent intent = new Intent(this, ExpenseItemsListActivity.class);
        intent.putExtra(ExpenseItemsListActivity.USER_DATA, userData);
        intent.putExtra(ExpenseItemsListActivity.CLAIM_UUID, claimID);
        startActivity(intent);
    }
    
    /**
     * spawns the datepicker fragment for startdate button
     */
    public void startDatePressed() {
    	Date date = claim.getStartDate();
    	
    	DatePickerFragment datePicker = new DatePickerFragment(date, new StartDateCallback());
        datePicker.show(getFragmentManager(), "datePicker");
    }
    
    /**
     * spawns the datpicker fragment for the end date button
     */
    public void endDatePressed() {
    	Date date = claim.getEndDate();
    	
        DatePickerFragment datePicker = new DatePickerFragment(date, new EndDateCallback());
        datePicker.show(getFragmentManager(), "datePicker");
    }
    
    /**
     * submits the selected claim and adds a comment if there exists one in the field
     */
    public void submitClaim() {
    	// submit only if all items of claim are flagged as complete
    	datasource.getAllItems(new ResultCallback<Collection<Item>>() {

			@Override
			public void onResult(Collection<Item> items) {
				boolean allComplete = true;
				for(Item item : items) {
					// only inspect items belonging to this claim
					if (item.getClaim().equals(claim.getUUID()))
						allComplete = allComplete && item.isComplete();
				}
				int dialogMessage = allComplete ? 
						R.string.claim_info_submit_confim :
						R.string.claim_info_not_all_items_complete;
				
				DialogInterface.OnClickListener submitDialogClickListener = new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				        switch (which){
				        case DialogInterface.BUTTON_POSITIVE:
				            claim.setStatus(Status.SUBMITTED);
				            ClaimInfoActivity.this.finish();
				            break;

				        case DialogInterface.BUTTON_NEGATIVE:
				        	Toast.makeText(ClaimInfoActivity.this, R.string.claim_info_not_submitted, Toast.LENGTH_SHORT).show();
				            break;
				        }
				    }
				};
				
				AlertDialog.Builder builder = new AlertDialog.Builder(ClaimInfoActivity.this);
				lastAlertDialog =  builder.setMessage(dialogMessage)
				       .setPositiveButton(android.R.string.yes, submitDialogClickListener)
				       .setNegativeButton(android.R.string.no, submitDialogClickListener)
				       .show();
			}

			@Override
			public void onError(String message) {
				Toast.makeText(ClaimInfoActivity.this, message, Toast.LENGTH_SHORT).show();
			}
    		
    	});
    }
    /**
     * returns the selected claim and adds a comment if there exists one in the field
     */
    public void returnClaim() {
    	DialogInterface.OnClickListener returnDialogClickListener = new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        switch (which){
		        case DialogInterface.BUTTON_POSITIVE:
		        	String commentText = ((TextView) findViewById(R.id.claimInfoCommentEditText)).getText().toString();
		        	
		        	// add approver comment if comment field is not empty
		        	if (!commentText.trim().equals("")) {
		        		claim.addComment(commentText);
		        	}
		        	
		        	claim.setApprover(userData.getUUID());
		        	claim.setStatus(Status.RETURNED);
		        	ClaimInfoActivity.this.finish();
		            break;

		        case DialogInterface.BUTTON_NEGATIVE:
		        	Toast.makeText(ClaimInfoActivity.this, R.string.claim_info_not_returned, Toast.LENGTH_SHORT).show();
		            break;
		        }
		    }
		};
		
		AlertDialog.Builder builder = new AlertDialog.Builder(ClaimInfoActivity.this);
		lastAlertDialog = builder.setMessage(R.string.claim_info_return_confirm)
		       .setPositiveButton(android.R.string.yes, returnDialogClickListener)
		       .setNegativeButton(android.R.string.no, returnDialogClickListener)
		       .show();
    }
    /**
     * approves the selected claim and adds a comment if there exists one in the field
     */
    public void approveClaim() {
    	DialogInterface.OnClickListener returnDialogClickListener = new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        switch (which){
		        case DialogInterface.BUTTON_POSITIVE:
		        	String commentText = ((TextView) findViewById(R.id.claimInfoCommentEditText)).getText().toString();
		        	
		        	// add approver comment if comment field is not empty
		        	if (!commentText.trim().equals("")) {
		        		claim.addComment(commentText);
		        	}
		        	
		        	claim.setApprover(userData.getUUID());
		        	claim.setStatus(Status.APPROVED);
		        	ClaimInfoActivity.this.finish();
		            break;

		        case DialogInterface.BUTTON_NEGATIVE:
		        	Toast.makeText(ClaimInfoActivity.this, R.string.claim_info_not_approved, Toast.LENGTH_SHORT).show();
		            break;
		        }
		    }
		};
		
		AlertDialog.Builder builder = new AlertDialog.Builder(ClaimInfoActivity.this);
		lastAlertDialog = builder.setMessage(R.string.claim_info_approve_confirm)
		       .setPositiveButton(android.R.string.yes, returnDialogClickListener)
		       .setNegativeButton(android.R.string.no, returnDialogClickListener)
		       .show();
    }
    /**
     * Set the date in the date button after 
	 * datePicker fragment is spawned and 
	 * interacted with by the user
     * @param dateButton The button to be set
     * @param date Date to set the button to
     */
    private void setButtonDate(Button dateButton, Date date) {
    	java.text.DateFormat dateFormat = DateFormat.getMediumDateFormat(this);
    	String dateString = dateFormat.format(date);
		dateButton.setText(dateString);
    }
    
    /**
     * Callback for claim data.
     */
    class ClaimCallback implements ResultCallback<Claim> {
		@Override
		public void onResult(Claim claim) {
			ClaimInfoActivity.this.claim = claim;

	        // Prep the adapter for claim destinations
	        ClaimInfoActivity.this.destinationAdapter = new DestinationAdapter(claim, claim.getDestinations());
			
	        // Retrieve data
	        MultiCallback multi = new MultiCallback(new ClaimDataMultiCallback());
	        
	        // Create callbacks for MultiCallback
	        datasource.getAllItems(multi.<Collection<Item>>createCallback(MULTI_ITEMS_ID));
	        datasource.getUser(claim.getUser(), multi.<User>createCallback(MULTI_CLAIMANT_ID));
	        
	        UUID approverID = claim.getApprover();
	        if (approverID != null) {
	        	datasource.getUser(approverID, multi.<User>createCallback(MULTI_APPROVER_ID));
	        }
	        
	        multi.ready();
		}
		
		@Override
		public void onError(String message) {
			Toast.makeText(ClaimInfoActivity.this, message, Toast.LENGTH_LONG).show();
		}
	}
    
    /**
     * Callback for multiple types of claim data.
     */
    class ClaimDataMultiCallback implements ResultCallback<SparseArray<Object>> {
		@Override
		public void onResult(SparseArray<Object> result) {
			User claimant = (User) result.get(MULTI_CLAIMANT_ID);
			
			User approver = null;
			if (claim.getApprover() != null) {
				approver = (User) result.get(MULTI_APPROVER_ID);
			}
			
			// We know the return result is the right type, so an unchecked
			// cast shouldn't be problematic 
			@SuppressWarnings("unchecked")
            Collection<Item> items = (Collection<Item>) result.get(MULTI_ITEMS_ID);
			
			onGetAllData(items, claimant, approver);
		}
		
		@Override
		public void onError(String message) {
			Toast.makeText(ClaimInfoActivity.this, message, Toast.LENGTH_LONG).show();
		}
	}
    
    /**
     * Callback for claim deletion.
     */
    class DeleteCallback implements ResultCallback<Void> {
		@Override
		public void onResult(Void result) {
			finish();
		}
		
		@Override
		public void onError(String message) {
			Toast.makeText(ClaimInfoActivity.this, message, Toast.LENGTH_LONG).show();
		}
	}
    
    /**
     * Callback for when a new start date is selected.
     */
    class StartDateCallback implements DatePickerFragment.ResultCallback {
		@Override
		public void onDatePickerFragmentResult(Date result) {
			// Error if invalid date
			if (result.after(claim.getEndDate())) {
				String error = getString(R.string.claim_info_start_date_error);
				Toast.makeText(ClaimInfoActivity.this, error, Toast.LENGTH_LONG).show();

        	// Update date button and date
			} else {
				claim.setStartDate(result);
				
				Button button = (Button) findViewById(R.id.claimInfoStartDateButton);
				setButtonDate(button, result);
			}
		}
		
		@Override
		public void onDatePickerFragmentCancelled() {}
	}
    
    /**
     * Callback for when a new end date is selected.
     */
    class EndDateCallback implements DatePickerFragment.ResultCallback {
		@Override
		public void onDatePickerFragmentResult(Date result) {
			// Error if invalid date
			if (result.before(claim.getStartDate())) {
				String error = getString(R.string.claim_info_end_date_error);
				Toast.makeText(ClaimInfoActivity.this, error, Toast.LENGTH_LONG).show();

        	// Update date button and calendar
			} else {
				claim.setEndDate(result);
				
				Button button = (Button) findViewById(R.id.claimInfoEndDateButton);
				setButtonDate(button, result);
			}
		}
		
		@Override
		public void onDatePickerFragmentCancelled() {}
	}
    
    /**
     * Callback for when a new item is added.
     */
    class CreateNewItemCallback implements ResultCallback<Item> {
        @Override
        public void onResult(Item result){
            launchExpenseItemInfo(result);
        }
        @Override
        public void onError(String message){
            Toast.makeText(ClaimInfoActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    }
}
