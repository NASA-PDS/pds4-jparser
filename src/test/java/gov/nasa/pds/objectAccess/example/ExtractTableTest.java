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

package gov.nasa.pds.objectAccess.example;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

public class ExtractTableTest {

	private static final String UTF8_CHARSET = "UTF-8";
	private static final String CRLF = "\r\n";

	private static final String FIELD_NAME = "field,1";

	private static final String[] STRING_VALUES = {
		"",
		"x",
		"this is a test"
	};

	private static final String[] INT8_VALUES = {
		"0", "1", "-1", Long.toString(- (1L << 7)), Long.toString((1L << 7) - 1)
	};

	private static final String[] INT16_VALUES = {
		"0", "1", "-1", Long.toString(- (1L << 15)), Long.toString((1L << 15) - 1)
	};

	private static final String[] INT32_VALUES = {
		"0", "1", "-1", Long.toString(- (1L << 31)), Long.toString((1L << 31) - 1)
	};

	private static final String[] INT64_VALUES = {
		"0", "1", "-1", Long.toString(1L << 63), Long.toString((1L << 63) - 1)
	};

	private static final String[] UINT8_VALUES = {
		"0", "1", Long.toString(1L << 7), Long.toString((1L << 8) - 1)
	};

	private static final String[] UINT16_VALUES = {
		"0", "1", Long.toString(1L << 15), Long.toString((1L << 16) - 1)
	};

	private static final String[] UINT32_VALUES = {
		"0", "1", Long.toString(1L << 31), Long.toString((1L << 32) - 1)
	};

	private static final String[] UINT64_VALUES = {
		"0", "1", BigInteger.ONE.shiftLeft(63).toString(), BigInteger.ONE.shiftLeft(64).subtract(BigInteger.ONE).toString()
	};

	private static final String[] REAL_VALUES = {
		"0.0", "1.0", "-1.0", "1000.0", "-1000.0"
	};

	// Text and delimited field data types.

	private static final FieldType[] TEXT_AND_DELIMITED_FIELD_TYPES = {
		new FieldType("ASCII_AnyURI", new TextAccessor(false), 20, new String[] {"http://www.yahoo.com"}),
		new FieldType("ASCII_Boolean", new BooleanTextAccessor(), 20, new String[] {"false", "true"}),
		new FieldType("ASCII_DOI", new TextAccessor(false), 20, STRING_VALUES),
		new FieldType("ASCII_Date", new TextAccessor(false), 20, new String[] {"2000-01-01Z"}),
		new FieldType("ASCII_Date_DOY", new TextAccessor(false), 20, new String[] {"2000-001Z"}),
		new FieldType("ASCII_Date_Time", new TextAccessor(false), 20, new String[] {"2000-01-01T00:00:00Z"}),
		new FieldType("ASCII_Date_Time_DOY", new TextAccessor(false), 20, new String[] {"2000-001T00:00:00Z"}),
		new FieldType("ASCII_Date_Time_UTC", new TextAccessor(false), 20, new String[] {"2000-01-01T00:00:00Z"}),
		new FieldType("ASCII_Date_Time_YMD", new TextAccessor(false), 20, new String[] {"2000-01-01T00:00:00Z"}),
		new FieldType("ASCII_Date_YMD", new TextAccessor(false), 20, new String[] {"2000-01-01Z"}),
		new FieldType("ASCII_Directory_Path_Name", new TextAccessor(false), 20, new String[] {"abc/def/ghi"}),
		new FieldType("ASCII_File_Name", new TextAccessor(false), 20, new String[] {"sample.dat"}),
		new FieldType("ASCII_File_Specification_Name", new TextAccessor(false), 20, new String[] {"sample.dat"}),
		new FieldType("ASCII_Integer", new IntegerTextAccessor(), 30, INT64_VALUES),
		new FieldType("ASCII_LID", new TextAccessor(false), 50, new String[] {"urn:nasa:pds:bundle:collection:product"}),
		new FieldType("ASCII_LIDVID", new TextAccessor(false), 50, new String[] {"urn:nasa:pds:bundle:collection:product:1.0"}),
		new FieldType("ASCII_LIDVID_LID", new TextAccessor(false), 50, new String[] {"urn:nasa:pds:bundle:collection:product:1.0"}),
		new FieldType("ASCII_MD5_Checksum", new TextAccessor(false), 40, new String[] {"b1946ac92492d2347c6235b4d2611184"}),
		new FieldType("ASCII_NonNegative_Integer", new IntegerTextAccessor(), 30, UINT64_VALUES),
		new FieldType("ASCII_Numeric_Base16", new TextAccessor(false), 20, new String[] {}),
		new FieldType("ASCII_Numeric_Base2", new TextAccessor(false), 20, new String[] {}),
		new FieldType("ASCII_Numeric_Base8", new TextAccessor(false), 20, new String[] {}),
		new FieldType("ASCII_Real", new DecimalTextAccessor(), 20, REAL_VALUES),
		new FieldType("ASCII_String", new TextAccessor(false), 20, STRING_VALUES),
		new FieldType("ASCII_Time", new TextAccessor(false), 20, new String[] {"00:00:00Z"}),
		new FieldType("ASCII_VID", new TextAccessor(false), 20, new String[] {"1.0"}),
		new FieldType("UTF8_String", new TextAccessor(false), 20, STRING_VALUES),
	};

	// Binary field data types.
	private static final FieldType[] BINARY_FIELD_TYPES = {
		new FieldType("ASCII_AnyURI", new TextAccessor(false), 20, STRING_VALUES),
		new FieldType("ASCII_Boolean", new BooleanTextAccessor(), 20, new String[] {"false", "true"}),
		new FieldType("ASCII_DOI", new TextAccessor(false), 20, STRING_VALUES),
		new FieldType("ASCII_Date", new TextAccessor(false), 20, new String[] {"2000-01-01Z"}),
		new FieldType("ASCII_Date_DOY", new TextAccessor(false), 20, new String[] {"2000-001Z"}),
		new FieldType("ASCII_Date_Time", new TextAccessor(false), 20, new String[] {"2000-01-01T00:00:00Z"}),
		new FieldType("ASCII_Date_Time_DOY", new TextAccessor(false), 20, new String[] {"2000-001T00:00:00Z"}),
		new FieldType("ASCII_Date_Time_UTC", new TextAccessor(false), 20, new String[] {"2000-01-01T00:00:00Z"}),
		new FieldType("ASCII_Date_Time_YMD", new TextAccessor(false), 20, new String[] {"2000-01-01T00:00:00Z"}),
		new FieldType("ASCII_Date_YMD", new TextAccessor(false), 20, new String[] {"2000-01-01Z"}),
		new FieldType("ASCII_Directory_Path_Name", new TextAccessor(false), 20, new String[] {"abc/def/ghi"}),
		new FieldType("ASCII_File_Name", new TextAccessor(false), 20, new String[] {"sample.dat"}),
		new FieldType("ASCII_File_Specification_Name", new TextAccessor(false), 20, new String[] {"sample.dat"}),
		new FieldType("ASCII_Integer", new IntegerTextAccessor(), 20, new String[] {}),
		new FieldType("ASCII_LID", new TextAccessor(false), 50, new String[] {"urn:nasa:pds:bundle:collection:product"}),
		new FieldType("ASCII_LIDVID", new TextAccessor(false), 50, new String[] {"urn:nasa:pds:bundle:collection:product:1.0"}),
		new FieldType("ASCII_LIDVID_LID", new TextAccessor(false), 50, new String[] {"urn:nasa:pds:bundle:collection:product:1.0"}),
		new FieldType("ASCII_MD5_Checksum", new TextAccessor(false), 40, new String[] {"b1946ac92492d2347c6235b4d2611184"}),
		new FieldType("ASCII_NonNegative_Integer", new TextAccessor(false), 30, UINT64_VALUES),
		new FieldType("ASCII_Numeric_Base16", new TextAccessor(false), 20, new String[] {}),
		new FieldType("ASCII_Numeric_Base2", new TextAccessor(false), 20, new String[] {}),
		new FieldType("ASCII_Numeric_Base8", new TextAccessor(false), 20, new String[] {}),
		new FieldType("ASCII_Real", new DecimalTextAccessor(), 20, REAL_VALUES),
		new FieldType("ASCII_String", new TextAccessor(false), 20, STRING_VALUES),
		new FieldType("ASCII_Time", new TextAccessor(false), 20, new String[] {"00:00:00Z"}),
		new FieldType("ASCII_VID", new TextAccessor(false), 20, new String[] {"1.0"}),
		/*
		new FieldType("ComplexLSB16", new TextAccessor(false), 20, new String[] {}),
		new FieldType("ComplexLSB8", new TextAccessor(false), 20, new String[] {}),
		new FieldType("ComplexMSB16", new TextAccessor(false), 20, new String[] {}),
		new FieldType("ComplexMSB8", new TextAccessor(false), 20, new String[] {}),
		*/
		new FieldType("IEEE754LSBDouble", new BinaryDoubleAccessor(false), 8, REAL_VALUES),
		new FieldType("IEEE754LSBSingle", new BinaryFloatAccessor(false), 4, REAL_VALUES),
		new FieldType("IEEE754MSBDouble", new BinaryDoubleAccessor(true), 8, REAL_VALUES),
		new FieldType("IEEE754MSBSingle", new BinaryFloatAccessor(true), 4, REAL_VALUES),
		/*
		new FieldType("SignedBitString", new TextAccessor(false), 20, new String[] {}),
		*/
		new FieldType("SignedByte", new BinaryIntegerAccessor(true, true), 1, INT8_VALUES),
		new FieldType("SignedLSB2", new BinaryIntegerAccessor(true, false), 2, INT16_VALUES),
		new FieldType("SignedLSB4", new BinaryIntegerAccessor(true, false), 4, INT32_VALUES),
		new FieldType("SignedLSB8", new BinaryIntegerAccessor(true, false), 8, INT64_VALUES),
		new FieldType("SignedMSB2", new BinaryIntegerAccessor(true, true), 2, INT16_VALUES),
		new FieldType("SignedMSB4", new BinaryIntegerAccessor(true, true), 4, INT32_VALUES),
		new FieldType("SignedMSB8", new BinaryIntegerAccessor(true, true), 8, INT64_VALUES),
		new FieldType("UTF8_String", new TextAccessor(false), 20, STRING_VALUES),
		/*
			new FieldType("UnsignedBitString", new TextAccessor(false), 20, new String[] {}),
		 */
		new FieldType("UnsignedByte", new BinaryIntegerAccessor(false, true), 1, UINT8_VALUES),
		new FieldType("UnsignedLSB2", new BinaryIntegerAccessor(false, false), 2, UINT16_VALUES),
		new FieldType("UnsignedLSB4", new BinaryIntegerAccessor(false, false), 4, UINT32_VALUES),
		new FieldType("UnsignedLSB8", new BinaryIntegerAccessor(false, false), 8, UINT64_VALUES),
		new FieldType("UnsignedMSB2", new BinaryIntegerAccessor(false, true), 2, UINT16_VALUES),
		new FieldType("UnsignedMSB4", new BinaryIntegerAccessor(false, true), 4, UINT32_VALUES),
		new FieldType("UnsignedMSB8", new BinaryIntegerAccessor(false, true), 8, UINT64_VALUES),
	};

	private Configuration config;
	private File labelFile;
	private File dataFile;
	private File resultFile;

	@BeforeClass
	private void configureFreeMarker() throws IOException {
		config = new Configuration(Configuration.VERSION_2_3_21);
		config.setDirectoryForTemplateLoading(new File("src/test/resources/data_type_tests"));
		config.setDefaultEncoding(UTF8_CHARSET);
		config.setTemplateExceptionHandler(TemplateExceptionHandler.DEBUG_HANDLER);
	}

	@BeforeMethod
	public void createFiles() throws IOException {
		labelFile = File.createTempFile("label-", ".xml");
		dataFile = File.createTempFile("data-", ".dat");
		resultFile = File.createTempFile("result-", ".txt");
	}

	@AfterMethod
	public void deleteFiles() {
		labelFile.delete();
		dataFile.delete();
		resultFile.delete();
	}

	@Test(dataProvider="TextAndDelimitedTypeTests")
	public void testReadTableCharacterCSV(FieldType fieldType) throws IOException, TemplateException {
		writeLabel("TableCharacter-template.xml", getTableProperties(fieldType, true), labelFile);
		writeTextData(dataFile, fieldType.getValues(), fieldType.getLength(), fieldType.getAccessor());

		ExtractTable.main(new String[] {"--csv", "-o", resultFile.getAbsolutePath(), labelFile.getAbsolutePath()});
		checkCSVValues(resultFile, fieldType.getValues());
	}

	@Test(dataProvider="TextAndDelimitedTypeTests")
	public void testReadTableCharacterFixed(FieldType fieldType) throws IOException, TemplateException {
		writeLabel("TableCharacter-template.xml", getTableProperties(fieldType, true), labelFile);
		writeTextData(dataFile, fieldType.getValues(), fieldType.getLength(), fieldType.getAccessor());

		ExtractTable.main(new String[] {"-o", resultFile.getAbsolutePath(), labelFile.getAbsolutePath()});
		checkTextValues(resultFile, fieldType.getLength(), fieldType.isRightJustified(), fieldType.getValues());
	}

	@Test(dataProvider="TextAndDelimitedTypeTests")
	public void testReadTableDelimitedCSV(FieldType fieldType) throws IOException, TemplateException {
		writeLabel("TableDelimited-template.xml", getTableProperties(fieldType, true), labelFile);
		writeDelimitedData(dataFile, fieldType.getValues(), fieldType.getAccessor());

		ExtractTable.main(new String[] {"--csv", "-o", resultFile.getAbsolutePath(), labelFile.getAbsolutePath()});
		checkCSVValues(resultFile, fieldType.getValues());
	}

	@DataProvider(name="TextAndDelimitedTypeTests")
	private Object[][] getTextAndDelimitedTypeTests() {
		Object[][] result = new Object[TEXT_AND_DELIMITED_FIELD_TYPES.length][];
		for (int i=0; i < TEXT_AND_DELIMITED_FIELD_TYPES.length; ++i) {
			result[i] = new Object[] {TEXT_AND_DELIMITED_FIELD_TYPES[i]};
		}
		return result;
	}

	@Test(dataProvider="BinaryTypeTests")
	public void testReadTableBinaryCSV(FieldType fieldType) throws IOException, TemplateException {
		writeLabel("TableBinary-template.xml", getTableProperties(fieldType, false), labelFile);
		writeBinaryData(dataFile, fieldType.getValues(), fieldType.getLength(), fieldType.getAccessor());

		ExtractTable.main(new String[] {"--csv", "-o", resultFile.getAbsolutePath(), labelFile.getAbsolutePath()});
		checkCSVValues(resultFile, fieldType.getValues());
	}

	@DataProvider(name="BinaryTypeTests")
	private Object[][] getBinaryTypeTests() {
		Object[][] result = new Object[BINARY_FIELD_TYPES.length][];
		for (int i=0; i < BINARY_FIELD_TYPES.length; ++i) {
			result[i] = new Object[] {BINARY_FIELD_TYPES[i]};
		}
		return result;
	}

	private TableProperties getTableProperties(FieldType fieldType, boolean isText) {
		TableProperties props = new TableProperties();
		props.setFileName(dataFile.getName());
		props.setRecordLength(fieldType.getLength() + (isText ? CRLF.length() : 0));
		props.setRecordCount(fieldType.getValues().length);
		props.setFieldName(FIELD_NAME);
		props.setFieldType(fieldType.getTypeName());
		props.setFieldLength(fieldType.getLength());

		return props;
	}

	private void writeLabel(String templateName, TableProperties props, File labelFile) throws IOException, TemplateException {
		Template template = config.getTemplate(templateName);
		Writer out = new FileWriter(labelFile);
		template.process(props, out);
		out.close();
	}

	private void writeTextData(File outputFile, String[] values, int fieldLength, FieldAccessor accessor) throws IOException {
		OutputStream out = new FileOutputStream(dataFile);

		for (String value : values) {
			byte[] b = accessor.string2Bytes(value, fieldLength);
			out.write(b);
			out.write(CRLF.getBytes(UTF8_CHARSET));
		}

		out.close();
	}

	private void writeDelimitedData(File outputFile, String[] values, FieldAccessor accessor) throws IOException {
		OutputStream out = new FileOutputStream(dataFile);

		for (String value : values) {
			out.write(value.getBytes(UTF8_CHARSET));
			out.write(CRLF.getBytes(UTF8_CHARSET));
		}

		out.close();
	}

	private void writeBinaryData(File outputFile, String[] values, int fieldLength, FieldAccessor accessor) throws IOException {
		OutputStream out = new FileOutputStream(dataFile);

		for (String value : values) {
			byte[] b = accessor.string2Bytes(value, fieldLength);
			out.write(b);
		}

		out.close();
	}

	private void checkCSVValues(File f, String[] values) throws IOException {
		CSVParser parser = CSVFormat.DEFAULT.parse(new FileReader(f));
		List<CSVRecord> records = parser.getRecords();
		if (records.size() != values.length + 1) {
			int x = 1;
		}
		assertEquals(records.size(), values.length + 1);

		int row=0;
		for (CSVRecord record : records) {
			assertEquals(record.size(), 1);
			if (row == 0) {
				assertEquals(record.get(0), FIELD_NAME);
			} else {
				assertEquals(record.get(0), values[row-1]);
			}
			++row;
		}
	}


	private void checkTextValues(File f, int length, boolean isRightJustified, String[] values) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(f));
		List<String> lines = new ArrayList<String>();

		for (;;) {
			String line = in.readLine();
			if (line == null) {
				break;
			}

			lines.add(line);
		}

		assertEquals(lines.size(), values.length + 1);

		assertEquals(lines.get(0).trim(), FIELD_NAME);

		for (int row=1; row <= values.length; ++row) {
			if (!isRightJustified) {
				assertTrue(lines.get(row).startsWith(values[row-1]));
			} else {
				assertTrue(lines.get(row).endsWith(values[row-1]));
			}
			++row;
		}
	}
	private void writeTableRow(OutputStream out, FieldType fieldType, int length, String strValue) throws IOException {
		out.write(fieldType.getAccessor().string2Bytes(strValue, length));
	}

	private static class FieldType {

		private String typeName;
		private FieldAccessor accessor;
		private int length;
		private String[] values;

		public FieldType(String typeName, FieldAccessor accessor, int length, String[] values) {
			this.typeName = typeName;
			this.accessor = accessor;
			this.length = length;
			this.values = values;
		}

		public boolean isRightJustified() {
			return accessor.isRightJustified();
		}

		public String getTypeName() {
			return typeName;
		}

		public void setTypeName(String typeName) {
			this.typeName = typeName;
		}

		public FieldAccessor getAccessor() {
			return accessor;
		}

		public void setAccessor(FieldAccessor accessor) {
			this.accessor = accessor;
		}

		public int getLength() {
			return length;
		}

		public void setLength(int length) {
			this.length = length;
		}

		public String[] getValues() {
			return values;
		}

		public void setValues(String[] values) {
			this.values = values;
		}

		@Override
		public String toString() {
			return "{field:" + typeName + "}";
		}

	}

	private static abstract class FieldAccessor {

		public abstract byte[] string2Bytes(String value, int length);

		public final String bytes2String(byte[] b) {
			return bytes2String(b, 0, b.length);
		}

		public abstract String bytes2String(byte[] b, int offset, int length);

		public boolean isRightJustified() {
			return false;
		}

		protected String bytes2StringText(byte[] b, int offset, int length) {
			try {
				return new String(b, offset, length, UTF8_CHARSET);
			} catch (UnsupportedEncodingException e) {
				// Cannot happen - UTF-8 must be supported in Java.
				throw new RuntimeException("UTF-8 character set is missing");
			}
		}

		protected byte[] orderBytes(byte[] b, int offset, int length, boolean isBigEndian) {
			if (!isBigEndian) {
				for (int i=offset, j=length-1; i < j; ++i, --j) {
					byte temp = b[i];
					b[i] = b[j];
					b[j] = temp;
				}
			}

			return b;
		}

	}

	private static class TextAccessor extends FieldAccessor {

		private boolean isRightJustified;

		public TextAccessor(boolean isRightJustified) {
			this.isRightJustified = isRightJustified;
		}

		@Override
		public byte[] string2Bytes(String value, int length) {
			try {
				return justify(value, length).getBytes(UTF8_CHARSET);
			} catch (UnsupportedEncodingException e) {
				// Cannot happen - UTF-8 must be supported in Java.
				throw new RuntimeException("UTF-8 character set is missing");
			}
		}

		@Override
		public String bytes2String(byte[] b, int offset, int length) {
			return justify(bytes2StringText(b, offset, length).trim(), length);
		}

		protected String justify(String s, int length) {
			if (isRightJustified) {
				return String.format("%" + length + "s", s);
			} else {
				return String.format("%-" + length + "s", s);
			}
		}

		@Override
		public boolean isRightJustified() {
			return isRightJustified;
		}

	}

	private static class IntegerTextAccessor extends TextAccessor {

		public IntegerTextAccessor() {
			super(true);
		}

		@Override
		public String bytes2String(byte[] b, int offset, int length) {
			String strValue = bytes2StringText(b, offset, length).trim();
			BigInteger integerValue = new BigInteger(strValue);
			return justify(integerValue.toString(), length);
		}

	}

	private static class DecimalTextAccessor extends TextAccessor {

		public DecimalTextAccessor() {
			super(true);
		}

		@Override
		public String bytes2String(byte[] b, int offset, int length) {
			String strValue = bytes2String(b, offset, length).trim();
			BigDecimal decimalValue = new BigDecimal(strValue);
			return justify(String.format("%.1f", decimalValue), length);
		}

	}

	private static class BooleanTextAccessor extends TextAccessor {

		public BooleanTextAccessor() {
			super(true);
		}

		@Override
		public String bytes2String(byte[] b, int offset, int length) {
			String strValue = bytes2String(b, offset, length).trim();
			boolean booleanValue = Boolean.valueOf(strValue);
			return justify(String.valueOf(booleanValue), length);
		}

	}

	private static class BinaryIntegerAccessor extends FieldAccessor {

		private boolean isSigned;
		private boolean isBigEndian;

		public BinaryIntegerAccessor(boolean isSigned, boolean isBigEndian) {
			this.isSigned = isSigned;
			this.isBigEndian = isBigEndian;
		}

		@Override
		public byte[] string2Bytes(String value, int length) {
			byte[] b = new BigInteger(value).toByteArray();
			if (b.length > length) {
				byte[] temp = new byte[length];
				System.arraycopy(b, b.length-length, temp, 0, length);
				b = temp;
			} else if (b.length < length) {
				byte[] temp = new byte[length];
				System.arraycopy(b, 0, temp, length - b.length, b.length);
				if (isSigned && b[0] < 0) {
					for (int i=0; i < length - b.length; ++i) {
						temp[i] = (byte) 0xFF;
					}
				}
				b = temp;
			}

			return orderBytes(b, 0, b.length, isBigEndian);
		}

		@Override
		public String bytes2String(byte[] b, int offset, int length) {
			byte[] temp = new byte[length + 1];
			System.arraycopy(b, offset, temp, 1, length);
			if (!isSigned || b[0] >= 0) {
				temp[0] = 0;
			} else {
				temp[0] = (byte) 0xFF;
			}
			orderBytes(temp, 1, length, isBigEndian);

			return new BigInteger(temp).toString();
		}

	}

	private static class BinaryFloatAccessor extends FieldAccessor {

		private boolean isBigEndian;

		public BinaryFloatAccessor(boolean isBigEndian) {
			this.isBigEndian = isBigEndian;
		}

		@Override
		public byte[] string2Bytes(String value, int length) {
			assert (length == Float.SIZE/Byte.SIZE);

			int bits = Float.floatToIntBits(Float.parseFloat(value));
			byte[] b = {
					(byte) ((bits >> 24) & 0xFF),
					(byte) ((bits >> 16) & 0xFF),
					(byte) ((bits >> 8) & 0xFF),
					(byte) (bits & 0xFF),
			};

			return orderBytes(b, 0, length, isBigEndian);
		}

		@Override
		public String bytes2String(byte[] b, int offset, int length) {
			assert (length == Float.SIZE/Byte.SIZE);

			byte[] temp = orderBytes(b, offset, length, isBigEndian);
			int bits =
					(temp[0] << 24)
					| ((temp[1] << 16) & 0xFF)
					| ((temp[2] << 8) & 0xFF)
					| (temp[0] & 0xFF);

			return Float.toString(Float.intBitsToFloat(bits));
		}

	}

	private static class BinaryDoubleAccessor extends FieldAccessor {

		private boolean isBigEndian;

		public BinaryDoubleAccessor(boolean isBigEndian) {
			this.isBigEndian = isBigEndian;
		}

		@Override
		public byte[] string2Bytes(String value, int length) {
			assert (length == Double.SIZE/Byte.SIZE);

			long bits = Double.doubleToLongBits(Double.parseDouble(value));
			byte[] b = {
					(byte) ((bits >> 56) & 0xFF),
					(byte) ((bits >> 48) & 0xFF),
					(byte) ((bits >> 40) & 0xFF),
					(byte) ((bits >> 32) & 0xFF),
					(byte) ((bits >> 24) & 0xFF),
					(byte) ((bits >> 16) & 0xFF),
					(byte) ((bits >> 8) & 0xFF),
					(byte) (bits & 0xFF),
			};

			return orderBytes(b, 0, length, isBigEndian);
		}

		@Override
		public String bytes2String(byte[] b, int offset, int length) {
			assert (length == Double.SIZE/Byte.SIZE);

			byte[] temp = orderBytes(b, offset, length, isBigEndian);
			long bits =
					(temp[0] << 56)
					| ((temp[1] << 48) & 0xFF)
					| ((temp[1] << 40) & 0xFF)
					| ((temp[1] << 32) & 0xFF)
					| ((temp[1] << 24) & 0xFF)
					| ((temp[1] << 16) & 0xFF)
					| ((temp[2] << 8) & 0xFF)
					| (temp[0] & 0xFF);

			return Double.toString(Double.longBitsToDouble(bits));
		}

	}

	public static class TableProperties {

		private String fileName;
		private int recordCount;
		private int recordLength;
		private String fieldName;
		private String fieldType;
		private int fieldLength;

		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

		public int getRecordCount() {
			return recordCount;
		}

		public void setRecordCount(int recordCount) {
			this.recordCount = recordCount;
		}

		public int getRecordLength() {
			return recordLength;
		}

		public void setRecordLength(int recordLength) {
			this.recordLength = recordLength;
		}

		public String getFieldName() {
			return fieldName;
		}

		public void setFieldName(String fieldName) {
			this.fieldName = fieldName;
		}

		public String getFieldType() {
			return fieldType;
		}

		public void setFieldType(String fieldType) {
			this.fieldType = fieldType;
		}

		public int getFieldLength() {
			return fieldLength;
		}

		public void setFieldLength(int fieldLength) {
			this.fieldLength = fieldLength;
		}

	}

}
