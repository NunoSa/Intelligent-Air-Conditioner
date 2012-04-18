package ase.rcontrol;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import ase.interfaces.InterruptibleModule;
import ase.modules.Timer;

/**
 * 
 * ATtiny20
 * http://www.atmel.com/devices/ATTINY20.aspx?tab=parameters
 * $0.7646
 * 
 */

public class MCU extends Thread implements InterruptibleModule{
	
	//private Timer
	private InterruptHandler intHandler = new InterruptHandler(this);
	
	/* I/O */
	public static final int TEMPUPPIN = 1;
	private Button bTempUp; // PA0
	public static final int TEMPDOWNPIN = 2;
	private Button bTempDown; // PA1
	public static final int POWERPIN = 3;
	private Button bPower; // PA2
	private IREmitter irEmitter = new IREmitter(); // PA3
	
	private static final int BITTIMERPIN = 4;
	private static final int FRAMETIMERPIN = 5;
	private static final int BITTIMERDELAY = 30;
	private static final int FRAMETIMERDELAY = 100;
	private Timer bitTimer = new Timer(this, BITTIMERPIN); // PB0
	private Timer frameTimer = new Timer(this, FRAMETIMERPIN); // PA7
	
	/* Interrupts */
	private LinkedBlockingQueue<Integer> interrupts = new LinkedBlockingQueue<Integer>();
	
	private LinkedBlockingQueue<Boolean> resumeSignals = new LinkedBlockingQueue<Boolean>();
	
	/* Flags */
	private volatile boolean frameTimerInterrupted = false;
	private volatile boolean tempUpPressed = false;
	private volatile boolean tempDownPressed = false;
	private volatile boolean powerPressed = false;
	private volatile boolean bitReady = false;
	private boolean frameReady = true;
	private boolean sending = false;
	
	/* DATA */
	private Queue<Boolean> bitBuffer = new LinkedList<Boolean>();
	private boolean[] address = new boolean[10];
	private volatile int bitCounter = 0;
	
	public MCU(String add){
		int pos = 0;
		for(int i = 0; i < 5; i++){
			if(add.charAt(i) == '0'){
				address[pos++] = false;
				address[pos++] = true;
			}else{
				address[pos++] = true;
				address[pos++] = false;
			}
		}
	}
	
	private void addFrame(boolean[] cmd){
		// 2 start bits
		bitBuffer.add(true); bitBuffer.add(false);
		bitBuffer.add(true); bitBuffer.add(false);
		
		// Togle bit (ignored, always 1)
		bitBuffer.add(true); bitBuffer.add(false);
		
		// Address bits
		for(boolean i : address)
			bitBuffer.add(i);
		
		for(boolean i: cmd)
			bitBuffer.add(i);
		
		bitBuffer.add(false);
	}
	
	public void configureButtons(Button up, Button down, Button power){
		this.bTempUp = up;
		this.bTempDown = down;
		this.bPower = power;
	}
	
	public void run(){
		
		intHandler.start();
		irEmitter.turnOff();
		while(true){
			
			try {
				resumeSignals.take();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(frameTimerInterrupted){
				frameTimerInterrupted = false;
				frameReady = true;
				if(bitBuffer.isEmpty())
					sending = false;
				else{
					sending = true;
					bitTimer.initiate(BITTIMERDELAY);
					//bitReady = true;
				}
			}
			
			if(bitReady){
				bitReady = false;
				
				boolean bit = bitBuffer.poll();
				
				if(bit) irEmitter.sendLight();
				else irEmitter.turnOff();
				
				//System.out.println(bit+" "+bitCounter);
				
				bitCounter++;
				
				if(bitCounter >= 29){
					// NextFrame
					frameReady = false;
					frameTimer.initiate(FRAMETIMERDELAY);
					bitCounter = 0;
				}/*else
					bitTimer.initiate(BITTIMERDELAY);*/
			}
			if(tempUpPressed){
				tempUpPressed = false;
				
				/* 010000 */
				boolean[] cmd = new boolean[12];
				// bit 0
				cmd[0] = false;
				cmd[1] = true;
				// bit 1
				cmd[2] = true;
				cmd[3] = false;
				// bit 0
				cmd[4] = false;
				cmd[5] = true;
				// bit 0
				cmd[6] = false;
				cmd[7] = true;
				// bit 0
				cmd[8] = false;
				cmd[9] = true;
				// bit 0
				cmd[10] = false;
				cmd[11] = true;
				
				addFrame(cmd);
				
				if(!sending && frameReady){
					sending = true;
					//bitReady = true;
					bitTimer.initiate(BITTIMERDELAY);
				}
			}
			if(tempDownPressed){
				tempDownPressed = false;
				
				/* 010001 */
				boolean[] cmd = new boolean[12];
				// bit 0
				cmd[0] = false;
				cmd[1] = true;
				// bit 1
				cmd[2] = true;
				cmd[3] = false;
				// bit 0
				cmd[4] = false;
				cmd[5] = true;
				// bit 0
				cmd[6] = false;
				cmd[7] = true;
				// bit 0
				cmd[8] = false;
				cmd[9] = true;
				// bit 1
				cmd[10] = true;
				cmd[11] = false;
				
				addFrame(cmd);
				
				if(!sending && frameReady){
					sending = true;
					//bitReady = true;
					bitTimer.initiate(BITTIMERDELAY);
				}
			}
			if(powerPressed){
				powerPressed = false;
				
				/* 001100 */
				boolean[] cmd = new boolean[12];
				// bit 0
				cmd[0] = false;
				cmd[1] = true;
				// bit 0
				cmd[2] = false;
				cmd[3] = true;
				// bit 1
				cmd[4] = true;
				cmd[5] = false;
				// bit 1
				cmd[6] = true;
				cmd[7] = false;
				// bit 0
				cmd[8] = false;
				cmd[9] = true;
				// bit 0
				cmd[10] = false;
				cmd[11] = true;
				
				addFrame(cmd);
				
				if(!sending && frameReady){
					sending = true;
					//bitReady = true;
					bitTimer.initiate(BITTIMERDELAY);
				}
			}
		}
	}

	@Override
	public void interruptModule(int ioPin) {
		this.interrupts.offer(ioPin);
	}

	private class InterruptHandler extends Thread{
		
		private Thread main;
		
		public InterruptHandler(Thread main){
			this.main = main;
		}
		
		public void run(){
			
			int intPin = 0;
			while(true){
				try {
					intPin = interrupts.take();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				main.suspend();
				
				switch(intPin){
					case TEMPUPPIN:
						tempUpPressed = true;
						resumeSignals.add(true);
						break;
					case TEMPDOWNPIN:
						tempDownPressed = true;
						resumeSignals.add(true);
						break;
					case POWERPIN:
						powerPressed = true;
						resumeSignals.add(true);
						break;
					case BITTIMERPIN:
						bitReady = true;
						resumeSignals.add(true);
						if(bitCounter < 28){
						//bitTimerInterrupted = true;
							bitTimer.initiate(BITTIMERDELAY);
						}
						break;
					case FRAMETIMERPIN:
						resumeSignals.add(true);
						frameTimerInterrupted = true;
						break;
				}
				main.resume();
			}
		}
	}
}
