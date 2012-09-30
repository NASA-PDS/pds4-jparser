package gov.nasa.pds.objectAccess;

import gov.nasa.arc.pds.xml.generated.Array2DImage;
import gov.nasa.arc.pds.xml.generated.FieldBinary;
import gov.nasa.arc.pds.xml.generated.FieldCharacter;
import gov.nasa.arc.pds.xml.generated.FileAreaObservational;
import gov.nasa.arc.pds.xml.generated.ProductObservational;
import gov.nasa.arc.pds.xml.generated.TableBinary;
import gov.nasa.arc.pds.xml.generated.TableCharacter;
import gov.nasa.arc.pds.xml.generated.TableDelimited;
import gov.nasa.pds.domain.Array2DImageProduct;
import gov.nasa.pds.domain.Collection;
import gov.nasa.pds.domain.DocumentProduct;
import gov.nasa.pds.domain.PDSObject;
import gov.nasa.pds.domain.TableCharacterProduct;
import gov.nasa.pds.domain.TokenizedLabel;

import java.io.File;
import java.util.List;

/**
 * 
 * Provides access to PDS4 objects.
 *
 */
public interface ObjectProvider {
	
	
	/** Gets the root file path of the object archive(s) for this ObjectProvider.
	 * @return the root file path of the object archive(s) for this ObjectProvider
	 */
	public abstract String getArchiveRoot();
	
	/** Gets the root file path of the object archive(s) for this ObjectProvider.
	 * @return the root file path of the object archive(s) for this ObjectProvider
	 */
	public abstract File getRoot();
	
	/** Returns a list of Array2DImage objects given an observation file area object.
	 * @param observationalFileArea
	 * @return
	 */
	public abstract List<Array2DImage> getArray2DImages(FileAreaObservational observationalFileArea);
	
	/**
	 * Returns a list of table objects.
	 * @param observationalFileArea
	 * @return
	 */
	public List<Object> getTableObjects(FileAreaObservational observationalFileArea);
	
	/**
	 * Returns a list of TableCharacter objects given an observation file area object.
	 * @param observationalFileArea
	 * @return list of TableCharacter objects
	 */
	public abstract List<TableCharacter> getTableCharacters(FileAreaObservational observationalFileArea);
	
	/**
	 * Returns a list of TableBinary objects given an observation file area object.
	 * @param observationalFileArea
	 * @return list of TableBinary objects
	 */
	public abstract List<TableBinary> getTableBinaries(FileAreaObservational observationalFileArea);
	
	/**
	 * Returns a list of TableDelimited objects given an observation file area object.
	 * @param observationalFileArea
	 * @return list of TableDelimited objects
	 */
	public abstract List<TableDelimited> getTableDelimited(FileAreaObservational observationalFileArea);
	
	/**
	 * Returns a list of FieldCharacter objects given a table character object.
	 * @param table TableCharacter object
	 * @return list of FieldCharacter objects
	 */
	public abstract List<FieldCharacter> getFieldCharacters(TableCharacter table);
	
	/**
	 * Returns a list of FieldBinary objects given a table binary object.
	 * @param table TableBinary object 
	 * @return
	 */
	public abstract List<FieldBinary> getFieldBinaries(TableBinary table);
	
	/**
	 * @param relativeXmlFilePath the XML file path and name of the product to obtain, relative
	 * to the ObjectAccess archive root 
	 * @return an instance of ProductObservational
	 */
	public abstract ProductObservational getObservationalProduct(String relativeXmlFilePath);
	
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
	public abstract <T> T getProduct(File labelFile, Class<T> productClass) throws ParseException;
	
	/**
	 * Writes a label given the product XML file.
	 * 
	 * @param relativeXmlFilePath the XML file path and name of the product to set, relative
	 * to the ObjectAccess archive root  
	 */
	public abstract void setObservationalProduct(String relativeXmlFilePath, ProductObservational product);
	
	 /**
	  * Given a product XML, returns a full parsed product-specific data object (or an error).
	  * This object is typically populated with its child contents.
	  * Also returns its label in flattened form, HTML formatted.
	  * The default product type is a collection.
	  * 
	  * @param thisRelativeFilename filename of this object, relative to archive (bundle) root
	  * @return an object of <productType>
	  * @throws Exception upon error
	  */
	public abstract Array2DImageProduct parseImageProduct(
			String thisRelativeFilename) throws Exception;

	 /**
	  * Given a product XML, returns a full parsed product-specific data object (or an error).
	  * This object is typically populated with its child contents.
	  * Also returns its label in flattened form, HTML formatted.
	  * The default product type is a collection.
	  * 
	  * @param thisRelativeFilename filename of this object, relative to archive (bundle) root
	  * @return an object of <productType>
	  * @throws Exception upon error
	  */
	public abstract DocumentProduct parseDocumentProduct(
			String thisRelativeFilename) throws Exception;

	 /**
	  * Given a product XML, returns a full parsed product-specific data object (or an error).
	  * This object is typically populated with its child contents.
	  * Also returns its label in flattened form, HTML formatted.
	  * The default product type is a collection.
	  * 
	  * @param thisRelativeFilename filename of this object, relative to archive (bundle) root
	  * @return an object of <productType>
	  * @throws Exception upon error
	  */
	public abstract Collection parseCollection(String thisRelativeFilename)
			throws Exception;


	/**
	 * Parses a table object.
	 * @param thisRelativeFilename filename of this object, relative to archive (bundle) root
	 * @param page_size maximum number of elements to read 
	 * @return an object of <productType>
	 * @throws Exception upon error
	 */
	public abstract TableCharacterProduct parseTableCharacterProduct(
			String thisRelativeFilename, int page_size) throws Exception;

	/** 
	 * Given a parent path, returns an object representation.  The object representation includes its
	 * object type and the children references (unparsed). Also gets a child 
     * count for example to know whether your gui is going to page.
     * 
	 * @param thisRelativeFilename filename of this object, relative to archive (bundle) root
	 * @return an object with core attributes about its type and contents
	 * @throws Exception upon error
	 */
	public abstract PDSObject makePDS4Object(String thisRelativeFilename) throws Exception;

	/**
	 * 	 
	 * Parses a PDS file and returns a tokenized representation of the label, suitable for formatting into a display.
	 * 
	 * @param thisRelativeFilename the file to parse.
	 * @param separator An HTML separator that will be inserted between label name-value pairs.
	 * @return a TokenizedLabel object
	 * @throws Exception upon error
	 */
	public TokenizedLabel getTokenizedLabel(String thisRelativeFilename, String separator) throws Exception;

	/**
	 * Parses a PDS file and returns an HTML String representation of the label.
	 * 
	 * @param thisRelativeFilename the file to parse.
	 * @param separator An HTML separator that will be inserted between label name-value pairs.
	 * @return An HTML string representing the PDS Label.
	 * @throws Exception upon error
	 */
	public String getFlattenedLabel(String thisRelativeFilename, String separator) throws Exception;

}