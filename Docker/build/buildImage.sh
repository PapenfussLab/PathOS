version=1.5.2
pathos_home=/usr/local/dev/PathOS/Curate

cp $pathos_home/target/Curate-${version}.war PathOS.war
cp $pathos_home/build/libs/Loader-all-${version}.jar Loader-all.jar

docker login -u dockerpathos -p Scrooge0 docker.io

docker build -t curate -f Dockerfile-curate .
docker build -t loader -f Dockerfile-loader .

docker tag curate:latest dockerpathos/pathos_curate:${version}
docker tag loader:latest dockerpathos/pathos_loader:${version}

docker push dockerpathos/pathos_curate:${version}
docker push dockerpathos/pathos_loader:${version}
