package org.openpilot_nonag.rtlsdrlib;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import java.io.IOException;

abstract class CommonUsbSerialDriver
  implements UsbSerialDriver
{
  public static final int DEFAULT_READ_BUFFER_SIZE = 262144;
  public static final int DEFAULT_WRITE_BUFFER_SIZE = 16384;
  protected static UsbDeviceConnection mConnection = null;
  protected final UsbDevice mDevice;
  protected byte[] mReadBuffer;
  protected final Object mReadBufferLock = new Object();
  protected byte[] mWriteBuffer;
  protected final Object mWriteBufferLock = new Object();

  public CommonUsbSerialDriver(UsbDevice paramUsbDevice, UsbDeviceConnection paramUsbDeviceConnection)
  {
    this.mDevice = paramUsbDevice;
    mConnection = paramUsbDeviceConnection;
    this.mReadBuffer = new byte[262144];
    this.mWriteBuffer = new byte[16384];
  }

  public abstract void close()
    throws IOException;

  public abstract boolean getCD()
    throws IOException;

  public abstract boolean getCTS()
    throws IOException;

  public abstract boolean getDSR()
    throws IOException;

  public abstract boolean getDTR()
    throws IOException;

  public final UsbDevice getDevice()
  {
    return this.mDevice;
  }

  public abstract boolean getRI()
    throws IOException;

  public abstract boolean getRTS()
    throws IOException;

  public abstract void open()
    throws IOException;

  public abstract int read(byte[] paramArrayOfByte, int paramInt)
    throws IOException;

  public abstract void setDTR(boolean paramBoolean)
    throws IOException;

  public abstract void setParameters(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws IOException;

  public abstract void setRTS(boolean paramBoolean)
    throws IOException;

  public final void setReadBufferSize(int paramInt)
  {
    synchronized (this.mReadBufferLock)
    {
      if (paramInt == this.mReadBuffer.length)
        return;
      this.mReadBuffer = new byte[paramInt];
      return;
    }
  }

  public final void setWriteBufferSize(int paramInt)
  {
    synchronized (this.mWriteBufferLock)
    {
      if (paramInt == this.mWriteBuffer.length)
        return;
      this.mWriteBuffer = new byte[paramInt];
      return;
    }
  }

  public abstract int write(byte[] paramArrayOfByte, int paramInt)
    throws IOException;
}

/* Location:           /Users/kevinfinisterre/Desktop/ADSB_USB_playstore.jar
 * Qualified Name:     com.wilsonae.rtlsdrlib.CommonUsbSerialDriver
 * JD-Core Version:    0.6.2
 */
