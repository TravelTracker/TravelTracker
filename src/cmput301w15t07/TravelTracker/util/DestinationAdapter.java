package cmput301w15t07.TravelTracker.util;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cmput301w15t07.TravelTracker.R;
import cmput301w15t07.TravelTracker.model.Claim;
import cmput301w15t07.TravelTracker.model.Destination;
import cmput301w15t07.TravelTracker.model.Status;
import cmput301w15t07.TravelTracker.model.UserData;
import cmput301w15t07.TravelTracker.model.UserRole;

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
    private LinearLayout linearLayout;
    
    /** The currently open destination editor fragment. */
    private DestinationEditorFragment destinationEditor = null;
    
    /** The current view being used in the destination editor fragment. */
    private View editingView = null;
    
    /** Whether the current view being used in the destination editor fragment is new or not. */
    private boolean newDestination = false;
    
    private final static int LIST_VIEW_ID = R.layout.claim_info_destinations_list_item;
    private final static int LIST_LOCATION_ID = R.id.claimInfoDestinationsListItemLocationTextView;
    private final static int LIST_REASON_ID = R.id.claimInfoDestinationsListItemReasonTextView;
    
    public DestinationAdapter (Claim claim, ArrayList<Destination> destinations) {
        this.claim = claim;
        this.destinations = destinations;
    }
    
    /**
     * Get the currently displayed destination editor fragment.
     * @return The DestinationEditorFragment, or null if there isn't one.
     */
    public DestinationEditorFragment getDestinationEditorFragment() {
        return destinationEditor;
    }

    public void setDestinations(ArrayList<Destination> destinations) {
        this.destinations = destinations;
    }
    
    public void displayView(Context context, UserData userData, LinearLayout linearLayout, FragmentManager manager) {
        this.context = context;
        this.linearLayout = linearLayout;
        
        linearLayout.removeAllViews();
        
        for (Destination destination : destinations) {
            View view = createView(destination, userData, manager);
            linearLayout.addView(view);
        }
    }
    
    private View createView(Destination destination, UserData userData, final FragmentManager manager) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(LIST_VIEW_ID, linearLayout, false);
        setDestination(view, destination);
        
        if (userData.getRole().equals(UserRole.CLAIMANT) && (claim.getStatus().equals(Status.IN_PROGRESS) ||
                                                             claim.getStatus().equals(Status.RETURNED))) {
            view.setOnClickListener(new View.OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    newDestination = false;
                    promptEditDestination(v, manager);
                }
            });
            
            view.setOnLongClickListener(new View.OnLongClickListener() {
                
                @Override
                public boolean onLongClick(View v) {
                    promptDeleteDestination(v);
                    return false;
                }
            });
            
        }
        
        updateReasonVisibilty(view, destination.getReason());
        
        return view;
    }
    
    public void addDestination(Context context, UserData userData, LinearLayout linearLayout, FragmentManager manager) {
        this.context = context;
        this.linearLayout = linearLayout;
        
        Destination destination = new Destination("", "");
        destinations.add(destination);
        View view = createView(destination, userData, manager);
        
        newDestination = true;
        promptEditDestination(view, manager);
    }
    
    /**
     * Prompt for editing a destination.
     */
    private void promptEditDestination(View view, FragmentManager manager) {
        Destination destination = (Destination) view.getTag();
        editingView = view;
        destinationEditor = new DestinationEditorFragment(destination.getLocation(), destination.getReason(), new DestinationCallback());
        destinationEditor.show(manager, "destinationEditor");
    }
    
    private void editDestination(View view, String location, String reason) {
        Destination destination = (Destination) view.getTag();
        destinations.remove(destination);
        
        destination = new Destination(location, reason);
        destinations.add(destination);
        setDestination(view, destination);
        
        updateReasonVisibilty(view, destination.getReason());
        
        claim.setDestinations(destinations);
    }
    
    /**
     * Prompt for deleting a destination.
     */
    private void promptDeleteDestination(final View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.claim_info_delete_destination)
               .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteDestination(view);
                    }
               })
               .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                    }
               });
        builder.create().show();
    }
    
    private void deleteDestination(View view) {
        Destination destination = (Destination) view.getTag();
        destinations.remove(destination);
        
        claim.setDestinations(destinations);
        linearLayout.removeView(view);
    }
    
    private void setDestination(View view, Destination destination) {
        view.setTag(destination);
        setText(view, LIST_LOCATION_ID, destination.getLocation());
        setText(view, LIST_REASON_ID, destination.getReason());
    }
    
    private void setText(View view, int id, String text) {
        TextView t = (TextView) view.findViewById(id);
        t.setText(text);
    }
    
    private void updateReasonVisibilty(View view, String reason) {
        TextView reasonTextView = (TextView) view.findViewById(LIST_REASON_ID);
        
        if (reason.isEmpty()) {
            reasonTextView.setVisibility(View.GONE);
        } else {
            reasonTextView.setVisibility(View.VISIBLE);
        }
    }
    
    private void setEditorDefaults() {
        destinationEditor = null;
        editingView = null;
        newDestination = false;
    }
    
    /**
     * Callback for when new destination fields are approved.
     */
    class DestinationCallback implements DestinationEditorFragment.ResultCallback {
        @Override
        public void onDestinationEditorFragmentResult(ArrayList<String> result) {
            String location = result.get(DestinationEditorFragment.LOCATION_INDEX);
            String reason = result.get(DestinationEditorFragment.REASON_INDEX);
            
            // Invalid location
            if (location.isEmpty()) {
                String error = context.getString(R.string.claim_info_destination_error);
                Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                
            // Valid location and reason, so update the destination
            } else {
                editDestination(editingView, location, reason);
                
                if (newDestination) {
                    linearLayout.addView(editingView);
                }
            }
            
            setEditorDefaults();
        }

        @Override
        public void onDestinationEditorFragmentCancelled() {
            if (newDestination) {
                Destination destination = (Destination) editingView.getTag();
                destinations.remove(destination);
            }
            
            setEditorDefaults();
        }
    }
}
