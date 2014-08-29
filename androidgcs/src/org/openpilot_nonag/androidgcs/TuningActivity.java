package org.openpilot_nonag.androidgcs;

import org.openpilot_nonag.androidgcs.drawer.NavDrawerActivityConfiguration;
import org.openpilot_nonag.androidgcs.util.SmartSave;
import org.openpilot_nonag.androidgcs.views.ScrollBarView;
import org.openpilot_nonag.uavtalk.UAVDataObject;

import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;

public class TuningActivity extends ObjectManagerActivity {
	private final String TAG = TuningActivity.class.getSimpleName();

	private final boolean DEBUG = false;

	private SmartSave smartSave;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		// Attempt to hide the soft keyboard when control receives keyboard focus.
		//getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		
		super.onCreate(savedInstanceState);
		
	}

	@Override
	void onOPConnected() {
		super.onOPConnected();

		if (DEBUG)
			Log.d(TAG, "onOPConnected()");
		
		UAVDataObject stabilizationSettings;
		// Subscribe to updates from ManualControlCommand and show the values
		// for crude feedback

		stabilizationSettings = (UAVDataObject) objMngr.getObject("StabilizationSettingsBank1");
		
		smartSave = new SmartSave(objMngr, this,
				stabilizationSettings,
				(Button) findViewById(R.id.saveBtn),
				(Button) findViewById(R.id.applyBtn),
				(Button) findViewById(R.id.loadBtn));

		smartSave.addControlMapping((ScrollBarView) findViewById(R.id.rollRateKp), "RollRatePID", 0);
		smartSave.addControlMapping((ScrollBarView) findViewById(R.id.rollRateKi), "RollRatePID", 1);
		smartSave.addControlMapping((ScrollBarView) findViewById(R.id.pitchRateKp), "PitchRatePID", 0);
		smartSave.addControlMapping((ScrollBarView) findViewById(R.id.pitchRateKi), "PitchRatePID", 1);
		smartSave.addControlMapping((ScrollBarView) findViewById(R.id.rollKp), "RollPI", 0);
		smartSave.addControlMapping((ScrollBarView) findViewById(R.id.pitchKp), "PitchPI", 0);
		smartSave.addControlMapping((ScrollBarView) findViewById(R.id.rollRateKd), "RollRatePID", 2);
		smartSave.addControlMapping((ScrollBarView) findViewById(R.id.pitchRateKd), "PitchRatePID", 2);
		smartSave.fetchSettings(); // Robustly request an update of the settings
		smartSave.refreshSettingsDisplay();
	}

	@Override
	protected NavDrawerActivityConfiguration getNavDrawerConfiguration() {
		NavDrawerActivityConfiguration navDrawer = getDefaultNavDrawerConfiguration();
		navDrawer.setMainLayout(R.layout.tuning);
		return navDrawer;
	}

}
