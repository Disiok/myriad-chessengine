package engine;

import rules.*;

public class Myriad {
	public interface PersonalityCore {
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
		public int passed_pawn_value = o;
		
		public int evaluatePosition (Position p){
			int total_value = 0;
		    
		    // The Values of Different Pieces. Ex. The identifier for pawns is 0. Therefore PIECE_VALUES[ imaginary_pawn.getType() ] == 100 .
		    final int PIECE_VALUES = {100,450,300,300,950,0};
		    Piece[] pieces;
		    // thinking of simply making one and using self-call method to find the points of the other side.
		    //Piece[] opp_pieces;
		    
		    if (p.isWhiteToMove()){
		    	pieces = p.getWhitePieces();
		    	// opp_pieces = p.getBlackPieces();
		    }
		    else {
		    	pieces = p.getBlackPieces();
		    	// opp_pieces = p.getWhitePieces();
		    }
		    
		    // adds all self pieces
		    for (int i = 0; i < pieces.length; i++ ){
		      total_value += PIECE_VALUES[ pieces[i].getType() ];
		    }
		    	    
		    // pawn structure - passed pawns, more efficient way welcomed.
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
