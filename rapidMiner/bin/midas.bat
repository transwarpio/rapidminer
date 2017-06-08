@echo off
SETLOCAL ENABLEDELAYEDEXPANSION

rem ##############################################################
rem ###                                                        ###
rem ###      Windows Start Script for Midas        ###
rem ###                                                        ###
rem ###  This script tries to determine the location of        ###
rem ###  Midas, searches for a proper Java         ###
rem ###  executable and starts the GUI version.                ###
rem ###                                                        ###
rem ##############################################################

rem #############################################
rem ###                                       ###
rem ###  Setting or Guessing MIDAS_HOME  ###
rem ###                                       ###
rem #############################################

rem ### remove _JAVA_OPTIONS environment variable for this run ###
rem ### it could contain stuff that break Studio launching so we ignore it completely ###
set _JAVA_OPTIONS=

rem ###  set MIDAS_HOME to the correct directory if you changed the location of this start script  ###

if "%MIDAS_HOME%"=="" goto guessrapidminerhome
goto javahome

:guessrapidminerhome
for %%? in ("%~dp0.") do set MIDAS_HOME=%%~f?
echo MIDAS_HOME environment variable is not set. Trying the directory '%MIDAS_HOME%'...
goto javahome

rem ############################
rem ###                      ###
rem ###  Searching for Java  ###
rem ###                      ###
rem ############################

:javahome
set LOCAL_JRE_JAVA=%MIDAS_HOME%\jre\bin\java.exe
if exist "%LOCAL_JRE_JAVA%" goto localjre
goto checkjavahome

:localjre
set JAVA=%LOCAL_JRE_JAVA%
echo Using local jre: %JAVA%...
goto commandlinearguments

:checkjavahome
if "%JAVA_HOME%"=="" goto checkpath
set JAVA_CHECK=%JAVA_HOME%\bin\java.exe
if exist "%JAVA_CHECK%" goto globaljre 
goto error3

:globaljre
set JAVA=%JAVA_HOME%\bin\java
echo Using global jre: %JAVA%...
goto commandlinearguments

:checkpath
java -version 2> nul:
if errorlevel 1 goto error2
goto globaljrepath

:globaljrepath
set JAVA=java
echo Using global jre found on path: %JAVA%
goto commandlinearguments

rem #########################################
rem ###                                   ###
rem ###  Handling Command Line Arguments  ###
rem ###                                   ###
rem #########################################

:commandlinearguments
set CMD_LINE_ARGS=
:args
if [%1]==[] goto update
set CMD_LINE_ARGS=%CMD_LINE_ARGS% %1
shift
goto args

rem ###########################
rem ###                     ###
rem ###  Performing Update  ###
rem ###                     ###
rem ###########################

:update
set RUINSTALL_DIR="%HOMEDRIVE%%HOMEPATH%\.Midas\update\RUinstall"
if exist %RUINSTALL_DIR% goto perform_update
goto start

:perform_update
echo Performing Midas Update ...
xcopy %RUINSTALL_DIR% "%MIDAS_HOME%" /c /s /y /i
rmdir %RUINSTALL_DIR% /s /q
goto start

rem #############################
rem ###                       ###
rem ###  Starting Midas  ###
rem ###                       ###
rem #############################

:start
set CHECK_VERSION_FILE="%APPDATA%\check_rm_java_version"
"%JAVA%" -version 2>&1 | findstr /i "version" >  %CHECK_VERSION_FILE%
for /F "tokens=3" %%g in ('type %CHECK_VERSION_FILE%') do (
    set JAVAVER=%%g
)
set JAVAVER=%JAVAVER:"=%
echo Java Version: %JAVAVER%
del %CHECK_VERSION_FILE%
goto gatherSettings

rem ##################################
rem ##                              ##
rem ##  Generate JVM start options  ##
rem ##                              ##
rem ##################################

:gatherSettings
set TEMP_FILE="%APPDATA%\tempRMS6L"
"%JAVA%" -cp "%MIDAS_HOME%"\lib\* com.rapidminer.launcher.JVMOptionBuilder > %TEMP_FILE%
set /p JVM_OPTIONS= < %TEMP_FILE%
del %TEMP_FILE%
if "!JVM_OPTIONS!"=="" ( goto error4 )
goto launch

rem ##################################
rem ##                              ##
rem ##  Launching Midas        ##
rem ##                              ##
rem ##################################

:launch
echo Launching Midas GUI now...
"%JAVA%" %JVM_OPTIONS% -Dmidas -Dfile.encoding=utf8 -cp "%MIDAS_HOME%"\lib\*;"%MIDAS_HOME%"\lib\jdbc\* io.transwarp.midas.GUILauncher %CMD_LINE_ARGS%
goto startEnd

:startEnd
if errorlevel 2 goto update 
goto endGUI

rem ########################
rem ###                  ###
rem ###  Error messages  ###
rem ###                  ###
rem ########################

:error1
echo.
echo ERROR: Neither 
echo %RAPIDMINER_JAR% 
echo nor 
echo %BUILD% 
echo was found.
echo If you use the source version of Midas, try 
echo 'ant build' or 'ant dist' first.
echo.
pause
goto end

:error2
echo.
echo ERROR: Java cannot be found. 
echo Please install Java properly (check if JAVA_HOME is 
echo correctly set or ensure that 'java' is part of the 
echo PATH environment variable).
echo.
pause
goto end

:error3
echo.
echo ERROR: Java cannot be found in the path JAVA_HOME
echo Please install Java properly (it seems that the 
echo environment variable JAVA_HOME does not point to 
echo a Java installation).
echo.
pause
goto end

:error4
echo.
echo ERROR: Launch settings could not be set
echo Please install Midas properly and do
echo not change the Midas.bat file.
echo.
pause
goto end

rem #############
rem ###       ###
rem ###  END  ###
rem ###       ###
rem #############

:endGUI
pause

:end
