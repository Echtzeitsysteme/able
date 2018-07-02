#!/bin/sh

echo Starting SED deactivation of CAPLED in ANDROID MANIFEST ...

sed 's/\<activity android:name=\"de.tudarmstadt.es.capled.CapLEDActivity\" \/\>/\<!--\<activity android:name=\"de.tudarmstadt.es.capled.CapLEDActivity\" \/\>--\>/' ../android/Application/src/main/AndroidManifest.xml > ../android/Application/src/main/AndroidManifest2.xml && mv ../android/Application/src/main/AndroidManifest2.xml ../android/Application/src/main/AndroidManifest.xml

sed 's/\<receiver android/\<!--\<receiver android/' ../android/Application/src/main/AndroidManifest.xml > ../android/Application/src/main/AndroidManifest2.xml && mv ../android/Application/src/main/AndroidManifest2.xml ../android/Application/src/main/AndroidManifest.xml

sed 's/\<\/receiver\>/\<\/receiver\>--\>/' ../android/Application/src/main/AndroidManifest.xml > ../android/Application/src/main/AndroidManifest2.xml && mv ../android/Application/src/main/AndroidManifest2.xml ../android/Application/src/main/AndroidManifest.xml

echo SED done.

echo Starting creation of .zip file.
zip -r ../ABLE.zip ../android ../psoc LICENSE -X --exclude @zipABLEExclude.lst

echo ABLE Project zipped!

echo Starting Recovery...

sed 's/\<!--\<activity android:name=\"de.tudarmstadt.es.capled.CapLEDActivity\" \/\>--\>/<activity android:name=\"de.tudarmstadt.es.capled.CapLEDActivity\" \/\>/' ../android/Application/src/main/AndroidManifest.xml > ../android/Application/src/main/AndroidManifest2.xml && mv ../android/Application/src/main/AndroidManifest2.xml ../android/Application/src/main/AndroidManifest.xml

sed 's/\<!--\<receiver android/\<receiver android/' ../android/Application/src/main/AndroidManifest.xml > ../android/Application/src/main/AndroidManifest2.xml && mv ../android/Application/src/main/AndroidManifest2.xml ../android/Application/src/main/AndroidManifest.xml

sed 's/\<\/receiver\>--\>/\<\/receiver\>/' ../android/Application/src/main/AndroidManifest.xml > ../android/Application/src/main/AndroidManifest2.xml && mv ../android/Application/src/main/AndroidManifest2.xml ../android/Application/src/main/AndroidManifest.xml

echo Recovery done. Finished!
