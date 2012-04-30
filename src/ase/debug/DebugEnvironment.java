package ase.debug;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.text.DefaultCaret;

import java.awt.event.InputMethodListener;
import java.awt.event.InputMethodEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.net.ServerSocket;
import java.net.Socket;

public class DebugEnvironment {

	private boolean movement = false;
	private RandomAccessFile f;
	private volatile int ambTemp = 20;
	private TempUpdater tUpdater = new TempUpdater();

	private ServerSocket providerSocket;
	
	
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
		
		try {
			providerSocket = new ServerSocket(2004);
			System.out.println("Started Server Socket on Port 2004");
			
			new SocketThreadPool(providerSocket).start();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Initializing Debug Window");
		
		initialize();
		tUpdater.start();
	}
	
	private class SocketThreadPool extends Thread
	{
		ServerSocket providerSocket;
		
		public SocketThreadPool(ServerSocket providerSocket)
		{
			this.providerSocket = providerSocket;
		}
		
		@Override
		public void run()
		{
			do
			{
				try {
					System.out.println("Listening for Clients");
					Socket connection = providerSocket.accept();
					System.out.println("New Client Accepted");
					new SocketThread(connection).start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} while (true);
		}
	}
	
	private class SocketThread extends Thread
	{
		private Socket socket;
		private ObjectOutputStream out;
		private ObjectInputStream in;

		public SocketThread(Socket socket)
		{
			this.socket = socket;
			System.out.println("Socket Open");
		}
		
		public void run()
		{
			System.out.println("Running");

			try {
				out = new ObjectOutputStream(socket.getOutputStream());
				out.flush();
				in = new ObjectInputStream(socket.getInputStream());

				String message = (String) in.readObject();
				
				System.out.println(message);
				
				// process message, and update accordingly
				String[] parts = message.split("\\|");
				
				if (parts[0].equals("IREmitterOUT"))
				{
					textRemoteControlOut.append(parts[1], parts[3]);					
				}
			
				if (parts[0].equals("IREmitterIN"))
				{
					textRemoteControlIn.append(parts[1], parts[3]);					
				}

				if (parts[0].equals("InsideUnitOUT"))
				{
					textRemoteControlOut.append(parts[1], parts[3]);					
				}
			
				if (parts[0].equals("InsideUnitIN"))
				{
					textRemoteControlIn.append(parts[1], parts[3]);					
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} finally {
				try{
					in.close();
					out.close();
					//providerSocket.close();
				} catch(IOException ioException) {
					ioException.printStackTrace();
				}
			}

			System.out.println("Finish");
		}
	}

	JFrame frame;	
	JTextField textRoomTemp;
	JTextField textAmbTemp;

	DebugTextArea textRemoteControlIn;
	DebugTextArea textRemoteControlOut; 
	
	DebugTextArea textInsideUnitIn;
	DebugTextArea textInsideUnitOut; 
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize()
	{
		frame = new JFrame();
		frame.setBounds(100, 100, 1000, 600);
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

		// Remote Control
		JLabel labelRC = new JLabel("Remote Control");
		labelRC.setBounds(20, 120, 200, 20);
		frame.getContentPane().add(labelRC);
		
		JLabel labelRCIn = new JLabel("In");
		labelRCIn.setBounds(20, 140, 200, 20);
		frame.getContentPane().add(labelRCIn);

		textRemoteControlIn = new DebugTextArea();
		textRemoteControlIn.setBounds(20, 160, 200, 100);
		frame.getContentPane().add(textRemoteControlIn);

		JLabel labelRCOut = new JLabel("Out");
		labelRCOut.setBounds(20, 260, 200, 20);
		frame.getContentPane().add(labelRCOut);

		textRemoteControlOut = new DebugTextArea();
		textRemoteControlOut.setBounds(20, 280, 200, 100);
		frame.getContentPane().add(textRemoteControlOut);

		// Inside Unit
		JLabel labelIU = new JLabel("Inside Unit");
		labelIU.setBounds(220, 120, 200, 20);
		frame.getContentPane().add(labelIU);
		
		JLabel labelIUIn = new JLabel("In");
		labelIUIn.setBounds(220, 140, 200, 20);
		frame.getContentPane().add(labelIUIn);

		textInsideUnitIn = new DebugTextArea();
		textInsideUnitIn.setBounds(220, 160, 200, 100);
		frame.getContentPane().add(textInsideUnitIn);

		JLabel labelIUOut = new JLabel("Out");
		labelIUOut.setBounds(220, 260, 200, 20);
		frame.getContentPane().add(labelIUOut);

		textInsideUnitOut = new DebugTextArea();
		textInsideUnitOut.setBounds(220, 280, 200, 100);
		frame.getContentPane().add(textInsideUnitOut);

	}
	
	private class DebugTextArea extends JScrollPane
	{
		private int length;
		private int lengthExtra;
		private JTextArea textArea;		

		public DebugTextArea()
		{
			super();

			textArea = new JTextArea();
			((DefaultCaret) textArea.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
			
			
			setViewportView(textArea);
			setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		}
		
		public void append(String origin, String str)
		{
			length += str.length();
			lengthExtra += str.length();
			
			String buffer = "";
			
			// breaks frames in parts with 8 bits
			for(final String token : str.split("(?<=\\G.{10})"))
			{
			    buffer += token + "\n";
			}
			
			//str = str + "\n";
			
			textArea.append(str);
			
			if (lengthExtra == 8)
			{
				textArea.append(" ");
			}

			if (lengthExtra >= 16)
			{
				lengthExtra = 0;
				textArea.append("\n");
			}
		}
	}
	
	private class TempUpdater extends Thread
	{	
		private TempUpdater()
		{
			setDaemon(true);
		}
		
		public void run(){
			
			float actTemp = 0.0F;
			
			while(true){
				
				try {
					sleep(10000);
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
