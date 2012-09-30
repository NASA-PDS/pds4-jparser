package gov.nasa.pds.domain;

import java.util.Iterator;

/**
 * An object with this interface has file and object children.
 *
 */
public interface HasChildren {

	public Iterator<String> filenameIterator();

	public String getFirstFilename();

	public int getFilenamesCount();

	public void clearAllFilenames();

	public void addFilename(String filename);

	public Iterator<PDSObject> childrenIterator();
	
	public PDSObject getFirstChild();

	public int getChildCount();
	
	public void clearChildren();
	
	public void addChild(PDSObject child);

}