/**
 ******************************************************************************
 * @file       TfrActivity.java
 * @author     The OpenPilot Team, http://www.openpilot.org Copyright (C) 2012.
 * @brief      Shows the Tfr activity.
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

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class TfrActivity extends Activity {
 
	private WebView webView;
 
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
 
		webView = (WebView) findViewById(R.id.webView1);

		webView.setWebViewClient(new WebViewClient() {
		        @Override
	        	public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            			// Handle the error
        		}

        		@Override
        		public boolean shouldOverrideUrlLoading(WebView view, String url) {
            			view.loadUrl(url);
            			return true;
        		}
    		});

		webView.loadUrl("http://tfr.faa.gov/tfr2/list.html");
 
	}
 
}
