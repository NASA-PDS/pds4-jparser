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

package gov.nasa.pds.label;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import java.io.File;
import java.util.List;
import org.testng.annotations.Test;
import gov.nasa.arc.pds.xml.generated.ProductObservational;
import gov.nasa.pds.label.object.DataObject;
import gov.nasa.pds.label.object.TableObject;
import gov.nasa.pds.objectAccess.ParseException;

public class LabelTest {

  private static final String TABLE_CHARACTER_LABEL =
      "src/test/resources/1000/Product_Table_Character.xml";

  @Test
  public void testProductInfo() throws ParseException {
    Label label = Label.open(new File(TABLE_CHARACTER_LABEL));
    assertSame(label.getProductClass(), ProductObservational.class);
    assertEquals(label.getProductType(), ProductType.PRODUCT_OBSERVATIONAL);
    assertSame(label.getLabelStandard(), LabelStandard.PDS4);
    assertEquals(label.getStandardVersion(), "1.0.0.0");
  }

  @Test
  public void testGetObjects() throws Exception {
    Label label = Label.open(new File(TABLE_CHARACTER_LABEL));
    List<DataObject> objects = label.getObjects();
    assertEquals(objects.size(), 1); // Should be only a single table.
    assertTrue(objects.get(0) instanceof TableObject);

    TableObject table = (TableObject) objects.get(0);
    assertEquals(table.getOffset(), 0);
  }

  @Test
  public void testGetTableObjects() throws Exception {
    Label label = Label.open(new File(TABLE_CHARACTER_LABEL));
    List<TableObject> objects = label.getObjects(TableObject.class);
    assertEquals(objects.size(), 1); // Should be only a single table.
    assertTrue(objects.get(0) instanceof TableObject);

    TableObject table = objects.get(0);
    assertEquals(table.getOffset(), 0);
  }

  @Test(expectedExceptions = {NullPointerException.class})
  public void testClose() throws ParseException {
    Label label = Label.open(new File(TABLE_CHARACTER_LABEL));
    label.close();
    @SuppressWarnings("unused")
    String version = label.getStandardVersion();
  }

  @Test
  public void testReadCharacterTable() throws Exception {
    Label label = Label.open(new File("src/test/resources/1000/Product_Table_Character.xml"));
    List<TableObject> tables = label.getObjects(TableObject.class);
    assertEquals(tables.size(), 1);

    TableObject table = tables.get(0);
    assertEquals(table.getFields().length, 10);
    int recordCount = 0;
    while (table.readNext() != null) {
      ++recordCount;
    }

    assertEquals(recordCount, 23);
  }

  @Test
  public void testReadBinaryTable() throws Exception {
    Label label = Label.open(new File("src/test/resources/1000/Binary_Table_Test.xml"));
    List<TableObject> tables = label.getObjects(TableObject.class);
    assertEquals(tables.size(), 1);

    TableObject table = tables.get(0);
    assertEquals(table.getFields().length, 16); // 4 + 8 bit-fields, + 2 + 2xgroup of 1 = 16
    int recordCount = 0;
    while (table.readNext() != null) {
      ++recordCount;
    }

    assertEquals(recordCount, 2);
  }

  @Test
  public void testReadDelimitedTable() throws Exception {
    Label label = Label.open(new File("src/test/resources/1000/Product_Table_Delimited.xml"));
    List<TableObject> tables = label.getObjects(TableObject.class);
    assertEquals(tables.size(), 1);

    TableObject table = tables.get(0);
    assertEquals(table.getFields().length, 13);
    int recordCount = 0;
    while (table.readNext() != null) {
      ++recordCount;
    }

    assertEquals(recordCount, 3);
  }

}
