package engine;

import java.util.*;
import rules.*;

public class PositionPlus extends Position {
	public Piece [] getMinorPieces (boolean mapToSearch){
		Piece [] map = mapToSearch ? white_map : black_map;
		Vector <Piece> des = new Vector <Piece> (4,0);
		for (Piece p : map){
			byte type = p.getType();
			if (type == Piece.KNIGHT || type == Piece.BISHOP){
				des.add(new Piece (p.getPosition(),type,p.getColour()));
			}
		}
		Piece [] toReturn = new Piece [des.size()];
		return des.toArray(toReturn);
	}
}
