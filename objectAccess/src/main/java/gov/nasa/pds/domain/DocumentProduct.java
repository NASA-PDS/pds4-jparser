package gov.nasa.pds.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**  Contains information about an document product. */

public class DocumentProduct extends PDSObject implements  Serializable, HasURL {

	private static final long serialVersionUID = -5415501504343973632L;
	private String url;
	private final transient List<String> visualElements = new ArrayList<String>();
	private String documentType;
	private String documentLocalIdentifier;
	private String creation_date_time;

	public DocumentProduct(String archiveRoot, String thisRelativeFileName) {
		super(archiveRoot, thisRelativeFileName);
	}
	
	public DocumentProduct() {
		super();
	}

	@Override
	public String getUrl() {
		return url;
	}

	@Override
	public void setUrl(String url) {
		this.url = url;
	}
	
	public List<String> getVisualElements() {
		return visualElements;
	}
	
	public boolean hasVisualElements() {
		return visualElements.size() > 0;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public String getDocumentLocalIdentifier() {
		return documentLocalIdentifier;
	}

	public void setDocumentLocalIdentifier(String documentLocalIdentifier) {
		this.documentLocalIdentifier = documentLocalIdentifier;
	}

	public String getCreation_date_time() {
		return creation_date_time;
	}

	public void setCreation_date_time(String creation_date_time) {
		this.creation_date_time = creation_date_time;
	}
}
