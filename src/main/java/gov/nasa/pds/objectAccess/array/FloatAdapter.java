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

package gov.nasa.pds.objectAccess.array;

import java.nio.ByteBuffer;

/**
 * Implements a data type adapter for float values.
 */
public class FloatAdapter implements DataTypeAdapter {

  private IntegerAdapter valueAdapter;

  /**
   * Creates a new instance.
   * 
   * @param isBigEndian true, if the data is big-endian
   */
  public FloatAdapter(boolean isBigEndian) {
    // We first convert the bit pattern to a signed int, so
    // we need to have an underlying integer adapter.
    valueAdapter = new IntegerAdapter(Float.SIZE / Byte.SIZE, isBigEndian, false);
  }

  @Override
  public int getInt(ByteBuffer buf) {
    return (int) getValue(buf);
  }

  @Override
  public long getLong(ByteBuffer buf) {
    return (long) getValue(buf);
  }

  @Override
  public double getDouble(ByteBuffer buf) {
    return getValue(buf);
  }

  private double getValue(ByteBuffer buf) {
    int bits = valueAdapter.getInt(buf);
    return Float.intBitsToFloat(bits);
  }

}
