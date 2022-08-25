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
import gov.nasa.arc.pds.xml.generated.FieldBinary;
import gov.nasa.arc.pds.xml.generated.FieldBit;
import gov.nasa.arc.pds.xml.generated.FieldCharacter;
import gov.nasa.arc.pds.xml.generated.FieldLength;
import gov.nasa.arc.pds.xml.generated.FieldLocation;
import gov.nasa.arc.pds.xml.generated.FileAreaObservational;
import gov.nasa.arc.pds.xml.generated.Offset;
import gov.nasa.arc.pds.xml.generated.PackedDataFields;
import gov.nasa.arc.pds.xml.generated.ProductObservational;
import gov.nasa.arc.pds.xml.generated.RecordBinary;
import gov.nasa.arc.pds.xml.generated.RecordCharacter;
import gov.nasa.arc.pds.xml.generated.RecordLength;
import gov.nasa.arc.pds.xml.generated.TableBinary;
import gov.nasa.arc.pds.xml.generated.TableCharacter;
import gov.nasa.pds.label.object.FieldType;
import gov.nasa.pds.label.object.TableRecord;
import gov.nasa.pds.objectAccess.DataType.NumericDataType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigInteger;

import org.apache.commons.io.FileUtils;
import org.testng.annotations.Test;

public class TableReaderTest {
	// character table meta data
	private String[][][] charData = new String[][][] {
			// offset, length, value, align, data type, field name, field number
			{{"1", "3", "91", "right", "ASCII_Integer", "SOL", "1"}, {"5", "5" ,"0.088", "right", "ASCII_Real", "LTST", "2"}},
			{{"1", "3", "92", "right", "ASCII_Integer", "SOL", "1"}, {"5", "5", "0.084", "right", "ASCII_Real", "LTST", "2"}}
		};

	// binary table meta data
	private final String[][][] binData = new String[][][] {
			// offset, length, value, data type, field name
			{{"1", "4", "234493158", "UnsignedMSB4", "SCLK_SECONDS"}, {"5", "8", "7.12055834903716"  , "IEEE754MSBDouble", "ROTATION_MOTOR_POSITION"}, {"13", "4", "1.04", "IEEE754MSBSingle", "TIME"}, {"17", "2", "34004", "UnsignedMSB2", "SCLK_SUBSECONDS"}, {"19", "1", "35", "UnsignedByte", "REVOLVE_MOTOR_CONTROLLER_STATUS"}, {"20", "8", "CORPWS01", "ASCII_String", "SCET"}},
			{{"1", "4", "234493158", "UnsignedMSB4", "SCLK_SECONDS"}, {"5", "8", "14.526978529605008", "IEEE754MSBDouble", "ROTATION_MOTOR_POSITION"}, {"13", "4", "1.04", "IEEE754MSBSingle", "TIME"}, {"17", "2", "34004", "UnsignedMSB2", "SCLK_SUBSECONDS"}, {"19", "1", "37", "UnsignedByte", "REVOLVE_MOTOR_CONTROLLER_STATUS"}, {"20", "8", "CORPWS01", "ASCII_String", "SCET"}}
		};

	// bitField data file value
	private final String[] bitFieldData = new String[] {"35", "37"};

	// bitField expected value
	private final String[][] bitFieldExpected= new String[][] {
			{"0", "0", "1", "0", "0", "0", "1", "1"},
			{"0", "0", "1", "0", "0", "1", "0", "1"}
		};

	@Test
	public void testTableCharacterReader() throws Exception {
		ObjectAccess oa = new ObjectAccess(new File("./src/test/resources/dph_example_products/product_table_character/"));
		String label = "CharTableReader.xml";

		// Create the label
		ProductObservational product = createProductLabel(oa, label);

		FileAreaObservational fileArea = product.getFileAreaObservationals().get(0);
		TableCharacter table = oa.getTableCharacters(fileArea).get(0);
		File dataFile = new File(FileUtils.toFile(oa.getRoot()), fileArea.getFile().getFileName());
		RecordCharacter record = table.getRecordCharacter();
		int rows = table.getRecords().intValueExact();
		int cols = record.getFields().intValueExact();

		// Create the data file
		createDataFile(table, dataFile, rows, cols, false, false);

		// Access table fields
		TableReader reader = new TableReader(table, dataFile);
		TableRecord rec;

		for (int i = 0; i < rows; i++) {
			rec = reader.readNext();
			for (int j = 0; j < cols; j++) {
				String[] data  = charData[i][j];
				String value   = data[2];
				String type    = data[4];
				String colName = data[5];

				assertEquals(rec.getString(j + 1).trim(), value);
				assertEquals(rec.getString(colName).trim(), value);

				if (type.contains("Integer")) {
					assertEquals(rec.getInt(j + 1), Integer.parseInt(value));
					assertEquals(rec.getLong(j + 1), Long.parseLong(value));
				}

				if (type.contains("Real")) {
					assertEquals(rec.getFloat(j + 1), Float.parseFloat(value));
					assertEquals(rec.getDouble("LTST"), Double.parseDouble(value));
				}
			}
		}
		reader.close();

		dataFile.deleteOnExit();
		new File(FileUtils.toFile(oa.getRoot()), label).deleteOnExit();
	}

	@Test
	public void testTableBinaryReader() throws Exception {
		ObjectAccess oa = new ObjectAccess(new File("./src/test/resources/dph_example_products/product_table_binary/"));
		String label = "BinaryTableReader.xml";

		// Create the label
		ProductObservational product = createBinaryProductLabel(oa, label);

		FileAreaObservational fileArea = product.getFileAreaObservationals().get(0);
		TableBinary table = oa.getTableBinaries(fileArea).get(0);
		File dataFile = new File(FileUtils.toFile(oa.getRoot()), fileArea.getFile().getFileName());
		RecordBinary record = table.getRecordBinary();
		int rows = table.getRecords().intValueExact();
		int cols = record.getFields().intValueExact();

		// Create the data file
		createDataFile(table, dataFile, rows, cols, true, false);

		// Access table fields
		TableReader reader = new TableReader(table, dataFile);
		TableRecord rec;

		for (int i = 0; i < rows; i++) {
			rec = reader.readNext();

			for (int j = 0; j < cols; j++) {
				String[] data  = binData[i][j];
				String type    = data[3];
				String colName = data[4];
				String value   = data[2];

				assertEquals(rec.getString(j + 1).trim(), value);
				assertEquals(rec.getString(colName).trim(), value);

				if (!type.equals("ASCII_String")) {
					NumericDataType numericType = Enum.valueOf(NumericDataType.class, type);
					switch(numericType) {
						case IEEE754MSBSingle:
							assertEquals(rec.getFloat(j + 1), Float.parseFloat(value), Float.MIN_NORMAL);
							assertEquals(rec.getFloat(colName), Float.parseFloat(value), Float.MIN_NORMAL);
							assertEquals(rec.getDouble(j + 1), Float.parseFloat(value), Double.MIN_NORMAL);
							break;
						case IEEE754MSBDouble:
							assertEquals(rec.getDouble(j + 1), Double.parseDouble(value), Double.MIN_NORMAL);
							break;
						case UnsignedByte:
							assertEquals(rec.getByte(j + 1), Byte.parseByte(value));
							assertEquals(rec.getShort(j + 1), Short.parseShort(value));
							assertEquals(rec.getShort(colName), Short.parseShort(value));
							assertEquals(rec.getLong(colName), Long.parseLong(value));
							assertEquals(rec.getInt(colName), Integer.parseInt(value));
							assertEquals(rec.getDouble(j + 1), Double.parseDouble(value), Double.MIN_NORMAL);
							break;
						case UnsignedMSB2:
							assertEquals(rec.getInt(j + 1), Integer.parseInt(value));
							assertEquals(rec.getLong(colName), Long.parseLong(value));
							assertEquals(rec.getDouble(j + 1), Double.parseDouble(value), Double.MIN_NORMAL);
							break;
						case UnsignedMSB4:
							assertEquals(rec.getLong(j + 1), Long.parseLong(value));
							assertEquals(rec.getDouble(j + 1), Double.parseDouble(value), Double.MIN_NORMAL);
							break;
					}
				}
			}
		}

		dataFile.deleteOnExit();
		new File(FileUtils.toFile(oa.getRoot()), label).deleteOnExit();
		reader.close();
	}

	//@Test
	public void testBitFieldReader() throws Exception {
		ObjectAccess oa = new ObjectAccess(new File("./src/test/resources/dph_example_products/product_table_binary/"));
		String label = "BinaryTableReader.xml";

		// Create the label
		ProductObservational product = createBitFieldBinaryProductLabel(oa, label);

		FileAreaObservational fileArea = product.getFileAreaObservationals().get(0);
		TableBinary table = oa.getTableBinaries(fileArea).get(0);
		File dataFile = new File(FileUtils.toFile(oa.getRoot()), fileArea.getFile().getFileName());
		RecordBinary record = table.getRecordBinary();
		int rows = table.getRecords().intValueExact();
		int cols = record.getFields().intValueExact();

		// Create the data file
		createDataFile(table, dataFile, rows, cols, true, true);

		// Access table fields
		TableReader reader = new TableReader(table, dataFile);
		TableRecord rec;

		for (int i = 0; i < rows; i++) {
			rec = reader.readNext();

			for (int j = 0; j < 8; j++) {
				String value = bitFieldExpected[i][j];
				assertEquals(rec.getLong(j + 1), Long.parseLong(value));
				assertEquals(rec.getInt (j + 1), Integer.parseInt(value));
				assertEquals(rec.getString(j + 1), value);
				assertEquals(rec.getString("BIT " + j), value);
			}
		}

		dataFile.deleteOnExit();
		new File(FileUtils.toFile(oa.getRoot()), label).deleteOnExit();
		reader.close();
	}

	private ProductObservational createProductLabel(ObjectAccess oa, String label) throws Exception {
		int cols = 2;    // number of fields
		int length = 11; // record length
		gov.nasa.arc.pds.xml.generated.File file = new gov.nasa.arc.pds.xml.generated.File();
		file.setFileName("CharTableReader.tab");

		RecordCharacter record = new RecordCharacter();
		RecordLength recLength = new RecordLength();
		recLength.setValue(BigInteger.valueOf(length));
		record.setRecordLength(recLength);
		record.setFields(BigInteger.valueOf(cols));

		for (int i = 0; i < cols; i++) {
			String[] data = charData[0][i];
			FieldCharacter field = new FieldCharacter();
			field.setDataType(data[4]);
			field.setName(data[5]);

			FieldLength len = new FieldLength();
			len.setValue(new BigInteger(data[1]));
			field.setFieldLength(len);

			FieldLocation loc = new FieldLocation();
			loc.setValue(new BigInteger(data[0]));
			field.setFieldLocation(loc);

			field.setFieldNumber(new BigInteger(data[6]));
			record.getFieldCharactersAndGroupFieldCharacters().add(field);
		}

		Offset offset = new Offset();
		offset.setValue(BigInteger.ZERO);

		TableCharacter table = new TableCharacter();
		table.setRecordCharacter(record);
		table.setRecords(BigInteger.valueOf(2));
		table.setOffset(offset);

		FileAreaObservational fileArea = new FileAreaObservational();
		fileArea.getDataObjects().add(table);
		fileArea.setFile(file);

		ProductObservational product = new ProductObservational();
		product.getFileAreaObservationals().add(fileArea);
		oa.setObservationalProduct(label, product);

		return oa.getObservationalProduct(label);
	}

	private ProductObservational createBinaryProductLabel(ObjectAccess oa, String label) throws Exception {
		int cols = 6;

		gov.nasa.arc.pds.xml.generated.File file = new gov.nasa.arc.pds.xml.generated.File();
		file.setFileName("BinaryTableReader.dat");

		RecordBinary record = new RecordBinary();
		RecordLength recLength = new RecordLength();
		recLength.setValue(BigInteger.valueOf(28));
		record.setRecordLength(recLength);
		record.setFields(BigInteger.valueOf(cols));

		for (int i = 0; i < cols; i++) {
			String[] data = binData[0][i];
			FieldBinary field = new FieldBinary();
			field.setDataType(data[3]);
			field.setName(data[4]);

			FieldLength len = new FieldLength();
			len.setValue(new BigInteger(data[1]));
			field.setFieldLength(len);

			FieldLocation loc = new FieldLocation();
			loc.setValue(new BigInteger(data[0]));
			field.setFieldLocation(loc);

			field.setFieldNumber(BigInteger.valueOf(i+1));
			record.getFieldBinariesAndGroupFieldBinaries().add(field);
		}

		Offset offset = new Offset();
		offset.setValue(BigInteger.ZERO);

		TableBinary table = new TableBinary();
		table.setRecordBinary(record);
		table.setRecords(BigInteger.valueOf(2));
		table.setOffset(offset);

		FileAreaObservational fileArea = new FileAreaObservational();
		fileArea.getDataObjects().add(table);
		fileArea.setFile(file);

		ProductObservational product = new ProductObservational();
		product.getFileAreaObservationals().add(fileArea);
		oa.setObservationalProduct(label, product);

		return oa.getObservationalProduct(label);
	}

	private ProductObservational createBitFieldBinaryProductLabel(ObjectAccess oa, String label) throws Exception {
		int cols = 1;
		gov.nasa.arc.pds.xml.generated.File file = new gov.nasa.arc.pds.xml.generated.File();
		file.setFileName("BinaryTableReader.dat");

		RecordBinary record = new RecordBinary();
		RecordLength recLength = new RecordLength();
		recLength.setValue(BigInteger.valueOf(2));
		record.setRecordLength(recLength);
		record.setFields(BigInteger.valueOf(cols));

		FieldBinary field = new FieldBinary();
		field.setDataType("UnsignedByte");
		field.setName("REVOLVE_MOTOR_CONTROLLER_STATUS");
		FieldLength len = new FieldLength();
		len.setValue(BigInteger.valueOf(1));
		field.setFieldLength(len);
		FieldLocation loc = new FieldLocation();
		loc.setValue(BigInteger.valueOf(1));
		field.setFieldLocation(loc);

		PackedDataFields dataFields = new PackedDataFields();
		for (int j = 0; j < 8; j++) {
			FieldBit bit = new FieldBit();
			bit.setStartBitLocation(BigInteger.valueOf(j + 1));
			bit.setStopBitLocation(BigInteger.valueOf(j + 1));
			bit.setName("BIT " + j);
			bit.setDataType(FieldType.UNSIGNEDBITSTRING.getXMLType());
			dataFields.getFieldBits().add(bit);
		}

		field.setPackedDataFields(dataFields);
		record.getFieldBinariesAndGroupFieldBinaries().add(field);

		Offset offset = new Offset();
		offset.setValue(BigInteger.valueOf(0));
		TableBinary table = new TableBinary();
		table.setRecordBinary(record);
		table.setRecords(BigInteger.valueOf(2));
		table.setOffset(offset);

		FileAreaObservational fileArea = new FileAreaObservational();
		fileArea.getDataObjects().add(table);
		fileArea.setFile(file);

		ProductObservational product = new ProductObservational();
		product.getFileAreaObservationals().add(fileArea);
		oa.setObservationalProduct(label, product);

		return oa.getObservationalProduct(label);
	}

	private void createDataFile(Object table, File dataFile, int rows, int cols, boolean isBinary, boolean isBitField) throws Exception {
		TableWriter writer = null;
		TableRecord record;

		try {
			OutputStream os = new FileOutputStream(dataFile);
			writer = new TableWriter(table, os);

			for (int i = 0; i < rows; i++) {
				record = writer.createRecord();

				if (isBinary) {
					//if (isBitField) {
					//	record.setByte(1, Byte.parseByte(bitFieldData[i]));
					//} else {
						for (int j = 0; j < cols; j++) {
							String[] data = binData[i][j];
							String dataType = data[3];
							String value = data[2];
							String name = data[4];

							if (dataType.contains("ASCII")) {
								record.setString(name, value);
							} else {
								NumericDataType numericType = Enum.valueOf(NumericDataType.class, dataType);
								switch(numericType) {
									case IEEE754MSBSingle:
										record.setFloat(name, Float.parseFloat(value));
										break;
									case IEEE754MSBDouble:
										record.setDouble(name, Double.parseDouble(value));
										break;
									case UnsignedByte:
										record.setByte(name, Byte.parseByte(value));
										break;
									case UnsignedMSB2:
										record.setShort(name, (short) Integer.parseInt(value));
										break;
									case UnsignedMSB4:
										record.setLong(name, Long.parseLong(value));
										record.setInt(name, Integer.parseInt(value));
										break;
								}
							}
						}
					//}
				} else {
					for (int j = 0; j < cols; j++) {
						String[] data = charData[i][j];
						record.setString(Integer.parseInt(data[6]), data[2]);

						if (j < cols - 1) {
							record.setString(",");
						}
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
	}
}
