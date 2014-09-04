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


	private void processAlarm(String alarmName, String alarmValue) {
    	
		AlarmState level = getAlarmValue(alarmValue);
		
		if(alarmName.equals("Attitude")){
			alarmsView.setAlarmStatus(Alarm.ATTI, level);
		}else if(alarmName.equals("Stabilization")){
			alarmsView.setAlarmStatus(Alarm.STAB, level);
		}else if(alarmName.equals("Guidance")){
			alarmsView.setAlarmStatus(Alarm.PATH, level);
		}else if(alarmName.equals("PathPlan")){
			alarmsView.setAlarmStatus(Alarm.PLAN, level);
		}else if(alarmName.equals("GPS")){
			alarmsView.setAlarmStatus(Alarm.GPS, level);
		}else if(alarmName.equals("Sensors")){
			alarmsView.setAlarmStatus(Alarm.SENSOR, level);
		}else if(alarmName.equals("Airspeed")){
			alarmsView.setAlarmStatus(Alarm.AIRSPD, level);
		}else if(alarmName.equals("Magnetometer")){
			alarmsView.setAlarmStatus(Alarm.MAG, level);
		}else if(alarmName.equals("Receiver")){
			alarmsView.setAlarmStatus(Alarm.INPUT, level);
		}else if(alarmName.equals("Actuator")){
			alarmsView.setAlarmStatus(Alarm.OUTPUT, level);
		}else if(alarmName.equals("I2C")){
			alarmsView.setAlarmStatus(Alarm.I2C, level);
		}else if(alarmName.equals("Telemetry")){
			alarmsView.setAlarmStatus(Alarm.TELEM, level);
		}else if(alarmName.equals("Battery")){
			alarmsView.setAlarmStatus(Alarm.BATT, level);
		}else if(alarmName.equals("FlightTime")){
			alarmsView.setAlarmStatus(Alarm.TIME, level);
		}else if(alarmName.equals("SystemConfiguration")){
			alarmsView.setAlarmStatus(Alarm.CONFIG, level);
		}else if(alarmName.equals("BootFault")){
			alarmsView.setAlarmStatus(Alarm.BOOT, level);
		}else if(alarmName.equals("OutOfMemory")){
			alarmsView.setAlarmStatus(Alarm.MEM, level);
		}else if(alarmName.equals("StackOverflow")){
			alarmsView.setAlarmStatus(Alarm.STACK, level);
		}else if(alarmName.equals("EventSystem")){
			alarmsView.setAlarmStatus(Alarm.EVENT, level);
		}else if(alarmName.equals("CPUOverload")){
			alarmsView.setAlarmStatus(Alarm.CPU, level);
		}else{
			Log.d(TAG, "Don't know what alarm [" + alarmName + "] is.");
		}
	}

	private AlarmState getAlarmValue(String alarmValue) {
		if(alarmValue.equals("Uninitialised"))
			return AlarmState.UNINIT;
		else if(alarmValue.equals("OK"))
			return AlarmState.OK;
		else if(alarmValue.equals("Warning"))
			return AlarmState.WARNING;
		else if(alarmValue.equals("Critical"))
			return AlarmState.CRITICAL;
		else if(alarmValue.equals("Error"))
			return AlarmState.CRITICAL;
		
		return AlarmState.NONE;
		
	}
}
