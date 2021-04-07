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

import java.nio.Buffer;
import java.nio.ByteBuffer;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class DoubleAdapterTest {

	@Test(dataProvider="FloatConversionTests")
	public void testConversion(
			double value
	) {
		testConversion(value, false);
		testConversion(value, true);
	}
	
	private void testConversion(double value, boolean isBigEndian) {
		long bits = Double.doubleToLongBits(value);
		if (!isBigEndian) {
			bits = reverseBytes(bits);
		}
		
		byte[] bytes = new byte[] {
				(byte) (bits >> 56),
				(byte) ((bits >> 48) & 0xFF),
				(byte) ((bits >> 40) & 0xFF),
				(byte) ((bits >> 32) & 0xFF),
				(byte) ((bits >> 24) & 0xFF),
				(byte) ((bits >> 16) & 0xFF),
				(byte) ((bits >> 8) & 0xFF),
				(byte) (bits & 0xFF)
		};
		
		ByteBuffer buf = ByteBuffer.wrap(bytes);
		DoubleAdapter adapter = new DoubleAdapter(isBigEndian);
		
		((Buffer) buf).rewind();
		assertEquals(adapter.getDouble(buf), (double) value);
		((Buffer) buf).rewind();
		assertEquals(adapter.getInt(buf), (int) value);
		((Buffer) buf).rewind();
		assertEquals(adapter.getLong(buf), (long) value);
	}
	
	private long reverseBytes(long n) {
		return ((n >> 56) & 0xFFL)
			| ((n >> 40) & (0xFFL << 8))
			| ((n >> 24) & (0xFFL << 16))
			| ((n >> 8) & (0xFFL << 24))
			| ((n << 8) & (0xFFL << 32))
			| ((n << 24) & (0xFFL << 40))
			| ((n << 40) & (0xFFL << 48))
			| (n << 56);
	}
	
	@SuppressWarnings("unused")
	@DataProvider(name="FloatConversionTests")
	private Object[][] getFloatConversionTests() {
		return new Object[][] {
				// value
				{ 0.0 },
				{ 1.0 },
				{ Double.MIN_NORMAL },
				{ Double.MAX_VALUE },
				{ -Double.MAX_VALUE },
				{ Double.NaN },
				{ Double.POSITIVE_INFINITY },
				{ Double.NEGATIVE_INFINITY }
		};
	}
	
}
