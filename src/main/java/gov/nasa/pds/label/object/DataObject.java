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

package gov.nasa.pds.label.object;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import org.apache.commons.compress.utils.BoundedInputStream;
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import gov.nasa.pds.objectAccess.utility.Utility;

/**
 * Defines a base type for objects within a label.
 */
public abstract class DataObject {
  private class CappedSeekable implements SeekableByteChannel {
    private final long cap;
    private final long offset;
    private final SeekableByteChannel parent;
    CappedSeekable (SeekableByteChannel parent, long offset, long cap) throws IOException {
      this.cap = cap;
      this.offset = offset;
      this.parent = parent;
      this.parent.position(this.offset);
    }
    @Override
    public void close() throws IOException {
      this.parent.close();
    }
    @Override
    public boolean isOpen() {
      return this.parent.isOpen();
    }
    @Override
    public long position() throws IOException {
      return this.parent.position() - this.offset;
    }
    @Override
    public SeekableByteChannel position(long newPosition) throws IOException {
      this.parent.position(newPosition + offset);
      return this;
    }
    @Override
    public int read(ByteBuffer dst) throws IOException {
      dst.limit((int)(this.cap - this.position()));
      return this.parent.read(dst);
    }
    @Override
    public long size() throws IOException {
      return this.cap;
    }
    @Override
    public SeekableByteChannel truncate(long size) throws IOException {
      this.parent.truncate(size + offset);
      return this;
    }
    @Override
    public int write(ByteBuffer src) throws IOException {
      return this.parent.write(src);
    }
  }

  protected URL parentDir;
  protected gov.nasa.arc.pds.xml.generated.File fileObject;
  protected long offset;
  private long size;
  protected String name;
  protected String localIdentifier;
  protected SeekableByteChannel channel;
  protected DataObjectLocation dataObjectLocation;

  protected DataObject(File parentDir, long offset, long size) throws IOException {
    this(parentDir.toURI().toURL(), null, offset, size, null, null);
  }

  protected DataObject(File parentDir, gov.nasa.arc.pds.xml.generated.File fileObject, long offset,
      long size) throws IOException {
    this(parentDir.toURI().toURL(), fileObject, offset, size, null, null);
  }

  protected DataObject(URL parentDir, gov.nasa.arc.pds.xml.generated.File fileObject, long offset,
      long size, DataObjectLocation location) throws IOException, URISyntaxException {
    this(parentDir.toURI().toURL(), fileObject, offset, size, location, null);
  }

  protected DataObject(URL parentDir, gov.nasa.arc.pds.xml.generated.File fileObject, long offset,
      long size, DataObjectLocation location, String name) throws IOException {
    this.parentDir = parentDir;
    this.fileObject = fileObject;
    this.offset = offset;
    this.size = size;
    this.name = name;
    this.dataObjectLocation = location;
    this.channel = null;
  }

  /**
   * Gets a url that refers to the data file for this object.
   *
   * @return a {@link URL} for the file containing the data object
   * @throws MalformedURLException
   */
  public URL getDataFile() throws MalformedURLException {
    if (fileObject != null) {
      return new URL(parentDir, fileObject.getFileName());
    }
    return null;
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
    }
    URLConnection conn = null;
    try {
      conn = u.openConnection();
      return conn.getContentLengthLong() - offset;
    } finally {
      IOUtils.closeQuietly(conn.getInputStream());
    }
  }

  /**
   * Gets an input stream to the data object. This input stream will read from the first byte in the
   * data object to the last byte within that object. Other bytes outside of the range for the data
   * object will not be accessed.
   *
   * @return an input stream to the data object
   * @throws FileNotFoundException if the data file cannot be found
   * @throws IOException if there is an error reading the data file
   */
  public InputStream getInputStream() throws IOException {
    ReadableByteChannel ch = null;
    if (this.channel != null) {
      ch = this.channel;
    } else {
      ch = getChannel();
    }
    return new BufferedInputStream(Channels.newInputStream(ch));
  }

  /**
   * Gets a {@link SeekableByteChannel} for accessing the data object. The channel is read-only, and
   * represents only the portion of the data file containing the data object. You must remember to
   * call the closeChannel() method once reading of the data is finished.
   *
   * @return a <code>SeekableByteChannel</code> for reading bytes from the data object
   * 
   * @throws IOException if there is an error reading the data file
   */
  public SeekableByteChannel getChannel() throws IOException {
    if (channel != null) {
      return channel;
    }

    // If not file object exists, intentional or not, return null
    if (this.fileObject == null) {
      return null;
    }

    URL u = getDataFile();
    long datasize = getDataSize(u);
    try {
      if ("file".equalsIgnoreCase(u.getProtocol())) {
        channel = new CappedSeekable(Files.newByteChannel(Paths.get(u.toURI()), StandardOpenOption.READ), offset, datasize);
      } else {
        channel = createChannel(u, offset, datasize);
      }
    } catch (IOException io) {
      throw new IOException("Error reading data file '" + u.toString() + "': " + io.getMessage());
    } catch (URISyntaxException use) {
      throw new IOException("Error could not translaate '" + u.toString() + "' to a valid file path: " + use.getMessage());
    }
    return channel;
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
   * Creates a FileChannel that represents the portion of the data within the file. This is done by
   * creating a temp file in the OS default temp area.
   * 
   * The closeChannel() method will need to be called once reading of the data is finished.
   * 
   * @param url The data file.
   * @param offset The offset to the start of the data.
   * @param size The size of the data.
   * 
   * @return An SeekableByteChannel of the data.
   * 
   * @throws IOException If an error occurred creating this FileChannel.
   */
  private SeekableByteChannel createChannel(URL url, long offset, long size) throws IOException {
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
            throw new IOException("Error while copying data object to file '" + temp.toString()
                + "': Number of bytes read does not match the " + "expected size (got="
                + totalBytesRead + ", expected=" + size + ")");
          }
        } finally {
          IOUtils.closeQuietly(fileStream);
        }
        createdChannel =
            Files.newByteChannel(temp, StandardOpenOption.READ, StandardOpenOption.DELETE_ON_CLOSE);
      } else {
        BoundedInputStream bis = new BoundedInputStream(input, size);
        createdChannel = new SeekableInMemoryByteChannel(IOUtils.toByteArray(bis));
      }
    } finally {
      IOUtils.closeQuietly(input);
    }
    return createdChannel;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public DataObjectLocation getDataObjectLocation() {
    return dataObjectLocation;
  }

  public void setDataObjectLocation(DataObjectLocation dataObjectLocation) {
    this.dataObjectLocation = dataObjectLocation;
  }

  public String getLocalIdentifier() {
    return localIdentifier;
  }

  public void setLocalIdentifier(String localIdentifier) {
    this.localIdentifier = localIdentifier;
  }
}
