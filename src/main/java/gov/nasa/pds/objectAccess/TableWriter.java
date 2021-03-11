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

import gov.nasa.pds.label.object.FieldDescription;
import gov.nasa.pds.label.object.TableRecord;
import gov.nasa.pds.objectAccess.table.AdapterFactory;
import gov.nasa.pds.objectAccess.table.DelimiterType;
import gov.nasa.pds.objectAccess.table.TableAdapter;
import gov.nasa.pds.objectAccess.table.TableBinaryAdapter;
import gov.nasa.pds.objectAccess.table.TableDelimitedAdapter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVWriter;

/**
 * The <code>TableWriter</code> class is used for writing
 * fixed-width text, fixed-width binary and delimited data files.
 */
public class TableWriter {		
	private Charset charset;	
	private OutputStream outputStream;
	private TableAdapter adapter;
	private CSVWriter csvWriter = null;
	private TableRecord record = null;		
	private Map<String, Integer> map = new HashMap<String, Integer>();
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TableExporter.class);	
	private static final String US_ASCII = "US-ASCII";			
		
	/**
	 * Creates an instance of <code>TableWriter</code> for writing to a
	 * fixed-width text or binary data file. For fixed-width text file,
	 * 'carriage return + line feed' is used for record delimiter.
	 * 
	 * @param table a table object
	 * @param outputStream an output stream 
	 * @param charsetName the charset name to use for encoding the bytes.
	 * @throws UnsupportedCharsetException
	 */
	public TableWriter(Object table, OutputStream outputStream, String charsetName)	throws UnsupportedCharsetException, InvalidTableException {
		adapter = AdapterFactory.INSTANCE.getTableAdapter(table);		
		this.outputStream = outputStream;										
		setEncoding(charsetName);		
		createFieldMap();
	}
	
	/**
	 * Creates an instance of <code>TableWriter</code> for writing to
	 * a fixed-width text or binary data file and uses "US-ASCII"
	 * character set name for encoding. For fixed-width text file,
	 * 'carriage return + line feed' is used for record delimiter.  
	 * 
	 * @param table a table object
	 * @param outputStream an output stream
	 */
	public TableWriter(Object table, OutputStream outputStream) throws InvalidTableException {
		this(table, outputStream, US_ASCII);
	}	
	
	/**
	 * Creates an instance of <code>TableWriter</code> for writing to
	 * a delimited data file. It uses 'carriage return + line feed'
	 * for record delimiter.
	 * 
	 * @param table  a table object
	 * @param writer a writer object
	 */
	public TableWriter(Object table, Writer writer) throws InvalidTableException {
		adapter = AdapterFactory.INSTANCE.getTableAdapter(table);
		createFieldMap();
		// TODO: What should quotchar be set to? CSVWriter.NO_QUOTE_CHARACTER?
		csvWriter = new CSVWriter(
								writer,								
								((TableDelimitedAdapter) adapter).getFieldDelimiter(),
								CSVWriter.DEFAULT_QUOTE_CHARACTER,
								'\\',
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
			if (adapter instanceof TableDelimitedAdapter) {
				record = new DelimitedTableRecord(map, adapter.getFieldCount());
			} else {						
				record = new FixedTableRecord(adapter.getRecordLength(), map,
						adapter.getFields(), charset, (adapter instanceof TableBinaryAdapter));
			}
		} else {
			record.clear();
		}
			
		return record;		
	}
	
	/**
	 * Writes the table record to the output stream or writer.
	 * 
	 * @param record the <code>TableRecord</code> object
	 * @throws IOException
	 */
	public void write(TableRecord record) throws IOException {
		if (adapter instanceof TableDelimitedAdapter) {
			csvWriter.writeNext(((DelimitedTableRecord) record).getRecordValue());
		} else {
			outputStream.write(((FixedTableRecord) record).getRecordValue());
		}	
	}
	
	/**
	 * Flushes the output stream or writer.
	 * 
	 * @throws IOException
	 */
	public void flush() throws IOException {
		if (adapter instanceof TableDelimitedAdapter) {
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
		if (adapter instanceof TableDelimitedAdapter) {
			csvWriter.close();
		} else {
			outputStream.close();
		}	
	}

	/**
	 * Sets Charset for encoding the bytes.
	 * 
	 * @param charsetName the charset name
	 */
	private void setEncoding(String charsetName) {
		try {
			charset = Charset.forName(charsetName);
		} catch(UnsupportedCharsetException ex) {
			String msg = "The character set name is not a legal name.";
			LOGGER.error(msg, ex);
			throw new UnsupportedCharsetException(msg);
		}		
	}	
	
	private void createFieldMap() {
		map = new HashMap<String, Integer>();
		int fieldIndex = 1;
		
		for (FieldDescription field : adapter.getFields()) {
			if (!map.containsKey(field.getName())) {
				map.put(field.getName(), fieldIndex);
			}
			
			++fieldIndex;
		}
	}
}
