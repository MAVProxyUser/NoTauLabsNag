/**
 ******************************************************************************
 * @file       Controller.java
 * @author     The OpenPilot Team, http://www.openpilot.org Copyright (C) 2012.
 * @brief      Allows controlling the UAV over telemetry.  This activity
 *             pushes the appropriate settings to the remote device for it to
 *             listen to the GCSReceiver.
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
package org.openpilot_nonag.androidgcs;

import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

import org.openpilot_nonag.androidgcs.R;
import org.openpilot_nonag.uavtalk.UAVDataObject;
import org.openpilot_nonag.uavtalk.UAVObject;
import org.openpilot_nonag.uavtalk.UAVObjectField;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.MobileAnarchy.Android.Widgets.Joystick.DualJoystickView;
import com.MobileAnarchy.Android.Widgets.Joystick.JoystickMovedListener;
import com.MobileAnarchy.Android.Widgets.Joystick.JoystickView;

import android.view.*;
import android.widget.*;

public class Controller extends ObjectManagerActivity {
	private final String TAG = "Controller";

	private final boolean DEBUG = true;

	private final int THROTTLE_CHANNEL = 0;
	private final int ROLL_CHANNEL = 1;
	private final int PITCH_CHANNEL = 2;
	private final int YAW_CHANNEL = 3;
	private final int FLIGHTMODE_CHANNEL = 4;

	private final int CHANNEL_MIN = 1000;
	private final int CHANNEL_MAX = 2000;
	private final int CHANNEL_NEUTRAL = 1500;
	private final int CHANNEL_NEUTRAL_THROTTLE = 1100;

	private double throttle = 0.1, roll = 0.1, pitch = -0.1, yaw = 0;
	private boolean updated;
	private boolean leftJoystickHeld, rightJoystickHeld;

	Timer sendTimer = new Timer();
	TextView manualView;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.controller);
		manualView = (TextView) findViewById(R.id.manualControlValues);
	}

   // Use this for NVIDIA Shield input. 
   @Override
   public boolean onKeyDown(int i, KeyEvent keyevent)
    {
        Log.d("onKeyDown Keycode", String.valueOf(i));
	if (i == 108)
		Log.d("keypress:", "Start Button");
	if (i == 96)
		Log.d("keypress:", "A Button");
	if (i == 4)
		Log.d("keypress:", "Back Button");
	if (i == 97)
		Log.d("keypress:", "B Button");
	if (i == 100)
		Log.d("keypress:", "Y Button");
	if (i == 99)
		Log.d("keypress:", "X Button");
	if (i == 106)
		Log.d("keypress:", "Left Thumb Click");
	if (i == 107)
		Log.d("keypress:", "Right Thumb Click");
	if (i == 102)
		Log.d("keypress:", "Left Bumper");
	if (i == 103)
		Log.d("keypress:", "Right Bumper");
	
	return true;
    }

   // Use this for NVIDIA Shield input. 
   @Override
   public boolean onKeyUp(int i, KeyEvent keyevent)
    {
        Log.d("onKeyUp Keycode", String.valueOf(i));
	return true;
    }

   // Use this for NVIDIA Shield input. 
   @Override
    public boolean onGenericMotionEvent(MotionEvent motionevent)
    {
        float f = motionevent.getActionMasked();
        Log.d("onGenericMotionEvent Keycode", String.valueOf((new StringBuilder("MotionEvent Action =")).append(f).append("\nx: ").append(motionevent.getAxisValue(0)).append("\ny: ").append(motionevent.getAxisValue(1)).append("\nz: ").append(motionevent.getAxisValue(11)).append("\nRx: ").append(motionevent.getAxisValue(12)).append("  LTrigger: ").append(motionevent.getAxisValue(17)).append("\nRy: ").append(motionevent.getAxisValue(13)).append("  RTrigger: ").append(motionevent.getAxisValue(18)).append("\nRz: ").append(motionevent.getAxisValue(14)).append("\nHat X: ").append(motionevent.getAxisValue(15)).append("\nHat Y: ").append(motionevent.getAxisValue(16)).toString()));
        boolean flag;
        boolean flag1;
        if(motionevent.getAxisValue(15) != 0.0F)
            flag = true;
        else
            flag = false;
        if(motionevent.getAxisValue(16) != 0.0F)
            flag1 = true;
        else
            flag1 = false;

//        if(!(flag | flag1))
	return true;	
   }

	Observer settingsUpdated = new Observer() {
		@Override
		public void update(Observable observable, Object data) {
			// Once we have updated settings we can active the GCS receiver mode
			//if (DEBUG) 
			Log.d(TAG,"Got update from settings");
			UAVDataObject manualControlSettings = (UAVDataObject) objMngr.getObject("ManualControlSettings");
			if(manualControlSettings != null) {
				manualControlSettings.removeUpdatedObserver(this);
			}
			activateGcsReceiver();
		}
	};

	@Override
	void onOPConnected() {
		super.onOPConnected();

		//if (DEBUG) 
		Log.d(TAG, "onOPConnected()");

		DualJoystickView joystick = (DualJoystickView) findViewById(R.id.dualjoystickView);
		joystick.setMovementConstraint(JoystickView.CONSTRAIN_BOX);
		joystick.setMovementRange((int)MOVEMENT_RANGE, (int)MOVEMENT_RANGE);

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		int mode = Integer.decode(prefs.getString("controller_type", "1"));
		switch(mode) {
		case 1:
			if (DEBUG) Log.d(TAG, "Mode1 connected");
			joystick.setOnJostickMovedListener(mode1_left, mode1_right);
			break;
		case 2:
			if (DEBUG) Log.d(TAG, "Mode2 connected");
			joystick.setOnJostickMovedListener(mode2_left, mode2_right);
			break;
		default:
			Log.e(TAG, "Unknown controller type");
			return;
		}

		// Subscribe to updates from ManualControlCommand and show the values for crude feedback
		UAVDataObject manualControl = (UAVDataObject) objMngr.getObject("ManualControlCommand");
		registerObjectUpdates(manualControl);

		// Request a one time update before configuring for GCS control mode
		UAVDataObject manualSettings = (UAVDataObject) objMngr.getObject("ManualControlSettings");
		manualSettings.addUpdatedObserver(settingsUpdated);
		manualSettings.updateRequested();

		//! This timer task actually periodically sends updates to the UAV
		TimerTask controllerTask = new TimerTask() {
			@Override
			public void run() {
				uavobjHandler.post(new Runnable() {
					@Override
					public void run() {
						if ((leftJoystickHeld && rightJoystickHeld) || updated) {
							UAVObject gcsReceiver = objMngr.getObject("GCSReceiver");
							if (gcsReceiver == null) {
								Log.e(TAG, "No GCS Receiver object found");
								return;
							}

							UAVObjectField channels = gcsReceiver.getField("Channel");
							if(channels == null) {
								Log.e(TAG, "GCS Receiver object ill formatted");
								return;
							}

							channels.setValue(scaleChannel(throttle, CHANNEL_NEUTRAL_THROTTLE), THROTTLE_CHANNEL);
							channels.setValue(scaleChannel(roll, CHANNEL_NEUTRAL), ROLL_CHANNEL);
							channels.setValue(scaleChannel(pitch, CHANNEL_NEUTRAL), PITCH_CHANNEL);
							channels.setValue(scaleChannel(yaw, CHANNEL_NEUTRAL), YAW_CHANNEL);
							channels.setValue(scaleChannel(0, CHANNEL_NEUTRAL), FLIGHTMODE_CHANNEL);

							gcsReceiver.updated();

							updated = false;

							if (DEBUG) Log.d(TAG, "Send update" + gcsReceiver.toStringData());
						}
						updated = false;
					}
				});
			}
		};
		sendTimer.schedule(controllerTask, 500, 10);
	}

	/**
	 * Show the string description of manual control command
	 */
	@Override
	protected void objectUpdated(UAVObject obj) {
		if (obj.getName().compareTo("ManualControlCommand") == 0) {
			TextView manualView = (TextView) findViewById(R.id.manualControlValues);
			if (manualView != null)
				manualView.setText(obj.toStringData());
		}
	}

	/**
	 * Active GCS receiver mode
	 */
	private void activateGcsReceiver() {
		UAVObject manualControlSettings = objMngr.getObject("ManualControlSettings");

		if (manualControlSettings == null) {
			Toast.makeText(this, "Failed to get manual control settings", Toast.LENGTH_SHORT).show();
			return;
		}

		UAVObjectField channelGroups = manualControlSettings.getField("ChannelGroups");
		UAVObjectField channelNumber = manualControlSettings.getField("ChannelNumber");
		UAVObjectField channelMax = manualControlSettings.getField("ChannelMax");
		UAVObjectField channelNeutral = manualControlSettings.getField("ChannelNeutral");
		UAVObjectField channelMin = manualControlSettings.getField("ChannelMin");
		if (channelGroups == null || channelMax == null || channelNeutral == null ||
				channelMin == null || channelNumber == null) {
			Toast.makeText(this,  "Manual control settings not formatted correctly", Toast.LENGTH_SHORT).show();
			return;
		}

		/* Configure the manual control module how the GCS controller expects
		 * This order MUST correspond to the enumeration order of ChannelNumber in
		 * ManualControlSettings.
		 */
		int channels[] = { THROTTLE_CHANNEL, ROLL_CHANNEL, PITCH_CHANNEL, YAW_CHANNEL, FLIGHTMODE_CHANNEL };
		for (int i = 0; i < channels.length; i++) {
			channelGroups.setValue("GCS", channels[i]);
			channelNumber.setValue(1 + channels[i], i); // Add 1 because this uses 0 for "NONE"
			channelMin.setValue(CHANNEL_MIN, channels[i]);
			channelMax.setValue(CHANNEL_MAX, channels[i]);
			switch(channels[i]) {
			case THROTTLE_CHANNEL:
				channelNeutral.setValue(CHANNEL_NEUTRAL_THROTTLE, channels[i]);
				break;
			default:
				channelNeutral.setValue(CHANNEL_NEUTRAL, channels[i]);
			break;
			}
		}

		// Send settings to the UAV
		manualControlSettings.updated();


	}

	/**
	 * Scale the channels to the output range the flight controller expects
	 */
	private float scaleChannel(double in, double neutral) {
		// Check bounds
		if (in > 1)
			in = 1;
		if (in < -1)
			in = -1;

		if (in >= 0)
			return (float) (neutral + (CHANNEL_MAX - neutral) * in);
		return (float) (neutral + (neutral - CHANNEL_MIN) * in);
	}

	private final JoystickMovedListener mode1_left = new JoystickMovedListener() {
		@Override
		public void OnMoved(int pan, int tilt) {
			pitch = tilt / MOVEMENT_RANGE;
			yaw = pan / MOVEMENT_RANGE;
			leftJoystickHeld = true;
		}
		@Override
		public void OnReleased() { leftJoystickHeld = false; throttle = -1; updated = true; }
		@Override
		public void OnReturnedToCenter() { }
	};

	private final JoystickMovedListener mode1_right = new JoystickMovedListener() {
		@Override
		public void OnMoved(int pan, int tilt) {
			throttle = (-tilt + (MOVEMENT_RANGE -5)) / (MOVEMENT_RANGE - 5);
			throttle *= 0.5;
			if (throttle < 0)
				throttle = -1;
			roll = pan / MOVEMENT_RANGE;
			rightJoystickHeld = true;
		}
		@Override
		public void OnReleased() { rightJoystickHeld = false; throttle = -1; updated = true; }
		@Override
		public void OnReturnedToCenter() { }
	};

	private final JoystickMovedListener mode2_left = new JoystickMovedListener() {
		@Override
		public void OnMoved(int pan, int tilt) {
			throttle = (-tilt + (MOVEMENT_RANGE -5)) / (MOVEMENT_RANGE - 5);
			throttle *= 0.5;
			if (throttle < 0)
				throttle = -1;
			yaw = pan / MOVEMENT_RANGE;
			leftJoystickHeld = true;
		}
		@Override
		public void OnReleased() { leftJoystickHeld = false; throttle = -1; updated = true; }
		@Override
		public void OnReturnedToCenter() { }
	};

	private final JoystickMovedListener mode2_right = new JoystickMovedListener() {
		@Override
		public void OnMoved(int pan, int tilt) {
			pitch = tilt / MOVEMENT_RANGE;
			roll = pan / MOVEMENT_RANGE;
			rightJoystickHeld = true;
		}
		@Override
		public void OnReleased() { rightJoystickHeld = false; throttle = -1; updated = true; }
		@Override
		public void OnReturnedToCenter() { }
	};

	final double MOVEMENT_RANGE = 50.0;

}
