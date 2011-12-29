package gui;

import javax.swing.*;
import tree.*;
import rules.*;
import java.awt.*;
import java.awt.event.*;
import debug.*;

@SuppressWarnings("serial")
/**
 * This is the chess board "component" for the Myriad software. It displays the chess-board for user
 * input and output. This class, allows for communication between the human
 * and the computer.
 * @author Jesse Wang, Karl Zhang
 */
public class JChessBoard extends JPanel {
	// ----------------------Fields----------------------
	/**
	 * The position that <i>this</i> JChessBoard object contains. It is the
	 * "master" and official board.
	 */
	private static Position p;
	private static Move prior_move = null;
	private static int depth = 3;
	private static Pine tree;
	private static boolean ai_turn;
	/**
	 * The anchor for the start square of a user's move.
	 */
	private static byte clicked_square = -1;
	/**
	 * The engine that the JChessBoard is using.
	 */
	// private static Myriad engine = new Myriad();
	/**
	 * The colour that Myriad is, true for white, false for black.
	 */
	private static boolean ai_colour;
	/**
	 * The number of full moves that have been made.
	 */
	private static boolean PVP = false;
	private static int moveNumber = 1;
	/**
	 * 
	 */
	private static String moveList;
	// ----------------------End of Fields----------------------
	// ----------------------Constants----------------------
	/**
	 * The number of pixels per square. Used for painting purposes.
	 */
	public static final int PIXELS_PER_SQUARE = 60;
	/**
	 * The fixed size of the board. Used for painting purposes.
	 */
	public static final int TOTAL_PIXELS = 8 * PIXELS_PER_SQUARE;

	// ----------------------End of Constants----------------------
	// ----------------------Constructor----------------------
	/**
	 * Constructs a JChessBoard object. The position is not yet initialised! The
	 * human must initialise it by getting the program to invoke one of the
	 * init() methods below. The constructor initialises the JPanel and adds the
	 * appropriate mouseAdapter for the two point click system used in Myriad.
	 */
	public JChessBoard() {
		super();
		setPreferredSize(new Dimension(8 * PIXELS_PER_SQUARE,
				8 * PIXELS_PER_SQUARE));
		setOpaque(true);
		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent me) {
				if (p != null && (p.isWhiteToMove() != ai_colour || PVP)) {
					int y = me.getY() / PIXELS_PER_SQUARE;
					int x = me.getX() / PIXELS_PER_SQUARE;
					if (clicked_square == -1) {
						clicked_square = (byte) (ai_colour ? y * 0x10 + (7 - x)
								: (7 - y) * 0x10 + x);
						Piece q = p.getSquareOccupier(clicked_square);
						if (q.getColour() != (p.isWhiteToMove() ? Piece.WHITE
								: Piece.BLACK))
							clicked_square = -1;
						repaint();
					} else {
						final byte end_square = (byte) (ai_colour ? y * 0x10
								+ x + 7 : (7 - y) * 0x10 + x);
						if (end_square == clicked_square) {
							clicked_square = -1;
							repaint();
							return;
						}
						Piece s = p.getSquareOccupier(clicked_square);
						Piece e = p.getSquareOccupier(end_square);
						if (s.getType() == Piece.PAWN && !e.exists() && (end_square - clicked_square) % 0x10 != 0){
							registerHumanMove(new Move(clicked_square,
									(byte)(end_square + (p.isWhiteToMove()? -0x10: 0x10)), (byte) 5));
						} else if ((s.getType() == Piece.PAWN && !e.exists()
								&& (end_square - clicked_square) == 0x20 || (clicked_square - end_square) == 0x20)) {
							registerHumanMove(new Move(clicked_square,
									end_square, (byte) 10));
						} else if (s.getType() == Piece.KING
								&& (s.getPosition() == 0x04 || s.getPosition() == 0x74)) {
							if (s.getColour() == Piece.WHITE) {
								if (end_square == 0x02)
									registerHumanMove(Move.CASTLE[2]);
								else if (end_square == 0x06)
									registerHumanMove(Move.CASTLE[0]);
								else
									registerHumanMove(new Move(clicked_square,
											end_square));
							} else {
								if (end_square == 0x72)
									registerHumanMove(Move.CASTLE[3]);
								else if (end_square == 0x76)
									registerHumanMove(Move.CASTLE[1]);
								else
									registerHumanMove(new Move(clicked_square,
											end_square));
							}
						} else if (s.getType() == Piece.PAWN
								&& (end_square / 0x10 == 0x00 || end_square / 0x10 == 0x07)) {
							final JDialog jd = new JDialog();
							jd.setTitle("Promotion! Choose a piece to promote your pawn to:");
							jd.setModal(true);
							jd.setLocationRelativeTo(JChessBoard.this);
							jd.setSize(400, 110);
							jd.setLayout(new FlowLayout());
							for (int i = 0; i < 4; i++) {
								final JButton toAdd = new JButton();
								Image ico = ImageUtility
										.getPieceImage(new Piece((byte) 0,
												(byte) (1 + i),
												p.isWhiteToMove() ? Piece.WHITE
														: Piece.BLACK));
								toAdd.setName("" + i);
								toAdd.setIcon(new ImageIcon(ico));
								toAdd.addActionListener(new ActionListener() {
									public void actionPerformed(ActionEvent ae) {
										int promotion_code = Integer
												.parseInt(toAdd.getName());
										registerHumanMove(new Move(
												clicked_square, end_square,
												(byte) (promotion_code + 6)));
										jd.dispose();
									}
								});
								jd.setResizable(false);
								jd.add(toAdd);
							}
							jd.setVisible(true);
						} else
							registerHumanMove(new Move(clicked_square,
									end_square));
						if (ai_turn) {
							 tree.setCurrentLeaf(p, prior_move);
                             tree.NegaMax(p, prior_move, depth);
                             registerAIMove(tree.getBestMove());
							ai_turn = false;
						}
					}
				}
			}
		});
	}

	// ----------------------End of Constructors----------------------
	// ----------------------Methods----------------------
	/**
	 * Initialises the board from the starting position.
	 * 
	 * @param aiColour
	 *            The colour that the engine is playing, true for white, false
	 *            for black.
	 */
	public void init(boolean aiColour, boolean PVP) {
		ai_colour = aiColour;
		moveList = "";
		moveNumber = 1;
		p = new Position();
		JChessBoard.PVP = PVP;
		tree = new Pine(p);
	}
	/**
	 * Initialises the board from a FENPlus string.
	 * 
	 * @param aiColour
	 *            The colour that the engine is playing, true for white, false
	 *            for black.
	 */
	public void init(String FENPlus) {
		String[] FEN = FENPlus.split(",");
		p = new Position();
		ai_colour = FEN[0].equals("true") ? true : false;
		playMoveSequence(FEN[1]);
		tree = new Pine (p);
	}
	public void setDepth(int depth){
		JChessBoard.depth = depth;
		System.out.println(JChessBoard.depth);
	}
	/**
	 * Returns the current "official" active position that is embedded inside
	 * <i>this</i> JChessBoard object.
	 * 
	 * @return The current active position.
	 */
	public Position getEmbeddedPosition() {
		return p;
	}

	/**
	 * Displays an appropriate message that ends the game, if any messages are
	 * appropriate.
	 */
	public void displayEndMessage() {
		int res = p.getResult();
		if (res != Position.NO_RESULT) {
			if (res == Position.DRAW) {
				JOptionPane
						.showMessageDialog(
								Myriad_XSN.Reference,
								"Poor me! I couldn't beat you, looks like WIM Yuanling Yuan is\n"
										+ "better than Jesse in her dedication to Victoria Park...",
								"It's a draw!", JOptionPane.INFORMATION_MESSAGE);
				Myriad_XSN.Reference.notation_pane.append("\n1/2-1/2");
				Myriad_XSN.Reference.message_pane
						.append("The game ended in a draw.\n");
				return;
			}
			if (res == Position.WHITE_WINS)
				Myriad_XSN.Reference.notation_pane.append("\n1-0");
			else if (res == Position.BLACK_WINS)
				Myriad_XSN.Reference.notation_pane.append("\n0-1");
			boolean ai_win = (res == Position.WHITE_WINS && ai_colour)
					|| (res == Position.BLACK_WINS && !ai_colour);
			if (ai_win) {
				JOptionPane
						.showMessageDialog(
								Myriad_XSN.Reference,
								"Hey, I won! Now it's time to use my awesome chess skills to\n"
										+ "beat Kasparov! My programmers are awesome, aren't they?",
								"Myriad XSN wins!",
								JOptionPane.INFORMATION_MESSAGE);
				Myriad_XSN.Reference.message_pane
						.append("Myriad XSN has won.\n");
			} else {
				JOptionPane.showMessageDialog(Myriad_XSN.Reference,
						"WHAT?!?! You WON!?!? This is madness... no... no...\n"
								+ "THIS IS SPARTA!!!!", "You win!",
						JOptionPane.INFORMATION_MESSAGE);
				Myriad_XSN.Reference.message_pane
						.append(Myriad_XSN.Reference.playerName + " has won.\n");
			}
			p = null;
		}
	}

	/**
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent(Graphics graphix) {
		super.paintComponent(graphix);
		paintChessBoard(graphix);
		if (p != null)
			paintPieces(graphix);
	}

	/**
	 * Takes back the last two moves made, the changes are made in the embedded
	 * position. Note when taking back two moves, the AI's colour does not
	 * change.
	 */
	public void takeBack() {
		int ind = moveList.lastIndexOf('/');
		if (ind > 0)
			moveList = moveList.substring(0, ind);
		else
			moveList = "";
		ind = moveList.lastIndexOf('/');
		if (ind > 0)
			moveList = moveList.substring(0, ind);
		else
			moveList = "";
		playMoveSequence(moveList);
	}

	/**
	 * Returns the FENPlus version of this position. See FenUtility for more
	 * details on FENPlus.
	 * 
	 * @return The FENPlus string of this board.
	 */
	public String getFENPlus() {
		if (p != null)
			return Utility.saveFENPlus(ai_colour, moveList);
		else
			return null;
	}

	// ----------------------End of Methods----------------------
	// ----------------------Helper Methods----------------------
	/**
	 * Plays a sequence of moves embedded in the string. The string is assumed
	 * to have the string representations of moves with each element in the
	 * sequence of moves separated by a backslash. The moves will be played from
	 * the starting position.
	 * 
	 * @param seq
	 *            The sequence of moves.
	 */
	private void playMoveSequence(String seq) {
		p = new Position();
		moveNumber = 1;
		moveList = seq;
		Myriad_XSN.Reference.notation_pane.setText("");
		Myriad_XSN.Reference.appendStart(ai_colour);
		boolean isWhite = true;
		String[] moveString = seq.split("/");
		for (String ms : moveString) {
			if (!ms.equals("")) {
				Myriad_XSN.Reference.notation_pane.append((isWhite ? ""
						+ moveNumber + ".)" : "")
						+ ms + (isWhite ? " " : "\n"));
				p = p.makeMove(Move.toMove(ms));
				prior_move = Move.toMove(ms);
				if (!isWhite)
					moveNumber++;
				isWhite = !isWhite;
			}
		}
	}

	/**
	 * Paints a blank chess board with the proper squares shaded and algebraic
	 * coordinate markings.
	 * 
	 * @param graphix
	 *            The graphics context to paint with.
	 */
	private void paintChessBoard(Graphics graphix) {
		graphix.setColor(Color.white);
		graphix.fillRect(0, 0, TOTAL_PIXELS, TOTAL_PIXELS);
		graphix.setColor(Color.lightGray);
		for (int x = PIXELS_PER_SQUARE; x <= TOTAL_PIXELS; x += (2 * PIXELS_PER_SQUARE)) {
			for (int y = 0; y < TOTAL_PIXELS; y += (2 * PIXELS_PER_SQUARE)) {
				graphix.fillRect(x, y, PIXELS_PER_SQUARE, PIXELS_PER_SQUARE);
				graphix.fillRect(y, x, PIXELS_PER_SQUARE, PIXELS_PER_SQUARE);
			}
		}
	}

	/**
	 * Paints the pieces on the chess board with the help of the PieceImage
	 * utility.
	 * 
	 * @param graphix
	 *            The graphics context to paint with.
	 */
	private void paintPieces(Graphics graphix) {
		Piece[] arr = p.getWhitePieces();
		for (Piece p : arr) {
			byte loc = p.getPosition();
			int x = loc % 0x10;
			int y = loc / 0x10;
			Image im = ImageUtility.getPieceImage(p);
			if (ai_colour)
				graphix.drawImage(im, (7 - x) * PIXELS_PER_SQUARE, y * PIXELS_PER_SQUARE, null);
			else
				graphix.drawImage(im, x * PIXELS_PER_SQUARE, (7 - y) * PIXELS_PER_SQUARE, null);
		}
		arr = p.getBlackPieces();
		for (Piece p : arr) {
			byte loc = p.getPosition();
			int x = loc % 0x10;
			int y = loc / 0x10;
			Image im = ImageUtility.getPieceImage(p);
			if (ai_colour)
				graphix.drawImage(im, (7 - x) * PIXELS_PER_SQUARE, y
						* PIXELS_PER_SQUARE, null);
			else
				graphix.drawImage(im, x * PIXELS_PER_SQUARE, (7 - y)
						* PIXELS_PER_SQUARE, null);
		}
		graphix.setColor(Color.black);
		graphix.setFont(new Font("Courier New", Font.BOLD, 12));
		for (int i = 0; i < 8; i++) {
			if (ai_colour) {
				graphix.drawString("" + (i + 1), 5, i * PIXELS_PER_SQUARE + 15);
				graphix.drawString("" + (char) ('h' - i), i * PIXELS_PER_SQUARE
						+ 5, TOTAL_PIXELS - 12);
			} else {
				graphix.drawString("" + (8 - i), 5, i * PIXELS_PER_SQUARE + 15);
				graphix.drawString("" + (char) ('a' + i), i * PIXELS_PER_SQUARE
						+ 5, TOTAL_PIXELS - 12);
			}
		}
		if (clicked_square != -1) {
			if (ai_colour)
				graphix.drawRect((7 - clicked_square % 0x10)
						* PIXELS_PER_SQUARE, clicked_square / 0x10
						* PIXELS_PER_SQUARE, PIXELS_PER_SQUARE,
						PIXELS_PER_SQUARE);
			else
				graphix.drawRect(clicked_square % 0x10 * PIXELS_PER_SQUARE,
						(7 - clicked_square / 0x10) * PIXELS_PER_SQUARE,
						PIXELS_PER_SQUARE, PIXELS_PER_SQUARE);
		}
	}

	/**
	 * Registers a human move to the current board, if legal. Then, the AI is
	 * commissioned to make a reply in time with multithreading.
	 * 
	 * @param m
	 *            The move registered from the human through the attached
	 *            MouseAdapter. (see Constructor)
	 */
	private void registerHumanMove(Move m) {
		Move[] legalMoves = p.generateAllMoves();
		boolean isIllegal = true;
		for (Move k : legalMoves) {
			if (k.isEqual(m)) {
				boolean isWhite = p.isWhiteToMove();
				Myriad_XSN.Reference.notation_pane.append((isWhite ? ""
						+ moveNumber + ".)" : "")
						+ m.toString(p) + (isWhite ? " " : "\n"));
				moveList += (moveList.equals("") ? "" : "/") + m.toString(p);
				p = p.makeMove(m);
				prior_move = m;
				isIllegal = false;
				/* Fix swing worker */
				displayEndMessage();
				if (!isWhite)
					moveNumber++;
				break;
			}
		}
		if (isIllegal) {
			JOptionPane.showMessageDialog(Myriad_XSN.Reference, "Illegal Move",
					"Oh snap! That's an illegal move!",
					JOptionPane.ERROR_MESSAGE);
		} else
			if (!PVP) ai_turn = true;
		clicked_square = -1;
		Myriad_XSN.Reference.repaint();
		// information
		if (p!= null){
			Utility.printInfo(p, m);
		}
	}

	private void registerAIMove(Move m) {
		boolean isWhite = p.isWhiteToMove();
		Myriad_XSN.Reference.notation_pane.append((isWhite ? "" + moveNumber
				+ ".)" : "")
				+ m.toString(p) + (isWhite ? " " : "\n"));
		moveList += (moveList.equals("") ? "" : "/") + m.toString(p);
		p = p.makeMove(m);
		prior_move = m;
		/* Fix swing worker */
		displayEndMessage();
		if (!isWhite)
			moveNumber++;

		Myriad_XSN.Reference.repaint();
		// information
		if (p!= null){
			Utility.printInfo(p, m);
		}
	}
	// ----------------------End of Helper Methods----------------------
}