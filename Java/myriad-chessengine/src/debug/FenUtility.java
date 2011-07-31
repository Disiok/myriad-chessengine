package debug;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

import rules.*;
/**
 * This class is the FEN Utility used by the debug class. Methods here convert a compressed FEN String
 * into a Position object and visa versa. Note: FEN = Forsynth-Edwards Notation.
 * @author Andy Huang, Simon Suo
 */
public class FenUtility {
	/**
	 * Converts a FEN representation of a Position object into a Position object.
	 * @param fen The FEN Representation of a Position Object.
	 * @return A Position object as described by the FEN string.
	 */
	public static Position loadFEN(String fen) {
		String[] fenBoard = fen.split(" ");
		String[] rank = fenBoard[0].split("/");
		int charactersPerSet = 8, fileNumber = 0;
		int wp_count = 0, bp_count = 0, n_blank;
		Piece[] w_map = new Piece[16], b_map = new Piece[16];
		String c_rank;
		for(int i = 0; i < 8;i++) {
			fileNumber = 0;
			c_rank = rank[i];
			charactersPerSet = 8;
			for(int j = 0; j < charactersPerSet; j++){
				byte loc = (byte) ((7-i) * 0x10 + fileNumber);
				byte type = -1, color = -1;
				Piece piece = null;
				boolean addWhite = false;
				if(Character.isDigit(c_rank.charAt(j))) {
					n_blank = c_rank.charAt(j) - 48;
					charactersPerSet -= n_blank - 1;
					fileNumber += n_blank;
				}
				else {
					color = Character.isUpperCase(c_rank.charAt(j))? Piece.WHITE : Piece.BLACK;
					addWhite = Character.isUpperCase(c_rank.charAt(j))? true: false;
					switch (c_rank.charAt(j)){
						case 'p': case 'P': type = Piece.PAWN; break;
						case 'r': case 'R': type = Piece.ROOK; break;
						case 'n': case 'N': type = Piece.KNIGHT; break;
						case 'b': case 'B': type = Piece.BISHOP; break;
						case 'q': case 'Q': type = Piece.QUEEN; break;
						case 'k': case 'K': type = Piece.KING; break;
					}
					piece = new Piece (loc, type, color);
					if(addWhite){
						w_map[wp_count] = piece;
						wp_count ++;
					}
					else{
						b_map[bp_count] = piece;
						bp_count++;
					}
					fileNumber++;
				}       
			}
		}
		for(int i = wp_count; i < 16; i++) 	w_map[i] = Piece.getNullPiece();      
		for(int i = bp_count; i < 16; i++)	b_map[i] = Piece.getNullPiece();
		boolean whiteMove = fenBoard[1].equals("w") ? true : false;
		boolean[] castleRights = new boolean[]{false, false, false, false};
		String castle = fenBoard[2];
		for(int i = 0; i < castle.length(); i ++){
			if(castle.charAt(i) == 'K')castleRights[0] = true;
			else if(castle.charAt(i) == 'k')castleRights[1] = true;
			else if(castle.charAt(i) == 'Q')castleRights[2] = true;
			else if(castle.charAt(i) == 'q')castleRights[3] = true;
		}
		String enPassant = fenBoard[3];
		byte ensq;
		if (enPassant.equalsIgnoreCase("-")) ensq = -1;
		else ensq = (byte)((enPassant.charAt(0) - 'a') + (enPassant.charAt(1)-1)*0x10);
		byte fiftyMove = (byte) Integer.parseInt(fenBoard[4]);
		return new Position(fiftyMove, ensq, castleRights, whiteMove, w_map, b_map);
	}
	/**
	 * Converts a Position object into a FEN String that represents the object. Note: since
	 * the full-move clock is not used in the Position object, the full-move clock will not
	 * be stored in the FEN string.
	 * @param p The position to convert into FEN string.
	 * @return A string representing the FEN version of the board.
	 */
	public static String saveFEN(Position p){
		String str = "";
		String toAdd = "";
		// piece positions
		for (int i = 7 ; i >= 0 ; i --){
			int n_blank = 0;
			for (int j = 0; j < 8 ; j ++){
				byte c_sq = (byte)(i * 0x10 + j);
				Piece o_pos = p.getSquareOccupier(c_sq);
				if (o_pos.isEqual(Piece.getNullPiece())){
					n_blank ++;
					if (j == 7) str += n_blank;
				}
				else {
					if (n_blank != 0){
						str += n_blank;
						n_blank = 0;
					}
					byte col = o_pos.getColour();
					byte type = o_pos.getType();
					switch (type){
						case Piece.PAWN: toAdd = "p"; break;
						case Piece.ROOK: toAdd = "r"; break;
						case Piece.KNIGHT: toAdd = "n"; break;
						case Piece.BISHOP: toAdd = "b"; break;
						case Piece.QUEEN: toAdd = "q"; break;
						case Piece.KING: toAdd = "k"; break;
					}
					if (col == Piece.WHITE) toAdd = toAdd.toUpperCase();
					str += toAdd;
				}

			}
			if (i != 0) str += "/";
		}
		// castling fields
		str += " " + (p.isWhiteToMove() ? "w" : "b") + " ";
		String castling = "";
		boolean[] castle = p.getCastlingRights();
		if (castle[0]) castling += "K";
		if (castle[2]) castling += "Q";
		if (castle[1]) castling += "k";
		if (castle[3]) castling += "q";
		str += (castling.equals("") ? "-" : castling) + " ";
		// en passant
		byte en_passant_square = p.getEnPassantSquare();
		if (en_passant_square != -1)
			str +=""+(char)('a'+en_passant_square%0x10)+(en_passant_square/0x10+1);
		else str += "-";
		// 50 move clock
		str += " " + p.get50MoveCount();
		return str;
	}
	public static String saveFENPlus(Position p, boolean ai_colour, int moveNumber,LinkedList <Move> gamePlay){
		String FENPlus = saveFEN(p);
		FENPlus += "," + ai_colour + "," + moveNumber + ",";
		for (Move m : gamePlay){
			FENPlus += m.toString(p) + "/";
		}
		return FENPlus;
	}
	/**
	 * Convert a single FEN string into a more graphical representation of the board
	 * Simply used for debugging purpose
	 * @param fen The FEN string
	 */
	public static void displayBoard(String fen){
		String[] fenBoard = fen.split(" ");
		String[] rank = fenBoard[0].split("/");
		int c = 0;
		System.out.println("  a b c d e f g h");
		for (String c_rank : rank){
			System.out.print((8-c)+" ");
			for (int i = 0 ; i < c_rank.length(); i++){
				char ch = c_rank.charAt(i);
				if (ch < '9' && ch > '0')
					for (int j = 0 ; j < (ch-'0'); j++)
						System.out.print("_ ");
				else
					System.out.print(ch+" ");
			}
			System.out.println();
			c ++;
		}
	}
	public static void write(String file, String text) throws IOException{
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			out.write(text);
			out.close();
	}
	public static String read(String file) throws IOException{
		String text = null;
		BufferedReader in = new BufferedReader(new FileReader(file));
		text = in.readLine();
		in.close();
		return text;
	}
	public static byte switchSqRep(String sq){
		return (byte) (sq.charAt(0)-'a' + (sq.charAt(1) - '1') * 0x10);
	}
	public static String switchSqRep(byte sq){
		return ""+(char)('a'+sq % 0x10)+(sq / 0x10+1);
	}
}
