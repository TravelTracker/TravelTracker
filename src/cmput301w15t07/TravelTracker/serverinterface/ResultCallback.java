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

/**
 * Holds callback methods for completing an asynchronous action.
 * 
 * @author kdbanman
 */
public interface ResultCallback <T> {
    /**
     * Called when the action succeeds.
     * 
     * @param result The data returned.
     */
    public void onResult(T result);
    
    /**
     * Called when the action fails.
     * 
     * @param message The error message.
     */
    public void onError(String message);
}
