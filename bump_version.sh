#!/bin/bash
#update pathos version. this essentially changes in-place the files where version is stores

oldversion=$1
version=$2
#todo validate version

if [ -z "$1" ]; then
	echo "Usage: bump_version.sh oldversion newversion"
	exit 0
fi

if [ -z "$2" ]; then
        echo "Usage: bump_version.sh oldversion newversion"
   	exit 0
fi

if [[ "$2" =~ [^0-9.] ]]; then
	echo "Only dots and numbers in your new version number are allowed."
	exit 0
fi

perl -pi -e "s/app.version=$oldversion/app.version=$version/g" ./Curate/application.properties
perl -pi -e "s/app.version=$oldversion/app.version=$version/g" ./Curate/application.properties.default
perl -pi -e "s/version = '$oldversion'/version = '$version'/g" ./PathosCore/build.gradle
perl -pi -e "s/version = '$oldversion'/version = '$version'/g" ./Loader/build.gradle
perl -pi -e "s/version = '$oldversion'/version = '$version'/g" ./Canary/build.gradle
perl -pi -e "s/PathosCore-all:$oldversion/PathosCore-all:$version/g" ./Curate/grails-app/conf/BuildConfig.groovy
perl -pi -e "s/Configuration Ver $oldversion/Configuration Ver $version/g" ./PathosCore/src/etc/FilterRules.groovy
