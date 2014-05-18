package org.openpilot_nonag.rtlsdrlib;

import android.util.Log;
import java.io.IOException;

public class r820T_tuner
  implements RtlSdr_tuner_iface
{
  public static final int ATV_SIZE = 8;
  public static final int DIP_FREQ = 320000;
  public static final int DTMB = 21;
  public static final int DVB_C_6M = 19;
  public static final int DVB_C_8M = 18;
  public static final int DVB_T2_10M = 17;
  public static final int DVB_T2_1_7M = 16;
  public static final int DVB_T2_6M = 12;
  public static final int DVB_T2_7M = 13;
  public static final int DVB_T2_7M_2 = 14;
  public static final int DVB_T2_8M = 15;
  public static final int DVB_T_6M = 8;
  public static final int DVB_T_7M = 9;
  public static final int DVB_T_7M_2 = 10;
  public static final int DVB_T_8M = 11;
  public static final boolean FAST_MODE = true;
  public static final int FM = 23;
  public static final int FUNCTION_ERROR = -1;
  public static final int FUNCTION_SUCCESS = 0;
  static Freq_Info_Type Freq_Info1;
  public static final int IMR_TRIAL = 9;
  public static final int ISDB_T = 20;
  private static final int LOOP_THROUGH = 0;
  public static final boolean NORMAL_MODE = false;
  public static final int NTSC_MN = 0;
  public static final int PAL_BGH_8M = 4;
  public static final int PAL_B_7M = 3;
  public static final int PAL_DK = 2;
  public static final int PAL_I = 1;
  public static final int R620D = 5;
  public static final int R620S = 6;
  public static final int R820C = 4;
  public static final int R820T = 3;
  public static final int R820T_CHECK_ADDR = 0;
  public static final int R820T_CHECK_VAL = 105;
  public static final byte R820T_I2C_ADDR = 52;
  public static final int R820T_IF_FREQ = 3570000;
  public static final int R828 = 0;
  public static final int R828D = 1;
  public static final int R828S = 2;
  public static final int R828_ATSC = 22;
  public static final int R828_Xtal = 28800;
  public static final int SECAM_L = 5;
  public static final int SECAM_L1 = 7;
  public static final int SECAM_L1_INV = 6;
  public static final int STD_SIZE = 23;
  static SysFreq_Info_Type SysFreq_Info1;
  static Sys_Info_Type Sys_Info1;
  private static final String TAG = r820T_tuner.class.getSimpleName();
  public static final boolean TUNER_CLK_OUT = true;
  public static final boolean USE_16M_XTAL = false;
  public static final boolean USE_DIPLEXER = false;
  public static final int VCO_pwr_ref = 2;
  public static final String VERSION = "R820T_v1.49_ASTRO";
  public static final int VER_NUM = 49;
  static final int VGA_BASE_GAIN = -47;
  public static final int XTAL_HIGH_CAP_0P = 4;
  public static final int XTAL_LOW_CAP_0P = 3;
  public static final int XTAL_LOW_CAP_10P = 2;
  public static final int XTAL_LOW_CAP_20P = 1;
  public static final int XTAL_LOW_CAP_30P = 0;
  public static final int _UINT_X_ = 1;
  static int[] r820t_lna_gain_steps;
  static int[] r820t_mixer_gain_steps = arrayOfInt3;
  static int[] r820t_vga_gain_steps;
  byte BW_10M;
  byte BW_1_7M;
  byte BW_200K;
  byte BW_6M;
  byte BW_7M;
  byte BW_8M;
  R828_SectType[] IMR_Data;
  int R828_ADDRESS;
  byte[] R828_Arry;
  int R828_CAL_LO_khz;
  byte[] R828_Fil_Cal_code;
  byte[] R828_Fil_Cal_flag;
  R828_I2C_TYPE R828_I2C = new R828_I2C_TYPE();
  R828_I2C_LEN_TYPE R828_I2C_Len = new R828_I2C_LEN_TYPE();
  int R828_IF_khz;
  boolean R828_IMR_done_flag;
  byte R828_IMR_point_num;
  byte[] R828_iniArry;
  int Rafael_Chip;
  int Xtal_cap_sel;
  int Xtal_cap_sel_tmp;

  static
  {
    int[] arrayOfInt1 = new int[16];
    arrayOfInt1[1] = 26;
    arrayOfInt1[2] = 26;
    arrayOfInt1[3] = 30;
    arrayOfInt1[4] = 42;
    arrayOfInt1[5] = 35;
    arrayOfInt1[6] = 24;
    arrayOfInt1[7] = 13;
    arrayOfInt1[8] = 14;
    arrayOfInt1[9] = 32;
    arrayOfInt1[10] = 36;
    arrayOfInt1[11] = 34;
    arrayOfInt1[12] = 35;
    arrayOfInt1[13] = 37;
    arrayOfInt1[14] = 35;
    arrayOfInt1[15] = 36;
    r820t_vga_gain_steps = arrayOfInt1;
    int[] arrayOfInt2 = new int[16];
    arrayOfInt2[1] = 9;
    arrayOfInt2[2] = 13;
    arrayOfInt2[3] = 40;
    arrayOfInt2[4] = 38;
    arrayOfInt2[5] = 13;
    arrayOfInt2[6] = 31;
    arrayOfInt2[7] = 22;
    arrayOfInt2[8] = 26;
    arrayOfInt2[9] = 31;
    arrayOfInt2[10] = 26;
    arrayOfInt2[11] = 14;
    arrayOfInt2[12] = 19;
    arrayOfInt2[13] = 5;
    arrayOfInt2[14] = 35;
    arrayOfInt2[15] = 13;
    r820t_lna_gain_steps = arrayOfInt2;
    int[] arrayOfInt3 = new int[16];
    arrayOfInt3[1] = 5;
    arrayOfInt3[2] = 10;
    arrayOfInt3[3] = 10;
    arrayOfInt3[4] = 19;
    arrayOfInt3[5] = 9;
    arrayOfInt3[6] = 10;
    arrayOfInt3[7] = 25;
    arrayOfInt3[8] = 17;
    arrayOfInt3[9] = 10;
    arrayOfInt3[10] = 8;
    arrayOfInt3[11] = 16;
    arrayOfInt3[12] = 13;
    arrayOfInt3[13] = 6;
    arrayOfInt3[14] = 3;
    arrayOfInt3[15] = -8;
  }

  public r820T_tuner()
  {
    byte[] arrayOfByte = new byte[27];
    arrayOfByte[0] = -125;
    arrayOfByte[1] = 50;
    arrayOfByte[2] = 117;
    arrayOfByte[3] = -64;
    arrayOfByte[4] = 64;
    arrayOfByte[5] = -42;
    arrayOfByte[6] = 108;
    arrayOfByte[7] = -11;
    arrayOfByte[8] = 99;
    arrayOfByte[9] = 117;
    arrayOfByte[10] = 104;
    arrayOfByte[11] = 108;
    arrayOfByte[12] = -125;
    arrayOfByte[13] = -128;
    arrayOfByte[15] = 15;
    arrayOfByte[17] = -64;
    arrayOfByte[18] = 48;
    arrayOfByte[19] = 72;
    arrayOfByte[20] = -52;
    arrayOfByte[21] = 96;
    arrayOfByte[23] = 84;
    arrayOfByte[24] = -82;
    arrayOfByte[25] = 74;
    arrayOfByte[26] = -64;
    this.R828_iniArry = arrayOfByte;
    this.R828_ADDRESS = 52;
    this.Rafael_Chip = 3;
    this.R828_Arry = new byte[27];
    this.BW_6M = 0;
    this.BW_7M = 1;
    this.BW_8M = 2;
    this.BW_1_7M = 3;
    this.BW_10M = 4;
    this.BW_200K = 5;
    this.IMR_Data = new R828_SectType[5];
    this.R828_IMR_done_flag = false;
    this.R828_Fil_Cal_flag = new byte[23];
    this.R828_Fil_Cal_code = new byte[23];
    this.Xtal_cap_sel = 3;
    this.Xtal_cap_sel_tmp = 3;
  }

  boolean I2C_Read_Len(int paramInt, R828_I2C_LEN_TYPE paramR828_I2C_LEN_TYPE)
  {
    byte[] arrayOfByte1 = new byte[1];
    byte[] arrayOfByte2 = new byte[''];
    byte b1 = paramR828_I2C_LEN_TYPE.Len;
    if (SdrUSBDriver.rtlsdr_i2c_write_fn((byte)52, arrayOfByte1, (byte)1) < 0);
    while (SdrUSBDriver.rtlsdr_i2c_read_fn((byte)52, arrayOfByte2, b1) < 0)
      return false;
    for (byte b2 = 0; ; b2++)
    {
      if (b2 >= b1)
        return true;
      paramR828_I2C_LEN_TYPE.Data[b2] = ((byte)r820t_Convert(arrayOfByte2[b2]));
    }
  }

  boolean I2C_Write(int paramInt, R828_I2C_TYPE paramR828_I2C_TYPE)
  {
    byte[] arrayOfByte = new byte[2];
    arrayOfByte[0] = paramR828_I2C_TYPE.RegAddr;
    arrayOfByte[1] = paramR828_I2C_TYPE.Data;
    return SdrUSBDriver.rtlsdr_i2c_write_fn((byte)52, arrayOfByte, (byte)2) >= 0;
  }

  boolean I2C_Write_Len(int paramInt, R828_I2C_LEN_TYPE paramR828_I2C_LEN_TYPE)
  {
    new byte[paramR828_I2C_LEN_TYPE.Data.length];
    byte[] arrayOfByte = new byte[2];
    int i = paramR828_I2C_LEN_TYPE.RegAddr;
    long l1 = paramR828_I2C_LEN_TYPE.Len;
    int j = 0;
    if (j >= l1)
      return true;
    int k = (byte)(i + j);
    long l2 = (char)(int)(l1 - j);
    long l3;
    label74: int m;
    if (l2 > 1L)
    {
      l3 = 1L;
      arrayOfByte[0] = k;
      m = 0;
      label83: if (m < l3)
        break label179;
      Log.d(TAG, "WritingBuffer[0] " + arrayOfByte[0] + "," + " WritingBuffer[1] " + arrayOfByte[1] + " i= " + j);
    }
    label179: 
    do
    {
      if (SdrUSBDriver.rtlsdr_i2c_write_fn((byte)52, arrayOfByte, (byte)(int)(1L + l3)) >= 0)
      {
        j = (int)(1L + j);
        break;
        l3 = l2;
        break label74;
        arrayOfByte[(m + 1)] = paramR828_I2C_LEN_TYPE.Data[(j + m)];
        m++;
        break label83;
      }
      Log.d(TAG, "WritingBuffer[0] " + arrayOfByte[0] + "," + " WritingBuffer[1] " + arrayOfByte[1] + " i= " + j + " FAILED!!!");
    }
    while (0 + 1 <= 5);
    return false;
  }

  boolean R828_CompreCor(R828_SectType[] paramArrayOfR828_SectType)
  {
    for (int i = 3; ; i = (byte)(i - 1))
    {
      if (i <= 0)
        return true;
      if (paramArrayOfR828_SectType[0].Value > paramArrayOfR828_SectType[(i - 1)].Value)
      {
        R828_SectType localR828_SectType = paramArrayOfR828_SectType[0];
        paramArrayOfR828_SectType[0] = paramArrayOfR828_SectType[(i - 1)];
        paramArrayOfR828_SectType[(i - 1)] = localR828_SectType;
      }
    }
  }

  boolean R828_CompreStep(int paramInt, R828_SectType[] paramArrayOfR828_SectType, byte paramByte)
  {
    R828_SectType localR828_SectType = new R828_SectType();
    localR828_SectType.Phase_Y = paramArrayOfR828_SectType[0].Phase_Y;
    localR828_SectType.Gain_X = paramArrayOfR828_SectType[0].Gain_X;
    label58: boolean bool1;
    if (((0x1F & localR828_SectType.Gain_X) >= 9) || ((0x1F & localR828_SectType.Phase_Y) >= 9))
    {
      bool1 = true;
      label61: return bool1;
    }
    if (paramByte == 8)
      localR828_SectType.Gain_X = (1 + localR828_SectType.Gain_X);
    while (true)
    {
      this.R828_I2C.RegAddr = 8;
      this.R828_I2C.Data = ((byte)localR828_SectType.Gain_X);
      boolean bool2 = I2C_Write(paramInt, this.R828_I2C);
      bool1 = false;
      if (!bool2)
        break label61;
      this.R828_I2C.RegAddr = 9;
      this.R828_I2C.Data = ((byte)localR828_SectType.Phase_Y);
      boolean bool3 = I2C_Write(paramInt, this.R828_I2C);
      bool1 = false;
      if (!bool3)
        break label61;
      char[] arrayOfChar = new char[1];
      boolean bool4 = R828_Muti_Read(paramInt, (byte)1, arrayOfChar);
      bool1 = false;
      if (!bool4)
        break label61;
      localR828_SectType.Value = arrayOfChar[0];
      if (localR828_SectType.Value > paramArrayOfR828_SectType[0].Value)
        break label58;
      paramArrayOfR828_SectType[0].Gain_X = localR828_SectType.Gain_X;
      paramArrayOfR828_SectType[0].Phase_Y = localR828_SectType.Phase_Y;
      paramArrayOfR828_SectType[0].Value = localR828_SectType.Value;
      break;
      localR828_SectType.Phase_Y = (1 + localR828_SectType.Phase_Y);
    }
  }

  void R828_Delay_MS(int paramInt, long paramLong)
  {
  }

  boolean R828_F_IMR(int paramInt, R828_SectType[] paramArrayOfR828_SectType)
  {
    R828_SectType[] arrayOfR828_SectType1 = new R828_SectType[3];
    R828_SectType[] arrayOfR828_SectType2 = new R828_SectType[3];
    int i = 12;
    if (i >= 16)
      label22: if ((0x1F & paramArrayOfR828_SectType[0].Gain_X) != 0)
        break label180;
    label180: for (arrayOfR828_SectType1[0].Gain_X = (1 + (0xDF & paramArrayOfR828_SectType[0].Gain_X)); ; arrayOfR828_SectType1[0].Gain_X = (-1 + paramArrayOfR828_SectType[0].Gain_X))
    {
      arrayOfR828_SectType1[0].Phase_Y = paramArrayOfR828_SectType[0].Phase_Y;
      if (R828_IQ_Tree(paramInt, arrayOfR828_SectType1[0].Gain_X, arrayOfR828_SectType1[0].Phase_Y, 8, arrayOfR828_SectType1))
        break label197;
      return false;
      this.R828_I2C.RegAddr = 12;
      this.R828_I2C.Data = ((byte)(i + (0xF0 & this.R828_Arry[7])));
      if (!I2C_Write(paramInt, this.R828_I2C))
        return false;
      R828_Delay_MS(paramInt, 10L);
      char[] arrayOfChar = new char[1];
      if (!R828_Muti_Read(paramInt, (byte)1, arrayOfChar))
        return false;
      if (arrayOfChar[0] > ' ')
        break label22;
      i = (byte)(i + 1);
      break;
    }
    label197: if (!R828_CompreCor(arrayOfR828_SectType1))
      return false;
    arrayOfR828_SectType2[0].Gain_X = arrayOfR828_SectType1[0].Gain_X;
    arrayOfR828_SectType2[0].Phase_Y = arrayOfR828_SectType1[0].Phase_Y;
    arrayOfR828_SectType2[0].Value = arrayOfR828_SectType1[0].Value;
    arrayOfR828_SectType1[0].Gain_X = paramArrayOfR828_SectType[0].Gain_X;
    arrayOfR828_SectType1[0].Phase_Y = paramArrayOfR828_SectType[0].Phase_Y;
    if (!R828_IQ_Tree(paramInt, arrayOfR828_SectType1[0].Gain_X, arrayOfR828_SectType1[0].Phase_Y, 8, arrayOfR828_SectType1))
      return false;
    if (!R828_CompreCor(arrayOfR828_SectType1))
      return false;
    arrayOfR828_SectType2[1].Gain_X = arrayOfR828_SectType1[0].Gain_X;
    arrayOfR828_SectType2[1].Phase_Y = arrayOfR828_SectType1[0].Phase_Y;
    arrayOfR828_SectType2[1].Value = arrayOfR828_SectType1[0].Value;
    if ((0x1F & paramArrayOfR828_SectType[0].Gain_X) == 0);
    for (arrayOfR828_SectType1[0].Gain_X = (1 + (0x20 | paramArrayOfR828_SectType[0].Gain_X)); ; arrayOfR828_SectType1[0].Gain_X = (1 + paramArrayOfR828_SectType[0].Gain_X))
    {
      arrayOfR828_SectType1[0].Phase_Y = paramArrayOfR828_SectType[0].Phase_Y;
      if (R828_IQ_Tree(paramInt, arrayOfR828_SectType1[0].Gain_X, arrayOfR828_SectType1[0].Phase_Y, 8, arrayOfR828_SectType1))
        break;
      return false;
    }
    if (!R828_CompreCor(arrayOfR828_SectType1))
      return false;
    arrayOfR828_SectType2[2].Gain_X = arrayOfR828_SectType1[0].Gain_X;
    arrayOfR828_SectType2[2].Phase_Y = arrayOfR828_SectType1[0].Phase_Y;
    arrayOfR828_SectType2[2].Value = arrayOfR828_SectType1[0].Value;
    if (!R828_CompreCor(arrayOfR828_SectType2))
      return false;
    paramArrayOfR828_SectType[0] = arrayOfR828_SectType2[0];
    return true;
  }

  boolean R828_Filt_Cal(int paramInt1, int paramInt2, int paramInt3)
  {
    this.R828_I2C.RegAddr = 11;
    this.R828_Arry[6] = ((byte)(0x9F & this.R828_Arry[6] | 0x60 & Sys_Info1.HP_COR));
    this.R828_I2C.Data = this.R828_Arry[6];
    if (!I2C_Write(paramInt1, this.R828_I2C));
    do
    {
      do
      {
        do
        {
          do
          {
            do
            {
              return false;
              this.R828_I2C.RegAddr = 15;
              byte[] arrayOfByte1 = this.R828_Arry;
              arrayOfByte1[10] = ((byte)(0x4 | arrayOfByte1[10]));
              this.R828_I2C.Data = this.R828_Arry[10];
            }
            while (!I2C_Write(paramInt1, this.R828_I2C));
            this.R828_I2C.RegAddr = 16;
            this.R828_Arry[11] = ((byte)(0xFC & this.R828_Arry[11]));
            this.R828_I2C.Data = this.R828_Arry[11];
          }
          while ((!I2C_Write(paramInt1, this.R828_I2C)) || (!R828_PLL(paramInt1, paramInt2 * 1000, 23)));
          this.R828_I2C.RegAddr = 11;
          byte[] arrayOfByte2 = this.R828_Arry;
          arrayOfByte2[6] = ((byte)(0x10 | arrayOfByte2[6]));
          this.R828_I2C.Data = this.R828_Arry[6];
        }
        while (!I2C_Write(paramInt1, this.R828_I2C));
        R828_Delay_MS(paramInt1, 1L);
        this.R828_I2C.RegAddr = 11;
        byte[] arrayOfByte3 = this.R828_Arry;
        arrayOfByte3[6] = ((byte)(0xEF & arrayOfByte3[6]));
        this.R828_I2C.Data = this.R828_Arry[6];
      }
      while (!I2C_Write(paramInt1, this.R828_I2C));
      this.R828_I2C.RegAddr = 15;
      byte[] arrayOfByte4 = this.R828_Arry;
      arrayOfByte4[10] = ((byte)(0xFB & arrayOfByte4[10]));
      this.R828_I2C.Data = this.R828_Arry[10];
    }
    while (!I2C_Write(paramInt1, this.R828_I2C));
    return true;
  }

  Freq_Info_Type R828_Freq_Sel(int paramInt)
  {
    Freq_Info_Type localFreq_Info_Type = new Freq_Info_Type();
    if (paramInt < 50000)
    {
      localFreq_Info_Type.OPEN_D = 8;
      localFreq_Info_Type.RF_MUX_PLOY = 2;
      localFreq_Info_Type.TF_C = -33;
      localFreq_Info_Type.XTAL_CAP20P = 2;
      localFreq_Info_Type.XTAL_CAP10P = 1;
      localFreq_Info_Type.XTAL_CAP0P = 0;
      localFreq_Info_Type.IMR_MEM = 0;
      return localFreq_Info_Type;
    }
    if ((paramInt >= 50000) && (paramInt < 55000))
    {
      localFreq_Info_Type.OPEN_D = 8;
      localFreq_Info_Type.RF_MUX_PLOY = 2;
      localFreq_Info_Type.TF_C = -66;
      localFreq_Info_Type.XTAL_CAP20P = 2;
      localFreq_Info_Type.XTAL_CAP10P = 1;
      localFreq_Info_Type.XTAL_CAP0P = 0;
      localFreq_Info_Type.IMR_MEM = 0;
      return localFreq_Info_Type;
    }
    if ((paramInt >= 55000) && (paramInt < 60000))
    {
      localFreq_Info_Type.OPEN_D = 8;
      localFreq_Info_Type.RF_MUX_PLOY = 2;
      localFreq_Info_Type.TF_C = -117;
      localFreq_Info_Type.XTAL_CAP20P = 2;
      localFreq_Info_Type.XTAL_CAP10P = 1;
      localFreq_Info_Type.XTAL_CAP0P = 0;
      localFreq_Info_Type.IMR_MEM = 0;
      return localFreq_Info_Type;
    }
    if ((paramInt >= 60000) && (paramInt < 65000))
    {
      localFreq_Info_Type.OPEN_D = 8;
      localFreq_Info_Type.RF_MUX_PLOY = 2;
      localFreq_Info_Type.TF_C = 123;
      localFreq_Info_Type.XTAL_CAP20P = 2;
      localFreq_Info_Type.XTAL_CAP10P = 1;
      localFreq_Info_Type.XTAL_CAP0P = 0;
      localFreq_Info_Type.IMR_MEM = 0;
      return localFreq_Info_Type;
    }
    if ((paramInt >= 65000) && (paramInt < 70000))
    {
      localFreq_Info_Type.OPEN_D = 8;
      localFreq_Info_Type.RF_MUX_PLOY = 2;
      localFreq_Info_Type.TF_C = 105;
      localFreq_Info_Type.XTAL_CAP20P = 2;
      localFreq_Info_Type.XTAL_CAP10P = 1;
      localFreq_Info_Type.XTAL_CAP0P = 0;
      localFreq_Info_Type.IMR_MEM = 0;
      return localFreq_Info_Type;
    }
    if ((paramInt >= 70000) && (paramInt < 75000))
    {
      localFreq_Info_Type.OPEN_D = 8;
      localFreq_Info_Type.RF_MUX_PLOY = 2;
      localFreq_Info_Type.TF_C = 88;
      localFreq_Info_Type.XTAL_CAP20P = 2;
      localFreq_Info_Type.XTAL_CAP10P = 1;
      localFreq_Info_Type.XTAL_CAP0P = 0;
      localFreq_Info_Type.IMR_MEM = 0;
      return localFreq_Info_Type;
    }
    if ((paramInt >= 75000) && (paramInt < 80000))
    {
      localFreq_Info_Type.OPEN_D = 0;
      localFreq_Info_Type.RF_MUX_PLOY = 2;
      localFreq_Info_Type.TF_C = 68;
      localFreq_Info_Type.XTAL_CAP20P = 2;
      localFreq_Info_Type.XTAL_CAP10P = 1;
      localFreq_Info_Type.XTAL_CAP0P = 0;
      localFreq_Info_Type.IMR_MEM = 0;
      return localFreq_Info_Type;
    }
    if ((paramInt >= 80000) && (paramInt < 90000))
    {
      localFreq_Info_Type.OPEN_D = 0;
      localFreq_Info_Type.RF_MUX_PLOY = 2;
      localFreq_Info_Type.TF_C = 68;
      localFreq_Info_Type.XTAL_CAP20P = 2;
      localFreq_Info_Type.XTAL_CAP10P = 1;
      localFreq_Info_Type.XTAL_CAP0P = 0;
      localFreq_Info_Type.IMR_MEM = 0;
      return localFreq_Info_Type;
    }
    if ((paramInt >= 90000) && (paramInt < 100000))
    {
      localFreq_Info_Type.OPEN_D = 0;
      localFreq_Info_Type.RF_MUX_PLOY = 2;
      localFreq_Info_Type.TF_C = 52;
      localFreq_Info_Type.XTAL_CAP20P = 1;
      localFreq_Info_Type.XTAL_CAP10P = 1;
      localFreq_Info_Type.XTAL_CAP0P = 0;
      localFreq_Info_Type.IMR_MEM = 0;
      return localFreq_Info_Type;
    }
    if ((paramInt >= 100000) && (paramInt < 110000))
    {
      localFreq_Info_Type.OPEN_D = 0;
      localFreq_Info_Type.RF_MUX_PLOY = 2;
      localFreq_Info_Type.TF_C = 52;
      localFreq_Info_Type.XTAL_CAP20P = 1;
      localFreq_Info_Type.XTAL_CAP10P = 1;
      localFreq_Info_Type.XTAL_CAP0P = 0;
      localFreq_Info_Type.IMR_MEM = 0;
      return localFreq_Info_Type;
    }
    if ((paramInt >= 110000) && (paramInt < 120000))
    {
      localFreq_Info_Type.OPEN_D = 0;
      localFreq_Info_Type.RF_MUX_PLOY = 2;
      localFreq_Info_Type.TF_C = 36;
      localFreq_Info_Type.XTAL_CAP20P = 1;
      localFreq_Info_Type.XTAL_CAP10P = 1;
      localFreq_Info_Type.XTAL_CAP0P = 0;
      localFreq_Info_Type.IMR_MEM = 1;
      return localFreq_Info_Type;
    }
    if ((paramInt >= 120000) && (paramInt < 140000))
    {
      localFreq_Info_Type.OPEN_D = 0;
      localFreq_Info_Type.RF_MUX_PLOY = 2;
      localFreq_Info_Type.TF_C = 36;
      localFreq_Info_Type.XTAL_CAP20P = 1;
      localFreq_Info_Type.XTAL_CAP10P = 1;
      localFreq_Info_Type.XTAL_CAP0P = 0;
      localFreq_Info_Type.IMR_MEM = 1;
      return localFreq_Info_Type;
    }
    if ((paramInt >= 140000) && (paramInt < 180000))
    {
      localFreq_Info_Type.OPEN_D = 0;
      localFreq_Info_Type.RF_MUX_PLOY = 2;
      localFreq_Info_Type.TF_C = 20;
      localFreq_Info_Type.XTAL_CAP20P = 1;
      localFreq_Info_Type.XTAL_CAP10P = 1;
      localFreq_Info_Type.XTAL_CAP0P = 0;
      localFreq_Info_Type.IMR_MEM = 1;
      return localFreq_Info_Type;
    }
    if ((paramInt >= 180000) && (paramInt < 220000))
    {
      localFreq_Info_Type.OPEN_D = 0;
      localFreq_Info_Type.RF_MUX_PLOY = 2;
      localFreq_Info_Type.TF_C = 19;
      localFreq_Info_Type.XTAL_CAP20P = 0;
      localFreq_Info_Type.XTAL_CAP10P = 0;
      localFreq_Info_Type.XTAL_CAP0P = 0;
      localFreq_Info_Type.IMR_MEM = 1;
      return localFreq_Info_Type;
    }
    if ((paramInt >= 220000) && (paramInt < 250000))
    {
      localFreq_Info_Type.OPEN_D = 0;
      localFreq_Info_Type.RF_MUX_PLOY = 2;
      localFreq_Info_Type.TF_C = 19;
      localFreq_Info_Type.XTAL_CAP20P = 0;
      localFreq_Info_Type.XTAL_CAP10P = 0;
      localFreq_Info_Type.XTAL_CAP0P = 0;
      localFreq_Info_Type.IMR_MEM = 2;
      return localFreq_Info_Type;
    }
    if ((paramInt >= 250000) && (paramInt < 280000))
    {
      localFreq_Info_Type.OPEN_D = 0;
      localFreq_Info_Type.RF_MUX_PLOY = 2;
      localFreq_Info_Type.TF_C = 17;
      localFreq_Info_Type.XTAL_CAP20P = 0;
      localFreq_Info_Type.XTAL_CAP10P = 0;
      localFreq_Info_Type.XTAL_CAP0P = 0;
      localFreq_Info_Type.IMR_MEM = 2;
      return localFreq_Info_Type;
    }
    if ((paramInt >= 280000) && (paramInt < 310000))
    {
      localFreq_Info_Type.OPEN_D = 0;
      localFreq_Info_Type.RF_MUX_PLOY = 2;
      localFreq_Info_Type.TF_C = 0;
      localFreq_Info_Type.XTAL_CAP20P = 0;
      localFreq_Info_Type.XTAL_CAP10P = 0;
      localFreq_Info_Type.XTAL_CAP0P = 0;
      localFreq_Info_Type.IMR_MEM = 2;
      return localFreq_Info_Type;
    }
    if ((paramInt >= 310000) && (paramInt < 450000))
    {
      localFreq_Info_Type.OPEN_D = 0;
      localFreq_Info_Type.RF_MUX_PLOY = 65;
      localFreq_Info_Type.TF_C = 0;
      localFreq_Info_Type.XTAL_CAP20P = 0;
      localFreq_Info_Type.XTAL_CAP10P = 0;
      localFreq_Info_Type.XTAL_CAP0P = 0;
      localFreq_Info_Type.IMR_MEM = 2;
      return localFreq_Info_Type;
    }
    if ((paramInt >= 450000) && (paramInt < 588000))
    {
      localFreq_Info_Type.OPEN_D = 0;
      localFreq_Info_Type.RF_MUX_PLOY = 65;
      localFreq_Info_Type.TF_C = 0;
      localFreq_Info_Type.XTAL_CAP20P = 0;
      localFreq_Info_Type.XTAL_CAP10P = 0;
      localFreq_Info_Type.XTAL_CAP0P = 0;
      localFreq_Info_Type.IMR_MEM = 3;
      return localFreq_Info_Type;
    }
    if ((paramInt >= 588000) && (paramInt < 650000))
    {
      localFreq_Info_Type.OPEN_D = 0;
      localFreq_Info_Type.RF_MUX_PLOY = 64;
      localFreq_Info_Type.TF_C = 0;
      localFreq_Info_Type.XTAL_CAP20P = 0;
      localFreq_Info_Type.XTAL_CAP10P = 0;
      localFreq_Info_Type.XTAL_CAP0P = 0;
      localFreq_Info_Type.IMR_MEM = 3;
      return localFreq_Info_Type;
    }
    localFreq_Info_Type.OPEN_D = 0;
    localFreq_Info_Type.RF_MUX_PLOY = 64;
    localFreq_Info_Type.TF_C = 0;
    localFreq_Info_Type.XTAL_CAP20P = 0;
    localFreq_Info_Type.XTAL_CAP10P = 0;
    localFreq_Info_Type.XTAL_CAP0P = 0;
    localFreq_Info_Type.IMR_MEM = 4;
    return localFreq_Info_Type;
  }

  boolean R828_GPIO(int paramInt, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      byte[] arrayOfByte2 = this.R828_Arry;
      arrayOfByte2[10] = ((byte)(0x1 | arrayOfByte2[10]));
    }
    while (true)
    {
      this.R828_I2C.RegAddr = 15;
      this.R828_I2C.Data = this.R828_Arry[10];
      if (I2C_Write(paramInt, this.R828_I2C))
        break;
      return false;
      byte[] arrayOfByte1 = this.R828_Arry;
      arrayOfByte1[10] = ((byte)(0xFE & arrayOfByte1[10]));
    }
    return true;
  }

  boolean R828_GetRfGain(int paramInt, R828_RF_Gain_Info[] paramArrayOfR828_RF_Gain_Info)
  {
    this.R828_I2C_Len.RegAddr = 0;
    this.R828_I2C_Len.Len = 4;
    if (!I2C_Read_Len(paramInt, this.R828_I2C_Len))
      return false;
    paramArrayOfR828_RF_Gain_Info[0].RF_gain1 = ((byte)(0xF & this.R828_I2C_Len.Data[3]));
    paramArrayOfR828_RF_Gain_Info[0].RF_gain2 = ((byte)((0xF0 & this.R828_I2C_Len.Data[3]) >> 4));
    paramArrayOfR828_RF_Gain_Info[0].RF_gain_comb = ((byte)(2 * paramArrayOfR828_RF_Gain_Info[0].RF_gain1 + paramArrayOfR828_RF_Gain_Info[0].RF_gain2));
    return true;
  }

  boolean R828_IMR(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    R828_SectType[] arrayOfR828_SectType = new R828_SectType[1];
    int i = 0;
    int j = 0;
    label19: int k;
    int m;
    if (j >= 16)
    {
      byte[] arrayOfByte1 = this.R828_Arry;
      arrayOfByte1[19] = ((byte)(0xF0 & arrayOfByte1[19]));
      byte[] arrayOfByte2 = this.R828_Arry;
      arrayOfByte2[19] = ((byte)(i | arrayOfByte2[19]));
      k = 14400 * (8 * (i + 16));
      byte[] arrayOfByte3 = this.R828_Arry;
      arrayOfByte3[19] = ((byte)(0xDF & arrayOfByte3[19]));
      byte[] arrayOfByte4 = this.R828_Arry;
      arrayOfByte4[20] = ((byte)(0xFC & arrayOfByte4[20]));
      byte[] arrayOfByte5 = this.R828_Arry;
      arrayOfByte5[26] = ((byte)(0xFC & arrayOfByte5[26]));
      switch (paramInt2)
      {
      default:
        m = k / 4;
        byte[] arrayOfByte21 = this.R828_Arry;
        arrayOfByte21[19] = ((byte)(0x0 | arrayOfByte21[19]));
        byte[] arrayOfByte22 = this.R828_Arry;
        arrayOfByte22[20] = ((byte)(0x0 | arrayOfByte22[20]));
        byte[] arrayOfByte23 = this.R828_Arry;
        arrayOfByte23[26] = ((byte)(0x1 | arrayOfByte23[26]));
      case 0:
      case 1:
      case 2:
      case 3:
      case 4:
      }
    }
    while (true)
    {
      this.R828_I2C.RegAddr = 24;
      this.R828_I2C.Data = this.R828_Arry[19];
      if (I2C_Write(paramInt1, this.R828_I2C))
        break label650;
      return false;
      if (14400 * (8 * (j + 16)) >= 3100000)
      {
        i = j;
        break label19;
      }
      if (j == 15)
        i = j;
      j++;
      break;
      m = k / 48;
      byte[] arrayOfByte18 = this.R828_Arry;
      arrayOfByte18[19] = ((byte)(0x20 | arrayOfByte18[19]));
      byte[] arrayOfByte19 = this.R828_Arry;
      arrayOfByte19[20] = ((byte)(0x3 | arrayOfByte19[20]));
      byte[] arrayOfByte20 = this.R828_Arry;
      arrayOfByte20[26] = ((byte)(0x2 | arrayOfByte20[26]));
      continue;
      m = k / 16;
      byte[] arrayOfByte15 = this.R828_Arry;
      arrayOfByte15[19] = ((byte)(0x0 | arrayOfByte15[19]));
      byte[] arrayOfByte16 = this.R828_Arry;
      arrayOfByte16[20] = ((byte)(0x2 | arrayOfByte16[20]));
      byte[] arrayOfByte17 = this.R828_Arry;
      arrayOfByte17[26] = ((byte)(0x0 | arrayOfByte17[26]));
      continue;
      m = k / 8;
      byte[] arrayOfByte12 = this.R828_Arry;
      arrayOfByte12[19] = ((byte)(0x0 | arrayOfByte12[19]));
      byte[] arrayOfByte13 = this.R828_Arry;
      arrayOfByte13[20] = ((byte)(0x1 | arrayOfByte13[20]));
      byte[] arrayOfByte14 = this.R828_Arry;
      arrayOfByte14[26] = ((byte)(0x3 | arrayOfByte14[26]));
      continue;
      m = k / 6;
      byte[] arrayOfByte9 = this.R828_Arry;
      arrayOfByte9[19] = ((byte)(0x20 | arrayOfByte9[19]));
      byte[] arrayOfByte10 = this.R828_Arry;
      arrayOfByte10[20] = ((byte)(0x0 | arrayOfByte10[20]));
      byte[] arrayOfByte11 = this.R828_Arry;
      arrayOfByte11[26] = ((byte)(0x3 | arrayOfByte11[26]));
      continue;
      m = k / 4;
      byte[] arrayOfByte6 = this.R828_Arry;
      arrayOfByte6[19] = ((byte)(0x0 | arrayOfByte6[19]));
      byte[] arrayOfByte7 = this.R828_Arry;
      arrayOfByte7[20] = ((byte)(0x0 | arrayOfByte7[20]));
      byte[] arrayOfByte8 = this.R828_Arry;
      arrayOfByte8[26] = ((byte)(0x1 | arrayOfByte8[26]));
    }
    label650: this.R828_I2C.RegAddr = 25;
    this.R828_I2C.Data = this.R828_Arry[20];
    if (!I2C_Write(paramInt1, this.R828_I2C))
      return false;
    this.R828_I2C.RegAddr = 31;
    this.R828_I2C.Data = this.R828_Arry[26];
    if (!I2C_Write(paramInt1, this.R828_I2C))
      return false;
    if (!R828_MUX(paramInt1, m - 5300))
      return false;
    if (!R828_PLL(paramInt1, 1000 * (m - 5300), 23))
      return false;
    if (paramBoolean)
    {
      if (!R828_IQ(paramInt1, arrayOfR828_SectType))
        return false;
    }
    else
    {
      arrayOfR828_SectType[0].Gain_X = this.IMR_Data[3].Gain_X;
      arrayOfR828_SectType[0].Phase_Y = this.IMR_Data[3].Phase_Y;
      arrayOfR828_SectType[0].Value = this.IMR_Data[3].Value;
      if (!R828_F_IMR(paramInt1, arrayOfR828_SectType))
        return false;
    }
    switch (paramInt2)
    {
    default:
      this.IMR_Data[4].Gain_X = arrayOfR828_SectType[0].Gain_X;
      this.IMR_Data[4].Phase_Y = arrayOfR828_SectType[0].Phase_Y;
      this.IMR_Data[4].Value = arrayOfR828_SectType[0].Value;
    case 0:
    case 1:
    case 2:
    case 3:
    case 4:
    }
    while (true)
    {
      return true;
      this.IMR_Data[0].Gain_X = arrayOfR828_SectType[0].Gain_X;
      this.IMR_Data[0].Phase_Y = arrayOfR828_SectType[0].Phase_Y;
      this.IMR_Data[0].Value = arrayOfR828_SectType[0].Value;
      continue;
      this.IMR_Data[1].Gain_X = arrayOfR828_SectType[0].Gain_X;
      this.IMR_Data[1].Phase_Y = arrayOfR828_SectType[0].Phase_Y;
      this.IMR_Data[1].Value = arrayOfR828_SectType[0].Value;
      continue;
      this.IMR_Data[2].Gain_X = arrayOfR828_SectType[0].Gain_X;
      this.IMR_Data[2].Phase_Y = arrayOfR828_SectType[0].Phase_Y;
      this.IMR_Data[2].Value = arrayOfR828_SectType[0].Value;
      continue;
      this.IMR_Data[3].Gain_X = arrayOfR828_SectType[0].Gain_X;
      this.IMR_Data[3].Phase_Y = arrayOfR828_SectType[0].Phase_Y;
      this.IMR_Data[3].Value = arrayOfR828_SectType[0].Value;
      continue;
      this.IMR_Data[4].Gain_X = arrayOfR828_SectType[0].Gain_X;
      this.IMR_Data[4].Phase_Y = arrayOfR828_SectType[0].Phase_Y;
      this.IMR_Data[4].Value = arrayOfR828_SectType[0].Value;
    }
  }

  boolean R828_IMR_Cross(int paramInt, R828_SectType[] paramArrayOfR828_SectType, byte[] paramArrayOfByte)
  {
    R828_SectType[] arrayOfR828_SectType = new R828_SectType[5];
    R828_SectType localR828_SectType = new R828_SectType();
    int i = (byte)(0xC0 & this.R828_iniArry[3]);
    int j = (byte)(0xC0 & this.R828_iniArry[4]);
    localR828_SectType.Gain_X = 0;
    localR828_SectType.Phase_Y = 0;
    localR828_SectType.Value = 0;
    localR828_SectType.Value = 255;
    int k = 0;
    if (k >= 5)
    {
      if ((0x1F & localR828_SectType.Phase_Y) != 1)
        break label534;
      paramArrayOfByte[0] = 0;
      paramArrayOfR828_SectType[0].Gain_X = arrayOfR828_SectType[0].Gain_X;
      paramArrayOfR828_SectType[0].Phase_Y = arrayOfR828_SectType[0].Phase_Y;
      paramArrayOfR828_SectType[0].Value = arrayOfR828_SectType[0].Value;
      paramArrayOfR828_SectType[1].Gain_X = arrayOfR828_SectType[1].Gain_X;
      paramArrayOfR828_SectType[1].Phase_Y = arrayOfR828_SectType[1].Phase_Y;
      paramArrayOfR828_SectType[1].Value = arrayOfR828_SectType[1].Value;
      paramArrayOfR828_SectType[2].Gain_X = arrayOfR828_SectType[2].Gain_X;
      paramArrayOfR828_SectType[2].Phase_Y = arrayOfR828_SectType[2].Phase_Y;
    }
    for (paramArrayOfR828_SectType[2].Value = arrayOfR828_SectType[2].Value; ; paramArrayOfR828_SectType[2].Value = arrayOfR828_SectType[4].Value)
    {
      return true;
      if (k == 0)
      {
        arrayOfR828_SectType[k].Gain_X = i;
        arrayOfR828_SectType[k].Phase_Y = j;
      }
      while (true)
      {
        this.R828_I2C.RegAddr = 8;
        this.R828_I2C.Data = ((byte)arrayOfR828_SectType[k].Gain_X);
        if (I2C_Write(paramInt, this.R828_I2C))
          break;
        return false;
        if (k == 1)
        {
          arrayOfR828_SectType[k].Gain_X = i;
          arrayOfR828_SectType[k].Phase_Y = (j + 1);
        }
        else if (k == 2)
        {
          arrayOfR828_SectType[k].Gain_X = i;
          arrayOfR828_SectType[k].Phase_Y = (1 + (j | 0x20));
        }
        else if (k == 3)
        {
          arrayOfR828_SectType[k].Gain_X = (i + 1);
          arrayOfR828_SectType[k].Phase_Y = j;
        }
        else
        {
          arrayOfR828_SectType[k].Gain_X = (1 + (i | 0x20));
          arrayOfR828_SectType[k].Phase_Y = j;
        }
      }
      this.R828_I2C.RegAddr = 9;
      this.R828_I2C.Data = ((byte)arrayOfR828_SectType[k].Phase_Y);
      if (!I2C_Write(paramInt, this.R828_I2C))
        return false;
      char[] arrayOfChar = new char[1];
      if (!R828_Muti_Read(paramInt, (byte)1, arrayOfChar))
        return false;
      arrayOfR828_SectType[k].Value = arrayOfChar[0];
      if (arrayOfR828_SectType[k].Value < localR828_SectType.Value)
      {
        localR828_SectType.Value = arrayOfR828_SectType[k].Value;
        localR828_SectType.Gain_X = arrayOfR828_SectType[k].Gain_X;
        localR828_SectType.Phase_Y = arrayOfR828_SectType[k].Phase_Y;
      }
      k = (byte)(k + 1);
      break;
      label534: paramArrayOfByte[0] = 1;
      paramArrayOfR828_SectType[0].Gain_X = arrayOfR828_SectType[0].Gain_X;
      paramArrayOfR828_SectType[0].Phase_Y = arrayOfR828_SectType[0].Phase_Y;
      paramArrayOfR828_SectType[0].Value = arrayOfR828_SectType[0].Value;
      paramArrayOfR828_SectType[1].Gain_X = arrayOfR828_SectType[3].Gain_X;
      paramArrayOfR828_SectType[1].Phase_Y = arrayOfR828_SectType[3].Phase_Y;
      paramArrayOfR828_SectType[1].Value = arrayOfR828_SectType[3].Value;
      paramArrayOfR828_SectType[2].Gain_X = arrayOfR828_SectType[4].Gain_X;
      paramArrayOfR828_SectType[2].Phase_Y = arrayOfR828_SectType[4].Phase_Y;
    }
  }

  boolean R828_IMR_Prepare(int paramInt)
  {
    int i = 0;
    if (i >= 27)
    {
      this.R828_I2C.RegAddr = 5;
      this.R828_Arry[0] = ((byte)(0x20 | this.R828_Arry[0]));
      this.R828_I2C.Data = this.R828_Arry[0];
      if (I2C_Write(paramInt, this.R828_I2C))
        break label77;
    }
    label77: 
    do
    {
      do
      {
        do
        {
          do
          {
            do
            {
              do
              {
                do
                {
                  do
                  {
                    do
                    {
                      return false;
                      this.R828_Arry[i] = this.R828_iniArry[i];
                      i++;
                      break;
                      this.R828_I2C.RegAddr = 7;
                      this.R828_Arry[2] = ((byte)(0xEF & this.R828_Arry[2]));
                      this.R828_I2C.Data = this.R828_Arry[2];
                    }
                    while (!I2C_Write(paramInt, this.R828_I2C));
                    this.R828_I2C.RegAddr = 10;
                    this.R828_Arry[5] = ((byte)(0xF | this.R828_Arry[5]));
                    this.R828_I2C.Data = this.R828_Arry[5];
                  }
                  while (!I2C_Write(paramInt, this.R828_I2C));
                  this.R828_I2C.RegAddr = 11;
                  this.R828_Arry[6] = ((byte)(0x60 | 0x90 & this.R828_Arry[6]));
                  this.R828_I2C.Data = this.R828_Arry[6];
                }
                while (!I2C_Write(paramInt, this.R828_I2C));
                this.R828_I2C.RegAddr = 12;
                this.R828_Arry[7] = ((byte)(0xB | 0x60 & this.R828_Arry[7]));
                this.R828_I2C.Data = this.R828_Arry[7];
              }
              while (!I2C_Write(paramInt, this.R828_I2C));
              this.R828_I2C.RegAddr = 15;
              byte[] arrayOfByte = this.R828_Arry;
              arrayOfByte[10] = ((byte)(0xF7 & arrayOfByte[10]));
              this.R828_I2C.Data = this.R828_Arry[10];
            }
            while (!I2C_Write(paramInt, this.R828_I2C));
            this.R828_I2C.RegAddr = 24;
            this.R828_Arry[19] = ((byte)(0x10 | this.R828_Arry[19]));
            this.R828_I2C.Data = this.R828_Arry[19];
          }
          while (!I2C_Write(paramInt, this.R828_I2C));
          this.R828_I2C.RegAddr = 28;
          this.R828_Arry[23] = ((byte)(0x2 | this.R828_Arry[23]));
          this.R828_I2C.Data = this.R828_Arry[23];
        }
        while (!I2C_Write(paramInt, this.R828_I2C));
        this.R828_I2C.RegAddr = 30;
        this.R828_Arry[25] = ((byte)(0x80 | this.R828_Arry[25]));
        this.R828_I2C.Data = this.R828_Arry[25];
      }
      while (!I2C_Write(paramInt, this.R828_I2C));
      this.R828_Arry[1] = ((byte)(0x20 | this.R828_Arry[1]));
      this.R828_I2C.RegAddr = 6;
      this.R828_I2C.Data = this.R828_Arry[1];
    }
    while (!I2C_Write(paramInt, this.R828_I2C));
    return true;
  }

  boolean R828_IQ(int paramInt, R828_SectType[] paramArrayOfR828_SectType)
  {
    R828_SectType[] arrayOfR828_SectType = new R828_SectType[3];
    char[] arrayOfChar = new char[1];
    byte[] arrayOfByte = new byte[1];
    arrayOfChar[0] = '\000';
    for (int i = 12; ; i = (byte)(i + 1))
    {
      if (i >= 16);
      do
      {
        arrayOfR828_SectType[0].Gain_X = (0xC0 & this.R828_iniArry[3]);
        arrayOfR828_SectType[0].Phase_Y = (0xC0 & this.R828_iniArry[4]);
        if (R828_IMR_Cross(paramInt, arrayOfR828_SectType, arrayOfByte))
          break;
        return false;
        this.R828_I2C.RegAddr = 12;
        this.R828_I2C.Data = ((byte)(i + (0xF0 & this.R828_Arry[7])));
        if (!I2C_Write(paramInt, this.R828_I2C))
          return false;
        R828_Delay_MS(paramInt, 10L);
        if (!R828_Muti_Read(paramInt, (byte)1, arrayOfChar))
          return false;
      }
      while (arrayOfChar[0] > ' ');
    }
    if (arrayOfByte[0] == 1)
    {
      if (!R828_CompreCor(arrayOfR828_SectType))
        return false;
      if (!R828_CompreStep(paramInt, arrayOfR828_SectType, (byte)8))
        return false;
    }
    else
    {
      if (!R828_CompreCor(arrayOfR828_SectType))
        return false;
      if (!R828_CompreStep(paramInt, arrayOfR828_SectType, (byte)9))
        return false;
    }
    if (arrayOfByte[0] == 1)
    {
      if (!R828_IQ_Tree(paramInt, arrayOfR828_SectType[0].Gain_X, arrayOfR828_SectType[0].Phase_Y, 8, arrayOfR828_SectType))
        return false;
      if (!R828_CompreCor(arrayOfR828_SectType))
        return false;
      if (!R828_CompreStep(paramInt, arrayOfR828_SectType, (byte)9))
        return false;
    }
    else
    {
      if (!R828_IQ_Tree(paramInt, arrayOfR828_SectType[0].Phase_Y, arrayOfR828_SectType[0].Gain_X, 9, arrayOfR828_SectType))
        return false;
      if (!R828_CompreCor(arrayOfR828_SectType))
        return false;
      if (!R828_CompreStep(paramInt, arrayOfR828_SectType, (byte)8))
        return false;
    }
    if (arrayOfByte[0] == 1)
    {
      if (!R828_IQ_Tree(paramInt, arrayOfR828_SectType[0].Phase_Y, arrayOfR828_SectType[0].Gain_X, 9, arrayOfR828_SectType))
        return false;
    }
    else if (!R828_IQ_Tree(paramInt, arrayOfR828_SectType[0].Gain_X, arrayOfR828_SectType[0].Phase_Y, 8, arrayOfR828_SectType))
      return false;
    if (!R828_CompreCor(arrayOfR828_SectType))
      return false;
    if (!R828_Section(paramInt, arrayOfR828_SectType))
      return false;
    paramArrayOfR828_SectType[0] = arrayOfR828_SectType[0];
    this.R828_I2C.RegAddr = 8;
    this.R828_I2C.Data = ((byte)(0xC0 & this.R828_iniArry[3]));
    if (!I2C_Write(paramInt, this.R828_I2C))
      return false;
    this.R828_I2C.RegAddr = 9;
    this.R828_I2C.Data = ((byte)(0xC0 & this.R828_iniArry[4]));
    return I2C_Write(paramInt, this.R828_I2C);
  }

  boolean R828_IQ_Tree(int paramInt1, int paramInt2, int paramInt3, int paramInt4, R828_SectType[] paramArrayOfR828_SectType)
  {
    byte b;
    int i;
    boolean bool2;
    if (paramInt4 == 8)
    {
      b = 9;
      i = 0;
      if (i < 3)
        break label33;
      bool2 = true;
    }
    label33: char[] arrayOfChar;
    boolean bool4;
    do
    {
      boolean bool3;
      do
      {
        boolean bool1;
        do
        {
          return bool2;
          b = 8;
          break;
          this.R828_I2C.RegAddr = ((byte)paramInt4);
          this.R828_I2C.Data = ((byte)paramInt2);
          bool1 = I2C_Write(paramInt1, this.R828_I2C);
          bool2 = false;
        }
        while (!bool1);
        this.R828_I2C.RegAddr = b;
        this.R828_I2C.Data = ((byte)paramInt3);
        bool3 = I2C_Write(paramInt1, this.R828_I2C);
        bool2 = false;
      }
      while (!bool3);
      arrayOfChar = new char[1];
      bool4 = R828_Muti_Read(paramInt1, (byte)1, arrayOfChar);
      bool2 = false;
    }
    while (!bool4);
    paramArrayOfR828_SectType[i].Value = arrayOfChar[0];
    if (paramInt4 == 8)
    {
      paramArrayOfR828_SectType[i].Gain_X = paramInt2;
      paramArrayOfR828_SectType[i].Phase_Y = paramInt3;
      label168: if (i != 0)
        break label207;
      paramInt3++;
    }
    while (true)
    {
      i = (byte)(i + 1);
      break;
      paramArrayOfR828_SectType[i].Phase_Y = paramInt2;
      paramArrayOfR828_SectType[i].Gain_X = paramInt3;
      break label168;
      label207: if (i == 1)
        if ((paramInt3 & 0x1F) < 2)
        {
          int j = (byte)(2 - (paramInt3 & 0x1F));
          if ((paramInt3 & 0x20) != 0)
            paramInt3 = j | paramInt3 & 0xC0;
          else
            paramInt3 |= j | 0x20;
        }
        else
        {
          paramInt3 -= 2;
        }
    }
  }

  boolean R828_Init(int paramInt)
  {
    if (!R828_InitReg(paramInt))
      return false;
    if (!this.R828_IMR_done_flag)
    {
      if ((this.Rafael_Chip != 3) && (this.Rafael_Chip != 2) && (this.Rafael_Chip != 4))
        break label64;
      this.Xtal_cap_sel = 4;
    }
    label46: for (int i = 0; ; i++)
    {
      if (i >= 23)
      {
        if (!R828_InitReg(paramInt))
          break;
        return true;
        label64: if (!R828_Xtal_Check(paramInt))
          break;
        this.Xtal_cap_sel = this.Xtal_cap_sel_tmp;
        if (!R828_Xtal_Check(paramInt))
          break;
        if (this.Xtal_cap_sel_tmp > this.Xtal_cap_sel)
          this.Xtal_cap_sel = this.Xtal_cap_sel_tmp;
        if (!R828_Xtal_Check(paramInt))
          break;
        if (this.Xtal_cap_sel_tmp <= this.Xtal_cap_sel)
          break label46;
        this.Xtal_cap_sel = this.Xtal_cap_sel_tmp;
        break label46;
      }
      this.R828_Fil_Cal_flag[i] = 0;
      this.R828_Fil_Cal_code[i] = 0;
    }
  }

  boolean R828_InitReg(int paramInt)
  {
    this.R828_I2C_Len.RegAddr = 5;
    this.R828_I2C_Len.Len = 27;
    for (int i = 0; ; i++)
    {
      if (i >= 27)
      {
        if (I2C_Write_Len(paramInt, this.R828_I2C_Len))
          break;
        return false;
      }
      this.R828_I2C_Len.Data[i] = this.R828_iniArry[i];
    }
    return true;
  }

  boolean R828_MUX(int paramInt1, int paramInt2)
  {
    Freq_Info1 = R828_Freq_Sel(paramInt2);
    this.R828_I2C.RegAddr = 23;
    this.R828_Arry[18] = ((byte)(0xF7 & this.R828_Arry[18] | Freq_Info1.OPEN_D));
    this.R828_I2C.Data = this.R828_Arry[18];
    if (!I2C_Write(paramInt1, this.R828_I2C));
    label301: 
    do
    {
      do
      {
        do
        {
          return false;
          this.R828_I2C.RegAddr = 26;
          this.R828_Arry[21] = ((byte)(0x3C & this.R828_Arry[21] | Freq_Info1.RF_MUX_PLOY));
          this.R828_I2C.Data = this.R828_Arry[21];
        }
        while (!I2C_Write(paramInt1, this.R828_I2C));
        this.R828_I2C.RegAddr = 27;
        byte[] arrayOfByte1 = this.R828_Arry;
        arrayOfByte1[22] = ((byte)(0x0 & arrayOfByte1[22]));
        byte[] arrayOfByte2 = this.R828_Arry;
        arrayOfByte2[22] = ((byte)(arrayOfByte2[22] | Freq_Info1.TF_C));
        this.R828_I2C.Data = this.R828_Arry[22];
      }
      while (!I2C_Write(paramInt1, this.R828_I2C));
      this.R828_I2C.RegAddr = 16;
      byte[] arrayOfByte3 = this.R828_Arry;
      arrayOfByte3[11] = ((byte)(0xF4 & arrayOfByte3[11]));
      switch (this.Xtal_cap_sel)
      {
      default:
        this.R828_Arry[11] = ((byte)(0x8 | (this.R828_Arry[11] | Freq_Info1.XTAL_CAP0P)));
        this.R828_I2C.Data = this.R828_Arry[11];
      case 0:
      case 1:
      case 2:
      case 3:
      case 4:
      }
    }
    while (!I2C_Write(paramInt1, this.R828_I2C));
    int i;
    if (this.R828_IMR_done_flag)
      i = 0x3F & this.IMR_Data[Freq_Info1.IMR_MEM].Gain_X;
    for (int j = 0x3F & this.IMR_Data[Freq_Info1.IMR_MEM].Phase_Y; ; j = 0)
    {
      this.R828_I2C.RegAddr = 8;
      this.R828_Arry[3] = ((byte)(0xC0 & this.R828_iniArry[3]));
      this.R828_Arry[3] = ((byte)(i | this.R828_Arry[3]));
      this.R828_I2C.Data = this.R828_Arry[3];
      if (!I2C_Write(paramInt1, this.R828_I2C))
        break;
      this.R828_I2C.RegAddr = 9;
      this.R828_Arry[4] = ((byte)(0xC0 & this.R828_iniArry[4]));
      this.R828_Arry[4] = ((byte)(j | this.R828_Arry[4]));
      this.R828_I2C.Data = this.R828_Arry[4];
      if (!I2C_Write(paramInt1, this.R828_I2C))
        break;
      return true;
      this.R828_Arry[11] = ((byte)(0x8 | (this.R828_Arry[11] | Freq_Info1.XTAL_CAP20P)));
      break label301;
      this.R828_Arry[11] = ((byte)(0x8 | (this.R828_Arry[11] | Freq_Info1.XTAL_CAP10P)));
      break label301;
      this.R828_Arry[11] = ((byte)(0x8 | (this.R828_Arry[11] | Freq_Info1.XTAL_CAP0P)));
      break label301;
      this.R828_Arry[11] = ((byte)(this.R828_Arry[11] | Freq_Info1.XTAL_CAP0P));
      break label301;
      i = 0;
    }
  }

  boolean R828_Muti_Read(int paramInt, byte paramByte, char[] paramArrayOfChar)
  {
    int i = 0;
    int j = 0;
    int k = -1;
    R828_Delay_MS(paramInt, 5L);
    for (int m = 0; ; m = (byte)(m + 1))
    {
      boolean bool2;
      if (m >= 6)
      {
        paramArrayOfChar[0] = ((char)(i - j - k));
        bool2 = true;
      }
      boolean bool1;
      do
      {
        return bool2;
        this.R828_I2C_Len.RegAddr = 0;
        this.R828_I2C_Len.Len = ((byte)(paramByte + 1));
        bool1 = I2C_Read_Len(paramInt, this.R828_I2C_Len);
        bool2 = false;
      }
      while (!bool1);
      int n = this.R828_I2C_Len.Data[1];
      i = (char)(i + n);
      if (n < k)
        k = n;
      if (n > j)
        j = n;
    }
  }

  boolean R828_PLL(int paramInt1, int paramInt2, int paramInt3)
  {
    int i = 2;
    int j = 0;
    int k = 1770000 * 2;
    int m = 2;
    byte[] arrayOfByte1 = this.R828_Arry;
    arrayOfByte1[11] = ((byte)(0xEF & arrayOfByte1[11]));
    int n = SdrUSBDriver.rtlsdr_get_tuner_clock();
    this.R828_I2C.RegAddr = 16;
    this.R828_I2C.Data = this.R828_Arry[11];
    if (!I2C_Write(paramInt1, this.R828_I2C))
      return false;
    this.R828_I2C.RegAddr = 26;
    this.R828_Arry[21] = ((byte)(0xF3 & this.R828_Arry[21]));
    this.R828_I2C.Data = this.R828_Arry[21];
    if (!I2C_Write(paramInt1, this.R828_I2C))
      return false;
    this.R828_I2C.RegAddr = 18;
    this.R828_Arry[13] = ((byte)(0x80 | 0x1F & this.R828_Arry[13]));
    this.R828_I2C.Data = this.R828_Arry[13];
    int i11;
    if (!I2C_Write(paramInt1, this.R828_I2C))
    {
      return false;
      if ((i * (paramInt2 / 1000) >= 1770000) && (i * (paramInt2 / 1000) < k))
      {
        i11 = i;
        label225: if (i11 > 2);
      }
    }
    while (true)
    {
      this.R828_I2C_Len.RegAddr = 0;
      this.R828_I2C_Len.Len = 5;
      if (I2C_Read_Len(paramInt1, this.R828_I2C_Len))
        break label292;
      return false;
      i11 >>= 1;
      j++;
      break label225;
      i <<= 1;
      if (i <= 64)
        break;
      j = 0;
    }
    label292: int i1 = (0x30 & this.R828_I2C_Len.Data[4]) >> 4;
    if (i1 > 2)
      j--;
    while (true)
    {
      this.R828_I2C.RegAddr = 16;
      byte[] arrayOfByte2 = this.R828_Arry;
      arrayOfByte2[11] = ((byte)(0x1F & arrayOfByte2[11]));
      byte[] arrayOfByte3 = this.R828_Arry;
      arrayOfByte3[11] = ((byte)(arrayOfByte3[11] | j << 5));
      this.R828_I2C.Data = this.R828_Arry[11];
      if (I2C_Write(paramInt1, this.R828_I2C))
        break;
      return false;
      if (i1 < 2)
        j++;
    }
    long l = paramInt2 * i;
    int i2 = (int)(l / 2L / n);
    int i3 = (char)(int)((l - i2 * (n * 2)) / 1000L);
    int i4 = n / 1000;
    Log.d(TAG, "VCO_Freq " + Long.toString(l) + " Nint " + Long.toString(i2) + " VCO_Fra " + Long.toString(i3) + " LO_Freq " + Long.toString(paramInt2) + " MixDiv " + Long.toString(i));
    int i5;
    if (i3 < i4 / 64)
      i5 = 0;
    while (i2 > 63)
    {
      String str = TAG;
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = Integer.valueOf(paramInt2);
      Log.d(str, String.format("[R820T] No valid PLL values for %u Hz! ", arrayOfObject));
      return false;
      if (i3 > i4 * 127 / 64)
      {
        i2++;
        i5 = 0;
      }
      else if ((i3 > i4 * 127 / 128) && (i3 < i4))
      {
        i5 = i4 * 127 / 128;
      }
      else if ((i3 > i4) && (i3 < i4 * 129 / 128))
      {
        i5 = i4 * 129 / 128;
      }
      else
      {
        i5 = i3 + 0;
      }
    }
    int i6 = (i2 - 13) / 4;
    int i7 = -13 + (i2 - i6 * 4);
    this.R828_I2C.RegAddr = 20;
    this.R828_Arry[15] = 0;
    byte[] arrayOfByte4 = this.R828_Arry;
    arrayOfByte4[15] = ((byte)(arrayOfByte4[15] | i6 + (i7 << 6)));
    this.R828_I2C.Data = this.R828_Arry[15];
    if (!I2C_Write(paramInt1, this.R828_I2C))
      return false;
    this.R828_I2C.RegAddr = 18;
    byte[] arrayOfByte5 = this.R828_Arry;
    arrayOfByte5[13] = ((byte)(0xF7 & arrayOfByte5[13]));
    if (i5 == 0)
    {
      byte[] arrayOfByte6 = this.R828_Arry;
      arrayOfByte6[13] = ((byte)(0x8 | arrayOfByte6[13]));
    }
    this.R828_I2C.Data = this.R828_Arry[13];
    boolean bool = I2C_Write(paramInt1, this.R828_I2C);
    int i8 = 0;
    if (!bool)
    {
      return false;
      if (i5 > i4 * 2 / m)
      {
        i8 = (char)(i8 + 32768 / (m / 2));
        i5 -= i4 * 2 / m;
        if (m < 32768);
      }
    }
    int i10;
    while (true)
    {
      int i9 = (char)(i8 >> 8);
      i10 = (char)(i8 - (i9 << 8));
      this.R828_I2C.RegAddr = 22;
      this.R828_Arry[17] = ((byte)i9);
      this.R828_I2C.Data = this.R828_Arry[17];
      if (I2C_Write(paramInt1, this.R828_I2C))
        break label1011;
      return false;
      m = (char)(m << 1);
      if (i5 > 1)
        break;
    }
    label1011: this.R828_I2C.RegAddr = 21;
    this.R828_Arry[16] = ((byte)i10);
    this.R828_I2C.Data = this.R828_Arry[16];
    if (!I2C_Write(paramInt1, this.R828_I2C))
      return false;
    if ((this.Rafael_Chip == 5) || (this.Rafael_Chip == 1) || (this.Rafael_Chip == 0))
      if (paramInt3 <= 7)
        R828_Delay_MS(paramInt1, 20L);
    while (true)
    {
      this.R828_I2C_Len.RegAddr = 0;
      this.R828_I2C_Len.Len = 3;
      if (I2C_Read_Len(paramInt1, this.R828_I2C_Len))
        break;
      return false;
      R828_Delay_MS(paramInt1, 10L);
      continue;
      R828_Delay_MS(paramInt1, 10L);
    }
    if ((0x40 & this.R828_I2C_Len.Data[2]) == 0)
    {
      Log.d(TAG, "[R820T] PLL not locked for %u Hz! " + paramInt2);
      this.R828_I2C.RegAddr = 18;
      this.R828_Arry[13] = ((byte)(0x60 | 0x1F & this.R828_Arry[13]));
      this.R828_I2C.Data = this.R828_Arry[13];
      return I2C_Write(paramInt1, this.R828_I2C);
    }
    this.R828_I2C.RegAddr = 26;
    this.R828_Arry[21] = ((byte)(0x8 | this.R828_Arry[21]));
    this.R828_I2C.Data = this.R828_Arry[21];
    return I2C_Write(paramInt1, this.R828_I2C);
  }

  boolean R828_RfGainMode(int paramInt, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.R828_I2C.RegAddr = 5;
      this.R828_Arry[0] = ((byte)(0x10 | this.R828_Arry[0]));
      this.R828_I2C.Data = this.R828_Arry[0];
      if (!I2C_Write(paramInt, this.R828_I2C));
      do
      {
        do
        {
          do
          {
            return false;
            this.R828_I2C.RegAddr = 7;
            this.R828_Arry[2] = ((byte)(0xEF & this.R828_Arry[2]));
            this.R828_I2C.Data = this.R828_Arry[2];
          }
          while (!I2C_Write(paramInt, this.R828_I2C));
          this.R828_I2C_Len.RegAddr = 0;
          this.R828_I2C_Len.Len = 4;
        }
        while (!I2C_Read_Len(paramInt, this.R828_I2C_Len));
        this.R828_I2C.RegAddr = 12;
        this.R828_Arry[7] = ((byte)(0x8 | 0x60 & this.R828_Arry[7]));
        this.R828_I2C.Data = this.R828_Arry[7];
      }
      while (!I2C_Write(paramInt, this.R828_I2C));
      Log.d(TAG, "set manual gain mode");
    }
    while (true)
    {
      return true;
      this.R828_I2C.RegAddr = 5;
      this.R828_Arry[0] = ((byte)(0xEF & this.R828_Arry[0]));
      this.R828_I2C.Data = this.R828_Arry[0];
      if (!I2C_Write(paramInt, this.R828_I2C))
        break;
      this.R828_I2C.RegAddr = 7;
      this.R828_Arry[2] = ((byte)(0x10 | this.R828_Arry[2]));
      this.R828_I2C.Data = this.R828_Arry[2];
      if (!I2C_Write(paramInt, this.R828_I2C))
        break;
      this.R828_I2C.RegAddr = 12;
      this.R828_Arry[7] = ((byte)(0xB | 0x60 & this.R828_Arry[7]));
      this.R828_I2C.Data = this.R828_Arry[7];
      if (!I2C_Write(paramInt, this.R828_I2C))
        break;
      Log.d(TAG, "set auto gain mode");
    }
  }

  boolean R828_Section(int paramInt, R828_SectType[] paramArrayOfR828_SectType)
  {
    R828_SectType[] arrayOfR828_SectType1 = new R828_SectType[3];
    R828_SectType[] arrayOfR828_SectType2 = new R828_SectType[3];
    if ((0x1F & paramArrayOfR828_SectType[0].Gain_X) == 0);
    for (arrayOfR828_SectType1[0].Gain_X = (1 + (0xDF & paramArrayOfR828_SectType[0].Gain_X)); ; arrayOfR828_SectType1[0].Gain_X = (-1 + paramArrayOfR828_SectType[0].Gain_X))
    {
      arrayOfR828_SectType1[0].Phase_Y = paramArrayOfR828_SectType[0].Phase_Y;
      if (R828_IQ_Tree(paramInt, arrayOfR828_SectType1[0].Gain_X, arrayOfR828_SectType1[0].Phase_Y, 8, arrayOfR828_SectType1))
        break;
      return false;
    }
    if (!R828_CompreCor(arrayOfR828_SectType1))
      return false;
    arrayOfR828_SectType2[0].Gain_X = arrayOfR828_SectType1[0].Gain_X;
    arrayOfR828_SectType2[0].Phase_Y = arrayOfR828_SectType1[0].Phase_Y;
    arrayOfR828_SectType2[0].Value = arrayOfR828_SectType1[0].Value;
    arrayOfR828_SectType1[0].Gain_X = paramArrayOfR828_SectType[0].Gain_X;
    arrayOfR828_SectType1[0].Phase_Y = paramArrayOfR828_SectType[0].Phase_Y;
    if (!R828_IQ_Tree(paramInt, arrayOfR828_SectType1[0].Gain_X, arrayOfR828_SectType1[0].Phase_Y, 8, arrayOfR828_SectType1))
      return false;
    if (!R828_CompreCor(arrayOfR828_SectType1))
      return false;
    arrayOfR828_SectType2[1].Gain_X = arrayOfR828_SectType1[0].Gain_X;
    arrayOfR828_SectType2[1].Phase_Y = arrayOfR828_SectType1[0].Phase_Y;
    arrayOfR828_SectType2[1].Value = arrayOfR828_SectType1[0].Value;
    if ((0x1F & paramArrayOfR828_SectType[0].Gain_X) == 0);
    for (arrayOfR828_SectType1[0].Gain_X = (1 + (0x20 | paramArrayOfR828_SectType[0].Gain_X)); ; arrayOfR828_SectType1[0].Gain_X = (1 + paramArrayOfR828_SectType[0].Gain_X))
    {
      arrayOfR828_SectType1[0].Phase_Y = paramArrayOfR828_SectType[0].Phase_Y;
      if (R828_IQ_Tree(paramInt, arrayOfR828_SectType1[0].Gain_X, arrayOfR828_SectType1[0].Phase_Y, 8, arrayOfR828_SectType1))
        break;
      return false;
    }
    if (!R828_CompreCor(arrayOfR828_SectType1))
      return false;
    arrayOfR828_SectType2[2].Gain_X = arrayOfR828_SectType1[0].Gain_X;
    arrayOfR828_SectType2[2].Phase_Y = arrayOfR828_SectType1[0].Phase_Y;
    arrayOfR828_SectType2[2].Value = arrayOfR828_SectType1[0].Value;
    if (!R828_CompreCor(arrayOfR828_SectType2))
      return false;
    paramArrayOfR828_SectType[0] = arrayOfR828_SectType2[0];
    return true;
  }

  boolean R828_SetFrequency(int paramInt, R828_Set_Info paramR828_Set_Info, boolean paramBoolean)
  {
    Sys_Info1 = R828_Sys_Sel(8);
    int i;
    if (paramR828_Set_Info.R828_Standard == 7)
    {
      i = (int)(paramR828_Set_Info.RF_Hz - 'Ϩ' * Sys_Info1.IF_KHz);
      if (R828_MUX(paramInt, i / 1000))
        break label75;
    }
    label75: 
    do
    {
      do
      {
        do
        {
          do
          {
            do
            {
              do
              {
                do
                {
                  do
                  {
                    do
                    {
                      do
                      {
                        do
                        {
                          do
                          {
                            do
                            {
                              do
                              {
                                do
                                {
                                  do
                                  {
                                    return false;
                                    i = (int)(paramR828_Set_Info.RF_Hz + 'Ϩ' * Sys_Info1.IF_KHz);
                                    break;
                                  }
                                  while (!R828_PLL(paramInt, i, paramR828_Set_Info.R828_Standard));
                                  this.R828_IMR_point_num = Freq_Info1.IMR_MEM;
                                  SysFreq_Info1 = R828_SysFreq_Sel(paramR828_Set_Info.R828_Standard, (int)paramR828_Set_Info.RF_KHz);
                                  this.R828_Arry[24] = ((byte)(0x38 & this.R828_Arry[24] | 0xC7 & SysFreq_Info1.LNA_TOP));
                                  this.R828_I2C.RegAddr = 29;
                                  this.R828_I2C.Data = this.R828_Arry[24];
                                }
                                while (!I2C_Write(paramInt, this.R828_I2C));
                                this.R828_Arry[23] = ((byte)(0x7 & this.R828_Arry[23] | 0xF8 & SysFreq_Info1.MIXER_TOP));
                                this.R828_I2C.RegAddr = 28;
                                this.R828_I2C.Data = this.R828_Arry[23];
                              }
                              while (!I2C_Write(paramInt, this.R828_I2C));
                              byte[] arrayOfByte1 = this.R828_Arry;
                              this.R828_Arry[8];
                              arrayOfByte1[8] = ((byte)(0x0 | SysFreq_Info1.LNA_VTH_L));
                              this.R828_I2C.RegAddr = 13;
                              this.R828_I2C.Data = this.R828_Arry[8];
                            }
                            while (!I2C_Write(paramInt, this.R828_I2C));
                            byte[] arrayOfByte2 = this.R828_Arry;
                            this.R828_Arry[9];
                            arrayOfByte2[9] = ((byte)(0x0 | SysFreq_Info1.MIXER_VTH_L));
                            this.R828_I2C.RegAddr = 14;
                            this.R828_I2C.Data = this.R828_Arry[9];
                          }
                          while (!I2C_Write(paramInt, this.R828_I2C));
                          this.R828_I2C.RegAddr = 5;
                          byte[] arrayOfByte3 = this.R828_Arry;
                          arrayOfByte3[0] = ((byte)(0x9F & arrayOfByte3[0]));
                          byte[] arrayOfByte4 = this.R828_Arry;
                          arrayOfByte4[0] = ((byte)(arrayOfByte4[0] | SysFreq_Info1.AIR_CABLE1_IN));
                          this.R828_I2C.Data = this.R828_Arry[0];
                        }
                        while (!I2C_Write(paramInt, this.R828_I2C));
                        this.R828_I2C.RegAddr = 6;
                        byte[] arrayOfByte5 = this.R828_Arry;
                        arrayOfByte5[1] = ((byte)(0xF7 & arrayOfByte5[1]));
                        byte[] arrayOfByte6 = this.R828_Arry;
                        arrayOfByte6[1] = ((byte)(arrayOfByte6[1] | SysFreq_Info1.CABLE2_IN));
                        this.R828_I2C.Data = this.R828_Arry[1];
                      }
                      while (!I2C_Write(paramInt, this.R828_I2C));
                      this.R828_I2C.RegAddr = 17;
                      byte[] arrayOfByte7 = this.R828_Arry;
                      arrayOfByte7[12] = ((byte)(0xC7 & arrayOfByte7[12]));
                      byte[] arrayOfByte8 = this.R828_Arry;
                      arrayOfByte8[12] = ((byte)(arrayOfByte8[12] | SysFreq_Info1.CP_CUR));
                      this.R828_I2C.Data = this.R828_Arry[12];
                    }
                    while (!I2C_Write(paramInt, this.R828_I2C));
                    this.R828_I2C.RegAddr = 23;
                    byte[] arrayOfByte9 = this.R828_Arry;
                    arrayOfByte9[18] = ((byte)(0xCF & arrayOfByte9[18]));
                    byte[] arrayOfByte10 = this.R828_Arry;
                    arrayOfByte10[18] = ((byte)(arrayOfByte10[18] | SysFreq_Info1.DIV_BUF_CUR));
                    this.R828_I2C.Data = this.R828_Arry[18];
                  }
                  while (!I2C_Write(paramInt, this.R828_I2C));
                  this.R828_I2C.RegAddr = 10;
                  this.R828_Arry[5] = ((byte)(0x9F & this.R828_Arry[5] | SysFreq_Info1.FILTER_CUR));
                  this.R828_I2C.Data = this.R828_Arry[5];
                }
                while (!I2C_Write(paramInt, this.R828_I2C));
                this.R828_Arry[0] = ((byte)(0x9F & this.R828_Arry[0]));
                this.R828_Arry[1] = ((byte)(0xF7 & this.R828_Arry[1]));
                this.R828_I2C.RegAddr = 5;
                this.R828_I2C.Data = this.R828_Arry[0];
              }
              while (!I2C_Write(paramInt, this.R828_I2C));
              this.R828_I2C.RegAddr = 6;
              this.R828_I2C.Data = this.R828_Arry[1];
            }
            while (!I2C_Write(paramInt, this.R828_I2C));
            if (paramR828_Set_Info.R828_Standard <= 7)
              break label1532;
            if (!paramBoolean)
              break label1068;
            this.R828_Arry[24] = ((byte)(0xC7 & this.R828_Arry[24]));
            this.R828_I2C.RegAddr = 29;
            this.R828_I2C.Data = this.R828_Arry[24];
          }
          while (!I2C_Write(paramInt, this.R828_I2C));
          this.R828_Arry[23] = ((byte)(0xFB & this.R828_Arry[23]));
          this.R828_I2C.RegAddr = 28;
          this.R828_I2C.Data = this.R828_Arry[23];
        }
        while (!I2C_Write(paramInt, this.R828_I2C));
        this.R828_Arry[1] = ((byte)(0xBF & this.R828_Arry[1]));
        this.R828_I2C.RegAddr = 6;
        this.R828_I2C.Data = this.R828_Arry[1];
      }
      while (!I2C_Write(paramInt, this.R828_I2C));
      this.R828_Arry[21] = ((byte)(0x30 | 0xCF & this.R828_Arry[21]));
      this.R828_I2C.RegAddr = 26;
      this.R828_I2C.Data = this.R828_Arry[21];
    }
    while (!I2C_Write(paramInt, this.R828_I2C));
    label1068: label1532: 
    do
    {
      do
      {
        do
        {
          return true;
          this.R828_Arry[24] = ((byte)(0xC7 & this.R828_Arry[24]));
          this.R828_I2C.RegAddr = 29;
          this.R828_I2C.Data = this.R828_Arry[24];
          if (!I2C_Write(paramInt, this.R828_I2C))
            break;
          this.R828_Arry[23] = ((byte)(0xFB & this.R828_Arry[23]));
          this.R828_I2C.RegAddr = 28;
          this.R828_I2C.Data = this.R828_Arry[23];
          if (!I2C_Write(paramInt, this.R828_I2C))
            break;
          this.R828_Arry[1] = ((byte)(0xBF & this.R828_Arry[1]));
          this.R828_I2C.RegAddr = 6;
          this.R828_I2C.Data = this.R828_Arry[1];
          if (!I2C_Write(paramInt, this.R828_I2C))
            break;
          this.R828_Arry[21] = ((byte)(0x30 | 0xCF & this.R828_Arry[21]));
          this.R828_I2C.RegAddr = 26;
          this.R828_I2C.Data = this.R828_Arry[21];
          if (!I2C_Write(paramInt, this.R828_I2C))
            break;
          R828_Delay_MS(paramInt, 250L);
          this.R828_Arry[24] = ((byte)(0x18 | 0xC7 & this.R828_Arry[24]));
          this.R828_I2C.RegAddr = 29;
          this.R828_I2C.Data = this.R828_Arry[24];
          if (!I2C_Write(paramInt, this.R828_I2C))
            break;
          this.R828_Arry[23] = ((byte)(0xFB & this.R828_Arry[23] | 0x4 & SysFreq_Info1.MIXER_TOP));
          this.R828_I2C.RegAddr = 28;
          this.R828_I2C.Data = this.R828_Arry[23];
          if (!I2C_Write(paramInt, this.R828_I2C))
            break;
          this.R828_Arry[25] = ((byte)(0xE0 & this.R828_Arry[25] | SysFreq_Info1.LNA_DISbyteGE));
          this.R828_I2C.RegAddr = 30;
          this.R828_I2C.Data = this.R828_Arry[25];
          if (!I2C_Write(paramInt, this.R828_I2C))
            break;
          this.R828_Arry[21] = ((byte)(0x20 | 0xCF & this.R828_Arry[21]));
          this.R828_I2C.RegAddr = 26;
          this.R828_I2C.Data = this.R828_Arry[21];
        }
        while (I2C_Write(paramInt, this.R828_I2C));
        return false;
      }
      while ((paramBoolean) && (!paramBoolean));
      this.R828_Arry[1] = ((byte)(0xBF & this.R828_Arry[1]));
      this.R828_I2C.RegAddr = 6;
      this.R828_I2C.Data = this.R828_Arry[1];
      if (!I2C_Write(paramInt, this.R828_I2C))
        break;
      this.R828_Arry[24] = ((byte)(0xC7 & this.R828_Arry[24] | 0x38 & SysFreq_Info1.LNA_TOP));
      this.R828_I2C.RegAddr = 29;
      this.R828_I2C.Data = this.R828_Arry[24];
      if (!I2C_Write(paramInt, this.R828_I2C))
        break;
      this.R828_Arry[23] = ((byte)(0xFB & this.R828_Arry[23] | 0x4 & SysFreq_Info1.MIXER_TOP));
      this.R828_I2C.RegAddr = 28;
      this.R828_I2C.Data = this.R828_Arry[23];
      if (!I2C_Write(paramInt, this.R828_I2C))
        break;
      this.R828_Arry[25] = ((byte)(0xE0 & this.R828_Arry[25] | SysFreq_Info1.LNA_DISbyteGE));
      this.R828_I2C.RegAddr = 30;
      this.R828_I2C.Data = this.R828_Arry[25];
      if (!I2C_Write(paramInt, this.R828_I2C))
        break;
      this.R828_Arry[21] = ((byte)(0xCF & this.R828_Arry[21]));
      this.R828_I2C.RegAddr = 26;
      this.R828_I2C.Data = this.R828_Arry[21];
      if (!I2C_Write(paramInt, this.R828_I2C))
        break;
      this.R828_Arry[11] = ((byte)(0xFB & this.R828_Arry[11]));
      this.R828_I2C.RegAddr = 16;
      this.R828_I2C.Data = this.R828_Arry[11];
    }
    while (I2C_Write(paramInt, this.R828_I2C));
    return false;
  }

  boolean R828_SetRfGain(int paramInt1, int paramInt2)
  {
    int i = 0;
    int j = 0;
    int k = 0;
    int m = 0;
    if (m >= 15)
    {
      label18: this.R828_I2C.RegAddr = 5;
      this.R828_Arry[0] = ((byte)(k | 0xF0 & this.R828_Arry[0]));
      this.R828_I2C.Data = this.R828_Arry[0];
      if (I2C_Write(paramInt1, this.R828_I2C))
        break label132;
    }
    label132: 
    do
    {
      return false;
      if (i >= paramInt2)
        break label18;
      int[] arrayOfInt1 = r820t_lna_gain_steps;
      k = (byte)(k + 1);
      int n = i + arrayOfInt1[k];
      if (n >= paramInt2)
        break label18;
      int[] arrayOfInt2 = r820t_mixer_gain_steps;
      j = (byte)(j + 1);
      i = n + arrayOfInt2[j];
      m++;
      break;
      this.R828_I2C.RegAddr = 7;
      this.R828_Arry[2] = ((byte)(j | 0xF0 & this.R828_Arry[2]));
      this.R828_I2C.Data = this.R828_Arry[2];
    }
    while (!I2C_Write(paramInt1, this.R828_I2C));
    return true;
  }

  boolean R828_SetStandard(int paramInt1, int paramInt2)
  {
    int i = 0;
    if (i >= 27)
    {
      if (!this.R828_IMR_done_flag)
        break label100;
      this.R828_Arry[7] = ((byte)(0x1 | 0xF0 & this.R828_Arry[7] | this.Xtal_cap_sel << 1));
      label43: this.R828_I2C.RegAddr = 12;
      this.R828_I2C.Data = this.R828_Arry[7];
      if (I2C_Write(paramInt1, this.R828_I2C))
        break label122;
    }
    label100: label122: 
    do
    {
      do
      {
        do
        {
          do
          {
            do
            {
              do
              {
                do
                {
                  do
                  {
                    do
                    {
                      do
                      {
                        do
                        {
                          do
                          {
                            do
                            {
                              do
                              {
                                do
                                {
                                  return false;
                                  this.R828_Arry[i] = this.R828_iniArry[i];
                                  i = (byte)(i + 1);
                                  break;
                                  this.R828_Arry[7] = ((byte)(0xF0 & this.R828_Arry[7]));
                                  break label43;
                                  this.R828_I2C.RegAddr = 19;
                                  this.R828_Arry[14] = ((byte)(0x31 | 0xC0 & this.R828_Arry[14]));
                                  this.R828_I2C.Data = this.R828_Arry[14];
                                }
                                while (!I2C_Write(paramInt1, this.R828_I2C));
                                if (paramInt2 <= 7)
                                  break label225;
                                this.R828_I2C.RegAddr = 29;
                                this.R828_I2C.Data = ((byte)(0xC7 & this.R828_Arry[24]));
                              }
                              while (!I2C_Write(paramInt1, this.R828_I2C));
                              Sys_Info1 = R828_Sys_Sel(paramInt2);
                              this.R828_IF_khz = Sys_Info1.IF_KHz;
                              this.R828_CAL_LO_khz = Sys_Info1.FILT_CAL_LO;
                              if (this.R828_Fil_Cal_flag[paramInt2] != 0)
                                break label441;
                            }
                            while (!R828_Filt_Cal(paramInt1, Sys_Info1.FILT_CAL_LO, Sys_Info1.BW));
                            this.R828_I2C_Len.RegAddr = 0;
                            this.R828_I2C_Len.Len = 5;
                          }
                          while (!I2C_Read_Len(paramInt1, this.R828_I2C_Len));
                          this.R828_Fil_Cal_code[paramInt2] = ((byte)(0xF & this.R828_I2C_Len.Data[4]));
                          if ((this.R828_Fil_Cal_code[paramInt2] != 0) && (this.R828_Fil_Cal_code[paramInt2] != 15))
                            break label434;
                        }
                        while (!R828_Filt_Cal(paramInt1, Sys_Info1.FILT_CAL_LO, Sys_Info1.BW));
                        this.R828_I2C_Len.RegAddr = 0;
                        this.R828_I2C_Len.Len = 5;
                      }
                      while (!I2C_Read_Len(paramInt1, this.R828_I2C_Len));
                      this.R828_Fil_Cal_code[paramInt2] = ((byte)(0xF & this.R828_I2C_Len.Data[4]));
                      if (this.R828_Fil_Cal_code[paramInt2] == 15)
                        this.R828_Fil_Cal_code[paramInt2] = 0;
                      this.R828_Fil_Cal_flag[paramInt2] = 1;
                      this.R828_Arry[5] = ((byte)(0xE0 & this.R828_Arry[5] | Sys_Info1.FILT_Q | this.R828_Fil_Cal_code[paramInt2]));
                      this.R828_I2C.RegAddr = 10;
                      this.R828_I2C.Data = this.R828_Arry[5];
                    }
                    while (!I2C_Write(paramInt1, this.R828_I2C));
                    this.R828_Arry[6] = ((byte)(0x10 & this.R828_Arry[6] | Sys_Info1.HP_COR));
                    this.R828_I2C.RegAddr = 11;
                    this.R828_I2C.Data = this.R828_Arry[6];
                  }
                  while (!I2C_Write(paramInt1, this.R828_I2C));
                  this.R828_Arry[2] = ((byte)(0x7F & this.R828_Arry[2] | Sys_Info1.IMG_R));
                  this.R828_I2C.RegAddr = 7;
                  this.R828_I2C.Data = this.R828_Arry[2];
                }
                while (!I2C_Write(paramInt1, this.R828_I2C));
                this.R828_Arry[1] = ((byte)(0xCF & this.R828_Arry[1] | Sys_Info1.FILT_GAIN));
                this.R828_I2C.RegAddr = 6;
                this.R828_I2C.Data = this.R828_Arry[1];
              }
              while (!I2C_Write(paramInt1, this.R828_I2C));
              this.R828_Arry[25] = ((byte)(0x9F & this.R828_Arry[25] | Sys_Info1.EXT_ENABLE));
              this.R828_I2C.RegAddr = 30;
              this.R828_I2C.Data = this.R828_Arry[25];
            }
            while (!I2C_Write(paramInt1, this.R828_I2C));
            this.R828_Arry[0] = ((byte)(0x7F & this.R828_Arry[0] | Sys_Info1.LOOP_THROUGH1));
            this.R828_I2C.RegAddr = 5;
            this.R828_I2C.Data = this.R828_Arry[0];
          }
          while (!I2C_Write(paramInt1, this.R828_I2C));
          this.R828_Arry[26] = ((byte)(0x7F & this.R828_Arry[26] | Sys_Info1.LT_ATT));
          this.R828_I2C.RegAddr = 31;
          this.R828_I2C.Data = this.R828_Arry[26];
        }
        while (!I2C_Write(paramInt1, this.R828_I2C));
        this.R828_Arry[10] = ((byte)(0x7F & this.R828_Arry[10] | Sys_Info1.FLT_EXT_WIDEST));
        this.R828_I2C.RegAddr = 15;
        this.R828_I2C.Data = this.R828_Arry[10];
      }
      while (!I2C_Write(paramInt1, this.R828_I2C));
      this.R828_Arry[20] = ((byte)(0x9F & this.R828_Arry[20] | Sys_Info1.POLYFIL_CUR));
      this.R828_I2C.RegAddr = 25;
      this.R828_I2C.Data = this.R828_Arry[20];
    }
    while (!I2C_Write(paramInt1, this.R828_I2C));
    label225: return true;
  }

  boolean R828_Standby(int paramInt1, int paramInt2)
  {
    if (paramInt2 == 0)
    {
      this.R828_I2C.RegAddr = 6;
      this.R828_I2C.Data = -79;
      if (!I2C_Write(paramInt1, this.R828_I2C));
      do
      {
        return false;
        this.R828_I2C.RegAddr = 5;
        this.R828_I2C.Data = 3;
      }
      while (!I2C_Write(paramInt1, this.R828_I2C));
    }
    do
    {
      this.R828_I2C.RegAddr = 7;
      this.R828_I2C.Data = 58;
      if (!I2C_Write(paramInt1, this.R828_I2C))
        break;
      this.R828_I2C.RegAddr = 8;
      this.R828_I2C.Data = 64;
      if (!I2C_Write(paramInt1, this.R828_I2C))
        break;
      this.R828_I2C.RegAddr = 9;
      this.R828_I2C.Data = -64;
      if (!I2C_Write(paramInt1, this.R828_I2C))
        break;
      this.R828_I2C.RegAddr = 10;
      this.R828_I2C.Data = 54;
      if (!I2C_Write(paramInt1, this.R828_I2C))
        break;
      this.R828_I2C.RegAddr = 12;
      this.R828_I2C.Data = 53;
      if (!I2C_Write(paramInt1, this.R828_I2C))
        break;
      this.R828_I2C.RegAddr = 15;
      this.R828_I2C.Data = 104;
      if (!I2C_Write(paramInt1, this.R828_I2C))
        break;
      this.R828_I2C.RegAddr = 17;
      this.R828_I2C.Data = 3;
      if (!I2C_Write(paramInt1, this.R828_I2C))
        break;
      this.R828_I2C.RegAddr = 23;
      this.R828_I2C.Data = -12;
      if (!I2C_Write(paramInt1, this.R828_I2C))
        break;
      this.R828_I2C.RegAddr = 25;
      this.R828_I2C.Data = 12;
      if (!I2C_Write(paramInt1, this.R828_I2C))
        break;
      return true;
      this.R828_I2C.RegAddr = 5;
      this.R828_I2C.Data = -93;
      if (!I2C_Write(paramInt1, this.R828_I2C))
        break;
      this.R828_I2C.RegAddr = 6;
      this.R828_I2C.Data = -79;
    }
    while (I2C_Write(paramInt1, this.R828_I2C));
    return false;
  }

  SysFreq_Info_Type R828_SysFreq_Sel(int paramInt1, int paramInt2)
  {
    SysFreq_Info_Type localSysFreq_Info_Type = new SysFreq_Info_Type();
    switch (paramInt1)
    {
    case 16:
    case 17:
    case 18:
    case 19:
    default:
      localSysFreq_Info_Type.MIXER_TOP = 36;
      localSysFreq_Info_Type.LNA_TOP = -27;
      localSysFreq_Info_Type.LNA_VTH_L = 83;
      localSysFreq_Info_Type.MIXER_VTH_L = 117;
      localSysFreq_Info_Type.AIR_CABLE1_IN = 0;
      localSysFreq_Info_Type.CABLE2_IN = 0;
      localSysFreq_Info_Type.PRE_DECT = 64;
      localSysFreq_Info_Type.LNA_DISbyteGE = 14;
      localSysFreq_Info_Type.CP_CUR = 56;
      localSysFreq_Info_Type.DIV_BUF_CUR = 48;
      localSysFreq_Info_Type.FILTER_CUR = 64;
      return localSysFreq_Info_Type;
    case 8:
    case 9:
    case 10:
    case 11:
      if ((paramInt2 == 506000) || (paramInt2 == 666000) || (paramInt2 == 818000))
      {
        localSysFreq_Info_Type.MIXER_TOP = 20;
        localSysFreq_Info_Type.LNA_TOP = -27;
        localSysFreq_Info_Type.CP_CUR = 40;
      }
      for (localSysFreq_Info_Type.DIV_BUF_CUR = 32; ; localSysFreq_Info_Type.DIV_BUF_CUR = 48)
      {
        localSysFreq_Info_Type.LNA_VTH_L = 83;
        localSysFreq_Info_Type.MIXER_VTH_L = 117;
        localSysFreq_Info_Type.AIR_CABLE1_IN = 0;
        localSysFreq_Info_Type.CABLE2_IN = 0;
        localSysFreq_Info_Type.PRE_DECT = 64;
        localSysFreq_Info_Type.LNA_DISbyteGE = 14;
        localSysFreq_Info_Type.FILTER_CUR = 64;
        return localSysFreq_Info_Type;
        localSysFreq_Info_Type.MIXER_TOP = 36;
        localSysFreq_Info_Type.LNA_TOP = -27;
        localSysFreq_Info_Type.CP_CUR = 56;
      }
    case 12:
    case 13:
    case 14:
    case 15:
      localSysFreq_Info_Type.MIXER_TOP = 36;
      localSysFreq_Info_Type.LNA_TOP = -27;
      localSysFreq_Info_Type.LNA_VTH_L = 83;
      localSysFreq_Info_Type.MIXER_VTH_L = 117;
      localSysFreq_Info_Type.AIR_CABLE1_IN = 0;
      localSysFreq_Info_Type.CABLE2_IN = 0;
      localSysFreq_Info_Type.PRE_DECT = 64;
      localSysFreq_Info_Type.LNA_DISbyteGE = 14;
      localSysFreq_Info_Type.CP_CUR = 56;
      localSysFreq_Info_Type.DIV_BUF_CUR = 48;
      localSysFreq_Info_Type.FILTER_CUR = 64;
      return localSysFreq_Info_Type;
    case 20:
    }
    localSysFreq_Info_Type.MIXER_TOP = 36;
    localSysFreq_Info_Type.LNA_TOP = -27;
    localSysFreq_Info_Type.LNA_VTH_L = 117;
    localSysFreq_Info_Type.MIXER_VTH_L = 117;
    localSysFreq_Info_Type.AIR_CABLE1_IN = 0;
    localSysFreq_Info_Type.CABLE2_IN = 0;
    localSysFreq_Info_Type.PRE_DECT = 64;
    localSysFreq_Info_Type.LNA_DISbyteGE = 14;
    localSysFreq_Info_Type.CP_CUR = 56;
    localSysFreq_Info_Type.DIV_BUF_CUR = 48;
    localSysFreq_Info_Type.FILTER_CUR = 64;
    return localSysFreq_Info_Type;
  }

  Sys_Info_Type R828_Sys_Sel(int paramInt)
  {
    Sys_Info_Type localSys_Info_Type = new Sys_Info_Type();
    switch (paramInt)
    {
    case 16:
    case 17:
    case 18:
    case 19:
    default:
      localSys_Info_Type.IF_KHz = 'ᇚ';
      localSys_Info_Type.BW = this.BW_8M;
      localSys_Info_Type.FILT_CAL_LO = 68500;
      localSys_Info_Type.FILT_GAIN = 16;
      localSys_Info_Type.IMG_R = 0;
      localSys_Info_Type.FILT_Q = 16;
      localSys_Info_Type.HP_COR = 13;
      localSys_Info_Type.EXT_ENABLE = 96;
      localSys_Info_Type.LOOP_THROUGH1 = 0;
      localSys_Info_Type.LT_ATT = 0;
      localSys_Info_Type.FLT_EXT_WIDEST = 0;
      localSys_Info_Type.POLYFIL_CUR = 96;
      return localSys_Info_Type;
    case 8:
    case 12:
      localSys_Info_Type.IF_KHz = 'ෲ';
      localSys_Info_Type.BW = this.BW_6M;
      localSys_Info_Type.FILT_CAL_LO = 56000;
      localSys_Info_Type.FILT_GAIN = 16;
      localSys_Info_Type.IMG_R = 0;
      localSys_Info_Type.FILT_Q = 16;
      localSys_Info_Type.HP_COR = 107;
      localSys_Info_Type.EXT_ENABLE = 96;
      localSys_Info_Type.LOOP_THROUGH1 = 0;
      localSys_Info_Type.LT_ATT = 0;
      localSys_Info_Type.FLT_EXT_WIDEST = 0;
      localSys_Info_Type.POLYFIL_CUR = 96;
      return localSys_Info_Type;
    case 9:
    case 13:
      localSys_Info_Type.IF_KHz = '࿦';
      localSys_Info_Type.BW = this.BW_7M;
      localSys_Info_Type.FILT_CAL_LO = 60000;
      localSys_Info_Type.FILT_GAIN = 16;
      localSys_Info_Type.IMG_R = 0;
      localSys_Info_Type.FILT_Q = 16;
      localSys_Info_Type.HP_COR = 43;
      localSys_Info_Type.EXT_ENABLE = 96;
      localSys_Info_Type.LOOP_THROUGH1 = 0;
      localSys_Info_Type.LT_ATT = 0;
      localSys_Info_Type.FLT_EXT_WIDEST = 0;
      localSys_Info_Type.POLYFIL_CUR = 96;
      return localSys_Info_Type;
    case 10:
    case 14:
      localSys_Info_Type.IF_KHz = 'ᇚ';
      localSys_Info_Type.BW = this.BW_7M;
      localSys_Info_Type.FILT_CAL_LO = 63000;
      localSys_Info_Type.FILT_GAIN = 16;
      localSys_Info_Type.IMG_R = 0;
      localSys_Info_Type.FILT_Q = 16;
      localSys_Info_Type.HP_COR = 42;
      localSys_Info_Type.EXT_ENABLE = 96;
      localSys_Info_Type.LOOP_THROUGH1 = 0;
      localSys_Info_Type.LT_ATT = 0;
      localSys_Info_Type.FLT_EXT_WIDEST = 0;
      localSys_Info_Type.POLYFIL_CUR = 96;
      return localSys_Info_Type;
    case 11:
    case 15:
      localSys_Info_Type.IF_KHz = 'ᇚ';
      localSys_Info_Type.BW = this.BW_8M;
      localSys_Info_Type.FILT_CAL_LO = 68500;
      localSys_Info_Type.FILT_GAIN = 16;
      localSys_Info_Type.IMG_R = 0;
      localSys_Info_Type.FILT_Q = 16;
      localSys_Info_Type.HP_COR = 11;
      localSys_Info_Type.EXT_ENABLE = 96;
      localSys_Info_Type.LOOP_THROUGH1 = 0;
      localSys_Info_Type.LT_ATT = 0;
      localSys_Info_Type.FLT_EXT_WIDEST = 0;
      localSys_Info_Type.POLYFIL_CUR = 96;
      return localSys_Info_Type;
    case 20:
    }
    localSys_Info_Type.IF_KHz = '࿟';
    localSys_Info_Type.BW = this.BW_6M;
    localSys_Info_Type.FILT_CAL_LO = 59000;
    localSys_Info_Type.FILT_GAIN = 16;
    localSys_Info_Type.IMG_R = 0;
    localSys_Info_Type.FILT_Q = 16;
    localSys_Info_Type.HP_COR = 106;
    localSys_Info_Type.EXT_ENABLE = 64;
    localSys_Info_Type.LOOP_THROUGH1 = 0;
    localSys_Info_Type.LT_ATT = 0;
    localSys_Info_Type.FLT_EXT_WIDEST = 0;
    localSys_Info_Type.POLYFIL_CUR = 96;
    return localSys_Info_Type;
  }

  boolean R828_Xtal_Check(int paramInt)
  {
    int i = 0;
    if (i >= 27)
    {
      this.R828_I2C.RegAddr = 16;
      this.R828_Arry[11] = ((byte)(0xB | 0xF4 & this.R828_Arry[11]));
      this.R828_I2C.Data = this.R828_Arry[11];
      if (I2C_Write(paramInt, this.R828_I2C))
        break label85;
    }
    label85: 
    do
    {
      do
      {
        do
        {
          do
          {
            do
            {
              do
              {
                do
                {
                  do
                  {
                    do
                    {
                      do
                      {
                        do
                        {
                          do
                          {
                            return false;
                            this.R828_Arry[i] = this.R828_iniArry[i];
                            i++;
                            break;
                            this.R828_I2C.RegAddr = 26;
                            this.R828_Arry[21] = ((byte)(0xF3 & this.R828_Arry[21]));
                            this.R828_I2C.Data = this.R828_Arry[21];
                          }
                          while (!I2C_Write(paramInt, this.R828_I2C));
                          this.R828_I2C.RegAddr = 19;
                          this.R828_Arry[14] = ((byte)(0x7F | 0x80 & this.R828_Arry[14]));
                          this.R828_I2C.Data = this.R828_Arry[14];
                        }
                        while (!I2C_Write(paramInt, this.R828_I2C));
                        this.R828_I2C.RegAddr = 19;
                        this.R828_Arry[14] = ((byte)(0xBF & this.R828_Arry[14]));
                        this.R828_I2C.Data = this.R828_Arry[14];
                      }
                      while (!I2C_Write(paramInt, this.R828_I2C));
                      this.R828_I2C_Len.RegAddr = 0;
                      this.R828_I2C_Len.Len = 3;
                    }
                    while (!I2C_Read_Len(paramInt, this.R828_I2C_Len));
                    if (((0x40 & this.R828_I2C_Len.Data[2]) != 0) && ((0x3F & this.R828_I2C_Len.Data[2]) != 63))
                      break label833;
                    this.R828_I2C.RegAddr = 16;
                    this.R828_Arry[11] = ((byte)(0x2 | 0xFC & this.R828_Arry[11]));
                    this.R828_I2C.Data = this.R828_Arry[11];
                  }
                  while (!I2C_Write(paramInt, this.R828_I2C));
                  R828_Delay_MS(paramInt, 5L);
                  this.R828_I2C_Len.RegAddr = 0;
                  this.R828_I2C_Len.Len = 3;
                }
                while (!I2C_Read_Len(paramInt, this.R828_I2C_Len));
                if (((0x40 & this.R828_I2C_Len.Data[2]) != 0) && ((0x3F & this.R828_I2C_Len.Data[2]) != 63))
                  break label825;
                this.R828_I2C.RegAddr = 16;
                this.R828_Arry[11] = ((byte)(0x1 | 0xFC & this.R828_Arry[11]));
                this.R828_I2C.Data = this.R828_Arry[11];
              }
              while (!I2C_Write(paramInt, this.R828_I2C));
              R828_Delay_MS(paramInt, 5L);
              this.R828_I2C_Len.RegAddr = 0;
              this.R828_I2C_Len.Len = 3;
            }
            while (!I2C_Read_Len(paramInt, this.R828_I2C_Len));
            if (((0x40 & this.R828_I2C_Len.Data[2]) != 0) && ((0x3F & this.R828_I2C_Len.Data[2]) != 63))
              break label817;
            this.R828_I2C.RegAddr = 16;
            this.R828_Arry[11] = ((byte)(0xFC & this.R828_Arry[11]));
            this.R828_I2C.Data = this.R828_Arry[11];
          }
          while (!I2C_Write(paramInt, this.R828_I2C));
          R828_Delay_MS(paramInt, 5L);
          this.R828_I2C_Len.RegAddr = 0;
          this.R828_I2C_Len.Len = 3;
        }
        while (!I2C_Read_Len(paramInt, this.R828_I2C_Len));
        if (((0x40 & this.R828_I2C_Len.Data[2]) != 0) && ((0x3F & this.R828_I2C_Len.Data[2]) != 63))
          break label809;
        this.R828_I2C.RegAddr = 16;
        this.R828_Arry[11] = ((byte)(0xF7 & this.R828_Arry[11]));
        this.R828_I2C.Data = this.R828_Arry[11];
      }
      while (!I2C_Write(paramInt, this.R828_I2C));
      R828_Delay_MS(paramInt, 20L);
      this.R828_I2C_Len.RegAddr = 0;
      this.R828_I2C_Len.Len = 3;
    }
    while ((!I2C_Read_Len(paramInt, this.R828_I2C_Len)) || ((0x40 & this.R828_I2C_Len.Data[2]) == 0) || ((0x3F & this.R828_I2C_Len.Data[2]) == 63));
    this.Xtal_cap_sel_tmp = 4;
    while (true)
    {
      return true;
      label809: this.Xtal_cap_sel_tmp = 3;
      continue;
      label817: this.Xtal_cap_sel_tmp = 2;
      continue;
      label825: this.Xtal_cap_sel_tmp = 1;
      continue;
      label833: this.Xtal_cap_sel_tmp = 0;
    }
  }

  public int exit(int paramInt)
    throws IOException
  {
    return r820t_SetStandby(paramInt, 0);
  }

  public int init(int paramInt)
    throws IOException
  {
    R828_Init(paramInt);
    r820t_SetStandardMode(paramInt, 8);
    return 0;
  }

  int r820t_Convert(int paramInt)
  {
    int i = 0;
    int j = 128;
    int k = 1;
    for (int m = 0; ; m++)
    {
      if (m >= 8)
        return i;
      if ((k & paramInt) != 0)
        i += j;
      j /= 2;
      k *= 2;
    }
  }

  int r820t_SetRfFreqHz(int paramInt, long paramLong)
  {
    R828_Set_Info localR828_Set_Info = new R828_Set_Info();
    localR828_Set_Info.R828_Standard = 8;
    localR828_Set_Info.RF_Hz = paramLong;
    localR828_Set_Info.RF_KHz = (paramLong / 1000L);
    boolean bool = R828_SetFrequency(paramInt, localR828_Set_Info, false);
    int i = 0;
    if (!bool)
      i = -1;
    return i;
  }

  int r820t_SetStandardMode(int paramInt1, int paramInt2)
  {
    if (!R828_SetStandard(paramInt1, paramInt2))
      return -1;
    return 0;
  }

  int r820t_SetStandby(int paramInt1, int paramInt2)
  {
    if (!R828_Standby(paramInt1, paramInt2))
      return -1;
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
    return r820t_SetRfFreqHz(paramInt, paramLong);
  }

  public int set_gain(int paramInt1, int paramInt2)
    throws IOException
  {
    if (R828_SetRfGain(paramInt1, paramInt2))
      return 0;
    return -1;
  }

  public int set_gain_mode(int paramInt, boolean paramBoolean)
    throws IOException
  {
    if (R828_RfGainMode(paramInt, paramBoolean))
      return 0;
    return -1;
  }

  public int set_if_gain(int paramInt1, int paramInt2, int paramInt3)
    throws IOException
  {
    return 0;
  }

  class Freq_Info_Type
  {
    byte IMR_MEM;
    byte OPEN_D;
    byte RF_MUX_PLOY;
    byte TF_C;
    byte XTAL_CAP0P;
    byte XTAL_CAP10P;
    byte XTAL_CAP20P;

    Freq_Info_Type()
    {
    }
  }

  class R828_GPIO_Type
  {
    public static final boolean HI_SIG = true;
    public static final boolean LO_SIG;

    R828_GPIO_Type()
    {
    }
  }

  class R828_I2C_LEN_TYPE
  {
    byte[] Data = new byte[50];
    byte Len;
    byte RegAddr;

    R828_I2C_LEN_TYPE()
    {
    }
  }

  class R828_I2C_TYPE
  {
    byte Data;
    byte RegAddr;

    R828_I2C_TYPE()
    {
    }
  }

  class R828_IfAgc_Type
  {
    public static final int IF_AGC1 = 0;
    public static final int IF_AGC2 = 1;

    R828_IfAgc_Type()
    {
    }
  }

  class R828_InputMode_Type
  {
    public static final int AIR_IN = 0;
    public static final int CABLE_IN_1 = 1;
    public static final int CABLE_IN_2 = 2;

    R828_InputMode_Type()
    {
    }
  }

  class R828_LoopThrough_Type
  {
    public static final boolean LOOP_THROUGH1 = true;
    public static final boolean SIGLE_IN;

    R828_LoopThrough_Type()
    {
    }
  }

  class R828_RF_Gain_Info
  {
    byte RF_gain1;
    byte RF_gain2;
    byte RF_gain_comb;

    R828_RF_Gain_Info()
    {
    }
  }

  class R828_RF_Gain_TYPE
  {
    int RF_AUTO = 0;
    int RF_MANUAL;

    R828_RF_Gain_TYPE()
    {
    }
  }

  class R828_SectType
  {
    int Gain_X;
    int Phase_Y;
    int Value;

    R828_SectType()
    {
    }
  }

  class R828_Set_Info
  {
    r820T_tuner.R828_IfAgc_Type R828_IfAgc_Select;
    int R828_Standard;
    long RF_Hz;
    long RF_KHz;
    r820T_tuner.R828_LoopThrough_Type RT_Input;
    r820T_tuner.R828_InputMode_Type RT_InputMode;

    R828_Set_Info()
    {
    }
  }

  class SysFreq_Info_Type
  {
    byte AIR_CABLE1_IN;
    byte CABLE2_IN;
    byte CP_CUR;
    byte DIV_BUF_CUR;
    byte FILTER_CUR;
    byte LNA_DISbyteGE;
    byte LNA_TOP;
    byte LNA_VTH_L;
    byte MIXER_TOP;
    byte MIXER_VTH_L;
    byte PRE_DECT;

    SysFreq_Info_Type()
    {
    }
  }

  class Sys_Info_Type
  {
    int BW;
    byte EXT_ENABLE;
    int FILT_CAL_LO;
    byte FILT_GAIN;
    byte FILT_Q;
    byte FLT_EXT_WIDEST;
    byte HP_COR;
    char IF_KHz;
    byte IMG_R;
    byte LOOP_THROUGH1;
    byte LT_ATT;
    byte POLYFIL_CUR;

    Sys_Info_Type()
    {
    }
  }
}

/* Location:           /Users/kevinfinisterre/Desktop/ADSB_USB_playstore.jar
 * Qualified Name:     com.wilsonae.rtlsdrlib.r820T_tuner
 * JD-Core Version:    0.6.2
 */
