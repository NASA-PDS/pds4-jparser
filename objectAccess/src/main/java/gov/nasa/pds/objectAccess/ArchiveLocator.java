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
