package debug;
import rules.*;
import java.util.*;
import java.io.*;
public class PositionTimeTest extends Debug {

	@Override
	public String test(Position p) {
		long lg = System.nanoTime();
		p.generateAllMoves();
		long sg = System.nanoTime();
		return ""+((sg-lg)/1000);
	}

	public static void main (String [] argv) throws IOException{
	//	PositionTimeTest tst = new PositionTimeTest();
		//tst.startTest("FEN.txt", "Out.txt");
		BufferedReader rd = new BufferedReader (new FileReader("out.txt"));
		String s;
		int sum_sq = 0, sum = 0, count = 0;
		while ((s = rd.readLine()) != null){
			int val = Integer.parseInt(s);
			sum += val;
			sum_sq += val*val;
			count++;
		}
		System.out.println(sum_sq);
		double avg = sum/  (double) count;
		System.out.println(count*avg*avg);
		System.out.println(Math.sqrt((sum_sq-count*avg*avg)/count));
	}
}
