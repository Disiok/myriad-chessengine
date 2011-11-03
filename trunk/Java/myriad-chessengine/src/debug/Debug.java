package debug;

import java.io.*;
import rules.Position;

/**
 * This is the debug utility for Myriad. It is a slightly modified version of the robot judge.
 * The input file has reads FEN strings line by line. Start a line with * to have the string
 * treated as a comment.
 * @author Jesse Wang
 */
public abstract class Debug {
	/**
	 * This method tests whatever you want to test.
	 * @param p The Position object to test.
	 * @return A string describing the test results. Test
	 */
	public abstract String test (Position p);
	/**
	 * This is a testing utility. It allows you to specify a general comment along with the test
	 * cases describing the test. This outputs a file in the debug folder with a name specified
	 * by the user.
	 * @param testFileName The input test cases.
	 * @param outputFile The output file.
	 */
	public void startTest(String testFileName, String outputFile){
		InputStream is = getClass().getResourceAsStream(testFileName);
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		try {
			String s;
			BufferedWriter wr = new BufferedWriter (new FileWriter(outputFile));
			while ((s=rd.readLine())!=null){
				if (s.charAt(0)!='*'){
					Position p = FenUtility.loadFEN(s);
					wr.write(test(p));
					wr.newLine();
				}
			}
			wr.close();
			rd.close();
		} catch (IOException ios){
			System.out.println("IOException caught!");
		}
	}
}
