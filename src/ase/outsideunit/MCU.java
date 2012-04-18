package ase.outsideunit;

import java.util.concurrent.LinkedBlockingQueue;

import ase.interfaces.InterruptibleModule;
import ase.utils.ADCPin;
import ase.utils.Pin;

public class MCU extends Thread implements InterruptibleModule{
	
	/* Interrupts */
	private LinkedBlockingQueue<Integer> interrupts = new LinkedBlockingQueue<Integer>();
	private InterruptHandler intHandler = new InterruptHandler(this);
	
	/* IO */
	public static final int RXDPIN = 0;
	private Pin RXD;
	public static final int TXDPIN = 1;
	private Pin TXD;
	
	private UART uart = new UART();
	
	@Override
	public void interruptModule(int id) {
		this.interrupts.offer(id);
	}
	
	public void interrupt(){
		intHandler.interrupt();
		super.interrupt();
	}
	
	public void configurePins(Pin txd, Pin rxd){
		this.TXD = txd;
		this.RXD = rxd;
	}
	
	public void run(){
		uart.start();
	}
	
	private class InterruptHandler extends Thread{
		
		private MCU mcu;
		
		public InterruptHandler(MCU mcu){
			this.mcu = mcu;
			setDaemon(true);
		}
	}
	
	private class UART extends Thread{
		
		private final static int BITDELAY = 30;
		
		public char shiftReg;
		
		public UART(){
			setDaemon(true);
		}
		
		private void delay(){
			try {
				sleep(BITDELAY);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public void run(){
			
			while(true){
				
				while(RXD.readSignal()) delay();
				
				// Start bit Received
				delay();
				
				char bit = 0;
				int base = 128;
				for(int i = 0; i < 8; i++, base /= 2){
					if(RXD.readSignal()){
						// Bit 1
						bit += base;
					}
					// Otherwise bit 0 (do nothing)
					delay();
				}
				
				// Stop bit
				if(!RXD.readSignal()) System.out.println("Stop bit error");
				else{
					shiftReg = bit;
					System.out.println((int) bit);
					// TODO interrupt cpu
				}
			}
		}
	}

}
