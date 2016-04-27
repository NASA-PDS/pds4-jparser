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
 * Implements a data type adapter for float values.
 */
public class FloatAdapter implements DataTypeAdapter {
	
	private IntegerAdapter valueAdapter;
	
	/**
	 * Creates a new instance.
	 * 
	 * @param isBigEndian true, if the data is big-endian
	 */
	public FloatAdapter(boolean isBigEndian) {
		// We first convert the bit pattern to a signed int, so
		// we need to have an underlying integer adapter.
		valueAdapter = new IntegerAdapter(Float.SIZE / Byte.SIZE, isBigEndian, false);
	}

	@Override
	public int getInt(ByteBuffer buf) {
		return (int) getValue(buf);
	}

	@Override
	public long getLong(ByteBuffer buf) {
		return (long) getValue(buf);
	}

	@Override
	public double getDouble(ByteBuffer buf) {
		return getValue(buf);
	}
	
	private double getValue(ByteBuffer buf) {
		int bits = valueAdapter.getInt(buf);
		return Float.intBitsToFloat(bits);
	}

}
