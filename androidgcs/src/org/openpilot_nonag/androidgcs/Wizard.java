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

import org.openpilot_nonag.androidgcs.R;
import org.openpilot_nonag.androidgcs.util.SmartSave;
import org.openpilot_nonag.androidgcs.views.DropdownBoxView;
import org.openpilot_nonag.uavtalk.UAVDataObject;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

public class Wizard extends ObjectManagerActivity {
        private final String TAG = Wizard.class.getSimpleName();

        private final boolean DEBUG = false;

        private SmartSave smartSave;

        /** Called when the activity is first created. */
        @Override
        public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.wizard);
        }

        @Override
        void onOPConnected() {
                super.onOPConnected();

                if (DEBUG) Log.d(TAG, "onOPConnected()");
                UAVDataObject systemSettings;
                // Subscribe to updates from systemsettings and show the values for crude feedback
                try
                {
                        systemSettings = (UAVDataObject) objMngr.getObject("SystemSettings");
                        smartSave = new SmartSave(objMngr, systemSettings,
                                (Button) findViewById(R.id.saveBtn),
                                (Button) findViewById(R.id.applyBtn));

                        smartSave.addControlMapping((DropdownBoxView) findViewById(R.id.airframeType), "AirframeType", 0);
                        smartSave.refreshSettingsDisplay();
                }
                catch (NullPointerException e)
                {
                        Toast.makeText(this, "Catching Nulls on UAVObjects, link may be failing", Toast.LENGTH_SHORT).show();
                }
        }

}
