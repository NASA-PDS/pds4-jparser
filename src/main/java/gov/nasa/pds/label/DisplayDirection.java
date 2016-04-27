// Copyright 2006-2016, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
// Any commercial use must be negotiated with the Office of Technology Transfer
// at the California Institute of Technology.
//
// This software is subject to U. S. export control laws and regulations
// (22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software
// is subject to U.S. export control laws and regulations, the recipient has
// the responsibility to obtain export licenses or other export authority as
// may be required before exporting such information to foreign countries or
// providing access to foreign nationals.
//
// $Id$
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
