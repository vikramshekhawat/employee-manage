#!/bin/bash

# Employee Salary Management System - Quick Start Script
# This script sets up and runs both backend and frontend servers

echo "=================================="
echo "Employee Salary Management System"
echo "Quick Start Script"
echo "=================================="
echo ""

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed. Please install Java 17 or higher."
    exit 1
fi

echo "✅ Java found: $(java -version 2>&1 | head -n 1)"

# Check if Node.js is installed
if ! command -v node &> /dev/null; then
    echo "❌ Node.js is not installed. Please install Node.js 16 or higher."
    exit 1
fi

echo "✅ Node.js found: $(node --version)"
echo "✅ npm found: $(npm --version)"
echo ""

# Check if MySQL is running
echo "Checking MySQL connection..."
if ! mysql -u root -e "SELECT 1" &> /dev/null; then
    echo "⚠️  Warning: Cannot connect to MySQL. Please ensure MySQL is running."
    echo "You can start MySQL manually and run this script again."
else
    echo "✅ MySQL is running"
fi

echo ""
echo "=================================="
echo "Setting up Backend..."
echo "=================================="

# Build backend
echo "Building backend with Gradle..."
if [ -f "./gradlew" ]; then
    ./gradlew clean build
else
    echo "❌ Gradle wrapper not found. Please run from project root directory."
    exit 1
fi

if [ $? -eq 0 ]; then
    echo "✅ Backend build successful"
else
    echo "❌ Backend build failed"
    exit 1
fi

echo ""
echo "=================================="
echo "Setting up Frontend..."
echo "=================================="

# Install frontend dependencies
cd frontend

if [ ! -d "node_modules" ]; then
    echo "Installing frontend dependencies..."
    npm install
    if [ $? -eq 0 ]; then
        echo "✅ Frontend dependencies installed"
    else
        echo "❌ Frontend dependency installation failed"
        exit 1
    fi
else
    echo "✅ Frontend dependencies already installed"
fi

cd ..

echo ""
echo "=================================="
echo "Setup Complete!"
echo "=================================="
echo ""
echo "To start the application:"
echo ""
echo "1. Start Backend (in terminal 1):"
echo "   ./gradlew bootRun"
echo ""
echo "2. Start Frontend (in terminal 2):"
echo "   cd frontend && npm start"
echo ""
echo "3. Access the application:"
echo "   http://localhost:3000"
echo ""
echo "4. Login with:"
echo "   Username: admin"
echo "   Password: admin123"
echo ""
echo "=================================="

