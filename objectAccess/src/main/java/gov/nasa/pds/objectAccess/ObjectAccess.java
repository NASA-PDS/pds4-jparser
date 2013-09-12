package gov.nasa.pds.objectAccess;

import gov.nasa.arc.pds.xml.generated.Array2DImage;
import gov.nasa.arc.pds.xml.generated.FieldBinary;
import gov.nasa.arc.pds.xml.generated.FieldCharacter;
import gov.nasa.arc.pds.xml.generated.FileAreaObservational;
import gov.nasa.arc.pds.xml.generated.ProductObservational;
import gov.nasa.arc.pds.xml.generated.TableBinary;
import gov.nasa.arc.pds.xml.generated.TableCharacter;
import gov.nasa.arc.pds.xml.generated.TableDelimited;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
		Class clazz;
		ArrayList<Object> list = new ArrayList<Object>();
		for (Object obj : observationalFileArea.getArray2DsAndArray2DImagesAndArray2DMaps()) {
			clazz = obj.getClass();
			if (clazz.equals(TableCharacter.class)
					|| clazz.equals(TableBinary.class)
					|| clazz.equals(TableDelimited.class)) {
				list.add(obj);
			}
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
	public List<TableDelimited> getTableDelimiteds(FileAreaObservational observationalFileArea) {
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


	/**
	 * Determines if this is a PDS convertible image.
	 * @param child
	 * @return true if this image file can be converted, otherwise false.
	 */
    static boolean isConvertibleImage(String child) {
		return child.endsWith("raw");	// PTOOL-51 Need Spec, try <Array_2D_Image base_class="Array_Base">
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
