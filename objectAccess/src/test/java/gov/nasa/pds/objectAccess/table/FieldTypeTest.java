package gov.nasa.pds.objectAccess.table;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

public class FieldTypeTest {
	
	private static final String SHORT_STRING = "ASCII_Short_String_Collapsed";

	@Test
	public void testGetFieldType() {
		FieldType type = FieldType.getFieldType(SHORT_STRING);
		assertNotNull(type);
		assertEquals(type.getXMLType(), SHORT_STRING);
		assertNotNull(type.getAdapter());
		assertFalse(type.isRightJustified());
	}
	
	@Test(expectedExceptions={IllegalArgumentException.class})
	public void testBadXMLType() {
		FieldType.getFieldType("no-such-xml-type");
	}

	@Test
	public void testIsRightJustified() {
		assertFalse(FieldType.ASCII_SHORT_STRING_COLLAPSED.isRightJustified());
		assertTrue(FieldType.SIGNEDMSB4.isRightJustified());
	}

}
