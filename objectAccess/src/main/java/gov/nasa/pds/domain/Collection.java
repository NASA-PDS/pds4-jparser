package gov.nasa.pds.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**  Contains information about an bundle (collection) product. */

public class Collection extends PDSObject implements Serializable, HasFields, HasParsableChildren {

	private static final long serialVersionUID = -9203239602835433967L;

	// HasFields
	private List<FieldInfo> fields;
	
	public Collection(String archiveRoot, String thisRelativeFileName) {
		super(archiveRoot, thisRelativeFileName);
	}
	
	public Collection() {
		super();
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public void setLabel(String label) {
		this.label = label;		
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
