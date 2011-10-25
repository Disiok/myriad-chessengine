package debug;

import java.util.Vector;
import rules.*;

public class ToBeAdded {
	public static void main(String args[]){
		String FEN ="rnbqkbnr/pp1ppppp/8/2p5/2P5/1Q6/PP1PPPPP/RNB1KBNR b KQkq - 1";
		Position p = FenUtility.loadFEN(FEN);
		FenUtility.displayBoard(FEN);
		for (Piece piece: getPiecesControllingSquare(p, (byte)0x20,Piece.WHITE))
			System.out.println(piece);
	}
	/**
	 * Return all pieces of a certain color that is controlling the specific square.
	 * @param sq the specified square location represented in 0x88
	 * @param p the current position
	 * @param col the color in concern
	 * @return Vector of pieces that of a certain color that control a certain square
	 */
	public static Vector<Piece> getPiecesControllingSquare(Position p, byte sq, byte col){
		Vector <Piece> toReturn = new Vector <Piece> (10,3);
		byte next_pos;
		Piece c_p;
		//Pawn
		for (byte diff : Position.DIAGONALS){
			if ((col==Piece.WHITE && diff < 0) || (col==Piece.BLACK) && diff > 0){
				c_p = p.getSquareOccupier ((byte)(sq + diff));
				if (((sq + diff)& 0x88)==0 
						&& c_p.getType()==Piece.PAWN
						&& c_p.getColour()==col)
					toReturn.add(c_p);
			}
		}
		//Knight
		for (int j = 0; j < 8; j++){
			next_pos = (byte)(sq + Position.KNIGHT_MOVES[j]);
			c_p = p.getSquareOccupier (next_pos);
			if ((next_pos&0x88)==0&&c_p.getColour()==col&&c_p.getType()==Piece.KNIGHT)
				toReturn.add(c_p);
		}
		//Horizontal: Rook, Queen
		for (byte diff : Position.HORIZONTALS){
			for (Piece piece : getAttackablePieces(p,sq,diff,col)){
				if (piece.getType() == Piece.QUEEN || piece.getType() == Piece.ROOK) 
					toReturn.add(piece);
				else break;
			}
		}
		//Diagonal: Queen, Bishop
		for (byte diff : Position.DIAGONALS){
			for (Piece piece : getAttackablePieces(p,sq, diff,col)){
				if (piece.getType() == Piece.QUEEN || piece.getType() == Piece.BISHOP) 
					toReturn.add(piece);
				if (piece.getType() == Piece.PAWN){
					if (toReturn.contains(piece))
						continue;
					else break;
				}
				else break;
			}
		}
		return toReturn;
	}
	//return all pieces on a specific line that can attack the specified location
	//including empty pieces
	//stop searching when reaches an opposite color piece.
	private static Vector<Piece> getAttackablePieces(Position p, byte loc, byte diff, byte col){
		Vector <Piece> toReturn = new Vector <Piece> (10,3);
		byte next_pos = loc;
		do{
			next_pos += diff;
			Piece o_pos = p.getSquareOccupier(next_pos);
			if (o_pos.getColour() == col) 
				toReturn.add(o_pos);
			else if (o_pos.getColour() == -1) continue;
			else break;
		}while ((next_pos&0x88)==0);
		return toReturn;
	}
}
