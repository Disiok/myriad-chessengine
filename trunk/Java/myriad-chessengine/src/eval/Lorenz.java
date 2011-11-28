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
	
	public static final short PAWN_VALUE = 100;
	public static final short KNIGHT_VALUE = 325;
	public static final short BISHOP_VALUE = 340;
	public static final short ROOK_VALUE = 500;
	public static final short QUEEN_VALUE = 975;
	
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
}