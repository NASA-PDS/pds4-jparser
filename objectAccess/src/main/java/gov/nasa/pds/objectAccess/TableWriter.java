package gov.nasa.pds.objectAccess;

import gov.nasa.pds.objectAccess.table.DelimiterType;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVWriter;

/**
 * The <code>TableWriter</code> class is used for writing fixed-width text, fixed-width binary and delimited data files.
 * 
 * @author psarram
 */
public class TableWriter {	
	private int recordLength;
	private Charset charset;
	private String type;
	private OutputStream outputStream;
	private CSVWriter csvWriter = null;
	private TableRecord record = null;	
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TableExporter.class);	
	private static final String US_ASCII = "US-ASCII";		
	private static final String CHARACTER = "character"; 
	private static final String BINARY = "binary"; 
	private static final String DELIMITED = "delimited";
		
	/**
	 * Constructs an instance of <code>TableWriter</code> for writing to a fixed-width text or binary data file. 
	 * Call the <code>setType</code> method to specify the type of data file. For fixed-width text file,
	 * 'carriage return + line feed' is used for record delimiter.
	 * 
	 * @param outputStream an output stream
	 * @param recordLength the record length in bytes
	 * @param charsetName  the charset name to use for encoding the bytes.
	 * @throws UnsupportedCharsetException
	 */
	public TableWriter(OutputStream outputStream, int recordLength, String charsetName) throws UnsupportedCharsetException {			
		this.outputStream = outputStream;
		this.recordLength = recordLength;				
		setEncoding(charsetName);
	}
	
	/**
	 * Creates an instance of <code>TableWriter</code> for writing to a fixed-width text or binary data file
	 * and uses "US-ASCII" character set name for encoding. Call <code>setType(type)</code> to specify the type
	 * of data file. For fixed-width text file, 'carriage return + line feed' is used for record delimiter.  
	 *   
	 * @param outputStream an output stream
	 * @param recordLength the record length in bytes	 
	 */
	public TableWriter(OutputStream outputStream, int recordLength) {			
		this.outputStream = outputStream;
		this.recordLength = recordLength;		
		this.charset = Charset.forName(US_ASCII);
	}	
	
	/**
	 * Creates an instance of <code>TableWriter</code> for writing to a delimited data file.
	 * It uses comma for field separator and 'carriage return + line feed' for record delimiter.
	 * 
	 * @param writer a writer object
	 */
	public TableWriter(Writer writer) {
		this.type = DELIMITED;
		this.csvWriter = new CSVWriter(
								writer,
								DelimiterType.COMMA.getFieldDelimiter(),
								CSVWriter.NO_QUOTE_CHARACTER,
								'\\',
								DelimiterType.CARRIAGE_RETURN_LINE_FEED.getRecordDelimiter()
							);		
	}	
	
	/**
	 * Creates an instance of <code>TableWriter</code> for writing to a delimited data file. 
	 * It uses 'carriage return + line feed' for record delimiter.
	 * 
	 * @param writer          a writer object
	 * @param fieldDelimiter  the field delimiter. It must be equal to one of the following values:
	 * 						  'comma', 'horizontal_tab', 'semicolon', 'vertical_bar' 	
	 */
	public TableWriter(Writer writer, String fieldDelimiter) {
		DelimiterType delimiterType = DelimiterType.getDelimiterType(fieldDelimiter);		
		this.type = DELIMITED;
		this.csvWriter = new CSVWriter(
								writer,
								delimiterType.getFieldDelimiter(),
								CSVWriter.NO_QUOTE_CHARACTER,
								DelimiterType.CARRIAGE_RETURN_LINE_FEED.getRecordDelimiter()
							);
	}		
	
	/**
	 * Creates a record for adding data.
	 * 
	 * @return an instance of <code>TableRecord</code>
	 */
	public TableRecord createRecord() {		
		if (record == null) {
			String type = getType();
			
			if (type == null) {
				String msg = "The data file type is not defined.";
				LOGGER.error(msg);
				throw new NullPointerException(msg);
			}
			
			if (type.equals(CHARACTER)) {
				record = new TableRecord(recordLength, charset, false);
			} else if (type.equals(BINARY)) {
				record = new TableRecord(recordLength, charset, true);
			} else if (type.equals(DELIMITED)) {
				record = new TableRecord();
			} else {
				String msg = "The data file type '" + type + "' is not valid. The Supported types are 'character', 'binray' and 'delimited'.";
				LOGGER.error(msg);
				throw new IllegalArgumentException(msg);
			}
		} else {
			record.clear();
		}
			
		return record;		
	}
	
	/**
	 * Writes a record to the output stream or writer.
	 * 
	 * @param record the <code>TableRecord</code> object
	 * @throws IOException
	 */
	public void write(TableRecord record) throws IOException {
		if (type.equals(DELIMITED)) {
			csvWriter.writeNext(record.getItems());
		} else {
			outputStream.write(record.getBuffer());
		}	
	}
	
	/**
	 * Flushes the output stream or writer.
	 * 
	 * @throws IOException
	 */
	public void flush() throws IOException {
		if (type.equals(DELIMITED)) {
			csvWriter.flush();
		} else {
			outputStream.flush();
		}	
	}
	
	/**
	 * Closes this table writer which may no longer be used for writing records. 
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {	
		if (type.equals(DELIMITED)) {
			csvWriter.close();
		} else {
			outputStream.close();
		}	
	}

	/**
	 * Sets the type of the data file to be written. Supported types are: 
	 * <ul>
	 * <li>character</li>
	 * <li>binary</li>
	 * <li>delimited</li>
	 * </ul>
	 * 
	 * @param type the data file type. 
	 */
	public void setType(String type) {
		assert (type != null && !type.isEmpty());
		
		this.type = type;
	}
	
	/**
	 * Returns the type of the data file.
	 * 
	 * @return the data file type
	 */
	public String getType() {
		return this.type;
	}
	
	/**
	 * Sets Charset for encoding the bytes.
	 * 
	 * @param charsetName the charset name
	 */
	private void setEncoding(String charsetName) {
		try { 
			if (charsetName == null || charsetName.length() == 0) {
				charsetName = US_ASCII;
			}				
			charset = Charset.forName(charsetName);
		} catch(UnsupportedCharsetException ex) {
			String msg = "The charset name used is not a legal name.";
			LOGGER.error(msg, ex);
			throw new UnsupportedCharsetException(msg);
		}		
	}		
}
