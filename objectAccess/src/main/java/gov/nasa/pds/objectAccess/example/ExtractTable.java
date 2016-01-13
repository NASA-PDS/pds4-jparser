package gov.nasa.pds.objectAccess.example;

import gov.nasa.arc.pds.xml.generated.FileAreaObservational;
import gov.nasa.arc.pds.xml.generated.ProductObservational;
import gov.nasa.arc.pds.xml.generated.TableCharacter;
import gov.nasa.arc.pds.xml.generated.TableDelimited;
import gov.nasa.pds.label.object.FieldDescription;
import gov.nasa.pds.label.object.TableRecord;
import gov.nasa.pds.objectAccess.ExporterFactory;
import gov.nasa.pds.objectAccess.ObjectAccess;
import gov.nasa.pds.objectAccess.ObjectProvider;
import gov.nasa.pds.objectAccess.TableReader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Implements a table extraction application. Uses the Apache
 * Jakarta Commons CLI library to parse the command line.
 */
public class ExtractTable {

	private static final String HELP_OPTION = "help";

	private static final String EXTRACT_ALL = "all";
	
	private static final String LIST_TABLES_OPTION = "list-tables";

	private static final String FIELDS_OPTION = "fields";

	private static final String INDEX_OPTION = "index";
	
	private static final String DATA_FILE_OPTION = "data-file";

	private static final String OUTPUT_FILE_OPTION = "output-file";

	private static final String CSV_OPTION = "csv";

	private static final String FIXED_WIDTH_OPTION = "fixed-width";

	private static final String FIELD_SEPARATOR_OPTION = "field-separator";

	private static final String QUOTE_CHARACTER_OPTION = "quote-character";

	private static final String PLATFORM_OPTION = "platform";

	private static final String UNIX_OPTION = "unix";

	private static final String WINDOWS_OPTION = "windows";

	/** A system property name for setting the program name in the
	 * usage message.
	 */
	private static final String PROGRAM_NAME = "pds4.tools.progname";

	private Options options;

	private boolean listTables;
	private boolean extractAll;
	private File labelFile;
	private File outputFile;
	private File dataFile;
	private PrintWriter out;
	private OutputFormat format;
	private String fieldSeparator;
	private String lineSeparator;
	private String quoteCharacter;
	private Pattern quoteCharacterPattern;
	private int tableIndex;
	private String[] requestedFields;

	/**
	 * Runs the application with given command-line arguments.
	 *
	 * @param args the command-line arguments
	 */
	public static void main(String[] args) {
		(new ExtractTable()).run(args);
	}

	/**
	 * Creates a new instance of the extraction object. Sets up
	 * the command-line options.
	 */
	public ExtractTable() {
		options = new Options();

		options.addOption("h", HELP_OPTION, false, "show help text");

		options.addOption("l", LIST_TABLES_OPTION, false, "list tables present in the product (overrides all but output file options)");

		Option tableIndex = new Option("n", INDEX_OPTION, true, "table index, if more than one table is present (1..N) (default is 1)");
		tableIndex.setArgName("NUMBER");
		options.addOption(tableIndex);

		Option dataFile = new Option("d", DATA_FILE_OPTION, true, "data file name, if more than one data file is present (default is the first one listed)");
		dataFile.setArgName("FILE");
		options.addOption(dataFile);
		
		Option fields = new Option("f", FIELDS_OPTION, true, "comma-separated list of field names or numbers (default is all fields)");
		fields.setArgName("FIELD_LIST");
		fields.setValueSeparator(',');
		options.addOption(fields);

		Option outputFile = new Option("o", OUTPUT_FILE_OPTION, true, "output file name (default is stdout)");
		outputFile.setArgName("FILE");
		options.addOption(outputFile);

		Option fieldSep = new Option("t", FIELD_SEPARATOR_OPTION, true, "output field separator (default is 1 space for fixed-width, or comma for CSV)");
		fieldSep.setArgName("SEP");
		options.addOption(fieldSep);

		options.addOption("c", CSV_OPTION, false, "output in CSV format");
		options.addOption("w", FIXED_WIDTH_OPTION, false, "output in fixed-width format (default)");

		Option quoteChar = new Option("q", QUOTE_CHARACTER_OPTION, true, "quote character (for CSV output)");
		quoteChar.setArgName("CHAR");
		options.addOption(quoteChar);

		options.addOption("W", WINDOWS_OPTION, false, "output using Windows line separator (CRLF)");
		options.addOption("U", UNIX_OPTION, false, "output using Unix line separator (LF)");
		options.addOption("P", PLATFORM_OPTION, false, "output using current platform line separator (default)");
		options.addOption("a", EXTRACT_ALL, false, "extract all tables");
	}

	/**
	 * Runs the extractor.
	 *
	 * @param args the command-line arguments
	 */
	private void run(String[] args) {
		parseArguments(args);

		if (outputFile != null) {
			try {
				out = new PrintWriter(new FileWriter(outputFile));
			} catch (IOException e) {
				System.err.println("Cannot open output file: " + e.getMessage());
				System.exit(1);
			}
		} else {
			out = new PrintWriter(new OutputStreamWriter(System.out));
		}

		if (!labelFile.isFile() || !labelFile.canRead()) {
			System.err.println("Cannot read label file " + labelFile.getPath());
			System.exit(1);
		}

		ObjectProvider objectAccess = new ObjectAccess();
		ProductObservational product = null;
		try {
			product = objectAccess.getProduct(labelFile, ProductObservational.class);
		} catch (gov.nasa.pds.objectAccess.ParseException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}

		for (FileAreaObservational fileArea : product.getFileAreaObservationals()) {
  		String fileName = fileArea.getFile().getFileName();
  		File dataFile = new File(labelFile.getParent(), fileName);
  		if (listTables) {
  		  out.println("\nfile: " + dataFile.getName());
  		}
  		int currentIndex = 1;
  		for (Object obj : objectAccess.getTableObjects(fileArea)) {
  			TableType tableType = TableType.FIXED_BINARY;
  			if (obj instanceof TableCharacter) {
  				tableType = TableType.FIXED_TEXT;
  			} else if (obj instanceof TableDelimited) {
  				tableType = TableType.DELIMITED;
  			}
  
  			TableReader reader = null;
  			try {
  				reader = ExporterFactory.getTableReader(obj, dataFile);
  			} catch (Exception ex) {
  				System.err.println("Cannot create a table reader for the table: " + ex.getMessage());
  				ex.printStackTrace();
  				out.close();
  				return;
  			}
  			if (listTables) {
  				out.println("  table " + currentIndex + ": " + tableType.getReadableType());
  				listFields(reader.getFields());
  			} else if (extractAll || (currentIndex == tableIndex && 
  			    (this.dataFile == null || this.dataFile.getName().equalsIgnoreCase(fileName))) ) {
  				extractTable(reader);
  				break;
  			}  
  			++currentIndex;
  		}
  		if (!listTables && (this.dataFile == null && !extractAll)) {
  		  break;
  		}
		}
		out.close();
	}

	/**
	 * Lists the fields in a table.
	 *
	 * @param fields an array of field descriptions
	 */
	private void listFields(FieldDescription[] fields) {
		int i = 0;

		for (FieldDescription field : fields) {
			++i;
			out.println("    field " + i + ": " + field.getName() + " (" + field.getType().getXMLType() + ")");
		}
	}

	/**
	 * Extracts a table to the output file.
	 *
	 * @param reader the table reader to use for reading data
	 */
	private void extractTable(TableReader reader) {
		FieldDescription[] fields = reader.getFields();
		int[] displayFields = getSelectedFields(fields);

		int[] fieldLengths = getFieldLengths(fields, displayFields);

		displayHeaders(fields, displayFields, fieldLengths);
		displayRows(reader, fields, displayFields, fieldLengths);
	}

	/**
	 * Gets an array of field indices to display. Uses the
	 * field indices specified on the command line, if any,
	 * otherwise all fields will be displayed.
	 *
	 * @param totalFields the total number of fields in the table
	 * @return an array of fields to display
	 */
	private int[] getSelectedFields(FieldDescription[] fields) {
		int[] displayFields;

		if (requestedFields == null) {
			displayFields = new int[fields.length];
			for (int i=0; i < fields.length; ++i) {
				displayFields[i] = i;
			}
		} else {
			displayFields = new int[requestedFields.length];
			for (int i=0; i < requestedFields.length; ++i) {
				displayFields[i] = findField(requestedFields[i], fields);
			}
		}

		return displayFields;
	}

	/**
	 * Try to convert a field name or index into a field index.
	 * Prints an error message and exits if the field is not
	 * present in the table.
	 *
	 * @param nameOrIndex the string form of the name or index requested
	 * @param fields the field descriptions for the table fields
	 * @return the index of the requested field
	 */
	private int findField(String nameOrIndex, FieldDescription[] fields) {
		// First try to convert as an integer.
		try {
			return Integer.parseInt(nameOrIndex) - 1;
		} catch (NumberFormatException ex) {
			// ignore
		}

		// Now try to find a matching field name, ignoring case.
		for (int i=0; i < fields.length; ++i) {
			if (nameOrIndex.equalsIgnoreCase(fields[i].getName())) {
				return i;
			}
		}

		// If we get here, then we couldn't find a matching field.
		System.err.println("Requested field not present in table: " + nameOrIndex);
		System.exit(1);
		return -1; // Still have to return, because Java doesn't know that exit() doesn't return.
	}

	/**
	 * Gets an array of field lengths to use for output.
	 *
	 * @param fields an array of field descriptions
	 * @param displayFields an array of field indices to display
	 * @return
	 */
	private int[] getFieldLengths(FieldDescription[] fields, int[] displayFields) {
		int[] fieldLengths = new int[displayFields.length];

		for (int i=0; i < displayFields.length; ++i) {
			int fieldIndex = displayFields[i];

			if (format == OutputFormat.CSV) {
				fieldLengths[i] = 0;
			} else {
				fieldLengths[i] = Math.max(fields[fieldIndex].getName().length(), fields[fieldIndex].getLength());
			}
		}

		return fieldLengths;
	}

	/**
	 * Displays the headers of the table.
	 *
	 * @param fields an array of field descriptions
	 * @param displayFields an array of field indices to display
	 * @param fieldLengths an array of field lengths to use for output
	 */
	private void displayHeaders(FieldDescription[] fields, int[] displayFields, int[] fieldLengths) {
		for (int i=0; i < displayFields.length; ++i) {
			if (i > 0) {
				out.append(fieldSeparator);
			}

			FieldDescription field = fields[displayFields[i]];
			displayJustified(field.getName(), fieldLengths[i], field.getType().isRightJustified());
		}
		out.append(lineSeparator);
	}

	/**
	 * Displays the rows from the table.
	 *
	 * @param reader the table reader for reading rows
	 * @param fields an array of field descriptions
	 * @param displayFields an array of field indices to display
	 * @param fieldLengths an array of field lengths to use for output
	 * @throws IOException
	 */
	private void displayRows(TableReader reader, FieldDescription[] fields, int[] displayFields, int[] fieldLengths) {
		TableRecord record;
		try {
			while ((record = reader.readNext()) != null) {
				for (int i=0; i < displayFields.length; ++i) {
					if (i > 0) {
						out.append(fieldSeparator);
					}

					int index = displayFields[i];
					FieldDescription field = fields[index];
					displayJustified(record.getString(index+1).trim(), fieldLengths[i], field.getType().isRightJustified());
				}

				out.append(lineSeparator);
			}
		} catch (IOException e) {
			System.err.println("Cannot read the next table record: " + e.getMessage());
			System.exit(1);
		}
	}

	/**
	 * Displays a string, justified in a field.
	 *
	 * @param s the string to display
	 * @param length the field length
	 * @param isRightJustified true, if the value should be right-justified, else left-justified
	 */
	private void displayJustified(String s, int length, boolean isRightJustified) {
		if (format == OutputFormat.CSV) {
			// Double any quote characters.
			if (s.contains(quoteCharacter)) {
				Matcher matcher = quoteCharacterPattern.matcher(s);
				s = matcher.replaceAll(quoteCharacter + quoteCharacter);
			}

			// If the value is all whitespace or contains the field separator, quote the value.
			if (s.trim().isEmpty() || s.contains(fieldSeparator)) {
				s = quoteCharacter + s + quoteCharacter;
			}
		}

		int padding = length - s.length();

		if (isRightJustified) {
			displayPadding(padding);
		}
		out.append(s);
		if (!isRightJustified) {
			displayPadding(padding);
		}
	}

	/**
	 * Displays a number of padding spaces.
	 *
	 * @param n the number of spaces
	 */
	private void displayPadding(int n) {
		for (int i=0; i < n; ++i) {
			out.append(' ');
		}
	}

	/**
	 * Parses the command-line arguments.
	 *
	 * @param args the command-line arguments
	 */
	private void parseArguments(String[] args) {
		CommandLineParser parser = new GnuParser();
		CommandLine cmdLine = null;
		try {
			cmdLine = parser.parse(options, args);
		} catch (ParseException e) {
			showHelp("Error parsing command-line options: " + e.getMessage(), 1);
		}

		if (cmdLine.hasOption(HELP_OPTION)) {
			showHelp(null, 0);
		}

		listTables = (cmdLine.hasOption(LIST_TABLES_OPTION));
		extractAll = (cmdLine.hasOption(EXTRACT_ALL));

		if (cmdLine.hasOption(INDEX_OPTION)) {
			tableIndex = Integer.parseInt(cmdLine.getOptionValue(INDEX_OPTION));
		} else {
			tableIndex = 1;
		}

		if (cmdLine.hasOption(DATA_FILE_OPTION)) {
		  dataFile = new File(cmdLine.getOptionValue(DATA_FILE_OPTION));
		} else {
		  dataFile = null;
		}
		
		String[] files = cmdLine.getArgs();
		if (files.length == 0) {
			showHelp("A label file is required", 1);
		}
		labelFile = new File(files[0]);

		if (cmdLine.hasOption(CSV_OPTION)) {
			format = OutputFormat.CSV;
		} else {
			format = OutputFormat.FIXED_WIDTH;
		}

		if (cmdLine.hasOption(FIELD_SEPARATOR_OPTION)) {
			fieldSeparator = cmdLine.getOptionValue(FIELD_SEPARATOR_OPTION);
		} else if (format == OutputFormat.FIXED_WIDTH) {
			fieldSeparator = " ";
		} else {
			fieldSeparator = ",";
		}

		if (cmdLine.hasOption(QUOTE_CHARACTER_OPTION)) {
			quoteCharacter = cmdLine.getOptionValue(QUOTE_CHARACTER_OPTION);
		} else {
			quoteCharacter = "\"";
		}
		quoteCharacterPattern = Pattern.compile("\\Q" + quoteCharacter + "\\E");

		if (cmdLine.hasOption(WINDOWS_OPTION)) {
			lineSeparator = "\r\n";
		} else if (cmdLine.hasOption(UNIX_OPTION)) {
			lineSeparator = "\n";
		} else {
			lineSeparator = System.getProperty("line.separator");
		}

		if (!cmdLine.hasOption(FIELDS_OPTION)) {
			requestedFields = null;
		} else {
			requestedFields = cmdLine.getOptionValue(FIELDS_OPTION).split(" *, *");
		}

		if (cmdLine.hasOption(OUTPUT_FILE_OPTION)) {
			outputFile = new File(cmdLine.getOptionValue(OUTPUT_FILE_OPTION));
		} else {
			outputFile = null;
		}
	}

	/**
	 * Shows the help message and, optionally, an error message, and exits.
	 *
	 * @param errorMessage the error message, or null if there is no error message
	 * @param exitCode the exit code to use
	 */
	private void showHelp(String errorMessage, int exitCode) {
		if (errorMessage != null) {
			System.err.println(errorMessage);
			System.err.println();
		}

		String programName = getClass().getName();
		if (System.getProperty(PROGRAM_NAME) != null) {
			programName = System.getProperty(PROGRAM_NAME);
		}

		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(programName + " [-f field,...] [-o outputfile] [options] labelfile", options);
		System.exit(exitCode);
	}

	/**
	 * Defines an enumeration for the different table types
	 * that can be extracted. Holds a readable description of
	 * the table type.
	 */
	private static enum TableType {

		/** A fixed-width binary table. */
		FIXED_BINARY("fixed-width binary table"),

		/** A fixed-width text table. */
		FIXED_TEXT("fixed-width character table"),

		/** A delimited table. */
		DELIMITED("delimited table");

		private String readableType;

		private TableType(String readableType) {
			this.readableType = readableType;
		}

		/**
		 * Gets the readable name for the table type.
		 *
		 * @return the name of the table type
		 */
		public String getReadableType() {
			return readableType;
		}

	}

	/**
	 * Defines an enumeration for the different output formats.
	 */
	private static enum OutputFormat {
		CSV, FIXED_WIDTH;
	}

}
