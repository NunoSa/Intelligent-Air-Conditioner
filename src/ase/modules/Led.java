package ase.modules;

import java.awt.Color;

import javax.swing.JPanel;

public class Led {
	
	private boolean on = false;
	private JPanel panel;
	
	public synchronized void setOn(){ 
		this.on = true; 
		panel.setBackground(Color.RED);
	}
	public synchronized void setOff(){ 
		this.on = false;
		panel.setBackground(Color.BLACK);
	}
	
	public Led(JPanel panel){
		this.panel = panel;
	}
	
	//DEBUG
	public synchronized boolean isOn(){ return this.on; }
}
