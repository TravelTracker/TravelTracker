package cmput301w15t07.TravelTracker.activity;

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

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import cmput301w15t07.TravelTracker.R;

/**
 * Lets the user select a location using the Google Maps API.
 * 
 * @author colp
 */
public class SelectLocationActivity extends TravelTrackerActivity {
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.select_location_menu, menu);
        
        return true;
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        setContentView(R.layout.select_location_activity);
        
        GoogleMap map = getMap();
        
        UiSettings settings = map.getUiSettings();
        settings.setZoomControlsEnabled(true);
        settings.setZoomGesturesEnabled(true);
        settings.setScrollGesturesEnabled(true);
        
        map.setOnMapClickListener(new LocationSelectedListener());
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        
        String msg = getString(R.string.tap_to_select_location);
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
        	onBackPressed();
        	break;
        	
        case R.id.select_location_sign_out:
        	signOut();
        	break;
        	
        default:
        	break;
        }
    	
        return super.onOptionsItemSelected(item);
    }
    
    /**
     * Get the map from the map fragment.
     * @return The GoogleMap in the select_location_activity_map fragment.
     */
    GoogleMap getMap() {
    	FragmentManager fm = getFragmentManager();
        MapFragment fragment = (MapFragment) fm.findFragmentById(R.id.select_location_activity_map);
        GoogleMap map = fragment.getMap();
        
        return map;
    }
    
    /**
     * Listener class for when a location is selected by the user.
     */
    class LocationSelectedListener implements OnMapClickListener {
		@Override
        public void onMapClick(LatLng location) {
			// TODO: Return data
        }
    }
}
