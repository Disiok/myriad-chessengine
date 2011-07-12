package engine;

import rules.*;

public class Myriad {
	public interface PersonalityCore {
		public static final int passed_pawn_value = 0;
	    public static final int [] PIECE_VALUES = {100,450,300,300,950,0};
	    
		public boolean requestFurtherDepth (Position p, boolean vantagePoint);
		public int evaluatePosition (Position p);
	}
	/*
	public class OpeningsCore implements PersonalityCore{
		// TODO: Openings book.
	}
	public class EndgamesCore implements PersonalityCore{
		// TODO: Tablebases
	}*/
	/*public class QuiescentCore implements PersonalityCore{
		// TODO: Quiescent search.
	}*/
	public class ObjectiveCore implements PersonalityCore{
		// return an evaluation of the position. Remember that E(pos) = white_score-black_score.
		// calculate all the white and black scores in the position given and sum them up.
		
		// FIXME: Always look at the position from white's vantage point only.
		public int evaluatePosition (Position p){
			int total_value = 0;
		    Piece[] w_pieces = p.getWhitePieces();
		    Piece[] b_pieces = p.getBlackPieces();
		    for (int i = 0; i < w_pieces.length; i++ ){
		    	// FIXME: do an array index out of bounds check for null pieces.
		    	total_value += PIECE_VALUES[w_pieces[i].getType()];
		    	total_value -= PIECE_VALUES[b_pieces[i].getType()];
		    }
		    /* pawn structure - passed pawns, more efficient way welcomed.
		    for (int i = 0; i < pieces.length; i++ ){
			      if (piece.getType()==0) {
			    	  byte direction;
			    	  
			    	  if (p.isWhiteToMove()) direction = p.UP_MOVE;
			    	  else direction = p.DOWN_MOVE;
			      
			    	  int j = 1;
			    	  while (p.getSquareOccupier(pieces[i].getPosition() + j*direction).isEqual(Piece.NULL_PIECE)){
			    		  if (pieces[i].getPosition() + j*direction >= 70 || pieces[i].getPosition() + j*direction <= 7){
			    			  total_value += passed_pawn_value;
			    			  break;
			    		  }
			    		  j++;
			    	  }
			    	  
			      }
			}
		    */
		    
		   return total_value;
			// TODO: Evaluate the chess position.
			// TODO: Count pawn structure. (see ChessProgramming wiki)
			// TODO: Count king safety. (see ChessProgramming wiki)
			// TODO: Count piece mobility. (see ChessProgramming wiki)
		}
		public boolean requestFurtherDepth (Position p, boolean vantagePoint){
			// IGNORE THIS FOR NOW.
			return false;
		}
	}
	public Myriad(){
		// TODO: Initialise Personality Cores.
	}
	public Move decideOnMove (Position p, boolean vantagePoint){
		return null;
	}
}
