/**
 ******************************************************************************
 * @file       HomePage.java
 * @author     The OpenPilot Team, http://www.openpilot.org Copyright (C) 2012.
 * @brief      Main launch page for the Android GCS actitivies
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

import java.util.ArrayList;

import org.openpilot_nonag.androidgcs.grid.ImageAdapter;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

public class HomePage extends ObjectManagerActivity {
	
	protected static final String TAG = HomePage.class.getSimpleName();
	
	private ArrayList<Integer> listMenuItems;
	private ArrayList<Integer> listMenuIcons;
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) { // This is not getting called any more? http://stackoverflow.com/questions/5620033/onconfigurationchanged-not-getting-called
	    super.onConfigurationChanged(newConfig);

	    // Checks the orientation of the screen
	    if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
	        Toast.makeText(this, "Landscape mode active", Toast.LENGTH_SHORT).show();
	    } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
	        Toast.makeText(this, "Portrait mode active", Toast.LENGTH_SHORT).show();
	    }
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gcs_home);
		
		prepareMenuItems();
		
		GridView gridview = (GridView) findViewById(R.id.gridview);
	    gridview.setAdapter(new ImageAdapter(this,listMenuItems,listMenuIcons));
		
	    gridview.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	            
	            int menuPos = listMenuIcons.get(position);
	            
	            if(menuPos == R.drawable.ic_browser){
	            	startActivity(new Intent(HomePage.this, ObjectBrowser.class));
	            }else if(menuPos == R.drawable.ic_pfd){
	            	startActivity(new Intent(HomePage.this, PfdActivity.class));
	            }else if(menuPos == R.drawable.ic_logging){
	            	startActivity(new Intent(HomePage.this, Logger.class));
	            }else if(menuPos == R.drawable.ic_alarms){
	            	startActivity(new Intent(HomePage.this, SystemAlarmActivity.class));
	            }else if(menuPos == R.drawable.ic_tuning){
	            	startActivity(new Intent(HomePage.this, TuningActivity.class));
	            }else if(menuPos == R.drawable.ic_map){
	            	startActivity(new Intent(HomePage.this, UAVLocation.class));
	            }else if(menuPos == R.drawable.ic_tfr){
	            	startActivity(new Intent(HomePage.this, TfrActivity.class));
	            }else if(menuPos == R.drawable.ic_cc){
	            	startActivity(new Intent(HomePage.this, ControlCenterActivity.class));
	            }else{
	            	Log.e(TAG, "invalid menu selection!");
	            }
	            
	        }
	    });

	}
	
	public void prepareMenuItems()
    {
		listMenuItems = new ArrayList<Integer>();
 
		listMenuItems.add(R.string.object_browser_name);
		listMenuItems.add(R.string.pfd_name);
		listMenuItems.add(R.string.logger_name);
		listMenuItems.add(R.string.alarms);
		listMenuItems.add(R.string.tuning);
		listMenuItems.add(R.string.location_name);
		listMenuItems.add(R.string.tfr_name);
		listMenuItems.add(R.string.cc_name);
 
		listMenuIcons = new ArrayList<Integer>();
		listMenuIcons.add(R.drawable.ic_browser);
		listMenuIcons.add(R.drawable.ic_pfd);
		listMenuIcons.add(R.drawable.ic_logging); 
		listMenuIcons.add(R.drawable.ic_alarms);
		listMenuIcons.add(R.drawable.ic_tuning); 
		listMenuIcons.add(R.drawable.ic_map);
		listMenuIcons.add(R.drawable.ic_tfr); 
		listMenuIcons.add(R.drawable.ic_cc);
          
    }

}
