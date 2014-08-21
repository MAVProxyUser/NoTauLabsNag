/**
 ******************************************************************************
 * @file       Map.java
 * @author     The OpenPilot Team, http://www.openpilot.org Copyright (C) 2012.
 * @brief      A widget that shows the status of telemetry.
 * @see        The GNU Public License (GPL) Version 3
 *****************************************************************************/
/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.openpilot_nonag.androidgcs.fragments;

import java.util.ArrayList;
import java.util.List;

import org.openpilot_nonag.androidgcs.R;
import org.openpilot_nonag.androidgcs.util.SmartSave;
import org.openpilot_nonag.uavtalk.UAVDataObject;
import org.openpilot_nonag.uavtalk.UAVObject;
import org.openpilot_nonag.uavtalk.UAVObjectField;
import org.openpilot_nonag.uavtalk.UAVObjectManager;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class Map extends ObjectManagerFragment implements
		OnMyLocationChangeListener {

	private static final String TAG = Map.class.getSimpleName();
	private static final int LOGLEVEL = 0;
	private static final boolean DEBUG = LOGLEVEL > 0;

	private GoogleMap mMap;
	private Marker mUavMarker;
	private Marker mHomeMarker;
	private MapView mapView;

	private LatLng homeLocation;
	private LatLng uavLocation;
	private LatLng uavNEDLocation;
	private LatLng touchLocation;
	private List<LatLng> UAVpathPoints = new ArrayList<LatLng>();
	private List<LatLng> NEDUAVpathPoints = new ArrayList<LatLng>();
	private List<LatLng> TabletpathPoints = new ArrayList<LatLng>();
	private Polyline UAVpathLine;
	private Polyline NEDUAVpathLine;
	private Polyline TabletpathLine;
	
	private SmartSave smartSave;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		Log.d(TAG, "onCreate");
		
	}

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		Log.d(TAG, "*** onCreateView");

		// disable
		this.getActivity().getWindow()
				.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		// inflate and return the layout
		View v = inflater.inflate(R.layout.map_fragment, container, false);

		final Button buttonApply = (Button) v.findViewById(R.id.applyBtn);
		buttonApply.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	updateObject("HomeLocation");
            }
        });
		buttonApply.setEnabled(false);
		
		final Button buttonSave = (Button) v.findViewById(R.id.saveBtn);
		buttonSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	saveObject("HomeLocation");
            }
        });
		buttonSave.setEnabled(false);
        
		mapView = (MapView) v.findViewById(R.id.map_view_fragment);
		mapView.onCreate(savedInstanceState);


		// get map type from preferences
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		
		int map_type = Integer.decode(prefs.getString("map_type", "1"));

		// Needs to call MapsInitializer before doing any CameraUpdateFactory
		// calls
		MapsInitializer.initialize(getActivity());

		// something wrong *here* (only on versions with out Google Play
		// Services) need to check instead of crashing.
		// should take care of this
		int status = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getActivity());
		
		if (status == ConnectionResult.SUCCESS) {

			// Gets to GoogleMap from the MapView and does initialization stuff
			mMap = mapView.getMap();
			mMap.getUiSettings().setMyLocationButtonEnabled(true);
			mMap.setIndoorEnabled(true);
			mMap.setMyLocationEnabled(true);
			mMap.setOnMyLocationChangeListener(this);
			
			switch (map_type) {
			case 0:
				mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
				mMap.getUiSettings().setIndoorLevelPickerEnabled(true);
			
				break;
			case 1:
				mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
				mMap.getUiSettings().setIndoorLevelPickerEnabled(true);
				break;
			case 2:
				mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
				break;
			case 3:
				mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
				break;
			}

			UAVpathLine = mMap.addPolyline(new PolylineOptions().width(5)
					.color(Color.WHITE));
			UAVpathLine.setPoints(UAVpathPoints);
			NEDUAVpathLine = mMap.addPolyline(new PolylineOptions().width(5)
					.color(Color.RED));
			NEDUAVpathLine.setPoints(NEDUAVpathPoints);
			TabletpathLine = mMap.addPolyline(new PolylineOptions().width(5)
					.color(Color.BLUE));
			TabletpathLine.setPoints(TabletpathPoints);

			registerForContextMenu(mapView);
//			mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//				
//				@Override
//				public void onMapClick(LatLng arg0) {
//					// TODO Auto-generated method stub
//					Log.d(TAG, "Simple Click");
//					
//				}
//			});
			
			mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
				@Override
				public void onMapLongClick(LatLng arg0) {
					Log.d(TAG, "Long Click");
					//getView().showContextMenu();
					mapView.showContextMenu();
					touchLocation = arg0;
				}
			});
			
			if(objMngr != null){
				
			}
			
			if (savedInstanceState != null) {
				Log.d(TAG, "*** savedInstanceState != null");
				
				if (mMap != null) {
					if (DEBUG) Log.d(TAG, "Initializing location from bundle");

					CameraPosition camPos = mMap.getCameraPosition();

					// Use current location as defaults in case not found
					LocationManager locationManager =
							(LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
					Location tabletLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
					if (tabletLocation == null) {
						tabletLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
					}
					LatLng lla = new LatLng(0,0);
					if (tabletLocation != null) {
						lla = new LatLng(tabletLocation.getLatitude(), tabletLocation.getLongitude());
					}
					
					// Get the position from bundle
					double map_lat = savedInstanceState.getDouble("org.openpilot.map_lat", lla.latitude);
					double map_lon = savedInstanceState.getDouble("org.openpilot.map_lon", lla.longitude);
					
					// Start with default and see if one is stored
					float zoom = 17;
					zoom = (float) savedInstanceState.getDouble("org.openpilot.cam_zoom", zoom);
					
					// Move there
					lla = new LatLng(map_lat, map_lon);
					if (DEBUG) Log.d(TAG, "Init location: " + lla);
					mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lla, zoom));
				}
			} 
			
		} else {
			Toast.makeText(getActivity(),
					"Google Play Services are not enabled.", Toast.LENGTH_SHORT)
					.show();
		}

		return v;

	}

	@Override
	public void onResume() {
		mapView.onResume();
		super.onResume();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		mapView.onLowMemory();
	}
	
	/**
	 * Fetch the data back from the view and then send it to the UAV
	 */
	private boolean updateObject(String uavoName) {
		
		if(objMngr != null){
			UAVDataObject dataObj = (UAVDataObject)objMngr.getObject(uavoName);
			long objectID = dataObj.getObjID();
			long instID = dataObj.getInstID();
			
			UAVObject obj = objMngr.getObject(objectID, instID);
			if (obj == null)
				return false;
	
			Log.d(TAG, "Updating object id " + obj.getObjID());
			obj.updated();
					
			return true;
		}
	
		return false;
	}
	/**
	 * Fetch the data back from the view and then send it to the UAV
	 */
	private void saveObject(String uavoName) {

		if(objMngr != null){
			UAVObject objPer = objMngr.getObject("ObjectPersistence");
	
			if( !updateObject(uavoName)  || objPer == null) {
				Toast.makeText(getActivity(), "Save failed", Toast.LENGTH_LONG).show();
				return;
			}
		
			UAVDataObject obj = (UAVDataObject)objMngr.getObject(uavoName);
			long objectID = obj.getObjID();
			long instID = obj.getInstID();
			
			long thisId = objectID < 0 ? 0x100000000l + objectID : objectID;
			objPer.getField("Operation").setValue("Save");
			objPer.getField("Selection").setValue("SingleObject");
			Log.d(TAG,"Saving with object id: " + objectID + " swapped to " + thisId);
			objPer.getField("ObjectID").setValue(thisId);
			objPer.getField("InstanceID").setValue(instID);
			objPer.updated();
	
			Toast.makeText(getActivity(), "Save succeeded", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		Activity mapAct = this.getActivity();
		MenuInflater inflater = mapAct.getMenuInflater();
		inflater.inflate(R.menu.map_click_actions, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		if(item.getItemId() == R.id.map_action_jump_to_uav){
			uavLocation = getUavLocation(); // null pointer somewhere around
											// here.
			if (uavLocation != null) {
				mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(
						uavLocation.latitude, uavLocation.longitude)));
			}
			return true;
		}
		else if(item.getItemId() == R.id.map_action_clear_uav_path){
			UAVpathPoints.clear();
			UAVpathLine.setPoints(UAVpathPoints);
			return true;
		}
		else if(item.getItemId() == R.id.map_action_clear_NEDuav_path){
			NEDUAVpathPoints.clear();
			NEDUAVpathLine.setPoints(NEDUAVpathPoints);
			return true;
		}
		else if(item.getItemId() == R.id.map_action_clear_tablet_path){
			TabletpathPoints.clear();
			TabletpathLine.setPoints(TabletpathPoints);
			return true;
		}
		else if(item.getItemId() == R.id.map_action_set_home){
			if (DEBUG) Log.d(TAG, "Touch point location is currently lat / lon pair "
					+ touchLocation.latitude + " " + touchLocation.longitude);

			if (objMngr != null) {
				UAVObject obj = objMngr.getObject("HomeLocation");
				if (obj != null) {

					// LatLng is in degrees, Latitude and Longitude is stored as "deg * 10e6"
					int lat = (int) (touchLocation.latitude * 10e6);
					int lon = (int) (touchLocation.longitude * 10e6);
					
					if(lat != 0.0 && lon != 0.0){
						if (DEBUG) Log.d(TAG, "setting home lat / lon pair "
								+ lat + " " + lon);
	
						
						UAVObjectField latField = obj.getField("Latitude");
						latField.setInt(lat);
						
						UAVObjectField longField = obj.getField("Longitude");
						longField.setInt(lon);
						
						UAVObjectField setField = obj.getField("Set");
						setField.setValue("FALSE");
						 
						obj.updated();
						obj.updateRequested();
						
						Toast.makeText(getActivity(), "Setting Home Location",
								Toast.LENGTH_SHORT).show();
	
						// TODO: altitude
						// UAVObjectField altField = obj.getField("Altitude");
						// altField.setDouble(alt);
	
						if (DEBUG) Log.d(TAG, "Home location will be set to lat / long pair "
								+ lat + " " + lon);
					}else{
						if (DEBUG) Log.d(TAG, "Invalid Home location, will not be set. values: "
								+ lat + " " + lon);
					}
				
				}
			}

			return true;
		}
		else{
			return super.onContextItemSelected(item);
		}
		

	}

	@Override
	public void onMyLocationChange(Location location) {
		// Getting latitude of the current location
		double latitude = location.getLatitude();
		// Getting longitude of the current location
		double longitude = location.getLongitude();
		// Creating a LatLng object for the current location
		LatLng latLng = new LatLng(latitude, longitude);

		Log.d(TAG, "Tablet location changed and is currently lat / lon pair "
				+ latitude + " " + longitude);
		TabletpathPoints.add(latLng);
		TabletpathLine.setPoints(TabletpathPoints);

	}

	@Override
	public void onOPConnected(UAVObjectManager objMngr) {
		super.onOPConnected(objMngr);
		this.objMngr = objMngr;

		final Button buttonSave = (Button) getView().findViewById(R.id.saveBtn);
		buttonSave.setEnabled(true);
		final Button buttonApply = (Button) getView().findViewById(R.id.applyBtn);
		buttonApply.setEnabled(true);
		
		UAVDataObject obj = (UAVDataObject) objMngr.getObject("HomeLocation");
		if (obj != null) {
			
			smartSave = new SmartSave(objMngr, obj,
				(Button) getView().findViewById(R.id.saveBtn),
				(Button) getView().findViewById(R.id.applyBtn));
			
			//obj.updateRequested(); // Make sure this is correct and been
			registerObjectUpdates(obj);
			objectUpdated(obj);
		} else {
			Log.d(TAG, "HomeLocation is null");
		}

		obj = (UAVDataObject) objMngr.getObject("PositionState");
		if (obj != null) {
			obj.updateRequested(); // Make sure this is correct and been updated
			registerObjectUpdates(obj);
			objectUpdated(obj);
		} else {
			Log.d(TAG, "PositionState is null");
		}

		obj = (UAVDataObject) objMngr.getObject("GPSPositionSensor");
		if (obj != null) {
			obj.updateRequested(); // Make sure this is correct and been updated
			registerObjectUpdates(obj);
			objectUpdated(obj);
		} else {
			Log.d(TAG, "GPSPositionSensor is null");
		}
	}
	
	private LatLng getUavLocation() {
		// Original code was reliant upon the following behavior:
		//
		// GPS sensor provides lat/long coordinates, you set a HomeLocation
		// UAV Position is converted in NED (North,East,Down) coordinates
		// relative to HomeLocation.
		//
		// PositionActual is now PositionState
		//
		// http://forums.openpilot.org/topic/1578-changes-to-gps-objects/
		// filled with the AHRS's filtered version of position, heading and
		// velocity vectors.
		// The GCS should be using the PositionActual object and not the
		// GPSPosition directly.
		// The reason is that the PositionActual will contain the more accurate
		// AHRS derived position (by using raw GPS, baro, and IMU data).
		//
		// For now we want to visualize the *raw* GPS info

		double lat = 0, lon = 0;
		if (objMngr != null) {
			UAVObject pos = objMngr.getObject("GPSPositionSensor");
			if (pos == null) {
				return new LatLng(0, 0);
			} else {
				// Log.d(TAG, "PositionState info is valid");
			}

			lat = (pos.getField("Latitude").getInt() * .0000001);
			lon = (pos.getField("Longitude").getInt() * .0000001);
			Log.d(TAG, "UAV location is currently lat / lon pair " + lat + " "
					+ lon);
		}
		return new LatLng(lat, lon);
	}

	private LatLng getNEDUavLocation() {
		UAVObject pos = objMngr.getObject("PositionState");
		if (pos == null) {
			Log.d(TAG,
					"unable to grab NED coordinates due to invalid PositionState");
			return new LatLng(0, 0);
		}
		UAVObject home = objMngr.getObject("HomeLocation");
		if (home == null) {
			Log.d(TAG,
					"unable to grab NED coordinates due to invalid HomeLocation");
			return new LatLng(0, 0);
		}

		double lat, lon, alt;
		lat = home.getField("Latitude").getInt() * .0000001;
		lon = home.getField("Longitude").getInt() * .0000001;
		alt = home.getField("Altitude").getDouble();
		
		if(DEBUG) Log.d(TAG, "HomeLocation  is currently lat / lon pair " + lat + " "
				+ lon);
		
		// Get the home coordinates
		double T0, T1;
		T0 = alt + 6.378137E6;
		T1 = Math.cos(lat * Math.PI / 180.0) * (alt + 6.378137E6);

		// Get the NED coordinates
		double NED0, NED1;
		NED0 = pos.getField("North").getDouble();
		NED1 = pos.getField("East").getDouble();

		// Compute the LLA coordinates
		lat = lat + (NED0 / T0) * 180.0 / Math.PI;
		lon = lon + (NED1 / T1) * 180.0 / Math.PI;

		if(DEBUG) Log.d(TAG, "UAV NED location is currently lat / lon pair " + lat + " "
				+ lon);
		return new LatLng(lat, lon);
	}

	// https://developers.google.com/maps/documentation/android/marker
	/**
	 * Called whenever any objects subscribed to via registerObjects update the
	 * marker location for home and the UAV
	 */
	@Override
	public void objectUpdated(UAVObject obj) {
		if (obj == null)
			return;
		if (obj.getName().compareTo("HomeLocation") == 0) {
			Double lat = obj.getField("Latitude").getInt() * .0000001;
			Double lon = obj.getField("Longitude").getInt() * .0000001;
			
			Log.d(TAG, "objUpdated ** HomeLocation is at lat / lon pair " + lat + " " + lon);

			if (lat != 0 && lon != 0) {
				homeLocation = new LatLng(lat, lon);
				if (mHomeMarker == null) {
					Log.d(TAG, "home marker is null so creating it");
					mHomeMarker = mMap.addMarker(new MarkerOptions()
							.anchor(0.5f, 0.5f)
							.position(
									new LatLng(homeLocation.latitude,
											homeLocation.longitude))
							.title("UAV_HOME")
							.snippet(
									String.format("%g, %g",
											homeLocation.latitude,
											homeLocation.longitude))
							.icon(BitmapDescriptorFactory
									.fromResource(R.drawable.ic_home)));
				} else {
					Log.d(TAG, "home location marker has been updated to "
							+ homeLocation.latitude + " "
							+ homeLocation.latitude);
					mHomeMarker.setPosition((new LatLng(homeLocation.latitude,
							homeLocation.longitude)));
					mHomeMarker.setSnippet(String.format("%g, %g",
							homeLocation.latitude, homeLocation.longitude));
				}
			} else {
				Log.d(TAG, "Home location is invalid lat / lon pair 0, 0");
			}
		}

		else if (obj.getName().compareTo("GPSPositionSensor") == 0) {
			uavLocation = getUavLocation();
			uavNEDLocation = getNEDUavLocation();
			LatLng loc = new LatLng(uavLocation.latitude, uavLocation.longitude);
			LatLng NEDloc = new LatLng(uavNEDLocation.latitude,
					uavNEDLocation.longitude);
			if (uavLocation.latitude != 0 && uavLocation.longitude != 0) {
				if (mUavMarker == null) {
					Log.d(TAG, "uav marker is null so creating it");
					CameraPosition camPos = mMap.getCameraPosition();
					LatLng lla = camPos.target;
					mUavMarker = mMap.addMarker(new MarkerOptions()
							.position(new LatLng(lla.latitude, lla.longitude))
							.title("UAV_LOCATION")
							.snippet(
									String.format("%g, %g",
											uavLocation.latitude,
											uavLocation.longitude))
							.icon(BitmapDescriptorFactory
									.fromResource(R.drawable.ic_uav)));
				} else {
					// Log.d(TAG, "uav location is being updated");
					mUavMarker.setPosition((new LatLng(uavLocation.latitude,
							uavLocation.longitude)));
				}
				UAVpathPoints.add(loc);
				UAVpathLine.setPoints(UAVpathPoints);

				if (uavNEDLocation.latitude != 0
						&& uavNEDLocation.longitude != 0) {
					Log.d(TAG, "NEDLocation is at lat / lon pair "
							+ uavNEDLocation.latitude + " "
							+ uavNEDLocation.longitude);
					NEDUAVpathPoints.add(NEDloc);
					NEDUAVpathLine.setPoints(NEDUAVpathPoints);
				}
			} else {
				Log.d(TAG, "UAV location is at invalid lat / lon pair 0, 0");
			}
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		Log.d(TAG, "*** onPause");

		
	}
	
	@Override
	public void onSaveInstanceState (Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.d(TAG, "*** onSaveInstanceState");
		
		if (mMap != null) {
			CameraPosition camPos = mMap.getCameraPosition();
			LatLng lla = camPos.target;
			outState.putDouble("org.openpilot.map_lat", lla.latitude);
			outState.putDouble("org.openpilot.map_lon", lla.longitude);
			outState.putDouble("org.openpilot.cam_zoom", camPos.zoom);
			
		}
	}
	
}
