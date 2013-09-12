package gov.nasa.pds.objectAccess.array;

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
		
		buf.rewind();
		assertEquals(adapter.getDouble(buf), (double) value);
		buf.rewind();
		assertEquals(adapter.getInt(buf), (int) value);
		buf.rewind();
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
