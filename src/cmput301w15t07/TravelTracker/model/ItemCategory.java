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
 * Model enum for expense Item Category.  Implements Stringable for easy Android
 * Spinner generation.
 * 
 * @author kdbanman
 * @author Braedy Kuzma
 *
 */
public enum ItemCategory implements Stringable<ItemCategory> {
	ACCOMODATION("Accomodation"),
	AIR_FARE("Air Fare"),
	FUEL("Fuel"),
	GROUND_TRANSPORT("Ground Transport"),
	MEAL("Meal"),
	MISC("Miscellaneous"),
	PARKING("Parking"),
	PRIVATE_AUTOMOBILE("Private Automobile"),
	REGISTRATION("Registration"),
	SUPPLIES("Supplies"),
	VEHICLE_RENTAL("Vehicle Rental");
	
	/**
	 * The string representation of the category.
	 */
	private String asString;
	
	ItemCategory(String asString) {
		this.asString = asString;
	}
	
	@Override
	public String toString() {
		return asString;
	}
}
