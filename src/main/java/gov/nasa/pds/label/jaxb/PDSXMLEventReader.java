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

import java.io.IOException;
import java.util.Iterator;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.EventReaderDelegate;

/**
 * Event reader when parsing a PDS4 Product Label.
 *
 * @author mcayanan
 *
 */
public class PDSXMLEventReader extends EventReaderDelegate {
  /** To hold context information. */
  private XMLLabelContext labelContext;

  /** The name of the root of the label. */
  private String root;

  /**
   * Constructor.
   *
   * @param xsr An XMLEventReader object.
   * @param root The name of the root element of the label.
   */
  public PDSXMLEventReader(XMLEventReader xsr, String root) {
    super(xsr);
    this.root = root;
    labelContext = new XMLLabelContext();
  }

  @Override
  public XMLEvent nextEvent() throws XMLStreamException {
    final XMLEvent e = super.nextEvent();
    if (e.getEventType() == XMLStreamConstants.START_ELEMENT) {
      final StartElement startElement = e.asStartElement();
      String name = startElement.getName().getLocalPart();
      if (name.equalsIgnoreCase(root)) {
        PDSNamespacePrefixMapper namespaces = null;
        try {
          namespaces = collectXmlns(startElement);
        } catch (IOException io) {
          throw new XMLStreamException(
              "Error while trying to read namespace " + "properties file: " + io.getMessage());
        }
        if (namespaces != null) {
          labelContext.setNamespaces(namespaces);
        }
        Attribute attr = startElement.getAttributeByName(
            new QName(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "schemaLocation"));
        if (attr != null) {
          String value = attr.getValue().trim();
          value = value.replaceAll("\\s+", "  ");
          labelContext.setSchemaLocation(value);
        }
      }
    } else if (e.getEventType() == XMLStreamConstants.PROCESSING_INSTRUCTION) {
      final ProcessingInstruction pi = (ProcessingInstruction) e;
      if ("xml-model".equalsIgnoreCase(pi.getTarget())) {
        if (pi.getData() != null) {
          labelContext.addXmlModel(pi.getData());
        }
      }
    }
    return e;
  }

  /**
   * Gather the namespaces.
   *
   * @param e The element with the namespaces in it.
   *
   * @return A mapping of the namespace prefixes to URIs.
   *
   * @throws IOException If an error occurred while reading the namepsaces.
   */
  private PDSNamespacePrefixMapper collectXmlns(StartElement e) throws IOException {
    final PDSNamespacePrefixMapper namespaces = new PDSNamespacePrefixMapper();
    final NamespaceContext nsCtx = e.getNamespaceContext();
    for (final Iterator i = e.getNamespaces(); i.hasNext();) {
      final Namespace ns = (Namespace) i.next();
      final String uri = ns.getNamespaceURI();
      final String prefix = ns.getPrefix();
      if (prefix.isEmpty()) {
        namespaces.setDefaultNamespaceURI(ns.getValue());
      }
      final String value = ns.getValue();

      namespaces.addNamespaceURIMapping(prefix, value);
    }

    return namespaces;
  }

  /**
   *
   * @return Returns the label context.
   */
  public XMLLabelContext getLabelContext() {
    return this.labelContext;
  }
}
