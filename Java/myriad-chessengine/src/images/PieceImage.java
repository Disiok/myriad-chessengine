package images;

import java.awt.Image;
import java.io.*;
import javax.imageio.*;
import rules.Piece;

/**
 * This class contains all the image files associated with the pieces. It provides a image retriever
 * method that gets the image from the Piece Constants specified in the Piece class.
 * @author Jesse Wang
 */
public enum PieceImage{
	WHITE_PAWN ("White Pawn.png"),
	BLACK_PAWN ("Black Pawn.png"),
	WHITE_ROOK ("White Rook.png"),
	BLACK_ROOK ("Black Rook.png"),
	WHITE_KNIGHT("White Knight.png"),
	BLACK_KNIGHT("Black Knight.png"),
	WHITE_BISHOP("White Bishop.png"),
	BLACK_BISHOP("Black Bishop.png"),
	WHITE_QUEEN ("White Queen.png"),
	BLACK_QUEEN ("Black Queen.png"),
	WHITE_KING ("White King.png"),
	BLACK_KING ("Black King.png");
	/**
	 * The Image that is encapsulated in the PieceImage object.
	 */
	private Image IMAGE;
	/**
	 * Constructor that constructs an image from the name of the gif stored in the images
	 * package.
	 * @param gifName The name of the gif.
	 */
	private PieceImage (String gifName){
		try {
			IMAGE = ImageIO.read(getClass().getResourceAsStream(gifName));
		} catch (IOException ios){
			IMAGE = null;
		}
	}
	/**
	 * Gets the image encapsulated an an enumeration object with the specified Piece type
	 * and colour according to the constants in the "Piece" calss.
	 * @param PieceType The type of the Piece.
	 * @param Colour The colour of the Piece.
	 * @return The image encapsulated in the specified enumeration type. Null if it does not
	 * exist.
	 */
	public static Image getPieceGivenID (int PieceType, int Colour){
		switch (PieceType){
			case Piece.PAWN:
				return (Colour==Piece.WHITE ? WHITE_PAWN : BLACK_PAWN).IMAGE;
			case Piece.BISHOP:
				return (Colour==Piece.WHITE ? WHITE_BISHOP : BLACK_BISHOP).IMAGE;
			case Piece.KNIGHT:
				return (Colour==Piece.WHITE ? WHITE_KNIGHT : BLACK_KNIGHT).IMAGE;
			case Piece.ROOK:
				return (Colour==Piece.WHITE ? WHITE_ROOK : BLACK_ROOK).IMAGE;
			case Piece.KING:
				return (Colour==Piece.WHITE ? WHITE_KING : BLACK_KING).IMAGE;
			case Piece.QUEEN:
				return (Colour==Piece.WHITE ? WHITE_QUEEN: BLACK_QUEEN).IMAGE;
		}
		return null;
	}
}
