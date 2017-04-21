#!/bin/bash

# Fail on error
#
set -e

export BUILD_HOME=/build/pathos
export PATHOS_HOME=/pathos

export PATHOS_GIT=${PATHOS_GIT:-"ssh://git@vm-115-146-91-157.melbourne.rc.nectar.org.au:7999/pat/pathos.git"}

export GRAILS_ENV=${GRAILS_ENV:-pa_local}
export GRAILS_OPTS="-Dhttp.proxyHost=10.126.160.170 -Dhttp.proxyPort=3128 -Dhttps.proxyHost=10.126.160.170 -Dhttps.proxyPort=3128 -Dgrails.dependency.cache.dir=/cache/grails"

if ! test -d pathos
then
    git clone ${PATHOS_BRANCH:+-b "$PATHOS_BRANCH"} "$PATHOS_GIT"
fi
cd pathos

pushd PathosCore
cat >> gradle.properties << EOF
systemProp.http.proxyHost=10.126.160.170
systemProp.http.proxyPort=3128
systemProp.https.proxyHost=10.126.160.170
systemProp.https.proxyPort=3128
EOF
gradle --gradle-user-home /cache/gradle --stacktrace uploadArchives
popd

pushd Curate
echo y | grails upgrade
grails war target/PathOS.war -Dgrails.env=${GRAILS_ENV} -Dpathos.config=war.properties --stacktrace
cp target/PathOS.war /pathos/
popd

