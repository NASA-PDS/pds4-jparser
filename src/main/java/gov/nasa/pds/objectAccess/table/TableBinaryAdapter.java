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

import java.util.ArrayList;
import java.util.List;
import gov.nasa.arc.pds.xml.generated.FieldBinary;
import gov.nasa.arc.pds.xml.generated.FieldBit;
import gov.nasa.arc.pds.xml.generated.GroupFieldBinary;
import gov.nasa.arc.pds.xml.generated.RecordBinary;
import gov.nasa.arc.pds.xml.generated.TableBinary;
import gov.nasa.pds.label.object.FieldDescription;
import gov.nasa.pds.label.object.FieldType;
import gov.nasa.pds.objectAccess.InvalidTableException;
import gov.nasa.pds.objectAccess.utility.Utility;

public class TableBinaryAdapter implements TableAdapter {

  private TableBinary table;
  private List<FieldDescription> fields;

  /**
   * Creates a new instance for a particular table.
   *
   * @param table the table
   */
  public TableBinaryAdapter(TableBinary table) throws InvalidTableException {
    this.table = table;

    this.fields = new ArrayList<FieldDescription>();

    RecordBinary rb = this.table.getRecordBinary();

    if (rb.getFields() == null) {
      throw new InvalidTableException("Invalid label definition. Missing fields attribute.");
    }

    if (rb.getGroups() == null) {
      throw new InvalidTableException("Invalid label definition. Missing groups attribute.");
    }

    expandFields(rb.getFieldBinariesAndGroupFieldBinaries(), 0, rb.getFields().intValueExact(),
        rb.getGroups().intValueExact());
  }

  private void expandFields(List<Object> fields, int baseOffset, int expectedFieldCount,
      int expectedGroupCount) throws InvalidTableException {
    int fieldsCounter = 0;
    int groupsCounter = 0;
    for (Object field : fields) {
      if (field instanceof FieldBinary) {
        expandField((FieldBinary) field, baseOffset);
        fieldsCounter++;
      } else {
        // Must be GroupFieldBinary
        expandGroupField((GroupFieldBinary) field, baseOffset);
        groupsCounter++;
      }
    }

    Utility.validateCounts(expectedFieldCount, fieldsCounter, "Invalid fields count definition.");
    Utility.validateCounts(expectedGroupCount, groupsCounter, "Invalid groups count definition.");
  }

  private void expandField(FieldBinary field, int baseOffset) {
    if (field.getPackedDataFields() != null) {
      expandPackedField(field, baseOffset);
    } else {
      expandBinaryField(field, baseOffset);
    }
  }

  private void expandBinaryField(FieldBinary field, int baseOffset) {
    FieldDescription desc = new FieldDescription();
    desc.setName(field.getName());
    desc.setType(FieldType.getFieldType(field.getDataType()));
    desc.setOffset(field.getFieldLocation().getValue().intValueExact() - 1 + baseOffset);
    desc.setLength(field.getFieldLength().getValue().intValueExact());
    desc.setSpecialConstants(field.getSpecialConstants());
    if (field.getFieldFormat() != null) {
      desc.setFieldFormat(field.getFieldFormat());
    }
    if (field.getFieldStatistics() != null) {
      if (field.getFieldStatistics().getMinimum() != null) {
        desc.setMinimum(field.getFieldStatistics().getMinimum());
      }
      if (field.getFieldStatistics().getMaximum() != null) {
        desc.setMaximum(field.getFieldStatistics().getMaximum());
      }
    }
    // We need to set the start and stop bit to avoid having an error
    // thrown when it comes time to reading in the data
    if (desc.getType().equals(FieldType.SIGNEDBITSTRING)
        || desc.getType().equals(FieldType.UNSIGNEDBITSTRING)) {
      desc.setStartBit(0);
      desc.setStopBit(desc.getLength() - 1);
    }
    this.fields.add(desc);
  }

  private void expandPackedField(FieldBinary field, int baseOffset) {
    for (FieldBit bitField : field.getPackedDataFields().getFieldBits()) {
      expandBitField(field, bitField, baseOffset);
    }
  }

  private void expandBitField(FieldBinary field, FieldBit bitField, int baseOffset) {
    FieldDescription desc = new FieldDescription();
    desc.setName(bitField.getName());
    desc.setType(FieldType.getFieldType(bitField.getDataType()));
    desc.setOffset(field.getFieldLocation().getValue().intValueExact() - 1 + baseOffset);
    desc.setLength(field.getFieldLength().getValue().intValueExact());
    desc.setSpecialConstants(field.getSpecialConstants());
    int startBit = 0;
    if (bitField.getStartBit() != null) {
      startBit = bitField.getStartBit().intValueExact();
    } else if (bitField.getStartBitLocation() != null) {
      startBit = bitField.getStartBitLocation().intValueExact();
    }
    desc.setStartBit(startBit - 1);
    int stopBit = 0;
    if (bitField.getStopBit() != null) {
      stopBit = bitField.getStopBit().intValueExact();
    } else if (bitField.getStopBitLocation() != null) {
      stopBit = bitField.getStopBitLocation().intValueExact();
    }
    desc.setStopBit(stopBit - 1);
    this.fields.add(desc);
  }

  private void expandGroupField(GroupFieldBinary group, int outerOffset)
      throws InvalidTableException {
    int baseOffset = outerOffset + group.getGroupLocation().getValue().intValueExact() - 1;

    int groupLength =
        group.getGroupLength().getValue().intValueExact() / group.getRepetitions().intValueExact();

    // Check that the group length is large enough for the contained fields.
    int actualGroupLength = getGroupExtent(group);

    if (groupLength != actualGroupLength) {
      String msg =
          "GroupFieldBinary attribute group_length is not equal the total size of contained fields. Group length: "
              + (groupLength * group.getRepetitions().longValueExact()) + ", Actual: "
              + (actualGroupLength * group.getRepetitions().longValueExact());
      throw new InvalidTableException(msg);
    }

    for (int i = 0; i < group.getRepetitions().intValueExact(); ++i) {
      expandFields(group.getFieldBinariesAndGroupFieldBinaries(), baseOffset,
          group.getFields().intValueExact(), group.getGroups().intValueExact());
      baseOffset += groupLength;
    }
  }

  private int getGroupExtent(GroupFieldBinary group) {
    int groupExtent = 0;

    for (Object o : group.getFieldBinariesAndGroupFieldBinaries()) {
      if (o instanceof GroupFieldBinary) {
        GroupFieldBinary field = (GroupFieldBinary) o;
        int fieldEnd =
            field.getGroupLocation().getValue().intValueExact() + getGroupExtent(field) - 1;
        groupExtent = Math.max(groupExtent, fieldEnd);
      } else {
        // Must be FieldBinary
        FieldBinary field = (FieldBinary) o;
        int fieldEnd = field.getFieldLocation().getValue().intValueExact()
            + field.getFieldLength().getValue().intValueExact() - 1;
        groupExtent = Math.max(groupExtent, fieldEnd);
      }
    }

    return groupExtent;
  }

  @Override
  public long getRecordCount() {
    return table.getRecords().longValueExact();
  }

  @Override
  public int getFieldCount() {
    return fields.size();
  }

  @Override
  public FieldDescription getField(int index) {
    return fields.get(index);
  }

  @Override
  public FieldDescription[] getFields() {
    return fields.toArray(new FieldDescription[fields.size()]);
  }

  @Override
  public List<FieldDescription> getFieldsList() {
    return fields;
  }

  @Override
  public long getOffset() {
    return table.getOffset().getValue().longValueExact();
  }

  @Override
  public int getRecordLength() {
    return table.getRecordBinary().getRecordLength().getValue().intValueExact();
  }

  @Override
  public String getRecordDelimiter() {
    return table.getRecordDelimiter();
  }

  @Override
  public char getFieldDelimiter() {
    return 0;
  }

  @Override
  public int getMaximumRecordLength() {
    return -1;
  }

}
