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

package cmput301w15t07.TravelTracker.test.activity;

import java.util.Calendar;
import java.util.Date;

import cmput301w15t07.TravelTracker.DataSourceSingleton;
import cmput301w15t07.TravelTracker.R;
import cmput301w15t07.TravelTracker.activity.ExpenseItemInfoActivity;
import cmput301w15t07.TravelTracker.activity.TravelTrackerActivity;
import cmput301w15t07.TravelTracker.model.Claim;
import cmput301w15t07.TravelTracker.model.DataSource;
import cmput301w15t07.TravelTracker.model.InMemoryDataSource;
import cmput301w15t07.TravelTracker.model.Item;
import cmput301w15t07.TravelTracker.model.ItemCategory;
import cmput301w15t07.TravelTracker.model.ItemCurrency;
import cmput301w15t07.TravelTracker.model.Receipt;
import cmput301w15t07.TravelTracker.model.User;
import cmput301w15t07.TravelTracker.model.UserData;
import cmput301w15t07.TravelTracker.model.UserRole;
import cmput301w15t07.TravelTracker.testutils.SynchronizedResultCallback;
import cmput301w15t07.TravelTracker.util.DatePickerFragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.Instrumentation;
import android.app.AlertDialog.Builder;
import android.app.Instrumentation.ActivityMonitor;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

public class ExpenseItemInfoActivityTest extends ActivityInstrumentationTestCase2<ExpenseItemInfoActivity> {
	DataSource dataSource;
	Instrumentation instrumentation; 
	ExpenseItemInfoActivity activity;
	User user; 
	Claim claim; 
	Item item; 
	
	public ExpenseItemInfoActivityTest() {
		super(ExpenseItemInfoActivity.class);
	}
	
	@Override 
	protected void setUp() throws Exception {
		dataSource = new InMemoryDataSource();
		DataSourceSingleton.setDataSource(dataSource);
		
		//Add a user 
		SynchronizedResultCallback<User> userCallback = new SynchronizedResultCallback<User>();
		dataSource.addUser(userCallback);
		userCallback.waitForResult();
		user = userCallback.getResult();
		
		//Add a claim 
		SynchronizedResultCallback<Claim> claimCallback = new SynchronizedResultCallback<Claim>();
		dataSource.addClaim(user, claimCallback);
		claimCallback.waitForResult();
		claim = claimCallback.getResult();
		
		
		//Add an item 
		SynchronizedResultCallback<Item> itemCallback = new SynchronizedResultCallback<Item>();
		dataSource.addItem(claim, itemCallback);
		itemCallback.waitForResult();
		item = itemCallback.getResult();
		
		super.setUp();
		
		instrumentation = getInstrumentation();
	}
	
	
	
	public void testViewExpenseItemIsComplete() throws InterruptedException{
		startWithItem(UserRole.APPROVER);
		
		CheckedTextView itemStatus = (CheckedTextView) activity.findViewById(R.id.expenseItemInfoStatusCheckedTextView);
		assertTrue("status should be incomplete", itemStatus.isChecked());
	}
	
	public void testViewExpenseItemDate() throws InterruptedException{
		startWithItem(UserRole.APPROVER);
		
		Button dateButton = (Button) activity.findViewById(R.id.expenseItemInfoDateButton);
		String buttonText = dateButton.getText().toString();
		
		assertEquals("Date should be shown in the correct format", "May 18, 2015", buttonText);
	}
	
	public void testViewExpenseItemAmount() throws InterruptedException{
		startWithItem(UserRole.APPROVER);
		
		EditText amount = (EditText) activity.findViewById(R.id.expenseItemInfoAmountEditText);
		String text = amount.getText().toString();
		
		assertEquals("Amount should be shown", "30.0", text);
		
	}
	
	public void testViewExpenseItemCurrency() throws InterruptedException{
		startWithItem(UserRole.APPROVER);
		
		Spinner spinner = (Spinner) activity.findViewById(R.id.expenseItemInfoCurrencySpinner);
		String currency = spinner.getSelectedItem().toString();
		
		assertEquals("Currency should be shown", "GBP", currency);
	}
	
	public void testViewExpenseItemCategory() throws InterruptedException{
		startWithItem(UserRole.APPROVER);
		
		Spinner spinner = (Spinner) activity.findViewById(R.id.expenseItemInfoCategorySpinner);
		String category = spinner.getSelectedItem().toString();
		
		assertEquals("Category should be shown", "Fuel", category);
	}
	
	public void testViewExpenseItemDescripton() throws InterruptedException{
		startWithItem(UserRole.APPROVER);
		
		EditText description =(EditText) activity.findViewById(R.id.expenseItemInfoDescriptionEditText);
		String text = description.getText().toString();
		
		assertEquals("description should be displayed", "Test Description", text);
	}
	
	//TODO test view reciept
	
	public void testSetExpenseItemDate() throws InterruptedException{
		startWithItem(UserRole.CLAIMANT);
		
		instrumentation.runOnMainSync(new Runnable() {
			
			@Override
			public void run() {
				Button dateButton = (Button) activity.findViewById(R.id.expenseItemInfoDateButton);
				dateButton.performClick();				
			}
		});
		
		instrumentation.waitForIdleSync();
		FragmentManager manager = activity.getFragmentManager();
		DatePickerFragment fragment = (DatePickerFragment) manager.findFragmentByTag("datePicker");
		DatePickerDialog dialog = (DatePickerDialog) fragment.getDialog();
		final DatePicker picker = dialog.getDatePicker();
		final Button accept = dialog.getButton(Dialog.BUTTON_POSITIVE);
		
		instrumentation.runOnMainSync(new Runnable() {
			
			@Override
			public void run() {
				picker.updateDate(1992, 05, 15);
				accept.performClick();
			}
		});
		instrumentation.waitForIdleSync();
		
		Button dateButton = (Button) activity.findViewById(R.id.expenseItemInfoDateButton);
		String text = dateButton.getText().toString();
		
		assertEquals("Date should update", "Jun 15, 1992", text);
	}
	
	public void testSetExpenseItemIsComplete() throws InterruptedException{
		startWithItem(UserRole.CLAIMANT);
		
		final CheckedTextView status = (CheckedTextView) activity.findViewById(R.id.expenseItemInfoStatusCheckedTextView);
		instrumentation.runOnMainSync(new Runnable() {
			
			@Override
			public void run() {
				status.performClick();
				
			}
		});
		
		assertFalse("itemstatus shold not be checked", status.isChecked());
	}
	
	
	
	public void testDeleteExpenseItem() throws InterruptedException {
		startWithItem(UserRole.CLAIMANT);
		
		instrumentation.invokeMenuActionSync(activity, R.id.expense_item_info_delete_item, 0);
		
		AlertDialog dialog = activity.getLastAlertDialog();
		assertTrue("Alert should be shown", dialog.isShowing());
		
		final Button confirm = dialog.getButton(Dialog.BUTTON_POSITIVE);
		
		instrumentation.runOnMainSync(new Runnable() {
			
			@Override
			public void run() {
				confirm.performClick();
				
			}
		});
		
		instrumentation.waitForIdleSync();
		assertTrue("Activity should finish", activity.isFinishing());
		
		SynchronizedResultCallback<Item> itemCallback = new SynchronizedResultCallback<Item>();
		dataSource.getItem(item.getUUID(), itemCallback);
		assertTrue("Item should not exist", itemCallback.waitForResult());
	}
	
	public void testExpenseItemInfoTakePhotoRecipt() throws InterruptedException {
		startWithItem(UserRole.CLAIMANT);
		
		instrumentation.runOnMainSync(new Runnable() {
			
			@Override
			public void run() {
				ImageView receipt = (ImageView) activity.findViewById(R.id.expenseItemInfoReceiptImageView);
				receipt.performClick();	
			}
		});
		
		instrumentation.waitForIdleSync();
		AlertDialog dialog = activity.getLastAlertDialog();
		assertTrue("dialog should be showing", dialog.isShowing());
		
		final Button takePhoto = dialog.getButton(Dialog.BUTTON_POSITIVE);
		
		instrumentation.runOnMainSync(new Runnable() {
			
			@Override
			public void run() {
				//takePhoto.performClick();
				
			}
		});
		//wait for child activity
		//HOW to get activity ID of camera app and test functionality? 
						
	}
	
	public void testViewPhotographReceipt() {
		
	}
	
	public void testDeletePhotographReceipt() {
		
	}
	
	public void testViewGeolocation(){
		
	}
	
	public void testEditGeolocation(){
		
	}
	
	public void testDeleteGeolocation(){
		
	}
	
	private void startWithItem(UserRole role) throws InterruptedException{
		//make a claim and item 
		user.setUserName("testing user");
		Calendar start = Calendar.getInstance();
		start.set(2015, 04, 01);
		claim.setStartDate(start.getTime());
		
		Calendar end = Calendar.getInstance();
		end.set(2015, 04, 18);
		claim.setEndDate(end.getTime());
		
		//add item for testing. 
		Item item = addItemToClaim(); 
		item.setAmount(30.f);
		item.setCurrency(ItemCurrency.GBP);
		item.setDate(end.getTime());
		item.setCategory(ItemCategory.FUEL);
		item.setComplete(true);
		item.setReceipt(new Receipt(null)); //set this to an actual image
		item.setDescription("Test Description");
		
		//create the intent 
		Intent intent = new Intent();
		intent.putExtra(TravelTrackerActivity.USER_DATA, 
				new UserData(user.getUUID(), user.getUserName(), role));
		intent.putExtra(TravelTrackerActivity.CLAIM_UUID, claim.getUUID());
		intent.putExtra(TravelTrackerActivity.ITEM_UUID, item.getUUID());
		
		//start the activity
		setActivityIntent(intent);
		activity = getActivity();
		activity.waitUntilLoaded();
		
		
	}
	private Item addItemToClaim() throws InterruptedException{
		SynchronizedResultCallback<Item> itemCallback = new SynchronizedResultCallback<Item>();
		dataSource.addItem(claim, itemCallback);
		itemCallback.waitForResult();
		Item item = itemCallback.getResult();
		
		return item;
	}

}
