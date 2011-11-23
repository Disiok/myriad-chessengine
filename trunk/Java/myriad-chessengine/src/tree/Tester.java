package tree;

import rules.*;
import debug.*;
public class Tester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Pine tree = new Pine();
		tree.NegaMax(4);
		FenUtility.displayBoard(FenUtility.saveFEN(tree.getBestPosition()));

	}

}
