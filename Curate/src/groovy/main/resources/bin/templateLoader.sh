#!/bin/bash
#

if [ -z "$PATHOS_HOME" ]; then
    echo "ERROR: \$PATHOS_HOME not set"
    echo "Have you run 'module load pathos/<pa_env>' ?"
    exit 1
fi

LOADER_LOG=${LOADER_LOG:-$PATHOS_HOME/etc/log4j-loader.properties}
JAVA_OPTS="-Xmx8g -Dlog4j.configuration=file:$LOADER_LOG"

#	Run Java
#
RUN=`basename $0`
java $JAVA_OPTS -cp $CURATE_HOME/lib/Loader*.jar org.petermac.pathos.loader.$RUN $*