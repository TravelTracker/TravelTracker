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

package cmput301w15t07.TravelTracker.testutils;

import cmput301w15t07.TravelTracker.serverinterface.ResultCallback;

/**
 * Allows result callbacks to return data through a synchronized block.
 * If an error occurs, this fails an assert.
 * 
 * Referenced
 * http://etutorials.org/Programming/Java+extreme+programming/Chapter+4.+JUnit/4.15+Testing+Asynchronous+Methods/
 * on 04/03/15
 * 
 * @author colp
 *
 * @param <T> The result type.
 */
public class SynchronizedResultCallback<T> implements ResultCallback<T> {
	/** The returned result */
	private T result = null;
	
	/** Whether a result has already been returned */
	private boolean hasResult = false;
	
	/** Whether an error occurred */
	private String error = null;
	
	/** How long to wait before giving up */
	static private final int timeOut = 1000;
	
	@Override
    public synchronized void onResult(T result) {
		this.result = result;
		hasResult = true;
		notifyAll();
    }

	@Override
    public synchronized void onError(String message) {
	    error = "SynchronizedResultCallback failed";
	    notifyAll();
    }
	
	/**
	 * Waits until the result is returned.
	 * 
	 * @return Whether a result was received
	 * @throws InterruptedException 
	 */
	public synchronized boolean waitForResult() throws InterruptedException {
		if (!hasResult && error == null) {
			wait(timeOut);
		}
		
		return hasResult;
	}

	/**
	 * Get the result which was returned. This should be called after waitForResult.
	 * @return The result.
	 */
	public T getResult() {
		assert hasResult : "getResult() must be called after waitForResult()";
		
		return result;
	}

	/**
	 * Check whether this has a result.
	 * @return true if there is a result.
	 */
	public boolean getHasResult() {
		return hasResult;
	}

	/**
	 * Get the error which was returned. This should be called after waitForResult.
	 * @return The error string.
	 */
	public String getError() {
		assert hasResult : "getResult() must be called after waitForResult()";
		
		return error;
	}
	
	
}
