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
package gov.nasa.pds.objectAccess.table;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * Implements a field adapter for binary, single-precision, floating-point fields.
 */
public class FloatBinaryFieldAdapter implements FieldAdapter {

	FieldAdapter intAdapter;

	public FloatBinaryFieldAdapter(boolean isBigEndian) {
		intAdapter = new IntegerBinaryFieldAdapter(Float.SIZE / Byte.SIZE, true, isBigEndian);
	}

	@Override
	public String getString(byte[] buf, int offset, int length, int startBit, int stopBit) {
		return Float.toString(getFloat(buf, offset, length, startBit, stopBit));
	}

	@Override
	public String getString(byte[] buf, int offset, int length, int startBit, int stopBit, Charset charset) {
		return Float.toString(getFloat(buf, offset, length, startBit, stopBit));
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
		int bits = intAdapter.getInt(buf, offset, length, 0, 0);
		return Float.intBitsToFloat(bits);
	}

	@Override
	public double getDouble(byte[] buf, int offset, int length, int startBit, int stopBit) {
		return getFloat(buf, offset, length, startBit, stopBit);
	}

	@Override
	public void setString(String value, int offset, int length, ByteBuffer buffer, boolean isRightJustified) {
		setFloat(Float.parseFloat(value), offset, length, buffer, isRightJustified);
	}

	@Override
	public void setString(String value, int offset, int length, ByteBuffer buffer,	boolean isRightJustified, Charset charset) {
		setFloat(Float.parseFloat(value), offset, length, buffer, isRightJustified);
	}

	@Override
	public void setByte(byte value, int offset, int length, ByteBuffer buffer, boolean isRightJustified) {
		throw new UnsupportedOperationException("Cannot set a binary float as an integer.");
	}

	@Override
	public void setShort(short value, int offset, int length, ByteBuffer buffer, boolean isRightJustified) {
		throw new UnsupportedOperationException("Cannot set a binary float as an integer.");
	}

	@Override
	public void setInt(int value, int offset, int length, ByteBuffer buffer, boolean isRightJustified) {
		throw new UnsupportedOperationException("Cannot set a binary float as an integer.");
	}

	@Override
	public void setLong(long value, int offset, int length, ByteBuffer buffer, boolean isRightJustified) {
		throw new UnsupportedOperationException("Cannot set a binary float as an integer.");
	}

	@Override
	public void setFloat(float value, int offset, int length, ByteBuffer buffer, boolean isRightJustified) {
		intAdapter.setFloat(value, offset, length, buffer, isRightJustified);
	}

	@Override
	public void setDouble(double value, int offset, int length, ByteBuffer buffer, boolean isRightJustified) {
		setFloat((float) value, offset, length, buffer, isRightJustified);
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
