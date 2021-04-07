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

public class DoubleBinaryFieldAdapterTest {
	
	private ByteBuffer buffer = ByteBuffer.allocate(20);
	
	@Test(dataProvider="doubleTests")
	public void testGetDoubleBigEndian(double value, int offset) {
		byte[] buf = longToBytes(Double.doubleToLongBits(value), true);
		DoubleBinaryFieldAdapter adapter = new DoubleBinaryFieldAdapter(true);
		assertEquals(adapter.getDouble(buf, 0, buf.length, 0, 0), value);
		assertEquals(adapter.getFloat(buf, 0, buf.length, 0, 0), (float) value);
	}
	
	@Test(dataProvider="doubleTests")
	public void testGetDoubleLittleEndian(double value, int offset) {
		byte[] buf = longToBytes(Double.doubleToLongBits(value), false);
		DoubleBinaryFieldAdapter adapter = new DoubleBinaryFieldAdapter(false);
		assertEquals(adapter.getDouble(buf, 0, buf.length, 0, 0), value);
		assertEquals(adapter.getFloat(buf, 0, buf.length, 0, 0), (float) value);
	}
	
	@SuppressWarnings("unused")
	@DataProvider(name="doubleTests")
	private Object[][] getDoubleTests() {
		return new Object[][] {
				// value to test, offset
				{ 0.0,  0 },
				{ 1.0,  0 },
				{ -1.0, 0 },
				{ Double.MAX_VALUE,  1 },
				{ -Double.MAX_VALUE, 1 },
				{ Double.POSITIVE_INFINITY, 2 },
				{ Double.NEGATIVE_INFINITY, 2 },
				{ Double.NaN,        3 },
				{ Double.MIN_NORMAL, 4 },
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

	@Test(dataProvider="doubleTests")
	public void testSetDoubleBigEndian(double value, int offset) {
		ByteBuffer buf = ByteBuffer.allocate(20);
		int length = Double.SIZE / Byte.SIZE;
		byte[] bytes = new byte[length];
		byte[] b = longToBytes(Double.doubleToLongBits(value), true);
		FieldAdapter adapter = new DoubleBinaryFieldAdapter(true);					
		adapter.setDouble(value, offset, length, buf, false);
		((Buffer) buf).position(offset);
		buf.get(bytes, 0, length);
		assertEquals(bytes, b);
		
		b = longToBytes(Double.doubleToLongBits((float) value), true);
		adapter.setFloat((float) value, offset, length, buf, false);	
		((Buffer) buf).position(offset);
		buf.get(bytes, 0, length);
		assertEquals(bytes, b);
	}
	
	@Test(dataProvider="doubleTests")
	public void testSetDoubleLittleEndian(double value, int offset) {
		ByteBuffer buf = ByteBuffer.allocate(20);
		int length = Double.SIZE / Byte.SIZE;
		byte[] bytes = new byte[length];
		byte[] b = longToBytes(Double.doubleToLongBits(value), false);
		FieldAdapter adapter = new DoubleBinaryFieldAdapter(false);		
		adapter.setDouble(value, offset, length, buf, false);	
		((Buffer) buf).position(offset);
		buf.get(bytes, 0, length);				
		assertEquals(bytes, b);
		
		b = longToBytes(Double.doubleToLongBits((float) value), false);
		adapter.setFloat((float) value, offset, length, buf, false);
		((Buffer) buf).position(offset);
		buf.get(bytes, 0, length);
		assertEquals(bytes, b);
	}
	
	@Test
	public void testSetString() {
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
		int length = Double.SIZE / Byte.SIZE;
		byte[] bytes = new byte[length];
		((Buffer) buffer).clear();
		FieldAdapter adapter = new DoubleBinaryFieldAdapter(true);		
		adapter.setString(Double.toString(3.14), 0, length, buffer, true);
		((Buffer) buffer).position(0);
		buffer.get(bytes, 0, length);
		assertEquals(bytes, b);
		
		adapter.setString(Double.toString(3.14), 0, length, buffer, true, Charset.forName("US-ASCII"));
		((Buffer) buffer).position(0);
		buffer.get(bytes, 0, length);
		assertEquals(bytes, b);		
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
	
	@Test(expectedExceptions={UnsupportedOperationException.class})
	public void testSetByte() {
		FieldAdapter adapter = new DoubleBinaryFieldAdapter(true);
		adapter.setByte(Byte.MAX_VALUE, 0, 1, buffer, false);
	}
	
	@Test(expectedExceptions={UnsupportedOperationException.class})
	public void testSetShort() {
		FieldAdapter adapter = new DoubleBinaryFieldAdapter(true);
		adapter.setShort(Short.MAX_VALUE, 0, 2, buffer, false);
	}
	
	@Test(expectedExceptions={UnsupportedOperationException.class})
	public void testSetInt() {
		FieldAdapter adapter = new DoubleBinaryFieldAdapter(true);
		adapter.setInt(Integer.MAX_VALUE, 0, 4, buffer, false);
	}
	
	@Test(expectedExceptions={UnsupportedOperationException.class})
	public void testSetLong() {
		FieldAdapter adapter = new DoubleBinaryFieldAdapter(true);
		adapter.setLong(Long.MAX_VALUE, 0, 8, buffer, false);
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
