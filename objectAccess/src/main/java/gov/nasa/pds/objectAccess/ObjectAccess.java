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
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.NoSuchElementException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;

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

	private JAXBContext getJAXBContext(String pkgName) throws JAXBException {
		ClassLoader currentLoader = Thread.currentThread().getContextClassLoader();
		if (!(currentLoader instanceof WorkaroundClassLoader)) {
			ClassLoader loader = new WorkaroundClassLoader(
					currentLoader!=null ? currentLoader : getClass().getClassLoader()
			);
			Thread.currentThread().setContextClassLoader(loader);
		}
		return JAXBContext.newInstance(pkgName);
	}

	@Override
	public <T> T getProduct(File labelFile, Class<T> productClass) throws ParseException {
		try {
			JAXBContext context = getJAXBContext("gov.nasa.arc.pds.xml.generated");
			Unmarshaller u = context.createUnmarshaller();
			u.setEventHandler(new LenientEventHandler());
			return productClass.cast(u.unmarshal(labelFile));
		} catch (JAXBException e) {
			LOGGER.error("Failed to load the product from the label.", e);
			throw new ParseException("Unable to parse the product label", e);
		}
	}

	@Override
	public ProductObservational getObservationalProduct(String relativeXmlFilePath) {
		try {
			JAXBContext context = getJAXBContext("gov.nasa.arc.pds.xml.generated");
			Unmarshaller u = context.createUnmarshaller();
			u.setEventHandler(new LenientEventHandler());
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
			JAXBContext context = getJAXBContext("gov.nasa.arc.pds.xml.generated");
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
	public List<Array> getArrays(FileAreaObservational fileArea) {
		List<Array> list = new ArrayList<Array>();
		for (Object obj : fileArea.getDataObjects()) {
			if (obj instanceof Array) {
				list.add(Array.class.cast(obj));
			}
		}
		return list;
	}

	@Override
    public List<Array2DImage> getArray2DImages(FileAreaObservational observationalFileArea) {
    	ArrayList<Array2DImage> list = new ArrayList<Array2DImage>();
    	for (Object obj : observationalFileArea.getDataObjects()) {
    		if (obj.getClass().equals(Array2DImage.class)) {
    			list.add(Array2DImage.class.cast(obj));
    		}
    	}
    	return list;
    }

	public List<Array3DImage> getArray3DImages(FileAreaObservational observationalFileArea) {
    ArrayList<Array3DImage> list = new ArrayList<Array3DImage>();
    for (Object obj : observationalFileArea.getDataObjects()) {
      if (obj.getClass().equals(Array3DImage.class)) {
        list.add(Array3DImage.class.cast(obj));
      }
    }
    return list;
	}
	
	public List<Array3DSpectrum> getArray3DSpectrums(FileAreaObservational observationalFileArea) {
    ArrayList<Array3DSpectrum> list = new ArrayList<Array3DSpectrum>();
    for (Object obj : observationalFileArea.getDataObjects()) {
      if (obj.getClass().equals(Array3DSpectrum.class)) {
        list.add(Array3DSpectrum.class.cast(obj));
      }
    }
    return list;	  
	}

	@Override
	public List<Object> getTableObjects(FileAreaObservational observationalFileArea) {
		Class<?> clazz;
		ArrayList<Object> list = new ArrayList<Object>();
		for (Object obj : observationalFileArea.getDataObjects()) {
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
		for (Object obj : observationalFileArea.getDataObjects()) {
			if (obj.getClass().equals(TableCharacter.class)) {
				list.add(TableCharacter.class.cast(obj));
			}
		}
		return list;
	}

	@Override
	public List<TableBinary> getTableBinaries(FileAreaObservational observationalFileArea) {
		ArrayList<TableBinary> list = new ArrayList<TableBinary>();
		for (Object obj : observationalFileArea.getDataObjects()) {
			if (obj.getClass().equals(TableBinary.class)) {
				list.add(TableBinary.class.cast(obj));
			}
		}
		return list;
	}

	@Override
	public List<TableDelimited> getTableDelimiteds(FileAreaObservational observationalFileArea) {
		ArrayList<TableDelimited> list = new ArrayList<TableDelimited>();
		for (Object obj : observationalFileArea.getDataObjects()) {
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
	public List<FieldDelimited> getFieldDelimiteds(TableDelimited table) {
		ArrayList<FieldDelimited> list = new ArrayList<FieldDelimited>();
		for (Object obj : table.getRecordDelimited().getFieldDelimitedsAndGroupFieldDelimiteds()) {
			if (obj.getClass().equals(FieldDelimited.class)) {
				list.add(FieldDelimited.class.cast(obj));
			}
		}
		return list;
	}

	@Override
	public List<GroupFieldDelimited> getGroupFieldDelimiteds(TableDelimited table) {
		ArrayList<GroupFieldDelimited> list = new ArrayList<GroupFieldDelimited>();
		for (Object obj : table.getRecordDelimited().getFieldDelimitedsAndGroupFieldDelimiteds()) {
			if (obj.getClass().equals(GroupFieldDelimited.class)) {
				list.add(GroupFieldDelimited.class.cast(obj));
			}
		}
		return list;
	}

	@Override
	public List<Object> getFieldDelimitedAndGroupFieldDelimiteds(TableDelimited table) {
		ArrayList<Object> list = new ArrayList<Object>();
		for (Object obj : table.getRecordDelimited().getFieldDelimitedsAndGroupFieldDelimiteds()) {
				list.add(obj);
		}
		return list;
	}

	@Override
	public List<Object> getFieldCharacterAndGroupFieldCharacters(TableCharacter table) {
		ArrayList<Object> list = new ArrayList<Object>();
		for (Object obj : table.getRecordCharacter().getFieldCharactersAndGroupFieldCharacters()) {
				list.add(obj);
		}
		return list;
	}

	@Override
	public List<Object> getFieldBinaryAndGroupFieldBinaries(TableBinary table) {
		ArrayList<Object> list = new ArrayList<Object>();
		for (Object obj : table.getRecordBinary().getFieldBinariesAndGroupFieldBinaries()) {
				list.add(obj);
		}
		return list;
	}

	@Override
	public List<Object> getTableObjects(FileAreaObservationalSupplemental observationalFileAreaSupplemental) {
		Class<?> clazz;
		ArrayList<Object> list = new ArrayList<Object>();
		for (Object obj : observationalFileAreaSupplemental.getDataObjects()) {
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

	/**
	 * Implements a validation event handler that attempts to continue the unmarshalling
	 * even if errors are present. Only fatal errors will halt the unmarshalling.
	 */
	private static class LenientEventHandler implements ValidationEventHandler {

		@Override
		public boolean handleEvent(ValidationEvent event) {
			if (event.getSeverity() == ValidationEvent.FATAL_ERROR) {
				System.err.println("Fatal error: " + event.getLocator().toString() + ": " + event.getMessage());
				return false;
			}

			return true;
		}

	}

	private static class WorkaroundClassLoader extends ClassLoader {

		private static final String IGNORED_RESOURCE = "META-INF/services/javax.xml.parsers.DocumentBuilderFactory";

		/**
		 * Create a new instance with the given parent classloader.
		 *
		 * @param parent the parent classloader
		 */
		public WorkaroundClassLoader(ClassLoader parent) {
			super(parent);
		}

		@Override
		protected Enumeration<URL> findResources(String name) throws IOException {
			if (!name.equals(IGNORED_RESOURCE)) {
				return super.findResources(name);
			} else {
				return new EmptyEnumeration<URL>();
			}
		}

		@Override
		protected URL findResource(String name) {
			if (!name.equals(IGNORED_RESOURCE)) {
				return super.findResource(name);
			} else {
				return null;
			}
		}

		@Override
		public URL getResource(String name) {
			if (!name.equals(IGNORED_RESOURCE)) {
				return super.getResource(name);
			} else {
				return null;
			}
		}

		@Override
		public Enumeration<URL> getResources(String name) throws IOException {
			if (!name.equals(IGNORED_RESOURCE)) {
				return super.getResources(name);
			} else {
				return new EmptyEnumeration<URL>();
			}
		}

	}

	private static class EmptyEnumeration<T> implements Enumeration<T> {

		@Override
		public boolean hasMoreElements() {
			return false;
		}

		@Override
		public T nextElement() throws NoSuchElementException {
			throw new NoSuchElementException();
		}

	}

}
