package eval;

import java.util.Vector;

import rules.*;

/**
 * Lorenz is the third framework to the chess engine. It is named after the Lorenz cipher
 * machine which use the similar bitwise manipulation techniques. This new framework is
 * approximately 30 times faster in certain areas by bypassing the string concactenation
 * step and replacing it with bitwise manipulation. 
 * @author Jesse Wang, Andy Huang, Jacob Huang
 */
public class Lorenz {
	public static final byte WHITE_ABSOLUTE_MATERIAL = 0;
	public static final byte BLACK_ABSOLUTE_MATERIAL = 1;
	public static final byte WHITE_RELATIVE_MATERIAL = 2;
	public static final byte BLACK_RELATIVE_MATERIAL = 3;
	public static final byte BISHOP_VS_KNIGHT = 4;
	public static final byte TWO_BISHOPS = 5;
	public static final byte OPPOSITE_BISHOPS = 6;
	public static final byte BUFFER1 = 7;
	public static final byte WHITE_COLUMN_A = 8;
	public static final byte WHITE_COLUMN_B = 9;
	public static final byte WHITE_COLUMN_C = 10;
	public static final byte WHITE_COLUMN_D = 11;
	public static final byte WHITE_COLUMN_E = 12;
	public static final byte WHITE_COLUMN_F = 13;
	public static final byte WHITE_COLUMN_G = 14;
	public static final byte WHITE_COLUMN_H = 15;
	public static final byte BUFFER2 = 16;
	public static final byte BLACK_COLUMN_A = 17;
	public static final byte BLACK_COLUMN_B = 18;
	public static final byte BLACK_COLUMN_C = 19;
	public static final byte BLACK_COLUMN_D = 20;
	public static final byte BLACK_COLUMN_E = 21;
	public static final byte BLACK_COLUMN_F = 22;
	public static final byte BLACK_COLUMN_G = 23;
	public static final byte BLACK_COLUMN_H = 24;
	public static final byte BUFFER3 = 25;
	public static final byte PAWN_ISLANDS = 25;
	public static final byte WHITE_PASSERS = 26;
	public static final byte BLACK_PASSERS = 27;
	public static final byte WHITE_DOUBLED_PAWNS = 28;
	public static final byte BLACK_DOUBLED_PAWNS = 29;
	public static final byte KING_TROPISM = 30;
	public static final byte PAWN_STORM = 31;
	public static final byte ANTI_SHIELD = 32;
	public static final byte KING_SHIELD = 33;
	
	public static final short PAWN_VALUE = 100;
	public static final short KNIGHT_VALUE = 325;
	public static final short BISHOP_VALUE = 340;
	public static final short ROOK_VALUE = 500;
	public static final short QUEEN_VALUE = 975;
	public static final byte[] PAWN_STORM_VALUES = { 0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0, 6, 6, 6, 6, 6, 6, 6, 6, 0, 0, 0, 0, 0, 0,
		0, 0, 8, 8, 8, 5, 4, 8, 8, 8, 0, 0, 0, 0, 0, 0, 0, 0, 2, 3, 3, 1,
		1, 3, 3, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	
	protected Piece[] white_pawns;
	protected Piece[] black_pawns;
	protected Piece[] white_pieces;
	protected Piece[] black_pieces;
	protected byte white_king;
	protected byte black_king;
	
	public long [] features = new long [50];
	protected Position position;
	
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
		for (Piece r: white_pawns){
			System.out.println(r);
		}
		for (Piece r: black_pawns){
			System.out.println(r);
		}
	}
	public void material(){
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
	public void bishopvknight (){
		if (features[WHITE_ABSOLUTE_MATERIAL] == 0) material();
		long rel_w = features[WHITE_RELATIVE_MATERIAL], rel_b = features[BLACK_RELATIVE_MATERIAL];
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
		if (w_b && b_n) features[BISHOP_VS_KNIGHT] = 0x100;
		else if (b_b && w_n) features[BISHOP_VS_KNIGHT] = 0x010;
		else features[BISHOP_VS_KNIGHT] = 0x001;
	}
	public void twobishops(){
		if (features[WHITE_ABSOLUTE_MATERIAL] == 0) material();
		long w_abs = features[WHITE_ABSOLUTE_MATERIAL], b_abs = features[BLACK_ABSOLUTE_MATERIAL], 
				n_bishops_w = (w_abs&(0xf<<12))>>4, n_bishops_b = (b_abs&(0xf<<4))>>4,to_return = 0x001;
		if (n_bishops_w == 2) to_return += 0x100;
		if (n_bishops_b == 2) to_return += 0x010;
		features[TWO_BISHOPS] = to_return;
	}
	public void oppositebishops(){
		if (features[WHITE_ABSOLUTE_MATERIAL] == 0) material();
		if ((features[WHITE_ABSOLUTE_MATERIAL]&(0xf<<4))>>4!=1 || 
				(features[BLACK_ABSOLUTE_MATERIAL]&(0xf<<4))>>4 !=1){
			features[OPPOSITE_BISHOPS] = 1;
			return;
		}
		int w_orient = 0, b_orient = 0;
		for (Piece q: white_pieces){
			if (q.getType() == Piece.BISHOP){
				byte r = q.getPosition();
				w_orient = ((r >> 4) + (r & 7)) & 1;
				break;
			}
		}
		for (Piece q: black_pieces){
			if (q.getType() == Piece.BISHOP){
				byte r = q.getPosition();
				b_orient = ((r >> 4) + (r & 7)) & 1;
				break;
			}
		}
		features[OPPOSITE_BISHOPS] = (((w_orient + b_orient)&1) == 1) ? 16 : 1;
	}
	public void pawnformation(){
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
	public void pawnislands(){
		if (features [BUFFER1] == 0) pawnformation();
		boolean w_alt = false, b_alt = false;
		int w_islands = 0, b_islands = 0;
		for (int i = 0; i < 8; i++){
			if (!w_alt && !(features[WHITE_COLUMN_A + i] == -1)) w_alt = true;
			else if (w_alt && features[WHITE_COLUMN_A + i] == -1){
				w_islands++;
				w_alt = false;
			}
			if (!b_alt && !(features[BLACK_COLUMN_A + i] == -1)) b_alt = true;
			else if (w_alt && features[WHITE_COLUMN_A + i] == -1){
				b_islands++;
				b_alt = false;
			}
		}
		features[PAWN_ISLANDS] = (w_islands << 4) + b_islands;
	}
	public void doublepawns(){
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
		features[WHITE_DOUBLED_PAWNS] = w_to_return;
		features[BLACK_DOUBLED_PAWNS] = b_to_return;
	}
	public void kingtropism(){
		long w_to_return = 0, b_to_return = 0;
		byte type, loc;
		int w_k_r = white_king>>4, b_k_r = black_king>>4, w_k_c = white_king&7, b_k_c=black_king&7,p_r,p_c;
		for (Piece q: white_pieces){
			if ((type = q.getType()) != Piece.NULL && type != Piece.PAWN){
				loc = q.getPosition();
				p_r = loc >> 4;
				p_c = loc & 7;
				w_to_return += (b_k_r - p_r)*(b_k_r - p_r) + (b_k_c - p_c)*(b_k_c - p_c);
			}
		}
		for (Piece q: black_pieces){
			if ((type = q.getType()) != Piece.NULL && type != Piece.PAWN){
				loc = q.getPosition();
				p_r = loc >> 4;
				p_c = loc & 7;
				b_to_return += (w_k_r - p_r)*(w_k_r - p_r) + (w_k_c - p_c)*(w_k_c - p_c);
			}
		}
		features[KING_TROPISM] = (w_to_return << 16) +  b_to_return;
	}
	public void antishield(){
		long w_to_return = 0, b_to_return = 0;
		byte pos;
		if ((white_king & 7) < 4){
			for (Piece q: black_pawns){
				pos = q.getPosition();
				if ((pos & 7) < 4 && (pos >> 4) < 5) b_to_return += PAWN_STORM_VALUES[pos];
			}
		} else {
			for (Piece q: black_pawns){
				pos = q.getPosition();
				if ((pos & 7) > 3 && (pos >> 4) < 5) b_to_return += PAWN_STORM_VALUES[pos];
			}
		}
		if ((black_king & 0x7) < 4){
			for (Piece q: white_pawns){
				pos = q.getPosition();
				if ((pos & 7) < 4 && (pos >> 4) > 2) 
					w_to_return += PAWN_STORM_VALUES[0x70 - (pos & 0x70) + (pos & 7)];
			}
		} else {
			for (Piece q: white_pawns){
				pos = q.getPosition();
				if ((pos & 7) > 3 && (pos >> 4) > 2) 
					w_to_return += PAWN_STORM_VALUES[0x70 - (pos & 0x70) + (pos & 7)];
			}
		}
		features[ANTI_SHIELD] = (w_to_return << 12) + b_to_return;
	}
	public void kingshield(){
		byte[] diff_weight_2 = Position.RADIALS;
		byte[] diff_weight_1 = Position.KNIGHT_MOVES;
		boolean w_flag = (white_king >> 4) < 2, b_flag = (black_king >> 4) > 5;
		long w_shield = 0, b_shield = 0;
		for (byte diff : diff_weight_2) {
			if (w_flag && (diff >> 4) >= 0 && search(white_pawns, (byte) (white_king + diff)).exists())
				w_shield += 2;
			if (b_flag && (diff >> 4) <= 0 && search(black_pawns, (byte) (black_king + diff)).exists())
				b_shield += 2;
		}
		for (byte diff : diff_weight_1) {
			if (w_flag && (diff >> 4) >= 0 && search(white_pawns, (byte) (white_king + diff)).exists())
				w_shield += 1;
			if (b_flag && (diff >> 4) <= 0 && search(black_pawns, (byte) (black_king + diff)).exists())
				b_shield += 1;
		}
		if (w_flag && search(white_pawns, (byte) (white_king + 2 * Position.UP_MOVE)).exists())
			w_shield += 1;
		if (w_flag && search(white_pawns, (byte) (white_king + 2 * Position.DOWN_MOVE)).exists())
			b_shield += 1;
		features[KING_SHIELD] = (w_shield << 16) + b_shield;
	}
	private static Piece search (Piece [] sorted_map, byte to_find){
		int hi = sorted_map.length - 1, lo = 0, mid = (hi+lo) / 2;
		while (hi > lo){
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
	private static void sort(Piece[] map, int lo, int hi) {
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
}