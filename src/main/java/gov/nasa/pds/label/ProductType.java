// Copyright 2019, California Institute of Technology ("Caltech").
// U.S. Government sponsorship acknowledged.
//
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// * Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
// * Redistributions must reproduce the above copyright notice, this list of
// conditions and the following disclaimer in the documentation and/or other
// materials provided with the distribution.
// * Neither the name of Caltech nor its operating division, the Jet Propulsion
// Laboratory, nor the names of its contributors may be used to endorse or
// promote products derived from this software without specific prior written
// permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package gov.nasa.pds.label;

import gov.nasa.arc.pds.xml.generated.Product;
import gov.nasa.arc.pds.xml.generated.ProductBrowse;
import gov.nasa.arc.pds.xml.generated.ProductBundle;
import gov.nasa.arc.pds.xml.generated.ProductCollection;
import gov.nasa.arc.pds.xml.generated.ProductDocument;
import gov.nasa.arc.pds.xml.generated.ProductObservational;
import gov.nasa.arc.pds.xml.generated.ProductThumbnail;

/**
 * Defines label product types, as enumeration constants. Not all PDS product types are
 * distinguished by the object-access library. The value {@link #PRODUCT_OTHER} is used for all
 * products not specially handled by the library.
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
