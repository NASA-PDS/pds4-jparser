package gov.nasa.pds.objectAccess;

import gov.nasa.arc.pds.xml.generated.Array2DImage;
import gov.nasa.arc.pds.xml.generated.FileAreaObservational;
import gov.nasa.arc.pds.xml.generated.ProductObservational;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import jpl.mipl.io.vicar.VicarInputFile;

import nom.tam.fits.Fits;
import nom.tam.fits.ImageHDU;

import org.testng.Assert;
import org.testng.annotations.Test;

import gov.nasa.pds.tools.LabelParserException;
import gov.nasa.pds.tools.dict.parser.DictionaryParser;
import gov.nasa.pds.tools.label.Label;
import gov.nasa.pds.tools.label.PointerResolver;
import gov.nasa.pds.tools.label.StandardPathResolver;
import gov.nasa.pds.tools.label.parser.DefaultLabelParser;
import gov.nasa.pds.tools.label.validate.Validator;

public class TwoDImageExporterTest {
	
	final String pdsXMLfilename = "src/test/resources/dph_example_products/Bundle_sampleProducts_20110415.xml";	
	final String filenameArray = "src/test/resources/dph_example_products/product_array_2d_image/Product_Array_2D_Image_20110415.xml";
	final String filenameTable = "src/test/resources/dph_example_products/product_table_character/Product_Table_Character_20110415.xml";
	final String filenameDoc = "src/test/resources/dph_example_products/product_document/Product_Document_20110415.xml";

	private final ObjectProvider objectAccess = new ObjectAccess(new File("src/test/resources/" +
			"dph_example_products/product_array_2d_image"));
	
	public int[][] sineImageValues() throws IOException {
		int width = 512; // Dimensions of the image
		int height = 512; 
		
		int[][] values = new int[width][height];
		FileOutputStream rawFile = new FileOutputStream(new File(objectAccess.getArchiveRoot()+"glpattern1.raw"));
		BufferedOutputStream bos = new BufferedOutputStream(rawFile);
		for(int h=0;h<height;h++) {
			for(int w=0;w<width;w++) { 
				bos.write(127+(int)(128*Math.sin(w/32.)*Math.sin(h/32.))); // Weird sin pattern. 
			}
		}
		bos.close();
		return values;
	}
	
	@Test
	public void testImageExport() throws IOException {
		ProductObservational p = objectAccess.getObservationalProduct("glpattern1.xml");
		FileAreaObservational fileArea = p.getFileAreaObservationals().get(0);
		List<Array2DImage> imageList = objectAccess.getArray2DImages(fileArea);
		for (Array2DImage img : imageList) {
			TwoDImageExporter ic = new TwoDImageExporter(fileArea, objectAccess);
			ic.setTargetPixelDepth(8);
			ic.maximizeDynamicRange(false);
			ic.setExportType("PNG");
			ic.setArray2DImage(img);
			//TODO Handle case where image is set first, then other settings are set
			FileOutputStream fos = new FileOutputStream(new File(objectAccess.getRoot().getAbsolutePath(), "glpattern1MDRFalse-8.png"));
			ic.convert(img, fos);
		}
		File outputFile = new File(objectAccess.getRoot().getAbsolutePath(), "glpattern1MDRFalse-8.png");
		Assert.assertTrue(outputFile.exists());
		BufferedImage testImage = ImageIO.read(outputFile);
		Raster raster = testImage.getData();
		int width = 512; // Dimensions of the image
		int height = 512; 
		for(int h=0;h<height;h++) {
			for(int w=0;w<width;w++) {
				int sample = raster.getSample(w, h, 0);
				int expected = 127+(int)(128*Math.sin(w/32.)*Math.sin(h/32.)); // Weird sin pattern.
				Assert.assertEquals(sample, expected);
			}
		}
		outputFile.deleteOnExit();
		
	}
	
	
	@Test
	public void testImageExport2() throws Exception {
		TwoDImageExporter ic = ExporterFactory.get2DImageExporter(new File(
				"src/test/resources/dph_example_products/product_array_2d_image/glpattern1.xml"), 0);
		ic.setTargetPixelDepth(8);
		ic.maximizeDynamicRange(false);
		ic.setExportType("PNG");
		FileOutputStream fos = new FileOutputStream(new File(objectAccess.getRoot().getAbsolutePath(), "glpattern1MDRFalse-8.png"));
		ic.convert(fos, 0);
		File outputFile2 = new File(objectAccess.getRoot().getAbsolutePath(), "glpattern1MDRFalse-8.png");
		Assert.assertTrue(outputFile2.exists());
		BufferedImage testImage = ImageIO.read(outputFile2);
		Raster raster = testImage.getData();
		int width = 512; // Dimensions of the image
		int height = 512; 
		for(int h=0;h<height;h++) {
			for(int w=0;w<width;w++) {
				int sample = raster.getSample(w, h, 0);
				int expected = 127+(int)(128*Math.sin(w/32.)*Math.sin(h/32.)); // Weird sin pattern.
				Assert.assertEquals(sample, expected);
			}
		}
		outputFile2.deleteOnExit();
		
	}
	
	@Test
	public void testVicarImageExport() throws Exception {
		File outputFile = new File(objectAccess.getRoot().getAbsolutePath(), "glpattern1MDRFalse-8.vic");
		File inputFile = new File(objectAccess.getRoot().getAbsolutePath(), "glpattern1.xml");
		TwoDImageExporter ic = ExporterFactory.get2DImageExporter(inputFile, 0);
		ic.setTargetPixelDepth(8);
		ic.maximizeDynamicRange(false);
		ic.setExportType("VICAR");
		FileOutputStream fos = new FileOutputStream(outputFile);
		ic.convert(fos, 0);
		VicarInputFile vicarFile = new VicarInputFile(new File(objectAccess.getRoot().getAbsolutePath(), 
				"glpattern1MDRFalse-8.vic").getAbsolutePath());
		//System.out.println(vicarFile.getVicarLabel().toString());
		Assert.assertEquals(vicarFile.getSystemLabel().getOrg(), "BSQ");
		Assert.assertEquals(vicarFile.getSystemLabel().getFormat(), "BYTE");
		Assert.assertEquals(vicarFile.getSystemLabel().getType(), "IMAGE");
		Assert.assertEquals(vicarFile.getSystemLabel().getRecsize(), 512);
		Assert.assertEquals(vicarFile.getSystemLabel().getNL(), 512);
		Assert.assertEquals(vicarFile.getSystemLabel().getNS(), 512);
		Assert.assertEquals(vicarFile.getSystemLabel().getNB(), 1);
		outputFile.deleteOnExit();
	}
	
	@Test
	public void testPDS3ImageExport() throws Exception {
		File outputFile = new File(objectAccess.getRoot().getAbsolutePath(), "glpattern1MDRFalse-8.pds3");
		File inputFile = new File(objectAccess.getRoot().getAbsolutePath(), "glpattern1.xml");
		TwoDImageExporter ic = ExporterFactory.get2DImageExporter(inputFile, 0);
		ic.setTargetPixelDepth(8);
		ic.maximizeDynamicRange(false);
		ic.setExportType("PDS3");
		FileOutputStream fos = new FileOutputStream(new File(objectAccess.getRoot().getAbsolutePath(), "glpattern1MDRFalse-8.pds3"));
		ic.convert(fos, 0);
		PointerResolver resolver = new StandardPathResolver();
		DefaultLabelParser parser = new DefaultLabelParser(resolver);
		Label labelObj = parser.parseLabel(outputFile);
		Validator validator = new Validator();
		try {
			validator.validate(labelObj, DictionaryParser.parse(new File("src/test/resources/pds_dictionary/pdsdd.full")));
			Assert.assertEquals(labelObj.getProblems().size(), 0);
			Assert.assertTrue(labelObj.isValid());
		} catch (LabelParserException e) {
			for (LabelParserException problem : labelObj.getProblems()) {
				//System.out.println(problem.getLineNumber() + ": " + problem.getType().name());
				//System.out.println(problem.getKey());
			}
		}
		outputFile.deleteOnExit();
	}
	
	@Test
	public void testFitsImageExport() throws Exception {
		File outputFile = new File(objectAccess.getRoot().getAbsolutePath(), "0030598439.fits");
		File inputFile = new File(objectAccess.getRoot().getAbsolutePath(), "0030598439.xml");
		TwoDImageExporter ic = ExporterFactory.get2DImageExporter(inputFile, 0);
		ic.setTargetPixelDepth(8);
		ic.maximizeDynamicRange(true);
		ic.setExportType("fits");
		FileOutputStream fos = new FileOutputStream(outputFile);
		ic.convert(fos, 0);
		Fits fitsFile = new Fits(outputFile);
		ImageHDU header = (ImageHDU) fitsFile.getHDU(0);
		Assert.assertEquals(header.getHeader().getIntValue("NAXIS"), 2);
		Assert.assertEquals(header.getBitPix(), 8);
		Assert.assertEquals(header.getHeader().getIntValue("NAXIS1"), 1024);
		Assert.assertEquals(header.getHeader().getIntValue("NAXIS2"), 1024);
		outputFile.deleteOnExit();
	}
}


