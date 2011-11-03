package eval;

public class ComponentParser {

}
/*
public class ControlledSquares {
	String [][] squares = new String [8][8];
	int [][] squares_control = new int [8][8];
	
	public static void initializeSquares (){
	 for (int i = 0; i< 8; i++){
		 for (int j = 0 ; j<8; j++){
			 squares[i][j]='|';
			 squares_control[i][j] = 0;
		 }
	 }
	}
	
	public static void calculateControlledSquares(){
		// TODO: loop through each piece and add the squares it control by using method addControl.
		// TODO: at the end, compare all the squares.
	}
	
	public static void addControl (char piece_type, byte pos){
		int row = pos / 0x10;
		int col = pos % 0x10;
		char piece_rep;
		boolean isWhite = piece_type < 'a';
		piece_type = piece_type.toUpperCase();
		
		if (piece_type == 'P') piece_rep = 'E';
		else if (piece_type == 'N') piece_rep = 'D';
		else if (piece_type == 'B') piece_rep = 'C';
		else if (piece_type == 'R') piece_rep = 'B';
		else if (piece_type == 'Q') piece_rep = 'A';
		
		int start_index = 0;
		String c_pieces = squares[row][col];
		// white|black
		if (!isWhite) start_index = c_pieces.indexOf('|');
		
		while (start_index < c_pieces.length() || c_pieces.charAt(start_index) != '|'){
			if (piece_rep > c_pieces.charAt(i)){
				c_pieces = c_pieces.substring(0,start_index) + piece_rep + c_pieces.substring(start_index);
				break;
			}
			start_index++;
		}
	}
    
	public static void compareAll(){
		for (int i = 0; i < 8; i++){
			for (int j = 0; j < 8; j++){
				String[] controls = squares[i][j].split('|');
				if (controls[0].compareTo(controls[1]) > 0) square_control[i][j] = square_control[i][j] + 100;
				else square_control[i][j] = square_control[i][j] - 100;
			}
		}
	}
}*/
