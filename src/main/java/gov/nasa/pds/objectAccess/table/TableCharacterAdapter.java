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

import gov.nasa.arc.pds.xml.generated.FieldCharacter;
import gov.nasa.arc.pds.xml.generated.GroupFieldCharacter;
import gov.nasa.arc.pds.xml.generated.TableCharacter;
import gov.nasa.pds.label.object.FieldDescription;
import gov.nasa.pds.label.object.FieldType;

import java.util.ArrayList;
import java.util.List;

public class TableCharacterAdapter implements TableAdapter {

	TableCharacter table;
	List<FieldDescription> fields;

	/**
	 * Creates a new instance for a particular table.
	 *
	 * @param table the table
	 */
	public TableCharacterAdapter(TableCharacter table) {
		this.table = table;

		fields = new ArrayList<FieldDescription>();
		expandFields(table.getRecordCharacter().getFieldCharactersAndGroupFieldCharacters(), 0);
	}

	private void expandFields(List<Object> fields, int baseOffset) {
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
		desc.setOffset(field.getFieldLocation().getValue() - 1 + baseOffset);
		desc.setLength(field.getFieldLength().getValue());
    if (field.getFieldFormat() != null) {
      desc.setFormat(field.getFieldFormat());
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

	private void expandGroupField(GroupFieldCharacter group, int outerOffset) {
		int baseOffset = outerOffset + group.getGroupLocation().getValue() - 1;

		int groupLength = group.getGroupLength().getValue() / group.getRepetitions();

		// Check that the group length is large enough for the contained fields.
		int actualGroupLength = getGroupExtent(group);

		if (groupLength < actualGroupLength) {
			System.err.println("WARNING: GroupFieldBinary attribute group_length is smaller than size of contained fields: "
					+ (groupLength * group.getRepetitions())
					+ "<"
					+ (actualGroupLength * group.getRepetitions()));
			groupLength = actualGroupLength;
		}

		for (int i=0; i < group.getRepetitions(); ++i) {
			expandFields(group.getFieldCharactersAndGroupFieldCharacters(), baseOffset);
			baseOffset += groupLength;
		}
	}

	private int getGroupExtent(GroupFieldCharacter group) {
		int groupExtent = 0;

		for (Object o : group.getFieldCharactersAndGroupFieldCharacters()) {
			if (o instanceof GroupFieldCharacter) {
				GroupFieldCharacter field = (GroupFieldCharacter) o;
				int fieldEnd = field.getGroupLocation().getValue() + getGroupExtent(field) - 1;
				groupExtent = Math.max(groupExtent, fieldEnd);
			} else {
				// Must be FieldCharacter
				FieldCharacter field = (FieldCharacter) o;
				int fieldEnd = field.getFieldLocation().getValue() + field.getFieldLength().getValue() - 1;
				groupExtent = Math.max(groupExtent,  fieldEnd);
			}
		}

		return groupExtent;
	}

	@Override
	public int getRecordCount() {
		return table.getRecords();
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
		return table.getOffset().getValue();
	}

	@Override
	public int getRecordLength() {
		return table.getRecordCharacter().getRecordLength().getValue();
	}

}
