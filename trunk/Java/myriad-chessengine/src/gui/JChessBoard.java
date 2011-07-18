package gui;

import images.PieceImage;
import debug.FenUtility;
import engine.*;
import javax.swing.*;
import rules.*;
import java.awt.*;
import java.awt.event.*;

@SuppressWarnings("serial")
/**
 * This is the chess board "component" for the Myriad software. It displays the chessboard for user
 * input and output. This class, allows for communciation between the human
 * and the computer.
 * @author Jesse Wang, Karl Zhang
 */
public class JChessBoard extends JPanel{
	/**
	 * The number of pixels per square. Used for painting purposes.
	 */
	public static final int PIXELS_PER_SQUARE = 60;
	/**
	 * The fixed size of the board. Used for painting purposes.
	 */
	public static final int TOTAL_PIXELS = 8*PIXELS_PER_SQUARE;
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
	 * Constructs a JChessBoard object. The position is not yet initialized! The human must initialize
	 * it by getting the program to invoke one of the init() methods below.
	 */
	public JChessBoard(){
		super();
		setPreferredSize(new Dimension(8*PIXELS_PER_SQUARE,8*PIXELS_PER_SQUARE));
		setOpaque(true);
		addMouseListener(new MouseAdapter(){
			public void mouseClicked (MouseEvent me){
				if( p !=null){
					int y = 7 - me.getY()/PIXELS_PER_SQUARE;
					int x = me.getX()/PIXELS_PER_SQUARE;
					if (clicked_square==-1) {
						clicked_square = (byte) (y*0x10+x);
						if (p.getSquareOccupier(clicked_square).isEqual(Piece.getNullPiece())) 
							clicked_square = -1;
						repaint();
					}
					else {
						byte end_square = (byte)(y*0x10+x);
						Piece s = p.getSquareOccupier(clicked_square);
						Piece e = p.getSquareOccupier(end_square);
						if (s.getType()==Piece.PAWN && e.isEqual(Piece.getNullPiece())&&
								(end_square - clicked_square) % 0x10 != 0){
							registerHumanMove (new Move(clicked_square, end_square, (byte)5));
						} else if (s.getType()==Piece.KING){
							if (s.getColour()==Piece.WHITE){
								if (clicked_square == 0x02 || end_square == 0x02)
									registerHumanMove(Move.CASTLING[2]);
								else if (clicked_square == 0x06 || end_square == 0x06)
									registerHumanMove(Move.CASTLING[0]);
							} else {
								if (clicked_square == 0x72 || end_square == 0x72)
									registerHumanMove(Move.CASTLING[3]);
								else if (clicked_square == 0x76 || end_square == 0x76)
									registerHumanMove(Move.CASTLING[1]);
							}
						} else registerHumanMove (new Move(clicked_square, end_square));
					}
				}
			}
		});
	}
	/**
	 * Initializes the board from the starting position.
	 * @param aiColour The colour that the engine is playing, true for white, false for black.
	 */
	public void init (boolean aiColour){
		ai_colour = aiColour;
		p = new Position();
	}
	/**
	 * Initializes the board from a specified position, pos.
	 * @param pos The position to start from.
	 * @param aiColour The colour that the engine is playing, true for white, false for black.
	 */
	public void init (Position pos, boolean aiColour){
		ai_colour = aiColour;
		p = pos;
	}
	/**
	 * Initializes the board with a specific position in the FEN (Forsynth-Edwards Notation) form.
	 * @param fen The FEN representation of the board to start from.
	 * @param aiColour The colour that the engine is playing, true for white, false for black.
	 */
	public void init (String fen, boolean aiColour){
		ai_colour = aiColour;
		p = FenUtility.loadFEN(fen);
	}
	public Position getEmbeddedPosition(){
		return p;
	}
	public void paintComponent(Graphics graphix){
		super.paintComponent(graphix);
		paintChessBoard(graphix);
		if (p != null) paintPieces(graphix);
	}
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
			Image im = PieceImage.getPieceGivenID(p.getType(),p.getColour());
			if (ai_colour) graphix.drawImage(im,(7-x)*PIXELS_PER_SQUARE,y*PIXELS_PER_SQUARE, null);
			else graphix.drawImage(im, x*PIXELS_PER_SQUARE, (7-y)*PIXELS_PER_SQUARE, null);
		}
		arr = p.getBlackPieces();
		for (Piece p : arr){
			byte loc = p.getPosition();
			int x = loc % 0x10;
			int y = loc / 0x10;
			Image im = PieceImage.getPieceGivenID(p.getType(),p.getColour());
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
			graphix.drawRect(clicked_square%0x10*PIXELS_PER_SQUARE, 
				(7-clicked_square/0x10)*PIXELS_PER_SQUARE, PIXELS_PER_SQUARE, PIXELS_PER_SQUARE);
		}
	}
	public void registerHumanMove (Move m){
		Move [] legalMoves = p.generateAllMoves();
		boolean isIllegal = true;
		for (Move k : legalMoves){
			if (k.isEqual(m)) {
				boolean isWhite = p.isWhiteToMove();
				Myriad_XSN.Reference.notation_pane.append
					((isWhite?""+moveNumber+".)":"")+m.toString(p)+(isWhite?" ":"\n"));
				p = p.makeMove(m);
				isIllegal = false;
				//Do multi threaded reply.
				//Move reply = engine.decideOnMove(p,ai_colour);
				//p.makeMove(reply);
				if (!isWhite) moveNumber++;
				break;
			} 
		}
		if (isIllegal){
			JOptionPane.showMessageDialog(Myriad_XSN.Reference, 
				"Illegal Move", "Oh snap! That's an illegal move!", JOptionPane.ERROR_MESSAGE);
		}
		System.out.println(m);
		System.out.println(FenUtility.saveFEN(p));
		FenUtility.displayBoard(FenUtility.saveFEN(p));
		for (Move q: p.generateAllMoves()){
			System.out.println(q.toString(p));
		}
		clicked_square = -1;
		Myriad_XSN.Reference.repaint();
	}
}
