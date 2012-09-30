package gov.nasa.pds.objectAccess;

import org.testng.Assert;
import org.testng.AssertJUnit;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ConverterTest {
	
	@SuppressWarnings("unused")
	@DataProvider(name="unsignedMSB4AsLongTest")
	private Object[][] getUnsignedMSB4AsLongTest() {
		return new Object[][] {
				{ new byte[] { 0x12, 0x34, 0x56, 0x78 }, 0, 0x12345678L },
				{ new byte[] { 0x11, 0x22, 0x33, 0x44 }, 0, 0x11223344L },	
				
				{ new byte[] { 0x11, 0x12, 0x34, 0x56, 0x78 }, 1, 0x12345678L }				
		};
	}
	
	@Test(dataProvider="unsignedMSB4AsLongTest")
	public void testGetUnsignedMSB4AsLong(byte[] buf, int offset, long expected) {		
		AssertJUnit.assertEquals(Converter.getUnsignedMSB4AsLong(buf, offset), expected);
	}
	
	@SuppressWarnings("unused")
	@DataProvider(name="unsignedMSB2AsLongTest")
	private Object[][] getUnsingedMSB2AsLongTest() {
		return new Object[][] {
				{ new byte[] { 0x12, 0x34 }, 0, 0x1234L },
				{ new byte[] { 0x34, 0x56 }, 0, 0x3456L },
				{ new byte[] { 0x56, 0x78 }, 0, 0x5678L },
		};
	}
	
	@Test(dataProvider="unsignedMSB2AsLongTest")
	public void testGetUnsignedMSB2AsLong(byte[] buf, int offset, long expected) {		
		Assert.assertEquals(Converter.getUnsignedMSB2AsLong(buf, offset), expected);
	}
	
	@SuppressWarnings("unused")
	@DataProvider(name="unsignedByteAsIntTest")
	private Object[][] getUnsignedByteAsIntTest() {
		return new Object[][] {
				{ new byte[] { 0x56 }, 0, 86},
				{ new byte[] { 0x78 }, 0, 120},		
				
				{ new byte[] { 0x56, 0x78 }, 1, 120},				
				
				{ new byte[] { 0x12, 0x34, 0x56, 0x78 }, 2, 86}
		};
	}
	
	@Test(dataProvider="unsignedByteAsIntTest")
	public void testGetUnsignedByteAsInt(byte[] buf, int offset, int expected) {		
		Assert.assertEquals(Converter.getUnsignedByteAsInt(buf, offset), expected);
	}
	
	@SuppressWarnings("unused")
	@DataProvider(name="IEEE745MSBDoubleAsDoubleTest")
	private Object[][] getIEEE745MSBDoubleAsDoubleTest() {
		return new Object[][] {
				{ new byte[] { 0x40, 0x1C, 0x7B, 0x73, (byte) 0xA5, (byte) 0xD9, 0x7F, 0x57 }, 0, 7.12055834903716},
				{ new byte[] { 0x3F, (byte) 0xB0, (byte) 0xAC, 0x01, (byte) 0x94, (byte) 0xEF, 0x54, (byte) 0xA5 }, 0, 6.5124605999999987E-2},
				{ new byte[] { 0x3F, 0x4B, (byte) 0xC2, 0x3C, 0x3C, (byte) 0xCF, (byte) 0xBD, 0x12 }, 0, 8.47129261840525457E-4 },
				{ new byte[] { 0x3F, (byte) 0xAB, 0x71, (byte) 0xB3, (byte) 0xB2, (byte) 0xB0, (byte) 0xA8, 0x10 }, 0, 5.3601852000000005E-2 },
				{ new byte[] { 0x46, 0x29, 0x3e, 0x59, 0x39, (byte) 0xA0, (byte) 0x8C, (byte) 0xEA}, 0, 1.0E30 }
		};
	}
	
	@Test(dataProvider="IEEE745MSBDoubleAsDoubleTest")
	public void testIEEE745MSBDoubleAsDouble(byte[] buf, int offset, double expected) {		
		Assert.assertEquals(Converter.getIEEE745MSBDoubleAsDouble(buf, offset), expected, Double.MIN_NORMAL);
	}
	
	@SuppressWarnings("unused")
	@DataProvider(name="IEEE745MSBSingleAsFloatTest")
	private Object[][] getIEEE745MSBSingleAsFloatTest() {
		return new Object[][] {
				{ new byte[] { 0x3F, (byte) 0x85, 0x1E, (byte) 0xB8 }, 0, 1.0399999618530273f},
				{ new byte[] { 0x40, 0x05, 0x1E, (byte) 0xB8 }, 0, 2.0799999237060547f}, 
				{ new byte[] { 0x45, 0x66, 0x59, (byte) 0xB4 }, 0, 3.6856064453125E3f},
				{ new byte[] { 0x45, 0x7D, 0x0E, 0x0E }, 0, 4.04887841796875E3f},
				{ new byte[] { 0x24, (byte) 0xA5, (byte) 0xF3, (byte) 0xF6}, 0 , 7.197062928785439E-17f} 
		};
	}
	
	@Test(dataProvider="IEEE745MSBSingleAsFloatTest")
	public void testIEEE745MSBSingleAsFloat(byte[] buf, int offset, float expected) {				
		Assert.assertEquals(Converter.getIEEE745MSBSingle(buf, offset), expected, Float.MIN_NORMAL);
	}
}
