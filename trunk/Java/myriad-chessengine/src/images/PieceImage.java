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
	WHITE_PAWN ("whitePawn.gif"),
	BLACK_PAWN ("blackPawn.gif"),
	WHITE_ROOK ("whiteRook.gif"),
	BLACK_ROOK ("blackRook.gif"),
	WHITE_KNIGHT("whiteKnight.gif"),
	BLACK_KNIGHT("blackKnight.gif"),
	WHITE_BISHOP("whiteBishop.gif"),
	BLACK_BISHOP("blackBishop.gif"),
	WHITE_QUEEN ("whiteQueen.gif"),
	BLACK_QUEEN ("blackQueen.gif"),
	WHITE_KING ("whiteKing.gif"),
	BLACK_KING ("blackKing.gif");
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
			IMAGE = ImageIO.read(new File (gifName));
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
