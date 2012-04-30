package ase.rcontrol;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import ase.utils.Logger;

/**
 * 
 * TSAL6100
 * http://search.digikey.com/scripts/DkSearch/dksus.dll?keywords=TSAL6100
 * $0.55
 * 
 */

public class IREmitter{
	
	//private File file = new File("IR");
	private RandomAccessFile f;
	
	public IREmitter(){
		try {
			File file = new File("IR");
			f = new RandomAccessFile(file, "rwd");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void sendLight(){
		
		try {
			f.write('1');
			f.seek(0);
			
			Logger.instance().debug("IREmitterOUT", "turnOff", "1");
			/*FileOutputStream out = new FileOutputStream(file);
			out.write('1');
			out.close();*/
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void turnOff(){
		try {
			f.write('0');
			f.seek(0);

			Logger.instance().debug("IREmitterOUT", "turnOff", "0");
			/*FileOutputStream out = new FileOutputStream(file);
			out.write('0');
			out.close();*/
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
