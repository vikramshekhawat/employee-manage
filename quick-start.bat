@echo off
REM Employee Salary Management System - Quick Start Script (Windows)
REM This script sets up and runs both backend and frontend servers

echo ==================================
echo Employee Salary Management System
echo Quick Start Script (Windows)
echo ==================================
echo.

REM Check if Java is installed
java -version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo X Java is not installed. Please install Java 17 or higher.
    pause
    exit /b 1
)

echo [OK] Java found
java -version 2>&1 | findstr /C:"version"

REM Check if Node.js is installed
node --version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo X Node.js is not installed. Please install Node.js 16 or higher.
    pause
    exit /b 1
)

echo [OK] Node.js found
node --version
echo [OK] npm found
npm --version
echo.

echo ==================================
echo Setting up Backend...
echo ==================================

REM Build backend
echo Building backend with Gradle...
if exist "gradlew.bat" (
    call gradlew.bat clean build
    if %ERRORLEVEL% EQU 0 (
        echo [OK] Backend build successful
    ) else (
        echo X Backend build failed
        pause
        exit /b 1
    )
) else (
    echo X Gradle wrapper not found. Please run from project root directory.
    pause
    exit /b 1
)

echo.
echo ==================================
echo Setting up Frontend...
echo ==================================

REM Install frontend dependencies
cd frontend

if not exist "node_modules" (
    echo Installing frontend dependencies...
    call npm install
    if %ERRORLEVEL% EQU 0 (
        echo [OK] Frontend dependencies installed
    ) else (
        echo X Frontend dependency installation failed
        pause
        exit /b 1
    )
) else (
    echo [OK] Frontend dependencies already installed
)

cd ..

echo.
echo ==================================
echo Setup Complete!
echo ==================================
echo.
echo To start the application:
echo.
echo 1. Start Backend (in terminal 1):
echo    gradlew.bat bootRun
echo.
echo 2. Start Frontend (in terminal 2):
echo    cd frontend
echo    npm start
echo.
echo 3. Access the application:
echo    http://localhost:3000
echo.
echo 4. Login with:
echo    Username: admin
echo    Password: admin123
echo.
echo ==================================
pause

