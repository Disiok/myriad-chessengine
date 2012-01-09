package debug;
import rules.*;
import java.util.*;

public class Performance {
	public static int check;
	public static void main(String args[]){
		Scanner sc= new Scanner(System.in);
		int depth = 6;
		String FEN = sc.nextLine();
		Position p = Utility.loadFEN(FEN);
		for (int i = 1; i < depth; i++){
			check = 0;
			long time = System.nanoTime();
			System.out.println("Depth: " + i);
			System.out.println("Nodes Evaluated: "+Perft(i, p));
			System.out.println("Checks: " +check);
			System.out.println("Milliseconds Elapsed: "+(System.nanoTime()- time)/1000000);
			System.out.println();
		}
	}
	public static int Perft (int depth, Position p){
		int nodes = 0;
		if (p.isInCheck(false)){
			check ++;
			System.out.println(check);
			Utility.displayBoard(Utility.saveFEN(p));
			
		}
		if (depth == 0){
			return 1;
		}
		Move[] move_list = p.generateAllMoves();
		for (Move m: move_list){
			nodes += Perft(depth-1, p.makeMove(m));
		}
		return nodes;
		
	}
}
