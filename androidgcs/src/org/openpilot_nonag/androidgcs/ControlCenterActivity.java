package org.openpilot_nonag.androidgcs;

import java.util.HashMap;
import java.util.List;

import org.openpilot_nonag.uavtalk.UAVObject;
import org.openpilot_nonag.uavtalk.UAVObjectField;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.VisibleRegion;

public class ControlCenterActivity extends ObjectManagerActivity {

	private static final String TAG = ControlCenterActivity.class
			.getSimpleName();

	public static int LOGLEVEL = 1;

	public static boolean VERBOSE = LOGLEVEL > 3;
	public static boolean WARN = LOGLEVEL > 2;
	public static boolean DEBUG = LOGLEVEL > 1;
	public static boolean ERROR = LOGLEVEL > 0;

	private final FlexibleHashMap<String, Integer> modesToId = new FlexibleHashMap<String, Integer>();

	private GoogleMap mMap;
	private SupportMapFragment mapFrag;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.control_center);
//		mapFrag = ((SupportMapFragment) getSupportFragmentManager()
//				.findFragmentById(R.id.map_view));

		// something wrong *here* (only on versions with out Google Play
		// Services) need to check instead of crashing. 
		// (this should take care of it)
//		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
//		if(status == ConnectionResult.SUCCESS) {
//		    // Awesome!
//		
//
//			mMap = mapFrag.getMap();
//			mMap.setMyLocationEnabled(true);
//			mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
//	
//			LatLng currentLatLng = null;
//	
//			if (mMap.isMyLocationEnabled()) {
//				if (DEBUG)
//					Log.d(TAG, "Map MyLocation is enabled");
//	
//				currentLatLng = getCurrentLatLng();
//	
//				if (currentLatLng != null) {
//					// Move the camera instantly to current location with a zoom of
//					// 15.
//					mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
//							currentLatLng, 20));
//	
//					// Zoom in, animating the camera.
//					mMap.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);
//				}
//				else
//				{
//					if(VERBOSE)
//						Log.v(TAG,"currentLatLng is null!");
//				}
//			} else {
//				if (DEBUG)
//					Log.d(TAG, "get my location is disabled!");
//			}
//		}
//		else{
//			Toast.makeText(this, "Need to have GooglePlay Services installed to use maps.", Toast.LENGTH_SHORT).show();
//		}
		// ((RadioGroup)
		// findViewById(R.id.modeSelectRadio)).setOnCheckedChangeListener(ToggleListener);

		// modesToId.add("PositionHold",R.id.positionHoldButton);
		// modesToId.add("PositionVarioFPV",R.id.positionVarioFpvButton);
		// modesToId.add("PositionVarioLOS",R.id.positionVarioLOSButton);
		// modesToId.add("PositionVarioNSEW",R.id.positionVarioNSEWButton);
		// modesToId.add("ReturnToBase",R.id.rtbButton);
		// modesToId.add("Manual",R.id.manualButton);
		// modesToId.add("Stabilized1",R.id.stabilizeOneButton);
		// modesToId.add("Stabilized2",R.id.stabilizeTwoButton);
		// modesToId.add("Stabilized3",R.id.stabilizeThreeButton);

		// disable for now
		// modesToId.add("Land",R.id.landButton);
		// modesToId.add("Auto",R.id.autoButton);
	}

	private LatLng getCurrentLatLng() {

		LatLng currentLatLng = null;

		try {
			LocationManager locationMgr = (LocationManager) this
					.getApplicationContext().getSystemService(LOCATION_SERVICE);

			if (locationMgr != null) {

				List<String> providers = locationMgr.getProviders(true);
				for (String provider : providers) {
					locationMgr.requestLocationUpdates(provider, 1000, 0,
							new LocationListener() {

								public void onLocationChanged(Location location) {
								}

								public void onProviderDisabled(String provider) {
								}

								public void onProviderEnabled(String provider) {
								}

								public void onStatusChanged(String provider,
										int status, Bundle extras) {
								}
							});
					Location location = locationMgr
							.getLastKnownLocation(provider);
					if (location != null) {
						double latitude = location.getLatitude();
						double longitude = location.getLongitude();
						currentLatLng = new LatLng(latitude, longitude);

					}
				}
			}
		} catch (Exception e) {
			Log.e(TAG, "Exception thrown while determing current Location!");
			e.printStackTrace();
		}

		return currentLatLng;
	}

	@Override
	void onOPConnected() {
		super.onOPConnected();

		// Get the current tablet mode desired to make sure screen reflects what
		// the
		// UAV is doing when we jump out of this activity
		UAVObject obj = objMngr.getObject("FlightStatus");
		UAVObjectField field;

		// Update the active mode
		if (obj != null && (field = obj.getField("FlightStatus")) != null) {
			String mode = field.getValue().toString();
			Log.d(TAG, "Connected and mode is: " + mode);

			Integer id = modesToId.getValue(mode);
			if (id == null)
				Log.e(TAG, "Unknown mode");
			else
				onToggle(findViewById(id));
		}

	}

	// deal with flight mode buttons
	public void onToggle(View view) {
		ToggleButton v = (ToggleButton) view;
		v.setChecked(true);
		((RadioGroup) view.getParent()).check(view.getId());
	}

	// could have used single HashMap and iterator, this should be move
	// efficient
	public static class FlexibleHashMap<K extends Object, V extends Object> {
		public HashMap<K, V> keyValue;
		public HashMap<V, K> valueKey;

		public FlexibleHashMap() {
			this.keyValue = new HashMap<K, V>();
			this.valueKey = new HashMap<V, K>();
		}

		public void add(K key, V value) {
			this.keyValue.put(key, value);
			this.valueKey.put(value, key);
		}

		public V getValue(K key) {
			return this.keyValue.get(key);
		}

		public K getKey(V value) {
			return this.valueKey.get(value);
		}

	}

	// ! Process the changes in the mode selector and pass that information to
	// device
	final RadioGroup.OnCheckedChangeListener ToggleListener = new RadioGroup.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(final RadioGroup radioGroup, final int i) {
			Log.d("Transmitter", "Toggled");
			for (int j = 0; j < radioGroup.getChildCount(); j++) {
				final ToggleButton view = (ToggleButton) radioGroup
						.getChildAt(j);
				view.setChecked(view.getId() == i);
			}

			// if (objMngr != null) {
			// UAVObject obj = objMngr.getObject("FlightStatus");
			// if (obj == null)
			// return;
			// UAVObjectField field = obj.getField("FlightMode");
			// if (field == null)
			// return;
			//
			// String mode = modesToId.getKey(i);
			//
			// if (mode != null) {
			// Log.i(TAG, "Selecting mode: " + mode);
			// field.setValue(mode);
			// } else
			// Log.e(TAG, "Unknown mode for this button");
			//
			// obj.updated();
			// }
			// if (objMngr != null){
			// UAVObject obj = objMngr.getObject("ManualControlCommand");
			// if (obj == null)
			// return;
			// UAVObjectField field = obj.getField("FlightModeSwitchPosition");
			// if (field == null)
			// return;
			//
			//
			//
			// }
		}
	};

}
