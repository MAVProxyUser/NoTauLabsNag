package org.openpilot_nonag.androidgcs;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AlarmsView  extends LinearLayout  {

	private static final String TAG = AlarmsView.class.getSimpleName();
	
	private View mView;
    private Context mContext;

    private TextView mTvAtti;
    private TextView mTvStab;
    private TextView mTvPath;
    private TextView mTvPlan;

	private TextView mTvGPS;

	private TextView mTvSensor;

	private TextView mTvAirSpd;

	private TextView mTvMag;

	private TextView mTvInput;

	private TextView mTvOutput;

	private TextView mTvI2C;

	private TextView mTvTelem;

	private TextView mTvBatt;

	private TextView mTvTime;

	private TextView mTvBoot;

	private TextView mTvMem;

	private TextView mTvStack;

	private TextView mTvEvent;

	private TextView mTvCpu;

	private TextView mTvConfig;
	
	
	public static enum Alarm {
	    ATTI, STAB, PATH, PLAN, GPS, SENSOR, AIRSPD, MAG, INPUT, OUTPUT, I2C, TELEM, BATT,
	    TIME, CONFIG, BOOT, MEM, STACK, EVENT, CPU
	}
	
	public static enum AlarmState {
		CRITICAL, ERROR, WARNING, NONE, UNINIT, OK
	}
	
	public AlarmsView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		this.mContext = context;
		
		LayoutInflater inflater;
	    inflater = (LayoutInflater) context
	            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    mView = inflater.inflate(R.layout.alarms_fragment, this);
	    
	    mTvAtti = (TextView) findViewById(R.id.tvATTI);
	    mTvStab = (TextView) findViewById(R.id.tvSTAB);
	    mTvPath = (TextView) findViewById(R.id.tvPATH);
	    mTvPlan = (TextView) findViewById(R.id.tvPLAN);
	    
	    mTvGPS = (TextView) findViewById(R.id.tvGPS);
	    mTvSensor = (TextView) findViewById(R.id.tvSENSR);
	    mTvAirSpd = (TextView) findViewById(R.id.tvAIRSPD);
	    mTvMag = (TextView) findViewById(R.id.tvMAG);
	    
	    mTvInput = (TextView) findViewById(R.id.tvINPUT);
	    mTvOutput = (TextView) findViewById(R.id.tvOUTPUT);
	    mTvI2C = (TextView) findViewById(R.id.tvI2C);
	    mTvTelem = (TextView) findViewById(R.id.tvTELEMETRY);
	    
	    mTvBatt= (TextView) findViewById(R.id.tvBATT);
	    mTvTime = (TextView) findViewById(R.id.tvTIME);
	    mTvConfig = (TextView) findViewById(R.id.tvCONFIG);
	    
	    mTvBoot = (TextView) findViewById(R.id.tvBOOT);
	    mTvMem = (TextView) findViewById(R.id.tvMEM);
	    mTvStack = (TextView) findViewById(R.id.tvSTACK);
	    mTvEvent = (TextView) findViewById(R.id.tvEVENT);
	    mTvCpu = (TextView) findViewById(R.id.tvCPU);
	  
	    
	}
	
	public void setAlarmStatus( Alarm alarm, AlarmState level){
		
		switch(alarm){
			case ATTI:
				mTvAtti.setBackgroundColor(getColor(level));
				break;
			case STAB:
				mTvStab.setBackgroundColor(getColor(level));
				break;
			case PATH:
				mTvPath.setBackgroundColor(getColor(level));
				break;
			case PLAN:
				mTvPlan.setBackgroundColor(getColor(level));
				break;
			case GPS:
				mTvGPS.setBackgroundColor(getColor(level));
				break;
				
			case SENSOR: 
				mTvSensor.setBackgroundColor(getColor(level));
				break;
			case AIRSPD:
				mTvAirSpd.setBackgroundColor(getColor(level));
				break;
			case MAG:
				mTvMag.setBackgroundColor(getColor(level));
				break;
			case INPUT:
				mTvInput.setBackgroundColor(getColor(level));
				break;
			case OUTPUT:
				mTvOutput.setBackgroundColor(getColor(level));
				break;
			case I2C:
				mTvI2C.setBackgroundColor(getColor(level));
				break;
			case TELEM:
				mTvTelem.setBackgroundColor(getColor(level));
				break;
			case BATT:
				mTvBatt.setBackgroundColor(getColor(level));
				break;
			case TIME:
				mTvTime.setBackgroundColor(getColor(level));
				break;
			case CONFIG:
				mTvConfig.setBackgroundColor(getColor(level));
				break;
			case BOOT:
				mTvBoot.setBackgroundColor(getColor(level));
				break;
			case MEM: 
				mTvMem.setBackgroundColor(getColor(level));
				break;
			case STACK:
				mTvStack.setBackgroundColor(getColor(level));
				break;
			case EVENT:
				mTvEvent.setBackgroundColor(getColor(level));
				break;
			case CPU:
				mTvCpu.setBackgroundColor(getColor(level));
				break;
				
		}
		invalidate();
		
			
	}
	
	private int getColor(AlarmState level){
		
		switch(level){
		case ERROR:
		case CRITICAL:
			return Color.RED;
		case WARNING:
			return Color.rgb(255,165,0);
		case OK:
			return Color.GREEN;
		case UNINIT:
		case NONE:
		default:
			return Color.LTGRAY;
		}
	}
	
	
	


}
