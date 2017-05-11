FROM openjdk:7-jdk

# Setup Grails
ENV GRAILS_VERSION 2.3.7
WORKDIR /usr/lib/jvm
RUN wget -q https://github.com/grails/grails-core/releases/download/v$GRAILS_VERSION/grails-$GRAILS_VERSION.zip && \
    unzip grails-$GRAILS_VERSION.zip && \
    rm -rf grails-$GRAILS_VERSION.zip && \
    ln -s grails-$GRAILS_VERSION grails

ENV GRAILS_HOME /usr/lib/jvm/grails
ENV PATH $GRAILS_HOME/bin:$PATH

# Setup Gradle
ENV GRADLE_VERSION 1.10
RUN mkdir -p /opt/gradle
WORKDIR /opt/gradle
RUN wget -q "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip" && \
    unzip gradle-${GRADLE_VERSION}-bin.zip && \
    rm gradle-${GRADLE_VERSION}-bin.zip && \
    ln -s /opt/gradle/gradle-${GRADLE_VERSION}/bin/gradle /usr/bin/gradle

RUN mkdir /pathos           && \
    mkdir /build            && \
    mkdir -p /cache         && \
    mkdir -p /cache/gradle  && \
    mkdir -p /cache/grails  && \
    mkdir -p /root/.ssh     && \
    mkdir -p /root/.grails  && \
    echo grails.dependency.cache.dir = \"/cache/grails\" > /root/.grails/settings.groovy

COPY pathos-ssh-config /root/.ssh/config
COPY pathos-git-rsa_id /root/.ssh/id_rsa
COPY docker_entrypoint.sh /
RUN chmod 0400 /root/.ssh/config    && \
    chmod 0400 /root/.ssh/id_rsa    && \
    chmod +x /docker_entrypoint.sh
WORKDIR /build
CMD /docker_entrypoint.sh

