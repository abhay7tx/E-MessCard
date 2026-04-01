#!/bin/bash
# ─────────────────────────────────────────────────
#  setup.sh  –  downloads dependencies into lib/
# ─────────────────────────────────────────────────

mkdir -p lib

echo "Downloading SQLite JDBC..."
curl -L "https://github.com/xerial/sqlite-jdbc/releases/download/3.45.3.0/sqlite-jdbc-3.45.3.0.jar" \
     -o lib/sqlite-jdbc.jar

echo "Downloading ZXing core..."
curl -L "https://repo1.maven.org/maven2/com/google/zxing/core/3.5.3/core-3.5.3.jar" \
     -o lib/zxing-core.jar

echo "Downloading ZXing Java SE..."
curl -L "https://repo1.maven.org/maven2/com/google/zxing/javase/3.5.3/javase-3.5.3.jar" \
     -o lib/zxing-javase.jar

echo ""
echo "All dependencies downloaded to lib/"
echo "Now run:  ./compile.sh  then  ./run.sh"
