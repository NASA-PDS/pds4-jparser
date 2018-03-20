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
    if(e.getEventType() == XMLStreamConstants.START_ELEMENT)
    {
      final StartElement startElement = e.asStartElement();
      String name = startElement.getName().getLocalPart();
      if (name.equalsIgnoreCase(root)) { 
        PDSNamespacePrefixMapper namespaces = null;
        try {
          namespaces = collectXmlns(startElement);
        } catch (IOException io) {
          throw new XMLStreamException("Error while trying to read namespace "
              + "properties file: " + io.getMessage());
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
    for (final Iterator i = e.getNamespaces();i.hasNext();) {
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
