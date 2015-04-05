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

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import cmput301w15t07.TravelTracker.R;
import cmput301w15t07.TravelTracker.model.Tag;

/**
 * A fragment for selecting tags and filter settings.
 * 
 * @author colp
 */
public class SelectTagFilterFragment extends DialogFragment {
	protected ListView listView;
    protected ResultCallback callback;
    protected TagCheckboxAdapter listAdapter;
	private HashSet<Tag> tags;
	private HashSet<UUID> selected;
	private boolean filterEnabled;
	
	/**
	 * Callback interface for results from SelectTagFragment.
	 */
	public interface ResultCallback {
		/**
		 * Called when the tags are selected.
		 * @param selected The selected tag UUIDs.
		 * @param filterEnabled Whether the filter is enabled.
		 */
		void onSelectTagFilterFragmentResult(HashSet<UUID> selected, boolean filterEnabled);
		
		/**
		 * Called when the dialog is cancelled.
		 */
		void onSelectTagFilterFragmentCancelled();
	}
	
	/**
	 * Construct the fragment with no tags selected.
	 * 
	 * @param tags The set of available tags.
	 * @param filterEnabled Whether the filter is enabled.
	 * @param callback The callback for when the user finishes entering data.
	 */
	public SelectTagFilterFragment(Collection<Tag> tags, boolean filterEnabled,
	        ResultCallback callback) {
	    this.tags = new HashSet<Tag>(tags);
        this.filterEnabled = filterEnabled;
	    this.selected = new HashSet<UUID>();
	    this.callback = callback;
    }
	
	/**
	 * Construct the fragment
	 * 
	 * @param tags The set of available tags.
     * @param filterEnabled Whether the filter is enabled.
	 * @param selected The set of tag UUIDs which are currently selected.
	 * @param callback The callback for when the user finishes entering data.
	 */
	public SelectTagFilterFragment(Collection<Tag> tags, boolean filterEnabled,
	        Collection<UUID> selected, ResultCallback callback) {
		this.tags = new HashSet<Tag>(tags);
        this.filterEnabled = filterEnabled;
		this.selected = new HashSet<UUID>(selected);
		this.callback = callback;
    }
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Create alert dialog
	    Builder builder = new Builder(getActivity())
    	.setPositiveButton(android.R.string.ok, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
		        HashSet<UUID> selected = listAdapter.getSelected();
		        
		        callback.onSelectTagFilterFragmentResult(selected, filterEnabled);
			}
		})
		.setNegativeButton(android.R.string.cancel, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				callback.onSelectTagFilterFragmentCancelled();
			}
		})
		.setTitle(getString(R.string.select_tag_filter_title));
		
	    // Inflate inner layout
	    LayoutInflater inflater = getActivity().getLayoutInflater();
	    View rootView = inflater.inflate(R.layout.select_tag_filter_fragment, null);
        builder.setView(rootView);
        
	    // Configure checkbox
	    final CheckBox cb = (CheckBox) rootView.findViewById(R.id.select_tag_filter_enable_checkbox);
	    cb.setChecked(filterEnabled);
	    
	    cb.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterEnabled = cb.isChecked();
                
                updateListEnabled();
            }
        });
	    
	    // Configure list view
        listView = (ListView) rootView.findViewById(R.id.select_tag_filter_listview);
	    listAdapter = new TagCheckboxAdapter(getActivity(), selected);
	    listAdapter.setEnabled(filterEnabled);
	    listView.setAdapter(listAdapter);
		
		listAdapter.clear();
		listAdapter.addAll(tags);
		
	    return builder.create();
	}
	
	private void updateListEnabled() {
	    listAdapter.setEnabled(filterEnabled);
	    
	    // Have to rebuild list to get checkboxes to update. Yuck.
	    listView.setAdapter(listAdapter);
	}
}
