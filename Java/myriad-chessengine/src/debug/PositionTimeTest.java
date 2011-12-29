package debug;

import rules.*;
import java.io.*;

public class PositionTimeTest extends Debug {
	@Override
	public String test(Position p) {
		Utility.displayBoard(Utility.saveFEN(p));
		long lg = System.nanoTime();
		p.getResult();
		long sg = System.nanoTime();
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
	}
}