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
