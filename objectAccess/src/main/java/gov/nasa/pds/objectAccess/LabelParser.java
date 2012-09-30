package gov.nasa.pds.objectAccess;


import gov.nasa.pds.domain.TokenizedLabel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/** Parses a PDS label. */

public class LabelParser {
	Logger logger = LoggerFactory.getLogger(LabelParser.class);

	String archiveRoot = null;
	private String filename;
	private String title;
	private String separatorToken;
	private StringBuffer sb = new StringBuffer();

	LabelParser(String archiveRoot, String filename) {
		this(archiveRoot, filename, "<br>");
	}

	LabelParser(String archiveRoot, String filename, String separatorToken) {
		super();
		this.archiveRoot = archiveRoot;
		this.filename = filename;
		this.separatorToken = separatorToken;
	}

	/**
	 * Flatten an xml instance into token separated lines.
	 * @return flattened and separated string
	 * @throws Exception upon error
	 */
	String flatten() throws Exception {
		logger.debug("xml transfrom {} ", filename);

		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			String thisFqFilename = ArchiveLocator.resolveDataItemLocation(archiveRoot, this.filename);
			Document doc = builder.parse(thisFqFilename);
			title = doc.getDocumentElement().getNodeName();

			NodeList childNodes = doc.getChildNodes();
			for (int k=0; k< childNodes.getLength(); k++) {
				Node node = childNodes.item(k);
				if (node.getNodeType() == Node.ELEMENT_NODE && node instanceof Element) {
					visit("",  (Element) node);
				}
			}

		}
		catch (Exception e) {

			logger.error("xml transfrom {} "+e.getMessage());
			throw new Exception(e);
		}
		return sb.toString();
	}


	void visit(String prefix, Element e) throws Exception {

		
		NodeList allChildren = e.getChildNodes();
		if (allChildren.getLength() == 1) {
			Node leaf = allChildren.item(0);
			if (leaf.getNodeType() == Node.TEXT_NODE && leaf instanceof CharacterData) {
				CharacterData cd = (CharacterData) leaf;
				sb.append(stringToHTMLString(prefix  + " = "+ cd.getData()));
				sb.append(this.separatorToken);
			} 
			return; 
		} else {

			for (int i=0; i < allChildren.getLength(); i++) {
				//com.sun.org.apache.xerces.internal.dom.DeferredTextImpl cannot be cast to org.w3c.dom.Element
				//http://download.oracle.com/javase/6/docs/api/org/w3c/dom/Node.html
				Node childNode = allChildren.item(i);
				if (childNode.getNodeType() == Node.ELEMENT_NODE || childNode instanceof Element) {
					Element child =  (Element) allChildren.item(i);
					String p = prefix.isEmpty() ? "" : prefix + ".";
					visit(p+child.getNodeName(), child);
				}
			}
		}
		return;
	}


	TokenizedLabel tokenize(String flatLabel) throws Exception {
		logger.debug("tokenizer:: ", filename);
		TokenizedLabel tokLabel = null;

		try {

			tokLabel = new TokenizedLabel(title);
			// For each flattened line, pull the subtitle token, and r and l hand side tokens.
			Pattern pattern = Pattern.compile("[\\w]+");
			String s = flatLabel;
			int startIdx = 0;
			int endIdx = 0;
			while (true) {
				endIdx = s.indexOf(this.separatorToken);
				if (endIdx == -1) break;
				String newLine = s.substring(startIdx, endIdx);               
				// Get one line of subtitle contents and add it to the tokenized label.
				Matcher m = pattern.matcher(newLine);
				if (m.find()) {
					int idx = m.end();
					String newSectionSubtitle = newLine.substring(0, idx);
					String contents = newLine.substring(idx + 1);
					String name = contents.substring(0, contents.indexOf('=') - 1);
					String value = contents.substring(contents.indexOf('=') + 2);
					tokLabel.putSectionNameValuePair(newSectionSubtitle, name, value);
				}
				s = s.substring(endIdx + this.separatorToken.length()); // remove current line 				
			}
		}
		catch (Exception e) {
			logger.error("tokenizer:: "+e.getMessage());
			throw new Exception(e);
		}
		return tokLabel;
	}

	private static String stringToHTMLString(String string) {
		StringBuffer sb = new StringBuffer(string.length());
		// true if last char was blank
		boolean lastWasBlankChar = false;
		int len = string.length();
		char c;

		for (int i = 0; i < len; i++)
		{
			c = string.charAt(i);
			if (c == ' ') {
				// blank gets extra work,
				// this solves the problem you get if you replace all
				// blanks with &nbsp;, if you do that you loss 
				// word breaking
				if (lastWasBlankChar) {
					lastWasBlankChar = false;
					sb.append("&nbsp;");
				}
				else {
					lastWasBlankChar = true;
					sb.append(' ');
				}
			}
			else {
				lastWasBlankChar = false;
				//
				// HTML Special Chars
				if (c == '"')
					sb.append("&quot;");
				else if (c == '&')
					sb.append("&amp;");
				else if (c == '<')
					sb.append("&lt;");
				else if (c == '>')
					sb.append("&gt;");
				else if (c == '\n')
					// Handle Newline
					sb.append("&lt;br/&gt;");
				else {
					int ci = 0xffff & c;
					if (ci < 160 )
						// nothing special only 7 Bit
						sb.append(c);
					else {
						// Not 7 Bit use the unicode system
						sb.append("&#");
						sb.append(new Integer(ci).toString());
						sb.append(';');
					}
				}
			}
		}
		return sb.toString();
	}


	String getTitle() {
		return title;
	}


}
