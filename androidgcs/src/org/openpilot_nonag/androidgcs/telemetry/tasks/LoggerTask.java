/**
 ******************************************************************************
 * @file       LoggerTask.java
 * @author     Tau Labs, http://taulabs.org, Copyright (C) 2012-2013
 * @brief      An @ref ITelemTask which generates logs
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
package org.openpilot_nonag.androidgcs.telemetry.tasks;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.openpilot_nonag.uavtalk.UAVObject;
import org.openpilot_nonag.uavtalk.UAVObjectManager;
import org.openpilot_nonag.uavtalk.UAVTalk;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class LoggerTask implements TelemTask {

	/* Debugging information */
	final String TAG = LoggerTask.class.getSimpleName();
	final boolean VERBOSE = false;
	final boolean DEBUG = false;

	final String LOGGER_DIR = "/OpenPilotGCS";

	private UAVObjectManager objMngr;
	private File file;
	private boolean logging;
	private FileOutputStream fileStream;
	private UAVTalk uavTalk;
	private final List<UAVObject> listeningList = new ArrayList<UAVObject>();

	private Long timestamp;

	public void startLogging() {

		File root = Environment.getExternalStorageDirectory();

		timestamp = System.currentTimeMillis();
		// Make the directory if it doesn't exist
		File logDirectory = new File(root, LOGGER_DIR);
		logDirectory.mkdirs();

		Date d = new Date();
		String date = (new SimpleDateFormat("yyyyMMdd_hhmmss")).format(d);
		String fileName = "logs_" + date + ".opl";

		file = new File(logDirectory, fileName);
		if (DEBUG)
			Log.d(TAG, "Trying for file: " + file.getAbsolutePath());
		try {
			if (root.canWrite()) {
				fileStream = new FileOutputStream(file);
				uavTalk = new UAVTalk(null, fileStream, objMngr);
				logging = true;
			} else {
				Log.e(TAG, "Unwriteable address");
			}
		} catch (IOException e) {
			Log.e(TAG, "Could not write file " + e.getMessage());
		}

		// Add listener to write the version header when available
		UAVObject firmwareIapObj = objMngr.getObject("FirmwareIAPObj");
		if (firmwareIapObj != null) {
			firmwareIapObj.addUpdatedObserver(firmwareIapUpdated);
		}

	}

	private final Observer firmwareIapUpdated = new Observer() {

		@Override
		public void update(Observable observable, Object data) {
			UAVObject firmwareIapObj = (UAVObject) data;
			if (firmwareIapObj == null)
				return;
			
			logObject(firmwareIapObj);
			
			firmwareIapObj.removeUpdatedObserver(this);
		}
	};

	public void stopLogging() {

	}

	@Override
	public void connect(UAVObjectManager o, Context context) {
		objMngr = o;

		// When new objects are registered, ensure we listen
		// to them
		o.addNewObjectObserver(newObjObserver);
		o.addNewInstanceObserver(newObjObserver);

		// Register all existing objects
		List<List<UAVObject>> objects = objMngr.getObjects();
		for (int i = 0; i < objects.size(); i++)
			for (int j = 0; j < objects.get(i).size(); j++)
				registerObject(objects.get(i).get(j));

		// For now default to starting to log
		startLogging();

	}

	private void registerObject(UAVObject obj) {
		synchronized (listeningList) {
			if (!listeningList.contains(obj)) {
				obj.addUpdatedObserver(objUpdatedObserver);
				listeningList.add(obj);
			}
		}
	}

	// ! Unregister all objects from logging
	private void unregisterAllObjects() {
		synchronized (listeningList) {
			for (int i = 0; i < listeningList.size(); i++) {
				listeningList.get(i).removeUpdatedObserver(objUpdatedObserver);
			}
			listeningList.clear();
		}
	}

	// ! Observer to catch when new objects or instances are registered
	private final Observer newObjObserver = new Observer() {
		@Override
		public void update(Observable observable, Object data) {
			UAVObject obj = (UAVObject) data;
			registerObject(obj);
		}
	};

	// ! Observer to catch when objects are updated
	private final Observer objUpdatedObserver = new Observer() {
		@Override
		public void update(Observable observable, Object data) {
			UAVObject obj = (UAVObject) data;
			logObject(obj);
		}
	};

	protected void logObject(UAVObject obj) {
		if (logging) {
			try {
				int elapsed = (int) (System.currentTimeMillis() - timestamp);

				fileStream.write((byte) (elapsed & 0xff));
				fileStream.write((byte) ((elapsed & 0x0000ff00) >> 8));
				fileStream.write((byte) ((elapsed & 0x00ff0000) >> 16));
				fileStream.write((byte) ((elapsed & 0xff000000) >> 24));

				long size = obj.getNumBytes() + 10 + 1;
				fileStream.write((byte) (size & 0x00000000000000ffl) >> 0);
				fileStream.write((byte) (size & 0x000000000000ff00l) >> 8);
				fileStream.write((byte) (size & 0x0000000000ff0000l) >> 16);
				fileStream.write((byte) (size & 0x00000000ff000000l) >> 24);
				fileStream.write((byte) (size & 0x000000ff00000000l) >> 32);
				fileStream.write((byte) (size & 0x0000ff0000000000l) >> 40);
				fileStream.write((byte) (size & 0x00ff000000000000l) >> 48);
				fileStream.write((byte) (size & 0xff00000000000000l) >> 56);

				uavTalk.sendObject(obj, false, false);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	@Override
	public void disconnect() {
		unregisterAllObjects();

	}

}
