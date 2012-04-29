package ase.outsideunit;

/**
 * 
 * AT90PWM216
 * http://www.atmel.com/devices/AT90PWM216.aspx
 * $2.59
 * 
 */

import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.JLabel;

import ase.interfaces.InterruptibleModule;
import ase.utils.ADCPin;
import ase.utils.Logger;
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
	
	private ADCPin compressorPin;
	
	private UART uart = new UART();
	private static final int UART_INTERRUPT = 2;
	
	/* DEBUG */
	private JLabel lblFrame;
	
	/* Flags */
	private volatile boolean byteReceived= false;
	
	public MCU(JLabel frame){
		this.lblFrame = frame;
	}
	
	@Override
	public void interruptModule(int id) {
		this.interrupts.offer(id);
	}
	
	public void interrupt(){
		intHandler.interrupt();
		super.interrupt();
	}
	
	public void configurePins(Pin txd, Pin rxd, ADCPin compressor){
		this.TXD = txd;
		this.RXD = rxd;
		this.compressorPin = compressor;
	}
	
	public void run(){
		uart.start();
		intHandler.start();
		
		while(true){
			
			try {
				sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if(byteReceived){
				byteReceived = false;
				int bytee = uart.shiftReg;
				
				//Debug
				//System.out.println(bytee);
				
				if(bytee >= 0 && bytee <= 44){
					float voltage = bytee / 100f;
					compressorPin.sendVoltage(voltage, true);
				}
			}
			
		}
	}
	
	private class InterruptHandler extends Thread{
		
		private MCU mcu;
		
		public InterruptHandler(MCU mcu){
			this.mcu = mcu;
			setDaemon(true);
		}
		
		public void run(){
			
			int interrupt = 0;
			while(true){
				try {
					interrupt = interrupts.take();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				mcu.suspend();
				
				switch(interrupt){
					case UART_INTERRUPT:
						byteReceived = true;
						break;
				}
				mcu.resume();
			}
		}
	}
	
	private class UART extends Thread{
		
		private final static int BITDELAY = 50;
		
		public volatile int shiftReg;
		
		public UART(){
			setDaemon(true);
		}
		
		private void reduceDelay(){
			try {
				sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
			
			boolean first = true;
			
			while(true){
				
				while(RXD.readSignal()) reduceDelay();
				
				// Start bit Received
				Logger.instance().debug("OutsideUnite", "UART Read", "1");
				delay();
				
				String bytee = "";
				for(int i = 0; i < 8; i++){
					if(RXD.readSignal()){
						// Bit 1
						bytee = bytee.concat("1");
					}else {
						bytee = bytee.concat("0");
					}						

					delay();
				}

				int bite = Integer.parseInt(bytee, 2);
				
				Logger.instance().debug("OutsideUnite", "UART Read", "" + bytee + " = " + bite);

				// Stop bit
				/*if(!RXD.readSignal()){
					System.err.println("Stop bit error");
					frame = frame.concat("0");
				}else{*/
					if(!first){
						shiftReg = bite;
						interruptModule(UART_INTERRUPT);
					}else 
						first = false;
				//}
				lblFrame.setText("Recv: "+bytee);
				delay();
			}
		}
	}

}
