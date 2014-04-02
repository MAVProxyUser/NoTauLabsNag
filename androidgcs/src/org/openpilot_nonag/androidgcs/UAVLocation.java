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

import android.os.Bundle;
import android.util.Log;

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

public class UAVLocation extends ObjectManagerActivity
{
	private final String TAG = "UAVLocation";
	private static int LOGLEVEL = 0;
//	private static boolean WARN = LOGLEVEL > 1;
	private static boolean DEBUG = LOGLEVEL > 0;

	private GoogleMap mMap;
//	private MapFragment mapFrag;
	private SupportMapFragment mapFrag;
	private Marker mUavMarker;
	private Marker mHomeMarker;

    LatLng homeLocation;
    LatLng uavLocation;

    @Override public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.map_layout);
//		mapFrag = ((MapFragment) getSupportFragmentManager().findFragmentById(R.id.map_view));

		mapFrag = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_view));

// something wrong *here*

		mMap = mapFrag.getMap();
		mMap.setMyLocationEnabled(true);
		mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
		//mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		//mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
		//mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

//                mapFrag.onCreate(savedInstanceState);
//		registerForContextMenu(mapFrag);

		mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                        @Override
                        public void onMapLongClick(LatLng arg0) {
                                Log.d(TAG, "Click");
                                // Animating to the touched position
                		mMap.animateCamera(CameraUpdateFactory.newLatLng(arg0));
                	}
		});

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

		obj = objMngr.getObject("PositionActual");
		if (obj != null) {
			obj.updateRequested(); // Make sure this is correct and been updated
			registerObjectUpdates(obj);
			objectUpdated(obj);
		}
	}

	private LatLng getUavLocation() {
		UAVObject pos = objMngr.getObject("PositionActual");
		if (pos == null)
			return new LatLng(0,0);

		UAVObject home = objMngr.getObject("HomeLocation");
		if (home == null)
			return new LatLng(0,0);

		double lat, lon, alt;
		lat = home.getField("Latitude").getDouble() / 10.0e6;
		lon = home.getField("Longitude").getDouble() / 10.0e6;
		alt = home.getField("Altitude").getDouble();

		// Get the home coordinates
		double T0, T1;
		T0 = alt+6.378137E6;
		T1 = Math.cos(lat * Math.PI / 180.0)*(alt+6.378137E6);

		// Get the NED coordinates
		double NED0, NED1;
		NED0 = pos.getField("North").getDouble();
		NED1 = pos.getField("East").getDouble();

		// Compute the LLA coordinates
		lat = lat + (NED0 / T0) * 180.0 / Math.PI;
		lon = lon + (NED1 / T1) * 180.0 / Math.PI;

		return new LatLng((int) (lat * 1e6), (int) (lon * 1e6));
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
			Double lat = obj.getField("Latitude").getDouble() / 10;
			Double lon = obj.getField("Longitude").getDouble() / 10;
			homeLocation = new LatLng(lat.intValue(), lon.intValue());
			if (mHomeMarker == null) {
				mHomeMarker = mMap.addMarker(new MarkerOptions()
			       .position(new LatLng(homeLocation.latitude, homeLocation.longitude))
			       .title("UAV")
			       .snippet("Fly fly fly")
			       .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_home)));
			} else {
				mHomeMarker.setPosition((new LatLng(homeLocation.latitude, homeLocation.longitude)));
			}
		} else if (obj.getName().compareTo("PositionActual") == 0) {
			uavLocation = getUavLocation();
			if (mUavMarker == null) {
				mUavMarker = mMap.addMarker(new MarkerOptions()
			       .position(new LatLng(uavLocation.latitude, uavLocation.longitude))
			       .title("UAV")
			       .snippet("Fly fly fly")
			       .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_uav)));
			} else {
				mUavMarker.setPosition((new LatLng(uavLocation.latitude, uavLocation.longitude)));
			}
		}
	}


}

