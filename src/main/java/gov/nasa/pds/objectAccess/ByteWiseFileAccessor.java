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
	private long fileContentSize;

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
    	this.fileContentSize = conn.getContentLengthLong();
	    if (checkSize) {
	    	if (this.fileContentSize < offset + size) {
	    		throw new IllegalArgumentException(
	    				"The file '" + url.toString()
	    				+ "' is shorter than the end of the table specified in the label ("
	    				+ this.fileContentSize + " < " + (offset+size) + ")"
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
  
  public long getFileContentSize() {
	  return this.fileContentSize;
  }
}
