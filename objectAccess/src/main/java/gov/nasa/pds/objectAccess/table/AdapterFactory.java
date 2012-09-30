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
