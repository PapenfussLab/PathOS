#!/bin/bash

#
# pathos_deploy.sh        Build and deploy PathOS from GIT repository
#
# 01    kdoig      22-Oct-16   Initial
# 02    aseleznev  26-May-16   Added options to generalise
# 03    kdoig      17-Aug-16   Cleaned up for v1.2 release
# 04    kdoig      08-Sep-16   Removed /lib/loader dir
#
# Usage: pathos_deploy.sh [options]
#
#   Options:
#           -e              pathos environment (pa_local, pa_uat, pa_prod, pa_stage)
#           -g              git branch name to check out (e.g. release/PathOS.1.2.3.RC4)
#           -b              build directory [current dir ./]
#           -d              PathOS deployment directory [/usr/local/PathOS]
#           -i              Use this flag to indicate that we are in a bamboo environment
#

#   To run this, you need:
#   Java JDK 1.7 from http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html
#   grails (we use 2.3.7 at time of writing) from https://github.com/grails/grails-core/releases/download/v2.3.7/grails-2.3.7.zip
#   gradle (we use 1.10 at time of writing)
#   git & a git ssh key with access to PathOS git repository
#
#   Other tasks for web app
#   Install MySql
#   Install Tomcat
#

BUILD_HOME=`pwd`
HELP=0
NARG=$#
IN_BAMBOO=0

#
#   Set home for PathOS deployment
#
export PATHOS_HOME=/usr/local/PathOS

#
#   GIT Repository base URL
#
PATHOS_GIT='https://github.com/PapenfussLab/PathOS.git'
#
#   GIT Branch
#
GIT_BRANCH='origin/master'

#
#   Default PathOS environment
#
GRAILS_ENV=pa_stage

#
#   Get options
#
while getopts d:e:b:g:hi\? opt                    # Add additional options here
do  case "$opt" in
    e)      GRAILS_ENV="$OPTARG";;
    g)      GIT_BRANCH="$OPTARG";;
    b)      BUILD_HOME="$OPTARG";;
    d)      PATHOS_HOME="$OPTARG";;
    i)      IN_BAMBOO=1;;
    h)      HELP=1;;
    [?])    HELP=1;;
    esac
done
shift `expr $OPTIND - 1`

#
#   output usage if required
#
if [ $NARG -eq 0 -o $# -ne 0 -o $HELP -eq 1 -o -z "$GRAILS_ENV" -o -z "$GIT_BRANCH" ]; then	# Set number of required arguments here
    echo "
    Usage: `basename $0` -e <pathos_environment> -b <git branch name>

    Options:
            -h              help description
            -e              pathos environment (pa_local, pa_uat, pa_prod, pa_stage, pa_research) [pa_stage]
            -g              git branch name to check out [origin/development]
            -b              build directory [current dir ./]
            -d              PathOS deployment directory [/usr/local/PathOS]
            -i              Use this flag to deploy from Bamboo

    PathOS pre-deploy: builds & deploys PathosCore and Loader to a local dir ($PATHOSHOME) and a WAR ready for deployment to specified environment.
    Note: requires gradle 1.10, grails 2.3.7.
    " 1>&2

    exit 1
fi

#
#   expand Build and Deploy paths
#
mkdir -p "$PATHOS_HOME"
mkdir -p "$BUILD_HOME"
export PATHOS_HOME=`cd "$PATHOS_HOME"; pwd`       # Make absolute path
export BUILD_HOME=`cd "$BUILD_HOME"; pwd`         # Make absolute path

echo "Building  PathOS in directory: $BUILD_HOME"
echo "Deploying PathOS in directory: $PATHOS_HOME"
echo "Git branch: $GIT_BRANCH"
echo

#
#   Confirm Build dir and Deploy dir
#
if [ $IN_BAMBOO -eq 0 ] ; then
    read -r -p "Proceed ? [y/n] " response
    if [[ $response =~ ^([yY][eE][sS]|[yY])$ ]]
    then
        echo "Continuing..."
    else
        echo "Exiting..."
        exit 1
    fi
fi

(
#
#   Checkout source from Bitbucket
#
if [ $IN_BAMBOO -eq 0 ] ; then
    echo "INFO: Checkout source from GIT"

    pushd "$BUILD_HOME"
        git init
        git remote add origin ${PATHOS_GIT}
        git pull origin master
        git fetch

        if ! [ "git branch --list $GIT_BRANCH " ]
        then
            echo "ERROR: Branch name $GIT_BRANCH does not exist. Exiting."
            exit 1
        fi

        git checkout $GIT_BRANCH
        git status
    popd
fi

#
#   Check if GRAILS_ENV matches glob/if exists
#
PATHOSCONFIG="$BUILD_HOME/PathosCore/src/etc/pa_example.properties"
if [ -d /pathology/NGS ];then
    PATHOSCONFIG=/pathology/NGS/PathOS_Deploy/etc/$GRAILS_ENV.properties
fi

if [ ! -f "$PATHOSCONFIG" ]; then
    echo "ERROR: Cannot find PathOS properties file [$PATHOSCONFIG] for environment $GRAILS_ENV"
    exit 1
fi

#
# Build PathosCore JAR
#
echo "INFO: Building PathosCore JAR"

pushd "$BUILD_HOME/PathosCore"
    gradle --stacktrace uploadArchives

    #
    #   Copy in non-repository files if PeterMac install
    #
    if [ -d /pathology/NGS/PathOS_Deploy ];then
        cp -vr /pathology/NGS/PathOS_Deploy/* $PATHOS_HOME
    fi
    cp -v $PATHOSCONFIG "$PATHOS_HOME/etc/pathos.properties"
    sed "s#PATHOS_HOME#${PATHOS_HOME}#" < src/etc/pa_example.properties > "$PATHOS_HOME/etc/pa_example.properties"
    sed "s#LOG_HOME#${PATHOS_HOME}#" < src/etc/log4j.properties > "$PATHOS_HOME/lib/log4j.properties"
    mkdir -p "$PATHOS_HOME/log"                                 # Logging directory
    mkdir -p "$PATHOS_HOME/Backup/${GRAILS_ENV}/Archive"        # Database backup directory
    mkdir -p "$PATHOS_HOME/Searchable"                          # Web app search index directory
    echo '### Run this command before starting Tomcat ###'
    echo '###############################################'
    echo sudo chown tomcat:tomcat "$PATHOS_HOME/Searchable"     # Must be writeable by tomcat
    echo '###############################################'
popd

#
# Build Loader JAR
#
echo "INFO: Building Loader JAR"

pushd "$BUILD_HOME"
    sh Loader/src/bin/MakeDevLinks.sh
popd
pushd "$BUILD_HOME/Loader"
    gradle --stacktrace uploadArchives
    sed "s#LOG_HOME#${PATHOS_HOME}#" < ../PathosCore/src/etc/loader.properties > "$PATHOS_HOME/lib/loader.properties"
popd

#
# Build Curate WAR
#
echo "INFO: Building Curate WAR"

pushd "$BUILD_HOME/Curate"
    DEFAULTCONFIG="$PATHOS_HOME/etc/pa_example.properties"
    cp -vf application.properties.default application.properties
    grails war target/PathOS.war -Dgrails.env=${GRAILS_ENV} -Dpathos.config="$DEFAULTCONFIG" --stacktrace
    mkdir -p "$PATHOS_HOME/war"
    cp -v target/PathOS.war "$PATHOS_HOME/war"
popd

) 2>&1 | tee pathos_build.log                 # Make a log of all build/deploy activity

exit 0
