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
import android.widget.Toast;
import cmput301w15t07.TravelTracker.R;
import cmput301w15t07.TravelTracker.activity.TravelTrackerActivity;
import cmput301w15t07.TravelTracker.model.Claim;
import cmput301w15t07.TravelTracker.model.Destination;
import cmput301w15t07.TravelTracker.model.UserData;

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
    
    /** Whether the current view being used in the destination editor fragment is new or not. */
    private boolean newDestination = false;
    
    /** The currently open destination deletion fragment. */
    private DestinationDeletionFragment destinationDeleter = null;
    
    /** Used to store the location for a destination that is being prompted for deletion. */
    private String deleteLocation;
    
    /** The current view being used in a destination fragment. */
    private View editingView = null;
    
    private final static int LIST_VIEW_ID = R.layout.claim_info_destinations_list_item;
    private final static int LIST_LOCATION_ID = R.id.claimInfoDestinationsListItemLocationTextView;
    private final static int LIST_REASON_ID = R.id.claimInfoDestinationsListItemReasonTextView;
    
    private final static int COLOR_SELECTED = android.R.color.darker_gray;
    private final static int COLOR_PLAIN = android.R.color.transparent;
    
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

    /**
     * Set the destinations list on the adapter
     * @param destinations The new ArrayList.
     */
    public void setDestinations(ArrayList<Destination> destinations) {
        this.destinations = destinations;
    }
    
    public void createList(Context context, UserData userData, LinearLayout linearLayout, FragmentManager manager) {
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
        
        if (TravelTrackerActivity.isEditable(claim.getStatus(), userData.getRole())) {
            view.setOnClickListener(new View.OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    v.setBackgroundColor(context.getResources().getColor(COLOR_SELECTED));
                    newDestination = false;
                    promptEditDestination(v, manager);
                }
            });
            
            view.setOnLongClickListener(new View.OnLongClickListener() {
                
                @Override
                public boolean onLongClick(View v) {
                    v.setBackgroundColor(context.getResources().getColor(COLOR_SELECTED));
                    promptDeleteDestination(v, manager);
                    return false;
                }
            });
            
        }
        
        updateReasonVisibilty(view, destination.getReason());
        
        return view;
    }
    
    /**
     * Starts a dialog with a new destination. If cancelled the destination is removed, otherwise
     * it is added to the claim.
     * 
     * @param context The Context the dialog will run in.
     * @param userData The UserData of the user invoking this.
     * @param linearLayout The LinearLayout the new Destination's view will be added to.
     * @param manager The FragmentManager of the activity calling this.
     */
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
        int index = destinations.indexOf(destination);
        destinations.remove(destination);
        
        destination = new Destination(location, reason);
        destinations.add(index, destination);
        setDestination(view, destination);
        
        updateReasonVisibilty(view, destination.getReason());
        
        claim.setDestinations(destinations);
    }
    
    /**
     * Prompt for deleting a destination.
     */
    private void promptDeleteDestination(View view, FragmentManager manager) {
        Destination destination = (Destination) view.getTag();
        deleteLocation = destination.getLocation();
        
        editingView = view;
        destinationDeleter = new DestinationDeletionFragment();
        destinationDeleter.show(manager, "destinationDeleter");
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
    
    private void setFragmentDefaults() {
        editingView.setBackgroundColor(context.getResources().getColor(COLOR_PLAIN));
        editingView = null;
        
        destinationEditor = null;
        newDestination = false;
        
        destinationDeleter = null;
    }
    
    /**
     * Callback for when new destination fields are approved.
     */
    class DestinationCallback implements DestinationEditorFragment.ResultCallback {
        @Override
        public void onDestinationEditorFragmentResult(String location, String reason) {
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
        }

        @Override
        public void onDestinationEditorFragmentDismissed(boolean cancelled) {
            if (newDestination && cancelled) {
                Destination destination = (Destination) editingView.getTag();
                destinations.remove(destination);
            }
            
            setFragmentDefaults();
        }
    }
    
    /**
     * Custom fragment for deleting a destination.
     */
    class DestinationDeletionFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            String message = context.getString(R.string.claim_info_delete_destination) + "\n\n\"" + deleteLocation + "\"";
            
            return new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteDestination(editingView);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                    }
                })
                .create();
        }
        
        @Override
        public void onDismiss(DialogInterface dialog) {
            setFragmentDefaults();
            super.onDismiss(dialog);
        }
    }
}
