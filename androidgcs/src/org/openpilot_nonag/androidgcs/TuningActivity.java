package org.openpilot_nonag.androidgcs;

import org.openpilot_nonag.androidgcs.R;
import org.openpilot_nonag.androidgcs.util.SmartSave;
import org.openpilot_nonag.androidgcs.views.ScrollBarView;
import org.openpilot_nonag.uavtalk.UAVDataObject;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

public class TuningActivity extends ObjectManagerActivity {
	private final String TAG = TuningActivity.class.getSimpleName();

	private final boolean DEBUG = false;

	private SmartSave smartSave;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tuning);
	}

	@Override
	void onOPConnected() {
		super.onOPConnected();

		if (DEBUG) Log.d(TAG, "onOPConnected()");
		UAVDataObject stabilizationSettings;
		// Subscribe to updates from ManualControlCommand and show the values for crude feedback
                try
                {
			stabilizationSettings = (UAVDataObject) objMngr.getObject("StabilizationSettingsBank1");
			smartSave = new SmartSave(objMngr, stabilizationSettings,
				(Button) findViewById(R.id.saveBtn),
				(Button) findViewById(R.id.applyBtn));

			smartSave.addControlMapping((ScrollBarView) findViewById(R.id.rollRateKp), "RollRatePID", 0);
			smartSave.addControlMapping((ScrollBarView) findViewById(R.id.rollRateKi), "RollRatePID", 1);
			smartSave.addControlMapping((ScrollBarView) findViewById(R.id.pitchRateKp), "PitchRatePID", 0);
			smartSave.addControlMapping((ScrollBarView) findViewById(R.id.pitchRateKi), "PitchRatePID", 1);
			smartSave.addControlMapping((ScrollBarView) findViewById(R.id.rollKp), "RollPI", 0);
			smartSave.addControlMapping((ScrollBarView) findViewById(R.id.pitchKp), "PitchPI", 0);
			smartSave.refreshSettingsDisplay();
                }
                catch (NullPointerException e)
                {
                        Toast.makeText(this, "Catching Nulls on UAVObjects, link may be failing" + e, Toast.LENGTH_SHORT).show();
                }
	}

}
