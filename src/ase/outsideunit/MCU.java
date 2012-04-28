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
				char bytee = uart.shiftReg;
				
				//Debug
				System.out.println((int) bytee);
				
				float voltage = bytee / 100f;
				compressorPin.sendVoltage(voltage, true);
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
			
			boolean first = true;
			
			while(true){
				
				while(RXD.readSignal()) delay();
				
				String frame = "0";
				
				// Start bit Received
				delay();
				
				char bit = 0;
				int base = 128;
				for(int i = 0; i < 8; i++, base /= 2){
					if(RXD.readSignal()){
						// Bit 1
						bit += base;
						frame = frame.concat("1");
					}else
						frame = frame.concat("0");
					
					delay();
				}
				
				// Stop bit
				if(!RXD.readSignal()){
					System.err.println("Stop bit error");
					frame = frame.concat("0");
				}else{
					if(!first){
						shiftReg = bit;
						interruptModule(UART_INTERRUPT);
					}else 
						first = false;
					frame = frame.concat("1");
				}
				lblFrame.setText("Recv: "+frame);
				delay();
			}
		}
	}

}
