package eval;

import eval.FeatureManager.*;
import rules.*;

public class DynamicFeatures extends Feature{
	public DynamicFeatures(Position bf, FeatureManager fm){
		super(bf, fm);
	}
	public DynamicFeatures(Feature bf) {
		super(bf);
	}
	public String detectOpenOrClosed(){
		// TODO: detect whether the position is open or closed.
		return null;
	}
	public String detectAttackingPlayer(){
		// TODO: detect which player is attacking.
		return null;
	}
	public String detectAttackingPotential(){
		// TODO: detect which player has attacking potential.
		return null;
	}
	public String detectKingsideOrQueensideAction(){
		// TODO: detect whether the majority of the action is taking place on the kingside or queenside.
		return null;
	}
}
