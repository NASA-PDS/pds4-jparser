package gov.nasa.pds.objectAccess;

/**
 * A utility class for converting byte arrays to various primitive data types. 
 * 
 * @author psarram
 */
public class Converter {
	
	private Converter() {		
	}
	
	/**
	 * Converts byte array of type unsingedMSB4 at the given offset to a long value.
	 * 
	 * @param bytes  an array from which the bytes will be read
	 * @param offset the offset within the array from which the bytes will be read
	 * @return a long value
	 */
	public static long getUnsignedMSB4AsLong(byte[] bytes, int offset) {
		// TODO: check buf size
		return ((bytes[offset] << 24) & 0xFF000000)    |
				((bytes[offset + 1] << 16) & 0xFF0000) |
				((bytes[offset + 2] << 8) & 0xFF00)    |
				(bytes[offset + 3] & 0xFF);						
	}
	
	/**
	 * Converts byte array of type unsingedMSB2 at the given offset to a long value.
	 * 
	 * @param bytes  an array from which the bytes will be read
	 * @param offset an offset within the array from which the bytes will be read
	 * @return a long value
	 */
	public static long getUnsignedMSB2AsLong(byte[] bytes, int offset) {
		return ((bytes[offset] << 8) & 0xFF00) |
			(bytes[offset + 1] & 0xFF);				
	}
	
	/**
	 * Converts byte array of type IEEE745MSBDouble at the given offset to a double value.
	 * 
	 * @param bytes  an array from which the bytes will be read
	 * @param offset an offset within the array from which the bytes will be read
	 * @return a double value
	 */
	public static double getIEEE745MSBDoubleAsDouble(byte[] bytes, int offset) {		
		return Double.longBitsToDouble(read64BitLong(bytes, offset));
	}
	
	/**
	 * Converts byte array of type IEEE745MSBSingle at the given offset to a float value.
	 *
	 * @param bytes  an array from which the bytes will be read
	 * @param offset an offset within the array from which the bytes will be read
	 * @return a float value
	 */
	public static float getIEEE745MSBSingle(byte[] bytes, int offset) {		
		return Float.intBitsToFloat(Long.valueOf(read32BitLong(bytes, offset)).intValue());
		
	}
	
	/**
	 * Converts byte array of type unsignedByte at the given offset to an integer value.
	 * 
	 * @param bytes  an array from which the bytes will be read
	 * @param offset an offset within the array from which the bytes will be read
	 * @return an integer value
	 */
	public static int getUnsignedByteAsInt(byte[] bytes, int offset) {
		return (int) (bytes[offset] & 0xFF);		
	}
	
	/**
	 * Converts byte array of type unsignedByte at the given offset to a short value.
	 * 
	 * @param bytes  an array from which the bytes will be read
	 * @param offset an offset within the array from which the bytes will be read
	 * @return a short value
	 */
	public static short getUnsignedByteAsShort(byte[] bytes, int offset) {
		return (short) (bytes[offset] & 0xFF);		
	}
	
	/**
	 * Converts byte array of type signedByte at the given offset to a byte value.
	 * 
	 * @param bytes  an array from which the bytes will be read
	 * @param offset an offset within the array from which the bytes will be read
	 * @return a byte value
	 */
	public static byte getSignedByte(byte[] bytes, int offset) {
		return (byte) bytes[offset];
	}
	
	private static long read32BitLong(byte[] bytes, int offset) {
		return (long) (								
				((long) (0xff & bytes[offset]) << 24) |
				((0xff & bytes[offset + 1]) << 16)    |
				((0xff & bytes[offset + 2]) << 8)     |
				(0xff & bytes[offset + 3]));
	}
	
	private static long read64BitLong(byte[] bytes, int offset){
		return (long) (
				((long) (0xff & bytes[offset]    ) << 56) |
				((long) (0xff & bytes[offset + 1]) << 48) |
				((long) (0xff & bytes[offset + 2]) << 40) |
				((long) (0xff & bytes[offset + 3]) << 32) |
				((long) (0xff & bytes[offset + 4]) << 24) |
				((0xff & bytes[offset + 5]) << 16) |
				((0xff & bytes[offset + 6]) << 8)  |
				(0xff & bytes[offset + 7]));
	}
}
