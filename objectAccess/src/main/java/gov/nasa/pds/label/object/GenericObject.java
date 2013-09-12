package gov.nasa.pds.label.object;

import java.io.File;

/**
 * Implements a generic interface to a data object, for objects
 * that are not otherwise specially handled.
 */
public class GenericObject extends DataObject {

	/**
	 * Creates a new instance.
	 *
	 * @param parentDir the parent directory of the data file
	 * @param fileObject the PDS4 file object for the data file
	 * @param offset the offset within the file of the start of the data object
	 * @param size the size of the data object, in bytes
	 */
	public GenericObject(File parentDir, gov.nasa.arc.pds.xml.generated.File fileObject, long offset, long size) {
		super(parentDir, fileObject, offset, size);
	}

}
