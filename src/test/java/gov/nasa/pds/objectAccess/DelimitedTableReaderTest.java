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
import static org.testng.Assert.assertNull;

import gov.nasa.arc.pds.xml.generated.FieldDelimited;
import gov.nasa.arc.pds.xml.generated.GroupFieldDelimited;
import gov.nasa.arc.pds.xml.generated.Offset;
import gov.nasa.arc.pds.xml.generated.RecordDelimited;
import gov.nasa.arc.pds.xml.generated.TableDelimited;
import gov.nasa.pds.label.object.FieldType;
import gov.nasa.pds.label.object.TableRecord;
import gov.nasa.pds.objectAccess.table.DelimiterType;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.BigInteger;
import java.util.List;

import org.testng.annotations.Test;

import au.com.bytecode.opencsv.CSVWriter;

public class DelimitedTableReaderTest {	
	private TableDelimited table;
	private File dataFile;	
	private String[] line1 = new String[] {"1234", "1234.0", "123.40", "test1_1", "test2_1"};
	private String[] line2 = new String[] {"9876", "9876.0", "987.60", "test1_2", "test2_2"};
	
	public DelimitedTableReaderTest() throws Exception {
		createTable();
		createDataFile();
	}
	
	@Test
	public void testDelimitedTableReader() throws Exception {		
		TableReader reader = new TableReader(table, dataFile);						
		assertEquals(reader.getFields().length, 5);
		
		TableRecord record = reader.readNext();
		assertEquals(record.findColumn("field1"), 1);	
		assertEquals(record.getInt("field1"), 1234);
		assertEquals(record.getFloat("field2"), 1234.0, Float.MIN_VALUE);
		assertEquals(record.getDouble("field3"), 123.40, Double.MIN_VALUE);				
		assertEquals(record.getString(4), "test1_1");
		assertEquals(record.getString(5), "test2_1");
			
		record = reader.readNext();
		assertEquals(record.findColumn("field4"), 4);	
		assertEquals(record.getInt("field1"), 9876);		
		assertEquals(record.getFloat("field2"), 9876.0, Float.MIN_VALUE);
		assertEquals(record.getDouble("field3"), 987.60, Double.MIN_VALUE);				
		assertEquals(record.getString(4), "test1_2");
		assertEquals(record.getString(5), "test2_2");
		
		assertNull(reader.readNext());
					
		TableRecord rec1 = reader.getRecord(1);
		assertEquals(rec1.getFloat("field2"), 1234.0, Float.MIN_VALUE);
		
		TableRecord rec2 = reader.getRecord(2);
		assertEquals(rec2.getFloat("field2"), 9876.0, Float.MIN_VALUE);
		
		assertNull(reader.readNext());	
	}
	
	@Test(expectedExceptions=IllegalArgumentException.class)
	public void testBadRowNumber() throws Exception {
		TableReader reader = new TableReader(table, dataFile);
		reader.getRecord(0);		
	}
	
	@Test(expectedExceptions=IllegalArgumentException.class)
	public void testOutOfRangeRowNumber() throws Exception {
		TableReader reader = new TableReader(table, dataFile);
		reader.getRecord(11);
	}
	
	private void createTable() {
		FieldDelimited f1 = new FieldDelimited();
		f1.setName("field1");
		f1.setDataType(FieldType.ASCII_INTEGER.getXMLType());
				
		FieldDelimited f2 = new FieldDelimited();
		f2.setName("field2");
		f2.setDataType(FieldType.ASCII_REAL.getXMLType());
		
		FieldDelimited f3 = new FieldDelimited();
		f3.setName("field3");
		f3.setDataType(FieldType.ASCII_REAL.getXMLType());
		
		FieldDelimited f4 = new FieldDelimited();
		f4.setName("field4");
		f4.setDataType(FieldType.ASCII_STRING.getXMLType());
		
		GroupFieldDelimited group = new GroupFieldDelimited();
		group.setRepetitions(BigInteger.valueOf(2));
				
		List<Object> groupFields = group.getFieldDelimitedsAndGroupFieldDelimiteds();
		groupFields.add(f4);
	
		RecordDelimited rec = new RecordDelimited();
		List<Object> fields = rec.getFieldDelimitedsAndGroupFieldDelimiteds();
		fields.add(f1);
		fields.add(f2);
		fields.add(f3);
		fields.add(group);
		
		TableDelimited tbl = new TableDelimited();
		tbl.setFieldDelimiter(DelimiterType.COMMA.getXmlType());
		tbl.setRecordDelimiter(DelimiterType.CARRIAGE_RETURN_LINE_FEED.getXmlType());
		tbl.setRecordDelimited(rec);
		tbl.setRecords(BigInteger.valueOf(2));
		tbl.setOffset(getOffset(0));
		
		this.table = tbl;
	}
	
	private void createDataFile() throws Exception {
		String path = "./src/test/resources/dph_example_products/product_table_delimited";
		File dataFile = new File(path, "delimited_reader_test.csv");
		
		FileOutputStream os = new FileOutputStream(dataFile);				
		Writer writer = new BufferedWriter(new OutputStreamWriter(os, "US-ASCII"));				
		CSVWriter csvWriter = new CSVWriter(writer,
											DelimiterType.COMMA.getFieldDelimiter(),
											CSVWriter.DEFAULT_QUOTE_CHARACTER,
											DelimiterType.CARRIAGE_RETURN_LINE_FEED.getRecordDelimiter());
		csvWriter.writeNext(line1);
		csvWriter.writeNext(line2);		
		csvWriter.flush();
		this.dataFile = dataFile;
	}
	
	private Offset getOffset(long value) {
		Offset offset = new Offset();
		offset.setValue(BigInteger.valueOf(value));
		return offset;
	}
}
