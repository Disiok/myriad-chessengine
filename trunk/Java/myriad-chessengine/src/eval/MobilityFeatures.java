package eval;

import java.util.Arrays;

import rules.*;
import eval.FeatureManager.Feature;

/**
 * Test with the following code snippet:
 * 	String FEN ="some FEN string";
	Position p = FenUtility.loadFEN(FEN);
	MobilityFeatures ft = new MobilityFeatures(new Feature (p, new FeatureManager(p)));
	String s = ft.detectControlSquares();
 * @author Jesse Wang
 *
 */
public class MobilityFeatures extends Feature {
	// relative exchange values used for control squares only
	private static final int king = Integer.MAX_VALUE;
	private static final int queen = 9;
	private static final int rook = 5;
	private static final int minor = 3;
	private static final int pawn = 1;

	public MobilityFeatures(Feature bf) {
		super(bf);
	}
	public String detectTrappedPieces (){
		// TODO: Find trapped pieces for both colours
		return null;
	}
	public String detectKnightFutures (){
		// TODO: Find knight futures for both colours
		return null;
	}
	public String detectControlSquares (){
		String w_pieces [] = new String [0x79], b_pieces [] = new String [0x79];
		Piece [] w_map = original_position.getWhitePieces(), b_map = original_position.getBlackPieces();
		String w_cntrl = "", b_cntrl = "";
		boolean isOnMove = original_position.isWhiteToMove();
		for (int i = 0; i < 0x79 ; i++) {
			w_pieces[i] = "";
			b_pieces[i] = "";
		}
		updateMap(w_pieces, w_map, original_position, true);
		updateMap(b_pieces, b_map, original_position, false);
		for (int i = 0; i < 8; i++){
			for (int j = 0; j < 8; j++){
				byte sq = (byte) (i*0x10 + j);
				char [] w_attack = w_pieces[sq].toCharArray(), b_attack = b_pieces[sq].toCharArray();
				Arrays.sort(w_attack);
				Arrays.sort(b_attack);
				int res = doBattle(w_attack, b_attack, isOnMove);
				if (res == 1) w_cntrl += "," + Move.x88ToString(sq);
				if (res == -1) b_cntrl += "," + Move.x88ToString(sq);
			}
		}
		return w_cntrl.substring(1) + "|" + b_cntrl.substring(1);
	}
	private static int doBattle (char [] w_attack, char [] b_attack, boolean toMove){
		int w_length = w_attack.length;
		int b_length = b_attack.length;
		if (w_length == 0){
			if (b_length == 0)return 0;
			else return -1;
		}
		else if (b_length == 0) return 1;
		
		int b_index = 0; 	int w_index = 0;
		int b_loss = 0;		int w_loss = 0;
		
		if (toMove){
			while (true){
				if (b_length > b_index){
					w_loss += switchVal(w_attack[w_index]);
					w_index++;
					if (w_length > w_index){
						b_loss += switchVal(b_attack[b_index]);
						b_index++;
						if (b_loss < w_loss){ // below: compare values eaten
							return -1; // black wins
						}
						else if(w_loss < b_loss){
							if (b_length > b_index){ // white wins, but what if black has more to counter
								if ((w_loss + switchVal(w_attack[w_index])) < b_attack[b_index-1]){
									return 1; // white wins even if black attacks once more 
								}
							}
						}
					}
					else{ 
						return -1; // black wins, white has nothing to eat it
					}
				}
				else {
					return 1; // white wins, black has nothing to eat it
				}
			}
		}
		else{
			while(true){
				if (w_length > w_index){
					b_loss += switchVal(b_attack[b_index]);
					b_index++;
					if (b_length > b_index){
						w_loss += switchVal(w_attack[w_index]);
						w_index++;
						if (w_loss < b_loss){ // below: compare values eaten
							return 1; // white wins
						}
						else if(b_loss < w_loss){
							if (w_length > w_index){ // black wins, but what if white has more to counter
								if ((b_loss + switchVal(b_attack[b_index])) < w_attack[w_index-1]){
									return -1; // black wins even if white attacks once more 
								}
							}
						}
					}
					else{ 
						return 1; // white wins, white has nothing to eat it
					}
				}
				else {
					return -1; // black wins, black has nothing to eat it
				}
			}
		}
		
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
				if (col){
					n_loc = c_loc + Position.LEFT_UP_MOVE;
					map[(n_loc & 0x88) == 0 ? n_loc: 0x78] += "a";
					n_loc = c_loc + Position.RIGHT_UP_MOVE;
					map[(n_loc & 0x88) == 0 ? n_loc: 0x78] += "a";
				}
				else{
					n_loc = c_loc + Position.LEFT_DOWN_MOVE;
					map[(n_loc & 0x88) == 0 ? n_loc: 0x78] += "a";
					n_loc = c_loc + Position.RIGHT_DOWN_MOVE;
					map[(n_loc & 0x88) == 0 ? n_loc: 0x78] += "a";
				}
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
						map [n_loc] += "b"; 
						Piece obstruct = p.getSquareOccupier((byte)n_loc);
						if (obstruct.getColour() == o_col) break;
						else if ((type = obstruct.getType()) != Piece.NULL) {
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
				for (byte d: Position.RADIALS){
					n_loc = c_loc + d;
					map [(n_loc & 0x88) == 0 ? n_loc : 0x78] += "e";
				}
				break;
			}
		}
	}
}
