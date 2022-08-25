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

package gov.nasa.pds.objectAccess.table;

import java.math.BigInteger;
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
    long getRecordCount();
	
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
