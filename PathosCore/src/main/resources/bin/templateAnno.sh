#!/bin/bash

export PATHOS_CONFIG=$PATHOS_HOME/etc/pathos.properties

PATHOSCORE_LOG=${PATHOSCORE_LOG:-$PATHOS_HOME/etc/log4j-pathoscore.properties}
JAVA_OPTS="-Xmx8g -Dlog4j.configuration=file:$PATHOSCORE_LOG"

java $JAVA_OPTS -cp $PATHOSCORE_HOME/lib/PathosCore*.jar org.petermac.annotate.$(basename $0) "$@"
