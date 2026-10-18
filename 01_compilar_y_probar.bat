@echo off
setlocal
cd /d "%~dp0"
where javac >nul 2>nul
if errorlevel 1 (
  echo ERROR: No se encontro javac. Instalar JDK 17+ y configurar PATH.
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
echo === Pruebas de clases POO sin MySQL ===
java -cp out PruebasPOO
pause
