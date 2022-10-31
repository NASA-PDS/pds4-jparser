package gov.nasa.pds.label.object;

import java.net.URL;

public class DataObjectLocation {
  /** The label associated with the data. */
  private URL label;

  /** The data file associated with the record. */
  private URL dataFile;

  /** The index of the fileArea associated with the data object. */
  private int fileArea;

  /** The index of the data object within the file area. */
  private int dataObject;

  /**
   * Constructor.
   *
   * @param label The label.
   * @param dataFile The data file.
   * @param fileArea The File_Area_* index.
   * @param dataObject The index of the data object within the file area
   */
  public DataObjectLocation(URL label, URL dataFile, int fileArea, int dataObject) {
    this.label = label;
    this.dataFile = dataFile;
    this.fileArea = fileArea;
    this.dataObject = dataObject;
  }

  /**
   * Constructor.
   *
   * @param fileArea The File_Area_* index.
   * @param dataObject The index of the data object within the file area
   */
  public DataObjectLocation(int fileArea, int dataObject) {
    this.label = null;
    this.dataFile = null;
    this.fileArea = fileArea;
    this.dataObject = dataObject;
  }

  public URL getLabel() {
    return label;
  }

  public void setLabel(URL label) {
    this.label = label;
  }

  public URL getDataFile() {
    return dataFile;
  }

  public void setDataFile(URL dataFile) {
    this.dataFile = dataFile;
  }

  public int getFileArea() {
    return fileArea;
  }

  public void setFileArea(int fileArea) {
    this.fileArea = fileArea;
  }

  public int getDataObject() {
    return dataObject;
  }

  public void setDataObject(int dataObject) {
    this.dataObject = dataObject;
  }
}
