package gov.nasa.pds.objectAccess.array;

import java.util.HashMap;
import java.util.Map;

/**
 * Implements an object that represents the type of an array element.
 */
public class ElementType {
	
	private static final Map<String, ElementType> TYPES = new HashMap<String, ElementType>();
	static {
		TYPES.put("IEEE754LSBDouble", new ElementType(Double.SIZE / Byte.SIZE, new DoubleAdapter(false)));
		TYPES.put("IEEE754MSBDouble", new ElementType(Double.SIZE / Byte.SIZE, new DoubleAdapter(true)));
		
		TYPES.put("IEEE754LSBSingle", new ElementType(Float.SIZE / Byte.SIZE, new FloatAdapter(false)));
		TYPES.put("IEEE754MSBSingle", new ElementType(Float.SIZE / Byte.SIZE, new FloatAdapter(true)));
		
		TYPES.put("SignedByte", new ElementType(1, new IntegerAdapter(1, false, false)));
		
		TYPES.put("SignedLSB2", new ElementType(Short.SIZE / Byte.SIZE, new IntegerAdapter(Short.SIZE / Byte.SIZE, false, false)));
		TYPES.put("SignedLSB4", new ElementType(Integer.SIZE / Byte.SIZE, new IntegerAdapter(Integer.SIZE / Byte.SIZE, false, false)));
		TYPES.put("SignedLSB8", new ElementType(Long.SIZE / Byte.SIZE, new IntegerAdapter(Long.SIZE / Byte.SIZE, false, false)));
		
		TYPES.put("SignedMSB2", new ElementType(Short.SIZE / Byte.SIZE, new IntegerAdapter(Short.SIZE / Byte.SIZE, true, false)));
		TYPES.put("SignedMSB4", new ElementType(Integer.SIZE / Byte.SIZE, new IntegerAdapter(Integer.SIZE / Byte.SIZE, true, false)));
		TYPES.put("SignedMSB8", new ElementType(Long.SIZE / Byte.SIZE, new IntegerAdapter(Long.SIZE / Byte.SIZE, true, false)));
		
		TYPES.put("UnsignedByte", new ElementType(1, new IntegerAdapter(1, false, true)));
		
		TYPES.put("UnsignedLSB2", new ElementType(Short.SIZE / Byte.SIZE, new IntegerAdapter(Short.SIZE / Byte.SIZE, false, true)));
		TYPES.put("UnsignedLSB4", new ElementType(Integer.SIZE / Byte.SIZE, new IntegerAdapter(Integer.SIZE / Byte.SIZE, false, true)));
		TYPES.put("UnsignedLSB8", new ElementType(Long.SIZE / Byte.SIZE, new IntegerAdapter(Long.SIZE / Byte.SIZE, false, true)));
		
		TYPES.put("UnsignedMSB2", new ElementType(Short.SIZE / Byte.SIZE, new IntegerAdapter(Short.SIZE / Byte.SIZE, true, true)));
		TYPES.put("UnsignedMSB4", new ElementType(Integer.SIZE / Byte.SIZE, new IntegerAdapter(Integer.SIZE / Byte.SIZE, true, true)));
		TYPES.put("UnsignedMSB8", new ElementType(Long.SIZE / Byte.SIZE, new IntegerAdapter(Long.SIZE / Byte.SIZE, true, true)));
	}

	private int size;
	private DataTypeAdapter adapter;
	
	/**
	 * Gets the element type for a given type name.
	 * 
	 * @param typeName the type name
	 * @return the element type for that type name
	 */
	public static ElementType getTypeForName(String typeName) {
		return TYPES.get(typeName);
	}
	
	private ElementType(int size, DataTypeAdapter adapter) {
		this.size = size;
		this.adapter = adapter;
	}
	
	/**
	 * Gets the element size.
	 * 
	 * @return the number of bytes for the element
	 */
	public int getSize() {
		return size;
	}
	
	/**
	 * Gets the data type adapter for the element type.
	 * 
	 * @return the data type adapter
	 */
	public DataTypeAdapter getAdapter() {
		return adapter;
	}
	
}
