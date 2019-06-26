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

import static org.testng.Assert.assertEquals;
import gov.nasa.pds.label.object.FieldDescription;
import gov.nasa.pds.label.object.FieldType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class FixedTableRecordTest {
		
	private Map<String, Integer> map = new HashMap<String, Integer>();;
	private List<FieldDescription> fields = new ArrayList<FieldDescription>();	
	
	public  FixedTableRecordTest() {
		createFieldMap();
		createFields();		
	}
	
	@Test(dataProvider="integerTests")	
	public void testGetIntegers(byte[] value, String name, long expectedValue) {		
		FixedTableRecord rec = new FixedTableRecord(value, map, fields.toArray(new FieldDescription[fields.size()]));
		
		assertEquals(rec.getLong(name), expectedValue);
		assertEquals(rec.getFloat(name), (float) expectedValue);
		assertEquals(rec.getDouble(name), (double) expectedValue);
		
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
	
	@SuppressWarnings("unused")
	@DataProvider(name="integerTests")
	private Object[][] getIntegerTests() {
		return new Object[][] {				
				{ new byte[] { 0x12, 0x34, 0x56, 0x78, 0x1A, 0x1B }, "field1", 0x12},
				{ new byte[] { 0x34, 0x12, 0x56, 0x78, 0x1A, 0x1B }, "field1", 0x34 },
				
				{ new byte[] { 0x12, 0x34, 0x56, 0x78, 0x1A, 0x1B, 0x1C, 0x1D, 0x1E }, "field2", 0x3456781A1B1C1D1EL },
				{ new byte[] { 0x12, 0x56, 0x34, 0x78, 0x1A, 0x1B, 0x1C, 0x1D, 0x1E }, "field2", 0x5634781A1B1C1D1EL },
				
				{ new byte[] { 0x12, 0x34, 0x56, 0x78, 0x1A, 0x1B, 0x1C, 0x1D, 0x1E, 0x1F, 0x21 } , "field3", 0x1F21 },
				{ new byte[] { 0x12, 0x34, 0x56, 0x78, 0x1A, 0x1B, 0x1C, 0x1D, 0x1E, 0x21, 0x1F } , "field3", 0x211F },
		};
	}
	
	@Test	
	public void testFindColumn() {
		byte[] value = {0x12, 0x34, 0x56, 0x78, 0x1A};
		FixedTableRecord rec = new FixedTableRecord(value, map, fields.toArray(new FieldDescription[fields.size()]));
		assertEquals(rec.findColumn("field5"), 5);
		assertEquals(rec.findColumn("field3"), 3);
	}
	
	@Test	
	public void testSetLong() {
				
	}
	
	@Test(dataProvider="IndexOutOfRangeTests", expectedExceptions=ArrayIndexOutOfBoundsException.class)	
	public void testIndexOutOfRange(int index) {
		byte[] value = {0x12, 0x34, 0x56, 0x78, 0x1A};
		FixedTableRecord rec = new FixedTableRecord(value, map, fields.toArray(new FieldDescription[fields.size()]));		
		rec.getInt(index);		
	}
	
	@SuppressWarnings("unused")
	@DataProvider(name="IndexOutOfRangeTests")
	private Integer[][] getIndexOutOfRangeTests() {
		return new Integer[][] {
				// field index
				{ 0 },
				{ fields.size()+1 }
		};
	}
	
	@Test(expectedExceptions=IllegalArgumentException.class)
	public void testIllegalFieldName() {
		byte[] value = {0x12, 0x34, 0x56, 0x78, 0x1A};
		FixedTableRecord rec = new FixedTableRecord(value, map, fields.toArray(new FieldDescription[fields.size()]));				
		rec.getShort("Bad_Field");
	}
	
	private void createFieldMap() {			
		map.put("field1", 1);
		map.put("field2", 2);
		map.put("field3", 3);
		map.put("field4", 4);
		map.put("field5", 5);
	}
	
	private void createFields() {
		fields.add(createFieldDescription(1, "field1", 0, FieldType.UNSIGNEDBYTE));
		fields.add(createFieldDescription(8, "field2", 1, FieldType.UNSIGNEDMSB8));
		fields.add(createFieldDescription(2, "field3", 9, FieldType.UNSIGNEDMSB2));
		fields.add(createFieldDescription(4, "field4", 11, FieldType.UNSIGNEDMSB4));
		fields.add(createFieldDescription(8, "field5", 15, FieldType.IEEE754MSBDOUBLE));		
	}
	
	private FieldDescription createFieldDescription(int length, String name, int offset, FieldType type) {		
		FieldDescription field = new FieldDescription();
		field.setLength(length);
		field.setName(name);
		field.setOffset(offset);
		field.setType(type);		
		return field;
	}
	
}
