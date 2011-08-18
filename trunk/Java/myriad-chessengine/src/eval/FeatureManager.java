package eval;

import java.util.*;
import java.lang.reflect.*;

import rules.Piece;
import rules.Position;

/**
 * This class manages all of the "Features" classes involved in the evaluation. The system involves Features
 * which are classes that describe a part of the game, e.g. dynamics. Features have components that describe 
 * the feature, e.g. dynamics would have whether the position is open or closed.
 * 
 * Even though the Feature class itself can be instantiated, it will have no use unless FeatureManager is
 * adapted to that feature.
 * @author Jesse Wang
 */
public class FeatureManager {
	//----------------------Subclasses----------------------
	/**
	 * The Feature class is a base class for the FeatureManager and comes with important methods that allow
	 * the FeatureManager to do its job.
	 * @author Jesse Wang
	 */
	public static class Feature {
		//----------------------Feature Subclass :: Instance Variables----------------------
		/** The hash table that maps a component name to its value. */
		private Hashtable <String, String> featureComponents = new Hashtable <String, String> ();
		/** The array containing all the method objects used to retrieve components. */
		private Method [] components = this.getClass().getDeclaredMethods();
		/** The white pawns. */
		protected Piece[] white_pawns;
		/** The white rooks. */
		protected Piece[] white_rooks;
		/** The white knights. */
		protected Piece[] white_knights;
		/** The white bishops. */
		protected Piece[] white_bishops;
		/** The white queens. */
		protected Piece[] white_queens;
		/** The white king. */
		protected Piece[] white_king;
		/** The black pawns. */
		protected Piece[] black_pawns;
		/** The black rooks. */
		protected Piece[] black_rooks;
		/** The black knights. */
		protected Piece[] black_knights;
		/** The black bishops. */
		protected Piece[] black_bishops;
		/** The black queens. */
		protected Piece[] black_queens;
		/** The black king. */
		protected Piece[] black_king;
		/** The castling availability. */
		protected boolean[] castling_rights;
		/** A pointer back to the original position. */
		protected Position original_position;
		protected FeatureManager featureManager;
		//----------------------End of Feature Subclass :: Instance Variables----------------------
		//----------------------Feature Subclass :: Constructors----------------------
		/**
		 * Constructs a Feature object from an existing position. The useful elements of the Position
		 * objects to the evaluation is then extracted and saved.
		 * @param basic_pos The Position to examine.
		 */
		public Feature (Position basic_pos, FeatureManager fm){
			original_position = basic_pos;
			featureManager = fm;
			Piece[] w_map = original_position.getWhitePieces();
			Piece[] b_map = original_position.getBlackPieces();
			castling_rights = original_position.getCastlingRights();
			white_pawns = getPieces(Piece.PAWN, w_map);
	 		white_rooks = getPieces(Piece.ROOK, w_map);
			white_knights = getPieces(Piece.KNIGHT, w_map);
			white_bishops = getPieces(Piece.BISHOP, w_map);
			white_queens = getPieces(Piece.QUEEN, w_map);
			white_king = getPieces(Piece.KING, w_map);
			black_pawns = getPieces(Piece.PAWN, b_map);
			black_rooks = getPieces(Piece.ROOK, b_map);
			black_knights = getPieces(Piece.KNIGHT, b_map);
			black_bishops = getPieces(Piece.BISHOP, b_map);
			black_queens = getPieces(Piece.QUEEN, b_map);
			black_king = getPieces(Piece.KING, b_map);
		}
		/**
		 * A constructor for a pre-existing feature, this is an important efficiency improvement because
		 * the feature objects share the same basic data.
		 * @param f A pre-existing Feature object.
		 */
		public Feature (Feature f){
			featureManager = f.featureManager;
			white_pawns = f.white_pawns;
	 		white_rooks = f.white_rooks;
			white_knights = f.white_knights;
			white_bishops = f.white_bishops;
			white_queens = f.white_queens;
			white_king = f.white_king;
			black_pawns = f.black_pawns;
			black_rooks = f.black_rooks;
			black_knights = f.black_knights;
			black_bishops = f.black_bishops;
			black_queens = f.black_queens;
			black_king = f.black_king;
			castling_rights = f.castling_rights;
			original_position = f.original_position;
		}
		//----------------------End of Feature Subclass :: Constructors----------------------
		//----------------------Feature Subclass :: Methods----------------------
		/**
		 * Returns the value of a component in the Feature object with a name specified. If the value
		 * does not exist in the current hash table, it will be created appropriately. An exception is
		 * thrown when the feature object is unable to generate a value for the specified component.
		 * 
		 * Note: all component detector methods MUST return a string, take no arguments, and have 
		 * detect+componentName as it's method name.
		 * @param compName The name of the component specified.
		 * @return The value of the component.
		 * @throws Exception If <i>this</i> object is unable to create the a value for the specified
		 * component.
		 */
		private String retrieveFeatureComponent (String compName) throws Exception{
			String toReturn = featureComponents.get(compName);
			if (toReturn == null){
				for (Method m : components){
					if (m.getName().equals("detect"+compName)){
						toReturn = (String) m.invoke(this);
						featureComponents.put(compName, toReturn);
					}
				}
			}
			return toReturn;
		}
		/**
		 * Gets all active pieces by a specified type in a specific map.
		 * @param p_type The piece type to search for.
		 * @param map The array to search for.
		 * @return An array containing all the pieces of the specified type.
		 */
		private Piece [] getPieces (byte p_type, Piece [] map){
			Vector <Piece> pieces = new Vector <Piece> (2,6);
			for (Piece p : map){
				byte c_type = p.getType();
				if (c_type == p_type){
					pieces.add(new Piece (p.getPosition(),c_type,p.getColour()));
				}
			}
			Piece [] toReturn = new Piece [pieces.size()];
			return pieces.toArray(toReturn);
		}
		//----------------------End of Feature Subclass :: Methods----------------------
	}
	//----------------------End of Subclasses----------------------
	//----------------------Instance Variables----------------------
	/** The features that the FeatureManagers is managing. */
	private Feature [] accessibleFeature;
	/** The underlying Position object. */
	private Position basic_feat;
	//----------------------End of Instance Variables----------------------
	//----------------------Constants----------------------
	/** The number of Feature objects that the FeatureManager is managing. */
	private static final byte NUM_FEATURES = 4;
	//----------------------End of Constants----------------------
	//----------------------Constructors----------------------
	/**
	 * Constructs a FeatureManager object for 1 Position. It is unwise and inefficient to construct
	 * more than 1 FeatureManager for 1 position.
	 * @param b_feat
	 */
	public FeatureManager (Position basic_pos) {
		accessibleFeature = new Feature [NUM_FEATURES];
		basic_feat = basic_pos;
		accessibleFeature[0] = new DynamicFeatures(basic_feat, this);
	}
	//----------------------End of Constants----------------------
	//----------------------Methods----------------------
	/**
	 * Retrieves the value of one of the feature components. 
	 * @param id The feature ID, from 0 to 3, 1 being Dynamic, 2 being Piece, 3 being Structural, 4 being
	 * mobility.
	 * @param componentName The name of the component.
	 * @return The value of the component.
	 * @throws IllegalArgumentException If the Feature or the component does not exist, or the specified
	 * feature cannot render the component.
	 */
	public String retrieveFeatureComponent(int id, String componentName) throws IllegalArgumentException{
		if (id > NUM_FEATURES) throw new IllegalArgumentException();
		Feature currentFeature = accessibleFeature [id];
		if (currentFeature == null){
			switch (id){
				case 0: accessibleFeature[id] = new DynamicFeatures(accessibleFeature[0]); break;
				case 1: accessibleFeature[id] = new PieceFeatures(accessibleFeature[0]); break;
				case 2: accessibleFeature[id] = new StructuralFeatures(accessibleFeature[0]); break;
				case 3: accessibleFeature[id] = new MobilityFeatures(accessibleFeature[0]); break;
			}
		}
		try {
			String toReturn = accessibleFeature[id].retrieveFeatureComponent(componentName);
			return toReturn;
		} catch (Exception ex){
			throw new IllegalArgumentException();
		}
	}
	//----------------------End of Constants----------------------
}
