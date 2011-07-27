package gui;

import images.PieceImage;
import javax.swing.*;
import rules.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import debug.FenUtility;
import engine.*;

@SuppressWarnings("serial")
/**
 * This is the chess board "component" for the Myriad software. It displays the chessboard for user
 * input and output. This class, allows for communciation between the human
 * and the computer.
 * @author Jesse Wang, Karl Zhang
 */
public class JChessBoard extends JPanel{
	//----------------------Fields----------------------
	/**
	 * The position that <i>this</i> JChessBoard object contains. It is the "master" and official
	 * board.
	 */
	private static Position p;
	/**
	 * The anchor for the start square of a user's move.
	 */
	private static byte clicked_square = -1;
	/**
	 * The engine that the JChessBoard is using.
	 */
	//private static Myriad engine = new Myriad();
	/**
	 * The colour that Myriad is, true for white, false for black.
	 */
	private static boolean ai_colour;
	/**
	 * The number of full moves that have been made.
	 */
	private static int moveNumber = 1;
	/**
	 * A linked list keeping track of all the moves that have been made.
	 */
	private static LinkedList <Move> gamePlay;
	/**
	 * The current set of chess pieces that is being used. Default is set number 4.
	 */
	private static int CurrentSet = 4;
	//----------------------End of Fields----------------------
	//----------------------Constants----------------------
	/**
	 * The number of pixels per square. Used for painting purposes.
	 */
	public static final int PIXELS_PER_SQUARE = 60;
	/**
	 * The fixed size of the board. Used for painting purposes.
	 */
	public static final int TOTAL_PIXELS = 8*PIXELS_PER_SQUARE;
	//----------------------End of Constants----------------------
	//----------------------Constructor----------------------
	/**
	 * Constructs a JChessBoard object. The position is not yet initialized! The human must initialize
	 * it by getting the program to invoke one of the init() methods below.
	 * The constructor initialises the JPanel and adds the appropriate mouseAdapter for the two point
	 * click system used in Myriad.
	 */
	public JChessBoard(){
		super();
		setPreferredSize(new Dimension(8*PIXELS_PER_SQUARE,8*PIXELS_PER_SQUARE));
		setOpaque(true);
		addMouseListener(new MouseAdapter(){
			public void mouseClicked (MouseEvent me){
				if( p !=null /*&& p.isWhiteToMove()!=ai_colour*/){
					int y = me.getY()/PIXELS_PER_SQUARE;
					int x = me.getX()/PIXELS_PER_SQUARE;
					if (clicked_square==-1) {
						clicked_square = (byte)(ai_colour ? y*0x10+(7-x): (7-y)*0x10+x);
						Piece q = p.getSquareOccupier(clicked_square);
						if (q.getColour()!=(p.isWhiteToMove()?Piece.WHITE:Piece.BLACK))clicked_square=-1;
						repaint();
					} else {
						final byte end_square = (byte)(ai_colour ? y*0x10+x+7 : (7-y)*0x10+x);
						if (end_square == clicked_square) {
							clicked_square = -1;
							repaint();
							return;
						}
						Piece s = p.getSquareOccupier(clicked_square);
						Piece e = p.getSquareOccupier(end_square);
						if (s.getType()==Piece.PAWN&&!e.exists()&&(end_square-clicked_square)%0x10!=0){
							registerHumanMove (new Move(clicked_square, end_square, (byte)5));
						} else if (s.getType()==Piece.KING &&(s.getPosition()==0x04||
								s.getPosition()==0x74)){
							if (s.getColour()==Piece.WHITE){
								if (end_square==0x02)registerHumanMove(Move.CASTLE[2]);
								else if(end_square==0x06) registerHumanMove(Move.CASTLE[0]);
								else registerHumanMove(new Move (clicked_square, end_square));
							} else {
								if (end_square==0x72)registerHumanMove(Move.CASTLE[3]);
								else if (end_square==0x76) registerHumanMove(Move.CASTLE[1]);
								else registerHumanMove(new Move (clicked_square, end_square));
							}
						} else if(s.getType()==Piece.PAWN&&
								(end_square/0x10==0x00||end_square/0x10==0x07)){
							final JDialog jd = new JDialog ();
							jd.setTitle("Promotion! Choose a piece to promote your pawn to:");
							jd.setModal(true);
							jd.setLocationRelativeTo(JChessBoard.this);
							jd.setSize(400,110);
							jd.setLayout(new FlowLayout());
							for (int i = 0; i < 4; i++){
								final JButton toAdd = new JButton();
								Image ico = PieceImage.getPieceGivenID(Piece.ROOK+i,
										p.isWhiteToMove()?Piece.WHITE:Piece.BLACK,CurrentSet);
								toAdd.setName(""+i);
								toAdd.setIcon(new ImageIcon(ico));
								toAdd.addActionListener(new ActionListener(){
									public void actionPerformed(ActionEvent ae){
										int promotion_code = Integer.parseInt(toAdd.getName());
										registerHumanMove(new Move 
											(clicked_square, end_square,(byte)(promotion_code+6)));
										jd.dispose();
									}
								});
								jd.setResizable(false);
								jd.add(toAdd);
							}
							jd.setVisible(true);
						} else registerHumanMove (new Move(clicked_square, end_square));
					}
				}
			}
		});
	}
	//----------------------End of Constructors----------------------
	//----------------------Methods----------------------
	/**
	 * Initializes the board from the starting position.
	 * @param aiColour The colour that the engine is playing, true for white, false for black.
	 */
	public void init (boolean aiColour){
		ai_colour = aiColour;
		gamePlay = new LinkedList<Move> ();
		moveNumber = 1;
		p = new Position();
	}
	/**
	 * Initializes the board from FEN.
	 * @param aiColour The colour that the engine is playing, true for white, false for black.
	 */
	public void init (boolean aiColour, String FEN){
		ai_colour = aiColour;
		gamePlay = new LinkedList<Move> ();
		moveNumber = 1;
		p = FenUtility.loadFEN(FEN);
	}
	/**
	 * Initializes the board from a specified position, pos.
	 * @param pos The position to start from.
	 * @param aiColour The colour that the engine is playing, true for white, false for black.
	 */
	public void init (Position pos, boolean aiColour){
		ai_colour = aiColour;
		gamePlay = new LinkedList<Move> ();
		moveNumber = 1;
		p = pos;
	}
	/**
	 * Returns the current "official" active position that is embedded inside <i>this</i> JChessBoard
	 * object.
	 * @return The current active position.
	 */
	public Position getEmbeddedPosition(){
		return p;
	}
	/**
	 * Sets the current set with a given ID. IDs should be retrieve with ImageUtility.getSetID(String)
	 * method.
	 * @param setID The ID number representing the set.
	 */
	public void setCurrentSet(int setID){
		CurrentSet = setID;
	}
	/**
	 * Displays an appropriate message that ends the game, if any messages are appropriate.
	 */
	public void displayEndMessage (){
		int res = p.getResult();
		if (res != Position.NO_RESULT){
			if (res == Position.DRAW){
				JOptionPane.showMessageDialog(Myriad_XSN.Reference, 
						"Poor me! I couldn't beat you, looks like WIM Yuanling Yuan is\n"+
						"better than Jesse in her dedication to Victoria Park...","It's a draw!", 
						JOptionPane.INFORMATION_MESSAGE);
				Myriad_XSN.Reference.notation_pane.append("\n1/2-1/2");
				Myriad_XSN.Reference.message_pane.append("The game ended in a draw.\n");
				return;
			}
			if (res == Position.WHITE_WINS) Myriad_XSN.Reference.notation_pane.append("\n1-0");
			else if (res == Position.BLACK_WINS) Myriad_XSN.Reference.notation_pane.append("\n0-1");
			boolean ai_win=(res==Position.WHITE_WINS&&ai_colour)||
						   (res==Position.BLACK_WINS&&!ai_colour);
			if (ai_win){
				JOptionPane.showMessageDialog(Myriad_XSN.Reference, 
						"Hey, I won! Now it's time to use my awesome chess skills to\n"+
						"beat Kasparov! My programmers are awesome, aren't they?","Myriad XSN wins!",
						JOptionPane.INFORMATION_MESSAGE);
				Myriad_XSN.Reference.message_pane.append("Myriad XSN has won.\n");
			} else {
				JOptionPane.showMessageDialog(Myriad_XSN.Reference,  
						"WHAT?!?! You WON!?!? This is madness... no... no...\n"+
						"THIS IS SPARTA!!!!", "You win!",
						JOptionPane.INFORMATION_MESSAGE);
				Myriad_XSN.Reference.message_pane.append(Myriad_XSN.Reference.playerName+" has won.\n");
			}
			p = null;
		}
	}
	/**
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent(Graphics graphix){
		super.paintComponent(graphix);
		paintChessBoard(graphix);
		if (p != null) paintPieces(graphix);
	}
	/**
	 * Takes back the last two moves made, the changes are made in the embedded position. Note
	 * when taking back two moves, the AI's colour does not change.
	 */
	public void takeBack(){
		if (!gamePlay.isEmpty()){
			p = new Position();
			moveNumber = 1;
			gamePlay.removeLast();
			if (!gamePlay.isEmpty()) gamePlay.removeLast();
			String playerName = Myriad_XSN.Reference.playerName;
			Myriad_XSN.Reference.notation_pane.setText(ai_colour?("Myriad XSN vs. "+playerName+
			"\n-----------\n"):(playerName + " vs. Myriad XSN\n-----------\n"));
			for (Move m: gamePlay){
				boolean isWhite = p.isWhiteToMove();
				Myriad_XSN.Reference.notation_pane.append
					((isWhite?""+moveNumber+".)":"")+m.toString(p)+(isWhite?" ":"\n"));
				if (!isWhite) moveNumber++;
				p = p.makeMove(m);
			}
		}
	}
	/**
	 * Return current FEN representation
	 */
	public String getFEN(){
		if (p != null)
			return FenUtility.saveFEN(p);
		else return "Game has not been started, please start or load game";
	}
	//----------------------End of Methods----------------------
	//----------------------Helper Methods----------------------
	/**
	 * Paints a blank chess board with the proper squares shaded and algebraic coordinate markings. 
	 * @param graphix The graphics context to paint with.
	 */
	private void paintChessBoard (Graphics graphix){
		graphix.setColor(Color.white);
		graphix.fillRect(0, 0, TOTAL_PIXELS, TOTAL_PIXELS);
		graphix.setColor(Color.lightGray);
		for (int x = PIXELS_PER_SQUARE; x <= TOTAL_PIXELS; x+=(2*PIXELS_PER_SQUARE)){
			for (int y = 0; y < TOTAL_PIXELS; y+=(2*PIXELS_PER_SQUARE)){
				graphix.fillRect(x,y,PIXELS_PER_SQUARE,PIXELS_PER_SQUARE);
				graphix.fillRect(y,x,PIXELS_PER_SQUARE,PIXELS_PER_SQUARE);
			}
		}
	}
	/**
	 * Paints the pieces on the chess board with the help of the PieceImage utility.
	 * @param graphix The graphics context to paint with.
	 */
	private void paintPieces (Graphics graphix){
		Piece[] arr = p.getWhitePieces();
		for (Piece p : arr){
			byte loc = p.getPosition();
			int x = loc % 0x10;
			int y = loc / 0x10;
			Image im = PieceImage.getPieceGivenID(p.getType(),p.getColour(),CurrentSet);
			if (ai_colour) graphix.drawImage(im,(7-x)*PIXELS_PER_SQUARE,y*PIXELS_PER_SQUARE, null);
			else graphix.drawImage(im, x*PIXELS_PER_SQUARE, (7-y)*PIXELS_PER_SQUARE, null);
		}
		arr = p.getBlackPieces();
		for (Piece p : arr){
			byte loc = p.getPosition();
			int x = loc % 0x10;
			int y = loc / 0x10;
			Image im = PieceImage.getPieceGivenID(p.getType(),p.getColour(),CurrentSet);
			if (ai_colour) graphix.drawImage(im,(7-x)*PIXELS_PER_SQUARE,y*PIXELS_PER_SQUARE, null);
			else graphix.drawImage(im, x*PIXELS_PER_SQUARE, (7-y)*PIXELS_PER_SQUARE, null);
		}
		graphix.setColor(Color.black);
		graphix.setFont(new Font("Courier New", Font.BOLD, 12));
		for (int i = 0; i < 8; i++){
			if (ai_colour){
				graphix.drawString(""+(i+1), 5, i*PIXELS_PER_SQUARE + 15);
				graphix.drawString(""+(char)('h'-i),i*PIXELS_PER_SQUARE+5,TOTAL_PIXELS-12);
			} else {
				graphix.drawString(""+(8-i), 5, i*PIXELS_PER_SQUARE + 15);
				graphix.drawString(""+(char)('a'+i),i*PIXELS_PER_SQUARE+5,TOTAL_PIXELS-12);
			}
		}
		if (clicked_square != -1){
			if (ai_colour) graphix.drawRect((7-clicked_square%0x10)*PIXELS_PER_SQUARE, 
					clicked_square/0x10*PIXELS_PER_SQUARE, PIXELS_PER_SQUARE, PIXELS_PER_SQUARE);
			else graphix.drawRect(clicked_square%0x10*PIXELS_PER_SQUARE, 
					(7-clicked_square/0x10)*PIXELS_PER_SQUARE, PIXELS_PER_SQUARE, PIXELS_PER_SQUARE);
		}
	}
	/**
	 * Registers a human move to the current board, if legal. Then, the AI is commissioned to make a
	 * reply in time with multi-threading.
	 * @param m The move registered from the human through the MouseAdapter.
	 */
	private void registerHumanMove (Move m){
		Move [] legalMoves = p.generateAllMoves();
		boolean isIllegal = true;
		for (Move k : legalMoves){
			if (k.isEqual(m)) {
				boolean isWhite = p.isWhiteToMove();
				Myriad_XSN.Reference.notation_pane.append
					((isWhite?""+moveNumber+".)":"")+m.toString(p)+(isWhite?" ":"\n"));
				p = p.makeMove(m);
				isIllegal = false;
				/* Fix swing worker */
				displayEndMessage();
				if (!isWhite) moveNumber++;
				gamePlay.add(m);
				break;
			} 
		}
		if (isIllegal){
			JOptionPane.showMessageDialog(Myriad_XSN.Reference, "Illegal Move", 
					"Oh snap! That's an illegal move!", JOptionPane.ERROR_MESSAGE);
		}
		System.out.println(m);
		System.out.println(FenUtility.saveFEN(p));
		FenUtility.displayBoard(FenUtility.saveFEN(p));
		for (Move q: p.generateAllMoves()){
			System.out.println(q.toString(p));
		}
		//testing area
		PositionFeatures pf = new PositionFeatures(new PositionPlus(p));
		System.out.println("---");
		
		//doubled bishop test
		System.out.println(pf.b_D_Bishop);
		System.out.println(pf.w_D_Bishop);
		
		//some pawn test
		Vector <Piece> v = pf.w_B_Pawn;
		Piece[] ps = new Piece[v.size()];
		ps = v.toArray(ps);
		for (Piece pss : ps){
			System.out.println(pss);			
		}
		
		//some outpost test
		Vector <Byte> x = pf.w_Outpost;
		Byte[] xs = new Byte[x.size()];
		xs = x.toArray(xs);
		for (Byte xss : xs){
			System.out.println(xss);			
		}
		
		System.out.println("---");
		//end testing area
		
		System.out.println("-------------------");
		clicked_square = -1;
		Myriad_XSN.Reference.repaint();
	}
	//----------------------End of Helper Methods----------------------
}