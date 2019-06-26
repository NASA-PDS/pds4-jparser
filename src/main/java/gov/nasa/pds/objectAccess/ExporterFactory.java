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

import java.io.File;
import java.net.URL;

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
    return get2DImageExporter(label.toURI().toURL(), fileAreaIndex);
  }
	
	/**
	 * Gets an instance of a Array2DImage exporter.
	 *
	 * @param label the PDS label file
	 * @param fileAreaIndex the file area inside the label containing the data to export
	 * @return an instance of a TwoDImageExporter
	 * @throws Exception
	 */
	public static TwoDImageExporter get2DImageExporter(URL label, int fileAreaIndex) throws Exception {
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
    return get3DImageExporter(label.toURI().toURL(), fileAreaIndex);
  }
	
	 /**
   * Gets an instance of an Array3DImage exporter.
   *
   * @param label the PDS label file.
   * @param fileAreaIndex the file area inside the label containing the data to export.
   * @return an instance of a ThreeDImageExporter.
   * @throws Exception
   */
	public static ThreeDImageExporter get3DImageExporter(URL label, int fileAreaIndex) throws Exception {
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
    return get3DSpectrumExporter(label.toURI().toURL(), fileAreaIndex);
  }
	  
  /**
   * Gets an instance of an Array3DSpectrum exporter.
   *
   * @param label the PDS label file.
   * @param fileAreaIndex the file area inside the label containing the data to export.
   * @return an instance of a ThreeDSpectrumExporter.
   * @throws Exception
   */	
  public static ThreeDSpectrumExporter get3DSpectrumExporter(URL label, int fileAreaIndex) throws Exception {
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
    return getTableExporter(label.toURI().toURL(), fileAreaIndex);
  }
  
	/**
	 * Gets an instance of a Table exporter.
	 *
	 * @param label the PDS label file
	 * @param fileAreaIndex the file area inside the label containing the data to export
	 * @return an instance of a TableExporter
	 * @throws Exception
	 */
	public static TableExporter getTableExporter(URL label, int fileAreaIndex) throws Exception {
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
    return getTableReader(tableObject, dataFile.toURI().toURL());
  }
	
	/**
	 * Gets a table reader object for a given table and data file.
	 *
	 * @param tableObject the table object, binary, character, or delimited
	 * @param dataFile the data file containing the table
	 * @return a table reader for the table
	 * @throws Exception if there is an error reading the file
	 */
	public static TableReader getTableReader(Object tableObject, URL dataFile) throws Exception {
		return new TableReader(tableObject, dataFile, true);
	}
}
