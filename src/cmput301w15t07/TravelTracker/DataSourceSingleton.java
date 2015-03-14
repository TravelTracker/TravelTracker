package cmput301w15t07.TravelTracker;

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

import cmput301w15t07.TravelTracker.model.DataSource;

/**
 * A common interface for MockApplication and the actual Application to supply
 * a singleton DataSource to activities.
 * 
 * @author kdbanman
 *
 */
public interface DataSourceSingleton {
	/**
	 * Set the application's data source.
	 * 
	 * This should only be used for dependency injection testing.
	 * 
	 * @param source The new source to use.
	 */
	public void setDataSource(DataSource source);
	
	/**
	 * Get the application's data source.
	 * @return The single instance of DataSource for the application.
	 */
	public DataSource getDataSource();
}
