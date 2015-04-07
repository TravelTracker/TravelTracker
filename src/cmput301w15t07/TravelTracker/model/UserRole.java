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

import cmput301w15t07.TravelTracker.R;
import android.content.Context;

/**
 * Model enum for User Role.
 * 
 * @author colp,
 *         therabidsquirel
 *
 */
public enum UserRole implements ContextStringable {
    CLAIMANT(R.string.enum_user_role_claimant),
    APPROVER(R.string.enum_user_role_approver);
    
    private final int id;
    
    private UserRole(int id) {
        this.id = id;
    }
    
    public String getString(Context context) {
        return context.getString(id);
    }
    
    /**
     * This method returns the UserRole instance corresponding to the passed string.
     * @param text The text to search for.
     * @param context The Android context in which this is operating.
     * @return The matching UserRole.
     */
    public static UserRole fromString(String text, Context context) {
        if (text != null) {
            for (UserRole i : UserRole.values()) {
                if (text.equalsIgnoreCase(i.getString(context))) {
                    return i;
                }
            }
        }
        return null;
    }
}
