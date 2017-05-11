FROM tomcat:7-jre7
RUN mkdir -p /pathology/NGS/pa_local/etc \
             /pathology/NGS/pa_local/log \
             /pathology/NGS/pa_local/Report \
             /pathology/NGS/pa_local/Searchable
ADD Default-Reports.tgz /pathology/NGS/pa_local/Report/
COPY pathos.properties /pathology/NGS/pa_local/etc/
COPY PathOS.war /usr/local/tomcat/webapps/
ENV JAVA_OPTS="-server -Dgrails.env=pa_local -Dpathos.config=/pathology/NGS/pa_local/etc/pathos.properties"
