FROM ubuntu:16.04

#Install Core
RUN apt-get update && apt-get -y upgrade && \
apt-get -y install software-properties-common apt-transport-https curl
RUN apt-key adv --keyserver keyserver.ubuntu.com --recv-keys E084DAB9 &&\
    apt-key adv --keyserver keyserver.ubuntu.com --recv-keys 40976EAF437D05B5 &&\
    apt-key adv --keyserver keyserver.ubuntu.com --recv-keys 3B4FE6ACC0B21F32 &&\
    echo "deb https://cran.uni-muenster.de/bin/linux/ubuntu xenial/" >> /etc/apt/sources.list && \
    echo "deb http://ftp.halifax.rwth-aachen.de/ubuntu/ xenial-backports main restricted universe" >> /etc/apt/sources.list
RUN apt-get update && apt-get -y upgrade && \
    apt-get install -y aptitude && \
    apt-get -y install tomcat7
#Install Libraries
RUN     mkdir /opt/lib && \
 mkdir /opt/lib/RLibs && \
 export R_LIBS=/opt/lib/RLibs && \
aptitude install -y r-base-core r-base-dev r-base-html r-cran-boot r-cran-class r-cran-cluster r-cran-codetools r-cran-foreign r-cran-kernsmooth r-cran-lattice r-cran-mass r-cran-matrix r-cran-mgcv r-cran-nlme r-cran-nnet r-cran-rpart r-cran-spatial r-cran-survival r-doc-html r-recommended &&\
    apt-get install -y r-cran-rcolorbrewer &&\
    Rscript -e 'install.packages("randomForest", repos="http://R-Forge.R-project.org" , lib="/opt/lib/RLibs")' &&\
    apt-get install -y python python-pip &&\
 pip install --upgrade pip && \
 pip install numpy && \
 pip install watchdog && \
 pip install pandas && \
  echo "CATALINA_OPTS=\"-Djava.awt.headless=true -Xms128m -Xmx1536m -XX:+UseConcMarkSweepGC -Xss20m -XX:MaxPermSize=256m \"" > /usr/share/tomcat7/bin/setenv.sh

ENV R_LIBS=/opt/lib/RLibs
# Copy Application

RUN mkdir /etc/opt/grails/ &&\
    mkdir /srv/cancersys &&\
    mkdir /srv/cancersys/ImportedFiles &&\
    mkdir /srv/cancersys/Data &&\
    chmod -R 777 /srv


COPY csys-db-config.groovy /etc/opt/grails/csys-db-pub.groovy
# Expose the default tomcat port

ADD run.sh /root/run.sh
RUN chmod +x /root/run.sh &&\
    apt-get install -y mariadb-client-10.0

EXPOSE 8080

COPY target/csys-db-pub.war /var/lib/tomcat7/webapps/csys-db-pub.war

CMD ["/root/run.sh"]