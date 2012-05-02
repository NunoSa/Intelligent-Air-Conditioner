import java.awt.EventQueue;

import ase.debug.DebugEnvironment;
import ase.insideunit.Inside;
import ase.outsideunit.Outside;
import ase.rcontrol.RControl;


public class Main {

	public static void main(String[] args) {
		
		// DebugEnvironment
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
		
		// InsideUnit
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
		
		// OutsideUnit
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
		
		// RControl
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
}
