package org.openpilot_nonag.androidgcs;


public enum Alarms {

	ATTI("Attitude"),
	STAB("Stabilization"),
	PATH("Guidance"),
	PLAN("PathPlan"),
	GPS("GPS"),
	SENSOR("Sensors"),
	AIRSPD("Airspeed"),
	MAG("Magnetometer"),
	INPUT("Receiver"),
	OUTPUT("Actuator"),
	I2C("I2C"),
	TELEM("Telemetry"),
	BATT("Battery"),
	TIME("FlightTime"),
	CONFIG("SystemConfiguration"),
	BOOT("BootFault"),
	MEM("OutOfMemory"),
	STACK("StackOverflow"),
	EVENT("EventSystem"),
	CPU("CPUOverload"),
	MANUAL("ManualControl")
	;
	
	private final String text;
	private String name;
	/**
     * @param text
     */
    private Alarms(final String text) {
        this.text = text;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return text;
    }
    
    public static Alarms getEnumByString(String code){
        for(Alarms e : Alarms.values()){
            if(code == e.text) return e;

        }
        return null;
    }
   
    
    
}
