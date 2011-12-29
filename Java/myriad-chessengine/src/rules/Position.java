package rules;

import java.util.*;

import tree.Zobrist;
/**
 * Myriad's representation of a particular position. This is a basic class that underlines the
 * properties of a position, such as the availability of castling, 50 move rule count, etc.
 * 
 * The piece-centric board representation is used, with coordinates used by the "0x88"
 * algorithm. Note that this is an immutable object. Once an object is created via a constructor
 * it cannot be changed!
 * @author Spork Innovation Technologies
 */
public final class Position {
	//----------------------Instance Variables----------------------
	/**
	 * Counts the number of moves since the last pawn move or capture.
	 */
	private byte ply_count;
	/**
	 * A flag describing the future availability of white's castling kingside. E.g. whether 
	 * or not the king or rooks have already moved. This does not mean that castling is a 
	 * legal move in <i>this</i> position.
	 */
	private boolean w_kingside;
	/**
	 * A flag describing the future availability of white's castling queenside. E.g. whether 
	 * or not the king or rooks have already moved. This does not mean that castling is a 
	 * legal move in <i>this</i> position.
	 */
	private boolean b_kingside;
	/**
	 * A flag describing the future availability of black's castling kingside. E.g. whether 
	 * or not the king or rooks have already moved. This does not mean that castling is a 
	 * legal move in <i>this</i> position.
	 */
	private boolean w_queenside;
	/**
	 * A flag describing the future availability of black's castling queenside. E.g. whether 
	 * or not the king or rooks have already moved. This does not mean that castling is a 
	 * legal move in <i>this</i> position.
	 */
	private boolean b_queenside;
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
	/**
	 * Stores all legal moves in the current position.
	 */
	private Move [] all_moves;
	/**
	 * Stores the check status in the current position.
	 */
	private int is_in_check = -1;
	/**
	 * Stores the Zobrist hash of the current position. Used for transposition tables.
	 */
	private final long zobrist;
	//----------------------End of Instance Variables----------------------
	//----------------------Constants----------------------
	/** The distance between 1 up move. */
	public static final byte UP_MOVE = 0x10;
	/** The distance between 1 down move. */
	public static final byte DOWN_MOVE = -0x10;
	/** The distance between 1 left move. */
	public static final byte LEFT_MOVE = -0x01;
	/** The distance between 1 right move. */
	public static final byte RIGHT_MOVE = 0x01;
	/** The distance between 1 diagonal left and up move. */
	public static final byte LEFT_UP_MOVE = 0xf;
	/** The distance between 1 diagonal right and up move. */
	public static final byte RIGHT_UP_MOVE = 0x11;
	/** The distance between 1 diagonal left and down move. */
	public static final byte LEFT_DOWN_MOVE = -0x11;
	/** The distance between 1 diagonal right and down move.*/
	public static final byte RIGHT_DOWN_MOVE = -0xf;
	/** The storage for the differences of all knight moves. */
	public static final byte [] KNIGHT_MOVES = {0x21,0x1f,-0x1f, -0x21, 0x12, -0xe, 0xe, -0x12};
	/** The storage for the differences of all diagonal moves. */
	public static final byte [] DIAGONALS = {0x11, -0x11, 0xf, -0xf};
	/** The storage for the differences of all horizontal/vertical moves.*/
	public static final byte [] HORIZONTALS = {0x10, -0x10, 0x1, -0x1};
	/** The storage for the differences of all radial moves. */
	public static final byte [] RADIALS = {0x11, -0xf, 0xf,-0x11,0x10,-0x01, -0x10, 0x01};
	/** The storage for the difference of all pawn capture moves for white. */
	public static final byte[] WHITE_PAWN_ATTACK = {0xf, 0x11} ;
	/** The storage for the difference of all pawn capture moves for black. */
	public static final byte[] BLACK_PAWN_ATTACK = {-0x11, -0xf};
	/** The signal given by the gameResult() method that means a draw (or stalemate).*/ 
	public static final int DRAW = 0;
	/** The signal given by the gameResult() method that means white wins.*/
	public static final int WHITE_WINS = 1;
	/** The signal given by the gameResult() method that means black wins.*/
	public static final int BLACK_WINS = -1;
	/** The signal given by the gameResult() method that means no result has been reached yet.*/
	public static final int NO_RESULT = -2;
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
		ply_count = fifty_move;
		en_passant_square = epsq;
		w_kingside = castling_rights[0];
		b_kingside = castling_rights[1];
		w_queenside = castling_rights[2];
		b_queenside = castling_rights[3];
		white_map = w_map;
		black_map = b_map;
		is_White_to_Move = whiteturn;
		zobrist = Zobrist.createinitialhash(white_map, black_map, castling_rights, en_passant_square);
	}
	/**
	 * Default Constructor: Constructs a Position object with the same settings as the initial
	 * start-up position.
	 */
	public Position (){
		ply_count = 0;
		en_passant_square = -1;
		w_kingside = true;
		b_kingside = true;
		w_queenside = true;
		b_queenside = true;
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
		Piece temp = white_map[12];
		white_map[12] = white_map[0];
		white_map[0] = temp;
		temp = black_map[12];
		black_map[12] = black_map[0];
		black_map[0] = temp;
		zobrist = Zobrist.createinitialhash(white_map, black_map, getCastlingRights(), en_passant_square);
	}
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
	 * @param new_hash The appropriate new Zobrist hash.
	 */
	private Position (byte fifty_move, byte epsq, boolean [] castling_rights, 
			boolean whiteturn, Piece[] w_map, Piece[] b_map, long new_hash){
		ply_count = fifty_move;
		en_passant_square = epsq;
		w_kingside = castling_rights[0];
		b_kingside = castling_rights[1];
		w_queenside = castling_rights[2];
		b_queenside = castling_rights[3];
		white_map = w_map;
		black_map = b_map;
		is_White_to_Move = whiteturn;
		zobrist = new_hash;
	}
	//----------------------End of Constructors----------------------

	//----------------------Methods----------------------
	/**
	 * Gets the Zobrist hash of this Position object.
	 * @return the Zobrist hash of this Position object.
	 */
	public long getHash (){
		return zobrist;
	}
	/**
	 * Gets the castling rights of a board in the order specified in the Constructor.
	 * @returns The castling rights of this position.
	 */
	public boolean [] getCastlingRights (){
		boolean [] toReturn = {w_kingside, b_kingside, w_queenside, b_queenside};
		return toReturn;
	}
	/**
	 * Returns <i>this</i> position's current 50 move rule counter.
	 * @return The 50 move rule counter.
	 */
	public byte get50MoveCount(){
		return ply_count;
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
	 * Checks if in the current position, whether or not the king is in check.
	 * @return true if the king is in check, false otherwise.
	 */
	public boolean isInCheck(){
		if (is_in_check != -1) return (is_in_check == 1);
		Piece[] c_map = is_White_to_Move ? white_map : black_map;
		byte o_col = is_White_to_Move?Piece.BLACK:Piece.WHITE,c_col=(byte)(-1*o_col),
				k_loc=c_map[0].getPosition(),type;
		Piece obstruct;
		int next_pos = 0;
		// radial attacks
		for (int i = 0; i < 8; i++){
			next_pos = k_loc + RADIALS[i];
			while ((next_pos & 0x88) == 0){
				obstruct = getSquareOccupier((byte)next_pos);
				if (obstruct.getColour() == c_col) break;
				if ((type = obstruct.getType()) != Piece.NULL){
					if (type == Piece.PAWN) {
						if(i < 4 && c_col*(1-i) >= 0 && c_col*((next_pos>>4)-(k_loc>>4)) == 1){
							is_in_check = 1;
							return true;
						}
						break;
					} else if (type == Piece.BISHOP){
						if (i < 4) {
							is_in_check = 1;
							return true;
						}
						break;
					} else if (type == Piece.QUEEN) {
						is_in_check = 1;
						return true;
					} else if (type == Piece.ROOK) {
						if (i > 3){
							is_in_check = 1;
							return true;
						}
						break;
					}
					break;
				}
				next_pos += RADIALS[i];
			}
		}
		// knight moves
		for (byte diff : KNIGHT_MOVES){
			if (getSquareOccupier((byte)(k_loc+diff), !is_White_to_Move).getType()==Piece.KNIGHT){
				is_in_check = 1;
				return true;
			}
		}
		is_in_check = 0;
		return false;
	}
	/**
	 * Checks if in the current position, whether or not the king is in check.
	 * @return true if the king is in check, false otherwise.
	 */
	private boolean isInCheck(boolean king_call){
		Piece[] c_map = is_White_to_Move ? white_map : black_map;
		byte o_col = is_White_to_Move?Piece.BLACK:Piece.WHITE,c_col=(byte)(-1*o_col),
				k_loc=c_map[0].getPosition(),type;
		Piece obstruct;
		int next_pos = 0;
		// radial attacks
		for (int i = 0; i < 8; i++){
			next_pos = k_loc + RADIALS[i];
			while ((next_pos & 0x88) == 0){
				obstruct = getSquareOccupier((byte)next_pos);
				if (obstruct.getColour() == c_col) break;
				if ((type = obstruct.getType()) != Piece.NULL){
					if (type == Piece.PAWN) {
						if(i < 4 && c_col*(1-i) >= 0 && c_col*((next_pos>>4)-(k_loc>>4)) == 1){
							is_in_check = 1;
							return true;
						}
						break;
					} else if (type == Piece.BISHOP){
						if (i < 4) {
							is_in_check = 1;
							return true;
						}
						break;
					} else if (type == Piece.QUEEN) {
						is_in_check = 1;
						return true;
					} else if (type == Piece.ROOK) {
						if (i > 3){
							is_in_check = 1;
							return true;
						}
						break;
					}
					break;
				}
				next_pos += RADIALS[i];
			}
		}
		// knight moves
		for (byte diff : KNIGHT_MOVES){
			if (getSquareOccupier((byte)(k_loc+diff), !is_White_to_Move).getType()==Piece.KNIGHT){
				is_in_check = 1;
				return true;
			}
		}
		is_in_check = 0;
		return false;
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
	/**
	 * Generates all the moves possible in this Position object. This method does so by generating
	 * all the moves according to the pieces and filters out all moves that result in a check.
	 * @return An array containing all the legal moves in this position.
	 */
	public Move [] generateAllMoves (){
		if (all_moves != null) return all_moves;
		Piece[] current_map = is_White_to_Move ? white_map : black_map;
		LinkedList <Move> pieceMoves = new LinkedList <Move> ();
		if (isInCheck()){
			byte king_sq = current_map[0].getPosition();
			Piece[] tP = getThreateningPieces(king_sq, is_White_to_Move);
			if (tP.length > 0) {
				if (tP.length > 1) {
					// move the king
					for (Move m : generatePieceMoves(king_sq, RADIALS, true)) {
						Move reverse = new Move(m.getEndSquare(), m.getStartSquare());
						current_map[0] = current_map[0].move(m);
						if (!isInCheck(true)) pieceMoves.add(m);
						current_map[0] = current_map[0].move(reverse);
					}
				} else {
					// move the king
					LinkedList<Move> all_m = generatePieceMoves(king_sq, RADIALS, true);
					for (Move m : all_m) {
						Move reverse = new Move(m.getEndSquare(),
								m.getStartSquare());
						current_map[0] = current_map[0].move(m);
						if (!isInCheck(true)) pieceMoves.add(m);
						current_map[0] = current_map[0].move(reverse);
					}
					Piece p = tP[0];
					// put piece in between or kill threatening piece
					byte loc = p.getPosition();
					byte type = p.getType();
					byte next_pos = king_sq;
					if (type != Piece.PAWN && type != Piece.KNIGHT) {
						byte diff = getDifference(loc, king_sq);
						do {
							next_pos += diff;
							pieceMoves.addAll(getThreateningMoves(next_pos, !is_White_to_Move));
						} while (next_pos != loc);
					}
					else{
						pieceMoves.addAll(getThreateningMoves(loc, !is_White_to_Move));
						if (type == Piece.PAWN && en_passant_square == (is_White_to_Move? 0x10: -0x10) + loc){
							byte diffs[] = {0x01, -0x01};
							for (byte diff: diffs){
								if (getSquareOccupier((byte)(loc+ diff), is_White_to_Move).getType() == Piece.PAWN){
									pieceMoves.add(new Move((byte)(loc+ diff), en_passant_square, (byte)5));
								}
							}
						}
					}
				}
			}
		} else {
			//if the king is currently not in check
			Piece[][] ga_map = getGuardianAssailantMap(current_map[0].getPosition());               
			for (Piece current_piece : current_map){
				Piece assailant = Piece.getNullPiece();
				byte c_pos = current_piece.getPosition(), next_pos, 
						c_col = is_White_to_Move?Piece.WHITE:Piece.BLACK;
				for (int m = 0 ; m < 8; m++)
					if (current_piece.getPosition() == ga_map[0][m].getPosition()) assailant = ga_map[1][m];
				if (assailant.getType() == Piece.NULL){
					byte advance = is_White_to_Move?UP_MOVE:DOWN_MOVE,promotion_row=(byte)(is_White_to_Move?7:0), 
							start_row=(byte)(is_White_to_Move?1:6), c_type = current_piece.getType();
					Piece o_pos;
					switch (c_type){
					case Piece.PAWN:
						byte[] attack = is_White_to_Move? WHITE_PAWN_ATTACK : BLACK_PAWN_ATTACK;
						next_pos = (byte) (c_pos + advance);
						if (getSquareOccupier(next_pos).getColour()==Piece.NULL_COL&&(next_pos&0x88)==0){
							if (next_pos >> 4 == promotion_row){
								pieceMoves.add(new Move(c_pos,next_pos,(byte)6));
								pieceMoves.add(new Move(c_pos,next_pos,(byte)7));
								pieceMoves.add(new Move(c_pos,next_pos,(byte)8));
								pieceMoves.add(new Move(c_pos,next_pos,(byte)9));
							} else pieceMoves.add(new Move(c_pos,next_pos));
							if (c_pos >> 4 == start_row){
								next_pos = (byte) (next_pos+advance);
								if (getSquareOccupier (next_pos).getColour()==Piece.NULL_COL)
									pieceMoves.add(new Move(c_pos,next_pos, (byte)10));
							}
						}
						for (byte atk : attack){
							next_pos = (byte) (c_pos + atk);
							if ((next_pos & 0) == 0){
								if (next_pos == en_passant_square){
									pieceMoves.add(new Move(c_pos,(byte)(next_pos-UP_MOVE*c_col),(byte)5));
								} else {
									o_pos = getSquareOccupier(next_pos, !is_White_to_Move);
									if (o_pos.getColour() != Piece.NULL_COL){
										if (next_pos >> 4 == promotion_row){
											pieceMoves.add(new Move(c_pos,next_pos,(byte)6));
											pieceMoves.add(new Move(c_pos,next_pos,(byte)7));
											pieceMoves.add(new Move(c_pos,next_pos,(byte)8));
											pieceMoves.add(new Move(c_pos,next_pos,(byte)9));
										} else pieceMoves.add(new Move(c_pos,next_pos));
									}
								}
							}
						}
						break;
					case Piece.ROOK:
						pieceMoves.addAll(generatePieceMoves(c_pos, HORIZONTALS, false));
						break;
					case Piece.KNIGHT:
						pieceMoves.addAll(generatePieceMoves(c_pos, KNIGHT_MOVES, true));
						break;
					case Piece.BISHOP:
						pieceMoves.addAll(generatePieceMoves(c_pos, DIAGONALS, false));
						break;
					case Piece.QUEEN:
						pieceMoves.addAll(generatePieceMoves(c_pos, RADIALS, false));
						break;
					case Piece.KING:
						for (Move m: generatePieceMoves(c_pos, RADIALS, true)){
							Move reverse = new Move(m.getEndSquare(), m.getStartSquare());
							current_map[0] = current_map[0].move(m);
							if (!isInCheck(true))
								pieceMoves.add(m);
							current_map[0] = current_map[0].move(reverse);
						};
						boolean[] castle_rights = getCastlingRights();
						for (int i = 0 ; i < 4; i++){
							boolean can_castle = castle_rights[i];
							int diff = i < 2 ? RIGHT_MOVE : LEFT_MOVE, range = i < 2 ? 2 : 3;
							next_pos = c_pos;
							for(int j = 0; j < range; j++) {
								if (can_castle){
									if ((is_White_to_Move && i%2 == 0)||((!is_White_to_Move) && i%2==1)){
										next_pos = (byte) (next_pos + diff);
										if ((getSquareOccupier(next_pos).isEqual(Piece.getNullPiece()))){
											current_map[0] = current_piece.move((byte) diff);
											if (isInCheck()){
												can_castle = false;
											}
										} 
										else can_castle = false;
										current_map[0] = current_piece;
									}
									else can_castle = false;
								}
								else break;
							}
							if (can_castle) pieceMoves.add(Move.CASTLE[i]);
						}
						break;
					}
				} else {
					next_pos = assailant.getPosition();
					byte g_row = (byte) (c_pos>>4), g_col = (byte)(c_pos & 0x7), a_row =(byte)(next_pos>>4), 
							a_col = (byte) (next_pos & 0x7), type = current_piece.getType();
					if (type == Piece.QUEEN) pieceMoves.add(new Move (c_pos, next_pos));
					else if (type == Piece.ROOK && (a_row - g_row == 0 || a_col - g_col == 0))
						pieceMoves.add(new Move(c_pos, next_pos));
					else if (type == Piece.BISHOP && (Math.abs(a_row - g_row) == Math.abs(a_col-g_col)))
						pieceMoves.add(new Move(c_pos, next_pos));
					else if (type==Piece.PAWN&&(a_row-g_row)*c_col>0 && Math.abs(a_col-g_col)==1) 
						pieceMoves.add(new Move(c_pos, next_pos));

				}
			}
		}
		all_moves = new Move [pieceMoves.size()];
		all_moves = pieceMoves.toArray(all_moves);
		return all_moves;
	}
	/**
	 * Makes a move on the position. Since Position objects are immutable, one must reassign the
	 * variable. e.g. <code>p = p.makeMove(m)</code>.
	 * @param m The move to make on the current Position.
	 * @return A new position with the move made on it.
	 */
	public Position makeMove (Move m){
		byte start = m.getStartSquare(), end = m.getEndSquare(), mod = m.getModifier();
		int s_l = getIndiceOfPiece(start, is_White_to_Move), h_l = getIndiceOfPiece(end, !is_White_to_Move);
		Piece [] onMove_copy = Arrays.copyOf(is_White_to_Move ? white_map: black_map, white_map.length);
		Piece [] offMove_copy = Arrays.copyOf(is_White_to_Move ? black_map: white_map, white_map.length);
		Piece[] w_copy = is_White_to_Move ? onMove_copy: offMove_copy;
		Piece[] b_copy = is_White_to_Move ? offMove_copy : onMove_copy;
		boolean inc_ply = true;
		boolean [] castlingRights = Arrays.copyOf(getCastlingRights(), 4);
		byte new_eps = -1, c_col = is_White_to_Move ? Piece.WHITE: Piece.BLACK, o_col = (byte)(c_col * -1);
		long new_hash = zobrist;
		
		onMove_copy [s_l] = onMove_copy[s_l].move(m);
		new_hash=Zobrist.xorinout(new_hash,end,start,onMove_copy[s_l].getType(),c_col);
		if (h_l != -1) {
			inc_ply = false;
			new_hash= Zobrist.xorout(new_hash,offMove_copy[h_l].getPosition(),offMove_copy[h_l].getType(),o_col);
			int last_ind = getLastPieceIndice(!is_White_to_Move);
			offMove_copy [h_l] = offMove_copy[h_l].destroy();
			Piece swap = offMove_copy[last_ind];
			offMove_copy[last_ind] = offMove_copy[h_l];
			offMove_copy[h_l] = swap;
		}
		// deal with the "specialness" of the modifiers
		switch (mod){
		case 1: 
			w_copy[0] = w_copy[0].move((byte)2);
			new_hash = Zobrist.xorinout(new_hash, (byte) 6, (byte) 4, Piece.KING, Piece.WHITE);
			castlingRights [0] = false;
			castlingRights [2] = false;
			break;
		case 2: 
			b_copy[0] = b_copy[0].move((byte)2); 
			new_hash = Zobrist.xorinout(new_hash, (byte) 0x76, (byte) 0x74, Piece.KING, Piece.BLACK);
			castlingRights [1] = false;
			castlingRights [3] = false;
			break;
		case 3: 
			w_copy[0] = w_copy[0].move((byte)-2); 
			new_hash = Zobrist.xorinout(new_hash, (byte) 2, (byte) 4, Piece.KING, Piece.WHITE);
			castlingRights [0] = false;
			castlingRights [2] = false;
			break;
		case 4: 
			b_copy[0] = b_copy[0].move((byte)-2); 
			new_hash = Zobrist.xorinout(new_hash, (byte) 0x72, (byte) 0x74, Piece.KING, Piece.BLACK);
			castlingRights [1] = false;
			castlingRights [3] = false;
			break;
		case 5: 
			w_copy[s_l] = w_copy[s_l].move((byte)(c_col * UP_MOVE));
			new_hash = Zobrist.xorinout(new_hash, (byte)(end+(c_col*UP_MOVE)), end, Piece.PAWN, c_col);
			break;
		case 6: case 7: case 8: case 9: 
			w_copy[s_l] = new Piece (end,(byte)(mod - 5),c_col);
			new_hash = Zobrist.xorpromotion (new_hash, end, (byte) (mod - 5), c_col);
			break;
		}
		if (onMove_copy[s_l].getType() == Piece.PAWN) {
			// only consider epsq when en_passant is possible
			if (mod == 10 && (getSquareOccupier((byte)(end + LEFT_MOVE),!is_White_to_Move).exists()||
						getSquareOccupier((byte)(end + RIGHT_MOVE), !is_White_to_Move).exists()))
				new_eps = (byte) (end + (is_White_to_Move ? DOWN_MOVE: UP_MOVE));
			inc_ply = false;
		}
		// deal with castling rights
		if (start == 4) {
			castlingRights [0] = false;
			castlingRights [2] = false;
		} else if (start == 0x74){
			castlingRights [1] = false;
			castlingRights [3] = false;
		} else if (start == 0 || end == 0) castlingRights[2] = false;
		else if (start == 7 || end == 7) castlingRights[0] = false;
		else if (start == 0x77 || end == 0x77) castlingRights[1] = false;
		else if (start == 0x70 || end == 0x70) castlingRights[3] = false;
		
		new_hash = Zobrist.xorcastling(new_hash, getCastlingRights(), castlingRights);
		new_hash = Zobrist.xorepsq(new_hash, en_passant_square, new_eps);
		return new Position ((byte)(inc_ply ? ply_count+1: 0), new_eps, castlingRights, !is_White_to_Move,
			(is_White_to_Move ? onMove_copy : offMove_copy), (is_White_to_Move ? offMove_copy : onMove_copy),
			new_hash);
	}
	/** 
	 * Returns the ending game decision for the positions. This returns the result of this position
	 * object, it it has already been decided.
	 * @return the result of the game, masked by one of the constants. DRAW for a draw. WHITE_WINS
	 * if white wins. BLACK_WINS if black wins. NO_RESULT otherwise.
	 */
	public int getResult(){
		if (all_moves == null) generateAllMoves();
		if (all_moves.length == 0) {
			if (!this.isInCheck()) return DRAW;
			else return (is_White_to_Move ? BLACK_WINS : WHITE_WINS); 
		}
		if (ply_count == 100) return DRAW;
		int whitePiecesLeft = getLastPieceIndice(true) + 1;
		int blackPiecesLeft = getLastPieceIndice(false) + 1;
		if (whitePiecesLeft == 1){
			if (blackPiecesLeft == 1) return DRAW;
			else if (blackPiecesLeft == 2)
				if (black_map[1].getType()==Piece.KNIGHT) return DRAW; 
				else if (blackPiecesLeft == 3)
					if (black_map[1].getType()==Piece.KNIGHT&&black_map[2].getType()==Piece.KNIGHT) return DRAW;
		} else if (blackPiecesLeft == 1){
			if (whitePiecesLeft == 2)
				if (white_map[1].getType()==Piece.KNIGHT) return DRAW;
			if (whitePiecesLeft == 3)
				if (white_map[1].getType()==Piece.KNIGHT&&white_map[2].getType()==Piece.KNIGHT) return DRAW;
		} else if (whitePiecesLeft == 2 && blackPiecesLeft == 2){
			if (white_map[1].getType()==Piece.BISHOP && black_map[1].getType()==Piece.BISHOP){
				byte w_pos = white_map[1].getPosition(), b_pos = black_map[1].getPosition();
				if ((((w_pos >> 4) + (w_pos & 7)) & 1) + (((b_pos >> 4) + (w_pos & 7)) & 1) == 1) return DRAW;
			}
		}
		return NO_RESULT;
	}
	/**
	 * Returns the occupier of a specific square, or the null piece if the square is empty. This
	 * method does so by running through both arrays of maps.
	 * @return the occupier of a specific square, the null piece if the square is empty.
	 */
	public Piece getSquareOccupier (byte square){
		for (int i = 0; i< 16; i++){
			byte pos = white_map[i].getPosition();
			if (pos == square) return white_map[i];
			else if (pos < 0) break;
		}
		for (int i = 0; i< 16; i++){
			byte pos = black_map[i].getPosition();
			if (pos == square) return black_map[i];
			else if (pos < 0) break;
		}
		return Piece.getNullPiece();
	}
	/**
	 * Returns the occupier of a specific square, or the null piece if the square is empty. This
	 * method does so by running a specific map.
	 * @return the occupier of a specific square, the null piece if the square is empty.
	 */
	public Piece getSquareOccupier (byte square, Piece[] map){
		for (int i = 0; i< map.length; i++){
			byte pos = map[i].getPosition();
			if (pos == square) return map[i];
			else if (pos < 0) break;
		}
		return Piece.getNullPiece();
	}
	/**
	 * Returns the occupier of a specific square, or the null piece if the square is empty. This
	 * method does so by running a specific map.
	 * @param square The square to search for.
	 * @param toSearch The map to search in, true if white, false if black.
	 * @return the occupier of the specific square, the null piece if the square is empty.
	 */
	public Piece getSquareOccupier (byte square, boolean toSearch){
		Piece [] map = toSearch ? white_map: black_map; 
		for (Piece p: map){
			byte pos = p.getPosition();
			if (pos == square) return p;
			else if (pos < 0) break;
		}
		return Piece.getNullPiece();
	}
	//----------------------Helper Methods----------------------
	/**
	 * Generates an vector of moves for a mask of differences for a piece. This method does so with
	 * a while loop for each difference if the motion is continuous, stopping on an opponent's piece.
	 * @param c_pos The current location.
	 * @param differences The difference for each direction from c_pos.
	 * @param cont Whether the piece moves in continuous motion, false if it does, true otherwise.
	 * @return A vector containing all the possible straight moves.
	 */
	private LinkedList<Move> generatePieceMoves(byte c_pos, byte[] differences, boolean cont){
		LinkedList <Move> AllMoves = new LinkedList <Move> ();
		byte c_col = is_White_to_Move ? Piece.WHITE : Piece.BLACK, o_col = (byte)(-1*c_col);
		for (int i = 0; i < differences.length; i++){
			byte next_pos = (byte) (c_pos + differences[i]);
			while ((next_pos&0x88)==0){
				Piece o_pos = getSquareOccupier(next_pos);
				if (o_pos.getColour()!=c_col) {
					AllMoves.add(new Move(c_pos, next_pos));
					if (o_pos.getColour()==o_col) break;
				}
				else break;
				if (cont) break;
				next_pos += differences[i];
			}
		}
		return AllMoves;
	}
	/**
	 * Returns all "guardians" and "assailants" on the board. A guardian is a piece shielding the king from
	 * attack. An assailant is an attacking piece.
	 * @param k_loc The current location of the king.
	 * @return All the guardian and assailant pieces.
	 */
	private Piece [][] getGuardianAssailantMap(byte k_loc){
		Piece[][] guard_assail = new Piece[2][8];
		byte o_col = is_White_to_Move?Piece.BLACK:Piece.WHITE, c_col=(byte)(-1*o_col), next_pos, colour, type=-1;
		boolean guardian_reached = false, reset_required = true;
		for (int i = 0; i < RADIALS.length; i++){
			next_pos = k_loc;
			guardian_reached = false;
			reset_required = true;
			Piece guardian = Piece.getNullPiece(), assailant = Piece.getNullPiece();
			do{
				next_pos += RADIALS[i];
				Piece o_pos = getSquareOccupier(next_pos);
				colour = o_pos.getColour();
				if (colour == c_col && !guardian_reached){
					guardian = o_pos;
					guardian_reached = true;
				}else if (colour == o_col && guardian_reached){
					if ((type = o_pos.getType())==Piece.KNIGHT||type==Piece.PAWN||type==Piece.KING) break;
					assailant = o_pos;
					reset_required = false;
					break;
				} else if (colour != Piece.NULL_COL) break;
			} while ((next_pos&0x88)==0);
			if (reset_required){
				guard_assail [0][i] = Piece.getNullPiece();
				guard_assail [1][i] = Piece.getNullPiece();
			} else {
				if ((type == Piece.ROOK && i < 4)|| (type == Piece.BISHOP && i > 3)){
					guard_assail [0][i] = Piece.getNullPiece();
					guard_assail [1][i] = Piece.getNullPiece();
				} else {
					guard_assail [0][i] = guardian;
					guard_assail [1][i] = assailant;
				}
			}
		}
		return guard_assail;
	}
	/**
	 * Returns the last index of the last piece that is not null.
	 * @param forWhite whether or not to search in white's pieces or black's.
	 */
	private int getLastPieceIndice(boolean forWhite){
		if (forWhite) for (int i = 15; i>=0; i--){if (white_map[i].getType() != Piece.NULL) return i;}
		else for (int i = 15; i>=0; i--) {if (black_map[i].getType() != Piece.NULL) return i;}
		return 0;
	}
	/**
	 * Looks for the index of a certain piece in an appropriate map.
	 * @param p The piece to look for.
	 * @param map The map to look in. True for White, false for Black.
	 * @return The index in the appropriate map that contains the specified piece. -1 if no such
	 * piece exists.
	 */
	private int getIndiceOfPiece (byte location, boolean map){
		int ind = -1;
		Piece [] mapToSearch = map ? white_map : black_map;
		for (int i = 0; i < 16; i++) if (location == mapToSearch[i].getPosition()) ind = i;
		return ind;
	}
	private Piece[] getThreateningPieces(byte loc, boolean col) {
		Vector<Piece> threateningPieces = new Vector<Piece>(10, 3);
		byte o_col = col ? Piece.BLACK : Piece.WHITE;
		byte c_col = (byte) ((-1) * o_col);
		byte type;
		boolean loc_occupied = !getSquareOccupier(loc).equals(
				Piece.getNullPiece());
		boolean melee;
		Piece c_pos;
		int next_pos = 0;

		for (int i = 0; i < 8; i++) {
			next_pos = loc + RADIALS[i];
			melee = true;
			while ((next_pos & 0x88) == 0) {
				c_pos = getSquareOccupier((byte) next_pos);
				if (c_pos.getColour() == c_col)
					break;
				if ((type = c_pos.getType()) != Piece.NULL) {
					if (loc_occupied && type == Piece.PAWN && i < 4
							&& ((c_col > 0 && i % 2 == 0) || (c_col < 0 && i % 2 == 1)) && melee)
						threateningPieces.add(c_pos);
					if (!loc_occupied && type == Piece.PAWN && i + c_col == 5) threateningPieces.add(c_pos);
					if (type == Piece.BISHOP && i < 4) threateningPieces.add(c_pos);
					if (type == Piece.QUEEN) threateningPieces.add(c_pos);
					if (type == Piece.ROOK && i > 3) threateningPieces.add(c_pos);
					break;
				}
				melee = false;
				next_pos += RADIALS[i];
			}
		}
		for (byte diff : KNIGHT_MOVES) {
			c_pos = getSquareOccupier((byte) (loc + diff), !col);
			if (c_pos.getType() == Piece.KNIGHT)
				threateningPieces.add(c_pos);
		}
		Piece[] toReturn = new Piece[threateningPieces.size()];
		return (Piece[]) threateningPieces.toArray(toReturn);
	}
	private LinkedList<Move> getThreateningMoves(byte loc, boolean col) {
		Piece[] threateningPiece = getThreateningPieces(loc, col);
		LinkedList<Move> threateningMove = new LinkedList<Move>();
		byte start_loc = 0;
		for (Piece p : threateningPiece) {
			start_loc = p.getPosition();
			Move move = new Move(start_loc, loc);
			threateningMove.add(move);
		}
		return threateningMove;
	}
	private byte getDifference(byte loc, byte k_loc) {
		byte d = 0, next_loc = k_loc;
		for (byte diff : RADIALS) {
			next_loc = k_loc;
			do {
				next_loc += diff;
				if (next_loc == loc) {
					d = diff;
					break;
				}
			} while ((next_loc & 0x88) == 0);
			if (d != 0) break;
		}
		return d;
	}
}