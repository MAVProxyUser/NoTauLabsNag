package org.openpilot_nonag.androidgcs;

import android.os.Bundle;

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

		setContentView(R.layout.control_center);
		
	}



}
