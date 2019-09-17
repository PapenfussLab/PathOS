#!/bin/bash
#
#	Wrap VcfDbVerify to check that recent VCFs have been loaded into PathOS
#
#   edit:   01  02-nov-18   Ken Doig    Initial create
#

#set -e

pa_env=pa_prod
basedir=/pathology/NGS/Samples/Molpath
email=Christopher.Welsh@petermac.org
days=3
logfile=/tmp/vdv$$.log

#	Find all VCFs over an hour old but less than $days old
#
#vcfs=`find /pathology/NGS/Samples/Molpath/181012_NS500817_0403_AH2HKKBGX9/09337079/09337079.vcf`
vcfs=`find ${basedir}/181*/*/*.vcf -type f -mmin +60 -a -mtime -$days`

#	Check VCFs and AlignStats have been loaded into PathOS
#
VcfDbVerify --rdb ${pa_env} --actual --qc $vcfs > $logfile 2>&1

#	Email results
#
if [ $? -gt 0 ]; then
  mail -s "VcfDbVerify: failed " ${email} < $logfile 
else
  mail -s "VcfDbVerify: success" ${email} < $logfile 
  rm -f $logfile
fi
