
package gov.nasa.pds.objectAccess;

import gov.nasa.pds.domain.Array2DImageProduct;
import gov.nasa.pds.domain.DocumentProduct;
import gov.nasa.pds.domain.HasFields;
import gov.nasa.pds.domain.PDSObject;
import gov.nasa.pds.domain.TableCharacterProduct;
import gov.nasa.pds.domain.TokenizedLabel;
import gov.nasa.pds.domain.TokenizedLabelSection;

import java.util.Arrays;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

public class ObjectAccessTest {

	final static String archiveRoot = ".";
	final String pdsXMLfilename = "src/test/resources/dph_example_products/Bundle_sampleProducts_20110415.xml";	
	final String filenameArray = "src/test/resources/dph_example_products/product_array_2d_image/Product_Array_2D_Image_20110415.xml";
	final String filenameTable = "src/test/resources/dph_example_products/product_table_character/Product_Table_Character_20110415.xml";
	final String filenameDoc = "src/test/resources/dph_example_products/product_document/Product_Document_20110415.xml";
	final String expectedFirstFilename = "src/test/resources/dph_example_products/product_array_2d_image/i943630r.raw";
	final String expectedFirstFn = "src/test/resources/dph_example_products/product_document/meca_rdr_sis.pdf";

	@Test 
	public void testParseAnyObject() throws Exception {
		ObjectProvider oa = new ObjectAccess(archiveRoot);
		PDSObject pdsObject = oa.makePDS4Object(pdsXMLfilename); 
		Assert.assertEquals( pdsObject.getTypeID(), "gov.nasa.pds.domain.Collection"); //default object type
	}

	@Test(enabled = false)
	public void testTable() throws Exception {
		ObjectProvider oa = new ObjectAccess(archiveRoot);
		TableCharacterProduct tableProduct = oa.parseTableCharacterProduct(filenameTable, 2000);
		HasFields fieldSample = (HasFields)tableProduct.getFirstChild();
		Assert.assertEquals(fieldSample.getFields().size(), 10);
		Assert.assertEquals(tableProduct.getLogical_identifier(), "urn:nasa:pds:example.DPH.sampleProducts:exampleProducts:TableChar.PHX-M-TT-5-WIND-VEL-DIR-V1.0");
	}

	@Test
	public void test2DArray() throws Exception {
		ObjectProvider oa = new ObjectAccess(archiveRoot);
		Array2DImageProduct imageProduct = oa.parseImageProduct(filenameArray); 
		Assert.assertEquals(imageProduct.getLogical_identifier(), "urn:nasa:pds:example.DPH.sampleProducts:exampleProducts:Array2D_Image.MPFL-M-IMP-2-EDR-V1.0");
		Assert.assertEquals(imageProduct.getFirstFilename(), expectedFirstFilename);
	}

	@Test(enabled = false)
	public void testDocument() throws Exception {
		ObjectProvider oa = new ObjectAccess(archiveRoot);
		DocumentProduct product = oa.parseDocumentProduct(filenameDoc); 
		Assert.assertEquals(product.getLogical_identifier(), "urn:nasa:pds:example.DPH.sampleProducts:exampleProducts:DocumentProduct.PHX-M-MECA-4-NIRDR-V1.0");
		Assert.assertEquals(product.getFirstFilename(), expectedFirstFn);
	}
	
	@Test
	public void testLabel() throws Exception {
		final String sep = "xxx";
		String[] expectedSections = {"Identification_Area_Product","Cross_Reference_Area_Product","Observation_Area","File_Area_Observational"};
		
		ObjectProvider oa = new ObjectAccess(archiveRoot);
		String flatLabel = oa.getFlattenedLabel(filenameTable, sep);
		Assert.assertEquals(LabelParserTest.countBreaks(flatLabel, sep), 142);
		Assert.assertEquals(LabelParserTest.countEquals(flatLabel), 147); // some descriptions have the '=' char
  
		TokenizedLabel tokLabel = oa.getTokenizedLabel(filenameTable, sep);
        Map <String,TokenizedLabelSection> allSections = tokLabel.getAllSections();
        Assert.assertEquals(allSections.size(), 4);
        Assert.assertTrue(allSections.keySet().containsAll(Arrays.asList(expectedSections)));
        TokenizedLabelSection fileSection = allSections.get("File_Area_Observational");
        Assert.assertEquals(fileSection.getAllNameValuePairs().size(),111);
	}
}

