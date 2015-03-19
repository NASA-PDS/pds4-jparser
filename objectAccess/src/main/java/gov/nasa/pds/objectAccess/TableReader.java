package gov.nasa.pds.objectAccess;

import gov.nasa.pds.label.object.FieldDescription;
import gov.nasa.pds.label.object.TableRecord;
import gov.nasa.pds.objectAccess.table.AdapterFactory;
import gov.nasa.pds.objectAccess.table.TableAdapter;
import gov.nasa.pds.objectAccess.table.TableDelimitedAdapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVReader;

/**
 * The <code>TableReader</code> class defines methods for reading table records.
 */
public class TableReader {
	private static final Logger LOGGER = LoggerFactory.getLogger(TableReader.class);

	private TableAdapter adapter;
	private long offset;
	private int currentRow = 0;
	private TableRecord record = null;
	private ByteWiseFileAccessor accessor = null;
	private Map<String, Integer> map = new HashMap<String, Integer>();
	private CSVReader csvReader = null;
	private List<String[]> delimitedRecordList;

	/**
	 * Constructs a <code>TableReader</code> instance for reading records from a
	 * data file associated with a table object.
	 *
	 * @param table a table object
	 * @param dataFile an input data file
	 *
	 * @throws NullPointerException if table offset is null
	 */
	public TableReader(Object table, File dataFile) throws Exception {
		adapter = AdapterFactory.INSTANCE.getTableAdapter(table);
		try {
			offset = adapter.getOffset();
		} catch (NullPointerException ex) {
			LOGGER.error("The table offset cannot be null.");
			throw ex;
		}

		if (adapter instanceof TableDelimitedAdapter) {
			InputStream is = new FileInputStream(dataFile);
			is.skip(offset);
			BufferedReader buffer = new BufferedReader(new InputStreamReader(is, "US-ASCII"));
			csvReader = new CSVReader(buffer, ((TableDelimitedAdapter) adapter).getFieldDelimiter());
			delimitedRecordList = csvReader.readAll();
		} else {
			accessor = new ByteWiseFileAccessor(dataFile, offset, adapter.getRecordLength(), adapter.getRecordCount());
		}

		createFieldMap();
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
	 * Reads the next record from the data file.
	 *
	 * @return the next record, or null if no further records.
	 */
	public TableRecord readNext() throws IOException {
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
	 */
	public TableRecord getRecord(int index) throws IllegalArgumentException, IOException {
		int recordCount = adapter.getRecordCount();
		if (index < 1 || index > recordCount) {
			String msg = "The index is out of range 1 - " + recordCount;
			LOGGER.error(msg);
			throw new IllegalArgumentException(msg);
		}

		currentRow = index;
		return getTableRecord();
	}

	private TableRecord getTableRecord() throws IOException {
		if (adapter instanceof TableDelimitedAdapter) {
			String[] recordValue = delimitedRecordList.get(currentRow-1);
			if (recordValue.length != adapter.getFieldCount()) {
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
			byte[] recordValue = accessor.readRecordBytes(currentRow, 0, adapter.getRecordLength());
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

}
