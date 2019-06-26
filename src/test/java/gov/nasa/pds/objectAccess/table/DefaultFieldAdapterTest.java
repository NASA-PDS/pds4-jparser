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

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import org.testng.annotations.Test;

public class DefaultFieldAdapterTest {
	private static final byte[] DUMMY_BYTES = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8 };
	private static final Charset US_ASCII = Charset.forName("US-ASCII");	
	private static final int BYTE_SIZE = Byte.SIZE / Byte.SIZE;
	private static final int SHORT_SIZE = Short.SIZE / Byte.SIZE;
	private static final int INT_SIZE = Integer.SIZE / Byte.SIZE;
	private static final int LONG_SIZE = Long.SIZE / Byte.SIZE;
	private static final int FLOAT_SIZE = Float.SIZE / Byte.SIZE;
	private static final int DOUBLE_SIZE = Double.SIZE / Byte.SIZE;		
	
	private FieldAdapter adapter = new DefaultFieldAdapter();
	private ByteBuffer buffer = ByteBuffer.allocate(10);
	
	@Test(expectedExceptions={UnsupportedOperationException.class})
	public void testGetByte() {
		adapter.getByte(DUMMY_BYTES, 0, BYTE_SIZE, 0, 0);
	}
	
	@Test(expectedExceptions={UnsupportedOperationException.class})
	public void testGetShort() {
		adapter.getShort(DUMMY_BYTES, 0, SHORT_SIZE, 0, 0);
	}
	
	@Test(expectedExceptions={UnsupportedOperationException.class})
	public void testGetInt() {
		adapter.getInt(DUMMY_BYTES, 0, INT_SIZE, 0, 0);
	}
	
	@Test(expectedExceptions={UnsupportedOperationException.class})
	public void testGetLong() {
		adapter.getLong(DUMMY_BYTES, 0, LONG_SIZE, 0, 0);
	}
	
	@Test(expectedExceptions={UnsupportedOperationException.class})
	public void testGetFloat() {
		adapter.getFloat(DUMMY_BYTES, 0, FLOAT_SIZE, 0, 0);
	}
	
	@Test(expectedExceptions={UnsupportedOperationException.class})
	public void testGetDouble() {
		adapter.getDouble(DUMMY_BYTES, 0, FLOAT_SIZE, 0, 0);
	}
	
	@Test
	public void testGetString() {
		String s = "hello";
		byte[] b = s.getBytes(Charset.forName("US-ASCII"));
		
		assertEquals(adapter.getString(b, 0, b.length, 0, 0), s);
		assertEquals(adapter.getString(b, 0, b.length, 0, 0, US_ASCII), s);
	}
	
	@Test(expectedExceptions={UnsupportedOperationException.class})
	public void testSetByte() {
		adapter.setByte(Byte.MAX_VALUE, 0, BYTE_SIZE, buffer, false);
	}
	
	@Test(expectedExceptions={UnsupportedOperationException.class})
	public void testSetShort() {
		adapter.setShort(Short.MAX_VALUE, 0, SHORT_SIZE, buffer, false);
	}
	
	@Test(expectedExceptions={UnsupportedOperationException.class})
	public void testSetInt() {
		adapter.setInt(Integer.MAX_VALUE, 0, INT_SIZE, buffer, false);
	}
	
	@Test(expectedExceptions={UnsupportedOperationException.class})
	public void testSetLong() {
		adapter.setLong(Long.MAX_VALUE, 0, LONG_SIZE, buffer, false);
	}
	
	@Test(expectedExceptions={UnsupportedOperationException.class})
	public void testSetFloat() {
		adapter.setFloat(Float.MAX_VALUE, 0, FLOAT_SIZE, buffer, false);
	}
	
	@Test(expectedExceptions={UnsupportedOperationException.class})
	public void testSetDouble() {
		adapter.setDouble(Double.MAX_VALUE, 0, DOUBLE_SIZE, buffer, false);
	}
	
	@Test
	public void testSetString() {
		int len = 5;
		String s = "hello";
		byte[] bytes = new byte[len];
		buffer.clear(); // sets the buffer position to 0		
		adapter.setString(s, 0, len, buffer, false);
		assertEquals(buffer.position(), len);
		
		buffer.position(0);
		buffer.get(bytes, 0, len);			
		assertEquals(bytes, s.getBytes(US_ASCII));				
	}
	
	@Test
	public void testRightJustified() {
		int len = 7;		
		byte[] bytes = new byte[len];		
		buffer.clear(); 				
		adapter.setString("12345", 0, len, buffer, true);		
		buffer.position(0);
		buffer.get(bytes, 0, len);
		
		assertEquals(bytes, "  12345".getBytes(US_ASCII));
	}
	
	@Test
	public void testLeftJustified() {
		int len = 7;		
		byte[] bytes = new byte[len];		
		buffer.clear(); 				
		adapter.setString("hello", 0, len, buffer, false);		
		buffer.position(0);
		buffer.get(bytes, 0, len);
		
		assertEquals(bytes, "hello  ".getBytes(US_ASCII));
	}
}
