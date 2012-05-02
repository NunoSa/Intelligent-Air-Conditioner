package ase.outsideunit;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;

import ase.utils.ADCPin;
import ase.utils.MAX485;
import ase.utils.Pin;

public class Outside {

	public JFrame frmOutsideUnit;
	private JLabel lblFrame = new JLabel("Recv: ");
	private JLabel lblSpeed = new JLabel("Speed: ");
	
	private MCU mcu = new MCU(lblFrame);
	
	/* Pins */
	private Pin RXDPin = new Pin(mcu, MCU.RXDPIN);
	private Pin TXDPin = new Pin(mcu, MCU.TXDPIN);
	private ADCPin Compressor_MCU_Pin = new ADCPin(mcu, -1);
	
	/* Modules */
	private Compressor compressor = new Compressor(Compressor_MCU_Pin, lblSpeed);
	private MAX485 max485 = new MAX485(TXDPin, RXDPin, MAX485.SLAVEMODE);

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Outside window = new Outside();
					window.frmOutsideUnit.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Outside() {
		initialize();
		mcu.configurePins(TXDPin, RXDPin, Compressor_MCU_Pin);
		mcu.start();
		max485.start();
		compressor.start();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmOutsideUnit = new JFrame();
		frmOutsideUnit.setTitle("Outside Unit");
		frmOutsideUnit.setBounds(100, 100, 324, 109);
		frmOutsideUnit.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmOutsideUnit.getContentPane().setLayout(null);
		
		JLabel lblInside = new JLabel("Inside");
		lblInside.setBounds(24, 21, 61, 16);
		frmOutsideUnit.getContentPane().add(lblInside);
		
		lblFrame.setBounds(6, 49, 143, 16);
		frmOutsideUnit.getContentPane().add(lblFrame);
		
		JLabel lblCompressor = new JLabel("Compressor");
		lblCompressor.setBounds(190, 21, 97, 16);
		frmOutsideUnit.getContentPane().add(lblCompressor);

		lblSpeed.setBounds(161, 49, 126, 16);
		frmOutsideUnit.getContentPane().add(lblSpeed);
	}

}
