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
import gov.nasa.pds.domain.FieldInfo;
import gov.nasa.pds.domain.HasChildren;
import gov.nasa.pds.domain.HasFields;
import gov.nasa.pds.domain.HasParsableChildren;
import gov.nasa.pds.domain.PDSObject;
import gov.nasa.pds.domain.TableCharacterProduct;
import gov.nasa.pds.domain.TokenizedLabel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The <code>ObjectAccess</code> class is a point of entry into parsed PDS (including PDS 4/XML-schema-labeled) objects.
 * 
 * @author dcberrio
 */
public class ObjectAccess implements ObjectProvider {
	private static final Logger LOGGER = LoggerFactory.getLogger(ObjectAccess.class);
	private String archiveRoot;
	private File root;
	
	/**
	 * Creates a new instance with the current directory as the archive root
	 * path.
	 */
	public ObjectAccess() {
		this(new File("."));
	}
	
	/**
	 * Constructs an <code>ObjectAccess</code> object and sets the archive root path.
	 * 
	 * @param archiveRoot the archive root path
	 * @throws Exception
	 */
	public ObjectAccess(String archiveRoot) {
		this(new File(archiveRoot));
	}
	
	/**
	 * Constructs an <code>ObjectAccess</code> object and sets the archive root path.
	 * 
	 * @param archiveRoot the archive root path
	 * @throws Exception
	 */
	public ObjectAccess(File archiveRoot) {
		this.root = archiveRoot;
		this.archiveRoot = archiveRoot.getAbsolutePath();
	}
	
	@Override
	public <T> T getProduct(File labelFile, Class<T> productClass) throws ParseException {
		try {
			JAXBContext context = JAXBContext.newInstance( "gov.nasa.arc.pds.xml.generated");
			Unmarshaller u = context.createUnmarshaller();
			return productClass.cast(u.unmarshal(labelFile));
		} catch (JAXBException e) {
			LOGGER.error("Failed to load the product from the label.", e);
			throw new ParseException("Unable to parse the product label", e);
		}	
	}

	@Override
	public ProductObservational getObservationalProduct(String relativeXmlFilePath) {
		try {
			JAXBContext context = JAXBContext.newInstance( "gov.nasa.arc.pds.xml.generated");
			Unmarshaller u = context.createUnmarshaller();
			File f = new File(getRoot().getAbsolutePath() + File.separator + relativeXmlFilePath);									
			return (ProductObservational) u.unmarshal(f);
		} catch (JAXBException e) {
			LOGGER.error("Failed to get the product observational.", e);
			e.printStackTrace();
			return null;
		}	
	}
	
	@Override
	public void setObservationalProduct(String relativeXmlFilePath, ProductObservational product) {
		try {
			JAXBContext context = JAXBContext.newInstance( "gov.nasa.arc.pds.xml.generated");			
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			File f = new File(getRoot().getAbsolutePath() + File.separator + relativeXmlFilePath);						
			m.marshal(product, f);
		} catch (JAXBException e) {
			LOGGER.error("Failed to set the product observational.", e);
			e.printStackTrace();
		}	
	}
	
	@Override
    public List<Array2DImage> getArray2DImages(FileAreaObservational observationalFileArea) {
    	ArrayList<Array2DImage> list = new ArrayList<Array2DImage>();
    	for (Object obj : observationalFileArea.getArray2DsAndArray2DImagesAndArray2DMaps()) {
    		if (obj.getClass().equals(Array2DImage.class)) {
    			list.add(Array2DImage.class.cast(obj));
    		}
    	}
    	return list;
    }
	
	@Override
	public List<Object> getTableObjects(FileAreaObservational observationalFileArea) {
		ArrayList<Object> list = new ArrayList<Object>();
		for (Object obj : observationalFileArea.getArray2DsAndArray2DImagesAndArray2DMaps()) {
			list.add(obj);
		}
		return list;		
	}
	
	@Override
	public List<TableCharacter> getTableCharacters(FileAreaObservational observationalFileArea) {
		ArrayList<TableCharacter> list = new ArrayList<TableCharacter>();
		for (Object obj : observationalFileArea.getArray2DsAndArray2DImagesAndArray2DMaps()) {
			if (obj.getClass().equals(TableCharacter.class)) {
				list.add(TableCharacter.class.cast(obj));
			}
		}
		return list;
	}
	
	@Override
	public List<TableBinary> getTableBinaries(FileAreaObservational observationalFileArea) {
		ArrayList<TableBinary> list = new ArrayList<TableBinary>();
		for (Object obj : observationalFileArea.getArray2DsAndArray2DImagesAndArray2DMaps()) {
			if (obj.getClass().equals(TableBinary.class)) {
				list.add(TableBinary.class.cast(obj));
			}
		}
		return list;
	}
	
	@Override
	public List<TableDelimited> getTableDelimited(FileAreaObservational observationalFileArea) {
		ArrayList<TableDelimited> list = new ArrayList<TableDelimited>();
		for (Object obj : observationalFileArea.getArray2DsAndArray2DImagesAndArray2DMaps()) {
			if (obj.getClass().equals(TableDelimited.class)) {
				list.add(TableDelimited.class.cast(obj));
			}
		}
		return list;
	}
	
	@Override
	public List<FieldCharacter> getFieldCharacters(TableCharacter table) {
		ArrayList<FieldCharacter> list = new ArrayList<FieldCharacter>();
		for (Object obj : table.getRecordCharacter().getFieldCharactersAndGroupFieldCharacters()) {
			if (obj.getClass().equals(FieldCharacter.class)) {
				list.add(FieldCharacter.class.cast(obj));
			}
		}		
		return list;
	}
	
	@Override
	public List<FieldBinary> getFieldBinaries(TableBinary table) {
		ArrayList<FieldBinary> list = new ArrayList<FieldBinary>();
		for (Object obj : table.getRecordBinary().getFieldBinariesAndGroupFieldBinaries()) {
			if (obj.getClass().equals(FieldBinary.class)) {
				list.add(FieldBinary.class.cast(obj));
			}
		}
		return list;
	}
	
	@Override
	public Array2DImageProduct parseImageProduct(String thisRelativeFilename) throws Exception {
		
		LOGGER.debug(archiveRoot +" "+ thisRelativeFilename);
		DocumentBuilder builder =
			DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = builder.parse(ArchiveLocator.resolveDataItemLocation(archiveRoot, thisRelativeFilename));
	
		Array2DImageProduct pdsObject = null; 
		try {
			pdsObject = (Array2DImageProduct)this.makePDS4Object(thisRelativeFilename, doc);
			
			// label 
			LabelParser labelParser = new LabelParser(archiveRoot, thisRelativeFilename, "<br>");
			String v = labelParser.flatten();
			pdsObject.setLabel(v);			
			TokenizedLabel tokenizedLabel = labelParser.tokenize(v);
			pdsObject.setTokenizedLabel(tokenizedLabel);
			
			// parse "File_Area_Observational" for image specific children
			NodeList fileAreaXML = doc.getElementsByTagName("File_Area_Observational");
			assert fileAreaXML.getLength() > 0 : "no file area observational: "+ pdsObject.getThis_file_name();
			for (int k=0; k< fileAreaXML.getLength(); k++) {
				Element fileAreaObservational = (Element) fileAreaXML.item(k);

				//parse files
				NodeList fileXMLs = fileAreaObservational.getElementsByTagName("File");
				assert fileXMLs.getLength() == 1;
				Element fileXML = (Element)fileXMLs.item(0);

				//make child object - needed for basic info for tree display
				NodeList fns = fileXML.getElementsByTagName("file_name");
				assert fns.getLength() == 1;
				String fn = getCharacterDataFromElement((Element)fns.item(0));	
				
				String subpath = ArchiveLocator.getSubpath(thisRelativeFilename);//PTOOL-56
				if (subpath != null) {
					String relativeFn = subpath + "/" + fn;
					fn = relativeFn;
				}
				
				Array2DImageProduct newChild = new Array2DImageProduct(archiveRoot, fn); 
				pdsObject.addChild(newChild);
				newChild.setTitle(fn);
				newChild.setTokenizedLabel(tokenizedLabel);

				NodeList date = fileXML.getElementsByTagName("creation_date_time");
				if (date.getLength() > 0) {
					String value = getCharacterDataFromElement((Element)date.item(0));
					newChild.setCreation_date_time(value);
				}	    
				NodeList records = fileXML.getElementsByTagName("records");
				if (records.getLength() > 0) {
					String value = getCharacterDataFromElement((Element)records.item(0));
					newChild.setRecords(Integer.parseInt(value));
				}
				NodeList file_size = fileXML.getElementsByTagName("file_size");
				if (file_size.getLength() > 0) {
					String value = getCharacterDataFromElement((Element)file_size.item(0));
					newChild.setFile_size(Integer.parseInt(value));
				}

				// <Array_2D_Image> is optional
				NodeList arrayDatas = fileAreaObservational.getElementsByTagName("Array_2D_Image");
				if (arrayDatas.getLength() == 1) {
					Element arrayData = (Element)arrayDatas.item(0);
					NodeList axisDatas = arrayData.getElementsByTagName("Array_Axis");
					assert axisDatas.getLength() == 2;
					
					Element fastAxis = (Element)axisDatas.item(0);
                    NodeList fastCount = fastAxis.getElementsByTagName("elements");
                    assert fastCount.getLength() == 1;
					String v0 = getCharacterDataFromElement((Element)fastCount.item(0));

					Element otherAxis = (Element)axisDatas.item(1);
                    NodeList otherCount = otherAxis.getElementsByTagName("elements");
                    assert otherCount.getLength() == 1;                   
					String v1 = getCharacterDataFromElement((Element)otherCount.item(0));
					
					int rows = Integer.parseInt(v0);
					int cols = Integer.parseInt(v1);

					newChild.setArray_axis_1_elements(rows);
					newChild.setArray_axis_2_elements(cols);
				}
			}
		}
		catch (Exception e) {
			LOGGER.error("Exception ", e.getMessage());
			throw e;
		}
		return (Array2DImageProduct)pdsObject;
	}

	
	@Override
	public DocumentProduct parseDocumentProduct(String thisRelativeFilename) throws Exception {
		
		LOGGER.debug(archiveRoot +" "+ thisRelativeFilename);
		DocumentBuilder builder =
			DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = builder.parse(ArchiveLocator.resolveDataItemLocation(archiveRoot, thisRelativeFilename));

		DocumentProduct pdsObject = null; 
		try {
			pdsObject = (DocumentProduct) this.makePDS4Object(thisRelativeFilename, doc);

			// label 
			LabelParser labelParser = new LabelParser(archiveRoot, thisRelativeFilename, "<br>");
			String v = labelParser.flatten();
			pdsObject.setLabel(v);			
			TokenizedLabel tokenizedLabel = labelParser.tokenize(v);
			pdsObject.setTokenizedLabel(tokenizedLabel);
			
			NodeList formatSetXML = doc.getElementsByTagName("Document_Format_Set");
			for (int k=0; k< formatSetXML.getLength(); k++) {
				// for each format set (k), get one Document_Format.format_type and N Document_File
				Element formatSet = (Element) formatSetXML.item(k);

				//get type from Document_Format element 
				NodeList formatSets = formatSet.getElementsByTagName("Document_Format");
				assert formatSets.getLength() == 1;
				Element documentFormat = (Element)formatSets.item(0);
				NodeList formatTypesXML = documentFormat.getElementsByTagName("format_type");
				assert formatTypesXML.getLength() == 1;
				Element formatType = (Element)formatTypesXML.item(0);
				String type = getCharacterDataFromElement(formatType);

				// PTOOL-57 how to differentiate lead/real documents from associations
				NodeList documentFileXML = formatSet.getElementsByTagName("Document_File");			

				// get lead document in Document_Format_Set
				Element documentFile = (Element)documentFileXML.item(0); //0'th documentFileXML
				NodeList fns = documentFile.getElementsByTagName("file_name");
				String leadDocfile = getCharacterDataFromElement((Element)fns.item(0));
				// ... and its optional attrs
				NodeList dates = documentFile.getElementsByTagName("creation_date_time");
				String creationDate = dates.getLength() == 0 ? "" : getCharacterDataFromElement((Element)dates.item(0));					
				NodeList ids = documentFile.getElementsByTagName("local_identifier");
				String localId = ids.getLength() == 0 ? "" : getCharacterDataFromElement((Element)ids.item(0));	

				String subpath = ArchiveLocator.getSubpath(thisRelativeFilename);//PTOOL-56
				if (subpath != null) {
					String relativeFn = subpath + "/" + leadDocfile;
					leadDocfile = relativeFn;
				}
				DocumentProduct leadDocDI = new DocumentProduct(archiveRoot, leadDocfile); 
				//	leadDocDI.setExternalStandard(esid); 

				leadDocDI.setDocumentType(type);
				leadDocDI.setCreation_date_time(creationDate);
				leadDocDI.setDocumentLocalIdentifier(localId);
                leadDocDI.setTokenizedLabel(tokenizedLabel);
				leadDocDI.setTitle(leadDocfile);					
				pdsObject.addChild(leadDocDI);

				// append visualElements to leadDocument
				for (int i=1; i< documentFileXML.getLength(); i++) {

					Element nonLeadDoc = (Element)documentFileXML.item(i); //i'th <Document_File>

					NodeList f = nonLeadDoc.getElementsByTagName("file_name"); //i'th <Document_File>.<file_name>
					assert f.getLength() == 1;
					String nonLeadDocFile = getCharacterDataFromElement((Element)f.item(0));	

					NodeList d = nonLeadDoc.getElementsByTagName("directory_path_name"); //i'th <Document_File>.<directory_path_name>
					// assert d.getLength() == 1; optional tag directory_path_name
					String nonLeadDir = d.getLength() == 0 ? "" : getCharacterDataFromElement((Element)d.item(0)) +"/";	
					leadDocDI.getVisualElements().add(nonLeadDir + nonLeadDocFile);		
				}
			}		
		}
		catch (Exception e) {
			LOGGER.error("Exception ", e.getMessage());
			throw e;
		}
		return (DocumentProduct)pdsObject;
	}

	@Override
	public Collection parseCollection(String thisRelativeFilename) throws Exception {

		LOGGER.debug(archiveRoot +" "+ thisRelativeFilename);
		HasChildren pdsObject = null;
		DocumentBuilder builder =
			DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = builder.parse(ArchiveLocator.resolveDataItemLocation(archiveRoot, thisRelativeFilename));
		try {
			pdsObject = this.makePDS4Object(thisRelativeFilename, doc);

            if (!(pdsObject instanceof HasParsableChildren)) {
            	throw new Exception("Attempt to parse an XML with no parsable children.");
            }
            
			//  parse Inventory Parent (and substitutes its children)
			if (isInventory(doc)) {

				FieldInfo fieldInfo = null;
				NodeList allFields = doc.getElementsByTagName("Table_Field_File_Specification_Name"); //PTOOL-51
				assert (allFields != null && allFields.getLength() == 1) : "Invalid field spec for inventory";

				for (int i = 0; i < allFields.getLength(); i++) {
					fieldInfo = new FieldInfo();
					Element element = (Element) allFields.item(i);
					NodeList fieldInfoXML = null;			

					fieldInfoXML = element.getElementsByTagName("field_name");				
					if (fieldInfoXML.getLength() > 0) fieldInfo.setField_name(getCharacterDataFromElement((Element) fieldInfoXML.item(0)));

					fieldInfoXML = element.getElementsByTagName("field_data_type");
					if (fieldInfoXML.getLength() > 0) fieldInfo.setField_data_type(getCharacterDataFromElement((Element) fieldInfoXML.item(0)));

					fieldInfoXML = element.getElementsByTagName("field_location");
					if (fieldInfoXML.getLength() > 0) fieldInfo.setField_location(Integer.parseInt(getCharacterDataFromElement((Element) fieldInfoXML.item(0))));

					fieldInfoXML = element.getElementsByTagName("field_length");

					if (fieldInfoXML.getLength() > 0) fieldInfo.setField_length(Integer.parseInt(getCharacterDataFromElement((Element) fieldInfoXML.item(0))));

					fieldInfoXML = element.getElementsByTagName("field_format");
					if (fieldInfoXML.getLength() > 0) fieldInfo.setField_format(getCharacterDataFromElement((Element) fieldInfoXML.item(0)));

					fieldInfoXML = element.getElementsByTagName("field_unit");
					if (fieldInfoXML.getLength() > 0) fieldInfo.setField_unit(getCharacterDataFromElement((Element) fieldInfoXML.item(0)));

					fieldInfoXML = element.getElementsByTagName("field_description");
					if (fieldInfoXML.getLength() > 0) fieldInfo.setField_description(getCharacterDataFromElement((Element) fieldInfoXML.item(0)));

					((HasFields)pdsObject).getFields().add(fieldInfo);
				}

				// Swap out its one child (should be a tab file) for the actual inventory 
				assert pdsObject.getFilenamesCount() == 1 : "inventory child count invalid"; // PTOOL-58 child count is always one?
				List<String> inventory = readTabInventory(pdsObject.getFirstFilename(), ((HasFields)pdsObject).getFields());

				pdsObject.clearAllFilenames();

				for (int i=0; i < inventory.size(); i++) {
					pdsObject.addFilename(inventory.get(i));
				}

			} 

			// Look ahead to children to set child data types. This is needed to anticipate displays such as is Openable.
			// eg, when THIS is a Collection, its child Product Array child is not openable.
			Iterator<String> l = pdsObject.filenameIterator();
			while (l.hasNext()) {
				String f = l.next();
				DocumentBuilder b = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Document document = b.parse(ArchiveLocator.resolveDataItemLocation(archiveRoot, f));
				PDSObject newChild = makePDS4Object(f, document);
				newChild.setTitle(f);
				pdsObject.addChild(newChild);		
			}


		} catch (Exception e) {
			LOGGER.error("Exception ", e.getMessage());
			throw e;
		}
		return (Collection)pdsObject;

	}


	@Override
	public TableCharacterProduct parseTableCharacterProduct(String thisRelativeFilename, int page_size) throws Exception {
		
		LOGGER.debug(archiveRoot +" "+ thisRelativeFilename);
		PDSObject pdsObject = null;
		DocumentBuilder builder =
			DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = builder.parse(ArchiveLocator.resolveDataItemLocation(archiveRoot, thisRelativeFilename));
		try {
			pdsObject = this.makePDS4Object(thisRelativeFilename, doc);

			assert (pdsObject instanceof TableCharacterProduct);
			assert pdsObject.getFilenamesCount() == 1 : "table product child count invalid";

			// label 
			LabelParser labelParser = new LabelParser(archiveRoot, thisRelativeFilename, "<br>");
			String v = labelParser.flatten();
			pdsObject.setLabel(v);			
			TokenizedLabel tokenizedLabel = labelParser.tokenize(v);
			pdsObject.setTokenizedLabel(tokenizedLabel);
			
			Iterator<String> l = pdsObject.filenameIterator();
			while (l.hasNext()) {
				String f = l.next();
				TableCharacterProduct newChild = new TableCharacterProduct(this.archiveRoot, f);
				newChild.setTitle(f);
				pdsObject.addChild(newChild);
				newChild.setFields(parseFieldInfo(doc));
				newChild.setTokenizedLabel(tokenizedLabel);
				readTableData(f, (HasFields)newChild, page_size);
			}


		} catch (Exception e) {
			LOGGER.error("Exception ", e.getMessage());
			throw e;
		}
		return (TableCharacterProduct)pdsObject;

	}

	@Override
	public PDSObject makePDS4Object(String thisRelativeFilename) throws Exception {
		DocumentBuilder builder =
			DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = builder.parse(ArchiveLocator.resolveDataItemLocation(archiveRoot, thisRelativeFilename));
		return makePDS4Object(thisRelativeFilename, doc);
	}


	private PDSObject makePDS4Object(String thisRelativeFilename, Document doc) throws Exception {
		
		LOGGER.debug(archiveRoot +" "+ thisRelativeFilename);
        PDSObject pdsObject = null;
		
		// instantiate the object type upon knowing product class
		NodeList idAreaXML = doc.getElementsByTagName("Identification_Area_Product");		
		if (idAreaXML.getLength() == 0) idAreaXML = doc.getElementsByTagName("Identification_Area_Bundle"); //PTOOL-51  why not just ID Area? redundant to product_class
		if (idAreaXML.getLength() == 0) idAreaXML = doc.getElementsByTagName("Identification_Area_Collection");
		if (idAreaXML.getLength() == 0) idAreaXML = doc.getElementsByTagName("Identification_Area_Document");

		if (idAreaXML.getLength() == 0) {
			throw new Exception("Parser: unknown id type");
		}
		
		Element idElement = (Element) idAreaXML.item(0);		
		NodeList productClasses = idElement.getElementsByTagName("product_class");
		String productClass = getCharacterDataFromElement((Element)productClasses.item(0));

		if (productClass.equals("Product_Array_2D_Image")) {
			pdsObject = new Array2DImageProduct(this.archiveRoot, thisRelativeFilename);		
		} else {
			if (productClass.startsWith("Collection_")) {  //PTOOL-51
				if (! isInventory(doc)) {
					// PTOOL-51 we assert that all Collections are inventory style
					throw new Exception("Parser: inconsistent inventory collection for file:"+thisRelativeFilename);
				}
				pdsObject = new Collection(this.archiveRoot, thisRelativeFilename);
			} else {
				if (productClass.startsWith("Product_Table_")) {  //PTOOL-51 
					pdsObject = new TableCharacterProduct(this.archiveRoot, thisRelativeFilename);
				} else {
					if (productClass.equals("Product_Bundle") || productClass.equals("Archive_Bundle")) { //PHOENIX PTOOL-51 
						pdsObject = new Collection(archiveRoot, thisRelativeFilename); // not "new Bundle" PTOOL-51 a bundle is a collection
					} else {
						if (productClass.equals("Product_Document")) {
							pdsObject = new DocumentProduct(archiveRoot, thisRelativeFilename);
						} else {
							if (productClass.equals("Product_Stream_Delimited")) {
								pdsObject = new Collection(archiveRoot, thisRelativeFilename); // using collection for default PTOOL-51 a bundle is a collection
							} else {
								throw new Exception("Parser: unknown id type:"+productClass+ " ; in file:"+thisRelativeFilename);
							}
						}
					}
				}
			}
		}

		// Parse identification area
		pdsObject.setLogical_identifier(getCharacterDataFromElement((Element) idElement.getElementsByTagName("logical_identifier").item(0)));
		pdsObject.setTitle(getCharacterDataFromElement((Element) idElement.getElementsByTagName("title").item(0)));
	
		// parse all child filenames
		NodeList fileAreaXML = doc.getElementsByTagName("file_name"); 
		if (fileAreaXML.getLength() == 0) fileAreaXML = doc.getElementsByTagName("file_specification_name"); // PTOOL-51 for bundles this is file_specification_name 
		
		for (int k = 0; k< fileAreaXML.getLength(); k++) {
			String fn = getCharacterDataFromElement((Element) fileAreaXML.item(k));

			String subpath = ArchiveLocator.getSubpath(thisRelativeFilename);
			if (subpath != null) {
				String relativeFn = subpath + "/" + fn;
				fn = relativeFn;
			}
		    pdsObject.addFilename(fn);
		}
		return pdsObject;
	}

	/**
	 * Get field info from a document.
	 * @param doc Document of the parent
	 * @return list of field infos parsed from parent
	 * @throws Exception
	 */
	private List<FieldInfo> parseFieldInfo(Document doc) throws Exception {
		FieldInfo fieldInfo = null;
		NodeList allFields = doc.getElementsByTagName("Table_Character_Field");
		List<FieldInfo> fields = new ArrayList<FieldInfo>();

		for (int i = 0; i < allFields.getLength(); i++) {
			fieldInfo = new FieldInfo();
			Element element = (Element) allFields.item(i);  //ith Table_Character_Field
			NodeList fieldInfoXML = null;			

			fieldInfoXML = element.getElementsByTagName("field_name");				
			if (fieldInfoXML.getLength() > 0) fieldInfo.setField_name(getCharacterDataFromElement((Element) fieldInfoXML.item(0)));

			fieldInfoXML = element.getElementsByTagName("field_data_type");
			if (fieldInfoXML.getLength() > 0) fieldInfo.setField_data_type(getCharacterDataFromElement((Element) fieldInfoXML.item(0)));

			fieldInfoXML = element.getElementsByTagName("field_location");
			if (fieldInfoXML.getLength() > 0) fieldInfo.setField_location(Integer.parseInt(getCharacterDataFromElement((Element) fieldInfoXML.item(0))));

			fieldInfoXML = element.getElementsByTagName("field_length");
			if (fieldInfoXML.getLength() > 0) fieldInfo.setField_length(Integer.parseInt(getCharacterDataFromElement((Element) fieldInfoXML.item(0))));

			fieldInfoXML = element.getElementsByTagName("field_format");
			if (fieldInfoXML.getLength() > 0) fieldInfo.setField_format(getCharacterDataFromElement((Element) fieldInfoXML.item(0)));

			fieldInfoXML = element.getElementsByTagName("field_unit");
			if (fieldInfoXML.getLength() > 0) fieldInfo.setField_unit(getCharacterDataFromElement((Element) fieldInfoXML.item(0)));

			fieldInfoXML = element.getElementsByTagName("field_description");
			if (fieldInfoXML.getLength() > 0) fieldInfo.setField_description(getCharacterDataFromElement((Element) fieldInfoXML.item(0)));

			fields.add(fieldInfo);
		}
		return fields;
	}


	/**
	 * Determine if a document is a valid inventory doc.  
	 * PTOOL-51 This is my understanding of the spec as per Ron 7/25/11.
	 * @param doc the document being parsed.
	 * @return true if this document's children are specified in an inventory table, otherwise false.
	 */
	private static boolean isInventory(Document doc) {
		NodeList nodes = doc.getElementsByTagName("File_Area_Inventory_LIDVID_Primary");
		if (nodes.getLength() == 0) {
			nodes = doc.getElementsByTagName("File_Area_Inventory_LIDVID_Secondary");
		}
		if (nodes.getLength() == 0) {
			nodes = doc.getElementsByTagName("File_Area_Inventory_LID_Secondary");
		}
		return (nodes == null || nodes.getLength() < 1) ? false : true;
	}


	/**
	 * Determines if this is a PDS convertible image.
	 * @param child
	 * @return true if this image file can be converted, otherwise false.
	 */
    static boolean isConvertibleImage(String child) {
		return child.endsWith("raw");	// PTOOL-51 Need Spec, try <Array_2D_Image base_class="Array_Base">		
	}

	/**
	 * Parses an inventory file and returns the list of inventory items.
	 * @param inventoryFilename
	 * @param fieldInfos field info list for this inventory file
	 * @return list of inventory items
	 * @throws Exception upon error, for example, if the inventory file is not found
	 */
	private List<String> readTabInventory(String inventoryFilename, List<FieldInfo> fieldInfos) throws Exception {
		
		assert ((fieldInfos != null) && (fieldInfos.size() > 0)) : "invalid fieldInfos parameter while reading inventory file";
		List<String> rv = new ArrayList<String>();
		String thisFqFilename = ArchiveLocator.resolveDataItemLocation(archiveRoot, inventoryFilename);
		BufferedReader br = new BufferedReader(new FileReader(thisFqFilename));

		int file_specification_field_location = -1;
		for (FieldInfo fi : fieldInfos) {			
			if (fi.getField_name().equals("file_specification_name")) {  // PTOOL-51 need spec
				file_specification_field_location = fi.getField_location();
			}
		}

		String line = null;
		while ((line = br.readLine()) != null) {
			String fn = line.substring(file_specification_field_location - 1);  //java substr is zero based array
			rv.add(fn.trim());
		}
		return rv;
	}

	private static String getCharacterDataFromElement(Element e) {
		Node child = e.getFirstChild();
		if (child instanceof CharacterData) {
			CharacterData cd = (CharacterData) child;
			return cd.getData();
		}
		return "?";
	}
	
	@Override
	public String getFlattenedLabel(String thisRelativeFilename, String separator) throws Exception {
		LabelParser labelParser = new LabelParser(this.archiveRoot, thisRelativeFilename, separator);
		String v = labelParser.flatten();
		if (v == null) {
			LOGGER.warn("Null result getting flattened label for: "+thisRelativeFilename);
		}
		return v;
	}
	
	@Override
	public TokenizedLabel getTokenizedLabel(String thisRelativeFilename, String separator)  throws Exception {
		LabelParser labelParser = new LabelParser(this.archiveRoot, thisRelativeFilename, separator);
		String v = labelParser.flatten();
		if (v == null) {
			LOGGER.warn("Null result getting flattened label for: "+thisRelativeFilename);
		}
		TokenizedLabel tokenizedLabel = labelParser.tokenize(v);
		if (tokenizedLabel == null) {
			LOGGER.warn("Null result getting tokenized label for: "+thisRelativeFilename);
		}
		return tokenizedLabel;
	}
	
	/**
	 * Read contents of a table product.
	 * @param thisRelativeFilename table product file
	 * @param fields field info
	 * @param page_size the maximum number of table entries to be read
	 * @throws Exception upon error
	 */
	private void readTableData(String thisRelativeFilename, HasFields fields, int page_size) throws Exception {
		
		List<FieldInfo> fieldInfos = fields.getFields();
		assert ((fieldInfos != null) && (fieldInfos.size() > 0)) : "invalid fieldInfos parameter while reading table product";
		String thisFqFilename = ArchiveLocator.resolveDataItemLocation(archiveRoot, thisRelativeFilename); 
		BufferedReader br = new BufferedReader(new FileReader(thisFqFilename));

		String line = null;
		int row = 0;
		while (row < page_size && (line = br.readLine()) != null) {
			for (int col=0; col<fieldInfos.size(); col++) {
				FieldInfo fi = fieldInfos.get(col);
				int locn = fi.getField_location() - 1;				
				String v = line.substring(locn , locn + fi.getField_length());
				fi.getValues().add(v);
			}
			row++;
		}
	}
	
	@Override
	public String getArchiveRoot() {
		return this.archiveRoot;
	}
	
	@Override
	public File getRoot() {
		return this.root;
	}

}
