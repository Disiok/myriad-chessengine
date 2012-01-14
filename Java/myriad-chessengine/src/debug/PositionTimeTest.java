package debug;

import rules.*;
import java.io.*;
import eval.*;

public class PositionTimeTest extends Debug {

	public static long nodes = 0;
	
	@Override
	public String test(Position p) {
		Utility.displayBoard(Utility.saveFEN(p));
		Lorenz lz = new Lorenz (p);
		lz.get(Lorenz.WHITE_ABSOLUTE_MATERIAL);
		lz.get(Lorenz.BUFFER1);
		long lg = System.nanoTime();
		lz.get(Lorenz.OPEN_FILES);
		long sg = System.nanoTime();
		System.out.println(lz.features[Lorenz.OPEN_FILES]);
		return "" + ((sg - lg) / 1000);
	}
	public static void main(String[] argv) throws IOException {
		PositionTimeTest tst = new PositionTimeTest();
		tst.startTest("FEN.txt", "Out.txt");
		BufferedReader rd = new BufferedReader(new FileReader("Out.txt"));
		String s;
		int sum_sq = 0, sum = 0, count = 0;
		rd.readLine();
		while ((s = rd.readLine()) != null) {
			int val = Integer.parseInt(s);
			sum += val;
			sum_sq += (val * val);
			count++;
		}
		double avg = sum / (double) count;
		System.out.println("Average of " + (count - 1) + " = " + avg);
		System.out.println("Standard Deviation = " + Math.sqrt(sum_sq / count - avg * avg));
		/*Position k = Utility.loadFEN("rnbqkbnr/ppp1pppp/8/3p4/3P4/8/PPP1PPPP/RNBQKBNR w KQkq d6 0 2");
		long time = System.currentTimeMillis();
		perft(3, k);
		System.out.println(System.currentTimeMillis() - time);
		System.out.println(nodes);*/
	}
	
	public static void perft (int depth, Position r){
		Move [] lst;
		if (depth == 0){
			nodes++;
			return;
		}
		lst = r.generateAllMoves();
		if (lst.length == 0) return;
		for (Move d: lst){
			perft(depth -1, r.makeMove(d));
		}
	}
}