package tables;

import rules.*;

/**
 * The transposition table for Myriad. Organizes the information into a bitstring, which is stores
 * important data. The table will prefer deeper entries to shallower ones. E.g. if depth 6 and 4
 * there are 3! ways to reach the depth 6 position, but only 2! to reach the depth 4 position.
 * @author Andy Huang
 */
public class Round {
	// ----------------------Table Entry Fields----------------------
	private long[] hashes;				// true hash value
	private byte[] depth;				// depth of hash position
	private long[] bitstring_descript;	// bitstring descriptions
	// ----------------------Constants----------------------
	private final int MASK_INDEX;
	public final int size;
	public static final long SCORE_RSH = 23;
	public static final long EXACT_RSH = 22;
	public static final long BOUND_RSH = 21;
	public static final long STARTSQ_RSH = 13;
	public static final long ENDSQ_RSH = 5;
	public static final long MODIFIER_RSH = 1;
	public static final int MASK_BIT = 1;
	public static final int MASK_4BIT = 0xf;
	public static final int MASK_BYTE = 0xff;
	// ----------------------End of Constants----------------------
	// ----------------------Constructor----------------------
	/**
	 * Constructs a Round (transposition table) object with a 2^bits array indexes.
	 * @param bits The number of bits available for address indexing.
	 */
	public Round(int bits){
		size = (int)(Math.pow(2, bits));		
		hashes = new long[size];
		depth = new byte[size];
		bitstring_descript = new long[size];
		int temp = 0;
		for(int i = 0; i < bits; i++){
			temp |= 1 << i;
		}
		MASK_INDEX = temp;
	}
	// ----------------------End of Constructor----------------------
	// ----------------------Method----------------------
	/**
	 * Returns the maximum capacity of this Round object.
	 * @return the maximum capacity of this Round object
	 */
	public int getSize(){
		return size;
	}
	/**
	 * Adds an element to the hash table. 
	 * If two elements have the same hash, the old on will ALWAYS be overwritten
	 * @param hash The true Zobrist hash of the position.
	 * @param score The score (evaluated or bound) of the position.
	 * @param level The depth of the position.
	 * @param exactValue Whether or not the score is a bound.
	 * @param bound An alpha or beta bound? True if alpha, false if beta.
	 * @param move The refutation move if the score is a bound.
	 * @return Whether or not the entry was stored into the hash table.
	 */
	public boolean set(long hash, long score, byte level, boolean exactValue, boolean bound, 
					   Move move, boolean whiteMove){
		//System.out.println("Set called with: " + score + "," + hash);
		int index = (int)(hash & (MASK_INDEX));
		//if (hashes[index] == 0 || depth[index] < level){
			hashes[index] = hash;
			depth[index] = level;
			long string = 0;
			// construct bitstring, see constants for rsh and mask values.
			string = score; 
			string = (string << 1) + (exactValue ? 1 : 0);
			string = (string << 1) + (bound ? 1 : 0);
			if (move != null){
				string = (string << 8) + move.getStartSquare();
				string = (string << 8) + move.getEndSquare();
				string = (string << 4) + move.getModifier();
			} else string <<= 20;
			string = (string << 1) + (whiteMove ? 1 : 0);
			bitstring_descript[index] = string;
			return true;
		/*}
		return false;*/
	}
	/**
	 * Gets a hash from the hash table. 
	 * @param hash The true Zobrist hash.
	 * @return The descriptor bitstring. -1 if the hash was not found in the table.
	 */
	public long get(long hash){
		int index = (int)(hash & (MASK_INDEX));
		long string = -1;
		if(hashes[index] == hash) string = bitstring_descript [index];
		if (string != -1){
			//System.out.println("Get called, returned: " + (string >> SCORE_RSH) + ", Hash = " + hash);
		}
		return string;
	}
}