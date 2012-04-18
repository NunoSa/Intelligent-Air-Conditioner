package ase.rcontrol;

import ase.interfaces.InterruptibleModule;

public class Button {

	private InterruptibleModule module;
	private int ioPort;
	
	public Button(InterruptibleModule module, int ioPort){
		this.module = module;
		this.ioPort = ioPort;
	}
	
	public void pressed(){
		this.module.interruptModule(ioPort);
	}
}
