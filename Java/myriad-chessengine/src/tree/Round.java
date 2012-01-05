package tree;

public class Round {
	private long[] hashes;
	private byte[] depth;
	private boolean[] pv;
	private boolean[] bounds;
	private byte[] pointer;
	private int size = 0;
	private final int MASK;
	
	public Round(int bytes){
		size = (int)(Math.pow(2, bytes));		
		hashes = new long[size];
		depth = new byte[size];
		pv = new boolean[size];
		bounds = new boolean[size];
		pointer = new byte[size];
		MASK = (int)(Math.pow(2, size)) - 1;
	}
	
	public void set(long hash, byte level, boolean exactValue, boolean bound, byte special){
		int index = (int)(hash & (MASK));
		if(hashes[index] == 0){
			hashes[index] = hash;
			depth[index] = level;
			pv[index] = exactValue;
			bounds[index] = bound;	
		}
		else{
			if(pointer[index] != 0){
				index = index + pointer[index];
				hashes[index] = hash;
				depth[index] = level;
				pv[index] = exactValue;
				bounds[index] = bound;
			}
			else{
				boolean locFound = false;
				for(int i = 0; i < hashes.length && !locFound; i++){
					if(compare(hash, hashes[i])){
						index = i;
						hashes[index] = hash;
						depth[index] = level;
						pv[index] = exactValue;
						bounds[index] = bound;
						locFound = true;
					}
				}
			}
		}
	}
	// Returns a bitstring representing the hash.
	// Returns -1 if the hash is not found
	// 
	public int get(long hash){
		int index = (int)(hash & (MASK));
		int string = -1;
		if(hashes[index] == 0){
			return string;
		}
		else if(hashes[index] != hash){
			while(hashes[index] != hash && pointer[index] != 0){
				index += pointer[index];
			}
		}
		string = depth[index];
		string = (string << 4) + depth[index];
		if(pv[index]) string = (string << 1) + 1;
		else string = (string << 1);
		if(bounds[index]) string = (string << 1) + 1;
		else string = (string << 1);
		string = (string << 4) + pointer[index];
		
		return string;
	}
	
	public boolean compare(long hashA, long hashB){
		return true;
	}
}
