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
package gov.nasa.pds.label.object;

import gov.nasa.pds.objectAccess.utility.Utility;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.apache.commons.compress.utils.BoundedInputStream;
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

/**
 * Defines a base type for objects within a label.
 */
public abstract class DataObject {

	private URL parentDir;
	private gov.nasa.arc.pds.xml.generated.File fileObject;
	private long offset;
	private long size;
	private SeekableByteChannel channel;

	protected DataObject(File parentDir, gov.nasa.arc.pds.xml.generated.File fileObject, long offset, long size)
	    throws IOException {
	  this(parentDir.toURI().toURL(), fileObject, offset, size);
	}
	
	protected DataObject(URL parentDir, gov.nasa.arc.pds.xml.generated.File fileObject, long offset, long size)
	    throws IOException {
		this.parentDir = parentDir;
		this.fileObject = fileObject;
		this.offset = offset;
		this.size = size;
		this.channel = null;

		if (size < 0) {
			URL u = null;
			URLConnection conn = null;
			try {
			  u = getDataFile();
		    conn = u.openConnection();
		    size = conn.getContentLengthLong();
			} finally {		  
        IOUtils.closeQuietly(
            conn.getInputStream());
			}
		}
	}

	/**
	 * Gets a url that refers to the data file for this object.
	 *
	 * @return a {@link URL} for the file containing the data object
	 * @throws MalformedURLException 
	 */
	public URL getDataFile() throws MalformedURLException {
		return new URL(parentDir, fileObject.getFileName());
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

	private long getDataSize(URL u) throws IOException {
    if (size >= 0) {
      return size;
    } else {
      URLConnection conn = null;
      try {
        conn = u.openConnection();
        return conn.getContentLengthLong() - offset;
      } finally {     
        IOUtils.closeQuietly(
            conn.getInputStream());
      }
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
	public InputStream getInputStream() throws IOException {
	  ReadableByteChannel ch = null;
	  if (channel != null) {
	    ch = channel;
	  } else {
	    ch = getChannel();
	  }
	  return new BufferedInputStream(Channels.newInputStream(ch));
	}

	/**
	 * Gets a {@link SeekableByteChannel} for accessing the data object. 
	 * The channel is read-only, and represents only the portion of the 
	 * data file containing the data object. You must remember to call the
	 * closeChannel() method once reading of the data is finished.
	 *
	 * @return a <code>SeekableByteChannel</code> for reading bytes from the
	 *  data object
	 * 
	 * @throws IOException if there is an error reading the data file
	 */
	public SeekableByteChannel getChannel() throws IOException {
	  if (channel != null) {
	    return channel;
	  } else {
  		URL u = getDataFile();
  		long datasize = getDataSize(u);
  		try {
  		  channel = createChannel(u, offset, datasize);
  		} catch (IOException io) { 
  			throw new IOException("Error reading data file '"
  			    + u.toString() + "': " + io.getMessage());
  		}
      return channel;
	  }
	}
	
	
	/**
	 * Closes the underlying channel to the data.
	 * 
	 */
	public void closeChannel() {
	  try {
	    if (channel != null) {
	      channel.close();
	    }
    } catch (IOException e) {
      // Ignore
    }
	}
	
	/**
	 * Creates a FileChannel that represents the portion of the data within 
	 * the file. This is done by creating a temp file in the OS default temp
	 * area.
	 * 
	 * The closeChannel() method will need to be called once reading of the data
	 * is finished.
	 * 
	 * @param url The data file.
	 * @param offset The offset to the start of the data.
	 * @param size The size of the data.
	 * 
	 * @return An SeekableByteChannel of the data.
	 * 
	 * @throws IOException If an error occurred creating this FileChannel.
	 */
	private SeekableByteChannel createChannel(URL url, long offset, long size) 
	    throws IOException {
    FileOutputStream fileStream = null;
    Path temp = null;
    /** Indicates how large the buffer is. */
    final int MAX_SIZE = Integer.MAX_VALUE / 43;
    InputStream input = Utility.openConnection(url.openConnection());
    input.skip(offset);
    SeekableByteChannel createdChannel = null;
    try {
      if (size > MAX_SIZE) {
        temp = Files.createTempFile(FilenameUtils.getBaseName(url.toString()), null);
        temp.toFile().deleteOnExit();
        ReadableByteChannel channel = Channels.newChannel(input);
        try {
          fileStream = new FileOutputStream(temp.toFile());
          FileChannel fc = fileStream.getChannel();
          long totalBytesRead = 0;
          long bytesRead = fc.transferFrom(channel, 0, size);
          totalBytesRead += bytesRead;
          if (totalBytesRead != size) {
            do {
              fc.position(fc.position() + bytesRead);
              bytesRead = fc.transferFrom(channel, fc.position(), size);
              totalBytesRead += bytesRead;
            } while (bytesRead != 0 && totalBytesRead < size);
            fc.position(0);
          }
          if (totalBytesRead != size) {
            throw new IOException("Error while copying data object to file '"
                + temp.toString() + "': Number of bytes read does not match the "
                + "expected size (got=" + totalBytesRead + ", expected=" + size + ")");
          }
        } finally {
          IOUtils.closeQuietly(fileStream);
        }
        createdChannel = Files.newByteChannel(temp, StandardOpenOption.READ, 
            StandardOpenOption.DELETE_ON_CLOSE);
      } else {
        BoundedInputStream bis = new BoundedInputStream(input, size);
        createdChannel = new SeekableInMemoryByteChannel(IOUtils.toByteArray(bis)); 
      }
    } finally {
      IOUtils.closeQuietly(input);
    }
    return createdChannel;
	}
}
