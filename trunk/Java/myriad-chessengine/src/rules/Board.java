/**
 * 
 */
package rules;

/**
 * the board of the game
 * 
 * @author David Jeong
 *
 */
public class Board extends Piece 
{
	//constructors
	/**
	 * Constructs a board with the right pieces in its starting positions
	 */
	public Board ()
	{
		//the labelling is kind of weird, but just keep in mind 0~7 are pawns and the rest are commented
		
		//WHITE PIECES SET UP
		for (byte i = 0; i < 2; i ++)
		{
			for (int c = 0; c < super.MAX; c ++) 
				p[i][c] = new Piece (c, 1, super.PAWN, i);
			for (int c = 8; c < super.MAX + 8; c ++)
				p[i][(c%7)+1] = new Piece (0, 0, (c-6)%6, i);
		}
	}
	
	/**
	 * Returns an array of the pieces in their current positions (side isn't recorded
	 * because it isn't part of the piece Object)
	 * @return an array of the pieces in their current positions
	 */
	public Piece[][] getArray ()
	{
		Piece[][] toReturn = new Piece[super.MAX][super.MAX];
		for (int i = 0; i < NUM_SIDES; i ++)
			for (int c = 0; c < NUM_PIECES; c ++)
				toReturn[p[i][c].getx()][p[i][c].gety()] = p[i][c];
		return toReturn;
	}
	
	//public elements
	/**
	 * The  number of pieces on the board */
	public final int NUM_PIECES = 16;
	
	//private elements
	/**
	 An array of pieces which are used to reference each piece. The first array 
	 address addresses the side and the second addresses the type of the piece*/
	private Piece[][] p = new Piece [super.NUM_SIDES][NUM_PIECES];
}
