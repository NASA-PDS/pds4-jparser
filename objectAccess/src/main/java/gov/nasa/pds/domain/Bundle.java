package gov.nasa.pds.domain;

import java.io.Serializable;

/**  Contains information about an bundle (collection) product. */

public class Bundle  extends PDSObject implements Serializable, HasParsableChildren  {

	private static final long serialVersionUID = -1001705188156088654L;

	public Bundle(String archiveRoot, String thisRelativeFileName) {
		super(archiveRoot, thisRelativeFileName);
	}
	
	public Bundle() {
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
}
