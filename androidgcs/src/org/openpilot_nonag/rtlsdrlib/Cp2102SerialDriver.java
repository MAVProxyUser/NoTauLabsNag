package org.openpilot_nonag.rtlsdrlib;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.util.Log;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class Cp2102SerialDriver extends CommonUsbSerialDriver
{
  private static final int BAUD_RATE_GEN_FREQ = 3686400;
  private static final int CONTROL_WRITE_DTR = 256;
  private static final int CONTROL_WRITE_RTS = 512;
  private static final int DEFAULT_BAUD_RATE = 9600;
  private static final int MCR_ALL = 3;
  private static final int MCR_DTR = 1;
  private static final int MCR_RTS = 2;
  private static final int REQTYPE_HOST_TO_DEVICE = 65;
  private static final int SILABSER_IFC_ENABLE_REQUEST_CODE = 0;
  private static final int SILABSER_SET_BAUDDIV_REQUEST_CODE = 1;
  private static final int SILABSER_SET_BAUDRATE = 30;
  private static final int SILABSER_SET_LINE_CTL_REQUEST_CODE = 3;
  private static final int SILABSER_SET_MHS_REQUEST_CODE = 7;
  private static final String TAG = Cp2102SerialDriver.class.getSimpleName();
  private static final int UART_DISABLE = 0;
  private static final int UART_ENABLE = 1;
  private static final int USB_WRITE_TIMEOUT_MILLIS = 5000;
  private UsbEndpoint mReadEndpoint;
  private UsbEndpoint mWriteEndpoint;

  public Cp2102SerialDriver(UsbDevice paramUsbDevice, UsbDeviceConnection paramUsbDeviceConnection)
  {
    super(paramUsbDevice, paramUsbDeviceConnection);
  }

  public static Map<Integer, int[]> getSupportedDevices()
  {
    LinkedHashMap localLinkedHashMap = new LinkedHashMap();
    localLinkedHashMap.put(Integer.valueOf(4292), new int[] { 60000 });
    return localLinkedHashMap;
  }

  private void setBaudRate(int paramInt)
    throws IOException
  {
    byte[] arrayOfByte = new byte[4];
    arrayOfByte[0] = ((byte)(paramInt & 0xFF));
    arrayOfByte[1] = ((byte)(0xFF & paramInt >> 8));
    arrayOfByte[2] = ((byte)(0xFF & paramInt >> 16));
    arrayOfByte[3] = ((byte)(0xFF & paramInt >> 24));
    if (mConnection.controlTransfer(65, 30, 0, 0, arrayOfByte, 4, 5000) < 0)
      throw new IOException("Error setting baud rate.");
  }

  private int setConfigSingle(int paramInt1, int paramInt2)
  {
    return mConnection.controlTransfer(65, paramInt1, paramInt2, 0, null, 0, 5000);
  }

  public void close()
    throws IOException
  {
    setConfigSingle(0, 0);
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
    return true;
  }

  public boolean getRI()
    throws IOException
  {
    return false;
  }

  public boolean getRTS()
    throws IOException
  {
    return true;
  }

  public void open()
    throws IOException
  {
    int i = 0;
    while (true)
    {
      UsbInterface localUsbInterface2;
      int j;
      try
      {
        if (i >= this.mDevice.getInterfaceCount())
        {
          localUsbInterface2 = this.mDevice.getInterface(-1 + this.mDevice.getInterfaceCount());
          j = 0;
          if (j >= localUsbInterface2.getEndpointCount())
          {
            setConfigSingle(0, 1);
            setConfigSingle(7, 771);
            setConfigSingle(1, 384);
            if (1 == 0)
              close();
          }
        }
        else
        {
          UsbInterface localUsbInterface1 = this.mDevice.getInterface(i);
          if (mConnection.claimInterface(localUsbInterface1, true))
            Log.d(TAG, "claimInterface " + i + " SUCCESS");
          else
            Log.d(TAG, "claimInterface " + i + " FAIL");
        }
      }
      finally
      {
        if (0 == 0)
          close();
      }
      UsbEndpoint localUsbEndpoint = localUsbInterface2.getEndpoint(j);
      if (localUsbEndpoint.getType() == 2)
      {
        if (localUsbEndpoint.getDirection() == 128)
        {
          this.mReadEndpoint = localUsbEndpoint;
        }
        else
        {
          this.mWriteEndpoint = localUsbEndpoint;
          break label225;
          i++;
        }
      }
      else
        label225: j++;
    }
  }

  public int read(byte[] paramArrayOfByte, int paramInt)
    throws IOException
  {
    synchronized (this.mReadBufferLock)
    {
      int i = Math.min(paramArrayOfByte.length, this.mReadBuffer.length);
      int j = mConnection.bulkTransfer(this.mReadEndpoint, this.mReadBuffer, i, paramInt);
      if (j < 0)
        return 0;
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
    setBaudRate(paramInt1);
    int i;
    int j;
    label80: int k;
    switch (paramInt2)
    {
    default:
      i = 0x0 | 0x800;
      setConfigSingle(3, i);
      j = 0;
      switch (paramInt4)
      {
      default:
        setConfigSingle(3, j);
        k = 0;
        switch (paramInt3)
        {
        default:
        case 1:
        case 2:
        }
        break;
      case 1:
      case 2:
      }
      break;
    case 5:
    case 6:
    case 7:
    case 8:
    }
    while (true)
    {
      setConfigSingle(3, k);
      return;
      i = 0x0 | 0x500;
      break;
      i = 0x0 | 0x600;
      break;
      i = 0x0 | 0x700;
      break;
      i = 0x0 | 0x800;
      break;
      j = 0x0 | 0x10;
      break label80;
      j = 0x0 | 0x20;
      break label80;
      k = 0x0 | 0x0;
      continue;
      k = 0x0 | 0x2;
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
          k = mConnection.bulkTransfer(this.mWriteEndpoint, arrayOfByte, j, paramInt);
          if (k <= 0)
            throw new IOException("Error writing " + j + " bytes at offset " + i + " length=" + paramArrayOfByte.length);
        }
        else
        {
          System.arraycopy(paramArrayOfByte, i, this.mWriteBuffer, 0, j);
          arrayOfByte = this.mWriteBuffer;
        }
      }
      Log.d(TAG, "Wrote amt=" + k + " attempted=" + j);
      i += k;
    }
  }
}

/* Location:           /Users/kevinfinisterre/Desktop/ADSB_USB_playstore.jar
 * Qualified Name:     com.wilsonae.rtlsdrlib.Cp2102SerialDriver
 * JD-Core Version:    0.6.2
 */
