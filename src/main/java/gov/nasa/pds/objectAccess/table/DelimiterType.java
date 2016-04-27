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
package gov.nasa.pds.objectAccess.table;

import java.util.HashMap;
import java.util.Map;

/**
 * Defines a set of field and record delimiters for a table.
 * Indicates the type string that will be found in XML label instances, and
 * the character representation of the type for a field delimiter, or
 * the string representation of the type for a record delimiter.
 */
public enum DelimiterType {

	/** Comma field delimiter. */
	COMMA("comma", ','),

	/** Semicolon field delimiter. */
	SEMICOLON("semicolon", ';'),

	/** Horizontal tab field delimiter. */
	HORIZONTAL_TAB("horizontal_tab", (char) 9),

	/** Vertical tab field delimiter. */
	VERTICAL_BAR("vertical_bar", (char) 11),

	/** Carriage return and line feed (CRLF) record delimiter. */
	CARRIAGE_RETURN_LINE_FEED("carriage_return line_feed", "\r\n");

	private static Map<String, DelimiterType> xmlTypeMap = new HashMap<String, DelimiterType>();
	static {
		for (DelimiterType type : DelimiterType.values()) {
			xmlTypeMap.put(type.getXmlType(), type);
		}
	}

	private String xmlType;
	private String recordDelimiter;
	private char fieldDelimiter;

	private DelimiterType(String xmlType, char fieldDelimiter) {
		this.xmlType = xmlType;
		this.fieldDelimiter = fieldDelimiter;
	}

	private DelimiterType(String xmlType, String recordDelimiter) {
		this.xmlType = xmlType;
		this.recordDelimiter = recordDelimiter;
	}

	/**
	 * Gets the proper delimiter type for an XML type string in a
	 * label instance.
	 *
	 * @param xmlType the XML type string
	 * @return the delimiter type corresponding to the XML type
	 */
	public static DelimiterType getDelimiterType(String xmlType) {
		DelimiterType type = xmlTypeMap.get(xmlType.toLowerCase());
		if (type == null) {
			throw new IllegalArgumentException("No delimiter type definition found for XML type (" + xmlType + ")");
		}

		return type;
	}

	/**
	 * Gets the type string that will occur in XML labels.
	 *
	 * @return the XML type string
	 */
	public String getXmlType() {
		return xmlType;
	}

	/**
	 * Gets the character value for this (field) delimiter type.
	 *
	 * @return the field delimiter character
	 */
	public char getFieldDelimiter() {
		return fieldDelimiter;
	}

	/**
	 * Gets the string value for this (record) delimiter type.
	 *
	 * @return the record delimiter string
	 */
	public String getRecordDelimiter() {
		return recordDelimiter;
	}
}
