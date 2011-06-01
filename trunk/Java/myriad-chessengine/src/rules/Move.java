package rules;

/**
 * Myriad's representation of chess moves, each chess move consists of a starting square
 * an ending square and an appropriate modifier. 
 * 
 * @author Jesse Wang
 */
public class Move {
	public byte start_sq;
	public byte end_sq;
	public byte modifiers; 
			// 0 = no modifiers, 1 = k-side castling white, 2 = k-side castling black,
			// 3 = q-side castling white, 4 = q-side castling black, 5 = en passant.
			// 6 = promotion to bishop, 7 = promotion to knight, 8 = promotion to rook,
			// 9 = promotion to queen.
	public static final Move WHITE_K_SIDE_CASTLING = new Move((byte) 1);
	public static final Move BLACK_K_SIDE_CASTLING = new Move((byte) 2);
	public static final Move WHITE_Q_SIDE_CASTLING = new Move((byte) 3);
	public static final Move BLACK_Q_SIDE_CASTLING = new Move((byte) 4);
	
	// Constructor used for castling.
	private Move (byte modifiers){
		start_sq = 0;
		end_sq = 0;
		this.modifiers = modifiers;
	}
	// Constructor used for normal moves.
	public Move (byte startsq, byte endsq){
		start_sq = startsq;
		end_sq = endsq;
		modifiers = 0;
	}
	// Constructor special moves such as promotion which require a modifier.
	public Move (byte startsq, byte endsq, byte modifier){
		start_sq = startsq;
		end_sq = endsq;
		modifiers = modifier;
	}
}