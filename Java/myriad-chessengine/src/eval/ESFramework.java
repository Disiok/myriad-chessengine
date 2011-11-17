package eval;

import rules.*;
import java.util.*;

/**
 * The ESF: Evaluative String Framework is a framework that organises all the strings into a Features array.
 * The framework allows is simpler than the old FeatureManager framework and runs faster (construction is
 * 5 times faster). It retains all functionality of the old FeatureManager class.
 * @author Spork Innovations
 */
public class ESFramework {
	//----------------------Constants----------------------
	/** The index for the white material imbalance feature.*/
	public static final int WHITE_MATERIAL = 0;
	/** The index for the black material imbalance feature.*/
	public static final int BLACK_MATERIAL = 1;
	/** The index for the bishop vs. knight imbalance feature.*/
	public static final int BISHOP_VS_KNIGHT = 2;
	/** The index for the two bishops feature.*/
	public static final int TWO_BISHOPS = 3;
	/** The index for the opposite bishops imbalance feature.*/
	public static final int OPPOSITE_BISHOPS = 4;
	/** The index for the column structure grouping for white. */
	public static final int WHITE_COLUMN_A = 5;
	public static final int WHITE_COLUMN_B = 6;
	public static final int WHITE_COLUMN_C = 7;
	public static final int WHITE_COLUMN_D = 8;
	public static final int WHITE_COLUMN_E = 9;
	public static final int WHITE_COLUMN_F = 10;
	public static final int WHITE_COLUMN_G = 11;
	public static final int WHITE_COLUMN_H = 12;
	/** The index for the column structure grouping for white. */
	public static final int BLACK_COLUMN_A = 13;
	public static final int BLACK_COLUMN_B = 6;
	public static final int BLACK_COLUMN_C = 7;
	public static final int BLACK_COLUMN_D = 8;
	public static final int BLACK_COLUMN_E = 9;
	public static final int BLACK_COLUMN_F = 10;
	public static final int BLACK_COLUMN_G = 11;
	public static final int BLACK_COLUMN_H = 12;
	/** The index for the pawn island count feature for white. */
	public static final int WHITE_PAWN_ISLANDS = 13;
	/** The index for the pawn island count feature for black. */
	public static final int BLACK_PAWN_ISLANDS = 14;
	public static final int WHITE_ISOLANIS = 15;
	public static final int BLACK_ISOLANIS = 16;
	//----------------------End of Constants----------------------
	//----------------------Instance Variables----------------------
	/** The white pawns.*/
	protected Piece [] white_pawns;
	/** The black pawns.*/
	protected Piece [] black_pawns;
	/** The white bishops.*/
	private Piece [] white_bishops;
	/** The black bishops.*/
	private Piece [] black_bishops;
	/** The white pieces.*/
	private Piece [] white_pieces;
	/** The black pieces.*/
	private Piece [] black_pieces;
	/** A reference to link the current evaluative string with the associated position.*/
	protected Position pos_ref;
	/** The container for the evaluative strings.*/
	public String [] FEATURES = new String [50];
	//----------------------End of Instance Variables----------------------
	//----------------------Methods----------------------
	public ESFramework (Position p){
		Vector <Piece> w_p = new Vector <Piece>(8),b_p = new Vector <Piece>(8), 
				w_b = new Vector<Piece> (2,1), b_b = new Vector <Piece> (2,1);
		white_pieces = p.getWhitePieces();
		black_pieces = p.getBlackPieces();
		for (Piece q : white_pieces){
			int val = q.getType();
			if (val == Piece.NULL) break;
			switch (val){
				case Piece.PAWN: w_p.add(q); break;
				case Piece.BISHOP: w_b.add(q); break;
			}
		}
		for (Piece q : black_pieces){
			int val = q.getType();
			if (val == Piece.NULL) break; 
			switch (val){
				case Piece.PAWN: b_p.add(q); break;
				case Piece.BISHOP: b_b.add(q); break;
			}
		}
		white_pawns = w_p.toArray(new Piece[w_p.size()]);
		black_pawns = b_p.toArray(new Piece[b_p.size()]);
		white_bishops = w_b.toArray(new Piece [w_b.size()]);
		black_bishops = b_b.toArray(new Piece [b_b.size()]);
		pos_ref = p;
	}
	public void material (){
		int [][] material = new int [2][6];
		String w_material = "", b_material = "";
		for (Piece p: white_pieces){
			int type = p.getType();
			if (type != Piece.NULL) material [0][type] ++;
		}
		for (Piece p: black_pieces) {
			int type = p.getType();
			if (type != Piece.NULL) material [1][type] ++;
		}
		for (int i = 0; i < 5; i++){
			int diff = material[0][i] - material[1][i];
			if (diff != 0){
				int pure_diff = Math.abs(diff);
				String toAdd = "" + pure_diff;
				switch (i){
					case Piece.PAWN: toAdd += "p,"; break;
					case Piece.ROOK: toAdd += "r,"; break;
					case Piece.BISHOP: toAdd += "b,"; break;
					case Piece.KNIGHT: toAdd += "n,"; break;
					case Piece.QUEEN: toAdd += "q,"; break;
				}
				if (diff > 0) w_material += toAdd;
				else b_material += toAdd;
			}
		}
		FEATURES[WHITE_MATERIAL] = w_material;
		FEATURES[BLACK_MATERIAL] = b_material;
	}
	public void bishopvknight(){
		if (FEATURES[WHITE_MATERIAL] == null) material();
		String w_imb = FEATURES[WHITE_MATERIAL], b_imb = FEATURES[BLACK_MATERIAL];
		if (w_imb.contains("b")) {
			if (b_imb.contains("n")) {
				FEATURES[BISHOP_VS_KNIGHT] = "w";
				return;
			} else FEATURES[BISHOP_VS_KNIGHT] = "n";
		} else if (b_imb.contains("b")){
			if (w_imb.contains("n")){
				FEATURES[BISHOP_VS_KNIGHT] ="b";
				return;
			} else FEATURES[BISHOP_VS_KNIGHT] = "n";
		} else FEATURES[BISHOP_VS_KNIGHT] = "n";
	}
	public void twobishops(){
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
		if (flag) FEATURES[TWO_BISHOPS] = "" + w + b;
		else FEATURES[TWO_BISHOPS] = "n";
	}
	public void oppositebishops(){
		if (white_bishops.length == 1 && black_bishops.length == 1){
			byte w_loc = white_bishops[0].getPosition(), b_loc = black_bishops[0].getPosition();
			if (((w_loc >> 4 + w_loc & 0x7)&0x1 + (b_loc >> 4 + b_loc & 0x7) & 0x1) == 1)
				FEATURES[OPPOSITE_BISHOPS] = "y";
		}
		FEATURES[OPPOSITE_BISHOPS] = "n";
	}
	public void columnstruct (){
		for (int i = 0; i < 8; i++) FEATURES[WHITE_COLUMN_A + i] = "";
		for (int i = 0; i < 8; i++) FEATURES[BLACK_COLUMN_A + i] = "";
		for (Piece p : white_pawns){
			byte loc = p.getPosition();
			FEATURES[WHITE_COLUMN_A + (loc >> 4)] += loc + " ";
		}
		for (Piece p : black_pawns){
			byte loc = p.getPosition();
			FEATURES[BLACK_COLUMN_A + (loc >> 4)] += loc + " ";
		}
	}
	public void pawnislands(){
		if (FEATURES[WHITE_COLUMN_A] == null) columnstruct();
		boolean w_alt = false, b_alt = false;
		int w_islands = 0, b_islands = 0;
		for (int i = 0; i < 8; i++){
			if (!w_alt && !FEATURES[WHITE_COLUMN_A+i].equals("")) w_alt = true;
			if (w_alt && FEATURES[WHITE_COLUMN_A+i].equals("")) {
				w_islands++;
				w_alt = false;
			}
			if (!b_alt && !FEATURES[BLACK_COLUMN_A+i].equals("")) b_alt = true;
			if (b_alt && FEATURES[BLACK_COLUMN_A+i].equals("")){
				b_islands++;
				b_alt = false;
			}
		}
		FEATURES[WHITE_PAWN_ISLANDS] = "" + w_islands;
		FEATURES[BLACK_PAWN_ISLANDS] = "" + b_islands;
	}
	public void isolani (){
		StringBuffer White = new StringBuffer(""), Black = new StringBuffer("");
		for (int i = 0; i < 8; i++){
			if (!FEATURES[WHITE_COLUMN_A+i].equals("")){
				if ((i==0||FEATURES[WHITE_COLUMN_A+i-1].equals(""))&&(i==7||
						FEATURES[WHITE_COLUMN_A+i+1].equals(""))) White = White.append('a'+i);
			}
			if (!FEATURES[BLACK_COLUMN_A+i].equals("")){
				if ((i==0||FEATURES[BLACK_COLUMN_A+i].equals(""))&&(i==7||
						FEATURES[BLACK_COLUMN_A+i+1].equals(""))) Black = Black.append('a'+i);
			}
		}
		FEATURES[WHITE_ISOLANIS] = White.toString();
		FEATURES[BLACK_ISOLANIS] = Black.toString();
	}
	//----------------------End of Methods----------------------
}