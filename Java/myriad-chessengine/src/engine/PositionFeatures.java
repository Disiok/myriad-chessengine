package engine;

import rules.*;

public class PositionFeatures {
	
	private Position p;
	private Piece[] white_pawns;
	private Piece[] white_rooks;
	private Piece[] white_knights;
	private Piece[] white_bishops;
	private Piece[] white_queens; // are arrays since pawns can promote.
	private Piece white_king;
	
	private Piece[] black_pawns;
	private Piece[] black_rooks;
	private Piece[] black_knights;
	private Piece[] black_bishops;
	private Piece[] black_queens;
	private Piece black_king;
	
	
	public class (Position p){
		this.p = p;
		white_pawns = null; // PositionPlus.getPawns
 		white_rooks = null;
		white_knights = null;
		white_bishops = null;
		white_queens = null;
		
		black_pawns = null;
		black_rooks = null;
		black_bishops = null;
		black_knights = null;
		black_queens = null;
	}
	
	public void detectDoubledPawns(){
		String pawn_column_numbers = "";
        for (Piece c : white_pawns){
        	char c_column = Character.forDigit(c.getPosition() % 0x10, 10); 
        	for (int i = 0; i < pawn_column_numbers.length(); i++){
        		if (c_column == pawn_column_numbers.charAt(i)){
        			// do something
        			// pawn 1 will be c. the doubled pawns will be white_pawns[i]
        			// note. will receive repetitve results. (as in: 1-23,  2-13,  3-12);
        		}
        	}
        	pawn_column_numbers += "" + c_column;
        }
        
        for (Piece c : black_pawns){
        	char c_column = Character.forDigit(c.getPosition() % 0x10, 10); 
        	for (int i = 0; i < pawn_column_numbers.length(); i++){
        		if (c_column == pawn_column_numbers.charAt(i)){
        			// do something
        			// pawn 1 will be c. the doubled pawns will be white_pawns[i]
        			// note. will receive repetitve results. (as in: 1-23,  2-13,  3-12);
        		}
        	}
        	pawn_column_numbers += "" + c_column;
        }
    }
	
	public void detectIsolatedPawns(){
		boolean isolatedPawn;
		
		for (Piece c : white_pawns){
			isolatedPawn = true;
			Piece left_lane = Postion.getSquareOccupier((c.getPosition() + Position.LEFT_MOVE) % 10);
			Piece right_lane = Postion.getSquareOccupier((c.getPosition() + Position.RIGHT_MOVE) % 10);
			
			for (int i = 0; i < 8; i++){
				if (((left_lane.getType () == Piece.PAWN) && (left_lane.getColour() == Piece.WHITE)) 
						|| ((right_lane.getType () == Piece.PAWN) && (right_lane.getColour() == Piece.WHITE))){
					isolatedPawn = false;					
				}
				left_lane += Position.UP_MOVE;
				right_lane += Position.UP_MOVE;
			}
			
			if (isolatedPawn){
				// this piece has no pawns of the same colour in adjacent lanes.
				// do stuff with it
				// note. there could be pawns in the same lane
			}
		}
		
		for (Piece c : black_pawns){
			isolatedPawn = true;
			Piece left_lane = Postion.getSquareOccupier((c.getPosition() + Position.LEFT_MOVE) % 10);
			Piece right_lane = Postion.getSquareOccupier((c.getPosition() + Position.RIGHT_MOVE) % 10);
			
			for (int i = 0; i < 8; i++){
				if (((left_lane.getType () == Piece.PAWN) && (left_lane.getColour() == Piece.BLACK)) 
						|| ((right_lane.getType () == Piece.PAWN) && (right_lane.getColour() == Piece.BLACK))){
					isolatedPawn = false;					
				}
				left_lane += Position.UP_MOVE;
				right_lane += Position.UP_MOVE;
			}
			
			if (isolatedPawn){
				// this piece has no pawns of the same colour in adjacent lanes.
				// do stuff with it
				// note. there could be pawns in the same lane
			}
		}	
	}
	public void detectBackwardPawns(){
		for (Piece c : white_pawns){
			
			Piece left_down = Position.getSquareOccupier(c.getPosition() + Piece.LEFT_DOWN_MOVE);
			Piece right_down = Position.getSquareOccupier(c.getPosition() + Piece.RIGHT_DOWN_MOVE);
			if ((left_down.getType() != Piece.Pawn) && (right_down.getType() != Piece.Pawn)){
				// this pawn is unprotected by its own pawns.
				// do stuff with it.
				// note. this pawn may be protected by other things: bishops, knights, etc.
			}
		}
		for (Piece c : black	_pawns){
			
			Piece left_up = Position.getSquareOccupier(c.getPosition() + Piece.LEFT_UP_MOVE);
			Piece right_up = Position.getSquareOccupier(c.getPosition() + Piece.RIGHT_UP_MOVE);
			if ((left_down.getType() != Piece.Pawn) && (right_down.getType() != Piece.Pawn)){
				// this pawn is unprotected by its own pawns.
				// do stuff with it.
				// note. this pawn may be protected by other things: bishops, knights, etc.
			}
		}
	}
	
	public void detectPassedPawns(){
		// TODO: Detect Passed Pawns
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
