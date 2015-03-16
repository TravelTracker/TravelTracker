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

import java.util.concurrent.CountDownLatch;

import cmput301w15t07.TravelTracker.DataSourceSingleton;
import cmput301w15t07.TravelTracker.model.DataSource;
import cmput301w15t07.TravelTracker.model.Status;
import cmput301w15t07.TravelTracker.model.UserRole;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

/**
 * The base activity for TravelTracker activities.
 * 
 * Referenced http://stackoverflow.com/a/16636992 on 10/03/15
 * 
 * @author Elliot,
 *         therabidsquirel
 *
 */
@SuppressLint("Registered") // This is not an activity itself, it doesn't need to be registered
public class TravelTrackerActivity extends Activity {
    /** String used to retrieve user data from intent */
    public static final String USER_DATA = "cmput301w15t07.TravelTracker.userData";
    
    /** String used to retrieve claim UUID from intent */
    public static final String CLAIM_UUID = "cmput301w15t07.TravelTracker.claimUUID";
    
    /** String used to retrieve item UUID from intent */
    public static final String ITEM_UUID = "cmput301w15t07.TravelTracker.itemUUID";
    
    /** String used to retrieve Boolean from intent for whether item is created from ClaimInfo or not */
    public static final String FROM_CLAIM_INFO = "cmput301w15t07.TravelTracker.fromClaimInfo";
    
    /** Latch which is counted down when the activity loads its data */
	private CountDownLatch loadedLatch = new CountDownLatch(1);
	
	/** The data source (from DataSourceSingleton) */
	protected DataSource datasource;
	
	/**
	 * Called when the activity finishes loading.
	 */
	public void onLoaded() {
		loadedLatch.countDown();
	}
	
	/**
	 * Blocks the thread until the activity is loaded.
	 * @throws InterruptedException
	 */
	public void waitUntilLoaded() throws InterruptedException {
		loadedLatch.await();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
        datasource = DataSourceSingleton.getDataSource();
	}
	
    public void signOut() {
        // adapted from 
        //    http://stackoverflow.com/questions/6298275/how-to-finish-every-activity-on-the-stack-except-the-first-in-android
        // on 10 March 2015
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Removes other Activities from stack
        startActivity(intent);
    }

    public void appendNameToTitle(String name) {
        setTitle(getTitle() + " - " + name);
    }
    
    /**
     * Check if the user is a claimant and the claim status is either in_progress or returned 
     * @return True if the check passes, else False
     */
    public static boolean isEditable(Status status, UserRole role) {
        return  role.equals(UserRole.CLAIMANT) &&
                (status.equals(Status.IN_PROGRESS) ||
                 status.equals(Status.RETURNED));
    }
    
    public void disableButton(Button button) {
        button.setClickable(false);
        button.setAlpha(0.4f);
    }
}
