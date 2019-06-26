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
