package ase.insideunit;

/**
 * 
 * TEFT4300
 * http://search.digikey.com/scripts/DkSearch/dksus.dll?keywords=TEFT4300
 * $0.67
 * 
 */


import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;

public class IRSensor {
	
	private RandomAccessFile f;
	
	public IRSensor(){
		try {
			f = new RandomAccessFile("IR", "r");
		} catch (FileNotFoundException e) {
			// Create file
			try {
				FileWriter fw = new FileWriter("IR");
				fw.write('0');
				fw.close();
				
				f = new RandomAccessFile("IR", "r");
			} catch (IOException e1) {
				System.err.println(e1);
			}
		}
	}
	
	public boolean readValue(){
		char bit = '0';
		try {
			bit = (char) f.read();
			f.seek(0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(bit == '1') return true;
		else return false;
	}
	
}
