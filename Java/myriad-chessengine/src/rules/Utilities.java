package rules;

public class Utilities{
	public static byte twoDigitHexConverter (int someIntNumber){
		int tens = someIntNumber /10;
		int ones = someIntNumber %10;
		return (byte) (tens*16+ones);
	}
	public static int twoDigitIntConverter (byte someHexNumber){
		int tens = someHexNumber/16;
		int ones = someHexNumber%16;
		return 10*tens+ones;
	}
}