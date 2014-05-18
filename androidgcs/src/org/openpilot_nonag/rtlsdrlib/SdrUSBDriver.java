package org.openpilot_nonag.rtlsdrlib;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.util.Log;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class SdrUSBDriver extends CommonUsbSerialDriver
{
  private static final int BULK_TIMEOUT = 0;
  private static final byte CTRL_IN = -64;
  private static final byte CTRL_OUT = 64;
  private static final int DEF_RTL_XTAL_FREQ = 28800000;
  private static final char DEMOD_CTL = '　';
  private static final char DEMOD_CTL_1 = '》';
  private static final int E4K_CHECK_ADDR = 2;
  private static final int E4K_CHECK_VAL = 64;
  private static final int E4K_I2C_ADDR = 200;
  private static final char EEPROM_ADDR = ' ';
  private static final int FC0012_CHECK_ADDR = 0;
  private static final int FC0012_CHECK_VAL = 161;
  private static final int FC0012_I2C_ADDR = 198;
  private static final int FC0013_CHECK_ADDR = 0;
  private static final int FC0013_CHECK_VAL = 163;
  private static final int FC0013_I2C_ADDR = 198;
  private static final int FC2580_CHECK_ADDR = 1;
  private static final int FC2580_CHECK_VAL = 86;
  private static final int FC2580_I2C_ADDR = 172;
  private static final char GPD = '〄';
  private static final char GPO = '、';
  private static final char GPOE = '〃';
  private static final byte IICB = 6;
  private static final int LIBUSB_ENDPOINT_IN = 128;
  private static final int LIBUSB_ENDPOINT_OUT = 0;
  private static final int LIBUSB_REQUEST_TYPE_VENDOR = 64;
  private static final int MAX_RTL_XTAL_FREQ = 28801000;
  private static final int MAX_SAMP_RATE = 3200000;
  private static final int MIN_RTL_XTAL_FREQ = 28799000;
  private static final int R820T_CHECK_ADDR = 0;
  private static final int R820T_CHECK_VAL = 105;
  private static final int R820T_I2C_ADDR = 52;
  private static final long R820T_IF_FREQ = 3570000L;
  private static final int R828D_I2C_ADDR = 116;
  private static final int REQTYPE_HOST_TO_DEVICE = 65;
  private static final int RTLSDR_TUNER_E4000 = 1;
  private static final int RTLSDR_TUNER_FC0012 = 2;
  private static final int RTLSDR_TUNER_FC0013 = 3;
  private static final int RTLSDR_TUNER_FC2580 = 4;
  private static final int RTLSDR_TUNER_R820T = 5;
  private static final int RTLSDR_TUNER_R828D = 6;
  private static final int RTLSDR_TUNER_UNKNOWN = 0;
  private static final byte SYSB = 2;
  private static final String TAG = SdrUSBDriver.class.getSimpleName();
  private static final byte USBB = 1;
  private static final char USB_EPA_CTL = 'ⅈ';
  private static final char USB_EPA_MAXPKT = '⅘';
  private static final char USB_SYSCTL = ' ';
  private static final int USB_WRITE_TIMEOUT_MILLIS = 15000;
  private static int corr = 0;
  private static long rtl_xtal = 28800000L;
  private static long tun_xtal = 28800000L;
  private final boolean DEBUG = false;
  private final int MODES_AUTO_GAIN = -100;
  private final int MODES_DEFAULT_FREQ = 1090000000;
  private final int MODES_DEFAULT_RATE = 2000000;
  private final int MODES_MAX_GAIN = 999999;
  private boolean direct_sampling = false;
  private boolean enable_agc = false;
  private long freq = 1090000000L;
  private int gain = 999999;
  private UsbEndpoint mReadEndpoint;
  RtlSdr_tuner_iface myTuner;
  private long offs_freq = 0L;
  private int ppm_error = 0;
  private long rate = 3200000L;
  boolean set_gain_mode = true;
  private boolean tuner = false;
  private boolean tuner_exit = false;
  private boolean tuner_init = false;
  private boolean tuner_set_bw = true;
  private boolean tuner_set_gain = true;
  private int tuner_type = 0;

  public SdrUSBDriver(UsbDevice paramUsbDevice, UsbDeviceConnection paramUsbDeviceConnection)
  {
    super(paramUsbDevice, paramUsbDeviceConnection);
  }

  private static double APPLY_PPM_CORR(long paramLong, int paramInt)
  {
    return paramLong * (1.0D + paramInt / 1000000.0D);
  }

  private double TWO_POW(int paramInt)
  {
    return 1 << paramInt;
  }

  private void adsbInit()
  {
    boolean bool;
    if (this.gain == -100)
    {
      bool = false;
      rtlsdr_set_tuner_gain_mode(bool);
      if (this.gain == -100)
        break label293;
      if (this.gain == 999999)
      {
        int[] arrayOfInt = new int[100];
        this.gain = arrayOfInt[(-1 + rtlsdr_get_tuner_gains(arrayOfInt))];
        String str3 = TAG;
        StringBuilder localStringBuilder3 = new StringBuilder("Max available gain is: ");
        Object[] arrayOfObject3 = new Object[1];
        arrayOfObject3[0] = Double.valueOf(this.gain / 10.0D);
        Log.e(str3, String.format("%.2f", arrayOfObject3));
      }
      rtlsdr_set_tuner_gain(this.gain);
      String str2 = TAG;
      StringBuilder localStringBuilder2 = new StringBuilder("Setting gain to: ");
      Object[] arrayOfObject2 = new Object[1];
      arrayOfObject2[0] = Double.valueOf(this.gain / 10.0D);
      Log.e(str2, String.format("%.2f", arrayOfObject2));
    }
    while (true)
    {
      rtlsdr_set_freq_correction(this.ppm_error);
      if (this.enable_agc)
        rtlsdr_set_agc_mode(true);
      rtlsdr_set_center_freq(this.freq);
      rtlsdr_set_sample_rate(2000000L);
      rtlsdr_reset_buffer();
      String str1 = TAG;
      StringBuilder localStringBuilder1 = new StringBuilder("Gain reported by device: ");
      Object[] arrayOfObject1 = new Object[1];
      arrayOfObject1[0] = Double.valueOf(rtlsdr_get_tuner_gain() / 10.0D);
      Log.e(str1, String.format("%.2f", arrayOfObject1));
      return;
      bool = true;
      break;
      label293: Log.e(TAG, "Using automatic gain control.\n");
    }
  }

  public static Map<Integer, int[]> getSupportedDevices()
  {
    LinkedHashMap localLinkedHashMap = new LinkedHashMap();
    localLinkedHashMap.put(Integer.valueOf(3034), new int[] { 10296, 10290 });
    localLinkedHashMap.put(Integer.valueOf(3277), new int[] { 169, 179, 180, 181, 183, 184, 185, 192, 198, 211, 215, 224 });
    return localLinkedHashMap;
  }

  private static int libusb_control_transfer(byte paramByte1, byte paramByte2, char paramChar1, char paramChar2, byte[] paramArrayOfByte, char paramChar3, int paramInt)
  {
    return mConnection.controlTransfer(paramByte1, paramByte2, paramChar1, paramChar2, paramArrayOfByte, paramChar3, paramInt);
  }

  private int rtlsdr_deinit_baseband()
  {
    if ((this.tuner) && (!this.tuner_exit))
    {
      rtlsdr_set_i2c_repeater(true);
      rtlsdr_set_i2c_repeater(false);
    }
    int i = rtlsdr_write_reg((byte)2, '　', ' ', (byte)1);
    if (i != 0)
      Log.e(TAG, " ret " + i + "Line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
    this.tuner_exit = true;
    this.tuner = false;
    return 0;
  }

  private char rtlsdr_demod_read_reg(byte paramByte1, char paramChar, byte paramByte2)
  {
    byte[] arrayOfByte = new byte[2];
    char c = (char)paramByte1;
    int i = libusb_control_transfer((byte)-64, (byte)0, (char)(0x20 | paramChar << '\b'), c, arrayOfByte, (char)paramByte2, 15000);
    if (i < 0)
      Log.e(TAG, "rtlsdr_demod_read_reg error " + i);
    return (char)(arrayOfByte[1] << 8 | arrayOfByte[0]);
  }

  private int rtlsdr_demod_write_reg(byte paramByte1, char paramChar1, char paramChar2, byte paramByte2)
  {
    byte[] arrayOfByte = new byte[2];
    char c1 = (char)(paramByte1 | 0x10);
    char c2 = (char)(0x20 | paramChar1 << '\b');
    if (paramByte2 == 1)
      arrayOfByte[0] = ((byte)(paramChar2 & 0xFF));
    while (true)
    {
      arrayOfByte[1] = ((byte)(paramChar2 & 0xFF));
      byte b = libusb_control_transfer((byte)64, (byte)0, c2, c1, arrayOfByte, (char)paramByte2, 15000);
      if (b < 0)
        Log.e(TAG, "rtlsdr_demod_write_reg failed " + b);
      rtlsdr_demod_read_reg((byte)10, '\001', (byte)1);
      if (b != paramByte2)
        break;
      return 0;
      arrayOfByte[0] = ((byte)(paramChar2 >> '\b'));
    }
    return -1;
  }

  private long rtlsdr_get_center_freq()
  {
    return this.freq;
  }

  private boolean rtlsdr_get_direct_sampling()
  {
    return this.direct_sampling;
  }

  private int rtlsdr_get_freq_correction()
  {
    return corr;
  }

  private int rtlsdr_get_offset_tuning()
  {
    if (this.offs_freq < 0L)
      return 1;
    return 0;
  }

  private long rtlsdr_get_sample_rate()
  {
    return this.rate;
  }

  public static int rtlsdr_get_tuner_clock()
  {
    long[] arrayOfLong = new long[1];
    if (rtlsdr_get_xtal_freq(new long[1], arrayOfLong) < 0)
      return 0;
    return (int)arrayOfLong[0];
  }

  private long rtlsdr_get_tuner_gain()
  {
    return this.gain;
  }

  private int rtlsdr_get_tuner_gains(int[] paramArrayOfInt)
  {
    int[] arrayOfInt1 = { -10, 15, 40, 65, 90, 115, 140, 165, 190, 215, 240, 290, 340, 420 };
    int[] arrayOfInt2 = { -99, -40, 71, 179, 192 };
    int[] arrayOfInt3 = { -99, -73, -65, -63, -60, -58, -54, 58, 61, 63, 65, 67, 68, 70, 71, 179, 181, 182, 184, 186, 188, 191, 197 };
    int[] arrayOfInt4 = new int[1];
    int[] arrayOfInt5 = new int[29];
    arrayOfInt5[1] = 9;
    arrayOfInt5[2] = 14;
    arrayOfInt5[3] = 27;
    arrayOfInt5[4] = 37;
    arrayOfInt5[5] = 77;
    arrayOfInt5[6] = 87;
    arrayOfInt5[7] = 125;
    arrayOfInt5[8] = 144;
    arrayOfInt5[9] = 157;
    arrayOfInt5[10] = 166;
    arrayOfInt5[11] = 197;
    arrayOfInt5[12] = 207;
    arrayOfInt5[13] = 229;
    arrayOfInt5[14] = 254;
    arrayOfInt5[15] = 280;
    arrayOfInt5[16] = 297;
    arrayOfInt5[17] = 328;
    arrayOfInt5[18] = 338;
    arrayOfInt5[19] = 364;
    arrayOfInt5[20] = 372;
    arrayOfInt5[21] = 386;
    arrayOfInt5[22] = 402;
    arrayOfInt5[23] = 421;
    arrayOfInt5[24] = 434;
    arrayOfInt5[25] = 439;
    arrayOfInt5[26] = 445;
    arrayOfInt5[27] = 480;
    arrayOfInt5[28] = 496;
    int[] arrayOfInt6 = new int[1];
    switch (this.tuner_type)
    {
    default:
      int i1 = arrayOfInt6.length;
      System.arraycopy(arrayOfInt6, 0, paramArrayOfInt, 0, arrayOfInt6.length);
      return i1;
    case 1:
      int n = arrayOfInt1.length;
      System.arraycopy(arrayOfInt1, 0, paramArrayOfInt, 0, arrayOfInt1.length);
      return n;
    case 2:
      int m = arrayOfInt2.length;
      System.arraycopy(arrayOfInt2, 0, paramArrayOfInt, 0, arrayOfInt2.length);
      return m;
    case 3:
      int k = arrayOfInt3.length;
      System.arraycopy(arrayOfInt3, 0, paramArrayOfInt, 0, arrayOfInt3.length);
      return k;
    case 4:
      int j = arrayOfInt4.length;
      System.arraycopy(arrayOfInt4, 0, paramArrayOfInt, 0, arrayOfInt4.length);
      return j;
    case 5:
    }
    int i = arrayOfInt5.length;
    System.arraycopy(arrayOfInt5, 0, paramArrayOfInt, 0, arrayOfInt5.length);
    return i;
  }

  private int rtlsdr_get_tuner_type()
  {
    return this.tuner_type;
  }

  private static int rtlsdr_get_xtal_freq(long[] paramArrayOfLong1, long[] paramArrayOfLong2)
  {
    paramArrayOfLong1[0] = (()APPLY_PPM_CORR(rtl_xtal, corr));
    paramArrayOfLong2[0] = (()APPLY_PPM_CORR(tun_xtal, corr));
    return 0;
  }

  private static int rtlsdr_i2c_read(byte paramByte1, byte[] paramArrayOfByte, byte paramByte2)
  {
    return rtlsdr_read_array((byte)6, (char)paramByte1, paramArrayOfByte, paramByte2);
  }

  public static int rtlsdr_i2c_read_fn(byte paramByte1, byte[] paramArrayOfByte, byte paramByte2)
  {
    return rtlsdr_i2c_read(paramByte1, paramArrayOfByte, paramByte2);
  }

  public static byte rtlsdr_i2c_read_reg(int paramInt1, int paramInt2)
  {
    char c = (char)paramInt1;
    byte[] arrayOfByte1 = new byte[2];
    byte[] arrayOfByte2 = new byte[2];
    arrayOfByte2[0] = ((byte)paramInt2);
    rtlsdr_write_array((byte)6, c, arrayOfByte2, (byte)1);
    rtlsdr_read_array((byte)6, c, arrayOfByte1, (byte)1);
    return arrayOfByte1[0];
  }

  private static int rtlsdr_i2c_write(byte paramByte1, byte[] paramArrayOfByte, byte paramByte2)
  {
    return rtlsdr_write_array((byte)6, (char)paramByte1, paramArrayOfByte, paramByte2);
  }

  public static int rtlsdr_i2c_write_fn(byte paramByte1, byte[] paramArrayOfByte, byte paramByte2)
  {
    return rtlsdr_i2c_write(paramByte1, paramArrayOfByte, paramByte2);
  }

  public static int rtlsdr_i2c_write_reg(byte paramByte, char paramChar1, char paramChar2)
  {
    char c = (char)paramByte;
    byte[] arrayOfByte = new byte[2];
    arrayOfByte[0] = ((byte)paramChar1);
    arrayOfByte[1] = ((byte)paramChar2);
    return rtlsdr_write_array((byte)6, c, arrayOfByte, (byte)2);
  }

  private void rtlsdr_init_baseband()
  {
    char[] arrayOfChar = { 202, 220, 215, 216, 224, 242, 14, 53, 6, 80, 156, 13, 113, 17, 20, 113, 116, 25, 65, 165 };
    int i = rtlsdr_write_reg((byte)1, ' ', '\t', (byte)1);
    if (i != 0)
      Log.e(TAG, " ret " + i + "Line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
    int j = rtlsdr_write_reg((byte)1, '⅘', '\002', (byte)2);
    if (j != 0)
      Log.e(TAG, " ret " + j + "Line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
    int k = rtlsdr_write_reg((byte)1, 'ⅈ', 'ဂ', (byte)2);
    if (k != 0)
      Log.e(TAG, " ret " + k + "Line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
    int m = rtlsdr_write_reg((byte)2, '》', '"', (byte)1);
    if (m != 0)
      Log.e(TAG, " ret " + m + "Line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
    int n = rtlsdr_write_reg((byte)2, '　', 'è', (byte)1);
    if (n != 0)
      Log.e(TAG, " ret " + n + "Line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
    int i1 = rtlsdr_demod_write_reg((byte)1, '\001', '\024', (byte)1);
    if (i1 != 0)
      Log.e(TAG, " ret " + i1 + "Line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
    int i2 = rtlsdr_demod_write_reg((byte)1, '\001', '\020', (byte)1);
    if (i2 != 0)
      Log.e(TAG, " ret " + i2 + "Line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
    int i3 = rtlsdr_demod_write_reg((byte)1, '\025', '\000', (byte)1);
    if (i3 != 0)
      Log.e(TAG, " ret " + i3 + "Line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
    int i4 = rtlsdr_demod_write_reg((byte)1, '\026', '\000', (byte)2);
    if (i4 != 0)
      Log.e(TAG, " ret " + i4 + "Line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
    int i5 = 0;
    if (i5 >= 6);
    for (int i7 = 0; ; i7++)
    {
      if (i7 >= arrayOfChar.length)
      {
        int i9 = rtlsdr_demod_write_reg((byte)0, '\031', '\005', (byte)1);
        if (i9 != 0)
          Log.e(TAG, " ret " + i9 + "Line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        int i10 = rtlsdr_demod_write_reg((byte)1, '', 'ð', (byte)1);
        if (i10 != 0)
          Log.e(TAG, " ret " + i10 + "Line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        int i11 = rtlsdr_demod_write_reg((byte)1, '', '\017', (byte)1);
        if (i11 != 0)
          Log.e(TAG, " ret " + i11 + "Line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        int i12 = rtlsdr_demod_write_reg((byte)1, '\021', '\000', (byte)1);
        if (i12 != 0)
          Log.e(TAG, " ret " + i12 + "Line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        int i13 = rtlsdr_demod_write_reg((byte)1, '\004', '\000', (byte)1);
        if (i13 != 0)
          Log.e(TAG, " ret " + i13 + "Line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        int i14 = rtlsdr_demod_write_reg((byte)0, 'a', '`', (byte)1);
        if (i14 != 0)
          Log.e(TAG, " ret " + i14 + "Line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        int i15 = rtlsdr_demod_write_reg((byte)0, '\006', '', (byte)1);
        if (i15 != 0)
          Log.e(TAG, " ret " + i15 + "Line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        int i16 = rtlsdr_demod_write_reg((byte)1, '±', '\033', (byte)1);
        if (i16 != 0)
          Log.e(TAG, " ret " + i16 + "Line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        int i17 = rtlsdr_demod_write_reg((byte)0, '\r', '', (byte)1);
        if (i17 != 0)
          Log.e(TAG, " ret " + i17 + "Line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        return;
        int i6 = rtlsdr_demod_write_reg((byte)1, (char)(i5 + 22), '\000', (byte)1);
        if (i6 != 0)
          Log.e(TAG, " ret " + i6 + "Line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
        i5++;
        break;
      }
      int i8 = rtlsdr_demod_write_reg((byte)1, (char)(i7 + 28), arrayOfChar[i7], (byte)1);
      if (i8 != 0)
        Log.e(TAG, " ret " + i8 + "Line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
    }
  }

  private RtlSdr_tuner_iface rtlsdr_open()
    throws IOException
  {
    Log.e(TAG, "rtlsdr_open()");
    int i = rtlsdr_write_reg((byte)1, ' ', '\t', (byte)1);
    if (i < 0)
    {
      if (i != 0)
        Log.e(TAG, " ret " + i + "Line " + Thread.currentThread().getStackTrace()[2].getLineNumber());
      Log.e(TAG, "Resetting device...");
    }
    rtlsdr_init_baseband();
    rtlsdr_set_i2c_repeater(true);
    if ((0xFF & rtlsdr_i2c_read_reg(52, 0)) == 105)
    {
      Log.e(TAG, "Found Rafael Micro R820T tuner\n");
      this.tuner_type = 5;
      rtlsdr_demod_write_reg((byte)1, '±', '\032', (byte)1);
      rtlsdr_demod_write_reg((byte)0, '\b', 'M', (byte)1);
      rtlsdr_set_if_freq(3570000L);
      rtlsdr_demod_write_reg((byte)1, '\025', '\001', (byte)1);
      r820T_tuner localr820T_tuner2 = new r820T_tuner();
      localr820T_tuner2.init(0);
      rtlsdr_set_i2c_repeater(false);
      tun_xtal = rtl_xtal;
      return localr820T_tuner2;
    }
    int j = rtlsdr_i2c_read_reg(116, 0);
    r820T_tuner localr820T_tuner1 = null;
    if (j == 105)
    {
      Log.e(TAG, "Found Rafael Micro R828D tuner\n");
      this.tuner_type = 6;
      localr820T_tuner1 = new r820T_tuner();
      localr820T_tuner1.init(0);
      rtlsdr_set_i2c_repeater(false);
    }
    if ((0xFF & rtlsdr_i2c_read_reg(200, 2)) == 64)
    {
      Log.e(TAG, "Found Elonics E4000 tuner");
      this.tuner_type = 1;
      e4k_tuner locale4k_tuner = new e4k_tuner();
      locale4k_tuner.init(0);
      rtlsdr_set_i2c_repeater(false);
      tun_xtal = rtl_xtal;
      return locale4k_tuner;
    }
    if ((0xFF & rtlsdr_i2c_read_reg(198, 0)) == 163)
    {
      Log.e(TAG, "Found Fitipower FC0013 tuner");
      this.tuner_type = 3;
      tun_xtal = rtl_xtal;
      fc0013_tuner localfc0013_tuner = new fc0013_tuner();
      localfc0013_tuner.init(0);
      rtlsdr_set_i2c_repeater(false);
      return localfc0013_tuner;
    }
    rtlsdr_set_gpio_output((byte)5);
    rtlsdr_tuner_reset();
    if ((0x7F & (0xFF & rtlsdr_i2c_read_reg(172, 1))) == 86)
    {
      Log.e(TAG, "Found FCI 2580 tuner");
      this.tuner_type = 4;
      fc2580_tuner localfc2580_tuner = new fc2580_tuner();
      localfc2580_tuner.init(0);
      rtlsdr_set_i2c_repeater(false);
      tun_xtal = rtl_xtal;
      return localfc2580_tuner;
    }
    if ((0xFF & rtlsdr_i2c_read_reg(198, 0)) == 161)
    {
      Log.e(TAG, "Found Fitipower FC0012 tuner");
      rtlsdr_set_gpio_output((byte)6);
      this.tuner_type = 2;
      fc0012_tuner localfc0012_tuner = new fc0012_tuner();
      localfc0012_tuner.init(0);
      rtlsdr_set_i2c_repeater(false);
      tun_xtal = rtl_xtal;
      return localfc0012_tuner;
    }
    if (this.tuner_type == 0)
    {
      Log.e(TAG, "No supported tuner found\n");
      rtlsdr_set_direct_sampling(true);
      return null;
    }
    tun_xtal = rtl_xtal;
    rtlsdr_set_i2c_repeater(false);
    return localr820T_tuner1;
  }

  private static int rtlsdr_read_array(byte paramByte1, char paramChar, byte[] paramArrayOfByte, byte paramByte2)
  {
    char c = (char)(paramByte1 << 8);
    int i = libusb_control_transfer((byte)-64, (byte)0, paramChar, c, paramArrayOfByte, (char)paramByte2, 15000);
    Log.e(TAG + ":rtlsdr_read_array", "addr 0x" + Integer.toHexString(paramChar) + " index 0x" + Integer.toHexString(c) + " array " + paramArrayOfByte[0] + " len " + paramByte2);
    if (i < 0)
      Log.e(TAG, "rtlsdr_read_array error" + paramByte2);
    return i;
  }

  private int rtlsdr_read_eeprom(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, char paramChar)
  {
    int i = -3;
    byte[] arrayOfByte = new byte[2];
    if (paramChar + paramArrayOfByte2[0] > 256)
    {
      Log.e(TAG, " rtlsdr_read_eeprom offset + len > 256");
      i = -2;
    }
    int j;
    do
    {
      return i;
      j = rtlsdr_write_array((byte)6, ' ', paramArrayOfByte2, (byte)1);
    }
    while (j < 0);
    for (char c = '\000'; ; c++)
    {
      if (c >= paramChar)
        return j;
      arrayOfByte[0] = paramArrayOfByte1[c];
      j = rtlsdr_read_array((byte)6, ' ', arrayOfByte, (byte)1);
      if (j < 0)
      {
        Log.e(TAG, " rtlsdr_read_eeprom FAILED");
        return i;
      }
    }
  }

  private char rtlsdr_read_reg(byte paramByte1, char paramChar, byte paramByte2)
  {
    byte[] arrayOfByte = new byte[2];
    int i = libusb_control_transfer((byte)-64, (byte)0, paramChar, (char)(paramByte1 << 8), arrayOfByte, (char)paramByte2, 15000);
    if (i < 0)
      Log.e(TAG, "rtlsdr_read_reg failed " + i);
    return (char)(arrayOfByte[1] << 8 | arrayOfByte[0]);
  }

  private int rtlsdr_read_sync(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    return read(paramArrayOfByte, 0);
  }

  private int rtlsdr_reset_buffer()
  {
    rtlsdr_write_reg((byte)1, 'ⅈ', 'ဂ', (byte)2);
    rtlsdr_write_reg((byte)1, 'ⅈ', '\000', (byte)2);
    return 0;
  }

  private int rtlsdr_set_agc_mode(boolean paramBoolean)
  {
    if (paramBoolean);
    for (int i = 37; ; i = 5)
      return rtlsdr_demod_write_reg((byte)0, '\031', (char)i, (byte)1);
  }

  private int rtlsdr_set_center_freq(long paramLong)
  {
    int i = -1;
    if (this.direct_sampling)
      i = rtlsdr_set_if_freq(paramLong);
    while (true)
      if (i <= 0)
      {
        this.freq = paramLong;
        return i;
        if (this.myTuner == null)
          continue;
        rtlsdr_set_i2c_repeater(true);
        try
        {
          int j = this.myTuner.set_freq(0, paramLong);
          i = j;
          rtlsdr_set_i2c_repeater(false);
        }
        catch (IOException localIOException1)
        {
          while (true)
          {
            localIOException1.printStackTrace();
            Log.e(TAG, localIOException1.toString());
            try
            {
              this.myTuner.set_freq(0, paramLong - this.offs_freq);
            }
            catch (IOException localIOException2)
            {
              localIOException2.printStackTrace();
              Log.e(TAG, localIOException1.toString());
            }
          }
        }
      }
    this.freq = 0L;
    return i;
  }

  private int rtlsdr_set_direct_sampling(boolean paramBoolean)
  {
    if (paramBoolean)
      if ((this.tuner) && (this.tuner_exit))
      {
        rtlsdr_set_i2c_repeater(true);
        if (this.myTuner == null);
      }
    while (true)
    {
      int k;
      try
      {
        this.myTuner.exit(0);
        rtlsdr_set_i2c_repeater(false);
        int n = 0x0 | rtlsdr_demod_write_reg((byte)1, '±', '\032', (byte)1) | rtlsdr_demod_write_reg((byte)1, '\025', '\000', (byte)1) | rtlsdr_demod_write_reg((byte)0, '\b', 'M', (byte)1);
        if (paramBoolean)
        {
          i1 = 144;
          k = n | rtlsdr_demod_write_reg((byte)0, '\006', (char)i1, (byte)1);
          Log.e(TAG, "rtlsdr_set_direct_sampling Enabled direct sampling mode, input " + paramBoolean);
          this.direct_sampling = paramBoolean;
          return k | rtlsdr_set_center_freq(this.freq);
        }
      }
      catch (IOException localIOException2)
      {
        Log.e(TAG, "myTuner.exit(0) FAILED " + localIOException2.toString());
        localIOException2.printStackTrace();
        continue;
        int i1 = 128;
        continue;
      }
      RtlSdr_tuner_iface localRtlSdr_tuner_iface = this.myTuner;
      int i = 0;
      if (localRtlSdr_tuner_iface != null)
      {
        boolean bool = this.tuner_init;
        i = 0;
        if (bool)
          rtlsdr_set_i2c_repeater(true);
      }
      try
      {
        int m = this.myTuner.init(0);
        i = 0x0 | m;
        rtlsdr_set_i2c_repeater(true);
        if (this.tuner_type == 5)
        {
          j = i | rtlsdr_set_if_freq(3570000L) | rtlsdr_demod_write_reg((byte)1, '\025', '\001', (byte)1);
          k = j | rtlsdr_demod_write_reg((byte)0, '\006', '', (byte)1);
          Log.e(TAG, "Disabled direct sampling mode\n");
          this.direct_sampling = false;
        }
      }
      catch (IOException localIOException1)
      {
        while (true)
        {
          localIOException1.printStackTrace();
          Log.e(TAG, localIOException1.toString());
          i = 0;
          continue;
          int j = i | rtlsdr_set_if_freq(0L) | rtlsdr_demod_write_reg((byte)0, '\b', 'Í', (byte)1) | rtlsdr_demod_write_reg((byte)1, '±', '\033', (byte)1);
        }
      }
    }
  }

  private int rtlsdr_set_freq_correction(int paramInt)
  {
    long[] arrayOfLong1 = new long[1];
    long[] arrayOfLong2 = new long[1];
    if (corr == paramInt)
      return -2;
    corr = paramInt;
    int i = 0x0 | rtlsdr_set_sample_freq_correction(paramInt);
    if (rtlsdr_get_xtal_freq(arrayOfLong1, arrayOfLong2) < 0)
      return -3;
    if (this.freq != 0L)
      i |= rtlsdr_set_center_freq(this.freq);
    return i;
  }

  private void rtlsdr_set_gpio_bit(byte paramByte, boolean paramBoolean)
  {
    int i = (byte)(1 << paramByte);
    int j = rtlsdr_read_reg((byte)2, '、', (byte)1);
    if (paramBoolean);
    for (int k = j | i; ; k = j & (i ^ 0xFFFFFFFF))
    {
      rtlsdr_write_reg((byte)2, '、', (char)k, (byte)1);
      return;
    }
  }

  private void rtlsdr_set_gpio_output(byte paramByte)
  {
    int i = (byte)(1 << paramByte);
    rtlsdr_write_reg((byte)2, '、', (char)(rtlsdr_read_reg((byte)2, '〄', (byte)1) & (i ^ 0xFFFFFFFF)), (byte)1);
    rtlsdr_write_reg((byte)2, '〃', (char)(i | rtlsdr_read_reg((byte)2, '〃', (byte)1)), (byte)1);
  }

  private void rtlsdr_set_i2c_repeater(boolean paramBoolean)
  {
    if (paramBoolean);
    for (int i = 24; ; i = 16)
    {
      rtlsdr_demod_write_reg((byte)1, '\001', (char)i, (byte)1);
      return;
    }
  }

  private int rtlsdr_set_if_freq(long paramLong)
  {
    long l = ()(-1.0D * (paramLong * TWO_POW(22) / rtl_xtal));
    return rtlsdr_demod_write_reg((byte)1, '\031', (char)(int)(0x3F & l >> 16), (byte)1) | rtlsdr_demod_write_reg((byte)1, '\032', (char)(int)(0xFF & l >> 8), (byte)1) | rtlsdr_demod_write_reg((byte)1, '\033', (char)(int)(l & 0xFF), (byte)1);
  }

  private int rtlsdr_set_offset_tuning(boolean paramBoolean)
  {
    if (this.tuner_type == 5)
      return -2;
    if (this.direct_sampling)
      return -3;
    if (paramBoolean);
    for (long l = 170L * (this.rate / 2L) / 100L; ; l = 0L)
    {
      this.offs_freq = l;
      if (this.tuner)
      {
        rtlsdr_set_i2c_repeater(true);
        rtlsdr_set_i2c_repeater(false);
      }
      boolean bool = this.freq < this.offs_freq;
      int i = 0;
      if (bool)
        i = 0x0 | rtlsdr_set_center_freq(this.freq);
      return i;
    }
  }

  private int rtlsdr_set_sample_freq_correction(int paramInt)
  {
    int i = (char)(int)(paramInt * -1 * TWO_POW(24) / 1000000.0D);
    return 0x0 | rtlsdr_demod_write_reg((byte)1, '?', (char)(i & 0xFF), (byte)1) | rtlsdr_demod_write_reg((byte)1, '>', (char)(0x3F & i >> 8), (byte)1);
  }

  private int rtlsdr_set_sample_rate(long paramLong)
  {
    if (paramLong > 3200000L)
      paramLong = 3200000L;
    long l = 0xFFFFFFFC & ()(rtl_xtal * TWO_POW(22) / paramLong);
    double d = rtl_xtal * TWO_POW(22) / l;
    if (paramLong != d)
      Log.e(TAG, "Exact sample rate is: " + d + " Hz");
    if ((this.myTuner != null) && (this.tuner_set_bw))
      rtlsdr_set_i2c_repeater(true);
    try
    {
      this.myTuner.set_bw(0, (int)d);
      if (this.myTuner == null);
    }
    catch (IOException localIOException1)
    {
      try
      {
        this.myTuner.set_bw(0, (int)d);
        rtlsdr_set_i2c_repeater(false);
        this.rate = (()d);
        int i = 0x0 | rtlsdr_demod_write_reg((byte)1, '', (char)(0xFFFF & (char)(int)(l >> 16)), (byte)2) | rtlsdr_demod_write_reg((byte)1, '¡', (char)(int)(0xFFFF & l), (byte)2) | rtlsdr_set_sample_freq_correction(corr) | rtlsdr_demod_write_reg((byte)1, '\001', '\024', (byte)1) | rtlsdr_demod_write_reg((byte)1, '\001', '\020', (byte)1);
        if (this.offs_freq != 0L)
          rtlsdr_set_offset_tuning(true);
        return i;
        localIOException1 = localIOException1;
        Log.e(TAG, localIOException1.toString());
        localIOException1.printStackTrace();
      }
      catch (IOException localIOException2)
      {
        while (true)
        {
          Log.e(TAG, "error set_bw " + d);
          localIOException2.printStackTrace();
        }
      }
    }
  }

  private int rtlsdr_set_testmode(boolean paramBoolean)
  {
    if (paramBoolean);
    for (int i = 3; ; i = 5)
      return rtlsdr_demod_write_reg((byte)0, '\031', (char)i, (byte)1);
  }

  private int rtlsdr_set_tuner_gain(int paramInt)
  {
    if (this.tuner_set_gain)
      rtlsdr_set_i2c_repeater(true);
    try
    {
      this.myTuner.set_gain(0, paramInt);
      rtlsdr_set_i2c_repeater(false);
      this.gain = paramInt;
      return 0;
    }
    catch (IOException localIOException)
    {
      while (true)
      {
        Log.e(TAG, "set_gain error " + localIOException.toString());
        localIOException.printStackTrace();
      }
    }
  }

  private int rtlsdr_set_tuner_gain_mode(boolean paramBoolean)
  {
    if (this.set_gain_mode)
      rtlsdr_set_i2c_repeater(true);
    try
    {
      this.myTuner.set_gain_mode(0, paramBoolean);
      rtlsdr_set_i2c_repeater(false);
      return 0;
    }
    catch (IOException localIOException)
    {
      while (true)
      {
        Log.e(TAG, localIOException.toString());
        localIOException.printStackTrace();
      }
    }
  }

  private int rtlsdr_set_tuner_if_gain(int paramInt1, int paramInt2)
  {
    int i;
    if (this.myTuner != null)
      i = -1;
    RtlSdr_tuner_iface localRtlSdr_tuner_iface;
    do
    {
      return i;
      localRtlSdr_tuner_iface = this.myTuner;
      i = 0;
    }
    while (localRtlSdr_tuner_iface == null);
    rtlsdr_set_i2c_repeater(true);
    try
    {
      this.myTuner.set_if_gain(0, paramInt1, paramInt2);
      rtlsdr_set_i2c_repeater(false);
      return 0;
    }
    catch (IOException localIOException)
    {
      while (true)
      {
        Log.e(TAG, localIOException.toString());
        localIOException.printStackTrace();
      }
    }
  }

  private int rtlsdr_set_xtal_freq(long paramLong1, long paramLong2)
  {
    if ((paramLong1 > 0L) && ((paramLong1 < 28799000L) || (paramLong1 > 28801000L)))
      return -2;
    boolean bool1 = paramLong1 < 0L;
    int i = 0;
    if (bool1)
    {
      boolean bool2 = rtl_xtal < paramLong1;
      i = 0;
      if (bool2)
      {
        rtl_xtal = paramLong1;
        boolean bool3 = this.rate < 0L;
        i = 0;
        if (bool3)
          i = rtlsdr_set_sample_rate(this.rate);
      }
    }
    if (tun_xtal != paramLong2)
      if (0L != paramLong2)
        break label125;
    label125: for (tun_xtal = rtl_xtal; ; tun_xtal = paramLong2)
    {
      if (this.freq != 0L)
        i = rtlsdr_set_center_freq(this.freq);
      return i;
    }
  }

  private void rtlsdr_tuner_reset()
  {
    rtlsdr_set_gpio_bit((byte)5, true);
    rtlsdr_set_gpio_bit((byte)5, false);
  }

  private static int rtlsdr_write_array(byte paramByte1, char paramChar, byte[] paramArrayOfByte, byte paramByte2)
  {
    char c = (char)(0x10 | paramByte1 << 8);
    int i = libusb_control_transfer((byte)64, (byte)0, paramChar, c, paramArrayOfByte, (char)paramByte2, 15000);
    Log.e(TAG + ":rtlsdr_write_array", "addr 0x" + Integer.toHexString(paramChar) + " index 0x" + Integer.toHexString(c) + " array " + paramArrayOfByte[0] + " len " + paramByte2);
    if (i < 0)
      Log.e(TAG, "rtlsdr_write_array error byte count " + paramByte2);
    return i;
  }

  private int rtlsdr_write_eeprom(byte[] paramArrayOfByte, char paramChar1, char paramChar2)
    throws InterruptedException
  {
    byte[] arrayOfByte = new byte[2];
    int i;
    if (paramChar2 + paramChar1 > 256)
      i = -2;
    char c;
    do
    {
      return i;
      c = '\000';
      i = 0;
    }
    while (c >= paramChar2);
    arrayOfByte[0] = ((byte)(c + paramChar1));
    rtlsdr_write_array((byte)6, ' ', arrayOfByte, (byte)1);
    rtlsdr_read_array((byte)6, ' ', arrayOfByte, (byte)1);
    if (arrayOfByte[1] == paramArrayOfByte[c]);
    while (true)
    {
      c++;
      break;
      arrayOfByte[1] = paramArrayOfByte[c];
      if (rtlsdr_write_array((byte)6, ' ', arrayOfByte, (byte)2) != arrayOfByte.length)
        return -3;
      Thread.sleep(5000L);
    }
  }

  private int rtlsdr_write_reg(byte paramByte1, char paramChar1, char paramChar2, byte paramByte2)
  {
    byte[] arrayOfByte = new byte[2];
    char c = (char)(0x10 | paramByte1 << 8);
    if (paramByte2 == 1)
      arrayOfByte[0] = ((byte)(paramChar2 & 0xFF));
    while (true)
    {
      arrayOfByte[1] = ((byte)(paramChar2 & 0xFF));
      int i = libusb_control_transfer((byte)64, (byte)0, paramChar1, c, arrayOfByte, (char)paramByte2, 15000);
      if (i < 0)
        Log.e(TAG, "rtlsdr_write_reg failed " + i);
      return i;
      arrayOfByte[0] = ((byte)(paramChar2 >> '\b'));
    }
  }

  private int setConfigSingle(int paramInt1, int paramInt2)
  {
    return mConnection.controlTransfer(65, paramInt1, paramInt2, 0, null, 0, 15000);
  }

  public void close()
    throws IOException
  {
    rtlsdr_deinit_baseband();
    if (this.myTuner != null)
      this.myTuner.exit(0);
    mConnection.close();
  }

  public boolean getCD()
    throws IOException
  {
    return false;
  }

  public boolean getCTS()
    throws IOException
  {
    return false;
  }

  public boolean getDSR()
    throws IOException
  {
    return false;
  }

  public boolean getDTR()
    throws IOException
  {
    return false;
  }

  public boolean getRI()
    throws IOException
  {
    return false;
  }

  public boolean getRTS()
    throws IOException
  {
    return false;
  }

  public void open()
    throws IOException
  {
    int i = 0;
    int j = 0;
    int n;
    try
    {
      int k = this.mDevice.getInterfaceCount();
      i = 0;
      if (j >= k)
      {
        int m = this.mDevice.getInterfaceCount();
        n = 0;
        if (n >= m)
        {
          this.myTuner = rtlsdr_open();
          if (this.myTuner != null)
            adsbInit();
        }
      }
      else
      {
        UsbInterface localUsbInterface2 = this.mDevice.getInterface(j);
        boolean bool = mConnection.claimInterface(localUsbInterface2, true);
        i = 0;
        if (bool)
          Log.e(TAG, "claimInterface " + j + " SUCCESS");
        else
          Log.e(TAG, "claimInterface " + j + " FAIL");
      }
    }
    finally
    {
      if (i == 0)
        close();
    }
    UsbInterface localUsbInterface1 = this.mDevice.getInterface(n);
    if (localUsbInterface1 != null)
    {
      Log.e(TAG, "intf.getInterfaceClass()=" + n + " " + localUsbInterface1.getInterfaceClass() + " intf.getInterfaceSubclass()=" + n + " " + localUsbInterface1.getInterfaceSubclass() + " intf.getInterfaceProtocol()=" + n + " " + localUsbInterface1.getInterfaceProtocol());
      Log.e(TAG, "dataIface.getEndpointCount " + n + " " + localUsbInterface1.getEndpointCount());
    }
    int i1 = localUsbInterface1.getEndpointCount();
    int i2 = 0;
    label524: label528: 
    while (true)
    {
      UsbEndpoint localUsbEndpoint = localUsbInterface1.getEndpoint(i2);
      Log.e(TAG, "found endpoint getEndpointNumber= " + localUsbEndpoint.getEndpointNumber());
      Log.e(TAG, "found endpoint type= " + localUsbEndpoint.getType());
      Log.e(TAG, "found endpoint getDirection= " + localUsbEndpoint.getDirection());
      Log.e(TAG, "found endpoint getMaxPacketSize= " + localUsbEndpoint.getMaxPacketSize());
      if (localUsbEndpoint.getType() == 2)
        if (localUsbEndpoint.getDirection() == 128)
        {
          this.mReadEndpoint = localUsbEndpoint;
          Log.e(TAG, "found READ USB_ENDPOINT_XFER_BULK");
        }
        else
        {
          Log.e(TAG, "found WRITE USB_DIR_OUT->USB_ENDPOINT_XFER_BULK");
          break label524;
          j++;
          break;
        }
      while (true)
      {
        if (i2 < i1)
          break label528;
        i = 1;
        n++;
        break;
        i2++;
      }
    }
  }

  public int read(byte[] paramArrayOfByte, int paramInt)
    throws IOException
  {
    synchronized (this.mReadBufferLock)
    {
      int i = Math.min(paramArrayOfByte.length, this.mReadBuffer.length);
      int j = mConnection.bulkTransfer(this.mReadEndpoint, this.mReadBuffer, i, paramInt);
      System.arraycopy(this.mReadBuffer, 0, paramArrayOfByte, 0, j);
      return j;
    }
  }

  public void setDTR(boolean paramBoolean)
    throws IOException
  {
  }

  public void setParameters(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws IOException
  {
  }

  public void setRTS(boolean paramBoolean)
    throws IOException
  {
  }

  public void settings(boolean paramBoolean, int paramInt1, int paramInt2)
  {
    this.enable_agc = paramBoolean;
    this.ppm_error = paramInt2;
    this.gain = paramInt1;
  }

  public int write(byte[] paramArrayOfByte, int paramInt)
    throws IOException
  {
    Log.e(TAG, "Writes not support yet writeLength= " + paramArrayOfByte.length);
    return paramArrayOfByte.length;
  }
}

/* Location:           /Users/kevinfinisterre/Desktop/ADSB_USB_playstore.jar
 * Qualified Name:     com.wilsonae.rtlsdrlib.SdrUSBDriver
 * JD-Core Version:    0.6.2
 */
