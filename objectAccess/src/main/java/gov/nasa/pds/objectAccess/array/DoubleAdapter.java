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
