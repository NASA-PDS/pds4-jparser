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
