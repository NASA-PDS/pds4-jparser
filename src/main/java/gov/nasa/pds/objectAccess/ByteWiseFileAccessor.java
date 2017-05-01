// Copyright 2006-2017, by the California Institute of Technology.
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that provides common I/O functionality for PDS data objects.
 */
class ByteWiseFileAccessor {
	private static final Logger LOGGER = LoggerFactory.getLogger(ByteWiseFileAccessor.class);
	private int recordLength;
	private ByteBuffer buffer = null;

	 /**
   * Constructs a <code>ByteWiseFileAccessor</code> object
   * which maps a region of a data file into memory.
   *
   * @param file the data file
   * @param offset the offset within the data file
   * @param length the record length in bytes
   * @param records the number of records
   * @throws FileNotFoundException If <code>file</code> does not exist, is a directory
   *       rather than a regular file, or for some other reason cannot be opened for reading
   * @throws IOException If an I/O error occurs
   */
  ByteWiseFileAccessor(File file, long offset, int length, int records) throws FileNotFoundException, IOException {
    this(file.toURI().toURL(), offset, length, records);
  }
	
	/**
	 * Constructs a <code>ByteWiseFileAccessor</code> object
	 * which maps a region of a data file into memory.
	 *
	 * @param url the data file
	 * @param offset the offset within the data file
	 * @param length the record length in bytes
	 * @param records the number of records
	 * @throws FileNotFoundException If <code>file</code> does not exist, is a directory
	 * 		   rather than a regular file, or for some other reason cannot be opened for reading
	 * @throws IOException If an I/O error occurs
	 */
	ByteWiseFileAccessor(URL url, long offset, int length, int records) throws FileNotFoundException, IOException {
		this.recordLength = length;
		URLConnection conn = null;
		InputStream is = null;
		try {
		  conn = url.openConnection();
	    is = conn.getInputStream();
	     int size = length * records;
	      if (conn.getContentLengthLong() < offset + size) {
	        throw new IllegalArgumentException(
	            "The file '" + url.toString()
	            + "' is shorter than the end of the table specified in the label ("
	            + conn.getContentLength() + " < " + (offset+size) + ")"
	        );
	      }
	    is.skip(offset);
	    ReadableByteChannel channel = Channels.newChannel(is);
			this.buffer = ByteBuffer.allocate(size);
			int bytesRead = channel.read(this.buffer);
			this.buffer.flip();
			if (bytesRead < size) {
			  throw new IllegalArgumentException("Expected to read in " + size
			      + " bytes but only " + bytesRead + " bytes were read for "
			      + url.toString());
			}
		} catch (IOException ex) {
			LOGGER.error("I/O error.", ex);
			throw ex;
		} finally {
		  IOUtils.closeQuietly(is);
		}
	}

	/**
	 * Reads <code>length</code> bytes of data from a specified record at the given offset.
	 *
	 * @param recordNum the record number to read bytes from (1-relative)
	 * @param offset an offset within the record
	 * @param length the number of bytes to read from the record
	 * @return an array of bytes
	 */
	byte[] readRecordBytes(int recordNum, int offset, int length) {
		assert recordNum > 0;

		// The offset within the mapped buffer
		int fileOffset = (recordNum - 1) * this.recordLength;
		byte[] buf = new byte[this.recordLength];
		buffer.position(fileOffset);
		buffer.get(buf);

		return Arrays.copyOfRange(buf, offset, (offset + length));
	}
}
