package org.openpilot_nonag.rtlsdrlib;

import java.io.IOException;

public class e4k_tuner
  implements RtlSdr_tuner_iface
{
  private final int E4K_AGC11_LNA_GAIN_ENH = 1;
  private final int E4K_AGC1_LIN_MODE = 16;
  private final int E4K_AGC1_LNA_G_HIGH = 128;
  private final int E4K_AGC1_LNA_G_LOW = 64;
  private final int E4K_AGC1_LNA_UPDATE = 32;
  private final int E4K_AGC1_MOD_MASK = 15;
  private final int E4K_AGC6_LNA_CAL_REQ = 16;
  private final int E4K_AGC7_GAIN_STEP_5dB = 32;
  private final int E4K_AGC7_MIX_GAIN_AUTO = 1;
  private final int E4K_AGC8_SENS_LIN_AUTO = 1;
  private final int E4K_AGC_MOD_IF_DIG_LNA_AUTON = 7;
  private final int E4K_AGC_MOD_IF_DIG_LNA_SERIAL = 6;
  private final int E4K_AGC_MOD_IF_DIG_LNA_SUPERV = 8;
  private final int E4K_AGC_MOD_IF_PWM_LNA_AUTONL = 2;
  private final int E4K_AGC_MOD_IF_PWM_LNA_PWM = 5;
  private final int E4K_AGC_MOD_IF_PWM_LNA_SERIAL = 1;
  private final int E4K_AGC_MOD_IF_PWM_LNA_SUPERV = 3;
  private final int E4K_AGC_MOD_IF_SERIAL_LNA_AUTON = 9;
  private final int E4K_AGC_MOD_IF_SERIAL_LNA_PWM = 4;
  private final int E4K_AGC_MOD_IF_SERIAL_LNA_SUPERV = 10;
  private final int E4K_AGC_MOD_SERIAL = 0;
  private final int E4K_BAND_L = 3;
  private final int E4K_BAND_UHF = 2;
  private final int E4K_BAND_VHF2 = 0;
  private final int E4K_BAND_VHF3 = 1;
  private final int E4K_CHFCALIB_CMD = 1;
  private final int E4K_CLKOUT_DISABLE = 150;
  private final int E4K_DC1_CAL_REQ = 1;
  private final int E4K_DC5_I_LUT_EN = 1;
  private final int E4K_DC5_Q_LUT_EN = 2;
  private final int E4K_DC5_RANGE_DET_EN = 4;
  private final int E4K_DC5_RANGE_EN = 8;
  private final int E4K_DC5_TIMEVAR_EN = 16;
  private final int E4K_FILT3_DISABLE = 32;
  private final int E4K_F_MIX_BW_1M9 = 15;
  private final int E4K_F_MIX_BW_27M = 0;
  private final int E4K_F_MIX_BW_2M3 = 14;
  private final int E4K_F_MIX_BW_2M7 = 13;
  private final int E4K_F_MIX_BW_3M = 12;
  private final int E4K_F_MIX_BW_3M4 = 11;
  private final int E4K_F_MIX_BW_3M8 = 10;
  private final int E4K_F_MIX_BW_4M2 = 9;
  private final int E4K_F_MIX_BW_4M6 = 8;
  private final int E4K_I2C_ADDR = 200;
  private final int E4K_IF_FILTER_CHAN = 1;
  private final int E4K_IF_FILTER_MIX = 0;
  private final int E4K_IF_FILTER_RC = 3;
  private final int E4K_MASTER1_NORM_STBY = 2;
  private final int E4K_MASTER1_POR_DET = 4;
  private final int E4K_MASTER1_RESET = 1;
  private final int E4K_REG_AGC1 = 26;
  private final int E4K_REG_AGC11 = 36;
  private final int E4K_REG_AGC12 = 37;
  private final int E4K_REG_AGC2 = 27;
  private final int E4K_REG_AGC3 = 28;
  private final int E4K_REG_AGC4 = 29;
  private final int E4K_REG_AGC5 = 30;
  private final int E4K_REG_AGC6 = 31;
  private final int E4K_REG_AGC7 = 32;
  private final int E4K_REG_AGC8 = 33;
  private final int E4K_REG_BIAS = 120;
  private final int E4K_REG_CHFILT_CALIB = 123;
  private final int E4K_REG_CLKOUT_PWDN = 122;
  private final int E4K_REG_CLK_INP = 5;
  private final int E4K_REG_DC1 = 41;
  private final int E4K_REG_DC2 = 42;
  private final int E4K_REG_DC3 = 43;
  private final int E4K_REG_DC4 = 44;
  private final int E4K_REG_DC5 = 45;
  private final int E4K_REG_DC6 = 46;
  private final int E4K_REG_DC7 = 47;
  private final int E4K_REG_DC8 = 48;
  private final int E4K_REG_DCTIME1 = 112;
  private final int E4K_REG_DCTIME2 = 113;
  private final int E4K_REG_DCTIME3 = 114;
  private final int E4K_REG_DCTIME4 = 115;
  private final int E4K_REG_FILT1 = 16;
  private final int E4K_REG_FILT2 = 17;
  private final int E4K_REG_FILT3 = 18;
  private final int E4K_REG_GAIN1 = 20;
  private final int E4K_REG_GAIN2 = 21;
  private final int E4K_REG_GAIN3 = 22;
  private final int E4K_REG_GAIN4 = 23;
  private final int E4K_REG_I2C_REG_ADDR = 125;
  private final int E4K_REG_ILUT0 = 96;
  private final int E4K_REG_ILUT1 = 97;
  private final int E4K_REG_ILUT2 = 98;
  private final int E4K_REG_ILUT3 = 99;
  private final int E4K_REG_MASTER1 = 0;
  private final int E4K_REG_MASTER2 = 1;
  private final int E4K_REG_MASTER3 = 2;
  private final int E4K_REG_MASTER4 = 3;
  private final int E4K_REG_MASTER5 = 4;
  private final int E4K_REG_PWM1 = 116;
  private final int E4K_REG_PWM2 = 117;
  private final int E4K_REG_PWM3 = 118;
  private final int E4K_REG_PWM4 = 119;
  private final int E4K_REG_QLUT0 = 80;
  private final int E4K_REG_QLUT1 = 81;
  private final int E4K_REG_QLUT2 = 82;
  private final int E4K_REG_QLUT3 = 83;
  private final int E4K_REG_REF_CLK = 6;
  private final int E4K_REG_SYNTH1 = 7;
  private final int E4K_REG_SYNTH2 = 8;
  private final int E4K_REG_SYNTH3 = 9;
  private final int E4K_REG_SYNTH4 = 10;
  private final int E4K_REG_SYNTH5 = 11;
  private final int E4K_REG_SYNTH6 = 12;
  private final int E4K_REG_SYNTH7 = 13;
  private final int E4K_REG_SYNTH8 = 14;
  private final int E4K_REG_SYNTH9 = 15;
  private final int E4K_SYNTH1_BAND_SHIF = 1;
  private final int E4K_SYNTH1_PLL_LOCK = 1;
  private final int E4K_SYNTH7_3PHASE_EN = 8;
  private final int E4K_SYNTH8_VCOCAL_UPD = 4;
  private final byte[] if_stage1_gain = { -3, 6 };
  private final byte[] if_stage23_gain;
  private final byte[] if_stage4_gain;
  private final byte[] if_stage56_gain;
  private final byte[][] if_stage_gain;
  private final int[] if_stage_gain_len;

  public e4k_tuner()
  {
    byte[] arrayOfByte1 = new byte[4];
    arrayOfByte1[1] = 3;
    arrayOfByte1[2] = 6;
    arrayOfByte1[3] = 9;
    this.if_stage23_gain = arrayOfByte1;
    byte[] arrayOfByte2 = new byte[4];
    arrayOfByte2[1] = 1;
    arrayOfByte2[2] = 2;
    arrayOfByte2[3] = 2;
    this.if_stage4_gain = arrayOfByte2;
    this.if_stage56_gain = new byte[] { 3, 6, 9, 12, 15, 15, 15, 15 };
    byte[][] arrayOfByte = new byte[7][];
    arrayOfByte[1] = this.if_stage1_gain;
    arrayOfByte[2] = this.if_stage23_gain;
    arrayOfByte[3] = this.if_stage23_gain;
    arrayOfByte[4] = this.if_stage4_gain;
    arrayOfByte[5] = this.if_stage56_gain;
    arrayOfByte[6] = this.if_stage56_gain;
    this.if_stage_gain = arrayOfByte;
    int[] arrayOfInt = new int[7];
    arrayOfInt[1] = this.if_stage1_gain.length;
    arrayOfInt[2] = this.if_stage23_gain.length;
    arrayOfInt[3] = this.if_stage23_gain.length;
    arrayOfInt[4] = this.if_stage4_gain.length;
    arrayOfInt[5] = this.if_stage56_gain.length;
    arrayOfInt[6] = this.if_stage56_gain.length;
    this.if_stage_gain_len = arrayOfInt;
  }

  private int KHZ(int paramInt)
  {
    return paramInt * 1000;
  }

  private int MHZ(int paramInt)
  {
    return 1000 * (paramInt * 1000);
  }

  private int e4k_enable_manual_gain(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      e4k_reg_set_mask((byte)26, '\017', '\000');
      e4k_reg_set_mask((byte)32, '\001', '\000');
      return 0;
    }
    e4k_reg_set_mask((byte)26, '\017', '\t');
    e4k_reg_set_mask((byte)32, '\001', '\001');
    e4k_reg_set_mask((byte)36, '\007', '\000');
    return 0;
  }

  int closest_arr_idx(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    int i = 0;
    int j = -1;
    for (int k = 0; ; k++)
    {
      if (k >= paramInt1)
        return i;
      int m = unsigned_delta(paramInt2, paramArrayOfInt[k]);
      if (m < j)
      {
        j = m;
        i = k;
      }
    }
  }

  int e4k_if_filter_chan_enable(boolean paramBoolean)
  {
    if (paramBoolean);
    for (int i = 0; ; i = 32)
      return e4k_reg_set_mask((byte)18, ' ', (char)i);
  }

  int e4k_if_gain_set(int paramInt1, int paramInt2)
  {
    int i = find_stage_gain((byte)paramInt1, (byte)paramInt2);
    if (i < 0)
      return i;
    return 0;
  }

  int e4k_init()
  {
    e4k_reg_read(0);
    e4k_reg_write(0, 7);
    e4k_reg_write(5, 0);
    e4k_reg_write(6, 0);
    e4k_reg_write(122, -106);
    magic_init();
    e4k_reg_write(29, 16);
    e4k_reg_write(30, 4);
    e4k_reg_write(31, 26);
    e4k_reg_set_mask((byte)26, '\017', '\000');
    e4k_reg_set_mask((byte)32, '\001', '\000');
    e4k_enable_manual_gain(false);
    e4k_if_gain_set(1, 6);
    e4k_if_gain_set(2, 0);
    e4k_if_gain_set(3, 0);
    e4k_if_gain_set(4, 0);
    e4k_if_gain_set(5, 9);
    e4k_if_gain_set(6, 9);
    e4k_reg_set_mask((byte)45, '\003', '\000');
    e4k_reg_set_mask((byte)112, '\003', '\000');
    e4k_reg_set_mask((byte)113, '\003', '\000');
    return 0;
  }

  byte e4k_reg_read(int paramInt)
  {
    return SdrUSBDriver.rtlsdr_i2c_read_reg(200, paramInt);
  }

  int e4k_reg_set_mask(byte paramByte, char paramChar1, char paramChar2)
  {
    char c = e4k_reg_read(paramByte);
    if ((c & paramChar1) == paramChar2)
      return 0;
    return e4k_reg_write(paramByte, c & (paramChar1 ^ 0xFFFFFFFF) | paramChar2 & paramChar1);
  }

  int e4k_reg_write(int paramInt1, int paramInt2)
  {
    return SdrUSBDriver.rtlsdr_i2c_write_reg((byte)-56, (char)paramInt1, (char)paramInt2);
  }

  public int exit(int paramInt)
    throws IOException
  {
    return 0;
  }

  int find_stage_gain(byte paramByte1, byte paramByte2)
  {
    if (paramByte1 >= this.if_stage_gain.length)
    {
      i = -1;
      return i;
    }
    byte[] arrayOfByte = this.if_stage_gain[paramByte1];
    for (int i = 0; ; i++)
    {
      if (i >= this.if_stage_gain_len[paramByte1])
        return -1;
      if (arrayOfByte[i] == paramByte2)
        break;
    }
  }

  public int init(int paramInt)
    throws IOException
  {
    return e4k_init();
  }

  int magic_init()
  {
    e4k_reg_write(126, 1);
    e4k_reg_write(127, -2);
    e4k_reg_write(-126, 0);
    e4k_reg_write(-122, 80);
    e4k_reg_write(-121, 32);
    e4k_reg_write(-120, 1);
    e4k_reg_write(-97, 127);
    e4k_reg_write(-96, 7);
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
    return 0;
  }

  public int set_gain(int paramInt1, int paramInt2)
    throws IOException
  {
    return 0;
  }

  public int set_gain_mode(int paramInt, boolean paramBoolean)
    throws IOException
  {
    return 0;
  }

  public int set_if_gain(int paramInt1, int paramInt2, int paramInt3)
    throws IOException
  {
    return 0;
  }

  int unsigned_delta(int paramInt1, int paramInt2)
  {
    if (paramInt1 > paramInt2)
      return paramInt1 - paramInt2;
    return paramInt2 - paramInt1;
  }

  class e4k_band
  {
    int E4K_BAND_L = 3;
    int E4K_BAND_UHF = 2;
    int E4K_BAND_VHF2 = 0;
    int E4K_BAND_VHF3 = 1;

    e4k_band()
    {
    }
  }

  class e4k_if_filter
  {
    int E4K_IF_FILTER_CHAN;
    int E4K_IF_FILTER_MIX;
    int E4K_IF_FILTER_RC;

    e4k_if_filter()
    {
    }
  }

  class e4k_pll_params
  {
    int flo;
    int fosc;
    int intended_flo;
    byte r;
    byte r_idx;
    byte threephase;
    char x;
    byte z;

    e4k_pll_params()
    {
    }
  }

  class e4k_state
  {
    e4k_tuner.e4k_band band;
    byte i2c_addr;
    e4k_tuner.e4k_pll_params vco;

    e4k_state()
    {
    }
  }

  class reg_field
  {
    byte reg;
    byte shift;
    byte width;

    reg_field()
    {
    }
  }
}

/* Location:           /Users/kevinfinisterre/Desktop/ADSB_USB_playstore.jar
 * Qualified Name:     com.wilsonae.rtlsdrlib.e4k_tuner
 * JD-Core Version:    0.6.2
 */
