package eval;

import java.util.*;
import rules.*;

/**
 * Lorenz is the third framework to the chess engine. It is named after the Lorenz cipher
 * machine which use the similar bitwise manipulation techniques. This new framework is
 * approximately 30 times faster in certain areas by bypassing the string concactenation
 * step and replacing it with bitwise manipulation. 
 * @author Jesse Wang, Andy Huang, Jacob Huang
 */
public final class Lorenz {
	// ----------------------Constants----------------------
	// index keys
	/** The index containing the absolute material values for white.*/
	public static final byte WHITE_ABSOLUTE_MATERIAL = 0;
	/** The index containing the absolute material values for black.*/
	public static final byte BLACK_ABSOLUTE_MATERIAL = 1;
	/** The index containing the relative material values for white.*/
	public static final byte WHITE_RELATIVE_MATERIAL = 2;
	/** The index containing the relative material values for black.*/
	public static final byte BLACK_RELATIVE_MATERIAL = 3;
	/** The index containing the piece dynamics of the position.*/
	public static final byte DYNAMICS = 4;
	// Pawn formation strings.
	public static final byte BUFFER1 = 5;
	public static final byte WHITE_COLUMN_A = 6;
	public static final byte WHITE_COLUMN_B = 7;
	public static final byte WHITE_COLUMN_C = 8;
	public static final byte WHITE_COLUMN_D = 9;
	public static final byte WHITE_COLUMN_E = 10;
	public static final byte WHITE_COLUMN_F = 11;
	public static final byte WHITE_COLUMN_G = 12;
	public static final byte WHITE_COLUMN_H = 13;
	public static final byte BUFFER2 = 14;
	public static final byte BLACK_COLUMN_A = 15;
	public static final byte BLACK_COLUMN_B = 16;
	public static final byte BLACK_COLUMN_C = 17;
	public static final byte BLACK_COLUMN_D = 18;
	public static final byte BLACK_COLUMN_E = 19;
	public static final byte BLACK_COLUMN_F = 20;
	public static final byte BLACK_COLUMN_G = 21;
	public static final byte BLACK_COLUMN_H = 22;
	public static final byte BUFFER3 = 23;
	/** The index containing the number of pawn islands for both sides.*/
	public static final byte PAWN_ISLANDS = 24;
	/** The index containing the passed pawns for white.*/
	public static final byte WHITE_PASSERS = 25;
	/** The index containing the passed pawns for black.*/
	public static final byte BLACK_PASSERS = 26;
	/** The index containing the doubled pawns for white.*/
	public static final byte WHITE_DOUBLED_PAWNS = 27;
	/** The index containing the doubled pawns for black.*/
	public static final byte BLACK_DOUBLED_PAWNS = 28;
	/** The index containing the king tropism value for both sides. */
	public static final byte KING_SAFETY = 29;
	/** The index containing the number of backwards pawns for white.*/
	public static final byte WHITE_BACKWARDS = 30;
	/** The index containing the number of backwards pawns for black.*/
	public static final byte BLACK_BACKWARDS = 31;
	/** The index containing the number of isolanis for white.*/
	public static final byte WHITE_ISOLANIS = 32;
	/** The index containing the number of isolanis for black.*/
	public static final byte BLACK_ISOLANIS = 33;
	/** The index containing the "behind the pawn wall" space for both sides.*/
	public static final byte SPACE = 34;
	/** The index containing the sentinel squares for white.*/
	public static final byte WHITE_SENTINELS = 35;
	/** The index containing the sentinel squares for black.*/
	public static final byte BLACK_SENTINELS = 36;
	/** The index containing the open and half open files for the board, including diagonals.*/
	public static final byte OPEN_FILES = 37;
	// maximum features
	private static final byte MAX_FEATURES = 38;
	// useful constants
	private static final int BISHOP_MASK = 0xf0;
	private static final short PAWN_VALUE = 100;
	private static final short KNIGHT_VALUE = 325;
	private static final short BISHOP_VALUE = 340;
	private static final short ROOK_VALUE = 500;
	private static final short QUEEN_VALUE = 975;
	private static final byte[] PAWN_STORM_VALUES = { 0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0, 6, 6, 6, 6, 6, 6, 6, 6, 0, 0, 0, 0, 0, 0,
		0, 0, 8, 8, 8, 5, 4, 8, 8, 8, 0, 0, 0, 0, 0, 0, 0, 0, 2, 3, 3, 1,
		1, 3, 3, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	// ----------------------End of Constants----------------------
	// ----------------------Instance Variables----------------------
	private Piece[] white_pawns;
	private Piece[] black_pawns;
	private Piece[] white_pieces;
	private Piece[] black_pieces;
	private byte white_king;
	private byte black_king;
	public long [] features = new long [MAX_FEATURES];
	private Position position;
	// ----------------------End of Instance Variables----------------------
	// ----------------------Constructors----------------------
	/**
	 * Constructor: This creates the necessary tools for the framework's
	 * existence. Feature construction and recognition can take place without
	 * any additional parameters.
	 * @param p The position to evaluate.
	 */
	public Lorenz(Position p) {
		Vector<Piece> w_p = new Vector<Piece>(8), b_p = new Vector<Piece>(8);
		white_pieces = p.getWhitePieces();
		black_pieces = p.getBlackPieces();
		for (Piece q : white_pieces) if (q.getType() == Piece.PAWN) w_p.add(q);
		for (Piece q : black_pieces) if (q.getType() == Piece.PAWN) b_p.add(q); 
		white_king = white_pieces[0].getPosition();
		black_king = black_pieces[0].getPosition();
		white_pawns = w_p.toArray(new Piece[w_p.size()]);
		black_pawns = b_p.toArray(new Piece[b_p.size()]);
		position = p;
		sort(white_pawns, 0, white_pawns.length-1);
		sort(black_pawns, 0, black_pawns.length-1);
	}
	// ----------------------End of Constructors----------------------
	// ----------------------Methods----------------------
	/**
	 * Retrieves the stored feature value inside the specified index, please see
	 * the constants for the appropriate indices. If the value is not present, then
	 * the appropriate method is called recursively.
	 * @param featureIndex The index that maps to the feature.
	 * @return The value of the feature.
	 */
	public long get (byte featureIndex){
		if (featureIndex > MAX_FEATURES) throw new IllegalArgumentException();
		if (features[featureIndex] != 0) return features[featureIndex];
		switch (featureIndex){
		case WHITE_ABSOLUTE_MATERIAL: case BLACK_ABSOLUTE_MATERIAL: case WHITE_RELATIVE_MATERIAL:
		case BLACK_RELATIVE_MATERIAL: material(); break;
		case DYNAMICS: dynamics(); break;
		case BUFFER1: case WHITE_COLUMN_A: case WHITE_COLUMN_B: case WHITE_COLUMN_C: 
		case WHITE_COLUMN_D: case WHITE_COLUMN_E: case WHITE_COLUMN_F: case WHITE_COLUMN_G:
		case WHITE_COLUMN_H: case BUFFER2: case BLACK_COLUMN_A: case BLACK_COLUMN_B:
		case BLACK_COLUMN_C: case BLACK_COLUMN_D: case BLACK_COLUMN_E: case BLACK_COLUMN_F:
		case BLACK_COLUMN_G: case BLACK_COLUMN_H:
			pawnformation(); break;
		case SPACE: space(); break;
		case WHITE_SENTINELS: case BLACK_SENTINELS: sentinelsquares(); break;
		case WHITE_BACKWARDS: case BLACK_BACKWARDS: case WHITE_ISOLANIS: case BLACK_ISOLANIS:
			weakpawns(); break;
		case KING_SAFETY: kingsafety();
		case WHITE_DOUBLED_PAWNS: case BLACK_DOUBLED_PAWNS: doublepawns(); break;
		case PAWN_ISLANDS: pawnislands(); break;
		case OPEN_FILES: openlines(); break;
		default: return -1;
		}
		return features[featureIndex];
	}
	/**
	 * Returns the underlying position reference.
	 * @return the underlying position reference
	 */
	public Position getPosition (){
		return position;
	}
	/**
	 * Gets the absolute and relative material count. 
	 * The absolute material bitstring in partitioned as follows: (from left to right) byte 0: # pawns, 
	 * byte 1: # rooks, byte 2 : # knight, byte 3: # bishops, byte 4: # queens, byte 5 - 8: total score, 
	 * using the standard values.
	 * 
	 * The relative material values bitstring is partitioned as a vector of 8 bits each. The first
	 * 4 bits of the 8 bitstring is the piece type, as per standard modifiers in the Piece class and
	 * the relative difference.
	 */
	private void material(){
		int [][] material = new int [2][6];
		long w_absolute = 0, b_absolute = 0, w_relative = 0, b_relative = 0;
		int type, w_count = 0, b_count = 0;
		for (Piece p : white_pieces) if ((type = p.getType()) != Piece.NULL) material[0][type]++;
		for (Piece p : black_pieces) if ((type = p.getType()) != Piece.NULL) material[1][type]++;
		w_count += material[0][Piece.PAWN]*PAWN_VALUE;
		w_count += material[0][Piece.ROOK]*ROOK_VALUE;
		w_count += material[0][Piece.KNIGHT]*KNIGHT_VALUE;
		w_count += material[0][Piece.BISHOP]*BISHOP_VALUE;
		w_count += material[0][Piece.QUEEN]*QUEEN_VALUE; 
		b_count += material[1][Piece.PAWN]*PAWN_VALUE;
		b_count += material[1][Piece.ROOK]*ROOK_VALUE;
		b_count += material[1][Piece.KNIGHT]*KNIGHT_VALUE;
		b_count += material[1][Piece.BISHOP]*BISHOP_VALUE;
		b_count += material[1][Piece.QUEEN]*QUEEN_VALUE;
		w_absolute = w_count;
		b_absolute = b_count;
		for (int i = 0; i < 5; i++) w_absolute = (w_absolute << 4) + material[0][i];
		for (int i = 0; i < 5; i++) b_absolute = (b_absolute << 4) + material[1][i];
		for (int i = 0; i < 5; i++) {
			int diff = material[0][i] - material[1][i];
			if (diff != 0) {
				int pure_diff = Math.abs(diff);
				if (diff > 0) w_relative = (w_relative << 8) + (i << 4) + pure_diff;
				else b_relative = (b_relative << 8) + (i << 4) + pure_diff;
			}
		}
		features[WHITE_ABSOLUTE_MATERIAL] = w_absolute;
		features[BLACK_ABSOLUTE_MATERIAL] = b_absolute;
		features[WHITE_RELATIVE_MATERIAL] = w_relative;
		features[BLACK_RELATIVE_MATERIAL] = b_relative;
	}
	/**
	 * This is actually 3 different bistrings compressed into 1 bitstring to save space. This method
	 * evaluates the piece dynamics of the current position. There are three components are of this
	 * feature:
	 * 1 -> Bishop vs. Knight. The bitstring will be 0x100 if white has the bishop, 0x010 when black
	 * has the bishop, 0x001 when the bishop verus knight balance doesn't exist.
	 * 2 -> Two Bishops. The bistring will return 0x101 if black has them, 0x011 when white has them,
	 * and 0x111 if both sides have them.
	 * 3 -> Opposite coloured Bishops. The bistring will return 0x10 if an opposite coloured bishop
	 * situation exists. Returns 0x1 if it doesn't.
	 * 
	 * Each component has 4 bits each in the order (from left to right): bishop versus knight, 
	 * opposite bishops, two bishops.
	 */
	private void dynamics (){
		if (features[WHITE_ABSOLUTE_MATERIAL] == 0) material();
		long rel_w = features[WHITE_RELATIVE_MATERIAL], rel_b = features[BLACK_RELATIVE_MATERIAL],
			 abs_w = features[WHITE_ABSOLUTE_MATERIAL], abs_b = features[BLACK_ABSOLUTE_MATERIAL];
		long ops_str = 1, bvn_str, two_str = 1;
		long n_b_w = (abs_w & BISHOP_MASK) >> 4, n_b_b = (abs_b & BISHOP_MASK) >> 4;
		
		if (n_b_w == 2) two_str += 0x10;
		if (n_b_b == 2) two_str += 0x100;
		else if (n_b_w ==1 && n_b_b ==1){
			int w_orient = 0, b_orient = 0;
			for (Piece p: white_pieces){
				if (p.getType() == Piece.BISHOP){
					byte r = p.getPosition();
					w_orient = ((r >> 4) + (r & 7)) & 1;
					break;
				}
			}
			for (Piece p: black_pieces){
				if (p.getType() == Piece.BISHOP){
					byte r = p.getPosition();
					b_orient = ((r >> 4) + (r & 7)) & 1;
					break;
				}
				if (((w_orient + b_orient) & 1) == 1) ops_str = 16;
			}
		} 
		// bishop vs knight scenarios
		boolean w_b = false, b_b = false, w_n = false, b_n = false;
		while (rel_w != 0){
			long type = (rel_w & 0xff) >> 4;
			if (type == Piece.BISHOP) {
				w_b = true;
				break;
			} else if (type == Piece.KNIGHT){
				w_n = true;
				break;
			}
			rel_w = rel_w >> 8;
		}
		while (rel_b != 0){
			long type = (rel_b & 0xff) >> 4;
			if (type == Piece.BISHOP) {
				b_b = true;
				break;
			} else if (type == Piece.KNIGHT){
				b_n = true;
				break;
			}
			rel_b = rel_b >> 8;
		}
		if (w_b && b_n) bvn_str = 0x100;
		else if (b_b && w_n) bvn_str = 0x010;
		else bvn_str = 0x001;
		features[DYNAMICS] = (bvn_str << 8) + (ops_str << 4) + two_str; 
	}
	/**
	 * Returns the pawn formations by grouping the pawns into columns. This approach follows a 
	 * radix sort approach. If a specific column is empty, then -1 will be returned to that column.
	 * Otherwise, the column will be a vector of pawns and their locations.
	 */
	private void pawnformation(){
		features [BUFFER1] = -1;
		features [BUFFER2] = -1;
		features [BUFFER3] = -1;
		byte pos;
		int loc;
		for (Piece q: white_pawns){
			pos = q.getPosition();
			loc = WHITE_COLUMN_A + (pos & 7);
			features[loc] = (features[loc] << 8) + pos;
		}
		for (Piece q: black_pawns){
			pos = q.getPosition();
			loc = BLACK_COLUMN_A + (pos & 7);
			features[loc] = (features[loc] << 8) + pos;
		}
		for (int i = 0; i < 8; i++) if (features[WHITE_COLUMN_A+i] == 0) features[WHITE_COLUMN_A+i] = -1;
		for (int i = 0; i < 8; i++) if (features[BLACK_COLUMN_A+i] == 0) features[BLACK_COLUMN_A+i] = -1;
	}
	/**
	 * Determines the number of pawn islands for each side on the board and returns it to an
	 * appropriate index. The first 4 bits represents the number of black islands and the next 4
	 * bits represents the number of white islands.
	 */
	private void pawnislands(){
		if (features [BUFFER1] == 0) pawnformation();
		boolean w_alt = false, b_alt = false;
		int w_islands = 0, b_islands = 0;
		for (int i = 0; i < 9; i++){
			if (!w_alt && !(features[WHITE_COLUMN_A + i] == -1)) w_alt = true;
			else if (w_alt && features[WHITE_COLUMN_A + i] == -1){
				w_islands++;
				w_alt = false;
			}
			if (!b_alt && !(features[BLACK_COLUMN_A + i] == -1)) b_alt = true;
			else if (b_alt && features[WHITE_COLUMN_A + i] == -1){
				b_islands++;
				b_alt = false;
			}
		}
		features[PAWN_ISLANDS] = (w_islands << 4) + b_islands;
	}
	/**
	 * Returns a vectorized quantity with the number of pawns on the column and the column for
	 * both white and black. This does not include columns that have only 1 pawn in them.
	 */
	private void doublepawns(){
		if (features[WHITE_COLUMN_A] == 0) pawnformation();
		long w_to_return = 0, b_to_return = 0;
		for (int i = 0; i < 8; i++){
			long temp = features[WHITE_COLUMN_A + i];
			if (temp > 0xff){
				int count = 0;
				while (temp != 0){
					temp = temp >> 8;
					count++;
				}
				w_to_return = (w_to_return << 8) + (count << 4) + i;
			}
			temp = features[BLACK_COLUMN_A + i];
			if (temp > 0xff){
				int count = 0;
				while (temp != 0){
					temp = temp >> 8;
					count++;
				}
				b_to_return = (b_to_return << 8) + (count << 4) + i;
			}
		}
		features[WHITE_DOUBLED_PAWNS] = w_to_return == 0 ? -1: w_to_return;
		features[BLACK_DOUBLED_PAWNS] = b_to_return == 0 ? -1: b_to_return;
	}
	/**
	 * Calculates the "behind-the-pawn-wall space" value for both sides. The first 4 bytes
	 * are used for the "black" space and the next 4 bytes are used for the white space.
	 */
	private void space (){
		int w_space = 0, b_space = 0;
		for (Piece p : white_pawns) w_space += p.getPosition() >> 4;
		for (Piece p : black_pawns) b_space += 7 - (p.getPosition() >> 4);
		features[SPACE] = (w_space << 16) + b_space;
	}
	/**
	 * This is actually 3 bitstrings compressed into 1 bitstring. The reason for this compression is that
	 * the kingshield and antishield methods often returned 0. This would mean the manager often confused
	 * the result of the method as empty. However, the king tropism value is never 0, by combining these
	 * values, the whole value is never 0.
	 * The bit partitions work as follows:
	 * 1 -> 12 bits (left to right) allocated towards pawn shield, white first then black. The bitnumbers
	 * are from 44 to 56 for rightshift operations.
	 * 2 -> 12 bits (left to right) allocated towards pawn storm / anti shield, white first then black.
	 * The bit numbers are 32 to 44 for rightshift operations.
	 * 3 -> 32 bits are allocated as the remainder for king tropism values. 16 bits for each color.
	 */
	private void kingsafety(){
		long w_storm_Sc = 0, b_storm_Sc = 0, w_tropism_Sc = 0, b_tropism_Sc = 0, w_shield_Sc = 0, b_shield_Sc = 0;
		int w_k_r = white_king >> 4, b_k_r = black_king >> 4, w_k_c = white_king & 7,b_k_c = black_king & 7,
				p_r,p_c;
		byte[] diff_weight_2 = Position.RADIALS, diff_weight_1 = Position.KNIGHT_MOVES;
		boolean w_flag = (white_king >> 4) < 2, b_flag = (black_king >> 4) > 5;
		byte pos, type;
		// pawn storm
		if ((white_king & 7) < 4){
			for (Piece q: black_pawns){
				pos = q.getPosition();
				if ((pos & 7) < 4 && (pos >> 4) < 5) b_storm_Sc += PAWN_STORM_VALUES[pos];
			}
		} else {
			for (Piece q: black_pawns){
				pos = q.getPosition();
				if ((pos & 7) > 3 && (pos >> 4) < 5) b_storm_Sc += PAWN_STORM_VALUES[pos];
			}
		}
		if ((black_king & 0x7) < 4){
			for (Piece q: white_pawns){
				pos = q.getPosition();
				if ((pos & 7) < 4 && (pos >> 4) > 2) 
					w_storm_Sc += PAWN_STORM_VALUES[0x70 - (pos & 0x70) + (pos & 7)];
			}
		} else {
			for (Piece q: white_pawns){
				pos = q.getPosition();
				if ((pos & 7) > 3 && (pos >> 4) > 2) 
					w_storm_Sc += PAWN_STORM_VALUES[0x70 - (pos & 0x70) + (pos & 7)];
			}
		}
		// king tropism
		for (Piece q: white_pieces){
			if ((type = q.getType()) != Piece.NULL && type != Piece.PAWN){
				pos = q.getPosition();
				p_r = pos >> 4;
				p_c = pos & 7;
				w_tropism_Sc += (b_k_r - p_r)*(b_k_r - p_r) + (b_k_c - p_c)*(b_k_c - p_c);
			} else if (type == Piece.NULL) break; 
		}
		for (Piece q: black_pieces){
			if ((type = q.getType()) != Piece.NULL && type != Piece.PAWN){
				pos = q.getPosition();
				p_r = pos >> 4;
				p_c = pos & 7;
				b_tropism_Sc += (w_k_r - p_r)*(w_k_r - p_r) + (w_k_c - p_c)*(w_k_c - p_c);
			} else if (type == Piece.NULL) break; 
		}
		// king shield
		for (byte diff : diff_weight_2) {
			if (w_flag && (diff >> 4) >= 0 && search(white_pawns, (byte) (white_king + diff)).exists())
				w_shield_Sc += 2;
			if (b_flag && (diff >> 4) <= 0 && search(black_pawns, (byte) (black_king + diff)).exists())
				b_shield_Sc += 2;
		}
		for (byte diff : diff_weight_1) {
			if (w_flag && (diff >> 4) >= 0 && search(white_pawns, (byte) (white_king + diff)).exists())
				w_shield_Sc += 1;
			if (b_flag && (diff >> 4) <= 0 && search(black_pawns, (byte) (black_king + diff)).exists())
				b_shield_Sc += 1;
		}
		if (w_flag && search(white_pawns, (byte) (white_king + 2 * Position.UP_MOVE)).exists())
			w_shield_Sc += 1;
		if (b_flag && search(black_pawns, (byte) (white_king + 2 * Position.DOWN_MOVE)).exists())
			b_shield_Sc += 1;
		features[KING_SAFETY] = (((w_shield_Sc << 6) + b_shield_Sc) << 44) + 
				(((w_storm_Sc << 6) + b_storm_Sc) << 32) + ((w_tropism_Sc << 16) + b_tropism_Sc);
	}
	/**
	 * Returns the isolanis and the backward pawns to the proper indices for white and black. The
	 * format is a vectorized 8 byte 0x88 location for each weak pawn.
	 */
	private void weakpawns() {
		if (features[WHITE_COLUMN_A] == 0) pawnformation();
		long white_back = 0, black_back = 0, white_iso = 0, black_iso = 0;
		int w_prev_loc = 0x88, b_prev_loc = 0x88;
		for (int i = white_pawns.length - 1; i >= 0; i--) {
			byte c_pos = white_pawns[i].getPosition();
			if (i == 0) {
				if (features[(c_pos & 0x07) + 5] == 0 && features[(c_pos & 0x07) + 7] == 0)
					white_iso = (white_iso << 8) + c_pos;
			} else {
				byte next = white_pawns[i - 1].getPosition();
				if (Math.abs((c_pos & 0x07) - (next & 0x07)) > 1) {
					if (Math.abs((c_pos & 0x07) - w_prev_loc) != 1) {
						if (features[(c_pos & 0x07) + 5] == 0 && features[(c_pos & 0x07) + 7] == 0)
							white_iso = (white_iso << 8) + c_pos;
						else white_back = (white_back << 8) + c_pos;;
					}
				} else {
					if ((Math.abs((c_pos & 0x07) - w_prev_loc) != 1) && ((c_pos >> 4) != (next >> 4)))
						white_back = (white_back << 8) + c_pos;
					w_prev_loc = c_pos & 0x07;
				}
			}
		}
		for (int i = 0; i < black_pawns.length; i++) {
			byte c_pos = black_pawns[i].getPosition();
			if (i == black_pawns.length - 1) {
				if (features[(c_pos & 0x07) + 5] == 0 && features[(c_pos & 0x07) + 7] == 0)
					black_iso = (black_iso << 8) + c_pos;
			} else {
				byte next = black_pawns[i + 1].getPosition();
				if (Math.abs((c_pos & 0x07) - (next & 0x07)) > 1) {
					if (Math.abs(c_pos & 0x07 - b_prev_loc) != 1) {
						if (features[(c_pos & 0x07) + 5] == 0 && features[(c_pos & 0x07) + 7] == 0)
							black_iso = (black_iso << 8) + c_pos;
						else black_back = (black_back << 8) + c_pos;
					}
				} else {
					if ((Math.abs(c_pos & 0x07 - b_prev_loc) != 1) && (c_pos >> 4 != next >> 4))
						black_back = (black_iso << 8) + c_pos;
					b_prev_loc = c_pos & 0x07;
				}
			}
		}
		features[WHITE_BACKWARDS] = white_back == 0 ? -1: white_back;
		features[BLACK_BACKWARDS] = black_back == 0 ? -1: black_back;
		features[WHITE_ISOLANIS] = white_iso == 0 ? -1: white_iso;
		features[BLACK_ISOLANIS] = black_iso == 0 ? -1: black_iso;
	}
	/**
	 * Determines the sentinel squares using the "control" algorithm developed by IM Yangfan
	 * Zhou. All the squares under a particular color's control is implemented. The algorithm
	 * takes into account who is on the move, the number of pieces attacking a specific
	 * square, and the value of a potential exchange. 
	 */
	private void sentinelsquares(){
		boolean isOnMove = position.isWhiteToMove();
		long [] w_map = new long [0x79], b_map = new long[0x79];
		boolean [] w_sq = new boolean [64], b_sq = new boolean [64];
		updateMap (w_map, white_pieces, position, true);
		updateMap (b_map, black_pieces, position, false);
		for (int i = 0; i < 0x88; i++){
			if ((i & 0x88) == 0) {
				long w = countingSort(w_map[i]), b = countingSort(b_map[i]);
				int result = doBattle(w, b, isOnMove);
				if (result == 1) w_sq [(i>>4)*8+(i&7)] = true;
				if (result == -1) b_sq [(i>>4)*8+(i&7)] = true;
			}
		}
		long w_return = 0, b_return = 0, bin_c = 1;
		for (int i = 0; i < 64; i++){
			long construct = bin_c << i;
			if (w_sq [i]) w_return += construct;
			if (b_sq [i]) b_return += construct;
		}
		features[WHITE_SENTINELS] = w_return;
		features[BLACK_SENTINELS] = b_return;
	}
	/**
	 * Determines the major open and half open lines in the game. This includes all files and all
	 * diagonals with a length of 4 or greater. The first 16 bits (right to left) are allocated
	 * to storing the number of open lines in the order of: half-open diagonals, open diagonals,
	 * half open files, open files. The remaining bits are used to specify until which location
	 * are half open to.
	 */
	private void openlines(){
		if (features[WHITE_COLUMN_A] == 0) pawnformation();
		byte openVertical = 0, halfVertical = 0, openDiagonal = 0, halfDiagonal = 0;
		long toReturn = 0;
		//vertical open & half open files
		for (byte i = 0; i < 8; i++){
			if(features[WHITE_COLUMN_A+i] == -1 && features[BLACK_COLUMN_A+i] == -1) openVertical++;
			else if (features[WHITE_COLUMN_A+i] == -1 || features[BLACK_COLUMN_A+i] == -1) halfVertical++;
		}
		// half files open until?
		for (byte i = 0; i < 8; i++){
			if(features[WHITE_COLUMN_A+i] == -1 && features[BLACK_COLUMN_A+i] != -1){
				long temp = features[BLACK_COLUMN_A+i], latest;
				do {
					latest = temp & 0x7;
					temp >>= 4;
				} while (temp != 0);
				toReturn = (toReturn << 4) + latest;
			} else if(features[WHITE_COLUMN_A+i] != -1 && features[BLACK_COLUMN_A+i] == -1)
				toReturn = (toReturn << 4) + (features[WHITE_COLUMN_A+i] & 7);
			else toReturn <<=4;
		}
		// diagonals - prime diagonals going left from a1 to h8 towards the right, non prime toward left
		boolean [] open = {true, true, true, true, true, true, true};
		boolean [] half = {true, true, true, true, true, true, true};
		boolean [] primeOpen = {true, true, true, true, true, true, true};
		boolean [] primeHalf = {true, true, true, true, true, true, true};
		int diagonal;
		for (int i = 0; i < 2; i++){
			Piece [] to_loop = i == 0 ? white_pawns : black_pawns;
			for(Piece p : to_loop){
				byte s = p.getPosition();
				int row = s >> 4, col = s & 7;
				// center, both prime and non-prime apply
				if (row > 1 && row < 6 && col > 1 && col < 6){
					diagonal = row - col;
					if(diagonal < 0) diagonal += 7;
					if(primeOpen[diagonal]) primeOpen[diagonal] = false;
					else primeHalf[diagonal] = false;
					diagonal = (row+col) % 7;
					if(open[diagonal]) open[diagonal] = false;
					else half[diagonal] = false;
				} else {
					// quadrant IV and III, primes apply only in quadrant III.
					if(row > 3){
						if(col <= 3){
							diagonal = (row + col) % 7;
							if(open[diagonal]) open[diagonal] = false;
							else half[diagonal] = false;
						} else {
							diagonal = row-col;
							if(diagonal < 0) diagonal += 7;
							if(primeOpen[diagonal]) primeOpen[diagonal] = false;
							else primeHalf[diagonal] = false;
						}
					} else{
					// quadrant I and II, primes apply only in quadrant I
						if(col <= 3){
							diagonal = row-col;
							if(diagonal < 0) diagonal += 7;
							if(primeOpen[diagonal]) primeOpen[diagonal] = false;
							else primeHalf[diagonal] = false;						
						}else{
							diagonal = (row + col) % 7;
							if(open[diagonal]) open[diagonal] = false;
							else half[diagonal] = false;
						}
					}
				}
			}
		}
		for(int i = 0; i < 7; i++){
			if(half[i]){
				if(open[i]) openDiagonal++;
				else halfDiagonal++;
			}
			if(primeHalf[i]){
				if(primeOpen[i]) openDiagonal++;
				else halfDiagonal++;
			}
		}
		toReturn = (((toReturn << 4) + openVertical) << 4) + halfVertical;
		toReturn = (((toReturn << 4) + openDiagonal) << 4) + halfDiagonal;
		features[OPEN_FILES] = toReturn;
	}
	// Untested method. Detects passed pawns
	public void passedPawns(){
		long w_string = 0, b_string = 0;
		for (int i = 0; i < 8; i++){
			if (features[BLACK_COLUMN_A + i] != -1){
				boolean flag = true;
				long pos = features[WHITE_COLUMN_A+i] & 0xff;
				// last value must be furthest
				long left = features[BLACK_COLUMN_A+i-1], right = features[BLACK_COLUMN_A];
				if (left != -1) {
					while (left != 0){
						
					}
				}
			}
		}
		features[WHITE_PASSERS] = w_string;
		features[BLACK_PASSERS] = b_string;
	}
	// ----------------------Helper Methods----------------------
	/**
	 * Simulates a potential battle on a square between two specified forces and who is on the
	 * move.
	 * @param w_attack The white attacking forces.
	 * @param b_attack The black attacking forces.
	 * @param toMove The player to move.
	 * @return The "control" of the square, 1 for white, 1 for black.
	 */
	private static int doBattle (long w_attack, long b_attack, boolean toMove){
		if (w_attack == 0){
			if (b_attack != 0) return -1;
			else return 0;
		} else if (b_attack == 0) return 1;
		else {
			long w_loss = 0, b_loss = 0, to_bleed;
			if (toMove){
				while (w_attack != 0 && b_attack != 0){
					to_bleed = w_attack & 0xf;
					if (to_bleed == 0xe) to_bleed = Integer.MAX_VALUE;
					w_loss += to_bleed;
					if (w_loss < b_loss) return 1;
					if (w_loss == 0) break;
					w_attack = w_attack >> 4;
					to_bleed = b_attack & 0xf;
					if (to_bleed == 0xe) to_bleed = Integer.MAX_VALUE;
					b_loss += to_bleed;
					if (w_loss > b_loss) return -1;
					b_attack = b_attack >> 4;
				}
			} else {
				while (w_attack != 0 && b_attack != 0){
					to_bleed = b_attack & 0xf;
					if (to_bleed == 0xe) to_bleed = Integer.MAX_VALUE;
					b_loss += to_bleed;
					if (b_loss < w_loss) return -1;
					if (b_loss == 0) break;
					b_attack = b_attack >> 4;
					to_bleed = w_attack & 0xf;
					if (to_bleed == 0xe) to_bleed = Integer.MAX_VALUE;
					w_loss += to_bleed;
					if (b_loss > w_loss) return 1;
					w_attack = w_attack >> 4;
				}
			}
			if (w_loss < b_loss) return 1;
			if (b_loss < w_loss) return -1;
			if (w_loss == b_loss) {
				if (w_attack == 0) return -1;
				else if (b_attack == 0) return 1;
			}
			return -2;
		}
	}
	/**
	 * Updates the attack and defend maps.
	 * @param map The map to update.
	 * @param toApply The piece set to apply to.
	 * @param p The underlying position object.
	 * @param col The player to update the map for.
	 */
	private static void updateMap(long[] map, Piece[] toApply, Position p, boolean col) {
		byte o_col = col ? Piece.BLACK : Piece.WHITE;
		for (Piece r : toApply) {
			byte c_loc = r.getPosition(), type;
			int n_loc = c_loc;
			if ((type = r.getType()) == Piece.NULL) break;
			switch (type) {
			case Piece.PAWN:
				if (col) {
					n_loc = c_loc + Position.LEFT_UP_MOVE;
					map[(n_loc & 0x88) == 0?n_loc:0x78] = (map[(n_loc & 0x88)==0? n_loc:0x78]<<4)+1;
					n_loc = c_loc + Position.RIGHT_UP_MOVE;
					map[(n_loc & 0x88) == 0?n_loc:0x78] = (map[(n_loc & 0x88)==0? n_loc:0x78]<<4)+1;
				} else {
					n_loc = c_loc + Position.LEFT_DOWN_MOVE;
					map[(n_loc & 0x88) == 0?n_loc:0x78] = (map[(n_loc & 0x88)==0? n_loc:0x78]<<4)+1;
					n_loc = c_loc + Position.RIGHT_DOWN_MOVE;
					map[(n_loc & 0x88) == 0?n_loc:0x78] = (map[(n_loc & 0x88)==0? n_loc:0x78]<<4)+1;
				}
				break;
			case Piece.KNIGHT:
				for (byte d : Position.KNIGHT_MOVES) {
					n_loc = c_loc + d;
					map[(n_loc & 0x88) == 0?n_loc:0x78] = (map[(n_loc & 0x88)==0? n_loc:0x78]<<4)+3;
				}
				break;
			case Piece.BISHOP:
				for (byte d : Position.DIAGONALS) {
					n_loc = c_loc + d;
					while ((n_loc & 0x88) == 0) {
						map[n_loc] = (map[n_loc] << 4) + 3;
						Piece obstruct = p.getSquareOccupier((byte) n_loc);
						if (obstruct.getColour() == o_col) break;
						else if ((type = obstruct.getType()) != Piece.NULL) {
							// takes care of the "behind the back" pawn moves.
							if (type == Piece.PAWN && ((col && d > 0) || (!col && d < 0))) {
								n_loc += d;
								map[(n_loc & 0x88)==0?n_loc:0x78]=(map[(n_loc & 0x88)==0?n_loc:0x78]<<4)+1;
							} else if (type != Piece.BISHOP || type != Piece.QUEEN)break;
						}
						n_loc += d;
					}
				}
				break;
			case Piece.ROOK:
				for (byte d : Position.HORIZONTALS) {
					n_loc = c_loc + d;
					while ((n_loc & 0x88) == 0) {
						map[n_loc] = (map[n_loc] << 4) + 5;
						Piece obstruct = p.getSquareOccupier((byte) n_loc);
						if (obstruct.getColour() == o_col) break;
						else if ((type = obstruct.getType()) != Piece.NULL) 
							if (type != Piece.QUEEN || type != Piece.ROOK) break;
						n_loc += d;
					}
				}
				break;
			case Piece.QUEEN:
				for (byte d : Position.RADIALS) {
					n_loc = c_loc + d;
					while ((n_loc & 0x88) == 0) {
						map[n_loc] = (map[n_loc] << 4) + 9;
						Piece obstruct = p.getSquareOccupier((byte) n_loc);
						if (obstruct.getColour() == o_col)
							break;
						else if ((type = obstruct.getType()) != Piece.NULL) {
							if (type != Piece.BISHOP || type != Piece.ROOK || type != Piece.PAWN) break;
							if (type == Piece.PAWN && ((d&0x7)!=0&&(d>>4)!=0)&&((col&&d>0)||(!col&&d<0))){
								n_loc = n_loc + d;
								map[(n_loc & 0x88)==0?n_loc:0x78]=(map[(n_loc&0x88)==0?n_loc:0x78]<<4)+1;
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
					map[(n_loc & 0x88)==0?n_loc:0x78]= (map[(n_loc&0x88)==0?n_loc:0x78]<<4) + 0xe;
				}
				break;
			}
		}
	}
	/**
	 * Sorts a Lorenz bitstring.
	 * @param string A lorenz bitstring that is partitioned to 4 bits per index.
	 * @return A sorted Lorenz bitstring.
	 */
	private static long countingSort(long string){
		if (string <= 0xf) return string;
		else{
			long toReturn = 0;
			int[] c = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
			int sum = 0, index = 0;
			while (string != 0){
				c[(int)(string & 0xf)] ++;
				string = string >> 4;
			}
			for(int i = c.length - 1; i >= 0; i--){
				sum += c[i];
				c[i] = sum;
			}
			for(int i = c.length - 1; i >= 0; i--){
				for(int k = index; k < c[i]; k++) toReturn = (toReturn << 4) + i;
				index = c[i];
			}
			return toReturn;
		}
	}
	/**
	 * Performs a binary search through a sorted map of pieces.
	 * @param sorted_map A sorted map of pieces.
	 * @param to_find The location to find.
	 * @return The piece occupying the specified location.
	 */
	private static Piece search (Piece [] sorted_map, byte to_find){
		int hi = sorted_map.length - 1, lo = 0, mid = (hi+lo) / 2;
		while (hi >= lo){
			byte l = sorted_map[mid].getPosition();
			if (l == to_find) return sorted_map[mid];
			if (to_find > l){
				lo = mid+1;
				mid = (hi + lo) / 2;
			} else {
				hi = mid-1;
				mid = (hi + lo) / 2;
			}
		}
		return Piece.getNullPiece();
	}
	/**
	 * Sorts through a map of pieces by location in increasing order.
	 * @param map The map to sort.
	 * @param lo The lowest bound to sort through.
	 * @param hi The highest bound to sort through.
	 */
	private static void sort(Piece[] map, int lo, int hi) {
		if (map.length <= 1) return;
		int i = lo, j = hi;
		Piece temp, pivot = map[(lo + hi) / 2];
		byte pos = pivot.getPosition();
		do {
			while (map[i].getPosition() < pos) i++;
			while (map[j].getPosition() > pos) j--;
			if (i <= j) {
				temp = map[i];
				map[i] = map[j];
				map[j] = temp;
				i++;
				j--;
			}
		} while (i <= j);
		if (lo < j) sort(map, lo, j);
		if (i < hi) sort(map, i, hi);
	}
	// Untested method. Returns true if empty, false otherwise
	public static boolean boxEmpty(Piece[] enemy_map, byte pos, boolean isWhitePiece){
		byte startPos = (byte)(pos + Position.LEFT_MOVE);
		byte direction = isWhitePiece ? Position.UP_MOVE: Position.DOWN_MOVE;
		byte range = isWhitePiece ? (byte)(8 - (startPos >> 4)): (byte)(startPos >> 4);
		for(int i = 0; i < range; i++){
			for(int j = 0; j < 3; j++){
				for(Piece p: enemy_map){
					/*if(isWhitePiece){
						if((p.getPosition() >> 4) <= (startPos >> 4)) return true;
					}
					else{
						if((p.getPosition() >> 4) >= (startPos >> 4)) return true;
					}*/
					if(p.getPosition() == (byte)(startPos + (j*Position.RIGHT_MOVE))) return false;
				}
			}
			startPos = (byte)(startPos + direction);
		}
		return true;
	}
	// ----------------------End of Helper Methods----------------------
	// ----------------------End of Methods----------------------
}