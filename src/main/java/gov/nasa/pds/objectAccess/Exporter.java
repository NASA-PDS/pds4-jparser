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
package gov.nasa.pds.objectAccess;

import gov.nasa.arc.pds.xml.generated.FileAreaObservational;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Interface for exporting objects of type T.
 *
 * @author dcberrio
 * @param <T> the object type
 */
public interface Exporter<T> {

	/**
	 * Sets the desired export (output) type.
	 *
	 * @param exportType
	 */
	void setExportType(String exportType);

	/**
	 * Sets the object provider associated with the exporter.
	 *
	 * @param provider the ObjectProvider.
	 */
	void setObjectProvider(ObjectProvider provider);

	/**
	 * Sets the Observational File Area associated with the exporter.
	 *
	 * @param fileAreaObs the Observational File Area
	 */
	void setObservationalFileArea(FileAreaObservational fileAreaObs);

	/**
	 * Converts the input data file into the desired export type.
	 *
	 * @param object the input object of type T
	 * @param outputStream the output stream for the output object
	 * @throws IOException
	 */
	void convert(T object, OutputStream outputStream) throws IOException;

	/**
	 * Converts the object at index objectIndex into the desired export type.
	 *
	 * @param outputStream the output stream for the output object
	 * @param objectIndex  the index of the input object of type T in the associated
	 * 		  observational file area
	 * @throws IOException
	 */
	void convert(OutputStream outputStream, int objectIndex) throws IOException;

}
