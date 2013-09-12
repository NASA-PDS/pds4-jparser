package gov.nasa.pds.objectAccess.table;

import static org.testng.Assert.assertEquals;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class NumericTextFieldAdapterTest {
	
	private static final Charset US_ASCII = Charset.forName("US-ASCII");	
	
	private ByteBuffer buffer = ByteBuffer.allocate(50);
	private FieldAdapter adapter = new NumericTextFieldAdapter();

	@Test
	public void testGoodInt() {
		String s = "123";
		byte[] b = s.getBytes(Charset.forName("US-ASCII"));
		assertEquals(adapter.getByte(b, 0, b.length, 0, 0), Byte.parseByte(s));
		assertEquals(adapter.getShort(b, 0, b.length, 0, 0), Short.parseShort(s));
		assertEquals(adapter.getInt(b, 0, b.length, 0, 0), Integer.parseInt(s));
	}
	
	@Test(dataProvider="BadByteTests", expectedExceptions={NumberFormatException.class})
	public void testBadByte(String s) {
		byte[] b = s.getBytes(Charset.forName("US-ASCII"));
		assertEquals(adapter.getByte(b, 0, b.length, 0, 0), Byte.parseByte(s));
	}
	
	@SuppressWarnings("unused")
	@DataProvider(name="BadByteTests")
	private Object[][] getBadByteTests() {
		return new Object[][] {
				// string
				{ "128" },
				{ "-129" }
		};
	}
	
	@Test(dataProvider="BadShortTests", expectedExceptions={NumberFormatException.class})
	public void testBadShort(String s) {
		byte[] b = s.getBytes(Charset.forName("US-ASCII"));
		assertEquals(adapter.getShort(b, 0, b.length, 0, 0), Short.parseShort(s));
	}
	
	@SuppressWarnings("unused")
	@DataProvider(name="BadShortTests")
	private Object[][] getBadShortTests() {
		return new Object[][] {
				// string
				{ "32768" },
				{ "-32769" }
		};
	}
	
	@Test(dataProvider="BadIntTests", expectedExceptions={NumberFormatException.class})
	public void testBadInt(String s) {
		byte[] b = s.getBytes(Charset.forName("US-ASCII"));
		assertEquals(adapter.getInt(b, 0, b.length, 0, 0), Integer.parseInt(s));
	}
	
	@SuppressWarnings("unused")
	@DataProvider(name="BadIntTests")
	private Object[][] getBadIntTests() {
		return new Object[][] {
				// string
				{ "2147483648" },
				{ "-2147483649" }
		};
	}
	
	@Test
	public void testGoodLong() {
		String s = "123";
		byte[] b = s.getBytes(Charset.forName("US-ASCII"));
		assertEquals(adapter.getLong(b, 0, b.length, 0, 0), Long.parseLong(s));
	}
	
	@Test(expectedExceptions={NumberFormatException.class})
	public void testBadLong() {
		String s = "18446744073709551616"; // 2^64, not representable as a long
		byte[] b = s.getBytes(Charset.forName("US-ASCII"));
		assertEquals(adapter.getLong(b, 0, b.length, 0, 0), Long.parseLong(s));
	}
	
	@Test
	public void testGetFloat() {
		String s = "0.12345678901234567890";
		byte[] b = s.getBytes(Charset.forName("US-ASCII"));
		assertEquals(adapter.getFloat(b, 0, b.length, 0, 0), Float.parseFloat(s), 0.0000001);
	}
	
	@Test
	public void testGetDouble() {
		String s = "0.12345678901234567890";
		byte[] b = s.getBytes(Charset.forName("US-ASCII"));
		assertEquals(adapter.getDouble(b, 0, b.length, 0, 0), Double.parseDouble(s), 0.000000000000001);
	}
	
	@Test
	public void testSetGoodInt() {				
		int value = 123;
		byte[] b = Integer.toString(value).getBytes(US_ASCII);
		int length = b.length;
		byte[] bytes = new byte[length];		
		buffer.clear();
		
		adapter.setInt(value, 0, length, buffer, false);		
		buffer.position(0);
		buffer.get(bytes, 0, length);
		assertEquals(bytes, b);
				
		adapter.setShort((short) value, 5, length, buffer, false);		
		buffer.position(5);
		buffer.get(bytes, 0, length);
		assertEquals(bytes, b);
				
		adapter.setByte((byte) value, 10, length, buffer, false);		
		buffer.position(10);
		buffer.get(bytes, 0, length);
		assertEquals(bytes, b);		
	}
	
	@Test
	public void testSetLong() {				
		long value = 123;
		byte[] b = Long.toString(value).getBytes(US_ASCII);
		int length = b.length;
		byte[] bytes = new byte[length];		
		buffer.clear();		
		adapter.setLong(value, 0, length, buffer, false);		
		buffer.position(0);
		buffer.get(bytes, 0, length);
		assertEquals(bytes, b);
	}
	
	@Test
	public void testSetFloat() {				
		float value = 0.12345678901234567890f;
		byte[] b = Float.toString(value).getBytes(US_ASCII);
		int length = b.length;
		byte[] bytes = new byte[length];
		buffer.clear();		
		adapter.setFloat(value, 0, length, buffer, false);		
		buffer.position(0);
		buffer.get(bytes, 0, length);			
		assertEquals(bytes, b);
	}
	
	@Test
	public void testSetDouble() {				
		double value = 0.12345678901234567890;					   
		byte[] b = Double.toString(value).getBytes(US_ASCII);		
		int length = b.length;
		byte[] bytes = new byte[length];
		buffer.clear();
		adapter.setDouble(value, 0, length, buffer, false);		
		buffer.position(0);
		buffer.get(bytes, 0, length);		
		assertEquals(bytes, b);	
	}
	
	@Test(expectedExceptions=IllegalArgumentException.class)
	public void testBadFieldLength() {		
		ByteBuffer buffer = ByteBuffer.allocate(10);
		adapter.setFloat(Float.MAX_VALUE, 0, Float.SIZE / Byte.SIZE, buffer, false);
	}
}
