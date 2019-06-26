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

import gov.nasa.pds.objectAccess.ImageConverter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;

import org.testng.Assert;
import org.testng.annotations.Test;

public class ImageConverterTest extends Component {

	private static final long serialVersionUID = 1L;

	BufferedImage theImage;

	final static String inputFilename = "src/test/resources/dph_example_products/product_array_2d_image/i943630r.raw";
	final static String expectedFilename = "src/test/resources/dph_example_products/product_array_2d_image/i943630r.PNG";
	final static int cols = 248; // PDS "LINE SAMPLES" = cols
	final static int rows = 256; // PDS "LINES" = rows

	@Test
	public void convertRAWtoPNG() throws IOException {
		ImageConverter imageConverter = ImageConverter.getInstance();
		
		String convertedFilename = imageConverter.convert(inputFilename, expectedFilename, rows, cols);
		Assert.assertEquals(expectedFilename, convertedFilename);
	}

	@Test
	public void convertingPNGdoesNothing() throws IOException {
		ImageConverter imageConverter = ImageConverter.getInstance();
		
		String convertedFilename = imageConverter.convert(expectedFilename, expectedFilename, rows, cols);
		Assert.assertEquals(convertedFilename, expectedFilename);
	}



	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.drawImage(theImage, 10, 10, null);
	}

	/**
	 * This entry point is a convenience tester for GUI display of images. It is not run
	 * in the automated unit tests.
	 */
	public static void main(String[] args) throws Exception {
		ImageConverterTest imageConverterTest = new ImageConverterTest();

		ImageConverter imageConverter = ImageConverter.getInstance();
		
		BufferedImage bi = imageConverter.readToRaster(new File(inputFilename), rows, cols);

		imageConverterTest.theImage = bi;

		JFrame frame = new JFrame("Button test");
		frame.getContentPane().setBackground(Color.white);

		JButton button = new JButton("Test Enabled");
		frame.add(button, BorderLayout.NORTH);
		frame.add("Center", imageConverterTest);

		frame.pack();
		frame.setSize(500, 500);
		frame.setVisible(true);
	}
}
