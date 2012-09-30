package gov.nasa.pds.objectAccess;

import gov.nasa.pds.objectAccess.table.AdapterFactory;
import gov.nasa.pds.objectAccess.table.FieldDescription;
import gov.nasa.pds.objectAccess.table.TableAdapter;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The <code>TableReader</code> class defines methods for reading table records.
 * 
 * @author psarram
 */
public class TableReader {	
	private static final Logger LOGGER = LoggerFactory.getLogger(TableReader.class);

	private TableAdapter adapter;
	private long offset;
	private int currentRow = 0;	
	private TableRecord record = null;
	private ByteWiseFileAccessor accessor = null;
	private Map<String, Integer> map = new HashMap<String, Integer>();
	
	/**
	 * Constructs a <code>TableReader</code> instance for reading records from the data file associated with the table object.
	 * The valid table objects are <code>TableCharacter</code>, <code>TableBinary</code> and <code>TableDelimited</code>.
	 * 
	 * @param table    a table object
	 * @param dataFile an input data file
	 * @throws Exception
	 */
	public TableReader(Object table, File dataFile) throws Exception {
		adapter = AdapterFactory.INSTANCE.getTableAdapter(table);
		offset = adapter.getOffset();
		
		// Open reader for reading
		this.accessor = new ByteWiseFileAccessor(dataFile, offset, adapter.getRecordLength(), adapter.getRecordCount());

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
	public TableRecord readNext() {		
		currentRow++;				
		if (currentRow > adapter.getRecordCount()) {
			return null;
		}	
		
		return getTableRecord();
	}
	
	/**
	 * Gets access to the table record at the given index.
	 *  
	 * @param index the record index (1-relative)
	 * @return an instance of <code>TableRecord</code>
	 * @throws IllegalArgumentException If index is greater than the record number
	 */
	public TableRecord getRecord(int index) throws IllegalArgumentException {
		if (index > adapter.getRecordCount()) {			
			String msg = "The index is greater than the total records.";
			LOGGER.error(msg);
			throw new IllegalArgumentException(msg);
		}
		
		currentRow = index;
		return getTableRecord();	
	}
	
	private TableRecord getTableRecord() {
		byte[] recordBytes = accessor.readRecordBytes(currentRow, 0, adapter.getRecordLength());
		if (record != null) {
			record.setRecordBytes(recordBytes);
		} else {
			record = new TableRecord(recordBytes, map, adapter.getFields());
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
