package cmput301w15t07.TravelTracker.util;

import java.util.*;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import cmput301w15t07.TravelTracker.model.Claim;
import cmput301w15t07.TravelTracker.model.Destination;
import cmput301w15t07.TravelTracker.model.Item;
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
	 */
	public void rebuildList(Collection<Claim> claims, Collection<Item> items){
		this.claims = claims;
		this.items = items;
		//possible performance bottleneck
		clear();
		addAll(claims);
		sort(new Comparator<Claim>() {

			@Override
			public int compare(Claim lhs, Claim rhs) {
				return lhs.getStartDate().compareTo(rhs.getStartDate());
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
		LinearLayout destinationContainer = (LinearLayout) workingView.findViewById(R.id.claimsListDestinationContainer);
		LinearLayout totalsContainer = (LinearLayout) workingView.findViewById(R.id.claimsListTotalContainer);
		Claim claim = getItem(position);
		
		name.setText(claim.getName());
		date.setText(ClaimUtilities.formatDate(claim.getStartDate()));
		
		destinationContainer.removeAllViews();
		totalsContainer.removeAllViews();
		
		addTotals(claim, totalsContainer);
		addDestinations(claim, destinationContainer);
		
		return workingView;
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
		dynamicTotal.setText(total);
		parent.addView(dynamicTotal);	
	}
	
	private void addDestination(Destination dest, ViewGroup parent){
		TextView dynamicDestination = new TextView(getContext());
		dynamicDestination.setText(dest.getLocation());
		parent.addView(dynamicDestination);
	}
	
	
}
