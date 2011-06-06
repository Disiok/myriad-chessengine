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
public class Position
{
	//----------------------Instance Variables----------------------
	/**
	 * Counts the number of moves since the last pawn move or capture.
	 */
	private byte fifty_move_rule_count;
	/**
	 * A flag describing the future availability of white's castling kingside. E.g. whether 
	 * or not the king or rooks have already moved. This does not mean that castling is a 
	 * legal move in <i>this</i> position.
	 */
	private boolean white_k_side_castling_allowed;
	/**
	 * A flag describing the future availability of white's castling queenside. E.g. whether 
	 * or not the king or rooks have already moved. This does not mean that castling is a 
	 * legal move in <i>this</i> position.
	 */
	private boolean black_k_side_castling_allowed;
	/**
	 * A flag describing the future availability of black's castling kingside. E.g. whether 
	 * or not the king or rooks have already moved. This does not mean that castling is a 
	 * legal move in <i>this</i> position.
	 */
	private boolean white_q_side_castling_allowed;
	/**
	 * A flag describing the future availability of black's castling queenside. E.g. whether 
	 * or not the king or rooks have already moved. This does not mean that castling is a 
	 * legal move in <i>this</i> position.
	 */
	private boolean black_q_side_castling_allowed;
	/**
	 * A byte describing the location of the "en passant" square in 0x88 coordinates. This 
	 * value is -1 if there is no "en passant" square available. 
	 */
	private byte en_passant_square;
	/**
	 * A flag describing whose turn it is to move.
	 */
	private boolean is_White_to_Move;
	/**
	 * Stores the current location of all the white pieces on the board.
	 */
	private Piece[] white_map;
	/**
	 * Stores the current location of all the white pieces on the board.
	 */
	private Piece[] black_map;
	
	//----------------------End of Instance Variables----------------------
	//----------------------Constructors----------------------
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
	@SuppressWarnings("unchecked")
	public Position (byte fifty_move, byte epsq, boolean [] castling_rights, 
					boolean whiteturn, Piece[] w_map, Piece[] b_map){
		fifty_move_rule_count = fifty_move;
		en_passant_square = epsq;
		white_k_side_castling_allowed = castling_rights[0];
		black_k_side_castling_allowed = castling_rights[1];
		white_q_side_castling_allowed = castling_rights[2];
		black_q_side_castling_allowed = castling_rights[3];
		white_map = Arrays.copyOf(w_map);
		black_map = Arrays.copyOf(b_map);
		is_White_to_Move = whiteturn;
	}
	/**
	 * Default Constructor: Constructs a Position object with the same settings as the initial
	 * start-up position.
	 */
	public Position (){
		fifty_move_rule_count = 0;
		en_passant_square = -1;
		white_k_side_castling_allowed = true;
		black_k_side_castling_allowed = true;
		white_q_side_castling_allowed = true;
		black_q_side_castling_allowed = true;
		is_White_to_Move = true;
		white_map = new Piece[16];
		black_map = new Piece[16];
		for (int i = 0; i < 8; i ++){
			white_map[i] = new Piece ((byte)(0x10+i),Piece.PAWN,Piece.WHITE);
			black_map[i] = new Piece (byte)(0x60+i),Piece.PAWN,Piece.BLACK);
		}
		for (int i = 8; i < 13; i ++){
			white_map[i] = new Piece ((byte)(0x00+i),(byte)(Piece.ROOK+i),Piece.WHITE);
			black_map[i] = new Piece ((byte)(0x70+i),(byte)(Piece.ROOK+i),Piece.BLACK);
		}
		for (int i = 13; i < 15; i ++){
			white_map[i] = new Piece ((byte)(0x05+i),(byte)(Piece.BISHOP-i),Piece.WHITE);
			white_map[i] = new Piece ((byte)(0x75+i),(byte)(Piece.BISHOP-i),Piece.BLACK);
		}
	}
	//----------------------End of Constructor----------------------
	
	//----------------------Methods----------------------
	/**
	 * Gets the castling rights of a board in the order specified in the Constructor.
	 * @returns The castling rights of this position.
	 */
	public boolean [] getCastlingRights (){
		boolean [] toReturn = new boolean [4];
		toReturn[0] = white_k_side_castling_allowed;
		toReturn[1] = black_k_side_castling_allowed;
		toReturn[2] = white_q_side_castling_allowed;
		toReturn[3] = black_q_side_castling_allowed;
		return toReturn;
	}
	/**
	 * Returns <i>this</i> position's current 50 move rule counter.
	 * @return The 50 move rule counter.
	 */
	public byte get50MoveCount(){
		return fifty_move_rule_count;
	}
	/**
	 * Returns the "en passant-able" square using 0x88 cooridinates in <i>this</i> position.
	 * @return The en passant square in 0x88 coordinates.
	 */
	public byte getEnPassantSquare(){
		return en_passant_square;
	}
	/**
	 * Returns whether or not if it is white to play in <i>this</i> position.
	 * @return A boolean signalling whether or not it is white's turn.
	 */
	public boolean isWhiteToMove (){
		return is_White_to_Move;
	}
	/**
	 * Returns an array containing all the white pieces.
	 * @return an array containing all the white pieces.
	 */
	public Piece [] getWhitePieces (){
		Piece [] toReturn = new Piece[white_map.size()];
		toReturn = white_map.toArray(toReturn);
		return toReturn;
	}
	/**
	 * Returns an array containing all the black pieces.
	 * @return an array containing all the black pieces.
	 */
	public Piece [] getBlackPieces (){
		Piece [] toReturn = new Piece[black_map.size()];
		toReturn = black_map.toArray(toReturn);
		return toReturn;
	}
	public Position makeMove (Move m){
		// TODO: Make the move.
		return null;
	}
	public Move[] getLegalMoves (){
		// TODO: Generates all strictly legal moves.
		return null;
	}
	public Move[] generateAllMoves (){
		// TODO: Generates all possible moves, including illegal moves, ignoring checks.
		Piece[] current_map;
		vector <Move> all_moves = new vector <Move> ();
		if (is_White_to_Move){
			current_map = white_map;
		}
		else{
			current_map = black_map;
		}
		for (Piece current_piece : current_map){
			current_type = current_piece.getType();
			current_position = current_piece.getPosition();
			swith (current_type){
				case Piece.PAWN:
					if (is_White_to_Move){
						if ((getSquareOccupier(current_position+0x10) == null)&&(current_position+0x10 & 0x88 == 0)){
							all_moves.add(new Move(current_position, current_position+0x10));
						}
						if ((getSquareOccupier(current_position+0x20) == null)&&(current_position / 0x10 == 0x1)){
							all_moves.add(new Move(current_position, current_position+0x20));
						}
						if ((getSquareOccupier(current_position+0xf).getColour() == Piece.BLACK)){
							all_moves.add(new Move(current_position, current_position+0xf));
						}
						if ((getSquareOccupier(current_position+0x11).getColour() == Piece.BLACK)){
							all_moves.add(new Move(current_position, current_position+0x11));
						}
						if (current_position - 0x1 == en_passant_square){
							all_moves.add(new Move(current_position, current_position+0xf));
						}
						if (current_position + 0x1 == en_passant_square){
							all_moves.add(new Move(current_position, current_position+0x11));
						}
					}
					else{
						if ((getSquareOccupier(current_position - 0x10) == null)&&(current_position - 0x10 & 0x88 == 0)){
							all_moves.add(new Move(current_position, current_position - 0x10));
						}
						if ((getSquareOccupier(current_position - 0x20) == null)&&(current_position / 0x10 == 0x6)){
							all_moves.add(new Move(current_position, current_position - 0x20));
						}
						if ((getSquareOccupier(current_position - 0xf).getColour() == Piece.BLACK)){
							all_moves.add(new Move(current_position, current_position - 0xf));
						}
						if ((getSquareOccupier(current_position - 0x11).getColour() == Piece.BLACK)){
							all_moves.add(new Move(current_position, current_position - 0x11));
						}
						if (current_position - 0x1 == en_passant_square){
							all_moves.add(new Move(current_position, current_position - 0x11));
						}
						if (current_position + 0x1 == en_passant_square){
							all_moves.add(new Move(current_position, current_position - 0xf));
						}
					}
					break;
				case Piece.ROOK:
					// TODO: Generate all possible moves for all rooks
					break;
				case Piece.KNIGHT:
					// TODO: Generate all possible moves for all knights
					break;
				case Piece.BISHOP:
					// TODO: Generate all possible moves for all bishops
					break;
				case Piece.Queen:
					// TODO: Generate all possible moves for all queens
					break;
				case Piece.King:
					// TODO: Generate all possible moves for all kings
					break;
			}
			
		}
		return null;
	}
	/**
	 * Return the occupier of a specific square, return null if the square is empty.
	 * @return the occupier of a specific square, return null if the square is empty.
	 */
	public Piece getSquareOccupier (byte square){
		Piece square_occupier = null;
		for (int i = 0; i<16; i++){
			if (white_map[i].getPosition() == square){
				square_occupier = white_map[i];
				break;
			}
			else if (black_map[i].getPosition() == square){
				square_occupier = black_map[i];
				break;
			}
		}
		return square_occupier;
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