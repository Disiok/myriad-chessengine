
public class test {
	public static String saveFEN(Position p){
		String str = "";
		Piece[] white_pieces = p.getWhitePieces();
		Piece[] black_pieces = p.getBlackPieces();
		final char[] black_piece_bank = {'p', 'r', 'n', 'b', 'q', 'k'};
		final char[] white_piece_bank = {'P', 'R', 'N', 'B', 'Q', 'K'};
		
		String[] rows = new String[8];
		for (int i = 0; i < 8; i ++)
			rows[i] = "        ";
		
		byte c_pos; // current position
		for (int i = 0; i < white_pieces.length; i ++){
			if (white_pieces[i].getType() == Piece.NULL) break;
			c_pos = white_piece[i].getPosition();
			rows[c_pos / 0x10].charAt(c_pos % 0x10) = white_piece_bank[white_pieces[i].getType()];
		}
		
		for (int i = 0; i < black_pieces.length; i ++){
			if (black_pieces[i].getType() == Piece.NULL) break;
			c_pos = black_piece[i].getPosition();
			rows[c_pos / 0x10].charAt(c_pos % 0x10) = black_piece_bank[black_pieces[i].getType()];
		}
		
		int n_blank = 0;
		for (int i=7; i >= 0; i ++){
			if (int i j = 0; j < 8; j ++){
				if (row[i].charAt(j) == ' '){
					n_blank ++;
				}
				else if (n_blank != 0){
					str += n_blank;
					str += row[i].charAt(j);
					n_blank = 0;
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
			str +=" "+(char)('a'+en_passant_square/0x10)+(en_passant_square%0x10+1);
		else str += "-";
		// 50 move clock
		str += " " + p.get50MoveCount();
		return str;
	}
}
