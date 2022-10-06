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
import static org.testng.Assert.assertSame;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import gov.nasa.pds.label.object.FieldDescription;
import gov.nasa.pds.label.object.FieldType;

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
