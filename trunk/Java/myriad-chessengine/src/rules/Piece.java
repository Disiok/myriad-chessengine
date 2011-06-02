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
		this.pos = (MAX + 1) * 10 + MAX + 1;
		this.ptype = 0;
		this.state = false;
		this.colour = WHITE;
	}
	
	/**
	 * Makes a piece with the given position, type (and state = true)
	 * @param xpos the x position of the piece
	 * @param ypos the y position of the piece
	 * @param ptype the type of the piece (pawn, knight, etc..)
	 * @param side the colour of the piece
	 */
	public Piece (int xpos, int ypos, int ptype, byte side)
	{
		this.pos = (byte)(ypos*10 + xpos);
		this.ptype = ptype;
		this.state = true;
		this.colour = side;
	}
	
	//accessors
	/**
	 * Returns the position of the piece.
	 * @return position of the piece 
	 */
	public byte getpos () {
		return pos;
	}
	
	/**
	 * Returns the x-coordinate of the piece.
	 * @return x-coordinate of the piece
	 */
	public byte getx () {
		return (byte)(pos % 10);
	}
	
	/**
	 * Returns the y-coordinate of the piece.
	 * @return y-coordinate of th piece
	 */
	public byte gety () {
		return (byte)(pos / 10);
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
	public byte getside () {
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
	public void movey (byte d) {
		this.pos = (byte)(this.pos + (10 * d));
	}
	
	/**
	 * Moves the piece in the y direction by d spaces
	 * @param d the distance to be moved in the y direction
	 */
	public void movex (byte d) {
		this.pos = (byte)(this.pos + d);
	}
	
	/**
	 * Kills the piece by taking it off the board and changing the state
	 */
	public void kill () {
		this.state = false;
		this.pos = (MAX + 1) * 10 + MAX + 1;
	}
	
	//public elements
	/** The max width and length of the chess board */
	public final int MAX = 7;
	
	/** The colour/side identifier for white pieces */
	public final byte WHITE = 0;
	
	/** The colour/side identifier for black pieces */
	public final byte BLACK = 1;
	
	/** The  identifier for pawns */
	public final int PAWN = 0;
	
	/** The  identifier for bishops */
	public final int BISHOP = 3;
	
	/** The  identifier for knights */
	public final int KNIGHT = 2;
	
	/** The  identifier for rooks */
	public final int ROOK = 1;
	
	/** The  identifier for the queen */
	public final int QUEEN = 4;
	
	/** The  identifier for the king */
	public final int KING = 5;
	
	/** The  number of players in the game */
	public final int NUM_SIDES = 2;
	
	//private elements
	/** The state of the piece, whether the piece is alive or not */
	private boolean state;
	
	/** The position of the piece */
	private byte pos;
	
	/** The type of the piece, read the public elements for the corresponding ID's*/
	private int ptype;
	
	/** The side/colour of the piece */
	private byte colour;
}
