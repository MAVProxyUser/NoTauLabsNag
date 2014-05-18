package org.openpilot_nonag.rtlsdrlib;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.util.Log;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class FtdiSerialDriver extends CommonUsbSerialDriver
{
  private static final boolean ENABLE_ASYNC_READS = false;
  public static final int FTDI_DEVICE_IN_REQTYPE = 192;
  public static final int FTDI_DEVICE_OUT_REQTYPE = 64;
  private static final int MODEM_STATUS_HEADER_LENGTH = 2;
  private static final int SIO_MODEM_CTRL_REQUEST = 1;
  private static final int SIO_RESET_REQUEST = 0;
  private static final int SIO_RESET_SIO = 0;
  private static final int SIO_SET_BAUD_RATE_REQUEST = 3;
  private static final int SIO_SET_DATA_REQUEST = 4;
  private static final int SIO_SET_FLOW_CTRL_REQUEST = 2;
  public static final int USB_ENDPOINT_IN = 128;
  public static final int USB_ENDPOINT_OUT = 0;
  public static final int USB_READ_TIMEOUT_MILLIS = 5000;
  public static final int USB_RECIP_DEVICE = 0;
  public static final int USB_RECIP_ENDPOINT = 2;
  public static final int USB_RECIP_INTERFACE = 1;
  public static final int USB_RECIP_OTHER = 3;
  public static final int USB_TYPE_CLASS = 0;
  public static final int USB_TYPE_RESERVED = 0;
  public static final int USB_TYPE_STANDARD = 0;
  public static final int USB_TYPE_VENDOR = 0;
  public static final int USB_WRITE_TIMEOUT_MILLIS = 5000;
  private final String TAG = FtdiSerialDriver.class.getSimpleName();
  private int mInterface = 0;
  private int mMaxPacketSize = 64;
  private DeviceType mType = null;

  public FtdiSerialDriver(UsbDevice paramUsbDevice, UsbDeviceConnection paramUsbDeviceConnection)
  {
    super(paramUsbDevice, paramUsbDeviceConnection);
  }

  private long[] convertBaudrate(int paramInt)
  {
    int i = 24000000 / paramInt;
    int j = 0;
    int k = 0;
    int m = 0;
    int[] arrayOfInt = new int[8];
    arrayOfInt[1] = 3;
    arrayOfInt[2] = 2;
    arrayOfInt[3] = 4;
    arrayOfInt[4] = 1;
    arrayOfInt[5] = 5;
    arrayOfInt[6] = 6;
    arrayOfInt[7] = 7;
    int n = 0;
    label67: long l1;
    label94: long l2;
    if (n >= 2)
    {
      l1 = j >> 3 | arrayOfInt[(j & 0x7)] << 14;
      if (l1 != 1L)
        break label321;
      l1 = 0L;
      l2 = l1 & 0xFFFF;
      if ((this.mType != DeviceType.TYPE_2232C) && (this.mType != DeviceType.TYPE_2232H) && (this.mType != DeviceType.TYPE_4232H))
        break label336;
    }
    label193: label336: for (long l3 = 0L | 0xFF00 & (0xFFFF & l1 >> 8); ; l3 = 0xFFFF & l1 >> 16)
    {
      long[] arrayOfLong = new long[3];
      arrayOfLong[0] = k;
      arrayOfLong[1] = l3;
      arrayOfLong[2] = l2;
      return arrayOfLong;
      int i1 = i + n;
      int i2;
      if (i1 <= 8)
      {
        i1 = 8;
        i2 = (24000000 + i1 / 2) / i1;
        if (i2 >= paramInt)
          break label312;
      }
      for (int i3 = paramInt - i2; ; i3 = i2 - paramInt)
      {
        if ((n == 0) || (i3 < m))
        {
          j = i1;
          k = i2;
          m = i3;
          if (i3 == 0)
            break label67;
        }
        n++;
        break;
        if ((this.mType != DeviceType.TYPE_AM) && (i1 < 12))
        {
          i1 = 12;
          break label193;
        }
        if (i < 16)
        {
          i1 = 16;
          break label193;
        }
        if ((this.mType == DeviceType.TYPE_AM) || (i1 <= 131071))
          break label193;
        i1 = 131071;
        break label193;
      }
      if (l1 != 16385L)
        break label94;
      l1 = 1L;
      break label94;
    }
  }

  private final int filterStatusBytes(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, int paramInt2)
  {
    int i = 1 + paramInt1 / paramInt2;
    int j = 0;
    if (j >= i)
      return paramInt1 - i * 2;
    if (j == i - 1);
    for (int k = -2 + paramInt1 % paramInt2; ; k = paramInt2 - 2)
    {
      if (k > 0)
        System.arraycopy(paramArrayOfByte1, 2 + j * paramInt2, paramArrayOfByte2, j * (paramInt2 - 2), k);
      j++;
      break;
    }
  }

  public static Map<Integer, int[]> getSupportedDevices()
  {
    LinkedHashMap localLinkedHashMap = new LinkedHashMap();
    localLinkedHashMap.put(Integer.valueOf(1027), new int[] { 24577 });
    return localLinkedHashMap;
  }

  private int setBaudRate(int paramInt)
    throws IOException
  {
    long[] arrayOfLong = convertBaudrate(paramInt);
    long l1 = arrayOfLong[0];
    long l2 = arrayOfLong[1];
    long l3 = arrayOfLong[2];
    int i = mConnection.controlTransfer(64, 3, (int)l3, (int)l2, null, 0, 5000);
    if (i != 0)
      throw new IOException("Setting baudrate failed: result=" + i);
    return (int)l1;
  }

  public void close()
  {
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
    try
    {
      while (true)
      {
        if (i >= this.mDevice.getInterfaceCount())
        {
          reset();
          if (1 == 0)
            close();
          return;
        }
        if (!mConnection.claimInterface(this.mDevice.getInterface(i), true))
          break;
        Log.d(this.TAG, "claimInterface " + i + " SUCCESS");
        i++;
      }
      throw new IOException("Error claiming interface " + i);
    }
    finally
    {
      if (0 == 0)
        close();
    }
  }

  public int read(byte[] paramArrayOfByte, int paramInt)
    throws IOException
  {
    UsbEndpoint localUsbEndpoint = this.mDevice.getInterface(0).getEndpoint(0);
    int j;
    synchronized (this.mReadBufferLock)
    {
      int i = Math.min(paramArrayOfByte.length, this.mReadBuffer.length);
      j = mConnection.bulkTransfer(localUsbEndpoint, this.mReadBuffer, i, paramInt);
      if (j < 2)
        throw new IOException("Expected at least 2 bytes");
    }
    int k = filterStatusBytes(this.mReadBuffer, paramArrayOfByte, j, localUsbEndpoint.getMaxPacketSize());
    return k;
  }

  public void reset()
    throws IOException
  {
    int i = mConnection.controlTransfer(64, 0, 0, 0, null, 0, 5000);
    if (i != 0)
      throw new IOException("Reset failed: result=" + i);
    this.mType = DeviceType.TYPE_R;
  }

  public void setDTR(boolean paramBoolean)
    throws IOException
  {
  }

  public void setParameters(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws IOException
  {
    setBaudRate(paramInt1);
    int i;
    switch (paramInt4)
    {
    default:
      throw new IllegalArgumentException("Unknown parity value: " + paramInt4);
    case 0:
      i = paramInt2 | 0x0;
    case 1:
    case 2:
    case 3:
    case 4:
    }
    while (true)
      switch (paramInt3)
      {
      default:
        throw new IllegalArgumentException("Unknown stopBits value: " + paramInt3);
        i = paramInt2 | 0x100;
        continue;
        i = paramInt2 | 0x200;
        continue;
        i = paramInt2 | 0x300;
        continue;
        i = paramInt2 | 0x400;
      case 1:
      case 3:
      case 2:
      }
    int j = i | 0x0;
    while (true)
    {
      int k = mConnection.controlTransfer(64, 4, j, 0, null, 0, 5000);
      if (k == 0)
        break;
      throw new IOException("Setting parameters failed: result=" + k);
      j = i | 0x800;
      continue;
      j = i | 0x1000;
    }
  }

  public void setRTS(boolean paramBoolean)
    throws IOException
  {
  }

  public void settings(boolean paramBoolean, int paramInt1, int paramInt2)
  {
  }

  public int write(byte[] paramArrayOfByte, int paramInt)
    throws IOException
  {
    UsbEndpoint localUsbEndpoint = this.mDevice.getInterface(0).getEndpoint(1);
    int i = 0;
    while (true)
    {
      if (i >= paramArrayOfByte.length)
        return i;
      int j;
      int k;
      synchronized (this.mWriteBufferLock)
      {
        j = Math.min(paramArrayOfByte.length - i, this.mWriteBuffer.length);
        byte[] arrayOfByte;
        if (i == 0)
        {
          arrayOfByte = paramArrayOfByte;
          k = mConnection.bulkTransfer(localUsbEndpoint, arrayOfByte, j, paramInt);
          if (k <= 0)
            throw new IOException("Error writing " + j + " bytes at offset " + i + " length=" + paramArrayOfByte.length);
        }
        else
        {
          System.arraycopy(paramArrayOfByte, i, this.mWriteBuffer, 0, j);
          arrayOfByte = this.mWriteBuffer;
        }
      }
      Log.d(this.TAG, "Wrote amtWritten=" + k + " attempted=" + j);
      i += k;
    }
  }

  private static enum DeviceType
  {
    static
    {
      TYPE_AM = new DeviceType("TYPE_AM", 1);
      TYPE_2232C = new DeviceType("TYPE_2232C", 2);
      TYPE_R = new DeviceType("TYPE_R", 3);
      TYPE_2232H = new DeviceType("TYPE_2232H", 4);
      TYPE_4232H = new DeviceType("TYPE_4232H", 5);
      DeviceType[] arrayOfDeviceType = new DeviceType[6];
      arrayOfDeviceType[0] = TYPE_BM;
      arrayOfDeviceType[1] = TYPE_AM;
      arrayOfDeviceType[2] = TYPE_2232C;
      arrayOfDeviceType[3] = TYPE_R;
      arrayOfDeviceType[4] = TYPE_2232H;
      arrayOfDeviceType[5] = TYPE_4232H;
    }
  }
}

/* Location:           /Users/kevinfinisterre/Desktop/ADSB_USB_playstore.jar
 * Qualified Name:     com.wilsonae.rtlsdrlib.FtdiSerialDriver
 * JD-Core Version:    0.6.2
 */
