package gov.nasa.pds.domain;

/**
 * An object with this capability has web resources associated with it.
 * Examples are URLs to an image file or document.
 */
public interface HasURL {
	
	public String getUrl();

	public void setUrl(String url);
}
