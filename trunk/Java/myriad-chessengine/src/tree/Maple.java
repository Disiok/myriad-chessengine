package tree;
import rules.*;

public class Maple {
	private Move m;
	private Maple parent;
	private Maple[] children;
	public Maple (Maple parent, Move m, Position p){
		this.parent = parent;
		this.m = m;
		Move[] next_m = p.generateAllMoves();
		children = new Maple[next_m.length];
		for (int i = 0 ; i < next_m.length; i ++){
			children[i] = new Maple(this,next_m[i]);
		}	
	}
	public Maple (Maple parent, Move m){
		this.parent = parent;
		this.m = m;
		children = null;
	}
	
	public void setChildren(Position p){
		Move[] next_m = p.generateAllMoves();
		children = new Maple[next_m.length];
		for (int i = 0 ; i < next_m.length; i ++){
			children[i] = new Maple(this,next_m[i]);
		}	
	}
	public Maple[] getChildren(){
		return children;
	}
	public Move getPriorMove(){
		return m;
	}
	
}
