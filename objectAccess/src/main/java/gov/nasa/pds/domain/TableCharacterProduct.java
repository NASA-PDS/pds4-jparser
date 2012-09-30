package gov.nasa.pds.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**  Contains information about a table product. */

public class TableCharacterProduct extends PDSObject implements Serializable, HasFields {

	private static final long serialVersionUID = -4216665648989080433L;

	// HasFields
	private List<FieldInfo> fields;
	
	public TableCharacterProduct(String archiveRoot, String thisRelativeFileName) {
		super(archiveRoot, thisRelativeFileName);
	}

	public TableCharacterProduct() {
		super();
	}
	
	@Override
	public List<FieldInfo> getFields() {
		if (fields == null) {
			fields = new ArrayList<FieldInfo>();
		}
		return fields;
	}
	
	@Override
	public void setFields(List<FieldInfo> fields) {
		this.fields = fields;
	}
}
