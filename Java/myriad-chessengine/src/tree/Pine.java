package tree;

import debug.FenUtility;
import rules.*;

public class Pine {
	private Position original;
	public static int counter = 0;
	public Move best_m;

	public Pine(Position p) {
		original = p;
	}

	public Pine() {
		original = new Position();
	}

	public void NegaMax(int depth) {
		System.out.println(depth);
		int best = Integer.MIN_VALUE;
		Move [] all_m = original.generateAllMoves();
		
		for (Move m: all_m) {
			System.out.println(m);
			int current = -NegaMax(original.makeMove(m), depth - 1, Integer.MIN_VALUE,
					Integer.MAX_VALUE, 1); 
			if (current > best) {
				best_m = m;
				best = current;
			}
		}
	}
	private int NegaMax(Position p, int depth, int alpha, int beta, int color) {
		counter ++;
		if (p.isEndGame()|| depth == 0) {
			int n = p.getEval();
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