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
	
	public Piece [] getPawns (boolean mapToSearch){
		Piece [] map = mapToSearch ? white_map : black_map;
		Vector <Piece> des = new Vector <Piece> (8,0);
		for (Piece p : map){
			byte type = p.getType();
			if (type == Piece.PAWN){
				des.add(new Piece (p.getPosition(),type,p.getColour()));
			}
		}
		Piece [] toReturn = new Piece [des.size()];
		return des.toArray(toReturn);
	}
	
	public Piece [] getRooks (boolean mapToSearch){
		Piece [] map = mapToSearch ? white_map : black_map;
		Vector <Piece> des = new Vector <Piece> (2,1);
		for (Piece p : map){
			byte type = p.getType();
			if (type == Piece.ROOK){
				des.add(new Piece (p.getPosition(),type,p.getColour()));
			}
		}
		Piece [] toReturn = new Piece [des.size()];
		return des.toArray(toReturn);
	}
	
	public Piece [] getKnights (boolean mapToSearch){
		Piece [] map = mapToSearch ? white_map : black_map;
		Vector <Piece> des = new Vector <Piece> (2,1);
		for (Piece p : map){
			byte type = p.getType();
			if (type == Piece.KNIGHT){
				des.add(new Piece (p.getPosition(),type,p.getColour()));
			}
		}
		Piece [] toReturn = new Piece [des.size()];
		return des.toArray(toReturn);
	}
	
	public Piece [] getBishops (boolean mapToSearch){
		Piece [] map = mapToSearch ? white_map : black_map;
		Vector <Piece> des = new Vector <Piece> (2,1);
		for (Piece p : map){
			byte type = p.getType();
			if (type == Piece.BISHOP){
				des.add(new Piece (p.getPosition(),type,p.getColour()));
			}
		}
		Piece [] toReturn = new Piece [des.size()];
		return des.toArray(toReturn);
	}
	
	public Piece [] getQueens (boolean mapToSearch){
		Piece [] map = mapToSearch ? white_map : black_map;
		Vector <Piece> des = new Vector <Piece> (1,1);
		for (Piece p : map){
			byte type = p.getType();
			if (type == Piece.QUEEN){
				des.add(new Piece (p.getPosition(),type,p.getColour()));
			}
		}
		Piece [] toReturn = new Piece [des.size()];
		return des.toArray(toReturn);
	}
	
	public Piece getKing (boolean mapToSearch){
		Piece [] map = mapToSearch ? white_map : black_map;
		Vector <Piece> des = new Vector <Piece> (2,1);
		for (Piece p : map){
			byte type = p.getType();
			if (type == Piece.King){
				return (new Piece (p.getPosition(),type,p.getColour()));
			}
		}
	}
}
