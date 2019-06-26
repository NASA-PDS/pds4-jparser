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

package gov.nasa.pds.objectAccess.table;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.WordUtils;

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
	HORIZONTAL_TAB("horizontal tab", (char) 9),

	/** Vertical tab field delimiter. */
	VERTICAL_BAR("vertical bar", '|'),

	/** Carriage return and line feed (CRLF) record delimiter. */
	CARRIAGE_RETURN_LINE_FEED("carriage-return line-feed", "\r\n");

	private static Map<String, DelimiterType> xmlTypeMap = new HashMap<String, DelimiterType>();
	static {
		for (DelimiterType type : DelimiterType.values()) {
			xmlTypeMap.put(type.getXmlType().toLowerCase(), type);
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
		return WordUtils.capitalize(xmlType, new char[]{' ', '-'});
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
