package org.openpilot_nonag.androidgcs;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.openpilot_nonag.androidgcs.AlarmsView.Alarm;
import org.openpilot_nonag.androidgcs.AlarmsView.AlarmState;
import org.openpilot_nonag.uavtalk.UAVObject;
import org.openpilot_nonag.uavtalk.UAVObjectField;

import android.os.Bundle;
import android.util.Log;

public class ControlCenterActivity extends ObjectManagerActivity {

	private static final String TAG = ControlCenterActivity.class
			.getSimpleName();

	public static int LOGLEVEL = 1;

	public static boolean VERBOSE = LOGLEVEL > 3;
	public static boolean WARN = LOGLEVEL > 2;
	public static boolean DEBUG = LOGLEVEL > 1;
	public static boolean ERROR = LOGLEVEL > 0;

	AlarmsView alarmsView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.control_center);

		alarmsView = (AlarmsView) findViewById(R.id.alarmsView);

	}
	
	@Override
	void onOPConnected() {
		super.onOPConnected();
		
		UAVObject obj = objMngr.getObject("SystemAlarms");
		if (obj != null)
			registerObjectUpdates(obj);
		objectUpdated(obj);
	}
	
	@Override
	void onOPDisconnected() {
		super.onOPDisconnected();
		
		alarmsView.clearAlarms();
	}

	/**
	 * Called whenever any objects subscribed to via registerObjects
	 */
    @Override
	public void objectUpdated(UAVObject obj) {
		if (DEBUG)
			Log.d(TAG, "Updated");
		
		if (obj.getName().compareTo("SystemAlarms") == 0) {
			
			UAVObjectField a = obj.getField("Alarm");
			List<String> names = a.getElementNames();
			List <String> options = a.getOptions();
			
			// Rank the alarms by order of severity
			for (int j = options.size() - 1; j > 0; j--) {
				for (int i = 0; i < names.size(); i++) {
					String alarmName = names.get(i);
					String alarmValue = a.getValue(i).toString();
					//Log.d(TAG, alarmName + " : " + alarmValue);
					processAlarm(alarmName, alarmValue);
				}
			}
		}
    }


    /**
     * Pass alarm details to View for processing
     * @param alarmName
     * @param alarmValue
     */
	private void processAlarm(String alarmName, String alarmValue) {

		alarmsView.setAlarmStatus(alarmName, alarmValue);
		
	}

	
}
