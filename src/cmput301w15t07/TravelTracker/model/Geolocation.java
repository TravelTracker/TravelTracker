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

import java.io.Serializable;

import com.google.android.gms.maps.model.LatLng;

/**
 * A serializable alternative to Android's LatLng class. Immutable so that
 * the containing Document will always be alerted to changes.
 * 
 * @author colp
 */
public class Geolocation implements Serializable {
    private static final long serialVersionUID = -690533662069510219L;
    
    private double latitude;
    private double longitude;
    
    /**
     * Construct a Geolocation.
     * @param latitude The latitude.
     * @param longitude The longitude.
     */
    public Geolocation(double latitude, double longitude) {
    	this.latitude = latitude;
    	this.longitude = longitude;
    }
    
    /**
     * Get the location's latitude.
     * @return The latitude.
     */
	public double getLatitude() {
		return latitude;
	}
	
	/**
	 * Get the location's longitude.
	 * @return The longitude.
	 */
	public double getLongitude() {
		return longitude;
	}
	
	/**
	 * Get the LatLng representation of this location.
	 * @return This Geolocation as a LatLng.
	 */
	public LatLng getLatLng() {
		return new LatLng(getLatitude(), getLongitude());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(latitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(longitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Geolocation))
			return false;
		Geolocation other = (Geolocation) obj;
		if (Double.doubleToLongBits(latitude) != Double
				.doubleToLongBits(other.latitude))
			return false;
		if (Double.doubleToLongBits(longitude) != Double
				.doubleToLongBits(other.longitude))
			return false;
		return true;
	}
}
