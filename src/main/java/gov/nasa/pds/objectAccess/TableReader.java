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
import gov.nasa.pds.objectAccess.table.TableAdapter;
import gov.nasa.pds.objectAccess.table.TableBinaryAdapter;
import gov.nasa.pds.objectAccess.table.TableDelimitedAdapter;
import gov.nasa.pds.objectAccess.table.TableCharacterAdapter;
import gov.nasa.pds.objectAccess.utility.Utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.net.URLConnection;
import java.io.RandomAccessFile;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;

/**
 * The <code>TableReader</code> class defines methods for reading table records.
 */
public class TableReader {
	private static final Logger LOGGER = LoggerFactory.getLogger(TableReader.class);

	private TableAdapter adapter;
	private long offset;
	private int currentRow = 0;
	private TableRecord record = null;
	protected ByteWiseFileAccessor accessor = null;
	private Map<String, Integer> map = new HashMap<String, Integer>();
	private CSVReader csvReader = null;
	private List<String[]> delimitedRecordList;
	private BufferedReader bufferedReader = null;
	private InputStream inputStream = null;
	private long recordSize = 0;
	private char delimitedChar = ',';

	public TableReader(Object table, File dataFile) throws Exception {
	  this(table, dataFile.toURI().toURL());
	}
	
	 /**
   * Constructs a <code>TableReader</code> instance for reading records from a
   * data file associated with a table object.
   *
   * @param table a table object
   * @param dataFile an input data file
   *
   * @throws NullPointerException if table offset is null
   */
  public TableReader(Object table, URL dataFile) throws Exception {
    this(table, dataFile, true);
  }
	
  public TableReader(Object table, URL dataFile, boolean checkSize) throws Exception {
    this(table, dataFile, checkSize, false);
  }

  public TableReader(Object table, URL dataFile, boolean checkSize, boolean readEntireFile) throws Exception {
    this(table, dataFile, checkSize, readEntireFile, false);
  }
  
	/**
	 * Constructs a <code>TableReader</code> instance for reading records from a
	 * data file associated with a table object.
	 *
	 * @param table a table object
	 * @param dataFile an input data file
   * @param checkSize check that the size of the data file is equal to the 
   * size of the table (length * records) + offset.
	 * @param readEntireFile flag to read an entire file
	 * @param keepQuotationsFlag flag to keep the starting/ending quotes 
	 *
	 * @throws NullPointerException if table offset is null
	 */
	public TableReader(Object table, URL dataFile, boolean checkSize, 
	    boolean readEntireFile, boolean keepQuotationsFlag) throws Exception {
		adapter = AdapterFactory.INSTANCE.getTableAdapter(table);

        LOGGER.debug("TableReader:dataFile {}",dataFile);

		try {
			offset = adapter.getOffset();
		} catch (NullPointerException ex) {
			LOGGER.error("The table offset cannot be null.");
			throw ex;
		}
		if (adapter instanceof TableDelimitedAdapter) {
          LOGGER.debug("TableReader:instanceof TableDelimitedAdapter: {},{}",dataFile,adapter.getClass().getSimpleName());
		  TableDelimitedAdapter tda = (TableDelimitedAdapter) adapter;
		  this.inputStream = Utility.openConnection(dataFile.openConnection());
		  this.inputStream.skip(offset);
		  this.inputStream.mark(0);
		  bufferedReader = new BufferedReader(new InputStreamReader(this.inputStream, "US-ASCII"));
		  accessor = new ByteWiseFileAccessor(dataFile, offset, -1);
		  this.delimitedChar = tda.getFieldDelimiter();
		  
          // Use the flag keepQuotationsFlag to tell the CSVParserBuilder that we wish to keep the starting/ending quotes.
		  CSVParser parser = new CSVParserBuilder().withSeparator(this.delimitedChar).withKeepQuotations(keepQuotationsFlag).build();
		  this.csvReader = new CSVReaderBuilder(bufferedReader).withCSVParser(parser).build();
		} else {		
          LOGGER.debug("TableReader:else instanceof TableDelimitedAdapter: {},{}",dataFile,adapter.getClass().getSimpleName());
		  if (readEntireFile) {
		    accessor = new ByteWiseFileAccessor(dataFile, offset, adapter.getRecordLength());    
		  } else {
		    accessor = new ByteWiseFileAccessor(dataFile, offset, adapter.getRecordLength(),
			    adapter.getRecordCount(), checkSize);
		  }
		}
		createFieldMap();
	}
	
	public TableAdapter getAdapter() {
	  return this.adapter;
	}
	
	/**
	 * Gets the field descriptions for fields in the table.
	 *
	 * @return an array of field descriptions
	 */
	public FieldDescription[] getFields() {
		return adapter.getFields();
	}

	/**
	 * 
	 * @return the field map.
	 */
	public Map<String, Integer> getFieldMap() {
	  return map;
	}
	
	/**
	 * Reads the next record from the data file.
	 *
	 * @return the next record, or null if no further records.
	 * @throws CsvValidationException 
	 */
	public TableRecord readNext() throws IOException, CsvValidationException {
		currentRow++;
		if (currentRow > adapter.getRecordCount()) {
			return null;
		}
		
		return getTableRecord();
	}

    /**
     * Gets access to the table record given the index. The current row is set to
     * this index, thus, subsequent call to readNext() gets the next record from
     * this position.
     *
     * @param index the record index (1-relative)
     * @return an instance of <code>TableRecord</code>
     * @throws IllegalArgumentException if index is greater than the record number
     * @throws CsvValidationException 
     */
	public TableRecord getRecord(int index) throws IllegalArgumentException, IOException, CsvValidationException {
	    return(getRecord(index, false));
    }
	
	/**
	 * Gets access to the table record given the index. The current row is set to
	 * this index, thus, subsequent call to readNext() gets the next record from
	 * this position.
	 *
	 * @param index the record index (1-relative)
	 * @param keepQuotationsFlag flag to keep the starting/ending quotes or not.
	 * @return an instance of <code>TableRecord</code>
	 * @throws IllegalArgumentException if index is greater than the record number
	 * @throws CsvValidationException 
	 */
	public TableRecord getRecord(int index, boolean keepQuotationsFlag) throws IllegalArgumentException, IOException, CsvValidationException {
		int recordCount = adapter.getRecordCount();		
		if (index < 1 || index > recordCount) {
			String msg = "The index is out of range 1 - " + recordCount;
			LOGGER.error(msg);
			throw new IllegalArgumentException(msg);
		}
		// issue 189  - to handle large delimited file
		// instread of using the array list, re-position to the line after reset the inputstream
		if (currentRow>index) {
			this.inputStream.reset();
			this.bufferedReader = new BufferedReader(new InputStreamReader(this.inputStream, "US-ASCII"));
			// skip 'index-1' lines
			// check this again
			for (int i = 0; i < (index-1); i++) {
                this.bufferedReader.readLine();
            }

			CSVParser parser = new CSVParserBuilder().withSeparator(this.delimitedChar).withKeepQuotations(keepQuotationsFlag).build();
	        this.csvReader = new CSVReaderBuilder(bufferedReader).withCSVParser(parser).build();	
		}
		currentRow = index;	
		return getTableRecord();
	}

	private TableRecord getTableRecord() throws IOException, CsvValidationException {
        // DEBUG statements can be time consuming.  Should be uncommented by developer only.
		if (adapter instanceof TableDelimitedAdapter) {			
			//String[] recordValue = delimitedRecordList.get(currentRow-1);

			String[] recordValue = this.csvReader.readNext();
            //LOGGER.debug("getTableRecord: RECORDVALUE,record  RECORDVALUE={}, record={}",recordValue,record);
            //LOGGER.debug("getTableRecord: recordValue.length,currentRow {},{}",recordValue.length,currentRow);
			if (recordValue!=null && (recordValue.length != adapter.getFieldCount())) {
				throw new IOException("Record " + currentRow + " has wrong number of fields "
						+ "(expected " + adapter.getFieldCount() + ", got " + recordValue.length + ")"
						);
			}
			if (record != null) {
				((DelimitedTableRecord) record).setRecordValue(recordValue);
			} else {
				record = new DelimitedTableRecord(map, adapter.getFieldCount(), recordValue);
			} 
		} else {
            //LOGGER.debug("getTableRecord: currentRow,adapter.getRecordLength() {},{}",currentRow,adapter.getRecordLength());
			byte[] recordValue = accessor.readRecordBytes(currentRow, 0, adapter.getRecordLength());
            //LOGGER.debug("getTableRecord: recordValue.length,currentRow {},{}",recordValue.length,currentRow);
            //System.out.println("getTableRecord:early#exit#0001");
            //System.exit(0);
			if (record != null) {
				((FixedTableRecord) record).setRecordValue(recordValue);
			} else {
				record = new FixedTableRecord(recordValue, map, adapter.getFields());
			}
		}
		return record;
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
	
	/**
	 * Sets the current row.
	 * 
	 * @param row The row to set.
	 */
	public void setCurrentRow(int row) {
	  this.currentRow = row;
	}
	
	/**
	 * 
	 * @return the current row.
	 */
	public int getCurrentRow() {
	  return this.currentRow;
	}
	
	public ByteWiseFileAccessor getAccessor() {
	  return this.accessor;
	}

    private long parseBufferForLineCount(URL dataFile, byte [] bufferAsBytes) throws Exception {
        // Given a byte array, read through as if reading through a smaller file and count the lines.
        InputStream inputStream = new ByteArrayInputStream(bufferAsBytes);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        long linesInBuffer = 0;
        try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
            while (reader.readLine() != null) linesInBuffer++;
        } catch (IOException e) {
            LOGGER.error("Cannot count lines from file {} in parseBufferForLineCount() function",dataFile);
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
        LOGGER.debug("parseBufferForLineCount:linesInBuffer {}",linesInBuffer);
        return linesInBuffer;
    }

    private long countRecordsForTextTable(URL dataFile) throws Exception {
        // Count the number of records for a text file of any size.   The traditional BufferReader cannot handle files larger than 2GB.
        long numRecordsForTextTable = 0;

        // Use RandomAccessFile to get filesize larger than 2gb
        File aFile = new File(dataFile.toURI());
        RandomAccessFile raf = new RandomAccessFile(aFile, "r");
        raf.seek(offset); // Move the pointer to the offset first.

        FileChannel inChannel = raf.getChannel();
        int bufferSize = 1024*128;
        if (bufferSize > inChannel.size()) {
            bufferSize = (int) inChannel.size();
        }
        ByteBuffer buff = ByteBuffer.allocate(bufferSize);

        byte [] bufferAsBytes = null;
        while (inChannel.read(buff) > 0) {
            ((Buffer) buff).position(0); // Must point the pointer to the beginning of buff in order to access the elements in the array.
            bufferAsBytes = buff.array();  // Get the underlying byte array in ByteBuffer.

             // With the smaller buffer, we can safely read through the buffer for all lines and count them.
             numRecordsForTextTable = numRecordsForTextTable + this.parseBufferForLineCount(dataFile, bufferAsBytes);

             buff.clear();
        }
        raf.close();
        return(numRecordsForTextTable);
    }

    private long countRecordsForTableAdapterType(URL dataFile, long offset) throws Exception {
        // For TableCharacter, we have to rely on the size of each record and the file size to calculate the number
        // of records thus not having to read through the entire file.

        LOGGER.debug("countRecordsForTableAdapterType:dataFile,offset {},{}",dataFile,offset);
        LOGGER.debug("countRecordsForTableAdapterType:dataFile,adapter.getRecordLength() {},{}",dataFile,adapter.getRecordLength());

        long numRecords = -1;

        // Do a sanity check if the record size is not known or zero.  Not all labels provide the record size, for example comma separated files.
        // If the record size is not known or zero, unfortunately we must read through the file and count the records.

        if (adapter.getRecordLength() <= 0) {
            numRecords = this.countRecordsForTextTable(dataFile);
            LOGGER.debug("countRecordsForTableAdapterType:numRecords {}",numRecords);
            return(numRecords);
        }

        LOGGER.debug("countRecordsForTableAdapterType:numRecords:initial {}",numRecords);
		File aFile = new File(dataFile.toURI());
		RandomAccessFile raf = new RandomAccessFile(aFile, "r");
		raf.seek(offset);
	
		FileChannel inChannel = raf.getChannel();
		long fileSize = inChannel.size();  // The value of fileSize is now the rest content of the file after skipping any offset.
		raf.close();

        // The number of records is the size of the file divided by the record length
        numRecords = fileSize/adapter.getRecordLength();

        LOGGER.debug("countRecordsForTableAdapterType:numRecords {}",numRecords);

        return(numRecords);
    }
	
	/**
	 * @return the size of record (i.e. number of lines)
	 */
	public long getRecordSize(URL dataFile, Object table) throws Exception {
		adapter = AdapterFactory.INSTANCE.getTableAdapter(table);
		InputStream is = Utility.openConnection(dataFile.openConnection());

        LOGGER.debug("getRecordSize:adapter {}",adapter);
		try {
			offset = adapter.getOffset();
		} catch (NullPointerException ex) {
			LOGGER.error("The table offset cannot be null.");
			throw ex;
		}
		if (adapter instanceof TableDelimitedAdapter) {			
            LOGGER.debug("getRecordSize:adapter instanceof TableDelimitedAdapter");
            // The advantage of the new function countRecordsForTableAdapterType() is it does not
            // re-read the file but merely calculate how many records fit into the file given the record length.

            this.recordSize = this.countRecordsForTableAdapterType(dataFile, offset);

		} else {
            LOGGER.debug("getRecordSize:adapter instanceof TableDelimitedAdapter else");
			if (adapter instanceof TableBinaryAdapter)
				offset = 0;
			
			is.skip(offset);
			bufferedReader = new BufferedReader(new InputStreamReader(is, "US-ASCII"));
			if (adapter instanceof TableCharacterAdapter) {
                LOGGER.debug("getRecordSize:adapter instanceof TableCharacterAdapter");

                // The advantage of the new function countRecordsForTableAdapterType() is it does not
                // re-read the file but merely calculate how many records fit into the file given the record length.

                this.recordSize = this.countRecordsForTableAdapterType(dataFile, offset);

			}
			else {
                LOGGER.debug("getRecordSize:adapter instanceof TableCharacterAdapter else");

				this.recordSize = is.available();	
			
				// need to change to get filesize larger than 2gb
				File aFile = new File(dataFile.toURI());
				RandomAccessFile raf = new RandomAccessFile(aFile, "r");
				raf.seek(offset);
	
				FileChannel inChannel = raf.getChannel();
				long fileSize = inChannel.size();
				this.recordSize = fileSize;
				raf.close();
			}
		}			
        LOGGER.debug("getRecordSize:this.recordSize {}",this.recordSize);
        LOGGER.debug("getRecordSize:adapter.getRecordLength() {}",adapter.getRecordLength());

		return this.recordSize;
	}
	
	public long getOffset() {
		return this.offset;
	}
}
