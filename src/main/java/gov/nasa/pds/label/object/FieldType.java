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

import gov.nasa.pds.objectAccess.table.BitFieldAdapter;
import gov.nasa.pds.objectAccess.table.DefaultFieldAdapter;
import gov.nasa.pds.objectAccess.table.DoubleBinaryFieldAdapter;
import gov.nasa.pds.objectAccess.table.FieldAdapter;
import gov.nasa.pds.objectAccess.table.FloatBinaryFieldAdapter;
import gov.nasa.pds.objectAccess.table.IntegerBinaryFieldAdapter;
import gov.nasa.pds.objectAccess.table.NumericTextFieldAdapter;

import java.util.HashMap;
import java.util.Map;

/**
 * Defines the set of field types that may appear in tables of
 * any of the table types. Indicates the type string that
 * will be found in XML label instances, the field justification
 * when displayed in fixed-width format, and the field adapter
 * for reading and writing the field.
 */
public enum FieldType {

	/** Any URI. */
	ASCII_ANYURI("ASCII_AnyURI"),

	/** Boolean true or false. */
	ASCII_BOOLEAN("ASCII_Boolean", new DefaultFieldAdapter(), true),

	/** Digital object identifier. */
	ASCII_DOI("ASCII_DOI"),

	/** A date using day-of-year. */
	ASCII_DATE("ASCII_Date"),

	/** A date using day-of-year. */
	ASCII_DATE_DOY("ASCII_Date_DOY"),

	/** A date using day-of-year. */
	ASCII_DATE_TIME("ASCII_Date_Time"),

	/** A date-time using day-of-year. */
	ASCII_DATE_TIME_DOY("ASCII_Date_Time_DOY"),

	/** A date-time using day-of-year in UTC time zone. */
	ASCII_DATE_TIME_DOY_UTC("ASCII_Date_Time_DOY_UTC"),
	
	/** A date-time using year-month-day in UTC time zone. */
	ASCII_DATE_TIME_UTC("ASCII_Date_Time_UTC"),

	/** A date-time using year-month-day. */
	ASCII_DATE_TIME_YMD("ASCII_Date_Time_YMD"),

	 /** A date-time using year-month-day in UTC time zone. */
  ASCII_DATE_TIME_YMD_UTC("ASCII_Date_Time_YMD_UTC"),
	
	/** A date using year-month-day. */
	ASCII_DATE_YMD("ASCII_Date_YMD"),

	/** A directory path. */
	ASCII_DIRECTORY_PATH_NAME("ASCII_Directory_Path_Name"),

	/** A file name. */
	ASCII_FILE_NAME("ASCII_File_Name"),

	/** A file spec name. */
	ASCII_FILE_SPECIFICATION_NAME("ASCII_File_Specification_Name"),

	/** An integer. */
	ASCII_INTEGER("ASCII_Integer", new NumericTextFieldAdapter(), true),

	/** A logical identifier. */
	ASCII_LID("ASCII_LID"),

	/** A logical identifier with version ID. */
	ASCII_LIDVID("ASCII_LIDVID"),

	/** A logical identifier with version ID (???). */
	ASCII_LIDVID_LID("ASCII_LIDVID_LID"),

	/** An MD5 hash. */
	ASCII_MD5_CHECKSUM("ASCII_MD5_Checksum"),

	/** A nonnegative integer. */
	ASCII_NONNEGATIVE_INTEGER("ASCII_NonNegative_Integer", new NumericTextFieldAdapter(), true),

	/** A hexadecimal integer. */
	ASCII_NUMERIC_BASE16("ASCII_Numeric_Base16", new NumericTextFieldAdapter(), true),

	/** A base 2 integer. */
	ASCII_NUMERIC_BASE2("ASCII_Numeric_Base2"),

	/** A base 8 integer. */
	ASCII_NUMERIC_BASE8("ASCII_Numeric_Base8"),

	/** A floating-point value. */
	ASCII_REAL("ASCII_Real", new NumericTextFieldAdapter(), true),

	/** An ASCII string. */
	ASCII_STRING("ASCII_String"),

	/** A time. */
	ASCII_TIME("ASCII_Time"),

	/** A version ID. */
	ASCII_VID("ASCII_VID"),

	/** A complex, little-endian, 16-byte binary value. (double real, double imaginary) */
	COMPLEXLSB16("ComplexLSB16"),

	/** A complex, little-endian, 8-byte binary value. (float real, float imaginary) */
	COMPLEXLSB8("ComplexLSB8"),

	/** A complex, big-endian, 16-byte binary value. (double real, double imaginary) */
	COMPLEXMSB16("ComplexMSB16"),

	/** A complex, big-endian, 8-byte binary value. (float real, float imaginary) */
	COMPLEXMSB8("ComplexMSB8"),

	/** An 8-byte, little-endian IEEE real. */
	IEEE754LSBDOUBLE("IEEE754LSBDouble", new DoubleBinaryFieldAdapter(false), true),

	/** A 4-byte, little-endian IEEE real. */
	IEEE754LSBSINGLE("IEEE754LSBSingle", new FloatBinaryFieldAdapter(false), true),

	/** An 8-byte, big-endian IEEE real. */
	IEEE754MSBDOUBLE("IEEE754MSBDouble", new DoubleBinaryFieldAdapter(true), true),

	/** A 4-byte, big-endian IEEE real. */
	IEEE754MSBSINGLE("IEEE754MSBSingle", new FloatBinaryFieldAdapter(true), true),

	/** A signed 1-byte integer. */
	SIGNEDBYTE("SignedByte", new IntegerBinaryFieldAdapter(1, true, true), true),

	/** A signed, 2-byte, little-endian integer. */
	SIGNEDLSB2("SignedLSB2", new IntegerBinaryFieldAdapter(2, true, false), true),

	/** A signed, 4-byte, little-endian integer. */
	SIGNEDLSB4("SignedLSB4", new IntegerBinaryFieldAdapter(4, true, false), true),

	/** A signed, 8-byte, little-endian integer. */
	SIGNEDLSB8("SignedLSB8", new IntegerBinaryFieldAdapter(8, true, false), true),

	/** A signed, 2-byte, big-endian integer. */
	SIGNEDMSB2("SignedMSB2", new IntegerBinaryFieldAdapter(2, true, true), true),

	/** A signed, 4-byte, big-endian integer. */
	SIGNEDMSB4("SignedMSB4", new IntegerBinaryFieldAdapter(4, true, true), true),

	/** A signed, 8-byte, big-endian integer. */
	SIGNEDMSB8("SignedMSB8", new IntegerBinaryFieldAdapter(8, true, true), true),

	/** A Unicode string encoded into bytes using UTF-8 encoding. */
	UTF8_STRING("UTF8_String"),

	/** An unsigned, 1-byte integer. */
	UNSIGNEDBYTE("UnsignedByte", new IntegerBinaryFieldAdapter(1, false, true), true),

	/** An unsigned, 2-byte, little-endian integer. */
	UNSIGNEDLSB2("UnsignedLSB2", new IntegerBinaryFieldAdapter(2, false, false), true),

	/** An unsigned, 4-byte, little-endian integer. */
	UNSIGNEDLSB4("UnsignedLSB4", new IntegerBinaryFieldAdapter(4, false, false), true),

	/** An unsigned, 8-byte, little-endian integer. */
	UNSIGNEDLSB8("UnsignedLSB8", new IntegerBinaryFieldAdapter(8, false, false), true),

	/** An unsigned, 2-byte, big-endian integer. */
	UNSIGNEDMSB2("UnsignedMSB2", new IntegerBinaryFieldAdapter(2, false, true), true),

	/** An unsigned, 4-byte, big-endian integer. */
	UNSIGNEDMSB4("UnsignedMSB4", new IntegerBinaryFieldAdapter(4, false, true), true),

	/** An unsigned, 8-byte, big-endian integer. */
	UNSIGNEDMSB8("UnsignedMSB8", new IntegerBinaryFieldAdapter(8, false, true), true),

	/** A signed bit string in a packed field. */
	SIGNEDBITSTRING("SignedBitString", new BitFieldAdapter(true), true),

	/** An unsigned bit string in a packed field. */
	UNSIGNEDBITSTRING("UnsignedBitString", new BitFieldAdapter(false), true),
	
	UNKNOWN("Unknown", new DefaultFieldAdapter(), true);

	private static Map<String, FieldType> xmlTypeMap = new HashMap<String, FieldType>();
	static {
		for (FieldType fieldType : FieldType.values()) {
			xmlTypeMap.put(fieldType.getXMLType(), fieldType);
		}
	}

	private String xmlType;
	private FieldAdapter adapter;
	private boolean isRightJustified;

	private FieldType(String xmlType) {
		this(xmlType, new DefaultFieldAdapter(), false);
	}

	private FieldType(String xmlType, FieldAdapter adapter, boolean isRightJustified) {
		this.xmlType = xmlType;
		this.adapter = adapter;
		this.isRightJustified = isRightJustified;
	}

	/**
	 * Gets the proper field type for an XML type string in a
	 * label instance.
	 *
	 * @param xmlType the XML type string
	 * @return the field type corresponding to the XML type
	 */
	public static FieldType getFieldType(String xmlType) {
		FieldType type = xmlTypeMap.get(xmlType);
		if (type == null) {
			throw new IllegalArgumentException("No field type definition found for XML type (" + xmlType + ")");
		}

		return type;
	}

	/**
	 * Gets the type string that will occur in XML labels.
	 *
	 * @return the XML type string
	 */
	public String getXMLType() {
		return xmlType;
	}

	/**
	 * Gets a field adapter for this field type.
	 *
	 * @return the field adapter
	 */
	public FieldAdapter getAdapter() {
		return adapter;
	}

	/**
	 * Tests whether the field should be right justified on output.
	 *
	 * @return true, if the field should be right justified, false otherwise
	 */
	public boolean isRightJustified() {
		return isRightJustified;
	}

}
