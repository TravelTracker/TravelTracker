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


import cmput301w15t07.TravelTracker.activity.LoginActivity;
import android.app.Activity;
import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;

/**
 * Test for entry activity - Logging in.
 * 
 * Each relevant Use Case UC.XxxYyy is tested with method testXxxYyy()
 * 
 * @author kdbanman
 *
 */
public class LoginActivityTest extends ActivityInstrumentationTestCase2<LoginActivity> {

	Instrumentation instrumentation;
	Activity login;
	
	public LoginActivityTest() {
		super(LoginActivity.class);
	}
	
	public void testLoginExistingUser() {
		// should transition to claims list
		// should be claimant role
	}
	
	public void testLoginNewUser() {
		// getting existing claims must return none
		
		// should transition to empty claims list
		// should be claimant role
	}

}
