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
import com.opencsv.exceptions.CsvValidationException;
import gov.nasa.arc.pds.xml.generated.ByteStream;
import gov.nasa.pds.objectAccess.ExporterFactory;
import gov.nasa.pds.objectAccess.RawTableReader;
import gov.nasa.pds.objectAccess.TableReader;


/**
 * Implements an object that represents a table in a PDS product. The table may be binary,
 * character, or delimited.
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
  public TableObject(File parentDir, gov.nasa.arc.pds.xml.generated.File fileObject,
      Object tableObject, long offset, long size, DataObjectLocation location) throws Exception {
    this(parentDir.toURI().toURL(), fileObject, tableObject, offset, size, location);
  }

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
  public TableObject(URL parentDir, gov.nasa.arc.pds.xml.generated.File fileObject,
      Object tableObject, long offset, long size, DataObjectLocation location) throws Exception {
    super(parentDir, fileObject, offset, size, location);
    this.tableObject = tableObject;
    this.tableReader = null;

    this.name = ((ByteStream) tableObject).getName();
    this.localIdentifier = ((ByteStream) tableObject).getLocalIdentifier();
  }

  /**
   * Deprecated initializer. Missing DataObjectLocation
   */
  @Deprecated
  public TableObject(File parentDir, gov.nasa.arc.pds.xml.generated.File fileObject,
      Object tableObject, long offset, long size) throws Exception {
    this(parentDir.toURI().toURL(), fileObject, tableObject, offset, size, null);
  }

  /**
   * Deprecated initializer. Missing DataObjectLocation
   */
  @Deprecated
  public TableObject(URL parentDir, gov.nasa.arc.pds.xml.generated.File fileObject,
      Object tableObject, long offset, long size) throws Exception {
    this(parentDir, fileObject, tableObject, offset, size, null);
  }

  /**
   * Returns a table reader for this table.
   *
   * @return a table reader
   * @throws Exception if there is an error creating the table reader
   */
  public TableReader getTableReader() throws Exception {
    return ExporterFactory.getTableReader(tableObject, getDataFile());
  }

  /**
   * Returns a raw table reader for this table.
   *
   * @return a table reader
   * @throws Exception if there is an error creating the table reader
   */
  public RawTableReader getRawTableReader() throws Exception {
    // TODO update this with table index
    return ExporterFactory.getRawTableReader(tableObject, getDataFile(), dataObjectLocation);
  }

  /**
   * Gets the field descriptions for fields in the table.
   *
   * @return an array of field descriptions
   * @throws Exception
   */
  public FieldDescription[] getFields() throws Exception {
    if (tableReader == null) {
      this.tableReader = getTableReader();
    }
    return tableReader.getFields();
  }

  /**
   * Reads the next record from the data file.
   *
   * @return the next record, or null if no further records.
   * @throws IOException if there is an error reading from the data file
   * @throws CsvValidationException
   */
  public TableRecord readNext() throws IOException, CsvValidationException, Exception {
    if (tableReader == null) {
      this.tableReader = getTableReader();
    }
    return tableReader.readNext();
  }

  /**
   * Gets access to the table record given the index. The current row is set to this index, thus,
   * subsequent call to readNext() gets the next record from this position.
   *
   * @param index the record index (1-relative)
   * @return an instance of <code>TableRecord</code>
   * @throws Exception
   */
  public TableRecord getRecord(int index, boolean keepQuotationsFlag) throws Exception {
    if (tableReader == null) {
      this.tableReader = getTableReader();
    }
    return tableReader.getRecord(index, keepQuotationsFlag);
  }

  public Object getTableObject() {
    return tableObject;
  }

  public void setTableObject(Object tableObject) {
    this.tableObject = tableObject;
  }
}
