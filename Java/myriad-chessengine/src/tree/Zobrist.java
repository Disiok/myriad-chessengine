package tree;

import java.util.*;
import rules.*;

public class Zobrist {
	private static int[] multiplier = {1, 2, 4, 6, 8, 10};
	private static long hash = 0x00000000;
	// Index 0-63 = white pawn, 64 - 127 black pawn, 128 - 191 white bishop, 192 - 255 black bishop
    // 256 - 319 white knight, 320 - 383 black knight, 384 - 447 white rook, 448 - 511 black rook
    // 512 - 575 white queen, 576 - 639 black queen, 640 - 703 white king, 704 - 767 black king
    // 768 - 831 en passent squares, 832 - 835 castling rights(white kingside, white queenside, black kingside, black queenside)
    // 836 fifty move counter
	private static long[] array = new long[837];
	// Untested
	public static long zorbrist(Position pos, Move move){
		byte out = move.getStartSquare(), in = move.getStartSquare();
		long remove = 0x0;
	    Piece c_piece = pos.getSquareOccupier(out);
	    Piece occupier = pos.getSquareOccupier(in);
	    if(occupier != Piece.getNullPiece()){
	    	byte toRemove = getIndex(occupier.getPosition(), occupier.getType(), occupier.getColour());
	    	remove = array[toRemove];
	    }
	    byte c_pos = getIndex(out, c_piece.getType(), c_piece.getColour());
	    byte e_pos = getIndex(in, c_piece.getType(), c_piece.getColour());
	    return hash^array[c_pos]^remove^array[e_pos];
	}
	// Helper methods
	// Generates all 837 bitstrings required for the Hash system
	public static long[] bitstringGenerator(){
		Random rdm = new Random(1995);
	    long [] strings = new long[836];
	    for(int i = 0; i < strings.length; i++){
	    	long data = Math.abs(0x0 + rdm.nextLong());
	    	boolean unique = false;
	    	while(!unique){
	    		unique = true;
	    		for(int k = 0; k < i; k++){
	    			if(data == strings[k]){
	    				data = rdm.nextLong();
	    				unique = false;
	    			}
	    		}
	    	}
	    	strings[i] = data;
	    }
	    return strings;
	}
	// 
	public static byte getIndex(byte pos, byte id, byte color){
		int modifier = color == 0 ? 0 : 64;
	    byte index = 0;
	    byte type = id;
	    switch(type){
	    	case 0: {
	    		index = (byte)(modifier + multiplier[type]*(pos - ((pos >> 4) * 0x8)));
	    		break;
	    	}
	    	case 1: {
	    		index = (byte)(modifier + multiplier[type]*(pos - ((pos >> 4) * 0x8)));
	    		break;
	    	}
	    	case 2: {
	    		index = (byte)(modifier + multiplier[type]*(pos - ((pos >> 4) * 0x8)));
	    		break;
	    	}
	    	case 3: {
	    		index = (byte)(modifier + multiplier[type]*(pos - ((pos >> 4) * 0x8)));
	    		break;
	    	}
	    	case 4: {
	    		index = (byte)(modifier + multiplier[type]*(pos - ((pos >> 4) * 0x8)));
	    		break;
	    	}
	    	case 5: {
	    		index = (byte)(modifier + multiplier[type]*(pos - ((pos >> 4) * 0x8)));
	    		break;
	    	}
	    }
	    return index;
	}
}
