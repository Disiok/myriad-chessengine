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
	public String detectOpenPawnFiles(){
		int openFile = 0, halfFile = 0, diagOpen = 0, halfDiag = 0;
		int[] b_col = new int[black_pawns.length], w_col=new int[white_pawns.length];
		int[] b_hor = new int[black_pawns.length], w_hor=new int[white_pawns.length];

		for (int i = 0; i < white_pawns.length; i++) {
			w_col[i] = white_pawns[i].getPosition() & 0x7;
			w_hor[i] = white_pawns[i].getPosition() >> 4;
		}
		for (int i = 0; i < black_pawns.length; i++) {
			b_col[i] = black_pawns[i].getPosition() & 0x7;
			b_hor[i] = black_pawns[i].getPosition() >> 4;
		}
		//vertical openPawnFiles
		for (int i = 0; i < 8; i++){
			boolean whiteHas = false, blackHas = false;
			for(int j : w_col) if (j == i) whiteHas = true;
			for(int j : b_col) if (j == i) blackHas = true;

			if (!whiteHas && !blackHas) openFile++;
			else if ((whiteHas && !blackHas) || (!whiteHas && blackHas)) halfFile++;
		}

		//top left to bottom right open pawnFiles
		int yCount = 3, xCount = 0;
		while(xCount <= 4 && yCount <= 7){
			boolean whiteHas = false, blackHas = false;
			for(int x = xCount, y = yCount; y >= 0 || x <= 7 ; x++, y--)
			{
				for(int i = 0; i < black_pawns.length; i++)
				{
					if(b_col[i] == x && b_hor[i] == y) blackHas = true;
				}
				for(int i = 0; i < white_pawns.length; i++)
				{
					if(w_col[i] == x && w_hor[i] == y) whiteHas = true;
				}
			}
			if (!whiteHas && !blackHas) diagOpen++;
			else if ((whiteHas && !blackHas) || (!whiteHas && blackHas)) halfDiag++;

			if(yCount == 7 && xCount <= 4) xCount++;
			else if(yCount <= 7) yCount++;
		}

		// top right to bottom left pawn files
		yCount = 3;
		xCount = 7;
		while(xCount >= 3 && yCount <= 7){
			boolean whiteHas = false, blackHas = false;
			for(int x = xCount, y = yCount; y >= 0 || x >= 0 ; x--, y--)
			{
				for(int i = 0; i < black_pawns.length; i++)
				{
					if(b_col[i] == x && b_hor[i] == y) blackHas = true;
				}
				for(int i = 0; i < white_pawns.length; i++)
				{
					if(w_col[i] == x && w_hor[i] == y) whiteHas = true;
				}
			}
			if (!whiteHas && !blackHas) diagOpen++;
			else if ((whiteHas && !blackHas) || (!whiteHas && blackHas)) halfDiag++;

			if(yCount == 7 && xCount >= 3) xCount--;
			else if(yCount <= 7) yCount++;
		}
		return openFile + "|" + halfFile + "|" + diagOpen + "|" + halfDiag + "";
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
	public String detectPawnIslands(){
		String csg = featureManager.retrieveFeatureComponent(2, "ColumnStructureGroup");
		String [] w_b = csg.split("\\Q|\\E");
		String w = ","+ w_b[0], b = "," + w_b[1];
		String [] white_islands = w.split("#"), black_islands = b.split("#");
		int w_count = 0, b_count = 0;
		for (String q : white_islands) if (!q.equals(",")) w_count++;
		for (String q : black_islands) if (!q.equals(",")) b_count++;
		return w_count + "|" + b_count;
	}
	public String detectSpace(){
		int w_space = 0, b_space = 0;
		for(Piece p: white_pawns) w_space += p.getPosition() >> 4;
		for(Piece p: black_pawns) b_space += 7 - (p.getPosition() >> 4);
		return w_space + "|" + b_space;
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
		byte[] difference = new byte[]{Position.UP_MOVE, Position.RIGHT_UP_MOVE, Position.LEFT_UP_MOVE, 2*Position.UP_MOVE, 
				Position.KNIGHT_MOVES[0], Position.KNIGHT_MOVES[1], Position.KNIGHT_MOVES[4], Position.KNIGHT_MOVES[6], 
				2*Position.RIGHT_UP_MOVE, 2*Position.LEFT_UP_MOVE};
		String w_toReturn = "", b_toReturn = "";
		for(int i = 0; i < 2; i++){
			double value = 0;
			Piece king = i<1 ? white_king[0] : black_king[0];
			int lower_boundary = i<1 ? 0x10 : 0x50, upper_boundary = i<1 ? 0x30: 0x70;
			for(byte diff: difference){
				diff = i<1 ? (byte) diff : (byte) -diff;
				Piece occupier = p.getSquareOccupier((byte)(king.getPosition() + diff));
				boolean pawn = ((occupier.getType() == Piece.PAWN) 
						&& (byte)(king.getPosition() + diff) >= lower_boundary && (byte)(king.getPosition() + diff) <= upper_boundary);
				if (pawn){
					switch(diff){
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
		return w_toReturn + "|" + b_toReturn;
	}

	public String detectAntiPawnCover(Position p){
		String w_toReturn = "", b_toReturn = "";
		for(int i = 0; i < 2; i++){
			Piece king = i<1 ? white_king[0] : black_king[0];
			byte startLoc = (byte)(king.getPosition() + 2*Position.LEFT_MOVE);
			byte diff = i<1 ? Position.UP_MOVE : Position.DOWN_MOVE;
			for(int j = 0; j < 5; j++) {
				byte searchLoc = startLoc;
				for(int k = 0; k < 5; k++){
					Piece isPawn = p.getSquareOccupier(searchLoc);
					if(isPawn.getType() == Piece.PAWN && isPawn.getColour() != king.getColour()){
						w_toReturn += i<1 ? isPawn.toString() + " " : "";
						b_toReturn += i<1 ? "" : isPawn.toString() + " ";
					}
					searchLoc += diff;
				}
				startLoc += Position.RIGHT_MOVE;
			}
		}
		
		return w_toReturn.substring(0, w_toReturn.length()) + "| " + b_toReturn.substring(0, b_toReturn.length());
	}
}
