package gov.nasa.pds.objectAccess.table;

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

}
