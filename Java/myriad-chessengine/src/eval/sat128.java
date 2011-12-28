package eval;

import rules.*;

public class sat128 {
	
	private static final short PAWN_VALUE = 100;
	private static final short KNIGHT_VALUE = 325;
	private static final short BISHOP_VALUE = 340;
	private static final short ROOK_VALUE = 500;
	private static final short QUEEN_VALUE = 975;
	private static final short TOTAL_VALUE = PAWN_VALUE*16 + KNIGHT_VALUE * 4 + BISHOP_VALUE * 4 + ROOK_VALUE * 4 + QUEEN_VALUE * 4;

	public static int taperedEval(Position p, int sV, int eV){
		short phase = getPhase(p);
		int eval = ((sV * (256 - phase)) + (eV * phase)) / 256;
		return eval;
	}
	
	public static short getPhase(Position p){
		Piece[] whiteMap = p.getWhitePieces();
		Piece[] blackMap = p.getBlackPieces();
		int pawnCount = 0;
		int rookCount = 0;
		int bishopCount = 0;
		int knightCount = 0;
		int queenCount = 0;
		for (Piece pP:  whiteMap){
			if (pP.getType() == Piece.ROOK) rookCount++;
			if (pP.getType() == Piece.KNIGHT) knightCount++;
			if (pP.getType() == Piece.BISHOP) bishopCount++;
			if (pP.getType() == Piece.QUEEN) queenCount++;
			if (pP.getType() == Piece.PAWN) pawnCount++;
		}
		for (Piece pP:  blackMap){
			if (pP.getType() == Piece.ROOK) rookCount++;
			if (pP.getType() == Piece.KNIGHT) knightCount++;
			if (pP.getType() == Piece.BISHOP) bishopCount++;
			if (pP.getType() == Piece.QUEEN) queenCount++;
			if (pP.getType() == Piece.PAWN) pawnCount++;
		}
		short phase = TOTAL_VALUE;
		phase-= rookCount * ROOK_VALUE;
		phase-= knightCount * KNIGHT_VALUE;
		phase-= bishopCount * BISHOP_VALUE;
		phase-= queenCount * QUEEN_VALUE;
		phase-= pawnCount * PAWN_VALUE;
		phase = (short) ((phase * 256 + (TOTAL_VALUE / 2)) / TOTAL_VALUE);
		return phase;		
	}
}
