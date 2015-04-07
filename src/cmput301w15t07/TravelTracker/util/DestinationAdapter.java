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

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import cmput301w15t07.TravelTracker.R;
import cmput301w15t07.TravelTracker.model.Claim;
import cmput301w15t07.TravelTracker.model.Destination;
import cmput301w15t07.TravelTracker.model.Geolocation;

/**
 * Custom adapter for dealing with displaying and editing destinations dynamically
 * in a LinearLayout.
 * 
 * @author therabidsquirel
 * 
 */
public class DestinationAdapter {
    private Context context;
    private Claim claim;
    private ArrayList<Destination> destinations;
    private FragmentManager manager;
    private LinearLayout linearLayout;
    
    /** Whether list elements should be editable. */
    private boolean editable;
    
    private final static int LIST_VIEW_ID = R.layout.claim_info_destinations_list_item;
    private final static int LIST_LOCATION_ID = R.id.claimInfoDestinationsListItemLocationTextView;
    private final static int LIST_REASON_ID = R.id.claimInfoDestinationsListItemReasonTextView;
    
    private final static int COLOR_SELECTED = android.R.color.holo_blue_light;
    private final static int COLOR_PLAIN = android.R.color.transparent;
    
    public DestinationAdapter (Claim claim, ArrayList<Destination> destinations, FragmentManager manager) {
        this.claim = claim;
        this.destinations = destinations;
        this.manager = manager;
    }
    
    /**
     * Set the destinations list on the adapter
     * @param destinations The new ArrayList.
     */
    public void setDestinations(ArrayList<Destination> destinations) {
        this.destinations = destinations;
    }
    
    /**
     * Given a LinearLayout, recreate all of the views within it from the list of
     * destinations in this adapter.
     * @param context The context of the activity containing the LinearLayout.
     * @param linearLayout The LinearLayout the adapter will add views to.
     */
    public void createList(Context context, LinearLayout linearLayout) {
        this.context = context;
        this.linearLayout = linearLayout;
        
        linearLayout.removeAllViews();
        
        for (Destination destination : destinations) {
            View view = createView(destination);
            linearLayout.addView(view);
        }
    }
    
    /**
     * Given a destination, create a view populated with its information for display in
     * a custom LinearLayout. Also add click and long-click listeners to the view for
     * editing and deleting respectively.
     * @param destination The destination to create a view for.
     * @return The newly created view.
     */
    private View createView(Destination destination) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(LIST_VIEW_ID, linearLayout, false);
        setDestinationOnView(view, destination);
        
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setBackgroundColor(context.getResources().getColor(COLOR_SELECTED));
                promptEditDestination(v, getDestinationFromView(v));
            }
        });
        
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                v.setBackgroundColor(context.getResources().getColor(COLOR_SELECTED));
                promptDeleteDestination(v);
                return false;
            }
        });
        
        return view;
    }
    
    /**
     * Starts an editing dialog with a new destination.
     * @param context The Context the dialog will run in.
     * @param linearLayout The LinearLayout the new destination's view will be added to.
     */
    public void addDestination(Context context, LinearLayout linearLayout) {
        this.context = context;
        this.linearLayout = linearLayout;
        
        Destination destination = new Destination("", null, "");
        promptEditDestination(null, destination);
    }
    
    /**
     * Open the fragment for editing a destination.
     * @param view The view the destination belongs to. Pass null if a new destination.
     * @param destination The destination to be edited.
     */
    private void promptEditDestination(View view, Destination destination) {
        // We know if a new destination is being added the claim is editable.
        editable = (view == null) ? true : editable;
        
        DestinationEditorFragment editor = new DestinationEditorFragment(new DestinationCallback(), view, destination, editable);
        editor.show(manager, "destinationEditor");
    }
    
    /**
     * Given a view an edited destination belongs to, or null if the destination is new and
     * does not belong to a view yet, create a new destination from the new attributes.
     * Update the existing view or create and add a new one, and update the list of destinations
     * in the claim.
     * @param view The view of the destination to be edited or added. Will be null if a new destination.
     * @param location The new location of the destination.
     * @param geolocation The new geolocation of the destination.
     * @param reason The new reason of the destination.
     */
    private void editDestination(View view, String location, Geolocation geolocation, String reason) {
        Destination destination = new Destination(location, geolocation, reason);
        
        // A null view means a new destination.
        if (view == null) {
            view = createView(destination);
            linearLayout.addView(view);
            destinations.add(destination);
        } else {
            int index = destinations.indexOf(getDestinationFromView(view));
            setDestinationOnView(view, destination);
            destinations.set(index, destination);
        }
        
        claim.setDestinations(destinations);
    }
    
    /**
     * Open the fragment for deleting a destination.
     * @param view The view the destination belongs to.
     */
    private void promptDeleteDestination(View view) {
        DestinationDeletionFragment destinationDeleter = new DestinationDeletionFragment(view, getDestinationFromView(view));
        destinationDeleter.show(manager, "destinationDeleter");
    }
    
    /**
     * Given a view, delete its destination from this adapter and update the claim with the
     * new destinations list. Then remove the view from the LinearLayout.
     * @param view The view of the destination to be deleted.
     */
    private void deleteDestination(View view) {
        Destination destination = getDestinationFromView(view);
        destinations.remove(destination);
        claim.setDestinations(destinations);
        linearLayout.removeView(view);
    }
    
    /**
     * Given a view, set it with all of the information from a destination. Also set the
     * destination as a tag on the view, so it can be retrieved when needed.
     * @param view The view to set.
     * @param destination The destination to use.
     */
    private void setDestinationOnView(View view, Destination destination) {
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
    private Destination getDestinationFromView(View view) {
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

    /**
     * Callback for when new destination fields are approved.
     */
    class DestinationCallback implements DestinationEditorFragment.ResultCallback {
        @Override
        public void onDestinationEditorFragmentResult(View view, String location, Geolocation geolocation, String reason) {
            editDestination(view, location, geolocation, reason);
        }

        @Override
        public void onDestinationEditorFragmentDismissed(View view) {
            if (view != null)
                view.setBackgroundColor(context.getResources().getColor(COLOR_PLAIN));
        }
    }
    
    /**
     * Custom fragment for deleting a destination.
     */
    class DestinationDeletionFragment extends DialogFragment {
        private View view;
        private String location;
        
        public DestinationDeletionFragment(View view, Destination destination) {
            this.view = view;
            this.location = destination.getLocation();
        }
        
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            String message = context.getString(R.string.claim_info_delete_destination) + "\n\n\"" + location + "\"";
            
            return new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteDestination(view);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .create();
        }
        
        @Override
        public void onDismiss(DialogInterface dialog) {
            view.setBackgroundColor(context.getResources().getColor(COLOR_PLAIN));
            super.onDismiss(dialog);
        }
    }
}
