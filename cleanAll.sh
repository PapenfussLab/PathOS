#!/bin/bash
#
#	Script to clean monolithic PathOS and friends
#

gradle clean -p Tools
gradle clean --stacktrace -x test -p PathosCore
gradle clean -p Curate
