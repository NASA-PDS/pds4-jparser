package gov.nasa.pds.objectAccess.example;

import static org.testng.Assert.assertEquals;
import gov.nasa.pds.objectAccess.ParseException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.testng.annotations.Test;

public class TestGroupFieldWithRepetitions {

	@Test
	public void testGroupFieldWithRepetitions() throws ParseException, IOException {
		File tempOutputFile = File.createTempFile("testPDS339", ".csv");
		String[] args = {
				"-c",
				"-o",
				tempOutputFile.getAbsolutePath(),
				"src/test/resources/pds339/data1.xml"
		};

		ExtractTable.main(args);

		// 56 fields + two groups of 4 and 1414 repetitions.
		checkCSV(new BufferedReader(new FileReader(tempOutputFile)), 72, 56 + 4*1 + 1414*1);
	}

	/**
	 * Check that a CSV file has proper number of records and fields.
	 *
	 * @param reader a reader for the file data
	 * @param records the number of expected records
	 * @param fields the number of expected fields in each record
	 * @throws IOException
	 */
	private void checkCSV(BufferedReader reader, int records, int fields) throws IOException {
		int actualRecords = 0;
		String line;

		while ((line = reader.readLine()) != null) {
			++actualRecords;

			// Check field count on all but header record, so that we don't have to
			// deal with quoting.
			if (actualRecords > 1) {
				int actualFields = countFields(line);
				assertEquals(actualFields, fields, "Record " + actualRecords + " field count does not match expected");
			}
		}

		assertEquals(actualRecords-1, records, "Number of data records does not match expected");
	}

	private int countFields(String line) {
		int commaCount = 0;
		int commaLocation = line.indexOf(',');

		while (commaLocation > 0) {
			++commaCount;
			commaLocation = line.indexOf(',', commaLocation+1);
		}

		return commaCount + 1;
	}

}
