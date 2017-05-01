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
package gov.nasa.pds.objectAccess;

import gov.nasa.arc.pds.xml.generated.Array;
import gov.nasa.arc.pds.xml.generated.Array2DImage;
import gov.nasa.arc.pds.xml.generated.Array3DImage;
import gov.nasa.arc.pds.xml.generated.Array3DSpectrum;
import gov.nasa.arc.pds.xml.generated.FieldBinary;
import gov.nasa.arc.pds.xml.generated.FieldCharacter;
import gov.nasa.arc.pds.xml.generated.FieldDelimited;
import gov.nasa.arc.pds.xml.generated.FileAreaObservational;
import gov.nasa.arc.pds.xml.generated.FileAreaObservationalSupplemental;
import gov.nasa.arc.pds.xml.generated.GroupFieldDelimited;
import gov.nasa.arc.pds.xml.generated.ProductObservational;
import gov.nasa.arc.pds.xml.generated.TableBinary;
import gov.nasa.arc.pds.xml.generated.TableCharacter;
import gov.nasa.arc.pds.xml.generated.TableDelimited;

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
	 * Gets a list of Array objects from an observational file area.
	 *
	 * @param fileArea the observational file area
	 * @return an list of arrays, which may be empty
	 */
	List<Array> getArrays(FileAreaObservational fileArea);

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
	 */
	void setObservationalProduct(String relativeXmlFilePath, ProductObservational product);

}
