/**
 ******************************************************************************
 * @file       BluetoothDevicePreference.java
 * @author     The OpenPilot Team, http://www.openpilot.org Copyright (C) 2012.
 * @brief      A dialog in the preferences options that shows the paired BT
 *             devices.
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

package org.openpilot_nonag.androidgcs;

import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.util.Log;

public class BluetoothDevicePreference extends ListPreference {

    public BluetoothDevicePreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        BluetoothAdapter bta = BluetoothAdapter.getDefaultAdapter();
		if (bta == null)
		{
			Log.d("All bad", "BT is NOT supported...");
			return; // BT not supported
		}
		else
		{
			Log.d("All good", "BT is supported...");
		}
        Set<BluetoothDevice> pairedDevices = bta.getBondedDevices();
        CharSequence[] entries = new CharSequence[pairedDevices.size()];
        CharSequence[] entryValues = new CharSequence[pairedDevices.size()];
		if (pairedDevices.size() > 0) {
			int i = 0;
			for (BluetoothDevice dev : pairedDevices) {
				if (dev.getName() != null) {
					entries[i] = dev.getName();
					Log.d("All good", entries[i].toString());
				}
				else
				{
					entries[i] = "NoName";
					Log.d("All bad", entries[i].toString());
				}
				if (dev.getAddress() != null) {
					entryValues[i] = dev.getAddress();
					Log.d("All good", entryValues[i].toString());
					i++;
				}
				else
				{
					entryValues[i] = "00:11:22:33:44:55";
					Log.d("All bad", entryValues[i].toString());

				}
			}
        }
        setEntries(entries);
        setEntryValues(entryValues);
    }

    public BluetoothDevicePreference(Context context) {
        this(context, null);
    }

}
