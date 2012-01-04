package tree;

public class Round {
	private long[] hashes;
	private byte[] depth;
	private boolean[] pv;
	private boolean[] bounds;
	private byte[] pointer;
	private int size = 0;
	
	public Round(int bytes){
		size = (int)(Math.pow(2, bytes));		
		hashes = new long[size];
		depth = new byte[size];
		pv = new boolean[size];
		bounds = new boolean[size];
		pointer = new byte[size];
	}
	
	public void set(long hash, byte depth, boolean exactValue, boolean bound, byte special){
		
	}
	
	public int get(long hash){
		int index = (int)(hash & (2^size -1));
		int string = -1;
		if(hashes[index] != hash){
			do{
				index += pointer[index];
			} while(hashes[index] != hash);
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
	
	public boolean compare(){
		return true;
	}
}
