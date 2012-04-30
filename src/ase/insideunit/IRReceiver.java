package ase.insideunit;

/**
 * 
 * ATA2525
 * http://www.atmel.com/devices/ATA2525.aspx?tab=parameters
 * $2.28
 */

import ase.utils.Pin;

public class IRReceiver extends Thread {

	private static final int SIGNALTIMEDELAY = 30;
	private IRSensor sensor = new IRSensor();
	private Pin outPin;
	
	/* DATA */
	boolean[] signalPair = new boolean[2];
	
	public IRReceiver(Pin out){
		this.outPin = out;
	}
	
	private void delay(){
		try {
			sleep(SIGNALTIMEDELAY);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private boolean sendValidBit(){
		if(signalPair[0] ^ signalPair[1]){
			if(signalPair[0]){
				outPin.sendSignal(Pin.HIGH, false); // Bit 1
				//System.out.println("1");
			}
			else{
				outPin.sendSignal(Pin.LOW, false); // Bit 0
				//System.out.println("0");
			}
			return true;
		}
		else return false;
	}
	
	public void run(){
		
		boolean error;
		
		while(true){
			
			error = false;

			//==== 1st Start Bit (1) =======
			do{
				delay();
			}while(!sensor.readValue());

			//signalPair[0] = true;
			delay();
			if(sensor.readValue()) continue;
			/*signalPair[1] = sensor.readValue();
			if(!sendValidBit()) continue;*/
			
			// ==== 2nd Start Bit (1) &  Toogle Bit (1) ======
			
			for(int i = 0; i < 2; i++){
				delay();
				if(!sensor.readValue()) { error = true; break; }
				//signalPair[0] = true;
				delay();
				if(sensor.readValue()) { error = true; break; }
				/*signalPair[1] = sensor.readValue();
				if(!sendValidBit()) { error = true; break; }*/	
			}
			if(error) continue;
			
			// === Address Bits (5) & Command Bits (6) =====
			for(int i = 0; i < 11 ; i++){
				delay();
				signalPair[0] = sensor.readValue();
				delay();
				signalPair[1] = sensor.readValue();
				if(!sendValidBit()){
					error = true;
					break;
				}
			}
			if(error) continue;
		}
		
	}
	
}
