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

/**
 * Model object for Claim Destinations.
 * 
 * Setters are not exposed - object attributes are set at construction time.
 * This is so that mutations must be made at the Claim for observer notification
 * and cache dirtying.
 * 
 * @author kdbanman
 *
 */
public class Destination {
	private String location;
	private String reason;
	
	public Destination(String location, String reason) {
		this.location = location;
		this.reason = reason;
	}
	
	/**
	 * Get the name of the destination's location.
	 * @return The location.
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * Get the reason for travel to the destination.
	 * @return The reason.
	 */
	public String getReason() {
		return reason;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((reason == null) ? 0 : reason.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Destination))
			return false;
		Destination other = (Destination) obj;
		if (location == null) {
			if (other.location != null)
				return false;
		} else if (!location.equals(other.location))
			return false;
		if (reason == null) {
			if (other.reason != null)
				return false;
		} else if (!reason.equals(other.reason))
			return false;
		return true;
	}
	
}
