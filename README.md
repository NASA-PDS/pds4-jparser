# PDS4 Parser
Java library providing APIs for parsing and exporting information
on PDS4 table and image objects to various formats including CSV, PNG, Vicar, 
Fits, etc. The software is packaged in a JAR file.

# Compile
The software can be compiled with the "mvn compile" command but in order 
to create the JAR file, you must execute the "mvn compile jar:jar" command. 

# Documentation
The documentation including release notes, installation and operation of the 
software should be online at 
https://pds-engineering.jpl.nasa.gov/development/pds4/current/preparation/pds4-parser/index.html.
If it is not  accessible, you can execute the `mvn site:run` command and view the 
documentation locally at http://localhost:8080.

# System Requirements
Current software requires:
* Java 1.8 - the Ant build does not appear to complete succcessfully in later versions of Java
* Maven 2 - EN builds have not yet migrated to Maven 3, so unknown if the software will build correctly

# Distribution
In order to create a complete distribution package, execute the 
following commands: 

```
% mvn site
% mvn package
```
