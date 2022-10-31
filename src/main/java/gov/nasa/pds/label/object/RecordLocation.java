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

import java.net.URL;

/**
 * Class that holds table record location information.
 *
 * @author mcayanan
 *
 */
public final class RecordLocation {

  /** The label associated with the data. */
  private URL label;

  /** The data file associated with the record. */
  private URL dataFile;

  /** The location of table associated with the record. */
  private DataObjectLocation dataObjectLocation;

  /** The index of the record. */
  private long record;

  /**
   * Constructor.
   *
   * @param label The label.
   * @param dataFile The data file.
   * @param table The table index.
   * @param record The record index as integer.
   */
  public RecordLocation(URL label, URL dataFile, DataObjectLocation dataObjectLocation,
      int record) {
    this(label, dataFile, dataObjectLocation, (long) record);
  }

  /**
   * Constructor.
   *
   * @param label The label.
   * @param dataFile The data file.
   * @param table The table index.
   * @param record The record index as a long.
   */
  public RecordLocation(URL label, URL dataFile, DataObjectLocation dataObjectLocation,
      long record) {
    this.label = label;
    this.dataFile = dataFile;
    this.dataObjectLocation = dataObjectLocation;
    this.record = record;
  }

  /**
   *
   * @return the label.
   */
  public URL getLabel() {
    return this.label;
  }

  /**
   *
   * @return the data file.
   */
  public URL getDataFile() {
    return this.dataFile;
  }

  /**
   *
   * @return the data object location.
   */
  public DataObjectLocation getDataObjectLocation() {
    return this.dataObjectLocation;
  }

  /**
   *
   * @return the record index.
   */
  public long getRecord() {
    return this.record;
  }
}
