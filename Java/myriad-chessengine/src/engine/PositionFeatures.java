package engine;

import rules.*;
import java.util.*;

public class PositionFeatures {
	
	private PositionPlus pp;
	private Piece[] white_pawns;
	private Piece[] white_rooks;
	private Piece[] white_knights;
	private Piece[] white_bishops;
	private Piece[] white_queens;
	private Piece white_king;
	
	private Piece[] black_pawns;
	private Piece[] black_rooks;
	private Piece[] black_knights;
	private Piece[] black_bishops;
	private Piece[] black_queens;
	private Piece black_king;
	
	public Vector <Piece> w_I_Pawn = new Vector <Piece> (4,0);
	public Vector <Piece> b_I_Pawn = new Vector <Piece> (4,0);
	public Vector <Piece> w_D_Pawn = new Vector <Piece> (4,0);
	public Vector <Piece> b_D_Pawn = new Vector <Piece> (4,0);
	public Vector <Piece> w_B_Pawn = new Vector <Piece> (4,0);
	public Vector <Piece> b_B_Pawn = new Vector <Piece> (4,0);
	public Vector <Piece> w_P_Pawn = new Vector <Piece> (8,0);
	public Vector <Piece> b_P_Pawn = new Vector <Piece> (8,0);
	
	public PositionFeatures (PositionPlus pp){
		this.pp = pp;
		white_pawns = pp.getPieces(Piece.PAWN, true);
 		white_rooks = pp.getPieces(Piece.ROOK, true);
		white_knights = pp.getPieces(Piece.KNIGHT, true);
		white_bishops = pp.getPieces(Piece.BISHOP, true);
		white_queens = pp.getPieces(Piece.QUEEN, true);
		
		black_pawns = pp.getPieces(Piece.PAWN, false);
		black_rooks = pp.getPieces(Piece.ROOK, false);
		black_knights = pp.getPieces(Piece.KNIGHT, false);
		black_bishops = pp.getPieces(Piece.BISHOP, false);
		black_queens = pp.getPieces(Piece.QUEEN, false);
	}
	public void detectDoubledPawns(){
		boolean[] col = {true, false};
		for (boolean c_col : col){
			Piece[] map = c_col? white_pawns : black_pawns;
			Vector <Piece> v = new Vector <Piece> (4,0);
			for (Piece c : map){
				if (!v.contains(c)){
					byte c_file = (byte)(c.getPosition() % 0x10);
					byte c_loc = (byte)c.getPosition();
					for (Piece o: map){
						if ((o.getPosition() % 0x10 == c_file)
								&&(o.getPosition() != c_loc)){
							v.add(c);
							v.add(o);
							break;
						}
					}
				}
			}
			if (c_col) w_D_Pawn = v;
			else b_D_Pawn = v;
		}
    }
	public void detectIsolatedPawns(){
		boolean[] col = {true, false};
		for (boolean c_col : col){
			Piece[] map = c_col? white_pawns : black_pawns;
			for (Piece c : map){
				boolean isIsolated = true;
				byte left_file = (byte)(c.getPosition() % 0x10 + Position.LEFT_MOVE);
				if (left_file == 0xf) left_file = -1;
				byte right_file =(byte)(c.getPosition() % 0x10 + Position.RIGHT_MOVE);
				if (right_file == 0x8) right_file = -1;
				for (Piece o: map){
					if ((o.getPosition() % 0x10 == left_file)
							||(o.getPosition() % 0x10 == right_file)){
						isIsolated = false;
						break;
					}
				}
				if (isIsolated){
					if (c_col) w_I_Pawn.add(c);
					else b_I_Pawn.add(c);
				}
			}
		}
	}
	public void detectBackwardPawns(){
		boolean[] col = {true, false};
		for (boolean c_col : col){
			Piece[] map = c_col? white_pawns : black_pawns;
			for (Piece c : map){
				boolean isBackward = false;
				byte c_loc = c.getPosition();
				byte left_protection = c_col? (byte)(c_loc + Position.LEFT_DOWN_MOVE) : (byte) (c_loc + Position.LEFT_UP_MOVE);
				byte right_protection = c_col? (byte)(c_loc + Position.RIGHT_DOWN_MOVE) : (byte) (c_loc + Position.RIGHT_UP_MOVE);
				byte left_file = (byte)(c.getPosition() % 0x10 + Position.LEFT_MOVE);
				if (left_file == 0xf) left_file = -1;
				byte right_file =(byte)(c.getPosition() % 0x10 + Position.RIGHT_MOVE);
				if (right_file == 0x8) right_file = -1;
				for (Piece o: map){
					if ((o.getPosition() % 0x10 == left_file)
							||(o.getPosition() % 0x10 == right_file)){
						isBackward = true;
						break;
					}
				}
				for (Piece o: map){
					if ((o.getPosition()  == left_protection)
							||(o.getPosition() == right_protection)){
						isBackward = false;
						break;
					}
				}
				if (isBackward){
					if (c_col) w_B_Pawn.add(c);
					else b_B_Pawn.add(c);
				}
			}
		}
	}
	
	public void detectPassedPawns(){
		boolean[] col = {true, false};
		for (boolean c_col : col){
			Piece[] c_map = c_col? white_pawns : black_pawns;
			Piece[] o_map = c_col? black_pawns : white_pawns;
			for (Piece c : c_map){
				boolean isPassed = true;
				byte c_loc = c.getPosition();
				byte c_file = (byte)(c_loc % 0x10);
				byte left_file = (byte)(c_loc % 0x10 + Position.LEFT_MOVE);
				if (left_file == 0xf) left_file = -1;
				byte right_file =(byte)(c_loc % 0x10 + Position.RIGHT_MOVE);
				if (right_file == 0x8) right_file = -1;
				for (Piece o: o_map){
					byte o_loc = o.getPosition();
					if ((o_loc % 0x10 == left_file)
							||(o_loc % 0x10 == right_file)
							||(o_loc % 0x10 == c_file)){
						if (c_col && o_loc > c_loc)
							isPassed = false;
						else if ((!c_col) && o_loc < c_loc)
							isPassed = false;
						break;
					}
				}
				if (isPassed){
					if (c_col) w_P_Pawn.add(c);
					else b_P_Pawn.add(c);
				}
			}
		}
	}
	public void detectOutposts(){
		// TODO: Detect Passed Pawns
	}
	public void detectBishopVersusKnight(){
		// TODO: Detect if there is a b vs. knight material imbalance.
	}
	public void detectTwoBishops (){
		// TODO: Detect if there is a 2 bishop advantage.
	}
}
