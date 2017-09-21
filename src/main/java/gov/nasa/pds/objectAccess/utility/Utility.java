// Copyright 2006-2017, by the California Institute of Technology.
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
// $Id: Utility.java 15480 2017-03-01 18:20:30Z mcayanan $
package gov.nasa.pds.objectAccess.utility;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

import org.xml.sax.InputSource;

/**
 * Utility class.
 *
 * @author mcayanan
 *
 */
public class Utility {

  // Implementation is needed since pds.nasa.gov currently uses SNI
  // which is not supported in Java 6, but is supported in Java 7.
  static {
    javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
    new javax.net.ssl.HostnameVerifier(){
        public boolean verify(String hostname,
                javax.net.ssl.SSLSession sslSession) {
          if (hostname.equals("pds.nasa.gov")
              && hostname.equals(sslSession.getPeerHost())) {
            return true;
          } else {
            return false;
          }
        }
    });
   }

  /**
   * Method that opens a connection. Supports redirects.
   *
   * @param conn URL Connection
   *
   * @return input stream.
   * @throws IOException If an error occurred while opening
   * the stream.
   */
  public static InputStream openConnection(URLConnection conn)
      throws IOException {
    boolean redir;
    int redirects = 0;
    InputStream in = null;
    do {
      if (conn instanceof HttpURLConnection) {
        ((HttpURLConnection) conn).setInstanceFollowRedirects(false);
      }
      // This code can go away when we permanently move to Java 8
      if (conn instanceof HttpsURLConnection) {
        try {
          SSLContext context = SSLContext.getInstance("TLSv1.2");
          context.init(null, null, new java.security.SecureRandom());
          HttpsURLConnection test = (HttpsURLConnection) conn;
          SSLSocketFactory sf = test.getSSLSocketFactory();
          SSLSocketFactory d = HttpsURLConnection.getDefaultSSLSocketFactory();
          ((HttpsURLConnection) conn).setSSLSocketFactory(context.getSocketFactory());          
        } catch (Exception e) {
          throw new IOException(e.getMessage());
        }
      }
      // We want to open the input stream before getting headers
      // because getHeaderField() et al swallow IOExceptions.
      in = conn.getInputStream();
      redir = false;
      if (conn instanceof HttpURLConnection) {
        HttpURLConnection http = (HttpURLConnection) conn;
        int stat = http.getResponseCode();
        if (stat >= 300 && stat <= 307 && stat != 306 &&
            stat != HttpURLConnection.HTTP_NOT_MODIFIED) {
          URL base = http.getURL();
          String loc = http.getHeaderField("Location");
          URL target = null;
          if (loc != null) {
            target = new URL(base, loc);
          }
          http.disconnect();
          // Redirection should be allowed only for HTTP and HTTPS
          // and should be limited to 5 redirections at most.
          if (target == null || !(target.getProtocol().equals("http") ||
              target.getProtocol().equals("https")) ||
              redirects >= 5) {
            throw new SecurityException("illegal URL redirect");
          }
          redir = true;
          conn = target.openConnection();
          redirects++;
        }
      }
    } while (redir);
    return in;
  }
  
  /**
   * 
   * 
   * @param url
   * @return
   * @throws IOException
   */
  public static InputSource openConnection(URL url) throws IOException {
    InputSource inputSource = new InputSource(
        Utility.openConnection(url.openConnection()));
    URI uri = null;
    try {
      uri = url.toURI();
    } catch (URISyntaxException e) {
      // Ignore. Shouldn't happen!
    }
    inputSource.setSystemId(uri.toString());
    return inputSource;
  }
}
