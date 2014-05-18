package org.openpilot_nonag.rtlsdrlib;

import java.io.IOException;

public abstract interface UsbSerialDriver
{
  public static final int DATABITS_5 = 5;
  public static final int DATABITS_6 = 6;
  public static final int DATABITS_7 = 7;
  public static final int DATABITS_8 = 8;
  public static final int FLOWCONTROL_NONE = 0;
  public static final int FLOWCONTROL_RTSCTS_IN = 1;
  public static final int FLOWCONTROL_RTSCTS_OUT = 2;
  public static final int FLOWCONTROL_XONXOFF_IN = 4;
  public static final int FLOWCONTROL_XONXOFF_OUT = 8;
  public static final int PARITY_EVEN = 2;
  public static final int PARITY_MARK = 3;
  public static final int PARITY_NONE = 0;
  public static final int PARITY_ODD = 1;
  public static final int PARITY_SPACE = 4;
  public static final int STOPBITS_1 = 1;
  public static final int STOPBITS_1_5 = 3;
  public static final int STOPBITS_2 = 2;

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

  public abstract void settings(boolean paramBoolean, int paramInt1, int paramInt2);

  public abstract int write(byte[] paramArrayOfByte, int paramInt)
    throws IOException;
}

/* Location:           /Users/kevinfinisterre/Desktop/ADSB_USB_playstore.jar
 * Qualified Name:     com.wilsonae.rtlsdrlib.UsbSerialDriver
 * JD-Core Version:    0.6.2
 */
