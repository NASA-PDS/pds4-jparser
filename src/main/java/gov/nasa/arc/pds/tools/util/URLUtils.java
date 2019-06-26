package gov.nasa.arc.pds.tools.util;

import gov.nasa.arc.pds.tools.container.URLContentsContainer;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides utilities for URL manipulation.
 * 
 * @author jagander
 */
public class URLUtils {

	public static final List<String> TEXT_CONTENT_TYPES = new ArrayList<String>();

	static {
		TEXT_CONTENT_TYPES.add("text/html"); //$NON-NLS-1$
		TEXT_CONTENT_TYPES.add("text/plain"); //$NON-NLS-1$
	}

	public static boolean exists(final URI uri) {
		try {
			return exists(uri.toURL());
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean exists(final URL url) {
		HttpURLConnection.setFollowRedirects(false);

		try {
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setRequestMethod("HEAD"); //$NON-NLS-1$
			boolean exists = (connection.getResponseCode() == HttpURLConnection.HTTP_OK);
			connection.disconnect();
			return exists;

		} catch (IOException e) {
			// noop
		} catch (final ClassCastException e) {
			// assume url took the form file://
			File file;
			try {
				file = new File(url.toURI());
			} catch (URISyntaxException f) {
				file = new File(url.getPath());
			}
			return file.exists();
		}

		return false;
	}

	public static URL getAlternateCaseURL(final URL url) {
		try {
			final String filename = getFileName(url);
			final String parentURL = getParentURLString(url);
			final String altFilename = StrUtils
					.getAlternateCaseString(filename);
			return new URL(parentURL + altFilename);
		} catch (Exception e) {
			e.printStackTrace();
			// noop, transformation on original should be malformed
		}
		return null;
	}

	public static URL getCaseUnknownURL(final URL url) {
		if (!exists(url)) {
			final URL testURL = getAlternateCaseURL(url);
			if (exists(testURL)) {
				return testURL;
			}
		}
		return url;
	}

	public static URL getCaseUnknownURL(final URI root, final String path) {
		URL url = null;
		try {
			url = root.toURL();
		} catch (MalformedURLException e) {
			return null;
		}
		return getCaseUnknownURL(url, path);
	}

	public static URL getCaseUnknownURL(final URL root, final String path) {
		URL url = null;
		try {
			url = newURL(root, path);
		} catch (MalformedURLException e) {
			return null;
		}
		if (!exists(url)) {
			final URL testURL = getAlternateCaseURL(url);
			if (exists(testURL)) {
				return testURL;
			}
		}
		return url;
	}

	public static String getFileName(final String url) {
		// strip get params which may contain a forward slash
		// ignore domain stuff
		URL URLObj = null;
		try {
			URLObj = new URL(url);
		} catch (MalformedURLException e) {
			// not a valid url
			return null;
		}
		return getFileName(URLObj);
	}

	public static String getFileName(final URI uri) {
		try {
			return getFileName(uri.toURL());
		} catch (MalformedURLException e) {
			return null;
		}
	}

	public static String getFileName(final URL url) {
		String filename = url.getFile();
		int paramIndex = filename.lastIndexOf("?"); //$NON-NLS-1$
		if (paramIndex != -1) {
			filename = filename.substring(0, paramIndex);
		}
		// strip non-file part
		filename = filename.substring(filename.lastIndexOf("/") + 1, filename //$NON-NLS-1$
				.length());

		return filename;
	}

	public static String getParentURLString(final URL url) {
		final String urlString = url.toString();
		final int slashIndex = urlString.lastIndexOf("/"); //$NON-NLS-1$
		if (slashIndex != -1 && slashIndex != urlString.length()) {
			final String result = urlString.substring(0, slashIndex + 1);
			if (result.length() > 8) {
				return result;
			}
		}
		return urlString;
	}

	public static URL getParentURL(final URL url) {
		try {
			return new URL(getParentURLString(url));
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static URL getParentURL(final URI uri) {
		try {
			return getParentURL(uri.toURL());
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static URLContentsContainer getContent(final URL url,
			final long maxLength) throws IOException {
		long curLength = 0;
		int chunkSize = 1024;
		if (maxLength != -1 && maxLength < chunkSize) {
			chunkSize = (int) maxLength;
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		URLConnection connection = url.openConnection();
		connection.setReadTimeout(10000); // 10 seconds
		connection.setConnectTimeout(10000); // 10 seconds
		InputStream is = new BufferedInputStream(connection.getInputStream());
		final String contentType = getContentType(connection, url, is);
		final String charset = getCharset(connection, "ISO-8859-1"); //$NON-NLS-1$
		// assume is a text type if null since we don't want to skip something
		// valid
		if (contentType == null || TEXT_CONTENT_TYPES.contains(contentType)) {
			byte[] buf = new byte[chunkSize];
			int amtRead = 0;
			while ((maxLength == -1 || curLength < maxLength)
					&& -1 != (amtRead = is.read(buf))) {
				curLength += amtRead;
				out.write(buf, 0, amtRead);
			}

			is.close();
			out.close();
			byte[] response = out.toByteArray();
			return new URLContentsContainer(response, charset);
		}
		is.close();
		System.out.println(contentType + " cannot be converted to text");

		return null;
	}

	public static URLContentsContainer getContent(final URL url)
			throws IOException {
		return getContent(url, -1);

	}

	public static String getContentType(final URL url) throws IOException {
		final URLConnection connection = url.openConnection();
		final InputStream is = connection.getInputStream();
		final String contentType = getContentType(connection, url, is);
		is.close();
		return contentType;
	}

	public static String getContentType(final URLConnection connection,
			final URL url, final InputStream is) {
		String contentTypeString = connection.getContentType();
		// not foud? guess from input stream
		if (contentTypeString == null) {
			try {
				contentTypeString = URLConnection
						.guessContentTypeFromStream(is);
			} catch (final Exception e) {
				// noop
			}
		}
		// not found, guess from filename
		if (contentTypeString == null) {
			final String fileName = getFileName(url);
			contentTypeString = URLConnection
					.guessContentTypeFromName(fileName);
		}
		// if not null, parse string to get type from full string
		if (contentTypeString != null) {
			final int sep = contentTypeString.indexOf(";"); //$NON-NLS-1$
			if (sep != -1) {
				return contentTypeString.substring(0, sep);
			}
			contentTypeString.toLowerCase();
		}
		return contentTypeString;
	}

	public static String getCharset(final URLConnection connection,
			final String defaultCharset) {
		final String contentTypeString = connection.getContentType();
		return getCharset(contentTypeString, defaultCharset);
	}

	public static String getCharset(final String contentType,
			final String defaultCharset) {
		if (contentType != null) {
			final int sep = contentType.indexOf("charset="); //$NON-NLS-1$
			if (sep != -1) {
				return contentType.substring(sep + 8);
			}
		}
		return defaultCharset;
	}

	public static int getContentLength(final URL url) {
		int size = -1;
		try {
			final URLConnection connection = url.openConnection();
			size = connection.getContentLength();

			connection.getInputStream().close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return size;
	}

	public static URL newURL(final URL base, final String filename)
			throws MalformedURLException {
		final String baseString = base.toString();
		if (baseString.endsWith("/")) { //$NON-NLS-1$
			return new URL(base + filename);
		}
		return new URL(base + "/" + filename); //$NON-NLS-1$
	}

}
