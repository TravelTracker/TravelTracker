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

package cmput301w15t07.TravelTracker.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.SparseArray;
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
import cmput301w15t07.TravelTracker.model.DataSource;
import cmput301w15t07.TravelTracker.model.Item;
import cmput301w15t07.TravelTracker.model.ItemCategory;
import cmput301w15t07.TravelTracker.model.ItemCurrency;
import cmput301w15t07.TravelTracker.model.Receipt;
import cmput301w15t07.TravelTracker.model.UserData;
import cmput301w15t07.TravelTracker.model.UserRole;
import cmput301w15t07.TravelTracker.serverinterface.MultiCallback;
import cmput301w15t07.TravelTracker.serverinterface.ResultCallback;
import cmput301w15t07.TravelTracker.util.DatePickerFragment;
import cmput301w15t07.TravelTracker.util.Observer;

/**
 * Activity for viewing and managing data related to an individual Expense Item.
 * 
 * @author kdbanman,
 *         skwidz,
 *         therabidsquirel
 *
 */
public class ExpenseItemInfoActivity extends TravelTrackerActivity implements Observer<DataSource> {
    /** Key for multicallback for claim. */
    public static final int MULTI_CLAIM_KEY = 0;

    /** Key for multicallback for item. */
    public static final int MULTI_ITEM_KEY = 1;
    
    /** Data about the logged-in user. */
	private UserData userData;

    /** UUID of the claim. */
    private UUID claimID;

    /** The current claim. */
    Claim claim = null;
    
    /** UUID of the item. */
    private UUID itemID;
    
    /** The current item. */
    Item item = null;
    
    /** The menu for the activity. */
    private Menu menu = null;

    /** The last alert dialog. */
    AlertDialog lastAlertDialog = null;
    
    /** Boolean for whether we got to this activity from ClaimInfoActivity or not */
    private Boolean fromClaimInfo;
    
    /** The current receipt image filepath for viewing image */ 
    String currentPhotoPath = null;
    
    /** Uri for the receipt image */
    private Uri imageUri;
    
    private static final int CAMERA_REQUEST = 100;
    private static final int RESULT_LOAD_IMAGE = 999; 
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.expense_item_info_menu, menu);
        this.menu = menu;
        
        if (claim != null) {
            hideMenuItems(menu, claim);
        }
        
		return true;
	}
	
    private void hideMenuItems(Menu menu, Claim claim) {
        // Menu items
        MenuItem deleteItemMenuItem = menu.findItem(R.id.expense_item_info_delete_item);
        
        if (!isEditable(claim.getStatus(), userData.getRole())) {
            // Menu items that disappear when not editable
            deleteItemMenuItem.setEnabled(false).setVisible(false);
        }
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.expense_item_info_delete_item:
	        promptDeleteExpenseItem();
	        return true;
	        
	    case R.id.expense_item_info_sign_out:
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
		
		//user info from bundles
		Bundle bundle = getIntent().getExtras();
		userData = (UserData) bundle.getSerializable(USER_DATA);
        appendNameToTitle(userData.getName());
		
        // Get claim info
        claimID = (UUID) bundle.getSerializable(CLAIM_UUID);
        
        //get item into
        itemID = (UUID) bundle.getSerializable(ITEM_UUID);
        
        // Get whether we came from ClaimInfoActivity or not
        fromClaimInfo = (Boolean) bundle.getSerializable(FROM_CLAIM_INFO);
        
		datasource.addObserver(this);
	}

    @Override
    protected void onResume() {
        super.onResume();
        
        // Show loading circle
        setContentView(R.layout.loading_indeterminate);
        loading = true;
        
        updateActivity();
    }
    
    /**
     * Update the activity when the dataset changes.
     * Called in onResume() and update(DataSource observable).
     */
    @Override
    public void updateActivity() {
        // Multicallback for claim and item
        MultiCallback multi = new MultiCallback(new UpdateDataCallback());
        
        // Create callbacks
        datasource.getClaim(claimID, multi.<Claim>createCallback(MULTI_CLAIM_KEY));
        datasource.getItem(itemID, multi.<Item>createCallback(MULTI_ITEM_KEY));
        
        // Notify ready so callbacks can execute
        multi.ready();
    }
    
	@Override
	public void onBackPressed() {
	    // If we came here from ClaimInfoActivity, ExpenseItemsListActivity won't have been started
	    if (fromClaimInfo) {
	        launchExpenseItemsList();
	    }
        
	    super.onBackPressed();
	}

    /**
     * Launches the ExpenseItemsList activity.
     */
    private void launchExpenseItemsList() {
        Intent intent = new Intent(this, ExpenseItemsListActivity.class);
        intent.putExtra(ExpenseItemsListActivity.USER_DATA, userData);
        intent.putExtra(ExpenseItemsListActivity.CLAIM_UUID, claimID);
        startActivity(intent);
    }
	
    /**
     * Launches the ReceiptImageView activity.
     */
    private void launchReceiptImageView(){
        Intent intent = new Intent(this, ReceiptImageViewActivity.class);
        intent.putExtra(ReceiptImageViewActivity.ITEM_UUID, itemID);
        startActivity(intent);
    }
    
	/**
	 * Fill buttons/spinners/editText with data from item, set listeners, hide or
	 * disable things according to user's role and claim's status
	 * @param item The current expense item
	 */
	private void onGetAllData(final Item item) {
        if (!loading) {
            onLoaded();
            return;
        }
        
        loading = false;
        setContentView(R.layout.expense_info_activity); 
		
        populateItemInfo(item);
        
        if (menu != null) {
            hideMenuItems(menu, claim);
        }
        
        final CheckedTextView itemStatus = (CheckedTextView) findViewById(R.id.expenseItemInfoStatusCheckedTextView);
        Button dateButton = (Button) findViewById(R.id.expenseItemInfoDateButton);
        
        final EditText itemDescription = (EditText) findViewById(R.id.expenseItemInfoDescriptionEditText);
        final EditText itemAmount = (EditText) findViewById(R.id.expenseItemInfoAmountEditText);
        
        Spinner currencySpinner = (Spinner) findViewById(R.id.expenseItemInfoCurrencySpinner);
        Spinner categorySpinner = (Spinner) findViewById(R.id.expenseItemInfoCategorySpinner);
        
        ImageView receiptImage = (ImageView) findViewById(R.id.expenseItemInfoReceiptImageView);
        
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
                receiptImage.setOnClickListener(new View.OnClickListener() {
                    
                    @Override
                    public void onClick(View v) {
                    	if (item.getReceipt().getPhoto() == null) {
                    		promptTakePhoto();
                    	}else {
                    		promptChangePhoto();
                    	}
                    	
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
                    public void onTextChanged(CharSequence s, int start, int before, int count) { }
                    
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                    
                    @Override
                    public void afterTextChanged(Editable s) {
                    	item.setDescription(s.toString());   
                    }
                });
                
                // Add listener for amount edit text
                itemAmount.addTextChangedListener(new TextWatcher() {
                    
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) { }
                    
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                    
                    @Override
                    public void afterTextChanged(Editable s) {
                    	try {
                            item.setAmount(Float.parseFloat(s.toString()));
                        } catch (NumberFormatException e) {
                            //Dont do anything, the string is empty
                        }
                    }
                });
                
                // Add listener for currency spinner
                currencySpinner.setOnItemSelectedListener(new OnItemSelectedListener(){
                    
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
                        String currString = (String) parent.getItemAtPosition(position);
                        ItemCurrency currency = ItemCurrency.fromString(currString, ExpenseItemInfoActivity.this);
                        item.setCurrency(currency);
                    }
                    
                    public void onNothingSelected(AdapterView<?> arg0){}
                });
                
                // Add listener for category spinner
                categorySpinner.setOnItemSelectedListener(new OnItemSelectedListener(){
                    
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
                        String catString = (String) parent.getItemAtPosition(position);
                        ItemCategory category = ItemCategory.fromString(catString, ExpenseItemInfoActivity.this);
                        item.setCategory(category);
                    }
                    
                    public void onNothingSelected(AdapterView<?> arg0){}
                });
                
            } else {
                // These views should do nothing if the claim isn't editable
                disableView(itemStatus);
                disableView(dateButton);
                disableView(itemDescription);
                disableView(itemAmount);
                disableView(currencySpinner);
                disableView(categorySpinner);
            }
            
        }
        
        else if (userData.getRole().equals(UserRole.APPROVER)) {
            // The approver should see these views, but cannot use them.
            disableView(itemStatus);
            disableView(dateButton);
            disableView(itemDescription);
            disableView(itemAmount);
            disableView(currencySpinner);
            disableView(categorySpinner);
            
            // Views an approver doesn't need to see or have access to
            itemStatus.setVisibility(View.GONE);
            
            //set listener for receipt imagae to load view activity
            receiptImage.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (item.getReceipt().getPhoto() != null){
						launchReceiptImageView();
					}
				}
			});
            
        }
        
        onLoaded();
	}
	
	
	
	/**
	 * Prompt for changing, viewing, or deleting a receipt image
	 * will only be spawned if a receipt image exists for the current item
	 */
	public void promptChangePhoto(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.expense_item_info_change_receipt_message)
			.setPositiveButton(R.string.expense_item_info_change_image, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					promptTakePhoto();
					
				}
			})
			.setNeutralButton(R.string.expense_item_info_view_image, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					launchReceiptImageView();
					
				}
			})
			.setNegativeButton(R.string.expense_item_info_delete_image, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//set the receipt to a new one to delete photo 
					item.setReceipt(new Receipt());
					populateItemInfo(item);
				}
			});
		lastAlertDialog = builder.create();
		lastAlertDialog.show();
	}
	
	/**
	 * Prompt for creating a new receipt image.
	 */
	public void promptTakePhoto(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.expense_item_info_capture_receipt_message)
			.setPositiveButton(R.string.expense_item_info_take_photo, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					takePhoto();					
				}
			})
			.setNegativeButton(R.string.expense_item_info_choose_from_gallery, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					chooseImageFromGallery();
				}
			});
		lastAlertDialog = builder.create();
		lastAlertDialog.show();
	}
	
	public void chooseImageFromGallery(){
		Intent intent = new Intent(	Intent.ACTION_PICK, 
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent, RESULT_LOAD_IMAGE);
	}
	
	/**
	 * creates an image file with a unique name to be used by camera activity 
	 * @return image file with unique name
	 * @throws IOException if the file could not be created
	 */
	//Referenced developer.android.com/training/camera/photobasics.html
	private File createImageFile() throws IOException{
		File storageDir = Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES);
		String imageFileName = "JPEG_" 
				+ String.valueOf(System.currentTimeMillis()) + "_";
		File image = File.createTempFile(imageFileName, ".jpg", storageDir);
		currentPhotoPath = "file:" + image.getAbsolutePath();
		return image;
	}
	
	/**
	 * launch the camera activity and take a photo
	 */
	public void takePhoto(){		
		Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //check if there is an app that can handle the intent
        if (cameraIntent.resolveActivity(getPackageManager()) != null){
        	//create the file where the image should go
        	File imageFile = null;
        	try{
        		imageFile = createImageFile();
        	}catch (IOException exception){
        		//error occured when creating the file
        		Toast.makeText(ExpenseItemInfoActivity.this,
        		        getString(R.string.expense_item_info_failed_to_get_image),
        		        Toast.LENGTH_LONG).show();
        	}
        	//only continue if the file was succesfully created
        	if (imageFile != null){
        		imageUri = Uri.fromFile(imageFile);
        		cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        	}
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        }
	}
	
	//get the bmp thumbnail for the image 
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		
		//if Result is from takePhoto()
		if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK){
			Bitmap imageBitmap = null;
			try {
				imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
			} catch (FileNotFoundException e) {
				Toast.makeText(this, getString(R.string.expense_item_info_failed_to_get_image), Toast.LENGTH_SHORT).show();
			} catch (IOException e) {
                Toast.makeText(this, getString(R.string.expense_item_info_failed_to_get_image), Toast.LENGTH_SHORT).show();
			}
			item.setReceipt(new Receipt(imageBitmap));
		}
		//if result is from chooseImageFromGallery()
		//refrenced viralpatel.net/blogs/pick-image-from-galary-android-app/
		//i know this url is misspelled, but thats what the site is ^^
		else if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK){
			imageUri = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };
			Cursor cursor = getContentResolver().query(imageUri, 
					filePathColumn, null, null, null);
			cursor.moveToFirst();
			
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();
			
			item.setReceipt(new Receipt(BitmapFactory.decodeFile(picturePath)));
			
		}
	}
	/**
	 * Populate the fields with data.
	 * @param item The item being viewed.
	 */
	public void populateItemInfo(Item item) {
	    // Set the date.
        Button itemDateButton = (Button) findViewById(R.id.expenseItemInfoDateButton);
        setButtonDate(itemDateButton, item.getDate());
        
        // Populate receipt image.
        // TODO This should not have to worry about null values, Receipt should handle that.
        ImageView receiptImageView = (ImageView) findViewById(R.id.expenseItemInfoReceiptImageView);
        try {
        	if (item.getReceipt().getPhoto() == null) {
        		receiptImageView.setImageBitmap(BitmapFactory.decodeStream(getAssets()
        				.open("receipt.png")));
        	} else {
        		receiptImageView.setImageBitmap(item.getReceipt().getPhoto());
        	}
		} catch (IOException e1) {
			Toast.makeText(ExpenseItemInfoActivity.this, e1.getMessage(), Toast.LENGTH_LONG).show();
		}
      
        // Set the amount.
        String amount = Float.toString(item.getAmount());
        EditText itemAmount = (EditText) findViewById(R.id.expenseItemInfoAmountEditText);
        itemAmount.setText(amount);
        
        // Set currency spinner with strings from ItemCurrency.
        Spinner currencySpinner = (Spinner) findViewById(R.id.expenseItemInfoCurrencySpinner);
        currencySpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout
        		.simple_spinner_item, ItemCurrency.getStringArray(this)));
        currencySpinner.setSelection(item.getCurrency().ordinal(), true);
        
        // Set category spinner with strings from ItemCategory.
        Spinner categorySpinner = (Spinner) findViewById(R.id.expenseItemInfoCategorySpinner);
        categorySpinner.setAdapter(new ArrayAdapter<String>(this, android.R
        		.layout.simple_spinner_item, ItemCategory.getStringArray(this)));
        categorySpinner.setSelection(item.getCategory().ordinal(), true);
        
        // Set the description.
        EditText itemDescription = (EditText) findViewById(R.id.expenseItemInfoDescriptionEditText);
        itemDescription.setText(item.getDescription());
        
        CheckedTextView itemStatus = (CheckedTextView)
        		findViewById(R.id.expenseItemInfoStatusCheckedTextView);
        itemStatus.setChecked(item.isComplete());
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
     * Prompt for deleting the claim.
     */
    public void promptDeleteExpenseItem() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.claim_info_delete_message)
			   .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						deleteExpenseItem();
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
     * Get the last created AlertDialog.
     * @return The last dialog, or null if there isn't one.
     */
    public AlertDialog getLastAlertDialog() {
    	return lastAlertDialog;
    }
    
	public void deleteExpenseItem() {
		datasource.deleteItem(itemID, new DeleteCallback());
		
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
     * Multicallback meant to get all data required from the datasource that
     * this activity needs on update or resume.
     */
    class UpdateDataCallback implements ResultCallback<SparseArray<Object>> {
        /**
         * Saves the claim and item, then calls method with retrieved data.
         * 
         * @param result The request result.
         */
        @Override
        public void onResult(SparseArray<Object> result) {
            claim = (Claim) result.get(MULTI_CLAIM_KEY);
            item = (Item) result.get(MULTI_ITEM_KEY);
            ExpenseItemInfoActivity.this.onGetAllData(item);
        }

        @Override
        public void onError(String message) {
            Toast.makeText(ExpenseItemInfoActivity.this, message, Toast.LENGTH_LONG).show();
        }
    }
    
	/**
     * Callback for Item deletion.
     */
    class DeleteCallback implements ResultCallback<Void> {
		@Override
		public void onResult(Void result) {
			finish();
		}
		
		@Override
		public void onError(String message) {
			Toast.makeText(ExpenseItemInfoActivity.this, message, Toast.LENGTH_LONG).show();
		}
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
}
