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

import org.w3c.dom.Comment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.Instrumentation;
import android.app.Instrumentation.ActivityMonitor;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import cmput301w15t07.TravelTracker.DataSourceSingleton;
import cmput301w15t07.TravelTracker.R;
import cmput301w15t07.TravelTracker.activity.ClaimInfoActivity;
import cmput301w15t07.TravelTracker.activity.ExpenseItemsListActivity;
import cmput301w15t07.TravelTracker.activity.TravelTrackerActivity;
import cmput301w15t07.TravelTracker.model.ApproverComment;
import cmput301w15t07.TravelTracker.model.Claim;
import cmput301w15t07.TravelTracker.model.DataSource;
import cmput301w15t07.TravelTracker.model.InMemoryDataSource;
import cmput301w15t07.TravelTracker.model.Item;
import cmput301w15t07.TravelTracker.model.ItemCurrency;
import cmput301w15t07.TravelTracker.model.Status;
import cmput301w15t07.TravelTracker.model.User;
import cmput301w15t07.TravelTracker.model.UserData;
import cmput301w15t07.TravelTracker.model.UserRole;
import cmput301w15t07.TravelTracker.testutils.SynchronizedResultCallback;
import cmput301w15t07.TravelTracker.util.DatePickerFragment;

/**
 * Test for individual claim management activities.
 * 
 * Each relevant Use Case UC.XxxYyy is tested with method testXxxYyy()
 * 
 * @author kdbanman,
 * 		   colp,
 *         therabidsquirel
 *
 */
public class ClaimInfoActivityTest extends ActivityInstrumentationTestCase2<ClaimInfoActivity> {
	DataSource dataSource;
	Instrumentation instrumentation;
	ClaimInfoActivity activity;
	User user;
	Claim claim;

	public ClaimInfoActivityTest() {
		super(ClaimInfoActivity.class);
	}
	
	@Override
	protected void setUp() throws Exception {
		dataSource = new InMemoryDataSource();
		DataSourceSingleton.setDataSource(dataSource);
		
		// Add a user
		SynchronizedResultCallback<User> userCallback = new SynchronizedResultCallback<User>();
		dataSource.addUser(userCallback);
		userCallback.waitForResult();
		user = userCallback.getResult();
		
		// Add a claim
		SynchronizedResultCallback<Claim> claimCallback = new SynchronizedResultCallback<Claim>();
		dataSource.addClaim(user, claimCallback);
		claimCallback.waitForResult();
		claim = claimCallback.getResult();
		
	    super.setUp();
	    
	    instrumentation = getInstrumentation();
	}
	
	public void testDeleteExpenseClaim() throws InterruptedException {
		startWithClaim(UserRole.CLAIMANT);
		
		// Referenced http://stackoverflow.com/a/4125024 on 14/03/15
		instrumentation.invokeMenuActionSync(activity, R.id.claim_info_delete_claim, 0);
		
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
		
		SynchronizedResultCallback<Claim> claimCallback = new SynchronizedResultCallback<Claim>();
		dataSource.getClaim(claim.getUUID(), claimCallback);
		assertFalse("Claim should not longer exist", claimCallback.waitForResult());
	}
	
	public void testViewExpenseClaimStartDate() throws InterruptedException {
		startWithClaim(UserRole.APPROVER);
		
		Button startDateButton = (Button) activity.findViewById(R.id.claimInfoStartDateButton);
		String buttonText = startDateButton.getText().toString();
		
		assertEquals("Start date should be shown in correct format", "May 1, 2015", buttonText);
	}
	
	public void testViewExpenseClaimEndDate() throws InterruptedException {
		startWithClaim(UserRole.APPROVER);
		
		Button endDateButton = (Button) activity.findViewById(R.id.claimInfoEndDateButton);
		String buttonText = endDateButton.getText().toString();
		
		assertEquals("End date should be shown in correct format", "May 18, 2015", buttonText);
	}
	
	public void testViewExpenseClaimStatus() throws InterruptedException {
		startWithClaim(UserRole.APPROVER);
		
		TextView statusTextView = (TextView) activity.findViewById(R.id.claimInfoStatusTextView);
		String text = statusTextView.getText().toString();
		String expected = activity.getString(R.string.enum_status_in_progress);
		
		assertEquals("Status should be shown", expected, text);
	}
	
	public void testViewExpenseClaimItemTotals() throws InterruptedException {
		startWithClaim(UserRole.APPROVER);
		
		TextView totalsTextView = (TextView) activity.findViewById(R.id.claimInfoCurrencyTotalsListTextView);
		String text = totalsTextView.getText().toString();
		
		assertTrue("JPY total should be shown", text.contains("300.00 JPY"));
		assertTrue("CAD total should be shown", text.contains("50.50 CAD"));
	}
	
	public void testViewExpenseClaimItemCount() throws InterruptedException {
		startWithClaim(UserRole.APPROVER);
		
		Button itemsButton = (Button) activity.findViewById(R.id.claimInfoViewItemsButton);
		String text = itemsButton.getText().toString();
		
		String format = activity.getString(R.string.claim_info_view_items);
		String expected = String.format(format, 3);
		
		assertEquals("Expense count should be shown on button", expected, text);
	}
	
	public void testSetExpenseClaimStartDate() throws InterruptedException {
		startWithClaim(UserRole.CLAIMANT);
		
		instrumentation.runOnMainSync(new Runnable() {
			@Override
			public void run() {
				Button startDateButton = (Button) activity.findViewById(R.id.claimInfoStartDateButton);
				startDateButton.performClick();
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
				picker.updateDate(2013, 00, 24);
				accept.performClick();
			}
		});
		
		instrumentation.waitForIdleSync();
		
		Button startDateButton = (Button) activity.findViewById(R.id.claimInfoStartDateButton);
		String buttonText = startDateButton.getText().toString();
		
		assertEquals("Start date should update", "Jan 24, 2013", buttonText);
	}
	
	public void testSetExpenseClaimEndDate() throws InterruptedException {
		startWithClaim(UserRole.CLAIMANT);
		
		instrumentation.runOnMainSync(new Runnable() {
			@Override
			public void run() {
				Button endDateButton = (Button) activity.findViewById(R.id.claimInfoEndDateButton);
				endDateButton.performClick();
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
				picker.updateDate(2017, 05, 13);
				accept.performClick();
			}
		});
		instrumentation.waitForIdleSync();
		
		Button endDateButton = (Button) activity.findViewById(R.id.claimInfoEndDateButton);
		String buttonText = endDateButton.getText().toString();
		
		assertEquals("End date should update", "Jun 13, 2017", buttonText);
	}
	
	public void testSetExpenseClaimStartDateInvalid() throws InterruptedException {
		startWithClaim(UserRole.CLAIMANT);
		
		Button startDateButton = (Button) activity.findViewById(R.id.claimInfoStartDateButton);
		String oldText = startDateButton.getText().toString();
		
		instrumentation.runOnMainSync(new Runnable() {
			@Override
			public void run() {
				Button startDateButton = (Button) activity.findViewById(R.id.claimInfoStartDateButton);
				startDateButton.performClick();
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
				picker.updateDate(2016, 07, 12);
				accept.performClick();
			}
		});
		
		instrumentation.waitForIdleSync();
		
		String buttonText = startDateButton.getText().toString();
		
		assertEquals("Date should not update", oldText, buttonText);
	}
	
	public void testSetExpenseClaimEndDateInvalid() throws InterruptedException {
		startWithClaim(UserRole.CLAIMANT);
		
		Button endDateButton = (Button) activity.findViewById(R.id.claimInfoEndDateButton);
		String oldText = endDateButton.getText().toString();
		
		instrumentation.runOnMainSync(new Runnable() {
			@Override
			public void run() {
				Button endDateButton = (Button) activity.findViewById(R.id.claimInfoEndDateButton);
				endDateButton.performClick();
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
				picker.updateDate(2010, 8, 3);
				accept.performClick();
			}
		});
		
		instrumentation.waitForIdleSync();
		
		String buttonText = endDateButton.getText().toString();
		
		assertEquals("Date should not update", oldText, buttonText);
	}
	
	public void testViewExpenseClaimClaimant() throws InterruptedException {
		startWithClaim(UserRole.APPROVER);
		
		TextView claimantTextView = (TextView) activity.findViewById(R.id.claimInfoClaimantNameTextView);
		String text = claimantTextView.getText().toString();
		String expected = user.getUserName();
		
		assertEquals("Claimant name should be shown", expected, text);
	}
	
	public void testSubmitExpenseClaim() throws Throwable {
		startWithClaim(UserRole.CLAIMANT);
		
		final Button submitButton = (Button) activity.findViewById(R.id.claimInfoClaimSubmitButton);
		runTestOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				submitButton.performClick();
				try{
					AlertDialog dialog = activity.getLastAlertDialog();
					dialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick();
				} catch (NullPointerException e){
					Log.d("DEBUG", "Dialog was not shown");
				}		
			}
		});

		getInstrumentation().waitForIdleSync();
		assertEquals("Claim Status was not changed upon submit", Status.SUBMITTED, claim.getStatus());
		assertTrue(activity.isFinishing());
		
	}
	
	public void testAddCommentToExpenseItem() throws Throwable {
		startWithClaim(UserRole.APPROVER);
		
		final Button approveButton = (Button) activity.findViewById(R.id.claimInfoClaimApproveButton);
		final String approverComment = "Approved Comment";
		
		runTestOnUiThread(new Runnable() {
			
			@Override
			public void run() {
			EditText comment = (EditText) activity.findViewById(R.id.claimInfoCommentEditText);
			comment.setText(approverComment);
			approveButton.performClick();
			activity.getLastAlertDialog().getButton(AlertDialog.BUTTON_POSITIVE).performClick();
			}
		});
		getInstrumentation().waitForIdleSync();
		assertEquals(Status.APPROVED, claim.getStatus());
		for (ApproverComment comment : claim.getComments()){
			if (comment.getComment().equals(approverComment)){
				return;
			}
		}
		fail("Did not find the approver comment");
		
	}
	
	public void testReturnExpenseClaim() throws Throwable {
		claimActionHelper(R.id.claimInfoClaimReturnButton, Status.RETURNED);
	}
	
	public void testApproveExpenseClaim() throws Throwable {
		claimActionHelper(R.id.claimInfoClaimApproveButton, Status.APPROVED);
	}
	
	private void claimActionHelper(int buttonId, Status expectedStatus) throws Throwable{
		startWithClaim(UserRole.APPROVER);
		final Button approveButton = (Button) activity.findViewById(buttonId);
		
		runTestOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				approveButton.performClick();
				activity.getLastAlertDialog().getButton(AlertDialog.BUTTON_POSITIVE).performClick();
			}
		});
		getInstrumentation().waitForIdleSync();
		assertEquals(expectedStatus, claim.getStatus());
		assertTrue(activity.isFinishing());
		
	}
	
	public void testViewExpenseItems() throws InterruptedException {
		startWithClaim(UserRole.CLAIMANT);

		ActivityMonitor monitor = instrumentation.addMonitor(ExpenseItemsListActivity.class.getName(), null, false);
		
		final Button viewItemsButton = (Button) activity.findViewById(R.id.claimInfoViewItemsButton);
		instrumentation.runOnMainSync(new Runnable() {
			@Override
			public void run() {
				viewItemsButton.performClick();
			}
		});
		
		final Activity newActivity = monitor.waitForActivityWithTimeout(3000);
		
		assertNotNull("Expense list activity should start", newActivity);
		
		newActivity.finish();
		instrumentation.waitForIdleSync();
		
		// This is a stupid hack, but Android sometimes fails to back out in time
		Thread.sleep(100);
	}

	/**
	 * Start with a premade claim.
	 * 
	 * @param role The user's role.
	 * @throws InterruptedException 
	 */
	private void startWithClaim(UserRole role) throws InterruptedException {
		// Fill in claim data
		user.setUserName("Test user");
		claim.setStatus(Status.IN_PROGRESS);
		
		Calendar start = Calendar.getInstance();
		start.set(2015, 04, 01);
		claim.setStartDate(start.getTime());
		
		Calendar end = Calendar.getInstance();
		end.set(2015, 04, 18);
		claim.setEndDate(end.getTime());
		
		Item item = addItemToClaim();
		item.setCurrency(ItemCurrency.CAD);
		item.setAmount(30.5f);
		item.setDate(new Date());
		
		item = addItemToClaim();
		item.setCurrency(ItemCurrency.CAD);
		item.setAmount(20.f);
		item.setDate(new Date());
		
		item = addItemToClaim();
		item.setCurrency(ItemCurrency.JPY);
		item.setAmount(300.f);
		item.setDate(new Date());
		
		// Create the intent
		Intent intent = new Intent();
		intent.putExtra(TravelTrackerActivity.USER_DATA,
						new UserData(user.getUUID(), user.getUserName(), role));
		intent.putExtra(TravelTrackerActivity.CLAIM_UUID, claim.getUUID());
		
		// Start the activity
		setActivityIntent(intent);
		activity = getActivity();
		activity.waitUntilLoaded();
	}
	
	/**
	 * Add an empty item to the claim.
	 * 
	 * @return The item.
	 * @throws InterruptedException 
	 */
	private Item addItemToClaim() throws InterruptedException {
		SynchronizedResultCallback<Item> itemCallback = new SynchronizedResultCallback<Item>();
		dataSource.addItem(claim, itemCallback);
		itemCallback.waitForResult();
		Item item = itemCallback.getResult();
		
		return item;
	}
}
