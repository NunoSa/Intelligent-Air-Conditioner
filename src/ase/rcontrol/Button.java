package ase.rcontrol;

import ase.interfaces.InterruptibleModule;
import ase.utils.Logger;

public class Button {

	private InterruptibleModule module;
	private int ioPort;
	
	public Button(InterruptibleModule module, int ioPort){
		this.module = module;
		this.ioPort = ioPort;
	}
	
	public void pressed()
	{
		Logger.instance().debug("RControl", "Button", "" + ioPort);
		this.module.interruptModule(ioPort);
	}
}
