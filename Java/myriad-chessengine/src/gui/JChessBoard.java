package gui;

import images.PieceImage;
import engine.*;
import javax.swing.*;
import rules.*;
import java.awt.*;
import java.awt.event.*;

@SuppressWarnings("serial")
/**
 * This is the chess board "component" for the Myriad software. It displays the chessboard for user
 * input and output. This class, along with the AIAdaptor, allows for communciation between the human
 * and the computer.
 * @author Jesse Wang, Karl Zhang
 */
//TODO: Add move dragging.
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
	private static Myriad engine = new Myriad();
	/**
	 * The colour that Myriad is, true for white, false for black.
	 */
	private static boolean ai_colour;
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
					int x = me.getX()/PIXELS_PER_SQUARE;
					int y = me.getY()/PIXELS_PER_SQUARE;
					if (clicked_square==-1) clicked_square = (byte) (x*16+y);
					//TODO: Add registers for pawn promotion and en passant.
					else registerHumanMove (new Move(clicked_square, (byte)(x*16+y)));
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
		// TODO: Use the FEN Converter.
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
	    graphix.setColor(Color.black);
	    for (int i = 0; i < 8; i++){
	    	graphix.drawString(""+(i+1), 5, i*PIXELS_PER_SQUARE + 15);
	    	graphix.drawString(""+(char)('a'+i),i*PIXELS_PER_SQUARE+5,TOTAL_PIXELS-12);
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
			int y = 7 - loc / 0x10;
			Image im = PieceImage.getPieceGivenID(p.getType(),p.getColour());
			graphix.drawImage(im, x*PIXELS_PER_SQUARE, y*PIXELS_PER_SQUARE, null);
		}
		arr = p.getBlackPieces();
		for (Piece p : arr){
			byte loc = p.getPosition();
			int x = loc % 0x10;
			int y = 7 - loc / 0x10;
			Image im = PieceImage.getPieceGivenID(p.getType(),p.getColour());
			graphix.drawImage(im, x*PIXELS_PER_SQUARE, y*PIXELS_PER_SQUARE, null);
		}
	}
	public void registerHumanMove (Move m){
		Move [] legalMoves = p.generateAllMoves();
		for (Move k : legalMoves){
			if (k.isEqual(m)) {
				p = p.makeMove(m);
				Move reply = engine.decideOnMove(p,ai_colour);
				p.makeMove(reply);
				break;
			} else {
				clicked_square = -1;
				JOptionPane.showMessageDialog(Myriad_XSN.Reference, 
					"Illegal Move", "Oh snap! That's an illegal move!", JOptionPane.ERROR_MESSAGE);
			}
		}
		Myriad_XSN.Reference.repaint();
	}
}
