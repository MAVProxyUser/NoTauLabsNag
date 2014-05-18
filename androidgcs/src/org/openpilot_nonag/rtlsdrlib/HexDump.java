package org.openpilot_nonag.rtlsdrlib;

public class HexDump
{
  private static final char[] HEX_DIGITS = { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70 };

  public static String dumpHexString(byte[] paramArrayOfByte)
  {
    return dumpHexString(paramArrayOfByte, 0, paramArrayOfByte.length);
  }

  public static String dumpHexString(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    byte[] arrayOfByte = new byte[16];
    int i = 0;
    localStringBuilder.append("\n0x");
    localStringBuilder.append(toHexString(paramInt1));
    int j = paramInt1;
    int i1;
    if (j >= paramInt1 + paramInt2)
      if (i != 16)
        i1 = 1 + 3 * (16 - i);
    int i3;
    for (int i2 = 0; ; i2++)
    {
      if (i2 >= i1)
      {
        i3 = 0;
        if (i3 < i)
          break label263;
        return localStringBuilder.toString();
        int n;
        if (i == 16)
        {
          localStringBuilder.append(" ");
          n = 0;
          if (n >= 16)
          {
            localStringBuilder.append("\n0x");
            localStringBuilder.append(toHexString(j));
            i = 0;
          }
        }
        else
        {
          int k = paramArrayOfByte[j];
          localStringBuilder.append(" ");
          localStringBuilder.append(HEX_DIGITS[(0xF & k >>> 4)]);
          localStringBuilder.append(HEX_DIGITS[(k & 0xF)]);
          int m = i + 1;
          arrayOfByte[i] = k;
          j++;
          i = m;
          break;
        }
        if ((arrayOfByte[n] > 32) && (arrayOfByte[n] < 126))
          localStringBuilder.append(new String(arrayOfByte, n, 1));
        while (true)
        {
          n++;
          break;
          localStringBuilder.append(".");
        }
      }
      localStringBuilder.append(" ");
    }
    label263: if ((arrayOfByte[i3] > 32) && (arrayOfByte[i3] < 126))
      localStringBuilder.append(new String(arrayOfByte, i3, 1));
    while (true)
    {
      i3++;
      break;
      localStringBuilder.append(".");
    }
  }

  public static byte[] hexStringToByteArray(String paramString)
  {
    int i = paramString.length();
    byte[] arrayOfByte = new byte[i / 2];
    for (int j = 0; ; j += 2)
    {
      if (j >= i)
        return arrayOfByte;
      arrayOfByte[(j / 2)] = ((byte)(toByte(paramString.charAt(j)) << 4 | toByte(paramString.charAt(j + 1))));
    }
  }

  private static int toByte(char paramChar)
  {
    if ((paramChar >= '0') && (paramChar <= '9'))
      return paramChar - '0';
    if ((paramChar >= 'A') && (paramChar <= 'F'))
      return 10 + (paramChar - 'A');
    if ((paramChar >= 'a') && (paramChar <= 'f'))
      return 10 + (paramChar - 'a');
    throw new RuntimeException("Invalid hex char '" + paramChar + "'");
  }

  public static byte[] toByteArray(byte paramByte)
  {
    return new byte[] { paramByte };
  }

  public static byte[] toByteArray(int paramInt)
  {
    byte[] arrayOfByte = new byte[4];
    arrayOfByte[3] = ((byte)(paramInt & 0xFF));
    arrayOfByte[2] = ((byte)(0xFF & paramInt >> 8));
    arrayOfByte[1] = ((byte)(0xFF & paramInt >> 16));
    arrayOfByte[0] = ((byte)(0xFF & paramInt >> 24));
    return arrayOfByte;
  }

  public static byte[] toByteArray(short paramShort)
  {
    byte[] arrayOfByte = new byte[2];
    arrayOfByte[1] = ((byte)(paramShort & 0xFF));
    arrayOfByte[0] = ((byte)(0xFF & paramShort >> 8));
    return arrayOfByte;
  }

  public static String toHexString(byte paramByte)
  {
    return toHexString(toByteArray(paramByte));
  }

  public static String toHexString(int paramInt)
  {
    return toHexString(toByteArray(paramInt));
  }

  public static String toHexString(short paramShort)
  {
    return toHexString(toByteArray(paramShort));
  }

  public static String toHexString(byte[] paramArrayOfByte)
  {
    return toHexString(paramArrayOfByte, 0, paramArrayOfByte.length);
  }

  public static String toHexString(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    char[] arrayOfChar = new char[paramInt2 * 2];
    int i = paramInt1;
    int j = 0;
    while (true)
    {
      if (i >= paramInt1 + paramInt2)
        return new String(arrayOfChar);
      int k = paramArrayOfByte[i];
      int m = j + 1;
      arrayOfChar[j] = HEX_DIGITS[(0xF & k >>> 4)];
      j = m + 1;
      arrayOfChar[m] = HEX_DIGITS[(k & 0xF)];
      i++;
    }
  }
}

/* Location:           /Users/kevinfinisterre/Desktop/ADSB_USB_playstore.jar
 * Qualified Name:     com.wilsonae.rtlsdrlib.HexDump
 * JD-Core Version:    0.6.2
 */
