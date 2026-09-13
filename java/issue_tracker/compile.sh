#!/bin/bash
# =====================================================
# Compile and Run Script - Department Issue Tracker
# =====================================================

# Find the MySQL connector JAR
JAR=$(find lib -name "mysql-connector*.jar" 2>/dev/null | head -1)

if [ -z "$JAR" ]; then
    echo "ERROR: MySQL Connector JAR not found in lib/"
    echo "Please download mysql-connector-j-x.x.x.jar"
    echo "from https://dev.mysql.com/downloads/connector/j/"
    echo "and place it in the lib/ folder."
    exit 1
fi

echo "Using JDBC driver: $JAR"

# Create build directory
mkdir -p build

# Compile all Java files
echo "Compiling..."
find src -name "*.java" > sources.txt
javac -cp "$JAR" -d build @sources.txt
rm sources.txt

if [ $? -ne 0 ]; then
    echo "Compilation failed."
    exit 1
fi

echo "Compilation successful!"
echo ""

# Run the application
echo "Starting application..."
java -cp "build:$JAR" com.university.issuetracker.Main
