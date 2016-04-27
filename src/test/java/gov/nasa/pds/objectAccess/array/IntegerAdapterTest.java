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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.nio.ByteBuffer;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class IntegerAdapterTest {

	@Test(dataProvider="ConversionTests")
	public void testConversion(
			byte[] data,
			boolean isBigEndian,
			boolean isUnsigned,
			long expected
	) {
		ByteBuffer buf = ByteBuffer.wrap(data);
		IntegerAdapter adapter = new IntegerAdapter(data.length, isBigEndian, isUnsigned);
		
		assertEquals(adapter.getLong(buf), expected);
		buf.rewind();
		assertEquals(adapter.getDouble(buf), (double) expected);
		
		if (Integer.MIN_VALUE<=expected && expected<=Integer.MAX_VALUE) {
			buf.rewind();
			int actualInt = adapter.getInt(buf);
			assertEquals(actualInt, (int) expected);
		} else {
			try {
				buf.rewind();
				@SuppressWarnings("unused")
				int actualInt = adapter.getInt(buf);
				// If we get here, we didn't get an exception - fail
				fail("Expected exception was not thrown for out of range int value.");
			} catch (IllegalArgumentException ex) {
				// ignore - expected exception
			}
		}
	}
	
	@SuppressWarnings("unused")
	@DataProvider(name="ConversionTests")
	private Object[][] getConversionTests() {
		return new Object[][] {
				// data, isBigEndian, isUnsigned, expected
				{ new byte[] {0}, false, false, 0},
				{ new byte[] {1}, false, false, 1},
				{ new byte[] {127}, false, false, 127},
				{ new byte[] {(byte) 0x80}, false, false, -128},
				{ new byte[] {(byte) 0xFF}, false, false, -1},
				
				{ new byte[] {(byte) 0x80}, false, true, 128},
				{ new byte[] {(byte) 0xFF}, false, true, 255},
				
				{ new byte[] {0}, true, false, 0},
				{ new byte[] {1}, true, false, 1},
				{ new byte[] {127}, true, false, 127},
				{ new byte[] {(byte) 0x80}, true, false, -128},
				{ new byte[] {(byte) 0xFF}, true, false, -1},
				
				{ new byte[] {(byte) 0x80}, true, true, 128},
				{ new byte[] {(byte) 0xFF}, true, true, 255},

				// 2-byte tests.
				{ new byte[] {1, 0}, false, false, 1},
				{ new byte[] {0, 1}, false, false, 0x100},
				{ new byte[] {0, 1}, true, false, 1},
				{ new byte[] {1, 0}, true, false, 0x100},
				{ new byte[] {(byte) 0xFF, 1}, false, false, 511},
				{ new byte[] {1, (byte) 0xFF}, true, false, 511},
				{ new byte[] {(byte) 0xFF, (byte) 0xFF}, false, false, -1},
				{ new byte[] {(byte) 0xFF, (byte) 0xFF}, true, false, -1},
				{ new byte[] {(byte) 0xFF, (byte) 0xFF}, false, true, 65535},
				{ new byte[] {(byte) 0xFF, (byte) 0xFF}, true, true, 65535},
				
				// 4-byte tests
				{ new byte[] {1, 0, 0, 0}, false, false, 1},
				{ new byte[] {0, 1, 0, 0}, false, false, 0x100},
				{ new byte[] {0, 0, 1, 0}, false, false, 0x10000},
				{ new byte[] {0, 0, 0, 1}, false, false, 0x1000000},
				{ new byte[] {0, 0, 0, 1}, true, false, 1},
				{ new byte[] {0, 0, 1, 0}, true, false, 0x100},
				{ new byte[] {0, 1, 0, 0}, true, false, 0x10000},
				{ new byte[] {1, 0, 0, 0}, true, false, 0x1000000},
				{ new byte[] {(byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78}, false, false, 0x78563412},
				{ new byte[] {(byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78}, true, false, 0x12345678},
				{ new byte[] {(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF}, false, false, -1},
				{ new byte[] {(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF}, false, true, 0xFFFFFFFFL},
				{ new byte[] {(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF}, true, false, -1},
				{ new byte[] {(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF}, true, true, 0xFFFFFFFFL},
				
				// 8-byte tests
				{ new byte[] {1, 0, 0, 0, 0, 0, 0, 0}, false, false, 1},
				{ new byte[] {0, 0, 0, 0, 0, 0, 0, 1}, false, false, 1L << 56},
				{ new byte[] {0, 0, 0, 0, 0, 0, 0, 1}, true, false, 1},
				{ new byte[] {1, 0, 0, 0, 0, 0, 0, 0}, true, false, 1L << 56},
				{ new byte[] {
						(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
						(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0x7F, },
						false, false, 0x7FFFFFFFFFFFFFFFL
				},
				{ new byte[] {
						(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
						(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, },
						false, false, -1L
				},
				{ new byte[] {0, 0, 0, 0, 0, 0, 0, 0x10}, false, false, 0x1000000000000000L},
				{ new byte[] {0x10, 0, 0, 0, 0, 0, 0, 0}, true, false, 0x1000000000000000L},
				{ new byte[] {0, 0, 0, 0, 0, 0, 0, (byte) 0x80}, false, false, -0x8000000000000000L},
				{ new byte[] {(byte) 0x80, 0, 0, 0, 0, 0, 0, 0}, true, false, -0x8000000000000000L},
				{ new byte[] {
						(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
						(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, },
						true, false, -1L
				},
				{ new byte[] {
						(byte) 0x7F, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
						(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, },
						true, false, 0x7FFFFFFFFFFFFFFFL
				},
		};
	}
	
}
