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
