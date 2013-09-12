package gov.nasa.pds.label.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class LengthLimitedInputStreamTest {

	private static byte[] data;
	static {
		try {
			data = "hello".getBytes("US-ASCII");
		} catch (UnsupportedEncodingException ex) {
			// Cannot get here, since US-ASCII must always be present.
		}
	}
	
	private InputStream baseIn;
	
	private byte[] buf;
	
	@BeforeMethod
	public void init() {
		baseIn = new ByteArrayInputStream(data);
		buf = new byte[100];
	}
	
	@Test
	public void testReadEntireStream() throws IOException {
		LengthLimitedInputStream in = new LengthLimitedInputStream(baseIn, 0, data.length);
		assertEquals(in.available(), data.length);
		
		int nRead = in.read(buf, 0, buf.length);
		assertEquals(nRead, data.length);
		byte[] b = new byte[nRead];
		System.arraycopy(buf, 0, b, 0, nRead);
		assertEquals(b, data);
		assertEquals(in.available(), 0);
	}
	
	@Test
	public void testReadByBytes() throws IOException {
		LengthLimitedInputStream in = new LengthLimitedInputStream(baseIn, 1, 2);
		assertEquals(in.available(), 2);
		
		assertEquals(in.read(), (int) 'e');
		assertEquals(in.available(), 1);
		assertEquals(in.read(), (int) 'l');
		assertEquals(in.read(), -1);
		assertEquals(in.available(), 0);
	}
	
	@Test
	public void testSkip() throws IOException {
		LengthLimitedInputStream in = new LengthLimitedInputStream(baseIn, 1, 2);
		assertEquals(in.available(), 2);
		
		assertEquals(in.skip(1), 1);
		assertEquals(in.available(), 1);
		assertEquals(in.read(), (int) 'l');
		assertEquals(in.read(), -1);
		assertEquals(in.available(), 0);
	}
	
	@Test
	public void testStreamEndsEarly() throws IOException {
		LengthLimitedInputStream in = new LengthLimitedInputStream(baseIn, 0, 100);
		assertEquals(in.available(), 100);
		
		int nRead = in.read(buf, 0, buf.length);
		assertEquals(nRead, data.length);
		byte[] b = new byte[nRead];
		System.arraycopy(buf, 0, b, 0, nRead);
		assertEquals(b, data);
		assertEquals(in.available(), 100 - data.length);
		
		nRead = in.read(buf, 0, buf.length);
		assertEquals(nRead, -1);
		assertEquals(in.available(), 0);
	}
	
	@Test
	public void testStreamEndsEarlyByBytes() throws IOException {
		LengthLimitedInputStream in = new LengthLimitedInputStream(baseIn, 3, 100);
		assertEquals(in.available(), 100);
		
		assertEquals(in.read(), (int) 'l');
		assertEquals(in.available(), 99);
		assertEquals(in.read(), (int) 'o');
		assertEquals(in.available(), 98);
		assertEquals(in.read(), -1);
		assertEquals(in.available(), 0);
	}
	
}
