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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import gov.nasa.arc.pds.xml.generated.Array;
import gov.nasa.arc.pds.xml.generated.Array2DImage;
import gov.nasa.arc.pds.xml.generated.Array3DImage;
import gov.nasa.pds.objectAccess.array.ArrayAdapter;
import gov.nasa.pds.objectAccess.array.ElementType;


/**
 * Implements an object representing an array object in a PDS product.
 */
public class ArrayObject extends DataObject {

  private Array array;
  private ArrayAdapter adapter;
  private ElementType elementType;
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
   * @throws URISyntaxException
   */
  public ArrayObject(File parentDir, gov.nasa.arc.pds.xml.generated.File fileObject, Array array,
      long offset, DataObjectLocation location)
      throws FileNotFoundException, IOException, URISyntaxException {
    this(parentDir.toURI().toURL(), fileObject, array, offset, location);
  }

  /**
   * Creates a new array instance.
   *
   * @param parent the parent directory for the data file
   * @param fileObject the file object metadata
   * @param array the array object
   * @param offset the offset within the data file
   * @throws IOException if there is an error opening the data file
   * @throws FileNotFoundException if the data file is not found
   * @throws URISyntaxException
   */
  public ArrayObject(URL parent, gov.nasa.arc.pds.xml.generated.File fileObject, Array array,
      long offset, DataObjectLocation location)
      throws FileNotFoundException, IOException, URISyntaxException {
    super(parent, fileObject, offset, 0, location);
    this.array = array;

    dimensions = findDimensions();
    elementType = ElementType.getTypeForName(array.getElementArray().getDataType());
    setSize(findSize(elementType.getSize()));

    adapter = new ArrayAdapter(dimensions, elementType);

    this.name = array.getName();
    this.localIdentifier = array.getLocalIdentifier();
  }

  /**
   * Deprecated initializer. Missing DataObjectLocation
   */
  @Deprecated
  public ArrayObject(URL parent, gov.nasa.arc.pds.xml.generated.File fileObject, Array array,
      long offset) throws FileNotFoundException, IOException, URISyntaxException {
    this(parent, fileObject, array, offset, null);
  }

  /**
   * Deprecated initializer. Missing DataObjectLocation
   */
  @Deprecated
  public ArrayObject(File parentDir, gov.nasa.arc.pds.xml.generated.File fileObject, Array array,
      long offset) throws FileNotFoundException, IOException, URISyntaxException {
    this(parentDir.toURI().toURL(), fileObject, array, offset, null);
  }

  private int[] findDimensions() {
    int[] dims = new int[array.getAxes()];
    for (int i = 0; i < dims.length; ++i) {
      dims[i] = array.getAxisArraies().get(i).getElements().intValueExact();
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
    long count = 1;
    for (int dimension : dimensions) {
      count *= dimension;
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
    return adapter.getDouble(i1, i2, i3);
  }

  /**
   * Gets an array element, as an int.
   *
   * @param position the indices of the element
   * @return the value of the element, as an int
   * @throws IOException
   */
  public int getInt(int[] position) throws IOException {
    checkIndices(position);
    return adapter.getInt(position);
  }

  /**
   * Gets an array element, as a long.
   *
   * @param position the indices of the element
   * @return the value of the element, as a long
   * @throws IOException
   */
  public long getLong(int[] position) throws IOException {
    checkIndices(position);
    return adapter.getLong(position);
  }

  /**
   * Gets an array element, as a double.
   *
   * @param position the indices of the element
   * @return the value of the element, as a double
   * @throws IOException
   */
  public double getDouble(int[] position) throws IOException {
    checkIndices(position);
    return adapter.getDouble(position);
  }

  private void checkIndices(int[] position) {
    checkDimensions(position.length);

    for (int i = 0; i < dimensions.length; ++i) {
      if (position[i] < 0 || position[i] >= dimensions[i]) {
        throw new ArrayIndexOutOfBoundsException(
            "Index " + i + " out of bounds (" + position[i] + ")");
      }
    }
  }

  /**
   * Gets the entire 1-D array, as doubles.
   *
   * @return an array of double with all array elements
   * @throws IOException
   */
  public double[] getElements1D() throws IOException {
    checkDimensions(1);

    double[] values = new double[dimensions[0]];
    for (int i = 0; i < dimensions[0]; ++i) {
      values[i] = getDouble(new int[] {i});
    }

    return values;
  }

  /**
   * Gets the entire 2-D array, as doubles.
   *
   * @return an array of double with all array elements
   * @throws IOException
   */
  public double[][] getElements2D() throws IOException {
    checkDimensions(2);

    double[][] values = new double[dimensions[0]][dimensions[1]];
    for (int i = 0; i < dimensions[0]; ++i) {
      for (int j = 0; j < dimensions[1]; ++j) {
        values[i][j] = getDouble(i, j);
      }
    }

    return values;
  }

  /**
   * Gets the entire 3-D array, as doubles.
   *
   * @return an array of double with all array elements
   * @throws IOException
   */
  public double[][][] getElements3D() throws IOException {
    checkDimensions(3);

    double[][][] values = new double[dimensions[0]][dimensions[1]][dimensions[2]];
    for (int i = 0; i < dimensions[0]; ++i) {
      for (int j = 0; j < dimensions[1]; ++j) {
        for (int k = 0; k < dimensions[2]; ++k) {
          values[i][j][k] = getDouble(i, j, k);
        }
      }
    }

    return values;
  }

  private void checkDimensions(int expected) {
    if (expected != dimensions.length) {
      throw new IllegalArgumentException(
          "Array access with wrong number of dimensions: " + expected + "!=" + dimensions.length);
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

  /**
   * Returns a BufferedImage object with the type set to TYPE_BYTE_GRAY.
   * 
   * @return a BufferedImage
   */
  public BufferedImage as2DImage() {
    if (!(array instanceof Array2DImage)) {
      throw new UnsupportedOperationException("Data object is not a 2-D image.");
    }

    BufferedImage image =
        new BufferedImage(dimensions[0], dimensions[1], BufferedImage.TYPE_BYTE_GRAY);
    return image;
  }

  public ElementType getElementType() {
    return elementType;
  }

  public Array getArray() {
    return array;
  }

  public void setArray(Array array) {
    this.array = array;
  }

  public void open() throws IOException {
    if (this.adapter.getBuf() == null) {
      this.adapter.open(getChannel());
    }
  }

  public void close() throws IOException {
    this.adapter.close();
  }
}
