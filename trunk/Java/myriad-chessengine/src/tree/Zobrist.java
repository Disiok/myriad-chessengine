package tree;

import java.util.*;
import rules.*;

/**
 * This class serves as a static utility class for creating hashes. The hashes are created using the 
 * Zobrist algorithm of hashing the location with xors (the exclusive or operator).
 * @author Andy Huang
 */
public class Zobrist {
	//----------------------Constants----------------------
	/**Stores the multipliers for each piece object to access the array.*/
	private static final int[] multiplier = {1, 2, 4, 6, 8, 10, 12};
	/**The basic starting hash (empty board, no pieces, no castling rights)*/
	private static final long base_hash = 0x00000000;
	/** The index where castling hashes begin. */
	private static final int CASTLING_HASHES = 832;
	/** The id to use for en passant in conjunction with getIndex **/
	private static final byte EN_PASSANT_ID = 6;
	//----------------------End of Constants----------------------
	//----------------------Fields----------------------
	/**
	 * The hash indices and their values. Indices 0-127 are for pawns (2 colors, 64 squares), indices
	 * 128-255 are for rooks, indices 256-383 are for kights, indices 384-511 are for bishops, 
	 * indices 512-639 are for queens, 640-767 are for kings, 768-831 are for en passant squares,
	 * 832-835 are for castling rights.
	 */
	private static long[] hash_values = new long[836];
	//----------------------End of Fields----------------------
	//----------------------Methods----------------------
	/**
	 * Initialises the hashing system by generating the xor hash values. All hash values are unique,
	 * although there may be collisions within indices.
	 */
	public static void init(){
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
		hash_values = strings;
	}
	/**
	 * Creates a hash value from scratch. If there is an initial hash to compare to, then do not use
	 * this method as it is awfully slow!
	 * @param white The white pieces to hash into the string.
	 * @param black The black pieces to hash into the string.
	 * @param castling_rights The castling rights for the position.
	 * @oaram epsq The en-passant square for the position.
	 * @return The basic hash of the position, given the parameters.
	 */
	public static long createinitialhash (Piece [] white, Piece [] black, boolean [] castling_rights, byte epsq){
		long to_return = base_hash;
		for (Piece r: white) 
			if (r.exists()) to_return ^= hash_values[getIndex(r.getPosition(), r.getType(), r.getColour())];
		for (Piece r: black) 
			if (r.exists()) to_return ^= hash_values[getIndex(r.getPosition(), r.getType(), r.getColour())];
		for (int i = 0; i < 4; i++) if (castling_rights[i]) to_return ^= hash_values[CASTLING_HASHES+i];
		if ((epsq & 0x88) == 0) to_return ^= hash_values[getIndex(epsq, EN_PASSANT_ID, Piece.WHITE)];
		return to_return;
	}
	/**
	 * Xors out a square from the hash and xors in a new square from the hash. Used for normal moves.
	 * @param original_hash The original hash value.
	 * @param sq_in The square to hash in.
	 * @param sq_out The square to hash out.
	 * @param p_type The type of piece to hash.
	 * @param color The color of piece to hash.
	 * @return A new hash after the specified transformations.
	 */
	public static long xorinout (long original_hash, byte sq_in, byte sq_out, byte p_type, byte color){
		return original_hash^hash_values[getIndex(sq_in,p_type,color)]^hash_values[getIndex(sq_out,p_type,color)];
	}
	/**
	 * Xors out a square from the hash. Used for captures.
	 * @param original_hash The original hash value.
	 * @param sq_out The square to hash out.
	 * @param p_type The type of piece to hash hout.
	 * @param color The colour of the piece to hash out.
	 * @return A new hash after the specified transformations.
	 */
	public static long xorout (long original_hash, byte sq_out, byte p_type, byte color){
		return original_hash^hash_values[getIndex(sq_out, p_type, color)];
	}
	/**
	 * Xors in new castling rights.
	 * @param original_hash The original hash value.
	 * @param original_rights The original castling rights to hash out.
	 * @param new_rights The new castling rights to hash in.
	 * @return A new hash after the specified transformations.
	 */
	public static long xorcastling (long original_hash, boolean [] original_rights, boolean [] new_rights){
		long new_hash = original_hash;
		for (int i = 0; i < 4; i++)
			if (original_rights[i] &&!new_rights[i]) new_hash ^= hash_values[CASTLING_HASHES+i];
		return new_hash;
	}
	/**
	 * Xors in new en passant square designations.
	 * @param original_hash The original hash value.
	 * @param original_epsq The original en passant square to hash out.
	 * @param new_epsq The new en passant square to hash in.
	 * @return A new hash after the specified tranformations.
	 */
	public static long xorepsq (long original_hash, byte original_epsq, byte new_epsq){
		long new_hash = original_hash;
		if ((original_epsq & 0x88)==0) new_hash^=hash_values[getIndex(original_epsq,EN_PASSANT_ID,Piece.WHITE)];
		if ((new_epsq & 0x88)==0) new_hash^= hash_values[getIndex(original_epsq,EN_PASSANT_ID,Piece.WHITE)];
		return new_hash;
	}
	/**
	 * Xors in a promotion.
	 * @param original_hash The original hash value.
	 * @param position The position of the promoted pawn.
	 * @param newtype The identifier of the promoted piece.
	 * @param color The color of the promoted pawn.
	 * @return A new hash after the specified transformations.
	 */
	public static long xorpromotion (long original_hash, byte position, byte newtype, byte color){
		long new_hash = original_hash;
		new_hash^=hash_values[getIndex(position, Piece.PAWN, color)];
		new_hash^=hash_values[getIndex(position, newtype, color)];
		return new_hash;
	}
	//----------------------Helper Methods----------------------
	/**
	 * Gets the index containing the hash from the hash values array.
	 * @param pos The position, in hex notation.
	 * @param id The identifier, the piece identifier or 6 for en passant.
	 * @param color The color modifier.
	 * @return The index containing the appropriate hash.
	 */
	private static int getIndex(byte pos, byte id, byte color){
		int modifier = color == Piece.WHITE ? 0 : 64;
		return modifier+multiplier[id]*(pos-((pos >> 4)*0x8));
	}
	//----------------------End of Helper Methods----------------------
	//----------------------End of Methods----------------------
}