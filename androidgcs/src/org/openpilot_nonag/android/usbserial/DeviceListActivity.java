package org.openpilot_nonag.android.android.usbserial;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TwoLineListItem;
import com.wilsonae.rtlsdrlib.HexDump;
import com.wilsonae.rtlsdrlib.UsbSerialDriver;
import com.wilsonae.rtlsdrlib.UsbSerialProber;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class DeviceListActivity extends Activity
{
  private static final int MESSAGE_REFRESH = 101;
  private static final long REFRESH_TIMEOUT_MILLIS = 9000L;
  private static final int SETTINGS_RESULT = 1;
  static boolean mEnable_agc = false;
  static int mGain = -100;
  static int mPpm_value = 0;
  private final String TAG = DeviceListActivity.class.getSimpleName();
  Context context;
  private ImageButton imageButton;
  private ArrayAdapter<DeviceEntry> mAdapter;
  private List<DeviceEntry> mEntries = new ArrayList();
  private final Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      switch (paramAnonymousMessage.what)
      {
      default:
        super.handleMessage(paramAnonymousMessage);
        return;
      case 101:
      }
      DeviceListActivity.this.refreshDeviceList();
      DeviceListActivity.this.mHandler.sendEmptyMessageDelayed(101, 9000L);
    }
  };
  private ListView mListView;
  private ProgressBar mProgressBar;
  private TextView mProgressBarTitle;
  private UsbManager mUsbManager;

  private void hideProgressBar()
  {
    this.mProgressBar.setVisibility(4);
  }

  private void refreshDeviceList()
  {
    showProgressBar();
    new AsyncTask()
    {
      protected List<DeviceListActivity.DeviceEntry> doInBackground(Void[] paramAnonymousArrayOfVoid)
      {
        Log.d(DeviceListActivity.this.TAG, "Refreshing device list ...");
        SystemClock.sleep(1000L);
        ArrayList localArrayList = new ArrayList();
        Iterator localIterator1 = DeviceListActivity.this.mUsbManager.getDeviceList().values().iterator();
        while (true)
        {
          if (!localIterator1.hasNext())
            return localArrayList;
          UsbDevice localUsbDevice = (UsbDevice)localIterator1.next();
          List localList = UsbSerialProber.probeSingleDevice(DeviceListActivity.this.mUsbManager, localUsbDevice);
          Log.d(DeviceListActivity.this.TAG, "Found usb device: " + localUsbDevice);
          if (localList.isEmpty())
          {
            Log.d(DeviceListActivity.this.TAG, "  - No UsbSerialDriver available.");
            try
            {
              localArrayList.add(new DeviceListActivity.DeviceEntry(localUsbDevice, null));
            }
            catch (IOException localIOException2)
            {
              Log.d(DeviceListActivity.this.TAG, "  error" + localIOException2.toString());
              localIOException2.printStackTrace();
            }
          }
          else
          {
            Iterator localIterator2 = localList.iterator();
            while (localIterator2.hasNext())
            {
              UsbSerialDriver localUsbSerialDriver = (UsbSerialDriver)localIterator2.next();
              Log.d(DeviceListActivity.this.TAG, "  + " + localUsbSerialDriver);
              try
              {
                localArrayList.add(new DeviceListActivity.DeviceEntry(localUsbDevice, localUsbSerialDriver));
              }
              catch (IOException localIOException1)
              {
                Log.d(DeviceListActivity.this.TAG, "  error" + localIOException1.toString());
                localIOException1.printStackTrace();
              }
            }
          }
        }
      }

      protected void onPostExecute(List<DeviceListActivity.DeviceEntry> paramAnonymousList)
      {
        DeviceListActivity.this.mEntries.clear();
        DeviceListActivity.this.mEntries.addAll(paramAnonymousList);
        DeviceListActivity.this.mAdapter.notifyDataSetChanged();
        TextView localTextView = DeviceListActivity.this.mProgressBarTitle;
        Object[] arrayOfObject = new Object[1];
        arrayOfObject[0] = Integer.valueOf(DeviceListActivity.this.mEntries.size());
        localTextView.setText(String.format("%s device(s) found", arrayOfObject));
        DeviceListActivity.this.hideProgressBar();
        Log.d(DeviceListActivity.this.TAG, "Done refreshing, " + DeviceListActivity.this.mEntries.size() + " entries found.");
      }
    }
    .execute(new Void[] { null });
  }

  private void showConsoleActivity(UsbSerialDriver paramUsbSerialDriver)
  {
    SerialConsoleActivity.show(this, paramUsbSerialDriver);
  }

  private void showProgressBar()
  {
    this.mProgressBar.setVisibility(0);
    this.mProgressBarTitle.setText(2131099650);
  }

  private void showUserSettings()
  {
    startActivity(new Intent(getApplicationContext(), UserSettingActivity.class));
  }

  void InitPreferences()
  {
    SharedPreferences localSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    mEnable_agc = localSharedPreferences.getBoolean("enable_agc", false);
    mGain = Integer.parseInt(localSharedPreferences.getString("gain_value", "-100"));
    mPpm_value = Integer.parseInt(localSharedPreferences.getString("ppm_value", "0"));
  }

  public void addListenerOnButton()
  {
    this.imageButton = ((ImageButton)findViewById(2131296259));
    this.imageButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        DeviceListActivity.this.showUserSettings();
        Toast.makeText(DeviceListActivity.this.getBaseContext(), "ImageButton is clicked!", 0).show();
      }
    });
  }

  public void addListenerOnButton2()
  {
    ((ImageButton)findViewById(2131296260)).setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(DeviceListActivity.this.context);
        localBuilder.setTitle("Reset Default config settings");
        localBuilder.setMessage("Click yes to reset default values").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
          {
            PreferenceManager.setDefaultValues(DeviceListActivity.this.context, 2130968577, true);
          }
        }).setNegativeButton("No", new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
          {
            paramAnonymous2DialogInterface.cancel();
          }
        });
        localBuilder.create().show();
      }
    });
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(2130903041);
    this.context = this;
    addListenerOnButton();
    addListenerOnButton2();
    InitPreferences();
    this.mUsbManager = ((UsbManager)getSystemService("usb"));
    this.mListView = ((ListView)findViewById(2131296264));
    this.mProgressBar = ((ProgressBar)findViewById(2131296262));
    this.mProgressBarTitle = ((TextView)findViewById(2131296261));
    this.mAdapter = new ArrayAdapter(this, 17367047, this.mEntries)
    {
      public View getView(int paramAnonymousInt, View paramAnonymousView, ViewGroup paramAnonymousViewGroup)
      {
        TwoLineListItem localTwoLineListItem;
        DeviceListActivity.DeviceEntry localDeviceEntry;
        if (paramAnonymousView == null)
        {
          localTwoLineListItem = (TwoLineListItem)((LayoutInflater)DeviceListActivity.this.getSystemService("layout_inflater")).inflate(17367044, null);
          localDeviceEntry = (DeviceListActivity.DeviceEntry)DeviceListActivity.this.mEntries.get(paramAnonymousInt);
          Object[] arrayOfObject = new Object[2];
          arrayOfObject[0] = HexDump.toHexString((short)localDeviceEntry.device.getVendorId());
          arrayOfObject[1] = HexDump.toHexString((short)localDeviceEntry.device.getProductId());
          String str1 = String.format("Vendor %s Product %s", arrayOfObject);
          localTwoLineListItem.getText1().setText(str1);
          if (localDeviceEntry.driver == null)
            break label145;
        }
        label145: for (String str2 = localDeviceEntry.driver.getClass().getSimpleName(); ; str2 = "No Driver")
        {
          localTwoLineListItem.getText2().setText(str2);
          return localTwoLineListItem;
          localTwoLineListItem = (TwoLineListItem)paramAnonymousView;
          break;
        }
      }
    };
    this.mListView.setAdapter(this.mAdapter);
    this.mListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
    {
      public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
      {
        Log.d(DeviceListActivity.this.TAG, "Pressed item " + paramAnonymousInt);
        if (paramAnonymousInt >= DeviceListActivity.this.mEntries.size())
        {
          Log.w(DeviceListActivity.this.TAG, "Illegal position.");
          return;
        }
        UsbSerialDriver localUsbSerialDriver = ((DeviceListActivity.DeviceEntry)DeviceListActivity.this.mEntries.get(paramAnonymousInt)).driver;
        if (localUsbSerialDriver == null)
        {
          Log.d(DeviceListActivity.this.TAG, "No driver.");
          return;
        }
        DeviceListActivity.this.showConsoleActivity(localUsbSerialDriver);
      }
    });
  }

  protected void onPause()
  {
    super.onPause();
    this.mHandler.removeMessages(101);
  }

  protected void onResume()
  {
    super.onResume();
    this.mHandler.sendEmptyMessage(101);
  }

  private static class DeviceEntry
  {
    public UsbDevice device;
    public UsbSerialDriver driver;

    DeviceEntry(UsbDevice paramUsbDevice, UsbSerialDriver paramUsbSerialDriver)
      throws IOException
    {
      this.device = paramUsbDevice;
      this.driver = paramUsbSerialDriver;
      if (paramUsbSerialDriver != null)
        paramUsbSerialDriver.settings(DeviceListActivity.mEnable_agc, DeviceListActivity.mGain, DeviceListActivity.mPpm_value);
    }
  }
}

/* Location:           /Users/kevinfinisterre/Desktop/ADSB_USB_playstore.jar
 * Qualified Name:     com.wilsonae.android.usbserial.DeviceListActivity
 * JD-Core Version:    0.6.2
 */
