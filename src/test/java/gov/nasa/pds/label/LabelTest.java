// Copyright 2006-2016, by the California Institute of Technology.
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
package gov.nasa.pds.label;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import gov.nasa.arc.pds.xml.generated.ProductObservational;
import gov.nasa.pds.label.object.DataObject;
import gov.nasa.pds.label.object.TableObject;
import gov.nasa.pds.objectAccess.ParseException;

import java.io.File;
import java.util.List;

import org.testng.annotations.Test;

public class LabelTest {

	private static final String TABLE_CHARACTER_LABEL = "src/test/resources/1000/Product_Table_Character.xml";

	@Test
	public void testProductInfo() throws ParseException {
		Label label = Label.open(new File(TABLE_CHARACTER_LABEL));
		assertSame(label.getProductClass(), ProductObservational.class);
		assertEquals(label.getProductType(), ProductType.PRODUCT_OBSERVATIONAL);
		assertSame(label.getLabelStandard(), LabelStandard.PDS4);
		assertEquals(label.getStandardVersion(), "1.0.0.0");
	}

	@Test
	public void testGetObjects() throws Exception {
		Label label = Label.open(new File(TABLE_CHARACTER_LABEL));
		List<DataObject> objects = label.getObjects();
		assertEquals(objects.size(), 1); // Should be only a single table.
		assertTrue(objects.get(0) instanceof TableObject);

		TableObject table = (TableObject) objects.get(0);
		assertEquals(table.getOffset(), 0);
	}

	@Test
	public void testGetTableObjects() throws Exception {
		Label label = Label.open(new File(TABLE_CHARACTER_LABEL));
		List<TableObject> objects = label.getObjects(TableObject.class);
		assertEquals(objects.size(), 1); // Should be only a single table.
		assertTrue(objects.get(0) instanceof TableObject);

		TableObject table = objects.get(0);
		assertEquals(table.getOffset(), 0);
	}

	@Test(expectedExceptions={NullPointerException.class})
	public void testClose() throws ParseException {
		Label label = Label.open(new File(TABLE_CHARACTER_LABEL));
		label.close();
		@SuppressWarnings("unused")
		String version = label.getStandardVersion();
	}

	@Test
	public void testReadCharacterTable() throws Exception {
		Label label = Label.open(new File("src/test/resources/1000/Product_Table_Character.xml"));
		List<TableObject> tables = label.getObjects(TableObject.class);
		assertEquals(tables.size(), 1);

		TableObject table = tables.get(0);
		assertEquals(table.getFields().length, 10);
		int recordCount = 0;
		while (table.readNext() != null) {
			++recordCount;
		}

		assertEquals(recordCount, 23);
	}

	@Test
	public void testReadBinaryTable() throws Exception {
		Label label = Label.open(new File("src/test/resources/1000/Binary_Table_Test.xml"));
		List<TableObject> tables = label.getObjects(TableObject.class);
		assertEquals(tables.size(), 1);

		TableObject table = tables.get(0);
		assertEquals(table.getFields().length, 16); // 4 + 8 bit-fields, + 2 + 2xgroup of 1 = 16
		int recordCount = 0;
		while (table.readNext() != null) {
			++recordCount;
		}

		assertEquals(recordCount, 2);
	}

	@Test
	public void testReadDelimitedTable() throws Exception {
		Label label = Label.open(new File("src/test/resources/1000/Product_Table_Delimited.xml"));
		List<TableObject> tables = label.getObjects(TableObject.class);
		assertEquals(tables.size(), 1);

		TableObject table = tables.get(0);
		assertEquals(table.getFields().length, 13);
		int recordCount = 0;
		while (table.readNext() != null) {
			++recordCount;
		}

		assertEquals(recordCount, 3);
	}

}
