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
		for (int c = 0; c < super.MAX; c ++) 
			p[WHITE][c] = new Piece (c, 1, super.PAWN, super.WHITE);
		p[WHITE][8] = new Piece (0, 0, super.ROOK, super.WHITE);
		p[WHITE][9] = new Piece (1, 0, super.KNIGHT, super.WHITE);
		p[WHITE][10] = new Piece (2, 0, super.BISHOP, super.WHITE);
		p[WHITE][11] = new Piece (4, 0, super.KING, super.WHITE);
		p[WHITE][12] = new Piece (3, 0, super.QUEEN, super.WHITE);
		p[WHITE][13] = new Piece (7, 0, super.ROOK, super.WHITE);
		p[WHITE][14] = new Piece (6, 0, super.KNIGHT, super.WHITE);
		p[WHITE][15] = new Piece (5, 0, super.BISHOP, super.WHITE);
		
		//BLACK PIECES SET UP
		for (int c = 0; c < super.MAX; c ++) 
			p[BLACK][c] = new Piece (c, 1, super.PAWN, super.BLACK);
		p[BLACK][8] = new Piece (0, 0, super.ROOK, super.BLACK);
		p[BLACK][9] = new Piece (1, 0, super.KNIGHT, super.BLACK);
		p[BLACK][10] = new Piece (2, 0, super.BISHOP, super.BLACK);
		p[BLACK][11] = new Piece (4, 0, super.KING, super.BLACK);
		p[BLACK][12] = new Piece (3, 0, super.QUEEN, super.BLACK);
		p[BLACK][13] = new Piece (7, 0, super.ROOK, super.BLACK);
		p[BLACK][14] = new Piece (6, 0, super.KNIGHT, super.BLACK);
		p[BLACK][15] = new Piece (5, 0, super.BISHOP, super.BLACK);
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
