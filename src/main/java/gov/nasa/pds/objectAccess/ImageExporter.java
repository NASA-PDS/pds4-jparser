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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import gov.nasa.arc.pds.xml.generated.DisciplineArea;
import gov.nasa.arc.pds.xml.generated.DisplaySettings;
import gov.nasa.arc.pds.xml.generated.FileAreaObservational;
import gov.nasa.arc.pds.xml.generated.ProductObservational;

/**
 * Super class for all image exporter types.
 * 
 * @author mcayanan
 *
 */
public abstract class ImageExporter extends ObjectExporter {
  private List<DisplaySettings> displaySettings;
  
  /**
   * Constructor.
   * 
   * @param label label file.
   * @param fileAreaIndex The index of the File_Area_Observational element
   *  that contains the image to export.
   * 
   * @throws Exception If an error occurred parsing the label.
   */
  public ImageExporter(File label, int fileAreaIndex) throws Exception {
    super(label, fileAreaIndex);
    if (this.displaySettings == null) {
      this.displaySettings = new ArrayList<DisplaySettings>();
    }
  }
  
  /**
   * Constructor.
   * 
   * @param fileArea The File_Area_Observational element containing the
   *  image to export.
   * @param provider The ObjectProvider associated with the image to export.
   * 
   * @throws IOException
   */
  public ImageExporter(FileAreaObservational fileArea, 
      ObjectProvider provider) throws IOException {
    super(fileArea, provider);
    this.displaySettings = new ArrayList<DisplaySettings>();
  }
  
  /**
   * Parse the label.
   * 
   * @param label The label file.
   * @param fileAreaIndex The index of the File_Area_Observational element
   *  that contains the image to export.
   *  
   * @throws Exception If an error occurred while parsing the label.
   */
  protected void parseLabel(File label, int fileAreaIndex) throws Exception {
    if (label.canRead()) {
      setObjectProvider(new ObjectAccess(new File(label.getParent())));
      ProductObservational p = getObjectProvider().getProduct(label, 
          ProductObservational.class);
      DisciplineArea disciplineArea = null;
      try {
        disciplineArea = p.getObservationArea().getDisciplineArea();
        if (disciplineArea != null) {
          for (Object object : disciplineArea.getAnies()) {
            if (object instanceof DisplaySettings) {
              this.displaySettings.add((DisplaySettings) object);
            }
          }
        }
      } catch (IndexOutOfBoundsException e) {
        String message = "Label has no such ObservationalArea";
        logger.error(message);
        throw new Exception(message);
      }
      try {
        setObservationalFileArea(p.getFileAreaObservationals()
            .get(fileAreaIndex));
      } catch (IndexOutOfBoundsException e) {
        String message = "Label has no such ObservationalFileArea";
        logger.error(message);
        throw new Exception(message);
      }
    } else {
      String message = "Input file does not exist: " + label.getAbsolutePath();
      logger.error(message);
      throw new IOException(message);
    }
  }
  
  /**
   * Set the display settings.
   * 
   * @param displaySettings A list of DisplaySettings.
   */
  public void setDisplaySettings(List<DisplaySettings> displaySettings) {
    this.displaySettings = displaySettings;
  }
  
  /**
   * Get the display settings associated with the given identifier.
   * 
   * @param id The identifier to search.
   * 
   * @return The display settings associated with the given identifier.
   *   Returns null if none was found.
   */
  public DisplaySettings getDisplaySettings(String id) {
    for(DisplaySettings ds : displaySettings) {
      if (id.equals(ds.getLocalInternalReference()
          .getLocalIdentifierReference())) {
        return ds;
      }
    }
    return null;
  }
}
