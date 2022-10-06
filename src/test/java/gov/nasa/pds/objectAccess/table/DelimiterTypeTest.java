// Copyright 2019, California Institute of Technology ("Caltech").
// U.S. Government sponsorship acknowledged.
//
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// * Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
// * Redistributions must reproduce the above copyright notice, this list of
// conditions and the following disclaimer in the documentation and/or other
// materials provided with the distribution.
// * Neither the name of Caltech nor its operating division, the Jet Propulsion
// Laboratory, nor the names of its contributors may be used to endorse or
// promote products derived from this software without specific prior written
// permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package gov.nasa.pds.objectAccess.table;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import org.testng.annotations.Test;

public class DelimiterTypeTest {
  private static final String SEMICOLON = "Semicolon";
  private static final String CARRIAGE_RETURN_LINE_FEED = "Carriage-Return Line-Feed";

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

  @Test(expectedExceptions = {IllegalArgumentException.class})
  public void testBadXMLType() {
    DelimiterType.getDelimiterType("no-such-xml-type");
  }
}
