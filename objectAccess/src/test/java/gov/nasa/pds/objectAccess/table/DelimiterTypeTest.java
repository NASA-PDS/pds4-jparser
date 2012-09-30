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
