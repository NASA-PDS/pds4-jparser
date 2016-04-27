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
 * Class to encapsulate PDS data types referenced by name in XML-based labels.
 * 
 * @author dcberrio
 */
public interface DataType {

	/** 
	 * @author dcberrio
	 * Enumeration of numeric data types for tables and images.  Includes
	 * fields for providing number of bits and vicar label aliases.

	 */
	public enum NumericDataType {
		SignedByte (8),
		UnsignedByte (8, "BYTE"),
		SignedLSB2(16, "HALF"),
		UnsignedLSB2(16, "HALF"),
		SignedMSB2(16, "HALF"),
		UnsignedMSB2 (16, "HALF"),
		UnsignedMSB4 (32, "FULL"),
		UnsignedMSB8 (64),
		IEEE754MSBSingle (32, "REAL"),
		IEEE754MSBDouble (64, "DOUBLE"),
		ASCII_Integer (32),
		ASCII_Real(64);
		
		private int bits;
		private String vicarAlias;
		
		private NumericDataType(int numberOfBits) {
			bits = numberOfBits;
		}
		
		private NumericDataType(int numberOfBits, String alias) {
			bits = numberOfBits;
			vicarAlias = alias;
		}

		/** 
		 * Get number of bits for the data type.
		 * @return bits
		 */
		public int getBits() {
			return bits;
		}

		/** 
		 * Set number of bits for the data type.
		 * @param bits number of bits.
		 */
		public void setBits(int bits) {
			this.bits = bits;
		}

		/**
		 * Get VICAR alias for the data type.
		 * @return vicarAlias
		 */
		public String getVicarAlias() {
			return vicarAlias;
		}

		/**
		 * Set the VICAR alias for the data type.
		 * @param vicarAlias
		 */
		public void setVicarAlias(String vicarAlias) {
			this.vicarAlias = vicarAlias;
		}
	}
}
