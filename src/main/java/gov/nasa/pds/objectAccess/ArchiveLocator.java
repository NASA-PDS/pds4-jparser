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
package gov.nasa.pds.objectAccess;

import java.io.FileReader;

/**  Utility class for managing files. */
class ArchiveLocator {

	static final String SEP = System.getProperty("file.separator");

	public static String resolveDataItemLocation(String root, String relativeFilename) throws Exception {
		String srFile = root + "/"+ relativeFilename;
		String fn = srFile.replace('\\', SEP.charAt(0));
		return verifyFileExistsOnPlatform(fn);
	}

	public static String getSubpath(String relativeFilename) throws Exception {
		String normalizedFn = relativeFilename.replace('\\', SEP.charAt(0));
		int idx = normalizedFn.lastIndexOf(SEP.charAt(0));
		return idx < 0 ? null : relativeFilename.substring(0, idx);
	}

	public static String verifyFileExistsOnPlatform(String fname) throws Exception {
		try {
			new FileReader(fname);
		} catch (Exception e) {
			System.out.println("File does not exist: " + fname);
			 throw new Exception(e); //FileNotFoundException would need to be serialized
		}
		return fname;
	}

}
