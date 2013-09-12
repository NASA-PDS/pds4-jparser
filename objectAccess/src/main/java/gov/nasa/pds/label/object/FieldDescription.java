package gov.nasa.pds.label.object;


/**
 * Implements a description of a table field.
 */
public class FieldDescription {

	private String name;
	private FieldType type;
	private int offset;
	private int length;
	private int startBit;
	private int stopBit;
	
	/**
	 * Gets the field name.
	 * 
	 * @return the field name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the field name.
	 * 
	 * @param name the new field name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Gets the field type.
	 * 
	 * @return the field type
	 */
	public FieldType getType() {
		return type;
	}
	
	/**
	 * Sets the field type.
	 * 
	 * @param type the new field type
	 */
	public void setType(FieldType type) {
		this.type = type;
	}
	
	/**
	 * Gets the field offset, the number of bytes
	 * past the beginning of the record where the
	 * field starts. The offset is not used for
	 * delimited tables.
	 * 
	 * @return the field offset
	 */
	public int getOffset() {
		return offset;
	}
	
	/**
	 * Sets the field offset.
	 * 
	 * @param offset the field offset
	 */
	public void setOffset(int offset) {
		this.offset = offset;
	}
	
	/**
	 * Gets the field length, in bytes. The field
	 * length is not used for fields of delimited
	 * tables.
	 * 
	 * @return the field length
	 */
	public int getLength() {
		return length;
	}
	
	/**
	 * Sets the field length, in bytes.
	 * 
	 * @param length the new field length
	 */
	public void setLength(int length) {
		this.length = length;
	}

	/**
	 * Gets the start bit, for bit fields. Bits
	 * are counted from left to right, where zero
	 * is the leftmost bit.
	 * 
	 * @return the start bit
	 */
	public int getStartBit() {
		return startBit;
	}

	/**
	 * Sets the start bit, for bit fields.
	 * 
	 * @param startBit the new start bit
	 */
	public void setStartBit(int startBit) {
		this.startBit = startBit;
	}

	/**
	 * Gets the stop bit, for bit fields.
	 * 
	 * @return the stop bit
	 */
	public int getStopBit() {
		return stopBit;
	}

	/**
	 * Sets the stop bit, for bit fields.
	 * 
	 * @param stopBit the new stop bit
	 */
	public void setStopBit(int stopBit) {
		this.stopBit = stopBit;
	}
	
}
