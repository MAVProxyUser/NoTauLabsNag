/**
 ******************************************************************************
 * @file       LogManagerActivty.java
 * @author     The OpenPilot Team, http://www.openpilot.org Copyright (C) 2012.
 * @brief      Shows the PFD activity.
 * @see        The GNU Public License (GPL) Version 3
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

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.comparator.LastModifiedFileComparator;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class LogManagerActivty extends Activity{
	
	final String TAG = "LogManagerActivty";
	final String LOGGER_DIR ="/OpenPilotGCS";
	
	private final List<String> fileList = new ArrayList<String>();
	private File[] fileArray;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.log_manager);
		
		fileList.clear();
		fileArray = getLogFilesFromSDCard();
		if (fileArray != null) {
			for(File file : fileArray){ 
				fileList.add(file.getName());
			}
		}

		ArrayAdapter<String> logFileListAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, fileList);
		
		getFileListView().setAdapter(logFileListAdapter);

		getFileListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
		    @Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		    	Log.d(TAG, fileArray[position].getAbsolutePath());

		    	Intent intent = new Intent(Intent.ACTION_SEND);
	               intent.setType("application/octet-stream");
	               intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"noreply@openpilot",""});
	               intent.putExtra(Intent.EXTRA_SUBJECT, "OpenPilot AndroidGCS log file");
	               
	               String message = fileArray[position].getName();
	               File f = null;
	               try {
	            	   f = new File(fileArray[position].getAbsolutePath());
	            	   message += " size = " + f.length();
	               } catch (Exception e) {
	            	   // TODO Auto-generated catch block
	            	   e.printStackTrace();
	               }
	               intent.putExtra(Intent.EXTRA_TEXT, message);
	               intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+ fileArray[position].getAbsolutePath()));
	               startActivity(Intent.createChooser(intent, "Choose app to send file:"));
		    }
		});
		
	}
	
	//! Return the file list view
	private ListView getFileListView() {
		return (ListView) findViewById(R.id.logger_file_list);
	}
	
	private File[] getLogFilesFromSDCard() {
		File sdCard = Environment.getExternalStorageDirectory();
		File logsDirectory = new File(sdCard, LOGGER_DIR);
		File logsList[] = logsDirectory.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				return filename.contains(".opl");
			}	
		});
		
		if (logsList == null)
			return logsList;

		// Reverse the list so more recent files are first
		Arrays.sort(logsList,LastModifiedFileComparator.LASTMODIFIED_REVERSE );
		
		return logsList;
	}


}
