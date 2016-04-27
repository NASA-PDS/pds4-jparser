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
package gov.nasa.pds.objectAccess.array;

import java.nio.ByteBuffer;

/**
 * Implements a data type adapter for 4-byte integers.
 */
public class IntegerAdapter implements DataTypeAdapter {

	private static final int BYTE_MASK = 0xFF;
	private int elementSize;
	private boolean isBigEndian;
	private boolean isUnsigned;

	/**
	 * Creates a new adapter for an integer of given size.
	 *
	 * @param elementSize the number of bytes in the integer
	 * @param isBigEndian true, if the data is big-endian, false if little-endian
	 * @param isUnsigned true, if the data is unsigned, false if signed
	 */
	public IntegerAdapter(int elementSize, boolean isBigEndian, boolean isUnsigned) {
		this.elementSize = elementSize;
		this.isBigEndian = isBigEndian;
		this.isUnsigned = isUnsigned;
	}

	@Override
	public int getInt(ByteBuffer buf) {
		long value = getValue(buf);
		if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
			throw new IllegalArgumentException("Value out of range for int: " + Long.toString(value));
		}

		return (int) value;
	}

	@Override
	public long getLong(ByteBuffer buf) {
		return getValue(buf);
	}

	@Override
	public double getDouble(ByteBuffer buf) {
		return getValue(buf);
	}

	private long getValue(ByteBuffer buf) {
		byte[] b = new byte[elementSize];

		long value = 0;
		if (isBigEndian) {
			value = buf.get();
			if (isUnsigned) {
				value &= BYTE_MASK;
			}
			for (int i=1; i < b.length; ++i) {
				value = (value << 8) | (buf.get() & BYTE_MASK);
			}
		} else {
			for (int i=0; i < b.length; ++i) {
				int newByte = buf.get();
				if (i < b.length-1 || isUnsigned) {
					newByte &= BYTE_MASK;
				}
				value |= (((long) newByte) << (8 * i));
			}
		}

		return value;
	}

}
