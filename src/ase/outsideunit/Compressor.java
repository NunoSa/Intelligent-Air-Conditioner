package ase.outsideunit;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.swing.JLabel;

import ase.utils.ADCPin;

public class Compressor extends Thread {

	private ADCPin pin;
	private JLabel lblSpeed;
	private RandomAccessFile f;
	
	public Compressor(ADCPin pin, JLabel speed){
		this.pin = pin;
		this.lblSpeed = speed;
		
		try {
			f = new RandomAccessFile("TEMP", "rwd");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void run(){
		
		int speed, temp;
		float curTemp;
		
		while(true){
			
			try {
				sleep(3000); // TODO change time
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// Speed = voltage * 100
			speed = (int) (pin.readVoltage() * 100f);
			
			lblSpeed.setText("Speed: "+speed);
			
			if(speed != 0){
				
				// temp = -speed + 45
				temp = 45 - speed;
				try {
					curTemp = f.readFloat();
					f.seek(0);
					if(curTemp < temp){
						
						// Increase Temperature
						f.writeFloat(++curTemp);
						f.seek(0);
						
					}else if(curTemp > temp){
						
						// Decrease Temperature
						f.writeFloat(--curTemp);
						f.seek(0);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
}
