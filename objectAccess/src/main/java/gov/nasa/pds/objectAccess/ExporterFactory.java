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
