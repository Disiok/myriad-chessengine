package eval;

import eval.FeatureManager.Feature;
import rules.*;

public class AntiPawnCover extends Feature{
	public AntiPawnCover(Feature bf) {
		super(bf);
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
