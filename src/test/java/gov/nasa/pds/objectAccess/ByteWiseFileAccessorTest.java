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
import static org.testng.Assert.assertNotNull;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ByteWiseFileAccessorTest {

  private static final String BIN_DATA_FILE =
      "./src/test/resources/dph_example_products/product_table_binary/2d234493326edratf3d2537n0m1.dat";

  @SuppressWarnings("unused")
  @DataProvider(name = "readRecordBytesTest")
  private Object[][] readRecordBytesTest() {
    return new Object[][] {
        {1, 8, 8, new byte[] {0x40, 0x1C, 0x7B, 0x73, (byte) 0xA5, (byte) 0xD9, 0x7F, 0x57}},
        {2, 4, 2, new byte[] {0x04, (byte) 0xE8}}};
  }

  @Test(dataProvider = "readRecordBytesTest")
  public void testReadRecordBytes(int recordNum, int offset, int length, byte[] expected) {
    try {
      ByteWiseFileAccessor fileObject =
          new ByteWiseFileAccessor(new File(BIN_DATA_FILE), 0, 96, 336);
      byte[] bytes = fileObject.readRecordBytes(recordNum, offset, length);
      fileObject.close();
      assertNotNull(bytes);
      assertEquals(bytes, expected);
    } catch (Exception e) {
    }
  }

  @SuppressWarnings("unused")
  @DataProvider(name = "readDoubleTest")
  private Object[][] readDoubleTest() {
    return new Object[][] {{1, 8, 7.12055834903716}, {2, 16, 5.82455419999999973E-2}};
  }

  // Tests the case when the table length exceeds the file extent.
  // Here, the size of BIN_DATA_FILE is 32,356 = 336 records * 96 bytes/record.
  // We specify 97 bytes/record.
  @Test(expectedExceptions = {InvalidTableException.class})
  public void testFileTooShort() throws IOException, InvalidTableException {
    ByteWiseFileAccessor fileObject = new ByteWiseFileAccessor(new File(BIN_DATA_FILE), 0, 97, 336);
    fileObject.close();
  }

  @Test(expectedExceptions = {FileNotFoundException.class})
  public void testFileNotFoundException() throws Exception {
    ByteWiseFileAccessor fileObject = new ByteWiseFileAccessor(new File("file.dat"), 0, 96, 336);
    fileObject.close();
  }

  @Test
  public void testFileOffset() throws Exception {
    ByteWiseFileAccessor fileObject = new ByteWiseFileAccessor(new File(BIN_DATA_FILE), 96, 96, 3);
    byte[] bytes = fileObject.readRecordBytes(3, 4, 2);
    assertEquals(bytes, new byte[] {0x04, (byte) 0xE4});
  }
}
