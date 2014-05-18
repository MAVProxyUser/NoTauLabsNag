package org.openpilot_nonag.rtlsdrlib;

import java.io.IOException;

public abstract interface RtlSdr_tuner_iface
{
  public abstract int exit(int paramInt)
    throws IOException;

  public abstract int init(int paramInt)
    throws IOException;

  public abstract int set_bw(int paramInt1, int paramInt2)
    throws IOException;

  public abstract int set_freq(int paramInt, long paramLong)
    throws IOException;

  public abstract int set_gain(int paramInt1, int paramInt2)
    throws IOException;

  public abstract int set_gain_mode(int paramInt, boolean paramBoolean)
    throws IOException;

  public abstract int set_if_gain(int paramInt1, int paramInt2, int paramInt3)
    throws IOException;
}

/* Location:           /Users/kevinfinisterre/Desktop/ADSB_USB_playstore.jar
 * Qualified Name:     com.wilsonae.rtlsdrlib.RtlSdr_tuner_iface
 * JD-Core Version:    0.6.2
 */
