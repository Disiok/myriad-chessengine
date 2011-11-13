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
		for (String s: white_pawn) w_toReturn += (s.equals("") ? "#" : s.trim()) + ",";
		for (String s: black_pawn) b_toReturn += (s.equals("") ? "#" : s.trim()) + ",";
		return w_toReturn.substring(0,w_toReturn.length()-1)+"|"+b_toReturn.substring(0,b_toReturn.length()-1);
	}
	public String detectPassedPawns (){
		String csg = featureManager.retrieveFeatureComponent(2, "ColumnStructureGroup");
		String [] w_b = csg.split("\\Q|\\E"), w_file = w_b[0].split(","), b_file = w_b[1].split(",");
		String w_toReturn = "", b_toReturn = "";
		for (int i = 0; i < 8; i++){
			if (!w_file[i].equals("#") && b_file[i].equals("#")){
				if ((i == 0 || b_file[i-1].equals("#"))&& (i == 7 || b_file[i+1].equals("#"))){
					String [] passed = w_file[i].split(" ");
					w_toReturn += findFurthestPawn(true, passed) + ",";
				}
			}
			if (!b_file[i].equals("#") && w_file[i].equals("#")){
				if ((i == 0 || w_file[i-1].equals("#"))&& (i == 7 || w_file[i+1].equals("#"))){
					String [] passed = b_file[i].split(" ");
					b_toReturn += findFurthestPawn(false, passed) + ",";
				}
			}
		}
		w_toReturn = w_toReturn.equals("") ? w_toReturn : w_toReturn.substring(0, w_toReturn.length()-1);
		b_toReturn = b_toReturn.equals("") ? b_toReturn : b_toReturn.substring(0, b_toReturn.length()-1);
		return w_toReturn+"|"+b_toReturn;
	}
	private String findFurthestPawn (boolean direction, String [] pawns){
		int furthest = 0, furthest_ind = -1;
		for (int i = 0; i < pawns.length; i++){
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
		byte[] w_difference = new byte[]{Position.UP_MOVE, Position.RIGHT_UP_MOVE, Position.LEFT_UP_MOVE, 2*Position.UP_MOVE, 
				Position.KNIGHT_MOVES[0], Position.KNIGHT_MOVES[1], Position.KNIGHT_MOVES[4], Position.KNIGHT_MOVES[6], 
				2*Position.RIGHT_UP_MOVE, 2*Position.LEFT_UP_MOVE};
		byte[] b_difference = new byte[]{Position.DOWN_MOVE, Position.RIGHT_DOWN_MOVE, Position.LEFT_DOWN_MOVE, 2*Position.DOWN_MOVE, 
				Position.KNIGHT_MOVES[2], Position.KNIGHT_MOVES[3], Position.KNIGHT_MOVES[5], Position.KNIGHT_MOVES[7], 
				2*Position.RIGHT_DOWN_MOVE, 2*Position.LEFT_DOWN_MOVE};
		String w_toReturn = "", b_toReturn = "";
		double value = 0;
		for(int i = 0; i < 2; i++){
			Piece king = i<1 ? white_king[0] : black_king[0];
			int lower_boundry = i<1 ? 0x10 : 0x50, upper_boundry = i<1 ? 0x40: 0x50;
			for(byte diff: p.isWhiteToMove() ? w_difference : b_difference){
				boolean pawn = ((p.getSquareOccupier((byte)(king.getPosition() + diff)) != Piece.getNullPiece()) 
						&& (byte)(king.getPosition() + diff) >= lower_boundry && (byte)(king.getPosition() + diff) <= upper_boundry);
				if (pawn){
					switch((byte)(diff)){
						case Position.UP_MOVE: case Position.RIGHT_UP_MOVE: case Position.LEFT_UP_MOVE: value += 3; break;
						case Position.DOWN_MOVE: case Position.RIGHT_DOWN_MOVE: case Position.LEFT_DOWN_MOVE: value += 3; break;
						case 2*Position.UP_MOVE: value += 2.5; break;
						case 2*Position.DOWN_MOVE: value += 2.5; break;
						case 2*Position.UP_MOVE+Position.RIGHT_MOVE: case 2*Position.UP_MOVE+Position.LEFT_MOVE:
							case 2*Position.RIGHT_MOVE+Position.UP_MOVE: case 2*Position.LEFT_MOVE+Position.UP_MOVE: value += 2; break;
						case 2*Position.DOWN_MOVE+Position.RIGHT_MOVE: case 2*Position.DOWN_MOVE+Position.LEFT_MOVE:
							case 2*Position.RIGHT_MOVE+Position.DOWN_MOVE: case 2*Position.LEFT_MOVE+Position.DOWN_MOVE: value += 2; break;
						case 2*Position.RIGHT_UP_MOVE: case 2*Position.LEFT_UP_MOVE: value += 1; break;
						case 2*Position.RIGHT_DOWN_MOVE: case 2*Position.LEFT_DOWN_MOVE: value += 1; break;	
					}
				}
			}
			w_toReturn = i<1 ? Double.toString(value) : "";
			b_toReturn = i<1 ? "" : Double.toString(value);
		}
		return w_toReturn.substring(0,w_toReturn.length()-1)+"|"+b_toReturn.substring(0,w_toReturn.length()-1);
	}
}
