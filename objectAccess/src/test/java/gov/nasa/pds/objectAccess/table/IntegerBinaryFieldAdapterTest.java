package gov.nasa.pds.objectAccess.table;

import static org.testng.Assert.assertEquals;

import java.math.BigInteger;
import java.nio.ByteBuffer;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class IntegerBinaryFieldAdapterTest {

	private static final byte FF = (byte) 0xFF;

	@Test(dataProvider="integerTests")
	public void testMSB4(boolean isBigEndian, byte[] buf, int offset, int length, long expectedValue) {
		FieldAdapter adapter = new IntegerBinaryFieldAdapter(length, true, isBigEndian);

		assertEquals(adapter.getLong(buf, offset, length, 0, 0), expectedValue);
		assertEquals(adapter.getFloat(buf, offset, length, 0, 0), (float) expectedValue);
		assertEquals(adapter.getDouble(buf, offset, length, 0, 0), (double) expectedValue);

		BigInteger expectedBigInteger = new BigInteger(Long.toString(expectedValue));
		assertEquals(adapter.getBigInteger(buf, offset, length, 0, 0), expectedBigInteger);

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

	@Test
	public void testSetString() throws Exception {
		ByteBuffer buffer = ByteBuffer.allocate(20);
		int len = 5;
		String s = "12345";
		byte[] bytes = new byte[len];
		FieldAdapter adapter = new IntegerBinaryFieldAdapter(len, true, true);
		adapter.setString(s, 0, len, buffer, true);
		buffer.position(0);
		buffer.get(bytes, 0, len);
		assertEquals(bytes, s.getBytes("US-ASCII"));

		adapter = new IntegerBinaryFieldAdapter(len, true, false);
		adapter.setString(s, 10, len, buffer, true);
		buffer.position(10);
		buffer.get(bytes, 0, len);
		assertEquals(bytes, s.getBytes("US-ASCII"));
	}

	@Test(dataProvider="longTests")
	public void testSetLong(boolean isBigEndian, long value, int offset, int length, byte[] b) {
		byte[] bytes = new byte[length];
		ByteBuffer buf = ByteBuffer.allocate(20);
		FieldAdapter adapter = new IntegerBinaryFieldAdapter(length, true, isBigEndian);
		adapter.setLong(value, offset, length, buf, true);
		buf.position(offset);
		buf.get(bytes, 0, length);
		assertEquals(bytes, b);
	}

	@Test(dataProvider="longTests")
	public void testSetDouble(boolean isBigEndian, long value, int offset, int length, byte[] b) {
		ByteBuffer buf = ByteBuffer.allocate(20);
		FieldAdapter adapter = new IntegerBinaryFieldAdapter(length, true, isBigEndian);
		adapter.setDouble(value, offset, length, buf, true);
		assertEquals(buf.getDouble(offset), (double) value);
	}

	@Test(dataProvider="intTests")
	public void testSetInt(boolean isBigEndian, long value, int offset, int length, byte[] b) {
		byte[] bytes = new byte[length];
		ByteBuffer buf = ByteBuffer.allocate(20);
		FieldAdapter adapter = new IntegerBinaryFieldAdapter(length, true, isBigEndian);
		adapter.setInt((int) value, offset, length, buf, true);
		buf.position(offset);
		buf.get(bytes, 0, length);
		assertEquals(bytes, b);
	}

	@Test(dataProvider="intTests")
	public void testSetFloat(boolean isBigEndian, long value, int offset, int length, byte[] b) {
		ByteBuffer buf = ByteBuffer.allocate(20);
		FieldAdapter adapter = new IntegerBinaryFieldAdapter(length, true, isBigEndian);
		adapter.setFloat(value, offset, length, buf, true);
		assertEquals(buf.getFloat(offset), (float) value);
	}

	@Test(dataProvider="shortTests")
	public void testSetShort(boolean isBigEndian, long value, int offset, int length, byte[] b) {
		byte[] bytes = new byte[length];
		ByteBuffer buf = ByteBuffer.allocate(20);
		FieldAdapter adapter = new IntegerBinaryFieldAdapter(length, true, isBigEndian);
		adapter.setShort((short) value, offset, length, buf, true);
		buf.position(offset);
		buf.get(bytes, 0, length);
		assertEquals(bytes, b);
	}

	@Test(dataProvider="byteTests")
	public void testSetByte(boolean isBigEndian, long value, int offset, int length, byte[] b) {
		byte[] bytes = new byte[length];
		ByteBuffer buf = ByteBuffer.allocate(20);
		FieldAdapter adapter = new IntegerBinaryFieldAdapter(length, true, isBigEndian);
		adapter.setByte((byte) value, offset, length, buf, true);
		buf.position(offset);
		buf.get(bytes, 0, length);
		assertEquals(bytes, b);
	}

	@Test(expectedExceptions=IllegalArgumentException.class)
	public void testBadFieldLength() {
		ByteBuffer buffer = ByteBuffer.allocate(10);
		int length = Float.SIZE / Byte.SIZE;
		FieldAdapter adapter = new IntegerBinaryFieldAdapter(length, true, true);
		adapter.setString("1.2345", 0, length, buffer, true);
	}

	@SuppressWarnings("unused")
	@DataProvider(name="longTests")
	private Object[][] getLongTests() {
		return new Object[][] {
				// isBigEndian, long value, offset, length, byte[] expectedBytes
				{ true,  0x123456781A1B1C1DL, 0, 8, new byte[] { 0x12, 0x34, 0x56, 0x78, 0x1A, 0x1B, 0x1C, 0x1D } },
				{ true,  0x3456781A1B1C1D1EL, 1, 8, new byte[] { 0x34, 0x56, 0x78, 0x1A, 0x1B, 0x1C, 0x1D, 0x1E } },
				{ false, 0x123456781A1B1C1DL, 2, 8, new byte[] { 0x1D, 0x1C, 0x1B, 0x1A, 0x78, 0x56, 0x34, 0x12 } },
				{ false, 0x3456781A1B1C1D1EL, 3, 8, new byte[] { 0x1E, 0x1D, 0x1C, 0x1B, 0x1A, 0x78, 0x56, 0x34 } }
		};
	}

	@SuppressWarnings("unused")
	@DataProvider(name="intTests")
	private Object[][] getIntTests() {
		return new Object[][] {
				// isBigEndian, long value, offset, length, byte[] expectedBytes
				{ true,  0x12345678, 0, 4, new byte[] { 0x12, 0x34, 0x56, 0x78 } },
				{ true,  0x3456781A, 1, 4, new byte[] { 0x34, 0x56, 0x78, 0x1A } },
				{ false, 0x12345678, 2, 4, new byte[] { 0x78, 0x56, 0x34, 0x12 } },
				{ false, 0x3456781A, 3, 4, new byte[] { 0x1A, 0x78, 0x56, 0x34 } }
		};
	}

	@SuppressWarnings("unused")
	@DataProvider(name="shortTests")
	private Object[][] getShortTests() {
		return new Object[][] {
				// isBigEndian, long value, offset, length, byte[] expectedBytes
				{ true,  0x1234, 0, 2, new byte[] { 0x12, 0x34 } },
				{ true,  0x3456, 1, 2, new byte[] { 0x34, 0x56 } },
				{ false, 0x5678, 2, 2, new byte[] { 0x78, 0x56 } },
				{ false, 0x781A, 3, 2, new byte[] { 0x1A, 0x78 } }
		};
	}

	@SuppressWarnings("unused")
	@DataProvider(name="byteTests")
	private Object[][] getByteTests() {
		return new Object[][] {
				// isBigEndian, long value, offset, length, byte[] expectedBytes
				{ true,  0x12, 0, 1, new byte[] { 0x12 } },
				{ true,  0x34, 1, 1, new byte[] { 0x34 } },
				{ false, 0x12, 2, 1, new byte[] { 0x12 } },
				{ false, 0x34, 3, 1, new byte[] { 0x34 } }
		};
	}

	@Test(dataProvider="FourByteTests")
	public void testMSB4BigEndian(boolean isSigned, String stringValue, byte[] bytes) {
		FieldAdapter adapter = new IntegerBinaryFieldAdapter(bytes.length, isSigned, true);
		byte[] actualBytes = new byte[bytes.length];
		ByteBuffer buf = ByteBuffer.wrap(actualBytes);
		BigInteger value = new BigInteger(stringValue);
		adapter.setBigInteger(value, 0, bytes.length, buf, true);

		for (int i=0; i < bytes.length; ++i) {
			assertEquals(actualBytes[i], bytes[i], "Byte value at index " + i + " differs ("
					+ actualBytes[i] + "!=" + bytes[i] + ")");
		}
	}

	@Test(dataProvider="FourByteTests")
	public void testMSB4LittleEndian(boolean isSigned, String stringValue, byte[] bytes) {
		FieldAdapter adapter = new IntegerBinaryFieldAdapter(bytes.length, isSigned, false);
		byte[] actualBytes = new byte[bytes.length];
		ByteBuffer buf = ByteBuffer.wrap(actualBytes);
		BigInteger value = new BigInteger(stringValue);
		adapter.setBigInteger(value, 0, bytes.length, buf, true);

		for (int i=0; i < bytes.length; ++i) {
			assertEquals(actualBytes[bytes.length - i - 1], bytes[i], "Byte value at index " + i + " differs ("
					+ actualBytes[bytes.length - i - 1] + "!=" + bytes[i] + ")");
		}
	}

	@Test(dataProvider="FourByteTests")
	public void testGetSetBigEndian(boolean isSigned, String stringValue, byte[] bytes) {
		FieldAdapter adapter = new IntegerBinaryFieldAdapter(bytes.length, isSigned, false);
		byte[] actualBytes = new byte[bytes.length];
		ByteBuffer buf = ByteBuffer.wrap(actualBytes);
		BigInteger value = new BigInteger(stringValue);
		adapter.setBigInteger(value, 0, bytes.length, buf, true);

		BigInteger actual = adapter.getBigInteger(actualBytes, 0, bytes.length, 0, 0);
		assertEquals(actual, value);
	}

	@DataProvider(name="FourByteTests")
	private Object[][] getFourByteTests() {
		return new Object[][] {
			// signed? string value, bytes (big-endian)
				{ true, "0", new byte[] {0, 0, 0, 0, 0, 0, 0, 0} },
				{ false, "0", new byte[] {0, 0, 0, 0, 0, 0, 0, 0} },

				{ true, "1", new byte[] {0, 0, 0, 0, 0, 0, 0, 1} },
				{ false, "1", new byte[] {0, 0, 0, 0, 0, 0, 0, 1} },

				{ true, "9223372036854775807", new byte[] {0x7F, FF, FF, FF, FF, FF, FF, FF} }, // 2^63 - 1
				{ false, "9223372036854775807", new byte[] {0x7F, FF, FF, FF, FF, FF, FF, FF} }, // 2^63 - 1

				{ true, "-9223372036854775808", new byte[] {(byte) 0x80, 0, 0, 0, 0, 0, 0, 0} }, // - 2^63
				{ false, "9223372036854775808", new byte[] {(byte) 0x80, 0, 0, 0, 0, 0, 0, 0} }, // 2^63

				{ true, "-1", new byte[] {FF, FF, FF, FF, FF, FF, FF, FF} },
				{ false, "18446744073709551615", new byte[] {FF, FF, FF, FF, FF, FF, FF, FF} }, // 2^64 - 1
		};
	}

}
