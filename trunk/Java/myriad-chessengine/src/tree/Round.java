package tree;

import rules.*;

@SuppressWarnings("unused")
public class Round {
	private long[] hashes;
	private byte[] depth;
	private boolean[] pv;
	private boolean[] bounds;
	private byte[] startSq;
	private byte[] endSq;
	private byte[] modifier;
	private short[] score;	
	private int[] pointer;
	private int size = 0;
	private final int MASK_INDEX;
	public final static byte MASK_DEPTH = 30;
	public final static byte MASK_VALUE = 25;
	public final static byte MASK_BOUNDS = 24;
	public final static byte MASK_START = 16;
	public final static byte MASK_END = 8;
	public final static byte MASK_MOD = 4;
	public final static byte REFUTE_BYTE = 4;
	public final static byte REFUTE_BOOLEAN = 1;
	public final static byte REFUTE_SQUARE = 8;
	
	public Round(int bytes){
		size = (int)(Math.pow(2, bytes));		
		hashes = new long[size];
		depth = new byte[size];
		pv = new boolean[size];
		bounds = new boolean[size];
		pointer = new int[size];
		startSq = new byte[size];
		endSq = new byte[size];
		modifier = new byte[size];
		score = new short[size];
		int temp = 0;
		for(int i = 0; i < bytes; i++){
			temp |= 1 << i;
		}
		MASK_INDEX = temp;
	}
	
	public int getSize(){
		return size;
	}
	
	public void set(long hash, byte level, boolean exactValue, boolean bound, Move move){
		int index = (int)(hash & (MASK_INDEX));
		if(hashes[index] == 0){
			hashes[index] = hash;
			depth[index] = level;
			pv[index] = exactValue;
			bounds[index] = bound;	
			startSq[index] = move.getStartSquare();
			endSq[index] = move.getEndSquare();
			modifier[index] = move.getModifier();
		}
		else{
			if(pointer[index] != 0){
				//System.out.println("ding");
				index = index + pointer[index];
				hashes[index] = hash;
				depth[index] = level;
				pv[index] = exactValue;
				bounds[index] = bound;	
				startSq[index] = move.getStartSquare();
				endSq[index] = move.getEndSquare();
				modifier[index] = move.getModifier();
			}
			else{
				boolean locFound = false;
				System.out.println("dong");
				for(int i = 0; i < hashes.length && !locFound; i++){
					if(compare(hash, hashes[i])){
						index = i;
						hashes[index] = hash;
						depth[index] = level;
						pv[index] = exactValue;
						bounds[index] = bound;	
						startSq[index] = move.getStartSquare();
						endSq[index] = move.getEndSquare();
						modifier[index] = move.getModifier();
						locFound = true;
					}
				}
			}
		}
		int lowerBound = index + Byte.MIN_VALUE < 0 ? 0 : index + Byte.MIN_VALUE;
		int upperBound = index + Byte.MAX_VALUE > size ? size : index + Byte.MAX_VALUE;
		boolean set = false;
		for(int i = lowerBound; i <= upperBound && !set; i++){
			if(hashes[i] == 0){
				pointer[index] = i;
				set = true;
			}
		}
		//System.out.println("Index: " + index);
	}
	// Returns a bitstring representing the hash.
	// Returns -1 if the hash is not found
	// 
	public long get(long hash){
		int index = (int)(hash & (MASK_INDEX));
		long string = 0;
		if(hashes[index] == 0){
			return string;
		}
		else if(hashes[index] != hash){
			while(hashes[index] != hash && pointer[index] != 0){
				index += pointer[index];
			}
		}
		if(hashes[index] == hash){
			/*string = depth[index];
			string = (string << 4) + depth[index];
			if(pv[index]) string = (string << 1) + 1;
			else string = (string << 1);
			if(bounds[index]) string = (string << 1) + 1;
			else string = (string << 1);*/
			string = (string << 8) + startSq[index];
			string = (string << 8) + endSq[index];
			string = (string << 4) + modifier[index];
			string = (string << 4) + pointer[index];
		}
		System.out.println("Pointer: " + pointer[index]);
		System.out.println("Index: " + index);
		return string;
	}
	
	public boolean compare(long hashA, long hashB){
		return true;
	}
}
