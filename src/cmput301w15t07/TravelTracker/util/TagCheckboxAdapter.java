package cmput301w15t07.TravelTracker.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
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
 * Custom adapter used to display a checkbox next to select tags.
 * 
 * @author colp
 */

public class TagCheckboxAdapter extends ArrayAdapter<Tag> {
	private HashSet<UUID> selected;
	
	/**
	 * Constructor
	 * @param context The Android context in which to create the adapter.
	 * @param selected The UUIDs of selected tags.
	 */
	public TagCheckboxAdapter(Context context, Collection<UUID> selected) {
	    super(context, R.layout.select_tag_fragment_item, R.id.select_tag_fragment_item_checkbox);
	    
	    this.selected = new HashSet<UUID>(selected);
    }
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	    View view = super.getView(position, convertView, parent);
	    Tag tag = getItem(position);
	    final UUID uuid = tag.getUUID();
	    
	    final CheckBox checkBox = (CheckBox) view.findViewById(R.id.select_tag_fragment_item_checkbox);
	    checkBox.setText(tag.getTitle());
	    
	    // Check box if already selected
	    if (selected.contains(uuid)) {
	    	checkBox.setChecked(true);
	    } else {
	    	checkBox.setChecked(false);
	    }
	    
	    checkBox.setOnClickListener(new OnClickListener() {
			@Override
            public void onClick(View v) {
				if (checkBox.isChecked()) {
					selected.add(uuid);
				} else {
					selected.remove(uuid);
				}
            }
		});
	    
	    return view;
	}
	
	/**
	 * Get the UUIDs of selected tags.
	 * @return The set of selected tag UUIDs.
	 */
	public HashSet<UUID> getSelected() {
		return selected;
	}
}
