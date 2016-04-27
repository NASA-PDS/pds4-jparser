// Copyright 2006-2016, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
// Any commercial use must be negotiated with the Office of Technology Transfer
// at the California Institute of Technology.
//
// This software is subject to U. S. export control laws and regulations
// (22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software
// is subject to U.S. export control laws and regulations, the recipient has
// the responsibility to obtain export licenses or other export authority as
// may be required before exporting such information to foreign countries or
// providing access to foreign nationals.
//
// $Id$
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
