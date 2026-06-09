#!/usr/bin/env bash
set -e

# Check Java major version equals 21
if ! command -v java >/dev/null 2>&1; then
  echo "ERROR: java not found on PATH"
  exit 2
fi

ver=$(java -version 2>&1 | awk -F '"' 'NR==1{print $2}')
# ver examples: 21, 21.0.2, 17.0.8
major=$(echo "$ver" | cut -d. -f1)

 if [ "$major" -lt 21 ]; then
   echo "ERROR: Java 21 or newer required but found: $ver"
  echo "Options:"
  echo " - Install Temurin/Adoptium JDK 21 and ensure java -version shows 21"
  echo "   sdk install java 21.0.4-tem"
  exit 1
fi

echo "Java 21 OK: $ver"
exit 0
