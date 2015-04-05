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

package cmput301w15t07.TravelTracker.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;

import cmput301w15t07.TravelTracker.model.Item;

/**
 * Contains helper functions for working with Claims.
 * 
 * @author ryant26
 */
public class ClaimUtilities {
	
	/**
	 * This method returns a date string formatted in MMM d, yyyy
	 * @param date
	 * @return date string
	 */
	public static String formatDate(Date date){
		SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy", Locale.CANADA);
		return sdf.format(date);
	}
	
	/**
	 * Gets all the totals from a claim and returns a list of strings in 
	 * the format: (total) (currency) 
	 * @param items The collection of Items.
	 * @return The string of totals.
	 */
	public static ArrayList<String> getTotals(Collection<Item> items){
		Hashtable<String, Float> totals = new Hashtable<String, Float>();
		ArrayList<String> out = new ArrayList<String>();
		
		for (Item e : items){
			String key = e.getCurrency().toString();
			Float newAmt = e.getAmount();
			if (totals.containsKey(key)){
				Float amt = totals.get(key);
				amt += newAmt;
				totals.remove(key);
				totals.put(key, amt);
			} else {
				totals.put(key, newAmt);
			}
		}
		
		for (String key : totals.keySet()){
			Float amt = totals.get(key);
			out.add(String.format("%.2f", amt) + " " + key);
		}
		return out;
	}
}
