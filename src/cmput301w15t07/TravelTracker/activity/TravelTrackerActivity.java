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

import android.app.Activity;

/**
 * The base activity for TravelTracker activities.
 * 
 * Referenced http://stackoverflow.com/a/16636992 on 10/03/15
 * 
 * @author Elliot
 *
 */
public class TravelTrackerActivity extends Activity {
	private CountDownLatch loadedLatch = new CountDownLatch(1);
	
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
}
