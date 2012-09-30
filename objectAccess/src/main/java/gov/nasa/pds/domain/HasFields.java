package gov.nasa.pds.domain;

import java.util.List;

/**
 * An object implementing this interface has associated field information.
 * Examples are tables that have fields.
 */
public interface HasFields {
	
	public List<FieldInfo> getFields() ;
	public void setFields(List<FieldInfo> fields) ;
}
