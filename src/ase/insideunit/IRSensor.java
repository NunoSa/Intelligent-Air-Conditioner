package ase.insideunit;

/**
 * 
 * TEFT4300
 * http://search.digikey.com/scripts/DkSearch/dksus.dll?keywords=TEFT4300
 * $0.67
 * 
 */


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;

public class IRSensor {
	
	private RandomAccessFile f;
	
	public IRSensor(){
		try {
			File file = new File("IR");
			f = new RandomAccessFile(file, "rwd");
			System.out.println(file.getAbsolutePath());
		} catch (FileNotFoundException e) {
			// Ignore
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
