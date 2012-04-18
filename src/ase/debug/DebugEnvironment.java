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
import java.io.FileWriter;
import java.io.IOException;

public class DebugEnvironment {

	private JFrame frame;
	private JTextField textTemperature;
	private boolean movement = false;

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
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 234, 162);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblTemperature = new JLabel("Temperature:");
		lblTemperature.setBounds(6, 20, 91, 16);
		frame.getContentPane().add(lblTemperature);
		
		textTemperature = new JTextField();
		textTemperature.setBounds(93, 14, 61, 28);
		frame.getContentPane().add(textTemperature);
		textTemperature.setColumns(10);
		
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
		btnMovement.setBounds(81, 85, 117, 29);
		frame.getContentPane().add(btnMovement);
		
		JLabel lblMovement = new JLabel("Movement:");
		lblMovement.setBounds(6, 90, 91, 16);
		frame.getContentPane().add(lblMovement);
		
		JButton btnRefresh = new JButton("Refresh");
		btnRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int temp;
				try{
					temp = Integer.valueOf(textTemperature.getText());
				}catch(NumberFormatException e){
					JOptionPane.showMessageDialog(frame, "Wrong number", "Temperature", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				if(temp < -9 || temp > 99){
					JOptionPane.showMessageDialog(frame, "Out of range", "Temperature", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				FileWriter out;
				try {
					out = new FileWriter("TEMP");
					out.write(temp);
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		btnRefresh.setBounds(81, 48, 117, 29);
		frame.getContentPane().add(btnRefresh);
		
		JLabel lblTo = new JLabel("(-9 to 99)");
		lblTo.setBounds(159, 20, 61, 16);
		frame.getContentPane().add(lblTo);
	}
}
