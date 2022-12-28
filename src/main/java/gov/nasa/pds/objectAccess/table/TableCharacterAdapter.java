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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import gov.nasa.arc.pds.xml.generated.FieldCharacter;
import gov.nasa.arc.pds.xml.generated.GroupFieldCharacter;
import gov.nasa.arc.pds.xml.generated.RecordCharacter;
import gov.nasa.arc.pds.xml.generated.TableCharacter;
import gov.nasa.pds.label.object.FieldDescription;
import gov.nasa.pds.label.object.FieldType;
import gov.nasa.pds.objectAccess.InvalidTableException;

public class TableCharacterAdapter implements TableAdapter {
  private static final Logger LOG = LoggerFactory.getLogger(TableCharacterAdapter.class);
  private TableCharacter table;
  private List<FieldDescription> fields;

  /**
   * Creates a new instance for a particular table.
   *
   * @param table the table
   */
  public TableCharacterAdapter(TableCharacter table) throws InvalidTableException {
    LOG.debug("START");
    this.table = table;

    this.fields = new ArrayList<FieldDescription>();

    RecordCharacter rc = table.getRecordCharacter();

    if (rc.getFields() == null) {
      throw new InvalidTableException("Invalid label definition. Missing fields attribute.");
    }

    if (rc.getGroups() == null) {
      throw new InvalidTableException("Invalid label definition. Missing groups attribute.");
    }

    expandFields(rc.getFieldCharactersAndGroupFieldCharacters(), 0, rc.getFields().intValueExact(),
        rc.getGroups().intValueExact());

    LOG.debug("END");
  }

  private void expandFields(List<Object> fields, int baseOffset, int expectedFieldCount,
      int expectedGroupCount) throws InvalidTableException {
    for (Object field : fields) {
      if (field instanceof FieldCharacter) {
        expandField((FieldCharacter) field, baseOffset);
      } else {
        // Must be GroupFieldCharacter
        expandGroupField((GroupFieldCharacter) field, baseOffset);
      }
    }
  }

  private void expandField(FieldCharacter field, int baseOffset) {
    FieldDescription desc = new FieldDescription();
    desc.setName(field.getName());
    desc.setType(FieldType.getFieldType(field.getDataType()));
    desc.setOffset(field.getFieldLocation().getValue().intValueExact() - 1 + baseOffset);
    desc.setLength(field.getFieldLength().getValue().intValueExact());
    desc.setSpecialConstants(field.getSpecialConstants());
    if (field.getFieldFormat() != null) {
      desc.setFieldFormat(field.getFieldFormat());
    }
    if (field.getValidationFormat() != null) {
      desc.setValidationFormat(field.getValidationFormat());
    }
    if (field.getFieldStatistics() != null) {
      if (field.getFieldStatistics().getMinimum() != null) {
        desc.setMinimum(field.getFieldStatistics().getMinimum());
      }
      if (field.getFieldStatistics().getMaximum() != null) {
        desc.setMaximum(field.getFieldStatistics().getMaximum());
      }
    }
    fields.add(desc);
  }

  private void expandGroupField(GroupFieldCharacter group, int outerOffset)
      throws InvalidTableException {
    int baseOffset = outerOffset + group.getGroupLocation().getValue().intValueExact() - 1;

    int groupLength = group.getGroupLength().getValue().intValueExact();
    // Check that the group length is large enough for the contained fields.
    int actualGroupLength = getGroupExtent(group);
    if (groupLength < actualGroupLength) {
      String msg =
          "ERROR: GroupFieldCharacter attribute group_length is smaller than size of contained fields: "
              + (groupLength) + " < " + (actualGroupLength) + ".";
      groupLength = actualGroupLength;
      throw new InvalidTableException(msg);
    }
    if (groupLength > actualGroupLength) {
      String msg =
          "ERROR: GroupFieldCharacter attribute group_length is larger than size of contained fields: "
              + (groupLength) + " > " + (actualGroupLength) + ".";
      groupLength = actualGroupLength;
      throw new InvalidTableException(msg);

    }

    for (int i = 0; i < group.getRepetitions().intValueExact(); ++i) {
      expandFields(group.getFieldCharactersAndGroupFieldCharacters(), baseOffset,
          group.getFields().intValueExact(), group.getGroups().intValueExact());
      baseOffset += groupLength;
    }
  }

  private int getGroupExtent(GroupFieldCharacter group)
  {
	  int extent, groupExtent = 0;

	  for (Object o : group.getFieldCharactersAndGroupFieldCharacters())
	  {
		  if (o instanceof GroupFieldCharacter)
		  {
			  GroupFieldCharacter child = (GroupFieldCharacter) o;
			  extent = getGroupExtent(child) + child.getGroupLocation().getValue().intValueExact()-1;
			 
		  }
		  else
		  {
			  // Must be FieldCharacter
			  FieldCharacter field = (FieldCharacter) o;
			  extent = field.getFieldLocation().getValue().intValueExact()-1 +
					  field.getFieldLength().getValue().intValueExact();
		  }
		  
		  groupExtent = Math.max (extent, groupExtent);
	  }

	  return groupExtent * group.getRepetitions().intValue();
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
  public long getOffset() {
    return table.getOffset().getValue().longValueExact();
  }

  @Override
  public int getRecordLength() {
    return table.getRecordCharacter().getRecordLength().getValue().intValueExact();
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
  public List<FieldDescription> getFieldsList() {
    return this.fields;
  }

  @Override
  public int getMaximumRecordLength() {
    return -1;
  }

}
