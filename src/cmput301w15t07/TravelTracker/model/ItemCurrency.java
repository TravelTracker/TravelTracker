package cmput301w15t07.TravelTracker.model;

import java.util.Currency;

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
public enum ItemCurrency {
    CAD("CAD"),
    USD("USD"),
    EUR("EUR"),
    GBP("GBP"),
    CHF("CHF"),
    JPY("JPY"),
    CNY("CNY");
    
    private String asString;
    
    private ItemCurrency (String asString) {
        this.asString = asString;
    }
    
    @Override
    public String toString() {
        return asString;
    }
    
    public Currency getCurrency() {
        return Currency.getInstance(asString);
    }
    
    /**
     * This method returns the ItemCurrency instance corresponding to the passed string.
     * @param text
     * @return ItemCurrency
     */
    public static ItemCurrency fromString(String text) {
        if (text != null) {
          for (ItemCurrency i : ItemCurrency.values()) {
            if (text.equalsIgnoreCase(i.asString)) {
              return i;
            }
          }
        }
        return null;
      }

}
