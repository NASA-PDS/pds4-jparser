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

import gov.nasa.arc.pds.xml.generated.TableBinary;
import gov.nasa.arc.pds.xml.generated.TableCharacter;
import gov.nasa.arc.pds.xml.generated.TableDelimited;

/**
 * Implements a factory for getting adapter facade objects.
 */
public enum AdapterFactory {

	INSTANCE;

	/**
	 * Gets a table adapter facade object.
	 * 
	 * @param tableObject the table object for which we want an adapter
	 * @return the facade adapter object
	 */
	public TableAdapter getTableAdapter(Object tableObject) {
		if (tableObject instanceof TableBinary) {
			return new TableBinaryAdapter((TableBinary) tableObject);
		} else if (tableObject instanceof TableDelimited) {
			return new TableDelimitedAdapter((TableDelimited) tableObject);
		} else {
			// Must be TableCharacter
			return new TableCharacterAdapter((TableCharacter) tableObject);
		}
	}
	
}
