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

package cmput301w15t07.TravelTracker.model;

import java.util.UUID;

import cmput301w15t07.TravelTracker.serverinterface.Constants.Type;

/**
 * Model object for Users.
 * 
 * @author kdbanman,
 *         therabidsquirel
 *
 */
public class User extends Document {
	private String userName;
	private Geolocation homeLocation;

	/**
	 * Package protected constructor, intended for use only by DataSource.
	 * 
	 * @param docID UUID document identifier
	 */
	User(UUID docID) {
		super(docID);
		setType(Type.USER);
		
		userName = "";
		homeLocation = null; // As home location can be unset, there's no better default than null unfortunately.
	}
	
	/**
	 * Private no-args constructor for GSON.
	 */
	@SuppressWarnings("unused")
	private User() {
		this(UUID.randomUUID());
	}

	/**
	 * Get the user's name.
	 * @return The user's name.
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * Set the user's name.
	 * @param userName The user's name.
	 */
	public void setUserName(String userName) {
		this.userName = userName;
		this.<User>hasChanged(this);
	}
	
	/**
	 * Get the user's home location.
	 * @return The geolocation of the user's home location.
	 */
	public Geolocation getHomeLocation() {
		return homeLocation;
	}

	/**
	 * Set the user's home location.
	 * @param homeLocation The new home location.
	 */
	public void setHomeLocation(Geolocation homeLocation) {
		this.homeLocation = homeLocation;
		this.<User>hasChanged(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((homeLocation == null) ? 0 : homeLocation.hashCode());
		result = prime * result
				+ ((userName == null) ? 0 : userName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof User))
			return false;
		User other = (User) obj;
		if (homeLocation == null) {
			if (other.homeLocation != null)
				return false;
		} else if (!homeLocation.equals(other.homeLocation))
			return false;
		if (userName == null) {
			if (other.userName != null)
				return false;
		} else if (!userName.equals(other.userName))
			return false;
		return true;
	}
}
