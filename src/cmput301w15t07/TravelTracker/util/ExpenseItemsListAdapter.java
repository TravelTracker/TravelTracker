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
import java.util.UUID;

import cmput301w15t07.TravelTracker.R;
import cmput301w15t07.TravelTracker.model.Item;
import cmput301w15t07.TravelTracker.model.ItemCategory;
import cmput301w15t07.TravelTracker.model.ItemCurrency;
import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Adapter for filling fields of an expense_items_list_item_view with the
 * values of an Item. Extends an ArrayAdapter to manage Items from some
 * List.
 * 
 * @author braedy,
 *         therabidsquirel
 *
 */
public class ExpenseItemsListAdapter extends ArrayAdapter<Item> {
    private static final int LAYOUT_ID = R.layout.expense_items_list_item_view;
    
    private Collection<Item> items;
    
    public ExpenseItemsListAdapter(Context context) {
        super(context, LAYOUT_ID);
    }
    
    /**
     * Rebuilds the underlying list of items the adapter uses to display
     * data.
     * @param items The list of all items
     * @param claimID The claim to filter the list of Items by.
     */
    public void rebuildList(Collection<Item> items, UUID claimID) {
        this.items = items;
        
        // Clear the list
        clear();
        
        // Add items to the adapter
        for(Item item : this.items) {
            if (item.getClaim().equals(claimID)) {
                add(item);
            }
        }
        
        sort(new Comparator<Item>() {
            @Override
            public int compare(Item lhs, Item rhs) {
                return rhs.getDate().compareTo(lhs.getDate());
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
        
        Item itemData = getItem(position);
        
        // Incomplete view
        setIncompleteView(itemData, rowView);
        
        // Description view
        setDescriptionField(itemData, rowView);
        
        // Date view
        setDateView(itemData, rowView);
        
        // Category view
        setCategoryField(itemData, rowView);
        
        // Cost View
        setCostView(itemData, rowView);
        
        // Receipt view
        setReceiptView(itemData, rowView);
        
        return rowView;
    }

	private void setReceiptView(Item itemData, View rowView) {
		ImageView receiptView =
                (ImageView) rowView.findViewById(R.id.expenseItemsListItemViewReceiptImageView);
        if (itemData.getReceipt() != null && itemData.getReceipt().getPhoto() != null) {
            receiptView.setImageBitmap(itemData.getReceipt().getPhoto());
            receiptView.setVisibility(View.VISIBLE);
        }
        else {
            /* Clear image for image view.
             * http://stackoverflow.com/questions/2859212/how-to-clear-an-imageview-in-android */
            receiptView.setImageResource(android.R.color.black);
            receiptView.setVisibility(View.GONE);
        }
	}

	/**
	 * Sets up the cost view with given item data.
	 * @param itemData The data to use.
	 * @param rowView The parent view containing the cost view.
	 */
	private void setCostView(Item itemData, View rowView) {
		TextView costView =
        		(TextView) rowView.findViewById(
        				R.id.expenseItemsListItemViewCostTextView);
        ItemCurrency curr = itemData.getCurrency();
        String costString = String.valueOf(itemData.getAmount()) + " " + curr.getString(getContext());
        costView.setText(costString);
	}
	
	/**
	 * Sets up the date view with given item data.
	 * @param itemData The data to use.
	 * @param rowView The parent view containing the date view.
	 */
	private void setDateView(Item itemData, View rowView) {
		TextView dateView =
                (TextView) rowView.findViewById(
                		R.id.expenseItemsListItemViewDateTextView);
        java.text.DateFormat dateFormat = DateFormat.getMediumDateFormat(getContext());
        String dateString = dateFormat.format(itemData.getDate());
        dateView.setText(dateString);
	}
	
	/**
	 * Sets the incomplete view to show r not given item data.
	 * @param itemData The data to use.
	 * @param rowView The parent view containing the incomplete view.
	 */
	private void setIncompleteView(Item itemData, View rowView) {
		TextView incompleteView =
                (TextView) rowView.findViewById(
                		R.id.expenseItemsListItemViewStatusTextView);
        if (itemData.isComplete()) {
            incompleteView.setVisibility(View.GONE);
        }
        else {
            incompleteView.setVisibility(View.VISIBLE);
        }
	}
	
	/**
	 * Sets up the description view with given item data.
	 * @param itemData The data to use.
	 * @param rowView The parent view containing the description view.
	 */
	private void setDescriptionField(Item itemData, View rowView) {
		TextView descView =
                (TextView) rowView.findViewById(
                		R.id.expenseItemsListItemViewDescriptionTextView);
        descView.setText(itemData.getDescription());
	}
	
	/**
	 * Sets up the category view with given item data.
	 * @param itemData The data to use.
	 * @param rowView The parent view containing the category view.
	 */
    private void setCategoryField(Item itemData, View rowView) {
        TextView categoryView = 
        		(TextView) rowView.findViewById(
        				R.id.expenseItemsListItemViewCategoryTextView);
        ItemCategory category = itemData.getCategory();
        String categoryString = "Category: " + category.getString(getContext());
        categoryView.setText(categoryString);
    }
}
