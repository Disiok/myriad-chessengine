package eval;

import rules.*;

public class Crescent {
	private static final long MATERIAL_MASK = 0xfff00000;
	
	Lorenz lz;
	
	protected static class PieceTables {
		// TODO Implement piece square tables
	}
	protected static class Weights {
		// TODO Implement a set of weights
	}
	public Crescent (Position p){
		lz = new Lorenz (p);
	}
	public void determineSurplus(){
		long w_absolute = lz.get(Lorenz.WHITE_ABSOLUTE_MATERIAL), 
				b_absolute = lz.get(Lorenz.BLACK_ABSOLUTE_MATERIAL);
		long diff = ((w_absolute & MATERIAL_MASK) >> 20)- ((b_absolute & MATERIAL_MASK) >> 20);
		if (diff > 200) {
			// TODO: set weights to decisive white advantage
		} else if (diff >= 20){
			// TODO: set weights to convertible white advantage
		} else if (diff > -20){
			// TODO: set weights to play with even material
		} else if (diff < -20){
			// TODO: set weights to convertible black advantage
		} else if (diff < -200){
			// TODO: set weights to decisive black advantage
		}
	}
	public void determineGamePhase(){
		long w_absolute = lz.get(Lorenz.WHITE_ABSOLUTE_MATERIAL),
				b_absolute = lz.get(Lorenz.BLACK_ABSOLUTE_MATERIAL);
		long average = (((w_absolute & MATERIAL_MASK)>>20) + ((b_absolute & MATERIAL_MASK)>>20))/2;
		if (average > 2800){
			// set weights towards middle game.
		} else if (average > 2000){
			// set weights towards late-middle game.
		} else if (average > 1200){
			// set weights to early end game.
		} else {
			// set weights to end game. 
		}
	}
	public void weightSentinels(){
		// TODO: Check weights
		// TODO: Weight central squares.
		// TODO: Weight squares around the king if king middle game
		// TODO: Weight squares in front of "special pawns" in the late and endgame.
		// TODO: Weight squares on open and half open files in the middle and early end game
		// TODO: Devalue squares around the edge of the board.
	}
	public void determineFocus(){
		// TODO determine which side to focus
	}
}