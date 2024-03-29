package rules;

/**
 * This is the piece object, this object contains a hexadecimal location, piece type and colour.
 * Once you instantiate a piece object, it is considered immutable, that means, the contents cannot
 * be changed. You must reassign your variables to achieve the desired effect, like with a string.
 * @author Spork Innovation Technologies
 */
public final class Piece {
	//----------------------Instance Variables----------------------
	/** The position of the piece */
	private byte pos;
	/** The type of the piece, read the public elements for the corresponding ID's*/
	private byte ptype;
	/** The side/colour of the piece */
	private byte colour;
	//----------------------End of Instance Variables----------------------
	//----------------------Constants----------------------
	/** The colour/side identifier for white pieces. */
	public static final byte WHITE = 1;
	/** The colour/side identifier for black pieces. */
	public static final byte BLACK = -1;
	/** The colour/side identifier for null pieces. */
	public static final byte NULL_COL = 0;
	/** The identifier for pawns. */
	public static final byte PAWN = 0;
	/** The identifier for rooks. */
	public static final byte ROOK = 1;
	/** The identifier for knights. */
	public static final byte KNIGHT = 2;
	/** The identifier for bishops. */
	public static final byte BISHOP = 3;
	/** The identifier for the queen. */
	public static final byte QUEEN = 4;
	/** The identifier for the king. */
	public static final byte KING = 5;
	/** The identifier for the null piece. */
	public static final byte NULL = -1;
	/** The null inexistent piece. */
	private static final short PAWN_VALUE = 100;
	private static final short KNIGHT_VALUE = 325;
	private static final short BISHOP_VALUE = 330;
	private static final short ROOK_VALUE = 500;
	private static final short QUEEN_VALUE = 975;
	private static final Piece NULL_PIECE = new Piece ((byte)-1, (byte)-1, (byte)0);
	//----------------------End of Constants----------------------

	//----------------------Constructors----------------------      
	/**
	 * Makes a piece with the given position 0x88 coordinate and type 
	 * (and state = true).
	 * @param x88loc the 0x88 location of the piece.
	 * @param ptype the type of the piece (pawn, knight, etc..) according to the constants.
	 * @param colour the colour of the piece.
	 */
	public Piece (byte x88loc, byte ptype, byte colour){
		this.pos = x88loc;
		this.ptype = ptype;
		this.colour = colour;
	}
	//----------------------End of Constructors----------------------

	//----------------------Methods----------------------
	/**
	 * Returns whether or not the piece is on the board.
	 * @return Whether or not the piece is on the board.
	 */
	public boolean exists(){
		return ((pos & 0x88) == 0);
	}
	/**
	 * Returns the colour of the piece.
	 * @return the colour of the piece.
	 */
	public byte getColour () {
		return this.colour;
	}       
	/**
	 * Returns the position of the piece.
	 * @return the position of the piece.
	 */
	public byte getPosition () {
		return this.pos;
	}       
	/**
	 * Returns the type of the piece as defined by the piece constants above.
	 * @return the type of the piece.
	 */
	public byte getType () {
		return this.ptype;
	}
	/**
	 * Moves a piece a 0x88 difference in decimal, that is, the difference between
	 * 0x58 and 0x70 is actually not 12. It's 18. You need to put 18 for this method
	 * to work properly. Also, like Strings, you MUST reassign your variable, ex.
	 * <code>
	 * Piece p = new Piece (32, 4, 3);
	 * p = p.move (15)
	 * </code>
	 * Note: You <i>can</i> actually move your piece out of the board. Just use the
	 * exists() method to test whether or not it is out of the board.
	 * @param difference between the squares in decimal.
	 * @return A new Piece of the same type and colour, but a different location.
	 */
	public Piece move(byte difference){
		return new Piece((byte)(pos+difference),ptype,colour);
	}
	/**
	 * Applies a Move m to this object. This returns a new object with the move applied
	 * to it. This means you must reassign your variable.
	 */
	public Piece move (Move m){
		return new Piece (m.getEndSquare(), ptype, colour);
	}
	/**
	 * Returns the "null piece", or a piece that has no defined colour or piece type.
	 * You must reassign to get this effect. ex.
	 * <code>
	 * Piece p = new Piece (32, 4, 3);
	 * p = p.destroy();
	 * </code>
	 * @return The null piece.
	 */
	public Piece destroy () {
		return NULL_PIECE;
	}
	/**
	 * Returns the null piece. A piece that has no valid colour or type and at a non-existent
	 * location.
	 * @return The null piece.
	 */
	public static Piece getNullPiece(){
		return NULL_PIECE;
	}
	
	public short getPieceValue(){
		if(ptype == PAWN) return PAWN_VALUE;
		else if(ptype == ROOK) return ROOK_VALUE;
		else if(ptype == QUEEN) return QUEEN_VALUE;
		else if(ptype == KNIGHT) return KNIGHT_VALUE;
		else if(ptype == BISHOP) return BISHOP_VALUE;
		else if(ptype == KING) return Short.MAX_VALUE;
		return 0;
	}
	
	
	/**
	 * Compares this piece to another piece for equality. 
	 * @param otherPiece The piece to be compared to.
	 * @return True if every aspect is the same, false otherwise.
	 */
	public boolean isEqual(Piece other){
		return other.getPosition()==pos && other.getType()== ptype && other.getColour()==colour;
	}
	/**
	 * Returns a string representation of this Piece object.
	 * @return A string representation of this Piece object.
	 */
	public String toString(){
		String str = "";
		switch (ptype){
		case PAWN: break;
		case BISHOP: str+="B"; break;
		case KNIGHT: str+="N"; break;
		case ROOK: str+="R"; break;
		case QUEEN: str+="Q"; break;
		case KING: str+="K"; break;
		default: return "Null Piece";
		}
		str+=Move.x88ToString(pos);
		return str;
	}
	//----------------------End of Methods----------------------
}