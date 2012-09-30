package gov.nasa.pds.domain;


/**  Contains information about an image product. */

public class Array2DImageProduct extends PDSObject implements HasURL {

	private static final long serialVersionUID = -1141475561657987844L;
	private String url;
	private int file_size; 
	private int records;
	private String creation_date_time;
	private int array_axis_2_elements;
	private int array_axis_2_name;
	private int array_axis_2_unit;
	private int array_axis_1_elements;
	private int array_axis_1_name;
	private int array_axis_1_unit;
	
	public Array2DImageProduct(String archiveRoot, String thisRelativeFileName) {
		super(archiveRoot, thisRelativeFileName);
	}
	
	public Array2DImageProduct() {
		super();
	}
	
	 /**
     * Determines if this is a PDS convertable image.
     * PTOOL-51 
     * Need Spec, try <Array_2D_Image base_class="Array_Base">
     * @param child
     * @return
     */
    public static boolean isConvertableImage(String child) {

            return child.endsWith("raw");

    }


	public int getFile_size() {
		return file_size;
	}

	public void setFile_size(int file_size) {
		this.file_size = file_size;
	}

	public int getRecords() {
		return records;
	}

	public void setRecords(int records) {
		this.records = records;
	}

	public String getCreation_date_time() {
		return creation_date_time;
	}

	public void setCreation_date_time(String creation_date_time) {
		this.creation_date_time = creation_date_time;
	}

	public int getArray_axis_2_elements() {
		return array_axis_2_elements;
	}

	public void setArray_axis_2_elements(int array_axis_2_elements) {
		this.array_axis_2_elements = array_axis_2_elements;
	}

	public int getArray_axis_2_name() {
		return array_axis_2_name;
	}

	public void setArray_axis_2_name(int array_axis_2_name) {
		this.array_axis_2_name = array_axis_2_name;
	}

	public int getArray_axis_2_unit() {
		return array_axis_2_unit;
	}

	public void setArray_axis_2_unit(int array_axis_2_unit) {
		this.array_axis_2_unit = array_axis_2_unit;
	}

	public int getArray_axis_1_elements() {
		return array_axis_1_elements;
	}

	public void setArray_axis_1_elements(int array_axis_1_elements) {
		this.array_axis_1_elements = array_axis_1_elements;
	}

	public int getArray_axis_1_name() {
		return array_axis_1_name;
	}

	public void setArray_axis_1_name(int array_axis_1_name) {
		this.array_axis_1_name = array_axis_1_name;
	}

	public int getArray_axis_1_unit() {
		return array_axis_1_unit;
	}

	public void setArray_axis_1_unit(int array_axis_1_unit) {
		this.array_axis_1_unit = array_axis_1_unit;
	}
	
	@Override
	public String getUrl() {
		return url;
	}

	@Override
	public void setUrl(String url) {
		this.url = url;
	}
}
