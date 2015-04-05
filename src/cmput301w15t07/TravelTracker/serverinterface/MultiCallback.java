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

package cmput301w15t07.TravelTracker.serverinterface;

import android.util.SparseArray;

/**
 * Allows several ResultCallbacks to return their data through a single
 * callback.
 * 
 * @author colp
 */
public class MultiCallback {
	/** SparseArray of results. */
	private SparseArray<Object> results;
	
	/** The number of remaining callbacks this is waiting for. */
	private int remainingCallbacks;
	
	/** Whether all callbacks have been created by the user. */
	private boolean ready = false;
	
	/** The callback to call when everything else has returned. */
	private ResultCallback<SparseArray<Object>> finalCallback;
	
	/**
	 * Construct a MultiCallback.
	 * 
	 * If all data is succesfully received, onResult will be called on the passed
	 * ResultCallback with a SparseArray of data. If any callback fails, onError
	 * will be called on the passed ResultCallback.
	 * 
	 * @param finalCallback The ResultCallback to return the SparseArray of data to.
	 */
	public MultiCallback(ResultCallback<SparseArray<Object>> finalCallback) {
	    this.finalCallback = finalCallback;
	    remainingCallbacks = 0;
	    results = new SparseArray<Object>();
    }
	
	/**
	 * Creates a ResultCallback. When the data is returned to the final
	 * callback, the key for the callback's result will be the ID passed to
	 * this function.  
	 * 
	 * @param id The ID to use.
	 * @return The callback.
	 */
	public <T> ResultCallback<T> createCallback(final int id) {
		++remainingCallbacks;
		
		if (results.indexOfKey(id) >= 0) {
			throw new RuntimeException("Already added a callback with this ID");
		}
		
		if (ready) {
			throw new RuntimeException("Can't add callback after ready() has been called");
		}
		
		ResultCallback<T> callback = new ResultCallback<T>() {
			@Override
            public void onResult(T result) {
				// One callback complete; store the result
	            --remainingCallbacks;
	            results.put(id, result);
	            
	            // Try calling back to final callback if ready
	            if (ready) {
	            	attemptCallback();
	            }
            }

			@Override
            public void onError(String message) {
	            finalCallback.onError(message);
            }
		};
		
		return callback;
	}
	
	/**
	 * Call this when all desired callbacks have been added.
	 */
	public void ready() {
		ready = true;
		
		attemptCallback();
	}
	
	/**
	 * Call back the final callback if everything has been received.
	 */
	private void attemptCallback() {
		if (remainingCallbacks == 0) {
			finalCallback.onResult(results);
		}
	}
}
