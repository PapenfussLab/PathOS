version=1.5.2
pathos_home=/usr/local/dev/PathOS/Curate

cp $pathos_home/target/Curate-${version}.war PathOS.war
cp $pathos_home/build/libs/Loader-all-${version}.jar Loader-all.jar

docker login -u <user> -p <pass> docker.io

docker build -t curate -f Dockerfile-curate .
docker build -t loader -f Dockerfile-loader .

docker tag curate:latest <user>/pathos_curate:${version}
docker tag loader:latest <user>/pathos_loader:${version}

docker push <user>/pathos_curate:${version}
docker push <user>/pathos_loader:${version}
