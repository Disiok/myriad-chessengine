package gui;

import javax.swing.*;

@SuppressWarnings("serial")
public class Myriad_XSN extends JFrame{
	// creates event dispatch
	public static void main (String[] args){
		SwingUtilities.invokeLater(new Runnable(){
			public void run (){
				// TODO: set-up look and feel stuff
				new Myriad_XSN();
			}
		});
	}
	// initialises window constants.
	public Myriad_XSN(){
		//TODO: Write GUI.
		super ("Myriad XSN");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setSize(750,750);
		setVisible(true);
	}
}
