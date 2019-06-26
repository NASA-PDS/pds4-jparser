package gov.nasa.arc.pds.tools.util;

import gov.nasa.arc.pds.tools.container.FileMirror;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;
import java.util.Map.Entry;
import java.util.regex.Pattern;

/**
 * A helper class for File related functionality.
 * 
 * @author jagander
 * @version $Revision: $
 * 
 */
public class FileUtils {

	/**
	 * The number of bytes in a kilobyte.
	 */
	public static final long ONE_KB = 1024;

	/**
	 * The number of bytes in a megabyte.
	 */
	public static final long ONE_MB = ONE_KB * ONE_KB;

	/**
	 * The number of bytes in a gigabyte.
	 */
	public static final long ONE_GB = ONE_KB * ONE_MB;

	final static double ONE_TB = 1024 * ONE_GB;

	final static double ONE_PB = 1024 * ONE_TB;

	public final static String OPPOSITE_SEP_CHAR = File.separator.equals("/") ? "\\\\" //$NON-NLS-1$ //$NON-NLS-2$
			: "/"; //$NON-NLS-1$

	public final static String REGEX_SAFE_SEP = File.separator.equals("/") ? "/" //$NON-NLS-1$ //$NON-NLS-2$
			: "\\\\"; //$NON-NLS-1$

	public static boolean containsFile(final File sourceFile,
			final String searchName) {
		final File testFile = new File(sourceFile, searchName);
		return exists(testFile);
	}

	public static List<File> getFiles(final File sourceFile) {
		return getFiles(sourceFile, null, true);
	}

	public static List<File> getFiles(final File sourceFile,
			final String searchPattern) {
		return getFiles(sourceFile, searchPattern, true);
	}

	public static List<File> getFiles(final File sourceFile,
			final String regex, final boolean recursive) {
		Pattern searchPattern = null;
		if (regex != null) {
			searchPattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		}
		return getFilesByPattern(sourceFile, searchPattern, recursive);
	}

	public static List<File> getFilesByPattern(final File sourceFile,
			final Pattern searchPattern, final boolean recursive) {
		if (!sourceFile.exists()) {
			throw new RuntimeException("File \"" + sourceFile.toString()
					+ "\" was not found.");
		}
		final Stack<File> dirStack = new Stack<File>();

		final List<File> foundFiles = new ArrayList<File>();

		final boolean doSearch = searchPattern != null;

		if (!isSourceControl(sourceFile)) {
			if (sourceFile.isDirectory()) {
				dirStack.push(sourceFile);
			}
			if (doSearch) {
				if (searchPattern.matcher(sourceFile.getName()).matches()) {
					foundFiles.add(sourceFile);
				}
			} else {
				foundFiles.add(sourceFile);
			}
		}

		while (!dirStack.empty()) {
			final File curDir = dirStack.pop();
			final File[] tempFiles = curDir.listFiles();
			if (tempFiles != null) {
				for (final File curFile : tempFiles) {
					if (!isSourceControl(curFile)) {
						if (recursive && curFile.isDirectory()) {
							dirStack.push(curFile);
						}
						if (doSearch) {
							if (searchPattern.matcher(curFile.getName())
									.matches()) {
								foundFiles.add(curFile);
							}
						} else {
							foundFiles.add(curFile);
						}
					}
				}
			}
		}
		return foundFiles;
	}

	public static File getTopFileByPattern(final File sourceFile,
			final String regex, final Integer maxDepth) {
		final Pattern searchPattern = Pattern.compile(regex,
				Pattern.CASE_INSENSITIVE);
		return getTopFileByPattern(sourceFile, searchPattern, maxDepth);
	}

	// return first file that looks right up to a given depth
	public static File getTopFileByPattern(final File sourceFile,
			final Pattern searchPattern, final Integer maxDepth) {
		final Stack<File> dirStack = new Stack<File>();
		dirStack.push(sourceFile);

		int curDepth = 0;

		while (!dirStack.empty() && (maxDepth == null || curDepth < maxDepth)) {
			curDepth++;
			final File curDir = dirStack.pop();
			final File[] tempFiles = curDir.listFiles();
			if (tempFiles != null) {
				for (final File curFile : tempFiles) {
					if (searchPattern.matcher(curFile.getName()).matches()) {
						return curFile;
					} else if (curFile.isDirectory()) {
						dirStack.push(curFile);
					}
				}
			}
		}
		return null;

	}

	// get files from list that match regex
	public static List<File> getFiles(final List<File> sourceList,
			final String regex) {
		final List<File> matchingFiles = new ArrayList<File>();
		final Pattern searchPattern = Pattern.compile(regex,
				Pattern.CASE_INSENSITIVE);
		for (File curFile : sourceList) {
			if (searchPattern.matcher(curFile.getName()).matches()) {
				matchingFiles.add(curFile);
			}
		}
		return matchingFiles;
	}

	public static Map<Integer, File> getFileMap(final File sourceFile) {
		return getFileMap(sourceFile, null, true);
	}

	public static Map<Integer, File> getFileMap(final File sourceFile,
			final String searchPattern) {
		return getFileMap(sourceFile, searchPattern, true);
	}

	public static Map<Integer, File> getFileMap(final File sourceFile,
			final String regex, final boolean recursive) {
		Pattern searchPattern = null;
		if (regex != null) {
			searchPattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		}
		return getFileMapByPattern(sourceFile, searchPattern, recursive, true);
	}

	public static Map<Integer, File> getFileMapByPattern(final File sourceFile,
			final Pattern searchPattern, final boolean recursive,
			final boolean excludeSourceControlFiles) {
		if (!sourceFile.exists()) {
			throw new RuntimeException("File \"" + sourceFile.toString()
					+ "\" was not found.");
		}

		final Stack<File> dirStack = new Stack<File>();

		final Map<Integer, File> foundFiles = new HashMap<Integer, File>();

		final boolean doSearch = searchPattern != null;

		// check root folder - has to be recursive for sourcefile to add itself
		// as folder to search below
		conditionalAddFile(sourceFile, dirStack, foundFiles, searchPattern,
				doSearch, true, excludeSourceControlFiles);

		while (!dirStack.empty()) {
			final File curDir = dirStack.pop();
			final File[] tempFiles = curDir.listFiles();
			for (final File curFile : tempFiles) {
				conditionalAddFile(curFile, dirStack, foundFiles,
						searchPattern, doSearch, recursive,
						excludeSourceControlFiles);
			}
		}
		return foundFiles;
	}

	private static void conditionalAddFile(final File curFile,
			final Stack<File> dirStack, final Map<Integer, File> foundFiles,
			final Pattern searchPattern, final boolean doSearch,
			final boolean recursive, final boolean excludeSourceControlFiles) {
		// don't include subversion meta info
		if (recursive && curFile.isDirectory()) {
			if (!excludeSourceControlFiles || !isSourceControl(curFile)) {
				dirStack.push(curFile);
			}
		}
		if (doSearch) {
			final boolean matches = searchPattern.matcher(curFile.getName())
					.matches();
			if (matches) {
				if (!excludeSourceControlFiles || !isSourceControl(curFile)) {
					foundFiles.put(curFile.hashCode(), curFile);
				}
			}
		} else {
			if (!excludeSourceControlFiles || !isSourceControl(curFile)) {
				foundFiles.put(curFile.hashCode(), curFile);
			}
		}
	}

	// get files from list that match regex
	public static Map<Integer, File> getFileMap(
			final Map<Integer, File> sourceList, final String regex) {
		final Map<Integer, File> matchingFiles = new HashMap<Integer, File>();
		final Pattern searchPattern = Pattern.compile(regex,
				Pattern.CASE_INSENSITIVE);
		Iterator<Entry<Integer, File>> it = sourceList.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Integer, File> entry = it.next();
			final File file = entry.getValue();
			if (searchPattern.matcher(file.getName()).matches()) {
				matchingFiles.put(file.hashCode(), file);
			}
		}
		return matchingFiles;
	}

	public static String getContents(final File file) throws IOException {
		BufferedInputStream stream = new BufferedInputStream(
				new FileInputStream(file));
		byte[] buf = new byte[(int) file.length()];

		stream.read(buf);
		stream.close();

		final String contents = new String(buf, "utf-8"); //$NON-NLS-1$
		return contents;
	}

	public static File getBaseFile(final File file) {
		// if file and exists, return parent
		if (file.isFile()) {
			return file.getParentFile();
		}

		// if directory or doesn't exist and can't tell if directory, return
		// file
		if (file.isDirectory() || getExtension(file) == "") { //$NON-NLS-1$
			return file;
		}

		// doesn't exist but has non null extension
		return file.getParentFile();
	}

	public static File getValidParent(final File missingFile) {
		String curPath = missingFile.getAbsolutePath();
		while (curPath != null) {
			File curFile = new File(curPath);
			if (FileUtils.exists(curFile)) {
				return curFile;
			}
			curPath = curFile.getParent();
		}
		return null;
	}

	public static String getRelativePath(final File baseDirectory,
			final File targetFile) {
		// if no base, assume relative to root
		if (baseDirectory == null) {
			return targetFile.toString();
		}
		if (baseDirectory.equals(targetFile)) {
			return ""; //$NON-NLS-1$
		}
		final String basePath = baseDirectory.getAbsolutePath();
		final String fullPath = targetFile.getAbsolutePath();
		return getRelativePath(basePath, fullPath);
	}

	public static String getRelativePath(final URL baseDirectory,
			final URL targetFile) {
		if (baseDirectory.equals(targetFile)) {
			return ""; //$NON-NLS-1$
		}
		final String basePath = baseDirectory.toString();
		final String fullPath = targetFile.toString();
		return getRelativePath(basePath, fullPath);
	}

	public static String getRelativePath(final String basePath,
			final String fullPath) {
		if (basePath.equals(fullPath)) {
			return ""; //$NON-NLS-1$
		}
		final String normalBasePath = basePath.replaceAll(OPPOSITE_SEP_CHAR,
				REGEX_SAFE_SEP);
		final String normalFullPath = fullPath.replaceAll(OPPOSITE_SEP_CHAR,
				REGEX_SAFE_SEP);
		if (normalFullPath.indexOf(normalBasePath) == -1) {
			// try normalizing paths

			throw new RuntimeException("target file '" + normalFullPath
					+ "' is not a child of provided base directory '"
					+ normalBasePath + "'.");
		}
		if (normalFullPath.equals(normalBasePath)) {
			return ""; //$NON-NLS-1$
		}
		return normalFullPath.substring(normalBasePath.length() + 1,
				normalFullPath.length());
	}

	public static String getExtension(final String name) {
		final int dotIndex = name.lastIndexOf("."); //$NON-NLS-1$
		if (dotIndex != -1) {
			return name.substring(dotIndex + 1, name.length());
		}
		return ""; //$NON-NLS-1$
	}

	public static String getExtension(final File file) {
		final String name = file.getName();
		return getExtension(name);
	}

	public static String getBaseName(final File file) {
		final String name = file.getName();
		final int dotIndex = name.lastIndexOf("."); //$NON-NLS-1$
		if (dotIndex != -1) {
			return name.substring(0, dotIndex);
		}
		return name;
	}

	public static boolean isParent(final File parent, final File searchFile) {
		if (parent.equals(searchFile)) {
			return true;
		}
		if (!parent.isDirectory()) {
			return false;
		}
		final String parentPath = parent.getPath() + File.separator;
		final String searchPath = searchFile.getParent() + File.separator;
		return searchPath.startsWith(parentPath);
	}

	public static boolean isParent(final FileMirror parent,
			final FileMirror searchFile) {
		if (parent.equals(searchFile)) {
			return true;
		}
		if (!parent.isDirectory()) {
			return false;
		}

		// if test parent is the relative root, return true
		if (parent.getRelativePath().equals("")) { //$NON-NLS-1$
			return true;
		}
		final String parentPath = parent.getRelativePath() + File.separator;
		final String searchPath = searchFile.getParent() + File.separator;
		return searchPath.startsWith(parentPath);
	}

	public static boolean hasParent(final List<File> parents,
			final File searchFile) {
		for (final File parent : parents) {
			if (isParent(parent, searchFile)) {
				return true;
			}
		}
		return false;
	}

	// convert something like [foo.bar] to foo/bar because of index file
	// reference formats
	@SuppressWarnings("nls")
	public static String fromVaxPath(final String vaxPath) {
		// replace dots in substitution section with system specific slashes
		String returnString = vaxPath.replaceAll("(\\[.*)(\\.)(.*\\].*)", "$1"
				+ getRegexSeparator() + "$3");
		// replace substitution markers
		returnString = returnString.replaceAll("\\[(.*)\\](.*)", "$1"
				+ getRegexSeparator() + "$2");
		return returnString;
	}

	@SuppressWarnings("nls")
	public static String getRegexSeparator() {
		if (File.separator.equals("\\")) {
			return "\\\\";
		}
		return File.separator;
	}

	// get file in same directory as knownFile with a provided relative path
	public static File getSibling(final String fileName, final File knownFile) {
		final File knownDir = getBaseFile(knownFile);
		final File foundFile = new File(knownDir, fileName);
		return foundFile;
	}

	// case sensitive file exists check
	// TODO: hosted on unix system gives canonical path relative to tomcat root
	// rather than file system root. for now just compare name rather than path
	public static boolean exists(final File file) {
		if (!file.exists()) {
			return false;
		}
		try {
			// if the real path is not equal to the path used to instantiate the
			// File instance, it's not a match
			final String name = file.getName();
			final String conName = file.getCanonicalFile().getName();
			if (name.equals(conName)) {
				return true;
			}
		} catch (IOException e) {
			// noop
		}

		return false;
	}

	public static Properties loadProperties(File file) {
		return loadProperties(new Properties(), file);
	}

	public static Properties loadProperties(Properties props, File file) {
		FileInputStream is = null;
		boolean success = false;
		try {
			is = new FileInputStream(file);
			props.load(is);
			is.close();
			success = true;
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			close(is, success);
		}
		return props;
	}

	public static void close(Closeable closeMe, boolean reThrowExceptions) {
		if (closeMe != null) {
			try {
				closeMe.close();
			} catch (Throwable t) {
				if (reThrowExceptions) {
					throw new RuntimeException(t);
				}
			}
		}
	}

	// helper to get opposite cased file if search file not found. useful
	// for required files and folders that are expected to be in uppercase but
	// not necessarily required to be
	public static File getCaseUnknownFile(final File rootFile,
			final String searchName) {
		File file = new File(rootFile, searchName);
		if (!exists(file)) {
			file = getAlternateCaseFile(rootFile, searchName);
		}
		return file;
	}

	public static File getAlternateCaseFile(final File rootFile,
			final String searchName) {
		Character character = searchName.charAt(0);
		File file = null;
		if (Character.isLowerCase(character)) {
			file = new File(rootFile, searchName.toUpperCase());
		} else {
			file = new File(rootFile, searchName.toLowerCase());
		}
		return file;
	}

	public static void deleteChildren(final File file) {
		if (file.isDirectory()) {
			final File[] files = file.listFiles();
			for (final File curFile : files) {
				curFile.delete();
			}
		}
	}

	public static boolean forceDeleteAll(final File file) {
		boolean tempVal = true;
		if (file.isDirectory()) {
			final File[] files = file.listFiles();

			for (final File curFil : files) {
				tempVal = tempVal && forceDeleteAll(curFil);
			}
		}
		if (file.exists()) {
			return file.delete() && tempVal;
		}
		return false;
	}

	public static boolean empty(final File directory) {
		if (!directory.isDirectory()) {
			throw new RuntimeException("File \"" + directory.toString()
					+ "\" is not a directory");
		}
		final File[] files = directory.listFiles();
		if (files.length == 0) {
			return true;
		}
		if (files.length == 1 && files[0].getName().equals(".svn")) { //$NON-NLS-1$
			return true;
		}
		return false;
	}

	// TODO: make this cover other types of source control stuff and update to
	// use regex
	public static boolean isSourceControl(final File file) {
		if (file.getName().equals(".svn")) { //$NON-NLS-1$
			return true;
		}
		return false;
	}

	// TODO: this is rudimentary, flesh it out
	public static String getSafeName(final String string) {
		if (string == null) {
			return null;
		}
		String out = string.trim();
		out = out.replaceAll("/[^a-zA-Z0-9\\-_]/", ""); //$NON-NLS-1$ //$NON-NLS-2$
		return out;
	}
}
