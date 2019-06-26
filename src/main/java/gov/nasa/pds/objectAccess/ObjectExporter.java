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

package gov.nasa.pds.objectAccess;

import gov.nasa.arc.pds.xml.generated.FileAreaObservational;
import gov.nasa.arc.pds.xml.generated.ProductObservational;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Super class of all object type exporters.
 *
 * @author dcberrio
 */
public abstract class ObjectExporter {

	Logger logger = LoggerFactory.getLogger(ObjectExporter.class);
	private ObjectProvider objectProvider;
	private FileAreaObservational fileArea;
	
	public ObjectExporter() {
	  this.objectProvider = null;
	  this.fileArea = null;
	}

  /**
   * Super constructor.  Parses the input label file, reporting errors appropriately.
   * @param label the label file
   * @param fileAreaIndex the index of the observational file area to be
   * used by this exporter
   * @throws Exception
   */
  public ObjectExporter(File label, int fileAreaIndex) throws Exception {
    this(label.toURI().toURL(), fileAreaIndex);
  }	
	
	/**
	 * Super constructor.  Parses the input label file, reporting errors appropriately.
	 * @param label the label file
	 * @param fileAreaIndex the index of the observational file area to be
	 * used by this exporter
	 * @throws Exception
	 */
	public ObjectExporter(URL label, int fileAreaIndex) throws Exception {
		parseLabel(label, fileAreaIndex);
	}

	/**
	 * Super constructor.
	 *
	 * @param fileArea the observational file area to be used by this exporter
	 * @param provider the objectProvider that points to the location of the data to export
	 * @throws IOException
	 */
	public ObjectExporter(FileAreaObservational fileArea, ObjectProvider provider) throws IOException {
		this.objectProvider = provider;
		this.fileArea = fileArea;
	}



	protected void parseLabel(File label, int fileAreaIndex) throws Exception {
	  parseLabel(label.toURI().toURL(), fileAreaIndex);
	}
	
	protected void parseLabel(URL label, int fileAreaIndex) throws Exception {
	  boolean canRead = true;
	  try {
	    label.openStream().close();
	  } catch (IOException io) {
	    canRead = false;
	  }
		if (canRead) {
      URI labelUri = label.toURI().normalize();
      URL parentUrl = labelUri.getPath().endsWith("/") ?
          labelUri.resolve("..").toURL() :
            labelUri.resolve(".").toURL();
			this.objectProvider = new ObjectAccess(parentUrl);
			ProductObservational p = objectProvider.getProduct(label, ProductObservational.class);
			try {
				fileArea = p.getFileAreaObservationals().get(fileAreaIndex);
			} catch (IndexOutOfBoundsException e) {
				String message = "Label has no such ObservationalFileArea";
				logger.error(message);
				throw new Exception(message);
			}
		} else {
			String message = "Input file does not exist: " + label.toString();
			logger.error(message);
			throw new IOException(message);
		}
	}


	/**
	 * Sets the objectProvider associated with this exporter.
	 *
	 * @param provider the objectProvider associated with this exporter
	 */
	public void setObjectProvider(ObjectProvider provider) {
		this.objectProvider = provider;
	}

	/**
	 * Gets the objectProvider associated with this exporter.
	 *
	 * @return objectProvider the objectProvider associated with this exporter
	 */
	public ObjectProvider getObjectProvider() {
		return objectProvider;
	}


	/**
	 * Set the observational file area containing the data to be exported.
	 * @param fileAreaObs the observational file area containing the data to be exported
	 */
	public void setObservationalFileArea(FileAreaObservational fileAreaObs) {
		this.fileArea = fileAreaObs;
	}

	/**
	 * Gets the observational file area containing the data to be exported.
	 *
	 * @return fileArea the observational file area containing the data to be exported
	 */
	public FileAreaObservational getObservationalFileArea() {
		return fileArea;
	}
}
