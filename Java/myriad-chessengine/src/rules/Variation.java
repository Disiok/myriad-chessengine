package rules;

// Work in progress, do not touch please - thanks, Jesse.
public class Variation {
	public static class MoveNode extends Move{
		int evaluation;
		Move nextMove;
		
		public MoveNode (byte start, byte end){
			super (start, end);
		}
	}
}
