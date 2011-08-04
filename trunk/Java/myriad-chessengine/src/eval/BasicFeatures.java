package eval;

import java.util.*;
import rules.*;

public class BasicFeatures {
	private Piece[] white_pawns;
	private Piece[] white_rooks;
	private Piece[] white_knights;
	private Piece[] white_bishops;
	private Piece[] white_queens;
	private Piece[] white_king;
	private Piece[] black_pawns;
	private Piece[] black_rooks;
	private Piece[] black_knights;
	private Piece[] black_bishops;
	private Piece[] black_queens;
	private Piece[] black_king;
	private boolean[] castling_rights;
	private Position original_position;
	
	public BasicFeatures(Position p){
		original_position = p;
		Piece[] w_map = p.getWhitePieces();
		Piece[] b_map = p.getBlackPieces();
		castling_rights = p.getCastlingRights();
		white_pawns = getPieces(Piece.PAWN, w_map);
 		white_rooks = getPieces(Piece.ROOK, w_map);
		white_knights = getPieces(Piece.KNIGHT, w_map);
		white_bishops = getPieces(Piece.BISHOP, w_map);
		white_queens = getPieces(Piece.QUEEN, w_map);
		white_king = getPieces(Piece.KING, w_map);
		black_pawns = getPieces(Piece.PAWN, b_map);
		black_rooks = getPieces(Piece.ROOK, b_map);
		black_knights = getPieces(Piece.KNIGHT, b_map);
		black_bishops = getPieces(Piece.BISHOP, b_map);
		black_queens = getPieces(Piece.QUEEN, b_map);
		black_king = getPieces(Piece.KING, b_map);
	}
	private Piece [] getPieces (byte p_type, Piece [] map){
		Vector <Piece> pieces = new Vector <Piece> (2,6);
		for (Piece p : map){
			byte c_type = p.getType();
			if (c_type == p_type){
				pieces.add(new Piece (p.getPosition(),c_type,p.getColour()));
			}
		}
		Piece [] toReturn = new Piece [pieces.size()];
		return pieces.toArray(toReturn);
	}
	public Position retrieveEmbeddedPosition(){
		return original_position;
	}
	public boolean [] getCastlingRights(){
		return castling_rights;
	}
	public Piece [] getWhitePawns(){
		return white_pawns; 
	}
	public Piece [] getWhiteRooks(){
		return white_rooks;
	}
	public Piece [] getWhiteKnights(){
		return white_knights; 
	}
	public Piece [] getWhiteBishops(){
		return white_bishops; 
	}
	public Piece [] getWhiteQueens(){
		return white_queens; 
	}
	public Piece getWhiteKing(){
		return white_king[0]; 
	}
	public Piece [] getBlackPawns(){
		return black_pawns;
	}
	public Piece [] getBlackRooks(){
		return black_rooks;
	}
	public Piece [] getBlackKnights(){
		return black_knights;
	}
	public Piece [] getBlackBishops(){
		return black_bishops;
	}
	public Piece [] getBlackQueens(){
		return black_queens;
	}
	public Piece getBlackKing(){
		return black_king[0];
	}
}
