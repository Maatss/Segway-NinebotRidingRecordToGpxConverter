@echo off

REM Set working directory to current script location, solves issue where you drop files onto it from another window
Pushd "%~dp0"

REM Run jar and pass along all arguments
java -jar build\libs\SegwayJsonToGpxConverter-1.2.jar %*

REM Clear working directory
popd

pause
