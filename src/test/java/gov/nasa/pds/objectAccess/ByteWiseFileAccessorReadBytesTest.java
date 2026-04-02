package gov.nasa.pds.objectAccess;

import static org.testng.Assert.assertEquals;
import java.io.File;
import java.io.FileOutputStream;
import org.testng.annotations.Test;

/**
 * Tests for ByteWiseFileAccessor.readBytes() — the new bulk-read method added by the
 * buffered-read performance PR.  Each test targets a specific edge case identified
 * during review.
 */
public class ByteWiseFileAccessorReadBytesTest {

  private static final int BUF = 8192; // matches RawTableReader.READ_BUFFER_SIZE

  // ---------- helpers ----------

  /** Write bytes to a temp file and return a ByteWiseFileAccessor over the whole file. */
  private ByteWiseFileAccessor accessorFor(byte[] data) throws Exception {
    File tmp = File.createTempFile("bwfa_test_", ".dat");
    tmp.deleteOnExit();
    try (FileOutputStream fos = new FileOutputStream(tmp)) {
      fos.write(data);
    }
    // offset=0, recordLength=data.length, records=1, checkSize=false
    return new ByteWiseFileAccessor(tmp.toURI().toURL(), 0, data.length, 1, false);
  }

  // ---------- Issue 5a: \r\n straddling the 8 KB boundary ----------

  /**
   * \r is the last byte of an 8192-byte buffer fill; \n is the first byte of the next fill.
   * readBytes() must return both bytes correctly across the boundary.
   */
  @Test
  public void testReadBytes_crLfStraddlesBoundary() throws Exception {
    byte[] data = new byte[BUF + 50];
    java.util.Arrays.fill(data, 0, BUF - 1, (byte) 'A'); // 8191 A's
    data[BUF - 1] = '\r';                                  // last byte of first fill
    data[BUF]     = '\n';                                  // first byte of second fill
    java.util.Arrays.fill(data, BUF + 1, data.length, (byte) 'B');

    ByteWiseFileAccessor acc = accessorFor(data);

    // Read the full content in 8192-byte chunks, mimicking RawTableReader
    byte[] buf = new byte[BUF];
    int n1 = acc.readBytes(buf, 0, BUF);
    assertEquals(n1, BUF, "First read should fill the buffer");
    assertEquals(buf[BUF - 1], (byte) '\r', "Last byte of first chunk must be \\r");

    int n2 = acc.readBytes(buf, 0, BUF);
    assertEquals(buf[0], (byte) '\n', "First byte of second chunk must be \\n");

    acc.close();
  }

  // ---------- Issue 5b: bare \r at boundary (next byte is NOT \n) ----------

  @Test
  public void testReadBytes_bareCrAtBoundary() throws Exception {
    byte[] data = new byte[BUF + 20];
    java.util.Arrays.fill(data, 0, BUF - 1, (byte) 'A');
    data[BUF - 1] = '\r';
    java.util.Arrays.fill(data, BUF, data.length, (byte) 'C'); // no \n after \r

    ByteWiseFileAccessor acc = accessorFor(data);

    byte[] buf = new byte[BUF];
    int n1 = acc.readBytes(buf, 0, BUF);
    assertEquals(n1, BUF);
    assertEquals(buf[BUF - 1], (byte) '\r');

    int n2 = acc.readBytes(buf, 0, BUF);
    assertEquals(buf[0], (byte) 'C',
        "Byte after bare \\r must be 'C', not treated as part of CRLF");

    acc.close();
  }

  // ---------- Issue 5c: \r is the very last byte of the file ----------

  @Test
  public void testReadBytes_crAtEof() throws Exception {
    byte[] data = new byte[BUF]; // exactly one buffer-fill worth
    java.util.Arrays.fill(data, 0, BUF - 1, (byte) 'A');
    data[BUF - 1] = '\r';

    ByteWiseFileAccessor acc = accessorFor(data);

    byte[] buf = new byte[BUF];
    int n1 = acc.readBytes(buf, 0, BUF);
    assertEquals(n1, BUF, "Should read all bytes including trailing \\r");
    assertEquals(buf[BUF - 1], (byte) '\r');

    int n2 = acc.readBytes(buf, 0, BUF);
    assertEquals(n2, -1, "Should return -1 at EOF");

    acc.close();
  }

  // ---------- Issue 4: 0xFF byte is now treated as data, not EOF ----------

  /**
   * Old readByte() returned a signed byte widened to int: 0xFF -> -1 -> hit case -1 (EOF).
   * New readBytes() copies raw bytes; 0xFF must appear in the output unchanged.
   *
   * This test verifies the NEW behavior of readBytes().
   * Before merging to upstream, confirm that no existing PDS4 archive product
   * relies on the OLD 0xFF-as-EOF behavior.
   */
  @Test
  public void testReadBytes_ffByteIsNotEof() throws Exception {
    // "HELLO\xFF WORLD\n"
    byte[] data = new byte[]{'H','E','L','L','O',(byte)0xFF,' ','W','O','R','L','D','\n'};

    ByteWiseFileAccessor acc = accessorFor(data);

    byte[] buf = new byte[data.length];
    int n = acc.readBytes(buf, 0, buf.length);

    assertEquals(n, data.length, "readBytes must not stop at 0xFF");
    assertEquals(buf[5], (byte) 0xFF, "0xFF byte must be present in output");
    assertEquals(buf[6], (byte) ' ',  "Byte after 0xFF must be ' '");

    acc.close();
  }

  // ---------- Issue 3 (documentation): readBytes advances curPosition ----------

  /**
   * Demonstrates that after readBytes() pre-reads N bytes, a subsequent
   * readByte() call skips those already-buffered bytes — they share curPosition.
   *
   * In RawTableReader this is safe because readNextLine() is never mixed with
   * readByte() on the same accessor instance.  This test documents that contract.
   */
  @Test
  public void testReadBytes_advancesCurPosition() throws Exception {
    byte[] data = "ABCDEFGHIJ".getBytes();
    ByteWiseFileAccessor acc = accessorFor(data);

    byte[] buf = new byte[5];
    int n = acc.readBytes(buf, 0, 5);
    assertEquals(n, 5);
    assertEquals(new String(buf, 0, 5), "ABCDE");

    // readByte() continues from position 5, not 0
    assertEquals(acc.readByte(), (byte) 'F',
        "readByte() after readBytes() must continue from current position");

    acc.close();
  }
}
