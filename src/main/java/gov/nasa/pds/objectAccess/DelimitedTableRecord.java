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
package gov.nasa.pds.objectAccess;

import gov.nasa.pds.label.object.TableRecord;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements a delimited table record.
 */
public class DelimitedTableRecord implements TableRecord {

	private int fieldCount;
	private List<String> items;
	private String[] recordValue = null;
	private Map<String, Integer> fieldMap = new HashMap<String, Integer>();

	private static final Logger LOGGER = LoggerFactory.getLogger(DelimitedTableRecord.class);

	/**
	 * Creates an instance of <code>DelimitedTableRecord</code> for writing field
	 * values to a delimited table record.
	 *
	 * @param map a hash mapping field name to field index
	 * @param fieldCount the number of fields
	 */
	DelimitedTableRecord(Map<String, Integer> map, int fieldCount) {
		// TODO: set charset?
		this.fieldMap = map;
		this.fieldCount = fieldCount;
		this.items = new ArrayList<String>(fieldCount);
		initializeItems();
	}

	/**
	 * Creates an instance of <code>DelimitedTableRecord</code>
	 * for reading field values of a delimited table record.
	 *
	 * @param map a hash mapping field name to field index
	 * @param fieldCount the number of fields
	 * @param value String array representing the record value
	 */
	DelimitedTableRecord(Map<String, Integer> map, int fieldCount, String[] value) {
		this.fieldMap = map;
		this.fieldCount = fieldCount;
		setRecordValue(value);
	}

	@Override
	public int findColumn(String name) {
		checkFieldName(name);
		return this.fieldMap.get(name);
	}

	@Override
	public boolean getBoolean(String name) {
		checkFieldName(name);
		return getBoolean(this.fieldMap.get(name));
	}

	@Override
	public boolean getBoolean(int index) {
		checkIndexRange(index);
		String value = recordValue[index-1].trim();
		if (value.equals("true") || value.equals("1")) {
			return true;
		} else if (value.equals("false") || value.equals("0")) {
			return false;
		} else {
			throw new IllegalArgumentException("Field value is not a valid boolean (" + value + ")");
		}
	}

	@Override
	public short getShort(int index) {
		checkIndexRange(index);
		int value = Integer.parseInt(recordValue[index-1].trim());
		if (value < Short.MIN_VALUE || value > Short.MAX_VALUE) {
			throw new NumberFormatException("Value is out of range of a short (" + value + ")");
		}

		return (short) value;
	}

	@Override
	public short getShort(String name) {
		checkFieldName(name);
		return getShort(this.fieldMap.get(name));
	}

	@Override
	public byte getByte(int index) {
		checkIndexRange(index);
		int value = Integer.parseInt(recordValue[index-1].trim());
		if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE) {
			throw new NumberFormatException("Value is out of range of a byte (" + value + ")");
		}

		return (byte) value;
	}

	@Override
	public byte getByte(String name) {
		checkFieldName(name);
		return getByte(this.fieldMap.get(name));
	}

	@Override
	public long getLong(int index) {
		checkIndexRange(index);
		return Long.parseLong(recordValue[index-1].trim());

	}

	@Override
	public long getLong(String name) {
		checkFieldName(name);
		return getLong(this.fieldMap.get(name));
	}

	@Override
	public int getInt(int index) {
		checkIndexRange(index);
		return Integer.parseInt(recordValue[index-1].trim());
	}

	@Override
	public int getInt(String name) {
		checkFieldName(name);
		return getInt(this.fieldMap.get(name));
	}

	@Override
	public double getDouble(int index) {
		checkIndexRange(index);
		return Double.parseDouble(recordValue[index-1].trim());
	}

	@Override
	public double getDouble(String name) {
		checkFieldName(name);
		return getDouble(this.fieldMap.get(name));
	}

	@Override
	public float getFloat(int index) {
		checkIndexRange(index);
		return Float.parseFloat(recordValue[index-1].trim());
	}

	@Override
	public float getFloat(String name) {
		checkFieldName(name);
		return getFloat(this.fieldMap.get(name));
	}

	@Override
	public BigInteger getBigInteger(String name) {
		checkFieldName(name);
		return getBigInteger(this.fieldMap.get(name));
	}

	@Override
	public BigInteger getBigInteger(int index) {
		checkIndexRange(index);
		return new BigInteger(recordValue[index-1]);
	}

	@Override
	public String getString(int index) {
		checkIndexRange(index);
		return recordValue[index-1];
	}

	@Override
	public String getString(String name) {
		checkFieldName(name);
		return getString(this.fieldMap.get(name));
	}

	@Override
	public void setString(int index, String value) {
		checkIndexRange(index);
		items.remove(index-1);
		items.add(index-1, value);
	}

	@Override
	public void setString(String name, String value) {
		checkFieldName(name);
		setString(this.fieldMap.get(name), value);
	}

	@Override
	public void setString(String value) {
		throw new UnsupportedOperationException("Operation not supported.");
	}

	@Override
	public void setInt(int index, int value) {
		checkIndexRange(index);
		items.remove(index-1);
		items.add(index-1, Integer.toString(value));
	}

	@Override
	public void setInt(String name, int value) {
		checkFieldName(name);
		setInt(this.fieldMap.get(name), value);

	}

	@Override
	public void setDouble(int index, double value) {
		checkIndexRange(index);
		items.remove(index-1);
		items.add(index-1, Double.toString(value));
	}

	@Override
	public void setDouble(String name, double value) {
		checkFieldName(name);
		setDouble(this.fieldMap.get(name), value);
	}

	@Override
	public void setFloat(int index, float value) {
		checkIndexRange(index);
		items.remove(index-1);
		items.add(index-1, Float.toString(value));
	}

	@Override
	public void setFloat(String name, float value) {
		checkFieldName(name);
		setFloat(this.fieldMap.get(name), value);
	}

	@Override
	public void setLong(int index, long value) {
		checkIndexRange(index);
		items.remove(index-1);
		items.add(index-1, Long.toString(value));
	}

	@Override
	public void setLong(String name, long value) {
		checkFieldName(name);
		setLong(this.fieldMap.get(name), value);
	}

	@Override
	public void setShort(int index, short value) {
		checkIndexRange(index);
		items.remove(index-1);
		items.add(index-1, Short.toString(value));
	}

	@Override
	public void setShort(String name, short value) {
		checkFieldName(name);
		setShort(this.fieldMap.get(name), value);
	}

	@Override
	public void setByte(int index, byte value) {
		checkIndexRange(index);
		items.remove(index-1);
		items.add(index-1, Byte.toString(value));
	}

	@Override
	public void setByte(String name, byte value) {
		checkFieldName(name);
		setByte(this.fieldMap.get(name), value);
	}

	@Override
	public void clear() {
		items.clear();
		initializeItems();
	}

	/**
	 * Sets the record value.
	 *
	 * @param value String array that contains record value
	 */
	public void setRecordValue(String[] value) {
		this.recordValue = value;
	}

	/**
	 * Returns a string array that contains the record value.
	 *
	 * @return a String array that contains record value
	 */
	public String[] getRecordValue() {
		return items.toArray(new String[items.size()]);
	}

	private void checkIndexRange(int index) {
		if (index <= 0 || index > fieldCount) {
			String msg = "The index is out of range (1 to " + fieldCount + ").";
			LOGGER.error(msg);
			throw new ArrayIndexOutOfBoundsException(msg);
		}
	}

	private void initializeItems() {
		for (int i = 0; i < fieldCount; i++) {
			items.add("");
		}
	}

	private void checkFieldName(String name) {
		if (!this.fieldMap.containsKey(name)) {
			String msg = "'" + name + "' is not a valid field name.";
			LOGGER.error(msg);
			throw new IllegalArgumentException(msg);
		}
	}

}
