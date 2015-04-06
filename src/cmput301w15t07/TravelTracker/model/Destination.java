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

/**
 * Model object for Claim Destinations.
 * 
 * Setters are not exposed - object attributes are set at construction time.
 * This is so that mutations must be made at the Claim for observer notification
 * and cache dirtying.
 * 
 * @author kdbanman,
 *         therabidsquirel
 *
 */
public class Destination {
    private String location;
    private Geolocation geolocation;
    private String reason;
    
    public Destination(String location, Geolocation geolocation, String reason) {
        this.location = location;
        this.geolocation = geolocation;
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
     * Get the geolocation of the destination.
     * @return The geolocation.
     */
    public Geolocation getGeolocation() {
        return geolocation;
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
        result = prime * result
                + ((geolocation == null) ? 0 : geolocation.hashCode());
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
        if (geolocation == null) {
            if (other.geolocation != null)
                return false;
        } else if (!geolocation.equals(other.geolocation))
            return false;
        if (reason == null) {
            if (other.reason != null)
                return false;
        } else if (!reason.equals(other.reason))
            return false;
        return true;
    }
}
