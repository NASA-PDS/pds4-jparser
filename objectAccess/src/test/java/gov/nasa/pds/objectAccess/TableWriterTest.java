package gov.nasa.pds.objectAccess;

import static org.testng.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;

import org.testng.annotations.Test;

import au.com.bytecode.opencsv.CSVReader;

public class TableWriterTest {
	
	private final Object[][][] binData = new Object[][][] {
			// offset, length, value, expected
			{{1, 4, 234493158, new byte[] {0x0D, (byte) 0xFA, 0x14, (byte) 0xE6}}, {5, 8, 7.12055834903716  , new byte[] {0x40, 0x1C, 0x7B, 0x73, (byte) 0xA5, (byte) 0xD9, 0x7F, 0x57}}, {13, 4, 1.04f, new byte[] {0x3F, (byte) 0x85, (byte) 0x1E, (byte) 0xB8}}, {17, 2, 34004, new byte[] {(byte) 0x84, (byte) 0xD4}}, {19, 1, 35, new byte[] {(byte) 0x23}}, {20, 8, "CORPWS01", new byte[] {0x43, 0x4F, 0x52, 0x50, 0x57, 0x53, 0x30, 0x31}}},
			{{1, 4, 234493158, new byte[] {0x0D, (byte) 0xFA, 0x14, (byte) 0xE6}}, {5, 8, 14.526978529605008, new byte[] {0x40, 0x2D, 0x0D, (byte) 0xD0, 0x21, 0x3C, (byte) 0xB2, 0x03}}, {13, 4, 1.04f, new byte[] {0x3F, (byte) 0x85, (byte) 0x1E, (byte) 0xB8}}, {17, 2, 34004, new byte[] {(byte) 0x84, (byte) 0xD4}}, {19, 1, 37, new byte[] {(byte) 0x25}}, {20, 8, "CORPWS01", new byte[] {0x43, 0x4F, 0x52, 0x50, 0x57, 0x53, 0x30, 0x31}}}
	};
	
	
	private Object[][][] charData = new Object[][][] {
		 // offset, length, value, align
		 {{1, 3, "91", "right"}, {5, 5 ,"0.088", "right"}, {11, 9, "91.06951", "right"}, {21, 5, "5.156", "right"}, {27, 8, "4.7691", "right" }, {36, 19, "\"091A990R6M1.IMG\"", "left"}  , {56, 7, "4314.6", "right"}},
		 {{1, 3, "92", "right"}, {5, 5, "0.084", "right"}, {11, 9, "91.06951", "right"}, {21, 5, "5.156", "right"}, {27, 8, "19.1305", "right"}, {36, 19, "\"SS091A990R6M1.IMG\"", "left"}, {56, 7, "15300.0", "right"}}
	};
	
	private final String[][] delimitedData = new String[][] {
			{"127", "117", "19", "6", "5", "5", "7", "2", "3", "1", "2", "6", "11939"}, 
			{"28", "128", "57", "34", "49", "35", "30", "29", "19", "8", "19", "24", "11216"}, 
			{"29", "202", "139", "101", "96", "94", "74", "71", "73", "66", "70", "77", "10343"} 
	};
		
	@Test
	public void testCharacterTableWriter() throws IOException {
		TableRecord record;
		File file;
		Object[] data;
		int offset;
		int length;
		int rows = 2;
		int cols = 7;
		int recordLength = 64;
		TableWriter writer = null;
		byte[] bytes = new byte[recordLength];		
		byte[] dst = null;
		
		// Write a fixed-width text data file
		try {	
			file = new File("./src/test/resources/dph_example_products/CharTableWriterTest.tab");
			OutputStream os = new FileOutputStream(file);
			writer = new TableWriter(os, recordLength, null);						
			writer.setType("character");
			
			for (int i = 0; i < rows; i++) {
				record = writer.createRecord();				
				for (int j = 0; j < cols; j++) {		
					data = charData[i][j];
					record.add(Integer.parseInt(data[0].toString()) - 1, (String) data[2], Integer.parseInt(data[1].toString()), (String) data[3]);					
			
					if (j < cols - 1) {
						record.add(",");
					}	
				}
				writer.write(record);					
			}					
			writer.flush();
		} finally {			
			if (writer != null) {
				writer.close();
			}	
		}
		
		// Read the data file to test values
		InputStream in = new FileInputStream(file);
		for (int i = 0; i < rows; i++) {
			in.read(bytes);				
			for (int j = 0; j < cols; j++) {				
				data = charData[i][j];				
				offset = Integer.parseInt(data[0].toString()) - 1;
				length = Integer.parseInt(data[1].toString());
				dst = Arrays.copyOfRange(bytes, offset, (offset + length));	
				assertEquals(new String(dst).trim(), (String) data[2]);				
			}
		}
		
		file.deleteOnExit();
	}	
	
	
	@Test
	public void testBinaryTableWriter1() throws IOException {
		// Creates the table writer by passing the output stream, record length and charset name
		createBinaryDataFile(true);
	}
	
	@Test
	public void testBinaryTableWriter2() throws IOException {
		// Creates the table writer by passing the output stream and record length
		createBinaryDataFile(false);
	}
	
	private void createBinaryDataFile(boolean flag) throws IOException {		
		TableRecord record;
		Object[] data;
		File file; 
		int offset;
		int length;				
		int rows = 2;
		int cols = 6;		
		int recordLength = 27;
		TableWriter writer = null;
		byte[] bytes = new byte[recordLength];		
		byte[] dst = null;	
		
		try {					
			file = new File("./src/test/resources/dph_example_products/BinTableWriterTest.dat");
			OutputStream os = new FileOutputStream(file);
			if (flag) {
				writer = new TableWriter(os, recordLength, null);					
			} else {
				writer = new TableWriter(os, recordLength);
			}	
			
			writer.setType("binary");		
			record = writer.createRecord();
			record.addInt(0, 234493158);
			record.addDouble(4, 7.12055834903716);
			record.addFloat(12, 1.04f);
			record.addShort(16, (short) 34004);
			record.addByte(18, (byte) 0x23);
			record.add(19, "CORPWS01", 8);
			writer.write(record);			
			
			record = writer.createRecord();
			record.addInt(0, 234493158);
			record.addDouble(4, 14.526978529605008);
			record.addFloat(12, 1.04f);
			record.addShort(16, (short) 34004);
			record.addByte(18, (byte) 0x25);
			record.add(19, "CORPWS01", 8);
			writer.write(record);						
			writer.flush();
		} finally {			
			if (writer != null) {
				writer.close();
			}	
		}
		
		// Read the data file to test values
		InputStream in = new FileInputStream(file);
		for (int i = 0; i < rows; i++) {			
			in.read(bytes);			
			for (int j = 0; j < cols; j++) {				
				data = binData[i][j];				
				offset = Integer.parseInt(data[0].toString()) -1;
				length = Integer.parseInt(data[1].toString());
				dst = Arrays.copyOfRange(bytes, offset, (offset + length));
				assertEquals(dst, (byte[]) data[3]);				
			}
		}
		
		file.deleteOnExit();
	}
		
	@Test
	public void testDelimitedTableWriter1() throws IOException {
		// Creates the table writer by passing the writer object, field and record delimiters 
		createDelimitedDataFile(true);		
	}
	
	@Test
	public void testDelimitedTableWriter2() throws IOException {
		// Creates the table writer by passing the writer object
		createDelimitedDataFile(false);
	}
		
	private void createDelimitedDataFile(boolean flag) throws IOException {
		File file;
		TableRecord record;		
		TableWriter tableWriter = null;
		int cols = 13;
		int rows = 3;
		
		// Write a delimited data file
		try {			
			file = new File("./src/test/resources/dph_example_products/DelimitedTableWriterTest.csv");
			FileOutputStream os = new FileOutputStream(file);				
			Writer writer = new BufferedWriter(new OutputStreamWriter(os, "US-ASCII"));
			if (flag) {
				tableWriter = new TableWriter(writer, "comma");
			} else {
				tableWriter = new TableWriter(writer);
			}
			
			for (int i = 0; i < rows; i++) {
				record = tableWriter.createRecord();
				for (int j = 0; j < cols; j++) {					
					record.add(delimitedData[i][j]);			
				}
				tableWriter.write(record);
			}									
			tableWriter.flush();
		} finally {			
			if (tableWriter != null) {
				tableWriter.close();
			}	
		}
		
		// Reads in the delimited data file
		BufferedReader buffer = new BufferedReader(new FileReader(file));
		CSVReader reader = new CSVReader(buffer);
		for (int i = 0; i < rows; i++) {
			String[] line = reader.readNext();
			for (int j = 0; j < cols; j++) {				
				assertEquals(line[j], delimitedData[i][j]);
			}
		}
		
		file.deleteOnExit();
	}	
}
