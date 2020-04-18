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

package gov.nasa.pds.objectAccess;

import gov.nasa.arc.pds.xml.generated.Array;
import gov.nasa.arc.pds.xml.generated.Array2DImage;
import gov.nasa.arc.pds.xml.generated.Array3DImage;
import gov.nasa.arc.pds.xml.generated.Array3DSpectrum;
import gov.nasa.arc.pds.xml.generated.FieldBinary;
import gov.nasa.arc.pds.xml.generated.FieldCharacter;
import gov.nasa.arc.pds.xml.generated.FieldDelimited;
import gov.nasa.arc.pds.xml.generated.FileArea;
import gov.nasa.arc.pds.xml.generated.FileAreaAncillary;
import gov.nasa.arc.pds.xml.generated.FileAreaBrowse;
import gov.nasa.arc.pds.xml.generated.FileAreaObservational;
import gov.nasa.arc.pds.xml.generated.FileAreaObservationalSupplemental;
import gov.nasa.arc.pds.xml.generated.GroupFieldDelimited;
import gov.nasa.arc.pds.xml.generated.ProductObservational;
import gov.nasa.arc.pds.xml.generated.TableBinary;
import gov.nasa.arc.pds.xml.generated.TableCharacter;
import gov.nasa.arc.pds.xml.generated.TableDelimited;
import gov.nasa.pds.label.jaxb.XMLLabelContext;

import java.io.File;
import java.net.URL;
import java.util.List;

/**
 * Provides access to PDS4 objects.
 *
 */
public interface ObjectProvider {

	/**
	 * Gets the root file path of the object archive(s) for this ObjectProvider.
	 *
	 * @return the root file path of the object archive(s) for this ObjectProvider
	 */
	String getArchiveRoot();

	/**
	 * Gets the root file path of the object archive(s) for this ObjectProvider.
	 *
	 * @return the root file path of the object archive(s) for this ObjectProvider
	 */
	URL getRoot();

	 /**
   * Gets a list of Array objects from a file area.
   *
   * @param fileArea the file area.
   * @return an list of arrays, which may be empty.
   */
	List<Array> getArrays(FileArea fileArea);
	
	/**
	 * Gets a list of Array objects from an observational file area.
	 *
	 * @param fileArea the observational file area.
	 * @return an list of arrays, which may be empty.
	 */
	List<Array> getArrays(FileAreaObservational fileArea);

	/**
   * Gets a list of Array objects from a browse file area.
   *
   * @param fileArea the browse file area
   * @return an list of arrays, which may be empty
   */
	List<Array> getArrays(FileAreaBrowse fileArea);
	
	/**
	 *  Returns a list of Array2DImage objects given an observation file area object.
	 *
	 * @param observationalFileArea
	 * @return a list of image objects
	 */
	List<Array2DImage> getArray2DImages(FileAreaObservational observationalFileArea);

	 /**
   *  Returns a list of Array3DImage objects given an observation file area object.
   *
   * @param observationalFileArea
   * @return a list of image objects
   */
  List<Array3DImage> getArray3DImages(FileAreaObservational observationalFileArea);

  /**
  *  Returns a list of Array3DSpectrum objects given an observation file area object.
  *
  * @param observationalFileArea
  * @return a list of image objects
  */
  List<Array3DSpectrum> getArray3DSpectrums(FileAreaObservational observationalFileArea);
  
	/**
	 * Returns a list of table objects.
	 *
	 * @param observationalFileArea
	 * @return a list of table objects
	 */
	public List<Object> getTableObjects(FileAreaObservational observationalFileArea);

	public List<Object> getTableObjects(FileAreaBrowse browseFileArea);
	
	public List<Object> getTableObjects(FileAreaAncillary anciilaryFileArea);
	
	public List<Object> getTableObjects(FileArea fileArea);

	public List<Object> getTablesAndImages(FileAreaObservational observationalFileArea);

	public List<Object> getTablesAndImages(FileAreaBrowse browseFileArea);
	  
	public List<Object> getTablesAndImages(FileArea fileArea);
	
	/**
	 * Returns a list of TableCharacter objects given an observation file area object.
	 *
	 * @param observationalFileArea
	 * @return list of TableCharacter objects
	 */
	List<TableCharacter> getTableCharacters(FileAreaObservational observationalFileArea);

	/**
	 * Returns a list of TableBinary objects given an observation file area object.
	 *
	 * @param observationalFileArea
	 * @return list of TableBinary objects
	 */
	List<TableBinary> getTableBinaries(FileAreaObservational observationalFileArea);

	/**
	 * Returns a list of TableDelimited objects given an observation file area object.
	 *
	 * @param observationalFileArea
	 * @return list of TableDelimited objects
	 */
	List<TableDelimited> getTableDelimiteds(FileAreaObservational observationalFileArea);

	/**
	 * Returns a list of FieldCharacter objects given a table character object.
	 *
	 * @param table TableCharacter object
	 * @return list of FieldCharacter objects
	 */
	List<FieldCharacter> getFieldCharacters(TableCharacter table);

	/**
	 * Returns a list of FieldBinary objects given a table binary object.
	 *
	 * @param table TableBinary object
	 * @return list of FieldBinary objects
	 */
	List<FieldBinary> getFieldBinaries(TableBinary table);

	/**
	 * Returns a list of FieldDelimited objects given a table delimited object.
	 *
	 * @param table TableDelimited object
	 * @return list of FieldDelimited objects
	 */
	List<FieldDelimited> getFieldDelimiteds(TableDelimited table);

	/**
	 * Returns a list of GroupFieldDelimited objects given a table delimited object.
	 *
	 * @param table TableDelimited object
	 * @return list of GroupFieldDelimited objects
	 */
	List<GroupFieldDelimited> getGroupFieldDelimiteds(TableDelimited table);

	/**
	 * Returns a list of FieldDelimited and GroupFieldDelimited objects given a
	 * table delimited object.
	 *
	 * @param table TableDelimited object
	 * @return list of FieldDelimited and GroupFieldDelimited objects
	 */
	List<Object> getFieldDelimitedAndGroupFieldDelimiteds(TableDelimited table);

	/**
	 * Returns a list of FieldCharacter and GroupFieldCharacter objects given a
	 * table character object.
	 *
	 * @param table TableCharacter object
	 * @return list of FieldCharacter and GroupFieldCharacter objects
	 */
	List<Object> getFieldCharacterAndGroupFieldCharacters(TableCharacter table);

	/**
	 * Returns a list of FieldBinary and GroupFieldBinary objects given a
	 * table binary object.
	 *
	 * @param table TableBinary object
	 * @return list of FieldBinary and GroupFieldBinary objects
	 */
	List<Object> getFieldBinaryAndGroupFieldBinaries(TableBinary table);

	/**
	 * Returns a list of table objects.
	 *
	 * @param observationalFileAreaSupplemental
	 * @return list of observationalFileAreaSupplemental objects
	 */
	public List<Object> getTableObjects(FileAreaObservationalSupplemental observationalFileAreaSupplemental);

	/**
	 * Gets an instance of ProductObservational.
	 *
	 * @param  relativeXmlFilePath the XML file path and name of the product to obtain, relative
	 * 		   to the ObjectAccess archive root
	 * @return an instance of ProductObservational
	 */
	ProductObservational getObservationalProduct(String relativeXmlFilePath);

	/**
	 * Reads a product label of a specified class, and returns an instance of that class
	 * as a result.
	 *
	 * @param <T> the product object class
	 * @param labelFile the file containing the XML label
	 * @param productClass the product object class
	 * @return an instance of the product object
	 * @throws ParseException if there is an error parsing the label
	 */
	<T> T getProduct(File labelFile, Class<T> productClass) throws ParseException;
	
	/**
   * Reads a product label of a specified class, and returns an instance of that class
   * as a result.
   *
   * @param <T> the product object class
   * @param label the url containing the XML label
   * @param productClass the product object class
   * @return an instance of the product object
   * @throws ParseException if there is an error parsing the label
   */
  <T> T getProduct(URL label, Class<T> productClass) throws ParseException;

	/**
	 * Writes a label given the product XML file.
	 *
	 * @param relativeXmlFilePath the XML file path and name of the product to set, relative
	 * 		  to the ObjectAccess archive root
	 * 
	 * @param product The Product_Observational object to serialize into an XML file.
	 */
	void setObservationalProduct(String relativeXmlFilePath, ProductObservational product) throws Exception;
	
	 /**
   * Writes a label given the product XML file.
   *
   * @param relativeXmlFilePath the XML file path and name of the product to set, relative
   *      to the ObjectAccess archive root.
   *      
   * @param product The Product_Observational object to serialize into an XML file.
   * 
   * @param context A context to use when creating the XML file. Can be set to null.
   * 
   * @throws Exception If there was an error creating the XML file.
   */
  void setObservationalProduct(String relativeXmlFilePath, ProductObservational product, 
      XMLLabelContext context) throws Exception;

}
