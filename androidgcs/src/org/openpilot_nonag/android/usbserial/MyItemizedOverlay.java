package org.openpilot_nonag.android.android.usbserial;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import java.util.List;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedIconOverlay.OnItemGestureListener;
import org.osmdroid.views.overlay.OverlayItem;

class MyItemizedOverlay extends ItemizedIconOverlay<OverlayItem>
{
  protected static Context mContext;

  public MyItemizedOverlay(Context paramContext, List<OverlayItem> paramList)
  {
    super(paramContext, paramList, new ItemizedIconOverlay.OnItemGestureListener()
    {
      public boolean onItemLongPress(int paramAnonymousInt, OverlayItem paramAnonymousOverlayItem)
      {
        return false;
      }

      public boolean onItemSingleTapUp(int paramAnonymousInt, OverlayItem paramAnonymousOverlayItem)
      {
        return false;
      }
    });
    mContext = paramContext;
  }

  protected boolean onSingleTapUpHelper(int paramInt, OverlayItem paramOverlayItem, MapView paramMapView)
  {
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(mContext);
    localBuilder.setTitle(paramOverlayItem.getTitle());
    localBuilder.setMessage(paramOverlayItem.getSnippet());
    localBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        paramAnonymousDialogInterface.dismiss();
      }
    });
    localBuilder.show();
    return true;
  }
}

/* Location:           /Users/kevinfinisterre/Desktop/ADSB_USB_playstore.jar
 * Qualified Name:     com.wilsonae.android.usbserial.MyItemizedOverlay
 * JD-Core Version:    0.6.2
 */
