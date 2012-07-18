package debug;

import rules.*;
import tables.*;

/**
 * This class specifically tests the Round class (at the moment) and all of its functionalities
 * Also features how to retrieve data from the bitstrings stored by the Round class
 * @author Guest Andy
 */

public class Tester {
	public static void main(String[] args){
		testRound();
	}
	
	public static void testRound(){
		long hash = 123456789;
		
		Round table = new Round(3);
		Move tm_1 = new Move((byte) 0x10, (byte) 0x22, (byte) 3);
		table.set(123453701, 999999, (byte) 4, true, true, tm_1, false);	
		table.set(hash, -9999999, (byte) 4, true, true, tm_1, false);
		
		
		long data = table.get(123456789);
		
		System.out.println("Size of table: " + table.getSize());
		System.out.println("Hash: " + hash + " Bitstring: " + data);
		
		System.out.println("\nBitstring Data");
		// Retrieve score
		System.out.println("Score: " + (data >> Round.SCORE_RSH));
		// Retrieve exact value
		System.out.println("Exact Value: " + ((data >> Round.EXACT_RSH) & Round.MASK_BIT));
		// Retrieve bound
		System.out.println("Bound: " + ((data >> Round.BOUND_RSH) & Round.MASK_BIT));
		// Retrieve start sq
		System.out.println("Start Square: " + ((data >> Round.STARTSQ_RSH) & Round.MASK_BYTE));
		// Retrieve end sq
		System.out.println("End Square: " + ((data >> Round.ENDSQ_RSH) & Round.MASK_BYTE));
		// Retrieve modifier
		System.out.println("Modifier: " + ((data >> Round.MODIFIER_RSH) & Round.MASK_4BIT));
		// Retrieve white to move
		System.out.println("White to Move: " + (data & Round.MASK_BIT));
	}
}
