package tree;
import rules.*;
import eval.*;

public class Leaf {
	private int eval;
	private Position p;
	private ESFramework f;
	private Move[] p_moves;
	private int n_moves;
	private Leaf[] children;
	
	
	public Leaf(Position p) {
		this.p = p;
		children = null;
	}
	public void setChildren() {
		p_moves = p.generateAllMoves();
		n_moves = p_moves.length;
		children = new Leaf[n_moves];
		for (int i = 0 ; i <n_moves; i++){
			children[i] = new Leaf (p.makeMove(p_moves[i]));
		}
	}
	public Leaf[] getChildren(){
		return children;
	}
	public int getEval(){
		f = new ESFramework (p);
		eval = f.getEvaluation();
		return eval;
	}
	public Position getPosition(){
		return p;
	}
	public boolean isLastLeaf(){
		// TODO check whether is last leaf
		boolean isLastLeaf = false;
		return isLastLeaf;
	}
}