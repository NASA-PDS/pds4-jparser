package gov.nasa.pds.objectAccess;

import java.io.File;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.nasa.arc.pds.xml.generated.FileAreaObservational;
import gov.nasa.arc.pds.xml.generated.ProductObservational;

/** 
 * Super class of all object type exporters.
 * 
 * @author dcberrio
 */
public abstract class ObjectExporter {
	
	Logger logger = LoggerFactory.getLogger(ObjectExporter.class);
	private ObjectProvider objectProvider;
	private FileAreaObservational fileArea;
	
	/**
	 * Super constructor.  Parses the input label file, reporting errors appropriately.
	 * @param label the label file
	 * @param fileAreaIndex the index of the observational file area to be 
	 * used by this exporter
	 * @throws Exception
	 */
	public ObjectExporter(File label, int fileAreaIndex) throws Exception {
		parseLabel(label, fileAreaIndex);
	}
	
	/**
	 * Super constructor.  
	 * 
	 * @param fileArea the observational file area to be used by this exporter
	 * @param provider the objectProvider that points to the location of the data to export
	 * @throws IOException
	 */
	public ObjectExporter(FileAreaObservational fileArea, ObjectProvider provider) throws IOException {
		this.objectProvider = provider;
		this.fileArea = fileArea;
	}
	

	
	private void parseLabel(File label, int fileAreaIndex) throws Exception {
		if (label.canRead()) {
			this.objectProvider = new ObjectAccess(new File(label.getParent()));
			ProductObservational p = objectProvider.getProduct(label, ProductObservational.class);
			try {				
				fileArea = p.getFileAreaObservationals().get(fileAreaIndex);				
			} catch (IndexOutOfBoundsException e) {
				String message = "Label has no such ObservationalFileArea";
				logger.error(message);
				throw new Exception(message);
			}
		} else {
			String message = "Input file does not exist: " + label.getAbsolutePath();
			logger.error(message);
			throw new IOException(message);
		} 
	}
	
	
	/**
	 * Sets the objectProvider associated with this exporter.
	 * 
	 * @param provider the objectProvider associated with this exporter
	 */
	public void setObjectProvider(ObjectProvider provider) {
		this.objectProvider = provider;	
	}
	
	/** 
	 * Gets the objectProvider associated with this exporter.
	 * 
	 * @return objectProvider the objectProvider associated with this exporter
	 */
	public ObjectProvider getObjectProvider() {
		return objectProvider;	
	}

	
	/**
	 * Set the observational file area containing the data to be exported.
	 * @param fileAreaObs the observational file area containing the data to be exported
	 */
	public void setObservationalFileArea(FileAreaObservational fileAreaObs) {
		this.fileArea = fileAreaObs;
	}
	
	/**
	 * Gets the observational file area containing the data to be exported.
	 * 
	 * @return fileArea the observational file area containing the data to be exported
	 */
	public FileAreaObservational getObservationalFileArea() {
		return fileArea;
	}
}
