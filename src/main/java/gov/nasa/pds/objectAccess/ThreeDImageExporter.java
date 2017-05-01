// Copyright 2006-2017, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
// Any commercial use must be negotiated with the Office of Technology Transfer
// at the California Institute of Technology.
//
// This software is subject to U. S. export control laws and regulations
// (22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software
// is subject to U.S. export control laws and regulations, the recipient has
// the responsibility to obtain export licenses or other export authority as
// may be required before exporting such information to foreign countries or
// providing access to foreign nationals.
//
// $Id$
package gov.nasa.pds.objectAccess;

import gov.nasa.arc.pds.xml.generated.Array3DImage;
import gov.nasa.arc.pds.xml.generated.AxisArray;
import gov.nasa.arc.pds.xml.generated.DisplaySettings;
import gov.nasa.arc.pds.xml.generated.FileAreaObservational;
import gov.nasa.pds.label.DisplayDirection;
import gov.nasa.pds.objectAccess.DataType.NumericDataType;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BandedSampleModel;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.IndexColorModel;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.ParameterBlock;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
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

import jpl.mipl.io.plugins.DOMtoPDSlabel;
import jpl.mipl.io.plugins.ImageToPDS_DOM;
import jpl.mipl.io.vicar.AlreadyOpenException;
import nom.tam.fits.Fits;
import nom.tam.fits.FitsException;
import nom.tam.fits.FitsFactory;
import nom.tam.fits.ImageHDU;
import nom.tam.util.BufferedDataOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.primitives.UnsignedLong;
import com.sun.media.jai.codec.MemoryCacheSeekableStream;
import com.sun.media.jai.codec.SeekableStream;

/** 
 * Class for converting PDS Array_3D_Image products.
 * @author mcayanan
 *
 */
public class ThreeDImageExporter extends ImageExporter implements Exporter<Array3DImage> {

	Logger logger = LoggerFactory.getLogger(ThreeDImageExporter.class);

	private NumericDataType rawDataType;

	/**
	 * Default target settings are 8-bit gray scale
	 */
	private int targetPixelBitDepth = 8;
	private int targetLevels = (int) Math.pow(2,targetPixelBitDepth);
	private IndexColorModel colorModel;
	private BufferedImage bufferedImage;
	private int imageType = BufferedImage.TYPE_BYTE_INDEXED;
	private boolean maximizeDynamicRange = true;
	private String exportType = "PNG";
	private Array3DImage pdsImage;
	private boolean lineDirectionDown = true;
	private boolean sampleDirectionRight = true;
	private boolean firstIndexFastest = false;
	private double scalingFactor = 1.0;
	private double valueOffset = 0.0;
  private double dataMin = Double.NEGATIVE_INFINITY;
  private double dataMax = Double.POSITIVE_INFINITY;

	ThreeDImageExporter(FileAreaObservational fileArea, ObjectProvider provider) throws IOException {
		super(fileArea, provider);

	}

  ThreeDImageExporter(File label, int fileAreaIndex) throws Exception {
    this(label.toURI().toURL(), fileAreaIndex);
  }
	
	ThreeDImageExporter(URL label, int fileAreaIndex) throws Exception {
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
	public void convert(OutputStream outputStream, int objectIndex)
			throws IOException {
		List<Array3DImage> imageList = getObjectProvider().getArray3DImages(getObservationalFileArea());
		setArray3DImage(imageList.get(objectIndex));
		convert(getArray3DImage(), outputStream);
	}

	/**
	 * Converts a 3D array file to a viewable image file.
	 *
	 * @param outputStream the output stream
	 * @param array3DImage the array3DImage object to convert
	 * @throws IOException if there is an exception writing to the stream or reading the image
	 */
	@Override
	public void convert(Array3DImage array3DImage, OutputStream outputStream) throws IOException {
		setArray3DImage(array3DImage);
		int lines = 0;
		int samples = 0;
		int bands = 1;
		if (array3DImage.getAxes() == 3) {
			for (AxisArray axis : array3DImage.getAxisArraies()) {
				//TODO axis ordering -- how does axis order related to index order?
				if (axis.getSequenceNumber() == 3) {
					samples = axis.getElements();
				} else if (axis.getSequenceNumber() == 2) {
					lines = axis.getElements();
				} else {
				  bands = axis.getElements();
				}
			}
		}

		BufferedInputStream bufferedInputStream = new BufferedInputStream(
		        new URL(getObjectProvider().getRoot(),
				getObservationalFileArea().getFile().getFileName()).openStream());
		bufferedInputStream.skip(Integer.valueOf(array3DImage.getOffset().getValue()));
    int scanline_stride = samples;
    int[] band_offsets = new int[bands];
    int[] bank_indices = new int[bands];
    for (int i = 0; i < bands; i++) {
      band_offsets[i] = 0;
      bank_indices[i] = i;
    }
    int dataBufferType = DataBuffer.TYPE_FLOAT;
    SampleModel sampleModel = new BandedSampleModel(dataBufferType, 
        samples, lines, scanline_stride, bank_indices, band_offsets);
    ColorModel colorModel = PlanarImage.createColorModel(sampleModel);
    ImageTypeSpecifier imageType = new ImageTypeSpecifier(colorModel, sampleModel);
    bufferedImage = imageType.createBufferedImage(samples, lines);
		flexReadToRaster(bufferedInputStream, bufferedImage, lines, samples, bands);
		// Scale the image if there were no min/max values defined in the label.
    bufferedImage = scaleImage(bufferedImage);
    // Call JAI's reformat operation to allow floating point image data to 
    // be displayable
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

  /**
   * Scales the given image by performing amplitude rescaling.
   * 
   * @param bufferedImage The image to rescale.
   * @return The rescaled image.
   */
  private BufferedImage scaleImage(BufferedImage bufferedImage) {
    double minValue = dataMin;
    double maxValue = dataMax;
    if ( (minValue == Double.NEGATIVE_INFINITY) 
        || (maxValue == Double.POSITIVE_INFINITY) ) {
      ParameterBlock pbMaxMin = new ParameterBlock();
      pbMaxMin.addSource(bufferedImage);
      RenderedOp extrema = JAI.create("extrema", pbMaxMin);
      double[] allMins = (double[])extrema.getProperty("minimum");
      double[] allMaxs = (double[])extrema.getProperty("maximum");
      if (minValue == Double.NEGATIVE_INFINITY) {
        minValue = allMins[0];
      }
      if (maxValue == Double.POSITIVE_INFINITY) {
        maxValue = allMaxs[0];
      }
      for(int v=1;v<allMins.length;v++)
      {
        if (allMins[v] < minValue) minValue = allMins[v];
        if (allMaxs[v] > maxValue) maxValue = allMaxs[v];
      }
    }
    double[] subtractThis    = new double[1]; subtractThis[0]    = minValue;
    double[] multiplyBy = new double[1]; multiplyBy[0] = 255./(maxValue-minValue);
    PlanarImage planarImage = PlanarImage.wrapRenderedImage(bufferedImage);
    ParameterBlock pbSub = new ParameterBlock();
    pbSub.addSource(planarImage);
    pbSub.add(subtractThis);
    planarImage = (PlanarImage) JAI.create("subtractconst",pbSub,null);    
    ParameterBlock pbMult = new ParameterBlock();
    pbMult.addSource(planarImage);
    pbMult.add(multiplyBy);
    planarImage = (PlanarImage)JAI.create("multiplyconst",pbMult,null);
    return planarImage.getAsBufferedImage();
  }
	
  /**
   * Create a surrogate image from the given image so that it can be
   * displayable.
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
	
	private void setImageElementsDataType(Array3DImage array3dImage) {
		try {
			rawDataType = Enum.valueOf(NumericDataType.class, array3dImage.getElementArray().getDataType());
		} catch (Exception e) {
			logger.error("Array data type is not valid, null, or unsupported", e);
			throw new IllegalArgumentException("Array data type is not valid, null, or unsupported");
		}
	}


  /** Read in the data maximum and minimum values.
   * TODO
   * There's various types of range scaling/levels adjustment that we could do:
   * 1) Scale all values according to difference between maximum input value and
   *  the  target pixel bit depth using a linear transformation
   * 2) Scale all values according to the difference between the maximum
   * space of input values and the target pixel bit depth...ie not based on
   * actual input values
   * The default (maximizeDynamicRange true) effects 1) and maximizeDynamicRange false does #2.
   * @param array3dImage
   */
  private void setImageStatistics(Array3DImage array3dImage) {
    if (array3dImage.getLocalIdentifier() != null) {
      DisplaySettings ds = getDisplaySettings(array3dImage.getLocalIdentifier());
      if (ds != null) {
        DisplayDirection lineDir = null;
        try {
          lineDir = DisplayDirection.getDirectionFromValue(
            ds.getDisplayDirection().getVerticalDisplayDirection());
          if (lineDir.equals(DisplayDirection.BOTTOM_TO_TOP)) {
            lineDirectionDown = false;
          }
        } catch (NullPointerException ignore) {
          logger.error("Cannot find vertical_display_direction element "
              + "in the Display_Direction area with identifier '"
              + array3dImage.getLocalIdentifier() + "'.");
        }
        
        DisplayDirection sampleDir = null;
        try {
          sampleDir = DisplayDirection.getDirectionFromValue(
            ds.getDisplayDirection().getHorizontalDisplayDirection());
          if (sampleDir.equals(DisplayDirection.RIGHT_TO_LEFT)) {
            setSampleDirectionRight(false);
          }
        } catch (NullPointerException ignore) {
          logger.error("Cannot find horizontal_display_direction element "
              + "in the Display_Direction area with identifier '"
              + array3dImage.getLocalIdentifier() + "'.");          
        }
      } else {
        logger.info("No display settings found for identifier '"
            + array3dImage.getLocalIdentifier() + "'.");
      }
    } else {
      logger.info("No display settings found. Missing local_identifier "
          + "element in the Array_3D_Image area.");
    }
    
    if (array3dImage.getElementArray().getScalingFactor() != null) {
      scalingFactor = array3dImage.getElementArray().getScalingFactor().doubleValue();
    }
    
    if (array3dImage.getElementArray().getValueOffset() != null) {
      valueOffset = array3dImage.getElementArray().getValueOffset().doubleValue();
    }
    
    // Does the min/max values specified in the label represent the stored
    // value? If so, then we're doing this right in factoring the scaling_factor
    // and offset.
    if (array3dImage.getObjectStatistics() != null) {
      if (array3dImage.getObjectStatistics().getMinimum() != null) {
        dataMin = array3dImage.getObjectStatistics().getMinimum();
        dataMin = (dataMin * scalingFactor) + valueOffset;
      }
      if (array3dImage.getObjectStatistics().getMaximum() != null) {
        dataMax = array3dImage.getObjectStatistics().getMaximum();
        dataMax = (dataMax * scalingFactor) + valueOffset;
      }
    }
  }


	private void flexReadToRaster(BufferedInputStream inputStream, 
	    BufferedImage bufferedImage, int lines, int samples, int bands)
	    throws IOException {
		WritableRaster raster= bufferedImage.getRaster();
		int countBytes = -1;
		SeekableStream si = null;
		try {
      si = new MemoryCacheSeekableStream(inputStream);
			int xWrite = 0;
			int yWrite = 0;
		  for (int b = 0; b < bands; b++) {
				for (int y = 0; y<lines; y++) {
          if (lineDirectionDown) {
						yWrite = y;
					} else {
						yWrite = lines-y-1;
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
						//TODO test other input data types
	          if (sampleDirectionRight) {
							xWrite = x;
						} else {
							xWrite = samples-x-1;
						}
            value = (value * scalingFactor) + valueOffset;
            if (value < dataMin) {
              value = dataMin;
            }
            if (value > dataMax) {
              value = dataMax;
            }             
            raster.setSample(xWrite, yWrite, b, value);        
					}
				}
		  }
		} catch (Exception e) {
			String m = "EOF at byte number: "+countBytes+ "inputFile: " + inputStream;
			logger.error(m, e);
			throw new IOException(m);
		} finally {
			if (si != null) {
				try {si.close();} catch (IOException e) {}
			}
		}
		
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
			if (!lineDirectionDown) {
				// Flip the image vertically
				AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
				tx.translate(0, -bi.getHeight());
				//TODO should be no interpolation on a simple vertical flip
				AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
				bi = op.filter(bi, null);
			}
			// TODO What order does raster return data....by columns then rows?
			ImageHDU hdu = (ImageHDU) FitsFactory.HDUFactory(bi.getData().getDataElements(0, 0, bi.getWidth(), bi.getHeight(), null));
			hdu.addValue("NAXIS", 2, "NUMBER OF AXES");
			hdu.addValue("NAXIS1", bi.getHeight(), "NUMBER OF COLUMNS");
			hdu.addValue("NAXIS2", bi.getWidth(), "NUMBER OF ROWS");
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

	private void writeLabel(OutputStream outputStream, String type) throws AlreadyOpenException, IOException, Exception {
		if (type.equalsIgnoreCase("VICAR")) {
			VicarSystemLabelGenerator labelGenerator = new VicarSystemLabelGenerator();
			int cols = 0, rows = 0, bands = 1;
			if (pdsImage.getAxes() == 3) {
				for (AxisArray axis : pdsImage.getAxisArraies()) {
					//TODO axis ordering -- how does axis order related to index order?
					if (axis.getSequenceNumber() == 3) {
						cols = axis.getElements();
					} else if (axis.getSequenceNumber() == 2) {
						rows = axis.getElements();
					} else {
					  bands = axis.getElements();
					}
				}
			}
			labelGenerator.set_org("BSQ");  // Unless PDS label supports bands, a
											// Data Architecture will always be BSQ
			labelGenerator.set_nb(bands);
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
			outputStream.write(new DOMtoPDSlabel(imageToPdsDom.getDocument()).toString().getBytes("ASCII"));
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


	/** Return the target image pixel depth in bits
	 * @return targetPixelBitDepth
	 */
	public int getTargetPixelDepth() {
		return targetPixelBitDepth;
	}


	/** Set the target pixel bit depth
	 * @param targetPixelDepth the target pixel bit depth
	 */
	public void setTargetPixelDepth(int targetPixelDepth) {
		if (targetPixelDepth != 8 && targetPixelDepth != 16) {
			String message = "Supported pixel bit depths are 8 and 16";
			logger.error(message);
			throw new IllegalArgumentException(message);
		}
		this.targetPixelBitDepth = targetPixelDepth;
		this.targetLevels = (int) Math.pow(2,this.targetPixelBitDepth);
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


	/** Get whether or not input data elements are scaled up to the
	 * target pixel bit depth
	 * @return boolean
	 */
	public boolean maximizeDynamicRange() {
		return maximizeDynamicRange;
	}


	/** Set whether or not input data elements are scaled up to the
	 * maximum pixel bit depth
	 * @param dynamicRangeScaling
	 */
	public void maximizeDynamicRange(boolean dynamicRangeScaling) {
		this.maximizeDynamicRange = dynamicRangeScaling;
	}


	/** Get the export image format
	 * @return exportType the export image format
	 */
	public String getExportType() {
		return exportType;
	}


	/** Set the export image format.  The format is limited to those
	 * supported by Java.
	 * @param exportType the export image format
	 */
	@Override
	public void setExportType(String exportType) {
		Iterator<ImageWriter> imageWriters= ImageIO.getImageWritersByFormatName(exportType);
		if (imageWriters.hasNext()
				|| exportType.equalsIgnoreCase("VICAR")
				|| exportType.equalsIgnoreCase("PDS3")
				|| exportType.equalsIgnoreCase("fits")) {
			this.exportType = exportType;
		} else {
			String message = "The export image type " +exportType + " is not currently supported.";
			logger.error(message);
			throw new IllegalArgumentException(message);
		}
	}


	/** Is the sample direction to the right?
	 * @return sampleDirectionRight
	 */
	public boolean isSampleDirectionRight() {
		return sampleDirectionRight;
	}


	/** Set the sample direction.
	 * @param sampleDirectionRight
	 */
	public void setSampleDirectionRight(boolean sampleDirectionRight) {
		this.sampleDirectionRight = sampleDirectionRight;
	}


	/** Is the first index fastest?
	 * @return firstIndexFastest
	 */
	public boolean isFirstIndexFastest() {
		return firstIndexFastest;
	}


	/** Set whether the first index is fastest.
	 * @param firstIndexFastest
	 */
	public void setFirstIndexFastest(boolean firstIndexFastest) {
		this.firstIndexFastest = firstIndexFastest;
	}

	/** Get the Array 3D Image
	 * @return pdsImage
	 */
	public Array3DImage getArray3DImage() {
		return pdsImage;
	}


	/** Set the Array 3D Image
	 * @param img
	 */
	public void setArray3DImage(Array3DImage img) {
		this.pdsImage = img;
		setImageElementsDataType(pdsImage);
		setImageStatistics(pdsImage);
		setImageType();
	}
}
