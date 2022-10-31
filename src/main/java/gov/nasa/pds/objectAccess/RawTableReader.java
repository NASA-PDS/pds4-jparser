package gov.nasa.pds.objectAccess;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
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
 * @author mcayanan
 *
 */
public class RawTableReader extends TableReader {
  private static final Logger LOG = LoggerFactory.getLogger(RawTableReader.class);

  /** The data file. */
  private URL dataFile;

  /** The label associated with the table. */
  private URL label;

  private int nextCh = SOL;

  private static final int SOL = -10;

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
    String line = null;
    int ch = (char) -10;
    StringBuilder lineBuffer = new StringBuilder();
    if (nextCh == -1) {
      line = null;
      LOG.debug("readNextLine:nextCh == -1");
    } else {
      boolean newLine = false;
      boolean eof = false;
      while (!newLine && !eof) {
        if (nextCh != -10) {
          lineBuffer.append((char) nextCh);
        }
        nextCh = -10;
        if (accessor.hasRemaining()) {
          ch = accessor.readByte();
          switch (ch) {
            case '\r':
              // check for double newline char
              if (accessor.hasRemaining()) {
                nextCh = accessor.readByte();
                if (nextCh == '\n') {
                  // double line found
                  lineBuffer.append("\r\n");
                  newLine = true;
                  nextCh = -10;
                } else {
                  lineBuffer.append("\r");
                  newLine = true;
                }
              } else {
                eof = true;
                nextCh = -1;
              }
              break;

            case '\n':
              lineBuffer.append("\n");
              newLine = true;
              break;

            case -1:
              eof = true;
              nextCh = -1;
              break;

            default:
              if (ch != -1) {
                lineBuffer.append((char) ch);
              }
          }
        } else {
          eof = true;
          nextCh = -1;
        }
      }
      if (lineBuffer.length() > 0) {
        line = lineBuffer.toString();
        setCurrentRow(getCurrentRow() + 1);
      }
    }

    if (line != null) {
      LOG.debug("readNextLine:line:{},[{}]", line.length(), line);
    } else {
      LOG.debug("readNextLine:line is null");
    }

    return line;
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
