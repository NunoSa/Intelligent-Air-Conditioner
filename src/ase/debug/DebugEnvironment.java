package ase.debug;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.InputMethodListener;
import java.awt.event.InputMethodEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;

public class DebugEnvironment {

	private JFrame frame;
	private JTextField textRoomTemp;
	private boolean movement = false;
	private JTextField textAmbTemp;
	private RandomAccessFile f;
	private volatile int ambTemp = 20;
	private TempUpdater tUpdater = new TempUpdater();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DebugEnvironment window = new DebugEnvironment();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public DebugEnvironment() {
		try {
			f = new RandomAccessFile("TEMP", "rwd");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		initialize();
		tUpdater.start();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 321, 138);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblTemperature = new JLabel("Room Temp:");
		lblTemperature.setBounds(6, 20, 91, 16);
		frame.getContentPane().add(lblTemperature);
		
		textRoomTemp = new JTextField();
		textRoomTemp.setBounds(93, 14, 61, 28);
		frame.getContentPane().add(textRoomTemp);
		textRoomTemp.setColumns(10);
		
		final JButton btnMovement = new JButton("Start");
		btnMovement.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				char bit;
				if(movement){
					btnMovement.setText("Start");
					movement = false;
					bit = '0';
				}else{
					btnMovement.setText("Stop");
					movement = true;
					bit = '1';
				}
				FileWriter out;
				try {
					out = new FileWriter("MOV");
					out.write(bit);
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		btnMovement.setBounds(81, 83, 117, 29);
		frame.getContentPane().add(btnMovement);
		
		JLabel lblMovement = new JLabel("Movement:");
		lblMovement.setBounds(6, 88, 91, 16);
		frame.getContentPane().add(lblMovement);
		
		JButton btnRefreshRT = new JButton("Refresh");
		btnRefreshRT.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int temp;
				try{
					temp = Integer.valueOf(textRoomTemp.getText());
				}catch(NumberFormatException e){
					JOptionPane.showMessageDialog(frame, "Wrong number", "Temperature", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				if(temp < -9 || temp > 99){
					JOptionPane.showMessageDialog(frame, "Out of range", "Temperature", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				synchronized(f){
					try {
						f.writeFloat(temp);
						f.seek(0);
					} catch (IOException e) {
						System.err.println("Can not update room temperature!");
					}
				}
			}
		});
		btnRefreshRT.setBounds(227, 19, 85, 21);
		frame.getContentPane().add(btnRefreshRT);
		
		JLabel lblTo = new JLabel("(-9 to 99)");
		lblTo.setBounds(159, 20, 61, 16);
		frame.getContentPane().add(lblTo);
		
		JLabel lblNewLabel = new JLabel("Ambient temp:");
		lblNewLabel.setBounds(6, 49, 101, 16);
		frame.getContentPane().add(lblNewLabel);
		
		textAmbTemp = new JTextField(""+ambTemp);
		textAmbTemp.setBounds(103, 43, 61, 28);
		frame.getContentPane().add(textAmbTemp);
		textAmbTemp.setColumns(10);
		
		JButton btnRefreshAT = new JButton("Refresh");
		btnRefreshAT.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int temp;
				try{
					temp = Integer.valueOf(textAmbTemp.getText());
				}catch(NumberFormatException e){
					JOptionPane.showMessageDialog(frame, "Wrong number", "Temperature", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				if(temp < -9 || temp > 99){
					JOptionPane.showMessageDialog(frame, "Out of range", "Temperature", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				ambTemp = temp;
			}
		});
		btnRefreshAT.setBounds(169, 48, 85, 21);
		frame.getContentPane().add(btnRefreshAT);
	}
	
	private class TempUpdater extends Thread{
		
		private TempUpdater(){
			setDaemon(true);
		}
		
		public void run(){
			
			float actTemp = 0.0F;
			
			while(true){
				
				try {
					sleep(8000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				synchronized(f){
					try {
						actTemp = f.readFloat();
						f.seek(0);
						if(actTemp < ambTemp){
							actTemp += 0.5F;
							f.writeFloat(actTemp);
							f.seek(0);
						}else if(actTemp > ambTemp){
							actTemp -= 0.5F;
							f.writeFloat(actTemp);
							f.seek(0);
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
					}
				}
			}
		}
	}
}
