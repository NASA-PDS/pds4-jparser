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
import static org.testng.Assert.assertNotNull;

import org.testng.annotations.Test;

public class DelimiterTypeTest {
	private static final String SEMICOLON = "semicolon";
	private static final String CARRIAGE_RETURN_LINE_FEED = "carriage_return line_feed";

	@Test
	public void testGetFieldDelimiter() {
		DelimiterType type = DelimiterType.getDelimiterType(SEMICOLON);
		assertNotNull(type);
		assertEquals(type.getXmlType(), SEMICOLON);
		assertEquals(type.getFieldDelimiter(), ';');		
	}
		
	@Test
	public void testGetRecordDelimiter() {
		DelimiterType type = DelimiterType.getDelimiterType(CARRIAGE_RETURN_LINE_FEED);
		assertNotNull(type);
		assertEquals(type.getXmlType(), CARRIAGE_RETURN_LINE_FEED);
		assertEquals(type.getRecordDelimiter(), "\r\n");		
	}
	
	@Test(expectedExceptions={IllegalArgumentException.class})
	public void testBadXMLType() {
		DelimiterType.getDelimiterType("no-such-xml-type");
	}
}
