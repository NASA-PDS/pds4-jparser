package gov.nasa.arc.pds.tools.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class provides utilities for string formatting.
 *
 * @author jagander
 */
@SuppressWarnings("nls")
public class StrUtils {

  // default length to truncate to
  public static final int DEFAULT_TRUNCATE_LENGTH = 100;

  // javascript newline
  public static final String JS_NEWLINE = "\n";

  // xhtml newline
  public static final String HTML_NEWLINE = "<br />";

  // default stack trace lines in exception toString
  public static final int DEFAULT_STACK_LINES = 4;

  // regex to collapse whitespace
  public static final Pattern NORMALIZE_WHITE_SPACE_REGEX =
      Pattern.compile("\\s+", Pattern.MULTILINE);

  // regex to get base part of url, assumes valid url
  public static final Pattern URL_BASE_REGEX = Pattern.compile("^(https?://[^/]+)(([^\\?]*/).*)?");

  // all valid ascii characters including printable control characters
  public static final String ASCII_CHAR_RANGE = "\\r\\n\\t -~";

  public static final Pattern ASCII_CHARS_REGEX =
      Pattern.compile("^[" + ASCII_CHAR_RANGE + "]*$", Pattern.MULTILINE);

  public static final Pattern STRIP_ASCII_REGEX =
      Pattern.compile("[" + ASCII_CHAR_RANGE + "]+", Pattern.MULTILINE);

  // 1-3 digits followed by N collections of a separator and 3 digits followed
  // by an optional decimal separator and N digits
  // "10.000.000"
  public static final Pattern NUMERIC_REGEX =
      Pattern.compile("^[+-]?\\d{1,3}(((,\\d{3})*(\\.\\d+)?)|((\\.\\d{3})*(,\\d+)?))?$");

  public static final Pattern SIMPLE_NUMERIC_REGEX =
      Pattern.compile("^[+-]?((\\d+((,|\\.)\\d*)?)|((,|\\.)\\d+))$");

  // TODO: figure out how to support something like ...enum(';','a');
  public static final Pattern STATEMENT_REGEX =
      Pattern.compile("^[^;]+;\n", Pattern.MULTILINE | Pattern.DOTALL);

  public static final Pattern STRIP_PADDING = Pattern.compile(
      "[ \\t\\x0B]*([^ \\t\\x0B\\n\\r\\f]([^\\n\\r\\f]*[^ \\t\\x0B\\n\\r\\f])?)[ \\t\\x0B]*",
      Pattern.MULTILINE);

  /**
   * Special characters within a regular expression. Listed here so that variable strings that may
   * be used in a regular expression match can be escaped.
   * 
   * @see #escapeRegex(String)
   */
  public final static String[] REGEX_SPECIAL_CHARS =
      new String[] {"\\", "/", ".", "*", "+", "?", "|", "(", ")", "[", "]", "{", "}"};

  /**
   * Scrub a String for things that make it unsuitable to insert into a JavaScript string. Modify
   * the string as necessary to make it safe but preserve display.
   * <p>
   * Currently this only addresses non-escaped single quotes, double quotes and non-meaningful
   * backslashes. Other issues will be addressed as discovered.
   * 
   * @param string - the string to be cleaned.
   * @return The cleaned string.
   */
  // TODO: fix this to not escape things already escaped?
  public static String safeJS(final String string) {
    String returnString = string;
    // replace backslashes - make odd number even if not followed by char's
    // we want to remain escaped
    // returnString =
    // returnString.replaceAll("([^\\\\]?+(\\\\\\\\)*+\\\\[^\'\"\\\\]?+)",
    // "\\\\$1");
    // replace non escaped double quotes with escaped quotes
    // returnString =
    // returnString.replaceAll("((^\\\\)?+(\\\\\\\\)*+)(\"|\')",
    // "$1\\\\$4");
    returnString = returnString.replaceAll("(\"|\'|\\\\)", "\\\\$1");
    return returnString;
  }

  /**
   * Remove quotes surrounding a string. This is useful in cases that you are retrieving strings
   * from a file and each value is quoted.
   * 
   * @param string - the string to remove surrounding quotes from
   * @return original string minus surrounding quotes
   */
  public static String dequote(final String string) {
    return string.replaceFirst("^\"(.*)\"$", "$1");
  }

  /**
   * A generic string representation of a list. It just calls the toString method on each item and
   * inserts a xhtml break or newline between the items. What type of newline to use is dependent on
   * the flag for whether the output is for javascript.
   * 
   * @param list - a list of objects with a usable toString() on each object
   * @param isJS - flag for whether the output is for javascript or xhtml markup
   * @return a string representation of the list
   */
  public static String toString(final List<?> list, final Boolean isJS) {
    String lineBreak = null;
    String returnString = "";

    if (isJS == null || !isJS) {
      lineBreak = "<br />";
    } else {
      lineBreak = "\n";
    }
    Iterator<?> it = list.iterator();
    while (it.hasNext()) {
      Object element = it.next();
      returnString += element.toString();
      if (it.hasNext()) {
        returnString += lineBreak;
      }
    }
    return returnString;
  }

  /**
   * Get a reasonable string representation of an exception. "Reasonable" is either the exception
   * message or exception name plus some number of lines of the stacktrace.
   * 
   * @param e - exception to convert to string
   * @param isJS - flag to indicate if you want javascript newlines or html newlines
   * @param lines - number of stacktrace lines to add to representation
   * @return a string representation of an exception
   * 
   */
  public static String toString(final Exception e, Boolean isJS, Integer lines) {
    // default to HTML newlines if not set
    String newline = isJS == null || !isJS ? HTML_NEWLINE : JS_NEWLINE;
    final StackTraceElement[] stackTrace = e.getStackTrace();
    StackTraceElement[] causeTrace = null;
    Throwable causeE = e.getCause();
    if (causeE != null) {
      causeTrace = causeE.getStackTrace();
    }
    // default to full stack if set
    Integer numLines = lines == null ? stackTrace.length : lines;

    // get message
    String message = e.getMessage();

    // if there was no message, get the toString of the exception
    if (message == null) {
      message = e.toString();
    }

    // add a portion of the stack trace
    if (numLines > 0) {
      for (int i = 0; i < numLines; i++) {
        String stackString = stackTrace[i].toString();
        if (!stackString.equals("")) {
          message += newline + stackString;
        }
      }
    }

    if (causeTrace != null) {
      numLines = causeTrace.length;
      message += newline + "Caused by";
      // add a portion of the stack trace
      if (numLines > 0) {
        for (int i = 0; i < numLines; i++) {
          String stackString = causeTrace[i].toString();
          if (!stackString.equals("")) {
            message += newline + stackString;
          }
        }
      }
    }

    return message;
  }

  // use default number of lines in stack
  public static String toString(final Exception e, final Boolean isJS) {
    return toString(e, isJS, DEFAULT_STACK_LINES);
  }

  /**
   * The default usage of {@link #toString(List, Boolean)}, outputting in xhtml format.
   * 
   * @param list - a list of objects with a usable toString() on each object
   * @return a string representation of the list
   */
  public static String toString(final List<?> list) {
    return toString(list, null);
  }

  public static String toString(final Object[] list) {
    return toString(Arrays.asList(list), false);
  }

  public static String toString(final Object[] list, final Boolean isJS) {
    return toString(Arrays.asList(list), isJS);
  }

  public static String toString(final String[] list, final Boolean isJS) {
    return toString(Arrays.asList(list), isJS);
  }

  // NOTE: that this is not necessarily an accurate representation of file
  // since newlines may not match
  // NOTE: adds trailing newline to file even if there wasn't one
  // TODO: do we want to close stream hear or in outer
  public static String toString(final InputStream is) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    StringBuilder sb = new StringBuilder();

    String line = null;

    while ((line = reader.readLine()) != null) {
      sb.append(line + "\n");
    }

    is.close();

    return sb.toString();
  }

  // null protect toString()
  public static String toString(final Object object) {
    return object == null ? null : object.toString();
  }

  /**
   * Escape a string for use in a regular expression. This is useful when a block of text is to be
   * used in a regular expression match.
   * 
   * @param regexSource - source string to escape
   * @return an escaped version of the string for use in a regular expression
   * 
   * @see #REGEX_SPECIAL_CHARS
   */
  public static String escapeRegex(final String regexSource) {
    String returnString = regexSource;
    for (final String specialChar : REGEX_SPECIAL_CHARS) {
      returnString = returnString.replaceAll("\\" + specialChar, "\\\\\\" + specialChar);
    }
    return returnString;
  }

  /**
   * Truncate a given string to a default length.
   * 
   * @param string - string to truncate if necessary
   * @return truncated string if longer than specified length + an elipses else the original string
   * 
   * @see #DEFAULT_TRUNCATE_LENGTH
   * @see #truncate(String, Integer)
   */
  public static String truncate(final String string) {
    return truncate(string, null);
  }

  /**
   * Truncate a given string to a provided length.
   * <p>
   * Note that the returned string may be longer than the specified length due to the addition of an
   * elipses.
   * <p>
   * Note that truncate length is defaulted if null
   * 
   * @param string - string to truncate if necessary
   * @param length - length to truncate to
   * @return truncated string if longer than specified length + an elipses else the original string
   * 
   * @see #DEFAULT_TRUNCATE_LENGTH
   */
  public static String truncate(final String string, final Integer length) {
    Integer truncateLength = length == null ? DEFAULT_TRUNCATE_LENGTH : length;
    if (string == null || string.length() <= truncateLength) {
      return string;
    }
    return string.substring(0, truncateLength) + "...";
  }

  /**
   * Normalize a string by trimming and compacting whitespace.
   * 
   * @param string - string to normalize
   * @return a trimmed and whitespace-compressed version of the string
   * 
   * @see #NORMALIZE_WHITE_SPACE_REGEX
   */
  public static String normalize(final String string) {
    String returnString = string.trim();
    final Matcher matcher = NORMALIZE_WHITE_SPACE_REGEX.matcher(returnString);
    return matcher.replaceAll(" ");
  }

  /**
   * Get the base of a url such that you have a url string to the same folder. This is not
   * particularly useful to a restful interface.
   * <p>
   * Note that this requires the url string passed in to be a valid url. Typical usage is to get the
   * base from the current URL. In that case, the url will always be valid.
   * 
   * @param string - url to get base of.
   * @return The base of the passed in url string.
   * 
   * @see #URL_BASE_REGEX
   */
  public static String getURLBase(final String string) {
    final Matcher matcher = URL_BASE_REGEX.matcher(string);
    if (matcher.matches()) {
      String urlBase = matcher.group(1);
      final String pastDomain = matcher.group(3);
      if (pastDomain != null && pastDomain.length() > 0) {
        return urlBase + pastDomain;
      }
      return urlBase + "/";
    }
    return null;
  }

  public static String getURLFilename(final String url) {
    // strip get params which may contain a forward slash
    // ignore domain stuff
    URL URLObj = null;
    try {
      URLObj = new URL(url);
    } catch (MalformedURLException e) {
      // not a valid url
      return null;
    }
    return getURLFilename(URLObj);
  }

  public static String getURIFilename(final URI uri) {
    try {
      return getURLFilename(uri.toURL());
    } catch (MalformedURLException e) {
      return null;
    }
  }

  public static String getURLFilename(final URL url) {
    String filename = url.getFile();
    int paramIndex = filename.lastIndexOf("?");
    if (paramIndex != -1) {
      filename = filename.substring(0, paramIndex);
    }
    // strip non-file part
    filename = filename.substring(filename.lastIndexOf("/") + 1, filename.length());

    return filename;
  }

  /**
   * Test to see if string contains only ASCII characters.
   * <p>
   * Note that '^', '*' and '$' match the empty string in the regex and the single occurrence of the
   * empty string token can't do triple duty. We return true for an empty string explicitly rather
   * than using the regex.
   * 
   * @param string - string to test for non-ascii chars.
   * @return boolean indicating if the string is all ASCII chars
   * 
   * @see #ASCII_CHARS_REGEX
   */
  public static boolean isASCII(final String string) {
    if (string.length() == 0) {
      return true;
    }
    final Matcher matcher = ASCII_CHARS_REGEX.matcher(string);
    return matcher.matches();
  }

  /**
   * Get any and all characters that are not ASCII.
   * 
   * @param string - string to pull non-ASCII characters from
   * @return string containing all non-ASCII chars
   * 
   * @see #STRIP_ASCII_REGEX
   */
  public static String getNonASCII(final String string) {
    final Matcher matcher = STRIP_ASCII_REGEX.matcher(string);
    String returnString = matcher.replaceAll("");
    return returnString;
  }

  /**
   * A generic comma separated string representation of a list. It just calls the toString method on
   * each item and inserts a comma between the items.
   * 
   * @param list - a list of objects with a usable toString() on each object
   * 
   * @return a comma separated string representation of the list
   */
  // TODO: allow different separator char, quoting, or other formatting
  public static String toSeparatedString(final List<?> list) {
    String returnString = "";

    Iterator<?> it = list.iterator();
    while (it.hasNext()) {
      Object element = it.next();
      String val = null;
      val = element.toString();
      returnString += val;
      if (it.hasNext()) {
        returnString += ", ";
      }
    }
    return returnString;
  }

  public static String toSeparatedString(final Object[] array) {
    if (array == null) {
      return "";
    }
    List<Object> list = Arrays.asList(array);
    return toSeparatedString(list);
  }

  public static boolean isNumber(final String string) {
    if (string == null) {
      return false;
    }
    final Matcher simpleMatcher = SIMPLE_NUMERIC_REGEX.matcher(string);
    if (simpleMatcher.matches()) {
      return true;
    }
    final Matcher complexMatcher = NUMERIC_REGEX.matcher(string);
    return complexMatcher.matches();
  }

  public static Number getNumber(final String string, final Locale locale) {
    try {
      if (!isNumber(string)) {
        return null;
      }
      return NumberFormat.getInstance(locale).parse(string.replaceAll("\\+", ""));
    } catch (ParseException e) {
      // shouldn't be able to get here unless isNumber has a bug
      return null;
    }
  }

  public static Number getNumber(final String string) {
    final Locale locale = new Locale("en_us");
    return getNumber(string, locale);
  }

  public static Number getNumberLoose(final String string) {
    final String numberVal = string.replaceAll("[^0-9]", "");
    try {
      return NumberFormat.getInstance(new Locale("en_us")).parse(numberVal);
    } catch (ParseException e) {
      return null;
    }
  }

  public static List<String> toStatements(final String sql) {
    final List<String> statements = new ArrayList<>();
    final Matcher matcher = STATEMENT_REGEX.matcher(sql);
    while (matcher.find()) {
      final String currentMatch = sql.substring(matcher.start(), matcher.end());
      statements.add(currentMatch);
    }

    return Collections.unmodifiableList(statements);
  }

  public static boolean nullOrEmpty(final String string) {
    if (string == null || string.trim().equals("")) {
      return true;
    }
    return false;
  }

  // multiline strip trailing, useful when evaluating a multiline textual
  // value with padding
  public static String stripPadding(final String string) {
    if (string == null) {
      return null;
    }

    // TODO: consolidate replacements and see about making performant
    // strip lines that are just whitespace
    String temp = string.replaceAll("^[ \\t\\x0B]*(\\r\\n|\r|\n)", "");
    // any whitespace followed by not whitespace or newline followed by any
    // amount of not newline followed by non newline or whitespace
    // (optional) followed by any number of whitespace
    temp = temp.replaceAll(
        "[ \\t\\x0B]*([^ \\t\\x0B\\n\\r\\f]([^\\n\\r\\f]*[^ \\t\\x0B\\n\\r\\f])?)[ \\t\\x0B]*",
        "$1");
    // treat line endings as spaces so return plus line feed don't count as
    // 2 chars
    return temp.replaceAll("(\\r\\n|\\n\\r|\r|\n)", " ");
  }

  public static String toUpper(final String string) {
    String out = "";
    final String source = string.toLowerCase();
    String[] parts = source.split(" ");
    for (int i = 0; i < parts.length; i++) {
      final String part = parts[i];
      out += part.substring(0, 1).toUpperCase() + part.substring(1);
      if (i < parts.length - 1) {
        out += " ";
      }
    }
    return out;
  }

  // get a presentation version of the string, default to: trimming, replacing
  // underscores with spaces, uppercasing
  // TODO: add more actions and flags
  public static String getNice(final String string, final Boolean capitalize,
      final Boolean replaceUnderscore) {
    String out = string.trim();
    if (replaceUnderscore == null || replaceUnderscore) {
      out = out.replaceAll("_", " ");
    }
    if (capitalize == null || capitalize) {
      out = toUpper(out);
    }
    return out;
  }

  public static String getNice(final String string) {
    return getNice(string, true, true);
  }

  public static String addWildcards(final String string) {
    return string.replaceFirst("^\\**+(.*?)\\**+$", "*$1*");
  }

  public static String getAlternateCaseString(final String string) {
    Character character = string.charAt(0);
    if (Character.isLowerCase(character)) {
      return string.toUpperCase();
    }
    return string.toLowerCase();
  }

  // ex. get source string where might have either url, uri, file, or
  // string...
  public static String getNonNull(final Object... objects) {
    for (Object object : objects) {
      if (object != null) {
        return object.toString();
      }
    }
    return null;
  }
}
