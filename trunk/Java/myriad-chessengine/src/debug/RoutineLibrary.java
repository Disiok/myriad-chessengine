package debug;

import rules.Position;
import debug.ConsoleV2.Routine;
import eval.Lorenz;

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
			outputLine ("Currently implemented features: ");
			outputLine ("----------------------------------");
			outputLine ("\tclear or cls -> clears the console screen.");
			outputLine ("\tdisplay");
			outputLine ("\t\tdisplay -> displays the current position.");
			outputLine ("\t\tdisplay <some FenString> -> displays a specified position.");
			outputLine ("\texposition <some FenString> -> sets the current position to");
			outputLine ("\t\ta specified position.");
			outputLine ("\tinterrupt -> kills the current routine.");
			outputLine ("\tlorenz");
			outputLine ("\t\tlorenz -> displays all the Lorenz values for the current");
			outputLine ("\t\t\tposition.");
			outputLine ("\t\tlorenz lorenzindex1,lorenzindex2,... -> displays the Lorenz");
			outputLine ("\t\t\tvalues for specified Lorenz indices.");
		}
		
	}
}