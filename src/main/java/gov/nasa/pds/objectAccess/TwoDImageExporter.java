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

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.IndexColorModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.ParameterBlock;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedOp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.primitives.UnsignedLong;
import com.sun.media.jai.codec.SeekableStream;
import gov.nasa.arc.pds.xml.generated.Array2DImage;
import gov.nasa.arc.pds.xml.generated.AxisArray;
import gov.nasa.arc.pds.xml.generated.DisplaySettings;
import gov.nasa.arc.pds.xml.generated.FileAreaObservational;
import gov.nasa.pds.label.DisplayDirection;
import gov.nasa.pds.objectAccess.DataType.NumericDataType;
import jpl.mipl.io.plugins.DOMtoPDSlabel;
import jpl.mipl.io.plugins.ImageToPDS_DOM;
import jpl.mipl.io.vicar.AlreadyOpenException;
import nom.tam.fits.Fits;
import nom.tam.fits.FitsException;
import nom.tam.fits.FitsFactory;
import nom.tam.fits.ImageHDU;
import nom.tam.util.BufferedDataOutputStream;


/**
 * Class for converting 2D PDS images.
 *
 * @author dcberrio, mcayanan
 *
 */
public class TwoDImageExporter extends ImageExporter implements Exporter<Array2DImage> {

  Logger logger = LoggerFactory.getLogger(TwoDImageExporter.class);

  private NumericDataType rawDataType;

  /**
   * Default target settings are 8-bit gray scale
   */
  private int targetPixelBitDepth = 8;
  private int targetLevels = (int) Math.pow(2, targetPixelBitDepth);
  private IndexColorModel colorModel;
  private BufferedImage bufferedImage;
  private int imageType = BufferedImage.TYPE_BYTE_INDEXED;
  private boolean maximizeDynamicRange = true;
  private String exportType = "PNG";
  private Array2DImage pdsImage;
  private boolean lineDirectionDown = true;
  private boolean sampleDirectionRight = true;
  private boolean firstIndexFastest = true;
  private int numberOfBands = 1;
  private double scalingFactor = 1.0;
  private double valueOffset = 0.0;
  private double dataMin = Double.NEGATIVE_INFINITY;
  private double dataMax = Double.POSITIVE_INFINITY;


  TwoDImageExporter(FileAreaObservational fileArea, ObjectProvider provider) throws IOException {
    super(fileArea, provider);
  }

  TwoDImageExporter(File label, int fileAreaIndex) throws Exception {
    this(label.toURI().toURL(), fileAreaIndex);
  }

  TwoDImageExporter(URL label, int fileAreaIndex) throws Exception {
    super(label, fileAreaIndex);
  }


  private void setImageType() {
    switch (targetPixelBitDepth) {
      case 8:
        imageType = BufferedImage.TYPE_BYTE_INDEXED;
        break;
      case 16:
        imageType = BufferedImage.TYPE_USHORT_GRAY;
    }

  }

  @Override
  public void convert(OutputStream outputStream, int objectIndex) throws IOException {
    List<Array2DImage> imageList = getObjectProvider().getArray2DImages(getObservationalFileArea());
    setArray2DImage(imageList.get(objectIndex));
    convert(getArray2DImage(), outputStream);
  }

  /**
   * Converts a 2D array file to a viewable image file.
   *
   * @param outputStream the output stream
   * @param array2DImage the array2DImage object to convert
   * @throws IOException if there is an exception writing to the stream or reading the image
   */
  @Override
  public void convert(Array2DImage array2DImage, OutputStream outputStream) throws IOException {
    setArray2DImage(array2DImage);
    int lines = 0;
    int samples = 0;
    if (array2DImage.getAxes() == 2) {
      for (AxisArray axis : array2DImage.getAxisArraies()) {
        // TODO axis ordering -- how does axis order related to index order?
        if (axis.getSequenceNumber() == 2) {
          samples = axis.getElements().intValueExact();
        } else {
          lines = axis.getElements().intValueExact();
        }
      }
    }

    BufferedInputStream bufferedInputStream = new BufferedInputStream(
        new URL(getObjectProvider().getRoot(), getObservationalFileArea().getFile().getFileName())
            .openStream());
    bufferedInputStream.skip(array2DImage.getOffset().getValue().longValueExact());

    int scanline_stride = samples;
    int[] band_offsets = new int[1];
    for (int i = 0; i < 1; i++) {
      band_offsets[i] = 0;
    }
    /* Taken from Vicar IO library. */
    int dataBufferType = DataBuffer.TYPE_FLOAT;
    String dataType = array2DImage.getElementArray().getDataType();
    SampleModel sampleModel = new PixelInterleavedSampleModel(dataBufferType, samples, lines, 1,
        scanline_stride, band_offsets);
    ColorModel colorModel = PlanarImage.createColorModel(sampleModel);
    ImageTypeSpecifier imageType = new ImageTypeSpecifier(colorModel, sampleModel);
    bufferedImage = imageType.createBufferedImage(samples, lines);

    flexReadToRaster(bufferedInputStream, bufferedImage, lines, samples);
    // Auto scale the image if there were no min/max values defined in the label.
    bufferedImage = scaleImage(bufferedImage);

    // Call JAI's reformat operation to allow image data to be displayable
    bufferedImage = toDisplayableImage(bufferedImage);

    if (exportType.equals("VICAR") || exportType.equalsIgnoreCase("PDS3")) {
      try {
        writeLabel(outputStream, getExportType());
      } catch (Exception e) {
        // Caught by method
      }
    }
    // ImageIO write
    writeRasterImage(outputStream, bufferedImage);
    outputStream.close();
  }

  private void setImageElementsDataType(Array2DImage array2dImage) {
    try {
      setRawDataType(
          Enum.valueOf(NumericDataType.class, array2dImage.getElementArray().getDataType()));
    } catch (Exception e) {
      logger.error("Array data type is not valid, null, or unsupported", e);
      throw new IllegalArgumentException("Array data type is not valid, null, or unsupported");
    }
  }


  /**
   * Read in the data maximum and minimum values. TODO There's various types of range scaling/levels
   * adjustment that we could do: 1) Scale all values according to difference between maximum input
   * value and the target pixel bit depth using a linear transformation 2) Scale all values
   * according to the difference between the maximum space of input values and the target pixel bit
   * depth...ie not based on actual input values The default (maximizeDynamicRange true) effects 1)
   * and maximizeDynamicRange false does #2.
   *
   * @param array2dImage
   */
  private void setImageStatistics(Array2DImage array2dImage) {
    if (array2dImage.getDisplay2DImage() != null) {
      if (array2dImage.getDisplay2DImage().getLineDisplayDirection().equalsIgnoreCase("UP")) {
        lineDirectionDown = false;
      }
      if (array2dImage.getDisplay2DImage().getSampleDisplayDirection().equalsIgnoreCase("LEFT")) {
        setSampleDirectionRight(false);
      }
    }

    if (array2dImage.getLocalIdentifier() != null) {
      DisplaySettings ds = getDisplaySettings(array2dImage.getLocalIdentifier());
      if (ds != null) {
        DisplayDirection lineDir = null;
        try {
          lineDir = DisplayDirection
              .getDirectionFromValue(ds.getDisplayDirection().getVerticalDisplayDirection());
          if (lineDir.equals(DisplayDirection.BOTTOM_TO_TOP)) {
            lineDirectionDown = false;
          } else if (lineDir.equals(DisplayDirection.TOP_TO_BOTTOM)) {
            lineDirectionDown = true;
          }
        } catch (NullPointerException ignore) {
          logger.error("Cannot find vertical_display_direction element "
              + "in the Display_Direction area with identifier '"
              + array2dImage.getLocalIdentifier() + "'.");
        }

        DisplayDirection sampleDir = null;
        try {
          sampleDir = DisplayDirection
              .getDirectionFromValue(ds.getDisplayDirection().getHorizontalDisplayDirection());
          if (sampleDir.equals(DisplayDirection.RIGHT_TO_LEFT)) {
            setSampleDirectionRight(false);
          } else if (sampleDir.equals(DisplayDirection.LEFT_TO_RIGHT)) {
            setSampleDirectionRight(true);
          }
        } catch (NullPointerException ignore) {
          logger.error("Cannot find horizontal_display_direction element "
              + "in the Display_Direction area with identifier '"
              + array2dImage.getLocalIdentifier() + "'.");
        }
      } else {
        logger.info("No display settings found for identifier '" + array2dImage.getLocalIdentifier()
            + "'.");
      }
    } else {
      logger.info("No display settings found. Missing local_identifier "
          + "element in the Array_2D_Image area.");
    }

    if (array2dImage.getAxisIndexOrder().equalsIgnoreCase("LAST INDEX FASTEST")) {
      setFirstIndexFastest(false);
    }

    if (array2dImage.getElementArray().getScalingFactor() != null) {
      scalingFactor = array2dImage.getElementArray().getScalingFactor().doubleValue();
    }

    if (array2dImage.getElementArray().getValueOffset() != null) {
      valueOffset = array2dImage.getElementArray().getValueOffset().doubleValue();
    }

    // Does the min/max values specified in the label represent the stored
    // value? If so, then we're doing this right in factoring the scaling_factor
    // and offset.
    if (array2dImage.getObjectStatistics() != null) {
      if (array2dImage.getObjectStatistics().getMinimum() != null) {
        dataMin = array2dImage.getObjectStatistics().getMinimum();
        dataMin = (dataMin * scalingFactor) + valueOffset;
      }
      if (array2dImage.getObjectStatistics().getMaximum() != null) {
        dataMax = array2dImage.getObjectStatistics().getMaximum();
        dataMax = (dataMax * scalingFactor) + valueOffset;
      }
    }
    // TODO Handle adjusting dynamic range more completely?

  }


  private void flexReadToRaster(BufferedInputStream inputStream, BufferedImage bufferedImage,
      int lines, int samples) throws IOException {
    WritableRaster raster = bufferedImage.getRaster();
    int countBytes = -1;
    SeekableStream si = null;

    try {
      si = SeekableStream.wrapInputStream(inputStream, false);
      int xWrite = 0;
      int yWrite = 0;
      if (firstIndexFastest) {
        for (int x = 0; x < samples; x++) {
          if (sampleDirectionRight) {
            xWrite = x;
          } else {
            xWrite = samples - x - 1;
          }
          for (int y = 0; y < lines; y++) {
            countBytes += 2;
            double value = 0;
            switch (rawDataType) {
              case SignedByte:
                value = si.readByte();
                break;
              case UnsignedByte:
                value = si.readUnsignedByte();
                break;
              case UnsignedLSB2:
                value = si.readUnsignedShortLE();
                break;
              case SignedLSB2:
                value = si.readShortLE();
                break;
              case UnsignedMSB2:
                value = si.readUnsignedShort();
                break;
              case SignedMSB2:
                value = si.readShort();
                break;
              case UnsignedMSB4:
                value = si.readUnsignedInt();
                break;
              case UnsignedMSB8:
                value = UnsignedLong.valueOf(si.readLong()).doubleValue();
                break;
              case IEEE754MSBSingle:
                value = si.readFloat();
                break;
              case IEEE754MSBDouble:
                value = si.readDouble();
                break;
            }
            // TODO test other input data types
            if (lineDirectionDown) {
              yWrite = y;
            } else {
              yWrite = lines - y - 1;
            }
            value = (value * scalingFactor) + valueOffset;
            if (value < dataMin) {
              value = dataMin;
            }
            if (value > dataMax) {
              value = dataMax;
            }
            raster.setSample(xWrite, yWrite, 0, value);
          }
        }
      } else {
        for (int y = 0; y < lines; y++) {
          if (lineDirectionDown) {
            yWrite = y;
          } else {
            yWrite = lines - y - 1;
          }
          for (int x = 0; x < samples; x++) {
            countBytes += 2;
            double value = 0;
            switch (rawDataType) {
              case SignedByte:
                value = si.readByte();
                break;
              case UnsignedByte:
                value = si.readUnsignedByte();
                break;
              case UnsignedLSB2:
                value = si.readUnsignedShortLE();
                break;
              case SignedLSB2:
                value = si.readShortLE();
                break;
              case UnsignedMSB2:
                value = si.readUnsignedShort();
                break;
              case SignedMSB2:
                value = si.readShort();
                break;
              case UnsignedMSB4:
                value = si.readUnsignedInt();
                break;
              case UnsignedMSB8:
                value = UnsignedLong.valueOf(si.readLong()).doubleValue();
                break;
              case IEEE754MSBSingle:
                value = si.readFloat();
                break;
              case IEEE754MSBDouble:
                value = si.readDouble();
                break;
            }
            // TODO test other input data types
            if (sampleDirectionRight) {
              xWrite = x;
            } else {
              xWrite = samples - x - 1;
            }
            value = (value * scalingFactor) + valueOffset;
            if (value < dataMin) {
              value = dataMin;
            }
            if (value > dataMax) {
              value = dataMax;
            }
            raster.setSample(xWrite, yWrite, 0, value);
          }
        }
      }
    } catch (Exception e) {
      String m = "EOF at byte number: " + countBytes + "inputFile: " + inputStream;
      logger.error(m, e);
      throw new IOException(m);
    } finally {
      if (si != null) {
        try {
          si.close();
        } catch (IOException e) {
        }
      }
      if (inputStream != null) {
        try {
          inputStream.close();
        } catch (IOException ignore) {
          // Ignore
        }
      }
    }
  }

  /**
   * Auto scales the given image by performing amplitude rescaling.
   *
   * @param bufferedImage The image to rescale.
   * @return The rescaled image.
   */
  private BufferedImage scaleImage(BufferedImage bufferedImage) {
    double minValue = dataMin;
    double maxValue = dataMax;
    if ((minValue == Double.NEGATIVE_INFINITY) || (maxValue == Double.POSITIVE_INFINITY)) {
      ParameterBlock pbMaxMin = new ParameterBlock();
      pbMaxMin.addSource(bufferedImage);
      RenderedOp extrema = JAI.create("extrema", pbMaxMin);
      double[] allMins = (double[]) extrema.getProperty("minimum");
      double[] allMaxs = (double[]) extrema.getProperty("maximum");
      if (minValue == Double.NEGATIVE_INFINITY) {
        minValue = allMins[0];
      }
      if (maxValue == Double.POSITIVE_INFINITY) {
        maxValue = allMaxs[0];
      }
      for (int v = 1; v < allMins.length; v++) {
        if (allMins[v] < minValue) {
          minValue = allMins[v];
        }
        if (allMaxs[v] > maxValue) {
          maxValue = allMaxs[v];
        }
      }
    }
    double[] subtractThis = new double[1];
    subtractThis[0] = minValue;
    double[] multiplyBy = new double[1];
    multiplyBy[0] = 255. / (maxValue - minValue);
    PlanarImage planarImage = PlanarImage.wrapRenderedImage(bufferedImage);
    ParameterBlock pbSub = new ParameterBlock();
    pbSub.addSource(planarImage);
    pbSub.add(subtractThis);
    planarImage = JAI.create("subtractconst", pbSub, null);
    ParameterBlock pbMult = new ParameterBlock();
    pbMult.addSource(planarImage);
    pbMult.add(multiplyBy);
    planarImage = JAI.create("multiplyconst", pbMult, null);
    return planarImage.getAsBufferedImage();
  }

  /**
   * Create a surrogate image from the given image so that it can be displayable.
   *
   * @param bufferedImage The given image to reformat.
   *
   * @return The surrogate image.
   */
  private BufferedImage toDisplayableImage(BufferedImage bufferedImage) {
    ParameterBlock pbConvert = new ParameterBlock();
    pbConvert.addSource(bufferedImage);
    pbConvert.add(DataBuffer.TYPE_BYTE);
    PlanarImage planarImage = JAI.create("format", pbConvert);
    return planarImage.getAsBufferedImage();
  }

  private void writeRasterImage(OutputStream outputStream, BufferedImage bi) {
    // Store the image using the export format.
    try {
      if (exportType.equals("VICAR") || exportType.equals("PDS3")) {
        ImageIO.write(bi, "raw", outputStream);
      } else if (exportType.equalsIgnoreCase("fits")) {
        writeFitsFile(outputStream, bi);
      } else {
        ImageIO.write(bi, exportType, outputStream);
      }
    } catch (IOException e) {
      String message = "Error writing to output stream";
      logger.error(message, e);
    }
  }

  private void writeFitsFile(OutputStream outputStream, BufferedImage bi) {
    Fits f = new Fits();
    try {
      // FITS is defined with line direction up, opposite of java and other formats

      // Flip the image vertically
      AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
      // PDS-573
      tx.translate(0, -bi.getHeight());
      // TODO should be no interpolation on a simple vertical flip
      AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
      bi = op.filter(bi, null);


      // TODO What order does raster return data....by columns then rows?
      ImageHDU hdu = (ImageHDU) FitsFactory
          .HDUFactory(bi.getData().getDataElements(0, 0, bi.getWidth(), bi.getHeight(), null));
      hdu.addValue("NAXIS", 2, "NUMBER OF AXES");
      // PDS-573
      hdu.addValue("NAXIS1", bi.getWidth(), "NUMBER OF COLUMNS");
      hdu.addValue("NAXIS2", bi.getHeight(), "NUMBER OF ROWS");
      f.addHDU(hdu);
      BufferedDataOutputStream bdos = new BufferedDataOutputStream(outputStream);
      f.write(bdos);
      bdos.close();
    } catch (FitsException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  private void writeLabel(OutputStream outputStream, String type)
      throws AlreadyOpenException, IOException, Exception {
    if (type.equalsIgnoreCase("VICAR")) {
      VicarSystemLabelGenerator labelGenerator = new VicarSystemLabelGenerator();
      int cols = 0, rows = 0;
      if (pdsImage.getAxes() == 2) {
        for (AxisArray axis : pdsImage.getAxisArraies()) {
          // TODO axis ordering -- how does axis order related to index order?
          if (axis.getSequenceNumber() == 2) {
            cols = axis.getElements().intValueExact();
          } else {
            rows = axis.getElements().intValueExact();
          }
        }
      }
      labelGenerator.set_org("BSQ"); // Unless PDS label supports bands, a
                                     // Data Architecture will always be BSQ
      labelGenerator.set_nb(numberOfBands);
      labelGenerator.set_nl(cols);
      labelGenerator.set_ns(rows);
      labelGenerator.set_binc(1.0);
      labelGenerator.set_linc(1.0);
      labelGenerator.set_sinc(1.0);
      labelGenerator.set_datatype(getRawDataType().getVicarAlias());
      labelGenerator.set_tileHeight(rows);
      labelGenerator.set_tileWidth(cols);
      labelGenerator.set_pixelStride(1);
      labelGenerator.generateFile(outputStream);
    } else if (type.equalsIgnoreCase("PDS3")) {
      ImageToPDS_DOM imageToPdsDom = new ImageToPDS_DOM(bufferedImage);
      outputStream
          .write(new DOMtoPDSlabel(imageToPdsDom.getDocument()).toString().getBytes("ASCII"));
    } else {
      String message = "Unsupported label type: " + type;
      logger.error(message);
      throw new Exception(message);
    }
  }

  private IndexColorModel getColorModel() {
    return colorModel;
  }

  private void setColorModel(IndexColorModel colorModel) {
    this.colorModel = colorModel;
  }


  /**
   * Return the target image pixel depth in bits
   *
   * @return targetPixelBitDepth
   */
  public int getTargetPixelDepth() {
    return targetPixelBitDepth;
  }


  /**
   * Set the target pixel bit depth
   *
   * @param targetPixelDepth the target pixel bit depth
   */
  public void setTargetPixelDepth(int targetPixelDepth) {
    if (targetPixelDepth != 8 && targetPixelDepth != 16) {
      String message = "Supported pixel bit depths are 8 and 16";
      logger.error(message);
      throw new IllegalArgumentException(message);
    }
    this.targetPixelBitDepth = targetPixelDepth;
    this.targetLevels = (int) Math.pow(2, this.targetPixelBitDepth);
    switch (targetPixelBitDepth) {
      case 8:
        imageType = BufferedImage.TYPE_BYTE_INDEXED;
        break;
      case 16:
        imageType = BufferedImage.TYPE_USHORT_GRAY;
        break;
    }
  }


  private NumericDataType getRawDataType() {
    return rawDataType;
  }


  private void setRawDataType(NumericDataType rawDataType) {
    this.rawDataType = rawDataType;

  }


  /**
   * Get whether or not input data elements are scaled up to the target pixel bit depth
   *
   * @return boolean
   */
  public boolean maximizeDynamicRange() {
    return maximizeDynamicRange;
  }


  /**
   * Set whether or not input data elements are scaled up to the maximum pixel bit depth
   *
   * @param dynamicRangeScaling
   */
  public void maximizeDynamicRange(boolean dynamicRangeScaling) {
    this.maximizeDynamicRange = dynamicRangeScaling;
  }


  /**
   * Get the export image format
   *
   * @return exportType the export image format
   */
  public String getExportType() {
    return exportType;
  }


  /**
   * Set the export image format. The format is limited to those supported by Java.
   *
   * @param exportType the export image format
   */
  @Override
  public void setExportType(String exportType) {
    Iterator<ImageWriter> imageWriters = ImageIO.getImageWritersByFormatName(exportType);
    if (!imageWriters.hasNext() && !exportType.equalsIgnoreCase("VICAR")
        && !exportType.equalsIgnoreCase("PDS3") && !exportType.equalsIgnoreCase("fits")) {
      String message = "The export image type " + exportType + " is not currently supported.";
      logger.error(message);
      throw new IllegalArgumentException(message);
    }
    this.exportType = exportType;
  }


  /**
   * Is the sample direction to the right?
   *
   * @return sampleDirectionRight
   */
  public boolean isSampleDirectionRight() {
    return sampleDirectionRight;
  }


  /**
   * Set the sample direction.
   *
   * @param sampleDirectionRight
   */
  public void setSampleDirectionRight(boolean sampleDirectionRight) {
    this.sampleDirectionRight = sampleDirectionRight;
  }


  /**
   * Is the first index fastest?
   *
   * @return firstIndexFastest
   */
  public boolean isFirstIndexFastest() {
    return firstIndexFastest;
  }


  /**
   * Set whether the first index is fastest.
   *
   * @param firstIndexFastest
   */
  public void setFirstIndexFastest(boolean firstIndexFastest) {
    this.firstIndexFastest = firstIndexFastest;
  }

  /**
   * Get the Array 2D Image
   *
   * @return pdsImage
   */
  public Array2DImage getArray2DImage() {
    return pdsImage;
  }


  /**
   * Set the Array 2D Image
   *
   * @param img
   */
  public void setArray2DImage(Array2DImage img) {
    this.pdsImage = img;
    setImageElementsDataType(pdsImage);
    setImageStatistics(pdsImage);
    setImageType();
  }



}
