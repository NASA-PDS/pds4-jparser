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
package gov.nasa.pds.label.object;

import gov.nasa.pds.label.io.LengthLimitedInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Defines a base type for objects within a label.
 */
public abstract class DataObject {

	private File parentDir;
	private gov.nasa.arc.pds.xml.generated.File fileObject;
	private long offset;
	private long size;

	protected DataObject(File parentDir, gov.nasa.arc.pds.xml.generated.File fileObject, long offset, long size) {
		this.parentDir = parentDir;
		this.fileObject = fileObject;
		this.offset = offset;
		this.size = size;

		if (size < 0) {
			File f = getDataFile();
			if (f.exists()) {
				size = f.length();
			}
		}
	}

	/**
	 * Gets a file that refers to the data file for this object.
	 *
	 * @return a {@link File} for the file containing the data object
	 */
	public File getDataFile() {
		return new File(parentDir, fileObject.getFileName());
	}

	/**
	 * Gets the offset within the data file where the object data begins.
	 *
	 * @return the offset to the data
	 */
	public long getOffset() {
		return offset;
	}

	/**
	 * Gets the size of the data object within the data file.
	 *
	 * @return the size of the data object, in bytes
	 */
	public long getSize() {
		return size;
	}

	protected void setSize(long newSize) {
		size = newSize;
	}

	private long getDataSize(File f) {
		if (size >= 0) {
			return size;
		} else {
			return f.length() - offset;
		}
	}

	/**
	 * Gets an input stream to the data object. This input stream will
	 * read from the first byte in the data object to the last byte within
	 * that object. Other bytes outside of the range for the data object
	 * will not be accessed.
	 *
	 * @return an input stream to the data object
	 * @throws FileNotFoundException if the data file cannot be found
	 * @throws IOException if there is an error reading the data file
	 */
	public InputStream getInputStream() throws FileNotFoundException, IOException {
		File f = getDataFile();
		return new LengthLimitedInputStream(new FileInputStream(f), offset, getDataSize(f));
	}

	/**
	 * Gets a {@link ByteBuffer} for accessing the data object. The buffer is
	 * read-only, and represents only the portion of the data file containing
	 * the data object.
	 *
	 * @return a <code>ByteBuffer</code> for reading bytes from the data object
	 * @throws FileNotFoundException if the data file cannot be found
	 * @throws IOException if there is an error reading the data file
	 */
	public ByteBuffer getBuffer() throws FileNotFoundException, IOException {
		File f = getDataFile();
		if (!f.exists()) {
			throw new FileNotFoundException("Referenced data file does not exist: " + f.getAbsolutePath());
		}
		if (!f.canRead()) {
			throw new IOException("Referenced data file is not readable: " + f.getAbsolutePath());
		}
		return (new FileInputStream(f)).getChannel().map(FileChannel.MapMode.READ_ONLY, offset, getDataSize(f));
	}

}
