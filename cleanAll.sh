#!/bin/bash
#
#	Script to clean monolithic PathOS and friends
#

#	Clean out Maven and Grails caches
#
rm -rf ~/.m2/repository/org/petermac
rm -rf ~/.grails/ivy-cache/org.petermac.pathos

gradle clean -p Tools
gradle clean -p APIs
gradle clean -p Dynamic
gradle clean --stacktrace -x test -p PathosCore
gradle clean -p Curate
