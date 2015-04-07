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

package cmput301w15t07.TravelTracker;

import android.content.Context;
import cmput301w15t07.TravelTracker.model.CacheDataSource;
import cmput301w15t07.TravelTracker.model.DataSource;
import cmput301w15t07.TravelTracker.model.GeneratedDataSource;

/**
 * A singleton containing the applications's data source.
 * 
 * @author kdbanman
 *
 */
public class DataSourceSingleton {
    static private DataSource ds;
	
	/**
	 * Get the application's data source.
	 * 
	 * If no source has been set, a new data source is created.
	 * 
	 * @param appContext The application context for reporting error messages.
	 * @return The single instance of DataSource for the application.
	 */
    static public DataSource getDataSource(Context appContext) {
    	// As per
    	//   http://goo.gl/clVKsG
    	// we can rely on the application context never changing, so appContext is only
    	// used once (during initialization).
    	if (ds == null) {
    		//ds = new InMemoryDataSource();
    		ds = new GeneratedDataSource();
    		//ds = new CacheDataSource(appContext);
    	}
    	
		return ds;
	}
    
	/**
	 * Set the application's data source.
	 * 
	 * This should only be used for dependency injection testing.
	 * 
	 * @param source The new source to use.
	 */
    static public void setDataSource(DataSource source) {
		ds = source;
	}
}
