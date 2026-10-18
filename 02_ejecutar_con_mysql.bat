@echo off
setlocal
cd /d "%~dp0"
if not exist "lib\mysql-connector-j.jar" (
 echo ERROR: Falta lib\mysql-connector-j.jar
 echo Descargar MySQL Connector/J compatible desde el sitio oficial de MySQL,
 echo y copiar el archivo JAR renombrado a lib\mysql-connector-j.jar
 pause
 exit /b 1
)
if not exist out mkdir out
javac -encoding UTF-8 -d out src\*.java
if errorlevel 1 (
 echo ERROR: No se pudo compilar.
 pause
 exit /b 1
)
echo.
echo Se usaran ST_DB_URL, ST_DB_USER y ST_DB_PASS del entorno.
java -cp "out;lib\mysql-connector-j.jar" Main
pause
