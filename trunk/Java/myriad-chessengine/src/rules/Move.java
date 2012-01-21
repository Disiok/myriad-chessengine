package rules;

/**
 * Myriad's representation of chess moves, each chess move consists of a starting square
 * an ending square and an appropriate modifier. Once a Move object has been instantiated
 * it cannot be changed!
 * @author Jesse Wang
 */
public final class Move {
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
	private static final Move WHITE_K_SIDE_CASTLING = new Move((byte)7, (byte)5, (byte)1);
	/** A constant storing the special move of black castling kingside. */
	private static final Move BLACK_K_SIDE_CASTLING = new Move((byte)0x77, (byte)0x75, (byte)2);
	/** A constant storing the special move of white castling queenside. */
	private static final Move WHITE_Q_SIDE_CASTLING = new Move((byte)0, (byte)3, (byte)3);
	/** A constant storing the special move of black castling queenside. */
	private static final Move BLACK_Q_SIDE_CASTLING = new Move((byte)0x70, (byte)0x73, (byte)4);
	/** A constant array storing all special castling moves. */
	public static final Move[] CASTLE = 
		{WHITE_K_SIDE_CASTLING,BLACK_K_SIDE_CASTLING,WHITE_Q_SIDE_CASTLING,BLACK_Q_SIDE_CASTLING};
	//----------------------End of Constants----------------------

	//----------------------Constructors----------------------
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
	 * @param modifier Modifiers: 
	 * 0 = no modifier,
	 * 1 = White_king castling, 
	 * 2 = Black_king castling, 
	 * 3 = White_queen castling, 
	 * 4 = Black_queen castling, 
	 * 5 = en passant, 
	 * 6 = promote to rook,
	 * 7 = promote to knight, 
	 * 8 = promote to bishop, 
	 * 9 = promote to queen,
	 * 10 = capture,
	 * 16 = promote to rook with capture,
	 * 17 = promote to knight with capture, 
	 * 18 = promote to bishop with capture, 
	 * 19 = promote to queen with capture, 
	 * 20 = double advance,
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
		return m.getStartSquare()==start_sq&&m.getEndSquare()==end_sq&&(m.getModifier()==modifiers % 10);
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
		if (modifiers == 1) return "O-O (w)";
		else if (modifiers == 2) return "O-O (b)";
		else if (modifiers == 3) return "O-O-O (w)";
		else if (modifiers == 4) return "O-O-O (b)";
		String st = "";
		st += x88ToString(start_sq);
		st += x88ToString(end_sq);
		if (modifiers == 5) st += "e.p.";
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
		if (modifiers == 1) return "O-O (w)";
		else if (modifiers == 2) return "O-O (b)";
		else if (modifiers == 3) return "O-O-O (w)";
		else if (modifiers == 4) return "O-O-O (b)";
		String s = "";
		switch (q.getType()){
		case Piece.ROOK: s+="R"; break;
		case Piece.BISHOP: s+="B"; break;
		case Piece.KNIGHT: s+="N"; break;
		case Piece.QUEEN: s+="Q"; break;
		case Piece.KING: s+="K"; break;
		}
		s += x88ToString(start_sq);
		s += e.isEqual(Piece.getNullPiece()) ? "-" : ":"; 
		s += x88ToString(end_sq);
		if (modifiers == 5) s += "e.p.";
		else if (modifiers == 6) s += "=R";
		else if (modifiers == 7) s += "=N";
		else if (modifiers == 8) s += "=B";
		else if (modifiers == 9) s += "=Q";
		return s;
	}
	// utility methods
	public static Move toMove(String m_s){
		if (m_s.equals("O-O (w)")) return WHITE_K_SIDE_CASTLING;
		else if (m_s.equals("O-O (b)")) return BLACK_K_SIDE_CASTLING;
		else if (m_s.equals("O-O-O (w)")) return WHITE_Q_SIDE_CASTLING;
		else if (m_s.equals("O-O-O (b)")) return BLACK_Q_SIDE_CASTLING;
		if (m_s.length() == 5)
			return new Move(stringTo0x88(m_s.substring(0, 2)),stringTo0x88(m_s.substring(3)));
		else if (m_s.length() == 6)
			return new Move(stringTo0x88(m_s.substring(1, 3)),stringTo0x88(m_s.substring(4)));
		else if (m_s.length() == 7){
			char s = m_s.charAt(m_s.length()-1);
			byte md = 0 ;
			if (s == 'R') md = 6;
			else if (s == 'N') md = 7;
			else if (s == 'B') md = 8;
			else if (s == 'Q') md = 9;
			return new Move(stringTo0x88(m_s.substring(0, 2)),stringTo0x88(m_s.substring(3,5)), md);
		}
		else if (m_s.length() == 9)
			return new Move(stringTo0x88(m_s.substring(0, 2)),stringTo0x88(m_s.substring(3,5)), (byte)5);
		else return new Move((byte)0,(byte)0);
	} 
	public static byte stringTo0x88(String sq){
		return (byte) (sq.charAt(0)-'a' + (sq.charAt(1) - '1') * 0x10);
	}
	public static String x88ToString(byte sq){
		return ""+(char)('a'+sq % 0x10)+(sq / 0x10+1);
	}
}