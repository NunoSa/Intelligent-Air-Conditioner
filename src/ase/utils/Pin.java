package ase.utils;

import ase.interfaces.InterruptibleModule;

public class Pin {
	
	public static final boolean HIGH = true;
	public static final boolean LOW = false;
	
	private volatile boolean pinValue = false;
	private int pinNumber;
	
	InterruptibleModule module;
	
	public Pin(InterruptibleModule m, int n){
		this.module = m;
		this.pinNumber = n;
	}
	
	public synchronized void sendSignal(boolean value, boolean mcu){
			pinValue = value;
			if(!mcu) module.interruptModule(pinNumber);
	}
	
	public synchronized boolean readSignal()
	{
		return pinValue;
	}

}
