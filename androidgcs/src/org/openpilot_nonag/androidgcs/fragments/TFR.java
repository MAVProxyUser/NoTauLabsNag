/**
 ******************************************************************************
 * @file       TFR.java
 * @author     The OpenPilot Team, http://www.openpilot.org Copyright (C) 2012.
 * @brief      The TFR display fragment
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

package org.openpilot_nonag.androidgcs.fragments;

import org.openpilot_nonag.androidgcs.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.webkit.*;
import android.content.SharedPreferences;
import android.view.ViewGroup;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Button;
import android.content.Context;
import android.view.View.OnClickListener;

public class TFR extends Activity {
	private Button button;
 
	public void onCreate(Bundle savedInstanceState) {
		final Context context = this;
 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
 
		button = (Button) findViewById(R.id.buttonUrl);
 
		button.setOnClickListener(new OnClickListener() {
 
		  @Override
		  public void onClick(View arg0) {
//		    Intent intent = new Intent(context, TfrActivity.class);
//		    startActivity(intent);
		  }
 
		});
 
	}

}
