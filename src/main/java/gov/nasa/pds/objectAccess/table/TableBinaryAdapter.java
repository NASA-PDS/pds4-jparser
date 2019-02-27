// Copyright 2006-2019, by the California Institute of Technology.
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
		desc.setOffset(field.getFieldLocation().getValue().intValueExact() - 1 + baseOffset);
		desc.setLength(field.getFieldLength().getValue().intValueExact());
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
    if (desc.getType().equals(FieldType.SIGNEDBITSTRING) || 
        desc.getType().equals(FieldType.UNSIGNEDBITSTRING)) {
      desc.setStartBit(0);
      desc.setStopBit(desc.getLength() - 1);
    }
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
		desc.setOffset(field.getFieldLocation().getValue().intValueExact() - 1 + baseOffset);
		desc.setLength(field.getFieldLength().getValue().intValueExact());
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
		fields.add(desc);
	}

	private void expandGroupField(GroupFieldBinary group, int outerOffset) {
		int baseOffset = outerOffset + group.getGroupLocation().getValue().intValueExact() - 1;

		int groupLength = group.getGroupLength().getValue().intValueExact() / group.getRepetitions().intValueExact();

		// Check that the group length is large enough for the contained fields.
		int actualGroupLength = getGroupExtent(group);

		if (groupLength < actualGroupLength) {
			System.err.println("WARNING: GroupFieldBinary attribute group_length is smaller than size of contained fields: "
					+ (groupLength * group.getRepetitions().intValueExact())
					+ "<"
					+ (actualGroupLength * group.getRepetitions().intValueExact()));
			groupLength = actualGroupLength;
		}

		for (int i=0; i < group.getRepetitions().intValueExact(); ++i) {
			expandFields(group.getFieldBinariesAndGroupFieldBinaries(), baseOffset);
			baseOffset += groupLength;
		}
	}

	private int getGroupExtent(GroupFieldBinary group) {
		int groupExtent = 0;

		for (Object o : group.getFieldBinariesAndGroupFieldBinaries()) {
			if (o instanceof GroupFieldBinary) {
				GroupFieldBinary field = (GroupFieldBinary) o;
				int fieldEnd = field.getGroupLocation().getValue().intValueExact() + getGroupExtent(field) - 1;
				groupExtent = Math.max(groupExtent, fieldEnd);
			} else {
				// Must be FieldBinary
				FieldBinary field = (FieldBinary) o;
				int fieldEnd = field.getFieldLocation().getValue().intValueExact() + field.getFieldLength().getValue().intValueExact() - 1;
				groupExtent = Math.max(groupExtent,  fieldEnd);
			}
		}

		return groupExtent;
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
		return table.getRecordBinary().getRecordLength().getValue().intValueExact();
	}

}
