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
		desc.setSpecialConstants(field.getSpecialConstants());
		if (field.getMaximumFieldLength() != null) {
		  desc.setMaxLength(field.getMaximumFieldLength().getValue().intValueExact());
		}
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
