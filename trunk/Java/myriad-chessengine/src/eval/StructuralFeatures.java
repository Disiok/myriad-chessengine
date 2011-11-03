package eval;

import rules.*;
import eval.FeatureManager.*;

public class StructuralFeatures extends Feature{
	public StructuralFeatures(Feature bf) {
		super(bf);
	}
	public String detectColumnStructureGroup (){
		String [] white_pawn = new String [8], black_pawn = new String [8];
		String w_toReturn = "", b_toReturn = "";
		for (int i = 0; i < 8; i++) white_pawn[i] = "";
		for (int i = 0; i < 8; i++) black_pawn[i] = "";
		for (Piece p : white_pawns) white_pawn[p.getPosition()%0x10] += p.toString() + " ";
		for (Piece p : black_pawns) black_pawn[p.getPosition()%0x10] += p.toString() + " ";
		for (String s: white_pawn) w_toReturn += s.trim() + ",";
		for (String s: black_pawn) b_toReturn += s.trim() + ",";
		return w_toReturn.substring(0,w_toReturn.length()-1)+"|"+b_toReturn.substring(0,w_toReturn.length()-1);
	}
	public String detectPassedPawns (){
		String csg = featureManager.retrieveFeatureComponent(2, "ColumnStructureGroup");
		String [] w_b = csg.split("\\Q|\\e"), w_file = w_b[0].split(","), b_file = w_b[1].split(",");
		String w_toReturn = "", b_toReturn = "";
		for (int i = 0; i < 8; i++){
			if (!w_file[i].equals("") && b_file[i].equals("")){
				if ((i == 0 || b_file[i-1].equals(""))&& (i == 7 || b_file[i+1].equals(""))){
					String [] passed = w_file[i].split(" ");
					w_toReturn += findFurthestPawn(true, passed) + ",";
				}
			}
			if (!b_file[i].equals("") && w_file[i].equals("")){
				if ((i == 0 || w_file[i-1].equals(""))&& (i == 7 || w_file[i+1].equals(""))){
					String [] passed = w_file[i].split(" ");
					w_toReturn += findFurthestPawn(false, passed) + ",";
				}
			}
		}
		return w_toReturn.substring(0,w_toReturn.length()-1)+"|"+b_toReturn.substring(0,w_toReturn.length()-1);
	}
	private String findFurthestPawn (boolean direction, String [] pawns){
		int furthest = 1, furthest_ind = -1;
		for (int i = 0; i < 8; i++){
			int rnk = pawns[i].charAt(1) - '0';
			if (!direction) rnk = 8 - rnk;
			if (rnk > furthest) {
				furthest = rnk;
				furthest_ind = i;
			}
		}
		return pawns[furthest_ind];
	}
	public String detectPawnCover(Position p){
		byte[] difference = new byte[]{Position.UP_MOVE, Position.RIGHT_UP_MOVE, Position.LEFT_UP_MOVE, 2*Position.UP_MOVE, 
				Position.KNIGHT_MOVES[0], Position.KNIGHT_MOVES[1], Position.KNIGHT_MOVES[4], Position.KNIGHT_MOVES[5], 
				2*Position.RIGHT_UP_MOVE, 2*Position.LEFT_UP_MOVE};
		String w_toReturn = "", b_toReturn = "";
		double value = 0;
		for(int i = 0; i < 2; i++){
			Piece king = i<1 ? white_king[0] : black_king[0];
			int lower_boundry = i<1 ? 0x10 : 0x50, upper_boundry = i<1 ? 0x40: 0x50;
			for(byte diff: difference){
				diff = i<1 ? diff : (byte)-diff;
				boolean pawn = ((p.getSquareOccupier((byte)(king.getPosition() + diff)) != Piece.getNullPiece()) 
						&& (byte)(king.getPosition() + diff) >= lower_boundry && (byte)(king.getPosition() + diff) <= upper_boundry);
				if (pawn){
					switch((byte)Math.abs(diff)){
						case Position.UP_MOVE: case Position.RIGHT_UP_MOVE: case Position.LEFT_UP_MOVE: value += 3; break;
						case 2*Position.UP_MOVE: value += 2.5; break;
						case 2*Position.UP_MOVE+Position.RIGHT_MOVE: case 2*Position.UP_MOVE+Position.LEFT_MOVE:
							case 2*Position.RIGHT_MOVE+Position.UP_MOVE: case 2*Position.RIGHT_MOVE+Position.DOWN_MOVE: value += 2; break;
						case 2*Position.RIGHT_UP_MOVE: case 2*Position.LEFT_UP_MOVE: value += 1; break;						
					}
				}
			}
			w_toReturn = i<1 ? Double.toString(value) : "";
			b_toReturn = i<1 ? "" : Double.toString(value);
		}
		return w_toReturn.substring(0,w_toReturn.length()-1)+"|"+b_toReturn.substring(0,w_toReturn.length()-1);
	}
	public static void main (String [] args){
		Position p = new Position ();
		FeatureManager fm = new FeatureManager(p);
		System.out.println(fm.retrieveFeatureComponent(2, "PassedPawns"));
	}
	/*
 	public Vector <Piece> w_I_Pawn;
	public Vector <Piece> b_I_Pawn;
	public Vector <Piece> w_D_Pawn;
	public Vector <Piece> b_D_Pawn;
	public Vector <Piece> w_B_Pawn;
	public Vector <Piece> b_B_Pawn;
	public Vector <Piece> w_P_Pawn;
	public Vector <Piece> b_P_Pawn;	
	public Vector <Byte> w_Outpost;
	public Vector <Byte> b_Outpost;
	private void detectDoubledPawns(){
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
	private void detectIsolatedPawns(){
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
	private void detectBackwardPawns(){
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
	private void detectPassedPawns(){
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
	}*/
}
