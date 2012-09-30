package gov.nasa.pds.objectAccess;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;


public class ByteWiseFileAccessorTest {

	private static final String BIN_DATA_FILE = "./src/test/resources/dph_example_products/product_table_binary/2d234493326edratf3d2537n0m1.dat";
	
	@SuppressWarnings("unused")
	@DataProvider(name="readRecordBytesTest")
	private Object[][] readRecordBytesTest() {
		return new Object[][] {
				{ 1, 8, 8, new byte[] { 0x40, 0x1C, 0x7B, 0x73, (byte) 0xA5, (byte) 0xD9, 0x7F, 0x57} },
				{ 2, 4, 2, new byte[] { 0x04, (byte) 0xE8 } }
		};
	}
	
	@Test(dataProvider="readRecordBytesTest")
	public void testReadRecordBytes(int recordNum, int offset, int length, byte[] expected) {
		try {
			ByteWiseFileAccessor fileObject = new ByteWiseFileAccessor(new File(BIN_DATA_FILE), 0, 96, 336);
			byte[] bytes = fileObject.readRecordBytes(recordNum, offset, length);
			assertNotNull(bytes);
			assertEquals(bytes, expected);
		} catch(Exception e) { }		
	}	
	
	@SuppressWarnings("unused")
	@DataProvider(name="readDoubleTest")
	private Object[][] readDoubleTest() {
		return new Object[][] {
				{ 1, 8 , 7.12055834903716 },
				{ 2, 16, 5.82455419999999973E-2 }
		};
	}
	
	@Test
	public void testIOException() {
		boolean thrown = false;
		try {
			ByteWiseFileAccessor fileObject = new ByteWiseFileAccessor(new File(BIN_DATA_FILE), 0, 97, 336);			
		} catch(IOException e) {
			thrown = true;
		}
		
		assertTrue(thrown);		
	}
	
	@Test
	public void testFileNotFoundException() {
		boolean thrown = false;
		try{
			ByteWiseFileAccessor fileObject = new ByteWiseFileAccessor(new File("file.dat"), 0, 96, 336);
		} catch(FileNotFoundException e) {
			thrown = true;
		} catch(IOException e) { }	
		
		assertTrue(thrown);
	}	
	
	@Test
	public void testFileOffset() throws Exception {
		ByteWiseFileAccessor fileObject = new ByteWiseFileAccessor(new File(BIN_DATA_FILE), 96, 96, 3);
		byte[] bytes = fileObject.readRecordBytes(3, 4, 2);		
		assertEquals(bytes, new byte[] {0x04, (byte) 0xE4});
	}
}
