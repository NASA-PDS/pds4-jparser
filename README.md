# PDS4 JParser
Java library providing APIs for parsing and exporting information
on PDS4 table and image objects to various formats including CSV, PNG, Vicar, 
Fits, etc. The software is packaged in a JAR file.


# Dependencies
Two dependencies for this tool are not yet in Maven Central repository so they will need to be installed in your local maven repository in order to build and compile.

## PDS3 Product Tools
```1. Clone the repo and checkout v4.0.0:
```
git clone git@github.com:NASA-PDS-Incubator/pds3-product-tools.git
git checkout tags/v4.0.0
```
2. Run `mvn install` to install the package in your local maven repo.

# System Requirements
Current software requires:
* Java 1.8 - the Ant build does not appear to complete succcessfully in later versions of Java
* Maven 2 - EN builds have not yet migrated to Maven 3, so unknown if the software will build correctly


# Build
The software can be compiled and built with the "mvn compile" command but in order 
to create the JAR file, you must execute the "mvn compile jar:jar" command. 

In order to create a complete distribution package, execute the 
following commands: 

% mvn site
% mvn package


# Documentation
The documentation including release notes, installation and operation of the 
software should be online at 
https://pds-engineering.jpl.nasa.gov/development/pds4/current/preparation/validate/. If it is not 
accessible, you can execute the "mvn site:run" command and view the 
documentation locally at http://localhost:8080.