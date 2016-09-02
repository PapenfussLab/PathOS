#!/bin/bash
#
#	pathos_deploy.sh        Build and deploy PathOS from GIT repository
#
#	01		kdoig		22-Oct-16   Initial
#	02		aseleznev	26-May-16   Added options to generalise
#	03		kdoig		17-Aug-16   Cleaned up for v1.2 release
#
#	Usage: pathos_deploy.sh [options]
#
#   Options:
#           -e              pathos environment (pa_local, pa_uat, pa_prod, pa_stage)
#           -g              git branch name to check out (e.g. release/PathOS.1.2.3.RC4)
#           -b              build directory [current dir ./]
#           -d              PathOS deployment directory [/usr/local/PathOS]
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

#
#   Default params
#
BUILD_HOME=`pwd`
HELP=0

#
#   Set home for PathOS deployment
#
export PATHOS_HOME=/usr/local/PathOS

#
#   GIT Repository base URL
#
PATHOS_GIT='ssh://git@115.146.86.118:7999/pat/pathos.git'

#
#   GIT Branch
#
GIT_BRANCH='origin/development'

#
#   PathOS environment to set
#
GRAILS_ENV=pa_stage

nargs=$#
#
#   Get options
#
while getopts d:e:b:g:h\? opt                    # Add additional options here
do  case "$opt" in
    e)      GRAILS_ENV="$OPTARG";;
    g)      GIT_BRANCH="$OPTARG";;
    b)      BUILD_HOME="$OPTARG";;
    d)      PATHOS_HOME="$OPTARG";;
    h)      HELP=1;;
    [?])    HELP=1;;
    esac
done
shift `expr $OPTIND - 1`

#
#   output usage if required
#
if [ $nargs -eq 0 -o $# -ne 0 -o $HELP -eq 1 -o -z "$GRAILS_ENV" -o -z "$GIT_BRANCH" ]; then	# Set number of required arguments here
    echo "
    Usage: `basename $0` -e <pathos_environment> -b <git branch name>

    Options:
            -h              help description
            -e              pathos environment (pa_local, pa_uat, pa_prod, pa_stage) [pa_stage]
            -g              git branch name to check out [origin/development]
            -b              build directory [current dir ./]
            -d              PathOS deployment directory [/usr/local/PathOS]

    PathOS pre-deploy: builds & deploys PathosCore and Loader to a local dir ($PATHOSHOME) and a WAR ready for deployment to specified environment.
    Note: requires gradle 1.10, grails 2.3.7.
    " 1>&2

    exit 1
fi

#
#   Confirm Build dir and Deploy dir
#
mkdir -p "$PATHOS_HOME"
mkdir -p "$BUILD_HOME"
export PATHOS_HOME=`cd "$PATHOS_HOME"; pwd`       # Make absolute path
export BUILD_HOME=`cd "$BUILD_HOME"; pwd`         # Make absolute path

#
#   Only if interactive shell
#
if [ ! -z "$PS1" ]; then
    echo "Building  PathOS in directory: $BUILD_HOME"
    echo "Deploying PathOS in directory: $PATHOS_HOME"
    echo "Git branch: $GIT_BRANCH"
    echo
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
echo "INFO: Checkout source from GIT"

mkdir -p "$BUILD_HOME/PathOS"
pushd "$BUILD_HOME/PathOS"
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

#
#   Check if GRAILS_ENV matches glob/if exists
#
PATHOSCONFIG="$BUILD_HOME/PathOS/PathosCore/src/etc/pa_example.properites"
if [ -d /pathology/NGS ];then
    PATHOSCONFIG=/pathology/NGS/PathOS_Config/$GRAILS_ENV.properties
fi

if [ ! -f "$PATHOSCONFIG" ]; then
    echo "ERROR: Cannot find PathOS properties file [$PATHOSCONFIG] for environment $GRAILS_ENV"
    exit 1
fi

#
# Build PathosCore JAR
#
echo "INFO: Building PathosCore JAR"

pushd "$BUILD_HOME/PathOS/PathosCore"
    gradle --stacktrace uploadArchives
    cp -v $PATHOSCONFIG "$PATHOS_HOME/etc/pathos.properties"
    sed "s#PATHOS_HOME#${PATHOS_HOME}#" < src/etc/pa_example.properties > "$PATHOS_HOME/etc/pa_example.properties"
    sed "s#LOG_HOME#${PATHOS_HOME}#" < src/etc/log4j.properties > "$PATHOS_HOME/lib/log4j.properties"
    mkdir -p "$PATHOS_HOME/log"
popd

#
# Build Loader JAR
#
echo "INFO: Building Loader JAR"

pushd "$BUILD_HOME/PathOS"
    sh Loader/src/bin/MakeDevLinks.sh
popd
pushd "$BUILD_HOME/PathOS/Loader"
    gradle --stacktrace uploadArchives
    sed "s#LOG_HOME#${PATHOS_HOME}#" < ../PathosCore/src/etc/loader.properties > "$PATHOS_HOME/lib/loader/loader.properties"
popd

#
# Build Canary JAR
#
echo "INFO: Building Canary JAR"

pushd "$BUILD_HOME/PathOS/Canary"
    gradle --stacktrace uploadArchives
popd

#
# Build Curate WAR
#
echo "INFO: Building Curate WAR"

pushd "$BUILD_HOME/PathOS/Curate"
    DEFAULTCONFIG="$PATHOS_HOME/etc/pa_example.properties"
    cp -vf application.properties.default application.properties
    grails war target/PathOS.war -Dgrails.env=${GRAILS_ENV} -Dpathos.config="$DEFAULTCONFIG" --stacktrace
    mkdir -p "$PATHOS_HOME/war"
    cp -v target/PathOS.war "$PATHOS_HOME/war"
popd

) 2>&1 | tee pathos_build.log                 # Make a log of all build/deploy activity
 
