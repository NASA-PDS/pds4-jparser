// Copyright 2006-2018, by the California Institute of Technology.
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
package gov.nasa.pds.label.jaxb;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to hold context information from a PDS4 product label.
 * 
 * @author mcayanan
 *
 */
public class XMLLabelContext {
  /** Contains a mapping of namespace prefixes to URIs. */
  private PDSNamespacePrefixMapper namespaces;
  
  /** Contains the schema locations set in the SchemaLocation attribute. */
  private String schemaLocation;
  
  /** Contains the schematron references set in the label. */
  private List<String> xmlModels;
  
  /**
   * Constructor.
   */
  public XMLLabelContext() {
    this.namespaces = null;
    this.schemaLocation = null;
    xmlModels = new ArrayList<String>();
  }
  
  /**
   * 
   * @return get the namespaces.
   */
  public PDSNamespacePrefixMapper getNamespaces() {
    return this.namespaces;
  }
  
  /**
   * Sets the namespaces.
   * 
   * @param namespaces a mapping of namespace prefixes to URIs.
   */
  public void setNamespaces(PDSNamespacePrefixMapper namespaces) {
    this.namespaces = namespaces;
  }
  
  /**
   * 
   * @return get the SchemaLocation that was set in the label.
   */
  public String getSchemaLocation() {
    return this.schemaLocation;
  }
  
  /**
   * Sets the schemalocation.
   * 
   * @param location What was set in the SchemaLocation attribute.
   */
  public void setSchemaLocation(String location) {
    this.schemaLocation = location;
  }
  
  /**
   * @return Returns the values set in the xml-models processing instructions.
   */
  public List<String> getXmlModels() {
    return this.xmlModels;
  }
  
  /**
   * 
   * @return Returns string representations of the xml-models processing
   * instructions set in the label.
   */
  public String getXmlModelPIs() {
    String models = "\n";
    for (String xmlModel : this.xmlModels) {
      models += "<?xml-model " + xmlModel + "?>\n";
    }
    return models;
  }
  
  /**
   * Adds the xml-model value to the list already captured.
   * 
   * @param model The xml-model value.
   */
  public void addXmlModel(String model) {
    this.xmlModels.add(model);
  }
}
