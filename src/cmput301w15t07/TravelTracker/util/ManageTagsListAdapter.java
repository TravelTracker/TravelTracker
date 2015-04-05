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
import java.util.Comparator;
import java.util.Locale;
import java.util.UUID;

import cmput301w15t07.TravelTracker.model.Tag;
import cmput301w15t07.TravelTracker.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ManageTagsListAdapter extends ArrayAdapter<Tag> {
    private static final int LAYOUT_ID = android.R.layout.simple_list_item_1;
    private Collection<Tag> tags;

    public ManageTagsListAdapter(Context context) {
        super(context, LAYOUT_ID);
    }
    
    /**
     * Rebuilds the underlying list of tags the adapter uses to display
     * data.
     * @param tags The list of all items
     * @param userID The claim to filter the list of Items by.
     */
    public void rebuildList(Collection<Tag> tags, UUID userID) {
        this.tags = tags;
        
        // Clear the list
        clear();
        
        // Add items to the adapter
        for(Tag tag : this.tags) {
            if (tag.getUser().equals(userID)) {
                add(tag);
            }
        }
        
        sort(new Comparator<Tag>() {
            @Override
            public int compare(Tag lhs, Tag rhs) {
                /* Use Canada as the static Locale. This likely needs
                 * to be changed if this app left Canada 
                 */
                return lhs.getTitle().toLowerCase(Locale.CANADA)
                        .compareTo(rhs.getTitle().toLowerCase(Locale.CANADA));
            }
        });
        
        // Notify we changed
        notifyDataSetChanged();
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView;
        if (convertView != null) {
            rowView = convertView;
        }
        else {
            LayoutInflater inflator =(LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflator.inflate(LAYOUT_ID, parent, false);
        }
        
        Tag tagData = getItem(position);
        
        TextView view = (TextView) rowView.findViewById(android.R.id.text1);
        view.setText(tagData.getTitle());
        
        /* Sets the the row selected background to be the dark grey the other
         * list views use. Necessary in code rather than in the XML since
         * we're using a layout from android (simple_list_item_1) where we
         * can't affect the layout XML
         */
        rowView.setBackgroundResource(R.layout.background_activated);
        
        return rowView;
    }
}
