package eval;

import eval.FeatureManager.*;
import rules.*;

public class PieceFeatures extends Feature{
	public PieceFeatures (Feature bf){
		super (bf);
	}
	public String detectBishopVersusKnight(){
		return null;
	}
	public String detectTwoBishops (){
		boolean w = false, b = false;
		if (white_bishops.length >= 2 && isOnOppositeColour (white_bishops[0],white_bishops[1])) w = true;
		if (white_bishops.length >= 2 && isOnOppositeColour (black_bishops[0],black_bishops[1])) b = true;
		return ""+w+"|"+b;
	}
	public String detectMaterialImbalance(){
		String [] white_black = {"",""};
		getImbalanceMessage(white_pawns,black_pawns,white_black,"p");
		getImbalanceMessage(white_rooks,black_rooks,white_black,"r");
		getImbalanceMessage(white_knights,black_knights,white_black,"n");
		getImbalanceMessage(white_bishops,black_bishops,white_black,"b");
		getImbalanceMessage(white_queens,black_queens,white_black,"q");
		return white_black[0] + "|" + white_black[1];
	}
	public String detectOppositeColouredBishops(){
		if (white_bishops.length==1&&black_bishops.length==1&&
				isOnOppositeColour(white_bishops[0],black_bishops[0])) return "true";
		else return "false";
	}
	private String [] getImbalanceMessage(Piece[] a, Piece[] b, String [] w_b, String symbol){
		int sz = a.length - b.length;
		if (sz == 0) return w_b;
		else if (sz > 0) w_b[0] = w_b[0] + sz + symbol + ",";
		else if (sz < 0) w_b[1] = w_b[1] + (-1*sz) + symbol + ",";
		return w_b;
	}
	private boolean isOnOppositeColour (Piece a, Piece b){
		byte sq_a = a.getPosition(), sq_b = b.getPosition();
		int sq_colour_a = (sq_a/0x10 + sq_a%0x10)%2, sq_colour_b = (sq_b/0x10 + sq_b%0x10)%2;
		if (sq_colour_a + sq_colour_b == 1) return true;
		else return false;
	}
}
