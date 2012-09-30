package gov.nasa.pds.objectAccess.table;

import java.nio.charset.Charset;


/**
 * Defines an adapter object for table fields that can
 * read the field value from a byte array or write the
 * field value into a byte array. Also has methods for
 * formatting the value into an output writer for either
 * delimited or fixed-width output. Methods not appropriate
 * for the field type will throw {@link java.lang.UnsupportedOperationException}.
 */
public interface FieldAdapter {
	
	/**
	 * Gets the field value as a Java byte.
	 * 
	 * @param buf the byte array containing the field
	 * @param offset the field offset
	 * @param length the length of the field data
	 * @param startBit the start bit, for bit fields
	 * @param stopBit the stop bit, for bit fields
	 * @return the field value, as a short
	 */
	byte getByte(byte[] buf, int offset, int length, int startBit, int stopBit);
	
	/**
	 * Gets the field value as a Java short.
	 * 
	 * @param buf the byte array containing the field
	 * @param offset the field offset
	 * @param length the length of the field data
	 * @param startBit the start bit, for bit fields
	 * @param stopBit the stop bit, for bit fields
	 * @return the field value, as a short
	 */
	short getShort(byte[] buf, int offset, int length, int startBit, int stopBit);
	
	/**
	 * Gets the field value as a Java int.
	 * 
	 * @param buf the byte array containing the field
	 * @param offset the field offset
	 * @param length the length of the field data
	 * @param startBit the start bit, for bit fields
	 * @param stopBit the stop bit, for bit fields
	 * @return the field value, as an int
	 */
	int getInt(byte[] buf, int offset, int length, int startBit, int stopBit);
	
	/**
	 * Gets the field value as a Java long.
	 * 
	 * @param buf the byte array containing the field
	 * @param offset the field offset
	 * @param length the length of the field data
	 * @param startBit the start bit, for bit fields
	 * @param stopBit the stop bit, for bit fields
	 * @return the field value, as a long
	 */
	long getLong(byte[] buf, int offset, int length, int startBit, int stopBit);
	
	/**
	 * Gets the field value as a Java float.
	 * 
	 * @param buf the byte array containing the field
	 * @param offset the field offset
	 * @param length the length of the field data
	 * @param startBit the start bit, for bit fields
	 * @param stopBit the stop bit, for bit fields
	 * @return the field value, as a float
	 */
	float getFloat(byte[] buf, int offset, int length, int startBit, int stopBit);
	
	/**
	 * Gets the field value as a Java double.
	 * 
	 * @param buf the byte array containing the field
	 * @param offset the field offset
	 * @param length the length of the field data
	 * @param startBit the start bit, for bit fields
	 * @param stopBit the stop bit, for bit fields
	 * @return the field value, as a double
	 */
	double getDouble(byte[] buf, int offset, int length, int startBit, int stopBit);
	
	/**
	 * Gets the field value as a Java string, using the US-ASCII
	 * character set to convert from bytes to characters.
	 * 
	 * @param buf the byte array containing the field
	 * @param offset the field offset
	 * @param length the length of the field data
	 * @param startBit the start bit, for bit fields
	 * @param stopBit the stop bit, for bit fields
	 * @return the field value, as a string
	 */
	String getString(byte[] buf, int offset, int length, int startBit, int stopBit);
	
	/**
	 * Gets the field value as a Java string, using a specified
	 * character set to convert from bytes to characters.
	 * 
	 * @param buf the byte array containing the field
	 * @param offset the field offset
	 * @param length the length of the field data
	 * @param startBit the start bit, for bit fields
	 * @param stopBit the stop bit, for bit fields
	 * @param charset the character set to use when converting bytes to characters
	 * @return the field value, as a string
	 */
	String getString(byte[] buf, int offset, int length, int startBit, int stopBit, Charset charset);

}
