FROM openjdk:7-jre
ADD tools.tgz /opt/pathos/
COPY pathos.properties /opt/pathos/etc/pathos.properties
ENV PATH=$PATH:/opt/pathos/bin \
    PATHOS_HOME=/opt/pathos
CMD ["sleep", "infinity"]
