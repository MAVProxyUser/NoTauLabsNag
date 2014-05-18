package org.openpilot_nonag.rtlsdrlib;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public enum UsbSerialProber
{
  static
  {
    // Byte code:
    //   0: new 16	com/wilsonae/rtlsdrlib/UsbSerialProber$1
    //   3: dup
    //   4: ldc 17
    //   6: iconst_0
    //   7: invokespecial 21	com/wilsonae/rtlsdrlib/UsbSerialProber$1:<init>	(Ljava/lang/String;I)V
    //   10: putstatic 23	com/wilsonae/rtlsdrlib/UsbSerialProber:FTDI_SERIAL	Lcom/wilsonae/rtlsdrlib/UsbSerialProber;
    //   13: new 25	com/wilsonae/rtlsdrlib/UsbSerialProber$2
    //   16: dup
    //   17: ldc 26
    //   19: iconst_1
    //   20: invokespecial 27	com/wilsonae/rtlsdrlib/UsbSerialProber$2:<init>	(Ljava/lang/String;I)V
    //   23: putstatic 29	com/wilsonae/rtlsdrlib/UsbSerialProber:CDC_ACM_SERIAL	Lcom/wilsonae/rtlsdrlib/UsbSerialProber;
    //   26: new 31	com/wilsonae/rtlsdrlib/UsbSerialProber$3
    //   29: dup
    //   30: ldc 32
    //   32: iconst_2
    //   33: invokespecial 33	com/wilsonae/rtlsdrlib/UsbSerialProber$3:<init>	(Ljava/lang/String;I)V
    //   36: putstatic 35	com/wilsonae/rtlsdrlib/UsbSerialProber:RTL_SDR	Lcom/wilsonae/rtlsdrlib/UsbSerialProber;
    //   39: new 37	com/wilsonae/rtlsdrlib/UsbSerialProber$4
    //   42: dup
    //   43: ldc 38
    //   45: iconst_3
    //   46: invokespecial 39	com/wilsonae/rtlsdrlib/UsbSerialProber$4:<init>	(Ljava/lang/String;I)V
    //   49: putstatic 41	com/wilsonae/rtlsdrlib/UsbSerialProber:SILAB_SERIAL	Lcom/wilsonae/rtlsdrlib/UsbSerialProber;
    //   52: iconst_4
    //   53: anewarray 2	com/wilsonae/rtlsdrlib/UsbSerialProber
    //   56: astore_0
    //   57: aload_0
    //   58: iconst_0
    //   59: getstatic 23	com/wilsonae/rtlsdrlib/UsbSerialProber:FTDI_SERIAL	Lcom/wilsonae/rtlsdrlib/UsbSerialProber;
    //   62: aastore
    //   63: aload_0
    //   64: iconst_1
    //   65: getstatic 29	com/wilsonae/rtlsdrlib/UsbSerialProber:CDC_ACM_SERIAL	Lcom/wilsonae/rtlsdrlib/UsbSerialProber;
    //   68: aastore
    //   69: aload_0
    //   70: iconst_2
    //   71: getstatic 35	com/wilsonae/rtlsdrlib/UsbSerialProber:RTL_SDR	Lcom/wilsonae/rtlsdrlib/UsbSerialProber;
    //   74: aastore
    //   75: aload_0
    //   76: iconst_3
    //   77: getstatic 41	com/wilsonae/rtlsdrlib/UsbSerialProber:SILAB_SERIAL	Lcom/wilsonae/rtlsdrlib/UsbSerialProber;
    //   80: aastore
    //   81: aload_0
    //   82: putstatic 43	com/wilsonae/rtlsdrlib/UsbSerialProber:ENUM$VALUES	[Lcom/wilsonae/rtlsdrlib/UsbSerialProber;
    //   85: return
  }

  @Deprecated
  public static UsbSerialDriver acquire(UsbManager paramUsbManager)
  {
    return findFirstDevice(paramUsbManager);
  }

  @Deprecated
  public static UsbSerialDriver acquire(UsbManager paramUsbManager, UsbDevice paramUsbDevice)
  {
    List localList = probeSingleDevice(paramUsbManager, paramUsbDevice);
    if (!localList.isEmpty())
      return (UsbSerialDriver)localList.get(0);
    return null;
  }

  public static List<UsbSerialDriver> findAllDevices(UsbManager paramUsbManager)
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = paramUsbManager.getDeviceList().values().iterator();
    while (true)
    {
      if (!localIterator.hasNext())
        return localArrayList;
      localArrayList.addAll(probeSingleDevice(paramUsbManager, (UsbDevice)localIterator.next()));
    }
  }

  public static UsbSerialDriver findFirstDevice(UsbManager paramUsbManager)
  {
    Iterator localIterator = paramUsbManager.getDeviceList().values().iterator();
    while (true)
    {
      if (!localIterator.hasNext())
        return null;
      UsbDevice localUsbDevice = (UsbDevice)localIterator.next();
      UsbSerialProber[] arrayOfUsbSerialProber = values();
      int i = arrayOfUsbSerialProber.length;
      for (int j = 0; j < i; j++)
      {
        List localList = arrayOfUsbSerialProber[j].probe(paramUsbManager, localUsbDevice);
        if (!localList.isEmpty())
          return (UsbSerialDriver)localList.get(0);
      }
    }
  }

  public static List<UsbSerialDriver> probeSingleDevice(UsbManager paramUsbManager, UsbDevice paramUsbDevice)
  {
    ArrayList localArrayList = new ArrayList();
    UsbSerialProber[] arrayOfUsbSerialProber = values();
    int i = arrayOfUsbSerialProber.length;
    for (int j = 0; ; j++)
    {
      if (j >= i)
        return localArrayList;
      localArrayList.addAll(arrayOfUsbSerialProber[j].probe(paramUsbManager, paramUsbDevice));
    }
  }

  private static boolean testIfSupported(UsbDevice paramUsbDevice, Map<Integer, int[]> paramMap)
  {
    int[] arrayOfInt = (int[])paramMap.get(Integer.valueOf(paramUsbDevice.getVendorId()));
    if (arrayOfInt == null);
    while (true)
    {
      return false;
      int i = paramUsbDevice.getProductId();
      int j = arrayOfInt.length;
      for (int k = 0; k < j; k++)
        if (i == arrayOfInt[k])
          return true;
    }
  }

  protected abstract List<UsbSerialDriver> probe(UsbManager paramUsbManager, UsbDevice paramUsbDevice);
}

/* Location:           /Users/kevinfinisterre/Desktop/ADSB_USB_playstore.jar
 * Qualified Name:     com.wilsonae.rtlsdrlib.UsbSerialProber
 * JD-Core Version:    0.6.2
 */
