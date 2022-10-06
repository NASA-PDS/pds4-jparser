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

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.imageio.ImageIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/** Utility class for converting 2D images. */

class ImageConverter {

  private static final Logger LOGGER = LoggerFactory.getLogger(ImageConverter.class);

  // BufferedImage.TYPE_USHORT_GRAY; //NOTE the only rasters that work are byte.
  private static final ImageConverter INSTANCE = new ImageConverter();
  private static final int SCALE_12_TO_8_BITS = 4096 / 256;

  private ImageConverter() {}

  public static ImageConverter getInstance() {
    return INSTANCE;
  }

  /**
   * Converts a 2D array file to a viewable image file. The newly created image file is written to
   * the same directory and the input file.
   *
   * @param inputFilename
   * @param outputFilename
   * @param rows
   * @param cols
   * @return
   * @throws IOException
   */
  public String convert(String inputFilename, String outputFilename, final int rows, final int cols)
      throws IOException {

    File inputFile = new File(inputFilename);

    // read 2D array
    BufferedImage bi = readToRaster(inputFile.getAbsoluteFile(), rows, cols);

    // ImageIO write
    writeRasterImage(outputFilename, bi);

    return outputFilename;
  }

  BufferedImage readToRaster(File inputFilename, int rows, int cols) throws MalformedURLException {
    return readToRaster(inputFilename.toURI().toURL(), rows, cols);
  }

  BufferedImage readToRaster(URL inputFile, int rows, int cols) {
    BufferedImage rv = new BufferedImage(rows, cols, BufferedImage.TYPE_BYTE_GRAY); // TYPE_USHORT_GRAY
    WritableRaster raster = rv.getRaster();
    int countBytes = -1;
    DataInputStream di = null;

    try {
      di = new DataInputStream(inputFile.openStream());

      for (int y = 0; y < cols; y++) {
        for (int x = 0; x < rows; x++) {
          int firstByte = (0x000000FF & (di.readByte()));
          int secondByte = (0x000000FF & (di.readByte()));

          short anUnsignedShort = (short) (firstByte << 8 | secondByte);
          countBytes += 2;

          int value = anUnsignedShort;

          // int value = 127+(int)(128*Math.sin(x/32.)*Math.sin(y/32.)); // sin pattern, for
          // testing.
          // if (countBytes < 32) System.err.println("----scaled "+anUnsignedShort /
          // SCALE_12_TO_8_BITS);

          raster.setSample(x, y, 0, value / SCALE_12_TO_8_BITS);
        }
      }
    } catch (Exception e) {
      String m = "EOF at byte number: " + countBytes + "inputFile: " + inputFile.toString();
      LOGGER.error(m, e);
    } finally {
      if (di != null) {
        try {
          di.close();
        } catch (IOException e) {
        }
      }
    }
    return rv;
  }

  void writeRasterImage(String putputFilename, BufferedImage bi) {
    // Store the image using the PNG format.
    try {
      ImageIO.write(bi, "PNG", new File(putputFilename));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
