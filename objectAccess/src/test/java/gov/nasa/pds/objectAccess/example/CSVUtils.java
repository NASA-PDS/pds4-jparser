package gov.nasa.pds.objectAccess.example;

import static org.testng.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Implements utilities for testing with CSV data files.
 */
public class CSVUtils {

	/**
	 * Check that a CSV file has proper number of records and fields.
	 *
	 * @param reader a reader for the file data
	 * @param records the number of expected records
	 * @param fields the number of expected fields in each record
	 * @throws IOException
	 */
	static void checkCSV(BufferedReader reader, int records, int fields) throws IOException {
		int actualRecords = 0;
		String line;

		while ((line = reader.readLine()) != null) {
			++actualRecords;

			// Check field count on all but header record, so that we don't have to
			// deal with quoting.
			if (actualRecords > 1) {
				int actualFields = CSVUtils.countFields(line);
				assertEquals(actualFields, fields, "Record " + actualRecords + " field count does not match expected");
			}
		}

		assertEquals(actualRecords-1, records, "Number of data records does not match expected");
	}

	private static int countFields(String line) {
		int commaCount = 0;
		int commaLocation = line.indexOf(',');
		boolean inQuotes = false;

		for (int i=0; i < line.length(); ++i) {
			if (!inQuotes && line.charAt(i)==',') {
				++commaCount;
			} else if (line.charAt(i)=='"') {
				inQuotes = !inQuotes;
			}
		}

		return commaCount + 1;
	}

}
