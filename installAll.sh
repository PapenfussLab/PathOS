#!/bin/bash
#
#	Script to build monolithic PathOS and friends
#

set -x
set -e

gradle install -p Tools/YamlTools
gradle install -p Tools/AmqpTools
gradle install -p APIs/PathosApi

export PATHOS_HOME=/config/binaries/pathos_core/1.5.2

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
