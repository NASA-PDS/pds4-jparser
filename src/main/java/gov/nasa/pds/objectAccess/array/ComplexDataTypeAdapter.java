package gov.nasa.pds.objectAccess.array;

import java.nio.ByteBuffer;

public interface ComplexDataTypeAdapter extends DataTypeAdapter {
  /**
   * Gets the imaginary value as an int.
   * 
   * @param buf the buffer from which to get the value
   * @return the imaginary value, as an int
   */
  int getImagInt(ByteBuffer buf);
  /**
   * Gets the imaginary value as a long.
   * 
   * @param buf the buffer from which to get the value
   * @return the imaginary value, as a long
   */
  long getImagLong(ByteBuffer buf);
  /**
   * Gets the imaginary value as a double.
   * 
   * @param buf the buffer from which to get the value
   * @return the imaginary value, as a double
   */
  double getImagDouble(ByteBuffer buf);
  /**
   * Gets the real value as an int.
   * 
   * @param buf the buffer from which to get the value
   * @return the real value, as an int
   */
  int getRealInt(ByteBuffer buf);
  /**
   * Gets the real value as a long.
   * 
   * @param buf the buffer from which to get the value
   * @return the real value, as a long
   */
  long getRealLong(ByteBuffer buf);
  /**
   * Gets the real value as a double.
   * 
   * @param buf the buffer from which to get the value
   * @return the real value, as a double
   */
  double getRealDouble(ByteBuffer buf);
}
