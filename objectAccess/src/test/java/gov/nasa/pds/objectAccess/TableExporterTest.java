package gov.nasa.pds.objectAccess;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import gov.nasa.arc.pds.xml.generated.FileAreaObservational;
import gov.nasa.arc.pds.xml.generated.ProductObservational;
import gov.nasa.arc.pds.xml.generated.TableBinary;
import gov.nasa.arc.pds.xml.generated.TableCharacter;
import gov.nasa.arc.pds.xml.generated.TableDelimited;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import org.testng.annotations.Test;

import au.com.bytecode.opencsv.CSVReader;

public class TableExporterTest {
	
	private final String[][] dataObject= new String [][] {
			{"SOL" , "LTST"  , "LMST"      , "V"     , "DV+"  , "DV-"     , "DIR"        , "DDIR"     , "EXPOSURE TIME" , "FILE NAME"        },
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
		ObjectProvider objectAccess = new ObjectAccess(new File("./src/test/resources/dph_example_products/product_table_character/"));
		ProductObservational product = objectAccess.getObservationalProduct(fileName);
		FileAreaObservational fileArea = product.getFileAreaObservationals().get(0);
		List<TableCharacter> list = objectAccess.getTableCharacters(fileArea);
		File label = new File(objectAccess.getRoot().getAbsolutePath(), fileName);
		File outputFile = new File(objectAccess.getRoot().getAbsolutePath(), "char_table.csv");		
		exportToCSV(label, outputFile, list.get(0), 5, 10, dataObject, false);	
	}	
	
	@Test
	public void testExportGoupedFieldCharacterTableToCSV() throws Exception {
		String fileName = "Product_Table_Character_Grouped.xml";
		ObjectProvider objectAccess = new ObjectAccess(new File("./src/test/resources/dph_example_products/product_table_character_grouped/"));
		ProductObservational product = objectAccess.getObservationalProduct(fileName);
		FileAreaObservational fileArea = product.getFileAreaObservationals().get(0);
		List<TableCharacter> list = objectAccess.getTableCharacters(fileArea);				
		File label = new File(objectAccess.getRoot().getAbsolutePath(), fileName);
		File outputFile = new File(objectAccess.getRoot().getAbsolutePath(), "char_table_grouped.csv");		
		exportToCSV(label, outputFile, list.get(0), 6, 6, groupedCharDataObject, false);		
	}
	
	@Test
	public void testExportBinaryTableToCSV() throws Exception {
		//createBinaryFile();		
		String fileName = "Binary_Table_Test.xml";
		ObjectProvider objectAccess = new ObjectAccess(new File("./src/test/resources/dph_example_products/product_table_binary/"));
		ProductObservational product = objectAccess.getObservationalProduct(fileName);
		FileAreaObservational fileArea = product.getFileAreaObservationals().get(0);
		List<TableBinary> list = objectAccess.getTableBinaries(fileArea);									
		File label = new File(objectAccess.getRoot(), fileName);
		File outputFile = new File(objectAccess.getRoot().getAbsolutePath(), "binary_table.csv");		
		exportToCSV(label, outputFile, list.get(0), 2, 16, binDataObejct, true);				
	}
	
	@Test
	public void testExportDelimitedTableToCSV() throws Exception {					
		String fileName = "Product_Table_Delimited.xml";
		ObjectProvider objectAccess = new ObjectAccess(new File("./src/test/resources/dph_example_products/product_table_delimited/"));
		ProductObservational product = objectAccess.getObservationalProduct(fileName);
		FileAreaObservational fileArea = product.getFileAreaObservationals().get(0);
		List<TableDelimited> list = objectAccess.getTableDelimited(fileArea);
		File outputFile = new File(objectAccess.getRoot().getAbsolutePath(), "delimited_table.csv");
		File label = new File(objectAccess.getRoot().getAbsolutePath(), fileName);		
		exportToCSV(label, outputFile, list.get(0), 3, 13, delimitedDataObject, true);		
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
	
	// Creates a binary file with 2 records each 34 bytes long
	private void createBinaryFile() throws IOException {
		FileOutputStream os = new FileOutputStream(new File("./src/test/resources/dph_example_products/product_table_binary/binary-table-test.dat"));		
		
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
