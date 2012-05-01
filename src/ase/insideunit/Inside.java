package ase.insideunit;

import java.awt.EventQueue;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.TimeUnit;

import javax.swing.JLabel;

import ase.modules.Led;
import ase.utils.ADCPin;
import ase.utils.MAX485;
import ase.utils.Pin;

public class Inside{
	
	private JFrame frmInsideUnit;
	
	/* Led Panels */
	private final JPanel haPanel = new JPanel();
	private final JPanel hgPanel = new JPanel();
	private final JPanel hdPanel = new JPanel();
	private final JPanel hbPanel = new JPanel();
	private final JPanel hfPanel = new JPanel();
	private final JPanel hePanel = new JPanel();
	private final JPanel hcPanel = new JPanel();
	private final JPanel laPanel = new JPanel();
	private final JPanel lgPanel = new JPanel();
	private final JPanel ldPanel = new JPanel();	
	private final JPanel lbPanel = new JPanel();
	private final JPanel lfPanel = new JPanel();
	private final JPanel lePanel = new JPanel();
	private final JPanel lcPanel = new JPanel();
	private final JPanel intPanel = new JPanel();
	private final JLabel lblRemote = new JLabel("Remote:");
	private final JLabel lblRemoteFrame = new JLabel("Frame:");
	private final JLabel lblRemoteTemp = new JLabel("Temp:");
	private JLabel lblOutsideFrame = new JLabel("Sent:");
	
	private CPU cpuModule = new CPU(this, lblRemoteFrame, lblRemoteTemp, lblOutsideFrame, 1);
	
	/* Pins */
	private Pin Ir_MCU_Pin = new Pin(cpuModule, CPU.IRPIN);
	private ADCPin Temp_MCU_Pin = new ADCPin(cpuModule, CPU.TEMPSENSORPIN);
	private Pin PIR_MCU_Pin = new Pin(cpuModule, CPU.PIRPIN);
	private Pin RXDPin = new Pin(cpuModule, CPU.RXDPIN);
	private Pin TXDPin = new Pin(cpuModule, CPU.TXDPIN); 
	//private Pin INTMODEPin = new Pin(cpuModule, CPU.INTMODEPIN);
	
	/* Modules */
	private IRReceiver ir = new IRReceiver(Ir_MCU_Pin);
	private TempSensor tSensor = new TempSensor(Temp_MCU_Pin);
	private PIR pir = new PIR(PIR_MCU_Pin);
	private MAX485 max485 = new MAX485(TXDPin, RXDPin, MAX485.MASTERMODE);
	
	/* Leds */
	private Led ha = new Led(haPanel);
	private Led hb = new Led(hbPanel); 
	private Led hc = new Led(hcPanel);
	private Led hd = new Led(hdPanel); 
	private Led he = new Led(hePanel);
	private Led hf = new Led(hfPanel);
	private Led hg = new Led(hgPanel);
	private Led la = new Led(laPanel);
	private Led lb = new Led(lbPanel);
	private Led lc = new Led(lcPanel);
	private Led ld = new Led(ldPanel);
	private Led le = new Led(lePanel);
	private Led lf = new Led(lfPanel);
	private Led lg = new Led(lgPanel);
	private Led lint = new Led(intPanel);

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Inside window = new Inside();
					window.frmInsideUnit.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Inside() {
		initialize();
		
		/* Start threads */
		this.cpuModule.configurePins(Ir_MCU_Pin, Temp_MCU_Pin, PIR_MCU_Pin, TXDPin, RXDPin);
		this.cpuModule.configureLeds(ha, hb, hc, hd, he, hf, hg, la, lb, lc, ld, le, lf, lg, lint);
		this.cpuModule.start();
		this.ir.start();
		
		this.tSensor.start();
		this.pir.start();
		//this.max485.start();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmInsideUnit = new JFrame();
		frmInsideUnit.setTitle("Inside Unit");
		frmInsideUnit.setBounds(100, 100, 280, 336);
		frmInsideUnit.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmInsideUnit.getContentPane().setLayout(null);
		
		haPanel.setBounds(16, 34, 46, 10);
		haPanel.setBackground(Color.BLACK);
		frmInsideUnit.getContentPane().add(haPanel);
		
		hgPanel.setBounds(16, 81, 46, 10);
		hgPanel.setBackground(Color.BLACK);
		frmInsideUnit.getContentPane().add(hgPanel);
		
		hdPanel.setBounds(16, 128, 46, 10);
		hdPanel.setBackground(Color.BLACK);
		frmInsideUnit.getContentPane().add(hdPanel);
		
		hbPanel.setBounds(62, 44, 10, 37);
		hbPanel.setBackground(Color.BLACK);
		frmInsideUnit.getContentPane().add(hbPanel);
		
		hfPanel.setBounds(6, 44, 10, 37);
		hfPanel.setBackground(Color.BLACK);
		frmInsideUnit.getContentPane().add(hfPanel);
		
		hePanel.setBackground(Color.BLACK);
		hePanel.setBounds(6, 91, 10, 37);
		frmInsideUnit.getContentPane().add(hePanel);
		
		hcPanel.setBackground(Color.BLACK);
		hcPanel.setBounds(62, 91, 10, 37);
		frmInsideUnit.getContentPane().add(hcPanel);
		
		laPanel.setBackground(Color.BLACK);
		laPanel.setBounds(94, 34, 46, 10);
		frmInsideUnit.getContentPane().add(laPanel);
		
		lgPanel.setBackground(Color.BLACK);
		lgPanel.setBounds(94, 81, 46, 10);
		frmInsideUnit.getContentPane().add(lgPanel);
		
		ldPanel.setBackground(Color.BLACK);
		ldPanel.setBounds(94, 128, 46, 10);
		frmInsideUnit.getContentPane().add(ldPanel);
		
		lbPanel.setBackground(Color.BLACK);
		lbPanel.setBounds(140, 44, 10, 37);
		frmInsideUnit.getContentPane().add(lbPanel);
		
		lfPanel.setBackground(Color.BLACK);
		lfPanel.setBounds(84, 44, 10, 37);
		frmInsideUnit.getContentPane().add(lfPanel);
		
		lePanel.setBackground(Color.BLACK);
		lePanel.setBounds(84, 91, 10, 37);
		frmInsideUnit.getContentPane().add(lePanel);
		
		lcPanel.setBackground(Color.BLACK);
		lcPanel.setBounds(140, 91, 10, 37);
		frmInsideUnit.getContentPane().add(lcPanel);
		
		JLabel lblTemperature = new JLabel("Temperature");
		lblTemperature.setBounds(42, 6, 79, 16);
		frmInsideUnit.getContentPane().add(lblTemperature);
		
		intPanel.setBackground(Color.BLACK);
		intPanel.setBounds(201, 10, 10, 10);
		frmInsideUnit.getContentPane().add(intPanel);
		
		JLabel lblInt = new JLabel("Int");
		lblInt.setBounds(178, 6, 17, 16);
		frmInsideUnit.getContentPane().add(lblInt);
		lblRemote.setBounds(16, 163, 100, 16);
		
		frmInsideUnit.getContentPane().add(lblRemote);
		lblRemoteFrame.setBounds(6, 182, 158, 16);
		
		frmInsideUnit.getContentPane().add(lblRemoteFrame);
		lblRemoteTemp.setBounds(6, 200, 100, 16);
		
		frmInsideUnit.getContentPane().add(lblRemoteTemp);
		
		JLabel lblOutside = new JLabel("Outside:");
		lblOutside.setBounds(16, 228, 100, 16);
		frmInsideUnit.getContentPane().add(lblOutside);
		
		lblOutsideFrame.setBounds(6, 251, 158, 16);
		frmInsideUnit.getContentPane().add(lblOutsideFrame);
		
		final JLabel lblClock = new JLabel("");
		lblClock.setBounds(180, 260, 100, 16);
		frmInsideUnit.getContentPane().add(lblClock);

		final JButton buttonPlusMinute = new JButton("+1M");
		buttonPlusMinute.setBounds(180, 240, 70, 16);
		frmInsideUnit.getContentPane().add(buttonPlusMinute);
		buttonPlusMinute.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				cpuModule.addSeconds(60);
			}
		});
		
		final JButton buttonPlusHour = new JButton("+1H");
		buttonPlusHour.setBounds(180, 220, 70, 16);
		frmInsideUnit.getContentPane().add(buttonPlusHour);
		buttonPlusHour.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				cpuModule.addSeconds(3600);
			}
		});

		
		// refresh clock label
		Timer timerlblClock = new Timer(1000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				int totalSeconds = cpuModule.readSeconds();
				int hours = totalSeconds / 3600;
				int remainder = totalSeconds % 3600;
				int minutes = remainder / 60;
				int seconds = remainder % 60;

				
				lblClock.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
			}
		});
		timerlblClock.start();
	}
	
	public void PowerOn(){
		max485.start();
		pir.resume();
		tSensor.resume();
	}
	
	public void PowerOff(){

		pir.suspend();
		tSensor.suspend();
		max485.stop();
	}
}
