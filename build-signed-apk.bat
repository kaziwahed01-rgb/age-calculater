@echo off
cd /d i:\new-flutter-projects

REM Find Java installation
for /f "tokens=*" %%A in ('where java 2^>nul') do set JAVA_HOME=%%~dpA..

REM If Java not found in PATH, try common locations
if not defined JAVA_HOME (
    if exist "C:\Program Files\Java\jdk-11" set JAVA_HOME=C:\Program Files\Java\jdk-11
    if exist "C:\Program Files\Java\jdk-17" set JAVA_HOME=C:\Program Files\Java\jdk-17
    if exist "%ProgramFiles%\Android\Android Studio\jre" set JAVA_HOME=%ProgramFiles%\Android\Android Studio\jre
)

echo JAVA_HOME: %JAVA_HOME%

REM Generate keystore
if not exist "keystore.jks" (
    echo Generating keystore...
    "%JAVA_HOME%\bin\keytool.exe" -genkey -v -keystore keystore.jks -keyalg RSA -keysize 2048 -validity 10000 -alias agecalc_key -storepass agecalculator123 -keypass agecalculator123 -dname "CN=AgeCal Dev, OU=Development, O=MyCompany, L=City, S=State, C=US"
) else (
    echo Keystore already exists.
)

REM Build signed APK
echo Building signed release APK...
call gradlew.bat assembleRelease

REM Build AAB (Android App Bundle)
echo Building release bundle...
call gradlew.bat bundleRelease

echo.
echo Build complete!
echo Signed APK location: app\build\outputs\apk\release\app-release.apk
echo Bundle location: app\build\outputs\bundle\release\app-release.aab
pause

