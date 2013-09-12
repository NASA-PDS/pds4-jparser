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
		fields.add(desc);
	}
	
	private void expandGroupField(GroupFieldCharacter group, int outerOffset) {
		int baseOffset = outerOffset + group.getGroupLocation().getValue() - 1;
		int groupLength = group.getGroupLength().getValue();
		
		for (int i=0; i < group.getRepetitions(); ++i) {
			expandFields(group.getFieldCharactersAndGroupFieldCharacters(), baseOffset);
			baseOffset += groupLength;
		}
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
