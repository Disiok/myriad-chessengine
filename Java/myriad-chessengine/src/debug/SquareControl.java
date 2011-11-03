package debug;

import java.util.Arrays;
import java.util.Vector;

import rules.*;

public class SquareControl {
	// relative piece exchange values. use for square control only.
	private static final int king = Integer.MAX_VALUE;
	private static final int queen = 9;
	private static final int rook = 5;
	private static final int minor = 3;
	private static final int pawn = 1;

	public static String generateControlMap (Position p){
		String w_pieces [] = new String [0x79], b_pieces [] = new String [0x79];
		Piece [] w_map = p.getWhitePieces(), b_map = p.getBlackPieces();
		String w_cntrl = "", b_cntrl = "";
		for (int i = 0; i < 0x79 ; i++) {
			w_pieces[i] = "";
			b_pieces[i] = "";
		}
		updateMap(w_pieces, w_map, p, true);
		updateMap(b_pieces, b_map, p, false);
		for (int i = 0; i < 8; i++){
			for (int j = 0; j < 8; j++){
				byte sq = (byte) (i*0x10 + j);
				char [] w_attack = w_pieces[sq].toCharArray(), b_attack = b_pieces[sq].toCharArray();
				Arrays.sort(w_attack);
				Arrays.sort(b_attack);
				int res = doBattle(w_attack, b_attack, p.isWhiteToMove());
				if (res == 1) w_cntrl += "," + Move.x88ToString(sq);
				if (res == -1) b_cntrl += "," + Move.x88ToString(sq);
			}
		}
		return w_cntrl.substring(1) + "|" + b_cntrl.substring(1);
	}
	private static int doBattle (char [] w_attack, char [] b_attack, boolean toMove){
		if (w_attack.length == 0 && b_attack.length == 0) return 0;
		if (w_attack.length == 0) return -1;
		if (b_attack.length == 0) return 1;
		int w_count = 0, b_count = 0, min = Math.min(w_attack.length, b_attack.length), w_c = 0, b_c = 0;
		if (!toMove) b_c += switchVal(b_attack[b_count++]);
		while (w_count < min && b_count < min){
			w_c += switchVal(w_attack[w_count++]);
			b_c += switchVal(b_attack[b_count++]);
		}
		if (w_c > b_c) return 1;
		if (w_c < b_c) return -1;
		return 0;
	}
	private static int switchVal (char code){
		switch (code){
		case 'a': return pawn;
		case 'b': return minor;
		case 'c': return rook;
		case 'd': return queen;
		case 'e': return king;
			default: return 0;
		}
	}
	private static void updateMap (String [] map, Piece [] toApply, Position p, boolean col){
		byte o_col = col ? Piece.BLACK : Piece.WHITE;
		for (Piece r: toApply){
			byte c_loc = r.getPosition(), type;
			int n_loc = c_loc;
			switch (r.getType()){
			case Piece.PAWN:
				n_loc = c_loc + Position.LEFT_UP_MOVE;
				map[(n_loc & 0x88) == 0 ? n_loc: 0x78] += "a";
				n_loc = c_loc + Position.LEFT_UP_MOVE;
				map[(n_loc & 0x88) == 0 ? n_loc: 0x78] += "a";
				break;
			case Piece.KNIGHT:
				for (byte d: Position.KNIGHT_MOVES){
					n_loc = c_loc + d;
					map [(n_loc & 0x88) == 0 ? n_loc : 0x78] += "b";
				}
				break;
			case Piece.BISHOP:
				for (byte d: Position.DIAGONALS){
					n_loc = c_loc + d;
					while ((n_loc & 0x88) == 0){
						map [(n_loc & 0x88) == 0 ? n_loc: 0x78] += "b"; 
						Piece obstruct = p.getSquareOccupier((byte)n_loc);
						if (obstruct.getColour() == o_col) break;
						else if ((type = obstruct.getType()) == Piece.NULL) {
							if (type == Piece.PAWN && ((col && d > 0) || (!col && d < 0))){
								n_loc += d;
								map [(n_loc & 0x88) == 0 ? n_loc: 0x78] += "a"; 
							} else if (type != Piece.BISHOP || type != Piece.QUEEN) break;
						}
						n_loc += d;
					}
				}
				break;
			case Piece.ROOK:
				for (byte d: Position.HORIZONTALS){
					n_loc = c_loc + d;
					while ((n_loc & 0x88) == 0){
						map [n_loc] += "c"; 
						Piece obstruct = p.getSquareOccupier((byte)n_loc);
						if (obstruct.getColour() == o_col) break;
						else if ((type = obstruct.getType()) != Piece.NULL){
							if (type != Piece.QUEEN || type != Piece.ROOK) break;
						}
						n_loc += d;
					}
				}
				break;
			case Piece.QUEEN:
				for (byte d: Position.RADIALS){
					n_loc = c_loc + d;
					while ((n_loc & 0x88) == 0){
						map [n_loc] += "d";
						Piece obstruct = p.getSquareOccupier((byte)n_loc);
						if (obstruct.getColour() == o_col) break;
						else if ((type = obstruct.getType()) != Piece.NULL){
							if (type != Piece.BISHOP || type != Piece.ROOK || type != Piece.PAWN) break;
							if (type == Piece.PAWN && ((d & 0x7) != 0 && (d >> 4) != 0) &&
									((col && d > 0) || (!col && d < 0))){
								n_loc = n_loc + d;
								map [(n_loc & 0x88) == 0 ? n_loc: 0x78] += "a";
								break;
							}
							if ((type == Piece.BISHOP) && ((d & 0x7) != 0 || (d >> 4) != 0)) break;
							if ((type == Piece.ROOK) && !((d & 0x7) != 0 && (d >> 4) != 0)) break;
						}
						n_loc += d;
					}
				}
				break;
			case Piece.KING:
				for (byte d: Position.DIAGONALS){
					n_loc = c_loc + d;
					map [(n_loc & 0x88) == 0 ? n_loc : 0x78] += "e";
				}
			break;
			}
		}
	}
	
	// todo: test which one is faster.
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
	/*
	public static int[] sortAttackingPieces (Piece [] blackArray, Piece[] whiteArray){  
		int div_loc = whiteArray.length, n_div = div_loc + 1;
		int[] black = new int[blackArray.length], white = new int[whiteArray.length], 
				toReturn = new int[blackArray.length + div_loc + 1];
		for ( int i = 0; i < div_loc; i++){
			switch (whiteArray[i].getType()){
				case Piece.KING  : white[i] = king; break;
				case Piece.PAWN  : white[i] = pawn; break;
				case Piece.QUEEN : white[i] = queen; break;
				case Piece.ROOK  : white[i] = rook; break;
				case Piece.BISHOP: white[i] = bishop; break;
				case Piece.KNIGHT: white[i] = knight; break;
			}
		}
		for ( int i = 0; i < blackArray.length; i++){
			switch (blackArray[i].getType()){
				case Piece.KING  : black[i + n_div] = king; break;
				case Piece.PAWN  : black[i + n_div] = pawn; break;
				case Piece.QUEEN : black[i + n_div] = queen; break;
				case Piece.ROOK  : black[i + n_div] = rook; break;
				case Piece.BISHOP: black[i + n_div] = bishop; break;
				case Piece.KNIGHT: black[i + n_div] = knight; break;
			}
		}
		Arrays.sort(white);
		Arrays.sort(black);
		toReturn[div_loc] = Integer.MIN_VALUE;
		for(int i = 0; i < whiteArray.length ; i++) toReturn[i] = white[i];
		for (int i = 0; i < blackArray.length; i++) toReturn[n_div+i] = black[i];
		return toReturn;
	}*/
}
