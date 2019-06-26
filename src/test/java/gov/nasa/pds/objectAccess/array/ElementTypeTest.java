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

import static org.testng.Assert.assertEquals;

import java.nio.ByteBuffer;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ElementTypeTest {

	@Test(dataProvider="ConversionTests")
	public void testConversion(
			byte[] bytes,
			String xmlType,
			double expected
	) {
		ElementType type = ElementType.getTypeForName(xmlType);
		ByteBuffer buf = ByteBuffer.wrap(bytes);
		
		assertEquals(type.getSize(), bytes.length);
		assertEquals(type.getAdapter().getDouble(buf), expected);
	}
	
	@SuppressWarnings("unused")
	@DataProvider(name="ConversionTests")
	private Object[][] getConversionTests() {
		return new Object[][] {
				// bytes, xmlType, expected value
				{getIntegerBytes(0, 1, true), "SignedByte", 0},
				{getIntegerBytes(1, 1, true), "SignedByte", 1},
				{getIntegerBytes(0x7F, 1, true), "SignedByte", 127},
				{getIntegerBytes(0xFF, 1, true), "SignedByte", -1},
				{getIntegerBytes(0x80, 1, true), "SignedByte", -128},
				
				{getIntegerBytes(0, 1, true), "UnsignedByte", 0},
				{getIntegerBytes(1, 1, true), "UnsignedByte", 1},
				{getIntegerBytes(0x7F, 1, true), "UnsignedByte", 127},
				{getIntegerBytes(0xFF, 1, true), "UnsignedByte", 255},
				{getIntegerBytes(0x80, 1, true), "UnsignedByte", 128},
				
				// 2-byte tests
				{getIntegerBytes(0, 2, false), "SignedLSB2", 0},
				{getIntegerBytes(1, 2, false), "SignedLSB2", 1},
				{getIntegerBytes(0x7FFF, 2, false), "SignedLSB2", 32767},
				{getIntegerBytes(0xFFFF, 2, false), "SignedLSB2", -1},
				{getIntegerBytes(0x8000, 2, false), "SignedLSB2", -32768},
				
				{getIntegerBytes(0, 2, true), "SignedMSB2", 0},
				{getIntegerBytes(1, 2, true), "SignedMSB2", 1},
				{getIntegerBytes(0x7FFF, 2, true), "SignedMSB2", 32767},
				{getIntegerBytes(0xFFFF, 2, true), "SignedMSB2", -1},
				{getIntegerBytes(0x8000, 2, true), "SignedMSB2", -32768},
				
				{getIntegerBytes(0, 2, false), "UnsignedLSB2", 0},
				{getIntegerBytes(1, 2, false), "UnsignedLSB2", 1},
				{getIntegerBytes(0x7FFF, 2, false), "UnsignedLSB2", 32767},
				{getIntegerBytes(0xFFFF, 2, false), "UnsignedLSB2", 65535},
				{getIntegerBytes(0x8000, 2, false), "UnsignedLSB2", 32768},
				
				{getIntegerBytes(0, 2, true), "UnsignedMSB2", 0},
				{getIntegerBytes(1, 2, true), "UnsignedMSB2", 1},
				{getIntegerBytes(0x7FFF, 2, true), "UnsignedMSB2", 32767},
				{getIntegerBytes(0xFFFF, 2, true), "UnsignedMSB2", 65535},
				{getIntegerBytes(0x8000, 2, true), "UnsignedMSB2", 32768},
				
				// 4-byte tests
				{getIntegerBytes(0, 4, false), "SignedLSB4", 0},
				{getIntegerBytes(1, 4, false), "SignedLSB4", 1},
				{getIntegerBytes(0x7FFFFFFF, 4, false), "SignedLSB4", 0x7FFFFFFF},
				{getIntegerBytes(0xFFFFFFFF, 4, false), "SignedLSB4", -1},
				{getIntegerBytes(0x80000000, 4, false), "SignedLSB4", -0x80000000L},
				
				{getIntegerBytes(0, 4, true), "SignedMSB4", 0},
				{getIntegerBytes(1, 4, true), "SignedMSB4", 1},
				{getIntegerBytes(0x7FFFFFFF, 4, true), "SignedMSB4", 0x7FFFFFFF},
				{getIntegerBytes(0xFFFFFFFF, 4, true), "SignedMSB4", -1},
				{getIntegerBytes(0x80000000, 4, true), "SignedMSB4", -0x80000000L},
				
				{getIntegerBytes(0, 4, false), "UnsignedLSB4", 0},
				{getIntegerBytes(1, 4, false), "UnsignedLSB4", 1},
				{getIntegerBytes(0x7FFFFFFF, 4, false), "UnsignedLSB4", 0x7FFFFFFF},
				{getIntegerBytes(0xFFFFFFFF, 4, false), "UnsignedLSB4", 0xFFFFFFFFL},
				{getIntegerBytes(0x80000000, 4, false), "UnsignedLSB4", 0x80000000L},
				
				{getIntegerBytes(0, 4, true), "UnsignedMSB4", 0},
				{getIntegerBytes(1, 4, true), "UnsignedMSB4", 1},
				{getIntegerBytes(0x7FFFFFFF, 4, true), "UnsignedMSB4", 0x7FFFFFFF},
				{getIntegerBytes(0xFFFFFFFF, 4, true), "UnsignedMSB4", 0xFFFFFFFFL},
				{getIntegerBytes(0x80000000, 4, true), "UnsignedMSB4", 0x80000000L},

				// 8-byte tests
				{getIntegerBytes(0, 8, false), "SignedLSB8", 0},
				{getIntegerBytes(1, 8, false), "SignedLSB8", 1},
				{getIntegerBytes(0x7FFFFFFFFFFFFFFFL, 8, false), "SignedLSB8", 0x7FFFFFFFFFFFFFFFL},
				{getIntegerBytes(0xFFFFFFFFFFFFFFFFL, 8, false), "SignedLSB8", -1},
				{getIntegerBytes(0x8000000000000000L, 8, false), "SignedLSB8", 0x8000000000000000L},
				
				{getIntegerBytes(0, 8, true), "SignedMSB8", 0},
				{getIntegerBytes(1, 8, true), "SignedMSB8", 1},
				{getIntegerBytes(0x7FFFFFFFFFFFFFFFL, 8, true), "SignedMSB8", 0x7FFFFFFFFFFFFFFFL},
				{getIntegerBytes(0xFFFFFFFFFFFFFFFFL, 8, true), "SignedMSB8", -1},
				{getIntegerBytes(0x8000000000000000L, 8, true), "SignedMSB8", 0x8000000000000000L},
				
				// Note that unsigned 8-byte has to be treated same as signed, because
				// Java does not have an unsigned, 8-byte type.
				{getIntegerBytes(0, 8, false), "UnsignedLSB8", 0},
				{getIntegerBytes(1, 8, false), "UnsignedLSB8", 1},
				{getIntegerBytes(0x7FFFFFFFFFFFFFFFL, 8, false), "UnsignedLSB8", 0x7FFFFFFFFFFFFFFFL},
				{getIntegerBytes(0xFFFFFFFFFFFFFFFFL, 8, false), "UnsignedLSB8", -1},
				{getIntegerBytes(0x8000000000000000L, 8, false), "UnsignedLSB8", 0x8000000000000000L},
				
				{getIntegerBytes(0, 8, true), "UnsignedMSB8", 0},
				{getIntegerBytes(1, 8, true), "UnsignedMSB8", 1},
				{getIntegerBytes(0x7FFFFFFFFFFFFFFFL, 8, true), "UnsignedMSB8", 0x7FFFFFFFFFFFFFFFL},
				{getIntegerBytes(0xFFFFFFFFFFFFFFFFL, 8, true), "UnsignedMSB8", -1},
				{getIntegerBytes(0x8000000000000000L, 8, true), "UnsignedMSB8", 0x8000000000000000L},
				
				// Floating-point tests
				{getFloatBytes(0.0F, false), "IEEE754LSBSingle", 0},
				{getFloatBytes(1.0F, false), "IEEE754LSBSingle", 1.0},
				{getFloatBytes(Float.MIN_NORMAL, false), "IEEE754LSBSingle", Float.MIN_NORMAL},
				{getFloatBytes(Float.MAX_VALUE, false), "IEEE754LSBSingle", Float.MAX_VALUE},
				{getFloatBytes(-Float.MAX_VALUE, false), "IEEE754LSBSingle", -Float.MAX_VALUE},
				
				{getFloatBytes(0.0F, true), "IEEE754MSBSingle", 0},
				{getFloatBytes(1.0F, true), "IEEE754MSBSingle", 1.0},
				{getFloatBytes(Float.MIN_NORMAL, true), "IEEE754MSBSingle", Float.MIN_NORMAL},
				{getFloatBytes(Float.MAX_VALUE, true), "IEEE754MSBSingle", Float.MAX_VALUE},
				{getFloatBytes(-Float.MAX_VALUE, true), "IEEE754MSBSingle", -Float.MAX_VALUE},
				
				{getDoubleBytes(0.0, false), "IEEE754LSBDouble", 0},
				{getDoubleBytes(1.0, false), "IEEE754LSBDouble", 1.0},
				{getDoubleBytes(Double.MIN_NORMAL, false), "IEEE754LSBDouble", Double.MIN_NORMAL},
				{getDoubleBytes(Double.MAX_VALUE, false), "IEEE754LSBDouble", Double.MAX_VALUE},
				{getDoubleBytes(-Double.MAX_VALUE, false), "IEEE754LSBDouble", -Double.MAX_VALUE},
				
				{getDoubleBytes(0.0, true), "IEEE754MSBDouble", 0},
				{getDoubleBytes(1.0, true), "IEEE754MSBDouble", 1.0},
				{getDoubleBytes(Double.MIN_NORMAL, true), "IEEE754MSBDouble", Double.MIN_NORMAL},
				{getDoubleBytes(Double.MAX_VALUE, true), "IEEE754MSBDouble", Double.MAX_VALUE},
				{getDoubleBytes(-Double.MAX_VALUE, true), "IEEE754MSBDouble", -Double.MAX_VALUE},
		};
	}
	
	private byte[] getIntegerBytes(long value, int size, boolean isBigEndian) {
		byte[] b = new byte[size];
		
		for (int i=0; i < size; ++i) {
			b[i] = (byte) ((value >> 8 * (b.length - i - 1)) & 0xFF);
		}
		
		if (isBigEndian) {
			return b;
		} else {
			return reverseBytes(b);
		}
	}

	private byte[] reverseBytes(byte[] b) {
		byte[] newBytes = new byte[b.length];
		
		for (int i=0; i < b.length; ++i) {
			newBytes[i] = b[b.length - i - 1];
		}
		
		return newBytes;
	}
	
	private byte[] getFloatBytes(float value, boolean isBigEndian) {
		return getIntegerBytes(Float.floatToIntBits(value), Float.SIZE / Byte.SIZE, isBigEndian);
	}
	
	private byte[] getDoubleBytes(double value, boolean isBigEndian) {
		return getIntegerBytes(Double.doubleToLongBits(value), Double.SIZE / Byte.SIZE, isBigEndian);
	}
	
}
