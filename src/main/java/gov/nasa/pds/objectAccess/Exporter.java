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

import gov.nasa.arc.pds.xml.generated.FileAreaObservational;
import gov.nasa.pds.objectAccess.InvalidTableException;

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
	void convert(T object, OutputStream outputStream) throws IOException, InvalidTableException;

	/**
	 * Converts the object at index objectIndex into the desired export type.
	 *
	 * @param outputStream the output stream for the output object
	 * @param objectIndex  the index of the input object of type T in the associated
	 * 		  observational file area
	 * @throws IOException
	 */
	void convert(OutputStream outputStream, int objectIndex) throws IOException, InvalidTableException;

}
