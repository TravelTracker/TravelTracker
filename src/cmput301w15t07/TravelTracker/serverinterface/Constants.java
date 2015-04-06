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

package cmput301w15t07.TravelTracker.serverinterface;

public class Constants {
    
    public static final String CONNECTION_URL = "http://cmput301.softwareprocess.es:8080/";
    public static final String INDEX = "cmput301w15t07";
    
    
    public enum Type {
        USER("User"),
        CLAIM("Claim"),
        ITEM("Item"),
        TAG("Tag");
        
        private final String name;
        
        private Type(String s){
            name = s;
        }
        
        /**
         * This method returns the Status instance corresponding to the passed string.
         * @param text The text to search for.
         * @return The matching Document Type.
         */
        public static Type fromString(String text) {
            if (text != null) {
              for (Type t : Type.values()) {
                if (text.equalsIgnoreCase(t.toString())) {
                  return t;
                }
              }
            }
            return null;
         }
        
        public String toString(){
            return name;
        }
    }
}
