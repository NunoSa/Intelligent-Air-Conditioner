package ase.rcontrol;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JLabel;


import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class RControl extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	//private IRsender IRmodule = new IRsender("00001");
	
	private String irAddress = "00001";
	
	private MCU mcu = new MCU(irAddress);
	private Button bTempUp = new Button(mcu, MCU.TEMPUPPIN);
	private Button bTempDown = new Button(mcu, MCU.TEMPDOWNPIN);
	private Button bPower = new Button(mcu, MCU.POWERPIN);
	
	JLabel lblSent = new JLabel("Sent:");

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					RControl frame = new RControl();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public RControl() {
		setTitle("Remote Control");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 208, 227);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton powerButton = new JButton("");
		powerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lblSent.setText("Sent: 111"+irAddress+"001100"); // Standby code
				bPower.pressed();
			}
		});
		powerButton.setToolTipText("Click to turn on/off the device.");
		//powerButton.setIcon(new ImageIcon(RControl.class.getResource("/power.png")));
		powerButton.setBounds(131, 48, 55, 55);
		contentPane.add(powerButton);
		
		JLabel lblTemperature = new JLabel("Temperature");
		lblTemperature.setBounds(23, 20, 79, 16);
		contentPane.add(lblTemperature);
		
		JButton upButton = new JButton("");
		upButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lblSent.setText("Sent: 111"+irAddress+"010000");
				bTempUp.pressed();
			}
		});
		//upButton.setIcon(new ImageIcon(RControl.class.getResource("/up.png")));
		upButton.setBounds(33, 48, 55, 55);
		contentPane.add(upButton);
		
		JButton downButton = new JButton("");
		downButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lblSent.setText("Sent: 111"+irAddress+"010001");
				bTempDown.pressed();
			}
		});
		//downButton.setIcon(new ImageIcon(RControl.class.getResource("/down.png")));
		downButton.setBounds(33, 104, 55, 55);
		contentPane.add(downButton);

		lblSent.setBounds(6, 171, 180, 16);
		contentPane.add(lblSent);
		
		/* Start threads */
		this.mcu.configureButtons(this.bTempUp, this.bTempDown, this.bPower);
		this.mcu.start();
	}
}
