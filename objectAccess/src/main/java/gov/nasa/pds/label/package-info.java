/**
 * Implements opt-level classes for accessing PDS labels and their data
 * objects.
 *
 * <p>Users of this library should normally use {@link Label#open(java.io.File)}
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
