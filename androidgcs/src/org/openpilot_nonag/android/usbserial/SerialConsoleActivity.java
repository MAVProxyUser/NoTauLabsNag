package org.openpilot_nonag.android.android.usbserial;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.wilsonae.rtlsdrlib.SerialInputOutputManager;
import com.wilsonae.rtlsdrlib.SerialInputOutputManager.Listener;
import com.wilsonae.rtlsdrlib.UsbSerialDriver;
import com.wilsonae.rtlsdrlib.UsbSerialProber;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import org.osmdroid.ResourceProxy.string;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

public class SerialConsoleActivity extends Activity
{
  private static boolean DEBUG = false;
  private static final int MENU_ENRL_ID = 10;
  private static final int MENU_SEC_ID = 7;
  private static final int MENU_STREET_ID = 9;
  private static final int MENU_TAC_ID = 8;
  private static final int MENU_WAC_ID = 6;
  private static MapView mapView;
  private static UsbSerialDriver sDriver = null;
  private ConcurrentHashMap<Integer, aircraft> Aircrafts;
  private boolean GpsAvailable;
  private final int MAX_AVAILABLE = 1;
  private final int MODES_DATA_LEN = 262144;
  private final boolean MODES_DEBUG_BADCRC = false;
  private final boolean MODES_DEBUG_DEMOD = false;
  private final boolean MODES_DEBUG_DEMODERR = false;
  private final boolean MODES_DEBUG_DISPLAY = false;
  private final boolean MODES_DEBUG_GOODCRC = false;
  private final boolean MODES_DEBUG_NOPREAMBLE = false;
  private final int MODES_DEBUG_NOPREAMBLE_LEVEL = 25;
  private final boolean MODES_DEBUG_STATS = false;
  private final int MODES_FULL_LEN = 120;
  private final int MODES_ICAO_CACHE_LEN = 1024;
  private final int MODES_ICAO_CACHE_TTL = 60;
  private final int MODES_INTERACTIVE_REFRESH_TIME = 1500;
  private int MODES_INTERACTIVE_TTL = 60;
  private final int MODES_LONG_MSG_BITS = 112;
  private final int MODES_LONG_MSG_BYTES = 14;
  private final int MODES_PREAMBLE_US = 8;
  private final int MODES_SHORT_MSG_BITS = 56;
  private final int MODES_SHORT_MSG_BYTES = 7;
  private final int MODES_UNIT_FEET = 0;
  private final int MODES_UNIT_METERS = 1;
  private final int ONE_SECOND = 1000;
  private final Semaphore ReadSemaphore = new Semaphore(1, true);
  private final String TAG = SerialConsoleActivity.class.getSimpleName();
  boolean aggressive = false;
  final String[] ca_str = { "Level 1 (Survillance Only)", "Level 2 (DF0,4,5,11)", "Level 3 (DF0,4,5,11,20,21)", "Level 4 (DF0,4,5,11,20,21,24)", "Level 2+3+4 (DF0,4,5,11,20,21,24,code7 - is on ground)", "Level 2+3+4 (DF0,4,5,11,20,21,24,code7 - is on airborne)", "Level 2+3+4 (DF0,4,5,11,20,21,24,code7)", "Level 7 ???" };
  boolean check_crc = true;
  byte[] data = new byte[262144];
  int data_len = 262620;
  boolean data_ready = false;
  boolean exit = false;
  private boolean fix_errors = true;
  final String[] fs_str = { "Normal, Airborne", "Normal, On the ground", "ALERT,  Airborne", "ALERT,  On the ground", "ALERT & Special Position Identification. Airborne or Ground", "Special Position Identification. Airborne or Ground", "Value 6 is not assigned", "Value 7 is not assigned" };
  private Handler handler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      SerialConsoleActivity.this.mDumpTextView = ((TextView)SerialConsoleActivity.this.findViewById(2131296273));
      String str = paramAnonymousMessage.getData().getString("myKey");
      SerialConsoleActivity.this.mDumpTextView.setText(str);
      SerialConsoleActivity.this.mScrollView.smoothScrollTo(0, SerialConsoleActivity.this.mDumpTextView.getBottom());
    }
  };
  int[] icao_cache = new int[2048];
  private long interactive_last_update = 0L;
  private int interactive_rows = 10;
  double[] lat_s = new double[60];
  boolean lat_sf = false;
  private Context mContext;
  private TextView mDumpTextView;
  private List<DeviceEntry> mEntries = new ArrayList();
  private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();
  private final SerialInputOutputManager.Listener mListener = new SerialInputOutputManager.Listener()
  {
    public void onNewData(byte[] paramAnonymousArrayOfByte)
    {
      SerialConsoleActivity.this.numRead = paramAnonymousArrayOfByte.length;
      SerialConsoleActivity.this.data_ready = false;
      try
      {
        SerialConsoleActivity.this.ReadSemaphore.acquire();
        System.arraycopy(paramAnonymousArrayOfByte, 0, SerialConsoleActivity.this.data, 0, SerialConsoleActivity.this.numRead);
        SerialConsoleActivity.this.data_ready = true;
        SerialConsoleActivity.this.ReadSemaphore.release();
        return;
      }
      catch (InterruptedException localInterruptedException)
      {
        localInterruptedException.printStackTrace();
        Log.e(SerialConsoleActivity.this.TAG, "Error acquire ReadSemaphor " + localInterruptedException.toString());
      }
    }

    public void onRunError(Exception paramAnonymousException)
    {
      Log.d(SerialConsoleActivity.this.TAG, "Runner stopped.");
    }
  };
  private MyItemizedOverlay mMyTrafficOverlay;
  private ScrollView mScrollView;
  private SerialInputOutputManager mSerialIoManager;
  private UsbManager mUsbManager;
  private char[] maglut = new char[33282];
  char[] magnitude = new char[262144];
  private MapController mapController;
  private boolean mapOnlyView = true;
  private boolean metric = false;
  GeoPoint myCurrentPosition;
  private MyLocationNewOverlay myLocationOverlay;
  int numRead = 0;
  boolean onlyaddr = false;
  private boolean raw = false;
  char[] squares = new char[257];
  int stat_badcrc = 0;
  int stat_demodulated = 0;
  int stat_fixed = 0;
  int stat_goodcrc = 0;
  int stat_http_requests = 0;
  int stat_out_of_phase = 0;
  int stat_sbs_connections = 0;
  int stat_single_bit_fix = 0;
  int stat_two_bits_fix = 0;
  int stat_valid_preamble = 0;

  private GeoPoint GetMyLocation()
  {
    GeoPoint localGeoPoint = new GeoPoint(33.942001F, -118.408F);
    LocationManager localLocationManager;
    if (isGpsAvailable())
    {
      localLocationManager = (LocationManager)getSystemService("location");
      if (localLocationManager.isProviderEnabled("gps"))
      {
        Location localLocation1 = localLocationManager.getLastKnownLocation("gps");
        if (localLocation1 == null)
          break label74;
        localGeoPoint = new GeoPoint(localLocation1.getLatitude(), localLocation1.getLongitude());
      }
    }
    label74: Location localLocation2;
    do
    {
      do
        return localGeoPoint;
      while (!localLocationManager.isProviderEnabled("network"));
      localLocation2 = localLocationManager.getLastKnownLocation("network");
    }
    while (localLocation2 == null);
    return new GeoPoint(localLocation2.getLatitude(), localLocation2.getLongitude());
  }

  private boolean ICAOAddressWasRecentlySeen(int paramInt)
  {
    int i = ICAOCacheHashAddress(paramInt);
    int j = this.icao_cache[(i * 2)];
    int k = this.icao_cache[(1 + i * 2)];
    int m = (int)(System.currentTimeMillis() / 1000L);
    return (j != 0) && (j == paramInt) && (m - k <= 60);
  }

  private int ICAOCacheHashAddress(int paramInt)
  {
    int i = 73244475 * (paramInt ^ paramInt >> 16);
    int j = 73244475 * (i ^ i >> 16);
    return 0x3FF & (j ^ j >> 16);
  }

  static int SepAP112(byte[] paramArrayOfByte)
  {
    return 0xFFFFFF & (((0xFF & paramArrayOfByte[11]) << 8 | 0xFF & paramArrayOfByte[12]) << 8 | 0xFF & paramArrayOfByte[13]);
  }

  static int SepAP112(char[] paramArrayOfChar)
  {
    return 0xFFFFFF & (((0xFF & paramArrayOfChar[11]) << '\b' | 0xFF & paramArrayOfChar[12]) << 8 | 0xFF & paramArrayOfChar[13]);
  }

  static int SepAP56(byte[] paramArrayOfByte)
  {
    return 0xFFFFFF & (((0xFF & paramArrayOfByte[5]) << 8 | 0xFF & paramArrayOfByte[6]) << 8 | 0xFF & paramArrayOfByte[7]);
  }

  static int SepAP56(char[] paramArrayOfChar)
  {
    return 0xFFFFFF & (((0xFF & paramArrayOfChar[5]) << '\b' | 0xFF & paramArrayOfChar[6]) << 8 | 0xFF & paramArrayOfChar[7]);
  }

  private void addRecentlySeenICAOAddr(int paramInt)
  {
    int i = ICAOCacheHashAddress(paramInt);
    this.icao_cache[(i * 2)] = paramInt;
    this.icao_cache[(1 + i * 2)] = ((int)(System.currentTimeMillis() / 1000L));
  }

  private void backgroundTasks()
  {
    long l = System.currentTimeMillis();
    try
    {
      this.ReadSemaphore.acquire();
      if (!this.exit)
      {
        int i = computeMagnitudeVector(this.data, this.numRead);
        detectModeS(this.magnitude, i);
      }
      this.ReadSemaphore.release();
      if (Math.abs(l - this.interactive_last_update) > 1500L)
      {
        this.interactive_last_update = System.currentTimeMillis();
        interactiveRemoveStaleAircrafts();
        interactiveShowData();
        interactiveUpdateMap();
      }
      return;
    }
    catch (InterruptedException localInterruptedException)
    {
      while (true)
      {
        localInterruptedException.printStackTrace();
        Log.e("ReadSemaphore", "ReadSemaphore error" + localInterruptedException.toString());
      }
    }
  }

  private boolean bruteForceAP(char[] paramArrayOfChar, modesMessage parammodesMessage)
  {
    char[] arrayOfChar = new char[14];
    int i = parammodesMessage.msgtype;
    int j = parammodesMessage.msgbits;
    boolean bool2;
    if ((i != 0) && (i != 4) && (i != 5) && (i != 16) && (i != 20) && (i != 21))
    {
      bool2 = false;
      if (i != 24);
    }
    else
    {
      int k = -1 + j / 8;
      System.arraycopy(paramArrayOfChar, 0, arrayOfChar, 0, j / 8);
      int m = modesChecksum(arrayOfChar, j);
      arrayOfChar[k] = ((char)(arrayOfChar[k] ^ m & 0xFF));
      int n = k - 1;
      arrayOfChar[n] = ((char)(arrayOfChar[n] ^ 0xFF & m >> 8));
      int i1 = k - 2;
      arrayOfChar[i1] = ((char)(arrayOfChar[i1] ^ 0xFF & m >> 16));
      boolean bool1 = ICAOAddressWasRecentlySeen(0xFFFFFF & (0xFF0000 & arrayOfChar[(k - 2)] << '\020' | 0xFF00 & arrayOfChar[(k - 1)] << '\b' | 0xFF & arrayOfChar[k]));
      bool2 = false;
      if (bool1)
      {
        parammodesMessage.aa1 = ((byte)arrayOfChar[(k - 2)]);
        parammodesMessage.aa2 = ((byte)arrayOfChar[(k - 1)]);
        parammodesMessage.aa3 = ((byte)arrayOfChar[k]);
        bool2 = true;
      }
    }
    return bool2;
  }

  private void build_mag_lut()
  {
    int i = 0;
    if (i > 128)
      return;
    for (int j = 0; ; j++)
    {
      if (j > 128)
      {
        i++;
        break;
      }
      this.maglut[(j + i * 129)] = ((char)(int)Math.round(360.0D * Math.sqrt(i * i + j * j)));
    }
  }

  private int computeMagnitudeVector(byte[] paramArrayOfByte, int paramInt)
  {
    int i = 0;
    if (i >= paramInt)
      return paramInt / 2;
    int j;
    if (paramArrayOfByte[i] >= 0)
    {
      j = paramArrayOfByte[i];
      label22: if (paramArrayOfByte[(i + 1)] < 0)
        break label111;
    }
    label111: for (int k = paramArrayOfByte[(i + 1)]; ; k = 256 + paramArrayOfByte[(i + 1)])
    {
      int m = j - 127;
      int n = k - 127;
      if (m < 0)
        m = -m;
      if (n < 0)
        n = -n;
      this.magnitude[(i / 2)] = this.maglut[(n + m * 129)];
      i += 2;
      break;
      j = 256 + paramArrayOfByte[i];
      break label22;
    }
  }

  private int cprModFunction(int paramInt1, int paramInt2)
  {
    int i = paramInt1 % paramInt2;
    if (i < 0)
      i += paramInt2;
    return i;
  }

  private int decodeAC12Field(modesMessage parammodesMessage)
  {
    int i = 0x1 & parammodesMessage.msg[5];
    int j = 0;
    if (i != 0)
    {
      parammodesMessage.unit = 0;
      j = -1000 + 25 * (parammodesMessage.msg[5] >> '\001' << 4 | (0xF0 & parammodesMessage.msg[5]) >> '\004');
    }
    return j;
  }

  private int decodeAC13Field(modesMessage parammodesMessage)
  {
    int i = 0x40 & parammodesMessage.msg[3];
    int j = 0x10 & parammodesMessage.msg[3];
    if (i == 0)
    {
      parammodesMessage.unit = 0;
      int k = j & 0x10;
      int m = 0;
      if (k == 16)
        m = -1000 + 25 * ((0x1F & parammodesMessage.msg[2]) << '\006' | (0x80 & parammodesMessage.msg[3]) >> '\002' | (0x20 & parammodesMessage.msg[3]) >> '\001' | 0xF & parammodesMessage.msg[3]);
      return m;
    }
    parammodesMessage.unit = 1;
    return 0;
  }

  private void decodeCPR(aircraft paramaircraft)
  {
    double d1 = paramaircraft.even_cprlat;
    double d2 = paramaircraft.odd_cprlat;
    double d3 = paramaircraft.even_cprlon;
    double d4 = paramaircraft.odd_cprlon;
    int i = (int)Math.floor(0.5D + (59.0D * d1 - 60.0D * d2) / 131072.0D);
    double d5 = 6.0D * (cprModFunction(i, 60) + d1 / 131072.0D);
    double d6 = 6.101694915254237D * (cprModFunction(i, 59) + d2 / 131072.0D);
    if (d5 >= 270.0D)
      d5 -= 360.0D;
    if (d6 >= 270.0D)
      d6 -= 360.0D;
    if (cprNLFunction(d5) != cprNLFunction(d6));
    while (true)
    {
      return;
      if (paramaircraft.even_cprtime > paramaircraft.odd_cprtime)
      {
        int m = cprNFunction(d5, 0);
        int n = (int)Math.floor(0.5D + (d3 * (-1 + cprNLFunction(d5)) - d4 * cprNLFunction(d5)) / 131072.0D);
        paramaircraft.lon = (cprDlonFunction(d5, 0) * (cprModFunction(n, m) + d3 / 131072.0D));
      }
      for (paramaircraft.lat = d5; paramaircraft.lon > 180.0D; paramaircraft.lat = d6)
      {
        paramaircraft.lon -= 360.0D;
        return;
        int j = cprNFunction(d6, 1);
        int k = (int)Math.floor(0.5D + (d3 * (-1 + cprNLFunction(d6)) - d4 * cprNLFunction(d6)) / 131072.0D);
        paramaircraft.lon = (cprDlonFunction(d6, 1) * (cprModFunction(k, j) + d4 / 131072.0D));
      }
    }
  }

  private modesMessage decodeModesMessage(char[] paramArrayOfChar)
  {
    modesMessage localmodesMessage = new modesMessage();
    char[] arrayOfChar = { 63, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 63, 63, 63, 63, 63, 32, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 63, 63, 63, 63, 63, 63 };
    new byte[] { -115, 70, -110, -40, 96, -113, -80, -32, 12, -106, 36, -14, 66, -107 };
    int i = 0;
    boolean bool;
    label793: label878: int k;
    label960: int m;
    if (i >= 14)
    {
      localmodesMessage.msgtype = (0x1F & localmodesMessage.msg[0] >> '\003');
      localmodesMessage.msgbits = modesMessageLenByType(localmodesMessage.msgtype);
      localmodesMessage.aa1 = ((byte)localmodesMessage.msg[1]);
      localmodesMessage.aa2 = ((byte)localmodesMessage.msg[2]);
      localmodesMessage.aa3 = ((byte)localmodesMessage.msg[3]);
      localmodesMessage.addr = (0xFF0000 & localmodesMessage.aa1 << 16 | 0xFF00 & localmodesMessage.aa2 << 8 | 0xFF & localmodesMessage.aa3);
      localmodesMessage.metype = (0x1F & localmodesMessage.msg[4] >> '\003');
      localmodesMessage.mesub = (0x7 & localmodesMessage.msg[4]);
      localmodesMessage.time = System.currentTimeMillis();
      localmodesMessage.crc = (0xFF0000 & localmodesMessage.msg[(-3 + localmodesMessage.msgbits / 8)] << '\020' | 0xFF00 & localmodesMessage.msg[(-2 + localmodesMessage.msgbits / 8)] << '\b' | 0xFF & localmodesMessage.msg[(-1 + localmodesMessage.msgbits / 8)]);
      int j = modesChecksum(localmodesMessage.msg, localmodesMessage.msgbits);
      localmodesMessage.errorbit = -1;
      if (localmodesMessage.crc != j)
        break label1674;
      bool = true;
      localmodesMessage.crcok = bool;
      if ((!localmodesMessage.crcok) && (this.fix_errors) && ((localmodesMessage.msgtype == 11) || (localmodesMessage.msgtype == 17)))
      {
        int i5 = fixSingleBitErrors(localmodesMessage.msg, localmodesMessage.msgbits);
        localmodesMessage.errorbit = i5;
        if (i5 == -1)
          break label1680;
        localmodesMessage.crc = modesChecksum(localmodesMessage.msg, localmodesMessage.msgbits);
        localmodesMessage.crcok = true;
      }
      localmodesMessage.ca = (0x7 & localmodesMessage.msg[0]);
      localmodesMessage.fs = (0x7 & localmodesMessage.msg[0]);
      localmodesMessage.dr = (0x1F & localmodesMessage.msg[1] >> '\003');
      localmodesMessage.um = ((0x7 & localmodesMessage.msg[1]) << '\003' | localmodesMessage.msg[2] >> '\005');
      if (localmodesMessage.msg[2] < 0)
        break label1743;
      k = localmodesMessage.msg[2];
      if (localmodesMessage.msg[3] < 0)
        break label1758;
      m = localmodesMessage.msg[3];
      label977: int n = (m & 0x80) >> 5 | (k & 0x2) >> 0 | (k & 0x8) >> 3;
      int i1 = (m & 0x2) << 1 | (m & 0x8) >> 2 | (m & 0x20) >> 5;
      int i2 = (k & 0x1) << 2 | (k & 0x4) >> 1 | (k & 0x10) >> 4;
      localmodesMessage.identity = (((m & 0x1) << 2 | (m & 0x4) >> 1 | (m & 0x10) >> 4) + (n * 1000 + i1 * 100 + i2 * 10));
      if ((localmodesMessage.msgtype == 11) || (localmodesMessage.msgtype == 17))
        break label1781;
      if (!bruteForceAP(paramArrayOfChar, localmodesMessage))
        break label1773;
      localmodesMessage.crcok = true;
      label1125: if ((localmodesMessage.msgtype == 0) || (localmodesMessage.msgtype == 4) || (localmodesMessage.msgtype == 16) || (localmodesMessage.msgtype == 20))
        localmodesMessage.altitude = decodeAC13Field(localmodesMessage);
      if (localmodesMessage.msgtype == 17)
      {
        if ((localmodesMessage.metype < 1) || (localmodesMessage.metype > 4))
          break label1807;
        localmodesMessage.aircraft_type = (-1 + localmodesMessage.metype);
        localmodesMessage.flight = "";
        String str1 = localmodesMessage.flight;
        StringBuilder localStringBuilder1 = new StringBuilder(String.valueOf(str1));
        localmodesMessage.flight = arrayOfChar[(0x3F & localmodesMessage.msg[5] >> '\002')];
        String str2 = localmodesMessage.flight;
        StringBuilder localStringBuilder2 = new StringBuilder(String.valueOf(str2));
        localmodesMessage.flight = arrayOfChar[(0x3F & ((0x3 & localmodesMessage.msg[5]) << '\004' | localmodesMessage.msg[6] >> '\004'))];
        String str3 = localmodesMessage.flight;
        StringBuilder localStringBuilder3 = new StringBuilder(String.valueOf(str3));
        localmodesMessage.flight = arrayOfChar[(0x3F & ((0xF & localmodesMessage.msg[6]) << '\002' | localmodesMessage.msg[7] >> '\006'))];
        String str4 = localmodesMessage.flight;
        StringBuilder localStringBuilder4 = new StringBuilder(String.valueOf(str4));
        localmodesMessage.flight = arrayOfChar[(0x3F & localmodesMessage.msg[7])];
        String str5 = localmodesMessage.flight;
        StringBuilder localStringBuilder5 = new StringBuilder(String.valueOf(str5));
        localmodesMessage.flight = arrayOfChar[(0x3F & localmodesMessage.msg[8] >> '\002')];
        String str6 = localmodesMessage.flight;
        StringBuilder localStringBuilder6 = new StringBuilder(String.valueOf(str6));
        localmodesMessage.flight = arrayOfChar[(0x3F & ((0x3 & localmodesMessage.msg[8]) << '\004' | localmodesMessage.msg[9] >> '\004'))];
        String str7 = localmodesMessage.flight;
        StringBuilder localStringBuilder7 = new StringBuilder(String.valueOf(str7));
        localmodesMessage.flight = arrayOfChar[(0x3F & ((0xF & localmodesMessage.msg[9]) << '\002' | localmodesMessage.msg[10] >> '\006'))];
        String str8 = localmodesMessage.flight;
        StringBuilder localStringBuilder8 = new StringBuilder(String.valueOf(str8));
        localmodesMessage.flight = arrayOfChar[(0x3F & localmodesMessage.msg[10])];
        Log.d("Flt", "Flight:" + localmodesMessage.flight);
      }
    }
    while (true)
    {
      localmodesMessage.phase_corrected = 0;
      return localmodesMessage;
      localmodesMessage.msg[i] = paramArrayOfChar[i];
      i++;
      break;
      label1674: bool = false;
      break label793;
      label1680: if ((!this.aggressive) || (localmodesMessage.msgtype != 17))
        break label878;
      int i6 = fixTwoBitsErrors(paramArrayOfChar, localmodesMessage.msgbits);
      localmodesMessage.errorbit = i6;
      if (i6 == -1)
        break label878;
      localmodesMessage.crc = modesChecksum(localmodesMessage.msg, localmodesMessage.msgbits);
      localmodesMessage.crcok = true;
      break label878;
      label1743: k = 'Ā' + localmodesMessage.msg[2];
      break label960;
      label1758: m = 'Ā' + localmodesMessage.msg[3];
      break label977;
      label1773: localmodesMessage.crcok = false;
      break label1125;
      label1781: if ((!localmodesMessage.crcok) || (localmodesMessage.errorbit != -1))
        break label1125;
      addRecentlySeenICAOAddr(localmodesMessage.addr);
      break label1125;
      label1807: if ((localmodesMessage.metype >= 9) && (localmodesMessage.metype <= 18))
      {
        localmodesMessage.fflag = (0x4 & localmodesMessage.msg[6]);
        localmodesMessage.tflag = (0x8 & localmodesMessage.msg[6]);
        localmodesMessage.altitude = decodeAC12Field(localmodesMessage);
        localmodesMessage.raw_latitude = ((0x3 & localmodesMessage.msg[6]) << '\017' | localmodesMessage.msg[7] << '\007' | localmodesMessage.msg[8] >> '\001');
        localmodesMessage.raw_longitude = ((0x1 & localmodesMessage.msg[8]) << '\020' | localmodesMessage.msg[9] << '\b' | localmodesMessage.msg[10]);
      }
      else if ((localmodesMessage.metype == 19) && (localmodesMessage.mesub >= 1) && (localmodesMessage.mesub <= 4))
      {
        if ((localmodesMessage.mesub == 1) || (localmodesMessage.mesub == 2))
        {
          localmodesMessage.ew_dir = ((0x4 & localmodesMessage.msg[5]) >> '\002');
          localmodesMessage.ew_velocity = ((0x3 & localmodesMessage.msg[5]) << '\b' | localmodesMessage.msg[6]);
          localmodesMessage.ns_dir = ((0x80 & localmodesMessage.msg[7]) >> '\007');
          localmodesMessage.ns_velocity = ((0x7F & localmodesMessage.msg[7]) << '\003' | (0xE0 & localmodesMessage.msg[8]) >> '\005');
          localmodesMessage.vert_rate_source = ((0x10 & localmodesMessage.msg[8]) >> '\004');
          localmodesMessage.vert_rate_sign = ((0x8 & localmodesMessage.msg[8]) >> '\005');
          localmodesMessage.vert_rate = ((0x7 & localmodesMessage.msg[8]) << '\006' | (0xFC & localmodesMessage.msg[9]) >> '\002');
          localmodesMessage.velocity = ((int)Math.sqrt(localmodesMessage.ns_velocity * localmodesMessage.ns_velocity + localmodesMessage.ew_velocity * localmodesMessage.ew_velocity));
          if (localmodesMessage.velocity != 0)
          {
            int i3 = localmodesMessage.ew_velocity;
            int i4 = localmodesMessage.ns_velocity;
            if (localmodesMessage.ew_dir != 0)
              i3 *= -1;
            if (localmodesMessage.ns_dir != 0)
              i4 *= -1;
            localmodesMessage.heading = ((int)(360.0D * Math.atan2(i3, i4) / 6.283185307179586D));
            if (localmodesMessage.heading < 0)
              localmodesMessage.heading = (360 + localmodesMessage.heading);
          }
          else
          {
            localmodesMessage.heading = 0;
          }
        }
        else if ((localmodesMessage.mesub == 3) || (localmodesMessage.mesub == 4))
        {
          localmodesMessage.heading_is_valid = (0x4 & paramArrayOfChar[5]);
          localmodesMessage.heading = ((int)(2.8125D * ((0x3 & paramArrayOfChar[5]) << '\005' | paramArrayOfChar[6] >> '\003')));
        }
      }
    }
  }

  private void detectModeS(char[] paramArrayOfChar, int paramInt)
  {
    char[] arrayOfChar1 = new char[112];
    char[] arrayOfChar2 = new char[56];
    char[] arrayOfChar3 = new char['à'];
    int i = 0;
    int j = 0;
    if (j >= paramInt)
      return;
    if (i == 0)
      if ((paramArrayOfChar[j] > paramArrayOfChar[(j + 1)]) && (paramArrayOfChar[(j + 1)] < paramArrayOfChar[(j + 2)]) && (paramArrayOfChar[(j + 2)] > paramArrayOfChar[(j + 3)]) && (paramArrayOfChar[(j + 3)] < paramArrayOfChar[j]) && (paramArrayOfChar[(j + 4)] < paramArrayOfChar[j]) && (paramArrayOfChar[(j + 5)] < paramArrayOfChar[j]) && (paramArrayOfChar[(j + 6)] < paramArrayOfChar[j]) && (paramArrayOfChar[(j + 7)] > paramArrayOfChar[(j + 8)]) && (paramArrayOfChar[(j + 8)] < paramArrayOfChar[(j + 9)]) && (paramArrayOfChar[(j + 9)] > paramArrayOfChar[(j + 6)]));
    while (true)
    {
      j++;
      break;
      int i8 = (paramArrayOfChar[j] + paramArrayOfChar[(j + 2)] + paramArrayOfChar[(j + 7)] + paramArrayOfChar[(j + 9)]) / 6;
      if ((paramArrayOfChar[(j + 4)] < i8) && (paramArrayOfChar[(j + 5)] < i8) && (paramArrayOfChar[(j + 11)] < i8) && (paramArrayOfChar[(j + 12)] < i8) && (paramArrayOfChar[(j + 13)] < i8) && (paramArrayOfChar[(j + 14)] < i8))
      {
        this.stat_valid_preamble = (1 + this.stat_valid_preamble);
        if (i != 0)
        {
          System.arraycopy(paramArrayOfChar, j + 16, arrayOfChar3, 0, arrayOfChar3.length);
          if ((j != 0) && (detectOutOfPhase(paramArrayOfChar, j) != 0))
          {
            applyPhaseCorrection(paramArrayOfChar, j);
            this.stat_out_of_phase = (1 + this.stat_out_of_phase);
          }
        }
        int k = 0;
        int m = 0;
        int i3;
        label390: int i4;
        int i5;
        if (m >= 224)
        {
          if (i != 0)
            System.arraycopy(arrayOfChar3, 0, paramArrayOfChar, j + 16, arrayOfChar3.length);
          i3 = 0;
          if (i3 < 112)
            break label582;
          i4 = modesMessageLenByType(0x1F & arrayOfChar2[0] >> '\003') / 8;
          i5 = 0;
        }
        for (int i6 = 0; ; i6 += 2)
        {
          if (i6 >= 2 * (i4 * 8))
          {
            if (i5 / (i4 * 4) >= 2550)
              break label727;
            i = 0;
            break;
            int n = paramArrayOfChar[(16 + (j + m))];
            int i1 = paramArrayOfChar[(1 + (16 + (j + m)))];
            int i2 = n - i1;
            if (i2 < 0)
              i2 = -i2;
            if ((m > 0) && (i2 < 256))
              arrayOfChar1[(m / 2)] = arrayOfChar1[(-1 + m / 2)];
            while (true)
            {
              m += 2;
              break;
              if (n == i1)
              {
                arrayOfChar1[(m / 2)] = '\002';
                if (m < 112)
                  k++;
              }
              else if (n > i1)
              {
                arrayOfChar1[(m / 2)] = '\001';
              }
              else
              {
                arrayOfChar1[(m / 2)] = '\000';
              }
            }
            label582: arrayOfChar2[(i3 / 8)] = ((char)(0x80 & arrayOfChar1[i3] << '\007' | 0x40 & arrayOfChar1[(i3 + 1)] << '\006' | 0x20 & arrayOfChar1[(i3 + 2)] << '\005' | 0x10 & arrayOfChar1[(i3 + 3)] << '\004' | 0x8 & arrayOfChar1[(i3 + 4)] << '\003' | 0x4 & arrayOfChar1[(i3 + 5)] << '\002' | 0x2 & arrayOfChar1[(i3 + 6)] << '\001' | 0x1 & arrayOfChar1[(i3 + 7)]));
            i3 += 8;
            break label390;
          }
          i5 += Math.abs(paramArrayOfChar[(16 + (j + i6))] - paramArrayOfChar[(1 + (16 + (j + i6)))]);
        }
        label727: int i7;
        modesMessage localmodesMessage;
        if (k != 0)
        {
          boolean bool2 = this.aggressive;
          i7 = 0;
          if (bool2)
          {
            i7 = 0;
            if (k >= 3);
          }
        }
        else
        {
          localmodesMessage = decodeModesMessage(arrayOfChar2);
          if ((localmodesMessage.crcok) || (i != 0))
          {
            if (k == 0)
              this.stat_demodulated = (1 + this.stat_demodulated);
            if (localmodesMessage.errorbit != -1)
              break label905;
            if (!localmodesMessage.crcok)
              break label892;
            this.stat_goodcrc = (1 + this.stat_goodcrc);
          }
        }
        while (true)
        {
          if (i != 0);
          boolean bool1 = localmodesMessage.crcok;
          i7 = 0;
          if (bool1)
          {
            j += 2 * (8 + i4 * 8);
            i7 = 1;
            if (i != 0)
              localmodesMessage.phase_corrected = 1;
          }
          interactiveReceiveData(localmodesMessage);
          if ((i7 != 0) || (i != 0))
            break label961;
          j--;
          i = 1;
          break;
          label892: this.stat_badcrc = (1 + this.stat_badcrc);
          continue;
          label905: this.stat_badcrc = (1 + this.stat_badcrc);
          this.stat_fixed = (1 + this.stat_fixed);
          if (localmodesMessage.errorbit < 112)
            this.stat_single_bit_fix = (1 + this.stat_single_bit_fix);
          else
            this.stat_two_bits_fix = (1 + this.stat_two_bits_fix);
        }
        label961: i = 0;
      }
    }
  }

  private void displayLayerMenu()
  {
    final CharSequence[] arrayOfCharSequence = { "WAC Charts", "SEC Charts", "TAC Charts", "Low Enroute IFR Charts", "Street Map", "Zoom in", "Zoom out" };
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
    localBuilder.setTitle("Pick base layer");
    localBuilder.setItems(arrayOfCharSequence, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        Toast.makeText(SerialConsoleActivity.this.getApplicationContext(), arrayOfCharSequence[paramAnonymousInt], 0).show();
        switch (paramAnonymousInt)
        {
        default:
          Log.d(SerialConsoleActivity.this.TAG, "item" + paramAnonymousInt);
          return;
        case 0:
          SerialConsoleActivity.this.TilesOverlayAndCustomTileSource(6);
          return;
        case 1:
          SerialConsoleActivity.this.TilesOverlayAndCustomTileSource(7);
          return;
        case 2:
          SerialConsoleActivity.this.TilesOverlayAndCustomTileSource(8);
          return;
        case 3:
          SerialConsoleActivity.this.TilesOverlayAndCustomTileSource(10);
          return;
        case 4:
          SerialConsoleActivity.this.TilesOverlayAndCustomTileSource(9);
          return;
        case 5:
          SerialConsoleActivity.mapView.getController().zoomIn();
          return;
        case 6:
        }
        SerialConsoleActivity.mapView.getController().zoomOut();
      }
    });
    localBuilder.create().show();
  }

  private void displayModesMessage(modesMessage parammodesMessage)
  {
    String str1 = "";
    if ((this.check_crc) && (!parammodesMessage.crcok));
    label425: label852: 
    do
    {
      do
      {
        return;
        if ((this.onlyaddr) && (DEBUG))
        {
          String str11 = Integer.toHexString(0xFF & parammodesMessage.aa1);
          if (str11.length() == 1)
            str1 = str1 + '0';
          String str12 = str1 + str11 + ":";
          String str13 = Integer.toHexString(0xFF & parammodesMessage.aa2);
          if (str13.length() == 1)
            str12 = str12 + '0';
          String str14 = str12 + str13 + ":";
          String str15 = Integer.toHexString(0xFF & parammodesMessage.aa3);
          if (str15.length() == 1)
            str14 = str14 + '0';
          String str16 = str14 + str15 + ":";
          Log.d(this.TAG, str16);
          return;
        }
        if ((this.raw) && (DEBUG))
        {
          int i = 0;
          StringBuilder localStringBuilder2;
          if (i >= parammodesMessage.msgbits / 8)
          {
            Log.d("displayModesMessage", str1);
            localStringBuilder2 = new StringBuilder("CRC:").append(Integer.toHexString(0xFFFFFF & parammodesMessage.crc));
            if (!parammodesMessage.crcok)
              break label425;
          }
          for (String str10 = " ok"; ; str10 = " wrong")
          {
            Log.d("displayModesMessage", str10);
            return;
            String str9 = Integer.toHexString(0xFF & parammodesMessage.msg[i]);
            if (str9.length() == 1)
              str1 = str1 + '0';
            str1 = str1 + str9 + ":";
            i++;
            break;
          }
        }
        if (parammodesMessage.msgtype == 0)
        {
          Log.d("displayModesMessage", String.format("DF 0: Short Air-Air Surveillance.\n", new Object[0]));
          Object[] arrayOfObject35 = new Object[2];
          arrayOfObject35[0] = Integer.valueOf(parammodesMessage.altitude);
          if (parammodesMessage.unit == 1);
          for (String str8 = "meters"; ; str8 = "feet")
          {
            arrayOfObject35[1] = str8;
            Log.d("displayModesMessage", String.format("  Altitude       : %d %s\n", arrayOfObject35));
            Object[] arrayOfObject36 = new Object[3];
            arrayOfObject36[0] = Byte.valueOf(parammodesMessage.aa1);
            arrayOfObject36[1] = Byte.valueOf(parammodesMessage.aa2);
            arrayOfObject36[2] = Byte.valueOf(parammodesMessage.aa3);
            Log.d("displayModesMessage", String.format("  ICAO Address   : %02x%02x%02x\n", arrayOfObject36));
            return;
          }
        }
        if ((parammodesMessage.msgtype == 4) || (parammodesMessage.msgtype == 20))
        {
          Object[] arrayOfObject1 = new Object[2];
          arrayOfObject1[0] = Integer.valueOf(parammodesMessage.msgtype);
          String str2;
          Object[] arrayOfObject5;
          if (parammodesMessage.msgtype == 4)
          {
            str2 = "Surveillance";
            arrayOfObject1[1] = str2;
            Log.d("displayModesMessage", String.format("DF %d: %s, Altitude Reply.\n", arrayOfObject1));
            Object[] arrayOfObject2 = new Object[1];
            arrayOfObject2[0] = this.fs_str[parammodesMessage.fs];
            Log.d("displayModesMessage", String.format("  Flight Status  : %s\n", arrayOfObject2));
            Object[] arrayOfObject3 = new Object[1];
            arrayOfObject3[0] = Integer.valueOf(parammodesMessage.dr);
            Log.d("displayModesMessage", String.format("  DR             : %d\n", arrayOfObject3));
            Object[] arrayOfObject4 = new Object[1];
            arrayOfObject4[0] = Integer.valueOf(parammodesMessage.um);
            Log.d("displayModesMessage", String.format("  UM             : %d\n", arrayOfObject4));
            arrayOfObject5 = new Object[2];
            arrayOfObject5[0] = Integer.valueOf(parammodesMessage.altitude);
            if (parammodesMessage.unit != 1)
              break label852;
          }
          for (String str3 = "meters"; ; str3 = "feet")
          {
            arrayOfObject5[1] = str3;
            Log.d("displayModesMessage", String.format("  Altitude       : %d %s\n", arrayOfObject5));
            Object[] arrayOfObject6 = new Object[3];
            arrayOfObject6[0] = Byte.valueOf(parammodesMessage.aa1);
            arrayOfObject6[1] = Byte.valueOf(parammodesMessage.aa2);
            arrayOfObject6[2] = Byte.valueOf(parammodesMessage.aa3);
            Log.d("displayModesMessage", String.format("  ICAO Address   : %02x%02x%02x\n", arrayOfObject6));
            return;
            str2 = "Comm-B";
            break;
          }
        }
        if ((parammodesMessage.msgtype == 5) || (parammodesMessage.msgtype == 21))
        {
          String str4 = this.TAG;
          Object[] arrayOfObject7 = new Object[2];
          arrayOfObject7[0] = Integer.valueOf(parammodesMessage.msgtype);
          if (parammodesMessage.msgtype == 5);
          for (String str5 = "Surveillance"; ; str5 = "Comm-B")
          {
            arrayOfObject7[1] = str5;
            Log.d(str4, String.format("DF %d: %s, Identity Reply.\n", arrayOfObject7));
            Object[] arrayOfObject8 = new Object[1];
            arrayOfObject8[0] = this.fs_str[parammodesMessage.fs];
            Log.d("displayModesMessage", String.format("  Flight Status  : %s\n", arrayOfObject8));
            Object[] arrayOfObject9 = new Object[1];
            arrayOfObject9[0] = Integer.valueOf(parammodesMessage.dr);
            Log.d("displayModesMessage", String.format("  DR             : %d\n", arrayOfObject9));
            Object[] arrayOfObject10 = new Object[1];
            arrayOfObject10[0] = Integer.valueOf(parammodesMessage.um);
            Log.d("displayModesMessage", String.format("  UM             : %d\n", arrayOfObject10));
            Object[] arrayOfObject11 = new Object[1];
            arrayOfObject11[0] = Integer.toOctalString(0x1FFF & parammodesMessage.identity);
            Log.d("displayModesMessage", String.format("  Squawk         : %s\n", arrayOfObject11));
            Object[] arrayOfObject12 = new Object[3];
            arrayOfObject12[0] = Byte.valueOf(parammodesMessage.aa1);
            arrayOfObject12[1] = Byte.valueOf(parammodesMessage.aa2);
            arrayOfObject12[2] = Byte.valueOf(parammodesMessage.aa3);
            Log.d("displayModesMessage", String.format("  ICAO Address   : %02x%02x%02x\n", arrayOfObject12));
            if (parammodesMessage.msgtype != 21)
              break;
            Log.d("displayModesMessage", String.format("DF 21: MB additional field", new Object[0]));
            return;
          }
        }
        if (parammodesMessage.msgtype == 11)
        {
          Log.d("displayModesMessage", String.format("DF 11: All Call Reply.\n", new Object[0]));
          Object[] arrayOfObject33 = new Object[1];
          arrayOfObject33[0] = this.ca_str[parammodesMessage.ca];
          Log.d("displayModesMessage", String.format("  Capability  : %s\n", arrayOfObject33));
          Object[] arrayOfObject34 = new Object[3];
          arrayOfObject34[0] = Byte.valueOf(parammodesMessage.aa1);
          arrayOfObject34[1] = Byte.valueOf(parammodesMessage.aa2);
          arrayOfObject34[2] = Byte.valueOf(parammodesMessage.aa3);
          Log.d("displayModesMessage", String.format("  ICAO Address: %02X%02X%02X\n", arrayOfObject34));
          return;
        }
      }
      while (parammodesMessage.msgtype != 17);
      StringBuilder localStringBuilder1 = new StringBuilder(String.valueOf(String.format("DF 17: ADS-B message ", new Object[0])));
      Object[] arrayOfObject13 = new Object[3];
      arrayOfObject13[0] = Byte.valueOf(parammodesMessage.aa1);
      arrayOfObject13[1] = Byte.valueOf(parammodesMessage.aa2);
      arrayOfObject13[2] = Byte.valueOf(parammodesMessage.aa3);
      Log.d("displayModesMessage", String.format("ICAO Address   : %02X%02X%02X\n", arrayOfObject13));
      Object[] arrayOfObject14 = new Object[2];
      arrayOfObject14[0] = Integer.valueOf(parammodesMessage.ca);
      arrayOfObject14[1] = this.ca_str[parammodesMessage.ca];
      Log.d("displayModesMessage", String.format("  Capability     : %d (%s)\n", arrayOfObject14));
      Object[] arrayOfObject15 = new Object[1];
      arrayOfObject15[0] = Integer.valueOf(parammodesMessage.metype);
      Log.d("displayModesMessage", String.format("  Extended Squitter  Type: %d\n", arrayOfObject15));
      Object[] arrayOfObject16 = new Object[1];
      arrayOfObject16[0] = Integer.valueOf(parammodesMessage.mesub);
      Log.d("displayModesMessage", String.format("  Extended Squitter  Sub : %d\n", arrayOfObject16));
      Object[] arrayOfObject17 = new Object[1];
      arrayOfObject17[0] = getMEDescription(parammodesMessage.metype, parammodesMessage.mesub);
      Log.d("displayModesMessage", String.format("  Extended Squitter  Name: %s\n", arrayOfObject17));
      if ((parammodesMessage.metype >= 1) && (parammodesMessage.metype <= 4))
      {
        String[] arrayOfString = { "Aircraft Type D", "Aircraft Type C", "Aircraft Type B", "Aircraft Type A" };
        Object[] arrayOfObject32 = new Object[1];
        arrayOfObject32[0] = arrayOfString[parammodesMessage.aircraft_type];
        Log.d("displayModesMessage", String.format("    Aircraft Type  : %s\n", arrayOfObject32));
        Log.d("displayModesMessage", "    Identification :" + parammodesMessage.flight);
        return;
      }
      if ((parammodesMessage.metype >= 9) && (parammodesMessage.metype <= 18))
      {
        Object[] arrayOfObject27 = new Object[1];
        String str6;
        Object[] arrayOfObject28;
        if (parammodesMessage.fflag != 0)
        {
          str6 = "odd";
          arrayOfObject27[0] = str6;
          Log.d("displayModesMessage", String.format("    F flag   : %s\n", arrayOfObject27));
          arrayOfObject28 = new Object[1];
          if (parammodesMessage.tflag == 0)
            break label1817;
        }
        for (String str7 = "UTC"; ; str7 = "non-UTC")
        {
          arrayOfObject28[0] = str7;
          Log.d("displayModesMessage", String.format("    T flag   : %s\n", arrayOfObject28));
          Object[] arrayOfObject29 = new Object[1];
          arrayOfObject29[0] = Integer.valueOf(parammodesMessage.altitude);
          Log.d("displayModesMessage", String.format("    Altitude : %d feet\n", arrayOfObject29));
          Object[] arrayOfObject30 = new Object[1];
          arrayOfObject30[0] = Integer.valueOf(parammodesMessage.raw_latitude);
          Log.d("displayModesMessage", String.format("    Latitude : %d (not decoded)\n", arrayOfObject30));
          Object[] arrayOfObject31 = new Object[1];
          arrayOfObject31[0] = Integer.valueOf(parammodesMessage.raw_longitude);
          Log.d("displayModesMessage", String.format("    Longitude: %d (not decoded)\n", arrayOfObject31));
          return;
          str6 = "even";
          break;
        }
      }
      if ((parammodesMessage.metype != 19) || (parammodesMessage.mesub < 1) || (parammodesMessage.mesub > 4))
        break;
      if ((parammodesMessage.mesub == 1) || (parammodesMessage.mesub == 2))
      {
        Object[] arrayOfObject19 = new Object[1];
        arrayOfObject19[0] = Integer.valueOf(parammodesMessage.ew_dir);
        Log.d("displayModesMessage", String.format("    EW direction      : %d\n", arrayOfObject19));
        Object[] arrayOfObject20 = new Object[1];
        arrayOfObject20[0] = Integer.valueOf(parammodesMessage.ew_velocity);
        Log.d("displayModesMessage", String.format("    EW velocity       : %d\n", arrayOfObject20));
        Object[] arrayOfObject21 = new Object[1];
        arrayOfObject21[0] = Integer.valueOf(parammodesMessage.ns_dir);
        Log.d("displayModesMessage", String.format("    NS direction      : %d\n", arrayOfObject21));
        Object[] arrayOfObject22 = new Object[1];
        arrayOfObject22[0] = Integer.valueOf(parammodesMessage.ns_velocity);
        Log.d("displayModesMessage", String.format("    NS velocity       : %d\n", arrayOfObject22));
        Object[] arrayOfObject23 = new Object[1];
        arrayOfObject23[0] = Integer.valueOf(parammodesMessage.vert_rate_source);
        Log.d("displayModesMessage", String.format("    Vertical rate src : %d\n", arrayOfObject23));
        Object[] arrayOfObject24 = new Object[1];
        arrayOfObject24[0] = Integer.valueOf(parammodesMessage.vert_rate_sign);
        Log.d("displayModesMessage", String.format("    Vertical rate sign: %d\n", arrayOfObject24));
        Object[] arrayOfObject25 = new Object[1];
        arrayOfObject25[0] = Integer.valueOf(parammodesMessage.vert_rate);
        Log.d("displayModesMessage", String.format("    Vertical rate     : %d\n", arrayOfObject25));
        return;
      }
    }
    while ((parammodesMessage.mesub != 3) && (parammodesMessage.mesub != 4));
    label1817: Log.d("displayModesMessage", "    Heading status: " + parammodesMessage.heading_is_valid);
    Object[] arrayOfObject26 = new Object[1];
    arrayOfObject26[0] = Integer.valueOf(parammodesMessage.heading);
    Log.d("displayModesMessage", String.format("    Heading: %d", arrayOfObject26));
    return;
    Object[] arrayOfObject18 = new Object[2];
    arrayOfObject18[0] = Integer.valueOf(parammodesMessage.metype);
    arrayOfObject18[1] = Integer.valueOf(parammodesMessage.mesub);
    Log.d("displayModesMessage", String.format("    Unrecognized ME type: %d subtype: %d\n", arrayOfObject18));
  }

  private void dumpRawMessage(String paramString, char[] paramArrayOfChar1, char[] paramArrayOfChar2, int paramInt)
  {
    int i;
    int j;
    int k;
    int m;
    label48: String str1;
    if (paramArrayOfChar1[0] >= 0)
    {
      i = paramArrayOfChar1[0];
      j = 0x1F & i >> 3;
      k = -1;
      if ((j == 11) || (j == 17))
      {
        if (j != 11)
          break label195;
        m = 56;
        k = fixSingleBitErrors(paramArrayOfChar1, m);
        if (k == -1)
          k = fixTwoBitsErrors(paramArrayOfChar1, m);
        Log.d("DUMP", "msgbits " + m);
      }
      Log.d("DUMP", paramString);
      str1 = paramString + " ";
    }
    for (int n = 0; ; n++)
    {
      if (n >= 14)
      {
        Log.d("DUMP", str1);
        Log.d("DUMP", "(DF " + j + " Fixable: " + k);
        return;
        i = 'Ā' + paramArrayOfChar1[0];
        break;
        label195: m = 112;
        break label48;
      }
      String str2 = Integer.toHexString(0xFF & paramArrayOfChar1[n]);
      if (str2.length() == 1)
        str1 = str1 + '0';
      str1 = str1 + str2 + ":";
      if (n == 6)
        Log.d(this.TAG, " ... ");
    }
  }

  private void dumpStats()
  {
    Log.d("STATS", "valid preambles " + this.stat_valid_preamble);
    Log.d("STATS", "demodulated again after phase correction " + this.stat_out_of_phase);
    Log.d("STATS", "demodulated with zero errors " + this.stat_demodulated);
    Log.d("STATS", "with good crc " + this.stat_goodcrc);
    Log.d("STATS", "with bad crc " + this.stat_badcrc);
    Log.d("STATS", "errors corrected " + this.stat_fixed);
    Log.d("STATS", "single bit errors " + this.stat_single_bit_fix);
    Log.d("STATS", "two bits errors " + this.stat_two_bits_fix);
    Log.e("STATS1", "total usable messages " + (this.stat_goodcrc + this.stat_fixed));
  }

  private int fixSingleBitErrors(char[] paramArrayOfChar, int paramInt)
  {
    char[] arrayOfChar = new char[14];
    for (int i = 0; ; i++)
    {
      if (i >= paramInt)
        return -1;
      int j = i / 8;
      int k = 1 << 7 - i % 8;
      System.arraycopy(paramArrayOfChar, 0, arrayOfChar, 0, paramInt / 8);
      arrayOfChar[j] = ((char)(k ^ arrayOfChar[j]));
      if ((0xFFFFFF & (arrayOfChar[(-3 + paramInt / 8)] << '\020' | arrayOfChar[(-2 + paramInt / 8)] << '\b' | arrayOfChar[(-1 + paramInt / 8)])) == modesChecksum(arrayOfChar, paramInt))
      {
        System.arraycopy(arrayOfChar, 0, paramArrayOfChar, 0, paramInt / 8);
        return i;
      }
    }
  }

  private int fixTwoBitsErrors(char[] paramArrayOfChar, int paramInt)
  {
    byte[] arrayOfByte = new byte[14];
    int i = 0;
    if (i >= paramInt)
      return -1;
    int j = i / 8;
    int k = 1 << 7 - i % 8;
    for (int m = i + 1; ; m++)
    {
      if (m >= paramInt)
      {
        i++;
        break;
      }
      int n = m / 8;
      int i1 = 1 << 7 - m % 8;
      System.arraycopy(paramArrayOfChar, 0, arrayOfByte, 0, paramInt / 8);
      arrayOfByte[j] = ((byte)(k ^ arrayOfByte[j]));
      arrayOfByte[n] = ((byte)(i1 ^ arrayOfByte[n]));
      if ((0xFFFFFF & (arrayOfByte[(-3 + paramInt / 8)] << 16 | arrayOfByte[(-2 + paramInt / 8)] << 8 | arrayOfByte[(-1 + paramInt / 8)])) == modesChecksum(arrayOfByte, paramInt))
      {
        System.arraycopy(arrayOfByte, 0, paramArrayOfChar, 0, paramInt / 8);
        return i | m << 8;
      }
    }
  }

  private String getMEDescription(int paramInt1, int paramInt2)
  {
    String str = "Unknown";
    if ((paramInt1 >= 1) && (paramInt1 <= 4))
      str = "Aircraft Identification and Category";
    do
    {
      return str;
      if ((paramInt1 >= 5) && (paramInt1 <= 8))
        return "Surface Position";
      if ((paramInt1 >= 9) && (paramInt1 <= 18))
        return "Airborne Position (Baro Altitude)";
      if ((paramInt1 == 19) && (paramInt2 >= 1) && (paramInt2 <= 4))
        return "Airborne Velocity";
      if ((paramInt1 >= 20) && (paramInt1 <= 22))
        return "Airborne Position (GNSS Height)";
      if ((paramInt1 == 23) && (paramInt2 == 0))
        return "Test Message";
      if ((paramInt1 == 24) && (paramInt2 == 1))
        return "Surface System Status";
      if ((paramInt1 == 28) && (paramInt2 == 1))
        return "Extended Squitter Aircraft Status (Emergency)";
      if ((paramInt1 == 28) && (paramInt2 == 2))
        return "Extended Squitter Aircraft Status (1090ES TCAS RA)";
      if ((paramInt1 == 29) && ((paramInt2 == 0) || (paramInt2 == 1)))
        return "Target State and Status Message";
    }
    while ((paramInt1 != 31) || ((paramInt2 != 0) && (paramInt2 != 1)));
    return "Aircraft Operational Status Message";
  }

  private void interactiveRemoveStaleAircrafts()
  {
    long l = System.currentTimeMillis();
    Iterator localIterator = this.Aircrafts.entrySet().iterator();
    while (true)
    {
      if (!localIterator.hasNext())
      {
        if (DEBUG)
          Log.d("interactiveRemoveStaleAircrafts", "removeing old record after " + this.MODES_INTERACTIVE_TTL + " secs size " + this.Aircrafts.size());
        return;
      }
      if (Math.abs(l - ((aircraft)((Map.Entry)localIterator.next()).getValue()).seen) / 1000L > this.MODES_INTERACTIVE_TTL)
        localIterator.remove();
    }
  }

  private void interactiveShowData()
  {
    long l = System.currentTimeMillis();
    int i = 0;
    String str1 = "";
    new ArrayList();
    Iterator localIterator = this.Aircrafts.entrySet().iterator();
    if ((!localIterator.hasNext()) || (i >= this.interactive_rows))
    {
      String str2 = "valid preambles                          " + this.stat_valid_preamble + "\n";
      if (DEBUG)
        str2 = new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(str2)).append("demodulated with zero errors             ").append(this.stat_demodulated).append("\n").toString())).append("demodulated again after phase correction ").append(this.stat_out_of_phase).append("\n").toString())).append("single bit errors                        ").append(this.stat_single_bit_fix).append("\n").toString())).append("with good crc                            ").append(this.stat_goodcrc).append("\n").toString())).append("with bad crc                             ").append(this.stat_badcrc).append("\n").toString() + "two bits errors                          " + this.stat_two_bits_fix + "\n";
      final String str3 = str2 + "Hex\t\t\tFlight\t\tAlt\t\tSpeed\tLat\t\tLon\t\tTrk\tMsg\t\tSeen\tSquawk\tDistance\tBearing" + str1;
      Runnable local10 = new Runnable()
      {
        public void run()
        {
          SerialConsoleActivity.this.updateReceivedData(str3);
        }
      };
      runOnUiThread(local10);
      return;
    }
    aircraft localaircraft = (aircraft)((Map.Entry)localIterator.next()).getValue();
    String str4;
    int m;
    double d;
    label932: StringBuilder localStringBuilder13;
    Object[] arrayOfObject13;
    if ((localaircraft.lat != 0.0D) || (((localaircraft.lat != 0.0D) && (localaircraft.lon != 0.0D)) || (localaircraft.messages > 2L)))
    {
      int j = localaircraft.altitude;
      int k = localaircraft.speed;
      if (this.metric)
      {
        j = (int)(j / 3.2828D);
        k = (int)(1.852D * k);
      }
      StringBuilder localStringBuilder1 = new StringBuilder(String.valueOf(str1)).append("\n0x");
      Object[] arrayOfObject1 = new Object[1];
      arrayOfObject1[0] = Integer.valueOf(0xFFFFFF & localaircraft.addr);
      StringBuilder localStringBuilder2 = localStringBuilder1.append(String.format("%06X", arrayOfObject1)).append("\t");
      Object[] arrayOfObject2 = new Object[1];
      arrayOfObject2[0] = localaircraft.flight;
      StringBuilder localStringBuilder3 = localStringBuilder2.append(String.format("%8s", arrayOfObject2)).append("\t");
      Object[] arrayOfObject3 = new Object[1];
      arrayOfObject3[0] = Integer.valueOf(j);
      StringBuilder localStringBuilder4 = localStringBuilder3.append(String.format("%05d", arrayOfObject3)).append("\t");
      Object[] arrayOfObject4 = new Object[1];
      arrayOfObject4[0] = Integer.valueOf(k);
      StringBuilder localStringBuilder5 = new StringBuilder(String.valueOf(String.format("%03d", arrayOfObject4))).append("\t\t");
      Object[] arrayOfObject5 = new Object[1];
      arrayOfObject5[0] = Double.valueOf(localaircraft.lat);
      StringBuilder localStringBuilder6 = localStringBuilder5.append(String.format("%06.2f", arrayOfObject5)).append("\t");
      Object[] arrayOfObject6 = new Object[1];
      arrayOfObject6[0] = Double.valueOf(localaircraft.lon);
      StringBuilder localStringBuilder7 = localStringBuilder6.append(String.format("%06.2f", arrayOfObject6)).append("\t");
      Object[] arrayOfObject7 = new Object[1];
      arrayOfObject7[0] = Integer.valueOf(localaircraft.track);
      StringBuilder localStringBuilder8 = new StringBuilder(String.valueOf(String.format("%03d", arrayOfObject7))).append("\t");
      Object[] arrayOfObject8 = new Object[1];
      arrayOfObject8[0] = Long.valueOf(localaircraft.messages);
      StringBuilder localStringBuilder9 = localStringBuilder8.append(String.format("%03d", arrayOfObject8)).append("\t\t");
      Object[] arrayOfObject9 = new Object[1];
      arrayOfObject9[0] = Integer.valueOf((int)(Math.abs(l - localaircraft.seen) / 1000.0D));
      StringBuilder localStringBuilder10 = localStringBuilder9.append(String.format("%02d", arrayOfObject9)).append("\t\t");
      Object[] arrayOfObject10 = new Object[1];
      arrayOfObject10[0] = Integer.valueOf(0x1FFF & localaircraft.identity);
      str4 = String.format("%04d", arrayOfObject10) + "\t";
      if (!this.GpsAvailable)
        this.myCurrentPosition = ((GeoPoint)mapView.getMapCenter());
      if ((localaircraft.lat != 0.0D) || (localaircraft.lon != 0.0D))
        break label1054;
      m = 0;
      d = 0.0D;
      if (!this.metric)
        break label1103;
      int i1 = m / 1000;
      localStringBuilder13 = new StringBuilder(String.valueOf(str4));
      arrayOfObject13 = new Object[1];
      arrayOfObject13[0] = Integer.valueOf(i1);
    }
    label1054: label1103: StringBuilder localStringBuilder11;
    Object[] arrayOfObject11;
    for (String str5 = String.format("\t%03d km", arrayOfObject13); ; str5 = String.format("\t%03d NM", arrayOfObject11))
    {
      StringBuilder localStringBuilder12 = new StringBuilder(String.valueOf(str5));
      Object[] arrayOfObject12 = new Object[1];
      arrayOfObject12[0] = Integer.valueOf((int)d);
      str1 = String.format("\t\t%03d", arrayOfObject12) + " deg.";
      i++;
      break;
      m = this.myCurrentPosition.distanceTo(coordsToGeoPoint(localaircraft.lat, localaircraft.lon));
      d = this.myCurrentPosition.bearingTo(coordsToGeoPoint(localaircraft.lat, localaircraft.lon));
      break label932;
      int n = m / 1852;
      localStringBuilder11 = new StringBuilder(String.valueOf(str4));
      arrayOfObject11 = new Object[1];
      arrayOfObject11[0] = Integer.valueOf(n);
    }
  }

  private void interactiveUpdateMap()
  {
    ArrayList localArrayList1 = new ArrayList();
    Iterator localIterator = this.Aircrafts.entrySet().iterator();
    aircraft localaircraft;
    do
    {
      if (!localIterator.hasNext())
      {
        final ArrayList localArrayList2 = new ArrayList(localArrayList1);
        Runnable local9 = new Runnable()
        {
          public void run()
          {
            SerialConsoleActivity.this.mMyTrafficOverlay.removeAllItems();
            if (!localArrayList2.isEmpty())
              SerialConsoleActivity.this.mMyTrafficOverlay.addItems(localArrayList2);
            SerialConsoleActivity.mapView.postInvalidate();
          }
        };
        runOnUiThread(local9);
        return;
      }
      localaircraft = (aircraft)((Map.Entry)localIterator.next()).getValue();
    }
    while ((localaircraft.lat == 0.0D) || (localaircraft.lon == 0.0D));
    int i = localaircraft.altitude;
    int j = localaircraft.speed;
    if (this.metric)
    {
      i = (int)(i / 3.2828D);
      j = (int)(1.852D * j);
    }
    if (!this.GpsAvailable)
      this.myCurrentPosition = ((GeoPoint)mapView.getMapCenter());
    int k;
    double d;
    if ((localaircraft.lat == 0.0D) && (localaircraft.lon == 0.0D))
    {
      k = 0;
      d = 0.0D;
      label183: if (!this.metric)
        break label489;
    }
    label489: for (int m = k / 1000; ; m = k / 1852)
    {
      StringBuilder localStringBuilder1 = new StringBuilder(String.valueOf("Flight:\t\t" + localaircraft.flight + "\nAltitude:\t" + i + "\nSpeed:\t\t" + j + "\nTrack:\t\t" + localaircraft.track)).append("\nBearing\t");
      Object[] arrayOfObject1 = new Object[1];
      arrayOfObject1[0] = Integer.valueOf((int)d);
      StringBuilder localStringBuilder2 = localStringBuilder1.append(String.format("%3d", arrayOfObject1)).append("\nDistance:\t").append(m).append("\nSquawk:\t");
      Object[] arrayOfObject2 = new Object[1];
      arrayOfObject2[0] = Integer.valueOf(0x1FFF & localaircraft.identity);
      String str = String.format("%04d", arrayOfObject2);
      Object[] arrayOfObject3 = new Object[1];
      arrayOfObject3[0] = Integer.valueOf(0xFFFFFF & localaircraft.addr);
      OverlayItem localOverlayItem = new OverlayItem(String.format("%06X", arrayOfObject3), str, coordsToGeoPoint(localaircraft.lat, localaircraft.lon));
      localOverlayItem.setMarker(rotateDrawable((float)d));
      localArrayList1.add(localOverlayItem);
      break;
      k = this.myCurrentPosition.distanceTo(coordsToGeoPoint(localaircraft.lat, localaircraft.lon));
      d = this.myCurrentPosition.bearingTo(coordsToGeoPoint(localaircraft.lat, localaircraft.lon));
      break label183;
    }
  }

  private boolean isGpsAvailable()
  {
    PackageManager localPackageManager = getPackageManager();
    if (localPackageManager.hasSystemFeature("android.hardware.location.gps"));
    while (localPackageManager.hasSystemFeature("android.hardware.location.network"))
      return true;
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
    localBuilder.setTitle("No GPS");
    localBuilder.setMessage("Using Map center as default");
    localBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        paramAnonymousDialogInterface.dismiss();
      }
    });
    localBuilder.show();
    return false;
  }

  private int modesChecksum(byte[] paramArrayOfByte, int paramInt)
  {
    byte[] arrayOfByte1 = new byte[17];
    byte[] arrayOfByte2 = { -1, -12, 8 };
    if ((paramInt != 56) && (paramInt != 112))
    {
      Log.e("ERROR ", "modesChecksum fatal parameter error nb=" + paramInt);
      return 1;
    }
    arrayOfByte1[0] = 0;
    arrayOfByte1[1] = 0;
    arrayOfByte1[2] = 0;
    int i = paramInt - 25;
    int j = -3 + paramInt / 8;
    int k = j + 3;
    int m = 0;
    int n;
    label111: int i2;
    if (m >= j)
    {
      n = 0;
      if (n <= i)
        break label183;
      i2 = 0xFF0000 & arrayOfByte1[0] << 16 | 0xFF00 & arrayOfByte1[1] << 8 | 0xFF & arrayOfByte1[2];
      if (paramInt != 56)
        break label261;
      SepAP56(paramArrayOfByte);
    }
    while (true)
    {
      return 0xFFFFFF & i2;
      arrayOfByte1[(m + 3)] = paramArrayOfByte[m];
      m++;
      break;
      label183: int i1 = 0x1 & (LeftShift(arrayOfByte1, k) ^ 0x1 & arrayOfByte1[2]);
      arrayOfByte1[2] = ((byte)(i1 | 0xFE & arrayOfByte1[2]));
      if (i1 != 0)
      {
        arrayOfByte1[0] = ((byte)(arrayOfByte1[0] ^ arrayOfByte2[0]));
        arrayOfByte1[1] = ((byte)(arrayOfByte1[1] ^ arrayOfByte2[1]));
        arrayOfByte1[2] = ((byte)(arrayOfByte1[2] ^ arrayOfByte2[2]));
      }
      n++;
      break label111;
      label261: SepAP112(paramArrayOfByte);
    }
  }

  private int modesChecksum(char[] paramArrayOfChar, int paramInt)
  {
    char[] arrayOfChar = new char[17];
    byte[] arrayOfByte = { -1, -12, 8 };
    if ((paramInt != 56) && (paramInt != 112))
    {
      Log.e("ERROR ", "modesChecksum fatal parameter error nb=" + paramInt);
      return 1;
    }
    arrayOfChar[0] = '\000';
    arrayOfChar[1] = '\000';
    arrayOfChar[2] = '\000';
    int i = paramInt - 25;
    int j = -3 + paramInt / 8;
    int k = j + 3;
    int m = 0;
    int n;
    label111: int i2;
    if (m >= j)
    {
      n = 0;
      if (n <= i)
        break label183;
      i2 = 0xFF0000 & arrayOfChar[0] << '\020' | 0xFF00 & arrayOfChar[1] << '\b' | 0xFF & arrayOfChar[2];
      if (paramInt != 56)
        break label261;
      SepAP56(paramArrayOfChar);
    }
    while (true)
    {
      return 0xFFFFFF & i2;
      arrayOfChar[(m + 3)] = paramArrayOfChar[m];
      m++;
      break;
      label183: int i1 = 0x1 & (LeftShift(arrayOfChar, k) ^ 0x1 & arrayOfChar[2]);
      arrayOfChar[2] = ((char)(i1 | 0xFE & arrayOfChar[2]));
      if (i1 != 0)
      {
        arrayOfChar[0] = ((char)(arrayOfChar[0] ^ arrayOfByte[0]));
        arrayOfChar[1] = ((char)(arrayOfChar[1] ^ arrayOfByte[1]));
        arrayOfChar[2] = ((char)(arrayOfChar[2] ^ arrayOfByte[2]));
      }
      n++;
      break label111;
      label261: SepAP112(paramArrayOfChar);
    }
  }

  private void onDeviceStateChange()
  {
    stopIoManager();
    startIoManager();
  }

  private void refreshDeviceList()
  {
    new AsyncTask()
    {
      protected List<SerialConsoleActivity.DeviceEntry> doInBackground(Void[] paramAnonymousArrayOfVoid)
      {
        Log.d(SerialConsoleActivity.this.TAG, "Refreshing device list ...");
        ArrayList localArrayList = new ArrayList();
        Iterator localIterator1 = SerialConsoleActivity.this.mUsbManager.getDeviceList().values().iterator();
        while (true)
        {
          if (!localIterator1.hasNext())
            return localArrayList;
          UsbDevice localUsbDevice = (UsbDevice)localIterator1.next();
          List localList = UsbSerialProber.probeSingleDevice(SerialConsoleActivity.this.mUsbManager, localUsbDevice);
          Log.d(SerialConsoleActivity.this.TAG, "Found usb device: " + localUsbDevice);
          if (localList.isEmpty())
          {
            Log.d(SerialConsoleActivity.this.TAG, "  - No UsbSerialDriver available.");
            localArrayList.add(new SerialConsoleActivity.DeviceEntry(localUsbDevice, null));
          }
          else
          {
            Iterator localIterator2 = localList.iterator();
            while (localIterator2.hasNext())
            {
              UsbSerialDriver localUsbSerialDriver = (UsbSerialDriver)localIterator2.next();
              Log.d(SerialConsoleActivity.this.TAG, "  + " + localUsbSerialDriver);
              localArrayList.add(new SerialConsoleActivity.DeviceEntry(localUsbDevice, localUsbSerialDriver));
              SerialConsoleActivity.sDriver = localUsbSerialDriver;
            }
          }
        }
      }

      protected void onPostExecute(List<SerialConsoleActivity.DeviceEntry> paramAnonymousList)
      {
        SerialConsoleActivity.this.mEntries.clear();
        SerialConsoleActivity.this.mEntries.addAll(paramAnonymousList);
        String str = SerialConsoleActivity.this.TAG;
        Object[] arrayOfObject = new Object[1];
        arrayOfObject[0] = Integer.valueOf(SerialConsoleActivity.this.mEntries.size());
        Log.d(str, String.format("%s device(s) found", arrayOfObject));
        Log.d(SerialConsoleActivity.this.TAG, "Done refreshing, " + SerialConsoleActivity.this.mEntries.size() + " entries found.");
      }
    }
    .execute(new Void[] { null });
  }

  static void show(Context paramContext, UsbSerialDriver paramUsbSerialDriver)
  {
    sDriver = paramUsbSerialDriver;
    Intent localIntent = new Intent(paramContext, SerialConsoleActivity.class);
    localIntent.addFlags(1610612736);
    paramContext.startActivity(localIntent);
  }

  private void startIoManager()
  {
    if (sDriver != null)
    {
      Log.i(this.TAG, "Starting io manager ..");
      this.mSerialIoManager = new SerialInputOutputManager(sDriver, this.mListener);
      this.mExecutor.submit(this.mSerialIoManager);
    }
  }

  private void startThread()
  {
    new Thread(new Runnable()
    {
      public void run()
      {
        do
          SerialConsoleActivity.this.backgroundTasks();
        while (!SerialConsoleActivity.this.exit);
      }
    }).start();
    Log.d(this.TAG, "Thread Started ");
  }

  private void stopIoManager()
  {
    if (this.mSerialIoManager != null)
    {
      Log.i(this.TAG, "Stopping io manager ..");
      this.mSerialIoManager.stop();
      this.mSerialIoManager = null;
      this.exit = true;
    }
  }

  private void updateReceivedData(String paramString)
  {
    this.mDumpTextView.setText(paramString);
    this.mScrollView.smoothScrollTo(0, this.mDumpTextView.getBottom());
  }

  void InitPreferences()
  {
    SharedPreferences localSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    Boolean.valueOf(localSharedPreferences.getBoolean("enable_agc", false));
    this.aggressive = localSharedPreferences.getBoolean("two_bit_error_correction", false);
    this.MODES_INTERACTIVE_TTL = Integer.parseInt(localSharedPreferences.getString("ttl_value", "60"));
    this.fix_errors = localSharedPreferences.getBoolean("one_bit_error_correction", true);
    this.metric = localSharedPreferences.getBoolean("metric_units", false);
    DEBUG = localSharedPreferences.getBoolean("debug", false);
    this.check_crc = localSharedPreferences.getBoolean("crc_check", true);
    Integer.parseInt(localSharedPreferences.getString("gain_value", "-100"));
    localSharedPreferences.getBoolean("skyradar", true);
    Integer.parseInt(localSharedPreferences.getString("baud_rate", "115200"));
    Integer.parseInt(localSharedPreferences.getString("ppm_value", "0"));
    this.mapOnlyView = localSharedPreferences.getBoolean("map_only", true);
    this.interactive_rows = Integer.parseInt(localSharedPreferences.getString("rows_value", "10"));
  }

  int LeftShift(byte[] paramArrayOfByte, int paramInt)
  {
    int i = 0x80 & paramArrayOfByte[0];
    int j = 0;
    if (i != 0)
      j = 1;
    paramArrayOfByte[0] = ((byte)(paramArrayOfByte[0] << 1));
    for (int k = 1; ; k++)
    {
      if (k >= paramInt)
        return j;
      if ((0x80 & paramArrayOfByte[k]) != 0)
      {
        int m = k - 1;
        paramArrayOfByte[m] = ((byte)(0x1 | paramArrayOfByte[m]));
      }
      paramArrayOfByte[k] = ((byte)(paramArrayOfByte[k] << 1));
    }
  }

  int LeftShift(char[] paramArrayOfChar, int paramInt)
  {
    int i = 0x80 & paramArrayOfChar[0];
    int j = 0;
    if (i != 0)
      j = 1;
    paramArrayOfChar[0] = ((char)(paramArrayOfChar[0] << '\001'));
    for (int k = 1; ; k++)
    {
      if (k >= paramInt)
        return j;
      if ((0x80 & paramArrayOfChar[k]) != 0)
      {
        int m = k - 1;
        paramArrayOfChar[m] = ((char)(0x1 | paramArrayOfChar[m]));
      }
      paramArrayOfChar[k] = ((char)(paramArrayOfChar[k] << '\001'));
    }
  }

  public void TilesOverlayAndCustomTileSource(int paramInt)
  {
    String[] arrayOfString1 = { "http://thepilotmap.com/cb-sec/" };
    if (paramInt == 7)
      mapView.setTileSource(new XYTileSource("cb-sec", ResourceProxy.string.unknown, 4, 12, 256, ".png.tile", arrayOfString1)
      {
        public String getTileURLString(MapTile paramAnonymousMapTile)
        {
          new String();
          String str = getBaseUrl() + paramAnonymousMapTile.getZoomLevel() + "/" + paramAnonymousMapTile.getX() + "/" + paramAnonymousMapTile.getY() + this.mImageFilenameEnding;
          Log.d(SerialConsoleActivity.this.TAG, " getBaseUrl " + str);
          return str;
        }
      });
    if (paramInt == 6)
      mapView.setTileSource(new XYTileSource("cb-wac", ResourceProxy.string.unknown, 4, 12, 256, ".png.tile", arrayOfString1)
      {
        public String getTileURLString(MapTile paramAnonymousMapTile)
        {
          new String();
          String str = getBaseUrl() + paramAnonymousMapTile.getZoomLevel() + "/" + paramAnonymousMapTile.getX() + "/" + paramAnonymousMapTile.getY() + this.mImageFilenameEnding;
          Log.d(SerialConsoleActivity.this.TAG, " getBaseUrl " + str);
          return str;
        }
      });
    if (paramInt == 9)
    {
      String[] arrayOfString2 = { "http://tile.openstreetmap.org/" };
      mapView.setTileSource(new XYTileSource("Mapnik", ResourceProxy.string.unknown, 0, 18, 256, ".png", arrayOfString2)
      {
        public String getTileURLString(MapTile paramAnonymousMapTile)
        {
          new String();
          String str = getBaseUrl() + paramAnonymousMapTile.getZoomLevel() + "/" + paramAnonymousMapTile.getX() + "/" + paramAnonymousMapTile.getY() + this.mImageFilenameEnding;
          Log.d(SerialConsoleActivity.this.TAG, " getBaseUrl " + str);
          return str;
        }
      });
    }
    if (paramInt == 8)
      mapView.setTileSource(new XYTileSource("cb-tac", ResourceProxy.string.unknown, 4, 12, 256, ".png.tile", arrayOfString1)
      {
        public String getTileURLString(MapTile paramAnonymousMapTile)
        {
          new String();
          String str = getBaseUrl() + paramAnonymousMapTile.getZoomLevel() + "/" + paramAnonymousMapTile.getX() + "/" + paramAnonymousMapTile.getY() + this.mImageFilenameEnding;
          Log.d(SerialConsoleActivity.this.TAG, " getBaseUrl " + str);
          return str;
        }
      });
    if (paramInt == 10)
      mapView.setTileSource(new XYTileSource("cb-enrl", ResourceProxy.string.unknown, 4, 12, 256, ".png.tile", arrayOfString1)
      {
        public String getTileURLString(MapTile paramAnonymousMapTile)
        {
          new String();
          String str = getBaseUrl() + paramAnonymousMapTile.getZoomLevel() + "/" + paramAnonymousMapTile.getX() + "/" + paramAnonymousMapTile.getY() + this.mImageFilenameEnding;
          Log.d(SerialConsoleActivity.this.TAG, " getBaseUrl " + str);
          return str;
        }
      });
    mapView.setUseDataConnection(true);
    mapView.invalidate();
  }

  void applyPhaseCorrection(char[] paramArrayOfChar, int paramInt)
  {
    int i = paramInt + 16;
    int j = 0;
    if (j >= 222)
      return;
    if (paramArrayOfChar[(j + i)] > paramArrayOfChar[(i + (j + 1))])
      paramArrayOfChar[(i + (j + 2))] = ((char)('\005' * paramArrayOfChar[(i + (j + 2))] / 4));
    while (true)
    {
      j += 2;
      break;
      paramArrayOfChar[(i + (j + 2))] = ((char)('\004' * paramArrayOfChar[(i + (j + 2))] / 5));
    }
  }

  public GeoPoint coordsToGeoPoint(double paramDouble1, double paramDouble2)
  {
    return new GeoPoint((int)(paramDouble1 * 1000000.0D), (int)(paramDouble2 * 1000000.0D));
  }

  double cprDlonFunction(double paramDouble, int paramInt)
  {
    return 360.0D / cprNFunction(paramDouble, paramInt);
  }

  int cprNFunction(double paramDouble, int paramInt)
  {
    int i = cprNLFunction(paramDouble) - paramInt;
    if (i < 1)
      i = 1;
    return i;
  }

  int cprNLFunction(double paramDouble)
  {
    if (paramDouble < 10.4704713D)
      return 59;
    if (paramDouble < 14.828174369999999D)
      return 58;
    if (paramDouble < 18.186263570000001D)
      return 57;
    if (paramDouble < 21.029394929999999D)
      return 56;
    if (paramDouble < 23.545044870000002D)
      return 55;
    if (paramDouble < 25.829247070000001D)
      return 54;
    if (paramDouble < 27.938987099999999D)
      return 53;
    if (paramDouble < 29.911356860000001D)
      return 52;
    if (paramDouble < 31.772097080000002D)
      return 51;
    if (paramDouble < 33.539934359999997D)
      return 50;
    if (paramDouble < 35.228995980000001D)
      return 49;
    if (paramDouble < 36.85025108D)
      return 48;
    if (paramDouble < 38.41241892D)
      return 47;
    if (paramDouble < 39.922566840000002D)
      return 46;
    if (paramDouble < 41.38651832D)
      return 45;
    if (paramDouble < 42.809140120000002D)
      return 44;
    if (paramDouble < 44.194549510000002D)
      return 43;
    if (paramDouble < 45.546267229999998D)
      return 42;
    if (paramDouble < 46.867332519999998D)
      return 41;
    if (paramDouble < 48.160391279999999D)
      return 40;
    if (paramDouble < 49.42776439D)
      return 39;
    if (paramDouble < 50.671501659999997D)
      return 38;
    if (paramDouble < 51.893424690000003D)
      return 37;
    if (paramDouble < 53.095161529999999D)
      return 36;
    if (paramDouble < 54.278174720000003D)
      return 35;
    if (paramDouble < 55.443784440000002D)
      return 34;
    if (paramDouble < 56.593187559999997D)
      return 33;
    if (paramDouble < 57.727473539999998D)
      return 32;
    if (paramDouble < 58.847637759999998D)
      return 31;
    if (paramDouble < 59.954592769999998D)
      return 30;
    if (paramDouble < 61.049177739999998D)
      return 29;
    if (paramDouble < 62.132166589999997D)
      return 28;
    if (paramDouble < 63.204274789999999D)
      return 27;
    if (paramDouble < 64.266165229999999D)
      return 26;
    if (paramDouble < 65.318453099999999D)
      return 25;
    if (paramDouble < 66.361710079999995D)
      return 24;
    if (paramDouble < 67.396467740000006D)
      return 23;
    if (paramDouble < 68.423220220000005D)
      return 22;
    if (paramDouble < 69.442426310000002D)
      return 21;
    if (paramDouble < 70.454510749999997D)
      return 20;
    if (paramDouble < 71.459864730000007D)
      return 19;
    if (paramDouble < 72.458845449999998D)
      return 18;
    if (paramDouble < 73.451774420000007D)
      return 17;
    if (paramDouble < 74.438934160000002D)
      return 16;
    if (paramDouble < 75.420562570000001D)
      return 15;
    if (paramDouble < 76.396843910000001D)
      return 14;
    if (paramDouble < 77.367894609999993D)
      return 13;
    if (paramDouble < 78.333740829999996D)
      return 12;
    if (paramDouble < 79.294282249999995D)
      return 11;
    if (paramDouble < 80.249232129999996D)
      return 10;
    if (paramDouble < 81.198013489999994D)
      return 9;
    if (paramDouble < 82.139569809999998D)
      return 8;
    if (paramDouble < 83.071994450000005D)
      return 7;
    if (paramDouble < 83.991735629999994D)
      return 6;
    if (paramDouble < 84.891661909999996D)
      return 5;
    if (paramDouble < 85.755416210000007D)
      return 4;
    if (paramDouble < 86.535369979999999D)
      return 3;
    if (paramDouble < 87.0D)
      return 2;
    return 1;
  }

  int detectOutOfPhase(char[] paramArrayOfChar, int paramInt)
  {
    if (paramArrayOfChar[(paramInt + 3)] > paramArrayOfChar[(paramInt + 2)] / '\003');
    while (paramArrayOfChar[(paramInt + 10)] > paramArrayOfChar[(paramInt + 9)] / '\003')
      return 1;
    if (paramArrayOfChar[(paramInt + 6)] > paramArrayOfChar[(paramInt + 7)] / '\003')
      return -1;
    if (paramArrayOfChar[(paramInt - 1)] > paramArrayOfChar[(paramInt + 1)] / '\003')
      return -1;
    return 0;
  }

  public void initializeMyLocationOverelay()
  {
    this.mapController.setZoom(8);
    GpsMyLocationProvider localGpsMyLocationProvider = new GpsMyLocationProvider(getBaseContext());
    localGpsMyLocationProvider.setLocationUpdateMinDistance(1000.0F);
    localGpsMyLocationProvider.setLocationUpdateMinTime(60L);
    this.myLocationOverlay = new MyLocationNewOverlay(getBaseContext(), localGpsMyLocationProvider, mapView);
    this.myLocationOverlay.enableMyLocation();
    this.myLocationOverlay.setUseSafeCanvas(true);
    mapView.getOverlays().add(this.myLocationOverlay);
    localGpsMyLocationProvider.startLocationProvider(this.myLocationOverlay);
    this.myLocationOverlay.setDrawAccuracyEnabled(true);
    this.myLocationOverlay.enableFollowLocation();
  }

  public void initializeTrafficOverlay()
  {
    this.mMyTrafficOverlay = new MyItemizedOverlay(this, new ArrayList());
    mapView.getOverlays().add(this.mMyTrafficOverlay);
  }

  void interactiveReceiveData(modesMessage parammodesMessage)
  {
    if ((this.check_crc) && (!parammodesMessage.crcok))
      return;
    aircraft localaircraft;
    if (this.Aircrafts.containsKey(Integer.valueOf(0xFFFFFF & parammodesMessage.addr)))
    {
      localaircraft = (aircraft)this.Aircrafts.get(Integer.valueOf(parammodesMessage.addr));
      localaircraft.seen = System.currentTimeMillis();
      localaircraft.messages = (1L + localaircraft.messages);
      localaircraft.identity = parammodesMessage.identity;
      if ((parammodesMessage.msgtype != 0) && (parammodesMessage.msgtype != 4) && (parammodesMessage.msgtype != 20))
        break label164;
      localaircraft.altitude = parammodesMessage.altitude;
    }
    label397: 
    while (true)
    {
      this.Aircrafts.replace(Integer.valueOf(localaircraft.addr), localaircraft);
      return;
      localaircraft = new aircraft();
      localaircraft.addr = parammodesMessage.addr;
      this.Aircrafts.put(Integer.valueOf(localaircraft.addr), localaircraft);
      break;
      label164: if (parammodesMessage.msgtype == 17)
        if ((parammodesMessage.metype >= 1) && (parammodesMessage.metype <= 4))
        {
          localaircraft.flight = parammodesMessage.flight;
        }
        else if ((parammodesMessage.metype >= 9) && (parammodesMessage.metype <= 18))
        {
          localaircraft.altitude = parammodesMessage.altitude;
          if (parammodesMessage.fflag != 0)
          {
            localaircraft.odd_cprlat = parammodesMessage.raw_latitude;
            localaircraft.odd_cprlon = parammodesMessage.raw_longitude;
            localaircraft.odd_cprtime = System.currentTimeMillis();
          }
          while (true)
          {
            if (Math.abs(localaircraft.even_cprtime - localaircraft.odd_cprtime) > 30000L)
              break label397;
            decodeCPR(localaircraft);
            StringBuilder localStringBuilder1 = new StringBuilder("decode CPR result  lat:");
            Object[] arrayOfObject1 = new Object[1];
            arrayOfObject1[0] = Double.valueOf(localaircraft.lat);
            StringBuilder localStringBuilder2 = localStringBuilder1.append(String.format("%.2f", arrayOfObject1)).append(" lon:");
            Object[] arrayOfObject2 = new Object[1];
            arrayOfObject2[0] = Double.valueOf(localaircraft.lon);
            Log.e("decodeCPR", String.format("%.2f", arrayOfObject2));
            break;
            localaircraft.even_cprlat = parammodesMessage.raw_latitude;
            localaircraft.even_cprlon = parammodesMessage.raw_longitude;
            localaircraft.even_cprtime = System.currentTimeMillis();
          }
        }
        else if ((parammodesMessage.metype == 19) && ((parammodesMessage.mesub == 1) || (parammodesMessage.mesub == 2)))
        {
          localaircraft.speed = parammodesMessage.velocity;
          localaircraft.track = parammodesMessage.heading;
        }
    }
  }

  int modesMessageLenByType(int paramInt)
  {
    if ((paramInt == 16) || (paramInt == 17) || (paramInt == 19) || (paramInt == 20) || (paramInt == 21))
      return 112;
    return 56;
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mContext = this;
    InitPreferences();
    if (this.mapOnlyView)
      setContentView(2130903042);
    while (true)
    {
      mapView = (MapView)findViewById(2131296267);
      mapView.setBuiltInZoomControls(true);
      mapView.setMultiTouchControls(true);
      mapView.setScrollContainer(true);
      mapView.setUseSafeCanvas(true);
      mapView.setUseDataConnection(true);
      mapView.setBackgroundColor(-16776961);
      this.mapController = ((MapController)mapView.getController());
      this.mapController.setZoom(8);
      this.mUsbManager = ((UsbManager)getSystemService("usb"));
      refreshDeviceList();
      this.mDumpTextView = ((TextView)findViewById(2131296273));
      this.mScrollView = ((ScrollView)findViewById(2131296272));
      this.Aircrafts = new ConcurrentHashMap();
      initializeMyLocationOverelay();
      ImageView localImageView = (ImageView)findViewById(2131296268);
      localImageView.setBackgroundColor(-16777216);
      localImageView.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          SerialConsoleActivity.this.displayLayerMenu();
        }
      });
      initializeTrafficOverlay();
      ((ImageView)findViewById(2131296269)).setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          SerialConsoleActivity.mapView.postInvalidate();
          SerialConsoleActivity.mapView.getController().zoomIn();
        }
      });
      ((ImageView)findViewById(2131296270)).setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          SerialConsoleActivity.mapView.postInvalidate();
          SerialConsoleActivity.mapView.getController().zoomOut();
        }
      });
      build_mag_lut();
      startThread();
      return;
      setContentView(2130903043);
    }
  }

  protected void onPause()
  {
    super.onPause();
    stopIoManager();
    if (sDriver != null);
    try
    {
      sDriver.close();
      sDriver = null;
      this.myLocationOverlay.disableFollowLocation();
      return;
    }
    catch (IOException localIOException)
    {
      while (true)
        Log.e(this.TAG, "Error onPause device: " + localIOException.getMessage(), localIOException);
    }
  }

  protected void onResume()
  {
    super.onResume();
    refreshDeviceList();
    SystemClock.sleep(1000L);
    Log.d(this.TAG, "Resumed, sDriver=" + sDriver);
    if (sDriver == null)
    {
      this.mDumpTextView.setText("No serial device.");
      this.exit = true;
    }
    while (true)
    {
      onDeviceStateChange();
      this.myLocationOverlay.enableFollowLocation();
      return;
      try
      {
        sDriver.open();
        this.exit = false;
        this.mDumpTextView.setText("Software Defined Radio device: ");
      }
      catch (IOException localIOException1)
      {
        Log.e(this.TAG, "Error setting up device: " + localIOException1.getMessage(), localIOException1);
        this.mDumpTextView.setText("Error opening device: " + localIOException1.getMessage());
      }
    }
    try
    {
      sDriver.close();
      sDriver = null;
      return;
    }
    catch (IOException localIOException2)
    {
      while (true)
        Log.e(this.TAG, "Error onResume device: " + localIOException1.getMessage(), localIOException1);
    }
  }

  public BitmapDrawable rotateDrawable(float paramFloat)
  {
    Bitmap localBitmap1 = BitmapFactory.decodeResource(getApplicationContext().getResources(), 2130837504);
    Bitmap localBitmap2 = localBitmap1.copy(Bitmap.Config.ARGB_8888, true);
    localBitmap2.eraseColor(0);
    Canvas localCanvas = new Canvas(localBitmap2);
    Matrix localMatrix = new Matrix();
    localMatrix.setRotate(paramFloat, localCanvas.getWidth() / 2, localCanvas.getHeight() / 2);
    localCanvas.drawBitmap(localBitmap1, localMatrix, null);
    return new BitmapDrawable(localBitmap2);
  }

  private static class DeviceEntry
  {
    public UsbDevice device;
    public UsbSerialDriver driver;

    DeviceEntry(UsbDevice paramUsbDevice, UsbSerialDriver paramUsbSerialDriver)
    {
      this.device = paramUsbDevice;
      this.driver = paramUsbSerialDriver;
    }
  }

  class aircraft
  {
    int addr;
    int altitude;
    int even_cprlat;
    int even_cprlon;
    long even_cprtime;
    String flight = "        ";
    int identity;
    double lat;
    double lon;
    long messages;
    int odd_cprlat;
    int odd_cprlon;
    long odd_cprtime;
    long seen;
    int speed;
    int track;

    aircraft()
    {
    }
  }

  class modesMessage
  {
    byte aa1;
    byte aa2;
    byte aa3;
    int addr;
    int aircraft_type;
    int altitude;
    int ca;
    int crc;
    boolean crcok;
    int dr;
    int errorbit;
    int ew_dir;
    int ew_velocity;
    int fflag;
    String flight = "        ";
    int fs;
    int heading;
    int heading_is_valid;
    int identity;
    int mesub;
    int metype;
    char[] msg = new char[14];
    int msgbits;
    int msgtype;
    int ns_dir;
    int ns_velocity;
    int phase_corrected;
    int raw_latitude;
    int raw_longitude;
    int tflag;
    long time;
    int um;
    int unit;
    int velocity;
    int vert_rate;
    int vert_rate_sign;
    int vert_rate_source;

    modesMessage()
    {
    }
  }
}

/* Location:           /Users/kevinfinisterre/Desktop/ADSB_USB_playstore.jar
 * Qualified Name:     com.wilsonae.android.usbserial.SerialConsoleActivity
 * JD-Core Version:    0.6.2
 */
