The PDS4 Tools project contains software developed by Ames Research Center
thats reads and writes PDS4 data products. The software is packaged in a JAR file.

The software can be compiled and installed with the following commands:

% cd superpom
% cd mvn clean install 
% cd ../package 
% mvn clean install 

The documentation including installation of the software should be online at 
http://pds-cm.jpl.nasa.gov/pds4/preparation/pds4-tools/. If it is not 
accessible, you can execute the "mvn site:run" command and view the 
documentation locally at http://localhost:8080.
