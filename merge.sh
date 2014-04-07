WD=`pwd`
if [ ! -d "../OpenPilot/" ]; then
	git clone git://git.openpilot.org/OpenPilot.git ../OpenPilot
fi
rm -rf ../OpenPilot/androidgcs/
cp -rfv ./* ../OpenPilot/
cd ../OpenPilot
rm -rf ../OpenPilot/androidgcs/bin
make androidgcs_clean
make androidgcs V=1
mv build/androidgcs/bin/androidgcs-release.apk . 
adb install -r androidgcs-release.apk
cp -v androidgcs-release.apk $WD
