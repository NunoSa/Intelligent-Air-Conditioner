package ase.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Logger
{
	private static Logger logger;
	
	public static Logger instance()
	{
		if (logger == null)
		{
			logger = new Logger();
		}
		
		return logger;
	}
	
	private FileWriter writer;
	
	private Logger()
	{
		try {
			writer = new FileWriter("log");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void debug(String module, String method, String frame)
	{
			long millis = System.currentTimeMillis();
			Date date = new Date(millis);
			DateFormat formatter = new SimpleDateFormat("HH:mm:ss:SSS");
			String dateFormatted = formatter.format(date);
			
			String output = module + "|" + method + "|" + dateFormatted + "|" + frame ;

			logMessage(output);
			
			//System.out.println(output);
/*
		try {
			//writer.append();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
	}
	
	protected void logMessage(String message)
	{
		try {
			Socket requestSocket = new Socket("localhost", 2004);
			
			ObjectOutputStream out = new ObjectOutputStream(requestSocket.getOutputStream());
			out.flush();
			ObjectInputStream in = new ObjectInputStream(requestSocket.getInputStream());
			
			out.writeObject(message);
			out.flush();
			
			in.close();
			out.close();
			requestSocket.close();			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
