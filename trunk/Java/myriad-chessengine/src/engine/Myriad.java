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
			// test 
			// TODO: Evaluate the chess position.
			// TODO: Count the material, Queen = 950, Rook = 450, Knight,Bishop = 300, Pawn = 100
			// TODO: Count pawn structure. (see ChessProgramming wiki)
			// TODO: Count king safety. (see ChessProgramming wiki)
			// TODO: Count piece mobility. (see ChessProgramming wiki)
			return -1;
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
