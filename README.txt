The PDS4-Tools Library provides APIs for exporting PDS4 table
and image objects to various formats including CSV, PNG, Vicar, 
Fits, etc. The software is packaged in a JAR file.

The software can be compiled with the "mvn compile" command but in order 
to create the JAR file, you must execute the "mvn compile jar:jar" command. 
The documentation including release notes, installation and operation of the 
software should be online at 
http://pds-cm.jpl.nasa.gov/pds4/preparation/core/. If it is not 
accessible, you can execute the "mvn site:run" command and view the 
documentation locally at http://localhost:8080.

In order to create a complete distribution package, execute the 
following commands: 

% mvn site
% mvn package
