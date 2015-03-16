package cmput301w15t07.TravelTracker.model;

import android.content.Context;
import cmput301w15t07.TravelTracker.R;

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
 * Model enum for Expense Item Category.
 * 
 * @author kdbanman,
 *         therabidsquirel
 *
 */
public enum ItemCategory implements ContextStringable {
	ACCOMMODATION(R.string.enum_item_category_accommodation),
	AIR_FARE(R.string.enum_item_category_air_fare),
	FUEL(R.string.enum_item_category_fuel),
	GROUND_TRANSPORT(R.string.enum_item_category_ground_transport),
	MEAL(R.string.enum_item_category_meal),
	MISC(R.string.enum_item_category_miscellaneous),
	PARKING(R.string.enum_item_category_parking),
	PRIVATE_AUTOMOBILE(R.string.enum_item_category_private_automobile),
	REGISTRATION(R.string.enum_item_category_registration),
	SUPPLIES(R.string.enum_item_category_supplies),
	VEHICLE_RENTAL(R.string.enum_item_category_vehicle_rental);
	
	private final int id;
	
	private ItemCategory(int id) {
		this.id = id;
	}
	
	public String getString(Context context) {
	    return context.getString(id);
	}
	
	public int getId(){
		return this.id;
	}
	
	/**
	 * This method returns the ItemCategory instance corresponding to the passed string.
	 * @param text, context
	 * @return ItemCategory
	 */
	public static ItemCategory fromString(String text, Context context) {
	    if (text != null) {
	    	for (ItemCategory i : ItemCategory.values()) {
	    		if (text.equalsIgnoreCase(i.getString(context))) {
    				return i;
	    		}
	    	}
	    }
	    return null;
	}
	
}
