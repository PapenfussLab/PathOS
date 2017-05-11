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
% vi .env   ## edit the file .env and change PATHOS_DATA to be the root of the pipeline repository eg /Users/PathOS/docker/pathology
% docker-compose build

#	Check results
#
% docker images

#	Populate DB  (copy example DB from this repository - From Release Downloads https://github.com/PapenfussLab/PathOS/releases/tag/v1.3.0RC12-beta)
#
#	Note: curl may redirect
#
% curl -L -o dbalt.170125.sql.gz https://github.com/PapenfussLab/PathOS/releases/download/v1.3.0RC12-beta/dbalt.170125.sql.gz
% gzcat dbalt.170125.sql.gz | docker exec -i docker_pathos-mariadb_1 mysql -ubioinformatics -ppathos -D dblive -B

#	Start up docker pathos (50s !)
#
% docker-compose up [-d]   ## -d to run as daemon

## Go to browser (Mac)    https://localhost/PathOS/

#####################################################################
#
#	A d d i t i o n a l   c o m m a n d s   
#
#	Check that they came up and have the right ports
#
% docker container ls
% docker ps

#	To restart tomcat 
#
% docker restart docker_pathos-tomcat  # may need docker_pathos-tomcat_1

#	To populate an example pipeline data repository (VCFs BAMs etc)
#
#	From Release Downloads https://github.com/PapenfussLab/PathOS/releases/tag/v1.3.0RC12-beta
#
#	Note: curl may redirect
#
% cd /Users/PathOS
% curl -L -o pipeline.repository.tgz https://github.com/PapenfussLab/PathOS/releases/download/v1.3.0RC12-beta/pipeline.repository.tgz
% tar xvf pipeline.repository.tgz

