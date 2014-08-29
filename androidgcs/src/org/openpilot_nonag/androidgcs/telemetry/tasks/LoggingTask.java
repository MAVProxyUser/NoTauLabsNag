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

public class LoggingTask implements TelemTask {

	final String TAG = "Logger";
	final String LOGGER_DIR = "/OpenPilotGCS";

	final boolean VERBOSE = true;
	final boolean DEBUG = true;

	private boolean loggingActive = false;
	private boolean headerWritten = false;

	private UAVObjectManager objMgr;
	private final List<UAVObject> listeningList = new ArrayList<UAVObject>();
	private File file;
	private boolean logging;
	private FileOutputStream fileStream;
	private UAVTalk uavTalk;

	UAVObject firmwareIapObj;

	@Override
	public void connect(UAVObjectManager objMgr, Context context) {
		this.objMgr = objMgr;

		// When new objects are registered, ensure we listen
		// to them
		objMgr.addNewObjectObserver(newObjObserver);
		objMgr.addNewInstanceObserver(newObjObserver);

		// Register all existing objects
		List<List<UAVObject>> objects = objMgr.getObjects();
		for (int i = 0; i < objects.size(); i++)
			for (int j = 0; j < objects.get(i).size(); j++)
				registerObject(objects.get(i).get(j));

		// as soon as this comes in, store it
		firmwareIapObj = objMgr.getObject("FirmwareIAPObj");
		if (firmwareIapObj != null) {
			firmwareIapObj.addUpdatedObserver(firmwareIapUpdated);
		}

	}

	@Override
	public void disconnect() {
		unregisterAllObjects();
		endLogging();
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

	// ! Observer to catch when objects are updated
	private final Observer objUpdatedObserver = new Observer() {
		@Override
		public void update(Observable observable, Object data) {
			UAVObject obj = (UAVObject) data;
			logObject(obj);
		}
	};
	// ! Observer to catch when new objects or instances are registered
	private final Observer newObjObserver = new Observer() {
		@Override
		public void update(Observable observable, Object data) {
			UAVObject obj = (UAVObject) data;
			registerObject(obj);
		}
	};

	public void startLogging() {

		File root = Environment.getExternalStorageDirectory();

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

				// write firmwareIapObj to filestream
				if (this.firmwareIapObj != null) {
					if (DEBUG)
						Log.d(TAG, "firmwareIapObj != null");
					headerWritten = true;

				} else {
					if (DEBUG)
						Log.d(TAG, "firmwareIapObj IS null!");
				}

				uavTalk = new UAVTalk(null, fileStream, objMgr);
				logging = true;

			} else {
				Log.e(TAG, "Unwriteable address");
			}
		} catch (IOException e) {
			Log.e(TAG, "Could not write file " + e.getMessage());
		}

		loggingActive = file.canWrite();

		if (headerWritten)
			logObject(firmwareIapObj);

	}

	public boolean endLogging() {
		if (DEBUG)
			Log.d(TAG, "Stop logging");
		logging = false;
		loggingActive = false;
		try {
			if (fileStream != null) {
				fileStream.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
	}

	// ! Write an updated object to the log file
	private void logObject(UAVObject obj) {
		if (loggingActive && headerWritten) {
			if (VERBOSE)
				Log.v(TAG, "Updated: " + obj.toString());
			try {
				long time = System.currentTimeMillis();
				fileStream.write((byte) (time & 0xff));
				fileStream.write((byte) ((time & 0x0000ff00) >> 8));
				fileStream.write((byte) ((time & 0x00ff0000) >> 16));
				fileStream.write((byte) ((time & 0xff000000) >> 24));

				long size = obj.getNumBytes();
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
				Log.e(TAG, "Error writing object to log!", e);
			}

		}
	}

	private final Observer firmwareIapUpdated = new Observer() {

		@Override
		public void update(Observable observable, Object data) {
			UAVObject firmwareIapObj = (UAVObject) data;
			if (firmwareIapObj == null)
				return;

			logObject((UAVObject) data);

		}
	};

}
