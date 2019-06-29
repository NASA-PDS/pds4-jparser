# PDS4 JParser
Java library providing APIs for parsing and exporting information
on PDS4 table and image objects to various formats including CSV, PNG, Vicar, 
Fits, etc. The software is packaged in a JAR file.

# System Requirements
Current software requires:
* Java 1.8 - the Ant build does not appear to complete succcessfully in later versions of Java
* Maven 2 - EN builds have not yet migrated to Maven 3, so unknown if the software will build correctly

# Documentation
The documentation including release notes, installation and operation of the 
software should be online at 
https://pds-engineering.jpl.nasa.gov/development/pds4/current/preparation/validate/. If it is not 
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

# Release
Here is the procedure for releasing the software both in Github and pushing the JARs to the public Maven Central repo.

## Pre-Requisites
* Make sure you have your GPG Key created and sent to server.
* Make sure you have your .settings configured correctly for GPG:
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

## Operational Release
1. Checkout the dev branch.

2. Version the software:
```
mvn versions:set -DnewVersion=1.2.0
```

3. Deploy software to Sonatype Maven repo:
```
# Operational release
mvn clean site deploy -P release
```

4. Create pull request from dev -> master and merge.

5. Tag release in Github

6. Update version to next snapshot:
```
mvn versions:set -DnewVersion=1.3.0-SNAPSHOT
```

## SNAPSHOT Release
1. Checkout the dev branch.

2. Deploy software to Sonatype Maven repo:
```
# Operational release
mvn clean site deploy
```

# Maven JAR Dependency

https://search.maven.org/search?q=g:gov.nasa.pds%20AND%20a:pds4-jparser&core=gav