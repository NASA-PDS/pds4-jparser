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

package gov.nasa.pds.label;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import org.testng.annotations.Test;
import gov.nasa.pds.label.object.ArrayObject;
import gov.nasa.pds.label.object.DataObject;
import gov.nasa.pds.label.object.GenericObject;

public class TestFileSizeNotNeeded {

  /**
   * Tests that the file_size attribute is not needed in a label to recognize tables declared in the
   * label. This tests issue PDS-340 (https://oodt.jpl.nasa.gov/jira/browse/PDS-340?filter=11220).
   *
   * @throws Exception if there is a problem
   */
  @Test
  public void testFileSizeNotNeeded() throws Exception {
    createDataFile("src/test/resources/mvn_lpw.cdf", 230296);
    Label label = Label.open(new File("src/test/resources/mvn_lpw.xml"));
    List<DataObject> objects = label.getObjects();
    assertEquals(objects.size(), 5); // Header, Array_2D, Array_1D, Array_2D, Array_1D

    assertTrue(objects.get(0) instanceof GenericObject);
    checkArray(objects.get(1), 2);
    checkArray(objects.get(2), 1);
    checkArray(objects.get(3), 2);
    checkArray(objects.get(4), 1);
  }

  /**
   * Tests that a relative path in the current directory does not cause a problem. The test is
   * identical to {@link #testFileSizeNotNeeded()}, but copies the label file to a temporary file in
   * the current directory, first.
   *
   * @throws Exception if there is a problem
   */
  @Test
  public void testEmptyParentPath() throws Exception {
    File tempLabel = File.createTempFile("test", ".xml", new File("."));
    tempLabel.deleteOnExit();
    copyFile(new File("src/test/resources/mvn_lpw.xml"), tempLabel);
    createDataFile("mvn_lpw.cdf", 230296);

    Label label = Label.open(new File(tempLabel.getName()));
    List<DataObject> objects = label.getObjects();
    assertEquals(objects.size(), 5); // Header, Array_2D, Array_1D, Array_2D, Array_1D

    assertTrue(objects.get(0) instanceof GenericObject);
    checkArray(objects.get(1), 2);
    checkArray(objects.get(2), 1);
    checkArray(objects.get(3), 2);
    checkArray(objects.get(4), 1);
  }

  private void copyFile(File src, File dest) throws IOException {
    FileInputStream in = null;
    FileOutputStream out = null;

    try {
      in = new FileInputStream(src);
      out = new FileOutputStream(dest);
      byte[] buf = new byte[0x1000];

      for (;;) {
        int nRead = in.read(buf);
        if (nRead <= 0) {
          break;
        }

        out.write(buf, 0, nRead);
      }
    } finally {
      if (in != null) {
        in.close();
      }
      if (out != null) {
        out.close();
      }
    }
  }

  private void checkArray(DataObject obj, int numAxes) {
    assertTrue(obj instanceof ArrayObject);

    ArrayObject array = (ArrayObject) obj;
    assertEquals(array.getAxes(), numAxes);
  }

  private void createDataFile(String path, long size) throws IOException {
    File f = new File(path);
    f.deleteOnExit();
    OutputStream out = new FileOutputStream(f);
    try {
      byte[] buf = new byte[65536];
      while (size > 0) {
        int chunkSize = (size > buf.length ? buf.length : (int) size);
        out.write(buf, 0, chunkSize);
        size -= chunkSize;
      }
    } finally {
      out.close();
    }
  }

}
