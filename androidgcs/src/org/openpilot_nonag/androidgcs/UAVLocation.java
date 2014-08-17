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
import org.openpilot_nonag.uavtalk.UAVObjectField;
import java.math.BigDecimal;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
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
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.VisibleRegion;


public class UAVLocation extends ObjectManagerActivity {
	private final String TAG = "UAVLocation";
	private static int LOGLEVEL = 3;
	private static boolean WARN = LOGLEVEL > 1;
	private static boolean DEBUG = LOGLEVEL > 0;

	

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.uav_location);
	}
		

}
