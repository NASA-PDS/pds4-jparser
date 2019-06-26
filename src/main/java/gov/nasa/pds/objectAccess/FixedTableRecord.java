// Copyright 2019, California Institute of Technology ("Caltech").
// U.S. Government sponsorship acknowledged.
//
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// * Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
// * Redistributions must reproduce the above copyright notice, this list of
// conditions and the following disclaimer in the documentation and/or other
// materials provided with the distribution.
// * Neither the name of Caltech nor its operating division, the Jet Propulsion
// Laboratory, nor the names of its contributors may be used to endorse or
// promote products derived from this software without specific prior written
// permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package gov.nasa.pds.objectAccess;

import gov.nasa.pds.label.object.FieldDescription;
import gov.nasa.pds.label.object.FieldType;
import gov.nasa.pds.label.object.RecordLocation;
import gov.nasa.pds.label.object.TableRecord;
import gov.nasa.pds.objectAccess.table.DefaultFieldAdapter;
import gov.nasa.pds.objectAccess.table.DelimiterType;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements a fixed-width table record.
 */
public class FixedTableRecord implements TableRecord {
	private Charset charset;
	private ByteBuffer buffer = null;
	private byte[] recordBytes = null;
	private Map<String, Integer> fieldMap = new HashMap<String, Integer>();
	private FieldDescription[] fields;
	private RecordLocation location;

	private static final Logger LOGGER = LoggerFactory.getLogger(FixedTableRecord.class);

	/**
	 * Creates an instance of <code>FixedTableRecord</code>
	 * for writing field values to a fixed-width table record.
	 *
	 * @param length   the record length in bytes
	 * @param map      a hash mapping field name to field index
	 * @param fields   an array of field descriptions (field meta data)
	 * @param charset  the character set to use for encoding the bytes
	 * @param isBinary flag indicating the type of the data file.
	 * 				   Use true for binary and false for text file.
	 */
	FixedTableRecord(int length, Map<String, Integer> map, FieldDescription[] fields, Charset charset, boolean isBinary) {
		this.charset = charset;
		this.fieldMap = map;
		this.fields = fields;
		this.buffer = ByteBuffer.allocate(length);
		this.location = null;

		if (!isBinary) {
			String delimiter = DelimiterType.CARRIAGE_RETURN_LINE_FEED.getRecordDelimiter();

			// Populate record with space
			Arrays.fill(this.buffer.array(), (byte) 0x20);

			// Set the buffer position based on the length of the record delimiter
			// and add the delimiter at that position to the end of the record.
			this.buffer.position(length - delimiter.getBytes(charset).length);
			setString(delimiter);
		}
	}

	/**
	 * Creates an instance of <code>FixedTableRecord</code>
	 * for reading field values of a fixed-width table record.
	 *
	 * @param value  the record value
	 * @param map    a hash mapping field name to field index
	 * @param fields an array of field descriptions (field meta data)
	 */
	public FixedTableRecord(byte[] value, Map<String, Integer> map, FieldDescription[] fields) {
		this.fieldMap = map;
		this.fields = fields;
		this.location = null;
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
		FieldDescription field = getField(index);
		String value = field.getType().getAdapter().getString(recordBytes, field.getOffset(), field.getLength(), field.getStartBit(), field.getStopBit()).trim();
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
		FieldDescription field = getField(index);
		return field.getType().getAdapter().getShort(recordBytes, field.getOffset(), field.getLength(), field.getStartBit(), field.getStopBit());
	}

	@Override
	public short getShort(String name) {
		checkFieldName(name);
		return getShort(this.fieldMap.get(name));
	}

	@Override
	public byte getByte(int index) {
		FieldDescription field = getField(index);
		return field.getType().getAdapter().getByte(recordBytes, field.getOffset(), field.getLength(), field.getStartBit(), field.getStopBit());
	}

	@Override
	public byte getByte(String name) {
		checkFieldName(name);
		return getByte(this.fieldMap.get(name));
	}

	@Override
	public long getLong(int index) {
		FieldDescription field = getField(index);
		return field.getType().getAdapter().getLong(recordBytes, field.getOffset(), field.getLength(), field.getStartBit(), field.getStopBit());
	}

	@Override
	public long getLong(String name) {
		checkFieldName(name);
		return getLong(this.fieldMap.get(name));
	}

	@Override
	public int getInt(String name) {
		checkFieldName(name);
		return getInt(this.fieldMap.get(name));
	}

	@Override
	public int getInt(int index) {
		FieldDescription field = getField(index);
		return field.getType().getAdapter().getInt(recordBytes, field.getOffset(), field.getLength(), field.getStartBit(), field.getStopBit());
	}

	@Override
	public BigInteger getBigInteger(String name) {
		checkFieldName(name);
		return getBigInteger(this.fieldMap.get(name));
	}

	@Override
	public BigInteger getBigInteger(int index) {
		FieldDescription field = getField(index);
		return field.getType().getAdapter().getBigInteger(recordBytes, field.getOffset(), field.getLength(), field.getStartBit(), field.getStopBit());
	}

	@Override
	public double getDouble(String name) {
		checkFieldName(name);
		return getDouble(this.fieldMap.get(name));
	}

	@Override
	public double getDouble(int index) {
		FieldDescription field = getField(index);
		return field.getType().getAdapter().getDouble(recordBytes, field.getOffset(), field.getLength(), field.getStartBit(), field.getStopBit());
	}

	@Override
	public float getFloat(String name) {
		checkFieldName(name);
		return getFloat(this.fieldMap.get(name));
	}

	@Override
	public float getFloat(int index) {
		FieldDescription field = getField(index);
		return field.getType().getAdapter().getFloat(recordBytes, field.getOffset(), field.getLength(), field.getStartBit(), field.getStopBit());
	}

	@Override
	public String getString(String name) {
		checkFieldName(name);
		return getString(this.fieldMap.get(name));
	}
	
	public String getString(String name, Charset charset) {
    checkFieldName(name);
    return getString(this.fieldMap.get(name), charset);	  
	}

	@Override
	public String getString(int index) {
		FieldDescription field = getField(index);
		return field.getType().getAdapter().getString(recordBytes, field.getOffset(), field.getLength(), field.getStartBit(), field.getStopBit());
	}
	
  public String getString(int index, Charset charset) {
    FieldDescription field = getField(index);
    return field.getType().getAdapter().getString(recordBytes, field.getOffset(), field.getLength(), field.getStartBit(), field.getStopBit(), charset);
  }	

	@Override
	public void setString(int index, String value) {
		FieldDescription field = getField(index);
		FieldType type = field.getType();
		type.getAdapter().setString(value, field.getOffset(), field.getLength(), buffer, type.isRightJustified(), this.charset);
	}

	@Override
	public void setString(String name, String value) {
		checkFieldName(name);
		setString(this.fieldMap.get(name), value);
	}

	@Override
	public void setString(String value) {
		DefaultFieldAdapter adapter = new DefaultFieldAdapter();
		adapter.setString(value, buffer, this.charset);
	}

	@Override
	public void setInt(int index, int value) {
		FieldDescription field = getField(index);
		FieldType type = field.getType();
		type.getAdapter().setInt(value, field.getOffset(), field.getLength(), buffer, type.isRightJustified());
	}

	@Override
	public void setInt(String name, int value) {
		checkFieldName(name);
		setInt(this.fieldMap.get(name), value);
	}

	@Override
	public void setDouble(int index, double value) {
		FieldDescription field = getField(index);
		FieldType type = field.getType();
		type.getAdapter().setDouble(value, field.getOffset(), field.getLength(), buffer, type.isRightJustified());
	}

	@Override
	public void setDouble(String name, double value) {
		checkFieldName(name);
		setDouble(this.fieldMap.get(name), value);
	}

	@Override
	public void setFloat(int index, float value) {
		FieldDescription field = getField(index);
		FieldType type = field.getType();
		type.getAdapter().setFloat(value, field.getOffset(), field.getLength(), buffer, type.isRightJustified());
	}

	@Override
	public void setFloat(String name, float value) {
		checkFieldName(name);
		setFloat(this.fieldMap.get(name), value);
	}

	@Override
	public void setLong(int index, long value) {
		FieldDescription field = getField(index);
		FieldType type = field.getType();
		type.getAdapter().setLong(value, field.getOffset(), field.getLength(), buffer, type.isRightJustified());
	}

	@Override
	public void setLong(String name, long value) {
		checkFieldName(name);
		setLong(this.fieldMap.get(name), value);
	}

	@Override
	public void setShort(int index, short value) {
		FieldDescription field = getField(index);
		FieldType type = field.getType();
		type.getAdapter().setShort(value, field.getOffset(), field.getLength(), buffer, type.isRightJustified());
	}

	@Override
	public void setShort(String name, short value) {
		checkFieldName(name);
		setShort(this.fieldMap.get(name), value);
	}

	@Override
	public void setByte(int index, byte value) {
		FieldDescription field = getField(index);
		FieldType type = field.getType();
		type.getAdapter().setByte(value, field.getOffset(), field.getLength(), buffer, type.isRightJustified());
	}

	@Override
	public void setByte(String name, byte value) {
		checkFieldName(name);
		setByte(this.fieldMap.get(name), value);
	}

	@Override
	public void clear() {
		buffer.clear();
	}

	/**
	 * Sets the record value.
	 *
	 * @param value byte array that contains the record value
	 */
	public void setRecordValue(byte[] value) {
		this.recordBytes = value;
	}

	/**
	 * Returns a byte array that stores the record value.
	 *
	 * @return a byte array that contains the record value
	 */
	public byte[] getRecordValue() {
		return buffer.array();
	}

	private FieldDescription getField(int index) {
		if (index <= 0 || index > fields.length) {
			String msg = "The index is out of range (1 to " + fields.length + ").";
			LOGGER.error(msg);
			throw new ArrayIndexOutOfBoundsException(msg);
		}

		return fields[index - 1];
	}

	private void checkFieldName(String name) {
		if (!this.fieldMap.containsKey(name)) {
			String msg = "'" + name + "' is not a valid field name.";
			LOGGER.error(msg);
			throw new IllegalArgumentException(msg);
		}
	}

  @Override
  public RecordLocation getLocation() {
    return this.location;
  }

  @Override
  public void setLocation(RecordLocation location) {
    this.location = location;
  }
}
