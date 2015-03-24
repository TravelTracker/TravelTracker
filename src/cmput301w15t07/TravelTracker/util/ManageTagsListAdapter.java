package cmput301w15t07.TravelTracker.util;

import java.util.Collection;
import java.util.Comparator;
import java.util.UUID;

import cmput301w15t07.TravelTracker.model.Tag;
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
                return rhs.getTitle().compareTo(lhs.getTitle());
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
        
        return rowView;
    }
}
