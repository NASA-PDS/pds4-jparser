package gov.nasa.arc.pds.tools.container;

import java.io.UnsupportedEncodingException;

/**
 * A container class for the contents of URL or URI.
 *
 * @author jagander
 * @version $Revision: $
 *
 */
public class URLContentsContainer implements BaseContainerInterface {

  private final byte[] bytes;

  private final String charset;

  public URLContentsContainer(final byte[] bytes, final String charset) {
    this.bytes = bytes;
    this.charset = charset;
  }

  public byte[] getBytes() {
    return this.bytes;
  }

  public String getCharset() {
    return this.charset;
  }

  @Override
  public String toString() {
    try {
      return new String(this.bytes, this.charset);
    } catch (UnsupportedEncodingException e) {
      return new String(this.bytes);
    }
  }

}
