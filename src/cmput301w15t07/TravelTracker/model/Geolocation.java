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

import java.io.Serializable;

import com.google.android.gms.maps.model.LatLng;

/**
 * A serializable alternative to Android's LatLng class. Immutable so that
 * the containing Document will always be alerted to changes.
 * 
 * @author colp,
 *         therabidsquirel
 */
public class Geolocation implements Serializable {
    private static final long serialVersionUID = -690533662069510219L;
    
    /** In degrees, range of [-90, 90]. */
    private double latitude;
    
    /** In degrees, range of [-180, 180). */
    private double longitude;
    
    /**
     * Construct a Geolocation.
     * @param latitude The latitude in degrees, range of [-90, 90].
     * @param longitude The longitude in degrees, range of [-180, 180).
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
    
    /**
     * Taken on April 4, 2015 from:
     * (http://stackoverflow.com/a/123305)
     * 
     * Get the distance in kilometers between this geolocation and the provided
     * geolocation. Uses the Haversine formula.
     * 
     * @param other The geolocation to calculate the distance to.
     * @return The distance between this and other in kilometers.
     */
    public double distanceBetween(Geolocation other) {
        double earthRadius = 6371.0; // Earth's radius in kilometers.
        
        double lat1 = Math.toRadians(latitude);
        double lng1 = Math.toRadians(longitude);
        double lat2 = Math.toRadians(other.latitude);
        double lng2 = Math.toRadians(other.longitude);
        
        double sinLatDist = Math.sin((lat2 - lat1) / 2);
        double sinLngDist = Math.sin((lng2 - lng1) / 2);
        
        double a = Math.pow(sinLatDist, 2) + Math.pow(sinLngDist, 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return c * earthRadius;
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
