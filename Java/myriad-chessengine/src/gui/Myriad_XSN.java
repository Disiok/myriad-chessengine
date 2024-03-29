package gui;

import java.awt.*;
import javax.swing.*;
import rules.*;
import tables.*;
import debug.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

@SuppressWarnings("serial")
public class Myriad_XSN extends JFrame {
	public static Myriad_XSN Reference;
	public JChessBoard g_board;
	public JTextArea message_pane;
	public JTextArea notation_pane;
	public String playerName = "Player";
	public static Random rdm = new Random();

	public Myriad_XSN() {
		super("Myriad XSN");
		setLayout(new BorderLayout());
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		g_board = new JChessBoard();
		message_pane = new JTextArea();
		notation_pane = new JTextArea();
		message_pane.setFont(new Font("Consolas", Font.PLAIN, 13));
		notation_pane.setFont(new Font("Consolas", Font.PLAIN, 13));
		JMenuBar mainMenu = new JMenuBar();
		JMenu game = new JMenu("Game");
		game.add(new AbstractAction("New Game") {
			private String[] options = { "PVP", "White Player", "Black Player" };

			public void actionPerformed(ActionEvent ae) {
				String opt = (String) JOptionPane.showInputDialog(
						Myriad_XSN.this,
						"Good luck, would you like to play white or black?",
						"New Game?", JOptionPane.QUESTION_MESSAGE, null,
						options, "White");
				if (opt != null) {
					notation_pane.setText("");
					message_pane.append("Game started, good luck! "
							+ playerName + ". You play " + opt + ".\n");
					if (opt.equals("White Player")) {
						g_board.init(false, false);
						notation_pane.append(playerName
								+ " vs. Myriad XSN\n-----------\n");
					} else if(opt.equals("Black Player")){
						g_board.init(true, false);
						notation_pane.append("Myriad XSN vs. " + playerName
								+ "\n-----------\n");
					}else{
						g_board.init(false, true);
						notation_pane.append(playerName + " vs. " + playerName
								+ "\n-----------\n");
					}
					repaint();
				}
			}
		});
		game.add(new AbstractAction("Takeback") {
			public String[] messages = {
					"Here comes Spinzaku with the takeback! You can never challenge Spinzaku!",
					"Myriad's power level is OVER 9000! You definitely need a takeback!",
					"The cake was a lie!!! That piece wasnt there 5 seconds ago!",
					"Ooh Pie! *eats Pie* Hey, this position isn't the same as before?!?" };

			public void actionPerformed(ActionEvent ae) {
				if (g_board.getEmbeddedPosition() != null) {
					g_board.takeBack();
					repaint();
					JOptionPane.showMessageDialog(Myriad_XSN.this,
							messages[rdm.nextInt(messages.length)],
							"You took a takeback?!?",
							JOptionPane.WARNING_MESSAGE, null);
				}
			}
		});
		JMenu options = new JMenu("Options");
		options.add(new AbstractAction("Player Name") {
			public void actionPerformed(ActionEvent ae) {
				String name = (String) JOptionPane.showInputDialog(
						Myriad_XSN.this, "What is your name?", "Your name?",
						JOptionPane.QUESTION_MESSAGE, null, null, playerName);
				if (name != null) {
					playerName = name;
					message_pane.append("Name successfully changed to " + name
							+ ".\n");
				}
			}
		});
		JMenu about = new JMenu("About");
		about.add(new AbstractAction("About") {
			public void actionPerformed(ActionEvent ae) {
				JOptionPane
						.showMessageDialog(
								Myriad_XSN.this,
								"Myriad XSN - From Victoria Park CI's Software Design Team a.k.a. Spork "
										+ "Innovations!\n"
										+ "Special thanks to Victoria Park graduate David Jeong!\n"
										+ "Also, credit goes to TinyLAF for the incredible look and feel graphics!",
								"About", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		JMenu debug = new JMenu("Debug");
		final String[] files = { "Slot1", "Slot2", "Slot3", "Slot4", "Slot5" };
		final String[] depth = {"1","2","3","4","5","6","7"};
		debug.add(new AbstractAction("Clear Savefiles") {
			public void actionPerformed(ActionEvent ae) {
				for (String f : files) {
					String file = "save/" + f + ".txt";
					try {
						Utility.write(file, "");
					} catch (IOException ios) {
						JOptionPane.showMessageDialog(Myriad_XSN.this,
								"Error clearing savefiles", "Oh snap!",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		debug.add(new AbstractAction("Save Game") {
			public void actionPerformed(ActionEvent ae) {
				Position p = g_board.getEmbeddedPosition();
				if (p != null) {
					String save = (String) JOptionPane.showInputDialog(
							Myriad_XSN.this,
							"Please choose the savefile to save game",
							"Save Game?", JOptionPane.QUESTION_MESSAGE, null,
							files, "Slot1");
					if (save != null) {
						save = "save/" + save + ".txt";
						try {
							Utility.write(save, g_board.getFENPlus());
						} catch (IOException ios) {
							JOptionPane.showMessageDialog(Myriad_XSN.this,
									"Error saving game!", "Oh snap!",
									JOptionPane.ERROR_MESSAGE, null);
						}
					}
				} else
					JOptionPane.showMessageDialog(Myriad_XSN.this,
							"Game has not been started. "
									+ "Please start or load a game.",
							"Oh snap!", JOptionPane.WARNING_MESSAGE, null);
			}
		});
		debug.add(new AbstractAction("Load Game") {
			public void actionPerformed(ActionEvent ae) {
				String load = (String) JOptionPane.showInputDialog(
						Myriad_XSN.this,
						"Please choose the savefile to load the game from.",
						"Load Game?", JOptionPane.QUESTION_MESSAGE, null,
						files, "Slot1");
				if (load != null) {
					load = "save/" + load + ".txt";
					try {
						String FENPlus = Utility.read(load);
						if (FENPlus != null) {
							message_pane
									.append("Game has been succesfully loaded.\n");
							notation_pane.setText("");
							appendStart(FENPlus.contains("true"));
							g_board.init(FENPlus);
						} else
							JOptionPane
									.showMessageDialog(
											Myriad_XSN.this,
											"The savefile is empty. Please try another save file.",
											"Oh snap!",
											JOptionPane.ERROR_MESSAGE, null);
					} catch (IOException ios) {
						JOptionPane
								.showMessageDialog(
										Myriad_XSN.this,
										"The save file does not exist or is corrupted.",
										"Oh snap!", JOptionPane.ERROR_MESSAGE,
										null);
					}
				}
			}
		});
		debug.add(new AbstractAction("Load FEN"){
            public void actionPerformed(ActionEvent ae){
                    String load = (String) JOptionPane.showInputDialog(Myriad_XSN.this,
                                    "Please input FEN to load game","Load Game?", JOptionPane.QUESTION_MESSAGE,
                                    null, null, null);
                    if (load != null){
                            g_board.init(load,false);
                            message_pane.append("Game has been succesfully loaded");
                    }
            }
    });
		debug.add(new AbstractAction("Set Depth") {
			public void actionPerformed(ActionEvent ae) {
				String load = (String) JOptionPane.showInputDialog(
						Myriad_XSN.this,
						"Please choose the depth for NegaMax.",
						"Depth?", JOptionPane.QUESTION_MESSAGE, null,
						depth, "4");
				if (load != null) {
					int d = Integer.parseInt(load);
					g_board.setDepth(d);
				}
			}
		});
		mainMenu.add(debug);
		mainMenu.add(about);
		mainMenu.add(game);
		mainMenu.add(options);
		setJMenuBar(mainMenu);

		message_pane.setEditable(false);
		message_pane.setLineWrap(true);
		message_pane.setWrapStyleWord(true);
		message_pane.setBorder(BorderFactory.createTitledBorder("Messages"));
		message_pane
				.append("Interface loaded. Press Game-> New Game to begin a game.\n");
		JScrollPane mspscr = new JScrollPane(message_pane);
		notation_pane.setEditable(false);
		notation_pane.setLineWrap(true);
		notation_pane.setWrapStyleWord(true);
		notation_pane.setBorder(BorderFactory.createTitledBorder("Notation"));
		JScrollPane ntpscr = new JScrollPane(notation_pane);
		JSplitPane jsp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, mspscr,
				ntpscr);
		jsp.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		jsp.setPreferredSize(new Dimension(JChessBoard.TOTAL_PIXELS / 2,
				JChessBoard.TOTAL_PIXELS));

		add(new JChessBoard(), BorderLayout.CENTER);
		add(jsp, BorderLayout.EAST);
		setResizable(false);
		pack();
		setVisible(true);
		jsp.setDividerLocation(0.5f);
	}

	public void appendStart(boolean ai_colour) {
		if (ai_colour)
			notation_pane.append("Myriad XSN vs. " + playerName
					+ "\n-----------\n");
		else
			notation_pane.append(playerName + " vs. Myriad XSN\n-----------\n");
	}

	public static void main(String[] args) {
		Zobrist.init();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Toolkit.getDefaultToolkit().setDynamicLayout(true);
				System.setProperty("sun.awt.noerasebackground", "true");
				JFrame.setDefaultLookAndFeelDecorated(true);
				JDialog.setDefaultLookAndFeelDecorated(true);
				try {
					UIManager
							.setLookAndFeel("de.muntjak.tinylookandfeel.TinyLookAndFeel");
					Reference = new Myriad_XSN();
				} catch (Exception e) {
					System.out.println("Issues! Issues!");
					e.printStackTrace();
				}
			}
		});
	}
}