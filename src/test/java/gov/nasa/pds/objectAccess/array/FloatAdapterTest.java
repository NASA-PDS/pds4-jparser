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

public class FloatAdapterTest {

	@Test(dataProvider="FloatConversionTests")
	public void testConversion(
			float value
	) {
		testConversion(value, false);
		testConversion(value, true);
	}
	
	private void testConversion(float value, boolean isBigEndian) {
		int bits = Float.floatToIntBits(value);
		if (!isBigEndian) {
			bits = reverseBytes(bits);
		}
		
		byte[] bytes = new byte[] {
				(byte) (bits >> 24),
				(byte) ((bits >> 16) & 0xFF),
				(byte) ((bits >> 8) & 0xFF),
				(byte) (bits & 0xFF)
		};
		
		ByteBuffer buf = ByteBuffer.wrap(bytes);
		FloatAdapter adapter = new FloatAdapter(isBigEndian);
		
		((Buffer) buf).rewind();
		assertEquals(adapter.getDouble(buf), (double) value);
		((Buffer) buf).rewind();
		assertEquals(adapter.getInt(buf), (int) value);
		((Buffer) buf).rewind();
		assertEquals(adapter.getLong(buf), (long) value);
	}
	
	private int reverseBytes(int n) {
		return ((n >> 24) & 0xFF)
			| ((n >> 8) & 0xFF00)
			| ((n << 8) & 0xFF0000)
			| ((n << 24) & 0xFF000000);
	}
	
	@SuppressWarnings("unused")
	@DataProvider(name="FloatConversionTests")
	private Object[][] getFloatConversionTests() {
		return new Object[][] {
				// value
				{ 0.0F },
				{ 1.0F },
				{ Float.MIN_NORMAL },
				{ Float.MAX_VALUE },
				{ -Float.MAX_VALUE },
				{ Float.NaN },
				{ Float.POSITIVE_INFINITY },
				{ Float.NEGATIVE_INFINITY }
		};
	}
	
}
