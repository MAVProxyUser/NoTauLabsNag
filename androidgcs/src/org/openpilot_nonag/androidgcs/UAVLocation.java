/**
 ******************************************************************************
 * @file       UAVLocation.java
 * @author     The OpenPilot Team, http://www.openpilot.org Copyright (C) 2012.
 * @brief      Display the UAV location on google maps
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

package org.openpilot_nonag.androidgcs;
import org.openpilot_nonag.uavtalk.UAVObject;
import java.math.BigDecimal;

import android.location.Location;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import android.os.Bundle;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import android.graphics.Color;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;

import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
//import com.google.android.maps.GeoPoint;
import com.google.android.gms.maps.model.LatLng; // replaces GeoPoint
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import com.google.android.gms.maps.SupportMapFragment;
import android.view.WindowManager;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.VisibleRegion;
public class UAVLocation extends ObjectManagerActivity implements OnMyLocationChangeListener
{
	private final String TAG = "UAVLocation";
	private static int LOGLEVEL = 3;
	private static boolean WARN = LOGLEVEL > 1;
	private static boolean DEBUG = LOGLEVEL > 0;

	private GoogleMap mMap;
	private SupportMapFragment mapFrag;
	private Marker mUavMarker;
	private Marker mHomeMarker;

    	private LatLng homeLocation;
    	private LatLng uavLocation;
    	private LatLng touchLocation;
    	private List<LatLng> UAVpathPoints = new ArrayList<LatLng>();
    	private List<LatLng> TabletpathPoints = new ArrayList<LatLng>();
    	private Polyline UAVpathLine;
    	private Polyline TabletpathLine;

    	@Override
	public void onCreate(Bundle icicle) {

		super.onCreate(icicle);
     		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // disable sleep
		setContentView(R.layout.map_layout);
		mapFrag = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_view));

		// something wrong *here* (only on versions with out Google Play Services) need to check instead of crashing. 

		mMap = mapFrag.getMap();
		mMap.setMyLocationEnabled(true);
		mMap.setOnMyLocationChangeListener(this);
		mMap.setMapType(mMap.MAP_TYPE_HYBRID);

                UAVpathLine = mMap.addPolyline(new PolylineOptions().width(5).color(Color.WHITE));
                UAVpathLine.setPoints(UAVpathPoints);
                TabletpathLine = mMap.addPolyline(new PolylineOptions().width(5).color(Color.BLUE));
                TabletpathLine.setPoints(TabletpathPoints);

		registerForContextMenu(mapFrag.getView());
		mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                        @Override
                        public void onMapLongClick(LatLng arg0) {
                                Log.d(TAG, "Click");
				mapFrag.getView().showContextMenu();
				touchLocation = arg0;
                	}
		});

    }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v,
                                        ContextMenuInfo menuInfo) {
            super.onCreateContextMenu(menu, v, menuInfo);
	    Activity mapAct = (Activity) this;
            MenuInflater inflater = mapAct.getMenuInflater();
            inflater.inflate(R.menu.map_click_actions, menu);
        }

        @Override
        public boolean onContextItemSelected(MenuItem item) {

            switch (item.getItemId()) {
                case R.id.map_action_jump_to_uav:
			uavLocation = getUavLocation(); // null pointer somewhere around here. 
                    if (uavLocation != null) {
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(uavLocation.latitude, uavLocation.longitude)));
                    }
                    return true;
                case R.id.map_action_clear_uav_path:
                        UAVpathPoints.clear();
                        UAVpathLine.setPoints(UAVpathPoints);
                        return true;
                case R.id.map_action_clear_tablet_path:
                        TabletpathPoints.clear();
                        TabletpathLine.setPoints(TabletpathPoints);
                        return true;
                case R.id.map_action_set_home:
			homeLocation = touchLocation;
			// change this to actually push out the UAVO to the board.
                        return true;
                default:
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

                Log.d(TAG, "Tablet location changed and is currently lat / lon pair " + latitude + " " + longitude);
                TabletpathPoints.add(latLng);
//	        Log.d(TAG, "Tablet path point being added");
                TabletpathLine.setPoints(TabletpathPoints);

	    }

	@Override
	void onOPConnected() {
		super.onOPConnected();

		UAVObject obj = objMngr.getObject("HomeLocation");
		if (obj != null) {
			obj.updateRequested(); // Make sure this is correct and been updated
			registerObjectUpdates(obj);
			objectUpdated(obj);
		}
		else
		{
                                Log.d(TAG, "HomeLocation is null");
		}

		obj = objMngr.getObject("PositionState");
		if (obj != null) {
			obj.updateRequested(); // Make sure this is correct and been updated
			registerObjectUpdates(obj);
			objectUpdated(obj);
		}
		else
		{
                                Log.d(TAG, "PositionState is null");
		}

		obj = objMngr.getObject("GPSPositionSensor");
		if (obj != null) {
			obj.updateRequested(); // Make sure this is correct and been updated
			registerObjectUpdates(obj);
			objectUpdated(obj);
		}
		else
		{
                                Log.d(TAG, "GPSPositionSensor is null");
		}
	}

	private LatLng getUavLocation() {
		// Original code was reliant upon the following behavior: 
		//
		// GPS sensor provides lat/long coordinates, you set a HomeLocation 
		// UAV Position is converted in NED (North,East,Down) coordinates relative to HomeLocation.
		
		// PositionActual is now PositionState
		//
		// http://forums.openpilot.org/topic/1578-changes-to-gps-objects/
		// filled with the AHRS's filtered version of position, heading and velocity vectors.
		// The GCS should be using the PositionActual object and not the GPSPosition directly. 
		// The reason is that the PositionActual will contain the more accurate AHRS derived position (by using raw GPS, baro, and IMU data). 
		//
		// For now we want to visualize the *raw* GPS info

		UAVObject pos = objMngr.getObject("GPSPositionSensor");
		if (pos == null)
		{
			return new LatLng(0,0);
		}
                else
		{
//                                Log.d(TAG, "PositionState info is valid");
                }

		double lat, lon;
		lat = (pos.getField("Latitude").getDouble() * .0000001 );
		lon = (pos.getField("Longitude").getDouble() * .0000001 );
                Log.d(TAG, "UAV location is currently lat / lon pair " + lat + " " + lon);

		return new LatLng(lat, lon);
	}

	// https://developers.google.com/maps/documentation/android/marker
	/**
	 * Called whenever any objects subscribed to via registerObjects
	 * update the marker location for home and the UAV
	 */
	@Override
	protected void objectUpdated(UAVObject obj) {
		if (obj == null)
			return;
		if (obj.getName().compareTo("HomeLocation") == 0) {
			Double lat = obj.getField("Latitude").getDouble() * .0000001;
			Double lon = obj.getField("Longitude").getDouble() * .0000001;

	                Log.d(TAG, "HomeLocation is at lat / lon pair " + lat + " " + lon);

			if (lat !=0 && lon !=0)
			{
				homeLocation = new LatLng(lat, lon);
				if (mHomeMarker == null) {
	                	        Log.d(TAG, "home marker is null so creating it");
					mHomeMarker = mMap.addMarker(new MarkerOptions().anchor(0.5f,0.5f)
			       		.position(new LatLng(homeLocation.latitude, homeLocation.longitude))
			       		.title("UAV_HOME")
			       		.snippet(String.format("%g, %g", homeLocation.latitude, homeLocation.longitude))
			       		.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_home)));
				} else {
	                        	Log.d(TAG, "home location marker has been updated to " + homeLocation.latitude + " " + homeLocation.latitude);
					mHomeMarker.setPosition((new LatLng(homeLocation.latitude, homeLocation.longitude)));
					mHomeMarker.setSnippet(String.format("%g, %g", homeLocation.latitude, homeLocation.longitude));
				}
			}
			else
			{
				Log.d(TAG, "Home location is invalid lat / lon pair 0, 0");
			}
		}

		else if (obj.getName().compareTo("GPSPositionSensor") == 0) {
			uavLocation = getUavLocation();
			LatLng loc = new LatLng(uavLocation.latitude, uavLocation.longitude);
			if (mUavMarker == null) {
	                        Log.d(TAG, "uav marker is null so creating it");
				CameraPosition camPos = mMap.getCameraPosition();
				LatLng lla = camPos.target;
				mUavMarker = mMap.addMarker(new MarkerOptions()
			       .position(new LatLng(lla.latitude, lla.longitude))
			       .title("UAV_LOCATION")
			       .snippet(String.format("%g, %g", uavLocation.latitude, uavLocation.longitude))
			       .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_uav)));
			} else {
	                        //Log.d(TAG, "uav location is being updated");
				mUavMarker.setPosition((new LatLng(uavLocation.latitude, uavLocation.longitude)));
				mUavMarker.setSnippet(String.format("%g, %g", uavLocation.latitude, uavLocation.longitude));
			}
                        UAVpathPoints.add(loc);
	                //Log.d(TAG, "uav path point being added");
                        UAVpathLine.setPoints(UAVpathPoints);
		}
	}


}

