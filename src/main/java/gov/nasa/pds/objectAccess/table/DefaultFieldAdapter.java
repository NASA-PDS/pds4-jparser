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
 * Implements an adapter object for table fields that can
 * read the field value from a byte array or write the
 * field value into a byte array. Also has methods for
 * formatting the value into an output writer for either
 * delimited or fixed-width output. Methods not appropriate
 * for the field type will throw {@link java.lang.UnsupportedOperationException}.
 */
public class DefaultFieldAdapter implements FieldAdapter {

	private static final String NOT_SUPPORTED = "Operation not supported";

	private static final Charset US_ASCII;
	static {
		US_ASCII = Charset.forName("US-ASCII");
	}

	@Override
	public byte getByte(byte[] buf, int offset, int length, int startBit, int stopBit) {
		throw new UnsupportedOperationException(NOT_SUPPORTED);
	}

	@Override
	public short getShort(byte[] buf, int offset, int length, int startBit, int stopBit) {
		throw new UnsupportedOperationException(NOT_SUPPORTED);
	}

	@Override
	public int getInt(byte[] buf, int offset, int length, int startBit, int stopBit) {
		throw new UnsupportedOperationException(NOT_SUPPORTED);
	}

	@Override
	public long getLong(byte[] buf, int offset, int length, int startBit, int stopBit) {
		throw new UnsupportedOperationException(NOT_SUPPORTED);
	}

	@Override
	public float getFloat(byte[] buf, int offset, int length, int startBit, int stopBit) {
		throw new UnsupportedOperationException(NOT_SUPPORTED);
	}

	@Override
	public double getDouble(byte[] buf, int offset, int length, int startBit, int stopBit) {
		throw new UnsupportedOperationException(NOT_SUPPORTED);
	}

	@Override
	public String getString(byte[] buf, int offset, int length, int startBit, int stopBit) {
		return new String(buf, offset, length, US_ASCII);
	}

	@Override
	public String getString(byte[] buf, int offset, int length, int startBit, int stopBit, Charset charset) {
		return new String(buf, offset, length, charset);
	}

	@Override
	public void setByte(byte value, int offset, int length, ByteBuffer buffer, boolean isRightJustified) {
		throw new UnsupportedOperationException(NOT_SUPPORTED);
	}

	@Override
	public void setShort(short value, int offset, int length, ByteBuffer buffer, boolean isRightJustified) {
		throw new UnsupportedOperationException(NOT_SUPPORTED);
	}

	@Override
	public void setInt(int value, int offset, int length, ByteBuffer buffer, boolean isRightJustified) {
		throw new UnsupportedOperationException(NOT_SUPPORTED);
	}

	@Override
	public void setLong(long value, int offset, int length, ByteBuffer buffer, boolean isRightJustified) {
		throw new UnsupportedOperationException(NOT_SUPPORTED);
	}

	@Override
	public void setFloat(float value, int offset, int length, ByteBuffer buffer, boolean isRightJustified) {
		throw new UnsupportedOperationException(NOT_SUPPORTED);
	}

	@Override
	public void setDouble(double value, int offset, int length, ByteBuffer buffer, boolean isRightJustified) {
		throw new UnsupportedOperationException(NOT_SUPPORTED);
	}

	@Override
	public void setString(String value, int offset, int length, ByteBuffer buffer, boolean isRightJustified, Charset charset) {
		if (value.length() > length) {
			throw new IllegalArgumentException("The size of the value is greater than the field length.");
		}
		buffer.position(offset);
		buffer.put(getJustifiedValue(value, length, isRightJustified, charset), 0, length);
	}

	@Override
	public void setString(String value, int offset, int length, ByteBuffer buffer, boolean isRightJustified) {
		this.setString(value, offset, length, buffer, isRightJustified, Charset.forName("US-ASCII"));
	}

	public void setString(String value, ByteBuffer buffer, Charset charset) {
		buffer.put(value.getBytes(charset));
	}

	private byte[] getJustifiedValue(String value, int fieldLen, boolean isRightJustified, Charset charset) {
		// Add padding for left/right justification
		StringBuffer sb = new StringBuffer();
		int padding = fieldLen - value.length();

		if (isRightJustified) {
			for (int i = 0; i < padding; i++) {
				sb.append(' ');
			}
		}
		sb.append(value);
		if (!isRightJustified) {
			for (int i = 0; i < padding; i++) {
				sb.append(' ');
			}
		}

		return sb.toString().getBytes(charset);
	}

	@Override
	public BigInteger getBigInteger(byte[] buf, int offset, int length,
			int startBit, int stopBit) {
		throw new UnsupportedOperationException(NOT_SUPPORTED);
	}

	@Override
	public void setBigInteger(BigInteger value, int offset, int length,
			ByteBuffer buffer, boolean isRightJustified) {
		throw new UnsupportedOperationException(NOT_SUPPORTED);
	}
}
