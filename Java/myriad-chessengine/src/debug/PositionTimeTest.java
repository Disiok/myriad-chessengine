package debug;

import rules.*;
import java.io.*;

public class PositionTimeTest extends Debug {
	@Override
	public String test(Position p) {
		//FenUtility.displayBoard(FenUtility.saveFEN(p));
		long lg = System.nanoTime();
		//Lorenz lz = new Lorenz(p);
		/*lz.material();
		lz.bishopvknight();
		lz.twobishops();
		lz.oppositebishops();
		lz.pawnformation();
		lz.pawnislands();
		lz.doublepawns();
		lz.kingtropism();
		lz.kingshield();
		lz.sentinelsquares();*/
		long sg = System.nanoTime();
		//System.out.println(lz.features[Lorenz.WHITE_SENTINELS]);
		//System.out.println(lz.features[Lorenz.BLACK_SENTINELS]);
		return "" + ((sg - lg) / 1000);
	}

	public static void main(String[] argv) throws IOException {
		PositionTimeTest tst = new PositionTimeTest();
		tst.startTest("FEN.txt", "Out.txt");
		BufferedReader rd = new BufferedReader(new FileReader("out.txt"));
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
	}
}
