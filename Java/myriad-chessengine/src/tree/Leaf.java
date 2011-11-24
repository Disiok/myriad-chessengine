package tree;

import rules.*;
import eval.*;

public class Leaf {
	private Position p;
	private Move[] p_moves;
	private int n_moves;
	private Leaf[] children;
	private Move prior;

	public Leaf(Position p) {
		this.p = p;
		children = null;
		prior = null;
	}

	public Leaf(Position p, Move prior) {
		this.p = p;
		children = null;
		this.prior = prior;
	}

	public void setChildren() {
		p_moves = p.generateAllMoves();
		n_moves = p_moves.length;
		children = new Leaf[n_moves];
		for (int i = 0; i < n_moves; i++) {
			children[i] = new Leaf(p.makeMove(p_moves[i]), p_moves[i]);
		}
	}

	public Leaf[] getChildren() {
		return children;
	}

	public int getEval() {
		// TODO
		return (int) (Math.random() * 100);
	}

	public Position getPosition() {
		return p;
	}

	public Move getPriorMove() {
		return prior;
	}

	public boolean isLastLeaf() {
		// TODO
		return false;
	}
}