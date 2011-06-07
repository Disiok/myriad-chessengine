package rules;

/**
 * The Sandbox. Test your code here! Some methods are provided. Please do not remove any code that
 * you think might be important! Just comment it out!
 */
public class Sandbox{
	public static byte twoDigitHexConverter (int someIntNumber){
		int tens = someIntNumber /10;
		int ones = someIntNumber %10;
		return (byte) (tens*16+ones);
	}
	public static int twoDigitIntConverter (byte someHexNumber){
		int tens = someHexNumber/16;
		int ones = someHexNumber%16;
		return 10*tens+ones;
	}
	public static void main (String [] args){
		//Position b = new Position();
		// Initial Board Set-up Test = JW
		/*Piece [] ps = b.getWhitePieces();
		for (Piece p: ps){
			System.out.println(p.getPosition() + " " + p.getType() + " " + p.getColour());
		}
		Piece [] pb = b.getBlackPieces();
		for (Piece p: pb){
			System.out.println(p.getPosition() + " " + p.getType() + " " + p.getColour());
		}*/
		// Pawns and Knights Movement Test = JW
		/*
		Move [] m = b.generateAllMoves();
		for (Move s: m){
			System.out.println(twoDigitIntConverter(s.getStartSquare()) + " " + 
					twoDigitIntConverter(s.getEndSquare()));
		}*/
	}
}