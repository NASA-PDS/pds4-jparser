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

package gov.nasa.pds.label.object;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.Buffer;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import org.apache.commons.io.FileUtils;
import org.testng.annotations.Test;
import gov.nasa.arc.pds.xml.generated.FileSize;
import gov.nasa.arc.pds.xml.generated.UnitsOfStorage;

public class GenericObjectTest {

  @Test
  public void testGetters() throws IOException {
    File f = createTempFile("hello");
    gov.nasa.arc.pds.xml.generated.File fileObject = getFileObject(f);
    GenericObject obj = new GenericObject(f.getParentFile(), fileObject, 1, 2);

    assertEquals(FileUtils.toFile(obj.getDataFile()), f);
    assertEquals(obj.getOffset(), 1);
    assertEquals(obj.getSize(), 2);
    obj.closeChannel();
  }

  @Test
  public void testReadStreamEntireFile() throws IOException {
    File f = createTempFile("hello");
    gov.nasa.arc.pds.xml.generated.File fileObject = getFileObject(f);
    GenericObject obj = new GenericObject(f.getParentFile(), fileObject, 0, f.length());
    String actual = readStream(obj.getInputStream());
    assertEquals(actual, "hello");
    obj.closeChannel();
  }

  @Test
  public void testReadBufferEntireFile() throws IOException {
    File f = createTempFile("hello");
    gov.nasa.arc.pds.xml.generated.File fileObject = getFileObject(f);
    GenericObject obj = new GenericObject(f.getParentFile(), fileObject, 0, f.length());
    String actual = readBuffer(obj.getChannel(), "hello".length());
    assertEquals(actual, "hello");
    obj.closeChannel();
  }

  @Test
  public void testReadStreamPartial() throws IOException {
    File f = createTempFile("hello");
    gov.nasa.arc.pds.xml.generated.File fileObject = getFileObject(f);
    GenericObject obj = new GenericObject(f.getParentFile(), fileObject, 1, 2);
    String actual = readStream(obj.getInputStream());
    assertEquals(actual, "el");
    obj.closeChannel();
  }

  @Test
  public void testReadBufferPartial() throws IOException {
    File f = createTempFile("hello");
    gov.nasa.arc.pds.xml.generated.File fileObject = getFileObject(f);
    GenericObject obj = new GenericObject(f.getParentFile(), fileObject, 1, 2);
    String actual = readBuffer(obj.getChannel(), 2);
    assertEquals(actual, "el");
    obj.closeChannel();
  }

  private File createTempFile(String data) throws IOException {
    File f = File.createTempFile("test", ".txt");
    FileOutputStream out = new FileOutputStream(f);
    out.write(data.getBytes("US-ASCII"));
    out.close();
    return f;
  }

  private gov.nasa.arc.pds.xml.generated.File getFileObject(File f) {
    gov.nasa.arc.pds.xml.generated.File fileObject = new gov.nasa.arc.pds.xml.generated.File();

    fileObject.setCreationDateTime("2000-01-01T00:00:00Z");
    fileObject.setFileName(f.getName());
    FileSize size = new FileSize();
    size.setUnit(UnitsOfStorage.BYTE);
    size.setValue(BigInteger.valueOf(f.length()));
    fileObject.setFileSize(size);

    return fileObject;
  }

  private String readStream(InputStream in) throws IOException {
    byte[] b = new byte[1000];
    int nRead = in.read(b);

    if (nRead < 0) {
      throw new IOException("Error reading temp file - no bytes read.");
    }

    return new String(b, 0, nRead, "US-ASCII");
  }

  private String readBuffer(SeekableByteChannel ch, int length) throws IOException {
    ByteBuffer buf = ByteBuffer.allocate(length);
    int bytesRead = ch.read(buf);
    ((Buffer) buf).flip();
    byte[] b = new byte[length];
    buf.get(b);
    try {
      buf.get();
      // If we get here, we got more data, which we didn't expect.
      fail("Failed to reach end of buffer");
    } catch (BufferUnderflowException ex) {
      // ignore
    }

    return new String(b, "US-ASCII");
  }

}
