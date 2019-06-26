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
