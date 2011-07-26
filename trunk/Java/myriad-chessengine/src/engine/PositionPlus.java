package engine;

import java.util.*;
import rules.*;

public class PositionPlus extends Position {
	
	public Piece[] white_pawns;
	public Piece[] white_rooks;
	public Piece[] white_knights;
	public Piece[] white_bishops;
	public Piece[] white_queens;
	public Piece[] white_king;
	public Piece[] black_pawns;
	public Piece[] black_rooks;
	public Piece[] black_knights;
	public Piece[] black_bishops;
	public Piece[] black_queens;
	public Piece[] black_king;
	
	public PositionPlus(){
		super();
		white_pawns = getPieces(Piece.PAWN, true);
 		white_rooks = getPieces(Piece.ROOK, true);
		white_knights = getPieces(Piece.KNIGHT, true);
		white_bishops = getPieces(Piece.BISHOP, true);
		white_queens = getPieces(Piece.QUEEN, true);
		white_king = getPieces(Piece.KING,true);
		black_pawns = getPieces(Piece.PAWN, false);
		black_rooks = getPieces(Piece.ROOK, false);
		black_knights = getPieces(Piece.KNIGHT, false);
		black_bishops = getPieces(Piece.BISHOP, false);
		black_queens = getPieces(Piece.QUEEN, false);
		black_king = getPieces(Piece.KING,false);
	}
	public PositionPlus(Position p){
		super(p.get50MoveCount(), p.getEnPassantSquare(), p.getCastlingRights(), 
				p.isWhiteToMove(), p.getWhitePieces(), p.getBlackPieces());
		white_pawns = getPieces(Piece.PAWN, true);
 		white_rooks = getPieces(Piece.ROOK, true);
		white_knights = getPieces(Piece.KNIGHT, true);
		white_bishops = getPieces(Piece.BISHOP, true);
		white_queens = getPieces(Piece.QUEEN, true);
		white_king = getPieces(Piece.KING,true);
		black_pawns = getPieces(Piece.PAWN, false);
		black_rooks = getPieces(Piece.ROOK, false);
		black_knights = getPieces(Piece.KNIGHT, false);
		black_bishops = getPieces(Piece.BISHOP, false);
		black_queens = getPieces(Piece.QUEEN, false);
		black_king = getPieces(Piece.KING,false);
	}
	public Piece [] getMinorPieces (boolean mapToSearch){
		Piece [] map = mapToSearch ? white_map : black_map;
		Vector <Piece> des = new Vector <Piece> (4,1);
		for (Piece p : map){
			byte type = p.getType();
			if (type == Piece.KNIGHT || type == Piece.BISHOP){
				des.add(new Piece (p.getPosition(),type,p.getColour()));
			}
		}
		Piece [] toReturn = new Piece [des.size()];
		return des.toArray(toReturn);
	}
	public Piece [] getPieces (byte p_type, boolean mapToSearch){
		Piece [] map = mapToSearch ? white_map : black_map;
		Vector <Piece> des = new Vector <Piece> (8,1);
		for (Piece p : map){
			byte c_type = p.getType();
			if (c_type == p_type){
				des.add(new Piece (p.getPosition(),c_type,p.getColour()));
			}
		}
		Piece [] toReturn = new Piece [des.size()];
		return des.toArray(toReturn);
	}
}
