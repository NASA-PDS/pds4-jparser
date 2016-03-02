package gov.nasa.pds.objectAccess.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.testng.annotations.Test;

/**
 * Performs tests to verify that issue PDS-368 is fixed.
 */
public class TestPDS368 {

	@Test
	public void testExtractTable() throws IOException {
		File tempOutputFile = File.createTempFile("testPDS68", ".csv");
		String[] args = {
				"-c",
				"-o",
				tempOutputFile.getAbsolutePath(),
				"src/test/resources/pds368/maven_events_20140801.xml"
		};

		ExtractTable.main(args);

		// 56 fields + two groups of 4 and 1414 repetitions.
		CSVUtils.checkCSV(new BufferedReader(new FileReader(tempOutputFile)), 7162, 9);
	}

}
