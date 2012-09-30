package gov.nasa.pds.objectAccess.table;

import static org.testng.Assert.assertEquals;

import java.nio.charset.Charset;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class DoubleBinaryFieldAdapterTest {

	@Test(dataProvider="floatTests")
	public void testGetDoubleBigEndian(double value) {
		byte[] buf = longToBytes(Double.doubleToLongBits(value), true);
		DoubleBinaryFieldAdapter adapter = new DoubleBinaryFieldAdapter(true);
		assertEquals(adapter.getDouble(buf, 0, buf.length, 0, 0), value);
		assertEquals(adapter.getFloat(buf, 0, buf.length, 0, 0), (float) value);
	}
	
	@Test(dataProvider="floatTests")
	public void testGetDoubleLittleEndian(double value) {
		byte[] buf = longToBytes(Double.doubleToLongBits(value), false);
		DoubleBinaryFieldAdapter adapter = new DoubleBinaryFieldAdapter(false);
		assertEquals(adapter.getDouble(buf, 0, buf.length, 0, 0), value);
		assertEquals(adapter.getFloat(buf, 0, buf.length, 0, 0), (float) value);
	}
	
	@SuppressWarnings("unused")
	@DataProvider(name="floatTests")
	private Object[][] getFloatTests() {
		return new Object[][] {
				// value to test
				{ 0.0 },
				{ 1.0 },
				{ -1.0 },
				{ Double.MAX_VALUE },
				{ -Double.MAX_VALUE },
				{ Double.POSITIVE_INFINITY },
				{ Double.NEGATIVE_INFINITY },
				{ Double.NaN },
				{ Double.MIN_NORMAL },
		};
	}
	
	@Test
	public void testGetString() {
		long bits = Double.doubleToLongBits(3.14);
		byte[] b = new byte[] {
				(byte) ((bits >> 56) & 0xFF),
				(byte) ((bits >> 48) & 0xFF),
				(byte) ((bits >> 40) & 0xFF),
				(byte) ((bits >> 32) & 0xFF),
				(byte) ((bits >> 24) & 0xFF),
				(byte) ((bits >> 16) & 0xFF),
				(byte) ((bits >> 8) & 0xFF),
				(byte) (bits & 0xFF),
		};
		FieldAdapter adapter = new DoubleBinaryFieldAdapter(true);
		assertEquals(adapter.getString(b, 0, b.length, 0, 0), Double.toString(3.14));
		assertEquals(adapter.getString(b, 0, b.length, 0, 0, Charset.forName("US-ASCII")), Double.toString(3.14));
	}

	@Test(expectedExceptions={UnsupportedOperationException.class})
	public void testGetByte() {
		byte[] b = new byte[] { 0x12, 0x34, 0x56, 0x78, 0x1A, 0x1B, 0x1C, 0x1D };
		FieldAdapter adapter = new DoubleBinaryFieldAdapter(true);
		adapter.getByte(b, 0, b.length, 0, 0);
	}
	
	@Test(expectedExceptions={UnsupportedOperationException.class})
	public void testGetShort() {
		byte[] b = new byte[] { 0x12, 0x34, 0x56, 0x78, 0x1A, 0x1B, 0x1C, 0x1D };
		FieldAdapter adapter = new DoubleBinaryFieldAdapter(true);
		adapter.getShort(b, 0, b.length, 0, 0);
	}
	
	@Test(expectedExceptions={UnsupportedOperationException.class})
	public void testGetInt() {
		byte[] b = new byte[] { 0x12, 0x34, 0x56, 0x78, 0x1A, 0x1B, 0x1C, 0x1D };
		FieldAdapter adapter = new DoubleBinaryFieldAdapter(true);
		adapter.getInt(b, 0, b.length, 0, 0);
	}
	
	@Test(expectedExceptions={UnsupportedOperationException.class})
	public void testGetLong() {
		byte[] b = new byte[] { 0x12, 0x34, 0x56, 0x78, 0x1A, 0x1B, 0x1C, 0x1D };
		FieldAdapter adapter = new DoubleBinaryFieldAdapter(true);
		adapter.getLong(b, 0, b.length, 0, 0);
	}
	
	private byte[] longToBytes(long n, boolean isBigEndian) {
		if (isBigEndian) {
			return longToBytesBigEndian(n);
		} else {
			return longToBytesLittleEndian(n);
		}
	}

	private byte[] longToBytesBigEndian(long n) {
		int longBytes = Long.SIZE / Byte.SIZE;
		byte[] b = new byte[longBytes];
		
		for (int i=0; i < longBytes; ++i) {
			b[longBytes - i - 1] = (byte) (n & 0xFF);
			n >>= 8;
		}
		
		return b;
	}

	private byte[] longToBytesLittleEndian(long n) {
		int longBytes = Long.SIZE / Byte.SIZE;
		byte[] b = new byte[longBytes];
		
		for (int i=0; i < longBytes; ++i) {
			b[i] = (byte) (n & 0xFF);
			n >>= 8;
		}
		
		return b;
	}
	
}
