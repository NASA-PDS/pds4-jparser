package gov.nasa.pds.objectAccess.table;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class IntegerBinaryFieldAdapterTest {
	
	@Test(dataProvider="integerTests")
	public void testMSB4(boolean isBigEndian, byte[] buf, int offset, int length, long expectedValue) {
		FieldAdapter adapter = new IntegerBinaryFieldAdapter(length, true, isBigEndian);
		
		assertEquals(adapter.getLong(buf, offset, length, 0, 0), expectedValue);
		assertEquals(adapter.getFloat(buf, offset, length, 0, 0), (float) expectedValue);
		assertEquals(adapter.getDouble(buf, offset, length, 0, 0), (double) expectedValue);
		if (Integer.MIN_VALUE <= expectedValue && expectedValue <= Integer.MAX_VALUE) {
			assertEquals(adapter.getInt(buf, offset, length, 0, 0), (int) expectedValue);
		}
		if (Short.MIN_VALUE <= expectedValue && expectedValue <= Short.MAX_VALUE) {
			assertEquals(adapter.getShort(buf, offset, length, 0, 0), (short) expectedValue);
		}
		if (Byte.MIN_VALUE <= expectedValue && expectedValue <= Byte.MAX_VALUE) {
			assertEquals(adapter.getByte(buf, offset, length, 0, 0), (byte) expectedValue);
		}
	}
	
	@SuppressWarnings("unused")
	@DataProvider(name="integerTests")
	private Object[][] getIntegerTests() {
		return new Object[][] {
				// isBigEndian, byte[] buf, offset, length, long expectedValue
				{ true, new byte[] { 0x12, 0x34, 0x56, 0x78, 0x1A, 0x1B }, 0, 1, 0x12 },
				{ true, new byte[] { 0x12, 0x34, 0x56, 0x78, 0x1A, 0x1B }, 1, 1, 0x34 },
				{ false, new byte[] { 0x12, 0x34, 0x56, 0x78, 0x1A, 0x1B }, 0, 1, 0x12 },
				{ false, new byte[] { 0x12, 0x34, 0x56, 0x78, 0x1A, 0x1B }, 1, 1, 0x34 },
				
				{ true, new byte[] { 0x12, 0x34, 0x56, 0x78, 0x1A, 0x1B }, 0, 2, 0x1234 },
				{ true, new byte[] { 0x12, 0x34, 0x56, 0x78, 0x1A, 0x1B }, 1, 2, 0x3456 },
				{ false, new byte[] { 0x12, 0x34, 0x56, 0x78, 0x1A, 0x1B }, 0, 2, 0x3412 },
				{ false, new byte[] { 0x12, 0x34, 0x56, 0x78, 0x1A, 0x1B }, 1, 2, 0x5634 },
				
				{ true, new byte[] { 0x12, 0x34, 0x56, 0x78, 0x1A, 0x1B }, 0, 4, 0x12345678 },
				{ true, new byte[] { 0x12, 0x34, 0x56, 0x78, 0x1A, 0x1B }, 1, 4, 0x3456781A },
				{ false, new byte[] { 0x12, 0x34, 0x56, 0x78, 0x1A, 0x1B }, 0, 4, 0x78563412 },
				{ false, new byte[] { 0x12, 0x34, 0x56, 0x78, 0x1A, 0x1B }, 1, 4, 0x1A785634 },
				
				{ true, new byte[] { 0x12, 0x34, 0x56, 0x78, 0x1A, 0x1B, 0x1C, 0x1D, 0x1E }, 0, 8, 0x123456781A1B1C1DL },
				{ true, new byte[] { 0x12, 0x34, 0x56, 0x78, 0x1A, 0x1B, 0x1C, 0x1D, 0x1E }, 1, 8, 0x3456781A1B1C1D1EL },
				{ false, new byte[] { 0x12, 0x34, 0x56, 0x78, 0x1A, 0x1B, 0x1C, 0x1D, 0x1E }, 0, 8, 0x1D1C1B1A78563412L },
				{ false, new byte[] { 0x12, 0x34, 0x56, 0x78, 0x1A, 0x1B, 0x1C, 0x1D, 0x1E }, 1, 8, 0x1E1D1C1B1A785634L },
		};
	}
	
	@Test
	public void testGetString() {
		byte[] b = new byte[] { 0x12, 0x34, 0x56, 0x78 };
		FieldAdapter adapter = new IntegerBinaryFieldAdapter(4, true, true);
		assertEquals(adapter.getString(b, 0, b.length, 0, 0), Long.toString(0x12345678));
	}
	
	@Test(expectedExceptions={IllegalArgumentException.class})
	public void testBadDataLength() {
		byte[] b = new byte[] { 0x12, 0x34, 0x56, 0x78 };
		FieldAdapter adapter = new IntegerBinaryFieldAdapter(2, true, true);
		adapter.getInt(b, 0, b.length, 0, 0);
	}
	
	@Test(dataProvider="ByteOutOfRangeTests", expectedExceptions={NumberFormatException.class})
	public void testByteOutOfRange(byte[] b) {
		FieldAdapter adapter = new IntegerBinaryFieldAdapter(b.length, true, true);
		adapter.getByte(b, 0, b.length, 0, 0);
	}
	
	@Test(dataProvider="ShortOutOfRangeTests", expectedExceptions={NumberFormatException.class})
	public void testShortOutOfRange(byte[] b) {
		FieldAdapter adapter = new IntegerBinaryFieldAdapter(b.length, true, true);
		adapter.getShort(b, 0, b.length, 0, 0);
	}
	
	@Test(dataProvider="IntegerOutOfRangeTests", expectedExceptions={NumberFormatException.class})
	public void testIntegerOutOfRange(byte[] b) {
		FieldAdapter adapter = new IntegerBinaryFieldAdapter(b.length, true, true);
		adapter.getInt(b, 0, b.length, 0, 0);
	}
	
	@SuppressWarnings("unused")
	@DataProvider(name="ByteOutOfRangeTests")
	private Object[][] getByteOutOfRangeTests() {
		return new Object[][] {
				// byte[] bytes
				{ new byte[] { 0x01, 0x00 } },
				{ new byte[] { (byte) 0xFF, (byte) 0x7F } },
		};
	}
	
	@SuppressWarnings("unused")
	@DataProvider(name="ShortOutOfRangeTests")
	private Object[][] getShortOutOfRangeTests() {
		return new Object[][] {
				// byte[] bytes
				{ new byte[] { 0x01, 0x00, 0x00, } },
				{ new byte[] { (byte) 0xFF, 0x7F, (byte) 0xFF, } },
		};
	}
	
	@SuppressWarnings("unused")
	@DataProvider(name="IntegerOutOfRangeTests")
	private Object[][] getIntegerOutOfRangeTests() {
		return new Object[][] {
				// byte[] bytes
				{ new byte[] { 0x00, 0x00, 0x00, 0x00, (byte) 0x80, 0x00, 0x00, 0x00 } },
				{ new byte[] { 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00 } },
				{ new byte[] { (byte) 0xFF, 0x7F, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF } },
				{ new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFE, 0x00, 0x00, 0x00, 0x00 } },
		};
	}
	
}
