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
import gov.nasa.arc.pds.xml.generated.FileAreaObservational;
import gov.nasa.arc.pds.xml.generated.ProductObservational;
import gov.nasa.pds.label.object.TableRecord;
import gov.nasa.pds.objectAccess.table.AdapterFactory;
import gov.nasa.pds.objectAccess.table.TableAdapter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Arrays;

import org.testng.annotations.Test;

import au.com.bytecode.opencsv.CSVReader;

public class TableWriterTest {
	private final static String CHAR_TABLE_LABEL_PATH ="./src/test/resources/1000";	
	private final static String CHAR_TABLE_LABEL_NAME = "Product_Table_Character.xml";	
	private final File charLabelFile = new File(CHAR_TABLE_LABEL_PATH, CHAR_TABLE_LABEL_NAME);
	
	private final Object[][][] binData = new Object[][][] {
		// value, expected
		{{234493158, new byte[] {0x0D, (byte) 0xFA, 0x14, (byte) 0xE6}}, {34004, new byte[] {(byte) 0x84, (byte) 0xD4}}, {34004, new byte[] {(byte) 0x84, (byte) 0xD4}}, {7.12055834903716  , new byte[] {0x40, 0x1C, 0x7B, 0x73, (byte) 0xA5, (byte) 0xD9, 0x7F, 0x57}}, {35, new byte[] {(byte) 0x23}}, {"CORPWS01", new byte[] {0x43, 0x4F, 0x52, 0x50, 0x57, 0x53, 0x30, 0x31}}, {1311768465305705501L, new byte[] {0x12, 0x34, 0x56, 0x78, 0x1A, 0x1B, 0x1C, 0x1D}}, {1.04f, new byte[] {0x3F, (byte) 0x85, (byte) 0x1E, (byte) 0xB8}}, {1.04f, new byte[] {0x3F, (byte) 0x85, (byte) 0x1E, (byte) 0xB8}}},
		{{234493158, new byte[] {0x0D, (byte) 0xFA, 0x14, (byte) 0xE6}}, {34004, new byte[] {(byte) 0x84, (byte) 0xD4}}, {34004, new byte[] {(byte) 0x84, (byte) 0xD4}}, {14.526978529605008, new byte[] {0x40, 0x2D, 0x0D, (byte) 0xD0, 0x21, 0x3C, (byte) 0xB2, 0x03}}, {37, new byte[] {(byte) 0x25}}, {"CORPWS01", new byte[] {0x43, 0x4F, 0x52, 0x50, 0x57, 0x53, 0x30, 0x31}}, {1311768465305705501L, new byte[] {0x12, 0x34, 0x56, 0x78, 0x1A, 0x1B, 0x1C, 0x1D}}, {1.04f, new byte[] {0x3F, (byte) 0x85, (byte) 0x1E, (byte) 0xB8}}, {1.04f, new byte[] {0x3F, (byte) 0x85, (byte) 0x1E, (byte) 0xB8}}}
	};	
	private final Object[][][] charData = new Object[][][] {		 
		 {{"91"}, {"0.088"}, {"91.06951"}, {"5.156"}, {"4.7" }, {"0.01234"}, {"5.12345" }, {"100.8765"}, {"4314.6"}, {"SS091AA00R6M1.IMG"}}, 
		 {{"92"}, {"0.084"}, {"1.06"    }, {"5.1"  }, {"19.1"}, {"0.012"  }, {"6.123456"}, {"100.8765"}, {"4314.6"}, {"SS091AA00R.IMG"}}    
	};	
	private final String[][] delimitedData = new String[][] {
			{"127", "117", "19", "6", "5", "5", "7", "2", "3", "1", "2", "6", "11939"}, 
			{"28", "128", "57", "34", "49", "35", "30", "29", "19", "8", "19", "24", "11216"}, 
			{"29", "202", "139", "101", "96", "94", "74", "71", "73", "66", "70", "77", "10343"} 
	};
		
	@Test
	public void testCharacterTableWriter() throws IOException {
		Object[] data;		
		TableRecord record;
		ObjectAccess objectAccess = new ObjectAccess(CHAR_TABLE_LABEL_PATH);				
		ProductObservational product = getProduct(objectAccess, charLabelFile);			
		FileAreaObservational fileArea = getFileArea(product);		
		File dataFile = getDataFile(fileArea, CHAR_TABLE_LABEL_PATH); 
		OutputStream os = new FileOutputStream(dataFile);
		
		Object table = getTableObject(objectAccess, fileArea);		
		TableAdapter adapter = getTableAdapter(table);
		int cols = adapter.getFieldCount();
		int rows = 2;		
						
		TableWriter tableWriter = null;		
		try {				
			tableWriter = new TableWriter(table, os);
			for (int i = 0; i < rows; i++) {
				record = tableWriter.createRecord();				
				for (int j = 0; j < cols; j++) {
					if (j > 0) {
						record.setString(",");
					}					
					data = charData[i][j];
					String value = (String) data[0];
					record.setString(j+1, value);					
				}				
				tableWriter.write(record);					
			}	
			tableWriter.flush();
		} finally {			
			if (tableWriter != null) {
				tableWriter.close();				
			}				
		}		
		 
		byte[] bytes = new byte[adapter.getRecordLength()];		
		InputStream in = new FileInputStream(dataFile);
		
		// Read data file
		for (int i = 0; i < rows; i++) {
			in.read(bytes);				
			for (int j = 0; j < cols; j++) {				
				data = charData[i][j];				
				int offset = adapter.getField(j).getOffset(); 				
				byte[] actual = Arrays.copyOfRange(bytes, offset, (offset + adapter.getField(j).getLength()));	
				assertEquals(new String(actual, Charset.forName("US-ASCII")).trim(), (String) data[0]);				
			}
		}
		
		dataFile.deleteOnExit();
		in.close();
	}	
		
	@Test
	public void testBinaryTableWriter() throws IOException {				
		Object[] data;
		TableRecord record;
		TableWriter writer = null;
		String path = "./src/test/resources/1000";
		File labelFile = new File(path, "Table_Writer_Test.xml");
		
		ObjectAccess objectAccess = new ObjectAccess(path);				
		ProductObservational product = getProduct(objectAccess, labelFile);			
		FileAreaObservational fileArea = getFileArea(product);				
		File dataFile = getDataFile(fileArea, path);
		OutputStream os = new FileOutputStream(dataFile);
		
		Object table = getTableObject(objectAccess, fileArea);
		TableAdapter adapter = getTableAdapter(table);		
		int cols = adapter.getFieldCount();
		int rows = adapter.getRecordCount();		
		
		try {					
			writer = new TableWriter(table, os);										
			record = writer.createRecord();
			record.setInt(1, 234493158);
			record.setShort(2, (short) 34004);
			record.setShort(3, (short) 34004);
			record.setDouble(4, 7.12055834903716);						
			record.setByte(5, (byte) 0x23);
			record.setString(6, "CORPWS01");
			record.setLong(7, 1311768465305705501L);
			record.setFloat(8, 1.04f);
			record.setFloat(9, 1.04f);			
			writer.write(record);			
			
			record = writer.createRecord();
			record.setInt(1, 234493158);			
			record.setShort(2, (short) 34004);
			record.setShort(3, (short) 34004);			
			record.setDouble(4, 14.526978529605008);
			record.setByte(5, (byte) 0x25);
			record.setString(6, "CORPWS01");
			record.setLong(7, 1311768465305705501L);
			record.setFloat(8, 1.04f);
			record.setFloat(9, 1.04f);			
			writer.write(record);		
			writer.flush();
		} finally {			
			if (writer != null) {
				writer.close();
			}	
		}
				
		byte[] bytes = new byte[adapter.getRecordLength()];				
		InputStream in = new FileInputStream(dataFile);
		
		// Read data file 
		for (int i = 0; i < rows; i++) {			
			in.read(bytes);			
			for (int j = 0; j < cols; j++) {				
				data = binData[i][j];				
				int offset = adapter.getField(j).getOffset();				
				byte[] actual = Arrays.copyOfRange(bytes, offset, (offset + adapter.getField(j).getLength()));
				assertEquals(actual, (byte[]) data[1]);				
			}
		}
		
		dataFile.deleteOnExit();
		in.close();
	}
		
	@Test
	public void testDelimitedTableWriter()  throws IOException {									
		TableRecord record;
		TableWriter tableWriter = null;
		String path = "./src/test/resources/1000";
		File labelFile = new File(path, "Product_Table_Delimited.xml");
		
		ObjectAccess objectAccess = new ObjectAccess(path);				
		ProductObservational product = getProduct(objectAccess, labelFile);			
		FileAreaObservational fileArea = getFileArea(product);
		Object table = getTableObject(objectAccess, fileArea);
								
		File dataFile = getDataFile(fileArea, path);
		FileOutputStream os = new FileOutputStream(dataFile);				
		Writer writer = new BufferedWriter(new OutputStreamWriter(os, "US-ASCII"));
		
		TableAdapter adapter = getTableAdapter(table);
		int rows = adapter.getRecordCount();
		int cols = adapter.getFieldCount();
		
		try {	
			tableWriter = new TableWriter(table, writer);			
			for (int i = 0; i < rows; i++) {
				record = tableWriter.createRecord();
				for (int j = 0; j < cols; j++) {					
					record.setString(j + 1, delimitedData[i][j]);					
				}
				tableWriter.write(record);
			}
			tableWriter.flush();
		} finally {			
			if (tableWriter != null) {
				tableWriter.close();
			} 			
		}
		
		// Read in the delimited data file
		BufferedReader buffer = new BufferedReader(new FileReader(dataFile));
		CSVReader reader = new CSVReader(buffer);		
		for (int i = 0; i < rows; i++) {
			String[] line = reader.readNext();			
			for (int j = 0; j < cols; j++) {				
				assertEquals(line[j], delimitedData[i][j]);
			}
		}
		
		dataFile.deleteOnExit();
		buffer.close();
		reader.close();
	}
	
	@Test(expectedExceptions={UnsupportedCharsetException.class})
	public void testBadCharset() throws IOException {		
		ObjectAccess objectAccess = new ObjectAccess(CHAR_TABLE_LABEL_PATH);				
		ProductObservational product = getProduct(objectAccess, charLabelFile);			
		FileAreaObservational fileArea = getFileArea(product);		
		File dataFile = getDataFile(fileArea, CHAR_TABLE_LABEL_PATH); 
		OutputStream os = new FileOutputStream(dataFile);		
		Object table = getTableObject(objectAccess, fileArea);		
    	new TableWriter(table, os, "BAD_CHARSET_NAME");
	}
	
	private TableAdapter getTableAdapter(Object table) {
		 return AdapterFactory.INSTANCE.getTableAdapter(table);
	}
	
	private Object getTableObject(ObjectAccess objectAccess, FileAreaObservational fileArea) {
		return objectAccess.getTableObjects(fileArea).get(0);
	}
	
	private FileAreaObservational getFileArea(ProductObservational product) {
		return product.getFileAreaObservationals().get(0);
	}
	
	private ProductObservational getProduct(ObjectAccess objectAccess, File labelFile) {		
		ProductObservational product = null;
		try {
			product = objectAccess.getProduct(labelFile, ProductObservational.class);
		} catch (gov.nasa.pds.objectAccess.ParseException e) {
			// error
		}
		return product;
	}	
		
	private File getDataFile(FileAreaObservational fileArea, String path) throws FileNotFoundException {		
		return new File(path, "TEST_" + fileArea.getFile().getFileName());				
	}
}
