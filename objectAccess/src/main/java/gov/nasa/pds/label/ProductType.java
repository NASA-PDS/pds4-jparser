package gov.nasa.pds.label;

import gov.nasa.arc.pds.xml.generated.Product;
import gov.nasa.arc.pds.xml.generated.ProductBrowse;
import gov.nasa.arc.pds.xml.generated.ProductBundle;
import gov.nasa.arc.pds.xml.generated.ProductCollection;
import gov.nasa.arc.pds.xml.generated.ProductDocument;
import gov.nasa.arc.pds.xml.generated.ProductObservational;
import gov.nasa.arc.pds.xml.generated.ProductThumbnail;

/**
 * Defines label product types, as enumeration constants. Not all
 * PDS product types are distinguished by the object-access library.
 * The value {@link #PRODUCT_OTHER} is used for all products not
 * specially handled by the library.
 */
public enum ProductType {

	/** A PDS 3 version generic product. */
	PRODUCT_PDS3(null),

	/** A PDS4 observational product. */
	PRODUCT_OBSERVATIONAL(ProductObservational.class),

	/** A PDS4 browse product. */
	PRODUCT_BROWSE(ProductBrowse.class),

	/** A PDS4 thumbnail product. */
	PRODUCT_THUMBNAIL(ProductThumbnail.class),

	/** A PDS4 document product. */
	PRODUCT_DOCUMENT(ProductDocument.class),

	/** A PDS4 bundle product. */
	PRODUCT_BUNDLE(ProductBundle.class),

	/** A PDS4 collection product. */
	PRODUCT_COLLECTION(ProductCollection.class),

	/** Another product type not specifically handled by the object access library. */
	PRODUCT_OTHER(null);

	private Class<? extends Product> clazz;

	private ProductType(Class<? extends Product> clazz) {
		this.clazz = clazz;
	}

	/**
	 * Gets the product type for a PDS4 product class.
	 *
	 * @param clazz the product class
	 * @return the product type enumeration constant for that product class
	 */
	public static ProductType typeForClass(Class<? extends Product> clazz) {
		for (ProductType type : ProductType.values()) {
			if (type.clazz == clazz) {
				return type;
			}
		}

		return PRODUCT_OTHER;
	}

}
