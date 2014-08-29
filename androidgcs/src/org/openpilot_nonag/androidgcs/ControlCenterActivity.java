package org.openpilot_nonag.androidgcs;

import org.openpilot_nonag.androidgcs.drawer.NavDrawerActivityConfiguration;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;

public class ControlCenterActivity extends ObjectManagerActivity {

	private static final String TAG = ControlCenterActivity.class.getSimpleName();

	public static int LOGLEVEL = 1;
	
	public static boolean VERBOSE = LOGLEVEL > 3;
	public static boolean WARN = LOGLEVEL > 2;
	public static boolean DEBUG = LOGLEVEL > 1;
	public static boolean ERROR = LOGLEVEL > 0;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}
	
	public void onLoggerBtnToggle(View view){
		ToggleButton tglButton = (ToggleButton) view;
		if(tglButton.isChecked()){
			Toast.makeText(this,
					"Starting to write flight log.", Toast.LENGTH_SHORT)
					.show();
			loggingTask.startLogging();
		}else{
			Toast.makeText(this,
					"Logging ending", Toast.LENGTH_SHORT)
					.show();
			loggingTask.endLogging();
		}
		
		
		
	}

//    @Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		getMenuInflater().inflate(R.menu.options_menu, menu);
//		return true;
//	}

	@Override
	protected NavDrawerActivityConfiguration getNavDrawerConfiguration() {
		NavDrawerActivityConfiguration navDrawer = getDefaultNavDrawerConfiguration();
		navDrawer.setMainLayout(R.layout.control_center);
		return navDrawer;
	}
   

}
