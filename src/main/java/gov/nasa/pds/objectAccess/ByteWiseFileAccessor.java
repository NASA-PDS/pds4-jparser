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

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that provides common I/O functionality for PDS data objects.
 */
public class ByteWiseFileAccessor implements Closeable {
  private static final Logger LOGGER = LoggerFactory.getLogger(ByteWiseFileAccessor.class);
  private int recordLength;
  private RandomAccessFile raf = null;
  private FileChannel fileAccessChannel = null;
  private long totalFileContentSize;

  // https://vanillajava.blogspot.com/2011/12/using-memory-mapped-file-for-huge.html
  private static final int MAPPING_SIZE = 1 << 30;
  // The below setting is used by developer to split small files into multiple chunks. Do not
  // remove.
  // Having smaller chunks will force the function handleTooSmallMapping() to be called.
  // If uncommented, do not run validate on large files as you will run out of memory.
  // private static final int MAPPING_SIZE = 317; // TODO: Uncomment by developer only to split
  // small files into multiple chunks.
  private final List<ByteBuffer> mappings = new ArrayList<>();
  private long curPosition = 0;
  private long totalBytesRead = 0;

  /**
   * Constructs a <code>ByteWiseFileAccessor</code> object which maps a region of a data file into
   * memory.
   *
   * @param file the data file
   * @param offset the offset within the data file
   * @param length the record length in bytes
   * @param records the number of records
   * @throws FileNotFoundException If <code>file</code> does not exist, is a directory rather than a
   *         regular file, or for some other reason cannot be opened for reading
   * @throws IOException If an I/O error occurs
   * @throws InvalidTableException
   * @throws MissingDataException
   * @throws IllegalArgumentException
   */
  public ByteWiseFileAccessor(File file, long offset, int length, long records)
      throws FileNotFoundException, IOException, InvalidTableException {
    this(file.toURI().toURL(), offset, length, records);
  }

  /**
   * Constructs a <code>ByteWiseFileAccessor</code> object which maps a region of a data file into
   * memory.
   *
   * @param url the data file
   * @param offset the offset within the data file
   * @param length the record length in bytes
   * @param records the number of records
   * @throws FileNotFoundException If <code>file</code> does not exist, is a directory rather than a
   *         regular file, or for some other reason cannot be opened for reading
   * @throws IOException If an I/O error occurs
   * @throws InvalidTableException
   * @throws MissingDataException
   * @throws IllegalArgumentException
   */
  public ByteWiseFileAccessor(URL url, long offset, int length, long records)
      throws FileNotFoundException, IOException, InvalidTableException {
    this(url, offset, length, records, true);
  }

  public ByteWiseFileAccessor(URL url, long offset, int length, long records, boolean checkSize)
      throws FileNotFoundException, IOException, InvalidTableException {
    this(url, offset, length, records, true, null);
  }

  /**
   * Constructs a <code>ByteWiseFileAccessor</code> object which maps a region of a data file into
   * memory.
   *
   * @param url the data file
   * @param offset the offset within the data file
   * @param length the record length in bytes
   * @param records the number of records
   * @param checkSize check that the size of the data file is equal to the size of the table (length
   *        * records) + offset.
   * @throws FileNotFoundException If <code>file</code> does not exist, is a directory rather than a
   *         regular file, or for some other reason cannot be opened for reading
   * @throws IOException If an I/O error occurs
   * @throws InvalidTableException
   * @throws MissingDataException
   */
  public ByteWiseFileAccessor(URL url, long offset, int length, long records, boolean checkSize,
      RandomAccessFile raf) throws FileNotFoundException, IOException, InvalidTableException {
    this.raf = raf;
    if (this.raf == null) {
      try {
        File dataFile = new File(url.toURI());
        this.raf = new RandomAccessFile(dataFile, "r");
      } catch (java.net.URISyntaxException ex) {
        LOGGER.error("URI Syntax Error.", ex);
        // ex.printStackTrace();
        // throw ex;
      }
    }

    this.fileAccessChannel = this.raf.getChannel();
    initializeAccessor(url, offset, length, records, checkSize);
  }

  private void initializeAccessor(URL url, long offset, int length, long records, boolean checkSize)
      throws FileNotFoundException, IOException, InvalidTableException {
    this.recordLength = length;
    this.totalBytesRead = 0;

    try {
      // issue_189: handle the buffer size > 2GB to read a huge file

      this.totalFileContentSize = this.fileAccessChannel.size();

      long expectedBytesToRead, actualBytesToRead;
      expectedBytesToRead = length * records;
      long fileSizeMinusOffset = Math.max(0, this.totalFileContentSize - offset);

      actualBytesToRead = expectedBytesToRead;

      if (expectedBytesToRead <= 0 || expectedBytesToRead > fileSizeMinusOffset) {
        actualBytesToRead = fileSizeMinusOffset;
      }

      // https://stackoverflow.com/questions/55300976/memory-mapping-huge-files-in-java
      long tmpSize = actualBytesToRead;
      for (long offset2 = 0; offset2 < actualBytesToRead; offset2 += MAPPING_SIZE) {
        long size2 = Math.min(tmpSize, MAPPING_SIZE);
        mappings.add(
            this.fileAccessChannel.map(FileChannel.MapMode.READ_ONLY, (offset2 + offset), size2));
        tmpSize -= size2;
        LOGGER.debug("ByteWiseFileAccessor: mappings.add: offset2,offset {},{}", offset2, offset);
        LOGGER.debug("ByteWiseFileAccessor: mappings.add: size2,mappings.size {},{}", size2,
            mappings.size());
      }

      int bytesRead = 0;
      for (int i = 0; i < mappings.size(); i++) {
        bytesRead = mappings.get(i).capacity();
        totalBytesRead += bytesRead;
        LOGGER.debug("ByteWiseFileAccessor: i,bytesRead,totalBytesRead " + Integer.toString(i) + ","
            + Long.toString(bytesRead) + "," + Long.toString(totalBytesRead));
      }
      this.curPosition = 0;

      // if for whatever reason we don't read in sufficient bytes
      if (this.totalBytesRead < expectedBytesToRead) {
        if (expectedBytesToRead > fileSizeMinusOffset) {
          if ((fileSizeMinusOffset / length) * length == fileSizeMinusOffset)
            throw new InvalidTableException(
                "Data object is truncated. Expected bytes as defined by label: " + expectedBytesToRead
                    + " (" + records + " records times " + length + " bytes per record)" + ", Actual bytes in file: "
                    + fileSizeMinusOffset + " (" + (fileSizeMinusOffset / length) + " records times " + length + " bytes per record)");
          if ((fileSizeMinusOffset / records) * records == fileSizeMinusOffset)
              throw new InvalidTableException(
                      "Data object is truncated. Expected bytes as defined by label: " + expectedBytesToRead
                          + " (" + records + " records times " + length + " bytes per record)" + ", Actual bytes in file: "
                          + fileSizeMinusOffset + " (" + records + " records times " + (fileSizeMinusOffset/records) + " bytes per record)");
          throw new InvalidTableException(
                  "Data object is truncated. Expected bytes as defined by label: " + expectedBytesToRead
                      + " (" + records + " records times " + length + " bytes per record)" + ", Actual bytes in file: "
                      + fileSizeMinusOffset + " (" + ((float)(fileSizeMinusOffset) / (float)length) + " records times " + length + " bytes per record)"
                      + " OR (" + records + " records times " + ((float)fileSizeMinusOffset / (float)records) + " bytes per record)");
        } else {
          throw new InvalidTableException("Expected to read in " + expectedBytesToRead
              + " bytes but only " + totalBytesRead + " bytes were read for " + url.toString());
        }
      }

      LOGGER.debug("ByteWiseFileAccessor: url {}", url);
      LOGGER.debug("ByteWiseFileAccessor: fileSize,sizeToRead {},{}", url, expectedBytesToRead);
      LOGGER.debug("ByteWiseFileAccessor: totalBytesRead {}", this.totalBytesRead);
      LOGGER.debug("ByteWiseFileAccessor: mappings.size() {}", mappings.size());
    } catch (java.nio.channels.NonWritableChannelException ex) {
      // don't do anything
      // ex.printStackTrace();
    } catch (FileNotFoundException ex) {
      LOGGER.error("The file '" + url.toString() + "' is not found. ", ex);
      throw ex;
    } catch (IOException ex) {
      LOGGER.error("I/O error.", ex);
      throw ex;
    }
  }

  private byte[] handleTooSmallMapping(long recordNum, int offset, int length, ByteBuffer aBuf,
      int mapN, int offN, long fileOffset, byte[] buf) {
    // This function handle a special case when the remaining content of a chunk (mapping) is
    // smaller than the requested length to read.
    // Because large files are splitted into multiple chunks (called mappings), some records may
    // span over two chunks.
    // If a record span over two chunks, the first part of the record is extracted from the 1st
    // chunk and the
    // part of the record is extracted from the 2nd chunk.
    // Special note: buf is the input and output (will be modified)

    LOGGER.debug("handleTooSmallBuffer:recordNum,buf.length {},{}", recordNum, buf.length);
    LOGGER.debug("handleTooSmallBuffer: aBuf.remaining(),aBuf.hasRemaining() {},{}",
        aBuf.remaining(), aBuf.hasRemaining());
    LOGGER.debug("handleTooSmallBuffer: aBuf.isDirect() {}", aBuf.isDirect());
    LOGGER.debug("handleTooSmallBuffer: length,fileOffset {},{}", length, fileOffset);
    LOGGER.debug("handleTooSmallBuffer: mapN,offN {},{}", mapN, offN);
    LOGGER.debug("handleTooSmallBuffer: this.recordLength {}", this.recordLength);
    LOGGER.debug(
        "handleTooSmallBuffer:Record number {} spanning over two mappings.  Will perform an extra read.",
        recordNum);

    LOGGER.info("Record number {} spanning over two mappings.  Will perform an extra read.",
        recordNum);

    // Do a sanity check if there are actually another mapping to get.
    if ((mapN + 1) >= this.mappings.size()) {
      LOGGER.error("Expecting another mapping of file content while reading record " + recordNum);
      // System.exit(1);
      return (buf);
    }

    // Get the first part of the record from aBuf.remaining() bytes and
    // get the second part of the record in this.mappings.get(mapN+1).
    byte[] bufPortion1 = new byte[aBuf.remaining()];
    aBuf.get(bufPortion1);
    aBuf = this.mappings.get(mapN + 1); // Get the next mapping.
    ((Buffer) aBuf).position(0); // Point the position to the beginning.
    byte[] bufPortion2 = new byte[this.recordLength - bufPortion1.length]; // The second portion
                                                                           // size is the
                                                                           // difference.
    aBuf.get(bufPortion2); // Get the 2nd portion of record from next mapping.

    LOGGER.debug("handleTooSmallBuffer:bufPortion1 {}", bufPortion1);
    LOGGER.debug("handleTooSmallBuffer:bufPortion2 {}", bufPortion2);
    LOGGER.debug("handleTooSmallBuffer:bufPortion1 + bufPortion2 {}{}", new String(bufPortion1),
        new String(bufPortion2));
    LOGGER.debug("handleTooSmallBuffer: bufPortion1.length,bufPortion2.length {},{}",
        bufPortion1.length, bufPortion2.length);
    LOGGER.debug("handleTooSmallBuffer: this.recordLength {}", this.recordLength);

    // Copy bufPortion1 and bufPortion2 to buf so it can be returned.
    System.arraycopy(bufPortion1, 0, buf, 0, bufPortion1.length);
    System.arraycopy(bufPortion2, 0, buf, bufPortion1.length, bufPortion2.length);

    LOGGER.debug("handleTooSmallBuffer: buf [{}]", new String(buf));
    LOGGER.debug("handleTooSmallBuffer: buf.length [{}]", buf.length);

    // Because the original input value of length does not know that the record span over two
    // mappings,
    // reset it to the default length of the record.
    return (buf); // Variable buf is both input and output.
  }

  /**
   * Reads <code>length</code> bytes of data from a specified record at the given offset.
   *
   * @param recordNum the record number to read bytes from (1-relative)
   * @param offset an offset within the record
   * @param length the number of bytes to read from the record
   * @return an array of bytes
   */
  public byte[] readRecordBytes(long recordNum, int offset, int length) {
    assert recordNum > 0;
    // The offset within the mapped buffer
    long fileOffset = (recordNum - 1) * this.recordLength;
    byte[] buf = new byte[this.recordLength];
    // CHECK this again with big file
    int mapN = (int) (fileOffset / MAPPING_SIZE);
    int offN = (int) (fileOffset % MAPPING_SIZE);

    if (fileOffset < 0 || mapN < 0) {
      LOGGER.error("Negative fileOffset or index of mappings list.");
      return null;
    }

    ByteBuffer aBuf = mappings.get(mapN);
    ((Buffer) aBuf).position(offN); // need to check this

    // It is possible to read pass the buffer in variable 'buf'. Perform a check before the get()
    // function.
    // If not enough bytes left in the buffer, that means that the record we are reading is spanning
    // the boundary of two
    // mappings. So that means the first part of the record is in mappings.get(mapN) and 2nd part of
    // the record is in
    // mappings.get(mapN+1).
    //
    // The value of MAPPING_SIZE on linux is 1073741824

    LOGGER.debug("readRecordBytes:recordNum,offset {},{}", recordNum, offset);
    LOGGER.debug("readRecordBytes:recordNum,length {},{}", recordNum, length);
    LOGGER.debug("readRecordBytes:aBuf.remaining(),buf.length {},{}", aBuf.remaining(), buf.length);

    if (aBuf.remaining() >= buf.length) {
      aBuf.get(buf);
    } else {
      buf =
          this.handleTooSmallMapping(recordNum, offset, length, aBuf, mapN, offN, fileOffset, buf);
    }

    // need to check the offset of the bytes?
    byte[] bytesToReturn = Arrays.copyOfRange(buf, offset, (offset + length));

    LOGGER.debug("readRecordBytes:recordNum,buf.length {},{}", recordNum, buf.length);
    LOGGER.debug("readRecordBytes: aBuf.remaining(),aBuf.hasRemaining() {},{}", aBuf.remaining(),
        aBuf.hasRemaining());
    LOGGER.debug("readRecordBytes: aBuf.isDirect() {}", aBuf.isDirect());
    LOGGER.debug("readRecordBytes: length,fileOffset {},{}", length, fileOffset);
    LOGGER.debug("readRecordBytes: mapN,offN {},{}", mapN, offN);
    LOGGER.debug("readRecordBytes: this.recordLength {}", this.recordLength);
    LOGGER.debug("readRecordBytes: bytesToReturn.length {}", bytesToReturn.length);
    LOGGER.debug("readRecordBytes: bytesToReturn '{}'", new String(bytesToReturn));

    return bytesToReturn;
  }

  /**
   * Reads a byte from the buffer.
   * 
   * @return A byte.
   */
  public byte readByte() {
    int mapN = (int) (this.curPosition / MAPPING_SIZE);
    int offN = (int) (this.curPosition % MAPPING_SIZE);
    this.curPosition++;
    return mappings.get(mapN).get(offN);
  }

  /**
   * Marks the buffer.
   * 
   */
  public void mark() {
    int mapN = (int) (this.curPosition / MAPPING_SIZE);
    ((Buffer) mappings.get(mapN)).mark();
  }

  /**
   * Resets the buffer.
   * 
   */
  public void reset() {
    // reset all buffer??
    for (ByteBuffer mapping : mappings) {
      ((Buffer) mapping).reset();
    }
  }

  /**
   * Checks to see if the buffer can still be read.
   * 
   * @return 'true' if there are more bytes to be read. 'false' otherwise.
   */
  public boolean hasRemaining() {
    LOGGER.debug("hasRemaining: totalBytesRead {}", totalBytesRead);
    if ((this.totalBytesRead - this.curPosition) == 0) {
      return false;
    }
    return true;
  }

  public long getCurrentPosition() {
    return this.curPosition;
  }

  public long getTotalFileContentSize() {
    return this.totalFileContentSize;
  }

  public long getTotalBytesRead() {
    return this.totalBytesRead;
  }

  @Override
  public void close() throws IOException {
    LOGGER.debug("Closing ByteWiseFileAccessor");
    this.raf.close();
  }

  public RandomAccessFile getRandomAccessFile() {
    return raf;
  }
}
