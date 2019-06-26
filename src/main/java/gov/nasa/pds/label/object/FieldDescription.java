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


/**
 * Implements a description of a table field.
 */
public class FieldDescription {

	private String name;
	private FieldType type;
	private int offset;
	private int length;
	private int maxLength;
	private int startBit;
	private int stopBit;
	private String fieldFormat;
	private String validationFormat;
	private Double minimum;
	private Double maximum;
	
	public FieldDescription() {
	  name = "";
	  type = null;
	  offset = -1;
	  length = -1;
	  maxLength = -1;
	  startBit = -1;
	  stopBit = -1;
	  fieldFormat = "";
	  validationFormat = "";
	  minimum = null;
	  maximum = null;
	}
	
	/**
	 * Gets the field name.
	 * 
	 * @return the field name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the field name.
	 * 
	 * @param name the new field name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Gets the field type.
	 * 
	 * @return the field type
	 */
	public FieldType getType() {
		return type;
	}
	
	/**
	 * Sets the field type.
	 * 
	 * @param type the new field type
	 */
	public void setType(FieldType type) {
		this.type = type;
	}
	
	/**
	 * Gets the field offset, the number of bytes
	 * past the beginning of the record where the
	 * field starts. The offset is not used for
	 * delimited tables.
	 * 
	 * @return the field offset
	 */
	public int getOffset() {
		return offset;
	}
	
	/**
	 * Sets the field offset.
	 * 
	 * @param offset the field offset
	 */
	public void setOffset(int offset) {
		this.offset = offset;
	}
	
	/**
	 * Gets the field length, in bytes. The field
	 * length is not used for fields of delimited
	 * tables.
	 * 
	 * @return the field length
	 */
	public int getLength() {
		return length;
	}
	
	/**
	 * Sets the field length, in bytes.
	 * 
	 * @param length the new field length
	 */
	public void setLength(int length) {
		this.length = length;
	}

	 /**
   * Gets the maximum field length, in bytes. This
   * is only used for fields of delimited
   * tables.
   * 
   * @return the max field length
   */
  public int getMaxLength() {
    return maxLength;
  }
  
  /**
   * Sets the maximum field length, in bytes.
   * 
   * @param length the new maximum field length
   */
  public void setMaxLength(int length) {
    this.maxLength = length;
  }
	
	/**
	 * Gets the start bit, for bit fields. Bits
	 * are counted from left to right, where zero
	 * is the leftmost bit.
	 * 
	 * @return the start bit
	 */
	public int getStartBit() {
		return startBit;
	}

	/**
	 * Sets the start bit, for bit fields.
	 * 
	 * @param startBit the new start bit
	 */
	public void setStartBit(int startBit) {
		this.startBit = startBit;
	}

	/**
	 * Gets the stop bit, for bit fields.
	 * 
	 * @return the stop bit
	 */
	public int getStopBit() {
		return stopBit;
	}

	/**
	 * Sets the stop bit, for bit fields.
	 * 
	 * @param stopBit the new stop bit
	 */
	public void setStopBit(int stopBit) {
		this.stopBit = stopBit;
	}
	
	public String getFieldFormat() {
	  return fieldFormat;
	}
	
	public void setFieldFormat(String format) {
	  this.fieldFormat = format;
	}
	
  public String getValidationFormat() {
    return validationFormat;
  }
  
  public void setValidationFormat(String format) {
    this.validationFormat = format;
  }	
	
	public void setMinimum(Double min) {
	  this.minimum = min;
	}
	
	public Double getMinimum() {
	  return minimum;
	}
	
	public void setMaximum(Double max) {
	  this.maximum = max;
	}
	
	public Double getMaximum() {
	  return maximum;
	}
	
}
