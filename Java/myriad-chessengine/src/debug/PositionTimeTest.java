package debug;
import rules.*;
public class PositionTimeTest extends Debug {

	@Override
	public String test(Position p) {
		long lg = System.nanoTime();
		p.generateAllMoves();
		long sg = System.nanoTime();
		return ""+((sg-lg)/1000);
	}

	public static void main (String [] argv){
		PositionTimeTest tst = new PositionTimeTest();
		tst.startTest("FEN.txt", "Out.txt");
	}
}
