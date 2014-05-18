/**
 ******************************************************************************
 * @file       Preferences.java
 * @author     The OpenPilot Team, http://www.openpilot.org Copyright (C) 2012.
 * @brief      Displays the preferences dialog.
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

import org.openpilot_nonag.androidgcs.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.os.Build;
import android.widget.Toast;

public class Preferences extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Display the fragment as the main content.

        	if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			Log.d("OLD_API","This stuff is older than HoneyComb! I need to write more code, sorry");
			Toast.makeText(this, "OLD AS DIRT!", Toast.LENGTH_SHORT).show();
			Toast.makeText(this, "Please wait on new code!", Toast.LENGTH_SHORT).show();
		//            onCreatePreferenceActivity();
	        //	      getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment()).commit();
        	} else {
        		getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment()).commit();
        	}
	}
	public static class PrefsFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			// Load the preferences from an XML resource
			addPreferencesFromResource(R.xml.preferences);
		}
	}
}
