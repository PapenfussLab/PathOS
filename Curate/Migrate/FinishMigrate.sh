#!/bin/bash
#
#		Migrate.sh		Migrate Path-OS data into Grails Domain Classes
#
#		01		kdoig		28-Jun-13
#
#		Usage: Migrate
#
#vim:ts=4

#
#	process -options
#
HELP=0

while getopts h\? opt					# Add additional options here
do	case "$opt" in
	h)		HELP=1;;
	[?])	HELP=1;;
	esac
done
shift `expr $OPTIND - 1`

#
#	output usage if required
#
if [ $# -ne 0 -o $HELP -eq 1 ]; then	# Set number of required arguments here
	echo "
	Usage: `basename $0` [options] args

	Migrate Path-OS data into Grails Domain Classes
	" 1>&2 

	exit 1
fi

#
#	Start of script
#

#
#	Clear out database first
#
#grails dbm-drop-all 2>&1 > Migrate.log

#
#	Execute scripts in order
#
#grails  --verbose	run-script							\
#					userScripts/addUsers.groovy 		\
#					userScripts/addPatients.groovy		\
#					userScripts/addSamples.groovy		\
#					userScripts/addSeqruns.groovy		\
#					userScripts/addSeqSamples.groovy	\
#					2>&1 >> Migrate.log

(
grails  --verbose	run-script							\
					userScripts/addVariants.groovy		\
					) 2>&1 | tee FinishMigrate.log 


#					userScripts/addSeqVariants.groovy	\
