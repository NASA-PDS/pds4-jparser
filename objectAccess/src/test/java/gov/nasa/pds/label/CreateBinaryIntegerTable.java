package gov.nasa.pds.label;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * Generates binary table data files for testing. The output files should
 * be checked in to the revision control system.
 */
public class CreateBinaryIntegerTable {

	private static final FieldSpec[] FIELDS = {
		// 8 bit unsigned
		new FieldSpec(1, true, false),

		// 16, 32, 64 bit unsigned MSB
		new FieldSpec(2, true, false),
		new FieldSpec(4, true, false),
		new FieldSpec(8, true, false),

		// 16, 32, 64 bit unsigned LSB
		new FieldSpec(2, true, true),
		new FieldSpec(4, true, true),
		new FieldSpec(8, true, true),

		// 8 bit signed
		new FieldSpec(1, false, false),

		// 16, 32, 64 bit signed MSB
		new FieldSpec(2, false, false),
		new FieldSpec(4, false, false),
		new FieldSpec(8, false, false),

		// 16, 32, 64 bit signed LSB
		new FieldSpec(2, false, true),
		new FieldSpec(4, false, true),
		new FieldSpec(8, false, true),
	};

	public static void main(String[] args) throws IOException {
		FileOutputStream out = new FileOutputStream("src/test/resources/data_type_tests/BinaryIntegerTable.dat");

		// First record, all zero values.
		for (FieldSpec field : FIELDS) {
			byte[] b = makeZero(field.length);
			writeValue(out, b, field.length, field.isLSB);
		}

		// Second record, all 1 values.
		for (FieldSpec field : FIELDS) {
			byte[] b = makeOne(field.length);
			writeValue(out, b, field.length, field.isLSB);
		}

		// Third record, maximum value.
		for (FieldSpec field : FIELDS) {
			byte[] b = makeMaximum(field.length, field.isUnsigned);
			writeValue(out, b, field.length, field.isLSB);
		}

		// Fourth record, minimum value.
		for (FieldSpec field : FIELDS) {
			byte[] b = makeMinimum(field.length, field.isUnsigned);
			writeValue(out, b, field.length, field.isLSB);
		}

		out.close();
	}

	private static byte[] makeZero(int length) {
		byte[] b = new byte[length];
		Arrays.fill(b, (byte) 0);
		return b;
	}

	private static byte[] makeOne(int length) {
		byte[] b = new byte[length];
		Arrays.fill(b, (byte) 0);
		b[length-1] = 1;
		return b;
	}

	private static byte[] makeMaximum(int length, boolean isUnsigned) {
		byte[] b = new byte[length];
		Arrays.fill(b, (byte) 0xFF);
		if (!isUnsigned) {
			b[0] = 0x7F;
		}
		return b;
	}

	private static byte[] makeMinimum(int length, boolean isUnsigned) {
		byte[] b = new byte[length];
		Arrays.fill(b, (byte) 0);
		if (!isUnsigned) {
			b[0] = (byte) 0x80;
		}
		return b;
	}

	private static void writeValue(OutputStream out, byte[] b, int length, boolean isLSB) throws IOException {
		if (!isLSB) {
			out.write(b, 0, length);
		} else {
			for (int i=length-1; i >= 0; --i) {
				out.write(b[i]);
			}
		}
	}

	private static class FieldSpec {

		private boolean isLSB;
		private boolean isUnsigned;
		private int length;

		public FieldSpec(int length, boolean isUnsigned, boolean isLSB) {
			this.length = length;
			this.isUnsigned = isUnsigned;
			this.isLSB = isLSB;
		}

	}

}
