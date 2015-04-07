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

import java.util.*;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import cmput301w15t07.TravelTracker.model.Claim;
import cmput301w15t07.TravelTracker.model.Destination;
import cmput301w15t07.TravelTracker.model.Geolocation;
import cmput301w15t07.TravelTracker.model.Item;
import cmput301w15t07.TravelTracker.model.Status;
import cmput301w15t07.TravelTracker.model.Tag;
import cmput301w15t07.TravelTracker.model.User;
import cmput301w15t07.TravelTracker.model.UserRole;
import cmput301w15t07.TravelTracker.R;

/**
 * An adapter that displays Claims.
 * @author ryant26,
 *         therabidsquirel
 *
 */
public class ClaimAdapter extends ArrayAdapter<Claim> {
    private Collection<Item> items;
    private Collection<User> users;
    private Collection<Tag> tags;
    private User user;
    private UserRole role;
    
    public ClaimAdapter(Context context, UserRole role) {
        super(context, R.layout.claims_list_row_item);
        this.role = role;
    }
    
    /**
     * This function rebuilds the list with the passed claims
     * 
     * @param claims The claims to display.
     * @param items The user's expense items (used to find totals).
     * @param users The list of users (to get user names).
     * @param tags The list of tags (to get tag titles).
     * @param user The user (to find home location info).
     * @param filterTagIDs The list of tag UUIDs to filter by, or null for no filter. Only claims tagged with at least one of these tags will be displayed.
     */
    public void rebuildList(Collection<Claim> claims, Collection<Item> items,
            Collection<User> users, Collection<Tag> tags, User user,
            Collection<UUID> filterTagIDs){
        this.items = items;
        this.users = users;
        this.tags = tags;
        this.user = user;

        //possible performance bottleneck
        clear();
        
        if (filterTagIDs == null) {
            addAll(claims);
        } else {
            // Add claims one at a time
            for (Claim claim : claims) {
                ArrayList<UUID> claimTags = claim.getTags();
                
                // Only add if tagged correctly
                for (UUID uuid : claimTags) {
                    if (filterTagIDs.contains(uuid)) {
                        add(claim);
                        break;
                    }
                }
            }
        }
        
        sort(new Comparator<Claim>() {

            @Override
            public int compare(Claim lhs, Claim rhs) {
                int compare = lhs.getStartDate().compareTo(rhs.getStartDate());
                if (role.equals(UserRole.CLAIMANT)){
                    compare *= -1;
                }
                return compare;
            }

        });
        notifyDataSetChanged();
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View workingView;
        if (convertView != null){
            workingView = convertView;
        } else {
            LayoutInflater inflator = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            workingView = inflator.inflate(R.layout.claims_list_row_item, parent, false);
        }
        
        // Get all fields 
        TextView name = (TextView) workingView.findViewById(R.id.claimsListRowItemName);
        TextView date = (TextView) workingView.findViewById(R.id.claimsListRowItemDate);
        TextView status = (TextView) workingView.findViewById(R.id.claimsListRowItemStatus);
        TextView distHeader = (TextView) workingView.findViewById(R.id.claimsListRowItemDistanceHeader);
        TextView distLevel = (TextView) workingView.findViewById(R.id.claimsListRowItemDistanceLevel);
        LinearLayout destinationContainer = (LinearLayout) workingView.findViewById(R.id.claimsListDestinationContainer);
        LinearLayout totalsContainer = (LinearLayout) workingView.findViewById(R.id.claimsListTotalContainer);
        LinearLayout tagsLayout = (LinearLayout) workingView.findViewById(R.id.claimsListRowItemTagsLayout);
        Claim claim = getItem(position);
        
        setName(name, claim);
        
        if (role.equals(UserRole.APPROVER)){
            date.setText(ClaimUtilities.formatDate(claim.getStartDate()));
            distHeader.setVisibility(View.GONE);
            distLevel.setVisibility(View.GONE);
            tagsLayout.setVisibility(View.GONE); // Hide tags here
        } else if (role.equals(UserRole.CLAIMANT)) {
            date.setVisibility(View.GONE);
            setDistance(distLevel, claim);
            addTags(claim, tagsLayout); // Add tags here
        }

        setStatus(status, claim);
        
        destinationContainer.removeAllViews();
        totalsContainer.removeAllViews();
        
        addTotals(claim, totalsContainer);
        addDestinations(claim, destinationContainer);
        
        return workingView;
    }

    private void setName(TextView display, Claim claim){
        String nameStr = "";
        if (role.equals(UserRole.APPROVER)){
            nameStr += findUser(claim.getUser());
        } else {
            String sDate = ClaimUtilities.formatDate(claim.getStartDate());
            String eDate = ClaimUtilities.formatDate(claim.getEndDate());
            nameStr = sDate + " / " + eDate;
            display.setTextSize(18);
        }
        display.setText(nameStr);
    }
    
    private void setDistance(TextView display, Claim claim) {
        Context ctx = getContext();
        Geolocation homeLoc = user.getHomeLocation();
        ArrayList<Destination> destinations = claim.getDestinations();
        
        // User might not have set a home location yet.
        if (homeLoc == null) {
            display.setText(ctx.getString(R.string.claims_list_row_item_distance_no_home));
            display.setTextColor(Color.BLACK);
            display.setBackgroundColor(Color.GRAY);
            return;
            
        // Claim might have no destinations.
        } else if (destinations.isEmpty()) {
            display.setText(ctx.getString(R.string.claims_list_row_item_distance_no_dest));
            display.setTextColor(Color.BLACK);
            display.setBackgroundColor(Color.GRAY);
            return;
        }
        
        // We want to compare against the first destination in the claim.
        Geolocation otherLoc = destinations.get(0).getGeolocation();
        double distance = homeLoc.distanceBetween(otherLoc); // In kilometers.
        
        double maximum = 20038.0; // Should be max possible distance on Earth, but let's cap it just in case.
        double logBase = 2.0; // Base 2 logarithmic scale.
        double logMax = Math.log(maximum) / Math.log(logBase); // log2(20038.0) = 14.2904...
        
        double distCapped = (distance <= maximum) ? distance : maximum;
        double logLevel = logMax - ( Math.log(maximum / distCapped) / Math.log(logBase) );
        float distFraction = (float) (logLevel / logMax);
        
        if (logLevel <= 8.0) // 256 kilometers in log 2 scale.
            display.setText(ctx.getString(R.string.claims_list_row_item_distance_level_1));
        else if (logLevel <= 9.7) // 831.7464... kilometers in log 2 scale.
            display.setText(ctx.getString(R.string.claims_list_row_item_distance_level_2));
        else if (logLevel <= 11.4) // 2702.3522... kilometers in log 2 scale.
            display.setText(ctx.getString(R.string.claims_list_row_item_distance_level_3));
        else if (logLevel <= 13.1) // 8779.9682... kilometers in log 2 scale.
            display.setText(ctx.getString(R.string.claims_list_row_item_distance_level_4));
        else
            display.setText(ctx.getString(R.string.claims_list_row_item_distance_level_5));
        
        // We really just want the color to vary on values a little greater than the range
        // between level_5 and level_1. If we based it on the whole range, especially for
        // a small log base like 2, about half the color range would be taken up with
        // distances that are all very small. This would limit the distinction in color
        // between larger distances. For logLevel this creates a minimum cutoff at
        // (logMax - 7.3f) while the maximum cutoff is logMax. Values of logLevel outside
        // this range take on the color value of the appropriate end of the range.
        float hue = (1 - distFraction) * (((float) logMax) / 7.3f);
        hue = (hue <= 1) ? hue : 1;
        hue *= 100;
        
        float[] hsv = {hue, 1, 1};
        display.setTextColor(Color.HSVToColor(hsv));
        display.setBackgroundColor(Color.GRAY);
    }
    
    private void setStatus(TextView display, Claim claim){
        String statusStr = claim.getStatus().getString(getContext());
        if (role.equals(UserRole.APPROVER)){
            UUID approverID = claim.getApprover();
            
            if (approverID != null) {
                statusStr += " (last returned by " + findUser(approverID) + ")";
            }
        }
        
        display.setText(statusStr);
        if (claim.getStatus().equals(Status.APPROVED)){
            display.setTextColor(Color.GREEN);
        } else if (claim.getStatus().equals(Status.RETURNED)){
            display.setTextColor(Color.RED);
        } else {
            display.setTextColor(Color.BLACK);
        }
    }
    
    private String findUser(UUID user){
        String out = "";
        for (User u : users){
            if (u.getUUID().equals(user)){
                out =  u.getUserName();
            }
        }
        return out;
    }
    
    private void addTotals(Claim claim, ViewGroup parent){
        ArrayList<Item> relevantItems = new ArrayList<Item>();
        for (Item i : items){
            if (i.getClaim().equals(claim.getUUID())){
                relevantItems.add(i);                
            }
        }
        for (String total : ClaimUtilities.getTotals(relevantItems)){
            addTotal(total, parent);
        }
        
    }
    
    private void addDestinations(Claim claim, ViewGroup parent){
        for (Destination d : claim.getDestinations()){
            addDestination(d, parent);
        }
    }
    
    private void addTotal(String total, ViewGroup parent){
        TextView dynamicTotal = new TextView(getContext());
        dynamicTotal.setGravity(Gravity.END);
        dynamicTotal.setText(total);
        parent.addView(dynamicTotal);    
    }
    
    private void addDestination(Destination dest, ViewGroup parent){
        TextView dynamicDestination = new TextView(getContext());
        dynamicDestination.setText(dest.getLocation());
        parent.addView(dynamicDestination);
    }
    
    private void addTags(Claim claim, LinearLayout tagsLayout) {
        Collection<UUID> claimTagIDs = claim.getTags();
        
        // If no tags just hide
        if (claimTagIDs.size() == 0) {
            tagsLayout.setVisibility(View.GONE);
            return;
        }

        // There is no String.join until Java 8 or with libs.
        String tagsStr = "";
        for (UUID tagid : claimTagIDs) {
            // Complexity be damned
            for (Tag tag : tags) {
                if (tag.getUUID().equals(tagid)) {
                    tagsStr += tag.getTitle() + ", ";
                    break;
                }
            }
        }
        
        // Remove trailing ", "
        if (tagsStr.endsWith(", ")) {
            tagsStr = tagsStr.substring(0, tagsStr.lastIndexOf(", "));
        }

        // Make sure the layout is visible
        tagsLayout.setVisibility(View.VISIBLE);
        
        // Set text
        TextView tagstv = (TextView) tagsLayout.findViewById(
                R.id.claimsListRowItemTagsTextView);
        tagstv.setText(tagsStr);
    }
}
