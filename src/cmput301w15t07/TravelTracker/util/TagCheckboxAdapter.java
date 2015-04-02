package cmput301w15t07.TravelTracker.util;

import cmput301w15t07.TravelTracker.R;
import cmput301w15t07.TravelTracker.model.Tag;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

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
 * Custom adapter used to display a checkbox next to tags for the
 * FilterByTagFragment fragment.
 * 
 * @author colp
 */

public class TagCheckboxAdapter extends ArrayAdapter<Tag> {
	public TagCheckboxAdapter(Context context) {
	    super(context, R.layout.filter_by_tag_fragment_item, R.id.filter_by_fragment_item_checkbox);
    }
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	    View view = super.getView(position, convertView, parent);
	    
	    Tag item = getItem(position);
	    
	    CheckBox checkBox = (CheckBox) view.findViewById(R.id.filter_by_fragment_item_checkbox);
	    checkBox.setText(item.getTitle());
	    
	    return view;
	}
}
