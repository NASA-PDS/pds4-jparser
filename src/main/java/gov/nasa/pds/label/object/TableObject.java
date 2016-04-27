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
package gov.nasa.pds.label.object;

import gov.nasa.pds.objectAccess.ExporterFactory;
import gov.nasa.pds.objectAccess.TableReader;

import java.io.File;
import java.io.IOException;


/**
 * Implements an object that represents a table in a PDS
 * product. The table may be binary, character, or delimited.
 */
public class TableObject extends DataObject {

	private Object tableObject;
	private TableReader tableReader;

	/**
	 * Creates a new instance of the table object.
	 *
	 * @param parentDir the parent directory for the table object
	 * @param fileObject the file object describing the data file
	 * @param tableObject the table object describing the table
	 * @param offset the offset of the table object within the data file
	 * @param size the size of the table object, in bytes
	 * @throws Exception if there is any error accessing the table
	 */
	public TableObject(
			File parentDir,
			gov.nasa.arc.pds.xml.generated.File fileObject,
			Object tableObject,
			long offset,
			long size
	) throws Exception {
		super(parentDir, fileObject, offset, size);
		this.tableObject = tableObject;
		this.tableReader = getTableReader();
	}

	/**
	 * Returns a table reader for this table.
	 *
	 * @return a table reader
	 * @throws Exception if there is an error creating the table reader
	 */
	private TableReader getTableReader() throws Exception {
		return ExporterFactory.getTableReader(tableObject, getDataFile());
	}

	/**
	 * Gets the field descriptions for fields in the table.
	 *
	 * @return an array of field descriptions
	 */
	public FieldDescription[] getFields() {
		return tableReader.getFields();
	}

	/**
	 * Reads the next record from the data file.
	 *
	 * @return the next record, or null if no further records.
	 * @throws IOException if there is an error reading from the data file
	 */
	public TableRecord readNext() throws IOException {
		return tableReader.readNext();
	}

	/**
	 * Gets access to the table record given the index. The current row is set to
	 * this index, thus, subsequent call to readNext() gets the next record from
	 * this position.
	 *
	 * @param index the record index (1-relative)
	 * @return an instance of <code>TableRecord</code>
	 * @throws IllegalArgumentException if index is greater than the record number
	 * @throws IOException if there is an error reading from the data file
	 */
	public TableRecord getRecord(int index) throws IllegalArgumentException, IOException {
		return tableReader.getRecord(index);
	}

}
