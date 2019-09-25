# Dockerised PathOS

PathOS has a number of components and configuration elements that all have to work together for a functioning system. Using Docker makes this much easier. PathOS comes with a docker-compose.yaml file, which tells Docker how to download and join everything together to get up a working system, complete with a persistent database. With Docker, PathOS can be ran with a single command.

### Installing Docker

You must first install Docker, if you haven't yet. 

Unix users should install [Docker Engine](https://docs.docker.com/install/#server) and then [Docker Compose](https://docs.docker.com/compose/install/).

Users on a Windows system can use [Docker Desktop for Windows](https://docs.docker.com/docker-for-windows/install/).

Users on a Mac can use [Docker Desktop for Mac](https://docs.docker.com/docker-for-mac/install/).

Users on the NECTAR cluster should be able to install Docker and Docker Compose by running:

```
sudo bash
apt update
apt upgrade -y
apt install -y docker docker-compose
```

### Quick-Start

To run PathOS from the Docker image, you must first clone/download the PathOS repository.

```
git clone https://github.com/PapenfussLab/PathOS.git
```

Then, execute the following commands.

  ```
  # navigate to the Docker/database directory
  #
  cd PathOS/Docker/database

  # the below will run the PathOS docker container in the background
  #
  docker-compose up -d

  # the below will load sample data into pathos. skip this step if you do not want example data.
  # you should wait for the command to complete before proceeding to use PathOS.
  # since data persists between sessions this only needs to be ran once.
  #
  docker-compose run -v $PWD/load_dir/:/pathos-loader-input.d loader 
  ```

PathOS will then be available on localhost:8080/PathOS. You can log in with the default username _pathosadmin_ and password _pathos_.

You can stop the running image by executing `docker-compose down`.

Data will be saved between sessions, persisting on the file system in the PathOS/Docker/database/pathos-db-data directory.

### Build a custom PathOS image

To build a custom PathOS Docker image - say, once you have made changes to the source code - follow the instructions below.

First, you must first re-build PathOS.

```
  # navigate to the root PathOS dir
  #
  cd <path to your PathOS directory>

  # run script to clean PathOS
  #
  sh cleanAll.sh

  # run script to build PathOS
  #
  sh installAll.sh
```

Next, build a custom Docker image. 

  ```
  # set the necessary version and pathos_home variables. note that if you changed the PathOS
  # version (this has to be done across all .gradle files in the repo), change the version
  # variable below accordingly.
  #
  version=1.5.2                                           # change this to whatever our version is
  pathos_home = <path to your PathOS directory>/Curate    # set an absolute path to PathOS/Curate

  cd <path to your PathOS directory>/Docker/build         # navigate to Docker/Build

  # copy over the necessary war and jar files
  #
  cp $pathos_home/target/Curate-${version}.war PathOS.war
  cp $pathps_home/build/libs/Loader-all-${version} Loader-all.jar 

  # build a docker image
  #
  docker build -t my_curate -f Dockerfile-curate
  docker build -t my_loader -f Dockerfile-loader
  ```

You can now change the docker-compose.yaml file in the Docker/database directory to use this new image. Edit the file and change line 22 to `image: my_curate` and line 57 to `image: my_loader`. This will make the docker-compose file point to your new custom image instead of the public one. 

Now running `docker-compose up -d` from the Docker/database directory will run this custom PathOS image.
