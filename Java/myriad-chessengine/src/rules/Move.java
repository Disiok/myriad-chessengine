package rules;

/**
 * Myriad's representation of chess moves, each chess move consists of a starting square
 * an ending square and an appropriate modifier. Once a Move class has bee
 * @author Jesse Wang
 */
public class Move {
	//----------------------Instance Variables----------------------
	/** The starting square of the move.*/
	private byte start_sq;
	/** The destination of the move.*/
	private byte end_sq;
	/** Special modifiers to describe special moves. */
	private byte modifiers;
	//----------------------End of Instance Variables----------------------
	
	//----------------------Constants----------------------
	/** A constant storing the special move of white castling kingside. */
	public static final Move WHITE_K_SIDE_CASTLING = new Move((byte) 1);
	/** A constant storing the special move of black castling kingside. */
	public static final Move BLACK_K_SIDE_CASTLING = new Move((byte) 2);
	/** A constant storing the special move of white castling queenside. */
	public static final Move WHITE_Q_SIDE_CASTLING = new Move((byte) 3);
	/** A constant storing the special move of black castling queenside. */
	public static final Move BLACK_Q_SIDE_CASTLING = new Move((byte) 4);
	//----------------------End of Constants----------------------
	
	//----------------------Constructors----------------------
	/**
	 * Makes a special move with 0 as it's starting square and destination as well as a
	 * specified modifier.
	 */
	private Move (byte modifiers){
		start_sq = 0;
		end_sq = 0;
		this.modifiers = modifiers;
	}
	/**
	 * Makes a "normal" move with no special modifier.
	 * @param startsq The starting square.
	 * @param endsq The destination square.
	 */
	public Move (byte startsq, byte endsq){
		start_sq = startsq;
		end_sq = endsq;
		modifiers = 0;
	}
	/**
	 * Makes a special move that requires a start, destination, and a modifier.
	 * @param startsq The starting square.
	 * @param endsq The ending square.
	 * @param modifier Modifiers: 0 = no modifier, 5 = en passant, 6 = promote to knight,
	 * 7 = promote to bishop, 8 = promote to rook, 9 = promote to queen.
	 */
	public Move (byte startsq, byte endsq, byte modifier){
		start_sq = startsq;
		end_sq = endsq;
		modifiers = modifier;
	}
	/**
	 * Checks whether another move object is the same as the given move object.
	 * @param m Another move object.
	 * @return Whether or not the move object is the same as <i>this</i> object.
	 */
	public boolean isEqual(Move m){
		return m.getStartSquare()==start_sq&&m.getEndSquare()==end_sq&&m.getModifier()==modifiers;
	}
	/**
	 * Gets the starting square of this move object.
	 * @return The starting square.
	 */
	public byte getStartSquare (){
		return start_sq;
	}
	/**
	 * Gets the ending square of this move object.
	 * @return The ending square.
	 */
	public byte getEndSquare (){
		return end_sq;
	}
	/**
	 * Gets the modifier stored in this move.
	 * @return The modifier.
	 */
	public byte getModifier(){
		return modifiers;
	}
}