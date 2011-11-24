package tree;

import debug.FenUtility;
import rules.*;

public class Pine {
	private Leaf current_leaf;
	private Leaf best_child;
	private Leaf[] offsprings_of_best_child;

	public Pine(Position p) {
		current_leaf = new Leaf(p);
		current_leaf.setChildren();
	}

	public Pine() {
		current_leaf = new Leaf(new Position());
		current_leaf.setChildren();
	}

	public void setCurrentLeaf(Position p) {
		if (offsprings_of_best_child != null)
			for (Leaf offspring : offsprings_of_best_child) {
				if (FenUtility.saveFEN(offspring.getPosition()).equals(
						FenUtility.saveFEN(p))) {
					current_leaf = offspring;
				}
			}
		else {
			current_leaf = new Leaf(p);
			current_leaf.setChildren();
		}
	}

	public void NegaMax(int depth) {
		int best = Integer.MIN_VALUE;
		Leaf[] children = current_leaf.getChildren();
		for (Leaf child : children) {
			int current = -NegaMax(child, depth, Integer.MIN_VALUE,
					Integer.MAX_VALUE, 1);
			if (current > best) {
				best_child = child;
				best = current;
			}
		}
		offsprings_of_best_child = best_child.getChildren();
	}

	private int NegaMax(Leaf child, int depth, int alpha, int beta, int color) {
		if (child.isLastLeaf() || depth == 0) {
			int n = child.getEval();
			return color * n;
		} else {
			Leaf[] offsprings = child.getChildren();
			if (offsprings == null) {
				child.setChildren();
				offsprings = child.getChildren();
			}
			for (Leaf offspring : offsprings) {
				alpha = Math.max(alpha,
						-NegaMax(offspring, depth - 1, -beta, -alpha, -color));
				if (alpha > beta)
					break;
			}
			return alpha;
		}
	}

	public Position getBestPosition() {
		return best_child.getPosition();
	}

	public Move getBestMove() {
		return best_child.getPriorMove();
	}
}