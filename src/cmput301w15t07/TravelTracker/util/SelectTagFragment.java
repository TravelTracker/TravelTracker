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
import android.widget.ListView;
import cmput301w15t07.TravelTracker.R;
import cmput301w15t07.TravelTracker.model.Tag;

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
 * A fragment for selecting tags by which to filter the claim list.
 * 
 * @author colp
 */
public class SelectTagFragment extends DialogFragment {
	private TagCheckboxAdapter listAdapter;
	private ListView listView;
	private HashSet<Tag> tags;
	private HashSet<UUID> selected;
	private ResultCallback callback;
	
	/**
	 * Callback interface for results from SelectTagFragment.
	 */
	public interface ResultCallback {
		/**
		 * Called when the tags are selected.
		 * @param selected The selected tag UUIDs.
		 */
		void onSelectTagFragmentResult(HashSet<UUID> selected);
		
		/**
		 * Called when the dialog is cancelled.
		 */
		void onSelectTagFragmentCancelled();
	}
	
	/**
	 * Construct the fragment with no tags selected.
	 * 
	 * @param tags The set of available tags.
	 * @param callback The callback for when the user finishes entering data.
	 */
	public SelectTagFragment(Collection<Tag> tags, ResultCallback callback) {
	    this.tags = new HashSet<Tag>(tags);
	    this.selected = new HashSet<UUID>();
	    this.callback = callback;
    }
	
	/**
	 * Construct the fragment
	 * 
	 * @param tags The set of available tags.
	 * @param selected The set of tag UUIDs which are currently selected.
	 * @param callback The callback for when the user finishes entering data.
	 */
	public SelectTagFragment(Collection<Tag> tags, Collection<UUID> selected, ResultCallback callback) {
		this.tags = new HashSet<Tag>(tags);
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
				
				callback.onSelectTagFragmentResult(selected);
			}
		})
		.setNegativeButton(android.R.string.cancel, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				callback.onSelectTagFragmentCancelled();
			}
		});
		
	    // Inflate inner layout
	    LayoutInflater inflater = getActivity().getLayoutInflater();
	    listView = (ListView) inflater.inflate(R.layout.select_tag_fragment, null);
	    builder.setView(listView);
	    
	    // Configure list view
	    listAdapter = new TagCheckboxAdapter(getActivity(), selected);
	    listView.setAdapter(listAdapter);
		
		listAdapter.clear();
		listAdapter.addAll(tags);
		
	    return builder.create();
	}
}
