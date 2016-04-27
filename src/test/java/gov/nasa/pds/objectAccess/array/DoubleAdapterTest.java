// Copyright 2006-2016, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
// Any commercial use must be negotiated with the Office of Technology Transfer
// at the California Institute of Technology.
//
// This software is subject to U. S. export control laws and regulations
// (22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software
// is subject to U.S. export control laws and regulations, the recipient has
// the responsibility to obtain export licenses or other export authority as
// may be required before exporting such information to foreign countries or
// providing access to foreign nationals.
//
// $Id$
package gov.nasa.pds.objectAccess.array;

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
		
		buf.rewind();
		assertEquals(adapter.getDouble(buf), (double) value);
		buf.rewind();
		assertEquals(adapter.getInt(buf), (int) value);
		buf.rewind();
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
