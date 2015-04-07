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

import com.google.android.gms.maps.model.LatLng;

import cmput301w15t07.TravelTracker.R;
import cmput301w15t07.TravelTracker.model.Geolocation;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

/**
 * A fragment for editing a destination.
 * 
 * @author therabidsquirel
 *
 */
public class DestinationEditorFragment extends DialogFragment {
    private boolean editable;
    
    /**
     * Callback interface for results from DestinationEditorFragment.
     */
    public interface ResultCallback {
        /**
         * Called when the destination's changes are confirmed.
         * 
         * @param location The location of the destination.
         * @param reason The reason for going to the destination.
         */
        void onDestinationEditorFragmentResult(String location, Geolocation geolocation, String reason);
        
        /**
         * Called when the dialog is dismissed.
         * 
         * @param cancelled True if the dialog was closed in any manner but the positive button.
         */
        void onDestinationEditorFragmentDismissed(boolean cancelled);
    }

    /** The result listener. */
    private ResultCallback callback;
    
    /** The location of the destination. */
    String location;
    
    /** The geolocation of the destination. */
    Geolocation geolocation;
    
    /** The reason of the destination. */
    String reason;
    
    /** The dialog that will let the user edit the destination. */
    AlertDialog dialog;
    
    /** The listener for approving edits to the destination. */
    AcceptEditListener acceptEditListener;
    
    /** The fragment to edit and view the geolocation of the destination. */
    SelectLocationFragment geolocationFragment = null;
    
    /** False if the dialog was closed via the positive button. */
    private boolean cancelled = true;
    
    private final static int VIEW_ID = R.layout.claim_info_destinations_list_edit_prompt;
    private final static int LOCATION_EDIT_ID = R.id.claimInfoDestinationsListEditPromptLocationEditText;
    private final static int GEOLOCATION_BUTTON_ID = R.id.claimInfoDestinationsListEditPromptGeolocationButton;
    private final static int GEOLOCATION_CHECKBOX_ID = R.id.claimInfoDestinationsListEditPromptGeolocationCheckBox;
    private final static int REASON_EDIT_ID = R.id.claimInfoDestinationsListEditPromptReasonEditText;
    
    /**
     * Constructor
     * @param callback Callback for fragment results
     * @param location Location name
     * @param geolocation Physical geolocation (or null if not set)
     * @param reason Reason for travel to destination
     * @param manager The fragment manager
     * @param editable
     */
    public DestinationEditorFragment(ResultCallback callback, String location,
            Geolocation geolocation, String reason, boolean editable) {
        this.callback = callback;
        this.location = location;
        this.geolocation = geolocation;
        this.reason = reason;
        this.editable = editable;
    }

    /**
     * Returns a dialog with the custom view for editing a destination, populated with
     * the fields from the destination.
     * 
     * @param savedInstanceState
     * @return The custom dialog the user can interact with to edit a destination.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Context context = getActivity();
        
        LayoutInflater inflater = LayoutInflater.from(context);
        final View promptView = inflater.inflate(VIEW_ID, null, false);
        
        final EditText locationEditText = (EditText) promptView.findViewById(LOCATION_EDIT_ID);
        locationEditText.setText(location);
        locationEditText.setEnabled(editable);
        
        final Button geolocationButton = (Button) promptView.findViewById(GEOLOCATION_BUTTON_ID);
        updateGeolocationCheckBox(promptView, geolocation);
        
        final SelectLocationFragment.ResultCallback geoCallback = new SelectLocationFragment.ResultCallback() {
            @Override
            public void onSelectLocationResult(LatLng location) {
                geolocation = new Geolocation(location.latitude, location.longitude);
                updateGeolocationCheckBox(promptView, geolocation);
            }
            
            @Override
            public void onSelectLocationCancelled() {}
        };
        
        geolocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String location = locationEditText.getText().toString();
                String title = "\"" + location + "\"\n" + context.getString(R.string.claim_info_destination_edit_fragment_title);
                
                if (geolocation == null)
                    geolocationFragment = new SelectLocationFragment(geoCallback, null, title, editable);
                else
                    geolocationFragment = new SelectLocationFragment(geoCallback, geolocation.getLatLng(), title, editable);
                
                geolocationFragment.show(getFragmentManager(), "selectLocation");
            }
        });
        
        final EditText reasonEditText = (EditText) promptView.findViewById(REASON_EDIT_ID);
        reasonEditText.setText(reason);
        reasonEditText.setEnabled(editable);
        
        // Taken on February 1, 2015 from:
        // http://stackoverflow.com/questions/6070805/prevent-enter-key-on-edittext-but-still-show-the-text-as-multi-line
        // Disables the enter key for this EditText.
        reasonEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return (keyCode == KeyEvent.KEYCODE_ENTER);
            }
        });
        
        AlertDialog.Builder builder =  new AlertDialog.Builder(context)
            .setView(promptView)
            .setTitle(context.getString(R.string.claim_info_destination_edit_title))
            .setPositiveButton(context.getString(android.R.string.ok), null);
        
        if (editable) {
            builder.setNegativeButton(context.getString(android.R.string.cancel), null);
        }
        
        dialog = builder.create();
        
        acceptEditListener = new AcceptEditListener(context, dialog, locationEditText, reasonEditText);
        return dialog;
    }
    
    @Override
    public void onDismiss(DialogInterface dialog) {
        callback.onDestinationEditorFragmentDismissed(cancelled);
        cancelled = true;
        super.onDismiss(dialog);
    }
    
    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
        
        // This line taken on April 5, 2015 from:
        //     http://stackoverflow.com/a/20066590
        // The dialog and the acceptEditListener were not created, as onCreateDialog had not been called yet,
        // so this was throwing a null pointer exception before adding this line.
        manager.executePendingTransactions();
        
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(acceptEditListener);
    }
    
    private void updateGeolocationCheckBox(View view, Geolocation geolocation) {
        CheckBox checkBox = (CheckBox) view.findViewById(GEOLOCATION_CHECKBOX_ID);
        checkBox.setChecked(geolocation != null);
    }

    /**
     * Listener for the positive (OK) button in the destination editor fragment. If errors
     * in editing are present, will notify the user without closing the fragment.
     */
    public class AcceptEditListener implements android.view.View.OnClickListener {
        private Context context;
        private Dialog dialog;
        private EditText locationEditText;
        private EditText reasonEditText;
        
        public AcceptEditListener(Context context, Dialog dialog, EditText locationEditText, EditText reasonEditText) {
            this.context = context;
            this.dialog = dialog;
            this.locationEditText = locationEditText;
            this.reasonEditText = reasonEditText;
        }
        
        @Override
        public void onClick(View v) {
            if (!editable) {
                cancelled = true;
                dialog.dismiss();
            }
            
            location = locationEditText.getText().toString();
            
            // Empty location is invalid, don't close dialog.
            if (location.isEmpty()) {
                locationEditText.setError(context.getString(R.string.error_no_name));
                return;
            }
            
            // No geolocation is invalid, don't close dialog.
            if (geolocation == null) {
                String error = context.getString(R.string.claim_info_destination_error_geolocation);
                Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                return;
            }
            
            reason = reasonEditText.getText().toString();
            callback.onDestinationEditorFragmentResult(location, geolocation, reason);
            cancelled = false;
            dialog.dismiss();
        }
    }
}
