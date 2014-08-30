/**
 ******************************************************************************
 * @file       ObjectManagerFragment.java
 * @author     The OpenPilot Team, http://www.openpilot.org Copyright (C) 2012.
 * @brief      Base class for all fragments that use the UAVObjectManager.  This
 *             supports all the extensions the ObjectManagerActivity does, namely
 *             access to the UAVObjectManager and callbacks in the UI thread for
 *             object updates.
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

import java.util.Locale;

import org.openpilot_nonag.androidgcs.ObjectManagerActivity;
import org.openpilot_nonag.uavtalk.UAVObject;
import org.openpilot_nonag.uavtalk.UAVObjectManager;

import android.app.Activity;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.app.Fragment;
//import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;

public abstract class ObjectManagerFragment extends Fragment {

	private static final String TAG = ObjectManagerFragment.class.getSimpleName();
	private static final int LOGLEVEL = 0;
	private static boolean WARN = LOGLEVEL > 1;
	private static final boolean DEBUG = LOGLEVEL > 0;
	
	abstract protected String getDebugTag();
	
	UAVObjectManager objMngr;
	TextToSpeech tts = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (DEBUG) Log.d(TAG, "Created an ObjectManagerFragment");
		// For an activity this registers against the telemetry service intents.  Fragments must be notified by their
		// parent activity
		
		// TextToSpeech
		Intent checkIntent = new Intent();
		checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		
		tts = new TextToSpeech(getActivity().getApplicationContext(), new TextToSpeech.OnInitListener(){
			private int mCallCount = 0; // trying to investigate potential infinite loops

		    @Override
		    public void onInit(int status) {
		        if ((mCallCount % 100) == 1) {
		            // report this
		        }
		        mCallCount++;
		    }	
		});
		
		tts.setLanguage(Locale.US);

		
	}

	/**
	 * Attach to the parent activity so it can notify us when the connection
	 * changed
	 */
    @Override
    public void onAttach(Activity activity) {
    	super.onAttach(activity);
    	if (DEBUG) Log.d(TAG,"onAttach: " + getDebugTag());

        ObjectManagerActivity castActivity = null;
        try {
        	castActivity = (ObjectManagerActivity)getActivity();
        } catch (ClassCastException e) {
        	throw new android.app.Fragment.InstantiationException(
        			"Attaching a ObjectManagerFragment to an activity failed because the parent activity is not a ObjectManagerActivity",
        			e);
        }
        castActivity.addOnConnectionListenerFragment(this);
    }

	// The below methods should all be called by the parent activity at the appropriate times
	public void onOPConnected(UAVObjectManager objMngr) {
		this.objMngr = objMngr;
		if (DEBUG) Log.d(TAG,"onOPConnected");
	}

	public void onOPDisconnected() {
		objMngr = null;
		if (DEBUG) Log.d(TAG,"onOPDisconnected");
	}

	/**
	 * Called whenever any objects subscribed to via registerObjects
	 */
	public void objectUpdated(UAVObject obj) {

	}

	/**
	 * Register on the activities object monitor handler so that updates
	 * occur within that UI thread.  No need to maintain a handler for
	 * each fragment.
	 */
	protected void registerObjectUpdates(UAVObject object) {
		((ObjectManagerActivity) this.getActivity()).registerObjectUpdates(object, this);
	}

}
