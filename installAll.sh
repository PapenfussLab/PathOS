#!/bin/bash
#
#	Script to build monolithic PathOS and friends
#

# Reset BASH time counter
SECONDS=0

set -x
set -e

echo sdk u gradle 1.10   # set gradle
echo sdk u grails 2.3.7  # set grails

gradle install -p Tools/YamlTools
gradle install -p Tools/AmqpTools
gradle install -p Tools/Hl7Tools

gradle install -p APIs
gradle install -p Dynamic

export PATHOS_HOME=/config/binaries/pathos_core/1.5.3

pushd repos
	gradle build --parallel install -p aspose-word &
	gradle build --parallel install -p broad-sting-util &
	gradle build --parallel install -p gatk &
	gradle build --parallel install -p ssw &
	wait
popd

gradle install -x test -p PathosCore

pushd Curate
	cp -f application.properties.default application.properties
	grails war -Dgrails.env=pa_local -Dpathos.config=war.properties 

 	#	Build Loader commands
 	#
	gradle install
popd

echo "Elapsed: $(($SECONDS / 3600))hrs $((($SECONDS / 60) % 60))min $(($SECONDS % 60))sec"

