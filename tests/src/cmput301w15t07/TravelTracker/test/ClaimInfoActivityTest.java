package cmput301w15t07.TravelTracker.test;

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
import cmput301w15t07.TravelTracker.activity.ClaimInfoActivity;
import cmput301w15t07.TravelTracker.activity.LoginActivity;
import cmput301w15t07.TravelTracker.model.DataSource;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ActivityUnitTestCase;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

/**
 * Test for individual claim management activities.
 * 
 * Each relevant Use Case UC.XxxYyy is tested with method testXxxYyy()
 * 
 * @author kdbanman,
 * 		   colp
 *
 */
public class ClaimInfoActivityTest extends ActivityUnitTestCase<ClaimInfoActivity> {
	DataSource dataSource;
	Instrumentation instrumentation;
	Activity activity;

	public ClaimInfoActivityTest() {
		super(ClaimInfoActivity.class);
	}
	
	@Override
	protected void setUp() throws Exception {
	    /*super.setUp();
		
		MockTravelTrackerApp app = new MockTravelTrackerApp();
		setApplication(app);
		app.onCreate();
		
		dataSource = app.getDataSource();
	    
	    instrumentation = getInstrumentation();
	    
	    Intent intent = new Intent(instrumentation.getTargetContext(), ClaimInfoActivity.class);
	    startActivity(intent, null, null);
	    
	    activity = getActivity();*/
	}
	
	public void testCreateExpenseClaim() {
		
	}
	
	public void testEditExpenseClaim() {
		
	}
	
	public void testDeleteExpenseClaim() {
		
	}
	
	public void testViewExpenseClaimApprover() {
		
	}
	
	public void testViewExpenseClaimClaimant() {
		
	}
	
	public void testSubmitExpenseClaim() {
		
	}
	
	public void testAddCommentToExpenseItem() {
		
	}
	
	public void testReturnExpenseClaim() {
		
	}
	
	public void testApproveExpenseClaim() {
		
	}
	
	public void testListExpenseItems() {
		
	}

}
