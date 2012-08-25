package tree;

import eval.*;
import rules.*;
import tables.Round;

public class Pine {
	public static final Round table = new Round(16);

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
	public Move getBestMove(){
		return best_child.getPriorMove();
	}
	private long eval(Position p, int result) {
		//Assume the position does end in someone winning, 
		//or that this is the last level of search
		//d is the result of the game
		counter++; // counter should be added here
		Lorenz z = p.lz; //new Lorenz(p);
		long score = 0;
		long mat = (z.get(Lorenz.WHITE_ABSOLUTE_MATERIAL) & Crescent.MATERIAL_MASK)
				- (z.get(Lorenz.BLACK_ABSOLUTE_MATERIAL) & Crescent.MATERIAL_MASK);
		long w_sent = z.get(Lorenz.WHITE_SENTINELS), b_sent = z.get(Lorenz.BLACK_SENTINELS);
		int n_sq_w = 0, n_sq_b = 0;
		for (int i = 0; i < 64; i ++){
			if ((w_sent & 1) == 1) n_sq_w++;
			else if ((b_sent & 1) == 1) n_sq_b++;
			w_sent >>=1;
		b_sent >>=1;
		}
		return (mat + n_sq_w - n_sq_b + score);		
	}
	/**
	 * Starts NegaScout. When finished, we will know
	 * the best move to  * @param original The current position of the board
	 * @param prior_move The last moved played (by the opponent)
	 * @param depth Search depth down the tree
	 * @param color Some sign flipping thingy that no one understands
	 */	
	public void beginPVS (Position original, Move prior_move, int depth, int color) {
		System.out.println("PVS Start");
		System.out.println("PVS Depth = " + depth);
		Long time = System.nanoTime();

		counter = 0;
		long best = Long.MIN_VALUE;
		Maple[] children;
		
		if ((children=root_leaf.getChildren()) == null){
			root_leaf.setChildren(original);
			children = root_leaf.getChildren();
		}		
		for (Maple child: children) {
			long current = -PVS(child, original.makeMove(child.getPriorMove()), 
					depth - 1, Long.MIN_VALUE, Long.MAX_VALUE, -color);
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
	/**
	 * Starts NegaScout. When finished, we will know
	 * the best move to make
	 * @param original The current position of the board
	 * @param prior_move The last moved played (by the opponent)
	 * @param depth Search depth down the tree
	 */
	public void beginPVS(Position original,Move prior_move, int depth) {
		beginPVS(original, prior_move, depth, -1);
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
	 * @param color 1 is black CP's move, -1 is white CP's move?
	 * @return The worth of position p
	 */
	private long PVS(Maple child, Position p, int depth, long alpha, long beta, int color) {
		int outcome = p.getResult();
		//Nothing more to search if this is a terminal node,
		//we've reached infimum (lowest) depth,
		//or the game has ended (in a victory or a draw)
		//Well, if the game has ended, it must be terminal...
		if (depth == 0){
			long get = table.get(p.getHash());
			if (get != -1) return (get >> Round.SCORE_RSH)*color;
			long score = eval (p, outcome);
			table.set(p.getHash(), score, p.getHalfMoves(), false, false, child.getPriorMove(), p.isWhiteToMove());
			return score * color;
		} else if (outcome != 0) {
			if (outcome == Position.WHITE_WINS) return (Long.MAX_VALUE-2)*color;
			else if (outcome == Position.BLACK_WINS) return (Long.MIN_VALUE+2)*color;
			else if (outcome == Position.DRAW) return 0; 
		} else if (depth == 1){
			long futprune = eval(p, outcome);
			long alpha_limit = alpha - 325, beta_limit = beta + 325;
			if (futprune > alpha_limit || futprune < beta_limit) return futprune;
		} 
		Maple[] children;
		if ((children = child.getChildren()) == null) {
			child.setChildren(p);
			children = child.getChildren();
		}
		long b = beta;
		for (Maple n : children) {
			//p is the initial position. We get new positions by applying
			//the moves in the Maple leaves
			Position n_pos = p.makeMove(n.getPriorMove());
			long score = (table.get(n_pos.getHash()) >> Round.SCORE_RSH);
			if(score == -1) score =	-PVS(n, n_pos, depth - 1, -b, -alpha,-color);
			if ((alpha < score) && (score < beta) && (n != children[0])) 
				score =- PVS(n, n_pos, depth -1, -beta, -alpha, -color);
			if (score > alpha) b = (alpha = score ) + 1;
			if (alpha >= beta) {
				table.set(p.getHash(), score, p.getHalfMoves(), true, true, child.getPriorMove(), p.isWhiteToMove());
				return alpha;
			}
		}
		return alpha;
	}
}