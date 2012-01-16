package debug;

import rules.*;
import java.io.*;
import eval.Lorenz;

public class PositionTimeTest extends Debug {
	@Override
	public String test(Position p) {
		Utility.displayBoard(Utility.saveFEN(p));
		Lorenz lz = new Lorenz (p);
		lz.get(Lorenz.WHITE_ABSOLUTE_MATERIAL);
		lz.get(Lorenz.WHITE_COLUMN_A);
		lz.get(Lorenz.DYNAMICS);
		lz.get(Lorenz.KING_SAFETY);
		lz.get(Lorenz.WHITE_DOUBLED_PAWNS);
		lz.get(Lorenz.PAWN_ISLANDS);
		lz.get(Lorenz.OPEN_FILES);
		lz.get(Lorenz.WHITE_SENTINELS);
		lz.get(Lorenz.WHITE_ISOLANIS);
		long lg = System.nanoTime();
		lz.passedPawns();
		long sg = System.nanoTime();
		System.out.println(lz.get(Lorenz.KING_SAFETY));
		return "" + ((sg - lg) / 1000);
	}
	public static void main(String[] argv) throws IOException {
		PositionTimeTest tst = new PositionTimeTest();
		tst.startTest("FEN.txt", "Out.txt");
		BufferedReader rd = new BufferedReader(new FileReader("Out.txt"));
		String s;
		int sum_sq = 0, sum = 0, count = 0;
		while ((s = rd.readLine()) != null) {
			int val = Integer.parseInt(s);
			if (val < 10000){
				sum += val;
				sum_sq += (val * val);
				count++;
			}
		}
		double avg = sum / (double) count;
		System.out.println("Average of " + (count - 1) + " = " + avg);
		System.out.println("Standard Deviation = " + Math.sqrt(sum_sq / count - avg * avg));
	}
}