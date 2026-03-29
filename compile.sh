#!/bin/bash
# compile.sh — compiles all Java source files

mkdir -p out

CP="lib/sqlite-jdbc.jar:lib/zxing-core.jar:lib/zxing-javase.jar"

echo "Compiling..."
javac -cp "$CP" -d out src/*.java

if [ $? -eq 0 ]; then
    echo "Done! Run with: ./run.sh"
else
    echo "Compilation FAILED. Check errors above."
fi
