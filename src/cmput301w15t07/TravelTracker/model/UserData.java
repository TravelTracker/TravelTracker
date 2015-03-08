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
import java.util.UUID;

/**
 * A class which holds data about the current user. Serializable to be easily
 * passed between Activities.
 * 
 * This class is an implementation detail and is not included in UML.
 * 
 * @author colp
 *
 */
public class UserData implements Serializable {
	/**
	 * Serializable ID.
	 */
    private static final long serialVersionUID = 3694498368307870501L;

    private UUID uuid;
    private String name;
    private UserRole role;
    
    public UserData(UUID uuid, String name, UserRole role) {
	    this.uuid = uuid;
	    this.name = name;
	    this.role = role;
    }
	
	/**
	 * Get the user's UUID.
	 * @return The user's UUID.
	 */
	public UUID getUUID() {
		return uuid;
	}
    
    /**
     * Get the user's name.
     * @return The user's name.
     */
	public String getName() {
		return name;
	}
	
	/**
	 * Get the user's role.
	 * @return The user's role.
	 */
	public UserRole getRole() {
		return role;
	}
}
