package gov.nasa.pds.objectAccess.table;

import java.nio.charset.Charset;

/**
 * Implements a field adapter for binary integer fields.
 */
public class IntegerBinaryFieldAdapter implements FieldAdapter {

	private int dataLength;
	private boolean isSigned;
	private boolean isBigEndian;

	public IntegerBinaryFieldAdapter(int length, boolean isSigned, boolean isBigEndian) {
		this.dataLength = length;
		this.isSigned = isSigned;
		this.isBigEndian = isBigEndian;
	}
		
	@Override
	public String getString(byte[] buf, int offset, int length, int startBit, int stopBit) {
		return Long.toString(getFieldValue(buf, offset, length));
	}
		
	@Override
	public String getString(byte[] buf, int offset, int length, int startBit, int stopBit, Charset charset) {
		return Long.toString(getFieldValue(buf, offset, length));
	}

	@Override
	public byte getByte(byte[] buf, int offset, int length, int startBit, int stopBit) {
		long value = getFieldValue(buf, offset, length);
		if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE) {
			throw new NumberFormatException("Binary integer value out of range for byte (" + value + ")");
		}
		
		return (byte) value;
	}

	@Override
	public short getShort(byte[] buf, int offset, int length, int startBit, int stopBit) {
		long value = getFieldValue(buf, offset, length);
		if (value < Short.MIN_VALUE || value > Short.MAX_VALUE) {
			throw new NumberFormatException("Binary integer value out of range for short (" + value + ")");
		}
		
		return (short) value;
	}

	@Override
	public int getInt(byte[] buf, int offset, int length, int startBit, int stopBit) {
		long value = getFieldValue(buf, offset, length);
		if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
			throw new NumberFormatException("Binary integer value out of range for int (" + value + ")");
		}
		
		return (int) value;
	}

	@Override
	public long getLong(byte[] buf, int offset, int length, int startBit, int stopBit) {
		return getFieldValue(buf, offset, length);
	}

	@Override
	public float getFloat(byte[] buf, int offset, int length, int startBit, int stopBit) {
		return getFieldValue(buf, offset, length);
	}

	@Override
	public double getDouble(byte[] buf, int offset, int length, int startBit, int stopBit) {
		return getFieldValue(buf, offset, length);
	}

	private long getFieldValue(byte[] b, int offset, int length) {
		if (dataLength != length) {
			throw new IllegalArgumentException("Declared field length does not match data type length "
					+ "(" + length + "!=" + this.dataLength + ")");
		}
		
		if (isBigEndian) {
			return getFieldValueBigEndian(b, offset, length);
		} else {
			return getFieldValueLittleEndian(b, offset, length);
		}
	}

	private long getFieldValueBigEndian(byte[] b, int offset, int length) {
		long result = 0;
		
		for (int i=0; i < length; ++i) {
			if (i==0 && isSigned) {
				if (b[offset + i] < 0) {
					result = -1;
				}
			}
			result = (result << 8) | (b[offset + i] & 0xFF);
		}
		
		return result;
	}
	
	private long getFieldValueLittleEndian(byte[] b, int offset, int length) {
		long result = 0;
		
		for (int i = offset+length-1; i >= offset; --i) {
			if (i==offset+length-1 && isSigned) {
				if (b[i] < 0) {
					result = -1;
				}
			}
			result = (result << 8) | (b[i] & 0xFF);
		}
		
		return result;
	}

}
