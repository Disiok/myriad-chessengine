package eval;

import java.util.*;
import java.lang.reflect.*;

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
	/**
	 * The Feature class is a base class for the FeatureManager and comes with important methods that allow
	 * the FeatureManager to do its job.
	 * @author Jesse Wang
	 */
	public static class Feature {
		/**
		 * The hash table that maps a component name to its value.
		 */
		private Hashtable <String, String> featureComponents = new Hashtable <String, String> ();
		/**
		 * An embedded BasicFeatures object that is used to detect the components.
		 */
		protected BasicFeatures base_feature;
		/**
		 * Constructor: Embeds the BasicFeatures which describes the Position that is currently being
		 * evaluated.
		 * @param bf
		 */
		public Feature (BasicFeatures bf){
			base_feature = bf;
		}
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
				Method [] me = getClass().getDeclaredMethods();
				for (Method m : me){
					if (m.getName().equals("detect"+compName)){
						toReturn = (String) m.invoke(this);
						featureComponents.put(compName, toReturn);
					}
				}
			}
			return toReturn;
		}
	}
	/** The number of Feature objects that the FeatureManager is managing. */
	private static final byte NUM_FEATURES = 4;
	/** The features that the FeatureManagers is managing. */
	private Feature [] accessibleFeature;
	/** The underlying BasicFeatures object. */
	private BasicFeatures basic_feat;
	
	/**
	 * Constructs a FeatureManager object for 1 BasicFeature. It is unwise and inefficient to construct
	 * more than 1 FeatureManager for 1 position.
	 * @param b_feat
	 */
	public FeatureManager (BasicFeatures b_feat) {
		accessibleFeature = new Feature [NUM_FEATURES];
		basic_feat = b_feat;
	}
	/**
	 * Retrieves the value of one of the feature components. 
	 * @param id The feature ID, from 0 to 3, 1 being Dynamic, 2 being Piece, 3 being Structural, 4 being
	 * mobility.
	 * @param componentName The name of the component.
	 * @return The value of the component.
	 * @throws IllegalArgumentException If the Feature or the component does not exist, or the specified
	 * feature cannot render the component.
	 */
	public String retrieveFeatureComponent(byte id, String componentName) throws IllegalArgumentException{
		if (id > NUM_FEATURES) throw new IllegalArgumentException();
		Feature currentFeature = accessibleFeature [id];
		if (currentFeature == null){
			switch (id){
				case 0: accessibleFeature[id] = new DynamicFeatures(basic_feat); break;
				case 1: accessibleFeature[id] = new PieceFeatures(basic_feat); break;
				case 2: accessibleFeature[id] = new StructuralFeatures(basic_feat); break;
				case 3: accessibleFeature[id] = new MobilityFeatures(basic_feat); break;
			}
		}
		try {
			String toReturn = currentFeature.retrieveFeatureComponent(componentName);
			return toReturn;
		} catch (Exception ex){
			throw new IllegalArgumentException();
		}
	}
}
