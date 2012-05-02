package ase.modules;

import java.awt.Color;

import javax.swing.JPanel;

public class Led {
	
	private boolean on = false;
	private JPanel panel;
	private Color color;
	
	public synchronized void setOn(){ 
		this.on = true; 
		panel.setBackground(color);
	}
	public synchronized void setOff(){ 
		this.on = false;
		panel.setBackground(Color.BLACK);
	}
	
	public Led(JPanel panel, Color c){
		this.panel = panel;
		this.color = c;
		
	}
	
	//DEBUG
	public synchronized boolean isOn(){ return this.on; }
}
