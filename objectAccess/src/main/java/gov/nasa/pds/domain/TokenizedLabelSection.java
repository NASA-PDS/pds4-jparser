package gov.nasa.pds.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**  Contains information about a PDS label section. */

public class TokenizedLabelSection  implements Serializable {

	private static final long serialVersionUID = -4812616516676039885L;

	public TokenizedLabelSection() {
		super();
	}

	List<String[]> contents = new ArrayList<String[]>(); // List name value pairs


	public List<String[]> getAllNameValuePairs() {
		return contents;
	}

	public void addNameValuePair(String name, String value) {
		String[] pair = new String[2];
		pair[0] = name;
		pair[1] = value;
		contents.add(pair);
	}

}

