package eval;

import rules.*;
import java.util.*;

/**
 * The ESF: Evaluative String Framework is a framework that organises all the
 * strings into a Features array. The framework allows is simpler than the old
 * FeatureManager framework and runs faster (construction is 5 times faster). It
 * retains all functionality of the old FeatureManager class.
 * 
 * @author Spork Innovations
 */
public class ESFramework {
	// ----------------------Constants----------------------
	/** The index for the white material imbalance feature. */
	public static final int WHITE_MATERIAL = 0;
	/** The index for the black material imbalance feature. */
	public static final int BLACK_MATERIAL = 1;
	/** The index for the bishop vs. knight imbalance feature. */
	public static final int BISHOP_VS_KNIGHT = 2;
	/** The index for the two bishops feature. */
	public static final int TWO_BISHOPS = 3;
	/** The index for the opposite bishops imbalance feature. */
	public static final int OPPOSITE_BISHOPS = 4;
	/** The index for the column structure grouping for white. */
	public static final int BUFFER1 = 5;
	public static final int WHITE_COLUMN_A = 6;
	public static final int WHITE_COLUMN_B = 7;
	public static final int WHITE_COLUMN_C = 8;
	public static final int WHITE_COLUMN_D = 9;
	public static final int WHITE_COLUMN_E = 10;
	public static final int WHITE_COLUMN_F = 11;
	public static final int WHITE_COLUMN_G = 12;
	public static final int WHITE_COLUMN_H = 13;
	/** The index for the column structure grouping for white. */
	public static final int BUFFER2 = 14;
	public static final int BLACK_COLUMN_A = 14;
	public static final int BLACK_COLUMN_B = 15;
	public static final int BLACK_COLUMN_C = 16;
	public static final int BLACK_COLUMN_D = 17;
	public static final int BLACK_COLUMN_E = 18;
	public static final int BLACK_COLUMN_F = 19;
	public static final int BLACK_COLUMN_G = 20;
	public static final int BLACK_COLUMN_H = 21;
	public static final int BUFFER3 = 22;
	/** The index for the pawn island count feature for white. */
	public static final int WHITE_PAWN_ISLANDS = 23;
	/** The index for the pawn island count feature for black. */
	public static final int BLACK_PAWN_ISLANDS = 24;
	/** The index for the isolated pawns (isolani) feature for white. */
	public static final int WHITE_ISOLANIS = 25;
	/** The index for the isolated pawns (isolani) feature for black. */
	public static final int BLACK_ISOLANIS = 26;
	/** The index for the space count feature for white. */
	public static final int WHITE_SPACE = 27;
	/** The index for the space count feature for black. */
	public static final int BLACK_SPACE = 28;
	/** The index for the passed pawn feature for white. */
	public static final int WHITE_PASSED_PAWNS = 29;
	/** The index for the passed pawn feature for black. */
	public static final int BLACK_PASSED_PAWNS = 30;
	/** The index for the doubled pawn feature for white. */
	public static final int WHITE_DOUBLED_PAWNS = 31;
	/** The index for the doubled pawn feature for black. */
	public static final int BLACK_DOUBLED_PAWNS = 32;
	/** The index for the king shield feature for white. */
	public static final int WHITE_KING_SHIELD = 33;
	/** The index for the king shield feature for black. */
	public static final int BLACK_KING_SHIELD = 34;
	/** The index for the king tropism value for white. */
	public static final int WHITE_KING_TROPISM = 35;
	/** The index for the king tropism value for black. */
	public static final int BLACK_KING_TROPISM = 36;
	/** The index for the pawn storm (weighted average of adversary pawns near king) value for white. */
	public static final int WHITE_PAWN_STORM = 39;
	/** The index for the pawn storm (weighted average of adversary pawns near king) value for black. */
	public static final int BLACK_PAWN_STORM = 40;
	/** The index for the backwards pawn feature for white. */
	public static final int WHITE_BACKWARDS_PAWNS = 41;
	/** The index for the backwards pawn feature for black. */
	public static final int BLACK_BACKWARDS_PAWNS = 42;
	/** The index for the sentinel squares feature for white. */
	public static final int WHITE_SENTINEL_SQUARES = 43;
	/** The index for the sentinel squares feature for black. */
	public static final int BLACK_SENTINEL_SQUARES = 44;
	// Private Constants:
	/** The index for the pawn storm values. */
	private static final byte [] PAWN_STORM_VALUES = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,6,6,6,6,6,6,
		6,6,0,0,0,0,0,0,0,0,8,8,8,5,4,8,8,8,0,0,0,0,0,0,0,0,2,3,3,1,1,3,3,1,0,0,0,0,0,0,0,0,0,1,1,
		0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
	// ----------------------End of Constants----------------------
	// ----------------------Instance Variables----------------------
	/** The white pawns. */
	private Piece[] white_pawns;
	/** The black pawns. */
	private Piece[] black_pawns;
	/** The white bishops. */
	private Piece[] white_bishops;
	/** The black bishops. */
	private Piece[] black_bishops;
	/** The white pieces. */
	private Piece[] white_pieces;
	/** The black pieces. */
	private Piece[] black_pieces;
	/** The white king. */
	private Piece white_king;
	/** The black king. */
	private Piece black_king;
	/**
	 * A reference to link the current evaluative string with the associated
	 * position.
	 */
	protected Position pos_ref;
	/** The container for the evaluative strings. */
	public String[] features = new String[50];

	// ----------------------End of Instance Variables----------------------
	// ----------------------Constructors----------------------
	/**
	 * Constructor: This creates the necessary tools for the framework's
	 * existence. Feature construction and recognition can take place without
	 * any additional parameters.
	 * @param p The position to evaluate.
	 */
	public ESFramework(Position p) {
		Vector<Piece> w_p = new Vector<Piece>(8), b_p = new Vector<Piece>(8), 
				w_b = new Vector<Piece>(2, 1), b_b = new Vector<Piece>(2, 1);
		white_pieces = p.getWhitePieces();
		black_pieces = p.getBlackPieces();
		for (Piece q : white_pieces) {
			int val = q.getType();
			if (val == Piece.NULL) break;
			switch (val) {
			case Piece.PAWN: w_p.add(q); break;
			case Piece.BISHOP: w_b.add(q); break;
			case Piece.KING: white_king = q; break;
			}
		}
		for (Piece q : black_pieces) {
			int val = q.getType();
			if (val == Piece.NULL) break;
			switch (val) {
			case Piece.PAWN: b_p.add(q); break;
			case Piece.BISHOP: b_b.add(q); break;
			case Piece.KING: black_king = q; break;
			}
		}
		white_pawns = w_p.toArray(new Piece[w_p.size()]);
		black_pawns = b_p.toArray(new Piece[b_p.size()]);
		white_bishops = w_b.toArray(new Piece[w_b.size()]);
		black_bishops = b_b.toArray(new Piece[b_b.size()]);
		pos_ref = p;
		features[BUFFER1] = "";
		features[BUFFER2] = "";
		features[BUFFER3] = "";
	}

	// ----------------------End of Constructors----------------------
	// ----------------------Methods----------------------
	// ----------------------Instance Methods----------------------
	/**
	 * Generates the material imbalance between the two sides and stores it in
	 * the appropriate index. The format is [numberofpieces][short-hand piece
	 * type], separated by spaces. Note that the string begins with a space so
	 * that the result is not confused with the null string if material is even.
	 */
	public void material() {
		int[][] material = new int[2][6];
		String w_material = " ", b_material = " ";
		for (Piece p : white_pieces) {
			int type = p.getType();
			if (type != Piece.NULL) material[0][type]++;
		}
		for (Piece p : black_pieces) {
			int type = p.getType();
			if (type != Piece.NULL) material[1][type]++;
		}
		for (int i = 0; i < 5; i++) {
			int diff = material[0][i] - material[1][i];
			if (diff != 0) {
				int pure_diff = Math.abs(diff);
				String toAdd = "" + pure_diff;
				switch (i) {
				case Piece.PAWN: toAdd += "p "; break;
				case Piece.ROOK: toAdd += "r "; break;
				case Piece.BISHOP: toAdd += "b "; break;
				case Piece.KNIGHT: toAdd += "n "; break;
				case Piece.QUEEN: toAdd += "q "; break;
				}
				if (diff > 0) w_material += toAdd;
				else b_material += toAdd;
			}
		}
		features[WHITE_MATERIAL] = w_material;
		features[BLACK_MATERIAL] = b_material;
	}
	/**
	 * Detects whether the bishop or knight imbalance is present and stores it
	 * in the appropriate index. The string will be w if the bishop is on
	 * white's side, b if the bishop is on black's side, or n if the imbalance
	 * is not present.
	 */
	public void bishopvknight() {
		if (features[WHITE_MATERIAL] == null) material();
		String w_imb = features[WHITE_MATERIAL], b_imb = features[BLACK_MATERIAL];
		if (w_imb.contains("b")) {
			if (b_imb.contains("n")) {
				features[BISHOP_VS_KNIGHT] = "w";
				return;
			} else features[BISHOP_VS_KNIGHT] = "n";
		} else if (b_imb.contains("b")) {
			if (w_imb.contains("n")) {
				features[BISHOP_VS_KNIGHT] = "b";
				return;
			} else features[BISHOP_VS_KNIGHT] = "n";
		} else features[BISHOP_VS_KNIGHT] = "n";
	}
	/**
	 * Detects whether each side has a pair of bishops and stores it in the
	 * appropriate index. The string will be "w " if white has the pair but
	 * black does not, " b" if black has the bishop pair but white does not,
	 * "wb" if both players have the bishop pair, and "n" if neither player has
	 * the bishop pair.
	 */
	public void twobishops() {
		boolean flag = false;
		char w = 0, b = 0;
		if (white_bishops.length > 1) {
			w = 'w';
			flag = true;
		}
		if (black_bishops.length > 1) {
			b = 'b';
			flag = true;
		}
		if (flag) features[TWO_BISHOPS] = "" + w + b;
		else features[TWO_BISHOPS] = "n";
	}
	/**
	 * Detects whether a situation with opposite coloured bishops (1 bishop
	 * each, but one is on a dark square, but its adversary on a white square)
	 * is present and stores it in the appropriate index. "y" will be stored if
	 * the situation exists, "n" otherwise.
	 */
	public void oppositebishops() {
		if (white_bishops.length == 1 && black_bishops.length == 1) {
			byte w_loc = white_bishops[0].getPosition(), b_loc = black_bishops[0].getPosition();
			if (((w_loc >> 4 + w_loc & 0x7) & 0x1 + (b_loc >> 4 + b_loc & 0x7) & 0x1) == 1)
				features[OPPOSITE_BISHOPS] = "y";
		}
		features[OPPOSITE_BISHOPS] = "n";
	}
	/**
	 * Organises the pawns into structure by column and stores them into the
	 * appropriate index. This is a time-saving call for some of the pawn
	 * structure related strings in this class.
	 */
	public void columnstruct() {
		for (int i = 0; i < 8; i++) features[WHITE_COLUMN_A + i] = " ";
		for (int i = 0; i < 8; i++) features[BLACK_COLUMN_A + i] = " ";
		for (Piece p : white_pawns) {
			byte loc = p.getPosition();
			features[WHITE_COLUMN_A + (loc & 0x7)] += loc + " ";
		}
		for (Piece p : black_pawns) {
			byte loc = p.getPosition();
			features[BLACK_COLUMN_A + (loc & 0x7)] += loc + " ";
		}
	}
	/**
	 * Determines the number of pawn islands present in the pawn structure and
	 * stores them in the appropriate indices.
	 */
	public void pawnislands() {
		if (features[WHITE_COLUMN_A] == null) columnstruct();
		boolean w_alt = false, b_alt = false;
		int w_islands = 0, b_islands = 0;
		for (int i = 0; i < 8; i++) {
			if (!w_alt && !features[WHITE_COLUMN_A + i].equals(""))
				w_alt = true;
			if (w_alt && features[WHITE_COLUMN_A + i].equals("")) {
				w_islands++;
				w_alt = false;
			}
			if (!b_alt && !features[BLACK_COLUMN_A + i].equals(""))
				b_alt = true;
			if (b_alt && features[BLACK_COLUMN_A + i].equals("")) {
				b_islands++;
				b_alt = false;
			}
		}
		features[WHITE_PAWN_ISLANDS] = "" + w_islands;
		features[BLACK_PAWN_ISLANDS] = "" + b_islands;
	}
	/**
	 * Determines the column of the isolanis present in the pawn structure and
	 * stores them in the appropriate indices. (Note: Columns are stored in 0x88
	 * numbers from 0-7 inclusive)
	 */
	public void isolani() {
		if (features[WHITE_COLUMN_A] == null) columnstruct();
		StringBuffer White = new StringBuffer(" "), Black = new StringBuffer(
				" ");
		for (int i = 0; i < 8; i++) {
			if (!features[WHITE_COLUMN_A + i].equals(" ")) {
				if (features[WHITE_COLUMN_A+i-1].equals(" ")&&(features[WHITE_COLUMN_A+i+1].equals(" ")))
					White = White.append(i);
			}
			if (!features[BLACK_COLUMN_A + i].equals(" ")) {
				if (features[BLACK_COLUMN_A+i-1].equals(" ")&&(features[BLACK_COLUMN_A+i+1].equals(" ")))
					Black = Black.append(i);
			}
		}
		features[WHITE_ISOLANIS] = White.toString();
		features[BLACK_ISOLANIS] = Black.toString();
	}
	/**
	 * Determines the space enclosed by the pawn structure for both sides and
	 * stores them in the appropriate indices.
	 */
	public void space() {
		int w_space = 0, b_space = 0;
		for (Piece p : white_pawns) w_space += p.getPosition() >> 4;
		for (Piece p : black_pawns) b_space += 7 - (p.getPosition() >> 4);
		features[WHITE_SPACE] = w_space + "";
		features[BLACK_SPACE] = b_space + "";
	}
	/**
	 * Determines the column of the passed pawns present in the pawn structure
	 * for both sides and stores them in the appropriate indices. (Note: Columns
	 * are stored in 0x88 numbers from 0-7 inclusive)
	 */
	public void passpawn() {
		if (features[WHITE_COLUMN_A] == null) columnstruct();
		StringBuffer White = new StringBuffer(" "), Black = new StringBuffer(" ");
		for (int i = 0; i < 8; i++) {
			if (!features[WHITE_COLUMN_A + i].equals(" ")) {
				if (features[BLACK_COLUMN_A + i].equals(" ")) {
					if (features[BLACK_COLUMN_A+i-1].equals(" ")&&features[BLACK_COLUMN_A+i+1].equals(" "))
						White = White.append(i);
				}
			}
			if (!features[BLACK_COLUMN_A + i].equals(" ")) {
				if (features[WHITE_COLUMN_A + i].equals(" ")) {
					if (features[WHITE_COLUMN_A+i-1].equals(" ")&&features[WHITE_COLUMN_A+i+1].equals(" "))
						Black = Black.append(i);
				}
			}
		}
		features[WHITE_PASSED_PAWNS] = White.toString();
		features[BLACK_PASSED_PAWNS] = Black.toString();
	}
	/**
	 * Determines the columns where the double pawns are present in the pawn
	 * structures for both sides and stores them in the appropriate indices.
	 * (Note: Columns are stored in 0x88 numbers from 0-7 inclusive).
	 */
	public void doublepawn() {
		if (features[WHITE_COLUMN_A] == null) columnstruct();
		StringBuffer White = new StringBuffer(" "), Black = new StringBuffer(" ");
		for (int i = 0; i < 8; i++) {
			if (features[WHITE_COLUMN_A + i].length() > 5) White = White.append(i);
			if (features[BLACK_COLUMN_A + i].length() > 5) Black = Black.append(i);
		}
		features[WHITE_DOUBLED_PAWNS] = White.toString();
		features[BLACK_DOUBLED_PAWNS] = Black.toString();
	}
	/**
	 * Determines the king's "pawn shield" as a weighted average for both sides
	 * and stores them in the appropriate indices.
	 */
	public void kingshield() {
		byte w_loc = white_king.getPosition(), b_loc = black_king.getPosition();
		byte[] diff_weight_2 = Position.RADIALS;
		byte[] diff_weight_1 = Position.KNIGHT_MOVES;
		boolean w_flag = (w_loc >> 4) < 2, b_flag = (b_loc >> 4) > 5;
		int w_shield = 0, b_shield = 0;
		for (byte diff : diff_weight_2) {
			if (w_flag && (diff >> 4) >= 0 && search(white_pawns, (byte) (w_loc + diff)).exists())
				w_shield += 2;
			if (b_flag && (diff >> 4) <= 0 && search(black_pawns, (byte) (b_loc + diff)).exists())
				b_shield += 2;
		}
		for (byte diff : diff_weight_1) {
			if (w_flag && (diff >> 4) >= 0 && search(white_pawns, (byte) (w_loc + diff)).exists())
				w_shield += 1;
			if (b_flag && (diff >> 4) <= 0 && search(black_pawns, (byte) (b_loc + diff)).exists())
				b_shield += 1;
		}
		if (w_flag && search(white_pawns, (byte) (w_loc + 2 * Position.UP_MOVE)).exists())
			w_shield += 1;
		if (w_flag && search(white_pawns, (byte) (w_loc + 2 * Position.DOWN_MOVE)).exists())
			b_shield += 1;
		features[WHITE_KING_SHIELD] = "" + w_shield;
		features[BLACK_KING_SHIELD] = "" + b_shield;
	}

	/**
	 * Determines the average distance from the opposing side's pieces to each
	 * player's king and stores them in the appropriate indices.
	 */
	public void kingtropism() {
		int w_dist_sq = 0, w_loc = white_king.getPosition(), w_r = w_loc >> 4, w_c = w_loc & 0x7;
		byte type;
		for (Piece p : black_pieces) {
			if ((type = p.getType()) != Piece.NULL && type != Piece.PAWN
					&& type != Piece.KING) {
				int b_pos = p.getPosition(), b_r = b_pos >> 4, b_c = b_pos & 0x7, d_r = w_r
						- b_r, d_c = w_c - b_c;
				w_dist_sq += d_r * d_r + d_c * d_c;
			}
		}
		int b_dist_sq = 0, b_loc = black_king.getPosition(), b_r = b_loc >> 4, b_c = b_loc & 0x7;
		for (Piece p : white_pieces) {
			if ((type = p.getType()) != Piece.NULL && type != Piece.KING
					&& type != Piece.PAWN) {
				int w_pos = p.getPosition(), w_y = w_pos >> 4, w_x = w_pos & 0x7, d_r = b_r
						- w_y, d_c = b_c - w_x;
				b_dist_sq += d_r * d_r + d_c * d_c;
			}
		}
		features[WHITE_KING_TROPISM] = "" + w_dist_sq;
		features[BLACK_KING_TROPISM] = "" + b_dist_sq;
	}
	/**
	 * Determines a weighted sum distance of close pawns to the king's wing and
	 * stores them in the appropriate indices.
	 */
	public void antishield() {
		int totalWhiteWeight = 0, totalBlackWeight = 0;
		byte b_loc = black_king.getPosition(), w_loc = white_king.getPosition(), position;
		if ((w_loc & 0x7) < 4) {
			for (Piece p : black_pawns) {
				if (!p.exists()) break;
				position = p.getPosition();
				if ((position & 0x7)<4&&(position>>4)<5) totalBlackWeight+=PAWN_STORM_VALUES[position];
			}
		} else {
			for (Piece p : black_pawns) {
				if (!p.exists()) break;
				position = p.getPosition();
				if ((position&0x7)>3&&(position>>4)<5) totalBlackWeight+=PAWN_STORM_VALUES[position];
			}
		}
		if ((b_loc & 0x7) < 4) {
			for (Piece p : white_pawns) {
				if (!p.exists()) break;
				position = p.getPosition();
				if ((position & 0x7) < 4 && (position >> 4) > 2)
					totalWhiteWeight += PAWN_STORM_VALUES[0x70-(position&0x70)+(position&0x7)];
			}
		} else {
			for (Piece p : white_pawns) {
				if (!p.exists()) break;
				position = p.getPosition();
				if ((position & 0x7) > 3 && (position >> 4) > 2)
					totalWhiteWeight += PAWN_STORM_VALUES[0x70-(position&0x70)+(position&0x7)];
			}
		}
		features[WHITE_PAWN_STORM] = totalWhiteWeight + "";
		features[BLACK_PAWN_STORM] = totalBlackWeight + "";
	}
	// still buggy
	public void backwardspawn(){
		if (features[WHITE_COLUMN_A] == null) columnstruct();
		StringBuffer White = new StringBuffer(" "), Black = new StringBuffer(" "), 
				White_isolated = new StringBuffer(" "), Black_isolated = new StringBuffer(" ");
		sort(white_pawns, 0, white_pawns.length-1);
		sort(black_pawns, 0, black_pawns.length-1);
		for(int i = 0; i < white_pawns.length-2; i++){
			byte w_prev_loc = -0x70,c_pos=white_pawns[i].getPosition(),next=white_pawns[i+1].getPosition();
			if(Math.abs((c_pos & 0x07) - (next & 0x07)) > 1){
				if(Math.abs(c_pos & 0x07 - w_prev_loc) != 1){
					if(((c_pos & 0x07) == 0) && features[WHITE_COLUMN_B].equals(" ")) //?
						White_isolated.append(c_pos & 0x07);
					else if(((c_pos & 0x07) == 7) && features[WHITE_COLUMN_G].equals(" ")) // ?
						White_isolated.append(c_pos & 0x07);
					else if(features[(c_pos & 0x07)+5].equals(" ")&&features[(c_pos & 0x07)+7].equals(" "))
						White_isolated.append(c_pos & 0x07);
					else White.append(c_pos & 0x07);
				}
			} else {
				if((Math.abs(c_pos & 0x07 - w_prev_loc) != 1) && (c_pos >> 4 != next >> 4))
					White.append(c_pos & 0x07);
				w_prev_loc = (byte)(white_pawns[i+1].getPosition() & 0x07);
				i++;
			}
		}
		for(int i = black_pawns.length-1; i > 0; i--){
			byte b_prev_loc=-0x70, c_pos =black_pawns[i].getPosition(),next=black_pawns[i-1].getPosition();
			if(Math.abs((c_pos & 0x07) - (next & 0x07)) > 1){
				if(Math.abs(c_pos & 0x07 - b_prev_loc) != 1){
					if(((c_pos & 0x7) == 0) && features[BLACK_COLUMN_B].equals(" "))
						Black_isolated.append(c_pos & 0x7);
					else if(((c_pos & 0x7) == 7) && features[BLACK_COLUMN_G].equals(" "))
						Black_isolated.append(c_pos & 0x7);
					else if(features[(c_pos & 0x07)+5].equals(" ")&&features[(c_pos & 0x07)+7].equals(" "))
						Black_isolated.append(c_pos & 0x7);
					else Black.append(c_pos & 0x07);
				}
			} else {
				if((Math.abs(c_pos & 0x07 - b_prev_loc) != 1) && (c_pos >> 4 != next >> 4))
					Black.append(c_pos & 0x07);
				b_prev_loc = (byte)(next & 0x07);
				i--;
			}
		}
		features[WHITE_ISOLANIS] = White_isolated.toString();
		features[BLACK_ISOLANIS] = Black_isolated.toString();
		features[WHITE_BACKWARDS_PAWNS] = White.toString();
		features[BLACK_BACKWARDS_PAWNS] = Black.toString();
	}
	// still too slow
	public void sentinelsquares() {
		@SuppressWarnings("unchecked")
		LinkedList<Character> w_pieces[] = new LinkedList[0x79], b_pieces[] = new LinkedList[0x79];
		Piece[] w_map = pos_ref.getWhitePieces(), b_map = pos_ref.getBlackPieces();
		boolean isOnMove = pos_ref.isWhiteToMove();
		for (int i = 0; i < 0x79; i++) {
			w_pieces[i] = new LinkedList<Character>();
			b_pieces[i] = new LinkedList<Character>();
		}
		updateMap(w_pieces, w_map, pos_ref, true);
		updateMap(b_pieces, b_map, pos_ref, false);
		StringBuffer white_control = new StringBuffer();
		StringBuffer black_control = new StringBuffer();
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				byte sq = (byte) (i * 0x10 + j);
				Character[] w_attack = w_pieces[sq]
						.toArray(new Character[w_pieces[sq].size()]), b_attack = w_pieces[sq]
						.toArray(new Character[w_pieces[sq].size()]);
				Arrays.sort(w_attack);
				Arrays.sort(b_attack);
				int res = doBattle(w_attack, b_attack, isOnMove);
				if (res == 1) white_control.append(sq+",");
				else if (res == -1) black_control.append(sq+",");
			}
		}
		features[WHITE_SENTINEL_SQUARES] = white_control.toString();
		features[BLACK_SENTINEL_SQUARES] = black_control.toString();
	}
	// ----------------------End of Instance Methods----------------------
	// ----------------------Helper Methods----------------------
	/**
	 * Performs a linear search of a piece by location. Returns the piece if found, null piece
	 * otherwise.
	 * @param map The map of pieces to search for.
	 * @param loc The location to search.
	 * @return The piece at that location in the specified map. If the piece is not found, the
	 * null piece.
	 */
	private Piece search(Piece[] map, byte loc) {
		for (Piece q : map) {
			if (!q.exists()) return Piece.getNullPiece();
			if (q.getPosition() == loc) return q;
		}
		return Piece.getNullPiece();
	}
	/**
	 * Compares two pieces for sorting purposes. The comparison is made on a rank basis.
	 * @param alpha The piece to compare.
	 * @param beta The other piece to compare.
	 * @return A positive value if alpha is on a further rank than beta, a negative value if
	 * beta is on a further rank than alpha, 0 if they are on the same rank.
	 */
	private static int comparePiece(Piece alpha, Piece beta){
		int loc_one = (alpha.getPosition() >> 4), loc_two = (beta.getPosition() >> 4);
		if (loc_one == loc_two) return 0;
		return (loc_one > loc_two) ? 1: -1;
	}
	/**
	 * Sorts a map of pieces by rank (as in position on the chess board). Uses the quicksort
	 * algorithm. To start sort for the entire array, call sort (map, 0, map.length -1);
	 * @param map The map to sort.
	 * @param lo The lowest index to sort from.
	 * @param hi The highest index to sort from.
	 */
	private static void sort (Piece[] map, int lo, int hi){
		int i=lo, j=hi;
		Piece h;
		Piece x=map[(lo+hi)/2];
		do	{    
			while (comparePiece(map[i],x) == 1) i++; 
			while (comparePiece(map[j],x) == -1) j--;
			if (i<=j){
				h=map[i]; 
				map[i]=map[j]; 
				map[j]=h;
				i++; j--;
			}
		} while (i<=j);
		if (lo<j) sort(map, lo, j);
		if (i<hi) sort(map, i, hi);
	}
	// not tested 
	private static int doBattle(Character[] w_attack, Character[] b_attack, boolean toMove) {
		int w_length = w_attack.length;
		int b_length = b_attack.length;
		if (w_length == 0) {
			if (b_length == 0)return 0;
			else return -1;
		} else if (b_length == 0) return 1;
		int b_index = 0, w_index = 0, b_loss = 0, w_loss = 0;
		if (toMove) {
			while (true) {
				if (b_length > b_index) {
					w_loss += switchVal(w_attack[w_index++]);
					if (w_length > w_index) {
						b_loss += switchVal(b_attack[b_index++]);
						if (b_loss < w_loss) return -1;
						else if (w_loss < b_loss) {
							if (b_length > b_index)
								if ((w_loss + switchVal(w_attack[w_index])) < b_attack[b_index - 1])
									return 1;
						}
					} else return -1;
				} else return 1;
			}
		} else {
			while (true) {
				if (w_length > w_index) {
					b_loss += switchVal(b_attack[b_index]);
					b_index++;
					if (b_length > b_index) {
						w_loss += switchVal(w_attack[w_index]);
						w_index++;
						if (w_loss < b_loss) return 1;
						else if (b_loss < w_loss) {
							if (w_length > w_index)
								if ((b_loss + switchVal(b_attack[b_index])) < w_attack[w_index - 1])
									return -1;
						}
					} else return 1;
				} else return -1;
			}
		}
	}
	// implementation issues
	private static void updateMap(LinkedList<Character>[] map, Piece[] toApply, Position p, boolean col) {
		byte o_col = col ? Piece.BLACK : Piece.WHITE;
		for (Piece r : toApply) {
			byte c_loc = r.getPosition(), type;
			int n_loc = c_loc;
			switch (r.getType()) {
			case Piece.PAWN:
				if (col) {
					n_loc = c_loc + Position.LEFT_UP_MOVE;
					map[(n_loc & 0x88) == 0 ? n_loc : 0x78].add('a');
					n_loc = c_loc + Position.RIGHT_UP_MOVE;
					map[(n_loc & 0x88) == 0 ? n_loc : 0x78].add('a');
				} else {
					n_loc = c_loc + Position.LEFT_DOWN_MOVE;
					map[(n_loc & 0x88) == 0 ? n_loc : 0x78].add('a');
					n_loc = c_loc + Position.RIGHT_DOWN_MOVE;
					map[(n_loc & 0x88) == 0 ? n_loc : 0x78].add('a');
				}
				break;
			case Piece.KNIGHT:
				for (byte d : Position.KNIGHT_MOVES) {
					n_loc = c_loc + d;
					map[(n_loc & 0x88) == 0 ? n_loc : 0x78].add('b');
				}
				break;
			case Piece.BISHOP:
				for (byte d : Position.DIAGONALS) {
					n_loc = c_loc + d;
					while ((n_loc & 0x88) == 0) {
						map[n_loc].add('b');
						Piece obstruct = p.getSquareOccupier((byte) n_loc);
						if (obstruct.getColour() == o_col)
							break;
						else if ((type = obstruct.getType()) != Piece.NULL) {
							if (type == Piece.PAWN && ((col && d > 0) || (!col && d < 0))) {
								n_loc += d;
								map[(n_loc & 0x88) == 0 ? n_loc : 0x78].add('a');
							} else if (type != Piece.BISHOP || type != Piece.QUEEN)
								break;
						}
						n_loc += d;
					}
				}
				break;
			case Piece.ROOK:
				for (byte d : Position.HORIZONTALS) {
					n_loc = c_loc + d;
					while ((n_loc & 0x88) == 0) {
						map[n_loc].add('c');
						Piece obstruct = p.getSquareOccupier((byte) n_loc);
						if (obstruct.getColour() == o_col) break;
						else if ((type = obstruct.getType()) != Piece.NULL) {
							if (type != Piece.QUEEN || type != Piece.ROOK) break;
						}
						n_loc += d;
					}
				}
				break;
			case Piece.QUEEN:
				for (byte d : Position.RADIALS) {
					n_loc = c_loc + d;
					while ((n_loc & 0x88) == 0) {
						map[n_loc].add('d');
						Piece obstruct = p.getSquareOccupier((byte) n_loc);
						if (obstruct.getColour() == o_col) break;
						else if ((type = obstruct.getType()) != Piece.NULL) {
							if (type != Piece.BISHOP || type != Piece.ROOK || type != Piece.PAWN) break;
							if (type==Piece.PAWN&&((d & 0x7)!=0&&(d>>4)!=0)&&((col&&d>0)||(!col&&d<0))){
								n_loc = n_loc + d;
								map[(n_loc & 0x88) == 0 ? n_loc : 0x78].add('a');
								break;
							}
							if ((type == Piece.BISHOP) && ((d & 0x7) != 0 || (d >> 4) != 0)) break;
							if ((type == Piece.ROOK) && !((d & 0x7) != 0 && (d >> 4) != 0)) break;
						}
						n_loc += d;
					}
				}
				break;
			case Piece.KING:
				for (byte d : Position.RADIALS) {
					n_loc = c_loc + d;
					map[(n_loc & 0x88) == 0 ? n_loc : 0x78].add('e');
				}
				break;
			}
		}
	}
	// implementation issues
	private static int switchVal(char code) {
		switch (code) {
		case 'a': return 1;
		case 'b': return 3;
		case 'c': return 5;
		case 'd': return 9;
		case 'e': return Integer.MAX_VALUE;
		default: return 0;
		}
	}
	// ----------------------End of Helper Methods----------------------
	// ----------------------End of Methods----------------------
}