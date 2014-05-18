package org.openpilot_nonag.rtlsdrlib;

import android.util.Log;
import java.io.IOException;

public class fc0013_tuner
  implements RtlSdr_tuner_iface
{
  private static final String TAG = fc0013_tuner.class.getSimpleName();
  final int FC0013_CHECK_ADDR = 0;
  final int FC0013_CHECK_VAL = 163;
  final int FC0013_I2C_ADDR = 198;
  int[] fc0013_lna_gains;

  public fc0013_tuner()
  {
    int[] arrayOfInt = new int[48];
    arrayOfInt[0] = -99;
    arrayOfInt[1] = 2;
    arrayOfInt[2] = -73;
    arrayOfInt[3] = 3;
    arrayOfInt[4] = -65;
    arrayOfInt[5] = 5;
    arrayOfInt[6] = -63;
    arrayOfInt[7] = 4;
    arrayOfInt[8] = -63;
    arrayOfInt[10] = -60;
    arrayOfInt[11] = 7;
    arrayOfInt[12] = -58;
    arrayOfInt[13] = 1;
    arrayOfInt[14] = -54;
    arrayOfInt[15] = 6;
    arrayOfInt[16] = 58;
    arrayOfInt[17] = 15;
    arrayOfInt[18] = 61;
    arrayOfInt[19] = 14;
    arrayOfInt[20] = 63;
    arrayOfInt[21] = 13;
    arrayOfInt[22] = 65;
    arrayOfInt[23] = 12;
    arrayOfInt[24] = 67;
    arrayOfInt[25] = 11;
    arrayOfInt[26] = 68;
    arrayOfInt[27] = 10;
    arrayOfInt[28] = 70;
    arrayOfInt[29] = 9;
    arrayOfInt[30] = 71;
    arrayOfInt[31] = 8;
    arrayOfInt[32] = 179;
    arrayOfInt[33] = 23;
    arrayOfInt[34] = 181;
    arrayOfInt[35] = 22;
    arrayOfInt[36] = 182;
    arrayOfInt[37] = 21;
    arrayOfInt[38] = 184;
    arrayOfInt[39] = 20;
    arrayOfInt[40] = 186;
    arrayOfInt[41] = 19;
    arrayOfInt[42] = 188;
    arrayOfInt[43] = 18;
    arrayOfInt[44] = 191;
    arrayOfInt[45] = 17;
    arrayOfInt[46] = 197;
    arrayOfInt[47] = 16;
    this.fc0013_lna_gains = arrayOfInt;
  }

  private int fc0013_readreg(byte paramByte, byte[] paramArrayOfByte)
  {
    byte[] arrayOfByte = new byte[2];
    arrayOfByte[0] = paramByte;
    if (SdrUSBDriver.rtlsdr_i2c_write_fn((byte)-58, arrayOfByte, (byte)1) < 0);
    while (SdrUSBDriver.rtlsdr_i2c_read_fn((byte)-58, arrayOfByte, (byte)1) < 0)
      return -1;
    paramArrayOfByte[0] = arrayOfByte[0];
    return 0;
  }

  private int fc0013_set_vhf_track(int paramInt)
  {
    byte[] arrayOfByte = new byte[1];
    if (fc0013_readreg((byte)29, arrayOfByte) != 0)
      return -1;
    arrayOfByte[0] = ((byte)(0xE3 & arrayOfByte[0]));
    int i;
    if (paramInt <= 177500000)
      i = fc0013_writereg((byte)29, (byte)(0x1C | arrayOfByte[0]));
    while (true)
    {
      return i;
      if (paramInt <= 184500000)
        i = fc0013_writereg((byte)29, (byte)(0x18 | arrayOfByte[0]));
      else if (paramInt <= 191500000)
        i = fc0013_writereg((byte)29, (byte)(0x14 | arrayOfByte[0]));
      else if (paramInt <= 198500000)
        i = fc0013_writereg((byte)29, (byte)(0x10 | arrayOfByte[0]));
      else if (paramInt <= 205500000)
        i = fc0013_writereg((byte)29, (byte)(0xC | arrayOfByte[0]));
      else if (paramInt <= 219500000)
        i = fc0013_writereg((byte)29, (byte)(0x8 | arrayOfByte[0]));
      else if (paramInt < 300000000)
        i = fc0013_writereg((byte)29, (byte)(0x4 | arrayOfByte[0]));
      else
        i = fc0013_writereg((byte)29, (byte)(0x1C | arrayOfByte[0]));
    }
  }

  private int fc0013_writereg(byte paramByte1, byte paramByte2)
  {
    int i = SdrUSBDriver.rtlsdr_i2c_write_fn((byte)-58, new byte[] { paramByte1, paramByte2 }, (byte)2);
    int j = 0;
    if (i < 0)
      j = -1;
    return j;
  }

  public int exit(int paramInt)
    throws IOException
  {
    return 0;
  }

  int fc0013_init()
  {
    int i = 0;
    byte[] arrayOfByte = new byte[22];
    arrayOfByte[1] = 9;
    arrayOfByte[2] = 22;
    arrayOfByte[5] = 23;
    arrayOfByte[6] = 2;
    arrayOfByte[7] = 10;
    arrayOfByte[8] = -1;
    arrayOfByte[9] = 110;
    arrayOfByte[10] = -72;
    arrayOfByte[11] = -126;
    arrayOfByte[12] = -4;
    arrayOfByte[13] = 1;
    arrayOfByte[20] = 80;
    arrayOfByte[21] = 1;
    arrayOfByte[7] = ((byte)(0x20 | arrayOfByte[7]));
    arrayOfByte[12] = ((byte)(0x2 | arrayOfByte[12]));
    for (int j = 1; ; j++)
    {
      if (j >= arrayOfByte.length);
      do
      {
        return i;
        i = fc0013_writereg((byte)j, arrayOfByte[j]);
      }
      while (i < 0);
    }
  }

  int fc0013_set_gain_mode(boolean paramBoolean)
  {
    byte[] arrayOfByte = new byte[2];
    int i = 0x0 | fc0013_readreg((byte)13, arrayOfByte);
    if (paramBoolean)
      arrayOfByte[0] = ((byte)(0x8 | arrayOfByte[0]));
    while (true)
    {
      return i | fc0013_writereg((byte)13, arrayOfByte[0]) | fc0013_writereg((byte)19, (byte)10);
      arrayOfByte[0] = ((byte)(0xFFFFFFF7 & arrayOfByte[0]));
    }
  }

  int fc0013_set_lna_gain(int paramInt)
  {
    byte[] arrayOfByte = new byte[1];
    int i = 0x0 | fc0013_readreg((byte)20, arrayOfByte);
    arrayOfByte[0] = ((byte)(0xE0 & arrayOfByte[0]));
    for (int j = 0; ; j++)
    {
      if (j >= this.fc0013_lna_gains.length / 2);
      while (true)
      {
        return i | fc0013_writereg((byte)20, arrayOfByte[0]);
        if ((this.fc0013_lna_gains[(j * 2)] < paramInt) && (j + 1 != this.fc0013_lna_gains.length / 2))
          break;
        arrayOfByte[0] = ((byte)(arrayOfByte[0] | this.fc0013_lna_gains[(1 + j * 2)]));
      }
    }
  }

  int fc0013_set_params(int paramInt1, int paramInt2)
  {
    char[] arrayOfChar = new char[7];
    byte[] arrayOfByte = new byte[1];
    int i = SdrUSBDriver.rtlsdr_get_tuner_clock() / 2;
    if (fc0013_set_vhf_track(paramInt1) < 0)
      return -1;
    if (paramInt1 < 300000000)
    {
      if (fc0013_readreg((byte)7, arrayOfByte) < 0)
        return -1;
      if (fc0013_writereg((byte)7, (byte)(0x10 | arrayOfByte[0])) < 0)
        return -1;
      if (fc0013_readreg((byte)20, arrayOfByte) < 0)
        return -1;
      if (fc0013_writereg((byte)20, (byte)(0x1F & arrayOfByte[0])) < 0)
        return -1;
    }
    else if (paramInt1 <= 862000000)
    {
      if (fc0013_readreg((byte)7, arrayOfByte) < 0)
        return -1;
      if (fc0013_writereg((byte)7, (byte)(0xEF & arrayOfByte[0])) < 0)
        return -1;
      if (fc0013_readreg((byte)20, arrayOfByte) < 0)
        return -1;
      if (fc0013_writereg((byte)20, (byte)(0x40 | 0x1F & arrayOfByte[0])) < 0)
        return -1;
    }
    else
    {
      if (fc0013_readreg((byte)7, arrayOfByte) < 0)
        return -1;
      if (fc0013_writereg((byte)7, (byte)(0xEF & arrayOfByte[0])) < 0)
        return -1;
      if (fc0013_readreg((byte)20, arrayOfByte) < 0)
        return -1;
      if (fc0013_writereg((byte)20, (byte)(0x20 | 0x1F & arrayOfByte[0])) < 0)
        return -1;
    }
    int j;
    long l;
    int k;
    int n;
    int i1;
    if (paramInt1 < 37084000)
    {
      j = 96;
      arrayOfChar[5] = '';
      arrayOfChar[6] = '\000';
      l = paramInt1 * j;
      boolean bool = l < 3060000000L;
      k = 0;
      if (!bool)
      {
        arrayOfChar[6] = ((char)(0x8 | arrayOfChar[6]));
        k = 1;
      }
      int m = (char)(int)(l / i);
      if (l - m * i >= i / 2)
        m = (char)(m + 1);
      n = (byte)(m / 8);
      i1 = (byte)(m - n * 8);
      if (i1 < 2)
      {
        i1 = (byte)(i1 + 8);
        n = (byte)(n - 1);
      }
      if (n <= 31)
        break label667;
      arrayOfChar[1] = ((char)(i1 + 8 * (n - 31)));
      arrayOfChar[2] = '\037';
    }
    while (true)
    {
      if ((arrayOfChar[1] <= '\017') && (arrayOfChar[2] >= '\013'))
        break label682;
      Log.e("TAG", "[FC0013] no valid PLL combination found for " + paramInt1 + " HZ!");
      return -1;
      if (paramInt1 < 55625000)
      {
        j = 64;
        arrayOfChar[5] = '\002';
        arrayOfChar[6] = '\002';
        break;
      }
      if (paramInt1 < 74167000)
      {
        j = 48;
        arrayOfChar[5] = 'B';
        arrayOfChar[6] = '\000';
        break;
      }
      if (paramInt1 < 111250000)
      {
        j = 32;
        arrayOfChar[5] = '';
        arrayOfChar[6] = '\002';
        break;
      }
      if (paramInt1 < 148334000)
      {
        j = 24;
        arrayOfChar[5] = '"';
        arrayOfChar[6] = '\000';
        break;
      }
      if (paramInt1 < 222500000)
      {
        j = 16;
        arrayOfChar[5] = 'B';
        arrayOfChar[6] = '\002';
        break;
      }
      if (paramInt1 < 296667000)
      {
        j = 12;
        arrayOfChar[5] = '\022';
        arrayOfChar[6] = '\000';
        break;
      }
      if (paramInt1 < 445000000)
      {
        j = 8;
        arrayOfChar[5] = '"';
        arrayOfChar[6] = '\002';
        break;
      }
      if (paramInt1 < 593334000)
      {
        j = 6;
        arrayOfChar[5] = '\n';
        arrayOfChar[6] = '\000';
        break;
      }
      if (paramInt1 < 950000000)
      {
        j = 4;
        arrayOfChar[5] = '\022';
        arrayOfChar[6] = '\002';
        break;
      }
      j = 2;
      arrayOfChar[5] = '\n';
      arrayOfChar[6] = '\002';
      break;
      label667: arrayOfChar[1] = ((char)i1);
      arrayOfChar[2] = ((char)n);
    }
    label682: arrayOfChar[6] = ((char)(0x20 | arrayOfChar[6]));
    int i2 = (char)(((char)(int)((l - l / i * i) / 1000L) << '\017') / (i / 1000));
    if (i2 >= 16384)
      i2 = (char)(32768 + i2);
    arrayOfChar[3] = ((char)(i2 >> 8));
    arrayOfChar[4] = ((char)(i2 & 0xFF));
    arrayOfChar[6] = ((char)(0x3F & arrayOfChar[6]));
    switch (paramInt2)
    {
    default:
      arrayOfChar[5] = ((char)(0x7 | arrayOfChar[5]));
    case 6000000:
    case 7000000:
    }
    for (int i3 = 1; ; i3++)
    {
      if (i3 > 6)
      {
        if (fc0013_readreg((byte)17, arrayOfByte) >= 0)
          break label887;
        return -1;
        arrayOfChar[6] = ((char)(0x80 | arrayOfChar[6]));
        break;
        arrayOfChar[6] = ((char)(0x40 | arrayOfChar[6]));
        break;
      }
      if (fc0013_writereg((byte)i3, (byte)arrayOfChar[i3]) < 0)
        return -1;
    }
    label887: if (j == 64);
    for (int i4 = fc0013_writereg((byte)17, (byte)(0x4 | arrayOfByte[0])); i4 < 0; i4 = fc0013_writereg((byte)17, (byte)(0xFB & arrayOfByte[0])))
      return -1;
    int i5 = fc0013_writereg((byte)14, (byte)-128);
    if (i5 != 0)
      i5 = fc0013_writereg((byte)14, (byte)0);
    if (i5 != 0)
      i5 = fc0013_writereg((byte)14, (byte)0);
    if (i5 != 0)
      i5 = fc0013_readreg((byte)14, arrayOfByte);
    if (i5 < 0)
      return -1;
    arrayOfByte[0] = ((byte)(0x3F & arrayOfByte[0]));
    if (k != 0)
      if (arrayOfByte[0] > 60)
      {
        arrayOfChar[6] = ((char)(0xFFFFFFF7 & arrayOfChar[6]));
        i5 = fc0013_writereg((byte)6, (byte)arrayOfChar[6]);
        if (i5 != 0)
          i5 = fc0013_writereg((byte)14, (byte)-128);
        if (i5 != 0)
          i5 = fc0013_writereg((byte)14, (byte)0);
      }
    while (true)
    {
      return i5;
      if (arrayOfByte[0] < 2)
      {
        arrayOfChar[6] = ((char)(0x8 | arrayOfChar[6]));
        i5 = fc0013_writereg((byte)6, (byte)arrayOfChar[6]);
        if (i5 != 0)
          i5 = fc0013_writereg((byte)14, (byte)-128);
        if (i5 != 0)
          i5 = fc0013_writereg((byte)14, (byte)0);
      }
    }
  }

  public int init(int paramInt)
    throws IOException
  {
    fc0013_init();
    return 0;
  }

  public int set_bw(int paramInt1, int paramInt2)
    throws IOException
  {
    return 0;
  }

  public int set_freq(int paramInt, long paramLong)
    throws IOException
  {
    return fc0013_set_params((int)paramLong, 6000000);
  }

  public int set_gain(int paramInt1, int paramInt2)
    throws IOException
  {
    return fc0013_set_lna_gain(paramInt2);
  }

  public int set_gain_mode(int paramInt, boolean paramBoolean)
    throws IOException
  {
    return fc0013_set_gain_mode(paramBoolean);
  }

  public int set_if_gain(int paramInt1, int paramInt2, int paramInt3)
    throws IOException
  {
    return 0;
  }
}

/* Location:           /Users/kevinfinisterre/Desktop/ADSB_USB_playstore.jar
 * Qualified Name:     com.wilsonae.rtlsdrlib.fc0013_tuner
 * JD-Core Version:    0.6.2
 */
