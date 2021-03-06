package ase.utils;

/**
 * 
 * MAX485 
 * http://www.maxim-ic.com/datasheet/index.mvp/id/1111
 * $ 3.21
 * 
 */

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;

public class MAX485 {

	// Default values
	private static final char defaultA = '0';
	private static final char defaultB = '1';
	
	public static final boolean SLAVEMODE = false;
	public static final boolean MASTERMODE = true;
	private final static int BITDELAY = 5;
	private Pin DI;
	private Pin RO;
	private boolean RE_DE;
	private Transceiver t = null;
	private Receiver d = null;
	
	public MAX485(Pin di, Pin ro, boolean re_de){
		this.DI = di;
		this.RO = ro;
		this.RE_DE = re_de;
			
	}
	
	public void start(){
		if(RE_DE) t = new Transceiver();
		else if(d == null) d = new Receiver();
	}
	
	public void setRE_DE(boolean mode){
		
		if(mode == RE_DE) return;
		
		if(mode){
			// Master Mode
			d.stop = true;
			d = null;
			t = new Transceiver();
		}else{
			// Slave mode
			t.stop = true;
			t = null;
			d = new Receiver();
		}
	}
	
	public void stop(){
		if(RE_DE){ // Master mode
			t.stop = true;
		}
	}
	
	class Transceiver extends Thread{
		
		private RandomAccessFile A; // Inverted '-'
		private RandomAccessFile B; // Non.Inverted '+'
		private volatile boolean stop = false;
		
		public Transceiver(){
			try {
				A = new RandomAccessFile("A", "rwd");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				B = new RandomAccessFile("B", "rwd");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			start();
		}
		
		public void run(){
			
			boolean bit;
			
			while(!stop){
				bit = DI.readSignal();
				
				if(bit){
					// HIGH (bit 1)
					try {
						A.write('0');
						B.write('1');
						A.seek(0);
						B.seek(0);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else{
					// LOW (bit 0)
					try {
						A.write('1');
						B.write('0');
						A.seek(0);
						B.seek(0);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				try {
					sleep(BITDELAY);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			// Shutdown
			try {
				A.write('0');
				B.write('0');
				A.close();
				B.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	class Receiver extends Thread{
		
		private RandomAccessFile A; // Inverted '-'
		private RandomAccessFile B; // Non.Inverted '+'
		public volatile boolean stop = false;
		
		public Receiver(){
			try {
				A = new RandomAccessFile("A", "r");
			} catch (FileNotFoundException e) {
				try {
					FileWriter writer = new FileWriter("A");
					writer.write(defaultA);
					writer.close();
					A = new RandomAccessFile("A", "rwd");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			try {
				B = new RandomAccessFile("B", "r");
			} catch (FileNotFoundException e) {
				try {
					FileWriter writer = new FileWriter("B");
					writer.write(defaultB);
					writer.close();
					B = new RandomAccessFile("B", "rwd");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			start();
		}
		
		public void run(){
			
			char a = '0', b = '0';
			
			while(!stop){
				
				try {
					a = (char) A.read();
					b = (char) B.read();
					A.seek(0);
					B.seek(0);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if(b == '1'){
					
					if(a == '0')
						// Bit 1
						RO.sendSignal(Pin.HIGH, false); //System.out.println("1");
					else
						// Wrong signals (b and a are '1'), read again!
						continue;

				}else if(a == '1')
					// Bit 0
					RO.sendSignal(Pin.LOW, false);
					
				// If b and a are '0' means serial is powered off
				
				try {
					sleep(BITDELAY);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		
			try {
				A.close();
				B.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
