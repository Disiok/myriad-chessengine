package eval;

import rules.*;

public class Crescent {
	Lorenz lz;
	
	public Crescent (Position p){
		lz = new Lorenz (p);
	}
	public void determineGamePhase(){
		// TODO determine the game phase
	}
	public void weightSentinels(){
		// TODO weight each of the squares on the board for sentinel control
	}
	public void determineFocus(){
		// TODO determine which side to focus
	}
}
