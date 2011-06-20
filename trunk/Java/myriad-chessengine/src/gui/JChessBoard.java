package gui;

import images.PieceImage;

import javax.swing.*;
import rules.*;

import java.awt.*;

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
	private static final int PIXELS_PER_SQUARE = 60;
	/**
	 * The fixed size of the board. Used for painting purposes.
	 */
	private static final int TOTAL_PIXELS = 8*PIXELS_PER_SQUARE;
	/**
	 * The position that <i>this</i> JChessBoard object contains. It is the "master" and official
	 * board.
	 */
	private Position p;
	/**
	 * Constructs a JChessBoard object. The position is not yet initialized! The human must initialize
	 * it by getting the program to invoke one of the init() methods below.
	 */
	public JChessBoard(){
		super();
		setPreferredSize(new Dimension(8*PIXELS_PER_SQUARE,8*PIXELS_PER_SQUARE));
		setOpaque(true);
	}
	/**
	 * Initializes the board from the starting position.
	 */
	public void init (){
		p = new Position();
	}
	/**
	 * Initializes the board from a specified position, pos.
	 * @param pos The position to start from.
	 */
	public void init (Position pos){
		p = pos;
	}
	/**
	 * Initializes the board with a specific position in the FEN (Forsynth-Edwards Notation) form.
	 * @param fen The FEN representation of the board to start from.
	 */
	public void init (String fen){
		// TODO: Use the FEN Converter.
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
	    	graphix.drawString(""+(char)('a'+i),(i+1)*PIXELS_PER_SQUARE-10,TOTAL_PIXELS-5);
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
}
