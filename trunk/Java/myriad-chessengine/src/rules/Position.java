package rules;

/**
 * Myriad's representation of a particular position. This is a basic class that underlines the
 * properties of a position, such as the availability of castling, 50 move rule count, etc.
 * @author Jesse Wang
 */
public abstract class Position {
	public byte fifty_move_rule_count = 0;
	public byte repetition_count = 0;
	// note: this does not mean that castling is legal in the position. It simply means if it
	// can be allowed in the future.
	public boolean white_k_side_castling_allowed = true;
	public boolean black_k_side_castling_allowed = true;
	public boolean white_q_side_castling_allowed = true;
	public boolean black_q_side_castling_allowed = true;
	// positive if there exists a square, negative if the square does not exist.
	public byte en_passant_square = -1;
	
	public abstract void makeMove (Move m);
}
