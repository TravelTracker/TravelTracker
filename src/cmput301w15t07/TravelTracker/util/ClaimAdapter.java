package cmput301w15t07.TravelTracker.util;

import java.util.*;

import android.R.integer;
import android.content.Context;
import android.content.res.ColorStateList;
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
import cmput301w15t07.TravelTracker.model.Item;
import cmput301w15t07.TravelTracker.model.Status;
import cmput301w15t07.TravelTracker.model.UserRole;
import cmput301w15t07.TravelTracker.R;

public class ClaimAdapter extends ArrayAdapter<Claim>{
	
	private Collection<Claim> claims;
	private Collection<Item> items;
	
	public ClaimAdapter(Context context) {
		super(context, R.layout.claims_list_row_item);
	}
	
	/**
	 * This function rebuilds the list with the passed claims
	 * @param claims
	 * @param items
	 */
	public void rebuildList(Collection<Claim> claims, Collection<Item> items, UserRole role){
		this.claims = claims;
		this.items = items;
		final UserRole r = role;
		//possible performance bottleneck
		clear();
		addAll(claims);
		sort(new Comparator<Claim>() {

			@Override
			public int compare(Claim lhs, Claim rhs) {
				int compare = lhs.getStartDate().compareTo(rhs.getStartDate());
				if (r.equals(UserRole.CLAIMANT)){
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
		
		name.setText(claim.getName());
		date.setText(ClaimUtilities.formatDate(claim.getStartDate()));
		setStatus(status, claim.getStatus());
		
		destinationContainer.removeAllViews();
		totalsContainer.removeAllViews();
		
		addTotals(claim, totalsContainer);
		addDestinations(claim, destinationContainer);
		//TODO add tags
		
		return workingView;
	}
	
	
	private void setStatus(TextView display, Status status){
		display.setText(status.toString());
		if (status.equals(Status.APPROVED)){
			display.setTextColor(Color.GREEN);
		} else if (status.equals(Status.RETURNED)){
			display.setTextColor(Color.RED);
		} else {
			display.setTextColor(Color.BLACK);
		}
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
