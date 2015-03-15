package cmput301w15t07.TravelTracker.util;

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

import java.util.*;

import android.R.integer;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import cmput301w15t07.TravelTracker.model.Claim;
import cmput301w15t07.TravelTracker.model.Destination;
import cmput301w15t07.TravelTracker.model.Item;
import cmput301w15t07.TravelTracker.model.Status;
import cmput301w15t07.TravelTracker.model.User;
import cmput301w15t07.TravelTracker.model.UserRole;
import cmput301w15t07.TravelTracker.R;

/**
 * An adapter that displays Claims.
 * @author ryant26
 *
 */
public class ClaimAdapter extends ArrayAdapter<Claim>{
	
	private Collection<Claim> claims;
	private Collection<Item> items;
	private Collection<User> users;
	private UserRole role;
	
	public ClaimAdapter(Context context, UserRole role) {
		super(context, R.layout.claims_list_row_item);
		this.role = role;
	}
	
	/**
	 * This function rebuilds the list with the passed claims
	 * @param claims
	 * @param items
	 */
	public void rebuildList(Collection<Claim> claims, Collection<Item> items, Collection<User> users){
		this.claims = claims;
		this.items = items;
		this.users = users;

		//possible performance bottleneck
		clear();
		addAll(claims);
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
	
	/**
	 * this method filters the list by tag
	 * @param items
	 */
	public void filterListByTags(Collection<Item> items){
		//TODO filter list by tags
	}
	
	/**
	 * This method removes all applied filters
	 */
	public void removeAllFilters(){
		//TODO implement this
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
		LinearLayout destinationContainer = (LinearLayout) workingView.findViewById(R.id.claimsListDestinationContainer);
		LinearLayout totalsContainer = (LinearLayout) workingView.findViewById(R.id.claimsListTotalContainer);
		Claim claim = getItem(position);
		
		setName(name, claim);
		if (role.equals(UserRole.APPROVER)){
			date.setText(ClaimUtilities.formatDate(claim.getStartDate()));
		}
		setStatus(status, claim);
		
		destinationContainer.removeAllViews();
		totalsContainer.removeAllViews();
		
		addTotals(claim, totalsContainer);
		addDestinations(claim, destinationContainer);
		//TODO add approver for userRole = Approver
		//TODO add tags
		
		return workingView;
	}
	
	
	private void setName(TextView display, Claim claim){
		String nameStr = "";
		if (role.equals(UserRole.APPROVER)){
			nameStr += ":" + findUser(claim.getUser());
		} else {
			String sDate = ClaimUtilities.formatDate(claim.getStartDate());
			String eDate = ClaimUtilities.formatDate(claim.getEndDate());
			nameStr = sDate + " / " + eDate;
			display.setTextSize(18);
		}
		display.setText(nameStr);
	}
	
	private void setStatus(TextView display, Claim claim){
		String statusStr = claim.getStatus().toString();
		if (role.equals(UserRole.APPROVER)){
			try{
				statusStr += " :" + findUser(claim.getApprover());
			} catch (NullPointerException e){
				// No approver exists
				Log.d("DEBUG", "No approver exists for this claim");
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
	
	
}
