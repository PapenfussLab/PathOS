#!/bin/bash

# Fail on error
#
set -e

export BUILD_HOME=/build/pathos
export PATHOS_HOME=/pathos

export PATHOS_GIT=${PATHOS_GIT:-"https://github.com/PapenfussLab/PathOS.git"}

export GRAILS_ENV=${GRAILS_ENV:-pa_local}
export GRAILS_OPTS="-Dgrails.dependency.cache.dir=/cache/grails"

if test "$PATHOS_PROXY_HOST" != ""
then
    export GRAILS_OPTS="$GRAILS_OPTS -Dhttp.proxyHost=$PATHOS_PROXY_HOST -Dhttp.proxyPort=$PATHOS_PROXY_PORT -Dhttps.proxyHost=$PATHOS_PROXY_HOST -Dhttps.proxyPort=$PATHOS_PROXY_PORT"
fi

if ! test -d PathOS
then
    git clone ${PATHOS_BRANCH:+-b "$PATHOS_BRANCH"} "$PATHOS_GIT"
fi

cd PathOS

pushd PathosCore
if test "$PATHOS_PROXY_HOST" != ""
then
cat >> gradle.properties << EOF
systemProp.http.proxyHost=10.126.160.170
systemProp.http.proxyPort=3128
systemProp.https.proxyHost=10.126.160.170
systemProp.https.proxyPort=3128
EOF
fi
gradle --gradle-user-home /cache/gradle --stacktrace uploadArchives
popd

pushd Curate
echo y | grails upgrade
grails war target/PathOS.war -Dgrails.env=${GRAILS_ENV} -Dpathos.config=war.properties --stacktrace
cp target/PathOS.war /pathos/
popd

