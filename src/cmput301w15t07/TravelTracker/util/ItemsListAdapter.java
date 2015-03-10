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

import java.util.List;

import cmput301w15t07.TravelTracker.R;
import cmput301w15t07.TravelTracker.model.Item;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

/**
 * Adapter for filling fields of an expense_items_list_item_view with the
 * values of an Item. Extends an ArrayAdapter to manage Items from some
 * List.
 * @author braedy
 *
 */
public class ItemsListAdapter extends ArrayAdapter<Item> {
    private static final int LAYOUT_ID = R.layout.expense_items_list_item_view;

    public ItemsListAdapter(Context context, List<Item> objects) {
        super(context, LAYOUT_ID, objects);
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        
        return null;
    }

}
