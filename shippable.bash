#!/bin/bash

# CI steps for this repository
# Author: Roland Kluge
# Date: 2017-02-20

if [ ! -d "$ANDROID_HOME" ];
then
  echo "The ANDROID_HOME variable should point to the Android SDK installation. But is currently set to '$ANDROID_HOME'"
  exit -1
fi

# Build Android application
cd ./android

# Accept licenses by copying the Android license to the Android SDK home
# See also: https://developer.android.com/studio/intro/update.html#download-with-gradle
mkdir -p $ANDROID_HOME/licenses
cp -r ./licenses/* $ANDROID_HOME/licenses

bash ./gradlew clean test assemble
exitCode=$?
[ "$exitCode" == "0" ] || exit $exitCode

cd ..

# Provide test reports to Shippable
targetDirectoryForTestResults=/root/src/github.com/Echtzeitsysteme/able/shippable/testresults
if [ -d $targetDirectoryForTestResults ];
then
  cp ./android/api/build/test-results/TEST-*.xml $targetDirectoryForTestResults
else
  echo "Target directory for test results is missing: $targetDirectoryForTestResults"
fi