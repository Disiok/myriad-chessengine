package tuning;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;

import rules.*;

public class AlgebraicNotationConverter {	
	public static Position[] load(String string){
		LinkedList <Position> allPos = new LinkedList <Position> (); //
		
		Position pos = new Position();
		string = getMoves(string);
		String[] moves = string.split(" ");
		for(int i = 0; i < moves.length; i++){
			Move m = null;
			Piece[] map = i % 2 == 0 ? pos.getWhitePieces() : pos.getBlackPieces();
			byte side = i % 2 == 0 ? (byte)1 : (byte)-1;
			if(moves[i].indexOf('+') != -1){
				moves[i] = moves[i].substring(0, moves[i].indexOf('+'));
			}
			if(moves[i].indexOf('#') != -1){
				moves[i] = moves[i].substring(0, moves[i].indexOf('#'));
			}
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
							allPos.add(pos);
							break;
						}
					}
				}
			}
			else if(moves[i].indexOf('x') != -1){ // Captures
				boolean ep = false;
				if(moves[i].indexOf("e.p.") != -1){
					moves[i] = moves[i].substring(0, moves[i].indexOf("e.p."));
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
								if(ind == 1 && ((loc & 0xf) == getFile(m_loc) || (loc >> 4) == getRank(m_loc))){
									m = new Move(loc, getLoc(m_loc), (byte) 10);
									break;
								}
								else{
									char type = moves[i].charAt(1);
									if((type >= 'a' && type <= 'h') && ((loc & 0xf) == getFile(m_loc))){
										m = new Move(loc, getLoc(m_loc), (byte) 10);
										break;
									}
									else if((type >= '1' && type <= '8') && ((loc >> 4) == getRank(m_loc))){
										m = new Move(loc, getLoc(m_loc), (byte) 10);
										break;
									}
								}
							}
						}
						pos = pos.makeMove(m);
						allPos.add(pos);
						break;
					}
					case 'N':{
						for(Piece p: map){
							byte loc = p.getPosition();
							byte endLoc = getLoc(m_loc);
							if(p.getType() == 2){
								if(ind == 1 && (loc + Position.KNIGHT_MOVES[0] == endLoc
										|| loc + Position.KNIGHT_MOVES[1] == endLoc
										|| loc + Position.KNIGHT_MOVES[2] == endLoc
										|| loc + Position.KNIGHT_MOVES[3] == endLoc
										|| loc + Position.KNIGHT_MOVES[4] == endLoc
										|| loc + Position.KNIGHT_MOVES[5] == endLoc
										|| loc + Position.KNIGHT_MOVES[6] == endLoc
										|| loc + Position.KNIGHT_MOVES[7] == endLoc)){
									m = new Move(loc, endLoc, (byte) 10);
									break;
								}
								else{
									char type = moves[i].charAt(1);
									if((type >= 'a' && type <= 'h') 
											&& ((loc & 0xf) == (moves[i].charAt(1) - 97))){
										m = new Move(loc, endLoc, (byte) 10);
										break;
									}
									else if((type >= '1' && type <= '8')
											&& ((loc >> 4) == (moves[i].charAt(1) - 49))){
										m = new Move(loc, endLoc, (byte) 10);
										break;
									}
								}
							}
						}
						pos = pos.makeMove(m);
						allPos.add(pos);
						break;
					}
					case 'B':{
						for(Piece p: map){
							byte loc = p.getPosition();
							if(p.getType() == 3){
								if((Math.abs((loc & 0xf) - getFile(m_loc)) == Math.abs((loc >> 4) - getRank(m_loc)))){
									m = new Move(loc, getLoc(m_loc), (byte) 10);
									break;
								}
							}
						}
						pos = pos.makeMove(m);
						allPos.add(pos);
						break;
					}
					case 'Q':{
						for(Piece p: map){
							byte loc = p.getPosition();
							if(p.getType() == 4){
								if(ind == 1 && 
										((loc & 0xf) == getFile(m_loc) || (loc >> 4) == getRank(m_loc)) 
										|| (Math.abs((loc & 0xf) - getFile(m_loc)) == Math.abs((loc >> 4) - getRank(m_loc)))){
									m = new Move(loc, getLoc(m_loc), (byte) 10);
									break;
								}
								else{
									char type = moves[i].charAt(1);
									if((type >= 'a' && type <= 'h') 
											&& ((loc >> 4) == getFile(m_loc) 
											|| Math.abs((loc & 0xf) - getFile(m_loc)) == Math.abs((loc >> 4) - getRank(m_loc)))){
										m = new Move(loc, getLoc(m_loc), (byte) 10);
										break;
									}
									else if((type >= '1' && type <= '8') 
											&& ((loc & 0xf) == getRank(m_loc)
											|| Math.abs((loc >> 4) - getFile(m_loc)) == Math.abs((loc >> 4) - getRank(m_loc)))){
										m = new Move(loc, getLoc(m_loc), (byte) 10);
										break;
									}
								}
							}
						}
						pos = pos.makeMove(m);
						allPos.add(pos);
						break;
					}
					case 'K':{
						for(Piece p: map){
							if(p.getType() == 5){
								m = new Move(p.getPosition(), getLoc(m_loc), (byte) 10);
								break;
							}
						}
						pos = pos.makeMove(m);
						allPos.add(pos);
						break;
					}
					default:{
						for(Piece p: map){
							if(p.getType() == 0){
								byte loc = p.getPosition();
								if(!ep){
									if(loc + (side*Position.LEFT_UP_MOVE) == getLoc(m_loc) 
											|| loc + (side*Position.RIGHT_UP_MOVE) == getLoc(m_loc)){
										m = new Move(loc, getLoc(m_loc), (byte)10);
									}
								}
								else{
									if(loc + (Position.LEFT_MOVE) == getLoc(m_loc) 
											|| loc + (Position.RIGHT_MOVE) == getLoc(m_loc)){
										m = new Move(loc, getLoc(m_loc), (byte)5);
									}
								}
							}
						}
						pos = pos.makeMove(m);
						allPos.add(pos);
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
						if(((loc & 0xf) == getFile(m_loc)) && (loc + (side*Position.LEFT_UP_MOVE) == getLoc(m_loc) 
								|| loc + (side*Position.RIGHT_UP_MOVE) == getLoc(m_loc))){
							m = new Move(loc, getLoc(m_loc), mod);
							pos = pos.makeMove(m);
							allPos.add(pos);
							break;
						}
					}
				}				
				else{
					for(Piece p: map){
						byte loc = p.getPosition();
						if(loc + (side*Position.UP_MOVE) == getLoc(m_loc)){
							m = new Move(loc, getLoc(m_loc), mod);
							pos = pos.makeMove(m);
							allPos.add(pos);
							break;
						}
					}
				}
			}
			else if(moves[i].indexOf('-') != -1){ // Castling
				if(moves[i].indexOf('1') != -1){
					break;
				}
				else if(moves[i].length() == 3){
					if(side > 0){
						m = Move.CASTLE[0];
					}
					else{
						m = Move.CASTLE[1];
					}
				}
				else{
					if(side > 0){
						m = Move.CASTLE[2];
					}
					else{
						m = Move.CASTLE[3];
					}
				}
				pos = pos.makeMove(m);
				allPos.add(pos);
			}
			else{ // Non-pawn standard moves
				String m_loc = moves[i].substring(moves[i].length() - 2);
				char id = moves[i].charAt(0);
				char type = moves[i].charAt(1);
				switch(id){
					case 'R':{
						for(Piece p: map){
							if(p.getType() == 1){
								byte loc = p.getPosition();
								Move[] valid_moves = generateRQmoves(pos, loc, Position.HORIZONTALS, false, i);
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
										for(Move valid: valid_moves){
											if(valid.getStartSquare() == loc && valid.getEndSquare() == getLoc(m_loc)){
												m = new Move(loc, getLoc(m_loc));
												break;
											}
										}
									}
								}
							}
						}
						pos = pos.makeMove(m);
						allPos.add(pos);
						break;
					}
					case 'N':{
						for(Piece p: map){
							if(p.getType() == 2){
								byte loc = p.getPosition();
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
									if(loc + Position.KNIGHT_MOVES[0] == endLoc
											|| loc + Position.KNIGHT_MOVES[1] == endLoc
											|| loc + Position.KNIGHT_MOVES[2] == endLoc
											|| loc + Position.KNIGHT_MOVES[3] == endLoc
											|| loc + Position.KNIGHT_MOVES[4] == endLoc
											|| loc + Position.KNIGHT_MOVES[5] == endLoc
											|| loc + Position.KNIGHT_MOVES[6] == endLoc
											|| loc + Position.KNIGHT_MOVES[7] == endLoc){
										m = new Move(loc, getLoc(m_loc));
										break;
									}
								}
							}
						}
						pos = pos.makeMove(m);
						allPos.add(pos);
						break;
					}
					case 'B':{
						for(Piece p: map){
							if(p.getType() == 3){
								byte loc = p.getPosition();
								if(Math.abs((loc & 0xf) - getFile(m_loc)) == Math.abs((loc >> 4) - getRank(m_loc))){
									m = new Move(loc, getLoc(m_loc));
									break;
								}
							}
						}
						pos = pos.makeMove(m);
						allPos.add(pos);
						break;
					}
					case 'Q':{
						for(Piece p: map){
							if(p.getType() == 4){
								byte loc = p.getPosition();
								Move[] valid_moves = generateRQmoves(pos, loc, Position.RADIALS, false, i);
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
										for(Move valid: valid_moves){
											if(valid.getStartSquare() == loc && valid.getEndSquare() == getLoc(m_loc)){
												m = new Move(loc, getLoc(m_loc));
												break;
											}
										}
									}
								}
							}
						}
						pos = pos.makeMove(m);
						allPos.add(pos);
						break;
					}
					case 'K':{
						for(Piece p: map){
							if(p.getType() == 5){
								byte loc = p.getPosition();
								m = new Move(loc, getLoc(m_loc));
								break;
							}
						}
						pos = pos.makeMove(m);
						allPos.add(pos);
						break;
					}
				}
			}
		}
		
		Position[] positions = new Position[allPos.size()];
		positions = allPos.toArray(positions);
		return positions;
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
	
	// Removes the move numbers
	public static String getMoves(String game){
		String toReturn = "";
		String toRemove = "";
		for(int i = 1; i < 100; i++){
			toRemove = i + ". ";
			game = game.replace(toRemove, "");
		}
		toReturn  = game;
		return toReturn;
	}
	
	public static Move[] generateRQmoves(Position pos, byte c_pos, byte[] differences, boolean cont, int ind){
		LinkedList <Move> AllMoves = new LinkedList <Move> ();
		byte c_col = ind % 2 == 0 ? Piece.WHITE : Piece.BLACK, o_col = (byte)(-1*c_col);
		byte col;
		for (int i = 0; i < differences.length; i++){
			byte next_pos = (byte) (c_pos + differences[i]);
			while ((next_pos&0x88)==0){
				Piece o_pos = pos.getSquareOccupier(next_pos);
				col = o_pos.getColour();
				if (col!=c_col){
					if (col==o_col){
						AllMoves.add(new Move(c_pos, next_pos, (byte) 10));
						break;
					}
					else AllMoves.add(new Move(c_pos, next_pos));
				}
				else break;
				if (cont) break;
				next_pos += differences[i];
			}
		}
		Move[] moves = new Move[AllMoves.size()];
		moves = AllMoves.toArray(moves);
		return moves;
	}
	
	public static void main(String[] args) throws IOException {
		/*remove all blank lines*/
		BufferedReader eraser = new BufferedReader(new FileReader("PGN.txt"));
		String input = eraser.readLine();
		String rewrite = "";
		while(input != null){
			input = input.replaceAll("//s+", " ");
			if (input.compareTo(" ") != 0){
				rewrite += input + "\n";
			}
			input = eraser.readLine();
		}
		/*write to file*/
		PrintWriter write = new PrintWriter(new FileWriter("PGN.txt"));
		write.print(rewrite);
		write.close();
		
		/*Begin reading stuff*/
		BufferedReader readFile = new BufferedReader (new FileReader("PGN.txt"));
		
		String endIndicator = "";
		
		input=readFile.readLine();
		String moveText = "";
				
		while(input != null){
			System.out.println(input);
			
			if(input.indexOf("Result") != -1)
			{
				input = input.replaceAll("\\s+", "");
				
				//Tie
				if(input.indexOf("1/2-1/2") != -1){
					endIndicator = "1/2-1/2";
				}
				//1-0 white
				else if(input.indexOf("1-0") != -1){
					endIndicator = "1-0";
				}
				//0-1 black
				else if(input.indexOf("0-1") != -1){
					endIndicator = "0-1";
				}
				//on going *
			}
			else if(input.indexOf("[") == -1){
				if(input.indexOf(endIndicator) == -1){
					moveText+=input + " ";
				}
				else{
					moveText += input;
					/*get rid of numbers*/
					int counter = 1;
					while(moveText.indexOf(counter + ".") != -1){
						moveText = moveText.replaceFirst(counter + "\\.\\.\\.", "");
						moveText = moveText.replaceFirst(counter + "\\.", "");
						counter++;
					}
					moveText = moveText.replaceAll("\\s+", " ");
					moveText = moveText.substring(1, moveText.length());
					
					/*Load movetext*/
					//Position[] result = AlgebraicNotationConverter.load(moveText);
					
					/*Algorithm goes here*/
					moveText = "";
				}
			}
			
			input=readFile.readLine();
		}

		
		/*
		//String test = "e4 h6 e5 f5 exf5e.p.";
		//String test = "e4 f5 Nf3 Nf6 g4 Nxe4 Na3 Nc5 Nb5 d5 a3 d4 Nfxd4 c6 Nb3 Qd4 N5xd4 b5 Bxb5 h6 Qf3 Be6 d3 Na6 Bxh6 Rxh6 o-o-o";
		String test = "d4 Nc6 Nf3 d5 c4 dxc4 Qd2 Nxd4 Nxd4 c5 Nf5 Bxf5 Qe3 Qa5+ Nd2 Rd8 f4 g6 Qg3 f6 Qe3 Bg7 a3 Kf8 h3 h5 h4 a6 Qg1 b6 Qh2 b5 Qg1 Rd5 Qh2 Rd6 Qg3 Rh6 Qf2 Rd5 g3 e6 Qe3 Ne7 Qf2 Rh8 e3 Be4 Rg1 Kf7 Qh2 Nf5 Ke2 Bd3+ Kf3 Bc2 Nxc4 Bd1+ Be2 bxc4 Bxd1 Nxe3 Bxe3 Rd3 Rc1 Qb5 Kf2 Qxb2+ Be2 c3 Bxc5 Rc8 Rb1 Qd2 f5 Rxc5 fxe6+ Kxe6 Rb3 Qe3+ Ke1 Rd1+ Kxd1 Qd2# 0-1";
		AlgebraicNotationConverter tester = new AlgebraicNotationConverter();
		Utility util = new Utility();
		
		Position[] result = tester.load(test);
		
		for(Position p: result){
			String saveFEN = util.saveFEN(p);
			util.displayBoard(saveFEN);
			System.out.println("");
		}*/
		
		/*String test = "1. d4 Nc6 2. Nf3 d5 3. c4 dxc4 4. Qd2 Nxd4 5. Nxd4 c5 6. Nf5 Bxf5 7. Qe3 Qa5+";
		System.out.println(getMoves(test));*/
	}
}