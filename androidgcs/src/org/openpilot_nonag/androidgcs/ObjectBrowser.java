
package org.openpilot_nonag.androidgcs;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.openpilot_nonag.androidgcs.drawer.NavDrawerActivityConfiguration;
import org.openpilot_nonag.androidgcs.fragments.ObjectEditor;
import org.openpilot_nonag.androidgcs.fragments.ObjectViewer;
import org.openpilot_nonag.uavtalk.UAVDataObject;
import org.openpilot_nonag.uavtalk.UAVObject;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;

public class ObjectBrowser extends ObjectManagerActivity 
	implements OnSharedPreferenceChangeListener {

	private final String TAG = this.getClass().getSimpleName();
	int selected_index = -1;
	boolean connected;
	SharedPreferences prefs;
	ArrayAdapter<UAVDataObject> adapter;
	List<UAVDataObject> allObjects;

	enum DisplayMode {NONE, VIEW, EDIT};
	DisplayMode displayMode = DisplayMode.NONE;
	
	/**
	 * Display the fragment to edit this object
	 * @param id
	 */
	public void editObject(int id) {
		
		Log.d(TAG, "editObject("+id+")");

		displayMode = DisplayMode.EDIT;
		
		Bundle b = new Bundle();
		b.putString("org.openpilot_nonag.androidgcs.ObjectName", allObjects.get(selected_index).getName());
		b.putLong("org.openpilot_nonag.androidgcs.ObjectId", allObjects.get(selected_index).getObjID());
		b.putLong("org.openpilot_nonag.androidgcs.InstId", allObjects.get(selected_index).getInstID());

		Fragment newFrag = new ObjectEditor();
		newFrag.setArguments(b);
		
		FragmentTransaction trans = getFragmentManager().beginTransaction();
		trans.replace(R.id.object_information, newFrag);
		trans.addToBackStack(null);
		trans.commit();

	}
	
	/**
	 * Display the fragment to view this object
	 * @param id
	 */
	public void viewObject(int id) {
		Bundle b = new Bundle();
		b.putString("org.openpilot_nonag.androidgcs.ObjectName", allObjects.get(selected_index).getName());
		b.putLong("org.openpilot_nonag.androidgcs.ObjectId", allObjects.get(selected_index).getObjID());
		b.putLong("org.openpilot_nonag.androidgcs.InstId", allObjects.get(selected_index).getInstID());

		Fragment newFrag = new ObjectViewer();
		newFrag.setArguments(b);
		
		if (displayMode == DisplayMode.EDIT)
			getFragmentManager().popBackStack();

		FragmentTransaction trans = getFragmentManager().beginTransaction();
		trans.replace(R.id.object_information, newFrag);
		if (displayMode != DisplayMode.NONE)
			trans.addToBackStack(null);
		trans.commit();
		
		displayMode = DisplayMode.VIEW;
	}
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs.registerOnSharedPreferenceChangeListener(this);

		 ((CheckBox) findViewById(R.id.dataCheck)).setChecked(prefs.getBoolean("browser_show_data",true));
		 ((CheckBox) findViewById(R.id.settingsCheck)).setChecked(prefs.getBoolean("browser_show_settings",true));
		 
		 if (savedInstanceState != null) {
				displayMode = (DisplayMode) savedInstanceState.getSerializable("org.openpilot_nonag.browser.mode");
				selected_index = savedInstanceState.getInt("org.openpilot_nonag.browser.selected");
		 }
	}
	
	@Override
	protected NavDrawerActivityConfiguration getNavDrawerConfiguration() {
		NavDrawerActivityConfiguration navDrawer = getDefaultNavDrawerConfiguration();
		navDrawer.setMainLayout(R.layout.object_browser);
		return navDrawer;
	}
	
	@Override
	void onOPConnected() {
		super.onOPConnected();

		OnCheckedChangeListener checkListener = new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				prefs = PreferenceManager.getDefaultSharedPreferences(ObjectBrowser.this);
				Editor editor = prefs.edit();
				Log.d(TAG, "Writing settings");
				editor.putBoolean("browser_show_data", ((CheckBox) findViewById(R.id.dataCheck)).isChecked());
				editor.putBoolean("browser_show_settings", ((CheckBox) findViewById(R.id.settingsCheck)).isChecked());
				editor.commit();
			}
		};

		((CheckBox) findViewById(R.id.dataCheck)).setOnCheckedChangeListener(checkListener);
		((CheckBox) findViewById(R.id.settingsCheck)).setOnCheckedChangeListener(checkListener);

		updateList();
	}

	public void attachObjectView() {
		((Button) findViewById(R.id.editButton)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (selected_index > 0) {
					editObject(selected_index);
				}
			}
		});

		((Button) findViewById(R.id.object_load_button)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				UAVObject objPer = objMngr.getObject("ObjectPersistence");

				if (selected_index > 0 && objPer != null) {
					objPer.getField("Operation").setValue("Load");
					objPer.getField("Selection").setValue("SingleObject");
					Log.d(TAG,"Loading with object id: " + allObjects.get(selected_index).getObjID());
					objPer.getField("ObjectID").setValue(allObjects.get(selected_index).getObjID());
					objPer.getField("InstanceID").setValue(0);
					objPer.updated();

					allObjects.get(selected_index).updateRequested();
				}
			}
		});
	}

	/**
	 * Populate the list of UAVO objects based on the selected filter
	 */
	private void updateList() {

		boolean includeData = ((CheckBox) findViewById(R.id.dataCheck)).isChecked();
		boolean includeSettings = ((CheckBox) findViewById(R.id.settingsCheck)).isChecked();

		List<List<UAVDataObject>> allobjects = objMngr.getDataObjects();
		allObjects = new ArrayList<UAVDataObject>();
		ListIterator<List<UAVDataObject>> li = allobjects.listIterator();
		while(li.hasNext()) {
			List<UAVDataObject> objects = li.next();
			if(includeSettings && objects.get(0).isSettings())
				allObjects.addAll(objects);
			else if (includeData && !objects.get(0).isSettings())
				allObjects.addAll(objects);
		}

		ListView objects = (ListView) findViewById(R.id.object_list);
		adapter = new ArrayAdapter<UAVDataObject>(this, R.layout.object_browser_item, allObjects);
		objects.setAdapter(adapter);		
		objects.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				selected_index = position;
				viewObject(selected_index);
			}
		});
		
		if (selected_index >= 0) {
			objects.setSelection(selected_index);
			objects.setItemChecked(selected_index, true);
		}
	}


	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		Log.d(TAG, "Settings updated");
		if (key.equals("browser_show_data")) {
			((CheckBox) findViewById(R.id.dataCheck)).setChecked(prefs.getBoolean("browser_show_data",true));
			updateList();
		}
		if (key.equals("browser_show_settings")) {
			((CheckBox) findViewById(R.id.settingsCheck)).setChecked(prefs.getBoolean("browser_show_settings",true));
			updateList();
		}
	}
	
	@Override
	public void onSaveInstanceState (Bundle outState) {
		super.onSaveInstanceState(outState);
		
		outState.putSerializable("org.openpilot_nonag.browser.mode", displayMode);
		outState.putInt("org.openpilot_nonag.browser.selected", selected_index);
	}
}
