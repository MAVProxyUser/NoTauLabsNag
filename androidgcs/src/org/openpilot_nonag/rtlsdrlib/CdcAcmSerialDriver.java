package org.openpilot_nonag.rtlsdrlib;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.util.Log;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class CdcAcmSerialDriver extends CommonUsbSerialDriver
{
  private static final int GET_LINE_CODING = 33;
  private static final int SEND_BREAK = 35;
  private static final int SET_CONTROL_LINE_STATE = 34;
  private static final int SET_LINE_CODING = 32;
  private static final int USB_RECIP_INTERFACE = 1;
  private static final int USB_RT_ACM = 33;
  private final String TAG = CdcAcmSerialDriver.class.getSimpleName();
  private UsbEndpoint mControlEndpoint;
  private UsbInterface mControlInterface;
  private UsbInterface mDataInterface;
  private boolean mDtr = false;
  private UsbEndpoint mReadEndpoint;
  private boolean mRts = false;
  private UsbEndpoint mWriteEndpoint;

  public CdcAcmSerialDriver(UsbDevice paramUsbDevice, UsbDeviceConnection paramUsbDeviceConnection)
  {
    super(paramUsbDevice, paramUsbDeviceConnection);
  }

  public static Map<Integer, int[]> getSupportedDevices()
  {
    LinkedHashMap localLinkedHashMap = new LinkedHashMap();
    localLinkedHashMap.put(Integer.valueOf(9025), new int[] { 1, 67, 16, 66, 59, 68, 63, 68, 32822 });
    localLinkedHashMap.put(Integer.valueOf(5824), new int[] { 1155 });
    localLinkedHashMap.put(Integer.valueOf(1003), new int[] { 8260 });
    localLinkedHashMap.put(Integer.valueOf(7855), new int[] { 4 });
    return localLinkedHashMap;
  }

  private int sendAcmControlMessage(int paramInt1, int paramInt2, byte[] paramArrayOfByte)
  {
    UsbDeviceConnection localUsbDeviceConnection = mConnection;
    if (paramArrayOfByte != null);
    for (int i = paramArrayOfByte.length; ; i = 0)
      return localUsbDeviceConnection.controlTransfer(33, paramInt1, paramInt2, 0, paramArrayOfByte, i, 5000);
  }

  private void setDtrRts()
  {
    if (this.mRts);
    for (int i = 2; ; i = 0)
    {
      boolean bool = this.mDtr;
      int j = 0;
      if (bool)
        j = 1;
      sendAcmControlMessage(34, i | j, null);
      return;
    }
  }

  public void close()
    throws IOException
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
    return this.mDtr;
  }

  public boolean getRI()
    throws IOException
  {
    return false;
  }

  public boolean getRTS()
    throws IOException
  {
    return this.mRts;
  }

  public void open()
    throws IOException
  {
    Log.d(this.TAG, "claiming interfaces, count=" + this.mDevice.getInterfaceCount());
    Log.d(this.TAG, "Claiming control interface.");
    this.mControlInterface = this.mDevice.getInterface(0);
    Log.d(this.TAG, "Control iface=" + this.mControlInterface);
    if (!mConnection.claimInterface(this.mControlInterface, true))
      throw new IOException("Could not claim control interface.");
    this.mControlEndpoint = this.mControlInterface.getEndpoint(0);
    Log.d(this.TAG, "Control endpoint direction: " + this.mControlEndpoint.getDirection());
    Log.d(this.TAG, "Claiming data interface.");
    this.mDataInterface = this.mDevice.getInterface(1);
    Log.d(this.TAG, "data iface=" + this.mDataInterface);
    if (!mConnection.claimInterface(this.mDataInterface, true))
      throw new IOException("Could not claim data interface.");
    this.mReadEndpoint = this.mDataInterface.getEndpoint(1);
    Log.d(this.TAG, "Read endpoint direction: " + this.mReadEndpoint.getDirection());
    this.mWriteEndpoint = this.mDataInterface.getEndpoint(0);
    Log.d(this.TAG, "Write endpoint direction: " + this.mWriteEndpoint.getDirection());
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
    this.mDtr = paramBoolean;
    setDtrRts();
  }

  public void setParameters(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i;
    switch (paramInt3)
    {
    default:
      throw new IllegalArgumentException("Bad value for stopBits: " + paramInt3);
    case 1:
      i = 0;
    case 3:
    case 2:
    }
    while (true)
      switch (paramInt4)
      {
      default:
        throw new IllegalArgumentException("Bad value for parity: " + paramInt4);
        i = 1;
        continue;
        i = 2;
      case 0:
      case 1:
      case 2:
      case 3:
      case 4:
      }
    int j = 0;
    while (true)
    {
      byte[] arrayOfByte = new byte[7];
      arrayOfByte[0] = ((byte)(paramInt1 & 0xFF));
      arrayOfByte[1] = ((byte)(0xFF & paramInt1 >> 8));
      arrayOfByte[2] = ((byte)(0xFF & paramInt1 >> 16));
      arrayOfByte[3] = ((byte)(0xFF & paramInt1 >> 24));
      arrayOfByte[4] = i;
      arrayOfByte[5] = j;
      arrayOfByte[6] = ((byte)paramInt2);
      sendAcmControlMessage(32, 0, arrayOfByte);
      return;
      j = 1;
      continue;
      j = 2;
      continue;
      j = 3;
      continue;
      j = 4;
    }
  }

  public void setRTS(boolean paramBoolean)
    throws IOException
  {
    this.mRts = paramBoolean;
    setDtrRts();
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
      Log.d(this.TAG, "Wrote amt=" + k + " attempted=" + j);
      i += k;
    }
  }
}

/* Location:           /Users/kevinfinisterre/Desktop/ADSB_USB_playstore.jar
 * Qualified Name:     com.wilsonae.rtlsdrlib.CdcAcmSerialDriver
 * JD-Core Version:    0.6.2
 */
