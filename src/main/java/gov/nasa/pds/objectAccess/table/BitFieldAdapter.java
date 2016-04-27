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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements a field adapter for binary bit fields.
 */
public class BitFieldAdapter implements FieldAdapter {

	private static final Logger LOGGER = LoggerFactory.getLogger(BitFieldAdapter.class);
	private static final String NOT_SUPPORTED = "Operation not supported yet.";

	/** A long constant that has all bits on. */
	private static final long LONG_ALL_BITS_ONE = 0xFFFFFFFFFFFFFFFFL;

	private boolean isSigned;

	/**
	 * Creates a new bit field adapter with given signed-ness.
	 *
	 * @param isSigned true, if the bit field is signed
	 */
	public BitFieldAdapter(boolean isSigned) {
		this.isSigned = isSigned;
	}

	@Override
	public String getString(byte[] buf, int offset, int length, int startBit, int stopBit) {
		return Long.toString(getFieldValue(buf, offset, length, startBit, stopBit));
	}

	@Override
	public String getString(byte[] buf, int offset, int length, int startBit,
			int stopBit, Charset charset) {
		return Long.toString(getFieldValue(buf, offset, length, startBit, stopBit));
	}

	@Override
	public byte getByte(byte[] buf, int offset, int length, int startBit, int stopBit) {
		long value = getFieldValue(buf, offset, length, startBit, stopBit);
		if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE) {
			String msg = "Binary integer value out of range for byte (" + value + ")";
			LOGGER.error(msg);
			throw new NumberFormatException();
		}

		return (byte) value;
	}

	@Override
	public short getShort(byte[] buf, int offset, int length, int startBit, int stopBit) {
		long value = getFieldValue(buf, offset, length, startBit, stopBit);
		if (value < Short.MIN_VALUE || value > Short.MAX_VALUE) {
			String msg = "Binary integer value out of range for short (" + value + ")";
			LOGGER.error(msg);
			throw new NumberFormatException(msg);
		}

		return (short) value;
	}

	@Override
	public int getInt(byte[] buf, int offset, int length, int startBit, int stopBit) {
		long value = getFieldValue(buf, offset, length, startBit, stopBit);
		if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
			String msg = "Binary integer value out of range for int (" + value + ")";
			LOGGER.error(msg);
			throw new NumberFormatException(msg);
		}

		return (int) value;
	}

	@Override
	public long getLong(byte[] buf, int offset, int length, int startBit, int stopBit) {
		return getFieldValue(buf, offset, length, startBit, stopBit);
	}

	@Override
	public float getFloat(byte[] buf, int offset, int length, int startBit, int stopBit) {
		return getFieldValue(buf, offset, length, startBit, stopBit);
	}

	@Override
	public double getDouble(byte[] buf, int offset, int length, int startBit, int stopBit) {
		return getFieldValue(buf, offset, length, startBit, stopBit);
	}

	@Override
	public void setString(String value, int offset, int length, ByteBuffer buf, boolean isRightJustifed) {
		throw new UnsupportedOperationException(NOT_SUPPORTED);
	}

	@Override
	public void setString(String value, int offset, int length, ByteBuffer buffer, boolean isRightJustifed, Charset charset) {
		throw new UnsupportedOperationException(NOT_SUPPORTED);
	}

	@Override
	public void setInt(int value, int offset, int length, ByteBuffer buffer, boolean isRightJustifed) {
		throw new UnsupportedOperationException(NOT_SUPPORTED);
	}

	@Override
	public void setDouble(double value, int offset, int length, ByteBuffer buffer, boolean isRightJustifed) {
		throw new UnsupportedOperationException(NOT_SUPPORTED);
	}

	@Override
	public void setFloat(float value, int offset, int length, ByteBuffer buffer, boolean isRightJustifed) {
		throw new UnsupportedOperationException(NOT_SUPPORTED);
	}

	@Override
	public void setShort(short value, int offset, int length, ByteBuffer buffer, boolean isRightJustifed) {
		throw new UnsupportedOperationException(NOT_SUPPORTED);
	}

	@Override
	public void setByte(byte value, int offset, int length, ByteBuffer buffer, boolean isRightJustifed) {
		throw new UnsupportedOperationException(NOT_SUPPORTED);
	}

	@Override
	public void setLong(long value, int offset, int length, ByteBuffer buffer, boolean isRightJustifed) {
		throw new UnsupportedOperationException(NOT_SUPPORTED);
	}

	private long getFieldValue(byte[] b, int offset, int length, int startBit, int stopBit) {
		if (startBit < 0) {
			String msg = "Start bit is negative (" + startBit + ")";
			LOGGER.error(msg);
			throw new ArrayIndexOutOfBoundsException(msg);
		}
		if (stopBit >= length * Byte.SIZE) {
			String msg = "Stop bit past end of packed field (" + stopBit + " > " + (length * Byte.SIZE - 1) + ")";
			LOGGER.error(msg);
			throw new ArrayIndexOutOfBoundsException(msg);
		}
		if (stopBit - startBit + 1 > Long.SIZE) {
			String msg = "Bit field is wider than long (" + (stopBit-startBit+1) + " > " + Long.SIZE + ")";
			LOGGER.error(msg);
			throw new IllegalArgumentException(msg);
		}

		int startByte = startBit / Byte.SIZE;
		int stopByte = stopBit / Byte.SIZE;

		if (stopByte-startByte+1 > Long.SIZE / Byte.SIZE) {
			String msg = "Bit field spans bytes that are wider than a long "
				+ "(" + (stopByte-startByte+1) + " > " + (Long.SIZE / Byte.SIZE) + ")";
			LOGGER.error(msg);
			throw new NumberFormatException(msg);
		}

		long bytesValue = getBytesAsLong(b, offset, startByte, stopByte);

		// Now shift right to get rid of the extra bits.
		int extraRightBits = (stopByte + 1)*Byte.SIZE - stopBit - 1;
		long shiftedValue = bytesValue >> extraRightBits;

		return rightmostBits(shiftedValue, stopBit-startBit+1, isSigned);
	}

	// Default scope, for unit testing.
	static long rightmostBits(long value, int nBits, boolean isSigned) {
		long mask = 0;
		if (nBits > 0) {
			mask = LONG_ALL_BITS_ONE >>> (Long.SIZE - nBits);
		}
		long maskedValue = value & mask;

		// Now sign-extend, if signed.
		if (isSigned && nBits < Long.SIZE) {
			long signBit = 1L << (nBits - 1);
			if ((maskedValue & signBit) != 0) {
				maskedValue |= (LONG_ALL_BITS_ONE << nBits);
			}
		}

		return maskedValue;
	}

	static long getBytesAsLong(byte[] source, int off, int startByte, int stopByte) {
		long value = 0;

		for (int i=off+startByte; i <= off+stopByte; ++i) {
			value = (value << Byte.SIZE) | (source[i] & 0xFF);
		}

		return value;
	}

	@Override
	public BigInteger getBigInteger(byte[] buf, int offset, int length,
			int startBit, int stopBit) {
		String stringValue = Long.toString(getLong(buf, offset, length, startBit, stopBit));
		return new BigInteger(stringValue);
	}

	@Override
	public void setBigInteger(BigInteger value, int offset, int length,
			ByteBuffer buffer, boolean isRightJustified) {
		String stringValue = value.toString();
		setLong(Long.parseLong(stringValue), offset, length, buffer, isRightJustified);
	}

}
