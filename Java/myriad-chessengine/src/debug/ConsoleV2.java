package debug;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import rules.*;
import tree.*;

@SuppressWarnings("serial")
public class ConsoleV2 extends JFrame{
	static abstract class Routine implements Runnable {
		private ConsoleV2 cs;
		private Position p;
		String [] arg_var;
		public Routine(ConsoleV2 cs, Position p, String... argv){
			this.cs = cs;
			this.p = p;
			arg_var = argv;
		}
		protected abstract void start(Position p, String [] argv) throws Exception;
		public void outputLine(String s){cs.console.append ("<< " + s + "\n");}
		public void output (String s){cs.console.append(s);}
		public void run (){
			try {
				start (p, arg_var);
			} catch (Exception e){
				Object [] d = e.getStackTrace();
				outputLine("For position: " + Utility.saveFEN(p));
				outputLine("Error : " + e.getMessage());
				for (Object r: d) outputLine(r.toString());
				output("\n");
				outputLine("Fatal Error! Routine Terminated!");
			}
		}
	}
	
	JTextArea console = new JTextArea();
	JTextField input = new JTextField();
	Position current = new Position ();
	volatile Thread runningThread;
	
	public ConsoleV2 (){
		super ("Myriad Standalone Utility");
		setSize(500,600);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		input.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e){
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					console.append("\n>> " + input.getText() + "\n\n");
					ConsoleV2.this.consumeInput(input.getText());
					input.setText("");
				}
			}
		});
		console.setFont(new Font ("Consolas", Font.PLAIN, 11));
		console.setWrapStyleWord(true);
		console.setBorder(BorderFactory.createTitledBorder("Output"));
		console.setEditable(false);
		console.setText("Welcome to the Myriad XSN Standalone debug utility. \n"+
				"~~Myriad XSN (c) Spork Innovations~~ \n" +
				"**Utility last updated: 21 Jan. 2012** \n\n" +
				"<< Input 'help' for help menu. \n" +
				"-------------------------------\n");
		input.setFont(new Font ("Consolas", Font.PLAIN, 11));
		input.setBorder(BorderFactory.createTitledBorder("Input"));
		add (new JScrollPane(console), BorderLayout.CENTER);
		add (input, BorderLayout.SOUTH);
		setVisible(true);
	}
	
	public void consumeInput(String input){
		if (input.equals("cls") || input.equals("clear")) console.setText("");
		else if (input.equals("divby0")){
			Routine d = new Routine(this, current){
				protected void start(Position p, String[] argv) throws Exception {
					output("" + 3/0);
				}
			};
			runningThread = new Thread (d);
			runningThread.start();
		} else if (input.equals("takeupabunchoftime")){
			Routine d = new Routine (this, current){
				protected void start(Position p, String[] argv) throws Exception {
					Thread.sleep (3000);
					outputLine("Hi");
				}
			};
			runningThread = new Thread (d);
			runningThread.start();
		} else if (input.equals("help")){
			Routine d = new Routine(this, current){
				protected void start(Position p, String[] argv) throws Exception {
					outputLine (" ~Features implemented so far: ");
					outputLine ("\t clear or cls -> clears the output screen.");
					outputLine ("\t divby0 -> deliberately throw an exception (for debug purposes).");
					outputLine ("\t help -> display this help menu.");
					outputLine ("\t interrupt -> interrupts the current routine and destroys it.");
					outputLine ("\t takeupabunchoftime -> takes up a bunch of time (for debug purposes)");
				}
			};
			runningThread = new Thread (d);
			runningThread.start();
		} else if (input.equals("interrupt")) {
			killActive();
			console.append ("<< Active routine destroyed.\n");
		}
		else console.append("<< Input not recognized. Input 'help' for the help screen.\n");
	}
	
	@SuppressWarnings("deprecation")
	public void killActive (){
		if (runningThread != null) {
			runningThread.stop();
			runningThread = null;
		}
	}
	
	public static void main (String [] argv){
		Zobrist.init();
		SwingUtilities.invokeLater(new Runnable(){
			public void run (){
				new ConsoleV2();
			}
		});
	}
}