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
  
  /** The index of the table associated with the record. */
  private int table;
  
  /** The index of the record. */
  private int record;
  
  /**
   * Constructor.
   * 
   * @param label The label.
   * @param dataFile The data file.
   * @param table The table index.
   * @param record The record index.
   */
  public RecordLocation(URL label, URL dataFile, int table, int record) {
    this.label = label;
    this.dataFile = dataFile;
    this.table = table;
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
   * @return the table index.
   */
  public int getTable() {
    return this.table;
  }
    
  /**
   * 
   * @return the record index.
   */
  public int getRecord() {
    return this.record;
  }
}
