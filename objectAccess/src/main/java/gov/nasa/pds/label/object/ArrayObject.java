package gov.nasa.pds.label.object;

import gov.nasa.arc.pds.xml.generated.Array;
import gov.nasa.arc.pds.xml.generated.Array2DImage;
import gov.nasa.arc.pds.xml.generated.Array3DImage;
import gov.nasa.pds.objectAccess.array.ArrayAdapter;
import gov.nasa.pds.objectAccess.array.ElementType;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


/**
 * Implements an object representing an array object in a
 * PDS product.
 */
public class ArrayObject extends DataObject {

	private Array array;
	private ArrayAdapter adapter;
	int[] dimensions;

	/**
	 * Creats a new array instance.
	 *
	 * @param parentDir the parent directory for the data file
	 * @param fileObject the file object metadata
	 * @param array the array object
	 * @param offset the offset within the data file
	 * @throws IOException if there is an error opening the data file
	 * @throws FileNotFoundException if the data file is not found
	 */
	public ArrayObject(
			File parentDir,
			gov.nasa.arc.pds.xml.generated.File fileObject,
			Array array,
			long offset
	) throws FileNotFoundException, IOException {
		super(parentDir, fileObject, offset, 0);
		this.array = array;

		dimensions = findDimensions();
		ElementType elementType = ElementType.getTypeForName(array.getElementArray().getDataType());
		setSize(findSize(elementType.getSize()));
		adapter = new ArrayAdapter(dimensions, getBuffer(), elementType);
	}

	private int[] findDimensions() {
		int[] dims = new int[array.getAxes()];
		for (int i=0; i < dims.length; ++i) {
			dims[i] = array.getAxisArraies().get(i).getElements();
		}

		return dims;
	}

	/**
	 * Gets the dimensions of the array.
	 *
	 * @return an array of dimensions
	 */
	public int[] getDimensions() {
		return dimensions;
	}

	private long findSize(int elementSize) {
		int count = 1;
		for (int i=0; i < dimensions.length; ++i) {
			count *= dimensions[i];
		}

		return count * elementSize;
	}

	/**
	 * Gets the number of dimensions.
	 *
	 * @return the number of dimensions
	 */
	public int getAxes() {
		return dimensions.length;
	}

	/**
	 * Gets the size of an array element.
	 *
	 * @return the element size, in bytes
	 */
	public int getElementSize() {
		return adapter.getElementSize();
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
		return adapter.getDouble(i1, i2, i3);
	}

	/**
	 * Gets an array element, as an int.
	 *
	 * @param position the indices of the element
	 * @return the value of the element, as an int
	 */
	public int getInt(int[] position) {
		checkIndices(position);
		return adapter.getInt(position);
	}

	/**
	 * Gets an array element, as a long.
	 *
	 * @param position the indices of the element
	 * @return the value of the element, as a long
	 */
	public long getLong(int[] position) {
		checkIndices(position);
		return adapter.getLong(position);
	}

	/**
	 * Gets an array element, as a double.
	 *
	 * @param position the indices of the element
	 * @return the value of the element, as a double
	 */
	public double getDouble(int[] position) {
		checkIndices(position);
		return adapter.getDouble(position);
	}

	private void checkIndices(int[] position) {
		checkDimensions(position.length);

		for (int i=0; i < dimensions.length; ++i) {
			if (position[i] < 0 || position[i] >= dimensions[i]) {
				throw new ArrayIndexOutOfBoundsException("Index " + i + " out of bounds (" + position[i] + ")");
			}
		}
	}

	/**
	 * Gets the entire 2-D array, as doubles.
	 *
	 * @return an array of double with all array elements
	 */
	public double[][] getElements2D() {
		checkDimensions(2);

		double[][] values = new double[dimensions[0]][dimensions[1]];
		for (int i=0; i < dimensions[0]; ++i) {
			for (int j=0; j < dimensions[1]; ++j) {
				values[i][j] = getDouble(i, j);
			}
		}

		return values;
	}

	/**
	 * Gets the entire 3-D array, as doubles.
	 *
	 * @return an array of double with all array elements
	 */
	public double[][][] getElements3D() {
		checkDimensions(3);

		double[][][] values = new double[dimensions[0]][dimensions[1]][dimensions[2]];
		for (int i=0; i < dimensions[0]; ++i) {
			for (int j=0; j < dimensions[1]; ++j) {
				for (int k=0; k < dimensions[2]; ++k) {
					values[i][j][k] = getDouble(i, j, k);
				}
			}
		}

		return values;
	}

	private void checkDimensions(int expected) {
		if (expected != dimensions.length) {
			throw new IllegalArgumentException(
					"Array access with wrong number of dimensions: "
					+ expected
					+ "!="
					+ dimensions.length
			);
		}
	}

	/**
	 * Checks to see whether the array is an image.
	 *
	 * @return true, if the array is an image
	 */
	public boolean isImage() {
		return (array instanceof Array2DImage) || (array instanceof Array3DImage);
	}

	public BufferedImage as2DImage() {
		if (!(array instanceof Array2DImage)) {
			throw new UnsupportedOperationException("Data object is not a 2-D image.");
		}

		BufferedImage image = new BufferedImage(dimensions[0], dimensions[1], BufferedImage.TYPE_BYTE_GRAY);
		return image;
	}

}
