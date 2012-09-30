package gov.nasa.pds.objectAccess.table;

import static org.testng.Assert.assertEquals;

import java.nio.charset.Charset;

import org.testng.annotations.Test;

public class DefaultFieldAdapterTest {

	private static final byte[] DUMMY_BYTES = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8 };
	
	private FieldAdapter adapter = new DefaultFieldAdapter();
	
	@Test(expectedExceptions={UnsupportedOperationException.class})
	public void testGetByte() {
		adapter.getByte(DUMMY_BYTES, 0, Byte.SIZE / Byte.SIZE, 0, 0);
	}
	
	@Test(expectedExceptions={UnsupportedOperationException.class})
	public void testGetShort() {
		adapter.getShort(DUMMY_BYTES, 0, Short.SIZE / Byte.SIZE, 0, 0);
	}
	
	@Test(expectedExceptions={UnsupportedOperationException.class})
	public void testGetInt() {
		adapter.getInt(DUMMY_BYTES, 0, Integer.SIZE / Byte.SIZE, 0, 0);
	}
	
	@Test(expectedExceptions={UnsupportedOperationException.class})
	public void testGetLong() {
		adapter.getLong(DUMMY_BYTES, 0, Long.SIZE / Byte.SIZE, 0, 0);
	}
	
	@Test(expectedExceptions={UnsupportedOperationException.class})
	public void testGetFloat() {
		adapter.getFloat(DUMMY_BYTES, 0, Float.SIZE / Byte.SIZE, 0, 0);
	}
	
	@Test(expectedExceptions={UnsupportedOperationException.class})
	public void testGetDouble() {
		adapter.getDouble(DUMMY_BYTES, 0, Double.SIZE / Byte.SIZE, 0, 0);
	}
	
	@Test
	public void testGetString() {
		String s = "hello";
		byte[] b = s.getBytes(Charset.forName("US-ASCII"));
		
		assertEquals(adapter.getString(b, 0, b.length, 0, 0), s);
		assertEquals(adapter.getString(b, 0, b.length, 0, 0, Charset.forName("US-ASCII")), s);
	}
	
}
