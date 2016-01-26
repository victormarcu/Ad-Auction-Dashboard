package ui;

import java.util.Observable;
import java.util.Observer;

import javax.swing.JComponent;

import core.Model;

public class Controls extends JComponent implements Observer {

	// ==== Constants ====
	
	private static final long serialVersionUID = 824395947852730145L;
	
	// ==== Properties ====
	
	private final Model model;
	
	// ==== Constructor ====
	
	public Controls(Model model) {
		
		this.model = model;
	}
	
	// ==== Observer Implementation ====

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		
	}

}