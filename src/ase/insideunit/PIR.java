package ase.insideunit;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;

import ase.utils.Pin;

/**
 * 
 * PIR Sensor (#555-28027)
 * http://search.digikey.com/us/en/products/555-28027/555-28027-ND/1774435
 * $10.99
 *
 */

public class PIR extends Thread{

	private RandomAccessFile f;
	private boolean moving = false;
	private Pin pin;
	
	public PIR(Pin p){
		this.pin = p;
		try {
			f = new RandomAccessFile("MOV", "r");
		} catch (FileNotFoundException e) {
			// Create file
			try {
				FileWriter fw = new FileWriter("MOV");
				fw.write('0');
				fw.close();
				
				f = new RandomAccessFile("MOV", "r");
			} catch (IOException e1) {
				System.err.println(e1);
			}
		}
	}
	
	public void run(){
		
		while(true){
			
			if(moving != checkMovement()){
				moving = !moving;
				pin.sendSignal(moving, false); // HIGH if moving (Alarm), LOW otherwise
			}
			
			try {
				sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	private boolean checkMovement(){
		char mov = '0';
		try {
			mov = (char) f.read();
			f.seek(0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return mov == '1';
	}
	
}
