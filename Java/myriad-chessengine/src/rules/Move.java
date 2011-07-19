package rules;

/**
 * Myriad's representation of chess moves, each chess move consists of a starting square
 * an ending square and an appropriate modifier. Once a Move object has been instantiated
 * it cannot be changed!
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
	private static final Move WHITE_K_SIDE_CASTLING = new Move((byte) 1);
	/** A constant storing the special move of black castling kingside. */
	private static final Move BLACK_K_SIDE_CASTLING = new Move((byte) 2);
	/** A constant storing the special move of white castling queenside. */
	private static final Move WHITE_Q_SIDE_CASTLING = new Move((byte) 3);
	/** A constant storing the special move of black castling queenside. */
	private static final Move BLACK_Q_SIDE_CASTLING = new Move((byte) 4);
	/** A constant array storing all special castling moves. */
	public static final Move[] CASTLE = 
		{WHITE_K_SIDE_CASTLING,BLACK_K_SIDE_CASTLING,WHITE_Q_SIDE_CASTLING,BLACK_Q_SIDE_CASTLING};
	//----------------------End of Constants----------------------
	
	//----------------------Constructors----------------------
	/**
	 * Makes a special move with 0 as it's starting square and destination as well as a
	 * specified modifier.
	 * @param modifiers: 1 = white king side castling, 2 = black king side castling,
	 * 3 = white queen side castling, 4 = black queen side castling
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
	 * @param modifier Modifiers: 0 = no modifier, 5 = en passant, 6 = promote to rook,
	 * 7 = promote to knight, 8 = promote to bishop, 9 = promote to queen.
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
	/**
	 * Returns a string describing this move.
	 * @return A string describing this move. 
	 */
	public String toString(){
		String st = "";
		if (modifiers == 1) return "o-o(w)";
		else if (modifiers == 2) return "o-o(b)";
		else if (modifiers == 3) return "o-o-o(w)";
		else if (modifiers == 4) return "o-o-o(b)";
		int start_rank = start_sq / 0x10;
		int start_file = start_sq % 0x10;
		st += ""+(char)('a'+start_file)+(start_rank+1)+"-";
		//0x88 representation
		//st += ""+ String.format("0x%02X", start_sq)+"-";
		int end_rank = end_sq / 0x10;
		int end_file = end_sq % 0x10;
		st += ""+(char)('a'+end_file)+(end_rank+1);
		//0x88 representation
		//st += ""+ String.format("0x%02X", end_sq);
		if (modifiers == 5) st += "ep";
		else if (modifiers == 6) st += "=R";
		else if (modifiers == 7) st += "=N";
		else if (modifiers == 8) st += "=B";
		else if (modifiers == 9) st += "=Q";
		return st;
	}
	/**
	 * Converts the move object into expanded algebraic notation.
	 * @param p The current position, used to convert into expanded algebraic notation.
	 * @return A string, representing the expanded algebraic form of this move.
	 */
	public String toString (Position p){
		Piece q = p.getSquareOccupier(start_sq);
		Piece e = p.getSquareOccupier(end_sq);
		if (modifiers == 1 || modifiers == 3) return "O-O";
		else if (modifiers == 2 || modifiers == 4) return "O-O-O";
		String s = "";
		switch (q.getType()){
			case Piece.ROOK: s+="R"; break;
			case Piece.BISHOP: s+="B"; break;
			case Piece.KNIGHT: s+="N"; break;
			case Piece.QUEEN: s+="Q"; break;
			case Piece.KING: s+="K"; break;
		}
		s += ""+(char)('a'+start_sq % 0x10)+(start_sq / 0x10+1);
		s += e.isEqual(Piece.getNullPiece()) ? "-" : ":"; 
		s += ""+(char)('a'+end_sq % 0x10)+(end_sq / 0x10+1);
		if (modifiers == 5) s += "e.p.";
		else if (modifiers == 6) s += "=R";
		else if (modifiers == 7) s += "=N";
		else if (modifiers == 8) s += "=B";
		else if (modifiers == 9) s += "=Q";
		return s;
	}
}