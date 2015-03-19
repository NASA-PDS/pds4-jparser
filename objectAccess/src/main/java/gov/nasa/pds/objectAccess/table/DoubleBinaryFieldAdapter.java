package gov.nasa.pds.objectAccess.table;

import java.math.BigInteger;
import java.nio.ByteBuffer;
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

	@Override
	public void setString(String value, int offset, int length, ByteBuffer buffer, boolean isRightJustified) {
		setDouble(Double.parseDouble(value), offset, length, buffer, isRightJustified);
	}

	@Override
	public void setString(String value, int offset, int length, ByteBuffer buffer,	boolean isRightJustified, Charset charset) {
		setDouble(Double.parseDouble(value), offset, length, buffer, isRightJustified);
	}

	@Override
	public void setByte(byte value, int offset, int length, ByteBuffer buffer, boolean isRightJustified) {
		throw new UnsupportedOperationException("Cannot set a binary double as an integer.");
	}

	@Override
	public void setShort(short value, int offset, int length, ByteBuffer buffer, boolean isRightJustified) {
		throw new UnsupportedOperationException("Cannot set a binary double as an integer.");
	}

	@Override
	public void setInt(int value, int offset, int length, ByteBuffer buffer, boolean isRightJustified) {
		throw new UnsupportedOperationException("Cannot set a binary double as an integer.");
	}

	@Override
	public void setLong(long value, int offset, int length, ByteBuffer buffer, boolean isRightJustified) {
		throw new UnsupportedOperationException("Cannot set a binary double as an integer.");
	}

	@Override
	public void setFloat(float value, int offset, int length, ByteBuffer buffer, boolean isRightJustified) {
		setDouble(value, offset, length, buffer, isRightJustified);
	}

	@Override
	public void setDouble(double value, int offset, int length, ByteBuffer buffer, boolean isRightJustified) {
		longAdapter.setDouble(value, offset, length, buffer, isRightJustified);
	}

	@Override
	public BigInteger getBigInteger(byte[] buf, int offset, int length,
			int startBit, int stopBit) {
		String stringValue = Double.toString(getDouble(buf, offset, length, startBit, stopBit));
		return new BigInteger(stringValue);
	}

	@Override
	public void setBigInteger(BigInteger value, int offset, int length,
			ByteBuffer buffer, boolean isRightJustified) {
		String stringValue = value.toString();
		setDouble(Double.parseDouble(stringValue), offset, length, buffer, isRightJustified);
	}

}
