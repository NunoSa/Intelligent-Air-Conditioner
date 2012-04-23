package ase.utils;

import ase.interfaces.InterruptibleModule;

public class ADCPin {

	private volatile float voltage = 0f;
	private int pinNumber;
	
	InterruptibleModule module;
	
	public ADCPin(InterruptibleModule m, int n){
		this.module = m;
		this.pinNumber = n;
	}
	
	public synchronized void sendVoltage(float value, boolean mcu){
			voltage = value;
			if(!mcu) module.interruptModule(pinNumber);
	}
	
	public synchronized float readVoltage(){
		return voltage;
	}
}
