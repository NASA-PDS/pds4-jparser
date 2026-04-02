package gov.nasa.pds.objectAccess;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import gov.nasa.arc.pds.xml.generated.FieldCharacter;
import gov.nasa.arc.pds.xml.generated.FieldLength;
import gov.nasa.arc.pds.xml.generated.FieldLocation;
import gov.nasa.arc.pds.xml.generated.FileAreaObservational;
import gov.nasa.arc.pds.xml.generated.Offset;
import gov.nasa.arc.pds.xml.generated.ProductObservational;
import gov.nasa.arc.pds.xml.generated.RecordCharacter;
import gov.nasa.arc.pds.xml.generated.RecordLength;
import gov.nasa.arc.pds.xml.generated.TableCharacter;
import gov.nasa.pds.label.object.DataObjectLocation;

/**
 * Tests for the buffered readNextLine() implementation in RawTableReader,
 * focusing on line-ending edge cases at the internal buffer boundary (8192 bytes).
 */
public class RawTableReaderBufferedReadTest {

  private static final int BUFFER_SIZE = 8192; // Must match RawTableReader.READ_BUFFER_SIZE
  private File tempDataFile;
  private File tempDir;

  @AfterMethod
  public void cleanup() {
    if (tempDataFile != null && tempDataFile.exists()) {
      tempDataFile.delete();
    }
  }

  /**
   * Tests that a \\r\\n line ending that straddles the 8192-byte buffer boundary
   * is correctly handled: \\r at byte 8191 (last byte of first buffer fill),
   * \\n at byte 8192 (first byte of second buffer fill).
   */
  @Test
  public void testCrLfAtBufferBoundary() throws Exception {
    // Build data: (BUFFER_SIZE - 1) bytes of 'A', then \r\n, then "second\r\n"
    byte[] data = buildDataWithDelimiterAt(BUFFER_SIZE - 1, 'A', "\r\n", "second\r\n");

    RawTableReader reader = createReaderForData(data);
    try {
      String line1 = reader.readNextLine();
      assertNotNull(line1, "First line should not be null");
      // Line should be (BUFFER_SIZE - 1) A's + \r\n
      assertEquals(line1.length(), BUFFER_SIZE - 1 + 2);
      assertEquals(line1.substring(0, BUFFER_SIZE - 1), repeat('A', BUFFER_SIZE - 1));
      assertEquals(line1.substring(BUFFER_SIZE - 1), "\r\n");

      String line2 = reader.readNextLine();
      assertNotNull(line2, "Second line should not be null");
      assertEquals(line2, "second\r\n");

      String line3 = reader.readNextLine();
      assertNull(line3, "Should return null at EOF");
    } finally {
      reader.close();
    }
  }

  /**
   * Tests a bare \\r at the exact buffer boundary (byte 8191), followed by
   * a non-\\n character. The \\r should end the line and the next character
   * should appear in the following line.
   */
  @Test
  public void testBareCrAtBufferBoundary() throws Exception {
    // Build data: (BUFFER_SIZE - 1) bytes of 'B', then \r, then "next\n"
    byte[] data = buildDataWithDelimiterAt(BUFFER_SIZE - 1, 'A', "\r", "next\n");

    RawTableReader reader = createReaderForData(data);
    try {
      String line1 = reader.readNextLine();
      assertNotNull(line1, "First line should not be null");
      assertEquals(line1.length(), BUFFER_SIZE - 1 + 1); // content + \r
      assertEquals(line1.substring(BUFFER_SIZE - 1), "\r");

      String line2 = reader.readNextLine();
      assertNotNull(line2, "Second line should not be null");
      assertEquals(line2, "next\n");

      String line3 = reader.readNextLine();
      assertNull(line3, "Should return null at EOF");
    } finally {
      reader.close();
    }
  }

  /**
   * Tests a \\r at the exact buffer boundary where the file ends immediately
   * after (EOF after \\r). The line should contain the \\r and then EOF.
   */
  @Test
  public void testCrAtBufferBoundaryFollowedByEof() throws Exception {
    // Build data: (BUFFER_SIZE - 1) bytes of 'C', then \r, then nothing
    byte[] data = buildDataWithDelimiterAt(BUFFER_SIZE - 1, 'A', "\r", "");

    RawTableReader reader = createReaderForData(data);
    try {
      String line1 = reader.readNextLine();
      assertNotNull(line1, "First line should not be null");
      assertEquals(line1.length(), BUFFER_SIZE - 1 + 1); // content + \r
      assertEquals(line1.substring(BUFFER_SIZE - 1), "\r");

      String line2 = reader.readNextLine();
      assertNull(line2, "Should return null at EOF");
    } finally {
      reader.close();
    }
  }

  /**
   * Tests a \\n at the exact start of the second buffer fill (byte 8192),
   * preceded by content that fills the first buffer completely without a newline,
   * then has \\n as the first byte of the refill.
   */
  @Test
  public void testLfAtStartOfSecondBuffer() throws Exception {
    // Build data: BUFFER_SIZE bytes of 'D', then \n, then "after\n"
    byte[] data = buildDataWithDelimiterAt(BUFFER_SIZE, 'A', "\n", "after\n");

    RawTableReader reader = createReaderForData(data);
    try {
      String line1 = reader.readNextLine();
      assertNotNull(line1, "First line should not be null");
      assertEquals(line1.length(), BUFFER_SIZE + 1); // content + \n
      assertEquals(line1.substring(0, BUFFER_SIZE), repeat('A', BUFFER_SIZE));
      assertEquals(line1.substring(BUFFER_SIZE), "\n");

      String line2 = reader.readNextLine();
      assertNotNull(line2, "Second line should not be null");
      assertEquals(line2, "after\n");

      String line3 = reader.readNextLine();
      assertNull(line3, "Should return null at EOF");
    } finally {
      reader.close();
    }
  }

  /**
   * Tests basic multi-line reading with \\r\\n, \\n, and \\r delimiters
   * all within a single buffer fill (no boundary issues).
   */
  @Test
  public void testMixedLineEndings() throws Exception {
    String content = "line1\r\nline2\nline3\rline4";
    byte[] data = content.getBytes(StandardCharsets.UTF_8);

    RawTableReader reader = createReaderForData(data);
    try {
      assertEquals(reader.readNextLine(), "line1\r\n");
      assertEquals(reader.readNextLine(), "line2\n");
      assertEquals(reader.readNextLine(), "line3\r");
      assertEquals(reader.readNextLine(), "line4");
      assertNull(reader.readNextLine());
    } finally {
      reader.close();
    }
  }

  /**
   * Tests that an empty file causes an InvalidTableException during construction,
   * since ByteWiseFileAccessor rejects files that are smaller than the declared table size.
   */
  @Test(expectedExceptions = {InvalidTableException.class})
  public void testEmptyFile() throws Exception {
    byte[] data = new byte[0];
    createReaderForData(data);
  }

  // --- Helper methods ---

  private byte[] buildDataWithDelimiterAt(int prefixLen, char fillChar, String delimiter, String suffix) {
    byte[] prefix = new byte[prefixLen];
    Arrays.fill(prefix, (byte) fillChar);
    byte[] delimBytes = delimiter.getBytes(StandardCharsets.UTF_8);
    byte[] suffixBytes = suffix.getBytes(StandardCharsets.UTF_8);
    byte[] result = new byte[prefixLen + delimBytes.length + suffixBytes.length];
    System.arraycopy(prefix, 0, result, 0, prefixLen);
    System.arraycopy(delimBytes, 0, result, prefixLen, delimBytes.length);
    System.arraycopy(suffixBytes, 0, result, prefixLen + delimBytes.length, suffixBytes.length);
    return result;
  }

  /**
   * Creates a RawTableReader backed by a temporary file containing the given data.
   * Uses a TableCharacter with a single field spanning the entire record.
   */
  private RawTableReader createReaderForData(byte[] data) throws Exception {
    tempDir = new File("./src/test/resources/dph_example_products/product_table_character/");
    tempDataFile = File.createTempFile("rawreader_test_", ".tab", tempDir);
    tempDataFile.deleteOnExit();

    try (FileOutputStream fos = new FileOutputStream(tempDataFile)) {
      fos.write(data);
    }

    // Create a minimal TableCharacter with a single field
    int recordLength = Math.max(data.length, 1);

    RecordCharacter record = new RecordCharacter();
    RecordLength recLength = new RecordLength();
    recLength.setValue(BigInteger.valueOf(recordLength));
    record.setRecordLength(recLength);
    record.setFields(BigInteger.valueOf(1));
    record.setGroups(BigInteger.valueOf(0));

    FieldCharacter field = new FieldCharacter();
    field.setDataType("ASCII_String");
    field.setName("DATA");
    FieldLength fieldLen = new FieldLength();
    fieldLen.setValue(BigInteger.valueOf(recordLength));
    field.setFieldLength(fieldLen);
    FieldLocation loc = new FieldLocation();
    loc.setValue(BigInteger.valueOf(1));
    field.setFieldLocation(loc);
    field.setFieldNumber(BigInteger.valueOf(1));
    record.getFieldCharactersAndGroupFieldCharacters().add(field);

    Offset offset = new Offset();
    offset.setValue(BigInteger.ZERO);

    TableCharacter table = new TableCharacter();
    table.setRecordCharacter(record);
    table.setRecords(BigInteger.valueOf(1));
    table.setOffset(offset);

    DataObjectLocation location = new DataObjectLocation(1, 1);

    return new RawTableReader(table, tempDataFile.toURI().toURL(), null, location, true);
  }

  private static String repeat(char c, int count) {
    char[] chars = new char[count];
    Arrays.fill(chars, c);
    return new String(chars);
  }
}
