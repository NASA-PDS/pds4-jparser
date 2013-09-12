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

	/** A system property name for setting the program name in the
	 * usage message.
	 */
	private static final String PROGRAM_NAME = "pds4.tools.progname";

	private Options options;

	private boolean listTables;
	private File labelFile;
	private File outputFile;
	private PrintWriter out;
	private OutputFormat format;
	private String fieldSeparator;
	private String lineSeparator;
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

		options.addOption("h", "help", false, "show help text");

		options.addOption("l", "list-tables", false, "list tables present in the product (overrides all but output file options)");

		Option tableIndex = new Option("n", "index", true, "table index, if more than one table is present (1..N) (default is 1)");
		tableIndex.setArgName("NUMBER");
		options.addOption(tableIndex);

		Option fields = new Option("f", "fields", true, "comma-separated list of field names or numbers (default is all fields)");
		fields.setArgName("FIELD_LIST");
		fields.setValueSeparator(',');
		options.addOption(fields);

		Option outputFile = new Option("o", "output-file", true, "output file name (default is stdout)");
		outputFile.setArgName("FILE");
		options.addOption(outputFile);

		Option fieldSep = new Option("t", "field-separator", true, "output field separator (default is 1 space for fixed-width, or comma for CSV)");
		fieldSep.setArgName("SEP");
		options.addOption(fieldSep);

		options.addOption("c", "csv", false, "output in CSV format");
		options.addOption("w", "fixed-width", false, "output in fixed-width format (default)");

		Option quoteChar = new Option("q", "quote-character", true, "quote character (for CSV output)");
		quoteChar.setArgName("CHAR");
		options.addOption(quoteChar);

		options.addOption("W", "windows", false, "output using Windows line separator (CRLF)");
		options.addOption("U", "unix", false, "output using Unix line separator (LF)");
		options.addOption("P", "platform", false, "output using current platform line separator (default)");
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

		FileAreaObservational fileArea = product.getFileAreaObservationals().get(0);
		String fileName = fileArea.getFile().getFileName();
		File dataFile = new File(labelFile.getParent(), fileName);

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
			}

			if (listTables) {
				out.println("table " + currentIndex + ": " + tableType.getReadableType());
				listFields(reader.getFields());
			} else if (currentIndex == tableIndex) {
				extractTable(reader);
				break;
			}

			++currentIndex;
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
					System.out.println("DEBUG: " + record.getString(index+1).trim());
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

		if (cmdLine.hasOption("help")) {
			showHelp(null, 0);
		}

		listTables = (cmdLine.hasOption("list-tables"));

		if (cmdLine.hasOption("index")) {
			tableIndex = Integer.parseInt(cmdLine.getOptionValue("index"));
		} else {
			tableIndex = 1;
		}

		String[] files = cmdLine.getArgs();
		if (files.length == 0) {
			showHelp("A label file is required", 1);
		}
		labelFile = new File(files[0]);

		if (cmdLine.hasOption("csv")) {
			format = OutputFormat.CSV;
		} else {
			format = OutputFormat.FIXED_WIDTH;
		}

		if (cmdLine.hasOption("field-separator")) {
			fieldSeparator = cmdLine.getOptionValue("field-separator");
		} else if (format == OutputFormat.FIXED_WIDTH) {
			fieldSeparator = " ";
		} else {
			fieldSeparator = ",";
		}

		if (cmdLine.hasOption("windows")) {
			lineSeparator = "\r\n";
		} else if (cmdLine.hasOption("unix")) {
			lineSeparator = "\n";
		} else {
			lineSeparator = System.getProperty("line.separator");
		}

		if (!cmdLine.hasOption("fields")) {
			requestedFields = null;
		} else {
			requestedFields = cmdLine.getOptionValue("fields").split(" *, *");
		}

		if (cmdLine.hasOption("output-file")) {
			outputFile = new File(cmdLine.getOptionValue("output-file"));
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
