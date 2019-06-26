// Copyright 2019, California Institute of Technology ("Caltech").
// U.S. Government sponsorship acknowledged.
//
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// * Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
// * Redistributions must reproduce the above copyright notice, this list of
// conditions and the following disclaimer in the documentation and/or other
// materials provided with the distribution.
// * Neither the name of Caltech nor its operating division, the Jet Propulsion
// Laboratory, nor the names of its contributors may be used to endorse or
// promote products derived from this software without specific prior written
// permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package gov.nasa.pds.objectAccess.table;

import static org.testng.Assert.assertEquals;

import java.nio.charset.Charset;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class BitFieldAdapterTest {
	
	/** A byte value with all 1 bits. (We would write this as 0xFF, but
	 * that value is larger than Byte.MAX, since byte is signed.) */
	private static final int BYTE_FF = (byte) -1;

	private FieldAdapter adapter;
	private FieldAdapter signedAdapter;
	
	@BeforeMethod
	public void init() {
		adapter = new BitFieldAdapter(false);
		signedAdapter = new BitFieldAdapter(true);
	}
	
	@Test(dataProvider="integerTests")
	public void testIntegers(byte[] buf, int offset, int length, int startByte, int stopByte, long expectedValue) {
		assertEquals(adapter.getLong(buf, offset, length, startByte, stopByte), expectedValue);
		assertEquals(adapter.getFloat(buf, offset, length, startByte, stopByte), (float) expectedValue);
		assertEquals(adapter.getDouble(buf, offset, length, startByte, stopByte), (double) expectedValue);
		if (Integer.MIN_VALUE <= expectedValue && expectedValue <= Integer.MAX_VALUE) {
			assertEquals(adapter.getInt(buf, offset, length, startByte, stopByte), (int) expectedValue);
		}
		if (Short.MIN_VALUE <= expectedValue && expectedValue <= Short.MAX_VALUE) {
			assertEquals(adapter.getShort(buf, offset, length, startByte, stopByte), (short) expectedValue);
		}
		if (Byte.MIN_VALUE <= expectedValue && expectedValue <= Byte.MAX_VALUE) {
			assertEquals(adapter.getByte(buf, offset, length, startByte, stopByte), (byte) expectedValue);
		}
	}
	
	@SuppressWarnings("unused")
	@DataProvider(name="integerTests")
	private Object[][] getIntegerTests() {
		return new Object[][] {
				// byte[] buf, offset, length, startBit, stopBit, long expectedValue
				
				// long tests
				{ new byte[] { 0x12, 0x34, 0x56, 0x78, 0x1A, 0x1B, 0x1C, 0x1D, 0x1E }, 0, 9, 0, 63, 0x123456781A1B1C1DL },
				{ new byte[] { 0x12, 0x34, 0x56, 0x78, 0x1A, 0x1B, 0x1C, 0x1D, 0x1E }, 0, 9, 8, 71, 0x3456781A1B1C1D1EL },
				
				// int tests
				{ new byte[] { 0x12, 0x34, 0x56, 0x78, 0x1A, 0x1B, 0x1C, 0x1D, 0x1E }, 1, 8, 0, 31, 0x3456781AL },
				{ new byte[] { 0x12, 0x34, 0x56, 0x78, 0x1A, 0x1B, 0x1C, 0x1D, 0x1E }, 1, 8, 4, 35, 0x456781A1L },
				{ new byte[] { 0x12, 0x34, 0x56, 0x78, 0x1A, 0x1B, 0x1C, 0x1D, 0x1E }, 1, 8, 8, 39, 0x56781A1BL },
				
				// short tests
				{ new byte[] { 0x12, 0x34, 0x56, 0x78, 0x1A, 0x1B, 0x1C, 0x1D, 0x1E }, 1, 8, 0, 15, 0x3456L },
				{ new byte[] { 0x12, 0x34, 0x56, 0x78, 0x1A, 0x1B, 0x1C, 0x1D, 0x1E }, 1, 8, 4, 19, 0x4567L },
				{ new byte[] { 0x12, 0x34, 0x56, 0x78, 0x1A, 0x1B, 0x1C, 0x1D, 0x1E }, 1, 8, 8, 23, 0x5678L },
				
				// byte tests
				{ new byte[] { 0x12, 0x34, 0x56, 0x78, 0x1A, 0x1B, 0x1C, 0x1D, 0x1E }, 1, 8, 0, 7, 0x34L },
				{ new byte[] { 0x12, 0x34, 0x56, 0x78, 0x1A, 0x1B, 0x1C, 0x1D, 0x1E }, 1, 8, 4, 11, 0x45L },
				{ new byte[] { 0x12, 0x34, 0x56, 0x78, 0x1A, 0x1B, 0x1C, 0x1D, 0x1E }, 1, 8, 8, 15, 0x56L },
		};
	}
	
	@Test
	public void testGetString() {
		byte[] b = new byte[] { 0x12, 0x34, 0x56, 0x78 };
		assertEquals(adapter.getString(b, 0, b.length, 0, b.length*Byte.SIZE - 1), Long.toString(0x12345678));
		assertEquals(adapter.getString(b, 0, b.length, 0, b.length*Byte.SIZE - 1, Charset.forName("US-ASCII")), Long.toString(0x12345678));
	}
	
	@Test(dataProvider="ByteOutOfRangeTests", expectedExceptions={NumberFormatException.class})
	public void testByteOutOfRange(byte[] b) {
		signedAdapter.getByte(b, 0, b.length, 0, b.length*Byte.SIZE - 1);
	}
	
	@Test(dataProvider="ShortOutOfRangeTests", expectedExceptions={NumberFormatException.class})
	public void testShortOutOfRange(byte[] b) {
		signedAdapter.getShort(b, 0, b.length, 0, b.length*Byte.SIZE - 1);
	}
	
	@Test(dataProvider="IntegerOutOfRangeTests", expectedExceptions={NumberFormatException.class})
	public void testIntegerOutOfRange(byte[] b) {
		signedAdapter.getInt(b, 0, b.length, 0, b.length*Byte.SIZE - 1);
	}
	
	@SuppressWarnings("unused")
	@DataProvider(name="ByteOutOfRangeTests")
	private Object[][] getByteOutOfRangeTests() {
		return new Object[][] {
				// byte[] bytes
				{ new byte[] { 0x01, 0x00 } },
				{ new byte[] { (byte) 0xFF, 0x7F } },
		};
	}
	
	@SuppressWarnings("unused")
	@DataProvider(name="ShortOutOfRangeTests")
	private Object[][] getShortOutOfRangeTests() {
		return new Object[][] {
				// byte[] bytes
				{ new byte[] { 0x01, 0x00, 0x00 } },
				{ new byte[] { (byte) 0xFF, 0x7F, (byte) 0xFF } },
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
	
	
	
	@Test(expectedExceptions={ArrayIndexOutOfBoundsException.class})
	public void testNegativeStartBit() {
		byte[] b = { 0x00 };
		adapter.getLong(b, 0, b.length, -1, 1);
	}

	@Test(expectedExceptions={ArrayIndexOutOfBoundsException.class})
	public void testStopBitTooLarge() {
		byte[] b = { 0x00 };
		adapter.getLong(b, 0, b.length, 0, b.length * Byte.SIZE);
	}
	
	@Test(expectedExceptions={IllegalArgumentException.class})
	public void testFieldWiderThanLong() {
		byte[] b = new byte[9];
		adapter.getLong(b, 0, b.length, 0, Long.SIZE);
	}
	
	@Test(expectedExceptions={NumberFormatException.class})
	public void testBitFieldSpansMoreThanLong() {
		byte[] b = new byte[9];
		adapter.getLong(b, 0, b.length, 1, Long.SIZE);
	}

	@Test(dataProvider="rightmostBitsTests")
	public void testRightmostBits(long value, int nBits, long expected) {
		assertEquals(BitFieldAdapter.rightmostBits(value, nBits, false), expected);
	}
	
	@SuppressWarnings("unused")
	@DataProvider(name="rightmostBitsTests")
	private Object[][] getRightmostBitsTests() {
		return new Object[][] {
				// value, nBits, expected
				{ 0L, 0, 0L },
				{ 0L, 1, 0L },
				{ 0L, 32, 0L },
				
				{ 1L, 0, 0L },
				{ 1L, 1, 1L },
				{ 1L, 2, 1L },
				{ 1L, 63, 1L },
				
				{ 0xFFFFFFFFL, 1, 1L },
				{ 0xFFFFFFFFL, 32, 0xFFFFFFFFL },
				{ 0x7FFFFFFFL, 31, 0x7FFFFFFFL },
				{ 0xFFFFFFFFL, 31, 0x7FFFFFFFL },
				
				{ 0xFFFFFFFFFFFFFFFFL, 1, 1L },
				{ 0xFFFFFFFFFFFFFFFFL, 64, 0xFFFFFFFFFFFFFFFFL },
				{ 0x7FFFFFFFFFFFFFFFL, 63, 0x7FFFFFFFFFFFFFFFL },
				{ 0xFFFFFFFFFFFFFFFFL, 63, 0x7FFFFFFFFFFFFFFFL },
				
				{ 0xFFL, 1, 1L },
				{ 0xFFL, 2, 0x03L },
				{ 0xFFL, 7, 0x7FL },
				{ 0xFFL, 8, 0xFFL },
				{ 0xFFL, 9, 0xFFL },
				{ 0xFFL, 63, 0xFFL }
		};
	}
	
	@Test(dataProvider="SignedRightMostBitsTests")
	public void testSignedRightmostBits(long value, int nBits, long expected) {
		assertEquals(BitFieldAdapter.rightmostBits(value, nBits, true), expected);
	}
	
	@SuppressWarnings("unused")
	@DataProvider(name="SignedRightMostBitsTests")
	private Object[][] getSignedRightMostBitsTests() {
		return new Object[][] {
				// value, nBits, expected
				{ 0L, 0, 0L },
				{ 0L, 1, 0L },
				{ 0L, 32, 0L },
				
				{ 1L, 0, 0L },
				{ 1L, 1, 0xFFFFFFFFFFFFFFFFL },
				{ 1L, 2, 1L },
				{ 1L, 63, 1L },
				
				{ 0xFFFFFFFFL, 1, 0xFFFFFFFFFFFFFFFFL },
				{ 0xFFFFFFFFL, 33, 0xFFFFFFFFL },
				{ 0xFFFFFFFFL, 32, 0xFFFFFFFFFFFFFFFFL },
				{ 0x7FFFFFFFL, 32, 0x7FFFFFFFL },
				{ 0x7FFFFFFFL, 31, 0xFFFFFFFFFFFFFFFFL },
				{ 0xFFFFFFFFL, 31, 0xFFFFFFFFFFFFFFFFL },
				
				{ 0xFFFFFFFFFFFFFFFFL, 1, 0xFFFFFFFFFFFFFFFFL },
				{ 0xFFFFFFFFFFFFFFFFL, 64, 0xFFFFFFFFFFFFFFFFL },
				{ 0x7FFFFFFFFFFFFFFFL, 64, 0x7FFFFFFFFFFFFFFFL },
				{ 0x7FFFFFFFFFFFFFFFL, 63, 0xFFFFFFFFFFFFFFFFL },
				{ 0xFFFFFFFFFFFFFFFFL, 63, 0xFFFFFFFFFFFFFFFFL },
				
				{ 0xFFL, 1, 0xFFFFFFFFFFFFFFFFL },
				{ 0xFFL, 2, 0xFFFFFFFFFFFFFFFFL },
				{ 0xFFL, 8, 0xFFFFFFFFFFFFFFFFL },
				{ 0xFFL, 9, 0xFFL }
		};
	}
	
	@Test(dataProvider="bytesAsLongTests")
	public void testGetbytesAsLong(byte[] buf, int off, int len, int startByte, int stopByte, long expected) {
		assertEquals(BitFieldAdapter.getBytesAsLong(buf, off, startByte, stopByte), expected);
	}
	
	@SuppressWarnings("unused")
	@DataProvider(name="bytesAsLongTests")
	private Object[][] getbytesAsLongTests() {
		return new Object[][] {
				{ new byte[] { 0x12, 0x34, 0x56, 0x78 }, 0, 4, 0, 0, 0x12L },
				{ new byte[] { 0x12, 0x34, 0x56, 0x78 }, 0, 4, 1, 1, 0x34L },
				{ new byte[] { 0x12, 0x34, 0x56, 0x78 }, 0, 4, 2, 2, 0x56L },
				{ new byte[] { 0x12, 0x34, 0x56, 0x78 }, 0, 4, 3, 3, 0x78L },
				{ new byte[] { 0x12, 0x34, 0x56, 0x78 }, 0, 4, 0, 3, 0x12345678L },
				
				{ new byte[] { 0x11, 0x22, 0x12, 0x34, 0x56, 0x78, 0x33, 0x44 }, 2, 4, 0, 0, 0x12L },
				{ new byte[] { 0x11, 0x22, 0x12, 0x34, 0x56, 0x78, 0x33, 0x44 }, 2, 4, 1, 1, 0x34L },
				{ new byte[] { 0x11, 0x22, 0x12, 0x34, 0x56, 0x78, 0x33, 0x44 }, 2, 4, 2, 2, 0x56L },
				{ new byte[] { 0x11, 0x22, 0x12, 0x34, 0x56, 0x78, 0x33, 0x44 }, 2, 4, 3, 3, 0x78L },
				{ new byte[] { 0x11, 0x22, 0x12, 0x34, 0x56, 0x78, 0x33, 0x44 }, 2, 4, 0, 3, 0x12345678L },
				
				{ new byte[] { BYTE_FF }, 0, 1, 0, 0, 0xFFL },
				{ new byte[] { BYTE_FF, BYTE_FF, BYTE_FF, BYTE_FF }, 0, 1, 0, 0, 0xFFL },
				{ new byte[] { BYTE_FF, BYTE_FF, BYTE_FF, BYTE_FF }, 0, 1, 0, 1, 0xFFFFL },
				{ new byte[] { BYTE_FF, BYTE_FF, BYTE_FF, BYTE_FF }, 0, 1, 1, 3, 0xFFFFFFL },
				
				{ new byte[] { 0x7F, BYTE_FF, BYTE_FF, BYTE_FF }, 0, 1, 0, 3, 0x7FFFFFFFL },
		};
	}
	
}
