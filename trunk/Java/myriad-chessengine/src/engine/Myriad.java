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
		
		public int evaluatePosition (Position p){
			int total_value = 0;
		    
		    // The Values of Different Pieces. Ex. The identifier for pawns is 0. Therefore PIECE_VALUES[ imaginary_pawn.getType() ] == 100 .
		    final int PIECE_VALUES = {100,450,300,300,950,0};
		    Piece[] pieces;
		    Piece[] opp_pieces;
		    if (p.isWhiteToMove()){
		    	pieces = p.getWhitePieces();
		    	opp_pieces = p.getBlackPieces();
		    }
		    else {
		    	pieces = p.getBlackPieces();
		    	opp_pieces = p.getWhitePieces();
		    }
		    // adds all self pieces
		    for (int i = 0; i < pieces.length; i++ ){
		      total_value += PIECE_VALUES[ pieces[i].getType() ];
		    }
		    // minus all opponent's pieces
		    for (int i = 0; i < opp_pieces.length; i++ ){
			      total_value -= PIECE_VALUES[ opp_pieces[i].getType() ];
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
