// Copyright 2006-2016, by the California Institute of Technology.
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

import java.io.File;

/**
 * Factory pattern class to create specific object exporters.
 *
 * @author dcberrio
 */
public class ExporterFactory {

	// Utility class - avoid instantiation.
	private ExporterFactory() {
		// never called
	}

	/**
	 * Gets an instance of a Array2DImage exporter.
	 *
	 * @param label the PDS label file
	 * @param fileAreaIndex the file area inside the label containing the data to export
	 * @return an instance of a TwoDImageExporter
	 * @throws Exception
	 */
	public static TwoDImageExporter get2DImageExporter(File label, int fileAreaIndex) throws Exception {
		return new TwoDImageExporter(label, fileAreaIndex);
	}

	 /**
   * Gets an instance of an Array3DImage exporter.
   *
   * @param label the PDS label file.
   * @param fileAreaIndex the file area inside the label containing the data to export.
   * @return an instance of a ThreeDImageExporter.
   * @throws Exception
   */
	public static ThreeDImageExporter get3DImageExporter(File label, int fileAreaIndex) throws Exception {
	  return new ThreeDImageExporter(label, fileAreaIndex);
	}
	  
  /**
   * Gets an instance of an Array3DSpectrum exporter.
   *
   * @param label the PDS label file.
   * @param fileAreaIndex the file area inside the label containing the data to export.
   * @return an instance of a ThreeDSpectrumExporter.
   * @throws Exception
   */	
  public static ThreeDSpectrumExporter get3DSpectrumExporter(File label, int fileAreaIndex) throws Exception {
    return new ThreeDSpectrumExporter(label, fileAreaIndex);
  }
	
	/**
	 * Gets an instance of a Table exporter.
	 *
	 * @param label the PDS label file
	 * @param fileAreaIndex the file area inside the label containing the data to export
	 * @return an instance of a TableExporter
	 * @throws Exception
	 */
	public static TableExporter getTableExporter(File label, int fileAreaIndex) throws Exception {
		return new TableExporter(label, fileAreaIndex);
	}

	/**
	 * Gets an instance of a Array2DImage exporter.
	 *
	 * @param fileArea the file area object containing the data to export
	 * @param provider the object provider pointing to the PDS4 label
	 * @return an instance of a TwoDImageExporter
	 * @throws Exception
	 */
	public static TwoDImageExporter get2DImageExporter(FileAreaObservational fileArea,
			ObjectProvider provider)  throws Exception {
			return new TwoDImageExporter(fileArea, provider);
	}

	/**
   * Gets an instance of an Array3DImage exporter.
   *
   * @param fileArea the file area object containing the data to export
   * @param provider the object provider pointing to the PDS4 label
   * @return an instance of a ThreeDImageExporter.
   * @throws Exception
   */
	public static ThreeDImageExporter get3DImageExporter(FileAreaObservational fileArea,
	    ObjectProvider provider) throws Exception {
	  return new ThreeDImageExporter(fileArea, provider);
	}

  /**
   * Gets an instance of an Array3DSpectrum exporter.
   *
   * @param fileArea the file area object containing the data to export
   * @param provider the object provider pointing to the PDS4 label
   * @return an instance of a ThreeDSpectrumExporter.
   * @throws Exception
   */	
	public static ThreeDSpectrumExporter get3DSpectrumExporter(FileAreaObservational fileArea,
	    ObjectProvider provider) throws Exception {
	  return new ThreeDSpectrumExporter(fileArea, provider);
	}
	
	/**
	 * Gets an instance of a Table exporter.
	 *
	 * @param fileArea the file area object containing the data to export
	 * @param provider the object provider pointing to the PDS4 label
	 * @return an instance of a TableExporter
	 * @throws Exception
	 */
	public static TableExporter getTableExporter(FileAreaObservational fileArea,
			ObjectProvider provider)  throws Exception {
			return new TableExporter(fileArea, provider);
	}

	/**
	 * Gets a table reader object for a given table and data file.
	 *
	 * @param tableObject the table object, binary, character, or delimited
	 * @param dataFile the data file containing the table
	 * @return a table reader for the table
	 * @throws Exception if there is an error reading the file
	 */
	public static TableReader getTableReader(Object tableObject, File dataFile) throws Exception {
		return new TableReader(tableObject, dataFile);
	}

}
