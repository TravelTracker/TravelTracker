package cmput301w15t07.TravelTracker.model;

import java.util.Currency;

import cmput301w15t07.TravelTracker.R;
import android.content.Context;

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
 * Model enum for Expense Item Currency.
 * 
 * @author therabidsquirel
 *
 */
public enum ItemCurrency implements ContextStringable {
    CAD(R.string.enum_item_currency_CAD),
    USD(R.string.enum_item_currency_USD),
    EUR(R.string.enum_item_currency_EUR),
    GBP(R.string.enum_item_currency_GBP),
    CHF(R.string.enum_item_currency_CHF),
    JPY(R.string.enum_item_currency_JPY),
    CNY(R.string.enum_item_currency_CNY);
    
    private final int id;
    
    private ItemCurrency(int id) {
        this.id = id;
    }
    
    public String getString(Context context) {
        return context.getString(id);
    }
    
    public Currency getCurrency(Context context) {
        return Currency.getInstance(getString(context));
    }
    
    /**
     * This method returns the ItemCurrency instance corresponding to the passed string.
	 * @param text The text to search for.
	 * @param context The Android context in which this is operating.
	 * @return The matching ItemCurrency.
     */
    public static ItemCurrency fromString(String text, Context context) {
        if (text != null) {
        	for (ItemCurrency i : ItemCurrency.values()) {
        		if (text.equalsIgnoreCase(i.getString(context))) {
        			return i;
        		}
        	}
        }
        return null;
    }

}
