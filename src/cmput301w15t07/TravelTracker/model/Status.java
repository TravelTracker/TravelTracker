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

import cmput301w15t07.TravelTracker.utility.Stringable;

/**
 * Model enum for Claim Status.  Implements Stringable for easy Android
 * Spinner generation.
 * 
 * @author kdbanman
 *
 */
public enum Status implements Stringable<Status> {
	IN_PROGRESS("In Progress"),
	SUBMITTED("Submitted"),
	RETURNED("Returned"),
	APPROVED("Approved");
	
	private String asString;
	
	Status(String asString) {
		this.asString = asString;
	}
	
	@Override
	public String toString() {
		return this.asString;
	}
}