package gov.nasa.pds.objectAccess.table;

import gov.nasa.arc.pds.xml.generated.FieldBinary;
import gov.nasa.arc.pds.xml.generated.FieldBit;
import gov.nasa.arc.pds.xml.generated.GroupFieldBinary;
import gov.nasa.arc.pds.xml.generated.TableBinary;
import gov.nasa.pds.label.object.FieldDescription;
import gov.nasa.pds.label.object.FieldType;

import java.util.ArrayList;
import java.util.List;

public class TableBinaryAdapter implements TableAdapter {

	TableBinary table;
	List<FieldDescription> fields;

	/**
	 * Creates a new instance for a particular table.
	 *
	 * @param table the table
	 */
	public TableBinaryAdapter(TableBinary table) {
		this.table = table;

		fields = new ArrayList<FieldDescription>();
		expandFields(table.getRecordBinary().getFieldBinariesAndGroupFieldBinaries(), 0);
	}

	private void expandFields(List<Object> fields, int baseOffset) {
		for (Object field : fields) {
			if (field instanceof FieldBinary) {
				expandField((FieldBinary) field, baseOffset);
			} else {
				// Must be GroupFieldBinary
				expandGroupField((GroupFieldBinary) field, baseOffset);
			}
		}
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
		desc.setOffset(field.getFieldLocation().getValue() - 1 + baseOffset);
		desc.setLength(field.getFieldLength().getValue());
		fields.add(desc);
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
		desc.setOffset(field.getFieldLocation().getValue() - 1 + baseOffset);
		desc.setLength(field.getFieldLength().getValue());
		desc.setStartBit(bitField.getStartBit() - 1);
		desc.setStopBit(bitField.getStopBit() - 1);
		fields.add(desc);
	}

	private void expandGroupField(GroupFieldBinary group, int outerOffset) {
		int baseOffset = outerOffset + group.getGroupLocation().getValue() - 1;

		// Unfortunately, group_length is not used consistently by current labels,
		// so we cannot rely on it. Instead, find the size of each repetition
		// by adding up the field lengths in the group, and warn the user if
		// the values differ. PDS-339.
		int definedGroupLength = group.getGroupLength().getValue();

		int actualGroupLength = getGroupExtent(group);

		int groupLength = definedGroupLength;
		if (definedGroupLength != actualGroupLength) {
			System.err.println("WARNING: GroupFieldBinary attribute group_length does not match extent of contained fields: "
					+ definedGroupLength + "!=" + actualGroupLength);
			groupLength = actualGroupLength;
		}

		for (int i=0; i < group.getRepetitions(); ++i) {
			expandFields(group.getFieldBinariesAndGroupFieldBinaries(), baseOffset);
			baseOffset += groupLength;
		}
	}

	private int getGroupExtent(GroupFieldBinary group) {
		int groupExtent = 0;

		for (Object o : group.getFieldBinariesAndGroupFieldBinaries()) {
			if (o instanceof GroupFieldBinary) {
				GroupFieldBinary field = (GroupFieldBinary) o;
				int fieldEnd = field.getGroupLocation().getValue() + getGroupExtent(field) - 1;
				groupExtent = Math.max(groupExtent, fieldEnd);
			} else {
				// Must be FieldBinary
				FieldBinary field = (FieldBinary) o;
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
		return table.getRecordBinary().getRecordLength().getValue();
	}

}
