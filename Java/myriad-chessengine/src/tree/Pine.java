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
	private long NegaMax(Maple child, Position p, int depth, long alpha, long beta, int color) {
		counter ++;
		int d = p.getResult();
		if (d != Position.NO_RESULT|| depth == 0) {
			return eval(p, color, d);
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
	private long eval(Position p, int color, int d) {
		//Assume the position does end in someone winning, 
		//or that this is the last level of search
		//d is the result of the game
		
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
	
	/**
	 * Starts NegaScout. When finished, we will know
	 * the best move to make
	 * @param original The current position of the board
	 * @param prior_move The last moved played (by the opponent)
	 * @param depth Search depth down the tree
	 */
	public void beginPVS(Position original,Move prior_move, int depth) {
		System.out.println("PVS Start");
		System.out.println("PVS Depth = " + depth);
		Long time = System.nanoTime();
		
		counter = 0;
		long best = Long.MIN_VALUE;
		Maple[] children =root_leaf.getChildren();
		if (children == null){
			root_leaf.setChildren(original);
			children = root_leaf.getChildren();
		}		
		for (Maple child: children) {
			//It has to be negative because it's negascout??
			long current = -PVS(child, original.makeMove(child.getPriorMove()), 
					depth - 1, Long.MIN_VALUE, Long.MAX_VALUE); 
			if (current > best) {
				best_child = child;
				best = current;
			}
		}
		offsprings_of_best_child = best_child.getChildren();
		System.out.println("Time Elapsed = " + (System.nanoTime()- time)/1000000);
		System.out.println("Number of Positions Evaluated = " + counter);
		System.out.println("PVS Done");
	}	
	
	
	//Converted code from Wikipedia that I don't understand
	//Wikipedia says NegaScout = PVS. 
	//Well, look at those negative signs...
	//Well, PVS is shorter than Negascout.
	//Omitting int color parameter since it seems the default is that it be 1...
	/**
	 * The recurrent method in our Negascout implementation
	 * @param child The move which was made
	 * @param p The position we evaluate, having just made child's move
	 * @param depth Depth  down tree to search. Stops if depth is 0 
	 * @param alpha Alpha-value
	 * @param beta Beta-value
	 * @return The worth of position p
	 */
	private long PVS(Maple child, Position p, int depth, long alpha, long beta) {
		++counter;
		Maple[] children = child.getChildren();
		int outcome = p.getResult();
		//Nothing more to search if this is a terminal node,
		//we've reached infimum (lowest) depth,
		//or the game has ended (in a victory or a draw)
		if (children == null
				|| depth == 0 || outcome !=Position.NO_RESULT ) {
			return eval(p,1,outcome);
		}
		long b = beta;
		for (Maple n : children) {
			//p is the initial position. We get new positions by applying
			//the moves in the Maple leaves
			long score = -PVS(n, p.makeMove(n.getPriorMove()), 
					depth - 1, -b, -alpha);
			if (alpha < score && score < beta
					&& children[0] != n) {
				score = -PVS(n, p.makeMove(n.getPriorMove()),
						depth -1, -beta, -alpha);
			}
			alpha = Math.max(alpha, score);
			if (alpha >= beta) {
				return alpha;
			}
			beta = alpha + 1;
		}
		return alpha;
	}
}