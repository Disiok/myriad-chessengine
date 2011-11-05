
public class Test {
	public static void main(String[] args){
		String FEN ="some FEN string";
		Position p = FenUtility.loadFEN(FEN);
		MobilityFeatures ft = new MobilityFeatures(new Feature (p, new FeatureManager(p)));
		String s = ft.detectControlSquares();
	}
}
