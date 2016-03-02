package gov.nasa.pds.objectAccess.example;

import gov.nasa.pds.objectAccess.ParseException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.testng.annotations.Test;

/**
 * Implements tests for PDS-339.
 */
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
		CSVUtils.checkCSV(new BufferedReader(new FileReader(tempOutputFile)), 72, 56 + 4*1 + 1414*1);
	}

}
