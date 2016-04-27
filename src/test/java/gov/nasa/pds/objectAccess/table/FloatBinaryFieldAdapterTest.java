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
package gov.nasa.pds.objectAccess.table;

import static org.testng.Assert.assertEquals;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class FloatBinaryFieldAdapterTest {
	
	private ByteBuffer buffer = ByteBuffer.allocate(10);
	
	@Test(dataProvider="floatTests")
	public void testGetFloatBigEndian(float value, int offset) {
		byte[] buf = intToBytes(Float.floatToIntBits(value), true);
		FloatBinaryFieldAdapter adapter = new FloatBinaryFieldAdapter(true);
		assertEquals(adapter.getFloat(buf, 0, buf.length, 0, 0), value);
		assertEquals(adapter.getDouble(buf, 0, buf.length, 0, 0), (double) value);
	}
	
	@Test(dataProvider="floatTests")
	public void testGetFloatLittleEndian(float value, int offset) {
		byte[] buf = intToBytes(Float.floatToIntBits(value), false);
		FloatBinaryFieldAdapter adapter = new FloatBinaryFieldAdapter(false);
		assertEquals(adapter.getFloat(buf, 0, buf.length, 0, 0), value);
		assertEquals(adapter.getDouble(buf, 0, buf.length, 0, 0), (double) value);
	}
	
	@SuppressWarnings("unused")
	@DataProvider(name="floatTests")
	private Object[][] getFloatTests() {
		return new Object[][] {
				// value to test, offset
				{ 0.0F,  0 },
				{ 1.0F,  0 },
				{ -1.0F, 0 },
				{ Float.MAX_VALUE,   1 },
				{ -Float.MAX_VALUE,  1 },
				{ Float.POSITIVE_INFINITY, 2 },
				{ Float.NEGATIVE_INFINITY, 2 },
				{ Float.NaN,        3 },
				{ Float.MIN_NORMAL, 4 },
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
	
	@Test(dataProvider="floatTests")
	public void testSetFloatBigEndian(float value, int offset) {
		ByteBuffer buf = ByteBuffer.allocate(10);
		int length = Float.SIZE / Byte.SIZE;
		byte[] bytes = new byte[length];
		byte[] b = intToBytes(Float.floatToIntBits(value), true);
		FieldAdapter adapter = new FloatBinaryFieldAdapter(true);
		
		adapter.setFloat(value, offset, length, buf, false);
		buf.position(offset);
		buf.get(bytes, 0, length);
		assertEquals(bytes, b);
				
		adapter.setDouble((double) value, offset, length, buf, false);
		buf.position(offset);
		buf.get(bytes, 0, length);
		assertEquals(bytes, b);
	}
	
	@Test(dataProvider="floatTests")
	public void testSetFloatLittleEndian(float value, int offset) {
		ByteBuffer buf = ByteBuffer.allocate(10);
		int length = Float.SIZE / Byte.SIZE;
		byte[] bytes = new byte[length];
		byte[] b = intToBytes(Float.floatToIntBits(value), false);
		FieldAdapter adapter = new FloatBinaryFieldAdapter(false);
		
		adapter.setFloat(value, offset, length, buf, false);
		buf.position(offset);
		buf.get(bytes, 0, length);		
		assertEquals(bytes, b);
		
		adapter.setDouble((double) value, offset, length, buf, false);
		buf.position(offset);
		buf.get(bytes, 0, length);
		assertEquals(bytes, b);
	}
	
	@Test
	public void testSetString() {
		int length = Float.SIZE / Byte.SIZE;
		long bits = Float.floatToIntBits(3.14F);
		byte[] bytes = new byte[length];
		byte[] b = new byte[] {
				(byte) ((bits >> 24) & 0xFF),
				(byte) ((bits >> 16) & 0xFF),
				(byte) ((bits >> 8) & 0xFF),
				(byte) (bits & 0xFF),
		};
		FieldAdapter adapter = new FloatBinaryFieldAdapter(true);
		adapter.setString(Float.toString(3.14F), 0, length, buffer, true);
		buffer.get(bytes, 0, length);
		assertEquals(bytes, b);
		
		adapter.setString(Float.toString(3.14F), 4, length, buffer, true, Charset.forName("US-ASCII"));
		buffer.get(bytes, 0, length);
		assertEquals(bytes, b);
	}
	
	@Test(expectedExceptions={UnsupportedOperationException.class})
	public void testSetByte() {
		FieldAdapter adapter = new FloatBinaryFieldAdapter(true);
		adapter.setByte(Byte.MAX_VALUE, 0, 1, buffer, false);
	}
	
	@Test(expectedExceptions={UnsupportedOperationException.class})
	public void testSetShort() {
		FieldAdapter adapter = new FloatBinaryFieldAdapter(true);
		adapter.setShort(Short.MAX_VALUE, 0, 2, buffer, false);
	}
	
	@Test(expectedExceptions={UnsupportedOperationException.class})
	public void testSetInt() {
		FieldAdapter adapter = new FloatBinaryFieldAdapter(true);
		adapter.setInt(Integer.MAX_VALUE, 0, 4, buffer, false);
	}
	
	@Test(expectedExceptions={UnsupportedOperationException.class})
	public void testSetLong() {
		FieldAdapter adapter = new FloatBinaryFieldAdapter(true);
		adapter.setLong(Long.MAX_VALUE, 0, 8, buffer, false);
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
