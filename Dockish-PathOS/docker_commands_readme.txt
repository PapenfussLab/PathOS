##	Notes for building pathos with Docker
##

#	Download the docker pathos repository
#	Note this path must be able to be bind mounted into Docker containers.
#	On MacOS, /Users /Volumes /tmp and /private are available by default
#
% mkdir -p /Users/PathOS
% cd /Users/PathOS
% git init
% git remote add origin https://github.com/PapenfussLab/PathOS.git
% git pull origin master

#	Build image for pathos-build (About 45min first time) (About 7min on Mac) (2min on Cloud)
#
% cd Dockish-PathOS/docker/pathos-build/

% docker build --tag pathos-build .

#	Check image pathos-build was created
#	
% docker images

#	Create data container
#
% docker create -v /cache --name pathos-build-cache pathos-build

#	Run image
#
% mkdir /Users/PathOS/Dockish-PathOS/docker/pathos/   # Needs to be visible to docker shared volumes

#	Run image, populate pathos-build-cache (Mac ~45 min) and build artefacts (6 min) (Nectar PathosCore 2 min, Curate 24 min)
#
% docker run --rm -it -v /Users/PathOS/Dockish-PathOS/docker/pathos/:/pathos/ --volumes-from pathos-build-cache pathos-build

# check results
#
% ls -l /Users/PathOS/Dockish-PathOS/docker/pathos
total 221328
-rw-r--r--   1 kdd  1378455705  113316850 11 May 08:44 PathOS.war
drwxr-xr-x   3 kdd  1378455705        102 11 May 08:42 Pipeline
drwxr-xr-x  87 kdd  1378455705       2958 11 May 08:42 Report
drwxr-xr-x  46 kdd  1378455705       1564 11 May 08:42 bin
drwxr-xr-x  13 kdd  1378455705        442 11 May 08:42 etc
drwxr-xr-x   7 kdd  1378455705        238 11 May 08:42 lib

#	Setup image for pathos-tomcat 
#
% cd /Users/PathOS/Dockish-PathOS/docker
% cp -v pathos/PathOS.war pathos-tomcat

#	Setup image for pathos-tools
#
% cd /Users/PathOS/Dockish-PathOS/docker/pathos
% tar cvzf tools.tgz bin/ etc/ lib/ Pipeline/ Report/
% cp -v tools.tgz /Users/PathOS/Dockish-PathOS/docker/pathos-tools

#	Create a composite image of all dependent pathos images
#
% cd /Users/PathOS/Dockish-PathOS/docker
% vi .env   ## edit the file .env and change PATHOS_DATA to be the root of the pipeline repository eg /Users/PathOS/Dockish-PathOS/docker/pathology
% docker-compose build

#	Check results
#
% docker images

#	Start up docker pathos (~50s !)
#
% docker-compose up [-d]   ## -d to run as daemon

#	From a new terminal window
#	Check that the Docker containers came up and have the right ports
#
% docker container ls
% docker ps

#	Populate DB Copy example DB from this repository 
#	From Release Downloads https://github.com/PapenfussLab/PathOS/releases/tag/v1.3.0RC12-beta)
#
#	Note: curl may redirect
#	Note: container must be running to load DB
#
% curl -L -o dbalt.170125.sql.gz https://github.com/PapenfussLab/PathOS/releases/download/v1.3.0RC12-beta/dbalt.170125.sql.gz
% gunzip < dbalt.170125.sql.gz | docker exec -i docker_pathos-mariadb_1 mysql -ubioinformatics -ppathos -D dblive -B

#	To restart tomcat after the DB load (or UI errors)
#
% docker restart docker_pathos-tomcat_1

## Go to browser (Mac)    https://localhost/PathOS/

#	To login to PathOS use the following table of credentials
#	ROLE_ADMIN may create users via the LHS navigation pane/Users menu
#
#	The following role/user/pass combinations have been setup 
#	Role          User          Password
	ROLE_VIEWER   pathosviewer  pathos
	ROLE_LAB      pathoslab     pathos
	ROLE_CURATOR  pathoscurator pathos
	ROLE_ADMIN    pathosadmin   pathos

#	PathOS has an inbuilt IGV browser (www.igv.org) which can display sample VCFs and BAMs
#
#	To populate an example pipeline data repository (VCFs BAMs etc) follow the steps below
#	Because of the size of BAM files, only a few samples have VCF and BAMs for viewing.
#	Navigate to the following Sequencing Runs using Search
#	170104_M00139_0111_000000000-B327T
#	170105_M01053_0480_000000000-B327P
#	170106_M00139_0112_000000000-B3285
#	170106_M01053_0481_000000000-AVVKE
#
#	Load BAMs and VCFs from Release Downloads https://github.com/PapenfussLab/PathOS/releases/tag/v1.3.0RC12-beta
#	Note: curl may redirect
#
% cd /Users/PathOS/Dockish-PathOS/docker/
% curl -L -o pipeline.repository.tgz https://github.com/PapenfussLab/PathOS/releases/download/v1.3.0RC12-beta/pipeline.repository.tgz
% tar xvf pipeline.repository.tgz

