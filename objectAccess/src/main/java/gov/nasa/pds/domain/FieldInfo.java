package gov.nasa.pds.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**  Contains information about a field. */
public class FieldInfo implements Serializable {

	public FieldInfo() {
		super();
	}
	
	private static final long serialVersionUID = 1L;
	String field_name;
	String field_data_type;
	int field_location;
	int field_length;
	String field_format;
	String field_unit;
	String field_description;
	List <String> values = new ArrayList<String>();

	public String getField_name() {
		return field_name;
	}
	public void setField_name(String field_name) {
		this.field_name = field_name;
	}
	public String getField_data_type() {
		return field_data_type;
	}
	public void setField_data_type(String field_data_type) {
		this.field_data_type = field_data_type;
	}
	public int getField_location() {
		return field_location;
	}
	public void setField_location(int field_location) {
		this.field_location = field_location;
	}
	public int getField_length() {
		return field_length;
	}
	public void setField_length(int field_length) {
		this.field_length = field_length;
	}
	public String getField_format() {
		return field_format;
	}
	public void setField_format(String field_format) {
		this.field_format = field_format;
	}
	public String getField_unit() {
		return field_unit;
	}
	public void setField_unit(String field_unit) {
		this.field_unit = field_unit;
	}
	public String getField_description() {
		return field_description;
	}
	public void setField_description(String field_description) {
		this.field_description = field_description;
	}
	public List<String> getValues() {
		return values;
	}

}
