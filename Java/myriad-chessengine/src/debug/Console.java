package debug;
import rules.*;
import java.util.*;

public class Console {
	public static int check;
	public static int check_mate;
	public static int promotion;
	public static int castle;
	public static int ep;
	public static int capture;
	public static boolean terminate = false;
	public static void main(String args[]){
		Scanner sc= new Scanner(System.in);
		while (!terminate){
			String input = sc.nextLine();
			if (input.equals("perft"))	Perft(sc);
			else if (input.equals("divide"))	Divide(sc);
			else if (input.equals("terminate")) terminate = true;
		}
	}
	public static void Divide(Scanner sc){
		String FEN;
		int depth;
		System.out.print("FEN: ");
		FEN = sc.nextLine();
		System.out.print("Depth: ");
		depth = sc.nextInt();
		Position p = Utility.loadFEN(FEN); 
		System.out.println("Move\t\tNodes");
		Move [] moves = p.generateAllMoves();
		int t_n = 0;
		for (Move m: moves){
			Position next_p = p.makeMove(m);
			int node = Perft (depth -1, next_p);
			t_n += node;				
			System.out.println(m.toString(p) + "\t\t" + t_n + "\t\t" +Utility.saveFEN(next_p));
		}
		System.out.println("Moves: "+ moves.length);
		System.out.println("Nodes: " + t_n);
	}
	/*
	 * Calculate all valid moves from depth 1 to a given depth.
	 * This is used to test the speed of the move generator and to see if the move generator is correct
	 * 
	 */
	public static void Perft(Scanner sc){
		System.out.print("FEN: ");
		String FEN = sc.nextLine();
		System.out.print("Depth: ");
		int depth = sc.nextInt();
		Position p = Utility.loadFEN(FEN); 
		System.out.println("Depth\t\tNodes\t\tCapture\t\tE.p.\t\tCastles\t\tPromo\t\tChecks\t\tMates\t\tTime");
		for (int i = 1; i <= depth; i++){
			check = 0;
			check_mate = 0;
			promotion = 0;
			castle = 0;
			capture = 0;
			ep = 0;
			long time = System.nanoTime();
			System.out.println(i+"\t\t"+ Perft(i,p)+"\t\t"+capture+"\t\t"+ep+"\t\t"+castle+"\t\t"+promotion+"\t\t"+check+"\t\t"+check_mate+"\t\t"+(System.nanoTime()- time)/1000000);
		}
	}
	private static int Perft (int depth, Position p){
		int nodes = 0;
		if (depth == 0){
			byte mod = p.prior_move.getModifier();
			if (p.isInCheck(false)){
				check ++;
			}
			if (p.getResult()==Position.BLACK_WINS||p.getResult()==Position.WHITE_WINS){
				check_mate++;
			}
			else if (mod==11)
				capture ++;
			else if (mod==6||mod==7||mod==8||mod==9)
				promotion ++;
			else if (mod==1||mod==2||mod==3||mod==4)
				castle ++;
			else if (mod==5){		
				ep++;
				capture ++;
			}
			return 1;
		} 
		Move[] move_list = p.generateAllMoves();
		for (Move m: move_list){
			nodes += Perft(depth-1, p.makeMove(m));
		}
		return nodes;
		
	}
}
