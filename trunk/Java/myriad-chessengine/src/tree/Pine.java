package tree;

import eval.*;
import rules.*;

public class Pine {
	public static int counter;
	public static Move NegaMax(Position original, int depth) {
		
		System.out.println("Negamax Start");
		System.out.println("Negamax Depth = " + depth);
		
		counter = 0;
		long best = Integer.MIN_VALUE;
		Move [] all_m = original.generateAllMoves();
		Move best_m = null;
		
		for (Move m: all_m) {
			long current = -NegaMax(original.makeMove(m), depth - 1, Integer.MIN_VALUE,
					Integer.MAX_VALUE, 1); 
			if (current > best) {
				best_m = m;
				best = current;
			}
		}
		System.out.println("Number of Positions Evaluated = " + counter);
		System.out.println("NegaMax Done");
		System.out.println("-------------------");
		return best_m;
	}
	private static long NegaMax(Position p, int depth, long alpha, long beta, int color) {
		counter ++;
		if (p.getResult() != Position.NO_RESULT|| depth == 0) {
			Lorenz z = new Lorenz(p);
			long n = z.get(Lorenz.WHITE_ABSOLUTE_MATERIAL) - z.get(Lorenz.BLACK_ABSOLUTE_MATERIAL);
			return color * n;
		} 
		else {
			for (Move m: p.generateAllMoves()){
				alpha = Math.max(alpha,
						-NegaMax(p.makeMove(m), depth - 1, -beta, -alpha, -color));
				if (alpha > beta)
					break;
			}
			return alpha;
		}
	}
}