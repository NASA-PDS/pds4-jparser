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

/**
 * Encapsulates an exception the occurred while parsing an XML
 * label. Users of this class don't need to catch exceptions
 * from the underlying parsing library.
 */
public class ParseException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new instance with a given message and underlying
	 * cause.
	 * 
	 * @param message the exception message
	 * @param cause the underlying cause
	 */
	public ParseException(String message, Throwable cause) {
		super(message, cause);
	}

}
