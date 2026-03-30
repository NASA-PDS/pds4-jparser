# PDS4 JParser

[![DOI](https://zenodo.org/badge/DOI/10.5281/zenodo.5725957.svg)](https://doi.org/10.5281/zenodo.5725957) [![🤪 Unstable integration & delivery](https://github.com/NASA-PDS/pds4-jparser/actions/workflows/unstable-cicd.yaml/badge.svg)](https://github.com/NASA-PDS/pds4-jparser/actions/workflows/unstable-cicd.yaml) [![😌 Stable integration & delivery](https://github.com/NASA-PDS/pds4-jparser/actions/workflows/stable-cicd.yaml/badge.svg)](https://github.com/NASA-PDS/pds4-jparser/actions/workflows/stable-cicd.yaml)

Java library providing APIs for parsing and exporting information
on PDS4 table and image objects to various formats including CSV, PNG, Vicar, 
Fits, etc. The software is packaged in a JAR file.

Please visit the website https://NASA-PDS.github.io/pds4-jparser/ for information on installation, use, operation, and also *development* of this software.

**Looking for a Python library? See the [Planetary Data Reader (pdr)](https://github.com/MillionConcepts/pdr)**


# System Requirements
Current software requires:
* Java 17 or higher
* Maven 3

# Documentation
The documentation including release notes, installation and operation of the 
software should be online at https://NASA-PDS.github.io/pds4-jparser/. If it is not 
accessible, you can execute the "mvn site:run" command and view the 
documentation locally at http://localhost:8080.

# Build

The software can be compiled and built with the "mvn compile" command but in order 
to create the JAR file, you must execute the "mvn compile jar:jar" command. 

In order to create a complete distribution package, execute the 
following commands: 

```
mvn site
mvn package
```

# Upgrading the PDS4 Information Model (IM) Version

pds4-jparser generates its JAXB Java classes from a pinned PDS4 IM schema version. When a new IM release introduces new root product types, no corresponding Java class exists in the generated sources. Any downstream tool that processes a label with that root element will receive a JAXB `UnmarshalException`, causing product-level processing to be silently skipped. Keeping this library in sync with the IM ensures all current PDS4 product types can be parsed correctly.

**When to upgrade:** a new IM release introduces product types not covered by the current version, a downstream tool reports `UnmarshalException: unexpected element ... local:"Product_Xyz"`, or the PDS release cycle requires support for a new IM version.

## Step-by-step upgrade procedure

**Step 1 — Add the new schema files**

Download the new XSD files from the [PDS4 IM release page](https://pds.nasa.gov/datastandards/schema/released/) and place them in a new versioned directory:

```
src/build/resources/schema/<NEW_VERSION>/
  PDS4_PDS_<NEW_VERSION>.xsd
  PDS4_DISP_<NEW_VERSION>.xsd
```

**Step 2 — Update `model-version` in `pom.xml`**

In the `<properties>` section, update the pinned version:

```xml
<model-version>NEW_VERSION</model-version>
```

**Step 3 — Regenerate JAXB classes**

```bash
mvn clean generate-sources
```

Inspect `target/generated-sources/main/java/gov/nasa/arc/pds/xml/generated/` for new `Product*.java` classes introduced by the new IM version.

**Step 4 — Wire new product types into `Label.java`**

For each new `Product_Xyz` class, add a dispatch branch in `getDataObjects(Product)`:

```java
} else if (product instanceof ProductXyz) {
    return getDataObjects((ProductXyz) product);
}
```

Then add a private handler method. If the product type has `File_Area_*` children, iterate over them (see existing handlers for `ProductAncillary` or `ProductObservational` as templates). If it has no file areas, return `Collections.emptyList()` and document why (see `getDataObjects(ProductResource)` as an example).

**Step 5 — Update `ProductType.java`**

Add an enum constant for each new product type and import the generated class:

```java
/** A PDS4 Xyz product (introduced in IM X000). */
PRODUCT_XYZ(ProductXyz.class),
```

**Step 6 — Fix type-change compilation errors (if any)**

Occasionally a schema upgrade changes the Java type of a generated field (e.g. `int` → `BigInteger`). Compile to surface these:

```bash
mvn compile
```

Update affected source files to use the new type (e.g. replace `field == 3` with `field.intValueExact() == 3` for `BigInteger` fields).

**Step 7 — Verify**

```bash
mvn clean package
```

Run the full test suite. If test labels for the new product type are available, validate them manually:

```java
Label label = Label.open(new File("Product_Xyz_label.xml"));
System.out.println(label.getProductType());    // should print PRODUCT_XYZ
System.out.println(label.getObjects().size()); // 0 or more, depending on file areas
```

# Operational Release

## Run pre-build software

When new PDS4 Information Model is available, run the following script to download the latest IM and 
push to git.

```
$ build/pre-build.sh

pre-build.sh <dev_or_ops> <IM version>
     dev_or_ops - still in dev or released
     IM Version - e.g. 1D00

# For dev release of IM
build/pre-build.sh dev 1E00

For operation release of IM
build/pre-build.sh ops 1E00
```


## Release with ConMan

For internal JPL use, the ConMan software package can be used for releasing software, otherwise the following sections outline how to complete these steps manually.

## Manual Release

Note that GitHub Actions are enabled for the repository of this software so that automated SNAPSHOT and stable releases are available. Manual release instructions follow below.


### Update Version Numbers

Update pom.xml for the release version or use the Maven Versions Plugin using [semantic versioning](https://semver.org/). For IM release candidates and operational releases, PDS4 JParser will be built and deployed as an operational release and versioned and re-build if needed.

```
VERSION=1.1.0
mvn versions:set -DnewVersion=$VERSION
```

### Update Changelog
Update Changelog using [ Changelog Generator](https://github.com/github-changelog-generator/github-changelog-generator). Note: Make sure you set `$CHANGELOG_GITHUB_TOKEN` in your `.bash_profile` or use the `--token` flag.
```
github_changelog_generator --future-release v$VERSION
```

### Commit Changes
Commit changes using following template commit message:
```
[RELEASE] PDS4 Java Parser Library v$VERSION
```

### Build and Deploy Software to [Sonatype Maven Repo](https://repo.maven.apache.org/maven2/gov/nasa/pds/).

```
mvn clean site deploy -P release
```

Note: If you have issues with GPG, be sure to make sure you've created your GPG key, sent to server, and have the following in your `~/.m2/settings.xml`:
```
<profiles>
  <profile>
    <activation>
      <activeByDefault>true</activeByDefault>
    </activation>
    <properties>
      <gpg.executable>gpg</gpg.executable>
      <gpg.keyname>KEY_NAME</gpg.keyname>
      <gpg.passphrase>KEY_PASSPHRASE</gpg.passphrase>
    </properties>
  </profile>
</profiles>

```

### Push Tagged Release
```
git tag v$VERSION
git push --tags
```

### Deploy Site to Github Pages

From cloned repo:
```
git checkout gh-pages

# Create specific version site
mv target/site $VERSION
rm -fr target

# Sync latest version to ops 
rsync -av $VERSION/* .
git add .
git commit -m "Deploy v$VERSION docs"
git push origin gh-pages
```

### Update Versions For Development

Update `pom.xml` with the next SNAPSHOT version either manually or using Github Versions Plugin, e.g.:
```
git checkout main
VERSION=1.16.0-SNAPSHOT
mvn versions:set -DnewVersion=$VERSION
git add pom.xml
git commit -m "Update version for $VERSION development"
git push -u origin main
```

### Complete Release in Github
Currently the process to create more formal release notes and attach Assets is done manually through the [Github UI](https://github.com/NASA-PDS/pds4-jparser/releases/new) but should eventually be automated via script.

## Snapshot Release
1. Checkout the main branch.

2. Deploy software to Sonatype Maven repo:
```
mvn clean site deploy
```

# JAR Dependency Reference

## Official Releases
https://search.maven.org/search?q=g:gov.nasa.pds%20AND%20a:pds4-jparser&core=gav

## Snapshots
https://oss.sonatype.org/content/repositories/snapshots/gov/nasa/pds/pds4-jparser

If you want to access snapshots, add the following to your `~/.m2/settings.xml`:
```
<profiles>
  <profile>
     <id>allow-snapshots</id>
     <activation><activeByDefault>true</activeByDefault></activation>
     <repositories>
       <repository>
         <id>snapshots-repo</id>
         <url>https://oss.sonatype.org/content/repositories/snapshots</url>
         <releases><enabled>false</enabled></releases>
         <snapshots><enabled>true</enabled></snapshots>
       </repository>
     </repositories>
   </profile>
</profiles>
```
