package tuning;

import rules.*;
import java.io.*;

public class PGNDatabase {
	Position [] database;
	int gms;
	int start [], end [], result [];
	
	PGNDatabase (int games, File to_read){
		gms = games;
		start = new int [gms];
		end = new int [gms];
		result = new int [gms];
		// read file, store start and end via ply count
	}
	public Position get (int gameNumber, int ply){
		if (gameNumber > gms) return null;
		int ply_count = end[gameNumber] - start[gameNumber];
		if (ply > ply_count) return null;
		return database[start[gameNumber] + ply_count];
	}
	public boolean predictResult (int gameNumber, int res){
		return result[gameNumber] == res;
	}
}
