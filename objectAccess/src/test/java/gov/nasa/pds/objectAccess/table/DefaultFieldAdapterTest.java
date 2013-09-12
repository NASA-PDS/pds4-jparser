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
