package engine;

import java.util.*;
import rules.*;

public class PositionPlus extends Position {
	public Piece [] getMinorPieces (boolean mapToSearch){
		Piece [] map = mapToSearch ? white_map : black_map;
		Vector <Piece> des = new Vector <Piece> (4,1);
		for (Piece p : map){
			byte type = p.getType();
			if (type == Piece.KNIGHT || type == Piece.BISHOP){
				des.add(new Piece (p.getPosition(),type,p.getColour()));
			}
		}
		Piece [] toReturn = new Piece [des.size()];
		return des.toArray(toReturn);
	}
	public Piece [] getPieces (byte p_type, boolean mapToSearch){
		Piece [] map = mapToSearch ? white_map : black_map;
		Vector <Piece> des = new Vector <Piece> (8,1);
		for (Piece p : map){
			byte c_type = p.getType();
			if (c_type == p_type){
				des.add(new Piece (p.getPosition(),c_type,p.getColour()));
			}
		}
		Piece [] toReturn = new Piece [des.size()];
		return des.toArray(toReturn);
	}
}
