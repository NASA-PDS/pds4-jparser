package gov.nasa.pds.objectAccess.table;

import gov.nasa.arc.pds.xml.generated.FieldDelimited;
import gov.nasa.arc.pds.xml.generated.GroupFieldDelimited;
import gov.nasa.arc.pds.xml.generated.TableDelimited;

import java.util.ArrayList;
import java.util.List;

public class TableDelimitedAdapter implements TableAdapter {

	TableDelimited table;
	List<FieldDescription> fields;
	
	/**
	 * Creates a new instance for a particular table.
	 * 
	 * @param table the table
	 */
	public TableDelimitedAdapter(TableDelimited table) {
		this.table = table;

		fields = new ArrayList<FieldDescription>();
		expandFields(table.getRecordDelimited().getFieldDelimitedsAndGroupFieldDelimiteds());
	}
	
	private void expandFields(List<Object> fields) {
		for (Object field : fields) {
			if (field instanceof FieldDelimited) {
				expandField((FieldDelimited) field);
			} else {
				// Must be GroupFieldDelimited
				expandGroupField((GroupFieldDelimited) field);
			}
		}
	}
	
	private void expandField(FieldDelimited field) {
		FieldDescription desc = new FieldDescription();
		desc.setName(field.getName());
		desc.setType(FieldType.getFieldType(field.getDataType()));
		fields.add(desc);
	}
	
	private void expandGroupField(GroupFieldDelimited group) {
		for (int i=0; i < group.getRepetitions(); ++i) {
			// Have to copy the fields to a new array, because of element type.
			List<Object> fields = new ArrayList<Object>();
			for (FieldDelimited field : group.getFieldDelimiteds()) {
				fields.add(field);
			}
			expandFields(fields);
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
		return 0;
	}

}
