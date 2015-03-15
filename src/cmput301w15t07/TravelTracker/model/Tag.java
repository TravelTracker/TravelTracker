package cmput301w15t07.TravelTracker.model;

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

import java.util.UUID;

/**
 * Model object for Claim Tags for Claimant use.
 * 
 * @author kdbanman
 *
 */
public class Tag extends Document {
	private UUID user;
	private String title;
	
	/**
	 * Package protected constructor, intended for use only by DataSource.
	 * 
	 * @param docID UUID document identifier
	 */
	Tag(UUID docID) {
		super(docID);
	}
	
	/**
	 * Get the user to which this belongs.
	 * @return The user's UUID.
	 */
	public UUID getUser() {
		return this.user;
	}
	
	/**
	 * Set the user to which this belongs.
	 * @param user The user's UUID.
	 */
	public void setUser(UUID user) {
		this.user = user;
		this.updateObservers(this);
	}
	
	/**
	 * Get the tag's title.
	 * @return The title.
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * Set the tag's title.
	 * @param title The title.
	 */
	public void setTitle(String title) {
		this.title = title;
		this.updateObservers(this);
	}
}
