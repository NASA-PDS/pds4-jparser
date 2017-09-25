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

import gov.nasa.pds.objectAccess.utility.Utility;

/**
 * Class that provides common I/O functionality for PDS data objects.
 */
public class ByteWiseFileAccessor {
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
  public ByteWiseFileAccessor(File file, long offset, int length, int records) throws FileNotFoundException, IOException {
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
   *       rather than a regular file, or for some other reason cannot be opened for reading
   * @throws IOException If an I/O error occurs
   */
  public ByteWiseFileAccessor(URL url, long offset, int length, int records) 
      throws FileNotFoundException, IOException {
    this(url, offset, length, records, true);
  }
  
	/**
	 * Constructs a <code>ByteWiseFileAccessor</code> object
	 * which maps a region of a data file into memory.
	 *
	 * @param url the data file
	 * @param offset the offset within the data file
	 * @param length the record length in bytes
	 * @param records the number of records
	 * @param checkSize check that the size of the data file is equal to the 
	 * size of the table (length * records) + offset.
	 * @throws FileNotFoundException If <code>file</code> does not exist, is a directory
	 * 		   rather than a regular file, or for some other reason cannot be opened for reading
	 * @throws IOException If an I/O error occurs
	 */
  public ByteWiseFileAccessor(URL url, long offset, int length, int records, boolean checkSize)
	    throws FileNotFoundException, IOException {
		this.recordLength = length;
		URLConnection conn = null;
		InputStream is = null;
		ReadableByteChannel channel = null;
		try {
		  conn = url.openConnection();
	    is = Utility.openConnection(conn);
	    int size = length * records;
	    if (checkSize) {
  	    if (conn.getContentLengthLong() < offset + size) {
  	      throw new IllegalArgumentException(
  	        "The file '" + url.toString()
  	        + "' is shorter than the end of the table specified in the label ("
  	        + conn.getContentLength() + " < " + (offset+size) + ")"
  	      );
  	    }
	    }
	    is.skip(offset);
	    channel = Channels.newChannel(is);
      this.buffer = ByteBuffer.allocate(size);
      int totalBytesRead = 0;
      int bytesRead = 0;
      do {
        bytesRead = channel.read(this.buffer);
        totalBytesRead += bytesRead;
      } while (bytesRead > 0);
			this.buffer.flip();
			if (checkSize) {
  			if (totalBytesRead < size) {
  			  throw new IllegalArgumentException("Expected to read in " + size
  			      + " bytes but only " + totalBytesRead + " bytes were read for "
  			      + url.toString());
  			}
			}
		} catch (IOException ex) {
			LOGGER.error("I/O error.", ex);
			throw ex;
		} finally {
		  IOUtils.closeQuietly(is);
		  if (channel != null) {
		    channel.close();
		  }
		}
	}

	/**
	 * Constructor.
	 * 
	 * @param url The data file.
	 * @param offset The offset within the data file.
	 * @throws IOException If an I/O error occurs.
	 */
  public ByteWiseFileAccessor(URL url, long offset, int length) throws IOException {
    this.recordLength = length;
    URLConnection conn = null;
    InputStream is = null;
    ReadableByteChannel channel = null;
    try {
      conn = url.openConnection();
      is = Utility.openConnection(conn);
      long size = conn.getContentLengthLong() - offset;
      is.skip(offset);
      channel = Channels.newChannel(is);
      this.buffer = ByteBuffer.allocate(Long.valueOf(size).intValue());
      int bytesRead = 0;
      do {
        bytesRead = channel.read(this.buffer);
      } while (bytesRead > 0);
      this.buffer.flip();
    } catch (IOException ex) {
      LOGGER.error("I/O error.", ex);
      throw ex;
    } finally {
      IOUtils.closeQuietly(is);
      if (channel != null) {
        channel.close();
      }
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
  public byte[] readRecordBytes(int recordNum, int offset, int length) {
		assert recordNum > 0;

		// The offset within the mapped buffer
		int fileOffset = (recordNum - 1) * this.recordLength;
		byte[] buf = new byte[this.recordLength];
		buffer.position(fileOffset);
		buffer.get(buf);

		return Arrays.copyOfRange(buf, offset, (offset + length));
	}
	
	/**
	 * Reads a byte from the buffer.
	 * 
	 * @return A byte.
	 */
  public byte readByte() {
	  return buffer.get();
	}
	
	/**
	 * Marks the buffer.
	 * 
	 */
  public void mark() {
	  buffer.mark();
	}
	
	/**
	 * Resets the buffer.
	 * 
	 */
  public void reset() {
	  buffer.reset();
	}
	
	/**
	 * Checks to see if the buffer can still be read.
	 * 
	 * @return 'true' if there are more bytes to be read. 'false' otherwise.
	 */
  public boolean hasRemaining() {
	  return buffer.hasRemaining();
	}
}
