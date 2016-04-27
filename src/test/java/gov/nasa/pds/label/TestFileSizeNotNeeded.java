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
package gov.nasa.pds.label;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import gov.nasa.pds.label.object.ArrayObject;
import gov.nasa.pds.label.object.DataObject;
import gov.nasa.pds.label.object.GenericObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.testng.annotations.Test;

public class TestFileSizeNotNeeded {

	/**
	 * Tests that the file_size attribute is not needed in a label
	 * to recognize tables declared in the label. This tests issue
	 * PDS-340 (https://oodt.jpl.nasa.gov/jira/browse/PDS-340?filter=11220).
	 *
	 * @throws Exception if there is a problem
	 */
	@Test
	public void testFileSizeNotNeeded() throws Exception {
		createDataFile("src/test/resources/mvn_lpw.cdf", 230296);
		Label label = Label.open(new File("src/test/resources/mvn_lpw.xml"));
		List<DataObject> objects = label.getObjects();
		assertEquals(objects.size(), 5); // Header, Array_2D, Array_1D, Array_2D, Array_1D

		assertTrue(objects.get(0) instanceof GenericObject);
		checkArray(objects.get(1), 2);
		checkArray(objects.get(2), 1);
		checkArray(objects.get(3), 2);
		checkArray(objects.get(4), 1);
	}

	/**
	 * Tests that a relative path in the current directory
	 * does not cause a problem. The test is identical to
	 * {@link #testFileSizeNotNeeded()}, but copies the
	 * label file to a temporary file in the current directory,
	 * first.
	 *
	 * @throws Exception if there is a problem
	 */
	@Test
	public void testEmptyParentPath() throws Exception {
		File tempLabel = File.createTempFile("test", ".xml", new File("."));
		tempLabel.deleteOnExit();
		copyFile(new File("src/test/resources/mvn_lpw.xml"), tempLabel);
		createDataFile("mvn_lpw.cdf", 230296);

		Label label = Label.open(new File(tempLabel.getName()));
		List<DataObject> objects = label.getObjects();
		assertEquals(objects.size(), 5); // Header, Array_2D, Array_1D, Array_2D, Array_1D

		assertTrue(objects.get(0) instanceof GenericObject);
		checkArray(objects.get(1), 2);
		checkArray(objects.get(2), 1);
		checkArray(objects.get(3), 2);
		checkArray(objects.get(4), 1);
	}

	private void copyFile(File src, File dest) throws IOException {
		FileInputStream in = null;
		FileOutputStream out = null;

		try {
			in = new FileInputStream(src);
			out = new FileOutputStream(dest);
			byte[] buf = new byte[0x1000];

			for (;;) {
				int nRead = in.read(buf);
				if (nRead <= 0) {
					break;
				}

				out.write(buf, 0, nRead);
			}
		} finally {
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.close();
			}
		}
	}

	private void checkArray(DataObject obj, int numAxes) {
		assertTrue(obj instanceof ArrayObject);

		ArrayObject array = (ArrayObject) obj;
		assertEquals(array.getAxes(), numAxes);
	}

	private void createDataFile(String path, long size) throws IOException {
		File f = new File(path);
		f.deleteOnExit();
		OutputStream out = new FileOutputStream(f);
		try {
			byte[] buf = new byte[65536];
			while (size > 0) {
				int chunkSize = (size > buf.length ? buf.length : (int) size);
				out.write(buf, 0, chunkSize);
				size -= chunkSize;
			}
		} finally {
			out.close();
		}
	}

}
