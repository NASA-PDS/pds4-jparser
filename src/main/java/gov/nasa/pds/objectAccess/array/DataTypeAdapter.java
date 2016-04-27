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
package gov.nasa.pds.objectAccess.array;

import java.nio.ByteBuffer;

/**
 * Defines an interface for getting a data type in various formats.
 */
public interface DataTypeAdapter {

	/**
	 * Gets the value as an int.
	 * 
	 * @param buf the buffer from which to get the value
	 * @return the value, as an int
	 */
	int getInt(ByteBuffer buf);
	
	/**
	 * Gets the value as a long.
	 * 
	 * @param buf the buffer from which to get the value
	 * @return the value, as a long
	 */
	long getLong(ByteBuffer buf);
	
	/**
	 * Gets the value as a double.
	 * 
	 * @param buf the buffer from which to get the value
	 * @return the value, as a double
	 */
	double getDouble(ByteBuffer buf);
	
}
