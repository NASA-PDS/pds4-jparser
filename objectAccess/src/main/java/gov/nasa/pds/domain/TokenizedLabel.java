package gov.nasa.pds.domain;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**  Contains information about a PDS label. This label is broken into tokens suitable for display. */

public class TokenizedLabel implements Serializable {

    //	http://blog.holyjeez.com/2008/09/16/gwt-serialization-error-missing-default-constructor/
	private static final long serialVersionUID = -8245266403789955969L;

	public TokenizedLabel() {
		super();
		this.title = "defaultTitle";
	}


	public TokenizedLabel(String title) {
		this.title = title;
	}

    /** Collection of subsection headers and a block of subsection contents. 
     * This container maintains insertion order. 
     **/
	private Map<String, TokenizedLabelSection> sections = new LinkedHashMap<String, TokenizedLabelSection>();
    
	/** title of the label. */
	private String title;
	
	
	
	
	public String getTitle() {
		return title;
	}

	public Map<String, TokenizedLabelSection> getAllSections() {
		return sections;
	}
	
	public TokenizedLabelSection getSectionByTitle(String sectionTitle) {
		return sections.get(sectionTitle);
	}
	
    /**
     * Adds a new section entry.  If the section does not yet exists, makes a new subsection too.
     * @param sectionTitle title of the section
     * @param name section contents name
     * @param value section contents value
     */
	public void putSectionNameValuePair(String sectionTitle, String name, String value) {
		TokenizedLabelSection sc = sections.get(sectionTitle);
		if (sc == null) {
			sc = new TokenizedLabelSection();
		}
		sc.addNameValuePair(name, value);
		sections.put(sectionTitle, sc);	
	}
	
	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder("");
		sb.append("<h3>" + getTitle()+"</h3>");
		Map<String, TokenizedLabelSection> sections = getAllSections();
		for (String sectionTitle : sections.keySet()) {       	
			sb.append("<h5>" + sectionTitle+"</h5>");
			TokenizedLabelSection sc = sections.get(sectionTitle);
			List<String[]> nv = sc.getAllNameValuePairs();
			for (String[] pair : nv) {
				sb.append(pair[0] +" " + pair[1] +"<br>");
			}
		}
		return sb.toString();
	}
	
}
