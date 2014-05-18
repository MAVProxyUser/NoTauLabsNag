package org.openpilot_nonag.android.android.usbserial;

import android.os.Message;
import android.util.Log;
import org.osmdroid.tileprovider.util.SimpleInvalidationHandler;
import org.osmdroid.views.MapView;

class RequestCompleteHandler extends SimpleInvalidationHandler
{
  static int tileCount = 0;
  private MapView mMapView;

  public RequestCompleteHandler(MapView paramMapView)
  {
    super(paramMapView);
    this.mMapView = paramMapView;
  }

  public static int getTileCount()
  {
    return tileCount;
  }

  public void dispatchMessage(Message paramMessage)
  {
    super.dispatchMessage(paramMessage);
    Log.d("DEBUG", "HANDLER tilecount " + tileCount);
    tileCount = 1 + tileCount;
  }
}

/* Location:           /Users/kevinfinisterre/Desktop/ADSB_USB_playstore.jar
 * Qualified Name:     com.wilsonae.android.usbserial.RequestCompleteHandler
 * JD-Core Version:    0.6.2
 */
