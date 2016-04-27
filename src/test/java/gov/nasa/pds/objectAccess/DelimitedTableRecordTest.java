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
package gov.nasa.pds.objectAccess;

import static org.testng.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class DelimitedTableRecordTest {
	private int fieldCount = 5;
	private Map<String, Integer> map;
	
	public DelimitedTableRecordTest() {
		createFieldMap();
	}
	
	@Test
	public void testFindColumn() {
		DelimitedTableRecord rec = new DelimitedTableRecord(map, fieldCount);
		assertEquals(rec.findColumn("field5"), 5);
	}
	
	@Test
	public void testSetString() {
		DelimitedTableRecord rec1 = new DelimitedTableRecord(map, fieldCount);
		rec1.setString("field1", "test1"); // index 1
		rec1.setString("field3", "test3"); // index 3
		String[] value = rec1.getRecordValue();			
		assertEquals(value[0], "test1");			
		assertEquals(value[2], "test3");		
		
		DelimitedTableRecord rec2 = new DelimitedTableRecord(map, fieldCount);
		rec2.setString("field3", "test3"); // index 3
		rec2.setString("field1", "test1"); // index 1		
		value = rec2.getRecordValue();			
		assertEquals(value[0], "test1");			
		assertEquals(value[2], "test3");
	}
	
	@Test	
	public void testSetInt() {
		DelimitedTableRecord rec = new DelimitedTableRecord(map, fieldCount);
		rec.setInt("field2", Integer.MAX_VALUE); // index 2		
		rec.setInt("field1", Byte.MAX_VALUE);    // index 1
		String[] value = rec.getRecordValue();	
		
		assertEquals(Integer.parseInt(value[1]), Integer.MAX_VALUE);		
		assertEquals(Integer.parseInt(value[0]), Byte.MAX_VALUE);		
	}
	
	@Test
	public void testSetDouble() {
		DelimitedTableRecord rec = new DelimitedTableRecord(map, fieldCount);
		rec.setDouble("field3", Double.POSITIVE_INFINITY);  // index 3		
		rec.setDouble("field2", Double.MAX_VALUE);          // index 2				
		String[] value = rec.getRecordValue();	
		
		assertEquals(Double.parseDouble(value[2]), Double.POSITIVE_INFINITY); 
		assertEquals(Double.parseDouble(value[1]), Double.MAX_VALUE);
	}
	
	@Test
	public void testSetFloat() {
		DelimitedTableRecord rec = new DelimitedTableRecord(map, fieldCount);
		rec.setFloat("field5", Float.MIN_VALUE); // index 5
		rec.setFloat("field3", Float.MAX_VALUE); // index 3		
		String[] value = rec.getRecordValue();
				
		assertEquals(Float.parseFloat(value[4]), Float.MIN_VALUE);
		assertEquals(Float.parseFloat(value[2]), Float.MAX_VALUE);
	}
	
	@Test
	public void testSetLong() {
		DelimitedTableRecord rec = new DelimitedTableRecord(map, fieldCount);		
		rec.setLong("field2", Short.MIN_VALUE); // index 2
		rec.setLong("field1", Long.MAX_VALUE);  // index 1
		rec.setLong("field5", Long.MIN_VALUE);  // index 5
		String[] value = rec.getRecordValue();				
		
		assertEquals(Long.parseLong(value[1]), Short.MIN_VALUE);
		assertEquals(Long.parseLong(value[0]), Long.MAX_VALUE);		
		assertEquals(Long.parseLong(value[4]), Long.MIN_VALUE);
	}
	
	@Test
	public void testSetShort() {
		DelimitedTableRecord rec = new DelimitedTableRecord(map, fieldCount);
		rec.setShort("field5", Short.MAX_VALUE); // index 5
		rec.setShort("field3", Byte.MIN_VALUE);  // index 3
		String[] value = rec.getRecordValue();		
		
		assertEquals(Short.parseShort(value[4]), Short.MAX_VALUE);
		assertEquals(Short.parseShort(value[2]), Byte.MIN_VALUE);
	}
	
	@Test
	public void testSetByte() {
		DelimitedTableRecord rec = new DelimitedTableRecord(map, fieldCount);
		rec.setByte("field3", Byte.MIN_VALUE); // index 3
		rec.setByte("field1", Byte.MAX_VALUE); // index 1
		String[] value = rec.getRecordValue();	
		
		assertEquals(Byte.parseByte(value[2]), Byte.MIN_VALUE);
		assertEquals(Byte.parseByte(value[0]), Byte.MAX_VALUE);
	}
	
	@Test
	public void testClear() {
		DelimitedTableRecord rec = new DelimitedTableRecord(map, fieldCount);		
		for (int i = 0; i < fieldCount; i++) {
			rec.setString(i+1, "test" + i+1);
		}		
		rec.clear();		
		String[] value = rec.getRecordValue();		
		for (int j = 0; j < fieldCount; j++) {
			assertEquals(value[j], "");
		}
	}
	
	@Test(expectedExceptions=UnsupportedOperationException.class)
	public void testSetStringNoIndex() {
		DelimitedTableRecord rec = new DelimitedTableRecord(map, fieldCount);
		rec.setString("No_Index");
	}
		
	@Test
	public void testGetString() {		
		String[] value = new String[] { "54", "4660", "36984440", "3.4028235E38", "2169921498196235828" };
		DelimitedTableRecord rec = new DelimitedTableRecord(map, fieldCount, value);		
		assertEquals(rec.getString(1), value[0]);
		assertEquals(rec.getString(2), value[1]);
		assertEquals(rec.getString("field4"), value[3]);
	}
	
	@Test(dataProvider="IntegerTests")
	public void testIntegersGet(String name, String[] value, long expectedValue) {
		DelimitedTableRecord rec = new DelimitedTableRecord(map, fieldCount, value);
		
		assertEquals(rec.getLong(name), expectedValue);
		assertEquals(rec.getDouble(name), (double) expectedValue);
		assertEquals(rec.getFloat(name), (float) expectedValue);
		
		if (Integer.MIN_VALUE <= expectedValue && expectedValue <= Integer.MAX_VALUE) {
			assertEquals(rec.getInt(name), (int) expectedValue);
		}
		if (Short.MIN_VALUE <= expectedValue && expectedValue <= Short.MAX_VALUE) {
			assertEquals(rec.getShort(name), (short) expectedValue);
		}
		if (Byte.MIN_VALUE <= expectedValue && expectedValue <= Byte.MAX_VALUE) {
			assertEquals(rec.getByte(name), (byte) expectedValue);
		}		
	}
	
	@Test(dataProvider="ByteOutOfRangeTests", expectedExceptions={NumberFormatException.class})
	public void testByteOutOfRange(String[] value) {				
		DelimitedTableRecord rec = new DelimitedTableRecord(map, fieldCount, value);
		rec.getByte(1);		
	}
	
	@Test(dataProvider="ShortOutOfRangeTests", expectedExceptions={NumberFormatException.class})
	public void testShortOutOfRange(String[] value) {				
		DelimitedTableRecord rec = new DelimitedTableRecord(map, fieldCount, value);
		rec.getShort(1);		
	}
	
	@Test(dataProvider="IndexOutOfRangeTests", expectedExceptions=ArrayIndexOutOfBoundsException.class)
	public void testIndexOutOfRange(int index) {
		DelimitedTableRecord rec = new DelimitedTableRecord(map, fieldCount);
		rec.setString(index, "Bad_Index");
	}
	
	@Test(expectedExceptions=IllegalArgumentException.class)
	public void testIllegalFieldName() {		
		DelimitedTableRecord rec = new DelimitedTableRecord(map, fieldCount);
		rec.getInt("Bad_Field");
	}
	
	@SuppressWarnings("unused")
	@DataProvider(name="ShortOutOfRangeTests")
	private Object[][] getShortOutOfRangeTests() {		
		return new Object[][] {
				// String[] value
				{ new String[] { "-32769" }},
				{ new String[] { "32768"  }},
		};
	}
	
	@SuppressWarnings("unused")
	@DataProvider(name="ByteOutOfRangeTests")
	private Object[][] getByteOutOfRangeTests() {		
		return new Object[][] {
				// String[] value
				{ new String[] { "-129" }},
				{ new String[] { "128"  }},
		};
	}
	
	@SuppressWarnings("unused")
	@DataProvider(name="IndexOutOfRangeTests")
	private Integer[][] getIndexOutOfRangeTests() {
		return new Integer[][] {
				// field index
				{ 0 },
				{ fieldCount+1 }
		};
	}
	
	@SuppressWarnings("unused")
	@DataProvider(name="IntegerTests")
	private Object[][] getIntegerTests() {
		return new Object[][] {
				// field name, record value, expected value
				{ "field1" , new String[] { "54", "4660", "36984440", "2169921498196235828", "9223372036854775807"},  54},
				{ "field2" , new String[] { "54", "4660", "36984440", "2169921498196235828", "9223372036854775807"},  4660},
				{ "field3" , new String[] { "54", "4660", "36984440", "2169921498196235828", "9223372036854775807"},  36984440},
				{ "field4" , new String[] { "54", "4660", "36984440", "2169921498196235828", "9223372036854775807"},  2169921498196235828L},
				{ "field5" , new String[] { "54", "4660", "36984440", "2169921498196235828", "9223372036854775807"},  9223372036854775807L}
		};
	}
	
	private void createFieldMap() {
		map = new HashMap<String, Integer>();		
		map.put("field1", 1);
		map.put("field2", 2);
		map.put("field3", 3);
		map.put("field4", 4);
		map.put("field5", 5);
	}
}
