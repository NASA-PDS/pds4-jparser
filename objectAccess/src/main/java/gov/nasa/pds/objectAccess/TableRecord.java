package gov.nasa.pds.objectAccess;

import gov.nasa.pds.objectAccess.table.DelimiterType;
import gov.nasa.pds.objectAccess.table.FieldDescription;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that represents a record in a tabular data file.
 * 
 * @author psarram
 */
public class TableRecord {	
	private Charset charset;
	private String type;
	private ByteBuffer buffer = null;
	private byte[] recordBytes = null;
	private List<String> items = new ArrayList<String>();
	private Map<String, Integer> fieldMap = new HashMap<String, Integer>();
	private FieldDescription[] fields;
		
	private static final Logger LOGGER = LoggerFactory.getLogger(TableRecord.class);
	private static final String CHARACTER = "character"; 
	private static final String BINARY = "binary"; 
	private static final String DELIMITED = "delimited";
	
	/**
	 * Constructs a <code>TableRecord</code> instance used for writing a record to a delimited file.
	 */
	public TableRecord() {	
		// TODO: should we consider charset here?
		this.type= DELIMITED;					
	}
		
	/**
	 * Constructs a <code>TableRecord</code> instance used for writing a record to a fixed-width text or binary file.
	 * 
	 * @param length   the record length in bytes
	 * @param charset  the character set to use for encoding the bytes
	 * @param isBinary flag indicating the type of data file. Use true for binary and false for text file. 
	 */
	public TableRecord(int length, Charset charset, boolean isBinary) {			
		this.charset = charset;		
		this.buffer = ByteBuffer.allocate(length);
		
		if (isBinary) {
			this.type = BINARY;
		} else {
			this.type= CHARACTER;
			String delimiter = DelimiterType.CARRIAGE_RETURN_LINE_FEED.getRecordDelimiter();
			
			// Populate the record with space
			Arrays.fill(this.buffer.array(), (byte) 0x20);								
			
			// Set the buffer position based on the length of the record delimiter 
			// and add the delimiter at that position to the end of the record.
			this.buffer.position(length - delimiter.getBytes().length);
			add(delimiter);
		}		
	}
	
	/**
	 * Constructs a <code>TableRecord</code> instance used for reading field values.
	 * 
	 * @param recordBytes
	 * @param map
	 * @param metaData
	 */
	public TableRecord(byte[] recordBytes, Map<String, Integer> map, FieldDescription[] fields) {
		this.fieldMap = map;
		this.fields = fields;
		setRecordBytes(recordBytes);
	}
	
	/**
	 * Sets the record bytes. 
	 * 
	 * @param recordBytes byte array representing the record's value
	 */
	public void setRecordBytes(byte[] recordBytes) {
		this.recordBytes = recordBytes;
	}
	
	/**
	 * Gets the integer index of a field with the given name (1-relative).
	 * 
	 * @param name the name of a field
	 * @return the integer index of a field (1-relative)
	 */
	public int findColumn(String name) {
		return this.fieldMap.get(name);
	}
	
	/**
	 * Gets the short value of a field given the index.
	 * 
	 * @param index the column index (1-relative)
	 * @return a short value
	 * @throws IllegalArgumentException If cannot convert the number to short
	 */
	public short getShort(int index) {
		FieldDescription field = getField(index);
		return field.getType().getAdapter().getShort(recordBytes, field.getOffset(), field.getLength(), field.getStartBit(), field.getStopBit());
	}
	
	/**
	 * Gets the short value of a field given the name.
	 * 
	 * @param name the field name
	 * @return a short value
	 */
	public short getShort(String name) {
		return getShort(this.fieldMap.get(name));
	}

	/**
	 * Gets the byte value of a field given the index.
	 * 
	 * @param index the field index (1-relative)
	 * @return a byte value
	 * @throws IllegalArgumentException If cannot convert the number to byte
	 */
	public byte getByte(int index) {
		FieldDescription field = getField(index);
		return field.getType().getAdapter().getByte(recordBytes, field.getOffset(), field.getLength(), field.getStartBit(), field.getStopBit());
	}
	
	/**
	 * Gets the byte value of a field given the name.
	 * 
	 * @param name the field name
	 * @return a byte value
	 */
	public byte getByte(String name) {
		return getByte(this.fieldMap.get(name));
	}

	/**
	 * Gets the long value of a field given the index.
	 * 
	 * <p>It throws NumberFormatException if the number is out of range when converting from ASCII_Integer to long.</p>
	 * 
	 * @param index the field index (1-relative)
	 * @return a long value
	 * @throws IllegalArgumentException If cannot convert the number to long
	 * @throws NumberFormatException If the number is out of range for a long
	 */
	public long getLong(int index) {
		FieldDescription field = getField(index);
		return field.getType().getAdapter().getLong(recordBytes, field.getOffset(), field.getLength(), field.getStartBit(), field.getStopBit());
	}
	
	/**
	 * Gets the long value of a field given the name.
	 * 
	 * @param name the field name
	 * @return a long value
	 */
	public long getLong(String name) {
		return getLong(this.fieldMap.get(name));
	}	

	/**
	 * Gets the integer value of a field given the name.
	 * 
	 * @param name the field name
	 * @return an integer value
	 */
	public int getInt(String name) {
		return getInt(this.fieldMap.get(name));
	}
	
	/**
	 * Gets the integer value of a field given the index.
	 * 
	 * <p>It throws NumberFormatException if the number is out of range when converting from ASCII_Integer to int.</p>
	 * 
	 * @param index the field index (1-relative)
	 * @return an integer value
	 * @throws IllegalArgumentException If cannot convert the number to int
	 * @throws NumberFormatException If the number is out of range for an int
	 */
	public int getInt(int index) {
		FieldDescription field = getField(index);
		return field.getType().getAdapter().getInt(recordBytes, field.getOffset(), field.getLength(), field.getStartBit(), field.getStopBit());
	}
	
	/**
	 * Gets the double value of a field given the name.
	 * 
	 * @param name the field name
	 * @return a double value
	 */
	public double getDouble(String name) {		
		return getDouble(this.fieldMap.get(name));
	}
	
	/**
	 * Gets the double value of a field given the index.
	 * 
	 * <p> If the number is out of range when converting from ASCII_Real to double, the method returns 
	 * Double.POSITIVE_INFINITY or Double.NEGATIVE_INFINITY.</p>
	 * 
	 * @param index the field index (1-relative)
	 * @return a double value
	 * @throws IllegalArgumentException If cannot convert the number to double
	 */
	public double getDouble(int index) {
		FieldDescription field = getField(index);
		return field.getType().getAdapter().getDouble(recordBytes, field.getOffset(), field.getLength(), field.getStartBit(), field.getStopBit());
	}
	
	/**
	 * Gets the float value of a field given the name.
	 * 
	 * @param name the field name
	 * @return a float value
	 */
	public float getFloat(String name) {
		return getFloat(this.fieldMap.get(name));
	}
	
	/**
	 * Gets the float value of a field given the index.
	 * 
	 * <p> If the number is out of range when converting from ASCII_Real to float, the method returns 
	 * Float.POSITIVE_INFINITY or Float.NEGATIVE_INFINITY.</p>
	 * 
	 * @param index the field index (1-relative)
	 * @return a float value
	 * @throws IllegalArgumentException If cannot convert the number to float
	 */
	public float getFloat(int index) {
		FieldDescription field = getField(index);
		return field.getType().getAdapter().getFloat(recordBytes, field.getOffset(), field.getLength(), field.getStartBit(), field.getStopBit());
	}
	
	/**
	 * Gets the string value of a field given the name.
	 * 
	 * @param name the field name
	 * @return a String value
	 */
	public String getString(String name) {			
		return getString(this.fieldMap.get(name));
	}
	
	/**
	 * Gets the string value of a field given the index.
	 * 
	 * @param index the field index (1-relative)
	 * @return a String value
	 * @throws IllegalArgumentException If data type is not supported
	 */
	public String getString(int index) {		
		FieldDescription field = getField(index);
		return field.getType().getAdapter().getString(recordBytes, field.getOffset(), field.getLength(), field.getStartBit(), field.getStopBit());
	}
	
	/**
	 * Adds a string value to the record at the given index.
	 * Justifies the value to the right or left.
	 * 
	 * @param index the index at which the value will be written
	 * @param value  the String value to be added
	 * @param length the length of the field in bytes
	 * @param align  the text alignment. The supported values are "right" and "left".
	 *               Use "right" alignment for numeric values and "left" for non-numeric values. 
	 * @throws IllegalArgumentException If the size of the value is greater than the field length              
	 */
	public void add(int index, String value, int length, String align) {
		assert value != null;
		int len = value.length();
		byte[] bytes = null;
		
		// TODO: check the 'align' parameter
		
		if (len > length) {
			String msg = "The size of the value cannot be greater than field length.";
			LOGGER.error(msg);
			throw new IllegalArgumentException(msg);
		} else if (len < length) {			
			// Add empty space for left/right justification
			StringBuffer sb = new StringBuffer();
			int diff = length - len;			
			
			if (align.equalsIgnoreCase("RIGHT")) {
				for (int i = 0; i < diff; i++) {
					sb.append(" ");
				}						
				sb.append(value);
			} else {
				sb.append(value);
				for (int i = 0; i < diff; i++) {
					sb.append(" ");
				}
			}
			
			bytes = sb.toString().getBytes(this.charset);			
		} else {
			bytes = value.getBytes(this.charset);
		}
		
		buffer.position(index);
		buffer.put(bytes, 0, length);
	}
	
	/**
	 * Adds a string value to the record at the given index.
	 * 
	 * @param index the index at which the value will be written
	 * @param value  the string value to be added
	 * @param length the length of the field in bytes
	 * @throws IllegalArgumentException If the size of the value is greater than the field length
	 */
	public void add(int index, String value, int length) {
		assert value != null;
		
		if (value.length() > length) {
			String msg = "The size of the value cannot be greater than field length.";
			LOGGER.error(msg);
			throw new IllegalArgumentException(msg);
		}
		
		buffer.position(index);
		buffer.put(value.getBytes(this.charset), 0, length);
	}	
	
	/**
	 * Adds a String value to the record at the current offset.
	 * 
	 * @param value the String value to be added
	 */
	public void add(String value) {
		if (this.type.equals(DELIMITED)) {
			items.add(value);			
		} else {
			buffer.put(value.getBytes(this.charset));
		}		
	}
		
	/**
	 * Adds four bytes containing the value to this record at the given index.
     *
	 * @param index the index at which the value will be written
	 * @param value  the integer value to be added
	 */
	public void addInt(int index, int value) {
		buffer.putInt(index, value);
	}
	
	/**
	 * Adds eight bytes containing the double value to this record at the given index.
	 * 
	 * @param index the index at which the value will be written
	 * @param value  the double value to be added
	 */
	public void addDouble(int index, double value) {
		buffer.putDouble(index, value); 
	}
	
	/**
	 * Adds four bytes containing the float value to this record at the given index.
	 * 
	 * @param index the index at which the value will be written
	 * @param value  the float value to be added
	 */
	public void addFloat(int index, float value) {
		buffer.putFloat(index, value); 
	}
	
	/**
	 * Adds two bytes containing the short value to this record at the given index.
	 * 
	 * @param index the index at which the value will be written
	 * @param value  the short value to be added	
	 */
	public void addShort(int index, short value) {
		buffer.putShort(index, value);
	}
	
	/**
	 * Adds one byte to this record at the given index.
	 * 
	 * @param index the index at which the value will be written
	 * @param value  the byte value to be added 	 
	 */
	public void addByte(int index, byte value) {
		buffer.put(index, value);
	}
	
	/**
	 * Clears the content of the <code>TableRecord</code> object. 
	 */
	public void clear() {
		if (this.type.equals(DELIMITED)) {
			items.clear();
		} else { 
			buffer.clear();
		}			
	}
	
	/**
	 * Returns a byte array that stores the record's values.
	 * 
	 * @return a byte array that contains the record's values 
	 */
	public byte[] getBuffer() {
		return buffer.array();
	}
	
	/**
	 * Returns an array that stores the record's values.
	 * 
	 * @return a String array that contains record's values
	 */
	public String[] getItems() {			
		return items.toArray(new String[items.size()]);
	}
	
	private FieldDescription getField(int index) {
		if (index <= 0 || index > fields.length) {
			String msg = "The index is out of range (1 to " + fields.length + ").";
			throw new ArrayIndexOutOfBoundsException(msg);
		}
		
		return fields[index - 1];
	}
	
}
