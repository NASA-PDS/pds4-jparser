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
