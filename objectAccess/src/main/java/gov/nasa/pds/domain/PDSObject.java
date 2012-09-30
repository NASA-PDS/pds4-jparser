package gov.nasa.pds.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**  Contains information about a generalized object. */

public abstract class PDSObject implements Serializable, Comparable<PDSObject>, HasChildren {

	private static final long serialVersionUID = 1L;
	private String title;
	private String logical_identifier;
	private String this_file_name; 
	private String archiveRoot;
	protected String label;
	protected TokenizedLabel tokenizedLabel;


	/** Collection of String filenames of children.*/
	private List<String> file_names = new ArrayList<String>();
	
	/** Collection of children of this. */
	protected List<PDSObject> children = new ArrayList<PDSObject>();
	
	
	public PDSObject(String archiveRoot, String thisRelativeFileName) {
		this.archiveRoot = archiveRoot;
		this.this_file_name = thisRelativeFileName;
	}

	public PDSObject() {
		super();
	}

	public String getThis_file_name() {
		return this_file_name;
	}
	
	@Override
	public int compareTo(PDSObject object) {
		return this_file_name.compareToIgnoreCase(object.getThis_file_name());
	}
	
	/**
     * Get the type ID string for the type of the PDS object. Type IDs are unique for each type.
     * @return the type ID
     */
    public String getTypeID() {
        return this.getClass().getName();
    }
    
	public String getLogical_identifier() {
		return logical_identifier;
	}
	
	public void setLogical_identifier(String logical_identifier) {
		this.logical_identifier = logical_identifier;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String t) {
		this.title = t;
	}

	public String getArchiveRoot() {
		return this.archiveRoot;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
	public void setTokenizedLabel(TokenizedLabel l) {
		this.tokenizedLabel = l;	
	}

	public TokenizedLabel getTokenizedLabel() {
		return tokenizedLabel;
	}
	
	// Has Children
	@Override
	public Iterator<String> filenameIterator() {
		return file_names.iterator();
	}
	
	@Override
	public String getFirstFilename() {
		return file_names.get(0);
	}
		
	@Override
	public int getFilenamesCount() {
		return file_names.size();
	}
	
	@Override
	public void clearAllFilenames() {
		file_names.clear();
	}
	
	@Override
	public void addFilename(String fn) {
		file_names.add(fn);
	}

	@Override
	public Iterator<PDSObject> childrenIterator() {
		return children.iterator();
	}
	
	@Override
	public PDSObject getFirstChild() {
		return children.get(0);
	}
	
	@Override
	public int getChildCount() {
		return children.size();
	}
	
	@Override
	public void clearChildren() {
		children.clear();
	}
	
	@Override
	public void addChild(PDSObject i) {
		children.add(i);		
	}
	
	// needed only for RPC
	public PDSObject[] childrenAsArray() {
		return children.toArray(new PDSObject[children.size()]);  
	}
}
