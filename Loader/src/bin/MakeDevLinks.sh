#!/bin/bash
#
#	MakeDevLinks.sh		Make symbolic links to Curate Domain classes for Loader to use
#
#	Usage:  Run this when setting up the dev enviroment for IDE or when doing a CI build of Loader.jar
#
#			% cd /usr/local/dev/PathOS
#			% MakeDevLinks.sh
#
dir=Loader/src/main/groovy/org/petermac/pathos
if [ -d $dir ]; then 
	pushd $dir
	rm -rvf curate
	mkdir curate
	pushd curate
	    #	Link to Grails domain classes
	    #
	    for domain in  ../../../../../../../../Curate/grails-app/domain/org/petermac/pathos/curate/*
	    do
	        ln -s $domain
	    done
	    ln -s ../../../../../../../../Curate/src/groovy/org/petermac/pathos/curate/Taggable.groovy
    popd

    ls -1 curate

	#	Link to Grails Curate service classes
	#
	rm -rvf services
	mkdir services
	pushd services
	ln -s ../../../../../../../../Curate/grails-app/services/org/petermac/pathos/curate/VarFilterService.groovy VarFilterService.groovy
	ln -s ../../../../../../../../Curate/grails-app/services/org/petermac/pathos/curate/ReportService.groovy ReportService.groovy
	ln -s ../../../../../../../../Curate/grails-app/services/org/petermac/pathos/curate/StatsService.groovy StatsService.groovy
	ln -s ../../../../../../../../Curate/grails-app/services/org/petermac/pathos/curate/RelationService.groovy RelationService.groovy
	ln -s ../../../../../../../../Curate/grails-app/services/org/petermac/pathos/curate/TagService.groovy TagService.groovy
	ln -s ../../../../../../../../Curate/grails-app/services/org/petermac/pathos/curate/AmpliconRoiService.groovy AmpliconRoiService.groovy
	ln -s ../../../../../../../../Curate/grails-app/services/org/petermac/pathos/curate/CurVariantService.groovy CurVariantService.groovy
	ln -s ../../../../../../../../Curate/grails-app/services/org/petermac/pathos/curate/ReportRenderService.groovy ReportRenderService.groovy

	ls -1 *
fi
