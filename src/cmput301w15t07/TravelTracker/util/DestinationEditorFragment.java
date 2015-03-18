package cmput301w15t07.TravelTracker.util;

import cmput301w15t07.TravelTracker.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

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
 * A fragment for editing a destination.
 * 
 * @author therabidsquirel
 *
 */
public class DestinationEditorFragment extends DialogFragment {
    /**
     * Callback interface for results from DestinationEditorFragment.
     */
    public interface ResultCallback {
        /**
         * Called when the destination's changes are confirmed.
         * 
         * @param result The result (a location and a reason).
         */
        void onDestinationEditorFragmentResult(String location, String reason);
        
        /**
         * Called when the dialog is cancelled.
         */
        void onDestinationEditorFragmentCancelled();
    }

    /** The result listener. */
    private ResultCallback callback;
    
    /** The location of the destination. */
    String location;
    
    /** The reason of the destination. */
    String reason;
    
    private final static int VIEW_ID = R.layout.claim_info_destinations_list_edit_prompt;
    private final static int LOCATION_EDIT_ID = R.id.claimInfoDestinationsListEditPromptLocationEditText;
    private final static int REASON_EDIT_ID = R.id.claimInfoDestinationsListEditPromptReasonEditText;
    
    public DestinationEditorFragment(String location, String reason, ResultCallback callback) {
        this.callback = callback;
        this.location = location;
        this.reason = reason;
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
        Context context = getActivity();
        
        LayoutInflater inflater = LayoutInflater.from(context);
        final View promptView = inflater.inflate(VIEW_ID, null, false);
        
        final EditText locationEditText = (EditText) promptView.findViewById(LOCATION_EDIT_ID);
        locationEditText.setText(location);
        
        final EditText reasonEditText = (EditText) promptView.findViewById(REASON_EDIT_ID);
        reasonEditText.setText(reason);
        
        // Taken on February 1, 2015 from:
        // http://stackoverflow.com/questions/6070805/prevent-enter-key-on-edittext-but-still-show-the-text-as-multi-line
        // Disables the enter key for this EditText.
        reasonEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return (keyCode == KeyEvent.KEYCODE_ENTER);
            }
        });
        
        return new AlertDialog.Builder(context)
            .setView(promptView)
            .setTitle(context.getString(R.string.claim_info_destination_edit_title))
            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    location = locationEditText.getText().toString();
                    reason = reasonEditText.getText().toString();
                    callback.onDestinationEditorFragmentResult(location, reason);
                    dialog.dismiss();
                }
            })
            .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    callback.onDestinationEditorFragmentCancelled();
                    dialog.dismiss();
                }
            })
            .create();
    }
}
