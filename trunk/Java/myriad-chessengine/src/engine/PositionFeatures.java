package engine;

import rules.*;
import java.util.*;

public class PositionFeatures {
	
	
	private PositionPlus pp;
	public Vector <Piece> w_I_Pawn;
	public Vector <Piece> b_I_Pawn;
	public Vector <Piece> w_D_Pawn;
	public Vector <Piece> b_D_Pawn;
	public Vector <Piece> w_B_Pawn;
	public Vector <Piece> b_B_Pawn;
	public Vector <Piece> w_P_Pawn;
	public Vector <Piece> b_P_Pawn;
	public boolean w_D_Bishop;
	public boolean b_D_Bishop;
	public Vector <Byte> w_Outpost;
	public Vector <Byte> b_Outpost;
	
	public PositionFeatures (PositionPlus pp){
		this.pp = pp;
		detectDoubledPawns();
		detectIsolatedPawns();
		detectBackwardPawns();
		detectPassedPawns();
		detectOutposts();
		detectBishopVersusKnight();
		detectDoubleBishops();
		
	}
	public void detectDoubledPawns(){
		w_D_Pawn = new Vector <Piece> (4,0);
		b_D_Pawn = new Vector <Piece> (4,0);
		boolean[] col = {true, false};
		for (boolean c_col : col){
			Piece[] map = c_col? pp.white_pawns : pp.black_pawns;
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
		w_I_Pawn = new Vector <Piece> (4,0);
		b_I_Pawn = new Vector <Piece> (4,0);
		boolean[] col = {true, false};
		for (boolean c_col : col){
			Piece[] map = c_col? pp.white_pawns : pp.black_pawns;
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
		w_B_Pawn = new Vector <Piece> (4,0);
		b_B_Pawn = new Vector <Piece> (4,0);
		boolean[] col = {true, false};
		for (boolean c_col : col){
			Piece[] c_map = c_col? pp.white_pawns : pp.black_pawns;
			Piece[] o_map = c_col? pp.black_pawns : pp.white_pawns;
			for (Piece c : c_map){
				boolean isBackward = false;
				byte c_loc = c.getPosition();
				byte left_forward = c_col? (byte)(c_loc + Position.LEFT_UP_MOVE) : (byte) (c_loc + Position.LEFT_DOWN_MOVE);
				byte left = (byte)(c_loc + Position.LEFT_MOVE);
				byte right_forward = c_col? (byte)(c_loc + Position.RIGHT_UP_MOVE) : (byte) (c_loc + Position.RIGHT_DOWN_MOVE);
				byte right = (byte)(c_loc + Position.RIGHT_MOVE);
				for (Piece o: c_map){
					if ((o.getPosition()  == left_forward)
							||(o.getPosition() == right_forward)){
						byte block = (byte) ((c_col? (Position.UP_MOVE) : Position.DOWN_MOVE) +  o.getPosition()) ;
						for (Piece op : o_map){
							if (op.getPosition() == block){
								isBackward = true;
								break;
							}
						}
					}
					else if ((o.getPosition()  == left)
							||(o.getPosition() == right)){
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
		w_P_Pawn = new Vector <Piece> (8,0);
		b_P_Pawn = new Vector <Piece> (8,0);
		boolean[] col = {true, false};
		for (boolean c_col : col){
			Piece[] c_map = c_col? pp.white_pawns : pp.black_pawns;
			Piece[] o_map = c_col? pp.black_pawns : pp.white_pawns;
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
		w_Outpost = new Vector <Byte> (4,1);
		b_Outpost = new Vector <Byte> (4,1);
		boolean[] col = {true, false};
		for (boolean c_col : col){
			Piece[] c_map = c_col? pp.white_pawns : pp.black_pawns;
			Piece[] o_map = c_col? pp.black_pawns : pp.white_pawns;
			for (Piece c : c_map){
				byte c_loc = c.getPosition();
				byte left_forward = c_col? (byte)(c_loc + Position.LEFT_UP_MOVE) : (byte) (c_loc + Position.LEFT_DOWN_MOVE);
				byte right_forward = c_col? (byte)(c_loc + Position.RIGHT_UP_MOVE) : (byte) (c_loc + Position.RIGHT_DOWN_MOVE);
				byte[] sq = {left_forward, right_forward};
				for (byte c_sq : sq){
					boolean outpost = false;
					if (pp.getSquareOccupier(c_sq).getType() == -1 && (c_sq & 0x88) == 0){
						outpost = true;
						byte left_file = (byte)(c_sq % 0x10 + Position.LEFT_MOVE);
						if (left_file == 0xf) left_file = -1;
						byte right_file =(byte)(c_sq % 0x10 + Position.RIGHT_MOVE);
						if (right_file == 0x8) right_file = -1;
						for (Piece o : o_map){
							byte o_loc = o.getPosition();
							if (o_loc % 0x10 == left_file ||
									o_loc % 0x10 == right_file){
								if (c_col && (o_loc > c_sq + Position.RIGHT_MOVE)){
									outpost = false;
									break;
								}
								else if ((!c_col) && (o_loc < c_sq + Position.LEFT_MOVE)){
									outpost = false;
									break;
								}
							}
						}
					}
					if (outpost){
						if (c_col) w_Outpost.add(c_sq);
						else b_Outpost.add(c_sq);
					}
				}
			}
		}
	}
	public void detectBishopVersusKnight(){
		// TODO: Detect if there is a b vs. knight material imbalance.
	}
	public void detectDoubleBishops (){
		w_D_Bishop = false;
		b_D_Bishop = false;
		boolean[] col = {true, false};
		for (boolean c_col : col){
			Piece[] c_map = c_col? pp.white_bishops : pp.black_bishops;
			byte sq_col = -1;
			boolean doubleBishops = false;
			for (Piece c : c_map){
				if (sq_col == -1)
					sq_col = (byte)((c.getPosition()/0x10 + c.getPosition()%0x10)%2);
				else if ((c.getPosition()/0x10 + c.getPosition()%0x10)%2 != sq_col)
					doubleBishops = true;
			}
			if (doubleBishops){
				if (c_col) w_D_Bishop = true;
				else b_D_Bishop = true;
			}
		}
	}		
}
