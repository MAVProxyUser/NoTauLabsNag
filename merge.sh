if [ ! -d "../OpenPilot/" ]; then
	git clone git://git.openpilot.org/OpenPilot.git ../OpenPilot
fi
cp -rfv ./* ../OpenPilot/
cd ../OpenPilot
make androidgcs V=1
mv ../OpenPilot/build/androidgcs/bin/androidgcs-release.apk . 
adb install -r androidgcs-release.apk
