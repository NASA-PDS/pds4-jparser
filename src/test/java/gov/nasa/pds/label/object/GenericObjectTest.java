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
package gov.nasa.pds.label.object;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;
import gov.nasa.arc.pds.xml.generated.FileSize;
import gov.nasa.arc.pds.xml.generated.UnitsOfStorage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

import org.testng.annotations.Test;

public class GenericObjectTest {

	@Test
	public void testGetters() throws IOException {
		File f = createTempFile("hello");
		gov.nasa.arc.pds.xml.generated.File fileObject = getFileObject(f);
		GenericObject obj = new GenericObject(f.getParentFile(), fileObject, 1, 2);

		assertEquals(obj.getDataFile(), f);
		assertEquals(obj.getOffset(), 1);
		assertEquals(obj.getSize(), 2);
	}

	@Test
	public void testReadStreamEntireFile() throws IOException {
		File f = createTempFile("hello");
		gov.nasa.arc.pds.xml.generated.File fileObject = getFileObject(f);
		GenericObject obj = new GenericObject(f.getParentFile(), fileObject, 0, f.length());
		String actual = readStream(obj.getInputStream());
		assertEquals(actual, "hello");
	}

	@Test
	public void testReadBufferEntireFile() throws IOException {
		File f = createTempFile("hello");
		gov.nasa.arc.pds.xml.generated.File fileObject = getFileObject(f);
		GenericObject obj = new GenericObject(f.getParentFile(), fileObject, 0, f.length());
		String actual = readBuffer(obj.getBuffer(), "hello".length());
		assertEquals(actual, "hello");
	}

	@Test
	public void testReadStreamPartial() throws IOException {
		File f = createTempFile("hello");
		gov.nasa.arc.pds.xml.generated.File fileObject = getFileObject(f);
		GenericObject obj = new GenericObject(f.getParentFile(), fileObject, 1, 2);
		String actual = readStream(obj.getInputStream());
		assertEquals(actual, "el");
	}

	@Test
	public void testReadBufferPartial() throws IOException {
		File f = createTempFile("hello");
		gov.nasa.arc.pds.xml.generated.File fileObject = getFileObject(f);
		GenericObject obj = new GenericObject(f.getParentFile(), fileObject, 1, 2);
		String actual = readBuffer(obj.getBuffer(), 2);
		assertEquals(actual, "el");
	}

	private File createTempFile(String data) throws IOException {
		File f = File.createTempFile("test", ".txt");
		FileOutputStream out = new FileOutputStream(f);
		out.write(data.getBytes("US-ASCII"));
		out.close();
		return f;
	}

	private gov.nasa.arc.pds.xml.generated.File getFileObject(File f) {
		gov.nasa.arc.pds.xml.generated.File fileObject = new gov.nasa.arc.pds.xml.generated.File();

		fileObject.setCreationDateTime("2000-01-01T00:00:00Z");
		fileObject.setFileName(f.getName());
		FileSize size = new FileSize();
		size.setUnit(UnitsOfStorage.BYTE);
		size.setValue(BigInteger.valueOf(f.length()));
		fileObject.setFileSize(size);

		return fileObject;
	}

	private String readStream(InputStream in) throws IOException {
		byte[] b = new byte[1000];
		int nRead = in.read(b);

		if (nRead < 0) {
			throw new IOException("Error reading temp file - no bytes read.");
		}

		return new String(b, 0, nRead, "US-ASCII");
	}

	private String readBuffer(ByteBuffer buf, int length) throws UnsupportedEncodingException {
		byte[] b = new byte[length];
		buf.get(b);
		try {
			buf.get();
			// If we get here, we got more data, which we didn't expect.
			fail("Failed to reach end of buffer");
		} catch (BufferUnderflowException ex) {
			// ignore
		}

		return new String(b, "US-ASCII");
	}

}
