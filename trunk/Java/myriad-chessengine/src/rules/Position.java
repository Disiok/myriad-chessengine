   package rules;

   import java.util.*;
/** test for subeclipse */
/**
 * Myriad's representation of a particular position. This is a basic class that underlines the
 * properties of a position, such as the availability of castling, 50 move rule count, etc.
 * 
 * The piece-centric board representation is used, with coordinates used by the "0x88"
 * algorithm. Note that this is an immutable object. Once an object is created via a constructor
 * it cannot be changed!
 * @author Spork Innovation Technologies
 */
   public final class Position
   {
   //----------------------Instance Variables----------------------
   /**
    * Counts the number of moves since the last pawn move or capture.
    */
      private byte fifty_move_rule_count;
   /**
    * A flag describing the future availability of white's castling kingside. E.g. whether 
    * or not the king or rooks have already moved. This does not mean that castling is a 
    * legal move in <i>this</i> position.
    */
      private boolean white_k_side_castling_allowed;
   /**
    * A flag describing the future availability of white's castling queenside. E.g. whether 
    * or not the king or rooks have already moved. This does not mean that castling is a 
    * legal move in <i>this</i> position.
    */
      private boolean black_k_side_castling_allowed;
   /**
    * A flag describing the future availability of black's castling kingside. E.g. whether 
    * or not the king or rooks have already moved. This does not mean that castling is a 
    * legal move in <i>this</i> position.
    */
      private boolean white_q_side_castling_allowed;
   /**
    * A flag describing the future availability of black's castling queenside. E.g. whether 
    * or not the king or rooks have already moved. This does not mean that castling is a 
    * legal move in <i>this</i> position.
    */
      private boolean black_q_side_castling_allowed;
   /**
    * A byte describing the location of the "en passant" square in 0x88 coordinates. This 
    * value is -1 if there is no "en passant" square available. 
    */
      private byte en_passant_square;
   /**
    * A flag describing whose turn it is to move.
    */
      private boolean is_White_to_Move;
   /**
    * Stores the current location of all the white pieces on the board.
    */
      private Piece[] white_map;
   /**
    * Stores the current location of all the white pieces on the board.
    */
      private Piece[] black_map;
   //----------------------End of Instance Variables----------------------
   
   //----------------------Constants----------------------
   /** The distance between 1 up move. */
      private static final byte UP_MOVE = 0x10;
   /** The distance between 1 down move. */
      private static final byte DOWN_MOVE = -0x10;
   /** The distance between 1 left move. */
      private static final byte LEFT_MOVE = -0x01;
   /** The distance between 1 right move. */
      private static final byte RIGHT_MOVE = 0x01;
   /** The distance between 1 diagonal left and up move. */
      private static final byte LEFT_UP_MOVE = 0xf;
   /** The distance between 1 diagonal right and up move. */
      private static final byte RIGHT_UP_MOVE = 0x11;
   /** The distance between 1 diagonal left and down move. */
      private static final byte LEFT_DOWN_MOVE = -0x11;
   /** The distance between 1 diagonal right and down move.*/
      private static final byte RIGHT_DOWN_MOVE = -0xf;
   /** The storage for the differences of all knight moves. */
      private static final byte [] KNIGHT_MOVES = {2*UP_MOVE+RIGHT_MOVE,2*UP_MOVE+LEFT_MOVE,
         2*DOWN_MOVE+RIGHT_MOVE, 2*DOWN_MOVE+LEFT_MOVE, 2*RIGHT_MOVE+UP_MOVE, 2*RIGHT_MOVE+DOWN_MOVE,
         2*LEFT_MOVE+UP_MOVE, 2*LEFT_MOVE+DOWN_MOVE};
   /** The storage for the differences of all diagonal moves. */
      private static final byte [] DIAGONALS = {RIGHT_UP_MOVE, RIGHT_DOWN_MOVE, LEFT_UP_MOVE,
         RIGHT_DOWN_MOVE};
   /** The storage for the differences of all horizontal/vertical moves.*/
      private static final byte [] HORIZONTALS = {UP_MOVE, DOWN_MOVE, LEFT_MOVE, RIGHT_MOVE};
   /** The signal given by the gameResult() method that means a draw (or stalemate).*/ 
      private static final int DRAW = 0;
   /** The signal given by the gameResult() method that means white wins.*/
      private static final int WHITE_WINS = 1;
   /** The signal given by the gameResult() method that means black wins.*/
      private static final int BLACK_WINS = -1;
   /** The signal given by the gameResult() method that means no result has been reached yet.*/
      private static final int NO_RESULT = -2;
   //----------------------End of Constants----------------------
   
   //----------------------Constructors----------------------
   /**
    * Constructor: Constructs a board objects with the following parameters:
    * @param fifty_move The 50 move rule counter.
    * @param three_fold The 3 fold repetition counter.
    * @param epsq The en passant square.
    * @param castling_rights An array storing the castling rights, with index 0 being white
    * to the kingside, 1 being black to the kingside, 2 being white to the queenside, 3 being
    * black to the queenside.
    * @param whiteturn If it is currently white to move.
    * @param w_map An array containing all the current white pieces.
    * @param b_map An array containing all the current black pieces.
    */
      public Position (byte fifty_move, byte epsq, boolean [] castling_rights, 
      	boolean whiteturn, Piece[] w_map, Piece[] b_map){
         fifty_move_rule_count = fifty_move;
         en_passant_square = epsq;
         white_k_side_castling_allowed = castling_rights[0];
         black_k_side_castling_allowed = castling_rights[1];
         white_q_side_castling_allowed = castling_rights[2];
         black_q_side_castling_allowed = castling_rights[3];
         white_map = Arrays.copyOf(w_map, w_map.length);
         black_map = Arrays.copyOf(b_map, w_map.length);
         is_White_to_Move = whiteturn;
      }
   /**
    * Default Constructor: Constructs a Position object with the same settings as the initial
    * start-up position.
    */
      public Position (){
         fifty_move_rule_count = 0;
         en_passant_square = -1;
         white_k_side_castling_allowed = true;
         black_k_side_castling_allowed = true;
         white_q_side_castling_allowed = true;
         black_q_side_castling_allowed = true;
         is_White_to_Move = true;
         white_map = new Piece[16];
         black_map = new Piece[16];
         for (int i = 0; i < 8; i ++){
            white_map[i] = new Piece ((byte)(0x10+i),Piece.PAWN,Piece.WHITE);
            black_map[i] = new Piece ((byte)(0x60+i),Piece.PAWN,Piece.BLACK);
         }
         for (int i = 8; i < 13; i ++){
            white_map[i] = new Piece ((byte)(0x00+i-8),(byte)(Piece.ROOK+i-8),Piece.WHITE);
            black_map[i] = new Piece ((byte)(0x70+i-8),(byte)(Piece.ROOK+i-8),Piece.BLACK);
         }
         for (int i = 13; i < 16; i++){
            white_map[i] = new Piece ((byte)(0x05+i-13),(byte)(Piece.BISHOP-i+13),Piece.WHITE);
            black_map[i] = new Piece ((byte)(0x75+i-13),(byte)(Piece.BISHOP-i+13),Piece.BLACK);
         }
      }
   //----------------------End of Constructors----------------------
   
   //----------------------Methods----------------------
   /**
    * Gets the castling rights of a board in the order specified in the Constructor.
    * @returns The castling rights of this position.
    */
      public boolean [] getCastlingRights (){
         boolean [] toReturn = new boolean [4];
         toReturn[0] = white_k_side_castling_allowed;
         toReturn[1] = black_k_side_castling_allowed;
         toReturn[2] = white_q_side_castling_allowed;
         toReturn[3] = black_q_side_castling_allowed;
         return toReturn;
      }
   /**
    * Returns <i>this</i> position's current 50 move rule counter.
    * @return The 50 move rule counter.
    */
      public byte get50MoveCount(){
         return fifty_move_rule_count;
      }
   /**
    * Returns the "en passant-able" square using 0x88 cooridinates in <i>this</i> position.
    * @return The en passant square in 0x88 coordinates.
    */
      public byte getEnPassantSquare(){
         return en_passant_square;
      }
   /**
    * Returns whether or not if it is white to play in <i>this</i> position.
    * @return A boolean signalling whether or not it is white's turn.
    */
      public boolean isWhiteToMove (){
         return is_White_to_Move;
      }
   /**
    * Returns an array containing all the white pieces.
    * @return an array containing all the white pieces.
    */
      public Piece [] getWhitePieces (){
         return Arrays.copyOf(white_map,white_map.length);
      }
   /**
    * Returns an array containing all the black pieces.
    * @return an array containing all the black pieces.
    */
      public Piece [] getBlackPieces (){
         return Arrays.copyOf(black_map,black_map.length);
      }
   /**
    * Generates all the moves possible in this Position object. This method does so by generating
    * all the moves according to the pieces and filters out all moves that result in a check.
    * @return An array containing all the legal moves in this position.
    */
      public Move[] generateAllMoves (){
         Piece[] current_map = is_White_to_Move ? white_map : black_map;
         Vector <Move> all_moves = new Vector <Move> (20,3);
         for (Piece current_piece : current_map){
            byte c_type = current_piece.getType();
            byte c_pos = current_piece.getPosition();
            byte next_pos;
            Piece o_pos;
            switch (c_type){
               case Piece.PAWN:
                  if (is_White_to_Move){
                     next_pos = (byte) (c_pos + UP_MOVE);
                     o_pos = getSquareOccupier(next_pos);
                     if (o_pos.getColour()== -1 &&(next_pos&0x88)==0){
                        if (next_pos / 0x10 == 0x07){
                           all_moves.add(new Move(c_pos,next_pos,(byte)6));
                           all_moves.add(new Move(c_pos,next_pos,(byte)7));
                           all_moves.add(new Move(c_pos,next_pos,(byte)8));
                           all_moves.add(new Move(c_pos,next_pos,(byte)9));
                        }
                        else all_moves.add(new Move(c_pos,next_pos));
                        next_pos = (byte) (c_pos + 2*UP_MOVE);
                        o_pos = getSquareOccupier(next_pos);
                        if (o_pos.getColour()!=Piece.WHITE&&(c_pos/0x10 == 0x1))
                           all_moves.add(new Move(c_pos,next_pos));
                     }
                     next_pos = (byte) (c_pos + RIGHT_UP_MOVE);
                     o_pos = getSquareOccupier(next_pos);
                     if (o_pos.getColour()==Piece.BLACK||next_pos==en_passant_square)
                        all_moves.add(new Move(c_pos, next_pos,(byte)5));
                     next_pos = (byte) (c_pos + LEFT_UP_MOVE);
                     o_pos = getSquareOccupier(next_pos);
                     if (o_pos.getColour()==Piece.BLACK||next_pos==en_passant_square)
                        all_moves.add(new Move(c_pos, next_pos,(byte)5));
                  }
                  else{
                     next_pos = (byte) (c_pos + DOWN_MOVE);
                     o_pos = getSquareOccupier(next_pos);
                     if (o_pos.getColour()!=Piece.BLACK && (next_pos&0x88)==0) {
                        if (next_pos/0x10 == 0x00){
                           all_moves.add(new Move(c_pos,next_pos,(byte)6));
                           all_moves.add(new Move(c_pos,next_pos,(byte)7));
                           all_moves.add(new Move(c_pos,next_pos,(byte)8));
                           all_moves.add(new Move(c_pos,next_pos,(byte)9));
                        }
                        else all_moves.add(new Move(c_pos,next_pos));
                        next_pos = (byte) (c_pos + 2*DOWN_MOVE);
                        o_pos = getSquareOccupier(next_pos);
                        if (o_pos.getColour()!=Piece.BLACK && (c_pos/0x10==0x7)) 
                           all_moves.add(new Move(c_pos,next_pos));
                     }
                  
                     next_pos = (byte) (c_pos + LEFT_DOWN_MOVE);
                     o_pos = getSquareOccupier(next_pos);
                     if ((o_pos.getColour()==Piece.WHITE)||next_pos==en_passant_square)
                        all_moves.add(new Move(c_pos, next_pos,(byte)5));
                     next_pos = (byte) (c_pos + RIGHT_DOWN_MOVE);
                     o_pos = getSquareOccupier(next_pos);
                     if ((o_pos.getColour()==Piece.WHITE)||next_pos==en_passant_square)
                        all_moves.add(new Move(c_pos, next_pos,(byte)5));
                  }
                  break;
               case Piece.ROOK:
                  all_moves.addAll(generatePieceMoves(c_pos, HORIZONTALS, false));
                  break;
               case Piece.KNIGHT:
                  all_moves.addAll(generatePieceMoves(c_pos, KNIGHT_MOVES, true));
                  break;
               case Piece.BISHOP:
                  all_moves.addAll(generatePieceMoves(c_pos, DIAGONALS, false));
                  break;
               case Piece.QUEEN:
                  all_moves.addAll(generatePieceMoves(c_pos, HORIZONTALS, false));
                  all_moves.addAll(generatePieceMoves(c_pos, DIAGONALS, false));
                  break;
               case Piece.KING:
                  all_moves.addAll(generatePieceMoves(c_pos, HORIZONTALS, true));
                  all_moves.addAll(generatePieceMoves(c_pos, DIAGONALS, true));
                  if (!isInCheck()){
                     boolean[] castle_rights = getCastlingRights();
                     for (int i = 0 ; i < 4; i++){
                        boolean can_castle = castle_rights[i];
                        int n_sqr = i < 2? 2 : 3;
                        int diff = i < 2? LEFT_MOVE : RIGHT_MOVE;
                        if (can_castle){
                        // FIXME: Jesse thinks it is more efficient if you just edited 
                        // the king piece to be at next_pos and called isInCheck() instead.
                           if ((is_White_to_Move && i%2 == 0)||((!is_White_to_Move) && i%2==1)){
                              next_pos = c_pos;
                              for (int j = 0 ; j < n_sqr; j++){
                                 next_pos = (byte) (next_pos + diff);
                                 if (!(getSquareOccupier(next_pos).isEqual(Piece.getNullPiece())&&
                                 !isMoveResultInCheck(new Move(c_pos, next_pos)))){
                                    can_castle = false;
                                    break;
                                 }
                              }
                           }
                           else can_castle = false;
                        }
                        if (can_castle) all_moves.add(Move.CASTLING[i]);
                     }
                  }
                  break;
            }
         }
         for (Move m: all_moves)
         if (isMoveResultInCheck(m)) all_moves.remove(m);
         Move [] toReturn = new Move [all_moves.size()];
         toReturn = (Move[]) all_moves.toArray(toReturn);
         return toReturn;
      }
   /**
    * Returns whether or not if a given move results in a position that is check. 
    * @param m The move that would be made.
    * @return true if the resulting position results in check, false otherwise.
    */
      public boolean isMoveResultInCheck(Move m){
         return makeMove(m).isInCheck();
      }
   /**
    * Checks if in the current position, whether or not the king is in check.
    * @return true if the king is in check, false otherwise.
    */
      public boolean isInCheck(){
         Piece[] c_map = is_White_to_Move ? white_map : black_map;
         byte o_col = is_White_to_Move ? Piece.BLACK : Piece.WHITE;
         byte next_pos = 0;
         Piece c_p;
         byte k_loc = -1;
         for (int i = 0; i < c_map.length; i++)
            if (c_map[i].getType() == Piece.KING){
               k_loc = c_map[i].getPosition();
               break;
            }
         for (Piece p : getThreateningPieces(k_loc, DIAGONALS))
            if (p.getType() == Piece.QUEEN || p.getType() == Piece.ROOK){
               return true;
            }
         for (Piece p : getThreateningPieces(k_loc, HORIZONTALS))
            if (p.getType() == Piece.QUEEN || p.getType() == Piece.BISHOP){
               return true;
            }
         for (int j = 0; j < 8; j++){
            next_pos = (byte)(k_loc + KNIGHT_MOVES[0]);
            c_p = getSquareOccupier (next_pos);
            if ((next_pos&0x88)==0&&c_p.getColour()==o_col&&c_p.getType()==Piece.KNIGHT){
               return true;
            }
         }
         for (byte diff : DIAGONALS)
            if ((is_White_to_Move && diff > 0) || (!is_White_to_Move) && diff < 0){
               c_p = getSquareOccupier ((byte)(k_loc + diff));
               if ((next_pos & 0x88)==0&&c_p.getType()==Piece.PAWN&&c_p.getColour()==o_col){
                  return true;
               }
            }
         return false;
      }
   /**
    * Makes a move on the position. Since Position objects are immutable, one must reassign the
    * variable. e.g. <code>p = p.makeMove(m)</code>.
    * @param m The move to make on the current Position.
    * @return A new position with the move made on it.
    */
      public Position makeMove (Move m){
         byte start = m.getStartSquare();
         byte end = m.getEndSquare();
         Piece [] map = Arrays.copyOf(is_White_to_Move? white_map: black_map, white_map.length);
         Piece [] oth = Arrays.copyOf(is_White_to_Move? black_map: white_map, white_map.length);
         Piece p = getSquareOccupier(start);
         Piece o = getSquareOccupier(end);
         int ind_PieceToUpdate = getIndiceOfPiece(p, is_White_to_Move);
         int ind_CapturedPiece = getIndiceOfPiece(o,!is_White_to_Move);
         boolean[] cstl_rights = {white_k_side_castling_allowed,black_k_side_castling_allowed,
               white_q_side_castling_allowed,black_q_side_castling_allowed};
         byte epsq = -1;
         if (p.getType()==Piece.KING){
            if (start==(is_White_to_Move ? 0x04 : 0x74)){
               cstl_rights [is_White_to_Move ? 0 : 1] = false;
               cstl_rights [is_White_to_Move ? 2 : 3] = false;
            }
         } 
         else if (p.getType()==Piece.ROOK){
            if (start==(is_White_to_Move?0x00:0x70)) cstl_rights[is_White_to_Move ? 0 : 1] = false;
            else if (start==(is_White_to_Move?0x07:0x77)) cstl_rights[is_White_to_Move ? 2 : 3] = false;
         } 
         else if (p.getType()==Piece.PAWN){
            if (start / UP_MOVE == 0x06 && end / UP_MOVE == 0x04) epsq = (byte) (start + DOWN_MOVE);
            else if (start / UP_MOVE == 0x01 && end / UP_MOVE == 0x03) epsq = (byte) (start + UP_MOVE);
         }
         map [ind_PieceToUpdate] = p.move(m);
         if (ind_CapturedPiece > 0){
            int lastPiece = (is_White_to_Move ? getLastPieceIndice(false): getLastPieceIndice(true));
            oth[ind_CapturedPiece] = oth[lastPiece];
            oth[lastPiece] = oth[lastPiece].destroy(); 
         }
         return new Position (fifty_move_rule_count++, epsq, cstl_rights, !is_White_to_Move, 
            is_White_to_Move ? map : oth, is_White_to_Move ? oth : map);
      }
   /** 
    * Returns the ending game decision for the positions. This returns the result of this position
    * object, it it has already been decided.
    * @return the result of the game, masked by one of the constants. DRAW for a draw. WHITE_WINS
    * if white wins. BLACK_WINS if black wins. NO_RESULT otherwise.
    */ 
      public int getResult(){
         if (generateAllMoves().length == 0) {
            if (!this.isInCheck())
               return DRAW;
            else 
               return (is_White_to_Move ? BLACK_WINS : WHITE_WINS); 
         }
         if (fifty_move_rule_count == 100) 
            return DRAW;
      // insufficient material clause:
         int whitePiecesLeft = getLastPieceIndice(true);
         int blackPiecesLeft = getLastPieceIndice(false);
      //FIXME: JESSE SPOTS A BUG! THE KING MAY BE IN INDEX 0, 1, OR 2, IT IS NOT RIGHT TO ASSUME
      //THAT THE KING IS IN INDEX 0. UNLESS THE KING IS INITIALISED TO INDEX 0. (I THINK)
         if (whitePiecesLeft == 0){
            if (blackPiecesLeft==1)
               if (black_map[1].getType()==Piece.KNIGHT||black_map[1].getType()==Piece.BISHOP) 
                  return DRAW; 
            if (blackPiecesLeft==2)
               if (black_map[1].getType()==Piece.KNIGHT&&black_map[2].getType()==Piece.KNIGHT) 
                  return DRAW;
         }
         if (blackPiecesLeft == 0){
            if (whitePiecesLeft==1)
               if (white_map[1].getType()==Piece.KNIGHT||white_map[1].getType()==Piece.BISHOP) 
                  return DRAW;
            if (whitePiecesLeft==2)
               if (white_map[1].getType()==Piece.KNIGHT&&white_map[2].getType()==Piece.KNIGHT) 
                  return DRAW;
         }
         return NO_RESULT;
      }
      public static Position loadFEN(String fen) {
      //splitting string by space, to get the 6 fields
         String[] fenBoard = fen.split(" ");
      //splitting the string first field by slash, to get array of representation of each rank starting from 8, end 1
      //Ex: rank[0] represent rank 8 
         String[] rank = fenBoard[0].split("/");
      // 1 set is 1 rank as represented in Fen format (aka "rnbqkbnr" or "8")
         int charactersPerSet = 8, fileNumber = 0;
         int wp_count = 0, bp_count = 0;
         Piece[] w_map = new Piece[16];
         Piece[] b_map = new Piece[16];
         String c_rank;
         int n_blank;
      // Fills w_map and b_map with location of all the pieces
         for(int i = 0; i < 8;i++) {
            fileNumber = 0;
            c_rank = rank[i];
            charactersPerSet = 8;
            for(int j = 0; j < charactersPerSet; j++){
               byte loc = (byte) ((7-i) * 0x10 + fileNumber);
               byte type = -1;
               byte color = -1;
               Piece piece = null;
               boolean addWhite = false;
               boolean isCharacter = false;
               
					if(Character.isDigit(c_rank.charAt(j))) {
                  n_blank = c_rank.charAt(j) - 48;
                  charactersPerSet -= n_blank - 1;
               // Marks the x position on the board of the last non-occupied square
                  fileNumber += n_blank - 1;
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
      // Fills remainder of array with null pieces
         for(int i = wp_count; i < 16; i++) 	w_map[i] = Piece.getNullPiece();      
         for(int i = wp_count; i < 16; i++)		b_map[i] = Piece.getNullPiece();
      // Determines who makes next move
         boolean whiteMove = fenBoard[1].equals("w") ? true : false;
      // Determines castling rights
         boolean[] castleRights = new boolean[]{false, false, false, false};
         String castle = fenBoard[2];
         for(int i = 0; i < castle.length(); i ++){
            if(castle.charAt(i) == 'K')castleRights[0] = true;
            else if(castle.charAt(i) == 'k')castleRights[1] = true;
            else if(castle.charAt(i) == 'Q')castleRights[2] = true;
            else if(castle.charAt(i) == 'q')castleRights[3] = true;
         }
      // Determines en passent square
         String enPassant = fenBoard[3];
         byte ensq;
         if (enPassant.equalsIgnoreCase("-"))	ensq = -1;
         else	ensq = (byte)((enPassant.charAt(0) - 'a') * 0x88 + enPassant.charAt(1));  
      // Determine 50 move counter
         byte fiftyMove = (byte) Integer.parseInt(fenBoard[4]);
      //Determine full move counter
      //not used in Position generation
      //int fullMove = Integer.parseInt(fenBoard[5]);
         Position position = new Position(fiftyMove, ensq, castleRights, whiteMove, w_map, b_map);
         return position;
      }
      
   	public String saveFEN(){
   		String str = "";
   		String toAdd = "";
			byte c_sq;
   		Piece o_pos;
   		byte type;
   		byte col;
   		int n_blank;
   		
   		//write board
   		for (int i = 7 ; i >= 0 ; i --){
   			n_blank = 0;
   			for (int j = 0; j < 8 ; j ++){
   				c_sq = (byte)(i * 0x10 + j);
   				o_pos = getSquareOccupier(c_sq);
   				if (o_pos.isEqual(Piece.getNullPiece())){
   					n_blank ++;
   					if (j == 7) str += n_blank;
   				}
   				else {
   					if (n_blank != 0) str += n_blank;
   					col = o_pos.getColour();
   					type = o_pos.getType();
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
   		
   		//write active colour
   		str += " " + (is_White_to_Move? "w" : "b") + " ";
   		
   		//write castle rights
   		boolean[] castle = getCastlingRights();
   		boolean stub = true;
   		if (castle[0]){
   			str += "K";
   			stub = false;
   		}
   		if (castle[2]){
   			str += "Q";
   			stub = false;
   		}
   		if (castle[1]){
   			str += "k";
   			stub = false;
   		}
   		if (castle[3]){
   			str += "q";
   			stub = false;
   		}
   		if (stub) str += "-";
   		
   		//write enPassant square
   		if (en_passant_square != -1)str +=" "+(char)('a'+en_passant_square/0x10)+(en_passant_square%0x10+1);
   		else str += " -";
   		
   		//write half-move clock
   		str += " " + fifty_move_rule_count;
   		
   		//write full-move clock
   		//currently not used
   		//str += " " + full_move_count;
   		return str;
   	}
   //----------------------Helper Methods----------------------
   /**
    * Returns the last indice of the last piece that is not null
    * @param forWhite whether or not to search in white's pieces or black's.
    */
      private int getLastPieceIndice(boolean forWhite){
         if (forWhite){
            for (int i = 15; i>=0; i++){
               if (white_map[i].getType() != -1) 
                  return i;
            }
         }
         else {
            for (int i = 15; i>=0; i++)
               if (black_map[i].getType() != -1) 
                  return i;
         }
         return 0;
      }
   /**
    * Looks for the index of a certain piece in an appropriate map.
    * @param p The piece to look for.
    * @param map The map to look in. True for White, false for Black.
    * @return The index in the appropriate map that contains the specified piece. -1 if no such
    * piece exists.
    */
      private int getIndiceOfPiece (Piece p, boolean map){
         int ind = -1;
         Piece [] mapToSearch = map ? white_map : black_map;
         for (int i = 0; i < 16; i++){
            if (p.isEqual(mapToSearch[i])) ind = i;
         }
         return ind;
      }
   /**
    * Returns the occupier of a specific square, or the null piece if the square is empty. This
    * method does so by running through both arrays of maps.
    * @return the occupier of a specific square, the null piece if the square is empty.
    */
      private Piece getSquareOccupier (byte square){
         for (int i = 0; i<= getLastPieceIndice(true); i++){
            if (white_map[i].getPosition() == square)
               return white_map[i];
         }
         for (int i = 0; i<= getLastPieceIndice(false); i++){
            if (black_map[i].getPosition() == square)
               return black_map[i];
         }
         return Piece.getNullPiece();
      }
   /**
    * Generates an vector of moves for a mask of differences for a piece. This method does so with
    * a while loop for each difference if the motion is continuous, stopping on an opponent's piece.
    * @param c_pos The current location.
    * @param differences The difference for each direction from c_pos.
    * @param cont Whether the piece moves in continuous motion, false if it does, true otherwise.
    * @return A vector containing all the possible straight moves.
    */
      private Vector<Move> generatePieceMoves(byte c_pos, byte[] differences, boolean cont){
         Vector <Move> AllMoves = new Vector <Move> (10,3);
         byte c_col = is_White_to_Move ? Piece.WHITE : Piece.BLACK;
         byte o_col = is_White_to_Move ? Piece.BLACK : Piece.WHITE;
         for (int i = 0; i < differences.length; i++){
            byte next_pos = (byte) (c_pos + differences[i]);
            while ((next_pos&0x88)==0){
               Piece o_pos = getSquareOccupier(next_pos);
               if (o_pos.getColour()!=c_col) AllMoves.add(new Move(c_pos, next_pos));
               else 
                  break;
               if (cont) 
                  break;
               if (o_pos.getColour()==o_col) 
                  break;
               next_pos += differences[i];
            }
         }
         return AllMoves;
      }
   /**
    * Generates an vector of Pieces  for all the Pieces able to check the king along a straight
    * line with the appropriate distances in 0x88 as specified.
    * @param k_loc The current location for the king.
    * @param differences The difference for each direction from k_loc.
    * @return A vector containing all the Pieces that can check the king on a straight line.
    */
      private Piece [] getThreateningPieces(byte k_loc, byte[] differences){
         Vector <Piece> AllPieces = new Vector <Piece> (10,3);
         byte col = is_White_to_Move ? Piece.BLACK : Piece.WHITE;
         for (int i = 0; i < differences.length; i++){
            byte next_pos = k_loc;
            do{
               next_pos += differences[i];
               Piece o_pos = getSquareOccupier(next_pos);
               if (o_pos.getColour() == col){
                  AllPieces.add(o_pos);
                  break;
               }
               else if (o_pos.getColour() == col) 
                  break;
            }while ((next_pos&0x88)==0);
         }
         Piece [] toReturn = new Piece [AllPieces.size()];
         toReturn = (Piece[]) AllPieces.toArray(toReturn);
         return toReturn;
      }
   //----------------------End of Helper Methods----------------------
   //----------------------End of Methods----------------------
   }