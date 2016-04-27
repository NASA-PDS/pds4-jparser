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

/**
 * Implements opt-level classes for accessing PDS labels and their data
 * objects.
 *
 * <p>Users of this library should normally use {@link gov.nasa.pds.label.Label#open(java.io.File)}
 * to read an parse a label. Then, methods in <code>Label</code> can be used to
 * get the data objects referred to by the label.</p>
 *
 * <p>Example:</p>
 * <pre>
 * Label label = Label.open(new File("sample.xml"));
 * List&lt;DataObject&gt; objects = label.getObjects();
 *
 * &#x2F;&#x2F; Get the first object, which should be a table.
 * TableObject table = (TableObject) objects.get(0);
 * int nFields = table.getFields().length;
 * ...
 * </pre>
 */
package gov.nasa.pds.label;
