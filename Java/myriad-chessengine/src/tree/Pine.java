package tree;

import eval.*;
import rules.*;

public class Pine {
	private Maple root_leaf;
	private Maple best_child;
	private Maple[] offsprings_of_best_child;
	private static int counter;
	
	public Pine(Position p){
		root_leaf = new Maple(null, null, p);
	}

	public void setCurrentLeaf(Position p, Move prior_move) {
		if (offsprings_of_best_child != null)
			for (Maple offspring : offsprings_of_best_child) {
				if (offspring.getPriorMove().isEqual(prior_move)) {
					root_leaf = offspring;
					break;
				}
			}
		else {
			root_leaf = new Maple(null, prior_move, p);
		}
	}
	
	
	public void NegaMax(Position original,Move prior_move, int depth) {
		
		System.out.println("Negamax Start");
		System.out.println("Negamax Depth = " + depth);
		Long time = System.nanoTime();
		
		counter = 0;
		long best = Long.MIN_VALUE;
		Maple[] children =root_leaf.getChildren();
		if (children == null){
			root_leaf.setChildren(original);
			children = root_leaf.getChildren();
		}		
		for (Maple child: children) {
			long current = -NegaMax(child, original.makeMove(child.getPriorMove()), depth - 1, Integer.MIN_VALUE,
					Integer.MAX_VALUE, 1); 
			if (current > best) {
				best_child = child;
				best = current;
			}
		}
		offsprings_of_best_child = best_child.getChildren();
		System.out.println("Time Elapsed = " + (System.nanoTime()- time)/1000000);
		System.out.println("Number of Positions Evaluated = " + counter);
		System.out.println("NegaMax Done");
	}
	private static long NegaMax(Maple child, Position p, int depth, long alpha, long beta, int color) {
		counter ++;
		if (p.getResult() != Position.NO_RESULT|| depth == 0) {
			Lorenz z = new Lorenz(p);
			long n = z.get(Lorenz.WHITE_ABSOLUTE_MATERIAL) - z.get(Lorenz.BLACK_ABSOLUTE_MATERIAL);
			return color * n;
		} 
		else {
			Maple[] offsprings = child.getChildren();
			if (offsprings == null){
				child.setChildren(p);
				offsprings = child.getChildren();
			}
				for (Maple offspring: offsprings){
				alpha = Math.max(alpha,
						-NegaMax(offspring, p.makeMove(offspring.getPriorMove()), depth - 1, -beta, -alpha, -color));
				if (alpha > beta)
					break;
			}
			return alpha;
		}
	}
	public Move getBestMove(){
		return best_child.getPriorMove();
	}
}