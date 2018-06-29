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

import gov.nasa.arc.pds.xml.generated.FieldDelimited;
import gov.nasa.arc.pds.xml.generated.GroupFieldDelimited;
import gov.nasa.arc.pds.xml.generated.TableDelimited;
import gov.nasa.pds.label.object.FieldDescription;
import gov.nasa.pds.label.object.FieldType;

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
		if (field.getMaximumFieldLength() != null) {
		  desc.setMaxLength(field.getMaximumFieldLength().getValue().intValueExact());
		}
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
	
	private void expandGroupField(GroupFieldDelimited group) {
		for (int i=0; i < group.getRepetitions().intValueExact(); ++i) {
			// Have to copy the fields to a new array, because of element type.
			List<Object> fields = new ArrayList<Object>();
			for (Object field : group.getFieldDelimitedsAndGroupFieldDelimiteds()) {
				fields.add(field);
			}
			expandFields(fields);
		}
	}

	@Override
	public int getRecordCount() {
		return table.getRecords().intValueExact();
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
		return 0;
	}

	public char getFieldDelimiter() {
		return DelimiterType.getDelimiterType(table.getFieldDelimiter()).getFieldDelimiter();
	}
}
