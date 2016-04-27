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
package gov.nasa.pds.objectAccess.table;

import gov.nasa.pds.label.object.FieldDescription;

/**
 * Defines a facade object that gives access to a table,
 * either binary fixed, character fixed, or delimited.
 */
public interface TableAdapter {

	/**
	 * Gets the number of records in the table.
	 * 
	 * @return the number of records
	 */
	int getRecordCount();
	
	/**
	 * Gets the number of fields in each record.
	 * 
	 * @return the number of fields
	 */
	int getFieldCount();

	/**
	 * Returns the field at a given index. This field will
	 * be a simple field or a bit field. All grouped fields
	 * will have been expanded to their instances.
	 * 
	 * @param index the field index
	 * @return the field description
	 */
	FieldDescription getField(int index);
	
	/**
	 * Gets the definitions of fields from the table. The fields
	 * will be a simple field or a bit field. All grouped fiels
	 * will have been expanded to their instances.
	 * 
	 * @return an array of field descriptions
	 */
	FieldDescription[] getFields();
	
	/**
	 * Gets the offset into the data file where the table starts.
	 * 
	 * @return the table offset
	 */
	long getOffset();
	
	/**
	 * Gets the length of each record. For delimited tables
	 * the record length is not defined, so zero is returned.
	 * 
	 * @return the record length, or zero for a delimited table
	 */
	int getRecordLength();
	
}
