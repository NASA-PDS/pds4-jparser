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
import static org.testng.Assert.assertTrue;
import gov.nasa.arc.pds.xml.generated.FileAreaObservational;
import gov.nasa.arc.pds.xml.generated.ProductObservational;
import gov.nasa.arc.pds.xml.generated.TableBinary;
import gov.nasa.arc.pds.xml.generated.TableCharacter;
import gov.nasa.arc.pds.xml.generated.TableDelimited;
import gov.nasa.pds.objectAccess.table.TableDelimitedAdapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.UnsupportedCharsetException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.testng.annotations.Test;

import au.com.bytecode.opencsv.CSVReader;

public class TableExporterTest {
	private static final String DELIMITED_LABEL_NAME = "Product_Table_Delimited.xml";		
	private static final String DELIMITED_LABEL_PATH= "./src/test/resources/1000/";
			
	private final String[][] dataObject= new String [][] {
			{"SOL" , "LTST"  , "LMST"      , "V"     , "DV+"  , "DV-"     , "DIR"        , "DDIR"     , "EXPOSURE_TIME" , "FILE_NAME"        },
			{" 91" , "0.088" , " 91.06951" , "5.156" , "0.42" , "0.42656" , "125.547152" , "  4.7691" , "15300.0"       , "SS091A990R6M1.IMG"},
			{" 91" , "0.088" , " 91.06951" , "5.156" , "0.42" , "0.42656" , "125.547152" , "  4.7691" , "15300.0"       , "SS091A990R6M1.IMG"},
			{" 91" , "0.088" , " 91.07029" , "5.155" , "0.42",  "0.42652" , "125.550546" , "  4.7692" , "15300.0"       , "SS091A990R6M1.IMG"},
			{" 91" , "0.089" , " 91.07105" , "5.155" , "0.42",  "0.42657" , "125.550344" , "  4.7692" , "15300.0"       , "SS091A990R6M1.IMG"},			
			{" 91" , "0.377" , " 91.35854" , "2.225" , "0.72" , "0.56432" , "147.854445" , " 19.1305" , " 4314.6"       , "SS091AA00R6M1.IMG"}			
	};
	
	private final String[][] groupedCharDataObject= new String [][] {
			{"X COORDINATE", "Y COORDINATE", "Value", "X COORDINATE", "Y COORDINATE", "Value"},
			{"+0.0000E+000" , "+0.0000E+000" , "+2.7756E-017" , "+9.2549E-002" , "+0.0000E+000" , "+2.7756E-017" },
			{"+0.0000E+000" , "+1.0980E-001" , "+2.7756E-017" , "+9.2549E-002" , "+1.0980E-001" , "+2.7756E-017" },
			{"+0.0000E+000" , "+2.1961E-001" , "+2.7756E-017" , "+9.2549E-002" , "+2.1961E-001" , "+2.7756E-017" },
			{"+0.0000E+000" , "+3.2941E-001" , "+2.7756E-017" , "+9.2549E-002" , "+3.2941E-001" , "+2.7756E-017" },			
			{"+0.0000E+000" , "+4.3922E-001" , "+2.7756E-017" , "+9.2549E-002" , "+4.3922E-001" , "+2.7756E-017" }			
	};
	
	private final Object[][] binDataObejct = new Object[][] {
			{ "234493158", "34004", "63144", "7.12055834903716"  , "0", "0", "1", "0", "0", "0", "1", "1", "35", "CORPWS01", "7.197063E-17", "1.04" },
			{ "234493159", "1256" , "63144", "14.526978529605008", "0", "0", "1", "0", "0", "1", "0", "1", "37", "CORPWS01", "7.197063E-17", "1.04" }
	};
	
	private final Object[][] delimitedDataObject = new Object[][] {
			{"1,27", "117", "19", "6", "5", "5", "7", "2", "3", "1", "2", "6", "11939"}, 
			{"28", "128", "57", "34", "49", "35", "30", "29", "19", "8", "19", "24", "11216"}, 
			{"29", "202", "139", "101", "96", "94", "74", "71", "73", "66", "70", "77", "10343"} 
	};
			
	@Test
	public void testExportCharacterTableToCSV() throws Exception {
		String fileName = "Product_Table_Character.xml";
		ObjectProvider objectAccess = new ObjectAccess(new File("./src/test/resources/1000/"));
		ProductObservational product = objectAccess.getObservationalProduct(fileName);
		FileAreaObservational fileArea = product.getFileAreaObservationals().get(0);
		List<TableCharacter> list = objectAccess.getTableCharacters(fileArea);
		File label = new File(FileUtils.toFile(objectAccess.getRoot()), fileName);
		File outputFile = new File(FileUtils.toFile(objectAccess.getRoot()), "char_table.csv");		
		exportToCSV(label, outputFile, list.get(0), 5, 10, dataObject, false);	
	}	
	
	@Test
	public void testExportGoupedFieldCharacterTableToCSV() throws Exception {
		String fileName = "Product_Table_Character_Grouped.xml";
		ObjectProvider objectAccess = new ObjectAccess(new File("./src/test/resources/1000/"));
		ProductObservational product = objectAccess.getObservationalProduct(fileName);
		FileAreaObservational fileArea = product.getFileAreaObservationals().get(0);
		List<TableCharacter> list = objectAccess.getTableCharacters(fileArea);				
		File label = new File(FileUtils.toFile(objectAccess.getRoot()), fileName);
		File outputFile = new File(FileUtils.toFile(objectAccess.getRoot()), "char_table_grouped.csv");		
		exportToCSV(label, outputFile, list.get(0), 6, 6, groupedCharDataObject, false);		
	}
	
	@Test
	public void testExportBinaryTableToCSV() throws Exception {
		createBinaryFile();		
		String fileName = "Binary_Table_Test.xml";
		ObjectProvider objectAccess = new ObjectAccess(new File("./src/test/resources/1000/"));
		ProductObservational product = objectAccess.getObservationalProduct(fileName);
		FileAreaObservational fileArea = product.getFileAreaObservationals().get(0);
		List<TableBinary> list = objectAccess.getTableBinaries(fileArea);									
		File label = new File(FileUtils.toFile(objectAccess.getRoot()), fileName);
		File outputFile = new File(FileUtils.toFile(objectAccess.getRoot()), "binary_table.csv");		
		exportToCSV(label, outputFile, list.get(0), 2, 16, binDataObejct, true);				
	}
	
	@Test
	public void testExportDelimitedTableToCSV() throws Exception {					
		ObjectProvider objectAccess = new ObjectAccess(new File(DELIMITED_LABEL_PATH));
		ProductObservational product = objectAccess.getObservationalProduct(DELIMITED_LABEL_NAME);
		FileAreaObservational fileArea = product.getFileAreaObservationals().get(0);
		List<TableDelimited> list = objectAccess.getTableDelimiteds(fileArea);
		File outputFile = new File(FileUtils.toFile(objectAccess.getRoot()), "delimited_table.csv");
		File label = new File(FileUtils.toFile(objectAccess.getRoot()), DELIMITED_LABEL_NAME);		
		exportToCSV(label, outputFile, list.get(0), 3, 13, delimitedDataObject, true);		
	}
		
	@Test
	public void testConvert() throws Exception {
		ObjectProvider provider = new ObjectAccess(new File(DELIMITED_LABEL_PATH));				
		ProductObservational product = provider.getObservationalProduct(DELIMITED_LABEL_NAME);
		File outputFile = new File(FileUtils.toFile(provider.getRoot()), "convert_test.csv");
		FileOutputStream os = new FileOutputStream(outputFile);		
		TableExporter exporter = ExporterFactory.getTableExporter(product.getFileAreaObservationals().get(0), provider);
		exporter.setExportType("CSV");		
		assertEquals(exporter.getExportType(), "CSV");		
		exporter.convert(os, 0);
		assertTrue(outputFile.exists());
		
		BufferedReader buffer = new BufferedReader(new FileReader(outputFile));
		CSVReader reader = new CSVReader(buffer);				
		assertEquals(reader.readAll().size()-1, delimitedDataObject.length);
		outputFile.deleteOnExit();
		buffer.close();
		reader.close();
	}
	
	@Test
	public void testGetTableFields() throws Exception {
		ObjectProvider provider = new ObjectAccess(new File(DELIMITED_LABEL_PATH));			
		ProductObservational product = provider.getObservationalProduct(DELIMITED_LABEL_NAME);
		FileAreaObservational fileArea = product.getFileAreaObservationals().get(0);
		TableDelimited table = provider.getTableDelimiteds(fileArea).get(0);
		TableDelimitedAdapter adapter = new TableDelimitedAdapter(table);		
		TableExporter exporter = ExporterFactory.getTableExporter(fileArea, provider);
		assertEquals(exporter.getTableFields(table).length, adapter.getFields().length);		
	}
	
	@Test(expectedExceptions=UnsupportedCharsetException.class)
	public void testSetDecoder() throws Exception {
		ObjectProvider provider = new ObjectAccess(new File(DELIMITED_LABEL_PATH));
		File label = new File(FileUtils.toFile(provider.getRoot()), DELIMITED_LABEL_NAME);
		TableExporter exporter = ExporterFactory.getTableExporter(label, 0);		
		exporter.setDecoder("BAD-CHARSET-NAME");
	}
	
	@Test(expectedExceptions=UnsupportedCharsetException.class)
	public void testSetEncoder() throws Exception {
		ObjectProvider provider = new ObjectAccess(new File(DELIMITED_LABEL_PATH));
		File label = new File(FileUtils.toFile(provider.getRoot()), DELIMITED_LABEL_NAME);
		TableExporter exporter = ExporterFactory.getTableExporter(label, 0);		
		exporter.setEncoder("BAD-CHARSET-NAME");
	}
	
	private void exportToCSV(File label, File outputFile, Object table,	int rows, int cols, Object[][] dataObject, boolean isSkip) throws Exception {	
		FileOutputStream os = new FileOutputStream(outputFile);
		TableExporter exporter = ExporterFactory.getTableExporter(label, 0);		
		exporter.setDecoder("US-ASCII");
		exporter.convert(table, os);
		assertTrue(outputFile.exists());
		
		BufferedReader buffer = new BufferedReader(new FileReader(outputFile));
		CSVReader reader = new CSVReader(buffer);
		boolean isTrim = (table instanceof TableDelimited) ? true : false;			
		String[] line;
		
		if (isSkip) {
			// Skip the column header row
			line = reader.readNext();
		}		
		for (int i = 0; i < rows; i++) {
			line = reader.readNext();
			for (int j = 0; j < cols; j++) {				
				assertEquals((isTrim) ? line[j].trim() : line[j], dataObject[i][j]);					
			}
		}
		
		outputFile.deleteOnExit();
		buffer.close();
		reader.close();	
	}
	
	/*
	 *  Creates a binary file with 2 records each 34 bytes long
	 */
	private void createBinaryFile() throws IOException {
		FileOutputStream os = new FileOutputStream(new File("./src/test/resources/1000/binary-table-test.dat"));				
		ByteBuffer buf =  ByteBuffer.allocate(68); 
		buf.putInt(234493158);
		buf.putShort((short) 34004);
		buf.putShort((short) 63144);
		buf.putDouble(7.12055834903716);
		buf.put((byte) 0x23);
		buf.put((byte) 0x23);
		buf.put(new byte[] {0x43, 0x4F, 0x52, 0x50, 0x57, 0x53, 0x30, 0x31});
		buf.putFloat(7.197063E-17f);
		buf.putFloat(1.04f);
		buf.putInt(234493159);				
		buf.putShort((short) 1256);
		buf.putShort((short) 63144);
		buf.putDouble(14.526978529605008);
		buf.put((byte) 0x25);
		buf.put((byte) 0x25);
		buf.put(new byte[] {0x43, 0x4F, 0x52, 0x50, 0x57, 0x53, 0x30, 0x31});
		buf.putFloat(7.197063E-17f);
		buf.putFloat(1.04f);		
		os.write(buf.array());
		os.close();
 	}
}
