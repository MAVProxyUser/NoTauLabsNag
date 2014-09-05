if [ -z "$ANDROID_HOME" ]; then
	export ANDROID_HOME=~/adt-bundle-linux-x86_64-20140702/sdk/
fi

WD=`pwd`
if [ ! -d "../OpenPilot/" ]; then
	git clone git://git.openpilot.org/OpenPilot.git ../OpenPilot
fi

cp androidgcs/ant.project.properties androidgcs/project.properties
cd ../OpenPilot
git tag -l
git checkout tags/RELEASE-14.06.01

cd $WD
rm -rf ../OpenPilot/androidgcs/
cp -rfv ./* ../OpenPilot/
rm -rf ../OpenPilot/androidgcs/bin
#android update project -s --path ./androidgcs 
cd ../OpenPilot

make androidgcs_clean
make androidgcs V=1
mv build/androidgcs/bin/androidgcs-release.apk . 
adb install -r androidgcs-release.apk
cp -v androidgcs-release.apk $WD
cd $WD
cp androidgcs/eclipse.project.properties androidgcs/project.properties
