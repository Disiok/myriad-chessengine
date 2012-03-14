package tree;

import eval.*;
import rules.*;
import tables.Round;

public class Pine {
	static final Round table = new Round(16);
	
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
		int d = p.getResult();
		if (d != Position.NO_RESULT|| depth == 0) {
			if (d == Position.WHITE_WINS) return (Long.MAX_VALUE-2)*color;
			else if (d == Position.BLACK_WINS) return (Long.MIN_VALUE+2)*color;
			else if (d == Position.DRAW) return 0; 
			Lorenz z = new Lorenz(p);
			long score = 0;
			long mat = (z.get(Lorenz.WHITE_ABSOLUTE_MATERIAL) & Crescent.MATERIAL_MASK)
					- (z.get(Lorenz.BLACK_ABSOLUTE_MATERIAL) & Crescent.MATERIAL_MASK);
			long dyn = z.get(Lorenz.DYNAMICS);
			long two_bishops = dyn & 7;
			if (two_bishops == 5) score -= 20;
			else if (two_bishops == 3) score += 20;
			long w_sent = z.get(Lorenz.WHITE_SENTINELS), b_sent = z.get(Lorenz.BLACK_SENTINELS);
			int n_sq_w = 0, n_sq_b = 0;
			for (int i = 0; i < 64; i ++){
				if ((w_sent & 1) == 1) n_sq_w++;
				else if ((b_sent & 1) == 1) n_sq_b++;
				w_sent >>=1;
				b_sent >>=1;
			}
			return color * (mat + n_sq_w - n_sq_b + score);
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