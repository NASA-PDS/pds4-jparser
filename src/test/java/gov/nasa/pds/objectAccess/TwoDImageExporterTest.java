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

import gov.nasa.arc.pds.xml.generated.Array2DImage;
import gov.nasa.arc.pds.xml.generated.FileAreaObservational;
import gov.nasa.arc.pds.xml.generated.ProductObservational;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.List;

import javax.imageio.ImageIO;

import jpl.mipl.io.vicar.VicarInputFile;

import nom.tam.fits.Fits;
import nom.tam.fits.ImageHDU;

import org.apache.commons.io.FileUtils;
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

	private final static File productDir = new File("src/test/resources/1000/image");
	private final static File productFile = new File(productDir, "glpattern1.xml");
	
	private final ObjectProvider objectAccess;
	
	public TwoDImageExporterTest() throws MalformedURLException, URISyntaxException {
	  objectAccess = new ObjectAccess(new File("src/test/resources/" +
	      "1000/image")); 
	}
	
	public int[][] sineImageValues() throws IOException {
		int width = 512; // Dimensions of the image
		int height = 512; 
		
		int[][] values = new int[width][height];
		FileOutputStream rawFile = new FileOutputStream(new File(objectAccess.getArchiveRoot(), "glpattern1.raw"));
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
	public void testImageExport() throws IOException, ParseException {
		ProductObservational p = objectAccess.getProduct(productFile, ProductObservational.class);
		FileAreaObservational fileArea = p.getFileAreaObservationals().get(0);
		List<Array2DImage> imageList = objectAccess.getArray2DImages(fileArea);
		for (Array2DImage img : imageList) {
			TwoDImageExporter ic = new TwoDImageExporter(fileArea, objectAccess);
			ic.setTargetPixelDepth(8);
			ic.maximizeDynamicRange(false);
			ic.setExportType("PNG");
			ic.setArray2DImage(img);
			//TODO Handle case where image is set first, then other settings are set
			FileOutputStream fos = new FileOutputStream(new File(FileUtils.toFile(objectAccess.getRoot()), "glpattern1MDRFalse-8.png"));
			ic.convert(img, fos);
		}
		File outputFile = new File(productDir, "glpattern1MDRFalse-8.png");
		Assert.assertTrue(outputFile.exists());
		BufferedImage testImage = ImageIO.read(outputFile);
		Raster raster = testImage.getData();
		int width = 512; // Dimensions of the image
		int height = 512; 
		for(int h=0;h<height;h++) {
			for(int w=0;w<width;w++) {
				double sample = raster.getSample(w, h, 0);
				double expected = 127+(int)(128*Math.sin(w/32.)*Math.sin(h/32.)); // Weird sin pattern.
				Assert.assertEquals(sample, expected, 1.0);
			}
		}
		outputFile.deleteOnExit();
		
	}
	
	
	@Test
	public void testImageExport2() throws Exception {
		TwoDImageExporter ic = ExporterFactory.get2DImageExporter(productFile, 0);
		ic.setTargetPixelDepth(8);
		ic.maximizeDynamicRange(false);
		ic.setExportType("PNG");
		FileOutputStream fos = new FileOutputStream(new File(productDir, "glpattern1MDRFalse-8.png"));
		ic.convert(fos, 0);
		File outputFile2 = new File(productDir, "glpattern1MDRFalse-8.png");
		Assert.assertTrue(outputFile2.exists());
		BufferedImage testImage = ImageIO.read(outputFile2);
		Raster raster = testImage.getData();
		int width = 512; // Dimensions of the image
		int height = 512; 
		for(int h=0;h<height;h++) {
			for(int w=0;w<width;w++) {
				double sample = raster.getSample(w, h, 0);
				double expected = 127+(int)(128*Math.sin(w/32.)*Math.sin(h/32.)); // Weird sin pattern.
				Assert.assertEquals(sample, expected, 1.0);
			}
		}
		outputFile2.deleteOnExit();
		
	}
	
	@Test
	public void testVicarImageExport() throws Exception {
		File outputFile = new File(productDir, "glpattern1MDRFalse-8.vic");
		TwoDImageExporter ic = ExporterFactory.get2DImageExporter(productFile, 0);
		ic.setTargetPixelDepth(8);
		ic.maximizeDynamicRange(false);
		ic.setExportType("VICAR");
		FileOutputStream fos = new FileOutputStream(outputFile);
		ic.convert(fos, 0);
		VicarInputFile vicarFile = new VicarInputFile(new File(FileUtils.toFile(objectAccess.getRoot()), 
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
		File outputFile = new File(productDir, "glpattern1MDRFalse-8.pds3");
		TwoDImageExporter ic = ExporterFactory.get2DImageExporter(productFile, 0);
		ic.setTargetPixelDepth(8);
		ic.maximizeDynamicRange(false);
		ic.setExportType("PDS3");
		FileOutputStream fos = new FileOutputStream(new File(FileUtils.toFile(objectAccess.getRoot()), "glpattern1MDRFalse-8.pds3"));
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
		File outputFile = new File(productDir, "0030598439.fits");
		File inputFile = new File(productDir, "0030598439.xml");
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


