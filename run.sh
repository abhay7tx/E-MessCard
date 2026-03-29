#!/bin/bash
# run.sh — launches the Mess Card System

CP="out:lib/sqlite-jdbc.jar:lib/zxing-core.jar:lib/zxing-javase.jar"

java -cp "$CP" MessCardSystem
