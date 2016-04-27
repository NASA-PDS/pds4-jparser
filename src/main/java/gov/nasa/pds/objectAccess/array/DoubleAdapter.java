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
 * Implements a data type adapter for double values.
 */
public class DoubleAdapter implements DataTypeAdapter {
	
	private IntegerAdapter valueAdapter;
	
	/**
	 * Creates a new instance.
	 * 
	 * @param isBigEndian true, if element is big-endian
	 */
	public DoubleAdapter(boolean isBigEndian) {
		valueAdapter = new IntegerAdapter(Double.SIZE / Byte.SIZE, isBigEndian, true);
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
		long bits = valueAdapter.getLong(buf);
		return Double.longBitsToDouble(bits);
	}

}
