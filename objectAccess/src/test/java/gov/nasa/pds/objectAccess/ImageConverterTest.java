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
