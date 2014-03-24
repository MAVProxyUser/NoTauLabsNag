package org.openpilot_nonag.androidgcs;

import java.util.Timer;
import java.util.TimerTask;

//import com.google.android.maps.GeoPoint;
//import com.google.android.maps.MapFragment;
import android.support.v4.app.Fragment;
import com.google.android.gms.maps.model.LatLng; // replaces GeoPoint
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MyCustomMapView extends MapFragment {
    
    // Define the interface we will interact with from our Map
public interface OnLongpressListener {
    public void onLongpress(MapFragment view, LatLng longpressLocation);
}
 
/**
 * Time in ms before the OnLongpressListener is triggered.
 */
static final int LONGPRESS_THRESHOLD = 500;
 
/**
 * Keep a record of the center of the map, to know if the map
 * has been panned.
 */
private LatLng lastMapCenter;
 
private Timer longpressTimer = new Timer();
private MyCustomMapView.OnLongpressListener longpressListener;

/**
 * This method takes MotionEvents and decides whether or not
 * a longpress has been detected. This is the meat of the
 * OnLongpressListener.
 *
 * The Timer class executes a TimerTask after a given time,
 * and we start the timer when a finger touches the screen.
 *
 * We then listen for map movements or the finger being
 * removed from the screen. If any of these events occur
 * before the TimerTask is executed, it gets cancelled. Else
 * the listener is fired.
 * 
 * @param event
 */
private void handleLongpress(final MotionEvent event) 
{
     
}

}
