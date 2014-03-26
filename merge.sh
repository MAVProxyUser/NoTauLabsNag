if [ ! -d "../OpenPilot/" ]; then
	git clone git://git.openpilot.org/OpenPilot.git ../OpenPilot
fi
cp -rfv ./* ../OpenPilot/
cd ../OpenPilot
make androidgcs 
mv ../OpenPilot/build/androidgcs/bin/androidgcs-release.apk . 
