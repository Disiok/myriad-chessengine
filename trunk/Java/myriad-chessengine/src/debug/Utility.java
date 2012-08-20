package debug;

import java.io.*;

import rules.*;

/**
 * This class is the FEN Utility used by the debug class. Methods here convert a
 * compressed FEN String into a Position object and visa versa. Note: FEN =
 * Forsynth-Edwards Notation.
 * 
 * @author Andy Huang, Simon Suo
 */
public class Utility {
	/**
	 * Converts a FEN representation of a Position object into a Position
	 * object.
	 * 
	 * @param fen
	 *            The FEN Representation of a Position Object.
	 * @return A Position object as described by the FEN string.
	 */
	public static Position loadFEN(String fen) {
		String[] fenBoard = fen.split(" "), rank = fenBoard[0].split("/");
		int charactersPerSet = 8, fileNumber = 0;
		int wp_count = 0, bp_count = 0, n_blank;
		Piece[] w_map = new Piece[16], b_map = new Piece[16];
		String c_rank;
		for (int i = 0; i < 8; i++) {
			fileNumber = 0;
			c_rank = rank[i];
			charactersPerSet = 8;
			for (int j = 0; j < charactersPerSet; j++) {
				byte loc = (byte) ((7 - i) * 0x10 + fileNumber), type = -1, color = -1;
				Piece piece = null;
				boolean addWhite = false;
				if (Character.isDigit(c_rank.charAt(j))) {
					n_blank = c_rank.charAt(j) - 48;
					charactersPerSet -= n_blank - 1;
					fileNumber += n_blank;
				} else {
					color = Character.isUpperCase(c_rank.charAt(j)) ? Piece.WHITE : Piece.BLACK;
					addWhite = Character.isUpperCase(c_rank.charAt(j)) ? true : false;
					switch (c_rank.charAt(j)) {
					case 'p': case 'P': type = Piece.PAWN; break;
					case 'r': case 'R': type = Piece.ROOK; break;
					case 'n': case 'N': type = Piece.KNIGHT; break;
					case 'b': case 'B': type = Piece.BISHOP; break;
					case 'q': case 'Q': type = Piece.QUEEN; break;
					case 'k': case 'K': type = Piece.KING; break;
					}
					piece = new Piece(loc, type, color);
					if (addWhite) {
						w_map[wp_count] = piece;
						wp_count++;
					} else {
						b_map[bp_count] = piece;
						bp_count++;
					}
					fileNumber++;
				}
			}
		}
		for (int i = wp_count; i < 16; i++) w_map[i] = Piece.getNullPiece();
		for (int i = bp_count; i < 16; i++) b_map[i] = Piece.getNullPiece();
		for (int i = 0; i < 16; i++) {
			if (w_map[i].getType() == Piece.KING) {
				Piece temp = w_map[0];
				w_map[0] = w_map[i];
				w_map[i] = temp;
			}
			if (b_map[i].getType() == Piece.KING) {
				Piece temp = b_map[0];
				b_map[0] = b_map[i];
				b_map[i] = temp;
			}
		}
		boolean whiteMove = fenBoard[1].equals("w") ? true : false;
		boolean[] castleRights = new boolean[] { false, false, false, false };
		String castle = fenBoard[2];
		for (int i = 0; i < castle.length(); i++) {
			if (castle.charAt(i) == 'K') castleRights[0] = true;
			else if (castle.charAt(i) == 'k') castleRights[1] = true;
			else if (castle.charAt(i) == 'Q') castleRights[2] = true;
			else if (castle.charAt(i) == 'q') castleRights[3] = true;
		}
		String enPassant = fenBoard[3];
		byte ensq;
		if (enPassant.equalsIgnoreCase("-")) ensq = -1;
		else ensq = (byte) ((enPassant.charAt(0) - 'a') + (enPassant.charAt(1) - 1) * 0x10);
		byte fiftyMove = 0;
		short half_move_clock = 0;
		if (fenBoard.length == 5) {
			fiftyMove  = (byte) Integer.parseInt(fenBoard[4]);
			half_move_clock = (short)(Integer.parseInt(fenBoard[5])*2);
		}
		return new Position(fiftyMove, ensq, castleRights, whiteMove, w_map, b_map, half_move_clock);
	}

	/**
	 * Converts a Position object into a FEN String that represents the object.
	 * Note: since the full-move clock is not used in the Position object, the
	 * full-move clock will not be stored in the FEN string.
	 * 
	 * @param p
	 *            The position to convert into FEN string.
	 * @return A string representing the FEN version of the board.
	 */
	public static String saveFEN(Position p) {
		String toReturn = "", toAdd = "";
		for (int i = 7; i >= 0; i--) {
			int n_blank = 0;
			for (int j = 0; j < 8; j++) {
				byte c_sq = (byte) (i * 0x10 + j);
				Piece o_pos = p.getSquareOccupier(c_sq);
				if (o_pos.isEqual(Piece.getNullPiece())) {
					n_blank++;
					if (j == 7)
						toReturn += n_blank;
				} else {
					if (n_blank != 0) {
						toReturn += n_blank;
						n_blank = 0;
					}
					byte col = o_pos.getColour(), type = o_pos.getType();
					switch (type) {
					case Piece.PAWN:
						toAdd = "p";
						break;
					case Piece.ROOK:
						toAdd = "r";
						break;
					case Piece.KNIGHT:
						toAdd = "n";
						break;
					case Piece.BISHOP:
						toAdd = "b";
						break;
					case Piece.QUEEN:
						toAdd = "q";
						break;
					case Piece.KING:
						toAdd = "k";
						break;
					}
					if (col == Piece.WHITE)
						toAdd = toAdd.toUpperCase();
					toReturn += toAdd;
				}
			}
			if (i != 0)
				toReturn += "/";
		}
		toReturn += " " + (p.isWhiteToMove() ? "w" : "b") + " ";
		String castling = "";
		boolean[] castle = p.getCastlingRights();
		if (castle[0])
			castling += "K";
		if (castle[2])
			castling += "Q";
		if (castle[1])
			castling += "k";
		if (castle[3])
			castling += "q";
		toReturn += (castling.equals("") ? "-" : castling) + " ";
		byte en_passant_square = p.getEnPassantSquare();
		if (en_passant_square != -1)
			toReturn += Move.x88ToString(en_passant_square);
		else
			toReturn += "-";
		toReturn += " " + p.get50MoveCount();
		return toReturn;
	}

	/**
	 * Saves the FENPlus string, which consists of a sequence of moves, the
	 * colour that the AI is playing in as well as the current moveNumber. The
	 * output uses comma separated values.
	 * 
	 * @param ai_colour
	 *            The colour that Myriad XSN played during the game.
	 * @param moveList
	 *            The list of moves made during the course of the game.
	 * @return A FENPlus string as described above.
	 */
	public static String saveFENPlus(boolean ai_colour, String moveList) {
		return ai_colour + "," + moveList;
	}

	/**
	 * Prints to the console a text based graphical representation of the
	 * position object with the given FEN string.
	 * 
	 * @param fen
	 *            The FEN string representing the Position object.
	 */
	public static void displayBoard(String fen) {
		String[] fenBoard = fen.split(" ");
		String[] rank = fenBoard[0].split("/");
		boolean onMove = fenBoard[1].charAt(0) == 'w';
		int counter = 0;
		System.out.println("  a b c d e f g h");
		for (int k = 0; k < rank.length; k++) {
			System.out.print((8 - counter) + " ");
			for (int i = 0; i < rank[k].length(); i++) {
				char ch = rank[k].charAt(i);
				if (ch < '9' && ch > '0')
					for (int j = 0; j < (ch - '0'); j++)
						System.out.print("_ ");
				else
					System.out.print(ch + " ");
			}
			if (k == 7 && onMove)
				System.out.print(" o");
			if (k == 0 && !onMove)
				System.out.print(" o");
			System.out.println();
			counter++;
		}
	}
	public static void displayMoves(Position p){
		Move[] m = p.generateAllMoves();
		int length = m.length;
		for (int i = 0 ; i < length; i++){
			System.out.print(m[i].toString(p) + (i % 5 == 4? "\n" : "\t"));
		}
		System.out.print((length-1) % 5 == 4?"":"\n");
		System.out.println("Valid moves: " + length);
	}
	public static void printInfo(Position p, Move m){
		System.out.println("-------------------");
		System.out.println("Prior Move: " + m.toString(p));
		System.out.println(p.isInCheck(true)?"In check": "Not in check");
		String FEN = saveFEN(p);
		System.out.println(FEN);
		Utility.displayBoard(FEN);
		Utility.displayMoves(p);
		System.out.println("-------------------");
	}

	/**
	 * Writes a string to a file.
	 * 
	 * @param file
	 *            The name of the file to write to.
	 * @param text
	 *            The text to write to the file.
	 * @throws IOException
	 *             If any file errors occur, e.g. file unwritable.
	 */
	public static void write(String file, String text) throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(file));
		out.write(text);
		out.close();
	}

	/**
	 * Reads 1 line of a string from a file.
	 * 
	 * @param file
	 *            The name of the file to read from.
	 * @throws IOException
	 *             If any file errors occur, e.g. file unreadable.
	 */
	public static String read(String file) throws IOException {
		String text = null;
		BufferedReader in = new BufferedReader(new FileReader(file));
		text = in.readLine();
		in.close();
		return text;
	}
}