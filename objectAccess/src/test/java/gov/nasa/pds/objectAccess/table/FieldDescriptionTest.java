package gov.nasa.pds.objectAccess.table;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class FieldDescriptionTest {

	private FieldDescription desc;
	
	@BeforeMethod
	public void init() {
		desc = new FieldDescription();
	}
	
	@Test
	public void testFieldName() {
		desc.setName("target");
		assertEquals(desc.getName(), "target");
		desc.setName("size");
		assertEquals(desc.getName(), "size");
	}
	
	@Test
	public void testFieldLength() {
		desc.setLength(5);
		assertEquals(desc.getLength(), 5);
		desc.setLength(10);
		assertEquals(desc.getLength(), 10);
	}
	
	@Test
	public void testFieldOffset() {
		desc.setOffset(5);
		assertEquals(desc.getOffset(), 5);
		desc.setOffset(10);
		assertEquals(desc.getOffset(), 10);
	}
	
	@Test
	public void testFieldType() {
		FieldType type = FieldType.ASCII_SHORT_STRING_COLLAPSED;
		desc.setType(type);
		assertSame(desc.getType(), type);
		
		type = FieldType.SIGNEDMSB4;
		desc.setType(type);
		assertSame(desc.getType(), type);
	}
	
}
