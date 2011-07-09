   package rules;
   import java.io.*;
   import java.util.*;
   import java.lang.*;

/**
 * The Sandbox. Test your code here! Some methods are provided. Please do not remove any code that
 * you think might be important! Just comment it out!
 */
   public class Sandbox {
      public static byte twoDigitHexConverter (int someIntNumber){
         int tens = someIntNumber /10;
         int ones = someIntNumber %10;
         return (byte) (tens*16+ones);
      }
      public static int twoDigitIntConverter (byte someHexNumber){
         int tens = someHexNumber/16;
         int ones = someHexNumber%16;
         return 10*tens+ones;
      }
      public static void main (String [] args)throws IOException{
      // Initial Board Set-up Test = JW
      /*
      Position b = new Position();
      Piece [] ps = b.getWhitePieces();
      for (Piece p: ps){
      	System.out.println(p.getPosition() + " " + p.getType() + " " + p.getColour());
      }
      Piece [] pb = b.getBlackPieces();
      for (Piece p: pb){
      	System.out.println(p.getPosition() + " " + p.getType() + " " + p.getColour());
      }*/
      
      // Pawns and Knights Movement Test = JW
      /*
      Move [] m = b.generateAllMoves();
      for (Move s: m){
      	System.out.println(twoDigitIntConverter(s.getStartSquare()) + " " + 
      			twoDigitIntConverter(s.getEndSquare()));
      }*/
      
      // Move conversion into string = JW
      //Move m = new Move ((byte)0x14, (byte)0x34);
      //System.out.println(m);
      
      //Move Generation Test
      /*Position p = new Position();
      Move [] m = p.generateAllMoves();
      for (Move q : m) System.out.println(q);
      Piece [] ps = p.getWhitePieces();
         for (Piece b: ps){
            System.out.println(b.getPosition() + " " + b.getType() + " " + b.getColour());
         }
         Piece [] pb = p.getBlackPieces();
         for (Piece b: pb){
            System.out.println(b.getPosition() + " " + b.getType() + " " + b.getColour());
         }*/
      
      // FEN Saving/Loading Test...success
         String savefile = "save_1.txt";
         BufferedWriter out = new BufferedWriter(new FileWriter(savefile));
			Position p = new Position();
			
			out.write (p.saveFEN());
         out.close();
			
         BufferedReader in = new BufferedReader(new FileReader(savefile));
         String fen = in.readLine();
         p = Position.loadFEN(fen);

			Piece [] ps = p.getWhitePieces();
			for (Piece b: ps){
            System.out.println(b);
         }
         Piece [] pb = p.getBlackPieces();
         for (Piece b: pb){
            System.out.println(b);
         }
         Move [] m = p.generateAllMoves();
         for (Move q : m) System.out.println(q);

      }
   }