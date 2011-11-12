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
public final class Position {
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
	public static final byte [] KNIGHT_MOVES = {2*UP_MOVE+RIGHT_MOVE,2*UP_MOVE+LEFT_MOVE,
		2*DOWN_MOVE+RIGHT_MOVE, 2*DOWN_MOVE+LEFT_MOVE, 2*RIGHT_MOVE+UP_MOVE, 2*RIGHT_MOVE+DOWN_MOVE,
		2*LEFT_MOVE+UP_MOVE, 2*LEFT_MOVE+DOWN_MOVE};
	/** The storage for the differences of all diagonal moves. */
	public static final byte [] DIAGONALS = {RIGHT_UP_MOVE, RIGHT_DOWN_MOVE, LEFT_UP_MOVE,
		LEFT_DOWN_MOVE};
	/** The storage for the differences of all horizontal/vertical moves.*/
	public static final byte [] HORIZONTALS = {UP_MOVE, DOWN_MOVE, LEFT_MOVE, RIGHT_MOVE};
	/** The storage for the differences of all radial moves. */
	public static final byte [] RADIALS = {RIGHT_UP_MOVE, RIGHT_DOWN_MOVE, LEFT_UP_MOVE,
		LEFT_DOWN_MOVE,UP_MOVE, DOWN_MOVE, LEFT_MOVE, RIGHT_MOVE};
	/** The storage for the difference of all pawn capture moves for white. */
	public static final byte[] WHITE_PAWN_ATTACK = {LEFT_UP_MOVE, RIGHT_UP_MOVE} ;
	/** The storage for the difference of all pawn capture moves for black. */
	public static final byte[] BLACK_PAWN_ATTACK = {LEFT_DOWN_MOVE, RIGHT_DOWN_MOVE};
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
		fifty_move_rule_count = fifty_move;
		en_passant_square = epsq;
		white_k_side_castling_allowed = castling_rights[0];
		black_k_side_castling_allowed = castling_rights[1];
		white_q_side_castling_allowed = castling_rights[2];
		black_q_side_castling_allowed = castling_rights[3];
		white_map = w_map;
		black_map = b_map;
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
		Piece temp = white_map[12];
		white_map[12] = white_map[0];
		white_map[0] = temp;
		temp = black_map[12];
		black_map[12] = black_map[0];
		black_map[0] = temp;
	}
	//----------------------End of Constructors----------------------

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
	/**
	 * Generates all the moves possible in this Position object. This method does so by generating
	 * all the moves according to the pieces and filters out all moves that result in a check.
	 * @return An array containing all the legal moves in this position.
	 */
	public Move[] generateAllMoves (){
		Piece[] current_map = is_White_to_Move ? white_map : black_map;
		LinkedList <Move> pieceMoves = new LinkedList <Move> ();
		//if the king is currently not in check
		Piece[][] ga_map = getGuardianAssailantMap(current_map[0].getPosition());		
		for (Piece current_piece : current_map){
			Piece assailant = Piece.getNullPiece();
			byte c_pos = current_piece.getPosition(), next_pos, c_col = is_White_to_Move?Piece.WHITE:Piece.BLACK;
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
								pieceMoves.add(new Move(c_pos,next_pos));
						}
					}
					for (byte atk : attack){
						next_pos = (byte) (c_pos + atk);
						if ((next_pos & 0) == 0){
							if (next_pos == en_passant_square){
								pieceMoves.add(new Move(c_pos,next_pos,(byte)5));
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
						if (!isInCheck())
							pieceMoves.add(m);
						current_map[0] = current_map[0].move(reverse);
					};
					boolean[] castle_rights = getCastlingRights();
					int ind = getIndiceOfPiece(current_piece,is_White_to_Move? true:false);
					for (int i = 0 ; i < 4; i++){
						boolean can_castle = castle_rights[i];
						int diff = i < 2 ? RIGHT_MOVE : LEFT_MOVE, range = i < 2 ? 2 : 3;
						next_pos = c_pos;
						for(int j = 0; j < range; j++) {
							if (can_castle){
								if ((is_White_to_Move && i%2 == 0)||((!is_White_to_Move) && i%2==1)){
									next_pos = (byte) (next_pos + diff);
									if ((getSquareOccupier(next_pos).isEqual(Piece.getNullPiece()))){
										current_map[ind] = current_piece.move((byte) diff);
										if (isInCheck()){
											can_castle = false;
										}
									} 
									else can_castle = false;
									current_map[ind] = current_piece;
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
		Move [] toReturn = new Move [pieceMoves.size()];
		return (Move[]) pieceMoves.toArray(toReturn);
	}
	/**
	 * Checks if in the current position, whether or not the king is in check.
	 * @return true if the king is in check, false otherwise.
	 */
	public boolean isInCheck(){
		Piece[] c_map = is_White_to_Move ? white_map : black_map;
		byte o_col = is_White_to_Move ? Piece.BLACK : Piece.WHITE,c_col=(byte)(-1*o_col),k_loc=-1,type;
		Piece obstruct;
		int next_pos = 0;
		k_loc = c_map[0].getPosition();
		for (int i = 0; i < 8; i++){
			next_pos = k_loc + RADIALS[i];
			while ((next_pos & 0x88) == 0){
				obstruct = getSquareOccupier((byte)next_pos);
				if (obstruct.getColour() == c_col) break;
				if ((type = obstruct.getType()) != Piece.NULL){
					if (type == Piece.PAWN) return i < 4 && c_col*(1-i) >= 0;
					if (type == Piece.BISHOP) return i < 4;
					if (type == Piece.QUEEN) return true;
					if (type == Piece.ROOK) return i > 3;
					break;
				}
				next_pos += RADIALS[i];
			}
		}
		for (byte diff : KNIGHT_MOVES){
			if (getSquareOccupier((byte)(k_loc+diff), is_White_to_Move).getType()==Piece.KNIGHT) return true;
		}
		return false;
	}
	/**
	 * Makes a move on the position. Since Position objects are immutable, one must reassign the
	 * variable. e.g. <code>p = p.makeMove(m)</code>.
	 * @param m The move to make on the current Position.
	 * @return A new position with the move made on it.
	 */
	public Position makeMove (Move m){
		Piece [] map = Arrays.copyOf(is_White_to_Move? white_map: black_map, white_map.length);
		boolean[] cstl_rights = getCastlingRights();
		byte modifier = m.getModifier();
		if (modifier == 1){
			cstl_rights[0] = false;
			cstl_rights[2] = false;
			int ind_king = getIndiceOfPiece(getSquareOccupier((byte)0x04),true);
			int ind_rook = getIndiceOfPiece(getSquareOccupier((byte)0x07),true);
			map[ind_king] = map[ind_king].move((byte)(2*RIGHT_MOVE));
			map[ind_rook] = map[ind_rook].move((byte)(2*LEFT_MOVE));
			return new Position ((byte)0, (byte)-1, cstl_rights, false, map, black_map);
		} else if (modifier==2){
			cstl_rights[1] = false;
			cstl_rights[3] = false;
			int ind_king = getIndiceOfPiece(getSquareOccupier((byte)0x74),false);
			int ind_rook = getIndiceOfPiece(getSquareOccupier((byte)0x77),false);
			map[ind_king] = map[ind_king].move((byte)(2*RIGHT_MOVE));
			map[ind_rook] = map[ind_rook].move((byte)(2*LEFT_MOVE));
			return new Position ((byte)0, (byte)-1, cstl_rights, true, white_map, map);
		} else if (modifier==3){
			cstl_rights[0] = false;
			cstl_rights[2] = false;
			int ind_king = getIndiceOfPiece(getSquareOccupier((byte)0x04),true);
			int ind_rook = getIndiceOfPiece(getSquareOccupier((byte)0x00),true);
			map[ind_king] = map[ind_king].move((byte)(2*LEFT_MOVE));
			map[ind_rook] = map[ind_rook].move((byte)(3*RIGHT_MOVE));
			return new Position ((byte)0, (byte)-1, cstl_rights, false, map, black_map);
		} else if (modifier==4){
			cstl_rights[1] = false;
			cstl_rights[3] = false;
			int ind_king = getIndiceOfPiece(getSquareOccupier((byte)0x74),false);
			int ind_rook = getIndiceOfPiece(getSquareOccupier((byte)0x70),false);
			map[ind_king] = map[ind_king].move((byte)(2*LEFT_MOVE));
			map[ind_rook] = map[ind_rook].move((byte)(3*RIGHT_MOVE));
			return new Position ((byte)0, (byte)-1, cstl_rights, true, white_map, map);
		} else if (modifier==5){
			int ind_pawn = getIndiceOfPiece(getSquareOccupier(m.getStartSquare()),is_White_to_Move),
					ind_opce = getIndiceOfPiece(getSquareOccupier((byte)
							(m.getEndSquare()+(is_White_to_Move? DOWN_MOVE: UP_MOVE))),!is_White_to_Move);
			Piece [] oth = Arrays.copyOf(is_White_to_Move? black_map: white_map, white_map.length);
			map[ind_pawn] = map[ind_pawn].move(m);
			int lastPiece =(is_White_to_Move?getLastPieceIndice(false):getLastPieceIndice(true));
			oth[ind_opce]=oth[lastPiece];
			oth[lastPiece] = oth[lastPiece].destroy(); 
			return new Position ((byte)0, (byte)-1, cstl_rights, !is_White_to_Move,
					is_White_to_Move ? map : oth, is_White_to_Move ? oth : map);
		} else if (modifier > 5){
			int ind_pawn = getIndiceOfPiece(getSquareOccupier(m.getStartSquare()),is_White_to_Move),
					ind_opce = getIndiceOfPiece(getSquareOccupier(m.getEndSquare()),!is_White_to_Move);
			Piece [] oth = Arrays.copyOf(is_White_to_Move? black_map: white_map, white_map.length);
			map[ind_pawn] = new Piece(m.getEndSquare(),(byte)(Piece.ROOK+modifier-6),
					is_White_to_Move ? Piece.WHITE : Piece.BLACK);
			if (ind_opce > 0){
				int lastPiece =(is_White_to_Move?getLastPieceIndice(false):getLastPieceIndice(true));
				oth[ind_opce]=oth[lastPiece];
				oth[lastPiece] = oth[lastPiece].destroy(); 
			}
			return new Position ((byte)0, (byte)-1, cstl_rights, !is_White_to_Move,
					is_White_to_Move ? map : oth, is_White_to_Move ? oth : map);
		} else {
			Piece [] oth = Arrays.copyOf(is_White_to_Move? black_map: white_map, white_map.length);
			byte start = m.getStartSquare(), end = m.getEndSquare(), epsq = -1;
			Piece p = getSquareOccupier(start, is_White_to_Move), o = getSquareOccupier(end, !is_White_to_Move);
			int ind_PieceToUpdate = getIndiceOfPiece(p, is_White_to_Move), 
					ind_CapturedPiece = getIndiceOfPiece(o,!is_White_to_Move);
			boolean addply = true;
			if (p.getType()==Piece.KING){
				cstl_rights [is_White_to_Move ? 0 : 1] = false;
				cstl_rights [is_White_to_Move ? 2 : 3] = false;
			} else if (p.getType()==Piece.ROOK){
				if (start==(is_White_to_Move?0x00:0x70)) cstl_rights[is_White_to_Move?2:3]=false;
				else if (start==(is_White_to_Move?0x07:0x77)) cstl_rights[is_White_to_Move?0:1]=false;
			} else if (p.getType()==Piece.PAWN){
				if (start >> 4 == 0x06 && end >> 4 == 0x04) epsq = (byte)(start+DOWN_MOVE);
				else if (start >> 4 == 0x01 && end >> 4 == 0x03) epsq = (byte)(start+UP_MOVE);
				addply = false;
			}
			if (ind_CapturedPiece >= 0){
				if (map[ind_CapturedPiece].getType() == Piece.ROOK){
					byte pos = map[ind_PieceToUpdate].getPosition();
					if (pos == 0x00) cstl_rights[2] = false;
					else if (pos == 0x07) cstl_rights[0] = false;
					else if (pos == 0x70) cstl_rights[3] = false;
					else if (pos == 0x77) cstl_rights[1] = false;
				}
				int lastPiece =(is_White_to_Move?getLastPieceIndice(false):getLastPieceIndice(true));
				oth[ind_CapturedPiece]=oth[lastPiece];
				oth[lastPiece] = oth[lastPiece].destroy(); 
				addply = false;
			}
			map [ind_PieceToUpdate] = p.move(m);
			return new Position (addply ?(byte)(fifty_move_rule_count+1): 0, epsq, cstl_rights, 
					!is_White_to_Move, is_White_to_Move ? map : oth, is_White_to_Move ? oth : map);
		}
	}
	/** 
	 * Returns the ending game decision for the positions. This returns the result of this position
	 * object, it it has already been decided.
	 * @return the result of the game, masked by one of the constants. DRAW for a draw. WHITE_WINS
	 * if white wins. BLACK_WINS if black wins. NO_RESULT otherwise.
	 */ 
	public int getResult(){
		if (generateAllMoves().length == 0) {
			if (!this.isInCheck())return DRAW;
			else return (is_White_to_Move ? BLACK_WINS : WHITE_WINS); 
		}
		if (fifty_move_rule_count == 100) return DRAW;
		int whitePiecesLeft = getLastPieceIndice(true) + 1;
		int blackPiecesLeft = getLastPieceIndice(false) + 1;
		if (whitePiecesLeft == 1){
			if (blackPiecesLeft == 1) return DRAW;
			else if (blackPiecesLeft == 2)
				if (black_map[1].getType()==Piece.KNIGHT) return DRAW; 
				else if (blackPiecesLeft == 3)
					if (black_map[1].getType()==Piece.KNIGHT&&black_map[2].getType()==Piece.KNIGHT) return DRAW;
		}
		if (blackPiecesLeft == 1){
			if (whitePiecesLeft == 2)
				if (white_map[1].getType()==Piece.KNIGHT) return DRAW;
			if (whitePiecesLeft == 3)
				if (white_map[1].getType()==Piece.KNIGHT&&white_map[2].getType()==Piece.KNIGHT) return DRAW;
		}
		//bishop insufficient material rule
		boolean draw = true;
		int sq_col = -1;
		for (Piece b_p : black_map){
			if (b_p.getType() == Piece.KING) draw = true;
			else if (b_p.getType() == Piece.BISHOP){
				if (sq_col == -1) sq_col = ((b_p.getPosition()>>4)+(b_p.getPosition()&0x7))%2;
				else if ((b_p.getPosition()/0x10 + b_p.getPosition()%0x10)%2 != sq_col){
					draw = false;
					break;
				}
			}
			else if (b_p.getType() != -1) draw = false;
		}
		if (draw){
			for (Piece w_p : white_map){
				if (w_p.getType() == Piece.KING) draw = true;
				else if (w_p.getType() == Piece.BISHOP){
					if (sq_col == -1) sq_col = (w_p.getPosition()/0x10 + w_p.getPosition()%0x10)%2;
					else if ((w_p.getPosition()/0x10 + w_p.getPosition()%0x10)%2 != sq_col){
						draw = false;
						break;
					}
				}
				else if (w_p.getType() != -1) draw = false;
			}
		}
		if (draw) return DRAW;
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
	private int getIndiceOfPiece (Piece p, boolean map){
		int ind = -1;
		Piece [] mapToSearch = map ? white_map : black_map;
		if (p.exists()) for (int i = 0; i < 16; i++) if (p.isEqual(mapToSearch[i])) ind = i;
		return ind;
	}
	//----------------------End of Helper Methods----------------------
	//----------------------End of Methods----------------------
}