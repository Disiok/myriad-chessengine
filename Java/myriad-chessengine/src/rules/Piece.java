/**
 * 
 */
package rules;

/**
 * Piece object. I made it such that it implements the position object as well. This is done by
 * two integers which define the location of the piece. 
 * 
 * @author davidyjeong
 *
 */
public class Piece 
{
	//constructors
	/**
	 * Makes a new piece with no position, set on side = 0, ptype = 0 and state as false;
	 */
	public Piece ()
	{
		this.x = 0;
		this.y = 0;
		this.ptype = 0;
		this.state = false;
		this.colour = -1;
	}
	
	/**
	 * Makes a piece with the given position, type (and state = true)
	 * @param xpos the x position of the piece
	 * @param ypos the y position of the piece
	 * @param ptype the type of the piece (pawn, knight, etc..)
	 * @param side the colour of the piece
	 */
	public Piece (int xpos, int ypos, int ptype, int side)
	{
		this.x = xpos;
		this.x = ypos;
		this.ptype = ptype;
		this.state = true;
		this.colour = side;
	}
	
	//accessors
	/**
	 * Returns the x position of the piece.
	 * @return x position of the piece 
	 */
	public int getx () {
		return this.x;
	}
	
	/**
	 * Returns the y position of the piece.
	 * @return y position of the piece
	 */
	public int gety () {
		return this.y;
	}
	
	/**
	 * Returns the state of the piece 
	 * @return the state of the piece
	 */
	public boolean getstate () {
		return this.state;
	}
	
	/**
	 * Returns the colour of the piece
	 * @return the colour of the piece
	 */
	public int getside () {
		return this.colour;
	}
	
	/**
	 * Returns the type of the piece where the piece id's are in the public elements
	 * @return the type of the piece
	 */
	public int getpytype () {
		return this.ptype;
	}
	
	
	//mutators
	/**
	 * Moves the piece in the x direction by d spaces
	 * @param d the distance to be moved in the x direction
	 */
	public void movex (int d) {
		this.x = this.x + d;
	}
	
	/**
	 * Moves the piece in the y direction by d spaces
	 * @param d the distance to be moved in the y direction
	 */
	public void movey (int d) {
		this.y = this.y + d;
	}
	
	/**
	 * Kills the piece by taking it off the board and changing the state
	 */
	public void kill () {
		this.state = false;
		this.x = -1;
		this.y = -1;
	}
	
	//public elements
	/**
	 * The max width and length of the chess board */
	public final int MAX = 7;
	/**
	 * The colour/side identifier for white pieces */
	public final int WHITE = 0;
	/**
	 * The colour/side identifier for black pieces */
	public final int BLACK = 1;
	/**
	 * The  identifier for pawns */
	public final int PAWN = 1;
	/**
	 * The  identifier for bishops */
	public final int BISHOP = 2;
	/**
	 * The  identifier for knights */
	public final int KNIGHT = 3;
	/**
	 * The  identifier for rooks */
	public final int ROOK = 4;
	/**
	 * The  identifier for the queen */
	public final int QUEEN = 5;
	/**
	 * The  identifier for the king */
	public final int KING = 6;
	/**
	 * The  number of players in the game */
	public final int NUM_SIDES = 2;
	
	//private elements
	/**
	 The state of the piece, whether the piece is alive or not */
	private boolean state;
	/**
	 The x position of the piece */
	private int x;
	/**
	 The y position of the piece */
	private int y;
	/**
	 The type of the piece, read the public elements for the corresponding ID's*/
	private int ptype;
	/**
	 The side/colour of the piece */
	private int colour;
}
