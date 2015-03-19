package gov.nasa.pds.objectAccess.table;

import java.math.BigInteger;
import java.nio.ByteBuffer;

/**
 * Implements a field adapter for numeric fields stored in
 * textual format, as in a character table.
 */
public class NumericTextFieldAdapter extends DefaultFieldAdapter {

	@Override
	public byte getByte(byte[] buf, int offset, int length, int startBit, int stopBit) {
		int value = Integer.parseInt(getString(buf, offset, length, startBit, stopBit).trim());
		if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE) {
			throw new NumberFormatException("Value is out of range of a byte (" + value + ")");
		}

		return (byte) value;
	}

	@Override
	public short getShort(byte[] buf, int offset, int length, int startBit, int stopBit) {
		int value = Integer.parseInt(getString(buf, offset, length, startBit, stopBit).trim());
		if (value < Short.MIN_VALUE || value > Short.MAX_VALUE) {
			throw new NumberFormatException("Value is out of range of a short (" + value + ")");
		}

		return (short) value;
	}

	@Override
	public int getInt(byte[] buf, int offset, int length, int startBit, int stopBit) {
		return Integer.parseInt(getString(buf, offset, length, startBit, stopBit).trim());
	}

	@Override
	public long getLong(byte[] buf, int offset, int length, int startBit, int stopBit) {
		return Long.parseLong(getString(buf, offset, length, startBit, stopBit).trim());
	}

	@Override
	public float getFloat(byte[] buf, int offset, int length, int startBit, int stopBit) {
		return Float.parseFloat(getString(buf, offset, length, startBit, stopBit).trim());
	}

	@Override
	public double getDouble(byte[] buf, int offset, int length, int startBit, int stopBit) {
		return Double.parseDouble(getString(buf, offset, length, startBit, stopBit).trim());
	}

	@Override
	public BigInteger getBigInteger(byte[] buf, int offset, int length,
			int startBit, int stopBit) {
		return new BigInteger(getString(buf, offset, length, startBit, stopBit));
	}

	//
	// TODO: need to pass in charset to setString() in all setter?
	//

	@Override
	public void setByte(byte value, int offset, int length, ByteBuffer buffer, boolean isRightJustified) {
		setString(Byte.toString(value), offset, length, buffer, isRightJustified);
	}

	@Override
	public void setShort(short value, int offset, int length, ByteBuffer buffer, boolean isRightJustified) {
		setString(Short.toString(value), offset, length, buffer, isRightJustified);
	}

	@Override
	public void setInt(int value, int offset, int length, ByteBuffer buffer, boolean isRightJustified) {
		setString(Integer.toString(value), offset, length, buffer, isRightJustified);
	}

	@Override
	public void setLong(long value, int offset, int length, ByteBuffer buffer, boolean isRightJustified) {
		setString(Long.toString(value), offset, length, buffer, isRightJustified);
	}

	@Override
	public void setFloat(float value, int offset, int length, ByteBuffer buffer, boolean isRightJustified) {
		setString(Float.toString(value), offset, length, buffer, isRightJustified);
	}

	@Override
	public void setDouble(double value, int offset, int length, ByteBuffer buffer, boolean isRightJustified) {
		setString(Double.toString(value), offset, length, buffer, isRightJustified);
	}

	@Override
	public void setBigInteger(BigInteger value, int offset, int length,
			ByteBuffer buffer, boolean isRightJustified) {
		setString(value.toString(), offset, length, buffer, isRightJustified);
	}

}
