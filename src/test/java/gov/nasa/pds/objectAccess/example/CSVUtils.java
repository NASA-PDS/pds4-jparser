// Copyright 2019, California Institute of Technology ("Caltech").
// U.S. Government sponsorship acknowledged.
//
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// * Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
// * Redistributions must reproduce the above copyright notice, this list of
// conditions and the following disclaimer in the documentation and/or other
// materials provided with the distribution.
// * Neither the name of Caltech nor its operating division, the Jet Propulsion
// Laboratory, nor the names of its contributors may be used to endorse or
// promote products derived from this software without specific prior written
// permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

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
