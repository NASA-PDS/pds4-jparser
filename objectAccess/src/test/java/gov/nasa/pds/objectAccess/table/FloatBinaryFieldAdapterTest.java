package gov.nasa.pds.objectAccess.table;

import static org.testng.Assert.assertEquals;

import java.nio.charset.Charset;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class FloatBinaryFieldAdapterTest {

	@Test(dataProvider="floatTests")
	public void testGetFloatBigEndian(float value) {
		byte[] buf = intToBytes(Float.floatToIntBits(value), true);
		FloatBinaryFieldAdapter adapter = new FloatBinaryFieldAdapter(true);
		assertEquals(adapter.getFloat(buf, 0, buf.length, 0, 0), value);
		assertEquals(adapter.getDouble(buf, 0, buf.length, 0, 0), (double) value);
	}
	
	@Test(dataProvider="floatTests")
	public void testGetFloatLittleEndian(float value) {
		byte[] buf = intToBytes(Float.floatToIntBits(value), false);
		FloatBinaryFieldAdapter adapter = new FloatBinaryFieldAdapter(false);
		assertEquals(adapter.getFloat(buf, 0, buf.length, 0, 0), value);
		assertEquals(adapter.getDouble(buf, 0, buf.length, 0, 0), (double) value);
	}
	
	@SuppressWarnings("unused")
	@DataProvider(name="floatTests")
	private Object[][] getFloatTests() {
		return new Object[][] {
				// value to test
				{ 0.0F },
				{ 1.0F },
				{ -1.0F },
				{ Float.MAX_VALUE },
				{ -Float.MAX_VALUE },
				{ Float.POSITIVE_INFINITY },
				{ Float.NEGATIVE_INFINITY },
				{ Float.NaN },
				{ Float.MIN_NORMAL },
		};
	}
	
	@Test
	public void testGetString() {
		long bits = Float.floatToIntBits(3.14F);
		byte[] b = new byte[] {
				(byte) ((bits >> 24) & 0xFF),
				(byte) ((bits >> 16) & 0xFF),
				(byte) ((bits >> 8) & 0xFF),
				(byte) (bits & 0xFF),
		};
		FieldAdapter adapter = new FloatBinaryFieldAdapter(true);
		assertEquals(adapter.getString(b, 0, b.length, 0, 0), Float.toString(3.14F));
		assertEquals(adapter.getString(b, 0, b.length, 0, 0, Charset.forName("US-ASCII")), Float.toString(3.14F));
	}
	
	@Test(expectedExceptions={UnsupportedOperationException.class})
	public void testGetByte() {
		byte[] b = new byte[] { 0x12, 0x34, 0x56, 0x78 };
		FieldAdapter adapter = new FloatBinaryFieldAdapter(true);
		adapter.getByte(b, 0, b.length, 0, 0);
	}
	
	@Test(expectedExceptions={UnsupportedOperationException.class})
	public void testGetShort() {
		byte[] b = new byte[] { 0x12, 0x34, 0x56, 0x78 };
		FieldAdapter adapter = new FloatBinaryFieldAdapter(true);
		adapter.getShort(b, 0, b.length, 0, 0);
	}
	
	@Test(expectedExceptions={UnsupportedOperationException.class})
	public void testGetInt() {
		byte[] b = new byte[] { 0x12, 0x34, 0x56, 0x78 };
		FieldAdapter adapter = new FloatBinaryFieldAdapter(true);
		adapter.getInt(b, 0, b.length, 0, 0);
	}
	
	@Test(expectedExceptions={UnsupportedOperationException.class})
	public void testGetLong() {
		byte[] b = new byte[] { 0x12, 0x34, 0x56, 0x78 };
		FieldAdapter adapter = new FloatBinaryFieldAdapter(true);
		adapter.getLong(b, 0, b.length, 0, 0);
	}
	
	private byte[] intToBytes(int n, boolean isBigEndian) {
		if (isBigEndian) {
			return intToBytesBigEndian(n);
		} else {
			return intToBytesLittleEndian(n);
		}
	}

	private byte[] intToBytesBigEndian(int n) {
		int intBytes = Integer.SIZE / Byte.SIZE;
		byte[] b = new byte[intBytes];
		
		for (int i=0; i < intBytes; ++i) {
			b[intBytes - i - 1] = (byte) (n & 0xFF);
			n >>= 8;
		}
		
		return b;
	}

	private byte[] intToBytesLittleEndian(int n) {
		int intBytes = Integer.SIZE / Byte.SIZE;
		byte[] b = new byte[intBytes];
		
		for (int i=0; i < intBytes; ++i) {
			b[i] = (byte) (n & 0xFF);
			n >>= 8;
		}
		
		return b;
	}
	
}
