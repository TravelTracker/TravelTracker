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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import cmput301w15t07.TravelTracker.R;
import cmput301w15t07.TravelTracker.model.Destination;

/**
 * Custom adapter for dealing with displaying and editing destinations dynamically
 * in a LinearLayout.
 * 
 * @author therabidsquirel
 * 
 */
public class DestinationAdapter {
    /** Whether list elements should be editable. */
    private boolean editable;
    
    private final static int LIST_VIEW_ID = R.layout.claim_info_destinations_list_item;
    private final static int LIST_LOCATION_ID = R.id.claimInfoDestinationsListItemLocationTextView;
    private final static int LIST_REASON_ID = R.id.claimInfoDestinationsListItemReasonTextView;
    
    public final static int COLOR_SELECTED = android.R.color.holo_blue_light;
    public final static int COLOR_PLAIN = android.R.color.transparent;
    
    public DestinationAdapter(boolean editable) {
        setEditable(editable);
    }
    
    /**
     * Given a destination, create a view populated with its information for display in
     * a custom LinearLayout.
     * @param destination The destination to create a view for.
     * @param context The context of the calling activity.
     * @return The newly created view.
     */
    public View createView(Destination destination, Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(LIST_VIEW_ID, null);
        setDestinationOnView(view, destination);
        return view;
    }
    
    /**
     * Given a view, set it with all of the information from a destination. Also set the
     * destination as a tag on the view, so it can be retrieved when needed.
     * @param view The view to set.
     * @param destination The destination to use.
     */
    public void setDestinationOnView(View view, Destination destination) {
        view.setTag(destination);
        
        TextView locationTextView = (TextView) view.findViewById(LIST_LOCATION_ID);
        locationTextView.setText(destination.getLocation());
        
        TextView reasonTextView = (TextView) view.findViewById(LIST_REASON_ID);
        String reason = destination.getReason();
        if (reason.isEmpty()) {
            reasonTextView.setVisibility(View.GONE);
        } else {
            reasonTextView.setVisibility(View.VISIBLE);
            reasonTextView.setText(reason);
        }
    }
    
    /**
     * Get the destination that is stored as a tag in the view.
     * @param view The view to get the destination from.
     * @return The destination retrieved from the view.
     */
    public Destination getDestinationFromView(View view) {
        return (Destination) view.getTag();
    }
    
    /**
     * Check whether elements are editable.
     * @return Whether the elements are currently editable.
     */
    public boolean isEditable() {
        return editable;
    }

    /**
     * Set whether elements should be editable.
     * @param editable Whether elements should be editable.
     */
    public void setEditable(boolean editable) {
        this.editable = editable;
    }
}
