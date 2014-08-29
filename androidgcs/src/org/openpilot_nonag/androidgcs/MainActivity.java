package org.openpilot_nonag.androidgcs;

import org.openpilot_nonag.androidgcs.drawer.NavDrawerActivityConfiguration;
import org.openpilot_nonag.androidgcs.fragments.PFD;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends ObjectManagerActivity{
	
	private final String TAG = MainActivity.class.getSimpleName();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Only do this when null as the default on create will restore
		// the existing fragment after rotation
		if ( savedInstanceState == null ) {
			Fragment contentFrag;
			Bundle b = getIntent().getExtras();
			if (b == null) {
				contentFrag = new PFD();
				setTitle("PFD");
			} else {
				int id = b.getInt("ContentFrag");
				contentFrag = getFragmentById(id);

				String title = b.getString("ContentName");
				if (title != null)
					setTitle(title);
			}

			FragmentTransaction fragmentTransaction = getFragmentManager()
					.beginTransaction();
			fragmentTransaction.add(R.id.content_frame, contentFrag);
			fragmentTransaction.commit();
		}
		
	}

	@Override
	protected NavDrawerActivityConfiguration getNavDrawerConfiguration() {
		NavDrawerActivityConfiguration navDrawer = getDefaultNavDrawerConfiguration();
		navDrawer.setMainLayout(R.layout.drawer);
		return navDrawer;
	}

	
	
}
