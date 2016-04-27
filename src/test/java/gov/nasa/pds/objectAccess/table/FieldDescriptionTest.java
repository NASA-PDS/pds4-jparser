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
import static org.testng.Assert.assertSame;
import gov.nasa.pds.label.object.FieldDescription;
import gov.nasa.pds.label.object.FieldType;

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
		FieldType type = FieldType.ASCII_STRING;
		desc.setType(type);
		assertSame(desc.getType(), type);

		type = FieldType.SIGNEDMSB4;
		desc.setType(type);
		assertSame(desc.getType(), type);
	}

}
