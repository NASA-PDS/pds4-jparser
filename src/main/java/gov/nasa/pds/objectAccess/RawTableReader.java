package gov.nasa.pds.objectAccess;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.opencsv.exceptions.CsvValidationException;
import gov.nasa.pds.label.object.DataObjectLocation;
import gov.nasa.pds.label.object.RecordLocation;
import gov.nasa.pds.label.object.TableRecord;

/**
 * Table reader that provides the capability to read a table line by line rather than record by
 * record, which is more strict as it relies on the label metadata.
 *
 * <p><b>Important:</b> {@link #readNextLine()} uses an internal 8 KB read buffer that advances
 * {@code accessor.curPosition} ahead of what has been logically consumed. Do not interleave
 * {@code readNextLine()} calls with direct accessor methods ({@code readByte()}, {@code mark()},
 * {@code reset()}) on the same instance — the positions will desync. {@link #readNextFixedLine()}
 * is safe to use independently because it uses row-offset-based {@code readRecordBytes()}.</p>
 *
 * @author mcayanan
 *
 */
public class RawTableReader extends TableReader {
  private static final Logger LOG = LoggerFactory.getLogger(RawTableReader.class);

  /** The data file. */
  private URL dataFile;

  /** The label associated with the table. */
  private URL label;

  /**
   * EOF sentinel: set to -1 when the end of data has been reached.
   * Only used as a state flag (SOL or -1) in the buffered implementation.
   */
  private int nextCh = SOL;

  private static final int SOL = -10;

  private static final int READ_BUFFER_SIZE = 8192;
  private byte[] readBuffer = new byte[READ_BUFFER_SIZE];
  private int bufferPos = 0;
  private int bufferLimit = 0;

  /**
   * Constructor.
   * 
   * @param table The table object.
   * @param dataFile The data file.
   * @param label The label.
   * @param location The location of the table within the metadata definition
   * @param readEntireFile Set to 'true' to read in entire data file.
   * @throws Exception If table offset is null.
   */
  public RawTableReader(Object table, URL dataFile, URL label, DataObjectLocation location,
      boolean readEntireFile) throws Exception {
    super(table, dataFile, false, readEntireFile);
    this.dataFile = dataFile;
    this.label = label;
  }

  /**
   * Constructor.
   * 
   * @param table The table object.
   * @param dataFile The data file.
   * @param label The label.
   * @param location The location of the table within the metadata definition
   * @param readEntireFile Set to 'true' to read in entire data file.
   * @param keepQuotationsFlag Flag to optionally preserve the leading and trailing quotes.
   * @throws Exception If table offset is null.
   */
  public RawTableReader(Object table, URL dataFile, URL label, DataObjectLocation location,
      boolean readEntireFile, boolean keepQuotationsFlag) throws Exception {
    super(table, dataFile, location, false, readEntireFile, keepQuotationsFlag);
    this.dataFile = dataFile;
    this.label = label;
  }

  /**
   * Constructor.
   * 
   * @param table The table object.
   * @param dataFile The data file.
   * @param label The label.
   * @param location The location of the table within the metadata definition
   * @param readEntireFile Set to 'true' to read in entire data file.
   * @param keepQuotationsFlag Flag to optionally preserve the leading and trailing quotes.
   * @param fileChannel file channel stream used for ByteWideFileAccessor
   * @param inputStream input stream of the file used for CSVReader
   * @throws Exception If table offset is null.
   */
  public RawTableReader(Object table, URL dataFile, URL label, DataObjectLocation location,
      boolean readEntireFile, boolean keepQuotationsFlag, RandomAccessFile raf,
      InputStream inputStream) throws Exception {
    super(table, dataFile, location, false, readEntireFile, keepQuotationsFlag, raf, inputStream);
    this.dataFile = dataFile;
    this.label = label;
  }

  public RawTableReader(Object table, URL dataFile, DataObjectLocation location, boolean checkSize)
      throws InvalidTableException, Exception {
    this(table, dataFile, null, location, checkSize, true);
  }

  /**
   * Previews the next line in the data file.
   * 
   * @return the next line, or null if no further lines.
   * 
   * @throws IOException
   */
  public String readNextLine() throws IOException {
    if (nextCh == -1) {
      LOG.debug("readNextLine:nextCh == -1");
      return null;
    }

    StringBuilder lineBuffer = new StringBuilder();

    boolean newLine = false;
    boolean eof = false;

    while (!newLine && !eof) {
      // Refill buffer if empty
      if (bufferPos >= bufferLimit) {
        bufferLimit = accessor.readBytes(readBuffer, 0, READ_BUFFER_SIZE);
        bufferPos = 0;
        if (bufferLimit <= 0) {
          eof = true;
          nextCh = -1;
          break;
        }
      }

      // Scan buffer for line delimiter
      int scanStart = bufferPos;
      while (bufferPos < bufferLimit) {
        byte b = readBuffer[bufferPos];
        if (b == '\r') {
          // Append everything before the \r
          lineBuffer.append(new String(readBuffer, scanStart, bufferPos - scanStart, StandardCharsets.UTF_8));
          bufferPos++;
          // Check for \r\n
          if (bufferPos < bufferLimit) {
            if (readBuffer[bufferPos] == '\n') {
              lineBuffer.append("\r\n");
              bufferPos++;
            } else {
              lineBuffer.append("\r");
            }
          } else {
            // Need to read more to check for \n after \r
            bufferLimit = accessor.readBytes(readBuffer, 0, READ_BUFFER_SIZE);
            bufferPos = 0;
            if (bufferLimit > 0 && readBuffer[0] == '\n') {
              lineBuffer.append("\r\n");
              bufferPos = 1;
            } else if (bufferLimit <= 0) {
              lineBuffer.append("\r");
              nextCh = -1;
            } else {
              lineBuffer.append("\r");
            }
          }
          newLine = true;
          break;
        } else if (b == '\n') {
          lineBuffer.append(new String(readBuffer, scanStart, bufferPos - scanStart, StandardCharsets.UTF_8));
          lineBuffer.append("\n");
          bufferPos++;
          newLine = true;
          break;
        }
        bufferPos++;
      }

      // If we scanned to the end of the buffer without finding a newline, append what we have
      if (!newLine && !eof && bufferPos >= bufferLimit) {
        lineBuffer.append(new String(readBuffer, scanStart, bufferPos - scanStart, StandardCharsets.UTF_8));
      }
    }

    String line = null;
    if (lineBuffer.length() > 0) {
      line = lineBuffer.toString();
      setCurrentRow(getCurrentRow() + 1);
    }

    if (line != null) {
      LOG.debug("readNextLine:line:{},[{}]", line.length(), line);
    } else {
      LOG.debug("readNextLine:line is null");
    }

    return line;
  }

  /**
   * Previews the next fixed length line in the data file.
   * 
   * @return the next line, or null if no further lines.
   * 
   * @throws IOException
   */
  public String readNextFixedLine() throws IOException {
	byte[] line = null;
	int recordLength = this.getAdapter().getRecordLength();
	long next_row = this.getCurrentRow() + 1;

	if (0 <= this.accessor.getTotalBytesRead() - (next_row * recordLength)) {
      line = this.accessor.readRecordBytes(next_row, 0, recordLength);
      this.setCurrentRow(next_row);
    }
    return line == null ? null : new String(line, StandardCharsets.UTF_8);
  }

  /**
   * Converts the given line to a record.
   * 
   * @param line The line to convert.
   * @param row The row number to set.
   * 
   * @return A record.
   */
  public FixedTableRecord toRecord(String line, long row) {
    FixedTableRecord record = null;
    record = new FixedTableRecord(line.getBytes(), getFieldMap(), getFields());
    record.setLocation(new RecordLocation(label, dataFile, dataObjectLocation, row));
    return record;
  }

  /**
   * Reads the next record in the table.
   * 
   * @return a table record, with the location set.
   */
  @Override
  public TableRecord readNext() throws IOException {
    try {
      TableRecord record = super.readNext();
      if (record != null) {
        LOG.debug("Setting record location");
        record.setLocation(getLocation());
      }
      return record;
    } catch (CsvValidationException ex) {
      LOG.error("Function readNext() has failed");
      throw new IOException(ex.getMessage());
    }
  }

  /**
   * Gets a record in the table.
   * 
   * @param The index of the record.
   * 
   * @return a table record, with the location set.
   */
  @Override
  public TableRecord getRecord(long index, boolean keepQuotationsFlag)
      throws IllegalArgumentException, IOException {
    try {
      TableRecord record = super.getRecord(index, keepQuotationsFlag);
      record.setLocation(getLocation());
      return record;
    } catch (CsvValidationException ex) {
      LOG.error("Function getRecord has failed");
      throw new IOException(ex.getMessage());
    }
  }

  /**
   * 
   * @return the location of the record.
   */
  private RecordLocation getLocation() {
    return new RecordLocation(label, dataFile, dataObjectLocation, getCurrentRow());
  }
}
