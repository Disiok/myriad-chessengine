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
		// Move conversion into string = JW
		//Move m = new Move ((byte)0x14, (byte)0x34);
		//System.out.println(m);
		
		/**
		 * Rechecking moves. - JW. Bug error results. It seems to generate some ridiculous moves
		 * probably my fault somewhere. It appears that the 0x88 is not being checked. As I am getting
		 * e1-V1.
		 */
		Position p = new Position();
		Move [] m = p.generateAllMoves();
		for (Move q : m){
			System.out.println(q);
		}
	}
}