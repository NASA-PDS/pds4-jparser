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

package gov.nasa.pds.label.io;

import static org.testng.Assert.assertEquals;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class LengthLimitedInputStreamTest {

  private static byte[] data;
  static {
    try {
      data = "hello".getBytes("US-ASCII");
    } catch (UnsupportedEncodingException ex) {
      // Cannot get here, since US-ASCII must always be present.
    }
  }

  private InputStream baseIn;

  private byte[] buf;

  @BeforeMethod
  public void init() {
    baseIn = new ByteArrayInputStream(data);
    buf = new byte[100];
  }

  @Test
  public void testReadEntireStream() throws IOException {
    LengthLimitedInputStream in = new LengthLimitedInputStream(baseIn, 0, data.length);
    assertEquals(in.available(), data.length);

    int nRead = in.read(buf, 0, buf.length);
    assertEquals(nRead, data.length);
    byte[] b = new byte[nRead];
    System.arraycopy(buf, 0, b, 0, nRead);
    assertEquals(b, data);
    assertEquals(in.available(), 0);
  }

  @Test
  public void testReadByBytes() throws IOException {
    LengthLimitedInputStream in = new LengthLimitedInputStream(baseIn, 1, 2);
    assertEquals(in.available(), 2);

    assertEquals(in.read(), 'e');
    assertEquals(in.available(), 1);
    assertEquals(in.read(), 'l');
    assertEquals(in.read(), -1);
    assertEquals(in.available(), 0);
  }

  @Test
  public void testSkip() throws IOException {
    LengthLimitedInputStream in = new LengthLimitedInputStream(baseIn, 1, 2);
    assertEquals(in.available(), 2);

    assertEquals(in.skip(1), 1);
    assertEquals(in.available(), 1);
    assertEquals(in.read(), 'l');
    assertEquals(in.read(), -1);
    assertEquals(in.available(), 0);
  }

  @Test
  public void testStreamEndsEarly() throws IOException {
    LengthLimitedInputStream in = new LengthLimitedInputStream(baseIn, 0, 100);
    assertEquals(in.available(), 100);

    int nRead = in.read(buf, 0, buf.length);
    assertEquals(nRead, data.length);
    byte[] b = new byte[nRead];
    System.arraycopy(buf, 0, b, 0, nRead);
    assertEquals(b, data);
    assertEquals(in.available(), 100 - data.length);

    nRead = in.read(buf, 0, buf.length);
    assertEquals(nRead, -1);
    assertEquals(in.available(), 0);
  }

  @Test
  public void testStreamEndsEarlyByBytes() throws IOException {
    LengthLimitedInputStream in = new LengthLimitedInputStream(baseIn, 3, 100);
    assertEquals(in.available(), 100);

    assertEquals(in.read(), 'l');
    assertEquals(in.available(), 99);
    assertEquals(in.read(), 'o');
    assertEquals(in.available(), 98);
    assertEquals(in.read(), -1);
    assertEquals(in.available(), 0);
  }

}
