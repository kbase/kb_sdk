FROM ubuntu:14.04
MAINTAINER Shane Canon <scanon@lbl.gov>

# Update apt and install jdk and docker engine to get docker clients
RUN apt-get -y update && \
    apt-get -y install openjdk-7-jdk make git ant && \
    apt-get -y install apt-transport-https ca-certificates && \
    apt-key adv \
               --keyserver hkp://ha.pool.sks-keyservers.net:80 \
               --recv-keys 58118E89F3A912897C070ADBF76221572C52609D && \
    echo "deb https://apt.dockerproject.org/repo ubuntu-trusty main" > /etc/apt/sources.list.d/docker.list && \
    apt-get -y update && apt-get -y install docker-engine=1.11.2-0~trusty 

ADD . /src

# Add kb_sdk src and fix CallbackServer interface
RUN \
   cd /src && \
   sed -i 's/en0/eth0/' src/java/us/kbase/common/executionengine/CallbackServer.java && \
   make && \
   /src/entrypoint prune

ENV PATH=$PATH:/src/bin

ENTRYPOINT [ "/src/entrypoint" ]

CMD [ ]
