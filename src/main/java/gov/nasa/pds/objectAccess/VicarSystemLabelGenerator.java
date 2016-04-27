// Copyright 2006-2016, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
// Any commercial use must be negotiated with the Office of Technology Transfer
// at the California Institute of Technology.
//
// This software is subject to U. S. export control laws and regulations
// (22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software
// is subject to U.S. export control laws and regulations, the recipient has
// the responsibility to obtain export licenses or other export authority as
// may be required before exporting such information to foreign countries or
// providing access to foreign nationals.
//
// $Id$
package gov.nasa.pds.objectAccess;

import java.io.IOException;
import java.io.OutputStream;

import jpl.mipl.io.vicar.AlreadyOpenException;
import jpl.mipl.io.vicar.SystemLabel;
import jpl.mipl.io.vicar.VicarInputImage;
import jpl.mipl.io.vicar.VicarLabel;
import jpl.mipl.io.vicar.VicarLabelItem;
import jpl.mipl.io.vicar.VicarLabelSet;
import jpl.mipl.io.vicar.VicarOutputFile;

public class VicarSystemLabelGenerator {
	
    private String _outfile;
    private SystemLabel label;
    private int _nl;
    private int _ns;
    private int _nb;
    private String _datatype;
    private int _datatype_code;
    private String _org;
    private String _method = "line";
    private double _linc;
    private double _sinc;
    private double _binc;
    private int _tileHeight;
    private int _tileWidth;
    private int _pixelStride ;
    private String _infile = null;
    
    public VicarSystemLabelGenerator() {
    	
    }
    
    public void generateFile(OutputStream outputStream) throws AlreadyOpenException, IOException {
    	
    	// Set up and create the output file

    	VicarOutputFile voif = new VicarOutputFile();

    	if (_infile != null) {			// set primary input
    	    voif.setPrimaryInput(new VicarInputImage(_infile));
    	}
    
    	SystemLabel sys = voif.getSystemLabel();
    	sys.setOrg(_org);
    	sys.setNL(_nl);
    	sys.setNS(_ns);
    	sys.setNB(_nb);
    	sys.setFormat(_datatype);
    	_datatype_code = sys.getFormatCode();

    	voif.setSystemLabel(sys);

    	// Update the label, stuff it with things like GEN does

    	VicarLabel lbl = voif.getVicarLabel();
    	VicarLabelSet task = lbl.createHistoryTask("TestGen");
    	task.add(new VicarLabelItem("IVAL", 0.0f));
    	task.add(new VicarLabelItem("SINC", _sinc));
    	task.add(new VicarLabelItem("LINC", _linc));
    	task.add(new VicarLabelItem("BINC", _binc));
    	task.add(new VicarLabelItem("MODULO", 0.0f));

    	voif.setVicarLabel(lbl);
    	voif.open(outputStream);
    }
    
	/**
	 * @return the _outfile
	 */
	public String get_outfile() {
		return _outfile;
	}

	/**
	 * @param _outfile the _outfile to set
	 */
	public void set_outfile(String _outfile) {
		this._outfile = _outfile;
	}

	/**
	 * @return the _nl
	 */
	public int get_nl() {
		return _nl;
	}

	/**
	 * @param _nl the _nl to set
	 */
	public void set_nl(int _nl) {
		this._nl = _nl;
	}

	/**
	 * @return the _ns
	 */
	public int get_ns() {
		return _ns;
	}

	/**
	 * @param _ns the _ns to set
	 */
	public void set_ns(int _ns) {
		this._ns = _ns;
	}

	/**
	 * @return the _nb
	 */
	public int get_nb() {
		return _nb;
	}

	/**
	 * @param _nb the _nb to set
	 */
	public void set_nb(int _nb) {
		this._nb = _nb;
	}

	/**
	 * @return the _datatype
	 */
	public String get_datatype() {
		return _datatype;
	}

	/**
	 * @param _datatype the _datatype to set
	 */
	public void set_datatype(String _datatype) {
		this._datatype = _datatype;
	}

	/**
	 * @return the _datatype_code
	 */
	public int get_datatype_code() {
		return _datatype_code;
	}

	/**
	 * @param _datatype_code the _datatype_code to set
	 */
	public void set_datatype_code(int _datatype_code) {
		this._datatype_code = _datatype_code;
	}

	/**
	 * @return the _org
	 */
	public String get_org() {
		return _org;
	}

	/**
	 * @param _org the _org to set
	 */
	public void set_org(String _org) {
		this._org = _org;
	}

	/**
	 * @return the _method
	 */
	public String get_method() {
		return _method;
	}

	/**
	 * @param _method the _method to set
	 */
	public void set_method(String _method) {
		this._method = _method;
	}

	/**
	 * @return the _linc
	 */
	public double get_linc() {
		return _linc;
	}

	/**
	 * @param _linc the _linc to set
	 */
	public void set_linc(double _linc) {
		this._linc = _linc;
	}

	/**
	 * @return the _sinc
	 */
	public double get_sinc() {
		return _sinc;
	}

	/**
	 * @param _sinc the _sinc to set
	 */
	public void set_sinc(double _sinc) {
		this._sinc = _sinc;
	}

	/**
	 * @return the _binc
	 */
	public double get_binc() {
		return _binc;
	}

	/**
	 * @param _binc the _binc to set
	 */
	public void set_binc(double _binc) {
		this._binc = _binc;
	}

	/**
	 * @return the _tileHeight
	 */
	public int get_tileHeight() {
		return _tileHeight;
	}

	/**
	 * @param _tileHeight the _tileHeight to set
	 */
	public void set_tileHeight(int _tileHeight) {
		this._tileHeight = _tileHeight;
	}

	/**
	 * @return the _tileWidth
	 */
	public int get_tileWidth() {
		return _tileWidth;
	}

	/**
	 * @param _tileWidth the _tileWidth to set
	 */
	public void set_tileWidth(int _tileWidth) {
		this._tileWidth = _tileWidth;
	}

	/**
	 * @return the _pixelStride
	 */
	public int get_pixelStride() {
		return _pixelStride;
	}

	/**
	 * @param _pixelStride the _pixelStride to set
	 */
	public void set_pixelStride(int _pixelStride) {
		this._pixelStride = _pixelStride;
	}

	/**
	 * @return the _infile
	 */
	public String get_infile() {
		return _infile;
	}

	/**
	 * @param _infile the _infile to set
	 */
	public void set_infile(String _infile) {
		this._infile = _infile;
	}

	public SystemLabel getLabel() {
		return label;
	}

	public void setLabel(SystemLabel label) {
		this.label = label;
	}


}
