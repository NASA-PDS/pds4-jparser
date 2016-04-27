@echo off

:: This script starts the extract-table example program
:: from the PDS4 tools library examples.
::
:: Run "extract-table -h" to see usage information.
::
:: These environment variables alter how the program is
:: run:
::
:: PDS4_TOOLS_HOME - If set, classpath will include
::   all JAR files in $PDS4_TOOLS_HOME/lib/. Otherwise
::   defaults to the parent directory of this script.
::
:: PDS4_TOOLS_OPTS - This may contain additional JVM
::   arguments and options.
::
:: PDS4_TOOLS_ARGS - This may contain additional arguments
::   to pass to the application.
::
:: JAVACMD - If set, this is the command that runs the
::   Java JRE. Defaults to the usual Java command for the
::   JRE installed at $JAVA_HOME.
::
:: JAVA_HOME - If set, this is the location where the Java
::   JRE can be found. If not set, Java is assumed to be
::   in the classpath.
::
:: CLASSPATH - If set, this will be appended to the end of
::   the classpath obtained by adding all JARs in the lib/
::   directory.
::
:: exec_debug - If set to a true value, does not run the
::   applcation. Instead, just echoes the Java command that
::   would have been executed.

setlocal enableextensions

set PROGDIR=%~dp0

:: Get the location of the tools package.
if defined PDS4_TOOLS_HOME goto toolsHomeDefined

pushd %PROGDIR%
cd ..
set PDS4_TOOLS_HOME=%CD%
popd

:toolsHomeDefined
set LIB=%PDS4_TOOLS_HOME%\lib

set LOCAL_CLASSPATH=%LIB%\*
if not defined CLASSPATH goto noExtraClasspath
    set LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;%CLASSPATH%
:noExtraClasspath

if defined JAVACMD goto hasJavaCmd
if not defined JAVA_HOME goto noJavaHome

if not exist "%JAVA_HOME%\bin\java.exe" goto noJavaHome
set JAVACMD=%JAVA_HOME%\bin\java.exe
goto hasJavaCmd

:noJavaHome
set JAVACMD=java.exe

:hasJavaCmd

set MAIN_CLASS=gov.nasa.pds.objectAccess.example.ExtractTable

if not defined exec_debug goto runProgram

echo "%JAVACMD%" %PDS4_TOOLS_OPTS% -cp "%LOCAL_CLASSPATH%" -Dpds4.tools.home="%PDS4_TOOLS_HOME%" -Dpds4.tools.progname="%PROGNAME%" %MAIN_CLASS% %* %PDS4_TOOLS_ARGS%
goto done

:runProgram
"%JAVACMD%" %PDS4_TOOLS_OPTS% -cp "%LOCAL_CLASSPATH%" -Dpds4.tools.home="%PDS4_TOOLS_HOME%" -Dpds4.tools.progname="%PROGNAME%" %MAIN_CLASS% %* %PDS4_TOOLS_ARGS%

:done
