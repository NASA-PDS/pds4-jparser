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
import java.math.BigInteger;
import java.util.List;
import org.testng.annotations.Test;
import gov.nasa.arc.pds.xml.generated.FieldBinary;
import gov.nasa.arc.pds.xml.generated.FieldBit;
import gov.nasa.arc.pds.xml.generated.FieldLength;
import gov.nasa.arc.pds.xml.generated.FieldLocation;
import gov.nasa.arc.pds.xml.generated.GroupFieldBinary;
import gov.nasa.arc.pds.xml.generated.GroupLength;
import gov.nasa.arc.pds.xml.generated.GroupLocation;
import gov.nasa.arc.pds.xml.generated.PackedDataFields;
import gov.nasa.arc.pds.xml.generated.RecordBinary;
import gov.nasa.arc.pds.xml.generated.TableBinary;
import gov.nasa.pds.label.object.FieldDescription;
import gov.nasa.pds.label.object.FieldType;
import gov.nasa.pds.objectAccess.InvalidTableException;

public class TableBinaryAdapterTest {

  @Test
  public void testTableDefinition() throws InvalidTableException {
    TableBinary table = createTable();
    TableBinaryAdapter adapter = new TableBinaryAdapter(table);

    assertEquals(adapter.getRecordCount(), 1000);
    assertEquals(adapter.getFieldCount(), 5);

    FieldDescription field;

    field = adapter.getField(0); // field1
    assertEquals(field.getName(), "field1");
    assertEquals(field.getOffset(), 0);
    assertEquals(field.getLength(), 4);
    assertEquals(field.getType(), FieldType.SIGNEDMSB4);

    field = adapter.getField(1); // field2
    assertEquals(field.getName(), "field2");
    assertEquals(field.getOffset(), 4);
    assertEquals(field.getLength(), 4);
    assertEquals(field.getStartBit(), 0);
    assertEquals(field.getStopBit(), 3);
    assertEquals(field.getType(), FieldType.UNSIGNEDBITSTRING);

    field = adapter.getField(4); // field 5, 2nd instance of field4
    assertEquals(field.getName(), "field4");
    assertEquals(field.getOffset(), 12);
    assertEquals(field.getLength(), 4);
    assertEquals(field.getType(), FieldType.SIGNEDMSB4);
  }

  private TableBinary createTable() {
    FieldBinary f1 = new FieldBinary();
    f1.setName("field1");
    f1.setDataType(FieldType.SIGNEDMSB4.getXMLType());
    f1.setFieldLocation(getLocation(0));
    f1.setFieldLength(getLength(4));

    FieldBit f2 = new FieldBit();
    f2.setName("field2");
    f2.setStartBitLocation(BigInteger.valueOf(1));
    f2.setStopBitLocation(BigInteger.valueOf(4));
    f2.setDataType(FieldType.UNSIGNEDBITSTRING.getXMLType());

    FieldBit f3 = new FieldBit();
    f3.setName("field3");
    f3.setStartBitLocation(BigInteger.valueOf(5));
    f3.setStopBitLocation(BigInteger.valueOf(8));
    f3.setDataType(FieldType.UNSIGNEDBITSTRING.getXMLType());

    PackedDataFields packedFields = new PackedDataFields();
    List<FieldBit> bitFields = packedFields.getFieldBits();
    bitFields.add(f2);
    bitFields.add(f3);

    FieldBinary packedField = new FieldBinary();
    packedField.setName("packedField");
    packedField.setFieldLocation(getLocation(4));
    packedField.setFieldLength(getLength(4));
    packedField.setDataType(FieldType.SIGNEDMSB4.getXMLType());
    packedField.setPackedDataFields(packedFields);

    FieldBinary f4 = new FieldBinary();
    f4.setName("field4");
    f4.setDataType(FieldType.SIGNEDMSB4.getXMLType());
    f4.setFieldLocation(getLocation(0));
    f4.setFieldLength(getLength(4));

    GroupFieldBinary group = new GroupFieldBinary();
    group.setRepetitions(BigInteger.valueOf(2));
    group.setGroupLocation(getGroupLocation(8));
    group.setGroupLength(getGroupLength(8));
    group.setFields(BigInteger.valueOf(1));
    group.setGroups(BigInteger.valueOf(0));

    List<Object> groupFields = group.getFieldBinariesAndGroupFieldBinaries();
    groupFields.add(f4);

    RecordBinary rec = new RecordBinary();
    rec.setFields(BigInteger.valueOf(2));
    rec.setGroups(BigInteger.valueOf(1));

    List<Object> fields = rec.getFieldBinariesAndGroupFieldBinaries();
    fields.add(f1);
    fields.add(packedField);
    fields.add(group);

    TableBinary tbl = new TableBinary();
    tbl.setRecordBinary(rec);
    tbl.setRecords(BigInteger.valueOf(1000));

    return tbl;
  }

  private FieldLocation getLocation(int offset) {
    FieldLocation loc = new FieldLocation();
    loc.setValue(BigInteger.valueOf(offset + 1));
    return loc;
  }

  private FieldLength getLength(int length) {
    FieldLength len = new FieldLength();
    len.setValue(BigInteger.valueOf(length));
    return len;
  }

  private GroupLocation getGroupLocation(int offset) {
    GroupLocation loc = new GroupLocation();
    loc.setValue(BigInteger.valueOf(offset + 1));
    return loc;
  }

  private GroupLength getGroupLength(int length) {
    GroupLength len = new GroupLength();
    len.setValue(BigInteger.valueOf(length));
    return len;
  }

}
