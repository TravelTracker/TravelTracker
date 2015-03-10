package cmput301w15t07.TravelTracker.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;

import cmput301w15t07.TravelTracker.model.Claim;
import cmput301w15t07.TravelTracker.model.Item;

public class ClaimUtilities {
	
	/**
	 * This method returns a date string formatted in MMM d, yyyy
	 * @param date
	 * @return date string
	 */
	public static String formatDate(Date date){
		SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy");
		return sdf.format(date);
	}
	
	/**
	 * gets all the totals from a claim and returns a list of strings in 
	 * the format: (total) (currency) 
	 * @param claim
	 * @return total strings
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
