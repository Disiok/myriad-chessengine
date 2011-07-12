package debug;

import java.io.*;
import java.util.*;
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
	 * @return A string describing the test results.
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
		Scanner sc = new Scanner (System.in);
		System.out.println("Would you like to enter a comment? Enter a blank line to finish.");
		String read = "-", output = "";
		do {
			read = sc.nextLine();
			output += read;
		} while (!read.equals(""));
		try {
			String s;
			while ((s=rd.readLine())!=null){
				if (s.charAt(0)!='*'){
					Position p = FenUtility.loadFEN(s);
					output += test(p);
				}
			}
			BufferedWriter wr = new BufferedWriter (new FileWriter(outputFile));
			wr.write(output);
			wr.close();
			rd.close();
		} catch (IOException ios){
			System.out.println("IOException caught!");
		}
	}
}
