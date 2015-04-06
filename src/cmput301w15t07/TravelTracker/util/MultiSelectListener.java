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

import java.util.ArrayList;

import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView.MultiChoiceModeListener;

public class MultiSelectListener implements MultiChoiceModeListener{

    private multiSelectMenuListener listener;
    ArrayList<Integer> selectedItems = new ArrayList<Integer>();
    int menu;
    
    
    public MultiSelectListener(multiSelectMenuListener listener, int menu) {
        this.menu = menu;
        this.listener = listener;
    }
    
    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(this.menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        listener.menuButtonClicked(selectedItems, item);
        mode.finish();
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        selectedItems = new ArrayList<Integer>();
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position,
            long id, boolean checked) {
        if (checked){
            selectedItems.add(position);
        }else {
            selectedItems.remove(selectedItems.indexOf(position));
        }
    }
    
    public interface multiSelectMenuListener {
        
        public void menuButtonClicked(ArrayList<Integer> selectedItems, MenuItem item);
    }
    
}
