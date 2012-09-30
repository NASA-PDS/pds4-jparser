package gov.nasa.pds.objectAccess;

import gov.nasa.arc.pds.xml.generated.Array2DImage;
import gov.nasa.arc.pds.xml.generated.AxisArray;
import gov.nasa.arc.pds.xml.generated.FileAreaObservational;
import gov.nasa.pds.objectAccess.DataType.NumericDataType;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;

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


/** Class for converting 2D PDS images.
 * @author dcberrio
 * @param <T>
 *
 */
public class TwoDImageExporter extends ObjectExporter implements Exporter<Array2DImage> {
	
	Logger logger = LoggerFactory.getLogger(TwoDImageExporter.class);
	
	private NumericDataType rawDataType;
	
	/**
	 * Default target settings are 8-bit gray scale
	 */
	private int targetPixelBitDepth = 8;
	private int targetLevels = (int) Math.pow(2,targetPixelBitDepth);
	private IndexColorModel colorModel;
	private BufferedImage bufferedImage;
	private int imageType = BufferedImage.TYPE_BYTE_INDEXED;
	private double rangeScaleSlope;
	private double rangeScaleIntercept;
	private boolean maximizeDynamicRange = true;
	private String exportType = "PNG";
	private Array2DImage pdsImage;
	private boolean lineDirectionDown = true;
	private boolean sampleDirectionRight = true;
	private boolean firstIndexFastest = true;
	private int numberOfBands = 1;
	
	
	TwoDImageExporter(FileAreaObservational fileArea, ObjectProvider provider) throws IOException {
		super(fileArea, provider);
		
	}
	
	TwoDImageExporter(File label, int fileAreaIndex) throws Exception {
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
	
	public void convert(OutputStream outputStream, int objectIndex)
			throws IOException {
		List<Array2DImage> imageList = getObjectProvider().getArray2DImages(getObservationalFileArea());
		setArray2DImage(imageList.get(objectIndex));
		convert(getArray2DImage(), outputStream);
	}

	/**
	 * Converts a 2D array file to a viewable image file. 
	 * 
	 * @param outputFile (the output file)
	 * @param array2DImage the array2DImage object to convert
	 * @return outputFile 
	 * @throws IOException
	 */
	public void convert(Array2DImage array2DImage, OutputStream outputStream) throws IOException {
		setArray2DImage(array2DImage);
		int rows = 0, cols = 0;
		if (array2DImage.getAxes() == 2) {
			for (AxisArray axis : array2DImage.getAxisArraies()) {
				//TODO axis ordering -- how does axis order related to index order?
				if (axis.getSequenceNumber() == 2) {
					cols = axis.getElements();
				} else {
					rows = axis.getElements();
				}
			}
		}

		FileInputStream inputFileStream = new FileInputStream(new File(getObjectProvider().getRoot().getAbsolutePath(), 
				getObservationalFileArea().getFile().getFileName()));
		inputFileStream.skip(Integer.valueOf(array2DImage.getOffset().getValue()));
		byte[] levels = new byte[targetLevels];
		for(int c=0;c<targetLevels;c++)
			levels[c] = (byte)(c); 
		setColorModel(new IndexColorModel(getTargetPixelDepth(),targetLevels,levels,levels,levels)
				);
		switch (targetPixelBitDepth) {
			case 8:
				bufferedImage =  new BufferedImage(cols,rows,imageType,getColorModel());
				break;
			case 16:
				bufferedImage =  new BufferedImage(cols,rows,imageType);
				break;
		}
		
		flexReadToRaster(inputFileStream, bufferedImage, cols, rows);
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
			rawDataType = Enum.valueOf(NumericDataType.class, array2dImage.getElementArray().getDataType());
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
	 * @param array2dImage
	 */
	private void setImageStatistics(Array2DImage array2dImage) {
		double dataMin = array2dImage.getObjectStatistics().getMinimum();
		double dataMax = array2dImage.getObjectStatistics().getMaximum();
		
		if (array2dImage.getDisplay2DImage().getLineDisplayDirection().equals("UP")) {
			lineDirectionDown = false;
		}
		if (array2dImage.getDisplay2DImage().getSampleDisplayDirection().equals("LEFT")) {
			setSampleDirectionRight(false);
		}
		if (array2dImage.getAxisIndexOrder().equals("LAST_INDEX_FASTEST")) {
			setFirstIndexFastest(false);
		}
		
		if (dataMin == 0 && dataMax == 0) {
			int rows = 0, cols = 0;
			dataMin = Double.POSITIVE_INFINITY;
			dataMax = Double.NEGATIVE_INFINITY;
			if (array2dImage.getAxes() == 2) {
				for (AxisArray axis : array2dImage.getAxisArraies()) {
					//TODO axis ordering
					if (axis.getSequenceNumber() == 2) {
						cols = axis.getElements();
					} else {
						rows = axis.getElements();
					}
				}
			}

			File inputFile = new File(getObjectProvider().getRoot().getAbsolutePath(), getObservationalFileArea().getFile().getFileName());
			int countBytes = -1;
			DataInputStream di = null;
			
			try {
				di = new DataInputStream(new FileInputStream(inputFile));

				for (int y = 0; y < cols; y++){
					for(int x = 0; x<rows; x++){				
						countBytes += 2;
						double value = 0;
						switch (rawDataType) {
						case UnsignedByte: 
							value = (int) di.readByte();
							break;
						case UnsignedMSB2: 
							value = (int) di.readShort();
							break;
						case UnsignedMSB4:
							value = (int) di.readInt();
							break;
						case IEEE754MSBSingle:
							value = (float) di.readFloat();
							break;
						}
						if (value < dataMin) {
							dataMin = value;
						}
						if (value > dataMax) {
							dataMax = value;
						}
					}
				}
			} catch (Exception e) {
				String m = "EOF at byte number: "+countBytes+ "inputFile: " + inputFile;
				logger.error(m, e);
			} finally {
				if (di != null) {
					try {di.close();} catch (IOException e) {}
				}
			}	
		}
		if (this.maximizeDynamicRange) {
			rangeScaleSlope = targetLevels / (dataMax - dataMin + 1) ;
			rangeScaleIntercept = (dataMin*targetLevels) / (dataMin - dataMax - 1 );
		} else {
			rangeScaleSlope = targetLevels / Math.pow(2,rawDataType.getBits());
			rangeScaleIntercept = 0;
		}
		//TODO Handle adjusting dynamic range more completely?
		
	}


	private void flexReadToRaster(FileInputStream inputFileStream, BufferedImage bufferedImage, int rows, int cols) {
	 
		WritableRaster raster= bufferedImage.getRaster();
		int countBytes = -1;
		DataInputStream di = null;
		
		try {
			di = new DataInputStream(inputFileStream);
			int xWrite = 0, yWrite = 0;
			if (firstIndexFastest) {
				for (int y = 0; y < cols; y++){
					if (lineDirectionDown) {
						yWrite = y;
					} else {
						yWrite = cols-y-1;
					}
					for(int x = 0; x<rows; x++){				
						countBytes += 2;
						double value = 0;
						switch (rawDataType) {
						case UnsignedByte: 
							value = (int) di.readByte();
							break;
						case UnsignedMSB2: 
							value = (int) di.readShort();
							break;
						case UnsignedMSB4:
							value = (int) di.readInt();
							break;
						case IEEE754MSBSingle:
							value = (float) di.readFloat();
							break;
						}
						//TODO test other input data types
	
						if (sampleDirectionRight) {
							xWrite = x;
						} else {
							xWrite = rows-x-1;
						}
						raster.setSample(xWrite, yWrite, 0, value * rangeScaleSlope + rangeScaleIntercept ); 
	
					}
				}
			} else {  //TODO WHat has to change for last index fastest? is this correct?
				for (int x = 0; x<rows; x++){
					if (sampleDirectionRight) {
						xWrite = x;
					} else {
						xWrite = rows-x-1;
					}
					for (int y = 0; y < cols; y++){				
						countBytes += 2;
						double value = 0;
						switch (rawDataType) {
						case UnsignedByte: 
							value = (int) di.readByte();
							break;
						case UnsignedMSB2: 
							value = (int) di.readShort();
							break;
						case UnsignedMSB4:
							value = (int) di.readInt();
							break;
						case IEEE754MSBSingle:
							value = (float) di.readFloat();
							break;
						}
						//TODO test other input data types
						if (lineDirectionDown) {
							yWrite = y;
						} else {
							yWrite = cols-y-1;
						}

						raster.setSample(xWrite, yWrite, 0, value * rangeScaleSlope + rangeScaleIntercept ); 
	
					}
				}
			}
		} catch (Exception e) {
			String m = "EOF at byte number: "+countBytes+ "inputFile: " + inputFileStream;
			logger.error(m, e);
		} finally {
			if (di != null) {
				try {di.close();} catch (IOException e) {}
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
			int cols = 0, rows = 0;
			if (pdsImage.getAxes() == 2) {
				for (AxisArray axis : pdsImage.getAxisArraies()) {
					//TODO axis ordering -- how does axis order related to index order?
					if (axis.getSequenceNumber() == 2) {
						cols = axis.getElements();
					} else {
						rows = axis.getElements();
					}
				}
			}
			labelGenerator.set_org("BSQ");  // Unless PDS label supports bands, a
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
	public void setExportType(String exportType) {
		Iterator<ImageWriter> imageWriters= ImageIO.getImageWritersByFormatName(exportType);
		if (imageWriters.hasNext() || 
				exportType.equalsIgnoreCase("VICAR") || 
				exportType.equalsIgnoreCase("PDS3") ||
				exportType.equalsIgnoreCase("fits")) {
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
	
	/** Get the Array 2D Image
	 * @return pdsImage
	 */
	public Array2DImage getArray2DImage() {
		return pdsImage;
	}


	/** Set the Array 2D Image
	 * @param img
	 */
	public void setArray2DImage(Array2DImage img) {
		this.pdsImage = img;
		setImageElementsDataType(pdsImage);
		setImageStatistics(pdsImage);
		setImageType();
	}



}
