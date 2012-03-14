package debug;

import java.text.DecimalFormat;
import rules.*;
import debug.ConsoleV2.Routine;
import eval.*;

public class RoutineLibrary {
	public static class setCurrent extends Routine{
		public setCurrent(ConsoleV2 cs, Position p, String[] argv) {
			super(cs, p, argv);
		}
		protected void start(Position p, String[] argv) throws Exception {
			if (argv.length == 1) {
				outputLine ("Invalid usage. Usage is: exposition <some FenString>");
				return;
			}
			ConsoleV2 cs = getConsole();
			cs.current = concatFen(argv);
			outputLine ("Board successfully set.");
		}
	}
	public static class lorenzDescriptor extends Routine{
		public lorenzDescriptor(ConsoleV2 cs, Position p, String[] argv) {
			super(cs, p, argv);
		}
		protected void start(Position p, String [] argv) throws Exception {
			Lorenz lz = new Lorenz (p);
			outputLine("---------------Lorenz Start---------------");
			displayPosition();
			if (argv.length == 1){
				for (int i = 0; i < Lorenz.MAX_FEATURES; i++) outputDescriptor (lz.get((byte)i), i);
			} else {
				String [] specific = argv[1].split(",");
				for (String d: specific){
					int k = Integer.parseInt(d);
					outputDescriptor (lz.get((byte)k), k);
				}
			}
			outputLine("----------------Lorenz End----------------");
		}
		private void outputDescriptor (long value, int indice){
			String lineOut = "";
			switch (indice){
			case Lorenz.BLACK_ABSOLUTE_MATERIAL: lineOut = "Black Material = "; break;
			case Lorenz.BLACK_BACKWARDS: lineOut = "Black Backwards Pawns = "; break;
			case Lorenz.BLACK_DOUBLED_PAWNS: lineOut = "Black Doubled Pawns = "; break;
			case Lorenz.BLACK_ISOLANIS: lineOut = "Black Isolanis = "; break;
			case Lorenz.BLACK_PASSERS: lineOut = "Black Passed Pawns = "; break;
			case Lorenz.BLACK_SENTINELS: lineOut = "Black Sentinel Squares = "; break;
			case Lorenz.BLACK_RELATIVE_MATERIAL: lineOut = "Black Relative Material = "; break;
			case Lorenz.WHITE_ABSOLUTE_MATERIAL: lineOut = "White Material = "; break;
			case Lorenz.WHITE_BACKWARDS: lineOut = "White Backwards Pawns = "; break;
			case Lorenz.WHITE_DOUBLED_PAWNS: lineOut = "White Doubled Pawns = "; break;
			case Lorenz.WHITE_ISOLANIS: lineOut = "White Isolanis = "; break;
			case Lorenz.WHITE_PASSERS: lineOut = "White Passed Pawns = "; break;
			case Lorenz.WHITE_SENTINELS: lineOut = "White Sentinel Squares = "; break;
			case Lorenz.WHITE_RELATIVE_MATERIAL: lineOut = "White Relative Material = "; break;
			case Lorenz.KING_SAFETY: lineOut = "King Safety = "; break;
			case Lorenz.DYNAMICS: lineOut = "Dynamics = "; break;
			case Lorenz.OPEN_FILES: lineOut = "Open Files = "; break;
			case Lorenz.PAWN_ISLANDS: lineOut = "Pawn Islands = "; break;
			case Lorenz.SPACE: lineOut = "Space = "; break;
			default: return;
			}
			outputLine (lineOut + "0x" + Long.toHexString(value));
		}
	}
	public static class display extends Routine{
		public display(ConsoleV2 cs, Position p, String[] argv) {
			super(cs, p, argv);
		}
		protected void start(Position p, String[] argv) throws Exception {
			if (argv.length == 1) displayPosition();
			else {
				ConsoleV2 cs = getConsole();
				Position orig = cs.current;
				cs.current = concatFen(argv);
				displayPosition();
				cs.current = orig;
			}
			outputLine("Done.");
		}
	}
	public static class help extends Routine{
		public help(ConsoleV2 cs, Position p, String[] argv) {
			super(cs, p, argv);
		}
		protected void start(Position p, String[] argv) throws Exception {
			outputLine ("Currently implemented features include: ");
			outputLine ("clear or cls -> clears the console screen.");
			outputLine ("display");
			outputLine ("\tdisplay -> displays the current position.");
			outputLine ("\tdisplay <some FenString> -> displays a specified position.");
			outputLine ("exposition <some FenString> -> sets the current position to a specified position.");
			outputLine ("interrupt -> kills the current routine.");
			outputLine ("lorenz");
			outputLine ("\tlorenz -> displays all the Lorenz values for the current position.");
			outputLine ("\tlorenz lorenzindex1,lorenzindex2,... -> displays the Lorenz");
			outputLine ("\t\tvalues for specified Lorenz indices.");
			outputLine ("primeval -> calls the basic evaluation heuristic.");
			outputLine ("perft <depth>");
			outputLine ("\tperft <depth> -> calls a non-descriptive performance test for the ");
			outputLine ("\t\tcurrent position at a specified depth");
			outputLine ("\tperft <depth> <s>,<d> -> adding a 'd' will provide details for ");
			outputLine ("\t\tperformance test (checks, captures, etc.) and adding a 's' will");
			outputLine ("\t\tperform the perft for depth 1 to the specified depth. Example:");
			outputLine ("\t\tperft 5 d,s");
			outputLine ("divide <depth> -> performs a divide test to a specific depth.");
		}
	}
	public static class prim_evaluate extends Routine {
		public prim_evaluate (ConsoleV2 cs, Position p, String [] argv){
			super (cs, p, argv);
		}
		protected void start (Position p, String [] argv){
			displayPosition();
			Lorenz dp = new Lorenz (p);
			if (p.getResult() == Position.WHITE_WINS) outputLine ("+oo");
			else if (p.getResult() == Position.BLACK_WINS) outputLine ("-oo");
			else if (p.getResult() == Position.DRAW) outputLine ("=");
			else {
				long mat = (dp.get(Lorenz.WHITE_ABSOLUTE_MATERIAL) & Crescent.MATERIAL_MASK)
						- (dp.get(Lorenz.BLACK_ABSOLUTE_MATERIAL) & Crescent.MATERIAL_MASK);
				outputLine ("Material = " + mat);
				long w_sent = dp.get(Lorenz.WHITE_SENTINELS), b_sent = dp.get(Lorenz.BLACK_SENTINELS);
				long two_bishops = dp.get(Lorenz.DYNAMICS) & 7; 
				System.out.println(two_bishops);
				int score = 0;
				if (two_bishops == 5) score -= 20;
				else if (two_bishops == 3) score += 20;
				int n_sq_w = 0, n_sq_b = 0;
				for (int i = 0; i < 64; i ++){
					if ((w_sent & 1) == 1) n_sq_w++;
					else if ((b_sent & 1) == 1) n_sq_b++;
					w_sent >>=1;
					b_sent >>=1;
				}
				outputLine ("Square Control Difference = " + (n_sq_w - n_sq_b));
				outputLine ("Score = " + (mat + n_sq_w - n_sq_b + score));
			}
		}
	}
	public static class perft extends Routine {
		public perft (ConsoleV2 cs, Position p, String [] argv){
			super (cs, p, argv);
		}
		protected void start (Position p, String [] argv){
			if (argv.length == 1) {
				outputLine ("Invalid usage. Usage is: perft <depth> <descriptive?>,<serial?>");
				return;
			}
			int depth = 1;
			try {
				depth = Integer.parseInt(argv[1]);
			} catch (NumberFormatException ex){
				outputLine ("Invalid usage. Usage is: perft <depth> <descriptive?>,<serial?>");
				return;
			}
			boolean serial = false, descriptive = false;
			if (argv.length > 2){
				String [] st = argv[2].split(",");
				if (st[0].charAt(0) == 'd') descriptive = true;
				else if (st[0].charAt(0) == 's') serial = true;
				if (st.length > 1){
					if (st[1].charAt(0) == 'd') descriptive = true;
					else if (st[1].charAt(0) == 's') serial = true;
				}
			}
			outputLine("---------------Perf. Test Start---------------");
			if (descriptive) outputLine ("Depth\tNodes\tTime(ms)\tN/s\tCaptures\tChecks\tMates" +
					"\tPromotions\tEP\tCastle");
			else outputLine ("Depth\tNodes\tTime(ms)\tkN/s");
			int s_depth = serial ? 1 : depth;
			for (int i = s_depth; i <= depth; i++){
				check = 0;
				checkmate = 0;
				promotion = 0;
				castle = 0;
				ep = 0;
				capture = 0;
				long s_time = System.currentTimeMillis();
				int nodes = Perft(i, p);
				long e_time = System.currentTimeMillis() - s_time;
				double n_per_sec = nodes/ (double)(e_time);
				if (descriptive) outputLine(i+"\t"+nodes+"\t"+dcf.format(n_per_sec)+"\t"+e_time+"\t"+capture
						+"\t"+check+"\t"+checkmate+"\t"+promotion+"\t"+ep+"\t"+castle);
				else outputLine (i + "\t" + nodes + "\t" + e_time + "\t" + dcf.format(n_per_sec));
			}
			outputLine("----------------Perf. Test End----------------");
		}
	}
	public static class divide extends Routine {
		public divide(ConsoleV2 cs, Position p, String[] argv) {
			super(cs, p, argv);
		}
		protected void start(Position p, String[] argv) throws Exception {
			if (argv.length == 1) {
				outputLine ("Invalid usage. Usage is: divide <depth>");
				return;
			}
			int depth = 1;
			try {
				depth = Integer.parseInt(argv[1]);
			} catch (NumberFormatException ex){
				outputLine ("Invalid usage. Usage is: perft <depth> <descriptive?>,<serial?>");
				return;
			}
			Move [] div = p.generateAllMoves();
			outputLine("---------------Divide Start---------------");
			outputLine ("Move\tNodes\tResulting FEN");
			for (Move m: div){
				Position newposition = p.makeMove(m);
				int res = Perft(depth-1, newposition);
				outputLine(m.toString(p) + "\t" + res + "\t" + Utility.saveFEN(newposition));
			}
			outputLine("----------------Divide End----------------");
		}
	}
	public static final DecimalFormat dcf = new DecimalFormat("#.###");
	public static int check;
	public static int checkmate;
	public static int promotion;
	public static int castle;
	public static int ep;
	public static int capture;
	
	private static int Perft (int depth, Position p){
		int nodes = 0;
		if (depth == 0){
			byte mod = p.prior_move.getModifier();
			if (p.isInCheck(false)) check ++;
			if (p.getResult()==Position.BLACK_WINS||p.getResult()==Position.WHITE_WINS) checkmate++;
			switch (mod){
				case 10: capture++; break;
				case 6: case 7: case 8: case 9: promotion++; break;
				case 16: case 17: case 18: case 19: promotion++; capture++; break;
				case 1: case 2: case 3: case 4: castle++; break;
				case 5: ep++; capture++; break;
			}
			return 1;
		} 
		Move[] move_list = p.generateAllMoves();
		for (Move m: move_list){
			nodes += Perft(depth-1, p.makeMove(m));
		}
		return nodes;	
	}
}