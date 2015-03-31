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

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import cmput301w15t07.TravelTracker.R;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Lets the user select a location using the Google Maps API.
 * 
 * @author colp
 */
public class SelectLocationActivity extends TravelTrackerActivity {
    /** String used to retrieve start latitude from intent */
    public static final String START_LAT = "cmput301w15t07.TravelTracker.startLat";
    
    /** String used to retrieve start longitude from intent */
    public static final String START_LNG = "cmput301w15t07.TravelTracker.startLng";

    /** String used to return start latitude in intent */
    public static final String RESULT_LAT = "cmput301w15t07.TravelTracker.resultLat";
    
    /** String used to return start longitude in intent */
    public static final String RESULT_LNG = "cmput301w15t07.TravelTracker.resultLng";
    
    /** Default zoom level when a position is passed in the intent */
    private static final float zoomLevel = 5.f;
    
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
        
        // Set up map
        GoogleMap map = getMap();
        map.setOnMapClickListener(new LocationSelectedListener());
        map.setOnMapLoadedCallback(new OnMapLoadedCallback() {
			@Override
			public void onMapLoaded() {
		        String msg = getString(R.string.tap_to_select_location);
		        Toast.makeText(SelectLocationActivity.this, msg, Toast.LENGTH_LONG).show();
			}
		});
        
        UiSettings settings = map.getUiSettings();
        settings.setMapToolbarEnabled(false);
        settings.setZoomControlsEnabled(true);
        settings.setZoomGesturesEnabled(true);
        settings.setScrollGesturesEnabled(true);
        
        // Get bundle data
        Bundle bundle = getIntent().getExtras();
        
        if (bundle != null) {
	        Double startLat = bundle.getDouble(START_LAT, Double.NaN);
	        Double startLng = bundle.getDouble(START_LNG, Double.NaN);
	        
	        // Zoom in to and mark the current position
	        if (!startLat.isNaN() && !startLng.isNaN()) {
	        	LatLng position = new LatLng(startLat, startLng);
	        	
	        	map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, zoomLevel));
	        	map.addMarker(new MarkerOptions()
	        			.position(position));
	        }
        }
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
    
    public void onBackPressed() {
    	setResult(RESULT_CANCELED);
    	
    	super.onBackPressed();
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
			// Return the selected location
			Intent intent = new Intent();
			intent.putExtra(RESULT_LAT, location.latitude);
			intent.putExtra(RESULT_LNG, location.longitude);
			
			setResult(RESULT_OK, intent);
			finish();
        }
    }
}
