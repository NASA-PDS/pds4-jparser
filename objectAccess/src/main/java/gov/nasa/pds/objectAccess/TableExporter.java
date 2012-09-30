package gov.nasa.pds.objectAccess;

import gov.nasa.arc.pds.xml.generated.FileAreaObservational;
import gov.nasa.arc.pds.xml.generated.TableDelimited;
import gov.nasa.pds.objectAccess.table.AdapterFactory;
import gov.nasa.pds.objectAccess.table.DelimiterType;
import gov.nasa.pds.objectAccess.table.FieldDescription;
import gov.nasa.pds.objectAccess.table.TableAdapter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

/**
 * Defines methods for converting a table object to a desired export type. 
 * 
 * @author psarram
 */
public class TableExporter extends ObjectExporter implements Exporter<Object> {
	private Charset decoder;	
	private Charset encoder;
	private String exportType = "CSV";
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TableExporter.class);
	private static final String US_ASCII = "US-ASCII";
	
	/**
	 * Constructs a <code>TableExporter</code> instance. It parses the label object to 
	 * get access to the observational file area at index <code>fileAreaIndex</code>. 
	 * <p>
	 * The default "US-ASCII" character set is used for both decoding and encoding the bytes.
	 * Use the <code>setEncoder</code> and <code>setDecoder</code> methods to use a different character set.
	 * </p>
	 * 
	 * @param label         the label file
	 * @param fileAreaIndex the index of the observational file area to be used by this exporter
	 * @throws Exception
	 */
	TableExporter(File label, int fileAreaIndex) throws Exception {
		super(label, fileAreaIndex);
		
		setDecoder(US_ASCII);
		setEncoder(US_ASCII);
	}
	
	/**
	 * Constructs an instance of <code>TableExporter</code>. 
	 * 
	 * <p>
	 * The default "US-ASCII" character set is used for both decoding and encoding the bytes.
	 * Use the <code>setEncoder</code> and <code>setDecoder</code> methods to use a different character set.
	 * </p>
	 * 
	 * @param fileArea the observational file area to be used by this exporter
	 * @param provider the object provider that points to the location of the data to export
	 * @throws IOException
	 */
	TableExporter(FileAreaObservational fileArea, ObjectProvider provider) throws IOException {
		super(fileArea, provider);
		
		setDecoder(US_ASCII);
		setEncoder(US_ASCII);
	}
	
	/**
	 * Gets the fields of a given table object.
	 * 
	 * @param object the table object
	 * @return an array of field descriptions for the table
	 */
	public FieldDescription[] getTableFields(Object object) {
		return AdapterFactory.INSTANCE.getTableAdapter(object).getFields();
	}
	
	/**
	 * Converts the table object into the desired export type. The valid table objects are 
	 * <code>TableCharacter</code>, <code>TableBinary</code> and <code>TableDelimited</code>.
	 * 
	 * @param object        the table object to convert	 
	 * @param outputStream  the output stream for the output file 	 
	 * @throws IOException If an I/O error occurs
	 */
	public void convert(Object object, OutputStream outputStream) throws IOException {		
		File dataFile = new File(getObjectProvider().getRoot().getAbsolutePath(), getObservationalFileArea().getFile().getFileName());		
		Writer writer = new BufferedWriter(new OutputStreamWriter(outputStream, getEncoder()));
				
		if (getExportType().equals("CSV")) {
			exportToCSV(dataFile, writer, object);
		}
	}

	/**
	 * Converts the table object at the given <code>objectIndex</code> into the desired export type.
	 *
	 * @param outputStream the output stream for the output file
	 * @param objectIndex  the index of the input table object 
	 *                      	 
	 * @throws IOException If an I/O error occurs
	 */
	public void convert(OutputStream outputStream, int objectIndex) throws IOException {
		List<Object> list = getObjectProvider().getTableObjects(getObservationalFileArea());		
		convert(list.get(objectIndex), outputStream);		
	}
	
	/**
	 * Gets the desired export type.
	 * 
	 * @return the export type
	 */
	public String getExportType() {
		return this.exportType;
	}
	
	 
	
	/**
	 * Sets the desired export (output) type. Currently, "CSV" is the only supported type.
	 * 
	 * @param exportType the export type
	 */
	public void setExportType(String exportType) {
		this.exportType = exportType;		
	}
	
	/**
	 * Sets a character set to use for decoding the bytes.
	 * 
	 * @param charsetName the name of a character set 
	 * @throws UnsupportedCharsetException If name is not valid
	 */
	public void setDecoder(String charsetName) throws UnsupportedCharsetException {
		try { 
			if (charsetName == null || charsetName.length() == 0) {
				charsetName = US_ASCII;
			}				
			this.decoder = Charset.forName(charsetName);
		} catch(UnsupportedCharsetException ex) {			
			LOGGER.error("The charset name used is not a legal name.", ex);
			throw ex;
		}		
	}
	
	/**
	 * Returns the <code>Charset</code> to use for decoding the bytes.
	 * 
	 * @return a character set
	 */
	public Charset getDecoder() {
		return this.decoder;
	}
	
	/**
	 * Sets a character set to use for encoding the bytes.
	 * 
	 * @param charsetName the name of a character set
	 * @throws UnsupportedCharsetException If name is not valid
	 */
	public void setEncoder(String charsetName) throws UnsupportedCharsetException {
		try { 
			if (charsetName == null || charsetName.length() == 0) {
				charsetName = US_ASCII;
			}				
			this.encoder = Charset.forName(charsetName);
		} catch(UnsupportedCharsetException ex) {			
			LOGGER.error("The charset name used is not a legal name.", ex);
			throw ex;
		}		
	}
	
	/**
	 * Returns the <code>Charset</code> to use for encoding the bytes.
	 *  
	 * @return a character set
	 */
	public Charset getEncoder() {
		return this.encoder;
	}
	
	/*
	 * Exports a table object into a CSV file.
	 */
	private void exportToCSV(File dataFile, Writer writer, Object table) throws FileNotFoundException, IOException {
		if (table instanceof TableDelimited) {
			exportDelimitedTableToCSV(dataFile, writer, (TableDelimited) table, getDecoder());
		} else {
			exportFixedWidthTableToCSV(dataFile, writer, table, getDecoder());
		}
	}
	
	private void exportFixedWidthTableToCSV(File dataFile, Writer writer, Object table, Charset decoder) throws IOException {
		TableAdapter adapter = AdapterFactory.INSTANCE.getTableAdapter(table);
		FieldDescription[] fields = adapter.getFields();
		
		try {
			// Get csv writer			
			CSVWriter csvWriter = new CSVWriter(writer);						
			ByteWiseFileAccessor fileAccessor =
				new ByteWiseFileAccessor(
						dataFile,
						adapter.getOffset(),
						adapter.getRecordLength(),
						adapter.getRecordCount()
				);
			
			// Read column headers
			csvWriter.writeNext(getColumnHeaders(fields));
			
			// Go through the records and read the value of each field.
			for (int i = 1; i <= adapter.getRecordCount(); i++) {
				String[] data = readColumnData(fields, i, adapter.getRecordLength(), fileAccessor, decoder);
				csvWriter.writeNext(data);				
			}	
			
			csvWriter.flush();						
			csvWriter.close();			
		} catch(FileNotFoundException ex) {
			LOGGER.error("The data file does not exist or for some other reason cannot be opened for reading.", ex);	
			throw ex;
		} catch(IOException ex) {
			LOGGER.error("I/O error.", ex);
			throw ex;
		}
	}
	
	/*
	 * Exports a delimited table object into a CSV file.
	 */
	private void exportDelimitedTableToCSV(File dataFile, Writer writer, TableDelimited table, Charset charset) throws FileNotFoundException, IOException {
		TableAdapter adapter = AdapterFactory.INSTANCE.getTableAdapter(table);		
		int records = table.getRecords();
		long tableOffset = (long) table.getOffset().getValue();
		
		try {
			CSVWriter csvWriter = new CSVWriter(writer);
			
			// Get column headers			
			csvWriter.writeNext(getColumnHeaders(adapter.getFields()));						
					
			// Read column data			
			InputStream is = new FileInputStream(dataFile);
			is.skip(tableOffset);
			BufferedReader buffer = new BufferedReader(new InputStreamReader(is, charset));
			CSVReader reader = new CSVReader(buffer, getFieldDelimiter(table.getFieldDelimiter()));
			 
			for (int i = 0; i <records; i++) {
				String[] line = reader.readNext();
				csvWriter.writeNext(line);
			}
			
			csvWriter.flush();						
			csvWriter.close();	
			reader.close();
		} catch(FileNotFoundException ex) {			
			LOGGER.error("The data file does not exist or for some other reason cannot be opened for reading.", ex);
			throw ex;
		} catch(IOException ex) {
			LOGGER.error("I/O error.", ex);
			throw ex;
		}	
	} 
	
	/*
	 * Gets column headers.
	 */
	private String[] getColumnHeaders(FieldDescription[] fields) {
		List<String> headers = new ArrayList<String>();
		
		for (FieldDescription field : fields) {
			headers.add(field.getName());
		}
		
		return headers.toArray(new String[headers.size()]);
	}
	
	/*
	 * Reads column data
	 */
	private String[] readColumnData(FieldDescription[] fields, int recordNum, int recordLength,
			ByteWiseFileAccessor fileAccessor, Charset charset) {	
		
		List<String> data = new ArrayList<String>();
		byte[] bytes = fileAccessor.readRecordBytes(recordNum, 0, recordLength);
		
		for (FieldDescription field : fields) {
			String value = field.getType().getAdapter().getString(bytes, field.getOffset(), field.getLength(), field.getStartBit(), field.getStopBit(), charset);			
			data.add(value);
		}
		
		return data.toArray(new String[data.size()]);
	}	
	
	/* 
	 * Gets the field delimiter. The field delimiter must be equal to one of the following values:
	 * 'comma', 'horizontal_tab', 'semicolon', 'vertical_bar'
	 */
	private char getFieldDelimiter(String delimiter) {	
		assert (delimiter != null && !delimiter.isEmpty());
		
		DelimiterType type = DelimiterType.getDelimiterType(delimiter); 
				
		return type.getFieldDelimiter();
	}	
}
