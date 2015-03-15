package cmput301w15t07.TravelTracker.util;

import java.util.ArrayList;

import cmput301w15t07.TravelTracker.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
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
         * @param 
         */
        void onDestinationEditorFragmentResult(ArrayList<String> result);
        
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
    
    /** Whether the dialog was cancelled. */
    boolean cancelled;
    
    // Indices of destination fields in the ArrayList<String>.
    final static int LOCATION_INDEX = 0;
    final static int REASON_INDEX = 1;

    private final static int VIEW_ID = R.layout.claim_info_destinations_list_edit_prompt;
    private final static int LOCATION_EDIT_ID = R.id.claimInfoDestinationsListEditPromptLocationEditText;
    private final static int REASON_EDIT_ID = R.id.claimInfoDestinationsListEditPromptReasonEditText;
    
    public DestinationEditorFragment(String location, String reason, ResultCallback callback) {
        this.callback = callback;
        this.location = location;
        this.reason = reason;
        cancelled = false;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();
        
        DestinationEditorDialog dialog = new DestinationEditorDialog(context);
        dialog.setTitle(context.getString(R.string.claim_info_destination_edit_title));
        
        dialog.setButton(Dialog.BUTTON_POSITIVE, context.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cancelled = false;
            }
        });
        
        dialog.setButton(Dialog.BUTTON_NEGATIVE, context.getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cancelled = true;
            }
        });
        
        return dialog;
    }
    
    /**
     * 
     */
    class DestinationEditorDialog extends AlertDialog {
        private View promptView;
        private EditText locationEditText;
        private EditText reasonEditText;
        
        protected DestinationEditorDialog(Context context) {
            super(context);
        }
        
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            
            LayoutInflater inflater = LayoutInflater.from(getContext());
            promptView = inflater.inflate(VIEW_ID, null, false);
            
            locationEditText = (EditText) promptView.findViewById(LOCATION_EDIT_ID);
            locationEditText.setText(location);
            
            reasonEditText = (EditText) promptView.findViewById(REASON_EDIT_ID);
            reasonEditText.setText(reason);
            
            setView(promptView);
        }

        @Override
        public void dismiss() {
            if (cancelled) {
                callback.onDestinationEditorFragmentCancelled();
            } else {
                ArrayList<String> result = new ArrayList<String>();
                result.set(LOCATION_INDEX, locationEditText.getText().toString());
                result.set(REASON_INDEX, reasonEditText.getText().toString());
                callback.onDestinationEditorFragmentResult(result);
            }
        }
    }
}
