// Copyright 2006-2018, by the California Institute of Technology.
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
package gov.nasa.pds.objectAccess.array;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;

/**
 * Implements a class that gives access to the elements of an array.
 */
public class ArrayAdapter {

	private int[] dimensions;
	private ElementType elementType;
	private MappedBuffer buf;
	
	/**
	 * Creates a new array adapter with given dimensions, a channel
	 * with the array data, and element data type name.
	 * 
	 * @param dimensions the array dimensions
	 * @param channel the channel object containing the array data
	 * @param elementType the elmeent type
	 */
	public ArrayAdapter(int[] dimensions, SeekableByteChannel channel, ElementType elementType) {
		this.dimensions = dimensions;
		this.elementType = elementType;
		this.buf = new MappedBuffer(channel, elementType.getSize());
	}
	
	/**
	 * Gets the size of each element.
	 * 
	 * @return the element size, in bytes
	 */
	public int getElementSize() {
		return elementType.getSize();
	}
	
	/**
	 * Gets an element of a 2-D array, as an int.
	 * 
	 * @param row the row
	 * @param column the column
	 * @return the element value, as an int
	 * @throws IOException 
	 */
	public int getInt(int row, int column) throws IOException {
		return getInt(new int[] {row, column});
	}
	
	/**
	 * Gets an element of a 2-D array, as a long.
	 * 
	 * @param row the row
	 * @param column the column
	 * @return the element value, as a long
	 * @throws IOException 
	 */
	public long getLong(int row, int column) throws IOException {
		return getLong(new int[] {row, column});
	}
	
	/**
	 * Gets an element of a 2-D array, as a double.
	 * 
	 * @param row the row
	 * @param column the column
	 * @return the element value, as a double
	 * @throws IOException 
	 */
	public double getDouble(int row, int column) throws IOException {
		return getDouble(new int[] {row, column});
	}
	
	/**
	 * Gets an element of a 3-D array, as an int.
	 * 
	 * @param i1 the first index
	 * @param i2 the second index
	 * @param i3 the third index
	 * @return the element value, as an int
	 * @throws IOException 
	 */
	public int getInt(int i1, int i2, int i3) throws IOException {
		return getInt(new int[] {i1, i2, i3});
	}
	
	/**
	 * Gets an element of a 3-D array, as a long.
	 * 
	 * @param i1 the first index
	 * @param i2 the second index
	 * @param i3 the third index
	 * @return the element value, as a long
	 * @throws IOException 
	 */
	public long getLong(int i1, int i2, int i3) throws IOException {
		return getLong(new int[] {i1, i2, i3});
	}
	
	/**
	 * Gets an element of a 3-D array, as a double.
	 * 
	 * @param i1 the first index
	 * @param i2 the second index
	 * @param i3 the third index
	 * @return the element value, as a double
	 * @throws IOException 
	 */
	public double getDouble(int i1, int i2, int i3) throws IOException {
		return getDouble(new int[] {i1, i2, i3});
	}
	
	/**
	 * Gets an array element, as an int.
	 * 
	 * @param position the indices of the element
	 * @return the value of the element, as an int
	 * @throws IOException 
	 */
	public int getInt(int[] position) throws IOException {
		checkDimensions(position);
		ByteBuffer buf = moveToPosition(position);
		return elementType.getAdapter().getInt(buf);
	}
	
	/**
	 * Gets an array element, as a long.
	 * 
	 * @param position the indices of the element
	 * @return the value of the element, as a long
	 * @throws IOException 
	 */
	public long getLong(int[] position) throws IOException {
		checkDimensions(position);
		ByteBuffer buf = moveToPosition(position);
		return elementType.getAdapter().getLong(buf);
	}
	
	/**
	 * Gets an array element, as a double.
	 * 
	 * @param position the indices of the element
	 * @return the value of the element, as a double
	 * @throws IOException 
	 */
	public double getDouble(int[] position) throws IOException {
		checkDimensions(position);
		ByteBuffer buf = moveToPosition(position);
		return elementType.getAdapter().getDouble(buf);
	}
	
	private ByteBuffer moveToPosition(int[] position) throws IOException {
		long index = position[0];
		
		for (int i=1; i < position.length; ++i) {
			index = index*dimensions[i] + position[i]; 
		}
		index = index * elementType.getSize();
		return buf.getBuffer(index);
	}
	
	private void checkDimensions(int[] position) {
		if (position.length != dimensions.length) {
			throw new IllegalArgumentException(
					"Array position as wrong number of dimensions: "
					+ position.length
					+ "!="
					+ dimensions.length
			);
		}
	}
	
	/**
	 * Class that provides a mechanism for buffering the given data for 
	 * optimal I/O especially for greater than 2GB sized data.
	 * 
	 * @author mcayanan
	 *
	 */
	private class MappedBuffer {
	  /** The position within the data of where the buffer is. */
	  private long startPosition;
	  /** A buffer to cache a portion of the data. */
	  private ByteBuffer cachedBuffer;
	  /** Indicates how large the buffer is. */
	  private final int BUFFER_SIZE = Integer.MAX_VALUE / 43;
	  /** The data type size. */
	  private int dataTypeSize;
	  /** The channel containing the data. */
	  private SeekableByteChannel channel;
	  
	  /**
	   * Constructor.
	   * 
	   * @param channel The channel containing the data.
	   * @param dataTypeSize The data type size. 
	   */
	  public MappedBuffer(SeekableByteChannel channel, int dataTypeSize) {
	    this.channel = channel;
	    this.dataTypeSize = dataTypeSize;
	  }
	  
	  /**
	   * Get the buffer.
	   * 
	   * @param index The position of where to get the data.
	   * 
	   * @return The ByteBuffer.
	   * 
	   * @throws IOException
	   */
	  public ByteBuffer getBuffer(long index) throws IOException {
	    ByteBuffer buf = null;
	    if (cachedBuffer == null) {
	      buf = createNewBuffer(index);
	      cachedBuffer = buf;
	    } else {
	      buf = cachedBuffer;
	    }
	    /** 
	     * Conditions where we need a new buffer:
	     * - index less than startPosition
	     * - index greater than (startPosition + BUFFER_SIZE)
	     * 
	     */
	     if ( index < startPosition ||
	          (index + dataTypeSize) > (startPosition + BUFFER_SIZE)) {
	       buf = createNewBuffer(index);
	       cachedBuffer = buf;
	     } else {
	       int relativePosition = (int) (index - startPosition);
	       buf.position(relativePosition);
	     }
	     return buf;
	  }
	  
	  /**
	   * Creates a new buffer starting from the given index.
	   * 
	   * @param index The position of where to start creating the buffer.
	   * 
	   * @return A ByteBuffer that starts from the given index.
	   * 
	   * @throws IOException
	   */
	  private ByteBuffer createNewBuffer(long index) throws IOException {
	    channel.position(index);
	    ByteBuffer buf = null;
	    if ( (channel.size() - channel.position()) < (BUFFER_SIZE) ) {
	      buf = ByteBuffer.allocate((int) (channel.size() - channel.position()));
	    } else {
	      buf = ByteBuffer.allocate(BUFFER_SIZE);
	    }
	    channel.read(buf);
	    buf.flip();
	    startPosition = index;
	    return buf;
	  }
	}
}
