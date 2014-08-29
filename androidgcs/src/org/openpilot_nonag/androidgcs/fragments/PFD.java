/**
 ******************************************************************************
 * @file       PFD.java
 * @author     The OpenPilot Team, http://www.openpilot.org Copyright (C) 2012.
 * @brief      The PFD display fragment
 * @see        The GNU Public License (GPL) Version 3
 *
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

import org.openpilot_nonag.androidgcs.views.FlightStatusView;
import org.openpilot_nonag.androidgcs.views.GpsView;
import org.openpilot_nonag.androidgcs.views.HeadingView;
import org.openpilot_nonag.androidgcs.views.AltitudeView;
import org.openpilot_nonag.androidgcs.views.AttitudeView;
import org.openpilot_nonag.androidgcs.views.BatteryView;


import org.openpilot_nonag.androidgcs.R;
import org.openpilot_nonag.uavtalk.UAVObject;
import org.openpilot_nonag.uavtalk.UAVObjectManager;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PFD extends ObjectManagerFragment {

	private static final String TAG = ObjectManagerFragment.class
			.getSimpleName();
	private static final int LOGLEVEL = 0;
	private static boolean WARN = LOGLEVEL > 1;
	private static final boolean DEBUG = LOGLEVEL > 0;
	
	private String flightMode = "";
	private String armedStatus ="";

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.pfd, container, false);
	}

	@Override
	public void onOPConnected(UAVObjectManager objMngr) {

		super.onOPConnected(objMngr);
		
		if (DEBUG)
			Log.d(TAG, "On connected");

		UAVObject obj = objMngr.getObject("AttitudeState");
		if (obj != null)
			registerObjectUpdates(obj);
		objectUpdated(obj);
		
                obj = objMngr.getObject("GPSPositionSensor");
                if (obj != null) {
                        registerObjectUpdates(obj);
                        objectUpdated(obj);
                }

                obj = objMngr.getObject("PositionState");
                if (obj != null) {
                        registerObjectUpdates(obj);
                        objectUpdated(obj);
                }

                obj = objMngr.getObject("FlightBatteryState");
                if (obj != null) {
                        registerObjectUpdates(obj);
                        objectUpdated(obj);
                }

		obj = objMngr.getObject("FlightStatus");
		if(obj != null)
			registerObjectUpdates(obj);
		objectUpdated(obj);
	}

	/**
	 * Called whenever any objects subscribed to via registerObjects
	 */
	@Override
	public void objectUpdated(UAVObject obj) {
		
		if (DEBUG)
			Log.d(TAG, "Updated");

		if (obj.getName().compareTo("AttitudeState") == 0) {
			double pitch;
			double roll;
			double yaw;
	        	try
	        	{
                        	pitch = obj.getField("Pitch").getDouble();
                        	roll = obj.getField("Roll").getDouble();
                        	yaw = obj.getField("Yaw").getDouble();

                        	// TODO: These checks, while sensible, are necessary because the
                        	// callbacks aren't
                        	// removed when we switch to different activities sharing this fragment
                        	Activity parent = getActivity();
                        	AttitudeView attitude = null;
                        	HeadingView heading = null;
                        	if (parent != null) {
                        	        attitude = (AttitudeView) parent.findViewById(R.id.attitude_view);
                                	heading = (HeadingView) parent.findViewById(R.id.heading_view);
                        	}
                        	if (attitude != null) {
                                	attitude.setRoll(roll);
                                	attitude.setPitch(pitch);
                                	attitude.invalidate();
                        	}
                        	if (heading != null) {
                                	heading.setBearing(yaw);
                        	}
		    	}
	            	catch (NullPointerException e)
	            	{
	                	//Toast.makeText(this, "Catching Nulls on UAVObjects, link may be failing", Toast.LENGTH_SHORT).show();
	                	Log.d("PFD fragment", "Catching Nulls on UAVObjects, link may be failing");
	            	}
		}

                if (obj.getName().compareTo("GPSPositionSensor") == 0) {

                        int satellites = (int) obj.getField("Satellites").getDouble();
                        double pdop = obj.getField("PDOP").getDouble();

                        Activity parent = getActivity();
                        GpsView gpsView = null;
                        if (parent != null) {
                                gpsView = (GpsView) parent.findViewById(R.id.gps_view);
                        }
                        if (gpsView != null) {
                                gpsView.setSatellites(satellites);
                                gpsView.setPDOP(pdop);
                        }
                }

                if (obj.getName().compareTo("PositionState") == 0) {

                        int down = (int) obj.getField("Down").getDouble();

                        Activity parent = getActivity();
                        AltitudeView altitudeView = null;
                        if (parent != null) {
                                altitudeView = (AltitudeView) parent.findViewById(R.id.altitude_view);
                        }
                        if (altitudeView != null) {
                                altitudeView.setAltitude(-down);
                        }
                }

                if (obj.getName().compareTo("FlightBatteryState") == 0) {
                        double voltage = obj.getField("Voltage").getDouble();
                        double current = obj.getField("Current").getDouble();

                        Activity parent = getActivity();
                        BatteryView batteryView = null;
                        if (parent != null) {
                                batteryView = (BatteryView) parent.findViewById(R.id.battery_view);
                        }
                        if (batteryView != null) {
                                if (voltage == 0 && current == 0) {
                                        batteryView.setVisibility(View.INVISIBLE);
                                } else {
                                        batteryView.setVisibility(View.VISIBLE);
                                        batteryView.setCurrent(current);
                                        batteryView.setVoltage(voltage);
                                        
                                }
                        }
                }

		if (obj.getName().compareTo("FlightStatus") == 0) {
			try{

	                       	String armedStatus = obj.getField("Armed").getValue().toString();
	                        String flightMode = obj.getField("FlightMode").getValue().toString();

	                        Activity parent = getActivity();
        	                FlightStatusView flightStatusView = null;
        	                if (parent != null) {
        	                        flightStatusView = (FlightStatusView) parent.findViewById(R.id.flight_status_view);
        	                }
        	                if (flightStatusView != null) {
                	                flightStatusView.setArmed(armedStatus);
                        	        flightStatusView.setFlightMode(flightMode);
                        	}

			}
			catch (NullPointerException e)
	        	{
	        	        //Toast.makeText(this, "Catching Nulls on UAVObjects, link may be failing", Toast.LENGTH_SHORT).show();
	                	Log.d("PFD fragment", "Catching Nulls on UAVObjects, link may be failing");
	        	}

		}
		

	}
	
	@Override
	protected String getDebugTag() {
		return TAG;
	}
}
