package gov.nasa.pds.objectAccess;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import gov.nasa.pds.domain.TokenizedLabel;
import gov.nasa.pds.domain.TokenizedLabelSection;

import java.awt.Dimension;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.testng.annotations.Test;

public class LabelParserTest {

	final static String archiveRoot = ".";
	final static String archiveFilename = "src/test/resources/dph_example_products/Bundle_sampleProducts_20110415.xml";	
	final static String ProductArray = "src/test/resources/dph_example_products/product_array_2d_image/Product_Array_2D_Image_20110415.xml";
	final static String ProductTable = "src/test/resources/dph_example_products/product_table_character/Product_Table_Character_20110415.xml";


	@Test
	public void testFlatten() throws Exception {
		final String sep = "xxx";
		LabelParser transformXML = new LabelParser(archiveRoot, archiveFilename, sep);
		String v = transformXML.flatten();
		assertEquals(818, v.length());
		assertEquals("Product_Bundle", transformXML.getTitle());
		assertEquals(11, countEquals(v));
		assertEquals(10, countBreaks(v, sep));
	}
	

	@Test
	public void testTokenize() throws Exception {
		String expectedTitle = "Product_Array_2D_Image";
		final String separator = "<br>";
		
		LabelParser lp = new LabelParser(archiveRoot, ProductArray, separator);
		String v = lp.flatten();
		assertEquals("Product_Array_2D_Image", lp.getTitle());
		assertEquals(60, countEquals(v));  //number of flattened lines
		assertEquals(59, countBreaks(v, separator));
		
		TokenizedLabel tokenizedLabel = lp.tokenize(v);
		assertEquals(expectedTitle, tokenizedLabel.getTitle());		
		assertTrue(tokenizedLabel.getAllSections().containsKey("Identification_Area_Product"));
		TokenizedLabelSection idArea = tokenizedLabel.getSectionByTitle("Identification_Area_Product");
		assertEquals(10, idArea.getAllNameValuePairs().size());
		
		Map<String, TokenizedLabelSection> allSections = tokenizedLabel.getAllSections();
		assertEquals(4, allSections.size());
		TokenizedLabelSection fileObservationalArea = tokenizedLabel.getSectionByTitle("File_Area_Observational");
		assertEquals( "File.local_identifier", fileObservationalArea.getAllNameValuePairs().get(0)[0]);
		assertEquals( "i943630r.raw", fileObservationalArea.getAllNameValuePairs().get(0)[1]);		
	}
	

    static int countBreaks(String s, String separatorToken){
		String[] test = s.split(separatorToken);
		int count = test.length - 1;
		return count;
	}

    static int countEquals(String s){
		int foundIndex = -2;
		int total = 0;
		int startIdx = 0;
		final char match = '=';
		final int MATCH_LEN = 1;

		while (foundIndex != -1) {
			foundIndex = s.indexOf(match, startIdx);
			if (foundIndex > 0) {
				total++;
				startIdx = foundIndex +  MATCH_LEN;
			}
		}
		return total;
	}
	
	private String makeTable(String s, String separator) throws Exception {
		int foundIndex = -2;
		int startIdx = 0;		
        StringBuilder sb = new StringBuilder("<table>");      
		while (foundIndex != -1) {
			final String match1 = " = ";
			final int LEN = match1.length();
			foundIndex = s.indexOf(match1, startIdx);
			if (foundIndex > 0) {
				sb.append("<tr><td>" + s.substring(startIdx,foundIndex)); //append name content to left of = 
				startIdx = foundIndex +  LEN;
				
			}
			final String match = separator;
			final int MATCH_LEN = match.length();
			foundIndex = s.indexOf(match, startIdx);
			if (foundIndex > 0) {
				sb.append("</td><td>" + s.substring(startIdx, foundIndex) + "</td></tr>"); //value content to right of = 	
				startIdx = foundIndex +  MATCH_LEN;			
			}
		}
		sb.append("</table>");
		return sb.toString();
	}
	
	
	/**
	 * This entry point is a convenience tester for GUI display of labels. It is not run
	 * in the automated unit tests.
	 */
	static public void main(String[] args) throws Exception {
	    LabelParserTest test = new LabelParserTest();
		JFrame frame = new JFrame("test");
		
		final String sep = "<p>";
		LabelParser transformXML = new LabelParser(archiveRoot, ProductTable, sep);
		String v = transformXML.flatten();	
		TokenizedLabel tokLabel = transformXML.tokenize(v);		
		JPanel listPane = new JPanel();
		listPane.setLayout(new BoxLayout(listPane, BoxLayout.PAGE_AXIS));
		
		JLabel tok = new JLabel("<html><p><p>TOKENINZED: <p>"  + tokLabel.toString() +"</html>");
		listPane.add(tok);
		JLabel flat = new JLabel("<html><p><p>FLATTENED: <p>"  +v+"</html>");
		listPane.add(flat);
		JLabel tablef = new JLabel("<html><p><p>TABLED-FORMATTED: <p>"  + test.makeTable(v, sep)+"</html>");
		listPane.add(tablef);
			
		JScrollPane scrollPane = new JScrollPane(listPane);
		scrollPane.setPreferredSize(new Dimension(450, 300));		
        
		frame.add(scrollPane);
		frame.pack();
		frame.setVisible(true);
	}
}
