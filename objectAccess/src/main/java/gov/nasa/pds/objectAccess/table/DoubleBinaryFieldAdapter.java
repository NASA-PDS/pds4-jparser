package gov.nasa.pds.objectAccess.table;

import java.nio.charset.Charset;

/**
 * Implements a field adapter for binary, double-precision, floating-point fields.
 */
public class DoubleBinaryFieldAdapter implements FieldAdapter {
	
	FieldAdapter longAdapter;

	public DoubleBinaryFieldAdapter(boolean isBigEndian) {
		longAdapter = new IntegerBinaryFieldAdapter(Double.SIZE / Byte.SIZE, false, isBigEndian);
	}
	
	@Override
	public String getString(byte[] buf, int offset, int length, int startBit, int stopBit) {
		return Double.toString(getDouble(buf, offset, length, startBit, stopBit));
	}

	@Override
	public String getString(byte[] buf, int offset, int length, int startBit, int stopBit, Charset charset) {
		return Double.toString(getDouble(buf, offset, length, startBit, stopBit));
	}
	
	@Override
	public byte getByte(byte[] buf, int offset, int length, int startBit, int stopBit) {
		throw new UnsupportedOperationException("Cannot get a binary float as an integer.");
	}
	
	@Override
	public short getShort(byte[] buf, int offset, int length, int startBit, int stopBit) {
		throw new UnsupportedOperationException("Cannot get a binary float as an integer.");
	}
	
	@Override
	public int getInt(byte[] buf, int offset, int length, int startBit, int stopBit) {
		throw new UnsupportedOperationException("Cannot get a binary float as an integer.");
	}

	@Override
	public long getLong(byte[] buf, int offset, int length, int startBit, int stopBit) {
		throw new UnsupportedOperationException("Cannot get a binary float as an integer.");
	}

	@Override
	public float getFloat(byte[] buf, int offset, int length, int startBit, int stopBit) {
		return (float) getDouble(buf, offset, length, startBit, stopBit);
	}

	@Override
	public double getDouble(byte[] buf, int offset, int length, int startBit, int stopBit) {
		long bits = longAdapter.getLong(buf, offset, length, 0, 0);
		return Double.longBitsToDouble(bits);
	}

}
