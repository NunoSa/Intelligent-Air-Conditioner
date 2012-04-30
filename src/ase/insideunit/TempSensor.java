package ase.insideunit;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;

import ase.utils.ADCPin;

/**
 * 
 * TMP36
 * http://www.adafruit.com/products/165
 * $2.00
 *
 */

public class TempSensor extends Thread{
	
	private RandomAccessFile f;
	private float voltage = 100f;
	private ADCPin pin;
	
	private static final float defaultTemp = 20F;
	
	public TempSensor(ADCPin p){
		this.pin = p;
		try {
			f = new RandomAccessFile("TEMP", "r");
		} catch (FileNotFoundException e) {
			// Create file
			try {
				f = new RandomAccessFile("TEMP", "rwd");
				f.writeFloat(defaultTemp);
				f.seek(0);
			} catch (IOException e1) {
				System.err.println(e1);
			}
			
		}
	}
	
	public void run(){
		
		float volt;
		
		while(true){
			
			volt = readVoltage();
			if(volt != voltage){
				voltage = volt;
				pin.sendVoltage(voltage, false);
			}
			
			try {
				sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
	
	private float readVoltage(){
		float temp = 0.0F;
		try {
			temp = f.readFloat();
			f.seek(0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		float volt = (temp + 50f) / 100f;
		
		return volt;
	}
}
