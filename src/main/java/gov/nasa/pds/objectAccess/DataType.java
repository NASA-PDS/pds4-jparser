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

/**
 * Class to encapsulate PDS data types referenced by name in XML-based labels.
 *
 * @author dcberrio
 */
public interface DataType {

  /**
   * @author dcberrio Enumeration of numeric data types for tables and images. Includes fields for
   *         providing number of bits and vicar label aliases.
   * 
   */
  public enum NumericDataType {
    SignedByte(8), UnsignedByte(8, "BYTE"), SignedLSB2(16, "HALF"), SignedLSB4(32,
        "FULL"), SignedLSB8(64), UnsignedLSB2(16, "HALF"), UnsignedLSB4(32, "FULL"), UnsignedLSB8(
            64), SignedMSB2(16, "HALF"), SignedMSB4(32, "FULL"), SignedMSB8(64), UnsignedMSB2(16,
                "HALF"), UnsignedMSB4(32, "FULL"), UnsignedMSB8(64), IEEE754LSBSingle(32,
                    "REAL"), IEEE754LSBDouble(64, "DOUBLE"), IEEE754MSBSingle(32,
                        "REAL"), IEEE754MSBDouble(64, "DOUBLE"), ASCII_Integer(32), ASCII_Real(64);

    private int bits;
    private String vicarAlias;

    private NumericDataType(int numberOfBits) {
      bits = numberOfBits;
    }

    private NumericDataType(int numberOfBits, String alias) {
      bits = numberOfBits;
      vicarAlias = alias;
    }

    /**
     * Get number of bits for the data type.
     *
     * @return bits
     */
    public int getBits() {
      return bits;
    }

    /**
     * Set number of bits for the data type.
     *
     * @param bits number of bits.
     */
    public void setBits(int bits) {
      this.bits = bits;
    }

    /**
     * Get VICAR alias for the data type.
     *
     * @return vicarAlias
     */
    public String getVicarAlias() {
      return vicarAlias;
    }

    /**
     * Set the VICAR alias for the data type.
     *
     * @param vicarAlias
     */
    public void setVicarAlias(String vicarAlias) {
      this.vicarAlias = vicarAlias;
    }
  }
}
