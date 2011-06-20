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
	
	public Myriad_XSN(){
		super ("Myriad XSN");
		setLayout(new BorderLayout());
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		g_board = new JChessBoard();
		
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
						g_board.init();
						repaint();
						// TODO: issue game
				}
			}
		});
		mainMenu.add(game);
		
		setJMenuBar(mainMenu);
		add(new JChessBoard(), BorderLayout.CENTER);
		setResizable(false);
		pack();
		setVisible(true);
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