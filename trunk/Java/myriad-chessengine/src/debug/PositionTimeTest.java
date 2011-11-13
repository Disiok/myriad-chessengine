package debug;

import rules.*;
import java.io.*;
import eval.*;
import eval.FeatureManager.Feature;

public class PositionTimeTest extends Debug {
	@Override
	public String test(Position p) {
		StructuralFeatures sft = new StructuralFeatures(new Feature (p, new FeatureManager(p)));
		String s = sft.detectPawnCover(p);
		FenUtility.displayBoard(FenUtility.saveFEN(p));
		System.out.println(s);
		long lg = System.nanoTime();
		sft.detectPawnCover(p);
		long sg = System.nanoTime();
		return ""+((sg-lg)/1000);
	}

	public static void main (String [] argv) throws IOException{
		PositionTimeTest tst = new PositionTimeTest();
		tst.startTest("FEN.txt", "Out.txt");
		BufferedReader rd = new BufferedReader (new FileReader("out.txt"));
		String s;
		int sum_sq = 0, sum = 0, count = 0;
		rd.readLine();
		while ((s = rd.readLine()) != null){
			int val = Integer.parseInt(s);
			sum += val;
			sum_sq += (val*val);
			count++;
		}
		double avg = sum/ (double) count;
		System.out.println("Average = " + avg);
		System.out.println("Standard Deviation = " + Math.sqrt(sum_sq/count-avg*avg));
	}
}
