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

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import cmput301w15t07.TravelTracker.R;
import cmput301w15t07.TravelTracker.activity.SelectLocationActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * A dialog fragment which allows the user to set a location using
 * either GPS or SelectLocationActivity.
 * 
 * @author colp
 */
public class SelectLocationFragment extends DialogFragment {
	/**
	 * Callback interface for results from SelectLocationFragment.
	 */
	public interface ResultCallback {
		/**
		 * Called when the location is picked.
		 * @param location The new location picked.
		 */
		void onSelectLocationResult(LatLng location);
		
		/**
		 * Called when the dialog is cancelled.
		 */
		void onSelectLocationCancelled();
	}
	
	/** Request code for home location select */
	private static final int SELECT_LOCATION_REQUEST = 1;
	
	/** The amount of zoom applied to the lite map. */
	private static final float zoomLevel = 5.f;
	
	private MapFragment mapFragment;
	private GoogleMap map;
	private String title;
	private LatLng location;
	private Location currentLocation;
	private ResultCallback callback;
	
	/**
	 * Construct a fragment with no title or start location.
	 * @param callback The class to call back with results.
	 */
	public SelectLocationFragment(ResultCallback callback) {
		this.callback = callback;
	}
	
	/**
	 * Construct a fragment with a title.
	 * @param callback The class to call back with results.
	 * @param location The location to start at.
	 */
	public SelectLocationFragment(ResultCallback callback, LatLng location) {
	    this(callback);
	    
    	setLocation(location);
    }
	
	/**
	 * Construct a fragment with a title and a start location.
	 * @param callback The class to call back with results.
	 * @param location The location to start at.
	 * @param title The title of the dialog.
	 */
	public SelectLocationFragment(ResultCallback callback, LatLng location, String title) {
		this(callback, location);
		
		this.title = title;
	}
	
	public View onCreateView(android.view.LayoutInflater inflater,
			android.view.ViewGroup container, Bundle savedInstanceState) {
	    
		// Determine title
	    if (title == null) {
	    	title = getString(R.string.select_location_fragment_default_title);
	    }
	    
	    getDialog().setTitle(title);
	    
	    View view = inflater.inflate(R.layout.select_location_fragment, container);
	    
	    // Set up the map
	    // Referenced
	    // http://xperiment-andro.blogspot.ca/2013/02/nested-fragments.html
	    // on 30/04/15
	    GoogleMapOptions options = new GoogleMapOptions();
	    options.liteMode(true);
	    options.zOrderOnTop(true);
	    mapFragment = MapFragment.newInstance(options);
	    
	    // Add the fragment
	    FragmentTransaction trans = getChildFragmentManager().beginTransaction();
	    trans.add(R.id.select_location_fragment_map, mapFragment).commit();
	    
	    // Attach listeners to buttons
	    Button okButton = (Button) view.findViewById(R.id.select_location_fragment_ok_button);
	    okButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
				
				if (callback != null) {
					if (location == null) {
						callback.onSelectLocationCancelled();
					} else {
						callback.onSelectLocationResult(location);
					}
				}
			}
		});
	    
	    Button cancelButton = (Button) view.findViewById(R.id.select_location_fragment_cancel_button);
	    cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
				
				if (callback != null) {
					callback.onSelectLocationCancelled();
				}
			}
		});
	    
	    Button setToCurrentButton = (Button) view.findViewById(R.id.select_location_fragment_current_location_button);
	    setToCurrentButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Activity activity = getActivity();
		    	
		    	if (currentLocation == null) {
		    		String msg = getString(R.string.select_location_fragment_failed_to_get_location);
		    		Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
		    	} else {
		    		setLocation(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
		    	}
			}
		});
	    
	    Button setFromMapButton = (Button) view.findViewById(R.id.select_location_fragment_select_location_button);
	    setFromMapButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				launchSelectLocationActivity();
			}
		});
	    
	    // Get location updates
		LocationManager locMan = (LocationManager) getActivity().getSystemService(Activity.LOCATION_SERVICE);
		locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, new LocationListener() {
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {}
			
			@Override
			public void onProviderEnabled(String provider) {}
			
			@Override
			public void onProviderDisabled(String provider) {}
			
			@Override
			public void onLocationChanged(Location location) {
				currentLocation = location;
			}
		});
	    
	    return view;
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    
	    // Set up map if we haven't done so yet
	    if (map == null) {
		    // Remove the toolbar
	    	map = mapFragment.getMap();
		    UiSettings settings = map.getUiSettings();
		    settings.setMapToolbarEnabled(false);
		    
		    map.setOnMapClickListener(new OnMapClickListener() {
				@Override
				public void onMapClick(LatLng arg0) {
					launchSelectLocationActivity();
				}
			});
	    }
	    
	    updateMap();
	}
	
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    
	    // Get the location if it was returned
	    if (requestCode == SELECT_LOCATION_REQUEST && resultCode == Activity.RESULT_OK) {
	    	Double lat = data.getDoubleExtra(SelectLocationActivity.RESULT_LAT, Double.NaN);
	    	Double lng = data.getDoubleExtra(SelectLocationActivity.RESULT_LNG, Double.NaN);
	    	
	    	if (!lat.isNaN() && !lng.isNaN()) {
	    		LatLng location = new LatLng(lat, lng);
	    		setLocation(location);
	    	}
	    }
	}
	
	/**
	 * Set the map's location.
	 * @param location The new location to use.
	 */
	public void setLocation(LatLng location) {
		if (location == null) {
			this.location = null;
		} else {
			this.location = new LatLng(location.latitude, location.longitude);
		}
		
		updateMap();
	}
	
	/**
	 * Updates the map position if the map has been set up.
	 */
	private void updateMap() {
		if (map == null) {
			return;
		}
	    
    	View mapView = getView().findViewById(R.id.select_location_fragment_map);
	    
	    // Hide map if no location
	    if (location == null) {
	    	mapView.setVisibility(View.GONE);
	    	
	    } else {
	    	mapView.setVisibility(View.VISIBLE);
	    	
			map.clear();
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoomLevel));
			map.addMarker(new MarkerOptions().position(location));
	    }
	}
	
	/**
	 * Launch SelectLocationActivity.
	 */
	private void launchSelectLocationActivity() {
		Activity activity = getActivity();
		Intent intent = new Intent(activity, SelectLocationActivity.class);
		
		if (location != null) {
			intent.putExtra(SelectLocationActivity.START_LAT, location.latitude);
			intent.putExtra(SelectLocationActivity.START_LNG, location.longitude);
		}
    	
    	startActivityForResult(intent, SELECT_LOCATION_REQUEST);
	}
}
