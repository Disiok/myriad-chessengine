package debug;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import rules.*;
import debug.RoutineLibrary.*;
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
		public void newLine (){cs.console.append ("\n<< ");}
		public void output (String s){cs.console.append(s);}
		public void run (){
			try {
				start (p, arg_var);
				cs.console.setCaretPosition(cs.console.getText().length());
			} catch (Exception e){
				Object [] d = e.getStackTrace();
				outputLine("For position: " + Utility.saveFEN(p));
				outputLine("Error : " + e.getMessage());
				for (Object r: d) outputLine(r.toString());
				output("\n");
				outputLine("Fatal Error! Routine Terminated!");
			}
		}
		public ConsoleV2 getConsole(){
			return cs;
		}
		public void displayPosition(){
			String fen = Utility.saveFEN(cs.current);
			String[] fenBoard = fen.split(" ");
			String[] rank = fenBoard[0].split("/");
			boolean onMove = fenBoard[1].charAt(0) == 'w';
			int counter = 0;
			outputLine("  a b c d e f g h");
			output("<< ");
			for (int k = 0; k < rank.length; k++) {
				output((8 - counter) + " ");
				for (int i = 0; i < rank[k].length(); i++) {
					char ch = rank[k].charAt(i);
					if (ch < '9' && ch > '0')
						for (int j = 0; j < (ch - '0'); j++) output ("_ ");
					else output (ch + " ");
				}
				if (k == 7 && onMove) output(" o");
				if (k == 0 && !onMove) output (" o");
				newLine();
				counter++;
			}
			outputLine ("\n");
		}
		public Position concatFen (String[] argv){
			String concat = "";
			for (int i = 1; i < argv.length; i++){
				concat += argv[i] + " ";
			}
			return Utility.loadFEN(concat);
		}
	}
	
	JTextArea console = new JTextArea();
	JTextField input = new JTextField();
	JScrollPane scrollpane = new JScrollPane(console);
	Position current = new Position ();
	String last_input;
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
					last_input = input.getText();
					ConsoleV2.this.consumeInput(last_input);
					input.setText("");
				} else if (e.getKeyCode() == KeyEvent.VK_UP){
					input.setText(last_input);
				}
			}
		});
		console.setFont(new Font ("Consolas", Font.PLAIN, 11));
		console.setWrapStyleWord(true);
		console.setBorder(BorderFactory.createTitledBorder("Output"));
		console.setEditable(false);
		console.setText("Welcome to the Myriad XSN Standalone debug utility. \n"+
				"~~Myriad XSN (c) Spork Innovations~~ \n" +
				"**Utility last updated: 1 Feb. 2012** \n\n" +
				"<< Input 'help' for help menu. \n" +
				"-------------------------------\n");
		input.setFont(new Font ("Consolas", Font.PLAIN, 11));
		input.setBorder(BorderFactory.createTitledBorder("Input"));
		add (scrollpane, BorderLayout.CENTER);
		add (input, BorderLayout.SOUTH);
		setVisible(true);
	}
	
	public void consumeInput(String input){
		String [] id_arg = input.split(" ");
		id_arg[0] = id_arg[0].toLowerCase();
		if (id_arg[0].equals("cls") || id_arg[0].equals("clear")) console.setText("");
		else if (id_arg[0].equals("lorenz")){
			lorenzDescriptor r = new lorenzDescriptor (this, current, id_arg);
			runningThread = new Thread (r);
			runningThread.start();
		} else if (id_arg[0].equals("exposition")){
			setCurrent r = new setCurrent(this, current, id_arg);
			runningThread = new Thread (r);
			runningThread.start();
		} else if (id_arg[0].equals("interrupt")) {
			killActive();
			console.append ("<< Active routine destroyed.\n");
		} else if (id_arg[0].equals("display")){
			display r = new display(this, current, id_arg);
			runningThread = new Thread(r);
			runningThread.start();
		} else if (id_arg[0].equals("help")){
			help r = new help (this, current, id_arg);
			runningThread = new Thread(r);
			runningThread.start();
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