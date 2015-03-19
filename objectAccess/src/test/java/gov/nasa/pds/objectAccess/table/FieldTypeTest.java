package gov.nasa.pds.objectAccess.table;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import gov.nasa.pds.label.object.FieldType;

import org.testng.annotations.Test;

public class FieldTypeTest {

	private static final String ASCII_STRING = "ASCII_String";

	@Test
	public void testGetFieldType() {
		FieldType type = FieldType.getFieldType(ASCII_STRING);
		assertNotNull(type);
		assertEquals(type.getXMLType(), ASCII_STRING);
		assertNotNull(type.getAdapter());
		assertFalse(type.isRightJustified());
	}

	@Test(expectedExceptions={IllegalArgumentException.class})
	public void testBadXMLType() {
		FieldType.getFieldType("no-such-xml-type");
	}

	@Test
	public void testIsRightJustified() {
		assertFalse(FieldType.ASCII_STRING.isRightJustified());
		assertTrue(FieldType.SIGNEDMSB4.isRightJustified());
	}

}
