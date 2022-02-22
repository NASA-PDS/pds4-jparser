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

import java.nio.Buffer;
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
		((Buffer) buf).position(offset);
		buf.get(bytes, 0, length);
		assertEquals(bytes, b);
				
		adapter.setDouble((double) value, offset, length, buf, false);
		((Buffer) buf).position(offset);
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
		((Buffer) buf).position(offset);
		buf.get(bytes, 0, length);		
		assertEquals(bytes, b);
		
		adapter.setDouble((double) value, offset, length, buf, false);
		((Buffer) buf).position(offset);
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
