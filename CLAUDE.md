# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

PDS4 JParser is a Java library providing APIs for parsing and exporting information on PDS4 (Planetary Data System version 4) table and image objects to various formats including CSV, PNG, VICAR, FITS, etc. This library is used by NASA's Planetary Data System to work with planetary science data products.

**Requirements:** Java 17 or higher, Maven 3

## Build Commands

### Standard Build
```bash
# Compile only
mvn compile

# Create JAR file
mvn compile jar:jar

# Full build with site documentation
mvn site
mvn package

# Clean build
mvn clean package
```

### Testing
```bash
# Run all tests
mvn test

# Run a specific test class
mvn test -Dtest=LabelTest

# Run a specific test method
mvn test -Dtest=LabelTest#testSpecificMethod
```

### Deployment
```bash
# Deploy SNAPSHOT to Sonatype Maven repo
mvn clean site deploy

# Deploy release version
mvn clean site deploy -P release
```

### Local Documentation
```bash
# View documentation locally
mvn site:run
# Then open http://localhost:8080
```

## Code Generation

The project uses JAXB to generate Java classes from PDS4 XML schemas. This happens automatically during the build via the maven-antrun-plugin.

- Generated sources are placed in `target/generated-sources/main/java`
- The build script is in `src/build/resources/build.xml`
- Schema files are in `src/build/resources/schema/`
- Generation is controlled by the `model-version` property in pom.xml (currently 1Q00)

If you need to regenerate sources:
```bash
mvn clean generate-sources
```

## Upgrading the PDS4 Information Model (IM) Version

### Why this matters

pds4-jparser generates its JAXB classes from a pinned PDS4 IM schema version. When a new IM version
introduces new root product types (e.g. `Product_Resource` in 1Q00), no corresponding Java class
exists in the generated sources. Any downstream tool (e.g. validate) that processes a label with that
root element will get a JAXB `UnmarshalException`, and product-level processing is silently skipped.
Keeping pds4-jparser in sync with the IM ensures all PDS4 product types can be parsed.

### When to upgrade

Upgrade when:
- A new PDS4 IM release introduces product types or schema structures not covered by the current version
- A downstream tool reports `UnmarshalException: unexpected element ... local:"Product_Xyz"`
- The PDS release cycle requires support for a new IM version

### Step-by-step upgrade procedure

**Step 1 — Add the new schema files**

Download the new XSD files from the PDS4 IM release (available at
https://pds.nasa.gov/datastandards/schema/released/) and place them in a new versioned directory:

```
src/build/resources/schema/<NEW_VERSION>/
  PDS4_PDS_<NEW_VERSION>.xsd
  PDS4_DISP_<NEW_VERSION>.xsd
```

**Step 2 — Update `model-version` in `pom.xml`**

```xml
<!-- src/pom.xml, <properties> section -->
<model-version>NEW_VERSION</model-version>   <!-- e.g. 1R00 -->
```

**Step 3 — Regenerate JAXB classes**

```bash
mvn clean generate-sources
```

Inspect `target/generated-sources/main/java/gov/nasa/arc/pds/xml/generated/` for new
`Product*.java` classes introduced by the new IM version.

**Step 4 — Wire new product types into `Label.java`**

For each new `Product_Xyz` class, add a dispatch branch in `getDataObjects(Product product)`:

```java
} else if (product instanceof ProductXyz) {
    return getDataObjects((ProductXyz) product);
}
```

Then add a private handler method. If the product type has `File_Area_*` children, iterate over
them (see existing handlers for `ProductAncillary`, `ProductObservational`, etc. as templates).
If it has no file areas (like `ProductResource`), return `Collections.emptyList()` and document why.

**Step 5 — Update `ProductType.java`**

Add a constant for each new product type that should be distinguished:

```java
/** A PDS4 Xyz product (introduced in IM X000). */
PRODUCT_XYZ(ProductXyz.class),
```

Import the generated class at the top of the file.

**Step 6 — Verify**

```bash
mvn clean package
```

Run the test suite and, if available, validate a real label whose root element is the new product type:

```java
Label label = Label.open(new File("Product_Xyz_label.xml"));
// Should not throw; product type should resolve correctly
System.out.println(label.getProductType());  // PRODUCT_XYZ
System.out.println(label.getObjects().size()); // 0 or more, depending on file areas
```

## Architecture

### Core Package Structure

**`gov.nasa.pds.label`** - Core label parsing and representation
- `Label` - Main class for reading and parsing PDS4 XML labels
- `DisplayDirection`, `ProductType`, `LabelStandard` - Enums and constants
- `gov.nasa.pds.label.object` - Object model for data objects (tables, arrays, images)
  - `DataObject` - Base interface for data objects
  - `TableObject`, `ArrayObject`, `GenericObject` - Specific data object types
  - `TableRecord`, `RecordLocation`, `FieldDescription` - Table structure
- `gov.nasa.pds.label.jaxb` - JAXB-related utilities for XML parsing
  - `XMLLabelContext` - JAXB context for PDS4 labels
  - `PDSXMLEventReader` - Custom XML event reader

**`gov.nasa.pds.objectAccess`** - High-level API for accessing and exporting data
- `ObjectAccess` - Main entry point implementing `ObjectProvider`
- `ObjectProvider` - Interface for accessing data objects from labels
- Readers: `TableReader`, `RawTableReader`
- Writers: `TableWriter`
- Exporters: `ExporterFactory`, `TableExporter`, `ImageExporter`, `TwoDImageExporter`, `ThreeDImageExporter`, `ThreeDSpectrumExporter`
- Records: `FixedTableRecord`, `DelimitedTableRecord` - Implementations of `TableRecord`
- `ByteWiseFileAccessor` - Low-level file access for binary data
- `gov.nasa.pds.objectAccess.table` - Field adapters for reading table data
- `gov.nasa.pds.objectAccess.array` - Adapters for array/image data

**`gov.nasa.arc.pds.xml.generated`** - JAXB-generated classes from PDS4 schemas (in target/generated-sources)

### Key Design Patterns

1. **Provider Pattern**: `ObjectAccess` implements `ObjectProvider` to abstract access to data objects
2. **Factory Pattern**: `ExporterFactory` creates appropriate exporters based on data type
3. **Adapter Pattern**: Field and element adapters convert between PDS4 data types and Java types
4. **Record Pattern**: `TableRecord` interface with implementations for fixed-width and delimited tables

### Data Flow

1. Parse PDS4 XML label → `Label` object (uses JAXB-generated classes)
2. Create `ObjectAccess` from label → provides access to data objects
3. Get specific data objects (tables/arrays) → via `ObjectProvider` interface
4. Export to format → via appropriate exporter (CSV, PNG, FITS, VICAR, etc.)

### Local Repository

The project uses a local Maven repository at `repo/` for problematic JARs that aren't available in public repositories. This is configured in pom.xml and addresses issue software-issues-repo#141.

## Namespace Handling

PDS4 uses multiple XML namespaces defined in `src/main/resources/namespaces.properties`:
- `pds` - Core PDS4 namespace
- `disp` - Display dictionary
- `img`, `geom`, `cart`, etc. - Discipline-specific dictionaries
- Mission-specific namespaces (insight, mvn)

## Testing Notes

- Tests use TestNG framework
- Test data files are in `src/test/resources/`
- Example usage tests in `gov.nasa.pds.objectAccess.example`
- Unit tests cover adapters, readers, writers, and exporters

## Common Issues

- If build fails with "generated sources not found", run `mvn clean generate-sources`
- Schema version must match the PDS4 Information Model version in use
- Binary table reading requires proper byte order handling (LSB vs MSB)
- Field offset calculations are 1-based in PDS4 labels but 0-based in Java
