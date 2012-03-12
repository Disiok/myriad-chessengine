package debug;

import rules.*;

public class AlgebraicNotationConverter {
	public static Position load(String string){
		Position pos = new Position();
		String[] moves = string.split(" ");
		for(int i = 0; i < moves.length; i++){
			Move m = null;
			Piece[] map = i % 2 == 0 ? pos.getWhitePieces() : pos.getBlackPieces();
			byte side = i % 2 == 0 ? (byte)1 : (byte)-1;
			if(moves[i].length() == 2){ // Pawn moves
				for(Piece p: map){
					if(p.getType() == 0){
						byte loc = p.getPosition();
						if((loc & 0xf) == getFile(moves[i])){
							if(Math.abs((loc >> 4) - getRank(moves[i])) == 2){
								m = new Move(loc, getLoc(moves[i]), (byte)20);
							}
							else{
								m = new Move(loc, getLoc(moves[i]));
							}
							pos = pos.makeMove(m);
							break;
						}
					}
				}
			}
			else if(moves[i].indexOf('x') != -1){ // Captures
				boolean ep = false;
				if(moves[i].indexOf("e.p.") != -1){
					moves[i] = moves[i].substring(moves[i].indexOf("e.p."));
					ep = true;
				}				
				String m_loc = moves[i].substring(moves[i].length() - 2);
				char id = moves[i].charAt(0);
				int ind = moves[i].indexOf('x');
				switch(id){
					case 'R':{
						for(Piece p: map){
							byte loc = p.getPosition();
							if(p.getType() == 1){
								if(ind == 2 && ((loc & 0xf) == getFile(m_loc) || (loc >> 4) == getRank(m_loc))){
									m = new Move(loc, getLoc(m_loc), (byte) 10);
									pos = pos.makeMove(m);
									break;
								}
								else{
									char type = moves[i].charAt(1);
									if((type >= 'a' && type <= 'h') && ((loc & 0xf) == getFile(m_loc))){
										m = new Move(loc, getLoc(m_loc), (byte) 10);
										pos = pos.makeMove(m);
										break;
									}
									else if((type >= '1' && type <= '8') && ((loc >> 4) == getRank(m_loc))){
										m = new Move(loc, getLoc(m_loc), (byte) 10);
										pos = pos.makeMove(m);
										break;
									}
								}
							}
						}
						break;
					}
					case 'N':{
						for(Piece p: map){
							byte loc = p.getPosition();
							byte endLoc = getLoc(m_loc);
							if(p.getType() == 2){
								if(ind == 2 && (loc + pos.KNIGHT_MOVES[0] == endLoc
										|| loc + pos.KNIGHT_MOVES[1] == endLoc
										|| loc + pos.KNIGHT_MOVES[2] == endLoc
										|| loc + pos.KNIGHT_MOVES[3] == endLoc
										|| loc + pos.KNIGHT_MOVES[4] == endLoc
										|| loc + pos.KNIGHT_MOVES[5] == endLoc
										|| loc + pos.KNIGHT_MOVES[6] == endLoc
										|| loc + pos.KNIGHT_MOVES[7] == endLoc)){
									m = new Move(loc, endLoc, (byte) 10);
									pos = pos.makeMove(m);
									break;
								}
								else{
									char type = moves[i].charAt(1);
									if((type >= 'a' && type <= 'h') 
											&& ((loc >> 4) == (moves[i].charAt(2) - 97)) 
											&& (loc + pos.KNIGHT_MOVES[0] == endLoc
											|| loc + pos.KNIGHT_MOVES[1] == endLoc
											|| loc + pos.KNIGHT_MOVES[2] == endLoc
											|| loc + pos.KNIGHT_MOVES[3] == endLoc
											|| loc + pos.KNIGHT_MOVES[4] == endLoc
											|| loc + pos.KNIGHT_MOVES[5] == endLoc
											|| loc + pos.KNIGHT_MOVES[6] == endLoc
											|| loc + pos.KNIGHT_MOVES[7] == endLoc)){
										m = new Move(loc, endLoc, (byte) 10);
										pos = pos.makeMove(m);
										break;
									}
									else if((type >= '1' && type <= '8')
											&& ((loc & 0xf) == (moves[i].charAt(2) - 49)) 
											&& (loc + pos.KNIGHT_MOVES[0] == endLoc
											|| loc + pos.KNIGHT_MOVES[1] == endLoc
											|| loc + pos.KNIGHT_MOVES[2] == endLoc
											|| loc + pos.KNIGHT_MOVES[3] == endLoc
											|| loc + pos.KNIGHT_MOVES[4] == endLoc
											|| loc + pos.KNIGHT_MOVES[5] == endLoc
											|| loc + pos.KNIGHT_MOVES[6] == endLoc
											|| loc + pos.KNIGHT_MOVES[7] == endLoc)){
										m = new Move(loc, endLoc, (byte) 10);
										pos = pos.makeMove(m);
										break;
									}
								}
							}
						}
						break;
					}
					case 'B':{
						for(Piece p: map){
							byte loc = p.getPosition();
							if(p.getType() == 3){
								if((Math.abs((loc & 0xf) - getFile(m_loc)) == Math.abs((loc >> 4) - getRank(m_loc)))){
									m = new Move(loc, getLoc(m_loc), (byte) 10);
									pos = pos.makeMove(m);
									break;
								}
							}
						}
						break;
					}
					case 'Q':{
						for(Piece p: map){
							byte loc = p.getPosition();
							if(p.getType() == 4){
								if(ind == 2 && 
										((loc & 0xf) == getFile(m_loc) || (loc >> 4) == getRank(m_loc)) 
										|| (Math.abs((loc & 0xf) - getFile(m_loc)) == Math.abs((loc >> 4) - getRank(m_loc)))){
									m = new Move(loc, getLoc(m_loc), (byte) 10);
									pos = pos.makeMove(m);
									break;
								}
								else{
									char type = moves[i].charAt(1);
									if((type >= 'a' && type <= 'h') 
											&& ((loc >> 4) == getFile(m_loc) 
											|| Math.abs((loc & 0xf) - getFile(m_loc)) == Math.abs((loc >> 4) - getRank(m_loc)))){
										m = new Move(loc, getLoc(m_loc), (byte) 10);
										pos = pos.makeMove(m);
										break;
									}
									else if((type >= '1' && type <= '8') 
											&& ((loc & 0xf) == getRank(m_loc)
											|| Math.abs((loc >> 4) - getFile(m_loc)) == Math.abs((loc >> 4) - getRank(m_loc)))){
										m = new Move(loc, getLoc(m_loc), (byte) 10);
										pos = pos.makeMove(m);
										break;
									}
								}
							}
						}
						break;
					}
					case 'K':{
						for(Piece p: map){
							if(p.getType() == 5){
								m = new Move(p.getPosition(), getLoc(m_loc), (byte) 10);
								pos = pos.makeMove(m);
								break;
							}
						}
						break;
					}
					default:{
						for(Piece p: map){
							byte loc = p.getPosition();
							if(((loc & 0xf) == (id - 49)) && (loc + (side*pos.LEFT_UP_MOVE) == getLoc(m_loc) 
									|| loc + (side*pos.RIGHT_UP_MOVE) == getLoc(m_loc))){
								if(!ep){
									m = new Move(loc, getLoc(m_loc), (byte)10);
								}
								else{
									m = new Move(loc, getLoc(m_loc), (byte)5);
								}
							}
						}
						break;
					}
				}
			}
			else if(moves[i].indexOf('=') != -1){ // Promotions
				moves[i] = moves[i].substring(moves[i].indexOf('='));
				String m_loc = moves[i].substring(moves[i].length() - 2);
				char id = moves[i].charAt(moves[i].length() - 1);
				boolean cap = moves[i].indexOf('x') != -1;
				byte mod = 0;
				if(id == 'R') mod = 6;
				else if(id == 'N') mod = 7;
				else if(id == 'B') mod = 8;
				else if(id == 'Q') mod = 9;
				if(cap){
					mod += 10;
					for(Piece p: map){
						byte loc = p.getPosition();
						if(((loc & 0xf) == getFile(m_loc)) && (loc + (side*pos.LEFT_UP_MOVE) == getLoc(m_loc) 
								|| loc + (side*pos.RIGHT_UP_MOVE) == getLoc(m_loc))){
							m = new Move(loc, getLoc(m_loc), mod);
							pos = pos.makeMove(m);
							break;
						}
					}
				}				
				else{
					for(Piece p: map){
						byte loc = p.getPosition();
						if(loc + (side*pos.UP_MOVE) == getLoc(m_loc)){
							m = new Move(loc, getLoc(m_loc), mod);
							pos = pos.makeMove(m);
							break;
						}
					}
				}
			}
			else if(moves[i].indexOf('-') != -1){ // Castling
				byte mod = moves[i].length() == 3 ? (byte)1 : (byte)3;
				byte startsq = i % 2 == 0 ? (byte)0x04 : (byte)0x74;
				byte endsq = moves[i].length() == 3 ? (byte)(startsq + (2*pos.RIGHT_MOVE)) : (byte)(startsq + (3*pos.LEFT_MOVE));
				if(i % 2 != 0){
					mod ++;
				}
				m = new Move(startsq, endsq, mod);
				pos = pos.makeMove(m);
				break;
			}
			else{ // Non-pawn standard moves
				String m_loc = moves[i].substring(moves[i].length() - 2);
				char id = moves[i].charAt(0);
				char type = moves[i].charAt(1);
				switch(id){
					case 'R':{
						for(Piece p: map){
							byte loc = p.getPosition();
							if(p.getType() == 1){
								if(moves[i].length() == 4){
									if((type >= 'a' && type <= 'h') && ((loc & 0xf) == (type - 97))){
										m = new Move(loc, getLoc(m_loc));
										break;
									}
									else if((type >= '1' && type <= '8')&& ((loc >> 4) == (type - 49))){
										m = new Move(loc, getLoc(m_loc));
										break;
									}
								}
								else{
									if((loc & 0xf) == getFile(m_loc) || (loc >> 4) == getRank(m_loc)){
										m = new Move(loc, getLoc(m_loc));
										break;
									}
								}
							}
						}
						pos = pos.makeMove(m);
						break;
					}
					case 'N':{
						for(Piece p: map){
							byte loc = p.getPosition();
							if(p.getType() == 2){
								if(moves[i].length() == 4){
									if((type >= 'a' && type <= 'h') && ((loc & 0xf) == (type - 97))){
										m = new Move(loc, getLoc(m_loc));
										break;
									}
									else if((type >= '1' && type <= '8')&& ((loc >> 4) == (type - 49))){
										m = new Move(loc, getLoc(m_loc));
										break;
									}
								}
								else{
									byte endLoc = getLoc(m_loc);
									if(loc + pos.KNIGHT_MOVES[0] == endLoc
											|| loc + pos.KNIGHT_MOVES[1] == endLoc
											|| loc + pos.KNIGHT_MOVES[2] == endLoc
											|| loc + pos.KNIGHT_MOVES[3] == endLoc
											|| loc + pos.KNIGHT_MOVES[4] == endLoc
											|| loc + pos.KNIGHT_MOVES[5] == endLoc
											|| loc + pos.KNIGHT_MOVES[6] == endLoc
											|| loc + pos.KNIGHT_MOVES[7] == endLoc){
										m = new Move(loc, getLoc(m_loc));
										break;
									}
								}
							}
						}
						pos = pos.makeMove(m);
						break;
					}
					case 'B':{
						for(Piece p: map){
							byte loc = p.getPosition();
							if(p.getType() == 3){
								if(Math.abs((loc & 0xf) - getFile(m_loc)) == Math.abs((loc >> 4) - getRank(m_loc))){
									m = new Move(loc, getLoc(m_loc));
									break;
								}
							}
						}
						pos = pos.makeMove(m);
						break;
					}
					case 'Q':{
						for(Piece p: map){
							byte loc = p.getPosition();
							if(p.getType() == 4){
								if(moves[i].length() == 4){
									if((type >= 'a' && type <= 'h') && ((loc & 0xf) == (type - 97))){
										m = new Move(loc, getLoc(m_loc));
										break;
									}
									else if((type >= '1' && type <= '8')&& ((loc >> 4) == (type - 49))){
										m = new Move(loc, getLoc(m_loc));
										break;
									}
								}
								else{
									if(((loc & 0xf) == getFile(m_loc) || (loc >> 4) == getRank(m_loc)) 
										|| (Math.abs((loc & 0xf) - getFile(m_loc)) == Math.abs((loc >> 4) - getRank(m_loc)))){
										m = new Move(loc, getLoc(m_loc));
										break;
									}
								}
							}
						}
						pos = pos.makeMove(m);
						break;
					}
					case 'K':{
						for(Piece p: map){
							byte loc = p.getPosition();
							if(p.getType() == 5){
								m = new Move(loc, getLoc(m_loc));
								break;
							}
						}
						pos = pos.makeMove(m);
						break;
					}
				}
			}
		}
		return pos;
	}
	
	// Changes a alphanumeric representation of a square to a byte
	public static byte getLoc(String loc){
		byte toReturn = 0;
		char file = loc.charAt(loc.length() - 2);
		char rank = loc.charAt(loc.length() - 1); 
		toReturn = (byte)(rank - 49);
		toReturn = (byte)((toReturn << 4) + (file - 97));
		return toReturn;
	}
	
	// Returns the rank (x co-ordinate) of a position
	public static byte getFile(String loc){
		byte toReturn = 0;
		char rank = loc.charAt(0);
		toReturn = (byte)(rank - 97);
		return toReturn;
	}
	
	// Returns the file (y co-ordinate) of a position
	public static byte getRank(String loc){
		byte toReturn = 0;
		char file = loc.charAt(1);
		toReturn = (byte)(file - 49);
		return toReturn;
	}
	
	// First turn the string into array of moves, then use p.makeMove(Move) to update the position
	/*public static Position loadANC(String string){
		String[] moves = string.split(" ");
		
		Position pos = new Position();
		for(int i = 0; i < moves.length; i++){
			Piece[] pieces = i % 2 == 0 ? pos.getWhitePieces(): pos.getBlackPieces();
			byte side = i % 2 == 0 ? (byte)1 : (byte)-1;
			switch(moves[i].length()){
				// Pawn non-capturing moves
				case 2:{
					for(Piece p: pieces){
						if(conv(moves[i]) - p.getPosition() == side * pos.UP_MOVE){
							Move m = new Move(p.getPosition(), conv(moves[i]));
							pos = pos.makeMove(m);
							break;
						}
						else if(conv(moves[i]) - p.getPosition() == side * 2 * pos.UP_MOVE){
							Move m = new Move(p.getPosition(), conv(moves[i]), (byte)20);
							pos = pos.makeMove(m);
							break;
						}
					}
					break;
				}
				// Knight/Bishop/Rook/Queen/King non-capturing moves
				case 3:
				// Capturing moves, pawn promotions, special moves
				case 4:{
					char id = moves[i].charAt(0);
					String loc = moves[i].substring(moves[i].length() - 2);
					switch(id){
						case 'R':{
							for(Piece p: pieces){
								if(p.getType() == 1){
									if(((p.getPosition() >> 4) == getRank(moves[i])) 
										|| ((p.getPosition() & 0xf) == getFile(moves[i]))){
										Move m = null;
										if(moves[i].indexOf('x') != -1){
											m = new Move(p.getPosition(), conv(moves[i]), (byte)10);
										}
										else{
											m = new Move(p.getPosition(), conv(moves[i]));
										}
										pos = pos.makeMove(m);
										break;
									}
								}
							}
							break;
						} // End of case 'R':
						case 'N':{
							for(Piece p: pieces){
								if(p.getType() == 2){
									if(p.getPosition() + pos.KNIGHT_MOVES[0] == conv(moves[i]) 
										|| p.getPosition() + pos.KNIGHT_MOVES[1] == conv(moves[i]) 
										|| p.getPosition() + pos.KNIGHT_MOVES[2] == conv(moves[i]) 
										|| p.getPosition() + pos.KNIGHT_MOVES[3] == conv(moves[i])
										|| p.getPosition() + pos.KNIGHT_MOVES[4] == conv(moves[i])
										|| p.getPosition() + pos.KNIGHT_MOVES[5] == conv(moves[i])
										|| p.getPosition() + pos.KNIGHT_MOVES[6] == conv(moves[i])
										|| p.getPosition() + pos.KNIGHT_MOVES[7] == conv(moves[i])){
										Move m = null;
										if(moves[i].indexOf('x') != -1){
											m = new Move(p.getPosition(), conv(moves[i]), (byte)10);
										}
										else{
											m = new Move(p.getPosition(), conv(moves[i]));
										}
										pos = pos.makeMove(m);
										break;
									}
								}
							}
							break;
						} // End of case 'N':
						case 'B':{
							for(Piece p: pieces){
								if(p.getType() == 3){
									if((Math.abs((p.getPosition() >> 4) - getFile(moves[i]))) == 
										(Math.abs((p.getPosition() & 0xf) - getRank(moves[i])))){
										Move m = null;
										if(moves[i].indexOf('x') != -1){
											m = new Move(p.getPosition(), conv(moves[i]), (byte)10);
										}
										else{
											m = new Move(p.getPosition(), conv(moves[i]));
										}
										pos = pos.makeMove(m);
										break;
									}
								}
							}
							break;
						} // End of case 'B':
						case 'Q':{
							for(Piece p: pieces){
								if(p.getType() == 4){
									if((((p.getPosition() >> 4) == getRank(moves[i])) 
										|| ((p.getPosition() & 0xf) == getFile(moves[i])))
										|| ((Math.abs((p.getPosition() >> 4) - getFile(moves[i]))) 
										== (Math.abs((p.getPosition() & 0xf) - getRank(moves[i]))))){
										Move m = null;
										if(moves[i].indexOf('x') != -1){
											m = new Move(p.getPosition(), conv(moves[i]), (byte)10);
										}
										else{
											m = new Move(p.getPosition(), conv(moves[i]));
										}
										pos = pos.makeMove(m);
										break;
									}
								}
							}
							break;
						} // End of case 'Q':
						case 'K':{
							for(Piece p: pieces){
								if(p.getType() == 5){
									Move m = null;
									if(moves[i].indexOf('x') != -1){
										m = new Move(p.getPosition(), conv(moves[i]), (byte)10);
									}
									else{
										m = new Move(p.getPosition(), conv(moves[i]));
									}
									pos = pos.makeMove(m);
									break;
								}
							}
							break;
						} // End of case 'K':
						case '0':{
							for(Piece p: pieces){
								if(p.getType() == 5){
									if(side == 1){
										Move m = new Move(p.getPosition(), (byte)0x06, (byte)1);
										pos = pos.makeMove(m);
										break;
									}
									else{
										Move m = new Move(p.getPosition(), (byte)0x76, (byte)2);
										pos = pos.makeMove(m);
										break;
									}
								}
							}
							break;
						} // End of case '0':
						default:{
							for(Piece p: pieces){
								if((p.getPosition() >> 4) == getFile(moves[i])){
									Move m = new Move(p.getPosition(), conv(moves[i]), (byte)10);
									pos = pos.makeMove(m);
									break;
								}
							}
							break;
						} // End of default (Pawn captures)
					}
				}
				// Normal captures or special case moves (eg. Ngf3) or pawn promotion
				case 4:{
					if(moves[i].indexOf('x') != -1){
						char id = moves[i].charAt(0);
						switch(id){
							case 'R': {
								
								break;
							}
							case 'N': break;
							case 'B': break;
							case 'Q': break;
							case 'K': break;
							default: break;
						}
					}
					break;
				}
				// Special captures (eg. N5xf3)
				case 5:{
					break;
				}
			}
		}
		return pos;
	}*/
	
	
	public static void main(String[] args){
		String test = "e4 e5 Nf3";
		AlgebraicNotationConverter tester = new AlgebraicNotationConverter();
		Utility util = new Utility();
		
		Position stuff = tester.load(test);
		
		String saveFEN = util.saveFEN(stuff);
		util.displayBoard(saveFEN);

	}
}
