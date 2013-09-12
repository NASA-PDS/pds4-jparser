package gov.nasa.pds.objectAccess.array;

import java.nio.ByteBuffer;

/**
 * Implements a class that gives access to the elements of an array.
 */
public class ArrayAdapter {

	private int[] dimensions;
	private ByteBuffer buf;
	private ElementType elementType;
	
	/**
	 * Creates a new array adapter with given dimensions, byte buffer
	 * with the array data, and element data type name.
	 * 
	 * @param dimensions the array dimensions
	 * @param buf the byte buffer containing the array data
	 * @param elementType the elmeent type
	 */
	public ArrayAdapter(int[] dimensions, ByteBuffer buf, ElementType elementType) {
		this.dimensions = dimensions;
		this.buf = buf;
		this.elementType = elementType;
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
	 */
	public int getInt(int row, int column) {
		return getInt(new int[] {row, column});
	}
	
	/**
	 * Gets an element of a 2-D array, as a long.
	 * 
	 * @param row the row
	 * @param column the column
	 * @return the element value, as a long
	 */
	public long getLong(int row, int column) {
		return getLong(new int[] {row, column});
	}
	
	/**
	 * Gets an element of a 2-D array, as a double.
	 * 
	 * @param row the row
	 * @param column the column
	 * @return the element value, as a double
	 */
	public double getDouble(int row, int column) {
		return getDouble(new int[] {row, column});
	}
	
	/**
	 * Gets an element of a 3-D array, as an int.
	 * 
	 * @param i1 the first index
	 * @param i2 the second index
	 * @param i3 the third index
	 * @return the element value, as an int
	 */
	public int getInt(int i1, int i2, int i3) {
		return getInt(new int[] {i1, i2, i3});
	}
	
	/**
	 * Gets an element of a 3-D array, as a long.
	 * 
	 * @param i1 the first index
	 * @param i2 the second index
	 * @param i3 the third index
	 * @return the element value, as a long
	 */
	public long getLong(int i1, int i2, int i3) {
		return getLong(new int[] {i1, i2, i3});
	}
	
	/**
	 * Gets an element of a 3-D array, as a double.
	 * 
	 * @param i1 the first index
	 * @param i2 the second index
	 * @param i3 the third index
	 * @return the element value, as a double
	 */
	public double getDouble(int i1, int i2, int i3) {
		return getDouble(new int[] {i1, i2, i3});
	}
	
	/**
	 * Gets an array element, as an int.
	 * 
	 * @param position the indices of the element
	 * @return the value of the element, as an int
	 */
	public int getInt(int[] position) {
		checkDimensions(position);
		moveToPosition(position);
		return elementType.getAdapter().getInt(buf);
	}
	
	/**
	 * Gets an array element, as a long.
	 * 
	 * @param position the indices of the element
	 * @return the value of the element, as a long
	 */
	public long getLong(int[] position) {
		checkDimensions(position);
		moveToPosition(position);
		return elementType.getAdapter().getLong(buf);
	}
	
	/**
	 * Gets an array element, as a double.
	 * 
	 * @param position the indices of the element
	 * @return the value of the element, as a double
	 */
	public double getDouble(int[] position) {
		checkDimensions(position);
		moveToPosition(position);
		return elementType.getAdapter().getDouble(buf);
	}
	
	private void moveToPosition(int[] position) {
		int index = position[0];
		
		for (int i=1; i < position.length; ++i) {
			index = index*dimensions[i] + position[i]; 
		}
		
		buf.position(index * elementType.getSize());
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
	
}
