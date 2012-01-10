package debug;
import rules.*;
import java.util.*;

public class Performance {
	public static int check;
	public static int check_mate;
	public static int promotion;
	public static int castle;
	public static int ep;
	public static void main(String args[]){
		Scanner sc= new Scanner(System.in);
		int depth = 6;
		String FEN = sc.nextLine();
		Position p = Utility.loadFEN(FEN);
		System.out.println("Depth\t\tNodes\t\tE.p.\t\tCastles\t\tPromo\t\tChecks\t\tMates\t\tTime");
		for (int i = 1; i < depth; i++){
			check = 0;
			check_mate = 0;
			promotion = 0;
			castle = 0;
			ep = 0;
			long time = System.nanoTime();
			System.out.println(i+"\t\t"+ Perft(i,p)+"\t\t"+ep+"\t\t"+castle+"\t\t"+promotion+"\t\t"+check+"\t\t"+check_mate+"\t\t"+(System.nanoTime()- time)/1000000);
		}
	}
	public static int Perft (int depth, Position p){
		int nodes = 0;
		if (depth == 0){
			byte mod = p.prior_move.getModifier();
			if (p.isInCheck(false)){
				check ++;
			}
			if (p.getResult()==Position.BLACK_WINS||p.getResult()==Position.WHITE_WINS){
				check_mate++;
			}
			else if (mod==6||mod==7||mod==8||mod==9)
				promotion ++;
			else if (mod==1||mod==2||mod==3||mod==4)
				castle ++;
			else if (mod==5)		
				ep++;
			return 1;
		}
		Move[] move_list = p.generateAllMoves();
		for (Move m: move_list){
			nodes += Perft(depth-1, p.makeMove(m));
		}
		return nodes;
		
	}
}
