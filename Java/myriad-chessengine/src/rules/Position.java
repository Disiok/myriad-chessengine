package rules;

import java.util.Vector;

/**
 * Myriad's representation of a particular position. This is a basic class that underlines the
 * properties of a position, such as the availability of castling, 50 move rule count, etc.
 * 
 * The piece-centric board representation is used, with coordinates used by the "0x88"
 * algorithm. Note that this is an immutable object. Once an object is created via a constructor
 * it cannot be changed!
 * @author Spork Innovation Technologies
 */
public class Position{
	//----------------------Instance Variables----------------------
	/**
	 * Counts the number of moves since the last pawn move or capture.
	 */
	public final byte fifty_move_rule_count;
	/**
	 * Counts the number of times that the position has been repeated.
	 */
	public final byte repetition_count;
	/**
	 * A flag describing the future availability of white's castling kingside. E.g. whether 
	 * or not the king or rooks have already moved. This does not mean that castling is a 
	 * legal move in <i>this</i> position.
	 */
	public final boolean white_k_side_castling_allowed;
	/**
	 * A flag describing the future availability of white's castling queenside. E.g. whether 
	 * or not the king or rooks have already moved. This does not mean that castling is a 
	 * legal move in <i>this</i> position.
	 */
	public final boolean black_k_side_castling_allowed;
	/**
	 * A flag describing the future availability of black's castling kingside. E.g. whether 
	 * or not the king or rooks have already moved. This does not mean that castling is a 
	 * legal move in <i>this</i> position.
	 */
	public final boolean white_q_side_castling_allowed;
	/**
	 * A flag describing the future availability of black's castling queenside. E.g. whether 
	 * or not the king or rooks have already moved. This does not mean that castling is a 
	 * legal move in <i>this</i> position.
	 */
	public final boolean black_q_side_castling_allowed;
	/**
	 * A byte describing the location of the "en passant" square. This value is -1 if there is
	 * no "en passant" square available. 
	 */
	public final byte en_passant_square;
	/**
	 * A flag describing whose turn it is to move.
	 */
	public final boolean is_White_to_Move;
	/**
	 * Stores the current location of all the pieces on the board.
	 */
	public final Vector <Position.Piece> Pieces;
	//----------------------End of Instance Variables----------------------
	//----------------------Inner Classes----------------------
	/**
	 * This is an immutable Piece class that stores the type and location of the piece.
	 * Once a Piece object is created, it cannot be changed. This avoids memory reference
	 * issues. To move a piece, simply use the movePiece method and reassign the reference.
	 */
	public final class Piece {
		/**
		 * The square that the piece currently occupies.
		 */
		public final byte square;
		/**
		 * The type of chess piece that this object represents.
		 */
		public final char type;
		/**
		 * Constructor: Constructs a given chess piece based on it's current square and
		 * its type.
		 * @param square The square that the piece occupies in the 0x88 scheme.
		 * @param type Capital for white pieces, lower-case for black pieces. p = pawn,
		 * n = knight, b = bishop, r = rook, q = queen, and k = king.
		 */
		public Piece (byte square, char type){
			this.square = square;
			this.type = type;
		}
		/**
		 * Moves a piece to it's destination.
		 * @param dest The square that the piece will occupy.
		 * @return The new reference to the piece.
		 */
		public Piece movePiece (byte dest){
			return new Piece (dest, type);
		}
	}
	//----------------------End of Inner Classes----------------------
	//----------------------Constructor----------------------
	/**
	 * Constructor: Constructs a board objects with the following parameters:
	 * @param fifty_move The 50 move rule counter.
	 * @param three_fold The 3 fold repetition counter.
	 * @param epsq The en passant square.
	 * @param castling_rights An array storing the castling rights, with index 0 being white
	 * to the kingside, 1 being black to the kingside, 2 being white to the queenside, 3 being
	 * black to the queenside.
	 * @param whiteturn If it is currently white to move.
	 * @param map A vector containing all the current pieces.
	 */
	public Position (byte fifty_move, byte three_fold, byte epsq, boolean [] castling_rights, 
					boolean whiteturn, Vector<Position.Piece> map){
		fifty_move_rule_count = fifty_move;
		repetition_count = three_fold;
		en_passant_square = epsq;
		white_k_side_castling_allowed = castling_rights[0];
		black_k_side_castling_allowed = castling_rights[1];
		white_q_side_castling_allowed = castling_rights[2];
		black_q_side_castling_allowed = castling_rights[3];
		Pieces = map;
		is_White_to_Move = whiteturn;
	}
	/**
	 * Default Constructor: Constructs a Position object with the same settings as the initial
	 * start-up position.
	 */
	public Position (){
		fifty_move_rule_count = 0;
		repetition_count = 0;
		en_passant_square = -1;
		white_k_side_castling_allowed = true;
		black_k_side_castling_allowed = true;
		white_q_side_castling_allowed = true;
		black_q_side_castling_allowed = true;
		is_White_to_Move = true;
		Pieces = new Vector <Position.Piece> (32);
		// TODO: Add all 32 Pieces in their initial positions.
	}
	//----------------------End of Constructor----------------------
	//----------------------Methods----------------------
	public Position makeMove (Move m, Position p){
		// TODO: Make the move.
		return null;
	}
	public Move[] getLegalMoves (){
		// TODO: Generates all strictly legal moves.
		return null;
	}
	public Move[] generateAllMoves (){
		// TODO: Generates all possible moves, including illegal moves, ignoring checks.
		return null;
	}
	public boolean isInCheck(){
		// TODO: Checks if the king is in check.
		return false;
	}
	public boolean isLegalMove(){
		// TODO: Checks if it is a legal move.
		return false;
	}
	//----------------------End of Methods----------------------
}