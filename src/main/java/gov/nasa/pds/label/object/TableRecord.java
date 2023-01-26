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

import java.math.BigInteger;
import java.nio.charset.Charset;

/**
 * Defines an object for table records that can read the field values or write values to the fields
 * given a field name or field index.
 */
public interface TableRecord {

  /**
   * Gets the integer index of a field with the given name (1-relative).
   *
   * @param name the name of a field
   * @return the integer index of a field (1-relative)
   */
  int findColumn(String name);

  /**
   * Gets the boolean value of a field given the index.
   *
   * @param name the field name
   * @return the boolean value
   * @throws IllegalArgumentException if the value in the field is not a valid boolean value
   */
  boolean getBoolean(String name);

  /**
   * Gets the boolean value of a field given the index.
   *
   * @param index the field index
   * @return the boolean value
   * @throws IllegalArgumentException if the value in the field is not a valid boolean value
   */
  boolean getBoolean(int index);

  /**
   * Gets the short value of a field given the index.
   *
   * @param index the column index (1-relative)
   * @return a short value
   * @throws IllegalArgumentException If cannot convert the number to short
   */
  short getShort(int index);

  /**
   * Gets the short value of a field given the name.
   *
   * @param name the field name
   * @return a short value
   */
  short getShort(String name);

  /**
   * Gets the byte value of a field given the index.
   *
   * @param index the field index (1-relative)
   * @return a byte value
   * @throws IllegalArgumentException If cannot convert the number to byte
   */
  byte getByte(int index);

  /**
   * Gets the byte value of a field given the name.
   *
   * @param name the field name
   * @return a byte value
   */
  byte getByte(String name);

  /**
   * Gets the long value of a field given the index.
   *
   * <p>
   * It throws NumberFormatException if the number is out of range when converting from
   * ASCII_Integer to long.
   * </p>
   *
   * @param index the field index (1-relative)
   * @return a long value
   * @throws IllegalArgumentException If cannot convert the number to long
   * @throws NumberFormatException If the number is out of range for a long
   */
  long getLong(int index);

  /**
   * Gets the long value of a field given the name.
   *
   * @param name the field name
   * @return a long value
   */
  long getLong(String name);

  /**
   * Gets the field value as a Java BigInteger.
   *
   * @param name the name of the field
   * @return the field value, as a {@link BigInteger}
   */
  BigInteger getBigInteger(String name);

  /**
   * Gets the field value as a Java BigInteger.
   *
   * @param index the field index
   * @return the field value, as a {@link BigInteger}
   */
  BigInteger getBigInteger(int index);

  /**
   * Gets the integer value of a field given the name.
   *
   * @param name the field name
   * @return an integer value
   */
  int getInt(String name);

  /**
   * Gets the integer value of a field given the index.
   *
   * <p>
   * It throws NumberFormatException if the number is out of range when converting from
   * ASCII_Integer to int.
   * </p>
   *
   * @param index the field index (1-relative)
   * @return an integer value
   * @throws IllegalArgumentException If cannot convert the number to int
   * @throws NumberFormatException If the number is out of range for an int
   */
  int getInt(int index);

  /**
   * Gets the double value of a field given the name.
   *
   * @param name the field name
   * @return a double value
   */
  double getDouble(String name);

  /**
   * Gets the double value of a field given the index.
   *
   * <p>
   * If the number is out of range when converting from ASCII_Real to double, the method returns
   * Double.POSITIVE_INFINITY or Double.NEGATIVE_INFINITY.
   * </p>
   *
   * @param index the field index (1-relative)
   * @return a double value
   * @throws IllegalArgumentException If cannot convert the number to double
   */
  double getDouble(int index);

  /**
   * Gets the float value of a field given the name.
   *
   * @param name the field name
   * @return a float value
   */
  float getFloat(String name);

  /**
   * Gets the float value of a field given the index.
   *
   * <p>
   * If the number is out of range when converting from ASCII_Real to float, the method returns
   * Float.POSITIVE_INFINITY or Float.NEGATIVE_INFINITY.
   * </p>
   *
   * @param index the field index (1-relative)
   * @return a float value
   * @throws IllegalArgumentException If cannot convert the number to float
   */
  float getFloat(int index);

  /**
   * Gets the string value of a field given the name.
   *
   * @param name the field name
   * @return a String value
   */
  String getString(String name);

  /**
   * Gets the string value of a field given the name.
   *
   * @param name the field name
   * @param charset the charset to use.
   * @return a String value
   */
  String getString(String name, Charset charset);

  /**
   * Gets the string value of a field given the index.
   *
   * @param index the field index (1-relative)
   * @return a String value
   * @throws IllegalArgumentException If data type is not supported
   */
  String getString(int index);

  /**
   * Gets the string value of a field given the index.
   *
   * @param index the field index (1-relative)
   * @param charset The charset to use.
   * @return a String value
   * @throws IllegalArgumentException If data type is not supported
   */
  String getString(int index, Charset charset);

  /**
   * Sets a string value to a field given the index. In a fixed-width text file, numeric values are
   * right justified and non-numeric values are left justified.
   *
   * @param index the field index (1-relative)
   * @param value a string value
   * @throws IllegalArgumentException If the size of the value is greater than the field length
   */
  void setString(int index, String value);

  /**
   * Sets a string value to a field given the name. In a fixed-width text file, numeric values are
   * right justified and non-numeric values are left justified.
   *
   * @param name the field name
   * @param value a string value
   * @throws IllegalArgumentException If the size of the value is greater than field length
   */
  void setString(String name, String value);

  /**
   * Sets a String value to the record at the current offset. Use this method to write a field
   * delimiter in fixed-width text file.
   *
   * @param value a String value
   */
  void setString(String value);

  /**
   * Sets four bytes containing the integer value to a field given the index.
   *
   * @param index the field index (1-relative)
   * @param value an integer value
   */
  void setInt(int index, int value);

  /**
   * Sets four bytes containing the integer value to a field given the name.
   *
   * @param name the field name
   * @param value an integer value
   */
  void setInt(String name, int value);

  /**
   * Sets eight bytes containing the double value to a field given the index.
   *
   * @param index the field index (1-relative)
   * @param value a double value
   */
  void setDouble(int index, double value);

  /**
   * Sets eight bytes containing the double value to a field given the name.
   *
   * @param name the field name
   * @param value a double value
   */
  void setDouble(String name, double value);

  /**
   * Sets four bytes containing the float value to a field given the index.
   *
   * @param index the field index (1-relative)
   * @param value a float value
   */
  void setFloat(int index, float value);

  /**
   * Sets four bytes containing the float value to a field given the name.
   *
   * @param name the field name
   * @param value a float value
   */
  void setFloat(String name, float value);

  /**
   * Sets two bytes containing the short value to a field given the index.
   *
   * @param index the field index (1-relative)
   * @param value a short value
   */
  void setShort(int index, short value);

  /**
   * Sets two bytes containing the short value to a field given the index.
   *
   * @param name the field name
   * @param value a short value
   */
  void setShort(String name, short value);

  /**
   * Sets one byte value to a field given the index.
   *
   * @param index the field index (1-relative)
   * @param value a byte value
   */
  void setByte(int index, byte value);

  /**
   * Sets one byte value to a field given the name.
   *
   * @param name the field name
   * @param value a byte value
   */
  void setByte(String name, byte value);

  /**
   * Sets eight bytes containing the long value to the record given the field index.
   *
   * @param index the field index (1-relative)
   * @param value a long value
   */
  void setLong(int index, long value);

  /**
   * Sets eight bytes containing the long value to the record given the field name.
   *
   * @param name the field name
   * @param value a long value
   */
  void setLong(String name, long value);

  /**
   * Clears the content of the <code>TableRecord</code> object.
   */
  void clear();

  /**
   * @returns Length of the record in bytes.
   */
  int length();

  /**
   * 
   * @return Gets the record location.
   */
  public RecordLocation getLocation();

  /**
   * Sets the record location.
   * 
   * @param location the record location.
   */
  public void setLocation(RecordLocation location);
}
