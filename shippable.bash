#!/bin/bash

# CI steps for this repository
# Author: Roland Kluge
# Date: 2017-02-20

# Build Android application
cd ./android
bash ./gradlew clean test assemble
cd ..

# Provide test reports to Shippable
targetDirectoryForTestResults=/root/src/github.com/Echtzeitsysteme/able/shippable/testresults
if [ -d $targetDirectoryForTestResults ];
then
  cp ./android/api/build/test-results/TEST-*.xml $targetDirectoryForTestResults
else
  echo "Target directory for test results is missing: $targetDirectoryForTestResults"
fi