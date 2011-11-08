package debug;
import rules.*;

public class ToBeAdded {
	final static int QUEEN_MUL = 1;
	final static int BISHOP_MUL = 1;
	final static int ROOK_MUL = 1;
	final static int KNIGHT_MUL = 1;
	public int getKingTropism(byte k_loc, Piece[] o_map){
		int distance = 0 ;
		int k_rol = k_loc >> 4;
		int k_col = k_loc & 0x7;
		int c_pos, c_col,c_rol;
		int multiplier;
		for (Piece c_p : o_map){
			c_pos= c_p.getPosition();
			c_col = c_pos >> 4;
			c_rol = c_pos & 0x7;
			switch(c_p.getType()){
			case Piece.BISHOP: multiplier = BISHOP_MUL; break;
			case Piece.KNIGHT: multiplier = KNIGHT_MUL; break;
			case Piece.ROOK: multiplier = ROOK_MUL; break;
			case Piece.QUEEN: multiplier = QUEEN_MUL; break;
			default: multiplier = 0;
			}
			distance += multiplier * (Math.pow((k_rol - c_rol),2) + Math.pow((k_col - c_col), 2));
		}
		return distance;
	}
}
