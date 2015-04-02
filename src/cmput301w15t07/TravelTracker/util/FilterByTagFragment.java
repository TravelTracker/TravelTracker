package cmput301w15t07.TravelTracker.util;

import java.util.Collection;

import cmput301w15t07.TravelTracker.R;
import cmput301w15t07.TravelTracker.model.Tag;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
public class FilterByTagFragment extends DialogFragment {
	private TagCheckboxAdapter listAdapter;
	private Collection<Tag> tags;
	private Collection<Tag> selected;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Create alert dialog
	    Builder builder = new Builder(getActivity())
    	.setPositiveButton(android.R.string.ok, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		})
		.setNegativeButton(android.R.string.cancel, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
		
	    // Inflate inner layout
	    LayoutInflater inflater = getActivity().getLayoutInflater();
	    ListView listView = (ListView) inflater.inflate(R.layout.filter_by_tag_fragment, null);
	    builder.setView(listView);
	    
	    // Configure list view
	    listAdapter = new TagCheckboxAdapter(getActivity());
	    listView.setAdapter(listAdapter);
		
		listAdapter.clear();
		listAdapter.addAll(tags);
		
	    return builder.create();
	}
	
	/**
	 * Set the available tags.
	 * @param tags The list of the user's tags.
	 */
	public void setTags(Collection<Tag> tags) {
		this.tags = tags;
	}
}
