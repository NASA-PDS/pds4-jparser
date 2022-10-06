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
    xmlModels = new ArrayList<>();
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
   * @return Returns string representations of the xml-models processing instructions set in the
   *         label.
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
