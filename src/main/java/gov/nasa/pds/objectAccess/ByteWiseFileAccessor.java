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
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

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
	
	//https://vanillajava.blogspot.com/2011/12/using-memory-mapped-file-for-huge.html
	private static final int MAPPING_SIZE = 1 << 30;  
	private final List<ByteBuffer> mappings = new ArrayList<>();
	private long curPosition = 0;
	private long curListIndex = 0;
	private long totalBytesRead = 0;

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
    long lsize = (long)length * (long)records;
    this.totalBytesRead = 0;
    int bytesRead = 0;
    try {
      //issue_189: handle the buffer size > 2GB to read a huge file
      File dataFile = new File(url.toURI());
      RandomAccessFile raf = new RandomAccessFile(dataFile, "r");
      FileChannel inChannel = raf.getChannel();    
      long fileSize = this.fileContentSize = inChannel.size();
      long sizeToRead = lsize;
      // check this again
      if (sizeToRead>(fileSize-offset)) 
      	  sizeToRead = (fileSize-offset);
      
      //https://stackoverflow.com/questions/55300976/memory-mapping-huge-files-in-java
      long tmpSize = sizeToRead;
      for (long offset2 = 0; offset2 < sizeToRead; offset2 += MAPPING_SIZE) {
    	long size2 = Math.min(tmpSize, MAPPING_SIZE);
        mappings.add(inChannel.map(FileChannel.MapMode.READ_ONLY, (offset2+offset), size2));
        tmpSize -= size2;
        LOGGER.debug("ByteWiseFileAccessor: mappings.add: offset2,offset {},{}",offset2,offset);
        LOGGER.debug("ByteWiseFileAccessor: mappings.add: size2,mappings.size {},{}",size2,mappings.size());
      }   
      raf.close();
      for (int i=0; i<mappings.size(); i++) {
          bytesRead = mappings.get(i).capacity();
          totalBytesRead += bytesRead;
          LOGGER.debug("ByteWiseFileAccessor: i,bytesRead,totalBytesRead " + Integer.toString(i) + "," + Long.toString(bytesRead) + "," + Long.toString(totalBytesRead));
      }
      this.curPosition = 0;      
      if (checkSize) {
         if (totalBytesRead < lsize) {
    	    throw new IllegalArgumentException("Expected to read in " + lsize
    			      + " bytes but only " + totalBytesRead + " bytes were read for "
    			      + url.toString());
    	}
      }

      LOGGER.debug("ByteWiseFileAccessor: url {}",url);
      LOGGER.debug("ByteWiseFileAccessor: fileSize,sizeToRead {},{}",url,sizeToRead);
      LOGGER.debug("ByteWiseFileAccessor: totalBytesRead {}",totalBytesRead);
      LOGGER.debug("ByteWiseFileAccessor: mappings.size() {}",mappings.size());
    } catch (java.nio.channels.NonWritableChannelException ex) {
       // don't do anything
       //ex.printStackTrace();
    } catch (FileNotFoundException ex) {
    	LOGGER.error("The file '" + url.toString() + "' is not found. ", ex);
    	throw ex;
    } catch (IOException ex) {
  	  LOGGER.error("I/O error.", ex);
  	  throw ex;
  	} catch (java.net.URISyntaxException ex) {
  	  LOGGER.error("URI Syntax Error.", ex);
  	  //ex.printStackTrace();
      //throw ex;
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
	  int bytesRead = 0;
	  this.totalBytesRead = 0;
	  try {
		  File dataFile = new File(url.toURI());
		  RandomAccessFile raf = new RandomAccessFile(dataFile, "r");
		  FileChannel inChannel = raf.getChannel();    
		  long fileSize = this.fileContentSize = inChannel.size();
		  long size = (fileSize - offset);		  
		  long tmpSize = size;
		  for (long offset2 = 0; offset2 < size; offset2 += MAPPING_SIZE) {
			  long size2 = Math.min(tmpSize, MAPPING_SIZE);
			  mappings.add(inChannel.map(FileChannel.MapMode.READ_ONLY, (offset2+offset), size2));
			  tmpSize -= size2;
		  }   
		  raf.close();
		  for (int i=0; i<mappings.size(); i++) {
			 //Get the size based on content size of file
			 bytesRead = mappings.get(i).capacity();
			 totalBytesRead += bytesRead;
		  }
		  this.curPosition = 0;
	  } catch (java.nio.channels.NonWritableChannelException ex) {
		  // don't do anything
		  //ex.printStackTrace();
	  } catch (IOException ex) {
		  LOGGER.error("I/O error.", ex);
		  throw ex;
	  } catch (java.net.URISyntaxException ex) {
		  LOGGER.error("URI Syntax Error.", ex);
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
	  long fileOffset = (long)(recordNum-1)*this.recordLength;
	  byte[] buf = new byte[this.recordLength];
	  // CHECK this again with big file
	  int mapN = (int) (fileOffset / MAPPING_SIZE);
	  int offN = (int) (fileOffset % MAPPING_SIZE);

	  if (fileOffset<0 || mapN<0) {
		  LOGGER.error("Negative fileOffset or index of mappings list.");
		  return null;
	  }

	  ByteBuffer aBuf = mappings.get(mapN);
	  aBuf.position(offN);   // need to check this

      // It is possible to read pass the buffer in variable 'buf'.  Perform a check before the get() function.
      // If not enough bytes left in the buffer, that means that the record we are reading is spanning the boundary of two
      // mappings.  So that means the first part of the record is in mappings.get(mapN) and 2nd part of the record is in
      // mappings.get(mapN+1).
      //
      // The value of MAPPING_SIZE on linux is 1073741824

      LOGGER.debug("readRecordBytes:aBuf.remaining(),buf.length {},{}",aBuf.remaining(),buf.length);
      if (aBuf.remaining() >= buf.length) {
          aBuf.get(buf);
      } else {
          LOGGER.debug("readRecordBytes:recordNum,buf.length {},{}",recordNum,buf.length);
          LOGGER.debug("readRecordBytes: aBuf.remaining(),aBuf.hasRemaining() {},{}",aBuf.remaining(),aBuf.hasRemaining());
          LOGGER.debug("readRecordBytes: aBuf.isDirect() {}",aBuf.isDirect());
          LOGGER.debug("readRecordBytes: length,fileOffset {},{}",length,fileOffset);
          LOGGER.debug("readRecordBytes: mapN,offN {},{}",mapN,offN);
          LOGGER.debug("readRecordBytes: this.recordLength {}",this.recordLength);
          LOGGER.debug("Record number " + Integer.toString(recordNum) + " spanning over two mappings.  Will perform an extra read.");

          // Get the first part of the record from aBuf.remaining() bytes and 
          // get the second part of the record in mappings.get(mapN+1).
	      byte[] buf_portion_1 = new byte[aBuf.remaining()];
          aBuf.get(buf_portion_1);
          aBuf = mappings.get(mapN+1);                                             // Get the next mapping.
          aBuf.position(0);                                                        // Point the position to the beginning.
          byte[] buf_portion_2 = new byte[this.recordLength-buf_portion_1.length]; // The second portion size is the difference.
          aBuf.get(buf_portion_2);                                                 // Get the 2nd portion of record from next mapping.

          LOGGER.debug("readRecordBytes:buf_portion_1 [{}]",buf_portion_1);
          LOGGER.debug("readRecordBytes:buf_portion_2 [{}]",buf_portion_2);
          LOGGER.debug("readRecordBytes:buf_portion_1 + buf_portion_2 [{}][{}]",new String(buf_portion_1),new String(buf_portion_2));
          LOGGER.debug("readRecordBytes: buf_portion_1.length,buf_portion_2.length {},{}",buf_portion_1.length,buf_portion_2.length);
          LOGGER.debug("readRecordBytes: this.recordLength {}",this.recordLength);

          // Copy buf_portion_1 and buf_portion_2 to buf so it can be returned.
          System.arraycopy(buf_portion_1, 0, buf, 0, buf_portion_1.length);
          System.arraycopy(buf_portion_2, 0, buf, buf_portion_1.length, buf_portion_2.length);

          LOGGER.debug("readRecordBytes: buf [{}]",new String(buf));
          LOGGER.debug("readRecordBytes: buf.length [{}]",buf.length);

          // Because the original input value of length does not know that the record span over two mappings,
          // reset it to the default length of the record.
 
          length = this.recordLength;
      }

	  // need to check the offset of the bytes?
	  byte[] bytesToReturn = Arrays.copyOfRange(buf, offset, (offset + length));

      LOGGER.debug("readRecordBytes:recordNum,buf.length {},{}",recordNum,buf.length);
      LOGGER.debug("readRecordBytes: aBuf.remaining(),aBuf.hasRemaining() {},{}",aBuf.remaining(),aBuf.hasRemaining());
      LOGGER.debug("readRecordBytes: aBuf.isDirect() {}",aBuf.isDirect());
      LOGGER.debug("readRecordBytes: length,fileOffset {},{}",length,fileOffset);
      LOGGER.debug("readRecordBytes: mapN,offN {},{}",mapN,offN);
      LOGGER.debug("readRecordBytes: this.recordLength {}",this.recordLength);
      LOGGER.debug("readRecordBytes: bytesToReturn.length {}",bytesToReturn.length);
      LOGGER.debug("readRecordBytes: bytesToReturn {}",new String(bytesToReturn));

	  return bytesToReturn;
  }
	
	/**
	 * Reads a byte from the buffer.
	 * 
	 * @return A byte.
	 */
  public byte readByte() {
	  int mapN = (int)(this.curPosition / MAPPING_SIZE);
	  int offN = (int)(this.curPosition % MAPPING_SIZE);
	  this.curPosition++;
	  return mappings.get(mapN).get(offN);
	}
	
	/**
	 * Marks the buffer.
	 * 
	 */
  public void mark() {
	  int mapN = (int)(this.curPosition/MAPPING_SIZE);
	  mappings.get(mapN).mark();
	}
	
	/**
	 * Resets the buffer.
	 * 
	 */
  public void reset() {
	  // reset all buffer??
	  for (int i=0; i<mappings.size(); i++)
	    mappings.get(i).reset();
	}
	
	/**
	 * Checks to see if the buffer can still be read.
	 * 
	 * @return 'true' if there are more bytes to be read. 'false' otherwise.
	 */
  public boolean hasRemaining() {
	  if ((this.totalBytesRead-this.curPosition)==0)
		  return false;
	  else
		  return true;
	}
  
  public long getCurrentPosition() {
	  return this.curPosition;
  }
  
  public long getFileContentSize() {
	  return this.fileContentSize;
  }
}
