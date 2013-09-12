package gov.nasa.pds.label;

/**
 * Defines the various display directions that can be used by
 * axes within arrays and images.
 */
public enum DisplayDirection {

	/** Display is right-to-left. */
	LEFT("Left"),

	/** Display is left-to-right. */
	RIGHT("Right"),

	/** Display is bottom-to-top. */
	UP("Up"),

	/** Display is top-to-bottom. */
	DOWN("Down");

	private String elementValue;

	private DisplayDirection(String elementValue) {
		this.elementValue = elementValue;
	}

	/**
	 * Looks up a display direction based on the value within the metadata.
	 *
	 * @param value the metadata value
	 * @return the display direciton corresponding to the metadata value, or null if not found
	 */
	public static DisplayDirection getDirectionFromValue(String value) {
		for (DisplayDirection dir : DisplayDirection.values()) {
			if (dir.elementValue.equalsIgnoreCase(value)) {
				return dir;
			}
		}

		return null;
	}

}
