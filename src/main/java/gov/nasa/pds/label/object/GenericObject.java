// Copyright 2006-2017, by the California Institute of Technology.
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
package gov.nasa.pds.label.object;

import java.io.File;
import java.io.IOException;
import java.net.URL;

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
   * @throws IOException if an error occurred initializng the object
   */
  public GenericObject(File parentDir, gov.nasa.arc.pds.xml.generated.File fileObject, long offset, long size) 
      throws IOException {
    this(parentDir.toURI().toURL(), fileObject, offset, size);
  }
  
	/**
	 * Creates a new instance.
	 *
	 * @param parentDir the parent directory of the data file
	 * @param fileObject the PDS4 file object for the data file
	 * @param offset the offset within the file of the start of the data object
	 * @param size the size of the data object, in bytes
   * @throws IOException if an error occurred initializng the object
	 */
	public GenericObject(URL parentDir, gov.nasa.arc.pds.xml.generated.File fileObject, long offset, long size) 
	    throws IOException {
		super(parentDir, fileObject, offset, size);
	}

}
