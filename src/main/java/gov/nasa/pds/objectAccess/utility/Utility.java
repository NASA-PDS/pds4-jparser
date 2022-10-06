// Copyright 2019, California Institute of Technology ("Caltech").
// U.S. Government sponsorship acknowledged.
//
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// * Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
// * Redistributions must reproduce the above copyright notice, this list of
// conditions and the following disclaimer in the documentation and/or other
// materials provided with the distribution.
// * Neither the name of Caltech nor its operating division, the Jet Propulsion
// Laboratory, nor the names of its contributors may be used to endorse or
// promote products derived from this software without specific prior written
// permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

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
    javax.net.ssl.HttpsURLConnection
        .setDefaultHostnameVerifier(new javax.net.ssl.HostnameVerifier() {
          @Override
          public boolean verify(String hostname, javax.net.ssl.SSLSession sslSession) {
            if (hostname.equals("pds.nasa.gov") && hostname.equals(sslSession.getPeerHost())) {
              return true;
            }
            return false;
          }
        });
  }

  /**
   * Method that opens a connection. Supports redirects.
   *
   * @param conn URL Connection
   *
   * @return input stream.
   * @throws IOException If an error occurred while opening the stream.
   */
  public static InputStream openConnection(URLConnection conn) throws IOException {
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
        if (stat >= 300 && stat <= 307 && stat != 306
            && stat != HttpURLConnection.HTTP_NOT_MODIFIED) {
          URL base = http.getURL();
          String loc = http.getHeaderField("Location");
          URL target = null;
          if (loc != null) {
            target = new URL(base, loc);
          }
          http.disconnect();
          // Redirection should be allowed only for HTTP and HTTPS
          // and should be limited to 5 redirections at most.
          if (target == null
              || !(target.getProtocol().equals("http") || target.getProtocol().equals("https"))
              || redirects >= 5) {
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
    InputSource inputSource = new InputSource(Utility.openConnection(url.openConnection()));
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
