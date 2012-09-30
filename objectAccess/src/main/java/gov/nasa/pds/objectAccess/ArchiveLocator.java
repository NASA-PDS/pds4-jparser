package gov.nasa.pds.objectAccess;

import java.io.FileReader;

/**  Utility class for managing files. */
public class ArchiveLocator {
	static final String sep = System.getProperty("file.separator");

	public static String resolveDataItemLocation(String root, String relativeFilename) throws Exception {
		String srFile = root + "/"+ relativeFilename;
		String fn = srFile.replace('\\', sep.charAt(0));
		return verifyFileExistsOnPlatform(fn);
	}

	public static String getSubpath(String relativeFilename) throws Exception {
		String normalizedFn = relativeFilename.replace('\\', sep.charAt(0));
		int idx = normalizedFn.lastIndexOf(sep.charAt(0));
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
