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
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;


/**
 * Class to hold namespace information set in a product label.
 * 
 * @author mcayanan
 *
 */
public class PDSNamespacePrefixMapper extends NamespacePrefixMapper {
  public final static String FILE = "namespaces.properties";
  
  /** The default namespace set in the label. */
  private String defaultNamespaceURI;
  
  /**
   * A mapping of namepsace prefixes to URIs.
   */
  private Map<String, String> namespaceURIMapping = new LinkedHashMap<String, String>();

  /**
   * Constructor.
   * 
   * @throws IOException If there was an error loading the default namepsaces.
   */
  public PDSNamespacePrefixMapper() throws IOException {
    Properties properties = parseProperties();
    for (Object key : properties.keySet()) {
      namespaceURIMapping.put(key.toString(), properties.get(key).toString());
    }
    
  }
  
  /**
   * Loads the default namespaces into the map.
   * 
   * @return An object representation of the default namespaces.
   * 
   * @throws IOException If there was an error reading in the properties file.
   */
  private Properties parseProperties() throws IOException {
    URL propertyFile = PDSNamespacePrefixMapper.class.getResource(FILE);
    if (propertyFile == null) {
      throw new IOException(FILE + " could not be found.");
    }
    InputStream inputStream = null;
    Properties properties = new Properties();
    try {
      inputStream = propertyFile.openStream();
      properties.load(inputStream);
   } finally {
     IOUtils.closeQuietly(inputStream);
   }
   return properties;
 }
  
  /**
   * Sets the default namespace uri.
   * 
   * @param defaultNamespaceURI namespace uri.
   */
  public void setDefaultNamespaceURI(String defaultNamespaceURI) {
      this.defaultNamespaceURI = defaultNamespaceURI;
  }
  
  /**
   * @return Gets the default namespace uri.
   */
  public String getDefaultNamespaceURI(){
      return defaultNamespaceURI;
  }
  
  /**
   * Adds a namespace to the map.
   * 
   * @param prefix The namespace prefix.
   * @param URI The namespace uri.
   */
  public void addNamespaceURIMapping(String prefix, String URI) {
      namespaceURIMapping.put(prefix, URI);
  }
  
  @Override
  public String getPreferredPrefix(String namespaceUri, String suggestion,
      boolean requirePrefix) {
    if (namespaceUri.equalsIgnoreCase(defaultNamespaceURI)) {
      return "";
    } else {
      for (Map.Entry<String, String> entry : namespaceURIMapping.entrySet()) {
        if (entry.getValue().equals(namespaceUri)) {
          return entry.getKey();
        }
      }
      return null;
    }
  }

}
