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

import java.util.Currency;
import java.util.Date;
import java.util.UUID;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.View;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import cmput301w15t07.TravelTracker.R;
import cmput301w15t07.TravelTracker.model.Claim;
import cmput301w15t07.TravelTracker.model.Item;
import cmput301w15t07.TravelTracker.model.ItemCategory;
import cmput301w15t07.TravelTracker.model.ItemCurrency;
import cmput301w15t07.TravelTracker.model.UserData;
import cmput301w15t07.TravelTracker.model.UserRole;
import cmput301w15t07.TravelTracker.serverinterface.ResultCallback;
import cmput301w15t07.TravelTracker.util.DatePickerFragment;


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

    /** The current claim. */
    Claim claim;
    
    /** UUID of the item. */
    private UUID itemID;
    
    /** The current item. */
    Item item = null;
    
    /** Boolean for whether we got to this activity from ClaimInfoActivity or not */
    private Boolean fromClaimInfo;
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.expense_item_info_menu, menu);
		
		// Menu items
		MenuItem deleteItemMenuItem = menu.findItem(R.id.expense_item_info_delete_item);
		
        if (!isEditable(claim.getStatus(), userData.getRole())) {
            // Menu items that disappear when not editable
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
		
		//user info from bundles
		Bundle bundle = getIntent().getExtras();
		userData = (UserData) bundle.getSerializable(USER_DATA);
		
        // Get claim info
        claimID = (UUID) bundle.getSerializable(CLAIM_UUID);
        
        //get item into
        itemID = (UUID) bundle.getSerializable(ITEM_UUID);
        
        // Get whether we came from ClaimInfoActivity or not
        fromClaimInfo = (Boolean) bundle.getSerializable(FROM_CLAIM_INFO);
        
		appendNameToTitle(userData.getName());
	}
	
	protected void onResume() {
		super.onResume();
		
        // Show loading circle
        setContentView(R.layout.loading_indeterminate);
        
        // TODO This should probably be a MultiCallback
		datasource.getClaim(claimID, new getClaimCallback());
		datasource.getItem(itemID, new getItemCallback());
	}
	
	@Override
	public void onBackPressed() {
	    // If we came here from ClaimInfoActivity, ExpenseItemsListActivity won't have been started
	    if (fromClaimInfo) {
	        Intent intent = new Intent(this, ExpenseItemsListActivity.class);
	        intent.putExtra(USER_DATA, userData);
	        intent.putExtra(CLAIM_UUID, claimID);
	        startActivity(intent);
	    }
        
	    super.onBackPressed();
	}
	/**
	 * Fill buttons/spinners/editText with data from item, set listeners, hide or
	 * disable things according to user's role and claim's status
	 * @param item The current expense item
	 */
	private void onGetAllData(final Item item) {
        setContentView(R.layout.expense_info_activity); 
		
        populateItemInfo(item);
        
        final CheckedTextView itemStatus = (CheckedTextView) findViewById(R.id.expenseItemInfoStatusCheckedTextView);
        Button dateButton = (Button) findViewById(R.id.expenseItemInfoDateButton);
        
        EditText itemAmount = (EditText) findViewById(R.id.expenseItemInfoAmountEditText);
        EditText itemDescription = (EditText) findViewById(R.id.expenseItemInfoDescriptionEditText);
        
        if (userData.getRole().equals(UserRole.CLAIMANT)) {
            if (isEditable(claim.getStatus(), userData.getRole())) {
                // Attach view Listener for ItemStatus CheckedTextView
                itemStatus.setOnClickListener(new View.OnClickListener() {
                    
                    @Override
                    public void onClick(View v) {
                        if (itemStatus.isChecked()) {
                            itemStatus.setChecked(false);
                            item.setComplete(false);
                        } else {
                            itemStatus.setChecked(true);
                            item.setComplete(true);
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
                
                // Attach listener for expense date button
                dateButton.setOnClickListener(new View.OnClickListener() {
                    
                    @Override
                    public void onClick(View v) {
                        datePressed(v);
                    }
                });
                
                // Add listener for description editText
                itemDescription.addTextChangedListener(new TextWatcher() {
                    
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        item.setDescription(s.toString());
                    }
                    
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        // TODO Auto-generated method stub
                    }
                    
                    @Override
                    public void afterTextChanged(Editable s) {
                        // TODO Auto-generated method stub              
                    }
                });
                
                // Add listener for amount edit text
                itemAmount.addTextChangedListener(new TextWatcher() {
                    
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        try {
                            item.setAmount(Float.parseFloat(s.toString()));
                        } catch (NumberFormatException e) {
                            //Dont do anything, the string is empty
                        }
                    }
                    
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        // TODO Auto-generated method stub
                    }
                    
                    @Override
                    public void afterTextChanged(Editable s) {
                        // TODO Auto-generated method stub
                    }
                });
                
                // Add listener for currency spinner
                Spinner currencySpinner = (Spinner) findViewById(R.id.expenseItemInfoCurrencySpinner);
                currencySpinner.setOnItemSelectedListener(new OnItemSelectedListener(){
                    
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
                        Currency currency = ((ItemCurrency) parent.getItemAtPosition(position)).getCurrency(ExpenseItemInfoActivity.this);
                        item.setCurrency(currency);
                    }
                    
                    public void onNothingSelected(AdapterView<?> arg0){}
                
                });
                
                // Add listener for category spinner
                Spinner categorySpinner = (Spinner) findViewById(R.id.expenseItemInfoCategorySpinner);
                categorySpinner.setOnItemSelectedListener(new OnItemSelectedListener(){
                    
                    public void onItemSelected(AdapterView<?> adapter, View view, int position, long id){
                        item.setCategory( (ItemCategory) adapter.getItemAtPosition(position));
                    }
                    
                    public void onNothingSelected(AdapterView<?> arg0){}
                });
            } else {
                // These views should do nothing if the claim isn't editable
                disableButton(dateButton);
                disableEditText(itemAmount);
                disableEditText(itemDescription);
            }
            
        }
        
        else if (userData.getRole().equals(UserRole.APPROVER)) {
            // The approver should see these views, but cannot use them.
            disableButton(dateButton);
            disableEditText(itemAmount);
            disableEditText(itemDescription);
            
            // Views an approver doesn't need to see or have access to
            itemStatus.setVisibility(View.GONE);
        }
        
        onLoaded();
	}
	
	/**
	 * Populate the fields with data.
	 * @param item The item being viewed.
	 */
	public void populateItemInfo(Item item) {
        //TODO:catch null pointer exceptions for empty claims/fields
        Button itemDateButton = (Button) findViewById(R.id.expenseItemInfoDateButton);
        try {
            setButtonDate(itemDateButton, item.getDate());
        } catch (NullPointerException e){
            //the field is empty, so dont load anything
        }
            
        //TODO: populate receipt image
        
        //TODO: Note, amount string will have to be changed back to float before being inserted into model
        String amount = Float.toString(item.getAmount());
        EditText itemAmount = (EditText) findViewById(R.id.expenseItemInfoAmountEditText);
        try {
            itemAmount.setText(amount);
        } catch (NullPointerException e) {
            // the Field is empty, so dont load anything
        }
        
        //TODO: import data for currency spinner
        Spinner currencySpinner = (Spinner) findViewById(R.id.expenseItemInfoCurrencySpinner);
        currencySpinner.setAdapter(new ArrayAdapter<ItemCurrency>(this, android.R.layout.simple_spinner_item, ItemCurrency.values()));
        try {
            currencySpinner.setSelection(ItemCurrency.fromString(item.getCurrency().toString(), this).ordinal(),true);
        } catch (NullPointerException e) {
            // the field is null or empty, dont load anything
        }
        
        //TODO: import the category for the spinner
        //change generated data source file to get proper data for enums 
        Spinner categorySpinner = (Spinner) findViewById(R.id.expenseItemInfoCategorySpinner);
        categorySpinner.setAdapter(new ArrayAdapter<ItemCategory>(this, android.R.layout.simple_spinner_item, ItemCategory.values()));
        try {
            categorySpinner.setSelection(item.getCategory().ordinal(),true);
        } catch (NullPointerException e) {
            // Item is empty or null, dont load anything
        }
        
        EditText itemDescription = (EditText) findViewById(R.id.expenseItemInfoDescriptionEditText);
        try {
            itemDescription.setText(item.getDescription());
        } catch (NullPointerException e) {
            // the field is empty, so dont load anything
        }
        
        CheckedTextView itemStatus = (CheckedTextView) findViewById(R.id.expenseItemInfoStatusCheckedTextView);
        itemStatus.setChecked(item.isComplete());
	}
	
	/**
	 * get the index in a spinner array
	 * @param spinner
	 * @param string
	 * @return
	 */
	public int getIndex(Spinner spinner, String string){
		int index = 0;
		for(int i=0;i<spinner.getCount();i++){
			if (spinner.getItemAtPosition(i).equals(string)){
				index = i; 
			}
		}
		return index;
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
	
		
	public void deleteExpenseItem() {
		// TODO Auto-generated method stub
		
	}
	/**
	 * spawn a datepicker fragment when dateButton is pressed
	 * @param date
	 */
	public void datePressed(View date){
		Date itemDate = item.getDate();
		DatePickerFragment datePicker = new  DatePickerFragment(itemDate, new DateCallback());
		datePicker.show(getFragmentManager(), "datePicker");
	}
	/**
	 * callback for the datepicker fragment
	 */
	class DateCallback implements DatePickerFragment.ResultCallback{
		@Override
		public void onDatePickerFragmentResult(Date result) {
			
			
				item.setDate(result);
				
				Button button = (Button) findViewById(R.id.expenseItemInfoDateButton);
				setButtonDate(button, result);
			
		}
		
		@Override
		public void onDatePickerFragmentCancelled() {
			// Do nothing
		}
	}
	
	/**
	 * Callback for claim data.
	 */
	class getClaimCallback implements ResultCallback<Claim> {
        @Override
        public void onResult(Claim claim) {
            ExpenseItemInfoActivity.this.claim = claim;
        }

        @Override
        public void onError(String message) {
            Toast.makeText(ExpenseItemInfoActivity.this, message, Toast.LENGTH_LONG).show();
        }
	}
	
	/**
	 * Callback for item data.
	 */
	class getItemCallback implements ResultCallback<Item> {
        @Override
        public void onResult(Item item) {
            ExpenseItemInfoActivity.this.item = item;
            if (ExpenseItemInfoActivity.this.item != null){
                onGetAllData(item);
            }
            else{
                Toast.makeText(ExpenseItemInfoActivity.this, "the item var is null", Toast.LENGTH_LONG).show();
            }
        }
        
        @Override
        public void onError(String message) {
            Toast.makeText(ExpenseItemInfoActivity.this, message, Toast.LENGTH_LONG).show();
        }
	}
}
