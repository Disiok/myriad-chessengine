package gui;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

import ch.randelshofer.quaqua.QuaquaManager;

@SuppressWarnings("serial")
public class Myriad_XSN extends JFrame{
	// reference to control the main application.
	public static Myriad_XSN Reference;
	public JChessBoard g_board;
	public JTextArea message_pane;
	public JTextArea notation_pane;
	
	public Myriad_XSN(){
		super ("Myriad XSN");
		setLayout(new BorderLayout());
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		g_board = new JChessBoard();
		message_pane = new JTextArea();
		notation_pane = new JTextArea();
		
		//TODO: Do some menu stuff to allow for better UI.
		JMenuBar mainMenu = new JMenuBar();
		JMenu game = new JMenu ("Game");
		game.add(new AbstractAction("New Game"){
			private String [] options = {"White","Black"};
			public void actionPerformed(ActionEvent ae){
				String opt = (String) JOptionPane.showInputDialog(Myriad_XSN.this,
						"Good luck, would you like to play white or black?","New Game?",
						JOptionPane.QUESTION_MESSAGE,null,options,"White");
				if (opt!= null) {
						message_pane.append("Game started, good luck. You play "+opt+".\n");
						if (opt.equals("White")) g_board.init(false);
						else g_board.init(true);
						repaint();
				}
			}
		});
		mainMenu.add(game);
		setJMenuBar(mainMenu);
		
		message_pane.setEditable(false);
		message_pane.setLineWrap(true);
		message_pane.setWrapStyleWord(true);
		message_pane.setBorder(BorderFactory.createTitledBorder("Messages"));
		message_pane.append("Interface loaded. Press Game-> New Game to begin a game.\n");
		JScrollPane mspscr = new JScrollPane(message_pane);
		notation_pane.setEditable(false);
		notation_pane.setLineWrap(true);
		notation_pane.setWrapStyleWord(true);
		notation_pane.setBorder(BorderFactory.createTitledBorder("Notation"));
		JScrollPane ntpscr = new JScrollPane(notation_pane);
		JSplitPane jsp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, mspscr, ntpscr);
		jsp.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		jsp.setPreferredSize(new Dimension(JChessBoard.TOTAL_PIXELS/2,JChessBoard.TOTAL_PIXELS));
		
		add(new JChessBoard(), BorderLayout.CENTER);
		add(jsp, BorderLayout.EAST);
		setResizable(false);
		pack();
		setVisible(true);
		jsp.setDividerLocation(0.5f);
	}
	public static void main (String[] args){
		SwingUtilities.invokeLater(new Runnable(){
			public void run (){
				System.setProperty("Quaqua.tabLayoutPolicy","wrap");
				try { 
					UIManager.setLookAndFeel(QuaquaManager.getLookAndFeel());
					Reference = new Myriad_XSN();
				} catch (Exception e) {
					System.out.println("Issues! Issues!");
					e.printStackTrace();
				}
			}
		});
	}
}