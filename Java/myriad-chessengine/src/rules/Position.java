package rules;

import java.util.*;

/**
 * Myriad's representation of a particular position. This is a basic class that underlines the
 * properties of a position, such as the availability of castling, 50 move rule count, etc.
 * 
 * The piece-centric board representation is used, with coordinates used by the "0x88"
 * algorithm. Note that this is an immutable object. Once an object is created via a constructor
 * it cannot be changed!
 * @author Spork Innovation Technologies
 */
public final class Position
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
	
	//----------------------Constants----------------------
	/** The distance between 1 up move. */
	private static final byte UP_MOVE = 0x10;
	/** The distance between 1 down move. */
	private static final byte DOWN_MOVE = -0x10;
	/** The distance between 1 left move. */
	private static final byte LEFT_MOVE = -0x01;
	/** The distance between 1 right move. */
	private static final byte RIGHT_MOVE = 0x01;
	/** The distance between 1 diagonal left and up move. */
	private static final byte LEFT_UP_MOVE = 0xf;
	/** The distance between 1 diagonal right and up move. */
	private static final byte RIGHT_UP_MOVE = 0x11;
	/** The distance between 1 diagonal left and down move. */
	private static final byte LEFT_DOWN_MOVE = -0x11;
	/** The distance between 1 diagonal right and down move.*/
	private static final byte RIGHT_DOWN_MOVE = -0xf;
	/** The storage for the differences of all knight moves. */
	private static final byte [] KNIGHT_MOVES = {2*UP_MOVE+RIGHT_MOVE,2*UP_MOVE+LEFT_MOVE,
			2*DOWN_MOVE+RIGHT_MOVE, 2*DOWN_MOVE+LEFT_MOVE, 2*RIGHT_MOVE+UP_MOVE, 2*RIGHT_MOVE+DOWN_MOVE,
			2*LEFT_MOVE+UP_MOVE, 2*LEFT_MOVE+DOWN_MOVE};
	/** The storage for the differences of all diagonal moves. */
	private static final byte [] DIAGONALS = {RIGHT_UP_MOVE, RIGHT_DOWN_MOVE, LEFT_UP_MOVE,
			RIGHT_DOWN_MOVE};
	/** The storage for the differences of all horizontal/vertical moves.*/
	private static final byte [] HORIZONTALS = {UP_MOVE, DOWN_MOVE, LEFT_MOVE, RIGHT_MOVE};
	//----------------------End of Constants----------------------
	
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
	 * @param w_map An array containing all the current white pieces.
	 * @param b_map An array containing all the current black pieces.
	 */
	public Position (byte fifty_move, byte epsq, boolean [] castling_rights, 
					boolean whiteturn, Piece[] w_map, Piece[] b_map){
		fifty_move_rule_count = fifty_move;
		en_passant_square = epsq;
		white_k_side_castling_allowed = castling_rights[0];
		black_k_side_castling_allowed = castling_rights[1];
		white_q_side_castling_allowed = castling_rights[2];
		black_q_side_castling_allowed = castling_rights[3];
		white_map = Arrays.copyOf(w_map, w_map.length);
		black_map = Arrays.copyOf(b_map, w_map.length);
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
			black_map[i] = new Piece ((byte)(0x60+i),Piece.PAWN,Piece.BLACK);
		}
		for (int i = 8; i < 13; i ++){
			white_map[i] = new Piece ((byte)(0x00+i-8),(byte)(Piece.ROOK+i-8),Piece.WHITE);
			black_map[i] = new Piece ((byte)(0x70+i-8),(byte)(Piece.ROOK+i-8),Piece.BLACK);
		}
		for (int i = 13; i < 16; i++){
			white_map[i] = new Piece ((byte)(0x05+i-13),(byte)(Piece.BISHOP-i+13),Piece.WHITE);
			black_map[i] = new Piece ((byte)(0x75+i-13),(byte)(Piece.BISHOP-i+13),Piece.BLACK);
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
		return Arrays.copyOf(white_map,white_map.length);
	}
	/**
	 * Returns an array containing all the black pieces.
	 * @return an array containing all the black pieces.
	 */
	public Piece [] getBlackPieces (){
		return Arrays.copyOf(black_map,black_map.length);
	}
	public Position makeMove (Move m){
		byte start = m.getStartSquare();
		byte end = m.getEndSquare();
		Piece [] map = Arrays.copyOf(is_White_to_Move? white_map: black_map, white_map.length);
		Piece [] oth = Arrays.copyOf(is_White_to_Move? black_map: white_map, white_map.length);
		Piece p = getSquareOccupier(start);
		Piece o = getSquareOccupier(end);
		int ind_PieceToUpdate = getIndiceOfPiece(p, is_White_to_Move);
		int ind_CapturedPiece = getIndiceOfPiece(o,!is_White_to_Move);
		boolean[] cstl_rights = new boolean [4];
		byte epsq = -1;
		if (p.getType()==Piece.KING){
			if (start==0x04 || start == 0x74){
				cstl_rights [is_White_to_Move ? 0 : 1] = false;
				cstl_rights [is_White_to_Move ? 2 : 3] = false;
			}
		} else if (p.getType()==Piece.ROOK){
			if (start==0x00 || start== 0x70) cstl_rights [is_White_to_Move ? 0 : 1] = false;
			else if (start == 0x07 || start == 0x77) cstl_rights [is_White_to_Move ? 2 : 3] = false;
		} else if (p.getType()==Piece.PAWN){
			if (start / UP_MOVE == 0x06 && end / UP_MOVE == 0x04) epsq = (byte) (start + DOWN_MOVE);
			else if (start / UP_MOVE == 0x01 && end / UP_MOVE == 0x03) epsq = (byte) (start + UP_MOVE);
		}
		map [ind_PieceToUpdate] = p.move(m);
		if (ind_CapturedPiece > 0) oth [ind_CapturedPiece] = o.destroy();
		return new Position (fifty_move_rule_count++, epsq, cstl_rights, !is_White_to_Move, 
			is_White_to_Move ? map : oth, is_White_to_Move ? oth : map);
	}
	public Move[] getLegalMoves (){
		// TODO: Generates all strictly legal moves.
		return null;
	}
	public Move[] generateAllMoves (){
		Piece[] current_map = is_White_to_Move ? white_map : black_map;
		Vector <Move> all_moves = new Vector <Move> (20,3);
		byte c_col = is_White_to_Move ? Piece.WHITE : Piece.BLACK;
		for (Piece current_piece : current_map){
			byte c_type = current_piece.getType();
			byte c_pos = current_piece.getPosition();
			byte next_pos;
			Piece o_pos;
			switch (c_type){
				case Piece.PAWN:
					if (is_White_to_Move){
						next_pos = (byte) (c_pos + UP_MOVE);
						o_pos = getSquareOccupier(next_pos);
						if (o_pos.getColour()== -1 &&(next_pos&0x88)==0){
							if (next_pos / 0x10 == 0x07){
								all_moves.add(new Move(c_pos,next_pos,(byte)6));
								all_moves.add(new Move(c_pos,next_pos,(byte)7));
								all_moves.add(new Move(c_pos,next_pos,(byte)8));
								all_moves.add(new Move(c_pos,next_pos,(byte)9));
							}
							else all_moves.add(new Move(c_pos,next_pos));
							next_pos = (byte) (c_pos + 2*UP_MOVE);
							o_pos = getSquareOccupier(next_pos);
							if (o_pos.getColour()!=Piece.WHITE&&(c_pos/0x10 == 0x1))
								all_moves.add(new Move(c_pos,next_pos));
						}
						next_pos = (byte) (c_pos + RIGHT_UP_MOVE);
						o_pos = getSquareOccupier(next_pos);
						if (o_pos.getColour()==Piece.BLACK||next_pos==en_passant_square)
							all_moves.add(new Move(c_pos, next_pos,(byte)5));
						next_pos = (byte) (c_pos + LEFT_UP_MOVE);
						o_pos = getSquareOccupier(next_pos);
						if (o_pos.getColour()==Piece.BLACK||next_pos==en_passant_square)
							all_moves.add(new Move(c_pos, next_pos,(byte)5));
					}else{
						next_pos = (byte) (c_pos + DOWN_MOVE);
						o_pos = getSquareOccupier(next_pos);
						if (o_pos.getColour()!=Piece.BLACK && (next_pos&0x88)==0) {
							if (next_pos/0x10 == 0x00){
								all_moves.add(new Move(c_pos,next_pos,(byte)6));
								all_moves.add(new Move(c_pos,next_pos,(byte)7));
								all_moves.add(new Move(c_pos,next_pos,(byte)8));
								all_moves.add(new Move(c_pos,next_pos,(byte)9));
							}
							else all_moves.add(new Move(c_pos,next_pos));
							next_pos = (byte) (c_pos + 2*DOWN_MOVE);
							o_pos = getSquareOccupier(next_pos);
							if (o_pos.getColour()!=Piece.BLACK && (c_pos/0x10==0x7)) 
								all_moves.add(new Move(c_pos,next_pos));
						}
						
						next_pos = (byte) (c_pos + LEFT_DOWN_MOVE);
						o_pos = getSquareOccupier(next_pos);
						if ((o_pos.getColour()==Piece.WHITE)||next_pos==en_passant_square)
							all_moves.add(new Move(c_pos, next_pos,(byte)5));
						
						next_pos = (byte) (c_pos + RIGHT_DOWN_MOVE);
						o_pos = getSquareOccupier(next_pos);
						if ((o_pos.getColour()==Piece.WHITE)||next_pos==en_passant_square)
							all_moves.add(new Move(c_pos, next_pos,(byte)5));
					}
					break;
				case Piece.ROOK:
					all_moves.addAll(generateStraightMoves(c_pos, HORIZONTALS));
					break;
				case Piece.KNIGHT:
					for (int i = 0; i < 8; i++){
						next_pos = (byte)(c_pos + KNIGHT_MOVES[i]);
						o_pos = getSquareOccupier (next_pos);
						if ((next_pos & 0x88)==0 && o_pos.getColour()!=c_col) 
							all_moves.add(new Move(c_pos, next_pos));
					}
					break;
				case Piece.BISHOP:
					all_moves.addAll(generateStraightMoves(c_pos, DIAGONALS));
					break;
				case Piece.QUEEN:
					all_moves.addAll(generateStraightMoves(c_pos, HORIZONTALS));
					all_moves.addAll(generateStraightMoves(c_pos, DIAGONALS));
					break;
				case Piece.KING:
					// TODO: Generate all possible moves for all kings
					break;
			}
		}
		for (Move m: all_moves){
			if (makeMove(m).isInCheck()) all_moves.remove(m);
		}
		Move [] toReturn = new Move [all_moves.size()];
		toReturn = (Move[]) all_moves.toArray(toReturn);
		return toReturn;
	}
	/**
	 * Generates an vector of moves for continuous motion along a straight line with the
	 * appropriate distances in 0x88 as specified.
	 * @param c_pos The current location.
	 * @param differences The difference for each direction from c_pos.
	 * @return A vector containing all the possible straight moves.
	 */
	private Vector<Move> generateStraightMoves(byte c_pos, byte[] differences){
		Vector <Move> AllMoves = new Vector <Move> (10,3);
		for (int i = 0; i < differences.length; i++){
			byte next_pos = c_pos;
			do{
				next_pos += differences[i];
				Piece o_pos = getSquareOccupier(next_pos);
				if (o_pos.getColour() != (is_White_to_Move ? Piece.WHITE: Piece.BLACK)){
					AllMoves.add(new Move(c_pos, next_pos));
				}else break;
			}while ((next_pos&0x88)==0);
		}
		return AllMoves;
	}
	/**
	 * Returns the occupier of a specific square, or the null piece if the square is empty.
	 * @return the occupier of a specific square, the null piece if the square is empty.
	 */
	private Piece getSquareOccupier (byte square){
		Piece square_occupier = Piece.getNullPiece();
		for (int i = 0; i<16; i++){
			if (white_map[i].getPosition() == square){
				square_occupier = white_map[i];
				break;
			} else if (black_map[i].getPosition() == square){
				square_occupier = black_map[i];
				break;
			}
		}
		return square_occupier;
	}
	/**
	 * Looks for the index of a certain piece in an appropriate map.
	 * @param p The piece to look for.
	 * @param map The map to look in. True for White, false for Black.
	 * @return The index in the appropriate map that contains the specified piece.
	 */
	private int getIndiceOfPiece (Piece p, boolean map){
		int ind = -1;
		Piece [] arrayToSearch = map ? white_map : black_map;
		for (int i = 0; i < 16; i++){
			if (p.isEqual(arrayToSearch[i])) ind = i;
		}
		return ind;
	}
	/**
	 * Checks if in the current position, whether or not the king is in check.
	 * @return true if the king is in check, false otherwise.
	 */
	public boolean isInCheck(){
		Piece[] c_map = is_White_to_Move ? white_map : black_map;
		byte k_loc = -1;
		for (int i = 0; i < c_map.length; i++){
			if (c_map[i].getType() == Piece.KING){
				k_loc = c_map[i].getPosition();
				break;
			}
		}
		boolean[] castl_rights = {white_k_side_castling_allowed, black_k_side_castling_allowed, 
				white_q_side_castling_allowed, black_q_side_castling_allowed};
		Move [] m = new Position(fifty_move_rule_count, en_passant_square, castl_rights, !is_White_to_Move, 
				white_map, black_map).generateAllMoves();
		for (Move p: m){
			if (p.getEndSquare()==k_loc) return true;
		}
		return false;
	}
	public boolean isLegalMove(){
		// TODO: Checks if it is a legal move.
		return false;
	}
	//----------------------End of Methods----------------------
}