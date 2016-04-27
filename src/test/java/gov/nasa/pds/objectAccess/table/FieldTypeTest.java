// Copyright 2006-2016, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
// Any commercial use must be negotiated with the Office of Technology Transfer
// at the California Institute of Technology.
//
// This software is subject to U. S. export control laws and regulations
// (22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software
// is subject to U.S. export control laws and regulations, the recipient has
// the responsibility to obtain export licenses or other export authority as
// may be required before exporting such information to foreign countries or
// providing access to foreign nationals.
//
// $Id$
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
